package com.rising.anish.mysyncapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Anish Saha on 6/9/2017.
 */

public class UploadImage extends AppCompatActivity implements View.OnClickListener{

    ImageView imageToUpload, downloadedImage;
    Button bUploadImage, bDownloadImage, bSelectImage;
    EditText uploadImageName, downloadImageName;
    TextView databaseData;
    private static final int RESULT_LOAD_IMAGE = 1;
    private String SERVER_ADDRESS = null;

    private SwipeRefreshLayout swipeContainer;
    Picasso mPicasso = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SERVER_ADDRESS = this.getString(R.string.SERVER_ADDRESS);

        setContentView(R.layout.layout);

        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        downloadedImage = (ImageView) findViewById(R.id.downloadedImage);

        bSelectImage = (Button) findViewById(R.id.btSelectImageToUpload);
        bUploadImage = (Button) findViewById(R.id.btUploadImage);
        bDownloadImage = (Button) findViewById(R.id.btDownloadImage);

        uploadImageName = (EditText) findViewById(R.id.etUploadNames);
        downloadImageName = (EditText) findViewById(R.id.etDownloadName);

        databaseData = (TextView) findViewById(R.id.tvDB);
        new ReadDatabase().execute();

        bSelectImage.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);
        bDownloadImage.setOnClickListener(this);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                new ReadDatabase().execute();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



    }

    @Override
    public void onClick(View v) {
        Bitmap image;
        String name;
        switch (v.getId()){
            case R.id.btSelectImageToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.btUploadImage:
                image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
                name = uploadImageName.getText().toString();
                new UploadAnImage(image,name).execute();
                break;
            case R.id.btDownloadImage:
                name = downloadImageName.getText().toString();
                //new DownloadImage(name).execute();
                mPicasso.load(SERVER_ADDRESS + "pictures/" + name + ".JPG")
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE).into(downloadedImage);

                break;
        }
    }

    /*private void loadImagePicasso(String url){
        Picasso.with(getApplicationContext())
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(downloadedImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(getApplicationContext())
                                .load(url)
                                .into(downloadedImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso","Could not fetch image");
                                    }
                                });
                    }
                });
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(null);
            imageToUpload.setImageURI(selectedImage);
            bSelectImage.setText("Change Image");
            //nos***
            String path = selectedImage.toString();
            String nam = "IMG_" + path.substring(path.lastIndexOf("/")+1);
            uploadImageName.setText(nam);
            //******
        }
    }

    private class UploadAnImage extends AsyncTask<Void, Void , Void> {

        Bitmap image;
        String name;

        public UploadAnImage(Bitmap image, String name){
            this.image = image;
            this.name = name;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("image", encodedImage));
            dataToSend.add(new BasicNameValuePair("name", name));

            HttpParams httpRequestParams = getHttpRequestParams();
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "php/SavePictureUpdateDatabase.php");
            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                httpResponse = client.execute(post);
                httpEntity = httpResponse.getEntity();
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response  : ", entityResponse);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadImage extends AsyncTask<Void, Void, Bitmap>{

        String name;

        public DownloadImage(String name){
            this.name = name;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            String url = SERVER_ADDRESS + "pictures/" + name + ".jpg";

            try{
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000*30);
                connection.setReadTimeout(1000*30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if(bitmap!=null){
                downloadedImage.setImageBitmap(bitmap);
            }
        }
    }

    private class ReadDatabase extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {

            HttpParams httpRequestParams = getHttpRequestParams();
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "php/readDB.php");
            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;

            try{
                httpResponse = client.execute(post);
                httpEntity = httpResponse.getEntity();
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response  : ", entityResponse);
                return entityResponse;
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response!=null){
                databaseData.setText(response);
            }
            swipeContainer.setRefreshing(false);
        }
    }

    private HttpParams getHttpRequestParams(){
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000*30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000*30);
        return httpRequestParams;
    }
}

