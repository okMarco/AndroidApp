package com.hochan.tumlodr.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hochan.tumlodr.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by zhendong_chen on 2016/8/25.
 */
public abstract class BaseViewPagerFragment extends Fragment{

	private List<BaseFragment> mFragmentList = new ArrayList<>();
	private ViewPager mViewPager;
	private TabLayout mTabLayout;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_base_view_pager, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
		getFragmentList(mFragmentList);
		BasePagerAdpater basePagerAdpater = new BasePagerAdpater(mFragmentList, getFragmentManager());
		mViewPager.setAdapter(basePagerAdpater);
		if(mTabLayout != null){
			mTabLayout.setupWithViewPager(mViewPager);
		}
	}

	public void setTabLayout(TabLayout tabLayout){
		this.mTabLayout = tabLayout;
		if(mViewPager != null){
			mTabLayout.setupWithViewPager(mViewPager);
		}
	};

	public abstract void getFragmentList(List<BaseFragment> fragmentList);

	class BasePagerAdpater extends FragmentPagerAdapter{

		private List<BaseFragment> mFragmentList;

		public BasePagerAdpater(List<BaseFragment> fragmentList, FragmentManager fm) {
			super(fm);
			this.mFragmentList = fragmentList;
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentList.get(position).getTitle();
		}
	}
}
