package com.rising.anish.mysyncapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Anish Saha on 6/9/2017.
 */

public class PicturesActivity extends AppCompatActivity {
    /*IF STARTING ACTIVITY WITHOUT putExtra() IN INTENT, USE THIS
    Button uploadImages,uploadFiles;
    private SwipeRefreshLayout swipeContainer;
    private static final String SERVER_ADDRESS = "https://anishsaha12.000webhostapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictures_layout);

        new LoadImages(1).execute();



        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainerPictures);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                new LoadImages(2).execute();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



    }

    private class LoadImages extends AsyncTask<Void, Void, String> {

        private int loadMode;   // 1 = from memory/disk ; 2 = from server using internet

        public LoadImages(int loadMode){
            this.loadMode = loadMode;
        }

        @Override
        protected String doInBackground(Void... params) {



            if(loadMode==2) {
                //read from database the names of the images
                HttpParams httpRequestParams = getHttpRequestParams();
                HttpClient client = new DefaultHttpClient(httpRequestParams);
                HttpPost post = new HttpPost(SERVER_ADDRESS + "php/readPicsFilesDB.php");
                HttpResponse httpResponse = null;
                HttpEntity httpEntity = null;

                try {
                    httpResponse = client.execute(post);
                    httpEntity = httpResponse.getEntity();
                    String entityResponse = EntityUtils.toString(httpEntity);

                    Log.e("Entity Response  : ", entityResponse);
                    return entityResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{

                SharedPreferences pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String names = pref.getString("pictureNamesJSON", "");
                Log.e("Preferences- names: ", names);
                return names;
            }

            return null;
        }



        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            //store response string in shared preferences
            SharedPreferences pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("pictureNamesJSON", response);
            edit.commit();


            if(response!=null){
                //parse response
                String[] url = null;
                try {
                    JSONArray parent = new JSONArray(response);
                    url = new String[parent.length()];
                    for(int i=0; i<parent.length(); i++){
                        JSONObject finalObject = parent.getJSONObject(i);
                        url[i] = finalObject.getString("url");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //update Grid from net
                CustomGrid adapter = new CustomGrid(PicturesActivity.this, url, loadMode);
                GridView grid=(GridView)findViewById(R.id.grid);
                grid.setAdapter(adapter);
                final String[] finalName = url;
                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Toast.makeText(PicturesActivity.this, "You Clicked at " + finalName[+ position], Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PicturesActivity.this, ViewPicture.class);
                        intent.putExtra("url", finalName[+ position]);
                        startActivity(intent);
                    }
                });
            }
            swipeContainer.setRefreshing(false);
        }
    }

    private HttpParams getHttpRequestParams(){
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000*30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000*30);
        return httpRequestParams;
    }*/

    //IF THE ARRAYLIST IS PASSED IN THE INTENT USE THIS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pictures);

        ArrayList<ContDet> list =  (ArrayList<ContDet>)getIntent().getSerializableExtra("list");
        ArrayList<ContDet> contList = new ArrayList<ContDet>();

        for(int i=0;i<list.size();i++){
            ContDet contDet = list.get(i);
            if(Objects.equals(contDet.gettype(), "picture")){
                contList.add(contDet);

            }
        }
        String url[] = new String[contList.size()];
        for(int i=0;i<contList.size();i++){
            ContDet contDet = contList.get(i);
            url[i]=contDet.geturl();
        }

        CustomGrid adapter = new CustomGrid(PicturesActivity.this, url, 1);
        GridView grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        final String[] finalName = url;
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(PicturesActivity.this, "You Clicked at " + finalName[+ position], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PicturesActivity.this, ViewPicture.class);
                intent.putExtra("url", finalName[+ position]);
                startActivity(intent);
            }
        });

    }

}
