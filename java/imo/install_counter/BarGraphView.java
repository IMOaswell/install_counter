package imo.install_counter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BarGraphView
{
    static final int BAR_SPACING = 25;
    static ViewGroup create (final Context mContext) {
        final LinearLayout layout = new LinearLayout(mContext);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                                   LinearLayout.LayoutParams.MATCH_PARENT, 
                                   LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout () {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int padding = (int) layout.getWidth() / 60;
                    layout.setPadding(padding, padding, padding, padding + BAR_SPACING);
                    layout.setBackground(drawCanvas(layout, layout.getWidth(), layout.getHeight(), padding));
                }
            });

        layout.addView(createBar(mContext, "hello"));
        layout.addView(createBar(mContext, "world"));
        return layout;
    }

    static BitmapDrawable drawCanvas (View view, int width, int height, int padding) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
        
        float[] line2 = {padding, canvasHeight - padding, canvasWidth - padding, canvasHeight - padding};
        canvas.drawLine(line2[0], line2[1], line2[2], line2[3], paint);
        return new BitmapDrawable(view.getResources(), bitmap);
    }

    static TextView createBar (Context mContext, String text) {
        final TextView textview = new TextView(mContext);
        textview.setText(text);
        textview.setBackgroundColor(Color.BLACK);
        textview.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, BAR_SPACING, 0, 0);
        textview.setLayoutParams(layoutParams);
        textview.setLines(2);
        textview.setGravity(Gravity.CENTER_VERTICAL);
        textview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout () {
                    textview.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int padding = (int) textview.getHeight() / 6;
                    textview.setPadding(padding, padding, padding, padding);
                }
            });
        return textview;
    }
}
