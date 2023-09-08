package com.example.videoplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videoplayer.R;
import com.example.videoplayer.Model.VideoFiles;
import com.example.videoplayer.Services.FloatingWidgetService;
import com.example.videoplayer.callback;
import com.example.videoplayer.player;

import java.io.File;
import java.util.ArrayList;


public class InternalVideoFilesAdapter extends RecyclerView.Adapter<InternalVideoFilesAdapter.MyViewHolder> {

    private Context context;
    public static ArrayList<VideoFiles> folderVideoFiles;
    public callback listener;
    String timeString="";

    public InternalVideoFilesAdapter(Context context, ArrayList<VideoFiles> videoFiles, callback listener) {
        this.context = context;
        this.folderVideoFiles = videoFiles;
        this.listener=listener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.video_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fileName.setText(folderVideoFiles.get(position).getTitle());
        String sec=folderVideoFiles.get(position).getDuration();
    if(sec==null)
        timeString="00:00";
    else {
        Long totalSecs = Long.parseLong(sec) / 1000;
        Long hours = (totalSecs) / 3600;
        Long minutes = (totalSecs) % 3600 / 60;
        Long seconds = (totalSecs) % 60 + 1;
        Log.d("sec", String.valueOf(totalSecs));
        if (hours == 0)
            timeString = String.format("%02d:%02d", minutes, seconds);
        else
            timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
        holder.duration.setText(timeString);
        Glide.with(context).load(new File(folderVideoFiles.get(position).getPath())).into(holder.thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!FloatingWidgetService.floatingWidgetPlaying) {
                    Intent intent = new Intent(context, player.class);
                    intent.putExtra("p", position);
                    intent.putExtra("files", folderVideoFiles);
                    context.startActivity(intent);
                }

            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != RecyclerView.NO_POSITION)
                    listener.onIconMoreClick(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return folderVideoFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        ImageView thumbnail;
        TextView duration;
        TextView fileName;
        ImageView menu;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail=itemView.findViewById(R.id.thumbnail);
            duration=itemView.findViewById(R.id.videoDuration);
            fileName=itemView.findViewById(R.id.videoFileName);
            menu=itemView.findViewById(R.id.menu);
        }
    }
}
