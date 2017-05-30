package com.lws.allenglish.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Wilson on 2016/12/9.
 */

public class AllEnglishSqliteOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_SEARCH_WORD_HISTORY_TABLE = "create table SearchWordHistory ("
            + "id INTEGER primary key autoincrement, "
            + "word CHAR(50), "
            + "ph_en CHAR(50), "
            + "ph_am CHAR(50), "
            + "means TINYTEXT)";
    private static final String CREATE_WORD_COLLECTION_TABLE = "create table WordCollection ("
            + "id INTEGER primary key autoincrement, "
            + "word CHAR(50), "
            + "ph_en CHAR(50), "
            + "ph_am CHAR(50), "
            + "means TINYTEXT)";

    private static final String CREATE_TRANSLATION_RECORD_TABLE = "create table TranslationRecord ("
            + "id integer primary key autoincrement, "
            + "text TEXT, "
            + "result TEXT, "
            + "date CHAR(19), "
            + "source CHAR(4), "
            + "UNIQUE(text, source) ON CONFLICT REPLACE"
            + ")";

    private static final String CREATE_ICIBA_DAILY_SENTENCE_TABLE = "create table IcibaDailySentence ("
            + "id integer primary key autoincrement, "
            + "tts CHAR(128), "
            + "content CHAR(128), "
            + "note CHAR(128), "
            + "dateline CHAR(10)"
            + ")";

    public AllEnglishSqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_SEARCH_WORD_HISTORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_WORD_COLLECTION_TABLE);
        sqLiteDatabase.execSQL(CREATE_TRANSLATION_RECORD_TABLE);
        sqLiteDatabase.execSQL(CREATE_ICIBA_DAILY_SENTENCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
