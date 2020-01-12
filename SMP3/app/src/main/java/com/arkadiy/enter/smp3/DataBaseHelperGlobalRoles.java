package com.arkadiy.enter.smp3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arkadiy.enter.smp3.activities.LogInActivity;

public class DataBaseHelperGlobalRoles extends SQLiteOpenHelper {

    private static final int DATABASE_VARSION = 1;
    private static final String DATABASE_NAME = "GlobalRoles";
    private static final String TABLE_ROLES = "roles";
    private static final String IDENTIFIR = "identifier";
    private static final String DESCRIPTION = "description";
    private static final String ROLE="role";
    private static final String KEY_ID="id";
    private static String DB_PATH = "/data/data/com.arkadiy.enter.smp3/databases/";
    private static Context context;

    public DataBaseHelperGlobalRoles(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VARSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_CONNECTED = "CREATE TABLE "+ TABLE_ROLES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + IDENTIFIR + " INTEGER," + DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_USER_CONNECTED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_ROLES);
        onCreate(db);
    }
    public void addRoles(int identifier,String description){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(IDENTIFIR,identifier);
        values.put(DESCRIPTION,description);
        long result = db.insert(TABLE_ROLES , null,values);
        db.close();

    }
    public void getMyDepartments (){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ROLES;
        Cursor cursor = db.rawQuery(selectQuery,null);
        int count  = cursor.getCount() / cursor.getColumnCount();
        cursor.moveToFirst();

        for (int i = 0; i <count; i++){
            LogInActivity.DEPARTMANTS.put(cursor.getInt(1),cursor.getString(2));
            cursor.moveToNext();

        }
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
         db.delete(TABLE_ROLES,null,null);
        db.execSQL("delete  from "+ TABLE_ROLES);
        //db.execSQL("TRUNCATE table" + TABLE_ROLES);
        db.close();
    }

}
