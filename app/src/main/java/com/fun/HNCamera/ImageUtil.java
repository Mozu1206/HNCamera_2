package com.fun.HNCamera;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by shuwada on 2016/11/01.
 */

public class ImageUtil {
    public static Bitmap createBitmap(String path, int ori) {
        //ContentResolver contentResolver = context.getContentResolver();
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap imageBitmap = null;

        // メモリ上に画像を読み込まず、画像サイズ情報のみを取得する
        options.inJustDecodeBounds = true;
        imageBitmap = BitmapFactory.decodeFile(path, options);
        if (imageBitmap == null) {
            Log.d("tag","imageBitmapに入ってない");
        }
        // もし読み込む画像が大きかったら縮小して読み込む
        //inputStream = contentResolver.openInputStream(uri);
        int imageHeight = options.outHeight;
        Log.d("tag",""+imageHeight);
        int imageWidth = options.outWidth;
        Log.d("tag",""+imageWidth);
        String imageType = options.outMimeType;
        int inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);
        Log.d("tag","inSampleSize = "+inSampleSize);
        options.inJustDecodeBounds = false;
        if (inSampleSize > 1) {
            options.inSampleSize = inSampleSize;
            imageBitmap = BitmapFactory.decodeFile(path, options);
            if (imageBitmap == null) {
                Log.d("tag","imageBitmap2に入ってない");
            }
        } else {
            imageBitmap = BitmapFactory.decodeFile(path, options);
            if (imageBitmap == null) {
                Log.d("tag","imageBitmap3に入ってない");
            }
        }

        // Matrix インスタンス生成
        Matrix matrix = new Matrix();

        imageHeight = options.outHeight;
        imageWidth = options.outWidth;

        // 画像中心を基点にori度回転
        matrix.setRotate(ori, imageWidth/2, imageHeight/2);

        // 90度回転したBitmap画像を生成
        Bitmap imageBitmap2 = Bitmap.createBitmap(imageBitmap, 0, 0, imageWidth, imageHeight, matrix, true);

        return imageBitmap2;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // 画像の元サイズ
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

    public static int getOrientation(String path) {
        ExifInterface exifInterface;

        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException e) {
            Log.d("tag","Exif取れなかったよ" );
            return 0;
        }

        int orientation = 0;
        int exifR = exifInterface.getAttributeInt  (ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Log.d("tag","orientation1=" + exifR );
        switch (exifR) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                orientation = 90;
                Log.d("tag","orientation2=90");
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                orientation = 180;
                Log.d("tag","orientation2=180" );
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                orientation = 270;
                Log.d("tag","orientation2=270");
                break;
            default:
                orientation = 0;
                Log.d("tag","orientation2=0");
                break;
        }

        if(exifInterface != null) {
            // get latitude and longitude
            float[] latlong = new float[2];
            exifInterface.getLatLong(latlong);

            //String aperture = exifInterface.getAttribute (ExifInterface.TAG_APERTURE); // since API Level 11
            String datetime = exifInterface.getAttribute (ExifInterface.TAG_DATETIME);
            //String exposure = exifInterface.getAttribute (ExifInterface.TAG_EXPOSURE_TIME); // since API Level 11
            int flash = exifInterface.getAttributeInt  (ExifInterface.TAG_FLASH, 0);
            double focalLength = exifInterface.getAttributeDouble  (ExifInterface.TAG_FOCAL_LENGTH, 0);
            double altitude = exifInterface.getAttributeDouble (ExifInterface.TAG_GPS_ALTITUDE, 0); // since API Level 9
            double altitudeRef = exifInterface.getAttributeDouble (ExifInterface.TAG_GPS_ALTITUDE_REF, 0); // since API Level 9
            String datestamp = exifInterface.getAttribute (ExifInterface.TAG_GPS_DATESTAMP);
            String latitude = exifInterface.getAttribute (ExifInterface.TAG_GPS_LATITUDE);
            String latitudeRef = exifInterface.getAttribute (ExifInterface.TAG_GPS_LATITUDE_REF);
            String longitude = exifInterface.getAttribute (ExifInterface.TAG_GPS_LONGITUDE);
            String longitudeRef = exifInterface.getAttribute (ExifInterface.TAG_GPS_LONGITUDE_REF);
            String processing = exifInterface.getAttribute (ExifInterface.TAG_GPS_PROCESSING_METHOD);
            String timestamp = exifInterface.getAttribute (ExifInterface.TAG_GPS_TIMESTAMP);
            int imageLength = exifInterface.getAttributeInt (ExifInterface.TAG_IMAGE_LENGTH, 0);
            int imageWidth = exifInterface.getAttributeInt (ExifInterface.TAG_IMAGE_WIDTH, 0);
            //String iso = exifInterface.getAttribute (ExifInterface.TAG_ISO); // since API Level 11
            String make = exifInterface.getAttribute (ExifInterface.TAG_MAKE);
            String model = exifInterface.getAttribute (ExifInterface.TAG_MODEL);
            //int orientation = exifInterface.getAttributeInt  (ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            int whitebalance = exifInterface.getAttributeInt  (ExifInterface.TAG_WHITE_BALANCE, ExifInterface.WHITEBALANCE_AUTO);

            String orientationInfo = "";
            switch(orientation) {
                case ExifInterface.ORIENTATION_UNDEFINED :
                    orientationInfo = "UNDEFINED";
                    break;
                case ExifInterface.ORIENTATION_NORMAL :
                    orientationInfo = "NORMAL";
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL :
                    orientationInfo = "FLIP_HORIZONTAL";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180 :
                    orientationInfo = "ROTATE_180";
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL :
                    orientationInfo = "FLIP_VERTICAL";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90 :
                    orientationInfo = "ROTATE_90";
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE :
                    orientationInfo = "TRANSVERSE";
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE :
                    orientationInfo = "TRANSPOSE";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270 :
                    orientationInfo = "ROTATE_270";
                    break;
            }

            Log.d(TAG, "latlong : " + latlong[0] + ", " + latlong[1]);
            Log.d(TAG, "datetime : " + datetime);
            Log.d(TAG, "flash : " + flash + "  (" + (flash == 1 ? "on" : "off") + ")");
            Log.d(TAG, "focalLength : " + focalLength + "");
            Log.d(TAG, "datestamp : " + datestamp);
            Log.d(TAG, "altitude : " + altitude);
            Log.d(TAG, "altitudeRef : " + altitudeRef);
            Log.d(TAG, "latitude : " + latitude);
            Log.d(TAG, "latitudeRef : " + latitudeRef);
            Log.d(TAG, "longitude : " + longitude);
            Log.d(TAG, "longitudeRef : " + longitudeRef);
            Log.d(TAG, "processing : " + processing);
            Log.d(TAG, "timestamp : " + timestamp);
            Log.d(TAG, "imageLength : " + imageLength + "");
            Log.d(TAG, "imageWidth : " + imageWidth + "");
            Log.d(TAG, "make : " + make);
            Log.d(TAG, "model : " + model);
            Log.d(TAG, "orientation : " + orientation + "  (" + orientationInfo + ")");
            Log.d(TAG, "whitebalance : " + whitebalance + "  " + (whitebalance == 1 ? "manual" : "auto"));
        }


        return orientation;



    }

}


