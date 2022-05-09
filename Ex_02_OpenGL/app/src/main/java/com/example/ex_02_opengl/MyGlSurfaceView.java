package com.example.ex_02_opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGlSurfaceView extends GLSurfaceView {
    public MyGlSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2); // Manifest에서 하는 경우도 있지만 여기서 하는경우가 많다

        setRenderer(new MyGLRenderer(context));

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
