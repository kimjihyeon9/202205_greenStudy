package com.example.ex_02_opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
//    Square myBox;
    
    // 과제(집)
//    Square1 myDoor;
//    Square1 myHome;
//    Square1 Top;
//    Square1 HomeTop;
//    Square1 Window;
//    Square1 myHomeRight;
    
    ObjRenderer myTable;

    float [] mMVPMatrix = new float[16];
    float [] mProjectionMatrix = new float[16];
    float [] mViewMatrix = new float[16]; // 시선에 관한 것
    
    MyGLRenderer(Context context){
        myTable = new ObjRenderer(context,"table.obj", "table.jpg");
//        myTable = new ObjRenderer(context,"Soborg.obj", "kitchen.png");
    }

    // 직사각형 점의 좌표
    // 과제(집)
//    static float [] squareCoords1 = { // 집 몸통
//            // x, y, z
//            -0.5f, 0.5f, 0.5f,  // 왼쪽 위
//            -0.5f, -0.5f, 0.5f, // 왼쪽 아래
//            0.5f, -0.5f, 0.5f,   // 오른쪽 아래
//            0.5f, 0.5f, 0.5f   // 오른쪽 위
//    };
//    static float [] squareCoords2 = { // 문
//            // x, y, z
//            0.0f, 0.05f, 0.5f,  // 왼쪽 위
//            0.0f, -0.45f, 0.5f, // 왼쪽 아래
//            0.35f, -0.45f, 0.5f,   // 오른쪽 아래
//            0.35f, 0.05f, 0.5f   // 오른쪽 위
//    };
//    static float [] squareCoords3 = { // 굴뚝
//            // x, y, z
//            0.15f, 1.08f, 0.5f,  // 왼쪽 위
//            0.15f, 0.5f, 0.5f, // 왼쪽 아래
//            0.4f, 0.5f, 0.5f,   // 오른쪽 아래
//            0.4f, 1.08f, 0.5f   // 오른쪽 위
//    };
//    static float [] squareCoords4 = { // 집 지붕
//            // x, y, z
//            -0.5f, 0.5f, 0.5f,  // 왼쪽 위
//            -0.5f, -0.5f, 0.5f, // 왼쪽 아래
//            0.5f, -0.5f, 0.5f,   // 오른쪽 아래
//            0.5f, 0.5f, 0.5f,   // 오른쪽 위
//            0.0f, 1.0f, 0.5f
//    };
//    static float [] squareCoords5 = { // 창문
//            // x, y, z
//            -0.3f, 0.4f, 0.5f,  // 왼쪽 위
//            -0.3f, 0.2f, 0.5f, // 왼쪽 아래
//            -0.1f, 0.2f, 0.5f,   // 오른쪽 아래
//            -0.1f, 0.4f, 0.5f   // 오른쪽 위
//    };
//    static float [] squareCoords6 = { // 집 오른쪽 몸통
//            // x, y, z
//            0.5f, 0.5f, 0.5f,  // 왼쪽 위
//            0.5f, -0.5f, 0.5f, // 왼쪽 아래
//            0.5f, 0.5f, -0.5f,   // 오른쪽 아래
//            0.5f, 0.5f, -0.5f   // 오른쪽 위
//    };
//
//    // 그리는 순서
//    short [] drawOrder1 = {
//            0,1,2,
//            0,2,3,
//            0,3,4
//    };
//    short [] drawOrder2 = {
//            0,1,2,
//            0,2,3
//    };
//
//    float [] color1 = {1.0f, 0.5f, 0.3f, 1.0f}; // 주황
//    float [] color2 = {1.0f, 1.0f, 0.0f, 1.0f}; //
//    float [] color3 = {1.0f, 0.3f, 0.3f, 1.0f}; //
//    float [] color4 = {1.0f, 0.4f, 0.3f, 1.0f}; //
//    float [] color5 = {0.25f, 0.25f, 0.5f, 1.0f}; //

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f); // 노랑색
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f); // 형광하늘색?
        
//        myBox = new Square();

        // 과제(집)
//        myDoor = new Square1(squareCoords1, drawOrder1, color1);
//        myHome = new Square1(squareCoords2, drawOrder2, color2);
//        Top = new Square1(squareCoords3, drawOrder2, color3);
//        HomeTop = new Square1(squareCoords4, drawOrder1, color4);
//        Window = new Square1(squareCoords5, drawOrder1, color5);
//        myHomeRight = new Square1(squareCoords6, drawOrder2, color1);

//        myTable = new ObjRenderer("table.obj", "table.jpg"); // 에러 -> 생성자를 만들어서 거기로 이동
        myTable.init();
    }

    // 화면갱신 되면서 화면에서 배치
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0, width, height);

        float ratio = (float) width * 30 / height;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -100, 100, 20,100); // near ~ far 사이에서 물체가 보인다
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(mViewMatrix, 0,
                // x, y, z
                0,5,0, // 카메라 위치
                0,0,-30, // 카메라 시선
                0,1,0 // 카메라 윗방향
                );

//        Matrix.multiplyMM(mMVPMatrix,0, mProjectionMatrix,0, mViewMatrix,0);

        // 정사각형 그리기
//        myBox.draw(mMVPMatrix);

        // 과제(집)
//        Top.draw(mMVPMatrix);
//        HomeTop.draw(mMVPMatrix);
//        myDoor.draw(mMVPMatrix);
//        myHome.draw(mMVPMatrix);
//        Window.draw(mMVPMatrix);
//        myHomeRight.draw(mMVPMatrix);

        Matrix.setIdentityM(mMVPMatrix,0);

        myTable.setProjectionMatrix(mProjectionMatrix);
        myTable.setViewMatrix(mViewMatrix);
        myTable.setModelMatrix(mMVPMatrix);
        myTable.draw();
    }

    // GPU 를 이용하여 그리기를 연산한다
   static int loadShader(int type, String shaderCode){
        int res = GLES20.glCreateShader(type);

        GLES20.glShaderSource(res, shaderCode);
        GLES20.glCompileShader(res);

        return res;
    }
}
