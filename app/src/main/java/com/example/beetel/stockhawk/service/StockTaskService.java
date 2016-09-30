package com.example.beetel.stockhawk.service;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.util.Log;

import com.example.beetel.stockhawk.data.QuoteColumns;
import com.example.beetel.stockhawk.data.QuoteProvider;
import com.example.beetel.stockhawk.data.QuotesHistoricalDataColumns;
import com.example.beetel.stockhawk.network.ResponseGetHistoricalData;
import com.example.beetel.stockhawk.network.ResponseGetStock;
import com.example.beetel.stockhawk.network.ResponseGetStocks;
import com.example.beetel.stockhawk.network.StockQuote;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by example on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockTaskService extends GcmTaskService{
    private String LOG_TAG = StockTaskService.class.getSimpleName();
    private final static String INIT_QUOTES="\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\"";
    public final static String TAG_PERIODIC="periodic";
//    public static final int STATUS_OK=0;
//    public static final int STATUS_ERROR_JSON=1;
//    public static final int STATUS_SERVER_ERROR=2;
//    public static final int STATUS_SERVER_DOWN=3;
//    public static final int STATUS_NO_NETWORK=4;
//    public static final int STATUS_UNKNOWN=5;
//
//    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({STATUS_OK,STATUS_SERVER_ERROR,STATUS_NO_NETWORK,STATUS_ERROR_JSON,STATUS_UNKNOWN,STATUS_SERVER_DOWN})
//    public @interface StockStatuses{
//
//    }


    private OkHttpClient client = new OkHttpClient();
    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean isUpdate;
@SuppressWarnings("unused")
    public StockTaskService(){}

    public StockTaskService(Context context){
        mContext = context;
    }

//    String fetchData(String url) throws IOException{
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        return response.body().string();    }


    @Override
    public int onRunTask(TaskParams params) {
//        Cursor initQueryCursor;
        if (mContext == null) {
            return GcmNetworkManager.RESULT_FAILURE;
        }
//        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(StockDbService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .build();
            StockDbService service = retrofit.create(StockDbService.class);
            String multiple_stocks_query = "select * from yahoo.finance.quotes where symbol in(" + buildUrl(params) + ")";
            String single_stock_query="select * from yahoo.finance.quote where symbol in ("+buildUrl(params)+")";
            //for multiple stocks
            if (params.getTag().equals(StockIntentService.ACTION_INIT)) {

                Call<ResponseGetStocks> responseGetStocksCall = service.getStocks(multiple_stocks_query);
                Response<ResponseGetStocks> responseGetStocksResponse = responseGetStocksCall.execute();
                ResponseGetStocks responseGetStocks = responseGetStocksResponse.body();
                saveQuotes2Database(responseGetStocks.getStockQuotes());
            }
            //for single stocks
            else {
                Call<ResponseGetStock> responseGetStockCall = service.getStock(single_stock_query);
                Response<ResponseGetStock> responseGetStockResponse = responseGetStockCall.execute();
                ResponseGetStock responseGetStock = responseGetStockResponse.body();
                saveQuotes2Database(responseGetStock.getStockQuotes());

            }
            return GcmNetworkManager.RESULT_SUCCESS;
        }
        catch (SocketTimeoutException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IOException | RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return GcmNetworkManager.RESULT_FAILURE;
        }
        return 0;
    }
    // Base URL for the Yahoo query
