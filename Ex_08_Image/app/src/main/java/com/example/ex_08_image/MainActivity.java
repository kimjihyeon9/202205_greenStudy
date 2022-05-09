package com.example.ex_08_image;

import static com.google.ar.core.AugmentedImage.TrackingMethod.FULL_TRACKING;
import static com.google.ar.core.AugmentedImage.TrackingMethod.LAST_KNOWN_POSE;
import static com.google.ar.core.AugmentedImage.TrackingMethod.NOT_TRACKING;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GLSurfaceView mSurfaceView;
    MainRenderer mRenderer;

    Session mSession;
    Config mConfig;



    boolean mUserRequestedInstall = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBarANdTitleBar();
        setContentView(R.layout.activity_main);

        mSurfaceView = (GLSurfaceView)findViewById(R.id.gl_surface_view);

        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if(displayManager != null){
            displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
                @Override
                public void onDisplayAdded(int i) {}

                @Override
                public void onDisplayRemoved(int i) {}

                @Override
                public void onDisplayChanged(int i) {
                    synchronized (this){
                        mRenderer.mViewportChanged = true;
                    }
                }
            }, null);
        }

        mRenderer = new MainRenderer(this,new MainRenderer.RenderCallback() {
            @Override
            public void preRender() {
                if(mRenderer.mViewportChanged){
                    Display display = getWindowManager().getDefaultDisplay();
                    int displayRotation = display.getRotation();
                    mRenderer.updateSession(mSession, displayRotation);
                }

                mSession.setCameraTextureName(mRenderer.getTextureId());

                Frame frame = null;

                try {
                    frame = mSession.update();
                } catch (CameraNotAvailableException e) {
                    e.printStackTrace();
                }

                if(frame.hasDisplayGeometryChanged()){
                    mRenderer.mCamera.transformDisplayGeometry(frame);
                }

                //mRenderer.mObj.setModelMatrix(modelMatrix);

                Camera camera = frame.getCamera();
                float [] projMatrix = new float[16];
                camera.getProjectionMatrix(projMatrix,0,0.1f, 100f);
                float [] viewMatrix = new float[16];
                camera.getViewMatrix(viewMatrix,0);

                mRenderer.setProjectionMatrix(projMatrix);
                mRenderer.updateViewMatrix(viewMatrix);
            }
        });


        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setEGLConfigChooser(8,8,8,8,16,0);
        mSurfaceView.setRenderer(mRenderer);

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCameraPermission();
        try {
            if(mSession==null){
                switch(ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)){
                    case INSTALLED:
                        mSession = new Session(this);
                        Log.d("메인"," ARCore session 생성");
                        break;
                    case INSTALL_REQUESTED:
                        Log.d("메인"," ARCore 설치가 필요함");
                        mUserRequestedInstall = false;
                        break;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        mConfig = new Config(mSession);

        mSession.configure(mConfig);

        mConfig.setFocusMode(Config.FocusMode.AUTO);

        try {
            mSession.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }

        mSurfaceView.onResume();
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSurfaceView.onPause();
        mSession.pause();
    }

    void hideStatusBarANdTitleBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    void requestCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    0
            );
        }
    }

    // 이미지데이터베이스 설정
    void setUpImgDB(Config config){
        // 이미지 데이터베이스 생성
        AugmentedImageDatabase imageDatabase = new AugmentedImageDatabase(mSession);

        try {
            // 파일스트림로드
            InputStream is = getAssets().open("qr.png");
            // 파일스트림에서 Bitmap 생성
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            // 이미지데이터베이스에 bitmap 추가
            imageDatabase.addImage("bbbasd", bitmap);

            Log.d("이미지 추가", "bbb.png");
            is.close();

            // --------------------------------------
//            // 파일스트림로드
//            is = getAssets().open("qr.png");
//            // 파일스트림에서 Bitmap 생성
//            bitmap = BitmapFactory.decodeStream(is);
//            // 이미지데이터베이스에 bitmap 추가
//            imageDatabase.addImage("bbbasd", bitmap);
//
//            Log.d("이미지 추가", "bbb.png");
//            is.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // session config 에 생성한 이미지데이터베이스로 설정
    // 이미지추적 활성화

    // 이미지추적결과에 따른 그리기 설정
    void drawImages(Frame frame){

        mRenderer.isImgFind = false;

        // frame(카메라)에서 찾은 이미지들을 Collection으로 받아온다
        Collection<AugmentedImage> updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        // 찾은 이미지들을 돌린다다
        for (AugmentedImage img : updatedAugmentedImages) {

            if (img.getTrackingState() == TrackingState.TRACKING) {
                mRenderer.isImgFind = true;
                Pose imgPose = img.getCenterPose();
                Log.d("이미지 찾음", img.getIndex() + "" + imgPose.tx() + "," + imgPose.ty() + "," + imgPose.tz());

                float[] matrix = new float[16];
                imgPose.toMatrix(matrix, 0);
                mRenderer.mObj.setModelMatrix(matrix);

                // Use getTrackingMethod() to determine whether the image is currently
                // being tracked by the camera
//            switch (img.getTrackingMethod()){
//                case LAST_KNOWN_POSE:
//                    // The planar target is currently being tracked based on its last
//                    // known pose
//                    break;
//                case FULL_TRACKING:
//                    // The planar target is being using the current camera image
//                    break;
//                case NOT_TRACKING:
//                    break;
            }
        }
    }
}