package com.fun.HNCamera;

/**
 * Created by masa.snow on 2017/07/26.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {

    private static final int GETTRIM = 3;

    private ImageButton Camera;
    private ImageButton Folda;
    static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int BELOW_JELLYBEAN = -1;
    private static final int ABOVE_KITKAT = 1;
    private static final int CAMERA_CAPTURE = 2;
    private static final int RESULT_PICK_IMAGEFILE = 1001;
    ImageView imageView1;
    private Uri m_uri;
    private Uri fileUri;
    private static final int REQUEST_CHOOSER = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ImageButton Camera = (ImageButton)findViewById(R.id.imageButton);
        ImageButton Folda = (ImageButton)findViewById(R.id.imageButton2);
        imageView1 = (ImageView)findViewById(R.id.imageView1);

        Camera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v ){
                        playCamera();
                    }
                });

        Folda.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v ){
                        showGallery();
                    }
                });

     /*   Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                        startActivity(intent);
                        // startActivityを終了させます。
                        Main2Activity.this.finish();
                    }
                });*/


    }

    private void playCamera() {
        //カメラの起動Intentの用意
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        final Date date = new Date(System.currentTimeMillis());
        final SimpleDateFormat dataFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        final String filename = dataFormat.format(date) + ".jpg";
        Uri mSaveUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera", filename));
        //Uri mSaveUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/tmp.jpg"));
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, mSaveUri);
        startActivityForResult(intent, CAMERA_CAPTURE);

    }
    private void showGallery() {
        // ギャラリー用のIntent作成
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,BELOW_JELLYBEAN);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent,ABOVE_KITKAT);
        }
    }


    public void playTrim(){
        //トリミング用のIntent作成
        Intent _intent = new Intent(getApplicationContext(), TrimingActivity.class);
        startActivity(_intent);
    }



    private String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = null;
        if (resultCode != RESULT_OK) {
            // キャル時
            return;
        }
        switch (requestCode) {
            case BELOW_JELLYBEAN:
                String[] colums = {MediaStore.MediaColumns.DATA};
                Cursor cur = getContentResolver().query(data.getData(), colums, null, null, null);
                cur.moveToNext();
                filePath = cur.getString(0);
                Log.d("tag", "JELLYBEANのfilePath =" + filePath);
                cur.close();
                break;
            case ABOVE_KITKAT:
                filePath = getFilePath4Kitkat(data);
                Log.d("tag", "KITKATのfilePath=" + filePath);
                break;
            case CAMERA_CAPTURE:
                /*
                ContentResolver contentResolver = getContentResolver();
                String[] columns = {MediaStore.Images.Media.DATA};
                Cursor cursor = contentResolver.query(data.getData(), columns, null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);

                Log.d("tag", "CAMERA_CAPTUREのfilePath="+filePath);
                break;
*/
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                BitmapHolder._holdedBitmap = bitmap;
                playTrim();
                break;

            case GETTRIM:
                if (resultCode == RESULT_OK) {
//                    ((ImageView) findViewById(R.id.imageView1)).setImageBitmap(BitmapHolder._holdedBitmap);
                    Intent _intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(_intent);

                    Toast.makeText(this, "画像の取得に成功しました。\nトリミングおめでとう", Toast.LENGTH_SHORT).show();

//                BitmapHolder._holdedBitmap = null;
                } else {
                    Toast.makeText(this, "画像の取得に失敗しました。", Toast.LENGTH_SHORT).show();
                }
//                finish();
                break;
        }

        /*if (requestCode == 0) {

            Uri resultUri = (data != null ? data.getData() : m_uri);

            if (resultUri == null) {
                // 取得失敗
                return;
            }

            MediaScannerConnection.scanFile(
                    this,
                    new String[]{resultUri.getPath()},
                    new String[]{"image/jpeg"},
                    null
            );
            // 画像を設定

            String[] colums = {MediaStore.MediaColumns.DATA};
            Cursor cur = getContentResolver().query(data.getData(), colums, null, null, null);
            cur.moveToNext();
            filePath = cur.getString(0);
            Log.d("tag", filePath);
            String[] colums = {MediaStore.MediaColumns.DATA};
            Cursor cur = getContentResolver().query(data.getData(), colums, null, null, null);
            cur.moveToNext();
            filePath = cur.getString(0);
            Log.d("tag", filePath);
            imageView.setImageURI(resultUri);
        }*/

        if (requestCode == ABOVE_KITKAT || requestCode == BELOW_JELLYBEAN) {
            Log.d("tag", filePath);

            int ori = ImageUtil.getOrientation(filePath);
            Bitmap bmImg = ImageUtil.createBitmap(filePath, ori);
            if (bmImg == null) {
                Log.d("tag", "bmImgに入ってない");
            }
            BitmapHolder._holdedBitmap = bmImg;
            playTrim();
        }

        /*
r

        if (requestCode == GETTRIM) {
            if (resultCode == RESULT_OK) {
                ((ImageView) findViewById(R.id.imageView1)).setImageBitmap(BitmapHolder._holdedBitmap);
                Toast.makeText(this, "画像の取得に成功しました。\nトリミングおめでとう", Toast.LENGTH_SHORT).show();

//                BitmapHolder._holdedBitmap = null;
            } else {
                Toast.makeText(this, "画像の取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

*/


    }



    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void createFolderSaveImage(Bitmap imageToSave, String fileName) {
        String folderPath = Environment.getExternalStorageDirectory() + "/NewFolder/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, fileName);
        if (file.exists()) {
            file.delete();
        }
    }


    private void registAndroidDB(String path) {
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = this.getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put("_date", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getFilePath4Kitkat(Intent data) {
        String filePath = null;
        String[] strSplittendDocId = DocumentsContract.getDocumentId(data.getData()).split(":");
        String strId = strSplittendDocId[strSplittendDocId.length - 1];

        Cursor cur = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.MediaColumns.DATA}
                ,"_id=?"
                , new String[]{strId}
                , null
        );
        if (cur.moveToFirst()){
            filePath = cur.getString(0);
            Log.d("tag", filePath);
        }
        cur.close();
        return filePath;
    }


}






