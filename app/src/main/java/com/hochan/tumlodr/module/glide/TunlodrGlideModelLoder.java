package com.hochan.tumlodr.module.glide;

import android.support.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.jumblr.types.Photo;
import com.hochan.tumlodr.jumblr.types.PhotoSize;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.ScreenTools;

import java.io.InputStream;
import java.util.List;

/**
 * .
 * Created by hochan on 2017/12/24.
 */

public class TunlodrGlideModelLoder extends BaseGlideUrlLoader<Photo> {

	public static class Factory implements ModelLoaderFactory<Photo, InputStream> {

		private static final ModelCache<Photo, GlideUrl> modelCache = new ModelCache<>(500);

		@NonNull
		@Override
		public ModelLoader<Photo, InputStream> build(MultiModelLoaderFactory multiFactory) {
			return new TunlodrGlideModelLoder(multiFactory.build(GlideUrl.class, InputStream.class), modelCache);
		}

		@Override
		public void teardown() {
		}
	}

	private TunlodrGlideModelLoder(ModelLoader<GlideUrl, InputStream> urlLoader, ModelCache<Photo, GlideUrl> modelCache) {
		super(urlLoader, modelCache);
	}

	@Override
	protected String getUrl(Photo photo, int width, int height, Options options) {
		String url = OkHoGlideUtil.PHOTO_NORMAL_URL_CACHE.get(photo);
		if (url == null) {
			PhotoSize photoSize = getResolutionPhotoSize(width, photo);
			if (photoSize != null) {
				return photoSize.getUrl();
			} else {
				return null;
			}
		} else {
			return url;
		}
	}


	public static PhotoSize getResolutionPhotoSize(int width, Photo photo) {
		int column = ScreenTools.getScreenWidth(TumlodrApp.mContext) / width;
		PhotoSize photoSize = new PhotoSize();
		List<PhotoSize> allSizePhotos = photo.getSizes();
		if (allSizePhotos == null) {
			photoSize = photo.getOriginalSize();
		} else {
			int sizeCount = allSizePhotos.size();
			switch (AppConfig.mResolution) {
				case AppConfig.RESOLUTION_DEFAULT: {
					if (column == 4)
						photoSize = allSizePhotos.get(sizeCount > 2 ? sizeCount - 2 : sizeCount - 1);
					else {
						if (sizeCount > 3) {
							photoSize = allSizePhotos.get(sizeCount - 3);
						} else if (sizeCount > 1) {
							photoSize = allSizePhotos.get(1);
						} else {
							photoSize = photo.getOriginalSize();
						}
					}
					break;
				}
				case AppConfig.RESOLUTION_LOW: {
					photoSize = allSizePhotos.get(sizeCount - 1);
					break;
				}
				case AppConfig.RESOLUTION_HIGH: {
					photoSize = allSizePhotos.get(0);
					break;
				}
			}
		}
		return photoSize;
	}

	@Override
	public boolean handles(Photo photo) {
		return true;
	}
}
