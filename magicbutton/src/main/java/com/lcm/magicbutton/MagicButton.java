package com.lcm.magicbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2017/7/13 下午8:22
 * Desc: 贝塞尔曲线绘制的一个有趣的开关
 * *****************************************************************
 */

public class MagicButton extends View {
    private static final String TAG = "MagicButton";
    private Paint linePaint;
    private Paint mainPaint;

    private Circular rightCircular;  //右边的圆
    private Circular leftCircular;  //左边的圆


    private int width;
    private int height;

    private int centerX;
    private int centerY;

    private boolean isAnimatorStart = false;

    private boolean isChecked = false;
    private int checkedColor = Color.GREEN;
    private int unCheckedColor = Color.RED;
    private long duration = 400;
    private int paintColor;

    private OnCheckChangeListenter onCheckChangeListenter;

    public void setOnCheckChangeListenter(OnCheckChangeListenter onCheckChangeListenter) {
        this.onCheckChangeListenter = onCheckChangeListenter;
    }

    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            startAnimator();
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public MagicButton(Context context) {
        this(context, null);
    }

    public MagicButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获取自定义属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MagicButton, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.MagicButton_checkedColor) {
                checkedColor = a.getColor(attr, Color.GREEN);
            } else if (attr == R.styleable.MagicButton_unCheckedColor) {
                unCheckedColor = a.getColor(attr, Color.RED);
            } else if (attr == R.styleable.MagicButton_duration) {
                duration = (long) a.getFloat(attr, 400);
            } else if (attr == R.styleable.MagicButton_isChecked) {
                isChecked = a.getBoolean(attr, false);
            }
        }
        initPaint();
    }

    private void initPaint() {
        paintColor = isChecked ? checkedColor : unCheckedColor;

        rightCircular = new Circular();
        leftCircular = new Circular();

        linePaint = new Paint();
        mainPaint = new Paint();

        mainPaint.setColor(paintColor);
        mainPaint.setStyle(Paint.Style.FILL);
        mainPaint.setAntiAlias(true);

        linePaint.setColor(paintColor);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAntiAlias(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                startAnimator();
                break;
        }
        return true;
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

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = 120;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = 50;
        }


        rightCircular.setCenter(width / 2 - height / 2, 0, isChecked ? height / 2 - 4 : 0);
        leftCircular.setCenter(-width / 2 + height / 2, 0, isChecked ? 0 : height / 2 - 4);

        rightCircular.setHalfPoint((leftCircular.centerX + leftCircular.radius + rightCircular.centerX - rightCircular.radius) / 2, (leftCircular.centerY + rightCircular.centerY) / 2);
        leftCircular.setHalfPoint((leftCircular.centerX + leftCircular.radius + rightCircular.centerX - rightCircular.radius) / 2, (leftCircular.centerY + rightCircular.centerY) / 2);
        leftCircular.setMinRadius(height / 14);
        rightCircular.setMinRadius(height / 14);

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawColor(Color.GRAY);
        //绘制辅助坐标系
//        drawCoordinateSystem(canvas);
        canvas.translate(centerX, centerY);

        //绘制背景
        drawbgLine(canvas);


        mainPaint.setColor(paintColor);
        //绘制小圆
        if (rightCircular.isNeedDraw())
            canvas.drawCircle(rightCircular.centerX, rightCircular.centerY, rightCircular.radius, mainPaint);

        if (leftCircular.isNeedDraw())
            canvas.drawCircle(leftCircular.centerX, leftCircular.centerY, leftCircular.radius, mainPaint);

        //绘制圆中间的贝塞尔曲线
        if (rightCircular.isNeedDraw() && leftCircular.isNeedDraw())
            drawBezier(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawbgLine(Canvas canvas) {
        Path path = new Path();

        path.moveTo(-width / 2 + height / 2, height / 2);

        RectF rectF = new RectF(-width / 2, -height / 2, -width / 2 + height, height / 2);
        path.addArc(rectF, 90, 180);

        path.lineTo(width / 2 - height / 2, -height / 2);

        RectF rectF1 = new RectF(width / 2 - height, -height / 2, width / 2, height / 2);
        path.addArc(rectF1, -90, 180);

        path.lineTo(-width / 2 + height / 2, height / 2);


        linePaint.setColor(paintColor);
        canvas.drawPath(path, linePaint);


        Path path1 = new Path();
        path1.moveTo(-width / 2 + height / 2, height / 2 - 2);

        RectF rectF2 = new RectF(-width / 2 + 2, -height / 2 + 2, -width / 2 + height - 2, height / 2 - 2);
        path1.addArc(rectF2, 90, 180);

        path1.lineTo(width / 2 - height / 2, -height / 2 + 2);

        RectF rectF3 = new RectF(width / 2 - height + 2, -height / 2 + 2, width / 2 - 2, height / 2 - 2);
        path1.addArc(rectF3, -90, 180);

        path1.lineTo(-width / 2 + height / 2, height / 2 - 2);

        linePaint.setColor(Color.WHITE);
        canvas.drawPath(path1, linePaint);
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

        canvas.drawPath(path, mainPaint);
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


    /**
     * 切换动画
     */
    private void startAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float leftCenterX;
            private float rightCenterX;
            private float leftRadius;
            private float rightRadius;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue();

                if (!isChecked) {
                    rightRadius = curValue * (height / 2 - 4);
                    leftRadius = (1 - curValue) * (height / 2 - 4);

                    rightCenterX = (-width / 2 + height) + (width - height / 2 * 3) * curValue;
                    leftCenterX = (-width / 2 + height / 2) + (height / 2) * curValue;
                } else {
                    rightRadius = (1 - curValue) * (height / 2 - 4);
                    leftRadius = curValue * (height / 2 - 4);

                    rightCenterX = (width / 2 - height / 2) - (height / 2) * curValue;
                    leftCenterX = (width / 2 - height) - (width - height / 2 * 3) * curValue;
                }

                rightCircular.setCenter(rightCenterX, 0, rightRadius);
                leftCircular.setCenter(leftCenterX, leftCircular.centerY, leftRadius);

                rightCircular.setHalfPoint((leftCircular.centerX + leftCircular.radius + rightCircular.centerX - rightCircular.radius) / 2, (leftCircular.centerY + rightCircular.centerY) / 2);
                leftCircular.setHalfPoint((leftCircular.centerX + leftCircular.radius + rightCircular.centerX - rightCircular.radius) / 2, (leftCircular.centerY + rightCircular.centerY) / 2);
                postInvalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isChecked = !isChecked;
                if (onCheckChangeListenter != null) {
                    onCheckChangeListenter.onCheck(isChecked);
                }
                isAnimatorStart = false;
            }
        });


        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), isChecked ? checkedColor : unCheckedColor, isChecked ? unCheckedColor : checkedColor);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                paintColor = (int) animation.getAnimatedValue();
            }
        });

        colorAnimator.setDuration(duration);
        animator.setDuration(duration);

        if (!isAnimatorStart) {
            colorAnimator.start();
            animator.start();
            isAnimatorStart = true;
        }

    }
}
