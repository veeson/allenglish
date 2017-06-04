package com.lws.allenglish.db;

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

/**
 * Created by Wilson on 2016/12/8.
 */

public class DictionaryDatabaseManager {
    private static SQLiteDatabase dictionaryDatabase = null;
    private static final String DATABASE_FILENAME = "dictionary.db";
//    private static final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/dictionary";

    public static void openDatabase(Context context) {
        if (dictionaryDatabase == null) {
//            new OpenDictionaryDatabaseThread(context).start();
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
            cursor = null;
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
                int count = 0;
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

//    private static class OpenDictionaryDatabaseThread extends Thread {
//        private Context context;
//
//        OpenDictionaryDatabaseThread(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        public void run() {
//            try {
//                // 获得dictionary.db文件的绝对路径
////                String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
////                File dir = new File(DATABASE_PATH);
//                File dir = new File(context.getFilesDir(), DATABASE_FILENAME);
//                // 如果/sdcard/dictionary目录中存在，创建这个目录
////                if (!dir.exists())
////                    dir.mkdir();
//                // 如果在/sdcard/dictionary目录中不存在
//                // dictionary.db文件，则从res\raw目录中复制这个文件到
//                // SD卡的目录（/sdcard/dictionary）
//                if (!(new File(dir.toString())).exists()) {
//                    // 获得封装dictionary.db文件的InputStream对象
//                    InputStream is = context.getResources().openRawResource(R.raw.dictionary);
//                    FileOutputStream fos = new FileOutputStream(dir);
//                    byte[] buffer = new byte[8192];
//                    int count = 0;
//                    // 开始复制dictionary.db文件
//                    while ((count = is.read(buffer)) > 0) {
//                        fos.write(buffer, 0, count);
//                    }
//
//                    fos.close();
//                    is.close();
//                }
//                // 打开/sdcard/dictionary目录中的dictionary.db文件
//                dictionaryDatabase = SQLiteDatabase.openOrCreateDatabase(
//                        dir, null);
//                dictionaryDatabase.rawQuery("PRAGMA case_sensitive_like = ON", null);
////                dictionaryDatabase.rawQuery("CREATE INDEX index_word ON iciba (word_name)", null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

}

