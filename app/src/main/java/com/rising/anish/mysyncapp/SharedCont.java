package com.rising.anish.mysyncapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Anish Saha on 6/19/2017.
 */

public class SharedCont extends AppCompatActivity {

    private int type;
    private Uri receivedUri;
    private String receivedText;
    private String path;
    private String name;
    private String SERVER_ADDRESS = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SERVER_ADDRESS = this.getString(R.string.SERVER_ADDRESS);

        setContentView(R.layout.shared_content_upload);

        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();

        final EditText editText = (EditText) findViewById(R.id.etSend);
        Button button = (Button) findViewById(R.id.btSend);

        if(receivedType.startsWith("text/")){
            //handle sent text
            receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
            editText.setText(receivedText);
            type =  0;
        }
        else{
            //handle sent file
            type = 1;
            receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            path = FilePath.getPath(this, receivedUri);
            name = path.substring(path.lastIndexOf("/")+1);
            editText.setText(name);
        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(type==0){
                    Toast.makeText(SharedCont.this,"Sending...",Toast.LENGTH_SHORT).show();
                    receivedText = editText.getText().toString();
                    new SendMessage(receivedText).execute();
                }else{
                    name = editText.getText().toString();
                    Intent intent = new Intent(SharedCont.this, UploadFileService.class);
                    // add infos for the service which file to download and where to store
                    intent.putExtra("path", path);
                    intent.putExtra("name",name);
                    startService(intent);
                    finish();
                }
            }
        });
    }

    private class SendMessage extends AsyncTask<Void, Void , String> {

        String extension, content;

        public SendMessage(String content){
            this.extension = "msg";
            this.content = content;
        }

        @Override
        protected String doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("extension", extension));
            dataToSend.add(new BasicNameValuePair("content", content));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000*30);
            HttpConnectionParams.setSoTimeout(httpRequestParams, 1000*30);
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "php/insertMsgFilesDB.php");
            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                httpResponse = client.execute(post);
                httpEntity = httpResponse.getEntity();
                String entityResponse = EntityUtils.toString(httpEntity);

                return entityResponse;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response!=null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("error")==false) { //no error
                        finish();
                    } else {  //if JSON response from server shows an error
                        Toast.makeText(SharedCont.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("response", response);
            }else { //error
                Toast.makeText(SharedCont.this, "Error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
