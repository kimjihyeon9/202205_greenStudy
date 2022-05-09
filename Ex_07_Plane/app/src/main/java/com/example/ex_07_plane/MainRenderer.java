package com.example.ex_07_plane;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.google.ar.core.Session;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {

    CameraPreView mCamera;
    PointCloudRenderer mPointCloud;
    PlaneRenderer mPlane;
    Cube mCube;
    ObjRenderer mObj;

    ObjRenderer mObj01; // table

    boolean mViewportChanged;
    int mViewportWidth, mViewpotHeight;
    RenderCallback mRenderCallback;

    MainRenderer(Context context, RenderCallback callback){
        mRenderCallback = callback;
        mCamera = new CameraPreView();
        mPointCloud = new PointCloudRenderer();
        mPlane = new PlaneRenderer(Color.BLUE, 0.7f);

        // 큐브생성
        mCube = new Cube(0.3f, Color.YELLOW, 0.8f);
        mObj = new ObjRenderer(context, "andy.obj", "andy.png");

        mObj01 = new ObjRenderer(context, "table.obj", "table.jpg");
    }

    interface RenderCallback{
        void preRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);

        mCamera.init();
        mPointCloud.init();
        mPlane.init();
        mCube.init();
        mObj.init();

        mObj01.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0, width, height);
        mViewportChanged = true;
        mViewportWidth = width;
        mViewpotHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderCallback.preRender();

        GLES20.glDepthMask(false);
        mCamera.draw();
        GLES20.glDepthMask(true);
        mPointCloud.draw();

        mPlane.draw();
        mCube.draw();
        mObj.draw();

        mObj01.draw();
    }

    void updateSession(Session session, int displayRotation){
        if (mViewportChanged) {
            session.setDisplayGeometry(displayRotation, mViewportWidth, mViewpotHeight);
            mViewportChanged = false;
        }
    }

    void setProjectionMatrix(float [] matrix) {
        mPointCloud.updateProjMatrix(matrix);
        mPlane.setProjectionMatrix(matrix);
        mCube.setProjectionMatrix(matrix);
        mObj.setProjectionMatrix(matrix);

        mObj01.setProjectionMatrix(matrix);
    }

    void updateViewMatrix(float [] matrix) {
        mPointCloud.updateViewMatrix(matrix);
        mPlane.setViewMatrix(matrix);
        mCube.setViewMatrix(matrix);
        mObj.setViewMatrix(matrix);

        mObj01.setViewMatrix(matrix);
    }

    int getTextureId(){
        return mCamera == null ? -1 : mCamera.mTextures[0];
    }
}
