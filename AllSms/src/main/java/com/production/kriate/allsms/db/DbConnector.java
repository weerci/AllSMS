package com.production.kriate.allsms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Класс реализующий шлюз базы данных.
 * Реализует функционал сохранения, обновления, удаления и выборки из базы данных сущностей, описываемых классами
 * DbSms и DbCategory
 */
public class DbConnector {

    private static SQLiteDatabase mDataBase;
    private static DbConnector sDbConnector;
    private Context mContext;

    private DbConnector(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
        mContext = context;
    }
    public static DbConnector newInstance(@NotNull Context c){
        if (sDbConnector == null) {
            sDbConnector = new DbConnector(c);
        }
        return  sDbConnector;
    }

    @NotNull
    public Sms getSms(){
        return new Sms(mContext);
    }
    @NotNull
    public Category getCategory(){
        return new Category(mContext);
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
                for (int i = oldVersion+2/*Коррекция изначальная, 2 версия, имела 3 записи*/; i <= mQueries.size(); i++) {
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
        private Context mContext;
        public Sms(Context context){
            mContext = context;
        }
        // region + updater
        public long insert(@NotNull DbSms ds) {
            ContentValues cv = new ContentValues();
            cv.put(TableSms.COLUMN_TITLE_SMS, ds.getTitleSms());
            cv.put(TableSms.COLUMN_TEXT_SMS, ds.getTextSms());
            cv.put(TableSms.COLUMN_PHONE_NUMBER, ds.getPhoneNumber());
            cv.put(TableSms.COLUMN_PRIORITY, ds.getPriority());


            long i = DbSms.EMPTY_ID;
            mDataBase.beginTransaction();
            try {
                i = mDataBase.insert(TableSms.TABLE_NAME, null, cv);
                if (ds.getCategory() != null) {
                    addCategory(i, ds.getCategory().getId());
                } else {
                    removeCategory(i);
                }

                mDataBase.setTransactionSuccessful();
                return i;
            } finally {
                mDataBase.endTransaction();
            }
        }
        public int update(@NotNull DbSms ds) {
            ContentValues cv=new ContentValues();
            cv.put(TableSms.COLUMN_TITLE_SMS, ds.getTitleSms());
            cv.put(TableSms.COLUMN_TEXT_SMS, ds.getTextSms());
            cv.put(TableSms.COLUMN_PHONE_NUMBER, ds.getPhoneNumber());
            cv.put(TableSms.COLUMN_PRIORITY, ds.getPriority());

            int i;
            mDataBase.beginTransaction();
            try {
                i = mDataBase.update(TableSms.TABLE_NAME, cv, TableSms.COLUMN_ID + " = ?", new String[] { String.valueOf(ds.getId()) });
                if (ds.getCategory() != null) {
                    addCategory(ds.getId(), ds.getCategory().getId());
                } else {
                    removeCategory(ds.getId());
                }
                mDataBase.setTransactionSuccessful();
                return i;
            } finally {
                mDataBase.endTransaction();
            }
        }
        public void deleteOne(long id) {
            mDataBase.beginTransaction();
            try {
                removeCategory(id);
                mDataBase.delete(TableSms.TABLE_NAME, TableSms.COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
                mDataBase.setTransactionSuccessful();
            } finally {
                mDataBase.endTransaction();
            }

        }
        private void removeCategory(long idSms){
            mDataBase.delete(TableSmsCategory.TABLE_NAME, TableSmsCategory.COLUMN_ID_SMS + " = ?", new String[]{String.valueOf(idSms)});
        }
        private void addCategory(long idSms, long idCategory){
            removeCategory(idSms);
            mDataBase.execSQL("insert into " + TableSmsCategory.TABLE_NAME + "(" + TableSmsCategory.COLUMN_ID_CATEGORY + ", " + TableSmsCategory.COLUMN_ID_SMS +
                    ") values(" + String.valueOf(idCategory) + ", " + String.valueOf(idSms) + ")");

        }
        // endregion

        // region +Selectors
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
                        arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority, null));
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
                        arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority, null));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return arr;
        }
        // endregion
    }

    // Реализует взаимодействие с таблицей category
    public static class Category{
        private Context mContext;
        public Category(Context context){
            mContext = context;
        }
        // region + Updaters
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
        public void deleteOne(long idCategory){
            mDataBase.beginTransaction();
            try {
                mDataBase.delete(TableSmsCategory.TABLE_NAME, TableSmsCategory.COLUMN_ID_CATEGORY + " = ?", new String[] { String.valueOf(idCategory) });
                mDataBase.delete(TableCategory.TABLE_NAME, TableCategory.COLUMN_ID + " = ?", new String[] { String.valueOf(idCategory) });
                mDataBase.setTransactionSuccessful();
            } finally {
                mDataBase.endTransaction();
            }
        }
        // endregion

        // region +Selectors
        public DbCategory selectOne(long id){
            Cursor mCursor = mDataBase.query(TableCategory.TABLE_NAME, null, TableCategory.COLUMN_ID + " = ?", new String[] { String.valueOf(id) },
                    null, null, TableCategory.COLUMN_NAME);

            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()){
                    String name  = mCursor.getString(TableCategory.NUM_COLUMN_NAME);
                    return new DbCategory(id, name, null);
                } else {
                    return DbCategory.getEmptyCategory(mContext);
                }
            } finally {
                mCursor.close();
            }
        }
        public DbCategory selectForSms(long idSms){
            String queryText = String.format("select c.%1$s, c.%2$s from category c left join sms_category sc on c.id = sc.id_category where sc.id_sms = %3$s",
                    TableCategory.COLUMN_ID,
                    TableCategory.COLUMN_NAME,
                    idSms);
            Cursor mCursor = mDataBase.rawQuery(queryText, new String [] {});
            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()) {
                    long id = mCursor.getLong(TableCategory.NUM_COLUMN_ID);
                    String name = mCursor.getString(TableCategory.NUM_COLUMN_NAME);
                    return new DbCategory(id, name, null);
                } else {
                    return null;
                }
            } finally {
                mCursor.close();
            }
        }
        public ArrayList<DbCategory> selectAll(){
            ArrayList<DbCategory> arr = new ArrayList<>();
            return getAllCategories(arr);
        }
        public ArrayList<DbCategory> selectWithEmpty(){
            ArrayList<DbCategory> arr = new ArrayList<>();
            arr.add(DbCategory.getEmptyCategory(mContext));
            return getAllCategories(arr);

        }
        private ArrayList<DbCategory> getAllCategories(ArrayList<DbCategory> dbCategories){
            Cursor mCursor = mDataBase.query(TableCategory.TABLE_NAME, null, null, null, null, null, TableCategory.COLUMN_NAME);
            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()) {
                    do {
                        long id = mCursor.getLong(TableCategory.NUM_COLUMN_ID);
                        String name = mCursor.getString(TableCategory.NUM_COLUMN_NAME);
                        dbCategories.add(new DbCategory(id, name, null));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return dbCategories;
        }
        public ArrayList<DbSms> getSelectedSms(DbCategory category){
            ArrayList<DbSms> arr = new ArrayList<>();
            if (category == null) {
                return  arr;
            }
            String queryText = String.format("select s.%1$s, s.%2$s, s.%3$s, s.%4$s, s.%5$s from sms s left join sms_category sc on sc.id_sms = s.id where sc.id_category = %6$s",
                    TableSms.COLUMN_ID,
                    TableSms.COLUMN_TITLE_SMS,
                    TableSms.COLUMN_TEXT_SMS,
                    TableSms.COLUMN_PHONE_NUMBER,
                    TableSms.COLUMN_PRIORITY,
                    category.getId());
            Cursor mCursor = mDataBase.rawQuery(queryText, new String [] {});
            try {
                mCursor.moveToFirst();
                if (!mCursor.isAfterLast()) {
                    do {
                        long id = mCursor.getLong(TableSms.NUM_COLUMN_ID);
                        String titleSms = mCursor.getString(TableSms.NUM_COLUMN_TITLE_SMS);
                        String textSms = mCursor.getString(TableSms.NUM_COLUMN_TEXT_SMS);
                        String phoneNumber = mCursor.getString(TableSms.NUM_COLUMN_PHONE_NUMBER);
                        int priority = mCursor.getInt(TableSms.NUM_COLUMN_PRIORITY);
                        arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority, category));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return arr;
        }
        public ArrayList<DbSms> getAvailableSms(){
            String queryText = String.format("select s.%1$s, s.%2$s, s.%3$s, s.%4$s, s.%5$s  from sms s where s.id not in (select sc.id_sms from sms_category sc)",
                    TableSms.COLUMN_ID,
                    TableSms.COLUMN_TITLE_SMS,
                    TableSms.COLUMN_TEXT_SMS,
                    TableSms.COLUMN_PHONE_NUMBER,
                    TableSms.COLUMN_PRIORITY);

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
                        arr.add(new DbSms(id, titleSms, textSms, phoneNumber, priority, null));
                    } while (mCursor.moveToNext());
                }
            } finally {
                mCursor.close();
            }
            return arr;
        }
        // endregion
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
