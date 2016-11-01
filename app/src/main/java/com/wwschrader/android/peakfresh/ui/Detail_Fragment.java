package com.wwschrader.android.peakfresh.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wwschrader.android.peakfresh.R;
import com.wwschrader.android.peakfresh.UpdateProductTask;
import com.wwschrader.android.peakfresh.Utility;
import com.wwschrader.android.peakfresh.data.ProductColumns;
import com.wwschrader.android.peakfresh.data.ProductContentProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Warren on 9/8/2016.
 */
public class Detail_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener,
        AddCategoryDialogFragment.CategoryDialogListener {
    private static final String[] DETAIL_COLUMNS = {ProductColumns.PRODUCT_NAME,
            ProductColumns.PRODUCT_ICON, ProductColumns.PRODUCT_EXPIRATION_DATE,
            ProductColumns.PRODUCT_EXPIRATION_DATE, ProductColumns.PRODUCT_CATEGORY};
    private static final int  REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_REQUEST_CODE = 2;
    private Context mContext;
    private int mProduct_Id;
    private ArrayAdapter categoryArrayAdapter;
    private ImageView mImageView;
    private TextView mProduct_title;
    private TextView mExpirationSummary;
    private TextView mExpirationDate;
    private ImageButton mCameraButton;
    private ImageButton mTitleButton;
    private ImageButton mDateButton;
    private Spinner mCategorySpinner;
    private static String[] mProduct_ID_Array;
    private final static int LOADER_ID = 1;
    private final static int LOADER_ID_SPINNER = 2;
    public final static String PRODUCT_ID_KEY = "Product_Id";
    private static final String CURRENT_PHOTO_PATH = "currentPhotoPath";
    private String mCurrentPhotoPath;
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String IMAGE_POSITION = "image_position";
    private boolean onItemSelectedListenerFlag = false;
    private Calendar expirationDateCalendar = Calendar.getInstance();
    private String productName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        //restore photo image path
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(CURRENT_PHOTO_PATH) != null){
                mCurrentPhotoPath = savedInstanceState.getString(CURRENT_PHOTO_PATH);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mProduct_Id = arguments.getInt(PRODUCT_ID_KEY);
            mProduct_ID_Array = new String[]{Integer.toString(mProduct_Id)};
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.product_icon_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int position = getActivity().getIntent().getIntExtra(IMAGE_POSITION, 0);
            mImageView.setTransitionName(EXTRA_IMAGE + position);
            ViewCompat.setTransitionName(mImageView, EXTRA_IMAGE + position);
            Log.d("Set Transition name", mImageView.getTransitionName());
        }
        mProduct_title = (TextView) rootView.findViewById(R.id.product_title_detail);
        mTitleButton = (ImageButton) rootView.findViewById(R.id.product_title_button);
        mTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(mContext).title(R.string.new_product_dialog_title)
                        .content(R.string.new_product_dialog_content)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(getString(R.string.new_product_name), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                UpdateProductTask updateProductTask = new UpdateProductTask(mContext,
                                        mProduct_ID_Array, input.toString(), ProductColumns.PRODUCT_NAME);
                                updateProductTask.execute();
                            }
                        })
                        .show();
            }
        });
        mExpirationDate = (TextView) rootView.findViewById(R.id.product_expiration_date_detail);
        mDateButton = (ImageButton) rootView.findViewById(R.id.expiration_date_button);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setTargetFragment(Detail_Fragment.this, 0);
                newFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.date_picker_tag));
            }
        });
        mExpirationSummary = (TextView) rootView.findViewById(R.id.expiration_summary);
        mCameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mContext.checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CODE);
                    } else {
                        takePicture();
                    }
                } else {
                    takePicture();
                }

            }
        });

        //setup add to calendar button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.add_to_calendar_button);
        fab.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(Intent.ACTION_INSERT)
                           .setData(CalendarContract.Events.CONTENT_URI)
                           .putExtra(CalendarContract.Events.TITLE, productName+ " expires");
                   //set beginning time and end time for calendar intent
                   Calendar beginTime = expirationDateCalendar;
                   beginTime.set(Calendar.HOUR_OF_DAY, 12);
                   beginTime.set(Calendar.MINUTE, 0);
                   intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());

                   //set end time
                   Calendar endTime = beginTime;
                   endTime.set(Calendar.HOUR_OF_DAY, 13);
                   intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());

                   startActivity(intent);
               }
        });

        mCategorySpinner = (Spinner) rootView.findViewById(R.id.category_spinner);


        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            int adjMonth = month + 1;

            String newDate = adjMonth + "/" + day + "/" + year;
        }
    };



    //Handles thumbnail from taking picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            setPic();
            galleryAddPic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
        }
    }

    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensuring camera activity can handle intent
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            //Create file where photo should go
            File photoFile = null;
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                Log.e("External storage", "External Storage not available");
                return;
            }

            try {
                photoFile = Utility.createImageFile(mContext);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex){
                Log.e("createImageFile", ex.getMessage());
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(mContext, getString(R.string.uri_for_file), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 1000;
        int targetH = 1000;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);


    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);

        UpdateProductTask updateProductTask = new UpdateProductTask(getContext(), mProduct_ID_Array,
                contentUri.toString(), ProductColumns.PRODUCT_ICON);
        updateProductTask.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //save image filepath if needed
        if (mCurrentPhotoPath != null) {
            outState.putString(CURRENT_PHOTO_PATH, mCurrentPhotoPath);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mProduct_Id != 0 && id == LOADER_ID) {
            return new CursorLoader(
                    getActivity(),
                    ProductContentProvider.Products.PRODUCTS_URI,
                    DETAIL_COLUMNS,
                    ProductColumns._ID + " LIKE ?",
                    mProduct_ID_Array,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst() && loader.getId() == LOADER_ID) {
            mProduct_title.setText(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_NAME)));
            mExpirationDate.setText(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE)));
            mExpirationSummary.setText(Utility.getExpirationDateDescription(mContext, data.getString(data.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE))));


            try {
                //setup calendar to be used in the add to calendar intent
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                String dateFromDb = data.getString(data.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE));
                expirationDateCalendar.setTime(simpleDateFormat.parse(dateFromDb));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //set string as product name to be used in add to calendar intent
            productName = data.getString(data.getColumnIndex(ProductColumns.PRODUCT_NAME));

            //setup the view for the category spinner
            ArrayList<String> categoryArrayList = Utility.loadCategoryArray(Utility.CATEGORY_ARRAY, mContext, getString(R.string.detail_screen_tag));
            categoryArrayAdapter = new ArrayAdapter(mContext, R.layout.category_spinner_item, categoryArrayList);
            //set flag to false since onItemSelected is triggered when first set
            onItemSelectedListenerFlag = false;
            categoryArrayAdapter.setDropDownViewResource(R.layout.category_spinner_dropdown_item);
            mCategorySpinner.setAdapter(categoryArrayAdapter);
            mCategorySpinner.setSelection(categoryArrayAdapter.getPosition(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_CATEGORY))), false);
            mCategorySpinner.setOnItemSelectedListener(this);

            Glide.with(getContext())
                    .load(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_ICON)))
                    .placeholder(R.mipmap.ic_launcher)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                scheduleStartPostponedTransition(mImageView);
                            }
                            return false;
                        }
                    })
                    .into(mImageView);
        }
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getActivity().supportStartPostponedEnterTransition();
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        int adjMonth = month + 1;

        String newDate = adjMonth + "/" + day + "/" + year;
        UpdateProductTask updateProductTask = new UpdateProductTask(getContext(), mProduct_ID_Array,
                newDate, ProductColumns.PRODUCT_EXPIRATION_DATE);
        updateProductTask.execute();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //handle first time trigger of onItemSelected by checking flag for first time trigger
        if (onItemSelectedListenerFlag){
            if(parent.getItemAtPosition(position).toString().equals(getString(R.string.custom_spinner))){
                //launch fragment to add new category
                AddCategoryDialogFragment categoryDialogFragment = new AddCategoryDialogFragment();
                categoryDialogFragment.setTargetFragment(this, 0);
                categoryDialogFragment.show(getFragmentManager(), getString(R.string.new_category));

            }else {
                updateProductCategory(parent.getItemAtPosition(position).toString());
            }
        } else {
            //if it's the first time, set flag to true to run code next time
            onItemSelectedListenerFlag = true;
        }
    }

    private void updateProductCategory(String item){
        UpdateProductTask updateProductTask = new UpdateProductTask(getContext(), mProduct_ID_Array,
                item, ProductColumns.PRODUCT_CATEGORY);
        updateProductTask.execute();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onDialogPositiveClick(String category) {
        Utility.addItemToCategoryArray(Utility.CATEGORY_ARRAY, mContext, category);
        updateProductCategory(category);

        Toast.makeText(mContext, R.string.category_added, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick() {
        //restart the loader to reset the spinner
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        Toast.makeText(mContext, R.string.action_canceled, Toast.LENGTH_SHORT).show();
    }
}
