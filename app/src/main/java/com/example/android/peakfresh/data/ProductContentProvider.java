package com.example.android.peakfresh.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Warren on 8/23/2016.
 */

@net.simonvt.schematic.annotation.ContentProvider(authority = ProductContentProvider.AUTHORITY, database = Database.class)
public final class ProductContentProvider {

    public static final String AUTHORITY = "com.example.android.peakfresh.ProductContentProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String PRODUCTS = "products";
    }

    private static Uri buildUri(String... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path:paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = Database.PRODUCTS) public static class Products {

        @ContentUri(
                path = Path.PRODUCTS,
                type = "vnd.android.cursor.dir/products",
                defaultSort = ProductColumns._ID + " ASC")
        public static final Uri PRODUCTS_URI = buildUri(Path.PRODUCTS);

        @InexactContentUri(
                name = "PRODUCT_ID",
                path = Path.PRODUCTS + "/*",
                type = "vnd.android.cursor.dir/products",
                whereColumn = ProductColumns.PRODUCT_NAME,
                pathSegment = 1
        )
        public static Uri withName(String name){
            return buildUri(Path.PRODUCTS, name);
        }
    }
}
