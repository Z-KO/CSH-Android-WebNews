package edu.csh.cshwebnews.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WebNewsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "webnews.db";

    public WebNewsDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + WebNewsContract.UserEntry.TABLE_NAME
                + " (" + WebNewsContract.UserEntry._ID + " INTEGER, " +
                WebNewsContract.UserEntry.DISPLAY_NAME + " TEXT, " +
                WebNewsContract.UserEntry.EMAIL + " TEXT, " +
                WebNewsContract.UserEntry.CREATED_AT + " TEXT, " +
                WebNewsContract.UserEntry.IS_ADMIN + " TEXT);";

        final String SQL_CREATE_NEWSGROUP_TABLE = "CREATE TABLE " + WebNewsContract.NewsGroupEntry.TABLE_NAME
                + " ("+ WebNewsContract.NewsGroupEntry._ID + " INTEGER PRIMARY KEY," +
                WebNewsContract.NewsGroupEntry.DESCRIPTION + " TEXT NOT NULL, " +
                WebNewsContract.NewsGroupEntry.MAX_UNREAD_LEVEL + " INTEGER NOT NULL, " +
                WebNewsContract.NewsGroupEntry.NAME + " TEXT UNIQUE NOT NULL, " +
                WebNewsContract.NewsGroupEntry.NEWEST_POST_AT + " TEXT NOT NULL, " +
                WebNewsContract.NewsGroupEntry.OLDEST_POST_AT + " TEXT NOT NULL, " +
                WebNewsContract.NewsGroupEntry.POSTING_ALLOWED + " TEXT NOT NULL, " +
                WebNewsContract.NewsGroupEntry.UNREAD_COUNT + " TEXT NOT NULL);";

        final String SQL_CREATE_POST_TABLE = "CREATE TABLE " + WebNewsContract.PostEntry.TABLE_NAME + "(" +
                WebNewsContract.PostEntry._ID + " INTEGER PRIMARY KEY," +
                WebNewsContract.PostEntry.ANCESTOR_IDS + " TEXT, " +
                WebNewsContract.PostEntry.AUTHOR_KEY + " INTEGER NOT NULL, " +
                WebNewsContract.PostEntry.BODY + " TEXT NOT NULL, " +
                WebNewsContract.PostEntry.CREATED_AT + " TEXT NOT NULL, " +
                WebNewsContract.PostEntry.FOLLOWUP_NEWSGROUP_ID + " INTEGER, " +
                WebNewsContract.PostEntry.HAD_ATTACHMENTS + " TEXT NOT NULL, " +
                WebNewsContract.PostEntry.HEADERS + " TEXT NOT NULL, " +
                WebNewsContract.PostEntry.IS_DETHREADED + " TEXT NOT NULL, " +
                WebNewsContract.PostEntry.IS_STARRED + " TEXT NOT NULL, " +
                WebNewsContract.PostEntry.MESSAGE_ID + " TEXT NOT NULL, " +
                WebNewsContract.PostEntry.PERSONAL_LEVEL + " INTEGER NOT NULL, " +
                WebNewsContract.PostEntry.IS_STICKIED + " INTEGER NOT NULL, " +
                WebNewsContract.PostEntry.SUBJECT + " TEXT NOT NULL, " +
                WebNewsContract.PostEntry.NEWSGROUP_IDS + " TEXT NOT NULL, " +
                WebNewsContract.PostEntry.TOTAL_STARS + " INTEGER NOT NULL, " +
                WebNewsContract.PostEntry.CHILD_IDS + " TEXT, " +
                WebNewsContract.PostEntry.DESCENDANT_IDS + " TEXT, " +
                WebNewsContract.PostEntry.UNREAD_CLASS + " String);";

        final String SQL_CREATE_AUTHOR_TABLE = "CREATE TABLE " + WebNewsContract.AuthorEntry.TABLE_NAME +
                " (" + WebNewsContract.AuthorEntry._ID + " INTEGER PRIMARY KEY," +
                WebNewsContract.AuthorEntry.NAME + " TEXT NOT NULL, " +
                WebNewsContract.AuthorEntry.EMAIL + " TEXT NOT NULL, " +
                WebNewsContract.AuthorEntry.RAW + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + WebNewsContract.AuthorEntry._ID + ") REFERENCES " +
                WebNewsContract.PostEntry.TABLE_NAME + " (" + WebNewsContract.PostEntry.AUTHOR_KEY +
                "), " + " UNIQUE (" + WebNewsContract.AuthorEntry.NAME + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_NEWSGROUP_TABLE);
        db.execSQL(SQL_CREATE_POST_TABLE);
        db.execSQL(SQL_CREATE_AUTHOR_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WebNewsContract.NewsGroupEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WebNewsContract.PostEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WebNewsContract.UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WebNewsContract.AuthorEntry.TABLE_NAME);
        onCreate(db);
    }
}