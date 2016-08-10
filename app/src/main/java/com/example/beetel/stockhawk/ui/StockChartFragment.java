package com.example.beetel.stockhawk.ui;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.beetel.stockhawk.R;
import com.example.beetel.stockhawk.data.QuoteColumns;
import com.example.beetel.stockhawk.data.QuoteProvider;
import com.example.beetel.stockhawk.data.QuotesHistoricalDataColumns;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by beetel on 20/06/2016.
 */
public class StockChartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = StockChartFragment.class.getSimpleName();
    public static final String ARG_SYMBOL = "ARG_SYMBOL";
    public static final int CURSOR_LOADER_ID = 1;
    private static final int CURSOR_LOADER_ID_FOR_LINE_CHART = 2;
    private String sSymbol;
    private String sSelectedTab;
    @Bind(R.id.stock_name)
    TextView tv_name;
    @Bind(R.id.stock_symbol)
    TextView tv_symbol;
    @Bind(R.id.bid_price)
    TextView tv_bidPrice;
    @Bind(R.id.stock_chart)
    LineChartView sChart;
    @Bind(R.id.stock_change)
    TextView sChange;

    public StockChartFragment() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CURSOR_LOADER_ID) {
            return new CursorLoader(getContext(), QuoteProvider.Quotes.CONTENT_URI, new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                    QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP, QuoteColumns.NAME}, QuoteColumns.SYMBOL + "=\"" + sSymbol + "\"", null, null);
        } else if (id == CURSOR_LOADER_ID_FOR_LINE_CHART) {
            String sortOrder = QuoteColumns._ID + "ASC LIMIT 5";
        }
        return new CursorLoader(getContext(), QuoteProvider.QuotesHistoricData.CONTENT_URI,
                new String[]{QuotesHistoricalDataColumns._ID, QuotesHistoricalDataColumns.SYMBOL, QuotesHistoricalDataColumns.BIDPRICE, QuotesHistoricalDataColumns.Date}, QuotesHistoricalDataColumns.SYMBOL + "=\"" + sSymbol + "\"", null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CURSOR_LOADER_ID && data != null && data.moveToFirst()) {
            String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
            tv_symbol.setText(symbol);
            String bidPrice = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
            tv_bidPrice.setText(bidPrice);
            String name = data.getString(data.getColumnIndex(QuoteColumns.NAME));
            tv_name.setText(name);
            String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
            String percentChange = data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
            String sumUpChange = change + "(" + percentChange + ")";
            sChange.setText(sumUpChange);

        } else if (loader.getId() == CURSOR_LOADER_ID_FOR_LINE_CHART && data != null && data.moveToFirst()) {
            updateChart(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_SYMBOL)) {
            sSymbol = getArguments().getString(ARG_SYMBOL);
        }

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);
        getLoaderManager().initLoader(CURSOR_LOADER_ID_FOR_LINE_CHART, null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_details, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void updateChart(Cursor data) {
        List<AxisValue> axisValuesX = new ArrayList<>();
        List<PointValue> pointValues = new ArrayList<>();
        int counter = -1;
        do {

            counter++;
            String date = data.getString(data.getColumnIndex(QuotesHistoricalDataColumns.Date));
            String bidPrice = data.getString(data.getColumnIndex(QuotesHistoricalDataColumns.BIDPRICE));

            int x_axis = data.getCount() - 1 - counter;

            PointValue pointValue = new PointValue(x_axis, Float.valueOf(bidPrice));
            pointValue.setLabel(date);
            pointValues.add(pointValue);
            if (counter != 0 && counter % (data.getCount() / 3) == 0) {
                AxisValue axisValueX = new AxisValue(x_axis);
                axisValueX.setLabel(date);
                axisValuesX.add(axisValueX);
            }
        } while (data.moveToNext());
        Line line = new Line(pointValues).setColor(Color.WHITE).setCubic(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lines);

        Axis axisY = new Axis();
        axisY.setAutoGenerated(true);
        axisY.setHasLines(true);
        axisY.setMaxLabelChars(4);
        lineChartData.setAxisXBottom(axisY);

        Axis axisX = new Axis();
        axisX.setHasLines(true);
        axisX.setAutoGenerated(true);
        axisX.setMaxLabelChars(4);
        lineChartData.setAxisYLeft(axisY);

        sChart.setInteractive(false);
        sChart.setLineChartData(lineChartData);

        sChart.setVisibility(View.VISIBLE);

    }

    @Nullable
    private ActionBar getActionBar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            return activity.getSupportActionBar();
        }
        return null;
    }
}