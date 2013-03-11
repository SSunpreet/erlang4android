package com.ernovation.erlangforandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LibsOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 5;
	private static final String DATABASE_NAME = "libraries";
	private static final String LIBRARIES_TABLE_NAME = "libraries";
	private static final String LIBRARIES_TABLE_CREATE = "CREATE TABLE "
			+ LIBRARIES_TABLE_NAME
			+ " ("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "name TEXT, "
			+ "repository INTEGER, "
			+ "summary TEXT, "
			+ "abstract TEXT, "
			+ "keywords TEXT, "
			+ "author TEXT, "
			+ "packager TEXT, "
			+ "category TEXT, "
			+ "depends TEXT, "
			+ "home TEXT, "
			+ "url TEXT, "
			+ "version TEXT, "
			+ "date TEXT, "
			+ "state INTEGER, "
			+ "CONSTRAINT repoconstr FOREIGN KEY (repository) REFERENCES repositories (_id) ON DELETE CASCADE"
			+ ");";

	private static final String REPOSITORIES_TABLE_NAME = "repositories";
	private static final String REPOSITORIES_TABLE_CREATE = "CREATE TABLE "
			+ REPOSITORIES_TABLE_NAME + " ("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
			+ "url TEXT " + ");";

	private static final String DEFAULT_REPO_NAME = "Ernovation";
	private static final String DEFAULT_REPO_URL = "http://erlang.ernovation.com/files/repo/";

	public LibsOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(REPOSITORIES_TABLE_CREATE);
		db.execSQL(LIBRARIES_TABLE_CREATE);
		db.execSQL("INSERT INTO " + REPOSITORIES_TABLE_NAME
				+ " (name, url) VALUES (\"" + DEFAULT_REPO_NAME + "\",\""
				+ DEFAULT_REPO_URL + "\");");
		db.execSQL("CREATE UNIQUE INDEX lib_repo_version_idx ON "
				+ LIBRARIES_TABLE_NAME + "(name, repository, version)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			db.execSQL("DROP TABLE " + LIBRARIES_TABLE_NAME);
			db.execSQL("DROP TABLE " + REPOSITORIES_TABLE_NAME);
			onCreate(db);
		}
	}

}
