package com.hochan.tumlodr.ui.component;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mMs;
    private String  mFilePath;

    public SingleMediaScanner(Context context, String filePath) {
        mFilePath = filePath;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        if (mFilePath.endsWith(".mp4")) {
            mMs.scanFile(mFilePath, "video/mp4");
        }else {
            mMs.scanFile(mFilePath, "image/jpeg");
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
    }
}
