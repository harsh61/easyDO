package uoit.ca.easydo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DailyView extends AppCompatActivity {
    Button btn;
    EventDBHelper dbHelper;
    LinearLayout ll, ll2;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dbHelper.onUpgrade(dbHelper.getWritableDatabase(),0,1);
        setContentView(R.layout.activity_daily_view);
        btn =(Button)findViewById(R.id.dailyHeader);
        ll = (LinearLayout)findViewById(R.id.buttonLayout);
        ll2 = (LinearLayout) findViewById(R.id.buttonLayout2);
        checkBox = (CheckBox) findViewById(R.id.checkBox2);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        dbHelper = new EventDBHelper(this,null,null,1);

        Bundle data = getIntent().getExtras();
        long testLong = 0;
        if(data!= null) {
            String[] backEvents = data.getStringArray("Events");
            if (backEvents != null && backEvents.length > 0) {

                for (String s : backEvents) {
                    String[] datas = s.split("=");
                    String[] colour = datas[1].split(",");
                    String[] timest = datas[2].split(",");
                    String title = datas[3].substring(0, datas[3].length() - 1);
                    Log.d("DATA", colour[0] + " " + timest[0] + " " + title);
                    getCurrentDate(Long.parseLong(timest[0]));
                }
                testLong = data.getLong("localDate");
                compareDate(testLong);

            } else if (data.getLong("localDate") != 0) {
                getCurrentDate(data.getLong("localDate"));
                Log.d("Current Time", data.getLong("localDate") + "   ");
                testLong = data.getLong("localDate");
                compareDate(testLong);
            }
        } else {
            getCurrentDate(new Date().getTime());
            testLong = new Date().getTime();
            compareDate(testLong);
        }

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        final long finalTestLong = testLong;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNew(finalTestLong);
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                    Log.d("Task done changed", isChecked +"");
                                                    if(isChecked){
                                                        compareDate2(finalTestLong);
                                                    } else {
                                                        compareDate(finalTestLong);
                                                    }


                                                }
                                            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_daily_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getCurrentDate(Long time) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("MM / dd / yyyy ");
        String strDate = mdformat.format(time);
        btn.setText(strDate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View view) {
        Intent intent1 = new Intent(this, CalendarActivity.class);
        startActivity(intent1);
    }

    public void createNew(Long date) {
        Log.d("Test", "Hallo");
        Intent intent1 = new Intent(this, TaskInfoView.class);
        Bundle b = new Bundle();
        b.putString("ID","Daily");
        b.putLong("Date2",date);
        intent1.putExtras(b);
        startActivity(intent1);
    }

    public void compareDate(long date){
        ll2.removeAllViews();
        ll.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(date);

        Event[] all = dbHelper.databaseToArray();
        int[] statuss = dbHelper.databaseToStatus();
        final String[] stringss = dbHelper.databaseToDescription();
        int ctr = 0;
        int ctr2 = 0;
        if(all != null) {
            for (final Event event : all) {
                Log.d("all tasks",event.getData().toString());
                if(statuss[ctr2] == 0) {
                    final String des = stringss[ctr2];
                    cal2.setTimeInMillis(event.getTimeInMillis());
                    boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

                    if (sameDay) {
                        Log.d("Same Day", event.getData().toString());
                        Button btn1 = new Button(this);
                        btn1.setId(ctr);
                        final int id_ = btn1.getId();
                        btn1.setText(event.getData().toString());
                        ll.addView(btn1, lp);
                        btn1 = ((Button) findViewById(id_));

                        btn1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                goToTask(event,des, false);
                            }
                        });
                        ctr++;
                    }
                }
                ctr2 ++;
            }
        }
    }

    public void compareDate2(long date){
        //ll.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(date);

        Event[] all = dbHelper.databaseToArray();
        int[] statuss = dbHelper.databaseToStatus();
        String[] stringss = dbHelper.databaseToDescription();
        int ctr = 0;
        int ctr2 = 0;
        if(all != null) {
            for (final Event event : all) {
                Log.d("all tasks",event.getData().toString());
                if(statuss[ctr2] == 1) {
                    final String des = stringss[ctr2];
                    cal2.setTimeInMillis(event.getTimeInMillis());
                    boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

                    if (sameDay) {
                        Log.d("Same Day", event.getData().toString());
                        Button btn1 = new Button(this);
                        btn1.setId(100+ctr);
                        final int id_ = btn1.getId();
                        btn1.setText(event.getData().toString());
                        ll2.addView(btn1, lp);
                        btn1 = ((Button) findViewById(id_));

                        btn1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                goToTask(event,des,true);

                            }
                        });
                        ctr++;
                    }
                }
                ctr2 ++;
            }
        }
    }

    public void goToTask(Event event, String des, boolean clicked) {
        Intent intent = new Intent(this, TaskInfoView.class);
        Bundle bundle = new Bundle();
        bundle.putString("Title", event.getData().toString());
        bundle.putLong("Date", event.getTimeInMillis());
        bundle.putString("Des", des);
        bundle.putString("ID","Daily");
        bundle.putBoolean("Done", clicked);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

