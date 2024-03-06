package com.prayer.app.appcomponents.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prayer.app.R;



public class silent extends AppCompatActivity {

    Button startB,endB;
    TextView startText,endText;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.silentpage);
        startB=findViewById(R.id.StarttimeB);
        endB=findViewById(R.id.EndtimeB);
        startText=findViewById(R.id.StarttextView);
        endText=findViewById(R.id.EndtextView2);


        startB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog=new TimePickerDialog(silent.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hours, int minutes) {

                    endText.setText(String.valueOf(hours)+":"+String.valueOf(minutes));

                }


            }, 15, 50, true);



            }

        });





        endB .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               TimePickerDialog dialog=new TimePickerDialog(silent.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hours, int minutes) {

                       endText.setText(String.valueOf(hours)+":"+String.valueOf(minutes));

                   }


               }, 15, 50, true);



                   }

    });
    }


}




