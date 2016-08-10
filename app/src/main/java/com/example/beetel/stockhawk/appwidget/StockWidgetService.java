package com.example.beetel.stockhawk.appwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by beetel on 23/05/2016.
 */
public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent){
        return new StockWidgetFactory(this.getApplicationContext(),intent);

    }
}
