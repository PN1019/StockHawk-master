package com.example.beetel.stockhawk.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beetel on 13/06/2016.
 */
@SuppressWarnings("unused")
public class ResponseGetHistoricalData {
    @SerializedName("query")
    private Results sResults;

    public List<Quote>getHistoricData() {
        List<Quote> result = new ArrayList<>();
        if (sResults.getQuote() != null) {
            List<Quote> quotes = sResults.getQuote().getStockQuotes();
            for (Quote quote : quotes) {
                result.add(quote);
            }
        }
        return result;
    }
@SuppressWarnings("unused")
public class Results{
    @SerializedName("count")
    private String sCount;
    @SerializedName("results")
    private Quotes sQuote;

    public Quotes getQuote() {
        return sQuote;
    }
}

public class Quotes{
    @SerializedName("quote")
    private List<Quote>stockQuotes=new ArrayList<>();

    public List<Quote> getStockQuotes() {
        return stockQuotes;
    }
}
public class Quote {
    @SerializedName("Symbol")
    private String sSymbol;
    @SerializedName("Date")
    private String sDate;
    @SerializedName("Low")
    private String sLow;
    @SerializedName("High")
    private String sHigh;
    @SerializedName("Open")
    private String sOpen;


    public String getSymbol() {
        return sSymbol;
    }

    public String getDate() {
        return sDate;
    }

    public String getOpen() {
        return sOpen;
    }

    public String getHigh() {
        return sHigh;
    }

    public String getLow() {
        return sLow;
    }
  }
}