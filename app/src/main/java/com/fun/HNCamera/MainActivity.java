package com.fun.HNCamera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.fun.HNCamera.R;

import static android.R.attr.data;
import static android.R.attr.tag;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final int GETTRIM = 3;

    private Uri m_uri;
    private Uri fileUri;
    private static final int REQUEST_CHOOSER = 1000;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int EMO = 999;
    private final int PHY = 998;
    private final int CUL = 997;
    private final Handler handler = new Handler();
    // private int currentColor;
    private static final int BELOW_JELLYBEAN = -1;
    private static final int ABOVE_KITKAT = 1;
    private static final int CAMERA_CAPTURE = 2;
    private int currentColor;
    private ImageButton Physical;
    private ImageButton Culture;
    private ImageButton Emotional;
    private ImageButton save;
    private int physicalStatus = 0;
    private int cultureStatus = 0;
    private int emotionalStatus = 0;
    private String spinnerItems[] = {"Country", "Japan", "South Korea", "Singapore", "China","Taiwan"};
    private String spinnerItems2[] = {"Gender", "Male", "Female"};
    private String spinnerItems3[] = {"Age", "10's", "20's", "30's", "40's", "50's","60's","70's"};

    ImageView imageView1;
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setbuttonListener();
        ((ImageView)findViewById(R.id.imageView1)).setImageBitmap(BitmapHolder._holdedBitmap);
