package com.lws.allenglish.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lws.allenglish.R;
import com.lws.allenglish.bean.BaseWord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DictionaryDatabaseManager {
    private static SQLiteDatabase dictionaryDatabase = null;
    private static final String DATABASE_FILENAME = "dictionary.db";

    public static void openDatabase(Context context) {
        if (dictionaryDatabase == null) {
            openDictionaryDatabase(context);
        }
    }

    public static List<BaseWord> matchingWord(String keyword) {
        List<BaseWord> list = new ArrayList<>();
        String sql = "SELECT * FROM iciba WHERE word_name LIKE '" + keyword + "%' LIMIT 0,20";
        Cursor cursor = dictionaryDatabase.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            BaseWord baseWord = new BaseWord();
            baseWord.word = cursor.getString(1);
            baseWord.ph_en = cursor.getString(2);
            baseWord.ph_am = cursor.getString(3);
            baseWord.means = cursor.getString(4);
            list.add(baseWord);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public static BaseWord randomWord() {
        Random random = new Random();
        BaseWord baseWord = new BaseWord();
        String sql = "SELECT * FROM iciba WHERE id = " + random.nextInt(102047);
        Cursor cursor = dictionaryDatabase.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            baseWord.word = cursor.getString(1);
            baseWord.ph_en = cursor.getString(2);
            baseWord.ph_am = cursor.getString(3);
            baseWord.means = cursor.getString(4);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return baseWord;
    }

    private static void openDictionaryDatabase(Context context) {
        try {
            File dir = new File(context.getFilesDir(), DATABASE_FILENAME);
            if (!(new File(dir.toString())).exists()) {
                InputStream is = context.getResources().openRawResource(R.raw.dictionary);
                FileOutputStream fos = new FileOutputStream(dir);
                byte[] buffer = new byte[8192];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }
            dictionaryDatabase = SQLiteDatabase.openOrCreateDatabase(
                    dir, null);
            dictionaryDatabase.rawQuery("PRAGMA case_sensitive_like = ON", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

