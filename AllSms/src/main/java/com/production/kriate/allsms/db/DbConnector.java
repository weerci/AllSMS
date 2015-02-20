package com.production.kriate.allsms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by dima on 28.01.2015.
 * Реализует взаимодействие с базой данных template.db
 */
public class DbConnector {

    // Данные базы данных и таблиц
    private static final String DATABASE_NAME = "template.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "sms";

    // Название столбцов
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE_SMS = "TitleSms";
    private static final String COLUMN_TEXT_SMS = "TextSms";
    private static final String COLUMN_PHONE_NUMBER = "PhoneNumber";
    private static final String COLUMN_PRIORITY = "Priority";

    // Номера столбцов
    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_TITLE_SMS = 1;
    private static final int NUM_COLUMN_TEXT_SMS = 2;
    private static final int NUM_COLUMN_PHONE_NUMBER = 3;
    private static final int NUM_COLUMN_PRIORITY = 4;

    private SQLiteDatabase mDataBase;
    private static DbConnector sDbConnector;

    private DbConnector(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public static DbConnector newInstance(Context c){
        if (sDbConnector == null) {
            sDbConnector = new DbConnector(c.getApplicationContext());
        }
        return  sDbConnector;
    }

    public long insert(DbSms ds) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE_SMS, ds.getTitleSms());
        cv.put(COLUMN_TEXT_SMS, ds.getTextSms());
        cv.put(COLUMN_PHONE_NUMBER, ds.getPhoneNumber());
        cv.put(COLUMN_PRIORITY, ds.getPriority());

        return mDataBase.insert(TABLE_NAME, null, cv);
    }
    public int update(DbSms ds) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_TITLE_SMS, ds.getTitleSms());
        cv.put(COLUMN_TEXT_SMS, ds.getTextSms());
        cv.put(COLUMN_PHONE_NUMBER, ds.getPhoneNumber());
        cv.put(COLUMN_PRIORITY, ds.getPriority());
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] { String.valueOf(ds.getId()) });
    }
    public void deleteOne(long id) {
        mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }
    public void deleteArray(int[] ids) {
        for (int i : ids){
            deleteOne(i);
        }
    }
    public DbSms selectOne(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[] { String.valueOf(id) }, null, null, COLUMN_TITLE_SMS);

        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()){
            String titleSms  = mCursor.getString(NUM_COLUMN_TITLE_SMS);
            String textSms = mCursor.getString(NUM_COLUMN_TEXT_SMS);
            String phoneNumber = mCursor.getString(NUM_COLUMN_PHONE_NUMBER);
            int priority = mCursor.getInt(NUM_COLUMN_PRIORITY);

            return new DbSms(id, titleSms, textSms, phoneNumber, priority);
        } else {
            return DbSms.getEmptySms();
        }

    }
    public ArrayList<DbSms> selectAll(){
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, COLUMN_TITLE_SMS);

        ArrayList<DbSms> arr = new ArrayList<DbSms>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String titleSms = mCursor.getString(NUM_COLUMN_TITLE_SMS);
                String textSms = mCursor.getString(NUM_COLUMN_TEXT_SMS);
                String phoneNumber = mCursor.getString(NUM_COLUMN_PHONE_NUMBER);
                int priority = mCursor.getInt(NUM_COLUMN_PRIORITY);
                arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority));
            } while (mCursor.moveToNext());
        }
        return arr;
    }
    public ArrayList<DbSms> selectFavorite(){
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_PRIORITY + " = ?", new String[] { "1"}, null, null, COLUMN_TITLE_SMS);

        ArrayList<DbSms> arr = new ArrayList<DbSms>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String titleSms = mCursor.getString(NUM_COLUMN_TITLE_SMS);
                String textSms = mCursor.getString(NUM_COLUMN_TEXT_SMS);
                String phoneNumber = mCursor.getString(NUM_COLUMN_PHONE_NUMBER);
                int priority = mCursor.getInt(NUM_COLUMN_PRIORITY);
                arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority));
            } while (mCursor.moveToNext());
        }
        return arr;
    }
    public ArrayList<DbSms> selectOther(){
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_PRIORITY + " = ?", new String[] { "0"}, null, null, COLUMN_TITLE_SMS);

        ArrayList<DbSms> arr = new ArrayList<DbSms>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String titleSms = mCursor.getString(NUM_COLUMN_TITLE_SMS);
                String textSms = mCursor.getString(NUM_COLUMN_TEXT_SMS);
                String phoneNumber = mCursor.getString(NUM_COLUMN_PHONE_NUMBER);
                int priority = mCursor.getInt(NUM_COLUMN_PRIORITY);
                arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    private class OpenHelper extends SQLiteOpenHelper {

        private OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE_SMS + " TEXT, " +
                    COLUMN_TEXT_SMS + " TEXT, " +
                    COLUMN_PHONE_NUMBER + " TEXT, " +
                    COLUMN_PRIORITY + " INTEGER ); ";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}
