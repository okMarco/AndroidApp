package com.hochan.tumlodr.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.LruCache;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.jumblr.types.Photo;
import com.hochan.tumlodr.jumblr.types.PhotoPost;
import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.jumblr.types.TextPost;
import com.hochan.tumlodr.jumblr.types.Video;
import com.hochan.tumlodr.jumblr.types.VideoPost;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.model.data.TasksManagerModel;
import com.hochan.tumlodr.model.data.TextPostBody;
import com.hochan.tumlodr.model.data.download.DownloadRecordDatabase;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.component.SingleMediaScanner;
import com.liulishuo.filedownloader.FileDownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.hochan.tumlodr.model.data.TasksManagerModel.TYPE_VIDEO;
import static com.hochan.tumlodr.tools.Tools.getPicNameByUrl;

/**
 * .
 * Created by hochan on 2018/1/1.
 */

public class FileDownloadUtil {

	public static TumblrVideoDownloadInfo getVideoDownloadInfo(VideoPost videoPost) {
		if (videoPost == null || videoPost.getVideos() == null || videoPost.getVideos().size() <= 0) {
			return null;
		}
        return new TumblrVideoDownloadInfo(videoPost.video_url, videoPost.getThumbnailUrl());
	}

	@NonNull
	public static String getVideoPathByVideoUrl(String videoUrl) {
		return Tools.getStoragePathByFileName(geVideoNameByVideoUrl(videoUrl));
	}

	@NonNull
	public static String geVideoNameByVideoUrl(String videoUrl) {
		if (TextUtils.isEmpty(videoUrl)) {
			return String.valueOf(System.currentTimeMillis()) + ".mp4";
		}
		videoUrl = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
		if (videoUrl.endsWith(".mp4")) {
			return videoUrl;
		}else {
			return videoUrl + ".mp4";
		}
	}

	public static boolean download(Post post) {
		if (post.getType() == Post.PostType.PHOTO || post instanceof PhotoPost) {
			return downloadTumblrPics(((PhotoPost) post).getPhotos());
		} else if (post.getType() == Post.PostType.VIDEO || post instanceof VideoPost) {
			return downloadTumblrVideo((VideoPost) post);
		} else if (post.getType() == Post.PostType.TEXT || post instanceof TextPost) {
			if (((TextPost)post).getTextPostBody().getPhotos().size() > 0) {
				return downloadTumblrPics(((TextPost)post).getTextPostBody().getPhotos());
			}
		}
		return false;
	}

