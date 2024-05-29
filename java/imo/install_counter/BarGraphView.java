package imo.install_counter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import java.util.Collections;
import java.util.List;

public class BarGraphView
{
    static ViewGroup create (final Context mContext, final List<Integer> yValues) {
        final LinearLayout layout = new LinearLayout(mContext);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                                   LinearLayout.LayoutParams.MATCH_PARENT, 
                                   200));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout () {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    layout.setBackground(drawCanvas(layout, layout.getWidth(), layout.getHeight(), yValues));
                }
            });
        return layout;
    }

    static BitmapDrawable drawCanvas (View view, int width, int height, List<Integer> yValues) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        displayLines(canvas, yValues);
        return new BitmapDrawable(view.getResources(), bitmap);
    }
    
    static void displayLines(Canvas canvas, List<Integer> yValues){
        int canvasHeight = canvas.getHeight();
        Paint paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.FILL);
        
        int lineSpacing = canvas.getWidth() / yValues.size();
        int currentX = 10;
        int maxY = Collections.max(yValues);
        
        for(int yValue: yValues){
            float lineHeight = (yValue / (float) maxY) * canvasHeight;
            int startX = currentX;
            int startY = canvasHeight;
            int stopX = currentX;
            float stopY = canvasHeight - lineHeight;
            float[] line = {startX, startY, stopX, stopY};
            canvas.drawLine(line[0], line[1], line[2], line[3], paint);
            currentX += lineSpacing;
        }
    }
}
