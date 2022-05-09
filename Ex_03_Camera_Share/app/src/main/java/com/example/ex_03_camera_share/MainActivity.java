package com.example.ex_03_camera_share;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;

public class MainActivity extends AppCompatActivity {
    Session mSession;

    GLSurfaceView mySurView;

    MainRenderer mRenderer;

    Config mConfig; // ARCore session 설정정보를 받을 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySurView = (GLSurfaceView) findViewById(R.id.glsurfaceview);

        // MainActivity 의 화면 관리 메니져 --> 화면변화를 감지 :: 현재 시스템에서 서비스 지원
        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);

        // 화면 변화가 발생되면 MainRenderer 의 화면변환을 실행시킨다
        if (displayManager != null){
            // 화면에 대한 리스너 실행
            displayManager.registerDisplayListener(
                    // 익명클래스로 정의
                    new DisplayManager.DisplayListener() {

                        @Override
                        public void onDisplayAdded(int i) {

                        }

                        @Override
                        public void onDisplayRemoved(int i) {

                        }

                        // 화면이 변경되었다면
                        @Override
                        public void onDisplayChanged(int i) {
                            synchronized (this) {
                                // 화면 갱신인지 메소드 실행
                                mRenderer.onDisplayChanged();
                            }
                        }
                    } ,
                    null
            );
        }

        MainRenderer.RenderCallBack mr = new MainRenderer.RenderCallBack() {
            @Override
            public void preRender() {
                // 화면이 회전되었다면
                if(mRenderer.viewprotChanged){
                    // 현재 화면 가져오기w
                    Display display = getWindowManager().getDefaultDisplay();

                    mRenderer.updateSession(mSession, display.getRotation());
                }
                
                // session 객체와 연결해서 화면 그리기 하기
                mSession.setCameraTextureName(mRenderer.getTextureId());

                // 화면 그리기에서 사용할 frame --> session 이 업데이트 되면 새로운 프레임을 받는다
                Frame frame = null;

                try {
                    frame = mSession.update();
                } catch (CameraNotAvailableException e) {
                    e.printStackTrace();
                }

                // 화면을 바꾸기 위한 작업
//                mRenderer.transformDisplayGeometry(frame); // 아래랑 동일한것
                mRenderer.mCamera.transformDisplayGeometry(frame);
            }
        };

        mRenderer = new MainRenderer(mr);

        // pause 시 관련 데이터가 사라지는 것을 막는다
        mySurView.setPreserveEGLContextOnPause(true);
        // 버전을 2.0 사용
        mySurView.setEGLContextClientVersion(2);

        // 화면을 그리는 Renderer를 지정한다
        // 새로 정의한 MainRenderer를 사용한다
        mySurView.setRenderer(mRenderer);
        
        // 랜더링 계속 호출
        mySurView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mySurView.onPause();
        mSession.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPerm();

        try {
            if(mSession == null){
//                ARCore가 정상적으로 설치되어있는가?
//                Log.d("session requestInstall ? ",
//                        ArCoreApk.getInstance().requestInstall(this, true) + "");

                switch (ArCoreApk.getInstance().requestInstall(this, true)){
                    case INSTALLED: // ARCore 정상설치 되었음
                        // ARCore 가 정상설치 되어 session 을 생성가능한 형태임
                        mSession = new Session(this);
                        Log.d("session 인감", "session 생성이여!!!");
                        break;
                    case INSTALL_REQUESTED: // ARCore 설치 필요
                        Log.d("session 인감", "ARCore INSTALL_REQUSTED");
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 화면 갱신 시 세션설정 정보를 받아서 내세션의 설정으로 올린다.
        mConfig = new Config(mSession);

        mSession.configure(mConfig);

        try {
            mSession.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }

        mySurView.onResume();
    }

    // 카메라 퍼미션 요청
    void cameraPerm() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA},
                    0
            );
        }
    }
}