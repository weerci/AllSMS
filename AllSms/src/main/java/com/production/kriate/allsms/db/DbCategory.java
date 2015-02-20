package com.production.kriate.allsms.db;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dima on 12.02.2015.
 * Категория содержащая шаблоны выбранные по определенному критерию
 */
public class DbCategory implements Serializable {
    public static final long EMPTY_ID = -1;

    private long mId;       // Идентификатор категории в базе
    private String mName;  // Название категории
    private ArrayList<DbSms> mListSms; //Список шаблонов категории

    public DbCategory(long id, String name, ArrayList<DbSms> listSms){
        mId = id;
        mName = name;
        mListSms = listSms;
    }

    public static DbCategory getEmptyCategory() {
        return new DbCategory(EMPTY_ID, "",  null);
    }
    public long getId() {
        return mId;
    }
    public String getName() {
        return mName;
    }
    public ArrayList<DbSms> getListSms() {
        return mListSms;
    }
}
