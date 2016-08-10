package com.example.beetel.stockhawk.rest;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

/**
 * Created by example on 10/6/15.
 *  Credit to skyfishjy gist:
 *    https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the CursorRecyclerViewApater.java code and idea.
 */
public abstract class CursorRecyclerViewAdapter <VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{
  private static final String LOG_TAG = CursorRecyclerViewAdapter.class.getSimpleName();
  private Cursor mCursor;
  private boolean sDataIsValid;
  private int sRowIdColumn;
  private DataSetObserver mDataSetObserver;
  public CursorRecyclerViewAdapter(Cursor cursor){
    mCursor = cursor;
    sDataIsValid = cursor != null;
    sRowIdColumn = sDataIsValid ? mCursor.getColumnIndex("_id") : -1;
    mDataSetObserver = new NotifyingDataSetObserver();
    if (sDataIsValid){
      mCursor.registerDataSetObserver(mDataSetObserver);
    }
  }

  public Cursor getCursor(){
    return mCursor;
  }

  @Override
  public int getItemCount(){
    if (mCursor != null&&sDataIsValid){
      return mCursor.getCount();
    }
    return 0;
  }

  @Override
  public long getItemId(int position) {
    if (sDataIsValid && mCursor != null && mCursor.moveToPosition(position)){
      return mCursor.getLong(sRowIdColumn);
    }
    return 0;
  }

  @Override public void setHasStableIds(boolean hasStableIds) {
    super.setHasStableIds(true);
  }

  public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

  @Override
  public void onBindViewHolder(VH viewHolder, int position) {
    if (!sDataIsValid){
      throw new IllegalStateException("This should only be called when Cursor is valid");
    }
    if (!mCursor.moveToPosition(position)){
      throw new IllegalStateException("Could not move Cursor to position: " + position);
    }

    onBindViewHolder(viewHolder, mCursor);
  }

  public Cursor swapCursor(Cursor newCursor){
    if (newCursor == mCursor){
      return null;
    }
    final Cursor oldCursor = mCursor;
    if (oldCursor != null && mDataSetObserver != null){
      oldCursor.unregisterDataSetObserver(mDataSetObserver);
    }
    mCursor = newCursor;
    if (mCursor != null){
      if (mDataSetObserver != null){
        mCursor.registerDataSetObserver(mDataSetObserver);
      }
      sRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
      sDataIsValid = true;
      notifyDataSetChanged();
    }else{
      sRowIdColumn = -1;
      sDataIsValid = false;
      notifyDataSetChanged();
    }
    return oldCursor;
  }

  private class NotifyingDataSetObserver extends DataSetObserver{
    @Override public void onChanged() {
      super.onChanged();
      sDataIsValid = true;
      notifyDataSetChanged();
    }

    @Override public void onInvalidated() {
      super.onInvalidated();
      sDataIsValid = false;
      notifyDataSetChanged();
    }
  }
}