	private static boolean downloadTumblrVideo(final VideoPost videoPost) {
		final TumblrVideoDownloadInfo tumblrVideoDownloadInfo = FileDownloadUtil.getVideoDownloadInfo(videoPost);
		if (tumblrVideoDownloadInfo == null) {
			return false;
		}
		final String videoPath = tumblrVideoDownloadInfo.getVideoPath();
		if (!TextUtils.isEmpty(videoPath) && new File(videoPath).exists()) {
			RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH, videoPath));
			return true;
		}
		String videoUrl = tumblrVideoDownloadInfo.getVideoUrl();
		if (TextUtils.isEmpty(videoUrl)) {
			return false;
		}
		if (TumlodrApp.getProxy().isCached(videoUrl)) {
			File cacheDir = TumlodrApp.getAppCacheDir();
			String fileName = FileDownloadUtil.geVideoNameByVideoUrl(tumblrVideoDownloadInfo.getVideoUrl());
			final File cachedFile = new File(cacheDir, fileName);
			io.reactivex.Observable.create(new ObservableOnSubscribe<Integer>() {
				@SuppressWarnings("TryFinallyCanBeTryWithResources")
				@Override
				public void subscribe(ObservableEmitter<Integer> e) throws Exception {
					InputStream fromStream = new FileInputStream(cachedFile);
					OutputStream toStream = new FileOutputStream(videoPath);
					long totalSize = cachedFile.length();
					try {
						byte bt[] = new byte[1024];
						int c;
						int finishSize = 0;
						while ((c = fromStream.read(bt)) > 0) {
							toStream.write(bt, 0, c);
							finishSize = finishSize + c;
							e.onNext((int) (finishSize * 1.0f / totalSize * 100));
						}
						e.onComplete();
					} finally {
						fromStream.close();
						toStream.close();
					}
				}
			}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
					.subscribe(new BaseObserver<Integer>() {

						@Override
						public void onComplete() {
							DownloadRecordDatabase.insertNewFinishedTumlrVideoDownload(tumblrVideoDownloadInfo.getVideoUrl(),
									tumblrVideoDownloadInfo.getVideoPath(),
									tumblrVideoDownloadInfo.getVideoThumbnail(), TYPE_VIDEO);
							RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH, videoPath));
							SingleMediaScanner.scanFile(videoPath);
						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
							downloadTumblrVideoRemote(tumblrVideoDownloadInfo);
						}
					});
		} else {
			downloadTumblrVideoRemote(tumblrVideoDownloadInfo);
		}
		return true;
	}

	private static void downloadTumblrVideoRemote(TumblrVideoDownloadInfo tumblrVideoDownloadInfo) {
		if (tumblrVideoDownloadInfo != null) {
			String videoUrl = tumblrVideoDownloadInfo.getVideoUrl();
			FileDownloader.getImpl().create(videoUrl)
					.setPath(tumblrVideoDownloadInfo.getVideoPath())
					.addFinishListener(AppConfig.mDownloadFinishListener)
					.setListener(AppConfig.mFileDownloadListener)
					.start();
			DownloadRecordDatabase.insertNewTumblrNormalDownload(videoUrl, tumblrVideoDownloadInfo.mVideoPath,
					tumblrVideoDownloadInfo.getVideoThumbnail(), TYPE_VIDEO);
			RxBus.getInstance().send(new Events<>(Events.EVENT_FILE_ADD_TO_DOWNLOAD, null));
		}
		RxBus.getInstance().send(new Events<>(Events.EVENT_VIDEO_ADD_TO_DOWNLOAD_FAIL, null));
	}

	private static boolean downloadTumblrPics(List<Photo> photos) {
		boolean isAllExist = true;
		for (Photo photo : photos) {
			final String imageUrl = photo.getOriginalSize().getUrl();
			final String imageName = getPicNameByUrl(imageUrl);
			String path = Tools.getStoragePathByFileName(imageName);
			File file = new File(path);
			if (file.exists()) {
                SingleMediaScanner.scanFile(file.getAbsolutePath());
				continue;
			}
			isAllExist = false;
			downloadPicture(imageUrl, file, photo.getOriginalSize().getUrl());
		}
		if (isAllExist) {
			RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH,
					Tools.getStoragePathByFileName(getPicNameByUrl(photos.get(0).getOriginalSize().getUrl()))));
		} else {
			RxBus.getInstance().send(new Events<>(Events.EVENT_FILE_ADD_TO_DOWNLOAD, null));
		}
		return true;
	}

	public static void downloadPicture(final String imageUrl, final File file, String thumbnail) {
		FileDownloader.getImpl().create(imageUrl)
				.setPath(file.getAbsolutePath())
				.addFinishListener(AppConfig.mDownloadFinishListener)
				.setListener(AppConfig.mFileDownloadListener)
				.start();
		DownloadRecordDatabase.insertNewTumblrNormalDownload(imageUrl, file.getAbsolutePath(), thumbnail, TasksManagerModel.TYPE_IMAGE);
	}

	public static String getInstagramGroupDownloadDirect(String groupName) {
        String direct = Tools.getStoragePathByFileName("") + groupName + File.separator;
        if (!new File(direct).exists()) {
            //noinspection ResultOfMethodCallIgnored
            new File(direct).mkdirs();
        }
        return direct;
    }

	public static void addInstagramPicDownload(String url, String groupName) {
		String fileName = getFileNameOfInstagramUrl(url, ".jpg");
		final String toFile = getInstagramGroupDownloadDirect(groupName) + fileName;
		DownloadRecordDatabase.insertNewInstagramGroupDownload(groupName, url, toFile, null, TasksManagerModel.TYPE_IMAGE);
		if (new File(toFile).exists()) {
			return;
		}
		FileDownloader.getImpl().create(url)
				.setPath(toFile)
				.addFinishListener(AppConfig.mDownloadFinishListener)
				.setListener(AppConfig.mFileDownloadListener)
				.start();
	}

	public static void addInstagramVideoDownload(String thumbnail, String videoUrl, String groupName) {
		String fileName = getFileNameOfInstagramUrl(videoUrl, ".mp4");
		final String toFile = getInstagramGroupDownloadDirect(groupName) + fileName;
		DownloadRecordDatabase.insertNewInstagramGroupDownload(groupName, videoUrl, toFile, thumbnail, TasksManagerModel.TYPE_VIDEO);
		if (new File(toFile).exists()) {
			System.out.println("FileDownloadUtil" + ":" + "addInstagramPicDownload" + " --> " + "已存在");
			return;
		}
		FileDownloader.getImpl().create(videoUrl)
				.setPath(toFile)
				.addFinishListener(AppConfig.mDownloadFinishListener)
				.setListener(AppConfig.mFileDownloadListener)
				.start();
	}

	private static String getFileNameOfInstagramUrl(String url, String subfix) {
        if (url.contains("?")) {
            url = url.substring(0, url.lastIndexOf("?"));
        }
        String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
        if (!fileName.endsWith(subfix)) {
            fileName = fileName + subfix;
        }
        return fileName;
    }

	@SuppressWarnings({"unused", "WeakerAccess"})
	public static class TumblrVideoDownloadInfo {

		@Nullable
		private String mVideoUrl;
		private String mVideoPath;
		private String mVideoThumbnail;

		TumblrVideoDownloadInfo(@Nullable String videoUrl, String videoThumbnail) {
			mVideoUrl = videoUrl;
			mVideoThumbnail = videoThumbnail;
			mVideoPath = getVideoPathByVideoUrl(videoUrl);
		}

		public String getVideoUrl() {
			return mVideoUrl;
		}

		public String getVideoPath() {
			return mVideoPath;
		}

		public String getVideoThumbnail() {
			return mVideoThumbnail;
		}

		public void setVideoUrl(@Nullable String videoUrl) {
			mVideoUrl = videoUrl;
			mVideoPath = getVideoPathByVideoUrl(videoUrl);
		}

		public void setVideoPath(String videoPath) {
			mVideoPath = videoPath;
		}

		public void setVideoThumbnail(String videoThumbnail) {
			mVideoThumbnail = videoThumbnail;
		}
	}
}
