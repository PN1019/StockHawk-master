package com.example.beetel.stockhawk.rest;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.beetel.stockhawk.R;
import com.example.beetel.stockhawk.data.QuoteColumns;
import com.example.beetel.stockhawk.data.QuoteProvider;
import com.example.beetel.stockhawk.data.QuotesHistoricalDataColumns;
import com.example.beetel.stockhawk.touch_helper.SimpleItemTouchHelperCallback;
import com.example.beetel.stockhawk.ui.MyStocksActivity;

/**
 * Created by example on 10/6/15.
 *  Credit to skyfishjy gist:
 *    https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class QuoteCursorAdapter extends CursorRecyclerViewAdapter<QuoteCursorAdapter.ViewHolder>
    implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter{

  private static Context mContext;
  private int sChangeUnits;
  private static Typeface robotoLight;
  //private boolean isPercent;
  public QuoteCursorAdapter(Context context, Cursor cursor,int changeUnits){
    super(cursor);
    mContext = context;
      sChangeUnits=changeUnits;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
    robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.stock_list_content, parent, false);
    ViewHolder vh = new ViewHolder(itemView);
    return vh;
  }

  @Override
  public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor){
    viewHolder.symbol.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));
    viewHolder.bidPrice.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
    int sdk = Build.VERSION.SDK_INT;
    if (cursor.getInt(cursor.getColumnIndex(QuoteColumns.ISUP)) == 1){
      if (sdk >= Build.VERSION_CODES.LOLLIPOP){
        viewHolder.change.setBackground(
            mContext.getResources().getDrawable(R.drawable.percent_change_pill_green,mContext.getTheme()));
      }else {
        viewHolder.change.setBackground(
            mContext.getResources().getDrawable(R.drawable.percent_change_pill_green,mContext.getTheme()));
      }
    } else{
      if (sdk >= Build.VERSION_CODES.LOLLIPOP) {
        viewHolder.change.setBackground(
            mContext.getResources().getDrawable(R.drawable.percent_change_pill_red,mContext.getTheme()));
      }
    }
    if (sChangeUnits== MyStocksActivity.CHANGE_UNIT_PERCENTAGES){
      viewHolder.change.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
    } else{
      viewHolder.change.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE)));
    }
  }

  @Override
  public void onItemDismiss(int position) {

    String symbol =getSymbol(position);
    mContext.getContentResolver().delete(QuoteProvider.Quotes.withSymbol(symbol),null,null);
    mContext.getContentResolver().delete(QuoteProvider.QuotesHistoricData.CONTENT_URI, QuotesHistoricalDataColumns.SYMBOL+"=\""+symbol+"\"",null);
    notifyItemRemoved(position);
  }

  @Override public int getItemCount() {
    return super.getItemCount();
  }
  public String getSymbol(int position){
    Cursor cursor=getCursor();
    cursor.moveToPosition(position);
    return cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL));
  }

  public static class ViewHolder extends RecyclerView.ViewHolder
      implements SimpleItemTouchHelperCallback.ItemTouchHelperViewHolder, View.OnClickListener{
    public final TextView symbol;
    public final TextView bidPrice;
    public final TextView change;
    public ViewHolder(View itemView){
      super(itemView);
      symbol = (TextView) itemView.findViewById(R.id.stock_symbol);
      symbol.setTypeface(robotoLight);
      bidPrice = (TextView) itemView.findViewById(R.id.bid_price);
      change = (TextView) itemView.findViewById(R.id.change);
    }

    @Override
    public void onItemSelected(){
      itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear(){
      itemView.setBackgroundColor(0);
    }

    @Override
    public void onClick(View v) {

    }
  }
  public void setChangeUnits(int changeUnits){
    this.sChangeUnits=changeUnits;
  }
}
