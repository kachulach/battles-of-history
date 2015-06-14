package finki.ukim.mk.battlesofhistory.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
    This is a SQLiteOpenHelper class which is used for creating and upgrading an SQLite database for
       persisting data(like retrieved Conflict and Battle objects from dbpedia)

    The database contains two tables, which keep the retrieved data, named:
        - Battles
        - Conflicts

    The Conflicts table has the columns named:
        - _id - a unique identifier
        - Url - the url of the dbpedia resource

    The Battles table has the columns named:
        - _id - a unique identifier
        - Url - the url of the dbpedia resource
        - Place - an url of the dbpedia resource where the battle happened

    The ConflictBattleDbOpenHelper class also contains constant strings which are SQL expressions for
        creating new tables:
        - DATABASE_CREATE_CONFLICTS - string for creating the Conflicts table
        - DATABASE_CREATE_BATTLES - string for creating the Battles table

    There are two overriden methods:
        - public void onCreate(SQLiteDatabase db) - which basically says that when this object is created, the two
            sql table creation strings will be executed and the tables will be created
        - public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) - when there are changes in the
            tables, the old ones are dropped and new ones are created
 */
public class ConflictBattleDbOpenHelper extends SQLiteOpenHelper{

    public static final String COLUMN_ID_CONFLICT = "_id";
    public static final String COLUMN_URL_CONFLICT = "Url";

    public static final String COLUMN_ID_BATTLE= "_id";
    public static final String COLUMN_URL_BATTLE = "Url";
    public static final String COLUMN_PLACE_BATTLE = "Place";

    public static final String TABLE_NAME_CONFLICTS = "Conflicts";
    public static final String TABLE_NAME_BATTLES = "Battles";

    private static final int DATABASE_VERSION_CONFLICTS = 2;

    private static final String DATABASE_NAME_EXPRESSION_CONFLICTS = "ConflictsDatabase.db";

    private static final String DATABASE_CREATE_CONFLICTS = String
            .format("create table %s (%s  integer primary key autoincrement, "
                            + "%s text);",
                    TABLE_NAME_CONFLICTS, COLUMN_ID_CONFLICT, COLUMN_URL_CONFLICT);

    private static final String DATABASE_CREATE_BATTLES = String
            .format("create table %s (%s  integer primary key autoincrement, "
                            + "%s text, %s text);",
                    TABLE_NAME_BATTLES, COLUMN_ID_BATTLE, COLUMN_URL_BATTLE, COLUMN_PLACE_BATTLE);

    public ConflictBattleDbOpenHelper(Context context) {
        super(context, String.format(DATABASE_NAME_EXPRESSION_CONFLICTS), null,
                DATABASE_VERSION_CONFLICTS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_BATTLES);
        db.execSQL(DATABASE_CREATE_CONFLICTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME_CONFLICTS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME_BATTLES));
        onCreate(db);
    }
}
