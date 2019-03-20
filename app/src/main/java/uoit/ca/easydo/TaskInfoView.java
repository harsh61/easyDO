package uoit.ca.easydo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskInfoView extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EventDBHelper dbHelper;
    EditText taskName, taskDate, taskDescription;
    Button btn;
    Date selectedDate;
    CheckBox checkBox;
    Map<Integer, Integer> statuses = new HashMap<Integer, Integer>();
    Boolean newTask;
    Date zwDate;
    Bundle data;
    Date zwDate22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info_view);
        taskName = (EditText)findViewById(R.id.taskName);
        taskDescription = (EditText)findViewById(R.id.taskDescription);
        btn = (Button)findViewById(R.id.dateBtn);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        dbHelper = new EventDBHelper(this,null,null,1);
        newTask = true;
        data = getIntent().getExtras();
        if(data!= null) {
            newTask = false;
            if (data.getString("Title") != null){
                Log.d("Test", "im in");
                checkBox.setVisibility(View.VISIBLE);
                taskDescription.setText(data.getString("Des"));
                checkBox.setChecked(data.getBoolean("Done"));
                taskName.setText(data.getString("Title"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-DD-YYYY");
                zwDate = new Date(data.getLong("Date"));
                zwDate22 = zwDate;
                SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yyyy");
                String dateText = df2.format(zwDate);
                //System.out.println(dateText);
                btn.setText(dateText);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                        @Override
                                                        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                            Log.d("Task done changed", isChecked +"");
                                                            Map<Integer, Integer> map = getStatuses();
                                                            if(isChecked){
                                                                dbHelper.changeRecStatus(data.getString("Title"),data.getLong("Date"));
                                                            } else {
                                                                dbHelper.changeRecStatus1(data.getString("Title"), data.getLong("Date"));
                                                            }

                                                        }
                                                    }
                );
            } else if (data.getString("ID").equals("Calendar")){

            }
            else {
                zwDate = new Date(data.getLong("Date2"));
                SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yyyy");
                String dateText = df2.format(zwDate);
                //System.out.println(dateText);
                btn.setText(dateText);
            }



        }
    }

    public Map<Integer, Integer> getStatuses() {
        for (Map.Entry<Integer, Integer> test : statuses.entrySet()){
            Log.d("Key", "XXX");
        }
        return statuses;
    }

    public void saveEvent(View view) {
        if((selectedDate != null || !btn.getText().toString().isEmpty()) && !taskName.getText().toString().isEmpty()) {
            //if (newTask == true) {
                if (selectedDate != null) {
                    Event event = new Event(Color.BLACK, selectedDate.getTime(), taskName.getText().toString());
                    statuses.put(dbHelper.addRecord(event, taskDescription.getText().toString()), 0);
                    Event[] test = dbHelper.databaseToArray();

                    for (Event e : test) {
                        Log.d("DB entry", e.getData().toString() + "   " + e.getTimeInMillis());
                    }
                    goToDaily(selectedDate.getTime());
                } else if (zwDate == zwDate22) {
                    dbHelper.changeRecDescription(taskDescription.getText().toString(), data.getString("Title"),data.getLong("Date"));
                    goToDaily(zwDate.getTime());
                    return;

                } else {
                    Event[] test = dbHelper.databaseToArray();
                    Event event = new Event(Color.BLACK, zwDate.getTime(), taskName.getText().toString());
                    if (test != null) {
                        for (Event e : test) {
                            Log.d("DB entry", e.getData().toString() + "   " + e.getTimeInMillis());
                            if (e.toString() == event.toString()) {
                                goToDaily(new Date().getTime());
                                return;
                            }
                        }

                    }
                    statuses.put(dbHelper.addRecord(event,taskDescription.getText().toString()), 0);


                    goToDaily(zwDate.getTime());

                    //  }
                }

        } else {
            Toast.makeText(this, "Please select a title and a date!", Toast.LENGTH_LONG).show();
        }
    }

    private void goToDaily(long time) {
        Intent intent = new Intent(this,DailyView.class);
        Bundle extras = new Bundle();
        extras.putLong("localDate",time);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void onClick(View view) {
        if (data.getString("ID").equals("Calendar")) {
            Intent intent1 = new Intent(this, CalendarActivity.class);
            Bundle b = new Bundle();
            startActivity(intent1);
        } else {
            goToDaily(zwDate.getTime());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            String[] backEvents = data.getStringArrayExtra("Events");
            for (String s : backEvents){
                String[] datas = s.split("=");
                String[] colour = datas[1].split(",");
                String[] timest = datas[2].split(",");
                String title = datas[3].substring(0,datas[3].length()-1);
                Log.d("DATA", colour[0] + " " + timest[0] + " " + title );
            }
        }
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("MM / dd / yyyy ");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public void onClick2(View view){
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        selectedDate = c.getTime();

        StringBuilder dateStr = new StringBuilder().append(month + 1)
                .append("-").append(day).append("-").append(year)
                .append(" ");
        btn.setText(dateStr.toString());
    }
}