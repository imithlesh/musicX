package com.example.musicx;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterClass.OnItemClickListener {
    Toolbar  toolbar;
    SeekBar seekBar;
    ImageButton prevbtn,nextbtn,playbtn,pausebtn;
    RecyclerView recyclerView;
    TextView duration,currentdurtion,songTitle1;
    public static final int PERMISSION_READ = 0;
    MediaPlayer mediaPlayer=new MediaPlayer();
    ArrayList<ModelSong> modelSongs=new ArrayList<>();
    AdapterClass adapterClass=new AdapterClass(modelSongs,this,this);
    private final Handler hdlr = new Handler();
    private int mCurrentIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        songTitle1=findViewById(R.id.songtitle1);
        duration=findViewById(R.id.total_duration);
        currentdurtion=findViewById(R.id.currentDuration);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu));
        seekBar=findViewById(R.id.seekbar);
        prevbtn=findViewById(R.id.prev);
        nextbtn=findViewById(R.id.next);
        playbtn=findViewById(R.id.play);
        pausebtn=findViewById(R.id.pause);
        recyclerView=findViewById(R.id.rec);

        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterClass);
        if (checkPermission()){
            getAudioFiles();
        }
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Playing Audio", Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
                if (mediaPlayer.isPlaying())
                {
                    playbtn.setVisibility(View.GONE);
                    pausebtn.setVisibility(View.VISIBLE);
                }

            }

        });
        pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Pause", Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                    playbtn.setVisibility(View.VISIBLE);
                    pausebtn.setVisibility(View.GONE);
                }

        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if(mediaPlayer != null){
                        long totalDuration = mediaPlayer.getDuration();
                        long currentDuration = mediaPlayer.getCurrentPosition();
                        duration.setText(timerConversion((long) totalDuration));
                        currentdurtion.setText(timerConversion((long) currentDuration));
                        seekBar.setMax((int) totalDuration);
                        seekBar.setProgress((int) currentDuration);
                        hdlr.postDelayed(this, 1000);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
//        Toast.makeText(MainActivity.this, "next", Toast.LENGTH_SHORT).show();
        nextSong();
    }
});
        prevbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        prevSong();
//        Toast.makeText(MainActivity.this, "prev", Toast.LENGTH_SHORT).show();
    }
});
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    @Override
    public void onCompletion(MediaPlayer mp) {
    try {
    playSongNumber(mCurrentIndex+1);
    songTitle1.setText(modelSongs.get(mCurrentIndex+1).getSongName());
    pausebtn.setVisibility(View.VISIBLE);
    playbtn.setVisibility(View.GONE);
    mp.setLooping(true);
    } catch (Exception e) {
    e.printStackTrace();
} }}); }
        private void playSongNumber(int index) {
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(modelSongs.get(index).audioUri.getPath());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            seekBar.setProgress(0);
            seekBar.setMax(100);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
        private void nextSong(){
        mCurrentIndex++;
        mCurrentIndex %= modelSongs.size();
        playSongNumber(mCurrentIndex);
        songTitle1.setText(modelSongs.get(mCurrentIndex).getSongName());
        playbtn.setVisibility(View.GONE);
        pausebtn.setVisibility(View.VISIBLE);
    }
        private void prevSong(){
        mCurrentIndex = mCurrentIndex > 0 ? mCurrentIndex - 1 : modelSongs.size() - 1;
        playSongNumber(mCurrentIndex);
        songTitle1.setText(modelSongs.get(mCurrentIndex).getSongName());
        playbtn.setVisibility(View.GONE);
        pausebtn.setVisibility(View.VISIBLE);
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
        private void getAudioFiles() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                double duration = cursor.getDouble(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                Uri uri2 = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                modelSongs.add(new ModelSong(title, duration, uri2));
                adapterClass.updateAdapterList(modelSongs);
            } while (cursor.moveToNext());
        }
    }
        public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case  PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                    } else {
                        getAudioFiles();
                    }
                }
            }
        }
    }
        public void playAudio(int pos) {
        try  {

            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, modelSongs.get(pos).getAudioUri());
            mediaPlayer.prepare();
            mediaPlayer.start();
            songTitle1.setText(modelSongs.get(pos).getSongName());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "playAudio: "+e);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return  true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "musicX sharing with you");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
            case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("musicX");
                builder.setIcon(R.drawable.ic_cd_burning);
                builder.setMessage("\n musicX player \n version 0.01.001 \n developed @musicX \n addOnInfo: press and hold song \n to remove from list");
                builder.show();
                break;
            case R.id.exit:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("musicX \t\t Exit ");
                builder2.setIcon(R.drawable.ic_cd_burning);
                builder2.setCancelable(false);
                builder2.setMessage("\n are you sure want to exit? ");
                builder2.setPositiveButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder2.setNegativeButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alert = builder2.create();
                alert.show();
                break;
            default:
                break;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("musicX \t\t Exit ");
        builder2.setIcon(R.drawable.ic_cd_burning);
        builder2.setCancelable(false);
        builder2.setMessage("\n are you sure want to exit? ");
        builder2.setPositiveButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder2.setNegativeButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alert = builder2.create();
        alert.show();
    }
    @Override
    public void onItemClick(int pos) {

        Toast.makeText(this, modelSongs.get(pos).getSongName(), Toast.LENGTH_SHORT).show();
        playAudio(pos);
        mCurrentIndex=pos;
        pausebtn.setVisibility(View.VISIBLE);
        playbtn.setVisibility(View.GONE);
        songTitle1.setText(modelSongs.get(mCurrentIndex).getSongName());


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
