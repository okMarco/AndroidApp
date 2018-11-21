package com.hochan.tumlodr.ui.component;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * .
 * Created by hochan on 2018/6/5.
 */

public interface IPhotoLayout {

	ArrayList<String> getPhotoUrls();

	ArrayList<String> getThumbnailUrls();

	ArrayList<String> getPhotoNormalUrls();

	ImageView getImageViewInPosition(int position);

	int getImageViewCount();

	void reloadImage();
}
