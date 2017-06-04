package com.lws.allenglish.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.bean.TranslationRecord;
import com.lws.allenglish.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wilson on 2016/12/9.
 */

public class AllEnglishDatabaseManager {
    private static final String DATABASE_NAME = "AllEnglish"; // 数据库名
    private static final int DATABASE_VERSION = 1; // 数据库版本号
    private static AllEnglishDatabaseManager mDatabaseManager;
    private SQLiteDatabase mSqLiteDatabase;

    /**
     * 将构造方法私有化
     *
     * @param context
     */
    private AllEnglishDatabaseManager(Context context) {
        mSqLiteDatabase = new AllEnglishSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION).getWritableDatabase();
    }

    /**
     * 获取AllEnglishDatabaseManager实例
     *
     * @param context
     * @return
     */
    public synchronized static AllEnglishDatabaseManager getInstance(Context context) {
        if (mDatabaseManager == null) {
            mDatabaseManager = new AllEnglishDatabaseManager(context);
        }
        return mDatabaseManager;
    }

    public void saveQueriedWord(@NonNull BaseWord baseWord) {
        ContentValues values = new ContentValues();
        values.put("word", baseWord.word);
        values.put("ph_en", baseWord.ph_en);
        values.put("ph_am", baseWord.ph_am);
        values.put("means", baseWord.means);
        mSqLiteDatabase.insert("SearchWordHistory", null, values);
    }

    public List<BaseWord> loadQueriedWords() {
        List<BaseWord> list = new ArrayList<>();
        Cursor cursor = mSqLiteDatabase.rawQuery("SELECT * FROM SearchWordHistory", null);
        while (cursor.moveToNext()) {
            BaseWord baseWord = new BaseWord();
            baseWord.word = cursor.getString(1);
            baseWord.ph_en = cursor.getString(2);
            baseWord.ph_am = cursor.getString(3);
            baseWord.means = cursor.getString(4);
            list.add(baseWord);
        }
        closeCursor(cursor);
        return list;
    }

    public void saveCollectedWord(@NonNull BaseWord baseWord) {
        ContentValues values = new ContentValues();
        values.put("word", baseWord.word);
        values.put("ph_en", baseWord.ph_en);
        values.put("ph_am", baseWord.ph_am);
        values.put("means", baseWord.means);
        mSqLiteDatabase.insert("WordCollection", null, values);
    }

    /**
     * 取消某个收藏的单词
     */
    public void cancelCollectedWord(@NonNull String word) {
        mSqLiteDatabase.delete("WordCollection", "word = ?", new String[]{word});
    }

    /**
     * 取消所有收藏的单词
     */
    public void cancelAllCollectedWords() {
        mSqLiteDatabase.delete("WordCollection", null, null);
    }

    public List<BaseWord> loadCollectedWords() {
        List<BaseWord> list = new ArrayList<>();
        Cursor cursor = mSqLiteDatabase.rawQuery("SELECT * FROM WordCollection", null);
        while (cursor.moveToNext()) {
            BaseWord baseWord = new BaseWord();
            baseWord.word = cursor.getString(1);
            baseWord.ph_en = cursor.getString(2);
            baseWord.ph_am = cursor.getString(3);
            baseWord.means = cursor.getString(4);
            list.add(baseWord);
        }
        closeCursor(cursor);
        return list;
    }

    public void saveTranslationRecord(@NonNull TranslationRecord record) {
        ContentValues values = new ContentValues();
        values.put("text", record.text);
        values.put("result", record.result);
        values.put("date", TimeUtils.getCurrentTimeInString());
        values.put("source", record.source);
        mSqLiteDatabase.insert("TranslationRecord", null, values);
    }

    public List<TranslationRecord> loadTranslationRecords() {
        List<TranslationRecord> list = new ArrayList<>();
        Cursor cursor = mSqLiteDatabase.rawQuery("SELECT * FROM TranslationRecord", null);
        while (cursor.moveToNext()) {
            TranslationRecord record = new TranslationRecord();
            record.text = cursor.getString(1);
            record.result = cursor.getString(2);
            record.date = cursor.getString(3);
            record.source = cursor.getString(4);
            list.add(record);
        }
        closeCursor(cursor);
        return list;
    }

    /**
     * 删除一条翻译记录
     *
     * @param translationRecord
     */
    public void deleteTranslationRecord(@NonNull TranslationRecord translationRecord) {
        mSqLiteDatabase.delete("TranslationRecord", "text = ? and source = ?", new String[]{translationRecord.text, translationRecord.source});
    }

    /**
     * 删除所有翻译记录
     */
    public void deleteAllTranslationRecords() {
        mSqLiteDatabase.delete("TranslationRecord", null, null);
    }

    /**
     * 查询单词是否已经在单词查询历史数据表里面
     *
     * @param word
     * @return
     */
    public boolean existSearchWordHistory(@NonNull String word) {
        boolean exist = false;
        Cursor cursor = mSqLiteDatabase.rawQuery("SELECT word FROM SearchWordHistory WHERE word = ?", new String[]{word});
        if (cursor.moveToNext()) {
            exist = true;
        }
        closeCursor(cursor);
        return exist;
    }

    /**
     * 查询单词是否已经在单词查询历史数据表里面
     *
     * @param word
     * @return
     */
    public boolean isCollectedWord(@NonNull String word) {
        boolean is = false;
        Cursor cursor = mSqLiteDatabase.rawQuery("SELECT word FROM WordCollection WHERE word = ?", new String[]{word});
        if (cursor.moveToNext()) {
            is = true;
        }
        closeCursor(cursor);
        return is;
    }

    private void closeCursor(Cursor cursor) {
        if (!cursor.isClosed()) {
            cursor.close();
            cursor = null;
        }
    }
}
