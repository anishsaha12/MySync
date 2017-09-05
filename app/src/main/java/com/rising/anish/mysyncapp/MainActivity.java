package com.rising.anish.mysyncapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by Anish Saha on 6/11/2017.
 */

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PIC = 120;
    private static final int REQUEST_CODE_DOC = 121;
    private SwipeRefreshLayout swipeContainer;
    Button sendMenu, sendMessage;
    EditText messg;
    ListView listView;
    private String SERVER_ADDRESS = null;
    private static final String storageFileName = "DBcontentsJSON";
    ArrayList<ContDet> contList;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SERVER_ADDRESS = this.getString(R.string.SERVER_ADDRESS);

        setContentView(R.layout.drawer_layout);
        getWindow().setBackgroundDrawableResource(R.drawable.background) ;

        listView = (ListView) findViewById(R.id.lvContents);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        sendMenu = (Button) findViewById(R.id.btSendMenu);
        sendMessage = (Button) findViewById(R.id.btSendMessage);
        messg = (EditText) findViewById(R.id.etSendMessage);

        //nos*********************
        init();
        //************************

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ContDet contDet = (ContDet) parent.getItemAtPosition(position);
                if(contDet.gettype()=="picture"){
                    Intent intent = new Intent(MainActivity.this, ViewPicture.class);
                    intent.putExtra("url", contDet.geturl());
                    startActivity(intent);
                }else if(contDet.gettype()=="message"){

                }else{//doc
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(contDet.geturl()));
                    startActivity(intent);
                }

            }
        });

        getContent(2);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                getContent(1);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ?
                                0 : listView.getChildAt(0).getTop();
                swipeContainer.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        registerForContextMenu(listView);

        sendMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                registerForContextMenu(sendMenu);
                openContextMenu(sendMenu);
                unregisterForContextMenu(sendMenu);
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String msg = messg.getText().toString();
                ContDet item = new ContDet(null,null,null,"msg",msg);
                addToList(item);
                messg.setText("");
                new SendMessage(msg).execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        mNavigationView.setCheckedItem(R.id.nav_home);

    }

    private void init() {   //for drawer
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setCheckedItem(R.id.nav_home);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        /*Intent intent = new Intent(MainActivity.this, Test.class);
                        startActivity(intent);*/
                        break;
                    case R.id.nav_photos:
                        Intent intent = new Intent(MainActivity.this, PicturesActivity.class);
                        intent.putExtra("list",contList);
                        startActivity(intent);
                        break;
                    case R.id.nav_files:
                        Intent intent0 = new Intent(MainActivity.this, FilesActivity.class);
                        intent0.putExtra("list",contList);
                        startActivity(intent0);
                        break;
                    case R.id.nav_upload_pic:
                        Intent intent1 = new Intent(MainActivity.this, UploadImage.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_upload_file:
                        Intent intent2 = new Intent(MainActivity.this, UploadFile.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_refresh_database:
                        getContent(1);
                        swipeContainer.setRefreshing(true);
                        break;
                }
                return true;
            }
        });
    }

    // Inflate The Floating context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        if(v.getId()==R.id.btSendMenu){ //send button pressed
            inflater.inflate(R.menu.floating_context_menu_files, menu);
        }else {                         //list item long pressed
            inflater.inflate(R.menu.floating_context_menu_list, menu);
        }

    }

    // Method called when any menu item is clicked.
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                new DeleteFromPositionAndServer(info.position).execute();

                return true;

            case R.id.action_copy:
                AdapterView.AdapterContextMenuInfo info1 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int pos = info1.position;
                ContDet contItem = contList.get(pos);
                if(contItem.gettype()=="picture"){
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("picture url", contItem.geturl());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "URL copied to clip board", Toast.LENGTH_SHORT).show();
                }else if(contItem.gettype()=="message"){
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("message", contItem.getcontent());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "Message copied to clip board", Toast.LENGTH_SHORT).show();
                }else if(contItem.gettype()=="doc"){
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("file url", contItem.geturl());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "URL copied to clip board", Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.action_takePic:

                Toast.makeText(this, "Take pic", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.action_sendPic:

                Intent intent = new Intent(MainActivity.this, FileUpload.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, REQUEST_CODE_PIC);

                return true;

            case R.id.action_sendFile:

                Intent intent2 = new Intent(MainActivity.this, FileUpload.class);
                intent2.putExtra("type", 2);
                startActivityForResult(intent2, REQUEST_CODE_DOC);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void getContent(int mode) { //2=from stored JSON; 1=from internet

        if(mode==1) {
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_ADDRESS + "php/readFilesDB.php",

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //read from file

                                try {
                                    try {
                                        FileOutputStream fOut = openFileOutput(storageFileName, MODE_WORLD_READABLE);
                                        fOut.write(response.getBytes());
                                        fOut.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    contList = new ArrayList<ContDet>();
                                    JSONArray parent = new JSONArray(response);
                                    for (int i = 0; i < parent.length(); i++) {
                                        JSONObject finalObject = parent.getJSONObject(i);
                                        String id = finalObject.getString("id");
                                        String url = finalObject.getString("url");
                                        String name = finalObject.getString("name");
                                        String extension = finalObject.getString("extension");
                                        Log.e("extension", extension);
                                        String content = finalObject.getString("content");
                                        ContDet contDet = new ContDet(id, url, name, extension, content);
                                        contList.add(contDet);
                                        Log.e("id", contDet.getid());
                                    }

                                    ContAdapter contAdapter = null;
                                    contAdapter = new ContAdapter(MainActivity.this, R.layout.list_layout_pic, R.layout.list_layout_msg, R.layout.list_layout_doc, contList);

                                    listView.setAdapter(contAdapter);

                                    contAdapter.notifyDataSetChanged();
                                    swipeContainer.setRefreshing(false);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            swipeContainer.setRefreshing(false);
                            Log.e("error","Could not refresh "+error.toString());
                        }
                    }
            );

            stringRequest.setRetryPolicy(new DefaultRetryPolicy( 30000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue request = Volley.newRequestQueue(this);
            request.add(stringRequest);
            Log.e("timeout", String.valueOf(stringRequest.getTimeoutMs()));
        }else if(mode==2){
            String response="";
            try {
                FileInputStream fin = openFileInput(storageFileName);
                int c;
                while( (c = fin.read()) != -1){
                    response = response + Character.toString((char)c);
                }
            }
            catch(Exception e){
            }
            if(response!=""){
                try {
                    contList = new ArrayList<ContDet>();
                    JSONArray parent = new JSONArray(response);
                    for (int i = 0; i < parent.length(); i++) {
                        JSONObject finalObject = parent.getJSONObject(i);
                        String id = finalObject.getString("id");
                        String url = finalObject.getString("url");
                        String name = finalObject.getString("name");
                        String extension = finalObject.getString("extension");
                        Log.e("extension", extension);
                        String content = finalObject.getString("content");
                        ContDet contDet = new ContDet(id, url, name, extension, content);
                        contList.add(contDet);
                    }

                    ContAdapter contAdapter = null;
                    contAdapter = new ContAdapter(MainActivity.this, R.layout.list_layout_pic, R.layout.list_layout_msg, R.layout.list_layout_doc, contList);

                    listView.setAdapter(contAdapter);

                    contAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                    //getContent(1);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }else{
                getContent(1);
            }
        }
    }

    private void deleteFromPosition(int positionIndex){
        if(!contList.isEmpty()) {
            //add to list
            contList.remove(positionIndex);
            //

            ContAdapter contAdapter = null;
            contAdapter = new ContAdapter(MainActivity.this, R.layout.list_layout_pic, R.layout.list_layout_msg, R.layout.list_layout_doc, contList);

            listView.setAdapter(contAdapter);
            contAdapter.notifyDataSetChanged();
        }
    }


    private class DeleteFromPositionAndServer extends AsyncTask<Void, Void , String> {

        int positionIndex;
        String id;
        ContDet deleteItem;

        public DeleteFromPositionAndServer(int positionIndex){
            this.positionIndex = positionIndex;
            //delete from list
            deleteItem = contList.get(positionIndex);
            contList.remove(positionIndex);
            //

            ContAdapter contAdapter = null;
            contAdapter = new ContAdapter(MainActivity.this, R.layout.list_layout_pic, R.layout.list_layout_msg, R.layout.list_layout_doc, contList);

            listView.setAdapter(contAdapter);
            contAdapter.notifyDataSetChanged();
            id = deleteItem.getid();
        }

        @Override
        protected String doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id", id));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000*30);
            HttpConnectionParams.setSoTimeout(httpRequestParams, 1000*30);
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "php/deleteFileDB_id.php");
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
                        //******refresh stored JSON***
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_ADDRESS + "php/readFilesDB.php",

                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String resp) {
                                        //write to file
                                        try {
                                            FileOutputStream fOut = openFileOutput(storageFileName, MODE_WORLD_READABLE);
                                            fOut.write(resp.getBytes());
                                            fOut.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        Log.e("refresh",resp);
                                        //******refresh list items****
                                        getContent(2);
                                        //****************************

                                    }

                                },

                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }
                        );

                        RequestQueue request = Volley.newRequestQueue(MainActivity.this);
                        request.add(stringRequest);
                        //****************************


                    } else {  //if JSON response from server shows an error
                        Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        addToList(positionIndex,deleteItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("response", response);
            }else { //error
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                addToList(positionIndex,deleteItem);
            }
        }
    }

    private void addToList(ContDet additem){
        if(!contList.isEmpty()) {
            //add to list
            contList.add(additem);
            //

            ContAdapter contAdapter = null;
            contAdapter = new ContAdapter(MainActivity.this, R.layout.list_layout_pic, R.layout.list_layout_msg, R.layout.list_layout_doc, contList);

            listView.setAdapter(contAdapter);
            contAdapter.notifyDataSetChanged();
        }
    }

    private void addToList(int position, ContDet additem){
        if(!contList.isEmpty()) {
            //add to list
            contList.add(position,additem);
            //

            ContAdapter contAdapter = null;
            contAdapter = new ContAdapter(MainActivity.this, R.layout.list_layout_pic, R.layout.list_layout_msg, R.layout.list_layout_doc, contList);

            listView.setAdapter(contAdapter);
            contAdapter.notifyDataSetChanged();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REQUEST_CODE_PIC) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                int success = bundle.getInt("success");
                if(success==0) {//error
                    Log.e("success", "no");
                }else{
                    Log.e("success", "yes");
                    String resp = bundle.getString("response");
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if(jsonObject.getBoolean("error")){ //if JSON response from server shows an error
                            Toast.makeText(MainActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        }else{  //no error
                            ContDet item = new ContDet(null,null,null,"jpg",null);
                            addToList(item);
                            getContent(1);  //from net
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("response", resp);
                }
            }
        }else if (requestCode == REQUEST_CODE_DOC) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                int success = bundle.getInt("success");
                if(success==0) {//error
                    Log.e("success", "no");
                }else {
                    Log.e("success", "yes");
                    String resp = bundle.getString("response");
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if(jsonObject.getBoolean("error")){ //if JSON response from server shows an error
                            Toast.makeText(MainActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        }else{  //no error
                            ContDet item = new ContDet(null,null,null,"file",null);
                            addToList(item);
                            getContent(1);  //from net
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("response", resp);
                }
            }
        }
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
                        //******refresh stored JSON***
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_ADDRESS + "php/readFilesDB.php",

                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String resp) {
                                        //write to file
                                        try {
                                            FileOutputStream fOut = openFileOutput(storageFileName, MODE_WORLD_READABLE);
                                            fOut.write(resp.getBytes());
                                            fOut.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        Log.e("refresh",resp);
                                        //******refresh list items****
                                        getContent(2);
                                        //****************************

                                    }

                                },

                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }
                        );

                        RequestQueue request = Volley.newRequestQueue(MainActivity.this);
                        request.add(stringRequest);
                        //****************************


                    } else {  //if JSON response from server shows an error
                        Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        deleteFromPosition(contList.size()-1);
                        messg.setText(content);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("response", response);
            }else { //error
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                deleteFromPosition(contList.size()-1);
                messg.setText(content);
            }
        }
    }
}
