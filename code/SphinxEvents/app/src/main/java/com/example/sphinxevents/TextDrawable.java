package com.example.sphinxevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class TextDrawable {

    public static Drawable createTextDrawable(Context context, String text, int textColor, int size) {
        // Create a bitmap with the required dimensions
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Generate a random background color
        int backgroundColor = generateRandomColor();

        // Draw background
        canvas.drawColor(backgroundColor);

        // Prepare paint for text
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(size / 2f); // Set text size to half of drawable size
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER); // Center alignment for text

        // Calculate position for text
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Center the text vertically and horizontally
        float x = size / 2f; // Horizontal center
        float y = (size / 2f) + (bounds.height() / 2f); // Vertical center adjusted

        // Draw text onto canvas
        canvas.drawText(text, x, y, paint);

        // Return drawable
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    // Converts the Drawable to a Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static int generateRandomColor() {
        Random random = new Random();

        // Restrict RGB values to avoid too light colors
        int red = random.nextInt(128);   // Values from 0 to 127 (darker shades)
        int green = random.nextInt(128); // Values from 0 to 127 (darker shades)
        int blue = random.nextInt(128);  // Values from 0 to 127 (darker shades)

        return (0xFF << 24) | (red << 16) | (green << 8) | blue; // Alpha set to 255 (opaque)
    }

}
