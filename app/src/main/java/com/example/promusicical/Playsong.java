package com.example.promusicical;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Playsong extends AppCompatActivity {
    ImageView fastremine,fastforward,next,previous,pause;
    TextView txtstart,txtstop,txtsname;
    SeekBar seebar;
    ImageView imageview;

    String sname;
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mysong;

    Thread updateseekbar;
    int starttime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);

//        getSupportActionBar().setTitle("Now playing");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fastremine = findViewById(R.id.fastrevine);
        fastforward = findViewById(R.id.fastforward);
        next = findViewById(R.id.nextbtn);
        previous = findViewById(R.id.previousbtn);
        pause = findViewById(R.id.playbtn);
        txtsname = findViewById(R.id.txtsn);
        txtstart = findViewById(R.id.txtstart);
        txtstop = findViewById(R.id.txtstop);
        seebar = findViewById(R.id.seekbar);
        imageview = findViewById(R.id.imageview);


        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent playintent = getIntent();
        Bundle bundle = playintent.getExtras();
        mysong = (ArrayList) bundle.getParcelableArrayList("song");
        position = bundle.getInt("pos",0);
        String songname = playintent.getStringExtra("songname");
        txtsname.setSelected(true);
        Uri uri = Uri.parse(mysong.get(position).toString());
        sname = mysong.get(position).getName();
        txtsname.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String endtime = createtime(mediaPlayer.getDuration());
                txtstop.setText(endtime);
                String currenttime = createtime(mediaPlayer.getCurrentPosition());
                txtstart.setText(currenttime);
                handler.postDelayed(this,delay);
                if(currenttime.equals(endtime)){
                    next();
                }
                updateseekbar = new Thread(){
                    @Override
                    public void run() {
                        int totalduration = mediaPlayer.getDuration();
                        int currentposition = 0;
                        while(currentposition<totalduration){
                            try {
                                sleep(500);
                                currentposition = mediaPlayer.getCurrentPosition();
                                seebar.setProgress(currentposition);
                            }
                            catch (InterruptedException | IllegalStateException e){
                                e.printStackTrace();
                            }
                        }
                    }
                };
                seebar.setMax(mediaPlayer.getDuration());
                updateseekbar.start();
//        seebar.setProgressDrawable().
                seebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                });
            }
        },delay);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    pause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                    mediaPlayer.start();
                }
            }
        });

        // next lishtner
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next.performClick();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                mediaPlayer.release();
                position = (position+1)%mysong.size();
                Uri u = Uri.parse(mysong.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mysong.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                pause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                StartAnimation(imageview);
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                mediaPlayer.release();
                position = ((position-1)<0)?(mysong.size()-1):(position-1);
                Uri u = Uri.parse(mysong.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mysong.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                pause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                StartAnimation(imageview);
            }
        });
        fastforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        fastremine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

    }
    public String createtime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time+=min+":";
        if(sec<10){
            time+=0;
        }
        time+=sec;
        return time;
    }
    public void StartAnimation(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageview,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public void next(){
        mediaPlayer.start();
        mediaPlayer.release();
        position = (position+1)%mysong.size();
        Uri u = Uri.parse(mysong.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
        sname = mysong.get(position).getName();
        txtsname.setText(sname);
        mediaPlayer.start();
        pause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        StartAnimation(imageview);
    }
}