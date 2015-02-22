package com.production.kriate.allsms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by dima on 28.01.2015.
 * Реализует взаимодействие с таблицей sms
 */
@SuppressWarnings("ALL")
public class DbConnector {

    private static SQLiteDatabase mDataBase;
    private static DbConnector sDbConnector;

    private DbConnector(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }
    public static DbConnector newInstance(@NotNull Context c){
        if (sDbConnector == null) {
            sDbConnector = new DbConnector(c.getApplicationContext());
        }
        return  sDbConnector;
    }

    @NotNull
    public Sms getSms(){
        return new Sms();
    }
    @NotNull
    public Category getCategory(){
        return new Category();
    }

    private class OpenHelper extends SQLiteOpenHelper {
        // Данные базы данных и таблиц
        private static final String DATABASE_NAME = "template.db";
        private static final int DATABASE_VERSION = 2;
        private ArrayList<String> mQueries = new ArrayList<>();

        private OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            mQueries.add("CREATE TABLE sms (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, TitleSms TEXT, TextSms TEXT, PhoneNumber TEXT, Priority INTEGER); ");
            mQueries.add("CREATE TABLE category (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL); ");
            mQueries.add("CREATE TABLE sms_category (id_sms INTEGER REFERENCES sms (id), id_category INTEGER REFERENCES category (id), PRIMARY KEY (id_sms ASC, id_category ASC)); ");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            try {
                for (String s : mQueries) {
                    db.execSQL(s);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(@NotNull SQLiteDatabase db, int oldVersion, int newVersion) {
            db.beginTransaction();
            try {
                for (int i = 1; i <= newVersion; i++) {
                    db.execSQL(mQueries.get(i));
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    // Реализует взаимодействие с таблицой sms
    public static class Sms{
        public long insert(@NotNull DbSms ds) {
            ContentValues cv = new ContentValues();
            cv.put(TableSms.COLUMN_TITLE_SMS, ds.getTitleSms());
            cv.put(TableSms.COLUMN_TEXT_SMS, ds.getTextSms());
            cv.put(TableSms.COLUMN_PHONE_NUMBER, ds.getPhoneNumber());
            cv.put(TableSms.COLUMN_PRIORITY, ds.getPriority());

            return mDataBase.insert(TableSms.TABLE_NAME, null, cv);
        }
        public int update(@NotNull DbSms ds) {
            ContentValues cv=new ContentValues();
            cv.put(TableSms.COLUMN_TITLE_SMS, ds.getTitleSms());
            cv.put(TableSms.COLUMN_TEXT_SMS, ds.getTextSms());
            cv.put(TableSms.COLUMN_PHONE_NUMBER, ds.getPhoneNumber());
            cv.put(TableSms.COLUMN_PRIORITY, ds.getPriority());
            return mDataBase.update(TableSms.TABLE_NAME, cv, TableSms.COLUMN_ID + " = ?", new String[] { String.valueOf(ds.getId()) });
        }
        public void deleteOne(long id) {
            mDataBase.delete(TableSms.TABLE_NAME, TableSms.COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        }
        public void deleteArray(@NotNull int[] ids) {
            for (int i : ids){
                deleteOne(i);
            }
        }
        @NotNull
        public DbSms selectOne(long id) {
            Cursor mCursor = mDataBase.query(TableSms.TABLE_NAME, null, TableSms.COLUMN_ID + " = ?", new String[] { String.valueOf(id) }, null, null, TableSms.COLUMN_TITLE_SMS);

            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()){
                    String titleSms  = mCursor.getString(TableSms.NUM_COLUMN_TITLE_SMS);
                    String textSms = mCursor.getString(TableSms.NUM_COLUMN_TEXT_SMS);
                    String phoneNumber = mCursor.getString(TableSms.NUM_COLUMN_PHONE_NUMBER);
                    int priority = mCursor.getInt(TableSms.NUM_COLUMN_PRIORITY);

                    return new DbSms(id, titleSms, textSms, phoneNumber, priority);
                } else {
                    return DbSms.getEmptySms();
                }
            } finally {
                mCursor.close();
            }
        }
        @NotNull
        public ArrayList<DbSms> selectAll(){
            Cursor mCursor = mDataBase.query(TableSms.TABLE_NAME, null, null, null, null, null, TableSms.COLUMN_TITLE_SMS);
            ArrayList<DbSms> arr = new ArrayList<>();

            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()) {
                    do {
                        long id = mCursor.getLong(TableSms.NUM_COLUMN_ID);
                        String titleSms = mCursor.getString(TableSms.NUM_COLUMN_TITLE_SMS);
                        String textSms = mCursor.getString(TableSms.NUM_COLUMN_TEXT_SMS);
                        String phoneNumber = mCursor.getString(TableSms.NUM_COLUMN_PHONE_NUMBER);
                        int priority = mCursor.getInt(TableSms.NUM_COLUMN_PRIORITY);
                        arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return arr;
        }
        @NotNull
        public ArrayList<DbSms> selectFavorite(){
            Cursor mCursor = mDataBase.query(TableSms.TABLE_NAME, null, TableSms.COLUMN_PRIORITY + " = ?", new String[]{"1"}, null, null, TableSms.COLUMN_TITLE_SMS);
            ArrayList<DbSms> arr = new ArrayList<>();

            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()) {
                    do {
                        long id = mCursor.getLong(TableSms.NUM_COLUMN_ID);
                        String titleSms = mCursor.getString(TableSms.NUM_COLUMN_TITLE_SMS);
                        String textSms = mCursor.getString(TableSms.NUM_COLUMN_TEXT_SMS);
                        String phoneNumber = mCursor.getString(TableSms.NUM_COLUMN_PHONE_NUMBER);
                        int priority = mCursor.getInt(TableSms.NUM_COLUMN_PRIORITY);
                        arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return arr;
        }
        @NotNull
        public ArrayList<DbSms> selectOther(){
            Cursor mCursor = mDataBase.query(TableSms.TABLE_NAME, null, TableSms.COLUMN_PRIORITY + " = ?", new String[]{"0"}, null, null, TableSms.COLUMN_TITLE_SMS);
            ArrayList<DbSms> arr = new ArrayList<>();
            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()) {
                    do {
                        long id = mCursor.getLong(TableSms.NUM_COLUMN_ID);
                        String titleSms = mCursor.getString(TableSms.NUM_COLUMN_TITLE_SMS);
                        String textSms = mCursor.getString(TableSms.NUM_COLUMN_TEXT_SMS);
                        String phoneNumber = mCursor.getString(TableSms.NUM_COLUMN_PHONE_NUMBER);
                        int priority = mCursor.getInt(TableSms.NUM_COLUMN_PRIORITY);
                        arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return arr;
        }
    }
    // Реализует взаимодействие с таблицей category
    public static class Category{
        public long insert(@NotNull DbCategory dc){
            ContentValues cv = new ContentValues();
            cv.put(TableCategory.COLUMN_NAME, dc.getName());
            mDataBase.beginTransaction();
            long result = 0;
            try {
                result = mDataBase.insert(TableCategory.TABLE_NAME, null, cv);
                if (dc.getListSms() != null) {
                    addSms(dc.getListSms(), result);
                }
                mDataBase.setTransactionSuccessful();
            } finally {
                mDataBase.endTransaction();
            }

            return result;
        }
        public int update(@NotNull DbCategory dc){
            ContentValues cv=new ContentValues();
            cv.put(TableCategory.COLUMN_NAME, dc.getName());
            mDataBase.beginTransaction();
            int result;
            try {
                result = mDataBase.update(TableCategory.TABLE_NAME, cv, TableCategory.COLUMN_ID + " = ?", new String[] { String.valueOf(dc.getId()) });
                if (dc.getListSms() != null) {
                    addSms(dc.getListSms(), dc.getId());
                }
                mDataBase.setTransactionSuccessful();
            } finally {
                mDataBase.endTransaction();
            }
            return result;
        }
        private void addSms(@NotNull ArrayList<DbSms> dbSms, long id_category){
            mDataBase.delete(TableSmsCategory.TABLE_NAME, TableSmsCategory.COLUMN_ID_CATEGORY + " = ?", new String[]{String.valueOf(id_category)});
            for (DbSms i : dbSms) {
                mDataBase.execSQL("insert into "+TableSmsCategory.TABLE_NAME + "("+TableSmsCategory.COLUMN_ID_CATEGORY+", "+TableSmsCategory.COLUMN_ID_SMS+
                        ") values("+String.valueOf(id_category)+", "+String.valueOf(i.getId())+")");
            }
        }

        public void deleteOne(long id){
            mDataBase.delete(TableCategory.TABLE_NAME, TableCategory.COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        }
        public void deleteArray(@NotNull int[] ids){
            for (int i : ids) {
                deleteOne(i);
            }
        }
        @Nullable
        public DbCategory selectOne(long id){
            Cursor mCursor = mDataBase.query(TableCategory.TABLE_NAME, null, TableCategory.COLUMN_ID + " = ?", new String[] { String.valueOf(id) },
                    null, null, TableCategory.COLUMN_NAME);

            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()){
                    String name  = mCursor.getString(TableCategory.NUM_COLUMN_NAME);
                    return new DbCategory(id, name, null);
                } else {
                    return DbCategory.getEmptyCategory();
                }
            } finally {
                mCursor.close();
            }
        }
        @NotNull
        public ArrayList<DbCategory> selectAll(){
            Cursor mCursor = mDataBase.query(TableCategory.TABLE_NAME, null, null, null, null, null, TableCategory.COLUMN_NAME);
            ArrayList<DbCategory> arr = new ArrayList<>();
            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()) {
                    do {
                        long id = mCursor.getLong(TableCategory.NUM_COLUMN_ID);
                        String name = mCursor.getString(TableCategory.NUM_COLUMN_NAME);
                        arr.add(new DbCategory(id, name, null));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return arr;
        }
        public long addSms(long id_sms, long id_category){
            ContentValues cv = new ContentValues();
            cv.put(TableSmsCategory.COLUMN_ID_SMS, id_sms);
            cv.put(TableSmsCategory.COLUMN_ID_CATEGORY, id_category);

            return mDataBase.insert(TableSmsCategory.TABLE_NAME, null, cv);
        }
        public void removeSms(long id_sms, long id_category){
            mDataBase.delete(TableSmsCategory.TABLE_NAME, TableSmsCategory.COLUMN_ID_SMS +
                    " = ? AND " + TableSmsCategory.COLUMN_ID_CATEGORY + " = ?", new String[] { String.valueOf(id_sms), String.valueOf(id_category) });
        }
        @NotNull
        public ArrayList<DbSms> getSelectedSms(long idCategory){
            String queryText = String.format("select s.%1$s, s.%2$s, s.%3$s, s.%4$s, s.%5$s from sms s left join sms_category sc on sc.id_sms = s.id where sc.id_category = %6$s",
                    TableSms.COLUMN_ID,
                    TableSms.COLUMN_TITLE_SMS,
                    TableSms.COLUMN_TEXT_SMS,
                    TableSms.COLUMN_PHONE_NUMBER,
                    TableSms.COLUMN_PRIORITY,
                    idCategory);
            Cursor mCursor = mDataBase.rawQuery(queryText, new String [] {});
            ArrayList<DbSms> arr = new ArrayList<>();
            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()) {
                    do {
                        long id = mCursor.getLong(TableSms.NUM_COLUMN_ID);
                        String titleSms = mCursor.getString(TableSms.NUM_COLUMN_TITLE_SMS);
                        String textSms = mCursor.getString(TableSms.NUM_COLUMN_TEXT_SMS);
                        String phoneNumber = mCursor.getString(TableSms.NUM_COLUMN_PHONE_NUMBER);
                        int priority = mCursor.getInt(TableSms.NUM_COLUMN_PRIORITY);
                        arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return arr;
        }
        @NotNull
        public ArrayList<DbSms> getAvailableSms(long idCategory){
            String queryText = String.format("select s.%1$s, s.%2$s, s.%3$s, s.%4$s, s.%5$s  from sms s where s.id not in (select sc.id_sms from sms_category sc WHERE sc.id_category = %6$s)",
                    TableSms.COLUMN_ID,
                    TableSms.COLUMN_TITLE_SMS,
                    TableSms.COLUMN_TEXT_SMS,
                    TableSms.COLUMN_PHONE_NUMBER,
                    TableSms.COLUMN_PRIORITY,
                    idCategory);

            Cursor mCursor = mDataBase.rawQuery(queryText, new String [] {});
            ArrayList<DbSms> arr = new ArrayList<>();
            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()) {
                    do {
                        long id = mCursor.getLong(TableSms.NUM_COLUMN_ID);
                        String titleSms = mCursor.getString(TableSms.NUM_COLUMN_TITLE_SMS);
                        String textSms = mCursor.getString(TableSms.NUM_COLUMN_TEXT_SMS);
                        String phoneNumber = mCursor.getString(TableSms.NUM_COLUMN_PHONE_NUMBER);
                        int priority = mCursor.getInt(TableSms.NUM_COLUMN_PRIORITY);
                        arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return arr;
        }

    }

    // Таблица шаблонов Sms
    private static class TableSms {
        // Название таблицы
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

    }
    // Таблица категорий Sms
    private static class TableCategory{
        private static final String TABLE_NAME = "category";

        // Название столбцов
        private static final String COLUMN_ID = "id";
        private static final String COLUMN_NAME = "name";

        // Номера столбцов
        private static final int NUM_COLUMN_ID = 0;
        private static final int NUM_COLUMN_NAME = 1;
    }
    // Таблица связка sms и категорий
    private static class TableSmsCategory{
        private static final String TABLE_NAME = "sms_category";
        // Название столбцов
        private static final String COLUMN_ID_SMS = "id_sms";
        private static final String COLUMN_ID_CATEGORY = "id_category";

        // Номера столбцов
        private static final int NUM_COLUMN_ID_SMS = 0;
        private static final int NUM_COLUMN_ID_CATEGORY = 1;
    }
}
