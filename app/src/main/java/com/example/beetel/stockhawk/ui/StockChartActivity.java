package com.example.beetel.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.beetel.stockhawk.R;

/**
 * Created by beetel on 20/06/2016.
 */
public class StockChartActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if(savedInstanceState==null){
            Bundle arguments=new Bundle();
            arguments.putString(StockChartFragment.ARG_SYMBOL,getIntent().getStringExtra(StockChartFragment.ARG_SYMBOL));
            StockChartFragment fragment=new StockChartFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.stock_graph_container,fragment).commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
