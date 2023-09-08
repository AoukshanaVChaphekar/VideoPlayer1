package com.example.videoplayer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videoplayer.Fragments.InternalVideoFilesFragment;
import com.example.videoplayer.R;
import com.example.videoplayer.Model.VideoFiles;

import java.util.ArrayList;

public class InternalFolderAdapter extends RecyclerView.Adapter<InternalFolderAdapter.FolderViewHolder> {


    private ArrayList<String> folderName;
    private ArrayList<VideoFiles> videoFiles;
    private Context context;


    public InternalFolderAdapter(ArrayList<String> folderName, ArrayList<VideoFiles> videoFiles, Context context) {
        this.folderName = folderName;
        this.videoFiles = videoFiles;
        this.context = context;
    }


    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.folder_item,parent,false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        holder.folder.setText(folderName.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager=((AppCompatActivity)context).getSupportFragmentManager();
                InternalVideoFilesFragment internalVideoFilesFragment=new InternalVideoFilesFragment();
                Bundle args=new Bundle();
                args.putString("folderName",folderName.get(position));
                //open fragment
                internalVideoFilesFragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.mainFragment,internalVideoFilesFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderName.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder
    {

        TextView folder;
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folder=itemView.findViewById(R.id.folderName);
        }
    }
}
