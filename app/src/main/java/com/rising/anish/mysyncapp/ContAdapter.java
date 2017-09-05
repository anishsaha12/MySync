package com.rising.anish.mysyncapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Anish Saha on 6/11/2017.
 */

public class ContAdapter extends ArrayAdapter<ContDet> {

    Activity activity;
    int resourceId_pic, resourceId_msg, resourceId_doc;
    ArrayList<ContDet> data = new ArrayList<ContDet>();
    ContDet contDet;

    public ContAdapter(Activity activity, int resourceId_pic, int resourceId_msg, int resourceId_doc, ArrayList<ContDet> data) {
        super(activity, resourceId_pic, data);
        this.activity=activity;
        this.resourceId_pic=resourceId_pic;
        this.resourceId_msg=resourceId_msg;
        this.resourceId_doc=resourceId_doc;
        this.data=data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        contDet = data.get(position);
        if(contDet.gettype()=="picture"){
            PicHolder holder=null;

            LayoutInflater inflater=LayoutInflater.from(activity);
            row=inflater.inflate(resourceId_pic,parent,false);
            holder=new PicHolder();
            holder.imageView= (ImageView) row.findViewById(R.id.ivListPic);
            row.setTag(holder);


            Picasso mPicasso = Picasso.with(activity);
            mPicasso.setIndicatorsEnabled(true);
            mPicasso.load(contDet.geturl()).resize(250,250).centerCrop().into(holder.imageView);

            return row;

        }else if(contDet.gettype()=="message"){
            MsgHolder holder=null;

            LayoutInflater inflater=LayoutInflater.from(activity);
            row=inflater.inflate(resourceId_msg,parent,false);
            holder=new MsgHolder();
            holder.textView= (TextView) row.findViewById(R.id.tvListMsg);
            row.setTag(holder);


            holder.textView.setText(contDet.getcontent());
            return row;

        }else{  //doc
            DocHolder holder=null;

            LayoutInflater inflater=LayoutInflater.from(activity);
            row=inflater.inflate(resourceId_doc,parent,false);
            holder=new DocHolder();
            holder.textView= (TextView) row.findViewById(R.id.tvListDoc);
            row.setTag(holder);


            String txt = contDet.getname() + "\n" + contDet.geturl();

            holder.textView.setText(txt);
            return row;

        }

    }

    public void clearData() {
        data.clear();
    }


    class PicHolder
    {
        ImageView imageView;
    }

    class MsgHolder
    {
        TextView textView;
    }

    class DocHolder
    {
        TextView textView;
    }
}
