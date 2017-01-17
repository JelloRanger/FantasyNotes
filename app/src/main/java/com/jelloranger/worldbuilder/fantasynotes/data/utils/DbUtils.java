package com.jelloranger.worldbuilder.fantasynotes.data.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class DbUtils {

    private static final String TAG = DbUtils.class.getSimpleName();

    public static byte[] getBytes(final Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Log.d(TAG, String.format("Size of bitmap before compression: %d", bitmap.getByteCount()));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        Log.d(TAG, String.format("Size of bitmap after compression: %d", stream.toByteArray().length));
        return stream.toByteArray();
    }

    public static Bitmap getImage(final byte[] image) {
        if (image == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
