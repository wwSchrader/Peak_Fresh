package com.example.android.peakfresh.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Warren on 8/23/2016.
 */

@net.simonvt.schematic.annotation.ContentProvider(authority = ProductContentProvider.AUTHORITY, database = Database.class)
public final class ProductContentProvider {

    public static final String AUTHORITY = "com.example.android.peakfresh.ProductContentProvider";

    @TableEndpoint(table = Database.PRODUCTS) public static class Products {

        @ContentUri(
                path = "products",
                type = "vnd.android.cursor.dir/products",
                defaultSort = ProductColumns._ID + " ASC")
        public static final Uri PRODUCTS = Uri.parse("content://" + AUTHORITY + "/products");
    }
}
