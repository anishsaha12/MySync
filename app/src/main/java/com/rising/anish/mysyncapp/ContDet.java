package com.rising.anish.mysyncapp;

import java.io.Serializable;

/**
 * Created by Anish Saha on 6/11/2017.
 */

public class ContDet implements Serializable{
    private String id;
    private String url;
    private String name;
    private String extension;
    private String content;
    private String type;

    public ContDet(String i,String ur,String nme,String exten,String cntnt){
        id=i;
        url=ur;
        name=nme;
        extension=exten;
        content=cntnt;
        if(extension.contains("jpg")){
            type = "picture";
        }else if(extension.contains("msg")){
            type = "message";
        }else{
            type = "doc";
        }
    }

    public String getid(){
        return id;
    }
    public String geturl(){
        return url;
    }
    public String getname(){
        return name;
    }
    public String getextension(){
        return extension;
    }
    public String getcontent(){
        return content;
    }

    public String gettype(){
        return type;
    }
}
