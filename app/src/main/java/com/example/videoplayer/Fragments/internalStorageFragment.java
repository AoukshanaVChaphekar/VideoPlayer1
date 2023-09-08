package com.example.videoplayer.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.videoplayer.Adapter.InternalFolderAdapter;
import com.example.videoplayer.Adapter.InternalVideoFilesAdapter;
import com.example.videoplayer.BottomSheetDialog;
import com.example.videoplayer.R;
import com.example.videoplayer.callback;

import static com.example.videoplayer.MainActivity.folderList;
import static com.example.videoplayer.MainActivity.internalVideoFilesArrayList;
import static com.example.videoplayer.MainActivity.videoFilesArrayList;

public class internalStorageFragment extends Fragment implements callback {


    RecyclerView recyclerView,recyclerViewVideoFiles;
    InternalFolderAdapter folderAdapter;
    private InternalVideoFilesAdapter internalVideoFilesAdapter;
    public static BottomSheetDialog bottomSheetDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        InternalVideoFilesFragment.flag=false;
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Folders");
        View view=inflater.inflate(R.layout.fragment_internal_storage, container, false);
        recyclerView=view.findViewById(R.id.internalStorageRv);
        recyclerViewVideoFiles=view.findViewById(R.id.internalStorage);
        if(folderList!=null && folderList.size()>0 && internalVideoFilesArrayList!=null)
        {
            folderAdapter=new InternalFolderAdapter(folderList,internalVideoFilesArrayList,getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
            recyclerView.setAdapter(folderAdapter);
           recyclerView.setNestedScrollingEnabled(false);
        }
        if(videoFilesArrayList!=null && videoFilesArrayList.size()>0)
        {
            internalVideoFilesAdapter =new InternalVideoFilesAdapter(getContext(),videoFilesArrayList,this);
            recyclerViewVideoFiles.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
            recyclerViewVideoFiles.setAdapter(internalVideoFilesAdapter);
           recyclerViewVideoFiles.setNestedScrollingEnabled(false);
        }

        return view;
    }

    @Override
    public void onIconMoreClick(int position) {
        bottomSheetDialog=new BottomSheetDialog();
     //   Log.d("position", String.valueOf(position));
        bottomSheetDialog.setVideoPosition(position);
        Bundle args=new Bundle();
        args.putString("internalVideoFiles", String.valueOf(2));
        bottomSheetDialog.setArguments(args);
        bottomSheetDialog.show(getParentFragmentManager(),bottomSheetDialog.getTag());

    }
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Folders");
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Folders");

    }
}