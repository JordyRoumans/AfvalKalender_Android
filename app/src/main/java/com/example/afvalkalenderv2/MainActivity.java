package com.example.afvalkalenderv2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {


    List summaryToday,summaryTomorrow;
    int hours,minutes;
    //get datetimeformatter
    DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;
    //get tomorrow date
    Date dt = new Date();
    //format the date
    String tomorrow = dtf.format(LocalDateTime.from(dt.toInstant().atZone(ZoneId.of("UTC"))).plusDays(1));
    String dayAfterTomorrow = dtf.format(LocalDateTime.from(dt.toInstant().atZone(ZoneId.of("UTC"))).plusDays(2));
    //get a calender for the timer
    Calendar calender = Calendar.getInstance();
    //get datepicker
    DatePickerDialog.OnDateSetListener setListener;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check build version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //make notification channel
            NotificationChannel channel = new NotificationChannel("My notifications","My notifications", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }




    }

    private void getTodayAndTomorrow()
    {
        TextView txtSummaryToday = (TextView) findViewById(R.id.textViewSummaryToday);
        TextView txtSummaryTomorrow = (TextView) findViewById(R.id.textViewSummaryTomorrow);
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        summaryToday = databaseHelper.SelectFromDate(tomorrow);
        summaryTomorrow = databaseHelper.SelectFromDate(dayAfterTomorrow);
        databaseHelper.close();
        txtSummaryToday.setText(summaryToday.toString().replaceAll("\\[", "").replaceAll("\\]",""));
        txtSummaryTomorrow.setText(summaryTomorrow.toString().replaceAll("\\[", "").replaceAll("\\]",""));
    }

    private void loadSettings()
    {
        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        hours = Integer.parseInt(manager.getString("Hour","12"));
        minutes = Integer.parseInt(manager.getString("Minute","1"));
    }

    private void setCalender(Calendar c)
    {
        c.set(Calendar.HOUR_OF_DAY,hours);
        c.set(Calendar.MINUTE,minutes);
        //for higher accuracy
        c.set(Calendar.SECOND,1);

    }



    @Override
    protected void onStart() {
        super.onStart();

        loadSettings();
        getTodayAndTomorrow();

        //set time for repeating notification
        setCalender(calender);

        Intent intent = new Intent(getApplicationContext(),Notification_receiver.class);
        intent.setAction("MY_NOTIFICATION_MESSAGE");
        intent.putExtra("Summary",summaryTomorrow.toString().replaceAll("\\[", "").replaceAll("\\]",""));


        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);


        TextView txtSummarySearch = (TextView) findViewById(R.id.textViewSummarySearch);
        EditText txtSearch = (EditText) findViewById(R.id.editTextTextPersonName5);
        EditText datePick = findViewById(R.id.editTextDate);
        Calendar datePicker = Calendar.getInstance();
        int year = datePicker.get(Calendar.YEAR);
        int month = datePicker.get(Calendar.MONTH);
        int day = datePicker.get(Calendar.DAY_OF_MONTH);

        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSettings();
                setCalender(calender);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = year +"-"+month+1+"-"+day;
                List SummarySearch=null;

                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                txtSearch.setText(date);
                SummarySearch = databaseHelper.SelectFromDate(date.toString());
                txtSummarySearch.setText(SummarySearch.toString().replaceAll("\\[", "").replaceAll("\\]",""));

                databaseHelper.close();

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
        setCalender(calender);
        getTodayAndTomorrow();
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