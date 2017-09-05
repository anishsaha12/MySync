package com.rising.anish.mysyncapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Anish Saha on 6/16/2017.
 */

public class FilesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_layout);

        ArrayList<ContDet> list =  (ArrayList<ContDet>)getIntent().getSerializableExtra("list");
        ArrayList<ContDet> contList = new ArrayList<ContDet>();
        ListView listView = (ListView) findViewById(R.id.filesList);

        for(int i=0;i<list.size();i++){
            ContDet contDet = list.get(i);
            if(Objects.equals(contDet.gettype(), "doc")){
                contList.add(contDet);

            }
        }

        ContAdapter contAdapter = null;
        contAdapter = new ContAdapter(FilesActivity.this, R.layout.list_layout_pic, R.layout.list_layout_msg, R.layout.list_layout_doc, contList);

        listView.setAdapter(contAdapter);

        contAdapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ContDet contDet = (ContDet) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(contDet.geturl()));
                startActivity(intent);

            }
        });

    }
}