//      urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
//      urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.quotes where symbol "
//        + "in (", "UTF-8"));
//    } catch (UnsupportedEncodingException e) {
//      e.printStackTrace();
//    }
//    if (params.getTag().equals("init") || params.getTag().equals("periodic")){
//      isUpdate = true;
//      initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
//          new String[] { "Distinct " + QuoteColumns.SYMBOL }, null,
//          null, null);
//      if (initQueryCursor.getCount() == 0 || initQueryCursor == null){
//        // Init task. Populates DB with quotes for the symbols seen below
//        try {
//          urlStringBuilder.append(
//              URLEncoder.encode("\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")", "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//          e.printStackTrace();
//        }
//      } else if (initQueryCursor != null){
//        DatabaseUtils.dumpCursor(initQueryCursor);
//        initQueryCursor.moveToFirst();
//        for (int i = 0; i < initQueryCursor.getCount(); i++){
//          mStoredSymbols.append("\""+
//              initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol"))+"\",");
//          initQueryCursor.moveToNext();
//        }
//        mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
//        try {
//          urlStringBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//          e.printStackTrace();
//        }
//      }
//    } else if (params.getTag().equals("add")){
//      isUpdate = false;
//      // get symbol from params.getExtra and build query
//      String stockInput = params.getExtras().getString("symbol");
//      try {
//        urlStringBuilder.append(URLEncoder.encode("\""+stockInput+"\")", "UTF-8"));
//      } catch (UnsupportedEncodingException e){
//        e.printStackTrace();
//      }
//    }
////    // finalize the URL for the API query.
//    urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
//        + "org%2Falltableswithkeys&callback=");
//
//    String urlString;
//    String getResponse;
//    int result = GcmNetworkManager.RESULT_FAILURE;
//
//    if (urlStringBuilder != null){
//      urlString = urlStringBuilder.toString();
//      try{
//        getResponse = fetchData(urlString);
//        result = GcmNetworkManager.RESULT_SUCCESS;
//        try {
//          ContentValues contentValues = new ContentValues();
//          // update ISCURRENT to 0 (false) so new data is current
//          if (isUpdate){
//            contentValues.put(QuoteColumns.ISCURRENT, 0);
//            mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
//                null, null);
//          }
//          mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
//              Utils.quoteJsonToContentVals(getResponse));
//        }catch (RemoteException | OperationApplicationException e){
//          Log.e(LOG_TAG, "Error applying batch insert", e);
//        }
//      } catch (IOException e){
//        e.printStackTrace();
//      }
//    }
//
////    return result;
//  }
    private String buildUrl(TaskParams params)throws UnsupportedEncodingException
    {
        ContentResolver resolver=mContext.getContentResolver();
        if (params.getTag().equals(StockIntentService.ACTION_INIT)||params.getTag().equals(TAG_PERIODIC)){
            isUpdate=true;
            Cursor cursor=resolver.query(QuoteProvider.Quotes.CONTENT_URI,new String[]{"Distinct'" + QuoteColumns.SYMBOL+"'"},null,null,null);

            if (cursor!=null&&cursor.getCount()==0||cursor==null){
                return INIT_QUOTES;
            }
            else {
                DatabaseUtils.dumpCursor(cursor);
                cursor.moveToFirst();
                for (int i=0;i<cursor.getCount();i++){
                    mStoredSymbols.append("\"");
                    mStoredSymbols.append(cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));
                    mStoredSymbols.append("\",");
                    cursor.moveToNext();
                }
                mStoredSymbols.replace(mStoredSymbols.length()-1,mStoredSymbols.length(),"");
                return mStoredSymbols.toString();
            }
        }
        else if (params.getTag().equals(StockIntentService.ACTION_ADD)){
            isUpdate=false;
            String stockInput=params.getExtras().getString(StockIntentService.EXTRA_SYMBOL);
            return "\""+stockInput+"\"";
        }
        else {
            throw new IllegalStateException("Action not specified in TaskParams.");
        }
    }
    private void saveQuotes2Database(List<StockQuote>quotes)throws RemoteException,OperationApplicationException{
        ContentResolver resolver=mContext.getContentResolver();
        ArrayList<ContentProviderOperation>batchOperations=new ArrayList<>();
        for (StockQuote quote:quotes){
            batchOperations.add(QuoteProvider.buildBatchOperation(quote));
        }
        if(isUpdate)
        {
            ContentValues contentValues=new ContentValues();
            contentValues.put(QuoteColumns.ISCURRENT,0);
            resolver.update(QuoteProvider.Quotes.CONTENT_URI,contentValues,null,null);
        }
        resolver.applyBatch(QuoteProvider.AUTHORITY,batchOperations);
        for(StockQuote quote:quotes){
            try
            {
                loadHistoricalData(quote);

            }
            catch (IOException|RemoteException|OperationApplicationException e){
                Log.e(LOG_TAG,e.getMessage(),e);
            }
        }
    }
    private void loadHistoricalData(StockQuote quote) throws IOException,RemoteException,OperationApplicationException{
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currrentDate=new Date();

        Calendar calEndDate=Calendar.getInstance();
        calEndDate.setTime(currrentDate);
        calEndDate.add(Calendar.MONTH,0);

        Calendar calStartDate=Calendar.getInstance();
        calStartDate.setTime(currrentDate);
        calStartDate.add(Calendar.MONTH,-1);

        String startDate=dateFormat.format(calStartDate.getTime());
        String endDate=dateFormat.format(calEndDate.getTime());

        String query="select * from yahoo.finance.historicaldata where symbol=\""+quote.getSymbol()+
                "\"and startDate=\""+startDate+"\"and endDate=\""+endDate+"\"";
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(StockDbService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StockDbService stockDbService=retrofit.create(StockDbService.class);
        Call<ResponseGetHistoricalData>call=stockDbService.getStockHistoricalData(query);
        retrofit2.Response<ResponseGetHistoricalData>response;
        response=call.execute();
        ResponseGetHistoricalData responseGetHistoricalData=response.body();
        if(responseGetHistoricalData!=null) {
            saveQuoteHistoricalData2DB(responseGetHistoricalData.getHistoricData());

        }
    }


    private void saveQuoteHistoricalData2DB(List<ResponseGetHistoricalData.Quote>quotes)
            throws RemoteException,OperationApplicationException{
        ContentResolver resolver=mContext.getContentResolver();
        ArrayList<ContentProviderOperation>batchOperations=new ArrayList<>();
        for (ResponseGetHistoricalData.Quote quote:quotes){
            resolver.delete(QuoteProvider.QuotesHistoricData.CONTENT_URI,
                    QuotesHistoricalDataColumns.SYMBOL+"=\""+quote.getSymbol()+"\"",null);
            batchOperations.add(QuoteProvider.buildBatchOperation(quote));
        }
        resolver.applyBatch(QuoteProvider.AUTHORITY,batchOperations);

    }

//    static public void setStockStatus(Context context,@StockStatuses int stockStatus){
//        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor=sp.edit();
//        editor.putInt(context.getString(R.string.stockStatus),stockStatus);
//        editor.apply();
//    }


}
