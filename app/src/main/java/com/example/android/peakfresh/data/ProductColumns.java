package com.example.android.peakfresh.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Warren on 8/23/2016.
 */
public interface ProductColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull String PRODUCT_NAME = "product_name";

    @DataType(DataType.Type.TEXT) @NotNull String PRODUCT_EXPIRATION_DATE = "product_expiration_date";

    @DataType(DataType.Type.TEXT) @NotNull String PRODUCT_CATEGORY = "product_category";

    @DataType(DataType.Type.INTEGER) String PRODUCT_ICON = "product_icon";


}
