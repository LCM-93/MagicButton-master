package com.linkpay.bezierdemo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2017/7/13 下午8:47
 * Desc:
 * *****************************************************************
 */

public class Circular {

    private static final float C = 0.551915024494f; //常量

    //圆中心坐标
    float centerX;
    float centerY;

    //圆半径
    float radius;

    //圆与贝塞尔曲线相接的上顶点
    float topPointX;
    float topPointY;

    //圆与贝塞尔曲线相接的下顶点
    float bottomPointX;
    float bottomPointY;

    float minRadius = 0;

    private float[] mData = new float[8];  //顺时针记录绘制圆形的四个数据点
    private float[] mCtrl = new float[16];  //顺时针记录绘制圆形的八个控制点


    public void setCenter(float centerX, float centerY, float radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;

//        initPoint();

//        initCotrlPoint();
    }


    /**
     * TO-DO
     * 更精细的绘制圆，绘制小球的回弹效果
     */
    private void initCotrlPoint() {
        float mDifference = radius * C;
        //初始化数据点
        mData[0] = centerX;
        mData[1] = centerY + radius;

        mData[2] = centerX + radius;
        mData[3] = centerY;

        mData[4] = centerX;
        mData[5] = centerY - radius;

        mData[6] = centerX - radius;
        mData[7] = centerY;

        //初始化控制点
        mCtrl[0] = mData[0] + mDifference;
        mCtrl[1] = mData[1];

        mCtrl[2] = mData[2];
        mCtrl[3] = mData[3] + mDifference;

        mCtrl[4] = mData[2];
        mCtrl[5] = mData[3] - mDifference;

        mCtrl[6] = mData[4] + mDifference;
        mCtrl[7] = mData[5];

        mCtrl[8] = mData[4] - mDifference;
        mCtrl[9] = mData[5];

        mCtrl[10] = mData[6];
        mCtrl[11] = mData[7] - mDifference;

        mCtrl[12] = mData[6];
        mCtrl[13] = mData[7] + mDifference;

        mCtrl[14] = mData[0] - mDifference;
        mCtrl[15] = mData[1];
    }


    /**
     * TO-DO
     * 通过三阶贝塞尔曲线更精细的绘制圆，绘制小球的回弹效果
     *
     * @param canvas
     * @param mPaint
     */
    public void drawCircle(Canvas canvas, Paint mPaint) {
        Path path = new Path();
        path.moveTo(mData[0], mData[1]);

        path.cubicTo(mCtrl[0], mCtrl[1], mCtrl[2], mCtrl[3], mData[2], mData[3]);
        path.cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5]);
        path.cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7]);
        path.cubicTo(mCtrl[12], mCtrl[13], mCtrl[14], mCtrl[15], mData[0], mData[1]);

        canvas.drawPath(path, mPaint);
    }


    //直接以圆上顶点与下顶点作为与贝塞尔曲线相切的点
    private void initPoint() {
        topPointX = centerX;
        topPointY = -radius;

        bottomPointX = centerX;
        bottomPointY = radius;
    }


    //为了使贝塞尔曲线与圆相接的更加圆滑，动态计算相切的点
    public void setHalfPoint(float halfPointX, float halfPointY) {

        float halfDistance = (float) Math.sqrt((halfPointX - centerX) * (halfPointX - centerX) + (halfPointY - centerY) * (halfPointY - centerY));

        float a = (float) Math.sqrt(halfDistance * halfDistance - radius * radius);
        float b = radius * (radius / halfDistance);
        float c = radius * (a / halfDistance);

        topPointX = (halfPointX - centerX) > 0 ? centerX + b : centerX - b;
        topPointY = centerY - c;

        bottomPointX = (halfPointX - centerX) > 0 ? centerX + b : centerX - b;
        bottomPointY = centerY + c;
    }

    public void setMinRadius(float minRadius) {

        this.minRadius = minRadius;
    }

    //是否需要绘制
    public boolean isNeedDraw() {
        return radius > minRadius;
    }

}
