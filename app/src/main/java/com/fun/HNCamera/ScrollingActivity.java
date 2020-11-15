package com.fun.HNCamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.fun.HNCamera.R;

public class ScrollingActivity extends Activity {

    ImageButton emotional;
    ImageButton physics;
    ImageButton calture;
    private static final int EMOTIONAL=1;
    private static final int PHYSICS=2;
    private static final int CALTURE=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        setView();
        setLisners();
    }

    private void setView(){
        emotional = (ImageButton)findViewById(R.id.stampEmotional);
        physics = (ImageButton)findViewById(R.id.stampphysics);
        calture = (ImageButton)findViewById(R.id.stampcalture);
        emotional.setImageResource(R.drawable.stampemotional2);
        physics.setImageResource(R.drawable.stampphysical2);
        calture.setImageResource(R.drawable.stumpculture2);
    }

    private void setLisners(){
        emotional.setOnClickListener(click_emotional);
        physics.setOnClickListener(click_physics);
        calture.setOnClickListener(click_calture);
    }
    private View.OnClickListener click_emotional = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectstamp(EMOTIONAL);
        }
    };
    private View.OnClickListener click_physics = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectstamp(PHYSICS);
        }
    };
    private View.OnClickListener click_calture = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectstamp(CALTURE);
        }
    };

    private void selectstamp(int selected){
        Intent intent = new Intent();
        intent.putExtra("stamp_number", selected);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
