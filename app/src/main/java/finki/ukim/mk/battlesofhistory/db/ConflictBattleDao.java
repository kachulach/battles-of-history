package finki.ukim.mk.battlesofhistory.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import finki.ukim.mk.battlesofhistory.model.Battle;
import finki.ukim.mk.battlesofhistory.model.Conflict;

/*
    This is a class for instantating a Database Access Object.
    This object will provide methods which will help for accessing the SQLite database.
    The class contains methods and properties for both Conflict and Battle object database manipulation.

    properties:
        database = an SQLiteDatabase object which will be the database
        dbHelper - a ConflictBattleOpenHelper object for creating and opening a SQLite database.
        allColumnsConflict - a String array that contains the names of the columns in the database for Conflict
        allColumnsBattle - a String array that contains the names of the columns in the database for Conflict

    methods:
        public ConflictBattleDao(Context context) - a constructor
        public void openWritable() - getting a database with the dbHelper
        public void close() - closing the dbHelper and database
        public boolean insertConflict(Conflict item) - insertion of Conflict item in database
        public boolean insertBattle(Battle item) - insertion of Battle item in database
        public void trancateTableConflict() - deletion of Conflict table
        public void trancateTableBattle() - deletion of Battle table
        public boolean updateConflict(Conflict item) - update of a Conflict row
        public boolean updateBattle(Battle item) - update of a Battle row
        public List<Conflict> getAllItemsConflict() - returns a list of all Conflict rows
        public List<Battle> getAllItemsBattle() - returns a list of all Battle rows

    helper methods:
        protected Conflict cursorToItemConflict(Cursor cursor) - returns a Conflict object on the cursor's position
        protected Battle cursorToItemBattle(Cursor cursor) - returns a Battle object on the cursor's position
        protected ContentValues itemToContentValuesConflict(Conflict item) - returns a ContentValues object in which
            the properties (like uri or id) of the Conflict item send as argument are contained
        protected ContentValues itemToContentValuesBattle(Battle item) - returns a ContentValues object in which
            the properties (like uri or id) of the Battle item send as argument are contained
*/

public class ConflictBattleDao {

    private SQLiteDatabase database;
    private ConflictBattleDbOpenHelper dbHelper;

    private String[] allColumnsConflict = {
            ConflictBattleDbOpenHelper.COLUMN_ID_CONFLICT,
            ConflictBattleDbOpenHelper.COLUMN_URL_CONFLICT};

    private String[] allColumnsBattle = {
            ConflictBattleDbOpenHelper.COLUMN_ID_BATTLE,
            ConflictBattleDbOpenHelper.COLUMN_URL_BATTLE,
            ConflictBattleDbOpenHelper.COLUMN_PLACE_BATTLE};

    public ConflictBattleDao(Context context) {
        dbHelper = new ConflictBattleDbOpenHelper(context);
    }
    public void openWritable() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
        dbHelper.close();
    }

    public boolean insertConflict(Conflict item) {
        if (item.getId() != null) {
            return updateConflict(item);
        }
        long insertId = database.insert(ConflictBattleDbOpenHelper.TABLE_NAME_CONFLICTS, null,
                itemToContentValuesConflict(item));
        if (insertId > 0) {
            item.setId(insertId);
            return true;
        } else {
            return false;
        }
    }

    public boolean insertBattle(Battle item) {
        if (item.getId() != null) {
            return updateBattle(item);
        }
        long insertId = database.insert(ConflictBattleDbOpenHelper.TABLE_NAME_BATTLES, null,
                itemToContentValuesBattle(item));
        if (insertId > 0) {
            item.setId(insertId);
            return true;
        } else {
            return false;
        }
    }

    public void trancateTableConflict() {
        database.delete(ConflictBattleDbOpenHelper.TABLE_NAME_CONFLICTS, null, null);
    }

    public void trancateTableBattle() {
        database.delete(ConflictBattleDbOpenHelper.TABLE_NAME_BATTLES, null, null);
    }

    public boolean updateConflict(Conflict item) {
        long numRowsAffected = database.update(ConflictBattleDbOpenHelper.TABLE_NAME_CONFLICTS,
                itemToContentValuesConflict(item), ConflictBattleDbOpenHelper.COLUMN_ID_CONFLICT
                        + " = " + item.getId(), null);
        return numRowsAffected > 0;
    }

    public boolean updateBattle(Battle item) {
        long numRowsAffected = database.update(ConflictBattleDbOpenHelper.TABLE_NAME_BATTLES,
                itemToContentValuesBattle(item), ConflictBattleDbOpenHelper.COLUMN_ID_BATTLE
                        + " = " + item.getId(), null);
        return numRowsAffected > 0;
    }

    public List<Conflict> getAllItemsConflict() {
        List<Conflict> items = new ArrayList<Conflict>();

        Cursor cursor = database.query(ConflictBattleDbOpenHelper.TABLE_NAME_CONFLICTS,
                allColumnsConflict, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToItemConflict(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public List<Battle> getAllItemsBattle() {
        List<Battle> items = new ArrayList<Battle>();

        Cursor cursor = database.query(ConflictBattleDbOpenHelper.TABLE_NAME_BATTLES,
                allColumnsBattle, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToItemBattle(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    protected Conflict cursorToItemConflict(Cursor cursor) {
        Conflict item = new Conflict();
        item.setId(cursor.getLong(cursor
                .getColumnIndex(ConflictBattleDbOpenHelper.COLUMN_ID_CONFLICT)));

        item.setUri(cursor.getString(cursor
                .getColumnIndex(ConflictBattleDbOpenHelper.COLUMN_URL_CONFLICT)));

        return item;
    }

    protected Battle cursorToItemBattle(Cursor cursor) {
        Battle item = new Battle();
        item.setId(cursor.getLong(cursor
                .getColumnIndex(ConflictBattleDbOpenHelper.COLUMN_ID_BATTLE)));

        item.setUri(cursor.getString(cursor
                .getColumnIndex(ConflictBattleDbOpenHelper.COLUMN_URL_BATTLE)));

        item.setPlace(cursor.getString(cursor
                .getColumnIndex(ConflictBattleDbOpenHelper.COLUMN_PLACE_BATTLE)));

        return item;
    }


    protected ContentValues itemToContentValuesConflict(Conflict item) {
        ContentValues values = new ContentValues();
        if (item.getId() != null) {
            values.put(ConflictBattleDbOpenHelper.COLUMN_ID_CONFLICT, item.getId());
        }
        values.put(ConflictBattleDbOpenHelper.COLUMN_URL_CONFLICT, item.getUri());
        return values;
    }

    protected ContentValues itemToContentValuesBattle(Battle item) {
        ContentValues values = new ContentValues();
        if (item.getId() != null) {
            values.put(ConflictBattleDbOpenHelper.COLUMN_ID_BATTLE, item.getId());
        }
        values.put(ConflictBattleDbOpenHelper.COLUMN_URL_BATTLE, item.getUri());
        values.put(ConflictBattleDbOpenHelper.COLUMN_PLACE_BATTLE, item.getPlace());

        return values;
    }
}
