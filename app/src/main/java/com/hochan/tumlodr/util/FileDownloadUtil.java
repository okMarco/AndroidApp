package com.hochan.tumlodr.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.LruCache;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.model.data.TasksManagerModel;
import com.hochan.tumlodr.model.data.download.DownloadRecordDatabase;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.Tools;
import com.liulishuo.filedownloader.FileDownloader;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.hochan.tumlodr.model.data.TasksManagerModel.TYPE_VIDEO;
import static com.hochan.tumlodr.tools.Tools.getPicNameByUrl;
import static com.hochan.tumlodr.ui.adapter.PostAdapter.TYPE_PHOTO;

/**
 * .
 * Created by hochan on 2018/1/1.
 */

public class FileDownloadUtil {

	private static final String TUMBLR_ORIGINAL_VIDEO_URL_TAG = "video_file";

	private static LruCache<Long, TumblrVideoDownloadInfo> mVideoDownloadInfoLruCache = new LruCache<>(500);

	public static TumblrVideoDownloadInfo getVideoDownloadInfo(VideoPost videoPost) {
		if (videoPost == null || videoPost.getVideos() == null || videoPost.getVideos().size() <= 0) {
			return null;
		}
		TumblrVideoDownloadInfo tumblrVideoDownloadInfo = mVideoDownloadInfoLruCache.get(videoPost.getId());
		if (tumblrVideoDownloadInfo != null) {
			return tumblrVideoDownloadInfo;
		}
		Video video = videoPost.getVideos().get(0);
		Document document = Jsoup.parse(video.getEmbedCode());
		Elements elements = document.getElementsByTag("source");
		if (elements != null && elements.size() > 0) {
			String videoUrl = elements.get(0).attr("src");
			tumblrVideoDownloadInfo = new TumblrVideoDownloadInfo(videoUrl, videoPost.getThumbnailUrl());
			mVideoDownloadInfoLruCache.put(videoPost.getId(), tumblrVideoDownloadInfo);
			return tumblrVideoDownloadInfo;
		}
		return new TumblrVideoDownloadInfo(null, videoPost.getThumbnailUrl());
	}

	@Nullable
	private static String getTransformedVideoUrl(@Nullable String videoUrl) {
		if (TextUtils.isEmpty(videoUrl))
			return null;
		String transformedVideoUrl = null;
		if (videoUrl.contains("video_file")) {
			if (AppConfig.sForceStitchingVideoUrl) {
				transformedVideoUrl = getTumblrStitchedVideoUrl(videoUrl);
			} else if (VideoPlayLayout.sIsInEuro) {
				transformedVideoUrl = VideoPlayLayout.VIDEO_URL_INEURO.get(videoUrl);
			}
		}
		return TextUtils.isEmpty(transformedVideoUrl) ? videoUrl : transformedVideoUrl;
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
		videoUrl = Uri.parse(videoUrl).getPath();
		if (!TextUtils.isEmpty(videoUrl) && videoUrl.contains("tumblr")) {
			return (videoUrl.substring(videoUrl.lastIndexOf("tumblr"), videoUrl.length()) + ".mp4")
					.replace("/", "_");
		} else {
			return videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
		}
	}

	public static boolean download(Post post) {
		if (post.getType().equals(TYPE_PHOTO) || post instanceof PhotoPost) {
			return downloadTumblrPics((PhotoPost) post);
		} else if (post.getType().equals(TYPE_VIDEO) || post instanceof VideoPost) {
			return downloadTumblrVideo((VideoPost) post);
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

	@NonNull
	private static String getTumblrStitchedVideoUrl(@NonNull String videoUrl) {
		if (!videoUrl.endsWith("/480")) {
			videoUrl = "https://vt.media.tumblr.com" + videoUrl.substring(videoUrl.lastIndexOf("/")) + ".mp4";
		} else {
			videoUrl = videoUrl.replace("/480", "");
			videoUrl = "https://vt.media.tumblr.com" + videoUrl.substring(videoUrl.lastIndexOf("/")) + "_480.mp4";
		}
		return videoUrl;
	}

	private static void downloadTumblrVideoRemote(TumblrVideoDownloadInfo tumblrVideoDownloadInfo) {
		if (tumblrVideoDownloadInfo != null) {
			String videoUrl = tumblrVideoDownloadInfo.getVideoUrl();
			if (!TextUtils.isEmpty(videoUrl) && videoUrl.contains(TUMBLR_ORIGINAL_VIDEO_URL_TAG)
					&& VideoPlayLayout.sIsInEuro) {
				videoUrl = getTumblrStitchedVideoUrl(videoUrl);
			}
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

	private static boolean downloadTumblrPics(PhotoPost photoPost) {
		boolean isAllExist = true;
		for (Photo photo : photoPost.getPhotos()) {
			final String imageUrl = photo.getOriginalSize().getUrl();
			final String imageName = getPicNameByUrl(imageUrl);
			String path = Tools.getStoragePathByFileName(imageName);
			File file = new File(path);
			if (file.exists()) {
				continue;
			}
			isAllExist = false;
			downloadPicture(imageUrl, file, photo.getSizes().get(photo.getSizes().size() - 1).getUrl());
		}
		if (isAllExist) {
			RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH,
					Tools.getStoragePathByFileName(getPicNameByUrl(photoPost.getPhotos().get(0).getOriginalSize().getUrl()))));
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

	public static void addInstagramPicDownload(String url, String groupName) {
		String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		String direct = Tools.getStoragePathByFileName("") + groupName + File.separator;
		if (!new File(direct).exists()) {
			if (!new File(direct).mkdirs()) {
				direct = Tools.getStoragePathByFileName("");
			}

		}
		final String toFile = direct + fileName;
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
		String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length());
		String direct = Tools.getStoragePathByFileName("") + groupName + File.separator;
		if (!new File(direct).exists()) {
			if (!new File(direct).mkdirs()) {
				direct = Tools.getStoragePathByFileName("");
			}
		}
		final String toFile = direct + fileName;
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
			return getTransformedVideoUrl(mVideoUrl);
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
