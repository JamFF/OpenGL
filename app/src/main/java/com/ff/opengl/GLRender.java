package com.ff.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.ff.opengl.shape.Cube;
import com.ff.opengl.shape.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * description: 配合GLSurfaceView使用
 * author: FF
 * time: 2019-07-04 16:38
 */
public class GLRender implements GLSurfaceView.Renderer {

    private boolean isTriangle;

    private Triangle mTriangle;// 绘制三角形
    private Cube mCube;// 绘制立方体

    public GLRender(boolean isTriangle) {
        this.isTriangle = isTriangle;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 清空颜色
        GLES20.glClearColor(0, 0, 0, 0);
        // OpenGL初始化
        if (isTriangle) {
            mTriangle = new Triangle();
        } else {
            mCube = new Cube();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (isTriangle) {
            mTriangle.onSurfaceChanged(gl, width, height);
        } else {
            mCube.onSurfaceChanged(gl, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 不断被回调，由requestRender出发，类似View中的invaliadate
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);// 清空颜色
        // 进行OpenGL渲染
        if (isTriangle) {
            mTriangle.onDrawFrame(gl);
        } else {
            mCube.onDrawFrame(gl);
        }
    }
}
