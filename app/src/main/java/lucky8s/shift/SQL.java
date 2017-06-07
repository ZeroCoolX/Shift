package lucky8s.shift;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Christian on 5/24/15.
 */
public class SQL extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static Context context;
    private static final String DATABASE_NAME = "database.db";
    SQLiteDatabase database;

    public SQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int versionOld, int versionNew) {

    }
    public static void setContext(Context mcontext) {
        if (context == null)
            context = mcontext;
    }
    public void createTable(String tableName, String columnNames) {
        if(database == null) database = this.getWritableDatabase();
        database.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(" + columnNames + ");");
    }

    public void insert(String table,String fields, String variables) {
        if(database == null) database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();
        try {
            database.execSQL("INSERT into " + table + " ( " + fields + " ) values ( " + variables + " );");
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
    public void delete(String user, String table){
        if(database == null) database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();
        try {
            database.execSQL("delete from "+table+" where username = '"+user+"' ");
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
    public void deleteAllUserData(String user){
        if(database == null) database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();
        try {
            Cursor c = database.rawQuery("select name from sqlite_master where type='table'", null);
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    if(!c.getString(0).contains("android") && !c.getString(0).equals("tmp")) {
                        delete(user, c.getString(0));
                    }
                    c.moveToNext();
                }
            }
            if(c != null)
            {
                c.close();
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void insert(String table, String variables) {
        if(database == null) database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();
        try {
            database.execSQL("INSERT into " + table + " values ( " + variables + " );");
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

    }

    public void update(String table, String where, String update) {
        if(database == null) database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();
        try {
            database.execSQL("UPDATE " + table + " " + update + " " + where);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

    }

    public void dropTable(String table){
        if(database == null) database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();
        try {
            database.execSQL("DROP TABLE IF EXISTS '"+table+"'");
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

    }
    public boolean checkTable(String tableName){
        if(database == null) database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();

                return true;
            }
            cursor.close();

        }
        return false;
    }
    public boolean checkEntry(String tableName, String where) {
        if(database == null) database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select count(*) from " + tableName + where + ";", null);
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0) {
            cursor.close();

            return true;
        }
        else {
            cursor.close();

            return false;
        }
    }
    public int getCompletedPackCount(){
        if(database == null) database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select '' from score_levels where username = '"+User.getUser()+"' and level_30 > 0 ", null);
        if(cursor != null){
            return cursor.getCount();
        }
        return 0;
    }
    public String getPackCount(String pack) {
        if(database == null) database = this.getWritableDatabase();
        String result = "0";
        Cursor cursor = database.rawQuery("select (" +
                "case when level_1 > 0 then 1 else 0 end +" +
                "case when level_2 > 0 then 1 else 0 end +" +
                "case when level_3 > 0 then 1 else 0 end +" +
                "case when level_4 > 0 then 1 else 0 end +" +
                "case when level_5 > 0 then 1 else 0 end +" +
                "case when level_6 > 0 then 1 else 0 end +" +
                "case when level_7 > 0 then 1 else 0 end +" +
                "case when level_8 > 0 then 1 else 0 end +" +
                "case when level_9 > 0 then 1 else 0 end +" +
                "case when level_10 > 0 then 1 else 0 end +" +
                "case when level_11 > 0 then 1 else 0 end +" +
                "case when level_12 > 0 then 1 else 0 end +" +
                "case when level_13 > 0 then 1 else 0 end +" +
                "case when level_14 > 0 then 1 else 0 end +" +
                "case when level_15 > 0 then 1 else 0 end +" +
                "case when level_16 > 0 then 1 else 0 end +" +
                "case when level_17 > 0 then 1 else 0 end +" +
                "case when level_18 > 0 then 1 else 0 end +" +
                "case when level_19 > 0 then 1 else 0 end +" +
                "case when level_20 > 0 then 1 else 0 end +" +
                "case when level_21 > 0 then 1 else 0 end +" +
                "case when level_22 > 0 then 1 else 0 end +" +
                "case when level_23 > 0 then 1 else 0 end +" +
                "case when level_24 > 0 then 1 else 0 end +" +
                "case when level_25 > 0 then 1 else 0 end +" +
                "case when level_26 > 0 then 1 else 0 end +" +
                "case when level_27 > 0 then 1 else 0 end +" +
                "case when level_28 > 0 then 1 else 0 end +" +
                "case when level_29 > 0 then 1 else 0 end +" +
                "case when level_30 > 0 then 1 else 0 end) as count" +
                " from score_levels where pack ='"+pack+"' and username = '"+User.getUser()+"' ", null);
        if(cursor.moveToFirst()){
            result =  Integer.toString(cursor.getInt(cursor.getColumnIndex("count")));
            cursor.close();

            return result;
        } else {
            cursor.close();

            return result;
        }
    }
    public ArrayList<String> getPacks(String user){
        ArrayList<String> packs = new ArrayList<String>();
        if(database == null) database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select distinct pack from permissions_packs where username = '"+user+"' ",null);
        if(cursor.moveToFirst()){
            for(int x = 0; x < cursor.getCount();x++) {
                cursor.moveToPosition(x);
                packs.add(cursor.getString(cursor.getColumnIndex("pack")));
            }
            cursor.close();

            return packs;
        } else {
            cursor.close();

            return packs;
        }
    }
    public int getCoins(String type){
        if(database == null) database = this.getWritableDatabase();
        int coins = 0;
        Cursor cursor = null;
        String coin = type;
        if(coin.equals("silver") || coin.equals("gold")){
            coin = type.equals("gold")? "gold": "coins";
        }else{
            return 0;
        }
        cursor = database.rawQuery("select "+coin+ " from user where username = '"+User.getUser()+"' ", null);
        if(cursor.moveToFirst()){
            coins = (cursor.getInt(cursor.getColumnIndex(coin)));
            cursor.close();

            return coins;
        }else{
            cursor.close();

            return 0;
        }
    }
    public int addCoins(int coins, String type){
        if(database == null) database = this.getWritableDatabase();
        String coin = type;
        if(coin.equals("silver") || coin.equals("gold")){
            coin = type.equals("gold")? "gold": "coins";
        }else{
            return 0;
        }
        int currentCoins = getCoins(type);
        int newCoins = currentCoins + coins;
        database.execSQL("update user set "+coin+" = "+Integer.toString(newCoins)+" where username = '"+User.getUser()+"' ");
        

        return newCoins;
    }
    public boolean useCoins(int coins, String type){
        if(database == null) database = this.getWritableDatabase();
        String coin = type;
        if(coin.equals("silver") || coin.equals("gold")){
            coin = type.equals("gold")? "gold": "coins";
        }else{

            return false;
        }
            int currentCoins = getCoins(type);
            int newCoins = currentCoins - coins;
            if (newCoins >= 0) {
                database.execSQL("update user set "+coin+ " = " + Integer.toString(newCoins) + " where username = '" + User.getUser() + "' ");

                return true;
            } else {

                return false;
            }
    }
    public String getSingleResult(String tableName, String columnName, String where){
        if(database == null) database = this.getWritableDatabase();
        Cursor result = database.rawQuery("select " + columnName + " from " + tableName + " " + where + " ;", null);
        String results = null;
        if(result != null && result.moveToFirst()) {
            if (result.getType(0) == 0)
                results = null;
            else if (result.getType(0) == 1) {
                results = Integer.toString(result.getInt(0));
            } else if (result.getType(0) == 2) {
                results = Float.toString(result.getFloat(0));
            } else if (result.getType(0) == 3) {
                results = result.getString(0);
            }
        }
        if(result != null){
            result.close();

        }

        if(results == null){
            return "";
        }

        return results;
    }
    public HashMap<Integer, Integer> getLevels(String tableName, String where) {
        if(database == null) database = this.getWritableDatabase();
        HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();
        Cursor result = null;
        result = database.rawQuery("select " +
                "level_1," +
                "level_2," +
                "level_3," +
                "level_4," +
                "level_5," +
                "level_6," +
                "level_7," +
                "level_8," +
                "level_9," +
                "level_10," +
                "level_11," +
                "level_12," +
                "level_13," +
                "level_14," +
                "level_15," +
                "level_16," +
                "level_17," +
                "level_18," +
                "level_19," +
                "level_20," +
                "level_21," +
                "level_22," +
                "level_23," +
                "level_24," +
                "level_25," +
                "level_26," +
                "level_27," +
                "level_28," +
                "level_29," +
                "level_30" +
                (tableName.equals("score_levels")?",levels":"")+
                (tableName.equals("score_levels")?",score":"")+
                " from " + tableName + " " + where, null);
        if (!result.moveToFirst()) {
            result.close();

            return null;
        } else {
            result.moveToFirst();
            int columns = result.getColumnCount();
            for (int x = 0; x < columns; x++) {
                if (result.getType(x) == 0) {
                    levels.put(x + 1, null);
                } else if (result.getType(x) == 1) {
                    levels.put(x + 1, result.getInt(x));
                } else if (result.getType(x) == 3) {
                    levels.put(x + 1, Integer.valueOf(result.getString(x)));
                }
            }
            result.close();

            return levels;
        }
    }
    public void duplicateAll(String fromUser, String toUser){
        if(database == null) database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();
        try {
            if(database == null) database = this.getWritableDatabase();
            Cursor c = database.rawQuery("select name from sqlite_master where type='table'", null);
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    if(!c.getString(0).contains("android") && !c.getString(0).equals("user") && !c.getString(0).equals("tmp")) {
                        duplicateRow(c.getString(0), fromUser, toUser);
                    }
                    c.moveToNext();
                }
            }
            if(c != null)
            {
                c.close();
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
    public void duplicateRow(String table, String fromUser, String toUser) {
        if(database == null) database = this.getWritableDatabase();
        database.execSQL("drop table if exists tmp");
        database.execSQL("create temporary table tmp as select * from "+table+" where username = '"+fromUser+"' ");
        database.execSQL("update tmp set username = '"+toUser+"' ");
        database.execSQL("insert into "+table+" select * from tmp ");
        database.execSQL("drop table if exists tmp");
    }
}
