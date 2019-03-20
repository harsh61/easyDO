package uoit.ca.easydo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.github.sundeepk.compactcalendarview.domain.Event;

public class EventDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "easydo";
    private static final String TABLE_NAME = "events";
    private static final String COL_1 = "id";
    private static final String COL_2 = "title";
    private static final String COL_3 = "date";
    private static final String COL_4 = "description" ;
    private static final String COL_5 = "colour" ;
    private static final String COL_6 = "status" ;
    public static int currentID = 0;

    public EventDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_1 + " Integer PRIMARY KEY AUTOINCREMENT," +
                COL_2 +  " Text NOT NULL," +
                COL_3 + " Integer, "+
                COL_4 + " Text, "+
                COL_5 + " Text, "+
                COL_6 + " Integer DEFAULT 0)" + ";" ;

        Log.d("DBText","createTable: " + createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table " + TABLE_NAME + ";" );
        this.onCreate(db);
    }


    public int addRecord(Event event, String descrip) {
        currentID++;
        ContentValues values= new ContentValues();

        values.put(COL_2, event.getData().toString());
        values.put(COL_3, event.getTimeInMillis());
        values.put(COL_5, event.getColor());
        values.put(COL_4, descrip);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME,null, values);

        db.close();
        return currentID;
    }

    public void changeRecStatus(String nameInput, long dateInput){
        Log.d("inchangestatus", nameInput + " " + dateInput);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " +COL_6+ "=1 where " + COL_2 + "='"  + nameInput +"' AND " +COL_3 + "='" +dateInput+"';");
        db.close();
    }
    public void changeRecStatus1(String nameInput, long dateInput){
        Log.d("inchangestatus", nameInput + " " + dateInput);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " +COL_6+ "=0 where " + COL_2 + "='"  + nameInput +"' AND " +COL_3 + "='" +dateInput+"';");
        db.close();
    }

    public void changeRecDescription(String des, String nameInput, long dateInput){
        Log.d("inchangestatus", nameInput + " " + dateInput);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " +COL_4+ "='"+des+"' where " + COL_2 + "='"  + nameInput +"' AND " +COL_3 + "='" +dateInput+"';");
        db.close();
    }

    public  Event[] databaseToArray(){
        SQLiteDatabase db = getWritableDatabase();
        String query =" Select * from " + TABLE_NAME + ";";
        int size = 0;
        Cursor c = db.rawQuery(query,null);

        while(!(c.isAfterLast())){
            size++;
            c.moveToNext();
        }

        Log.d("DB size", size+"");
        size--;

        if(size < 0){
            return null;
        }

        Log.d("Product[] size", size + "");
        Event[] events = new Event[size];
        String result = "";

        c.moveToFirst();
        int size2 = 0;

        while(size2 < size){
            Log.d("Task done",c.getInt(c.getColumnIndex(COL_6))+"");
            events[size2] = new Event(c.getInt(c.getColumnIndex(COL_5)), c.getLong(c.getColumnIndex(COL_3)), c.getString(c.getColumnIndex(COL_2)));
            size2++;
            c.moveToNext();
        }

        db.close();
        return events;
    }

    public  int[] databaseToStatus(){
        SQLiteDatabase db = getWritableDatabase();
        String query =" Select * from " + TABLE_NAME + ";";
        int size = 0;
        Cursor c = db.rawQuery(query,null);

        while(!(c.isAfterLast())){
            size++;
            c.moveToNext();
        }

        Log.d("DB size", size+"");
        size--;

        if(size < 0){
            return null;
        }

        Log.d("Product[] size", size + "");
        int[] events = new int[size];
        String result = "";

        c.moveToFirst();
        int size2 = 0;

        while(size2 < size){
            Log.d("Task done",c.getInt(c.getColumnIndex(COL_6))+"");
            events[size2] = c.getInt(c.getColumnIndex(COL_6));
            size2++;
            c.moveToNext();
        }

        db.close();
        return events;
    }

    public  String[] databaseToDescription(){
        SQLiteDatabase db = getWritableDatabase();
        String query =" Select * from " + TABLE_NAME + ";";
        int size = 0;
        Cursor c = db.rawQuery(query,null);

        while(!(c.isAfterLast())){
            size++;
            c.moveToNext();
        }

        Log.d("DB size", size+"");
        size--;

        if(size < 0){
            return null;
        }

        Log.d("Product[] size", size + "");
        String[] events = new String[size];
        String result = "";

        c.moveToFirst();
        int size2 = 0;

        while(size2 < size){
            Log.d("Task done",c.getInt(c.getColumnIndex(COL_6))+"");
            events[size2] = c.getString(c.getColumnIndex(COL_4));
            size2++;
            c.moveToNext();
        }

        db.close();
        return events;
    }
}
