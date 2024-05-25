package imo.install_counter;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

public class BarGraphView extends View
 {
    
    BarGraphView(Context mContext){
        super(mContext);
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
        int canvasHeight = canvas.getHeight();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        
        float startX, startY;
        float stopX, stopY;
        
        float paddingHorizontal = canvas.getWidth() / 60;
        float paddingVertical = canvas.getHeight() / 60;
        
        startX = paddingHorizontal; 
        startY = paddingVertical;
        stopX = paddingHorizontal;
        stopY = canvasHeight - paddingVertical;
        
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}
