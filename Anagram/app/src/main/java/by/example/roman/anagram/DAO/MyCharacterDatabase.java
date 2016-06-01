package by.example.roman.anagram.DAO;

/**
 * Created by Roman on 23.02.2016.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class MyCharacterDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "letterQuestionDB.sqlite";
    private static final int DATABASE_VERSION = 1;

    public MyCharacterDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // you can use an alternate constructor to specify a database location
        // (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission
        // to write to it
        //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);

    }

    public String[] getBasicQuestionById(int id) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"QUESTION,ANSWER"};
        String  selection = "id == ?";
        String [] selectionArgs = {String.valueOf(id)};
        String sqlTables = "BASIC";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, selection, selectionArgs,
                null, null, null);

        c.moveToFirst();
        String [] data = {c.getString(0),c.getString(1)};
        return data;

    }
    public String[] getChampionQuestionById(int id) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"QUESTION,ANSWER"};
        String  selection = "ID == ?";
        String [] selectionArgs = {String.valueOf(id)};
        String sqlTables = "CHAMPION";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, selection, selectionArgs,
                null, null, null);

        c.moveToFirst();
        String [] data = {c.getString(0),c.getString(1)};
        return data;

    }
    public String[] getSuperstarQuestionById(int id) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"QUESTION,ANSWER"};
        String  selection = "ID == ?";
        String [] selectionArgs = {String.valueOf(id)};
        String sqlTables = "SUPERSTAR";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, selection, selectionArgs,
                null, null, null);

        c.moveToFirst();
        String [] data = {c.getString(0),c.getString(1)};
        return data;

    }
    public int getBasicMaxId() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"ID"};
        String sqlTables = "BASIC";

        qb.setTables(sqlTables);
        Cursor c = db.query(sqlTables, new String [] {"COUNT(ID)"}, null, null, null, null, null);

        c.moveToFirst();
        return c.getInt(0);

    }

    public int getChampionMaxId() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"ID"};
        String sqlTables = "CHAMPION";

        qb.setTables(sqlTables);
        Cursor c = db.query(sqlTables, new String[]{"COUNT(ID)"}, null, null, null, null, null);


        c.moveToFirst();
        return c.getInt(0);

    }

    public int getSuperstarMaxId() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"ID"};
        String sqlTables = "SUPERSTAR";

        qb.setTables(sqlTables);
        Cursor c = db.query(sqlTables, new String[]{"COUNT(ID)"}, null, null, null, null, null);


        c.moveToFirst();
        return c.getInt(0);

    }
    public void closeCursor(){
        close();
    }
}
