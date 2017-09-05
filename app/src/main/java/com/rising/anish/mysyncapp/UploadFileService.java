package com.rising.anish.mysyncapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.UUID;

/**
 * Created by Anish Saha on 6/19/2017.
 */

public class UploadFileService extends IntentService implements UploadStatusDelegate {
    public String UPLOAD_URL = null;
    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadFileService() {
        super("UploadService");
        UPLOAD_URL = this.getString(R.string.UPLOAD_URL);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        uploadReceiver.register(UploadFileService.this);

        String path = intent.getStringExtra("path");
        String name = intent.getStringExtra("name");

        if (path == null) {
            Toast.makeText(UploadFileService.this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();
                uploadReceiver.setDelegate(this);
                uploadReceiver.setUploadID(uploadId);

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("name", name) //Adding text parameter to the request
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload


            } catch (Exception exc) {
                Toast.makeText(UploadFileService.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {

    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
        uploadReceiver.unregister(this);
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        Toast.makeText(UploadFileService.this, "Uploaded!", Toast.LENGTH_SHORT).show();
        uploadReceiver.unregister(this);
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        uploadReceiver.unregister(this);
    }
}
