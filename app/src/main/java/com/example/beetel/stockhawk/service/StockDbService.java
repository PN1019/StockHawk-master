package com.example.beetel.stockhawk.service;

import com.example.beetel.stockhawk.network.ResponseGetHistoricalData;
import com.example.beetel.stockhawk.network.ResponseGetStocks;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by beetel on 13/06/2016.
 */
public interface StockDbService {
String BASE_URL="http://query.yahooapis.com";
@GET("/v1/public/yql?"+"format=json&diagnostics=true&"+"env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
retrofit2.Call<ResponseGetStocks>getStocks(@Query("q")String query);

    @GET("/v1/public/yql?"+"format=json&diagnostics=true&"+"env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    retrofit2.Call<ResponseGetStocks>getStock(@Query("q")String query);

    @GET("/v1/public/yql?"+"format=json&diagnostics=true&"+"env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    retrofit2.Call<ResponseGetHistoricalData>getStockHistoricalData(@Query("q")String query);


}
