package com.prayer.app;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.prayer.app.R;



public class silent extends AppCompatActivity {

    Button startB,endB;
    TextView startText,endText;
    public static final String PREF_SILENT = "silent";



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
                TimePickerDialog Dialog=new TimePickerDialog(com.prayer.app.silent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hours, int minutes) {

                        startText.setText(String.valueOf(hours)+":"+String.valueOf(minutes));

                    }


                }, 15, 50, true);

             Dialog.show();

            }

        });





        endB .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog Dialog=new TimePickerDialog(com.prayer.app.silent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hours, int minutes) {

                        endText.setText(String.valueOf(hours)+":"+String.valueOf(minutes));

                    }


                }, 15, 50, true);

                Dialog.show();

            }

        });
    }


}




