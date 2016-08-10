package com.example.beetel.stockhawk.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.beetel.stockhawk.R;
import com.example.beetel.stockhawk.data.QuoteColumns;
import com.example.beetel.stockhawk.data.QuoteProvider;
import com.example.beetel.stockhawk.rest.QuoteCursorAdapter;
import com.example.beetel.stockhawk.rest.RecyclerViewItemClickListener;
import com.example.beetel.stockhawk.service.StockIntentService;
import com.example.beetel.stockhawk.service.StockTaskService;
import com.example.beetel.stockhawk.touch_helper.SimpleItemTouchHelperCallback;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.example.beetel.stockhawk.rest.Utils;

public class MyStocksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,RecyclerViewItemClickListener.OnItemClickListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. For use in .
     */
    public static final int CHANGE_UNITS_DOLLARS = 0;
    public static final int CHANGE_UNIT_PERCENTAGES = 1;
    private final String EXTRA_CHANGE_UNITS = "EXTRA_CHANGE_UNITS";
    private final String EXTRA_ADD_DIALOG_OPENED = "EXTRA_ADD_DIALOG_OPENED";
    private CharSequence mTitle;
    private Intent mServiceIntent;
    private ItemTouchHelper mItemTouchHelper;
    private static final int CURSOR_LOADER_ID = 0;
    private QuoteCursorAdapter mCursorAdapter;
    private Context mContext;
    private Cursor mCursor;
    boolean isConnected;
    private MaterialDialog materialDialog;
    private int sChangeUnits = CHANGE_UNITS_DOLLARS;
    @Bind(R.id.rv_stock_list)
    RecyclerView sRecyclerView;
    @Bind(R.id.ll_state_no_connection)
    View sEmptyStateNoConnection;
    @Bind(R.id.ll_state_no_stocks)
    View sEmptyStateNoStocks;
    @Bind(R.id.google_progress)
    ProgressBar sprogressBar;
    @Bind(R.id.coordinate_layout)
    CoordinatorLayout sCoordinatorLayout;
//    @Bind(R.id.stock_graph_container)
//    FrameLayout sGraphContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stocks);
        ButterKnife.bind(this);

       // isConnected=Utils.isConnected(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

//    mContext = this;
//    ConnectivityManager cm =
//        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//    isConnected = activeNetwork != null &&
//        activeNetwork.isConnectedOrConnecting();
//    setContentView(R.layout.activity_my_stocks);
        // The intent service is for executing immediate pulls from the Yahoo API
        // GCMTaskService can only schedule tasks, they cannot execute immediately
        mServiceIntent = new Intent(this, StockIntentService.class);
        if (savedInstanceState == null) {
            // Run the initialize task service so that some stocks appear upon an empty database

            mServiceIntent.putExtra(StockIntentService.EXTRA_TAG, StockIntentService.ACTION_INIT);
            if (isConnectionAvailableOrNot(this)) {
                startService(mServiceIntent);
            } else {
                Snackbar.make(sCoordinatorLayout, getString(R.string.no_connection_msg),
                        Snackbar.LENGTH_LONG).show();
                // networkToast();
            }
        }
            else{
                sChangeUnits = savedInstanceState.getInt(EXTRA_CHANGE_UNITS);
                if (savedInstanceState.getBoolean(EXTRA_ADD_DIALOG_OPENED, false)) {
                    showDialogForAddingStocks();
                }
            }

        sRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,this));
    mCursorAdapter=new QuoteCursorAdapter(this,null,sChangeUnits);
    sRecyclerView.setAdapter(mCursorAdapter);

    getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
//
//        mCursorAdapter = new QuoteCursorAdapter(this, null);
//        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
//                new RecyclerViewItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View v, int position) {
//                        //TODO:
//                        // do something on item click
//                    }
//                }));
//        recyclerView.setAdapter(mCursorAdapter);
//
//
////        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
////        fab.attachToRecyclerView(recyclerView);
////        fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (isConnected) {
////                    new MaterialDialog.Builder(mContext).title(R.string.symbol_search)
////                            .content(R.string.content_test)
////                            .inputType(InputType.TYPE_CLASS_TEXT)
////                            .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
////                                @Override
////                                public void onInput(MaterialDialog dialog, CharSequence input) {
////                                    // On FAB click, receive user input. Make sure the stock doesn't already exist
////                                    // in the DB and proceed accordingly
////                                    Cursor c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
////                                            new String[]{QuoteColumns.SYMBOL}, QuoteColumns.SYMBOL + "= ?",
////                                            new String[]{input.toString()}, null);
////                                    if (c.getCount() != 0) {
////                                        Toast toast =
////                                                Toast.makeText(MyStocksActivity.this, "This stock is already saved!",
////                                                        Toast.LENGTH_LONG);
////                                        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
////                                        toast.show();
////                                        return;
////                                    } else {
////                                        // Add the stock to DB
////                                        mServiceIntent.putExtra("tag", "add");
////                                        mServiceIntent.putExtra("symbol", input.toString());
////                                        startService(mServiceIntent);
////                                    }
////                                }
////                            })
////                            .show();
////                } else {
////                    networkToast();
////                }
//
//            }
//        });


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
       ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(sRecyclerView);

            long period = 3600L;/*1 h=60*60*/
            long flex = 10L/*10s*/;


            // create a periodic task to pull stocks once every hour after the app has been opened. This
            // is so Widget data stays up to date.
            PeriodicTask periodicTask = new PeriodicTask.Builder()
                    .setService(StockTaskService.class)
                    .setPeriod(period)
                    .setFlex(flex)
                    .setTag(StockTaskService.TAG_PERIODIC)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .build();
            // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
            // are updated.
            GcmNetworkManager.getInstance(this).schedule(periodicTask);
        }


    public static boolean isConnectionAvailableOrNot(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connectedNetwork=connectivityManager.getActiveNetworkInfo();
        return connectedNetwork!=null&&connectedNetwork.isConnectedOrConnecting();
    }
    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }
