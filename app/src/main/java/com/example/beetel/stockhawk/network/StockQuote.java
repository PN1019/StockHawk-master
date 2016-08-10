package com.example.beetel.stockhawk.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by beetel on 12/06/2016.
 */
@SuppressWarnings("unused")
public class StockQuote {
@SerializedName("Change")
    private String sChange;

    @SerializedName("symbol")
    private String sSymbol;
    @SerializedName("Name")
    private String sName;

    @SerializedName("Bid")
    private String sBid;
    @SerializedName("ChangeInPercent")
    private String sChangeInPercent;

    public String getChange() {
        return sChange;
    }

    public String getBid() {
        return sBid;
    }

    public String getSymbol() {
        return sSymbol;
    }

    public String getName() {
        return sName;
    }

    public String getChangeInPercent() {
        return sChangeInPercent;
    }
}
