package com.example.musicx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewHolder> {
    ArrayList<ModelSong> modelSongs;
    Context context;
    OnItemClickListener listener;

    public AdapterClass(ArrayList<ModelSong> modelSongs, Context context, OnItemClickListener listener) {
        this.modelSongs = modelSongs;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songName.setText(modelSongs.get(position).getSongName());
        holder.duration.setText(timerConversion((long) modelSongs.get(position).getDuration()));

        holder.songName.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return modelSongs.size();
    }
    public void updateAdapterList(ArrayList<ModelSong> modelSongs) {
        this.modelSongs = modelSongs;
        notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView songName, duration;
        RelativeLayout relativeLayout ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.tracknameview);
            duration = itemView.findViewById(R.id.durationview);
            relativeLayout=itemView.findViewById(R.id.vID);
            relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    modelSongs.remove(getAdapterPosition());
                    Toast.makeText(AdapterClass.this.context,"Removed from playlist",Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    return  true;
                }
            });
        }
    }
    public String timerConversion(long value) {
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            audioTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%02d:%02d", mns, scs);
        }
        return audioTime;
    }

    interface OnItemClickListener{

        public void onItemClick(int position);

    }
}