//        imageView1.setImageBitmap(BitmapHolder._holdedBitmap);


        //ImageButton stampbutton = (ImageButton)findViewById(R.id.imageButton);
        //stampbutton.setImageResource(R.drawable.stampstamp2);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelected(false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setColor(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner2 = (Spinner) parent;
                String item = (String) spinner2.getSelectedItem();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter2
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerItems2);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner3.setAdapter(adapter2);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner3 = (Spinner) parent;
                String item = (String) spinner3.getSelectedItem();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinner4 = (Spinner) findViewById(R.id.spinner4);
        ArrayAdapter<String> adapter3
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerItems3);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner4.setAdapter(adapter3);
        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner4 = (Spinner) parent;
                String item = (String) spinner4.getSelectedItem();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setHSV(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ImageButton save = (ImageButton) findViewById(R.id.saveButton);
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        run();


                    }
                });
    }

    private void setHSV(float selected) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.liner1);
        float[] hsv = new float[3];
        Color.colorToHSV(currentColor, hsv);
        hsv[2] = (float) 0.5 + selected / 200;
        layout.setBackgroundColor(Color.HSVToColor(hsv));
    }

    private void setColor(int selected) {
        //背景(layoutView1)の状態を取得(インタンスを生成)
        LinearLayout layout = (LinearLayout) findViewById(R.id.liner1);
        //ボタンの状態(id)を取得
        switch (selected) {
            //0(1つ目)
            case 0:
                Toast.makeText(getApplicationContext(), "Nothing", Toast.LENGTH_SHORT).show();
                //背景色を白に変更
                currentColor = Color.rgb(255, 255, 255);
                break;
            //(12つ目)
            case 1:
                Toast.makeText(getApplicationContext(), "Positive", Toast.LENGTH_SHORT).show();
                //背景色を青に変更

                currentColor = Color.rgb(255, 200, 0);
                break;
            //2(3つ目)
            case 2:
                Toast.makeText(getApplicationContext(), "Negative", Toast.LENGTH_SHORT).show();
                //背景色を赤に変更
                currentColor = Color.rgb(190, 10, 120);
                break;
        }
        layout.setBackgroundColor(currentColor);
    }

    private void setbuttonListener() {
        // Button button1 = (Button) findViewById(R.id.buttonPanel);
        //Button button2 = (Button) findViewById(R.id.camera_button);
        //Button save = (Button) findViewById(R.id.Savebutton);
        Culture = (ImageButton) findViewById(R.id.Culture);
        Culture.setOnClickListener(this);
        Physical = (ImageButton) findViewById(R.id.Physical);
        Physical.setOnClickListener(this);
        Emotional = (ImageButton) findViewById(R.id.Emotional);
        Emotional.setOnClickListener(this);
        //button1.setOnClickListener(button1_onClick);
        //button2.setOnClickListener(button2_onClick);
        //save.setOnClickListener(save_click);
    }


    /*private View.OnClickListener Culture_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(i == 1){
                i = 2;
                ImageButton.setImageResource(R.drawable.culture未選択);
            }else if(i == 2){
                i = 0;
                ImageButton.setImageResource(R.drawable.eatenapple);
            }else{
                ImageButton.setImageResource(R.drawable.eatenapple);
            }
        }
    };*/

   /* private View.OnClickListener button1_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showGallery();
        }
    };*/

    /*private View.OnClickListener button2_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playCamera();
        }
    };*/

    private View.OnClickListener stampbutton_onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
            int requestcode = 666;
            startActivityForResult(intent, requestcode);
        }
    };

    /*private View.OnClickListener save_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            run();
        }
    };*/

    public void playCamera() {
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

    public void showGallery() {
        // ギャラリー用のIntent作成
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, BELOW_JELLYBEAN);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, ABOVE_KITKAT);
        }
    }

    /*
    public void playTrim(){
        //トリミング用のIntent作成
        Intent _intent = new Intent(getApplicationContext(), TrimingActivity.class);
        startActivityForResult(_intent, GETTRIM);
    }
*/
/*
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
                */
                /*
                ContentResolver contentResolver = getContentResolver();
                String[] columns = {MediaStore.Images.Media.DATA};
                Cursor cursor = contentResolver.query(data.getData(), columns, null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);

                Log.d("tag", "CAMERA_CAPTUREのfilePath="+filePath);
                break;
*/
  /*
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
                imageView1.setImageBitmap(bitmap);
                BitmapHolder._holdedBitmap = bitmap;
//                playTrim();
                break;

            case GETTRIM:
                if (resultCode == RESULT_OK) {
                    ((ImageView) findViewById(R.id.imageView1)).setImageBitmap(BitmapHolder._holdedBitmap);
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
/*
        if (requestCode == ABOVE_KITKAT || requestCode == BELOW_JELLYBEAN) {
            Log.d("tag", filePath);
            ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
            int ori = ImageUtil.getOrientation(filePath);
            Bitmap bmImg = ImageUtil.createBitmap(filePath, ori);
            if (bmImg == null) {
                Log.d("tag", "bmImgに入ってない");
            }
            BitmapHolder._holdedBitmap = bmImg;
            imageView1.setImageBitmap(bmImg);
            //          playTrim();
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


//    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getFilePath4Kitkat(Intent data) {
        String filePath = null;
        String[] strSplittendDocId = DocumentsContract.getDocumentId(data.getData()).split(":");
        String strId = strSplittendDocId[strSplittendDocId.length - 1];

        Cursor cur = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.MediaColumns.DATA}
                , "_id=?"
                , new String[]{strId}
                , null
        );
        if (cur.moveToFirst()) {
            filePath = cur.getString(0);
            Log.d("tag", filePath);
        }
        cur.close();
        return filePath;
    }

    public void run() {
        handler.post(new Runnable() {
            public void run() {
                final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                final Date date = new Date(System.currentTimeMillis());
                String filename = Environment.getExternalStorageDirectory() + "/HN Camera/" + df.format(date) + ".png";

                final File file = new File(filename);
                final File dir = new File(Environment.getExternalStorageDirectory() + "/HN Camera/");

                if (!dir.exists()) {
                    boolean result = dir.mkdirs();
                    System.out.println(result);
                    dir.mkdirs();
                }
                file.getParentFile().mkdir();
                saveCapture(findViewById(android.R.id.content), file);
                Toast.makeText(MainActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                String[] filePath = {filename};
                String[] mimeType = {"image/*"};
                MediaScannerConnection.scanFile(getApplicationContext(), filePath, mimeType, null);
            }
        });
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        startActivity(intent);
        // startActivityを終了させます。
        MainActivity.this.finish();
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

    public void saveCapture(View view, File file) {
        Bitmap capture = getViewCapture(view);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            capture.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            final Date date = new Date(System.currentTimeMillis());
            registAndroidDB("/strage/emulated/O/Pictures/" + df.format(date) + ".png");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos == null) return;
            try {
                fos.close();
            } catch (Exception ie) {
                fos = null;
            }
        }
    }

    private void registAndroidDB(String path) {
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = this.getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put("_date", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    public Bitmap getViewCapture(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        if (cache == null) return null;
        Bitmap screen_shot = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);
//        ImageView imageview = (ImageView) findViewById(R.id.imageView1);
//        imageview.setImageBitmap(screen_shot);
        return screen_shot;
    }


    public void onRadioButtonClicked(View view) {
        // ラジオボタンの選択状態を取得
        RadioButton radioButton = (RadioButton) view;
        // getId()でラジオボタンを識別し、ラジオボタンごとの処理を行う
        //押してあるか否か
        boolean checked = radioButton.isChecked();

    }

    public void onClick(View v) {
        if (v == Physical) {
            if (physicalStatus % 3 == 0) {
                Physical.setImageResource(R.drawable.physical);
                physicalStatus++;
            } else if (physicalStatus % 3 == 1) {
                Physical.setImageResource(R.drawable.physicalbad);
                physicalStatus++;
            } else if (physicalStatus % 3 == 2) {
                Physical.setImageResource(R.drawable.physicalglay);
                physicalStatus++;
            }

        } else if (v == Culture) {
            if (cultureStatus % 3 == 0) {
                Culture.setImageResource(R.drawable.culture);
                cultureStatus++;
            } else if (cultureStatus % 3 == 1) {
                Culture.setImageResource(R.drawable.culturebad);
                cultureStatus++;
            } else if (cultureStatus % 3 == 2) {
                Culture.setImageResource(R.drawable.cultureglay);
                cultureStatus++;
            }

        } else if (v == Emotional) {
            if (emotionalStatus % 3 == 0) {
                Emotional.setImageResource(R.drawable.emotional);
                emotionalStatus++;
            } else if (emotionalStatus % 3 == 1) {
                Emotional.setImageResource(R.drawable.emotionalbad);
                emotionalStatus++;
            } else if (emotionalStatus % 3 == 2) {
                Emotional.setImageResource(R.drawable.emotionalglay);
                emotionalStatus++;
            }
        }
    }
}

