package com.example.afvalkalenderv2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button btn;
    String summary,summaryTomorrow;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get current date
        DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;
        //format the date
        String datum = dtf.format(LocalDate.now());
        //get tomorrow date
        Date dt = new Date();
        //format the date
        String tomorrow = dtf.format(LocalDateTime.from(dt.toInstant().atZone(ZoneId.of("UTC"))).plusDays(1));


        //check build version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //make notification channel
            NotificationChannel channel = new NotificationChannel("My notifications","My notifications", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //bind button and textview
        btn = (Button) findViewById(R.id.button_1);
        TextView txtSummary = (TextView) findViewById(R.id.textViewSummary);



        //event listener
        Date finalDt = dt;
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

                try
                {
                    summary = ParseDates(datum);
                    summaryTomorrow = ParseDates(tomorrow);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                txtSummary.setText(summaryTomorrow);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"My notifications");
                builder.setContentTitle("Afval kalender");
                builder.setContentText(summaryTomorrow);
                builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                managerCompat.notify(1,builder.build());

                }


        });

    }

    private String ParseDates(String dateToCheck) throws IOException {
        String summary=null;
        String[] splitter;
        boolean found=false;

        BufferedReader reader1 = null;
        reader1 = new BufferedReader(
                new InputStreamReader(getAssets().open("afvalKalender")));

        try
        {
            //loop until date is found
            String mLine = reader1.readLine();
            while (mLine!=null && found!=true)
            {
                if (mLine.contains(dateToCheck))
                {
                    //if date is found loop a little further until summary
                    while (mLine!=null && found!=true)
                    {
                        //when summary is found save it and stop the loop
                        if (mLine.contains("SUMMARY"))
                        {
                            splitter =  mLine.split(":",2);
                            summary = splitter[1];
                            found=true;

                        }
                        mLine = reader1.readLine();
                    }


                }
                else
                {
                    //if no matching date is found, return a fitting message
                    summary="Geen afval ophaal verwacht";
                }
                mLine = reader1.readLine();

            }
        }
        catch (IOException e)
        {
            //log the exception
        }
        finally
        {
            try {
                reader1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        found=false;

        return summary;

    }

}