package com.udacity.sanketbhat.news4you.model;

import com.google.gson.annotations.SerializedName;

public class Source {

    @SerializedName("id")
    private String sid;

    @SerializedName("name")
    private String name;

    public String getSid() {
        return sid;
    }

    public void setSid(String id) {
        this.sid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
