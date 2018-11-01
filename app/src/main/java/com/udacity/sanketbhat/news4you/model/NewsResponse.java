package com.udacity.sanketbhat.news4you.model;

import com.google.gson.annotations.SerializedName;

public class NewsResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("code")
    private String errorCode;

    @SerializedName("message")
    private String message;

    @SerializedName("totalResults")
    private int totalResults;

    @SerializedName("articles")
    private Article[] articles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public Article[] getArticles() {
        return articles;
    }

    public void setArticles(Article[] articles) {
        this.articles = articles;
    }
}
