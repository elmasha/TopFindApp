package com.intech.topfindprovider.Models;

public class Counties {
    private String county;
    private long no;

    public Counties() {
        //empty constructor
    }

    public Counties(String county, long no) {
        this.county = county;
        this.no = no;
    }


    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public long getNo() {
        return no;
    }

    public void setNo(long no) {
        this.no = no;
    }

}
