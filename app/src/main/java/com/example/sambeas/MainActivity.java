package com.example.sambeas;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    Button recordButton, playButton;
    TextView recordPathText, timeTV;
    Boolean isPlaying = false;
    Boolean isRecording = false;

    Handler handler;
    int seconds = 0;
    String path = null;
    int dummySeconds = 0;
    int playableSeconds = 0;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordButton = findViewById(R.id.recordBtn);
        playButton = findViewById(R.id.playBtn);
        recordPathText = findViewById(R.id.textView);
        timeTV = findViewById(R.id.timeTextView);

        mediaPlayer = new MediaPlayer();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying){
                    if (path !=null){
                        try {
                            mediaPlayer.setDataSource(getRecordingFilePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "No recording found", Toast.LENGTH_SHORT).show();
                        return;
                    }



                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.stop();
                    isPlaying = true;
                    runTimer();

                }
                else{
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    mediaPlayer = new MediaPlayer();
                    isPlaying = false;
                    seconds = 0;

                }
            }
        });


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkRecordingPermission()){
                    if (!isRecording){
                        isRecording = true;
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                mediaRecorder = new MediaRecorder();
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                mediaRecorder.setOutputFile(getRecordingFilePath());
                                path = getRecordingFilePath();

                                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                                try {
                                    mediaRecorder.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                mediaRecorder.start();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recordPathText.setText(getRecordingFilePath());
                                        playableSeconds = 0;
                                        seconds = 0;
                                        dummySeconds = 0;
                                        runTimer();
                                        handler.removeCallbacksAndMessages(null);
                                    }
                                });


                            }
                        });
                    }
                    else{
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                mediaRecorder.stop();
                                mediaRecorder.release();
                                mediaRecorder = null;
                                playableSeconds = seconds;
                                dummySeconds = seconds;
                                seconds = 0;
                                isRecording = false;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        handler.removeCallbacksAndMessages(null);
                                        playableSeconds = 0;
                                        seconds = 0;
                                        dummySeconds = 0;
                                    }
                                });


                            }
                        });
                    }

                }else{
                    requestRecordingPermissions();
                }
            }
        });
    }
    private void requestRecordingPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_AUDIO_PERMISSION_CODE);
    }

    public boolean checkRecordingPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            requestRecordingPermissions();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_AUDIO_PERMISSION_CODE){
            boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (permissionToRecord){
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Path for the music to be stored and named
    private String getRecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File music = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(music,"testFile"+".mp3");
        return file.getPath();
    }

    private void runTimer(){
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minute = (seconds % 3600)/60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", minute, secs);
                timeTV.setText(time);

                if(isRecording || (isPlaying && playableSeconds !=-1)){
                    seconds++;
                    playableSeconds--;

                    if(playableSeconds ==-1 && isPlaying){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        mediaPlayer = new MediaPlayer();
                        playableSeconds = dummySeconds;
                        seconds = 0;
                        handler.removeCallbacksAndMessages(null);
                        return;
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}