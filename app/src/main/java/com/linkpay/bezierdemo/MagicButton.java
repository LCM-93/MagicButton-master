package com.linkpay.bezierdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2017/7/13 下午8:22
 * Desc:
 * *****************************************************************
 */

public class MagicButton extends View {
    private static final String TAG = "MagicButton";
    private Paint rightPaint;
    private Paint leftPaint;
    private Paint bezierPaint;

    private Circular rightCircular;  //右边的圆
    private Circular leftCircular;  //左边的圆

    private boolean isOpen = false;

    private int with;
    private int height;

    private int centerX;
    private int centerY;

    public MagicButton(Context context) {
        super(context);
    }

    public MagicButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView();
        initPaint();
    }

    private void initView() {
        with = 260;
        height = 80;
        rightCircular = new Circular();
        leftCircular = new Circular();
    }

    private void initPaint() {
        rightPaint = new Paint();
        leftPaint = new Paint();
        bezierPaint = new Paint();

        rightPaint.setColor(Color.RED);
        rightPaint.setStyle(Paint.Style.FILL);
        rightPaint.setAntiAlias(true);

        leftPaint.setColor(Color.RED);
        leftPaint.setStyle(Paint.Style.FILL);
        leftPaint.setAntiAlias(true);

        bezierPaint.setColor(Color.RED);
        bezierPaint.setStyle(Paint.Style.FILL);
        bezierPaint.setAntiAlias(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;

        Log.i(TAG, "centerX:" + centerX + "  centerY:" + centerY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        rightCircular.setCenter(with / 2 - height / 2, 0, 0);
        leftCircular.setCenter(-with / 2 + height / 2, 0, height / 2);

        rightCircular.setHalfPoint((leftCircular.centerX + leftCircular.radius + rightCircular.centerX - rightCircular.radius) / 2, (leftCircular.centerY + rightCircular.centerY) / 2);
        leftCircular.setHalfPoint((leftCircular.centerX + leftCircular.radius + rightCircular.centerX - rightCircular.radius) / 2, (leftCircular.centerY + rightCircular.centerY) / 2);
        leftCircular.setMinRadius(height/14);
        rightCircular.setMinRadius(height/14);

        setMeasuredDimension(with, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.GRAY);

        //绘制辅助坐标系
        drawCoordinateSystem(canvas);

        canvas.translate(centerX, centerY);



        
        //绘制小圆
        if (rightCircular.isNeedDraw())
//            rightCircular.drawCircle(canvas,rightPaint);
            canvas.drawCircle(rightCircular.centerX, rightCircular.centerY, rightCircular.radius, rightPaint);

        if (leftCircular.isNeedDraw())
//            leftCircular.drawCircle(canvas,leftPaint);
            canvas.drawCircle(leftCircular.centerX, leftCircular.centerY, leftCircular.radius, leftPaint);


        //绘制圆中间的贝塞尔曲线
        if (rightCircular.isNeedDraw() && leftCircular.isNeedDraw())
            drawBezier(canvas);


    }

    /**
     * 绘制贝塞尔曲线
     *
     * @param canvas
     */
    private void drawBezier(Canvas canvas) {
        Path path = new Path();
        path.moveTo(leftCircular.topPointX, leftCircular.topPointY);

        path.quadTo((rightCircular.centerX + leftCircular.centerX) / 2, 0, rightCircular.topPointX, rightCircular.topPointY);
        path.lineTo(rightCircular.bottomPointX, rightCircular.bottomPointY);
        path.quadTo((rightCircular.centerX + leftCircular.centerX) / 2, 0, leftCircular.bottomPointX, leftCircular.bottomPointY);
        path.close();

        canvas.drawPath(path, bezierPaint);
    }


    /**
     * 绘制坐标系
     *
     * @param canvas
     */
    private void drawCoordinateSystem(Canvas canvas) {
        canvas.save();  //使用新到图层绘制坐标系

        canvas.translate(centerX, centerY); //将坐标系移动到画布中央
//        canvas.scale(1, -1);  //翻转Y轴
        Paint fuzhuPaint = new Paint();

        fuzhuPaint.setColor(Color.BLACK);
        fuzhuPaint.setStrokeWidth(2);
        fuzhuPaint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(0, -200, 0, 200, fuzhuPaint);
        canvas.drawLine(-200, 0, 200, 0, fuzhuPaint);

        fuzhuPaint.setStrokeWidth(4);
        fuzhuPaint.setColor(Color.CYAN);
        canvas.drawPoint((leftCircular.centerX + leftCircular.radius + rightCircular.centerX - rightCircular.radius) / 2, (leftCircular.centerY + rightCircular.centerY) / 2, fuzhuPaint);

        canvas.restore();

    }


    public void startAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private float leftCenterX;
            private float rightCenterX;
            private float leftRadius;
            private float rightRadius;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue();

                if (!isOpen) {
                    rightRadius = curValue * height / 2;
                    leftRadius = (1 - curValue) * height / 2;

                    rightCenterX = (-with / 2 + height) + (with - height / 2 * 3) * curValue;
                    leftCenterX = (-with / 2 + height / 2) + (height / 2) * curValue;
                } else {
                    rightRadius = (1 - curValue) * height / 2;
                    leftRadius = curValue * height / 2;

                    rightCenterX = (with / 2 - height / 2) - (height / 2) * curValue;
                    leftCenterX = (with / 2 - height) - (with - height / 2 * 3) * curValue;
                }

                rightCircular.setCenter(rightCenterX, 0, rightRadius);
                leftCircular.setCenter(leftCenterX, leftCircular.centerY, leftRadius);

                rightCircular.setHalfPoint((leftCircular.centerX + leftCircular.radius + rightCircular.centerX - rightCircular.radius) / 2, (leftCircular.centerY + rightCircular.centerY) / 2);
                leftCircular.setHalfPoint((leftCircular.centerX + leftCircular.radius + rightCircular.centerX - rightCircular.radius) / 2, (leftCircular.centerY + rightCircular.centerY) / 2);

                if (curValue == 1) {
                    isOpen = !isOpen;
                }
                postInvalidate();
            }
        });


        animator.setDuration(1000);
        animator.start();
    }
}
