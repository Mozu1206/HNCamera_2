package com.fun.HNCamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by masa.snow on 2017/07/24.
 */

public class TrimingActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triming);
        System.gc();
        _bmOriginal = BitmapHolder._holdedBitmap.copy(Bitmap.Config.ARGB_8888, true);
//         _bmOriginal = BitmapFactory.decodeResource(getResources(),R.drawable.temple);
        BitmapHolder._holdedBitmap = null;
    }

    Bitmap _bmOriginal;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        final TrimView _tview = new TrimView(getApplicationContext());
        ((LinearLayout)findViewById(R.id.imgcontainer)).addView(_tview);
        int _width = ((FrameLayout)findViewById(R.id.fl1)).getWidth();
        int _height = ((FrameLayout)findViewById(R.id.fl1)).getHeight();

       // _bmOriginal = BitmapFactory.decodeResource(getResources(),R.drawable.temple);


        float _scaleW = (float) _width / (float) _bmOriginal.getWidth();
        float _scaleH = (float) _height / (float) _bmOriginal.getHeight();
        final float _scale = Math.min(_scaleW, _scaleH);
        Matrix matrix = new Matrix();
        matrix.postScale(_scale, _scale);

        final Bitmap _bm = Bitmap.createBitmap(_bmOriginal, 0, 0, _bmOriginal.getWidth(),_bmOriginal.getHeight(), matrix, true);


        ((ImageView)findViewById(R.id.imageView1)).setImageBitmap(_bm);

        ((Button)findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<Integer> _al = _tview.getTrimData();

                int _ix = (int)(_al.get(0)/_scale);
                int _iy = (int)(_al.get(1)/_scale);
                int _iwidth = (int)(_al.get(2)/_scale);
                int _iheight = (int)(_al.get(3)/_scale);

                _ix = (_ix>0) ? _ix : 0;
                _iy = (_iy>0) ? _iy : 0;
                _iwidth = (_iwidth + _ix < _bmOriginal.getWidth()) ? _iwidth : _bmOriginal.getWidth() - _ix;
                _iheight = (_iheight + _iy < _bmOriginal.getHeight()) ? _iheight : _bmOriginal.getHeight() - _iy;
                BitmapHolder._holdedBitmap = Bitmap.createBitmap(_bmOriginal, _ix, _iy, _iwidth, _iheight, null, true);
                setResult(RESULT_OK);
//                ((ImageView) findViewById(R.id.imageView1)).setImageBitmap(BitmapHolder._holdedBitmap);
                Intent _intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(_intent);
//                finish();
//                ((ImageView)findViewById(R.id.imageView1)).setImageBitmap(_bm);
            }
        });

        super.onWindowFocusChanged(hasFocus);
        _tview.sizeSet((int)(_bmOriginal.getWidth()*_scale),(int)(_bmOriginal.getHeight()*_scale));
    }



    public class TrimView extends View {
        public float _x = 0, _y = 0;
        Paint paint1;
        Paint paint2;
        Paint paint3;

        public TrimView(Context context) {
            super(context);
            paint1 = new Paint();
            paint1.setColor(0xcc000000);
            paint1.setAntiAlias(true);

            paint2 = new Paint();
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setColor(Color.LTGRAY);

            paint3 = new Paint();
            paint3.setAntiAlias(true);
            paint3.setColor(Color.LTGRAY);
        }

        int _w = 0;
        int _h = 0;
        int sqWidth = 0;
        int sqHeight = 0;
        int sqX = 0;
        int sqY = 0;


        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        /*
        _w = w;
        _h = h;
        sqWidth = w - 40;
        sqHeight = w - 40;
        sqX = w / 2;
        sqY = h / 2;
        */
        }

        public void sizeSet(int w, int h) {
            _w = w;
            _h = h;
            sqWidth = w - 40;
            sqHeight = w - 40;
            sqX = w / 2;
            sqY = h / 2;
        }

        public ArrayList<Integer> getTrimData(){
            ArrayList<Integer> _arl = new ArrayList<Integer>();
            _arl.add(sqX-sqWidth/2);
            _arl.add(sqY-sqHeight/2);
            _arl.add(sqWidth);
            _arl.add(sqHeight);

            return _arl;
        }

        protected void onDraw(Canvas canvas) {

            canvas.drawRect(0, 0, _w, sqY - sqHeight / 2, paint1);
            canvas.drawRect(0, sqY - sqHeight / 2, sqX - sqWidth / 2, sqY + sqHeight / 2, paint1);
            canvas.drawRect(sqX + sqWidth
                    / 2, sqY - sqHeight / 2, _w, sqY + sqHeight / 2, paint1);
            canvas.drawRect(0, sqY + sqHeight / 2, _w, _h, paint1);

            canvas.drawRect(sqX - sqWidth / 2, sqY - sqHeight / 2, sqX + sqWidth
                    / 2, sqY + sqHeight / 2, paint2);
            canvas.drawCircle(sqX, sqY - sqHeight / 2, 15, paint3);
            canvas.drawCircle(sqX, sqY + sqHeight / 2, 15, paint3);
            canvas.drawCircle(sqX - sqWidth / 2, sqY, 15, paint3);
            canvas.drawCircle(sqX + sqWidth / 2, sqY, 15, paint3);
        }

        String TouchMode = "NONE";
        float _distance = 0f;

        public boolean onTouchEvent(MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    _x = e.getX();
                    _y = e.getY();
                    if (sqX - sqWidth / 2+20 < _x && sqX + sqWidth / 2-20 > _x) {
                        // ｘ的にいうと触れている
                        if (sqY - sqHeight / 2+20 < _y && sqY + sqHeight / 2-20 > _y) {
                            // y的にいうと触れている
                            TouchMode = "MOVE";
                        }else if(sqY - sqHeight / 2-20 < _y && sqY + sqHeight / 2+20 > _y){
                            _distance = (float)culcDistance(sqX,sqY,(int)e.getX(),(int)e.getY());
                            TouchMode = "SCALE";
                        }
                    }else if(sqX - sqWidth / 2-20 < _x && sqX + sqWidth / 2+20 > _x){
                        if(sqY - sqHeight / 2-20 < _y && sqY + sqHeight / 2+20 > _y){
                            _distance = (float) culcDistance(sqX,sqY,(int)e.getX(),(int)e.getY());
                            Log.e("log",Float.toString(_distance));
                            TouchMode = "SCALE";
                        }
                    }


                    break;
                case MotionEvent.ACTION_MOVE:
                    if (TouchMode == "MOVE") {
                        float disX = e.getX() - _x;
                        float disY = e.getY() - _y;

                        sqX += disX;
                        sqY += disY;


                    }else if(TouchMode == "SCALE"){

                        float _cdistance = (float) culcDistance(sqX,sqY,(int)e.getX(),(int)e.getY());

                        float _rate = _cdistance/_distance;
                        _rate = (_rate < 1.05)? _rate: 1.05f;
                        _rate = (_rate > 0.95)? _rate: 0.95f;
                        sqWidth *= _rate;
                        if(sqWidth > _w){
                            sqWidth = _w;
                        }else if(sqWidth < 100){
                            sqWidth = 100;
                        }
                        sqHeight = sqWidth;
                    }
                    if (sqX - sqWidth / 2 < 0) {
                        sqX = sqWidth / 2;
                    } else if (sqX + sqWidth / 2 > _w) {
                        sqX = _w - sqWidth / 2;
                    }
                    if (sqY - sqHeight / 2 < 0) {
                        sqY = sqHeight / 2;
                    } else if (sqY + sqHeight / 2 > _h) {
                        sqY = _h - sqHeight / 2;
                    }
                    _distance = (float) culcDistance(sqX,sqY,(int)e.getX(),(int)e.getY());
                    _x = e.getX();
                    _y = e.getY();
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    TouchMode = "NONE";
                    break;
                default:
                    break;
            }
            return true;
        }

        private double culcDistance(int x1,int y1,int x2,int y2) {
            float x = x1 - x2;
            float y = y1 - y2;
            return java.lang.Math.sqrt(x * x + y * y);
        }


    }
}