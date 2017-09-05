package com.rising.anish.mysyncapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Anish Saha on 6/9/2017.
 */

public class CustomGrid extends BaseAdapter {

    private Context mContext;
    private final String[] url;
    Picasso mPicasso = null;
    private int loadMode;

    public CustomGrid(Context c, String[] url, int LM ) {
        mContext = c;
        this.url = url;
        loadMode = LM;
    }

    @Override
    public int getCount() {
        return url.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.single_grid, null);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            mPicasso = Picasso.with(mContext);
            mPicasso.setIndicatorsEnabled(true);

            if(loadMode==1){
                mPicasso.load(url[position]).resize(300,300).centerCrop().into(imageView);
                /*
                  this statement loads image. order of search image in Picasso is: Memory cache -> Disk cache -> Network
                  in order to directly load from memory or disk, networkPolicy has been added
                  even if not added it will load from memory/disk if available.
                */
            }
            else if(loadMode==2) {
                mPicasso.load(url[position]).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE).resize(100,100).centerCrop().into(imageView);
                /*
                  mPicasso.load(SERVER_ADDRESS + "pictures/" + name + ".JPG").into(downloadedImage);
                  this statement loads image. order of search image in Picasso is: Memory cache -> Disk cache -> Network
                  in order to directly load from network memoryPolicy and networkPolicy have been added
                 */
            }

        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}
