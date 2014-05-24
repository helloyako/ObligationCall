package com.helloyako.obligationcall.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by helloyako on 2014. 5. 18..
 *
 */
public class ObligationSQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_TABLE_NAME = "obligationcall";

    public static final String _INDEX = "_index";
    public static final String CALL_TIME = "call_time";
    public static final String IS_DONE = "is_done";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ObligationCallDB";
    private final String CREATE_STATEMENT = StringUtils.join(
            "create table ",
            DB_TABLE_NAME,
            "(",
            _INDEX," INTEGER PRIMARY KEY AUTOINCREMENT, ",
            CALL_TIME," INTEGER, ",
            IS_DONE," BOOLEAN",
            ")"
    );

    public ObligationSQLiteHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
