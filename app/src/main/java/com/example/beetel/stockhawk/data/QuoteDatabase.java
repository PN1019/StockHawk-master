package com.example.beetel.stockhawk.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by example on 10/5/15.
 */
@Database(version = QuoteDatabase.VERSION)
public class QuoteDatabase {
  public static final int VERSION = 9;
  @Table(QuoteColumns.class) public static final String QUOTES = "quotes";
  @Table(QuoteColumns.class)public static final String QUOTES_HISTORICAL_DATA="quotes_historical_data";

  private QuoteDatabase() {
  }
}
