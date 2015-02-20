package com.production.kriate.allsms.db;

import java.io.Serializable;

/**
 * Created by dima on 28.01.2015.
 * Шаблон с предопределенным номером, по которому отправляется SMS
 */
public class DbSms implements Serializable {
    public static final long EMPTY_ID = -1;

    private long mId;       // Идентификатор шаблона в базе
    private String mTitleSms;  // Название шаблона
    private String mTextSms;   // Текст отправляемый в SMS
    private String mPhoneNumber;    // Номер по которому отправляется SMS
    private int mPriority;  // Приоритет шаблона

    public  DbSms(long id, String titleSms, String textSms, String phoneNumber, int priority) {
        mId = id;
        mTitleSms = titleSms;
        mTextSms = textSms;
        mPriority = priority;
        mPhoneNumber = phoneNumber;
    }

    public static DbSms getEmptySms()
    {
        return new DbSms(EMPTY_ID, "", "", "", 0);
    }

    public long getId() {
        return mId;
    }
    public String getTitleSms() {
        return mTitleSms;
    }
    public String getTextSms() {
        return mTextSms;
    }
    public String getPhoneNumber() {
        return mPhoneNumber;
    }
    public int getPriority() {
        return mPriority;
    }
}
