package fr.bamlab.rnimageresizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by almouro on 19/11/15.
 */
class ImageResizerModule extends ReactContextBaseJavaModule {
    private Context context;

    public ImageResizerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    @Override
    public String getName() {
        return "ImageResizerAndroid";
    }

    @ReactMethod
    public void createResizedImage(String imagePath, int newWidth, int newHeight, String compressFormat,
                            int quality, int rotation, String outputPath, final Callback successCb, final Callback failureCb) {
        try {
            createResizedImageWithExceptions(imagePath, newWidth, newHeight, compressFormat, quality,
                    rotation, outputPath, successCb, failureCb);
        } catch (Exception e) {
            failureCb.invoke(e.getMessage());
        }
    }

    private void createResizedImageWithExceptions(String imagePath, int newWidth, int newHeight,
                                           String compressFormatString, int quality, int rotation, String outputPath,
                                           final Callback successCb, final Callback failureCb) throws IOException {
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.valueOf(compressFormatString);
        if (imagePath!=null && imagePath.indexOf("data:image/") < 0) {
            imagePath = imagePath.replace("file:", "");
        }

        String resizedImagePath = ImageResizer.createResizedImage(this.context, imagePath, newWidth,
                newHeight, compressFormat, quality, rotation, outputPath);
        String base64 = "";
        try {
            Bitmap bm = BitmapFactory.decodeFile(resizedImagePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            base64 = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WritableMap imageData = Arguments.createMap();
        imageData.putString("path", resizedImagePath);
        imageData.putString("base64", base64);
        successCb.invoke(imageData);
    }
}
