package com.example.videoplayer.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.videoplayer.Adapter.InternalVideoFilesAdapter;
import com.example.videoplayer.BottomSheetDialog;
import com.example.videoplayer.R;
import com.example.videoplayer.Model.VideoFiles;
import com.example.videoplayer.callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class InternalVideoFilesFragment extends Fragment implements callback {
    RecyclerView recyclerView;
    public static InternalVideoFilesAdapter internalVideoFilesAdapter;
    String myfolderName;
    public static ArrayList<VideoFiles> videoFiles=new ArrayList<>();
    public static BottomSheetDialog bottomSheetDialog;
    public static boolean flag=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_internal_video_files, container, false);
        recyclerView=view.findViewById(R.id.videoFolderRv);
        Bundle bundle=getArguments();
        myfolderName=bundle.getString("folderName");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(myfolderName);
        if(myfolderName!=null)
        {
            videoFiles=getInternalVideoFiles(getContext(),myfolderName);
            Collections.reverse(videoFiles);
        }
        if(videoFiles.size()>0)
        {
            flag=true;
            internalVideoFilesAdapter =new InternalVideoFilesAdapter(getContext(),videoFiles,this::onIconMoreClick);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
            recyclerView.setAdapter(internalVideoFilesAdapter);
        }
        return view;
    }
    public ArrayList<VideoFiles> getInternalVideoFiles(Context context, String folderName)
    {
        ArrayList<VideoFiles> tempFiles=new ArrayList<>();
        Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection={
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME

        };

        String selection=MediaStore.Video.Media.DATA+" like?";
        String[] selectionArgs=new String[]{"%"+folderName+"%"};
        Cursor cursor=context.getContentResolver().query(uri,projection,selection,selectionArgs,null);
        if(cursor!=null)
        {
            while(cursor.moveToNext())
            {
                String id=cursor.getString(0);
                String path=cursor.getString(1);
                String title=cursor.getString(2);
                String size=cursor.getString(3);
                String date_added=cursor.getString(4);
                String duration=cursor.getString(5);
                String fileName=cursor.getString(6);
                VideoFiles videoFiles=new VideoFiles(id,path,title,fileName,size,date_added,duration);

                tempFiles.add(videoFiles);

            }
            cursor.close();
        }
        return tempFiles;
    }
    @Override
    public void onIconMoreClick(int position) {
         bottomSheetDialog=new BottomSheetDialog();
        bottomSheetDialog.setVideoPosition(position);
        Bundle args=new Bundle();
        args.putString("internalVideoFiles", String.valueOf(1));
        bottomSheetDialog.setArguments(args);
        bottomSheetDialog.show(getParentFragmentManager(),bottomSheetDialog.getTag());
    }
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(myfolderName);
    }
    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(myfolderName);

    }
}