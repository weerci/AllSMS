package com.production.kriate.allsms.db;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Класс уровня модели.
 * Описывает сущность шаблона, позволяющего задать шаблон для sms, номера, по которому она отправляется
 * ее описанием и приоритетом.
 * Приоритет используется для задания свойства Избранные или Не избранные.
 * Шаблон с предопределенным номером, по которому отправляется SMS
 */
public class DbSms implements Serializable {
    public static final long EMPTY_ID = -1;

    private final long mId;       // Идентификатор шаблона в базе
    private final String mTitleSms;  // Название шаблона
    private final String mTextSms;   // Текст отправляемый в SMS
    private final String mPhoneNumber;    // Номер по которому отправляется SMS
    private final int mPriority;  // 0- не избранный, 1-избранный
    private DbCategory mCategory; // Категория, к которой принадлежит шаблон, может быть null

    public  DbSms(long id, String titleSms, String textSms, String phoneNumber, int priority,
                  DbCategory dbCategory) {
        mId = id;
        mTitleSms = titleSms;
        mTextSms = textSms;
        mPriority = priority;
        mPhoneNumber = phoneNumber;
        mCategory = dbCategory != null && dbCategory.getId() == -1 ? null : dbCategory;
    }

    @NotNull
    public static DbSms getEmptySms()
    {
        return new DbSms(EMPTY_ID, "", "", "", 0, null);
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
    public DbCategory getCategory() {return mCategory;}
    public void setCategory(DbCategory category){
        mCategory = category;
    }
}
