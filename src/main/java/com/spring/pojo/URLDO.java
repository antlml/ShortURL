package com.spring.pojo;

import java.util.Date;

public class URLDO {
    private Integer id;
    private String url;
    private String initialURL;
    private Date gmtCreate;
    private String idxHash;

    public URLDO() {
    }
    public URLDO(Integer id, String url, String initialURL, Date gmtCreate,
                 String idxHash) {
        this.id = id;
        this.url = url;
        this.initialURL = initialURL;
        this.gmtCreate = gmtCreate;
        this.idxHash = idxHash;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInitialURL() {
        return initialURL;
    }

    public void setInitialURL(String initialURL) {
        this.initialURL = initialURL;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getIdxHash() {
        return idxHash;
    }

    public void setIdxHash(String idxHash) {
        this.idxHash = idxHash;
    }
}
