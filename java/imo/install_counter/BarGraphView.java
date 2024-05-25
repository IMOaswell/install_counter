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
        
        float padding = canvas.getWidth() / 60;
        
        float[] line1 = {padding, padding, padding, canvasHeight - padding};
        canvas.drawLine(line1[0], line1[1], line1[2], line1[3], paint);
    }
}
