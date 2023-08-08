package com.example.mounika;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import android.hardware.Camera;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class VideoAudioActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int REQUEST_PERMISSION_CODE = 100;
    private MediaRecorder mediaRecorder;
    private SurfaceHolder surfaceHolder;
    private boolean isRecording = false;
    private String videoFilePath;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_audio);

        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        Button btnRecord = findViewById(R.id.btnRecord);
        videoView = findViewById(R.id.videoView);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
                } else {
                    if (checkPermissions()) {
                        startRecording();
                    } else {
                        requestPermissions();
                    }
                }
            }
        });
    }

    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                audioPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                REQUEST_PERMISSION_CODE);
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
            playRecordedVideo();
            triggerMediaScanner(videoFilePath);
        }
    }

    // Add this method to trigger the media scanner for the saved video file
    private void triggerMediaScanner(String filePath) {
        android.content.Intent mediaScanIntent = new android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        android.net.Uri contentUri = android.net.Uri.fromFile(new File(filePath));
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            int frontCameraId = getFrontCameraId();
            if (frontCameraId == -1) {
                Toast.makeText(this, "Front camera not available", Toast.LENGTH_SHORT).show();
                releaseMediaRecorder();
                return;
            }
            Camera camera = Camera.open(frontCameraId);
            Camera.Parameters parameters = camera.getParameters();
            camera.unlock();
            mediaRecorder.setCamera(camera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                mediaRecorder.setProfile(profile);
            } else {
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mediaRecorder.setVideoEncodingBitRate(10000000);
                mediaRecorder.setVideoFrameRate(30);
                mediaRecorder.setVideoSize(1280, 720);
            }
            videoFilePath = getOutputFilePath();
            mediaRecorder.setOutputFile(videoFilePath);
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                releaseMediaRecorder();
                Toast.makeText(this, "Error preparing recording: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                releaseMediaRecorder();
                Toast.makeText(this, "Error starting recording: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int getFrontCameraId() {
        int cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraCount; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return i;
            }
        }
        return -1;
    }

    private void playRecordedVideo() {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videoFilePath);
        videoView.start();
    }
    private static final String VIDEO_FOLDER_NAME = "Gallery"; // Change this to your desired folder name
    @NonNull
    private String getOutputFilePath() {
        File mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        if (!mediaDir.exists()) {
            mediaDir.mkdirs();
        }

        File videoFolder = new File(mediaDir, VIDEO_FOLDER_NAME);
        if (!videoFolder.exists()) {
            videoFolder.mkdirs();
        }

        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        return videoFolder.getPath() + File.separator + fileName;
    }


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (isRecording) {
            stopRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                startRecording();
            }
        }
    }
}