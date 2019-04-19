package com.hochan.tumlodr.util;

import android.annotation.SuppressLint;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * .
 * Created by hochan on 2017/10/26.
 */

@SuppressWarnings("WeakerAccess")
public class RxBus {

	private static RxBus mRxBus;
	private final Subject<Object> mBus = PublishSubject.create().toSerialized();

	private RxBus() {
	}

	public static RxBus getInstance() {
		if (mRxBus == null) {
			synchronized (RxBus.class) {
				if (mRxBus == null) {
					mRxBus = new RxBus();
				}
			}
		}
		return mRxBus;
	}

	public void send(Object o) {
		mBus.onNext(o);
	}

	public Observable<Object> toObservable() {
		return mBus;
	}

	public static SubscriberBuilder with(ActivityLifecycleProvider provider) {
		return new SubscriberBuilder(provider);
	}

	public static SubscriberBuilder with(FragmentLifecycleProvider provider) {
		return new SubscriberBuilder(provider);
	}

	@SuppressWarnings("unused")
	public static class SubscriberBuilder {
		private ActivityLifecycleProvider mActivityLifecycleProvider;
		private FragmentLifecycleProvider mFragmentLifecycleProvider;
		private FragmentEvent mFragmentEndEvent;
		private ActivityEvent mActivityEndEvent;
		private int eventCode;
		private Consumer<Object> onNext;
		private Consumer<Throwable> onError;

		private Predicate<Object> mFilter = new Predicate<Object>() {
			@Override
			public boolean test(@NonNull Object o) throws Exception {
				return !(o instanceof Events) || ((Events) o).mCode == eventCode;
			}
		};

		private Consumer<Throwable> mErrorConsumer = new Consumer<Throwable>() {

			@Override
			public void accept(Throwable throwable) throws Exception {
				throwable.printStackTrace();
			}
		};

		public SubscriberBuilder(ActivityLifecycleProvider provider) {
			this.mActivityLifecycleProvider = provider;
		}

		public SubscriberBuilder(FragmentLifecycleProvider provider) {
			this.mFragmentLifecycleProvider = provider;
		}

		public SubscriberBuilder setEventCode(int eventCode) {
			this.eventCode = eventCode;
			return this;
		}

		public SubscriberBuilder setEndEvent(FragmentEvent event) {
			this.mFragmentEndEvent = event;
			return this;
		}

		public SubscriberBuilder setEndEvent(ActivityEvent event) {
			this.mActivityEndEvent = event;
			return this;
		}

		public SubscriberBuilder onNext(Consumer<Object> action) {
			this.onNext = action;
			return this;
		}

		public SubscriberBuilder onError(Consumer<Throwable> action) {
			this.onError = action;
			return this;
		}

		@SuppressWarnings("ResultOfMethodCallIgnored")
		@SuppressLint("CheckResult")
		public void create() {
			if (mFragmentLifecycleProvider != null) {
				RxBus.getInstance().toObservable()
						.compose(mFragmentEndEvent == null ? mFragmentLifecycleProvider.bindToLifecycle() : mFragmentLifecycleProvider.bindUntilEvent(mFragmentEndEvent)) // 绑定生命周期
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(onNext, onError == null ? mErrorConsumer : onError);
			}
			if (mActivityLifecycleProvider != null) {
				RxBus.getInstance().toObservable()
						.compose(mActivityEndEvent == null ? mActivityLifecycleProvider.bindToLifecycle()
								: mActivityLifecycleProvider.bindUntilEvent(mActivityEndEvent))
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(onNext, onError == null ? mErrorConsumer : onError);
			}
		}
	}
}
