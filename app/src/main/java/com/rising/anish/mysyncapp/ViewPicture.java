package com.rising.anish.mysyncapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Anish Saha on 6/9/2017.
 */

public class ViewPicture extends AppCompatActivity {

    ImageView pic;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pic);


        url=null;
        pic = (ImageView) findViewById(R.id.ivViewPic);
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");

        Picasso mPicasso = Picasso.with(this);
        mPicasso.setIndicatorsEnabled(true);
        mPicasso.load(url).into(pic);
    }
}
