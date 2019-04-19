package com.hochan.tumlodr.module.glide;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.hochan.tumlodr.jumblr.types.Photo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

import static com.bumptech.glide.load.engine.cache.DiskCache.Factory.DEFAULT_DISK_CACHE_DIR;

/**
 * .
 * Created by hochan on 2017/12/24.
 */

@GlideModule(glideName = "TumlodrGlide")
public class TumlodrGlideModule extends AppGlideModule {

	@Override
	public void applyOptions(final Context context, GlideBuilder builder) {
		super.applyOptions(context, builder);
		builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context));
		builder.setMemoryCache(new LruResourceCache(0));
		builder.setDiskCache(new DiskLruCacheFactory(new DiskLruCacheFactory.CacheDirectoryGetter() {
			@Override
			public File getCacheDirectory() {
				File cacheDirectory = context.getCacheDir();
				if (cacheDirectory == null) {
					return null;
				}
				return new File(cacheDirectory, DEFAULT_DISK_CACHE_DIR);
			}
		}, 100 * 1024 * 1024));
	}

	@Override
	public void registerComponents(Context context, Glide glide, Registry registry) {
		super.registerComponents(context, glide, registry);
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		OkHttpClient client = builder
				.connectTimeout(100, TimeUnit.SECONDS)
				.readTimeout(100, TimeUnit.SECONDS)
				.addNetworkInterceptor(createInterceptor(new DispatchingProgressListener()))
				.build();
		registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
		registry.append(Photo.class, InputStream.class, new TunlodrGlideModelLoder.Factory());

	}

	public static void forget(String url) {
		DispatchingProgressListener.forget(url);
	}

	public static void expect(String url, UIProgressListener listener) {
		DispatchingProgressListener.expect(url, listener);
	}

	private static Interceptor createInterceptor(final ResponseProgressListener listener) {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Request request = chain.request();
				Response response = chain.proceed(request);
				return response.newBuilder()
						.body(new OkHttpProgressResponseBody(request.url(), response.body(), listener))
						.build();
			}
		};
	}

	private static class DispatchingProgressListener implements ResponseProgressListener {

		private static final WeakHashMap<String, UIProgressListener> LISTENERS = new WeakHashMap<>();
		private static final Map<String, Long> PROGRESSES = new HashMap<>();

		private final Handler handler;

		DispatchingProgressListener() {
			this.handler = new Handler(Looper.getMainLooper());
		}

		static void forget(String url) {
			LISTENERS.remove(url);
			PROGRESSES.remove(url);
		}

		static void expect(String url, UIProgressListener listener) {
			LISTENERS.put(url, listener);
		}

		@Override
		public void update(HttpUrl url, final long bytesRead, final long contentLength) {
			String key = url.toString();
			final UIProgressListener listener = LISTENERS.get(key);
			if (listener == null) {
				return;
			}
			if (contentLength <= bytesRead) {
				forget(key);
			}
			if (needsDispatch(key, bytesRead, contentLength, listener.getGranualityPercentage())) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						listener.onProgress(bytesRead, contentLength);
					}
				});
			}
		}

		private boolean needsDispatch(String key, long current, long total, float granularity) {
			if (granularity == 0 || current == 0 || total == current) {
				return true;
			}
			float percent = 100f * current / total;
			long currentProgress = (long) (percent / granularity);
			Long lastProgress = PROGRESSES.get(key);
			if (lastProgress == null || currentProgress != lastProgress) {
				PROGRESSES.put(key, currentProgress);
				return true;
			} else {
				return false;
			}
		}
	}

	private static class OkHttpProgressResponseBody extends ResponseBody {
		private final HttpUrl url;
		private final ResponseBody responseBody;
		private final ResponseProgressListener progressListener;
		private BufferedSource bufferedSource;

		OkHttpProgressResponseBody(HttpUrl url, ResponseBody responseBody, ResponseProgressListener progressListener) {
			this.url = url;
			this.responseBody = responseBody;
			this.progressListener = progressListener;
		}

		@Override
		public MediaType contentType() {
			return responseBody.contentType();
		}

		@Override
		public long contentLength() {
			return responseBody.contentLength();
		}

		@Override
		public BufferedSource source() {
			if (bufferedSource == null) {
				bufferedSource = Okio.buffer(source(responseBody.source()));
			}
			return bufferedSource;
		}

		private Source source(Source source) {
			return new ForwardingSource(source) {
				long totalBytesRead = 0L;

				@Override
				public long read(Buffer sink, long byteCount) throws IOException {
					long bytesRead = super.read(sink, byteCount);
					long fullLength = responseBody.contentLength();
					if (bytesRead == -1) { // this source is exhausted
						totalBytesRead = fullLength;
					} else {
						totalBytesRead += bytesRead;
					}
					progressListener.update(url, totalBytesRead, fullLength);
					return bytesRead;
				}
			};
		}
	}

	private interface ResponseProgressListener {
		void update(HttpUrl url, long bytesRead, long contentLength);
	}

	public interface UIProgressListener {
		void onProgress(long bytesRead, long expectedLength);

		/**
		 * Control how often the listener needs an update. 0% and 100% will always be dispatched.
		 *
		 * @return in percentage (0.2 = call {@link #onProgress} around every 0.2 percent of progress)
		 */
		float getGranualityPercentage();
	}

}
