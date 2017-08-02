package com.parasme.swopinfo.model;

import java.util.ArrayList;

/**
 * Created by SoNu on 8/2/2017.
 */

public class Retailer {
    private String retailerName, retailerLogo;
    private ArrayList<Store.Promotion> promotions;

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
}