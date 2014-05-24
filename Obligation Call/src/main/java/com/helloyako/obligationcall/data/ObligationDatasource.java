package com.helloyako.obligationcall.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.helloyako.obligationcall.model.CallInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloyako on 2014. 5. 18..
 *
 */
public class ObligationDatasource {
    private SQLiteDatabase database;
    private ObligationSQLiteHelper dbHelper;

    private String[] allColums = {};

    public ObligationDatasource(Context context){
        dbHelper = new ObligationSQLiteHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void deleteAll() {
        database.delete(ObligationSQLiteHelper.DB_TABLE_NAME,null, null);
    }

    public void createCall(long callTime) {
        ContentValues values = getContentValues(callTime);
        database.insert(ObligationSQLiteHelper.DB_TABLE_NAME, null, values);
    }

    public List<CallInfo> getAllCallInfo() {
        List<CallInfo> CallInfoList = new ArrayList<CallInfo>();
        Cursor cursor = database.query(ObligationSQLiteHelper.DB_TABLE_NAME,
                allColums, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            CallInfo CallInfo = cursorToCallInfo(cursor);
            CallInfoList.add(CallInfo);
            cursor.moveToNext();
        }
        closeCursor(cursor);
        return CallInfoList;
    }

    public void updateCall(int index, boolean isDone){
        ContentValues values = new ContentValues();
        values.put(ObligationSQLiteHelper.IS_DONE, isDone);
        String selection = ObligationSQLiteHelper._INDEX + "=?";
        String[] selectionArgs = { Integer.toString(index) };
        database.update(ObligationSQLiteHelper.DB_TABLE_NAME, values, selection, selectionArgs);

    }


    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    private CallInfo cursorToCallInfo(Cursor cursor) {
        int indexIndex = cursor
                .getColumnIndex(ObligationSQLiteHelper._INDEX);
        int callTimeIndex = cursor
                .getColumnIndex(ObligationSQLiteHelper.CALL_TIME);
        int inDoneIndex = cursor.getColumnIndex(ObligationSQLiteHelper.IS_DONE);

        return new CallInfo(cursor.getInt(indexIndex),
                cursor.getLong(callTimeIndex), cursor.getInt(inDoneIndex) > 0);
    }

    private ContentValues getContentValues(long callTime) {
        ContentValues values = new ContentValues();
        values.put(ObligationSQLiteHelper.CALL_TIME, callTime);
        values.put(ObligationSQLiteHelper.IS_DONE, 0);
        return values;
    }

    
}
