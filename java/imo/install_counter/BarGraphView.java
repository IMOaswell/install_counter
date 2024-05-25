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
        int canvasWidth = canvas.getWidth();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        
        float padding = canvas.getWidth() / 60;
        
        float[] line1 = {padding, padding, padding, canvasHeight - padding};
        canvas.drawLine(line1[0], line1[1], line1[2], line1[3], paint);
        
        float startX, startY, stopX, stopY;
        startX = padding;
        startY = canvasHeight - padding;
        stopX = canvasWidth - padding;
        stopY = canvasHeight - padding;
        float[] line2 = {startX, startY, stopX, stopY};
        canvas.drawLine(line2[0], line2[1], line2[2], line2[3], paint);
    }
}
