package com.tiptax;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class CurrencyDBWrapper {
	private SQLiteDatabase db;
	private CurrencyDB dbHelper;
	private String[] columns = { CurrencyDB.COLUMN_ID, CurrencyDB.CURRENCIES_COLUMN_CURRENCY, // 1
			CurrencyDB.CURRENCIES_COLUMN_RATE }; // 2

	private static final int CURRENCY_IDX = 1;
	private static final int RATE_IDX = 2;


	/*
	 * Constructor creates a new reference to the DB Helper
	 */
	public CurrencyDBWrapper(Context context) {
		dbHelper = new CurrencyDB(context);
	}

	/*
	 * Sets the current DB to a writable reference of the DB
	 */
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	/*
	 * Closes the DB reference
	 */
	public void close() {
		dbHelper.close();
	}


	/*
	 * Adds a Currency to the DB
	 */
	public long addCurrency(String currency, double rate) {
		ContentValues values = new ContentValues();
		values.put(CurrencyDB.CURRENCIES_COLUMN_CURRENCY, currency);
		values.put(CurrencyDB.CURRENCIES_COLUMN_RATE, rate);
		return db.insert(CurrencyDB.CURRENCIES_TABLE, null, values);
	}

	/*
	 * Changes the rate of a given currency
	 */
	public void changeCurrency(long sid, double rate) {
		ContentValues values = new ContentValues();
		values.put(CurrencyDB.CURRENCIES_COLUMN_RATE, rate);
		db.update(CurrencyDB.CURRENCIES_TABLE, values, CurrencyDB.COLUMN_ID + "=" + sid, null);
	}

	/*
	 * Deletes a Currency according to its ID
	 */
	public void deleteItem(long iid) {
		db.delete(CurrencyDB.CURRENCIES_TABLE, CurrencyDB.COLUMN_ID  + " = '" + iid + "'", null);
	}

	/*
	 * Returns an HashMap of all the currencies and their rates
	 */
	public HashMap<String, Double> getAll() {
		HashMap<String, Double> items = new HashMap<String, Double>();

		Cursor cursor = db.query(CurrencyDB.CURRENCIES_TABLE, columns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			items.put(cursor.getString(CURRENCY_IDX), cursor.getDouble(RATE_IDX));
			cursor.moveToNext();
		}

		cursor.close();
		return items;
	}


}
