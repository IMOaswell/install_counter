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
import android.view.Gravity;
import android.widget.SeekBar;

public class BarGraphView
{
    static ViewGroup create (final Context mContext, final List<Integer> yValues) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                                   LinearLayout.LayoutParams.MATCH_PARENT, 
                                   LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        
        final LinearLayout graph = new LinearLayout(mContext);
        graph.setLayoutParams(new LinearLayout.LayoutParams(
                                  LinearLayout.LayoutParams.MATCH_PARENT, 
                                  200));
        graph.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout () {
                    graph.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    graph.setBackground(drawCanvas(graph, graph.getWidth(), graph.getHeight(), yValues));
                    }
            });
        layout.addView(graph);
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
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        
        int lineSpacing = canvas.getWidth() / yValues.size();
        int currentX = 10;
        int maxY = Collections.max(yValues);
        
        for(int yValue: yValues){
            if(yValue > 0){
                float lineHeight = (yValue / (float) maxY) * canvasHeight;
                int startX = currentX;
                int startY = canvasHeight;
                int stopX = currentX;
                float stopY = canvasHeight - lineHeight;
                float[] line = {startX, startY, stopX, stopY};
                canvas.drawLine(line[0], line[1], line[2], line[3], paint);
            }
            canvas.drawCircle(currentX, canvasHeight - 1, 1, paint);
            currentX += lineSpacing;
        }
    }
}
