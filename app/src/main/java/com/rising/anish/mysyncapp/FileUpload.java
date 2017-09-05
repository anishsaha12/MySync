package com.rising.anish.mysyncapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.UUID;

/**
 * Created by Anish Saha on 6/13/2017.
 */

public class FileUpload extends AppCompatActivity implements UploadStatusDelegate {
    public String UPLOAD_URL = null;
    //File request code
    private int PICK_FILE_REQUEST = 1;
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Uri to store the image uri
    private Uri filePath;
    private int uploadType; //1=image, 2=file

    private ProgressBar spinner;

    Activity context = null;

    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UPLOAD_URL = this.getString(R.string.UPLOAD_URL);

        setContentView(R.layout.empty_layout);
        context = this;

        Bundle bundle = getIntent().getExtras();
        uploadType = bundle.getInt("type");

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        showFileChooser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }


    public void uploadMultipart() {
        //getting the actual path of the image
        String path = FilePath.getPath(context, filePath);
        String name = path.substring(path.lastIndexOf("/")+1);


        if (path == null) {

            Toast.makeText(context, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();
                uploadReceiver.setDelegate(this);
                uploadReceiver.setUploadID(uploadId);

                spinner.setVisibility(View.VISIBLE);

                //Creating a multi part request
                new MultipartUploadRequest(context, uploadId, UPLOAD_URL)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("name", name) //Adding text parameter to the request
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload


            } catch (Exception exc) {
                Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        //finish();
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        if(uploadType==1){
            intent.setType("image/*");
        }else if(uploadType==2){
            intent.setType("*/*");
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }

    //handling the file chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadMultipart();
        }else {
            Intent intent = new Intent();
            intent.putExtra("success", 0);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        Log.e("uploaded:", String.valueOf((uploadInfo.getProgressPercent())));
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
        Log.e("fail:", "Couldn't upload");
        spinner.setVisibility(View.GONE);
        Toast.makeText(context, "Cannot Upload", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.putExtra("success", 0);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        spinner.setVisibility(View.GONE);

        Intent intent = new Intent();
        Bundle extras = new Bundle();
        extras.putInt("success", 1);
        extras.putString("response", serverResponse.getBodyAsString());
        intent.putExtras(extras);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {

    }
}
