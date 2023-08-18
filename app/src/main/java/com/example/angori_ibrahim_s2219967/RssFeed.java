package com.example.angori_ibrahim_s2219967;

public class RssFeed {
    private String title;
    private String guid;
    private String pubdate;
    private String category;
    private String description;
    private double rate;
    private String lastbuilddate;
    public RssFeed() {

    }
    public RssFeed(String title, String guid, String pubdate, String category, String description, String lastbuilddate, double rate) {
        this.title = title;
        this.guid = guid;
        this.pubdate = pubdate;
        this.category = category;
        this.description = description;
        this.lastbuilddate = lastbuilddate;
        this.rate = rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastbuilddate() {
        return lastbuilddate;
    }

    public void setLastbuilddate(String lastbuilddate) {
        this.lastbuilddate = lastbuilddate;
    }

    public double getRate() {


        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }


    @Override
    public String toString() {
        return "RssFeed{" +
                "title='" + title + '\'' +
                ", guid='" + guid + '\'' +
                ", pubdate='" + pubdate + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", lastbuilddate='" + lastbuilddate + '\'' +
                ", rate='" + rate + '\'' +
                '}';
    }


    public void setLastBuildDate(String currentData) {
    }

    public void setCurrencyCode(String currencyCode) {
    }
}
