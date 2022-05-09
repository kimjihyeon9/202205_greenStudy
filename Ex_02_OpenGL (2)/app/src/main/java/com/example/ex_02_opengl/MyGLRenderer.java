package com.example.ex_02_opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    Square  myBox;

    float [] mMVPMatrix = new float[16];
    float [] mProjectionMatrix = new float[16];
    float [] mViewMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);

        myBox = new Square();
    }

    //화면갱신 되면서 화면 에서 배치
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width, height);

        float ratio = (float) width / height;

        Matrix.frustumM(mProjectionMatrix, 0,-ratio,ratio,-1,1,3,7);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(mViewMatrix, 0,
                //x, y, z
                  0,0,3,  //카메라 위치
                0,0,0, //카메라 시선
                0,1,0//카메라 윗방향
        );


        Matrix.multiplyMM(mMVPMatrix, 0,mProjectionMatrix,0, mViewMatrix,0);

        //정사각형 그리기
        myBox.draw(mMVPMatrix);
    }


    //GPU를 이용하여 그리기를 연산한다.
    static int loadShader(int type, String shaderCode){

        int res = GLES20.glCreateShader(type);

        GLES20.glShaderSource(res, shaderCode);
        GLES20.glCompileShader(res);
        return res;
    }
}
