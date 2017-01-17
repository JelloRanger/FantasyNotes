package com.jelloranger.worldbuilder.fantasynotes.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class BitmapDataObject implements Serializable {

    private Bitmap currentImage;

    public BitmapDataObject(final Bitmap bitmap) {
        currentImage = bitmap;
    }

    public Bitmap getBitmap() {
        return currentImage;
    }

    private void writeObject(final java.io.ObjectOutputStream out) throws IOException {

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        currentImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

        final byte[] byteArray = stream.toByteArray();

        out.writeInt(byteArray.length);
        out.write(byteArray);

        currentImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

    }

    private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

        final int bufferLength = in.readInt();

        final byte[] byteArray = new byte[bufferLength];

        int pos = 0;
        do {
            final int read = in.read(byteArray, pos, bufferLength - pos);

            if (read != -1) {
                pos += read;
            } else {
                break;
            }

        } while (pos < bufferLength);

        currentImage = BitmapFactory.decodeByteArray(byteArray, 0, bufferLength);

    }
}
