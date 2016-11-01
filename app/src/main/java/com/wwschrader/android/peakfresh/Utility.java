package com.wwschrader.android.peakfresh;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Warren on 9/12/2016.
 */
public class Utility {

    private static String SHARED_PREF_CATEGORY_ARRAY_PREF = "category_array";
    public static final String CATEGORY_ARRAY = "category_array";

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public static Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    public static String getExpirationDateDescription(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy", Locale.US);
        try {
            Calendar expirationDate = Calendar.getInstance();
            expirationDate.setTime(dateFormat.parse(date));
            Calendar evaluatorDate = Calendar.getInstance();

            //set evaluator date to exactly at 12am
            evaluatorDate.set(Calendar.HOUR_OF_DAY, 0);
            evaluatorDate.set(Calendar.MINUTE, 0);
            evaluatorDate.set(Calendar.SECOND, 0);
            evaluatorDate.set(Calendar.MILLISECOND, 0);

            //check to see what message to display based on the expiration date
            if (expirationDate.before(evaluatorDate)) {
                return "Expired on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires today!";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires tomorrow on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in two days on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in three days on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in four days on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in five days on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in six days on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about a week.";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 7);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about a week on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 7);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in two weeks on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 7);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in three weeks on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 7);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in four weeks on:";
            }
            evaluatorDate.add(Calendar.DAY_OF_YEAR, 7);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about a month on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about two months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about three months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about four months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about five months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about six months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about seven months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about eight months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about nine months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about ten months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about eleven months on:";
            }
            evaluatorDate.add(Calendar.MONTH, 1);
            if (expirationDate.before(evaluatorDate)) {
                return "Expires in about a year on:";
            } else {
                return "Expires in over a year on:";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Utility_expDate", "Parse exception");
            throw new RuntimeException();
        }
    }

    public static boolean saveCategoryArray(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_CATEGORY_ARRAY_PREF, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + mContext.getString(R.string.categor_shared_pref_modifier), array.length);
        for (int i = 0; i < array.length; i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }

    public static ArrayList<String> loadCategoryArray(String arrayName, Context mContext, String loadType) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_CATEGORY_ARRAY_PREF, 0);
        int size = prefs.getInt(arrayName + mContext.getString(R.string.categor_shared_pref_modifier), 0);
        ArrayList<String> arrayList= new ArrayList<String>();

        for(int i = 0; i < size; i++) {
            arrayList.add(prefs.getString(arrayName + "_" + i, null));
        }

        switch (loadType){
            case "main_screen":
                arrayList.add(0, "All");
                break;
            case "add_screen":
                arrayList.add(0, "Choose category");
                arrayList.add("Custom");
                break;
            case "detail_screen":
                arrayList.add("Custom");
        }

        return arrayList;
    }

    public static void addItemToCategoryArray(String arrayName, Context mContext, String newCategory) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_CATEGORY_ARRAY_PREF, 0);
        int size = prefs.getInt(arrayName + mContext.getString(R.string.categor_shared_pref_modifier), 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(arrayName + "_" + size, newCategory);
        size++;
        editor.putInt(arrayName + mContext.getString(R.string.categor_shared_pref_modifier), size);
        editor.apply();
    }

    public static void removeItemFromCategoryArray(String arrayName, Context mContext, ArrayList<String> categoryToDelete) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_CATEGORY_ARRAY_PREF, 0);
        int size = prefs.getInt(arrayName + mContext.getString(R.string.categor_shared_pref_modifier), 0);
        int adjustedSize = size;
        SharedPreferences.Editor editor = prefs.edit();
        boolean isSuccessful = false;

        for (int k = 0; k < categoryToDelete.size(); k++){
            int i;
            for (i = 0; i < size; i++){

                if (categoryToDelete.get(k).equals(prefs.getString(arrayName + "_" + i, null))){
                    editor.remove(prefs.getString(arrayName + "_" + i, null));
                    isSuccessful = editor.commit();
                    editor.putInt(arrayName + mContext.getString(R.string.categor_shared_pref_modifier), --adjustedSize);
                    break;
                }
            }

            if (isSuccessful){
                for (int j = i; j < size; j++){
                    //move up values in array
                    editor.putString(arrayName + "_" + j, prefs.getString(arrayName + "_" + (j + 1), null));
                    editor.remove(arrayName + "_" + (j + 1));
                    editor.apply();
                }
            }
        }

        editor.commit();
    }
}