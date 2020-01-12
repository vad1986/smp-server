package com.arkadiy.enter.smp3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelperUserConnected extends SQLiteOpenHelper {

    private static final int DATABASE_VARSION = 1;
    private static final String DATABASE_NAME = "userConnected";
    private static final String TABLE_USER = "user";
    private static final String USER_NAME = "user_name";
    private static final String PASSWORD = "password";
    private static final String PRIVATE_KEY = "private_key";
    private static final String USER_ID = "user_id";
    private static final String ROLE="role";
    private static final String KEY_ID="id";
    private static String DB_PATH = "/data/data/com.arkadiy.enter.smp3/databases/";
    private static Context context;

    public DataBaseHelperUserConnected(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VARSION);
        this.context = context;
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_CONNECTED = "CREATE TABLE "+ TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + USER_ID + " TEXT," + USER_NAME + " TEXT,"
                +PASSWORD +" TEXT," + PRIVATE_KEY + " TEXT," +ROLE +" TEXT" + ")";
        db.execSQL(CREATE_USER_CONNECTED);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_USER);
        onCreate(db);
    }
    public void addUser(String userId,String userName,String password,String privateKey,String role){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_ID,userId);
        values.put(USER_NAME,userName);
        values.put(PASSWORD,password);
        values.put(PRIVATE_KEY,privateKey);
        values.put(ROLE,role);
        long result = db.insert(TABLE_USER , null,values);
        db.close();

    }
    public String getPrivateKey(){

        String selectQuery = "SELECT " + PRIVATE_KEY+" FROM "+TABLE_USER;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor.getString(0);
    }
    public String getUserName(){
        String selectQuery = "SELECT " + USER_NAME+" FROM "+TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor.getString(0);
    }
    public String getPassword(){
        String selectQuery = "SELECT " + PASSWORD+" FROM "+TABLE_USER;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor.getString(0);
    }
    public String getUserId(){
        String selectQuery = "SELECT " + USER_ID+" FROM "+TABLE_USER;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor.getString(0);
    }

    public String getRole(){

        String selectQuery = "SELECT " + ROLE+" FROM "+TABLE_USER;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor.getString(0);
    }

    public String ifExist(){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM user where id=1";
        Cursor cursor = db.rawQuery(selectQuery,null);
//        cursor.getCount();

        if(cursor.getCount()> 0 ){
            cursor.moveToFirst();
            return cursor.getString(4);
        }else {return null;}
    }
    public String[] getUser (){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(selectQuery,null);
        int count  = cursor.getColumnCount();
        String list[] = new  String[count];
        if(cursor.moveToFirst()){

            for (int i = 0 ; i < count ; i++ ){
                list[i] = cursor.getString(i);
            }

        }
        return list;
    }
    public  void deleteParameters(){
        SQLiteDatabase db = getReadableDatabase();
        String whereClause = "id=?";
        String[] whereArgs = new String[] {"1"};
        db.delete("user", "id=?", whereArgs);
//        context.deleteDatabase(DATABASE_NAME);
    }//8vfZ1mHqNVBMNJ27yHPfuSvskPFNd1ky6HHXkqlx
}
