package com.ads.dataObjects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Campaign implements Comparable<Campaign>{
    private static SimpleDateFormat sdf;
    private double bid;
    private String category;
    private Calendar startDate;
    private Calendar endDate;
    private String name;
    private MyPriotityQue<Product> products;




    public Campaign(double bid, String category,String startDate, String name) throws ParseException {
        setDates(startDate);
        this.bid = bid;
        this.category = category;
        this.name = name;
    }

    private void setDates(String startDate) throws ParseException {
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date d = sdf.parse(startDate);
        this.startDate = Calendar.getInstance();
        this.endDate=Calendar.getInstance();
        this.startDate.setTime(d);
        this.endDate.setTime(d);
        this.endDate.add(Calendar.DATE,10);
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public MyPriotityQue<Product> getProducts() {
        return products;
    }

    public void setProducts(MyPriotityQue<Product> products) {
        this.products = products;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int compareTo(Campaign other) {

        if(this.getBid() <other.getBid()) {
            return 1;
        } else if (this.getBid() > other.getBid()) {
            return -1;
        } else {
            return 0;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campaign campaign = (Campaign) o;
        return Double.compare(campaign.getBid(), getBid()) == 0 &&
                campaign.getName().equals(getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, bid);
    }


    /**
     * there are two dates in this Class:
     * startDate = this is the day this Campaign starts
     * endDate = this is startDate+10 days
     * this method checks if today's date is'nt before startDate (giving the user option
     * to add future Campaigns) or endDate is'nt after today's day
     * @return
     */
    public boolean isActive(){
        if(this.startDate.compareTo(Calendar.getInstance())<=0
                && this.endDate.compareTo(Calendar.getInstance())>=0)return true;
        else return false;
    }
}
