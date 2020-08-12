package com.example.voice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity implements MediaPlayerUtils.Listener{

    FloatingActionButton fab;
    private boolean isSpeakButtonLongPressed = false;
    private RoomViewModel roomViewModel;
    private RoomModel roomModel;
    private String uuid;
    private RecyclerView recyclerView;

    private View.OnTouchListener speakTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View pView, MotionEvent pEvent) {
            pView.onTouchEvent(pEvent);

            if (pEvent.getAction() == MotionEvent.ACTION_DOWN){
                    startRecording();

            }
            else if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                if (isSpeakButtonLongPressed) {
                    // Do something when the button is released.
                    isSpeakButtonLongPressed = false;
                    stopRecording();
                }

            }
            return false;
        }
    };
    private String outputFile;
    private MediaRecorder myAudioRecorder;
    private List<RoomModel> audioList = new ArrayList<>();
    public List<AudioStatus> audioStatusList = new ArrayList<>();
    private Parcelable state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermissions();
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setSupportActionBar(toolbar);
        roomModel = new RoomModel();
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        roomViewModel.getRoomData().observe(this, new Observer<List<RoomModel>>() {
            @Override
            public void onChanged(List<RoomModel> roomModels) {
                audioList.clear();
                audioList.addAll(roomModels);
                for(int i = 0; i < audioList.size(); i++) {
                    audioStatusList.add(new AudioStatus(AudioStatus.AUDIO_STATE.IDLE.ordinal(), 0));
                }
                setRecyclerView(roomModels);
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isSpeakButtonLongPressed = true;
                return true;
            }
        });
        fab.setOnTouchListener(speakTouchListener);
    }

    private void setRecyclerView(List<RoomModel> roomModels) {
        AudioListAdapter adapter = new AudioListAdapter(this, roomModels);
        recyclerView.setAdapter(adapter);
    }

    private void checkForPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }

        }


    }

    private void startRecording() {

        uuid = UUID.randomUUID().toString();
        outputFile = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + uuid + ".3gp";
        myAudioRecorder = new MediaRecorder();

        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException ise) {

            Toast.makeText(this, "Recorder Start Failed", Toast.LENGTH_SHORT).show();

        } catch (IOException ioe) {

            ioe.printStackTrace();
            Toast.makeText(this, "Recorder Start Failed", Toast.LENGTH_SHORT).show();

        }

    }

    private void stopRecording(){


           myAudioRecorder.stop();
           myAudioRecorder.release();
           myAudioRecorder = null;
           Toast.makeText(getApplicationContext(), "Audio Recorded successfully", Toast.LENGTH_SHORT).show();
           updateDataToRoom(outputFile, uuid);


    }

    private void updateDataToRoom(String outputFile, String uuid) {

        roomModel.setIdentifier(uuid);
        roomModel.setPath(outputFile);
        roomViewModel.insertData(roomModel);
    }

    @Override
    protected void onPause() {
        super.onPause();
        state = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (state != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onDestroy() {
        MediaPlayerUtils.releaseMediaPlayer();
        super.onDestroy();
    }


    @Override
    public void onAudioComplete() {

        state = recyclerView.getLayoutManager().onSaveInstanceState();

        audioStatusList.clear();
        for(int i = 0; i < audioList.size(); i++) {
            audioStatusList.add(new AudioStatus(AudioStatus.AUDIO_STATE.IDLE.ordinal(), 0));
        }
        setRecyclerView(audioList);

        // Main position of RecyclerView when loaded again
        if (state != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }

    }

    @Override
    public void onAudioUpdate(int currentPosition) {
        int playingAudioPosition = -1;
        for(int i = 0; i < audioStatusList.size(); i++) {
            AudioStatus audioStatus = audioStatusList.get(i);
            if(audioStatus.getAudioState() == AudioStatus.AUDIO_STATE.PLAYING.ordinal()) {
                playingAudioPosition = i;
                break;
            }
        }

        if(playingAudioPosition != -1) {
            AudioListAdapter.ViewHolder holder
                    = (AudioListAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(playingAudioPosition);
            if (holder != null) {
                holder.seekBarAudio.setProgress(currentPosition);
            }
        }
    }
}