@Override
public void onSaveInstanceState(Bundle savedState) {
    super.onSaveInstanceState(savedState);
    savedState.putInt(EXTRA_CHANGE_UNITS, sChangeUnits);
    if (materialDialog != null) {
        savedState.putBoolean(EXTRA_ADD_DIALOG_OPENED, materialDialog.isShowing());
    }
}
    @Override
    protected void onStop() {
        super.onStop();
        if (materialDialog != null) {
            materialDialog.dismiss();
            materialDialog = null;
        }
    }
    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    public void showDialogForAddingStocks() {
        if (isConnectionAvailableOrNot(this)) {
            materialDialog = new MaterialDialog.Builder(this).title(R.string.add_symbol)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .autoDismiss(true)
                    .positiveText(R.string.agree)
                    .negativeText(R.string.disagree)
                    .input(R.string.input_hint, R.string.input_prefill, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            addQuote(input.toString());
                        }
                    }).build();
            materialDialog.show();
        } else {
            Snackbar.make(sCoordinatorLayout, getString(R.string.no_connection_msg), Snackbar.LENGTH_LONG).setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogForAddingStocks();

                }
            }).show();
        }
    }

    private void addQuote(final String stockQuote) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, new String[]
                                {QuoteColumns.SYMBOL},
                        QuoteColumns.SYMBOL + "=?", new String[]{stockQuote},
                        null);

                if (cursor != null) {
                    cursor.close();
                    return cursor.getCount() != 0;

                }
                return Boolean.FALSE;
            }



    @Override
    protected void onPostExecute(Boolean stockAlreadySaved) {
        if (stockAlreadySaved) {
            Snackbar.make(sCoordinatorLayout, R.string.already_saved, Snackbar.LENGTH_LONG).show();

        } else {
            Intent stockIntentService = new Intent(MyStocksActivity.this, StockIntentService.class);
            stockIntentService.putExtra(StockIntentService.EXTRA_TAG, StockIntentService.ACTION_ADD);
            stockIntentService.putExtra(StockIntentService.EXTRA_SYMBOL, stockQuote);
            startService(stockIntentService);
        }
    }


}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

@Override
public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        sEmptyStateNoStocks.setVisibility(View.GONE);
        sEmptyStateNoConnection.setVisibility(View.GONE);
        sprogressBar.setVisibility(View.VISIBLE);
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
        QuoteColumns.ISCURRENT + " = ?",
        new String[]{"1"},
        null);
        }

@Override
public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        sprogressBar.setVisibility(View.GONE);
        mCursorAdapter.swapCursor(data);
        if (mCursorAdapter.getItemCount() == 0) {
        if (!isConnected) {
        sEmptyStateNoConnection.setVisibility(View.VISIBLE);
        } else {

        sEmptyStateNoStocks.setVisibility(View.VISIBLE);
        }
        } else {
        sEmptyStateNoConnection.setVisibility(View.GONE);
        sEmptyStateNoStocks.setVisibility(View.GONE);
        }

        if (isConnectionAvailableOrNot(this)) {
        Snackbar.make(sCoordinatorLayout, getString(R.string.offline), Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
@Override
public void onClick(View v) {
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, MyStocksActivity.this);
        }
        }).show();
        }
        }

@Override
public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
        }



//            public void restoreActionBar() {
//                ActionBar actionBar = getSupportActionBar();
//                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//                actionBar.setDisplayShowTitleEnabled(true);
//                actionBar.setTitle(mTitle);
//            }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.my_stocks, menu);
                //restoreActionBar();
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();
                 switch (id) {
                     //noinspection SimplifiableIfStatement
                     case R.id.action_settings:
                         return true;
                   //  break;


                     case R.id.action_change_units:
                         // this is for changing stock changes from percent value to dollar value
                         switch (sChangeUnits) {
                             case CHANGE_UNITS_DOLLARS:
                                 sChangeUnits = CHANGE_UNIT_PERCENTAGES;
                                 mCursorAdapter.setChangeUnits(sChangeUnits);
                                 mCursorAdapter.notifyDataSetChanged();
                                 break;

                             case CHANGE_UNIT_PERCENTAGES:
                                 sChangeUnits = CHANGE_UNITS_DOLLARS;
                                 mCursorAdapter.setChangeUnits(sChangeUnits);
                                 mCursorAdapter.notifyDataSetChanged();
                                 break;

                         }
                 }



                return super.onOptionsItemSelected(item);
            }


//public void emptyViewBehaviour(){
//    if(mCursorAdapter.getItemCount()<=0){
//        //when Data not available
//
//        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(mContext);
//
//@StockTaskService.StockStatuses int stockStatus=sp.getInt(getString(R.string.stockStatus),-1);
//        String message=getString(R.string.data_unavailable)
//        //String msg=getString(R.string.)
//    }
//}

    @Override
    public void onItemClick(View v, int position) {
      Bundle args=new Bundle();
       Context context=v.getContext();
//        Intent intent=new Intent(context,StockChartActivity.class);
//        args.putString(StockChartFragment.ARG_SYMBOL,mCursorAdapter.getSymbol(position));
//
        StockChartFragment fragment=new StockChartFragment();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.stock_graph_container,fragment).commit();
    }

//    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
//                        .show();
//            } else {
//                Log.i(TAG, "This device is not supported.");
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }
}
