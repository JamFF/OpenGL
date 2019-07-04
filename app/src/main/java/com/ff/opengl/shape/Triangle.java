package com.ff.opengl.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * description: 三角形
 * author: FF
 * time: 2019-07-04 16:57
 */
public class Triangle {
    // OpenGL 操作步骤：1.初始化，2.渲染

    // 数据指针，地址
    private int mProgram;

    // 三角形的三个顶点坐标，x、y、z轴
    private static final float TRIANGLE_COORDS[] = {
            0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    // 颜色RGBA，4个元素
    private static final float COLOR[] = {1.0f, 1.0f, 1.0f, 1.0f};

    // 可以理解为针对float类型的管道
    private FloatBuffer vertexBuffer;
    // 顶点着色器，gl语言，vMatrix是矩阵，目的是为了渲染和预期一样的等腰三角形
    private String vertexShaderCode = "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;" +
            "void main(){" +
            "gl_Position=vMatrix*vPosition;" +
            "}";
    // 片元着色器
    private final String fragmentShaderCode = "precision mediump float;" +
            "uniform  vec4 vColor;" +
            "void main(){" +
            "gl_FragColor=vColor;" +
            "}";

    // 声明矩阵
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 固定的写法

        // 计算宽高比
        float ratio = (float) width / height;
        // 投影面矩阵，就是将三角形需要投影到的平面，mProjectMatrix是入参出参变量
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 120);
        // 设置相机
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7,// 相机的坐标
                0f, 0f, 0f,// 目标物的中心坐标，在原点
                0f, 1f, 0f);// 相机方向，从上往下看y为正
        // 计算变换矩阵，将GLSurfaceView里面的矩阵缩放转变为gl里面的缩放，变换的结果储存在mMVPMatrix中
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    public Triangle() {
        // 在GPU声明空间，3个顶点，9个元素，每个float4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(TRIANGLE_COORDS.length * 4);
        // GPU缓冲区字节顺序，设置为默认
        bb.order(ByteOrder.nativeOrder());

        // ByteBuffer需要转化为FloatBuffer
        vertexBuffer = bb.asFloatBuffer();
        // 把这门语法推送给GPU
        vertexBuffer.put(TRIANGLE_COORDS);
        vertexBuffer.position(0);

        // 创建顶点着色器，返回一个ID，使用2.0版本所以都是GLES20
        int shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        // 为顶点着色器设置代码
        GLES20.glShaderSource(shader, vertexShaderCode);
        // 在GPU进行编译
        GLES20.glCompileShader(shader);

        // 创建片元着色器，返回一个ID
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        // 为片元着色器设置代码
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        // 在GPU进行编译
        GLES20.glCompileShader(fragmentShader);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();
        // 将顶点着色器、片元着色器，放到Program进行管理
        GLES20.glAttachShader(mProgram, shader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        // 连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    // 渲染
    public void onDrawFrame(GL10 gl) {

        GLES20.glUseProgram(mProgram);
        // 获取gl的矩阵变量vMatrix
        int mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        // 向gl传递数据，参数：一个矩阵，GL_TRUE，变量的数据指针，偏移量
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        // 可以理解为指针，vPosition存放在GPU某个内存区域，Java层拿不到这个变量，只能拿到地址
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 打开：允许对变量读写
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 对顶点着色器赋值，变量地址，三个顶点，float类型，固定点值GL_FALSE，每一行字节数，偏移量
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                3 * 4, vertexBuffer);
        // 获取片元着色器的vColor变量
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // 对vColor赋值，1个颜色，颜色数组，偏移量
        GLES20.glUniform4fv(mColorHandle, 1, COLOR, 0);
        // TODO GPU渲染，绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        // 关闭
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
