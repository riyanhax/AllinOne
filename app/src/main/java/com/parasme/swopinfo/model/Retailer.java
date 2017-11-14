package com.parasme.swopinfo.model;

import java.util.ArrayList;

/**
 * Created by SoNu on 8/2/2017.
 */

public class Retailer {
    private String retailerName="", retailerLogo, retailerMessage;
    private String retailerId;
    private ArrayList<Store.Promotion> promotions;
    private String storeId;

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getRetailerLogo() {
        return retailerLogo;
    }

    public void setRetailerLogo(String retailerLogo) {
        this.retailerLogo = retailerLogo;
    }

    public ArrayList<Store.Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(ArrayList<Store.Promotion> promotions) {
        this.promotions = promotions;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getRetailerMessage() {
        return retailerMessage;
    }

    public void setRetailerMessage(String retailerMessage) {
        this.retailerMessage = retailerMessage;
    }
}