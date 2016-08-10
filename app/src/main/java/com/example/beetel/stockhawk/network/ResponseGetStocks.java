package com.example.beetel.stockhawk.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beetel on 13/06/2016.
 */
@SuppressWarnings("unused")
public class ResponseGetStocks {
    @SerializedName("query")
    private StockResults sResults;
public List<StockQuote>getStockQuotes(){
    List<StockQuote> result=new ArrayList<>();
    List<StockQuote>stockQuotes=sResults.getQuote().getStockQuotes();
    for (StockQuote  stockQuote:stockQuotes){
        if(stockQuote.getBid()!=null&&stockQuote.getChangeInPercent()!=null&&stockQuote.getChange()!=null){
            result.add(stockQuote);
        }
    }
    return result;
}
    @SuppressWarnings("unused")
    public class StockResults{
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
            private List<StockQuote> stockQuoteList=new ArrayList<>();

            public List<StockQuote>getStockQuotes(){
                return stockQuoteList;
        }
    }
}
