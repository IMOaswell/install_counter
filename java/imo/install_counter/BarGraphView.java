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
import android.widget.TextView;

public class BarGraphView
{
    Context mContext;
    List<Integer> yValues;
    List<String> xValues;
    int maxY = -1;
    OnProgressChange onProgressChange;
    
    BarGraphView(final Context mContext, final List<Integer> yValues){
        this.mContext = mContext;
        this.yValues = yValues;
    }
    BarGraphView stringsForEachY(List<String> xValues){
        this.xValues = xValues;
        return this;
    }
    BarGraphView setMaxY(int maxY){
        this.maxY = maxY;
        return this;
    }
    BarGraphView setOnProgressChange(OnProgressChange onProgressChange){
        this.onProgressChange = onProgressChange;
        return this;
    }
    
    View create(){
        return create(mContext, yValues, xValues, onProgressChange, maxY);
    }
    
    int getMaxY(){
        return Collections.max(yValues);
    }
    
    private static ViewGroup create (final Context mContext, final List<Integer> yValues, final List<String> xValues, final OnProgressChange onProgressChange, final int maxY) {
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
                    graph.setBackground(drawCanvas(graph, graph.getWidth(), graph.getHeight(), yValues, maxY));
                    }
            });
            
        final TextView textview = new TextView(mContext);
        textview.setGravity(Gravity.CENTER);
        
        final SeekBar seekbar = new SeekBar(mContext);
        seekbar.setMax(yValues.size() - 1);
        seekbar.setProgress(yValues.size() - 1);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                @Override public void onStartTrackingTouch(SeekBar v){}
                @Override public void onStopTrackingTouch(SeekBar v){}
                @Override
                public void onProgressChanged(SeekBar v, int progress, boolean fromUser){
                    graph.setBackground(drawCanvas(graph, graph.getWidth(), graph.getHeight(), yValues, progress, maxY));
                    StringBuilder sb = new StringBuilder();
                    if(xValues != null) sb.append(xValues.get(progress) + "");
                    sb.append(yValues.get(progress));
                    textview.setText(sb.toString());
                    if(onProgressChange != null) onProgressChange.run(progress);
                }
            });
            
        layout.addView(graph);
        layout.addView(textview);
        layout.addView(seekbar);
        return layout;
    }

    private static BitmapDrawable drawCanvas (View view, int width, int height, List<Integer> yValues, int mazY) {
        return drawCanvas(view, width, height, yValues, yValues.size() - 1, -1);
    }
    
    private static BitmapDrawable drawCanvas (View view, int width, int height, List<Integer> yValues, int headPosition, int maxY) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        displayLines(canvas, yValues, headPosition, maxY);
        return new BitmapDrawable(view.getResources(), bitmap);
    }
    
    private static void displayLines(Canvas canvas, List<Integer> yValues, int headPosition, int maxY){
        int canvasHeight = canvas.getHeight();
        Paint black_paint = new Paint();
        black_paint.setColor(Color.BLACK);
        black_paint.setStyle(Paint.Style.FILL);
        
        Paint red_paint = new Paint();
        red_paint.setColor(Color.RED);
        red_paint.setStyle(Paint.Style.FILL);
        
        int lineSpacing = canvas.getWidth() / yValues.size();
        int currentX = 10;
        if(maxY == -1) maxY = Collections.max(yValues);
        
        int i = 0;
        for(int yValue: yValues){
            i++;
            Paint paint = black_paint;
            if(i == headPosition + 1) paint = red_paint;
            
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
    
    public static class OnProgressChange {
        public void run(int progress){}
    }
}
