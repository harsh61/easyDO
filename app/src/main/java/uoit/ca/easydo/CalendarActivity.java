package uoit.ca.easydo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    CompactCalendarView compactCalendar;
    EventDBHelper dbHelper;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new EventDBHelper(this,null,null,1);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNew();
            }
        });

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(dateFormatMonth.format(new Date()));

        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        //Set an event for Teachers' Professional Day 2016 which is 21st of October
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        Log.d("Timestamp", timeStamp);

        Event[] events = dbHelper.databaseToArray();
        if(events != null) {
            for (Event event : events) {
                compactCalendar.addEvent(event);
            }
        }
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                Log.d("Date", dateClicked.toString());
                Log.d("Date Milli", dateClicked.getTime()+"");

                Intent messageIntent = new Intent(Intent.ACTION_PICK);
                List<Event> events = compactCalendar.getEvents(dateClicked.getTime());
                Log.d("size", events.size()+"");
                String[] ev = new String[events.size()];
                Bundle b = new Bundle();

                int ctr = 0;
                for(Event e : events){
                    ev[ctr] = e.toString();
                    Log.d("Mills",e.toString());
                    ctr++;
                }
                goBack(ev, dateClicked.getTime());
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }

    public void goBack(String[] events, long date){
        Intent intent = new Intent(this,DailyView.class);
        Bundle extras = new Bundle();
        extras.putStringArray("Events", events);
        extras.putLong("localDate",date);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void createNew() {
        Log.d("Test", "Hallo");
        Intent intent1 = new Intent(this, TaskInfoView.class);
        Bundle b = new Bundle();
        b.putString("ID", "Calendar");
        intent1.putExtras(b);
        startActivity(intent1);
    }
}
