package com.hochan.tumlodr.util;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.Tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static com.hochan.tumlodr.tools.Tools.getPicNameByUrl;

/**
 * .
 * Created by hochan on 2017/12/23.
 */

public class FileUtils {

	public static boolean checkFileExitsByUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		final String imageName = getPicNameByUrl(url);
		final String path = Tools.getStoragePathByFileName(imageName);
		final File file = new File(path);
		return file.exists();
	}


	public static int copy(String fromFile, String toFile) {
		//要复制的文件目录
		File[] currentFiles;
		File root = new File(fromFile);
		//如同判断SD卡是否存在或者文件是否存在
		//如果不存在则 return出去
		if (!root.exists()) {
			return -1;
		}
		//如果存在则获取当前目录下的全部文件 填充数组
		currentFiles = root.listFiles();

		//目标目录
		File targetDir = new File(toFile);
		//创建目录
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		//遍历要复制该目录下的全部文件
		for (int i = 0; i < currentFiles.length; i++) {
			if (currentFiles[i].isDirectory()) {
				copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

			} else {
				copyFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
			}
		}
		return 0;
	}


	public static int copyFile(String fromFile, String toFile) {
		try {
			InputStream fromStream = new FileInputStream(fromFile);
			OutputStream toStream = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fromStream.read(bt)) > 0) {
				toStream.write(bt, 0, c);
			}
			fromStream.close();
			toStream.close();
			return 0;
		} catch (Exception ex) {
			return -1;
		}
	}

	public static String getAssetsFile(String name) {
		InputStream is = null;
		BufferedInputStream bis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuilder stringBuilder = null;
		final AssetManager assetManager = TumlodrApp.getContext().getApplicationContext().getAssets();
		try {
			is = assetManager.open(name);
			bis = new BufferedInputStream(is);
			isr = new InputStreamReader(bis);
			br = new BufferedReader(isr);
			stringBuilder = new StringBuilder();
			String str;
			while ((str = br.readLine()) != null) {
				stringBuilder.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (bis != null) {
					bis.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (stringBuilder != null) {
			return stringBuilder.toString();
		} else {
			return null;
		}
	}
}
