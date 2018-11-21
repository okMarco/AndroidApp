package com.hochan.tumlodr.model.data;

import com.tumblr.jumblr.types.Photo;

import java.util.List;

/**
 * .
 * Created by hochan on 2017/9/24.
 */

public class TextPostBody {

	private List<Photo> mPhotos;
	private String mContent;

	public List<Photo> getPhotos() {
		return mPhotos;
	}

	public void setPhotos(List<Photo> mPhotos) {
		this.mPhotos = mPhotos;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}
}
