package com.example.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CustomView extends View {    //笔画列表
    List<Path> listStrokes = new ArrayList<Path>();
    Path pathStroke;
    Bitmap memBMP;
    Paint memPaint;
    Canvas memCanvas;
    boolean mBooleanOnTouch = false;   //上一个点
    float oldx;
    float oldy;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();        //每一次落下-抬起之间经过的点为一个笔画
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://落下
                pathStroke = new Path();
                pathStroke.moveTo(x, y);
                oldx = x;
                oldy = y;
                mBooleanOnTouch = true;
                listStrokes.add(pathStroke);
                break;
            case MotionEvent.ACTION_MOVE://移动
                // Add a quadratic bezier from the last point, approaching control point (x1,y1), and ending at (x2,y2).
                // 在Path结尾添加二次Bezier曲线
                if (mBooleanOnTouch) {
                    pathStroke.quadTo(oldx, oldy, x, y);
                    oldx = x;
                    oldy = y;
                    drawStrokes();
                }
                break;
            case MotionEvent.ACTION_UP://抬起
                if (mBooleanOnTouch) {
                    pathStroke.quadTo(oldx, oldy, x, y);
                    drawStrokes();
                    mBooleanOnTouch = false;//一个笔画已经画完
                }
                break;
        }       //本View已经处理完了Touch事件，不需要向上传递
        return true;
    }

    void drawStrokes() {
        if (memCanvas == null) {           //缓冲位图
            memBMP = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            memCanvas = new Canvas(); //缓冲画布
            memCanvas.setBitmap(memBMP); //为画布设置位图，图形实际保存在位图中
            memPaint = new Paint(); //画笔
            memPaint.setAntiAlias(true); //抗锯齿
            memPaint.setColor(Color.RED); //画笔颜色
            memPaint.setStyle(Paint.Style.STROKE); //设置填充类型
            memPaint.setStrokeWidth(5); //设置画笔宽度
        }
        for (Path path : listStrokes) {
            memCanvas.drawPath(path, memPaint);
        }
        invalidate(); //刷新屏幕
    }

    @Override

    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        if (memBMP != null)
            canvas.drawBitmap(memBMP, 0, 0, paint);
    }
}

