package com.ff.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * description: 本质还是SurfaceView
 * author: FF
 * time: 2019-07-04 16:25
 */
public class GLView extends GLSurfaceView {

    public GLView(Context context) {
        super(context);
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);// 设置OpenGL版本
        setRenderer(new GLRender(false));

        // RENDERMODE_WHEN_DIRTY模式，主动调用requestRender()进行渲染，效率高，按需渲染
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
