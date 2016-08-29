package com.example.android.peakfresh.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.peakfresh.ProductRecyclerView;
import com.example.android.peakfresh.R;

import java.util.ArrayList;
import java.util.List;


public class Main_Fragment_List extends Fragment {

    public static final String TAG = Main_Fragment_List.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    ArrayList<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_main__fragment__list, container, false
        );
        setupRecyclerView(mRecyclerView);

        //inflate layout for fragment
        return mRecyclerView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new ProductRecyclerView(getActivity(), getDataList()));
    }

    private List<String> getDataList() {
        list.add("Toast");
        return list;
    }
}
