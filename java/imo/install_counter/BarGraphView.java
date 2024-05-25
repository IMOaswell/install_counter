package imo.install_counter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

public class BarGraphView
{
    static ViewGroup create (final Context mContext) {
        final LinearLayout layout = new LinearLayout(mContext);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout () {
                    int padding = (int) layout.getWidth() / 60;
                    layout.setPadding(padding, padding, padding, padding);
                    int width = layout.getWidth();
                    int height = layout.getHeight();
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setStyle(Paint.Style.FILL);

                    int canvasHeight = canvas.getHeight();
                    int canvasWidth = canvas.getWidth();
                    float[] line1 = {padding, padding, padding, canvasHeight - padding};
                    canvas.drawLine(line1[0], line1[1], line1[2], line1[3], paint);
                    float[] line2 = {padding, canvasHeight - padding, canvasWidth - padding, canvasHeight - padding};
                    canvas.drawLine(line2[0], line2[1], line2[2], line2[3], paint);

                    BitmapDrawable bitmapDrawable = new BitmapDrawable(layout.getResources(), bitmap);
                    layout.setBackground(bitmapDrawable);
                }
            });
        return layout;
    }
}
