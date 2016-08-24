package com.example.android.peakfresh.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Warren on 8/23/2016.
 */

@net.simonvt.schematic.annotation.ContentProvider(authority = ContentProvider.AUTHORITY, database = Database.class)
public final class ContentProvider {

    public static final String AUTHORITY = "com.example.android.peakfresh.ContentProvider";

    @TableEndpoint(table = Database.PRODUCTS) public static class Products {

        @ContentUri(
                path = "products",
                type = "vnd.android.cursor.dir/products",
                defaultSort = ProductColumns._ID + " ASC")
        public static final Uri PRODUCTS = Uri.parse("content://" + AUTHORITY + "/products");
    }
}
