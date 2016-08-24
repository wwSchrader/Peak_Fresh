package com.example.android.peakfresh.data;

import net.simonvt.schematic.annotation.Table;

/**
 * Created by Warren on 8/23/2016.
 */
@net.simonvt.schematic.annotation.Database(version = Database.VERSION)
public final class Database {

    public static final int VERSION = 1;

    @Table(ProductColumns.class) public static final String PRODUCTS = "products";
}
