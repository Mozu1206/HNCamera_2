package com.fun.HNCamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.os.Handler;

import com.fun.HNCamera.MainActivity;
import com.fun.HNCamera.R;

public class startActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Handler hdl = new Handler();
        // 2000ms遅延
        hdl.postDelayed(new splashHandler(), 2000);
    }

    class splashHandler implements Runnable {
        public void run() {
            // スプラッシュ完了後に実行するActivityを指定します。
            Intent intent = new Intent(startActivity.this, Main2Activity.class);
            startActivity(intent);
            // startActivityを終了させます。
            startActivity.this.finish();
        }
    }

}
