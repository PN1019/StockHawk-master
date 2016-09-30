package com.example.beetel.stockhawk.service;

import com.example.beetel.stockhawk.network.ResponseGetHistoricalData;
import com.example.beetel.stockhawk.network.ResponseGetStock;
import com.example.beetel.stockhawk.network.ResponseGetStocks;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by beetel on 13/06/2016.
 */
public interface StockDbService {
String BASE_URL="https://query.yahooapis.com";
   @GET("/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22YHOO%22%2C%22AAPL%22%2C%22GOOG%22%2C%22MSFT%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
retrofit2.Call<ResponseGetStocks>getStocks(@Query("q")String query);

    @GET("/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22YHOO%22%2C%22AAPL%22%2C%22GOOG%22%2C%22MSFT%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    retrofit2.Call<ResponseGetStock>getStock(@Query("q")String query);

    @GET("/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22YHOO%22%20and%20startDate%20%3D%20%222009-09-11%22%20and%20endDate%20%3D%20%222010-03-10%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    retrofit2.Call<ResponseGetHistoricalData>getStockHistoricalData(@Query("q")String query);


}
