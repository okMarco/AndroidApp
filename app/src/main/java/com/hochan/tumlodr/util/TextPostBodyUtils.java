package com.hochan.tumlodr.util;

import android.text.TextUtils;

import com.hochan.tumlodr.jumblr.types.Photo;
import com.hochan.tumlodr.jumblr.types.PhotoSize;
import com.hochan.tumlodr.model.data.TextPostBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by hochan on 2017/9/24.
 */

public class TextPostBodyUtils {

	public static TextPostBody textPostBody(String body) {
		TextPostBody textPostBody = new TextPostBody();

		Document doc = Jsoup.parse(body);
		Elements figures = doc.select("figure");

		if (!figures.isEmpty()) {
			textPostBody.setPhotos(getPhotos(figures));
		}
		textPostBody.setContent(doc.html());
		return textPostBody;
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private static List<Photo> getPhotos(Elements figures) {
		List<Photo> photos = new ArrayList<>();
		if (!figures.isEmpty()) {
			for (Element figure : figures) {
				Element img = figure.child(0);
				if (img != null && img.nodeName().equals("img")) {
					String imgUrl = img.attr("src");
					String width = img.attr("data-orig-width");
					String height = img.attr("data-orig-height");
					if (TextUtils.isEmpty(width)) {
						width = String.valueOf("720");
					}
					if (TextUtils.isEmpty(height)) {
						height = width;
					}
					Photo photo = new Photo(imgUrl);
					PhotoSize photoSize = new PhotoSize();
					Class photoSizeClass = photoSize.getClass();
					Field[] photoSizeFields = photoSizeClass.getDeclaredFields();
					try {
						for (Field field : photoSizeFields) {
							field.setAccessible(true);
							//noinspection IfCanBeSwitch
							if (field.getName().equals("url")) {
								field.set(photoSize, imgUrl);
							} else if (field.getName().equals("width")) {
								field.set(photoSize, Integer.valueOf(width));
							} else if (field.getName().equals("height")) {
								field.set(photoSize, Integer.valueOf(height));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					Class photoClass = photo.getClass();
					Field[] photoFields = photoClass.getDeclaredFields();
					try {
						for (Field field : photoFields) {
							field.setAccessible(true);
							if (field.getName().equals("original_size")) {
								field.set(photo, photoSize);
							}
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

					photos.add(photo);
				}
				figure.remove();
			}
		}
		return photos;
	}
}
