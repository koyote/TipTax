package com.tiptax;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CurrencyDB extends SQLiteOpenHelper  {

	public static final String CURRENCIES_TABLE = "currencies";
	public static final String CURRENCIES_COLUMN_CURRENCY = "currency";
	public static final String CURRENCIES_COLUMN_RATE = "rate";

	public static final String COLUMN_ID = "_id";

	private static final String DATABASE_NAME = "currencies.db";
	private static final int DATABASE_VERSION = 2;
	private static final String SHOPPING_TABLE_CREATE_CREATED = "create table " + CURRENCIES_TABLE + "( " + COLUMN_ID
			+ " integer primary key autoincrement, " + CURRENCIES_COLUMN_CURRENCY + " TEXT not null, " + CURRENCIES_COLUMN_RATE + " REAL not null);";


	public CurrencyDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SHOPPING_TABLE_CREATE_CREATED);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CURRENCIES_TABLE);
		onCreate(db);
	}
}
