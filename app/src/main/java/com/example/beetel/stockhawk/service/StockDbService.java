package com.example.beetel.stockhawk.service;

import com.example.beetel.stockhawk.network.ResponseGetHistoricalData;
import com.example.beetel.stockhawk.network.ResponseGetStock;
import com.example.beetel.stockhawk.network.ResponseGetStocks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by beetel on 13/06/2016.
 */
public interface StockDbService {
String BASE_URL="https://query.yahooapis.com";

    @GET("/v1/public/yql?" + "format=json&diagnostics=true&" + "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseGetStocks> getStocks(@Query("q") String query);

    @GET("/v1/public/yql?" + "format=json&diagnostics=true&" + "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseGetStock> getStock(@Query("q") String query);

    @GET("/v1/public/yql?" + "format=json&diagnostics=true&" + "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseGetHistoricalData> getStockHistoricalData(@Query("q") String query);


}
