package com.example.videoplayer.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.videoplayer.MainActivity;
import com.example.videoplayer.R;
import com.example.videoplayer.Adapter.AllVideoAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static com.example.videoplayer.MainActivity.internalVideoFilesArrayList;

import static com.example.videoplayer.Fragments.InternalVideoFilesFragment.flag;

public class AllFragment extends Fragment  {

    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        flag=false;
        View view= inflater.inflate(R.layout.fragment_all, container, false);
        recyclerView=view.findViewById(R.id.allRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL, false));
        AllVideoAdapter videoAdapter=MainActivity.videoAdapter;

        if(internalVideoFilesArrayList!=null && internalVideoFilesArrayList.size()>0)
        {
            recyclerView.setAdapter(videoAdapter);
        }
        return view;
    }


}