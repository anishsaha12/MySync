package com.rising.anish.mysyncapp;

import android.content.Context;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;
import net.gotev.uploadservice.UploadStatusDelegate;

/**
 * Created by Anish Saha on 6/13/2017.
 */

public class SingleUploadBroadcastReceiver extends UploadServiceBroadcastReceiver {

    private String mUploadID;
    private UploadStatusDelegate mDelegate;

    public void setUploadID(String uploadID) {
        mUploadID = uploadID;
    }

    public void setDelegate(UploadStatusDelegate delegate) {
        mDelegate = delegate;
    }

    public SingleUploadBroadcastReceiver() {
        super();
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null) {
            mDelegate.onProgress(context, uploadInfo);
        }
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
        if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null) {
            mDelegate.onError(context, uploadInfo, exception);
        }
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null) {
            mDelegate.onCompleted(context, uploadInfo, serverResponse);
        }
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null) {
            mDelegate.onCancelled(context, uploadInfo);
        }
    }
}