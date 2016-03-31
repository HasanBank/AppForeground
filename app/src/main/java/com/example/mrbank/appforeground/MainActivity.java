package com.example.mrbank.appforeground;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mrbank.appforeground.DataBase.ObjectProgramUsing;
import com.example.mrbank.appforeground.DataBase.TableControllerPrograms;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    // Method to start the service
    public void startService(View view) {
        startService(new Intent(getBaseContext(), MyService.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), MyService.class));
    }

    public void readRecords(View view) {

        setContentView(R.layout.programuses);

        LinearLayout linearLayoutRecords = (LinearLayout) findViewById(R.id.linearLayoutRecords);
        linearLayoutRecords.removeAllViews();

        List<ObjectProgramUsing> programs = new TableControllerPrograms(this).read();

        if (programs.size() > 0) {

            for (ObjectProgramUsing obj : programs) {

                int id = obj.id;
                String programName = obj.programName;
                int totalDuration = obj.totalDuration;
                String category = obj.category;

                int secs;
                int mins;
                int hours;
                String duration;

                secs = totalDuration / 1000;

                hours = secs / (60*60);
                mins = (secs - ( hours *60*60) ) / 60;
                secs = secs - (hours*60*60) - (mins*60) ;

                duration = hours+"h "+mins+"m "+secs+"s ";

                String textViewContents = programName + " - " + duration + " - " + category;

                TextView textViewProgramItem= new TextView(this);
                textViewProgramItem.setPadding(0, 10, 0, 10);
                textViewProgramItem.setText(textViewContents);
                textViewProgramItem.setTag(Integer.toString(id));

                linearLayoutRecords.addView(textViewProgramItem);
            }

        }

        else {

            TextView locationItem = new TextView(this);
            locationItem.setPadding(8, 8, 8, 8);
            locationItem.setText("No records yet.");

            linearLayoutRecords.addView(locationItem);
        }

    }


}
