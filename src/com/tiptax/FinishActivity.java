package com.tiptax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class FinishActivity extends ListActivity {

	private class CurrencyConvertTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				ListIterator<Person> pi = personList.listIterator();

				while (pi.hasNext()) {
					Person p = pi.next();
					double amount = p.getDoubleValue();
					converter.setAmount(amount);
					pi.set(new Person(p.getName(), converter.convert(), currencyTo));
				}

			} catch (IOException e) {
				Log.e("CurrencyConvertTask", "IO Error! " + e);
				return false;
			} catch (JSONException e) {
				Log.e("CurrencyConvertTask", "JSON Error! " + e);
				return false;
			}
			return true;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				personAdapter.notifyDataSetChanged();
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Something went wrong getting currency convertion data. Please check your internet connection!", Toast.LENGTH_LONG);
				toast.show();
			}
			pdg.dismiss();
		}

		@Override
		protected void onPreExecute() {
			pdg = ProgressDialog.show(FinishActivity.this, "Please wait...", "Converting Currencies!", true);
		}
	}

	private PersonAdapter personAdapter;
	private ArrayList<Person> personList;
	private double totalPersonDue, totalTipAndTaxDue;
	private CurrencyConverter converter;
	private SharedPreferences prefs;
	private String currencyTo, currencyFrom;

	private ProgressDialog pdg;

	@Override
	public void onPause() {
		super.onPause();

		if (pdg != null) {
			pdg.dismiss(); // without this line, everything goes to shit!
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finish);

		// Handling intent
		Intent i = getIntent();
		personList = i.getParcelableArrayListExtra("persons");
		totalTipAndTaxDue = i.getDoubleExtra("totalTipAndTaxDue", 0);
		totalPersonDue = i.getDoubleExtra("totalPersonDue", 0);

		// Getting the currencies and converter
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Getting the default currency
		currencyFrom = prefs.getString("default_currency", "USD");

		// Filling up the list of persons with their respective values
		ListIterator<Person> pi = personList.listIterator();

		while (pi.hasNext()) {
			Person p = pi.next();
			double nextVal = p.getDoubleValue();
			pi.set(new Person(p.getName(), (nextVal + (nextVal / totalPersonDue) * totalTipAndTaxDue), p.getCurrency()));
		}

		personAdapter = new PersonAdapter(this, R.layout.personrow, personList);
		setListAdapter(personAdapter);

		converter = new CurrencyConverter(FinishActivity.this, currencyFrom, currencyTo, 0);

	}

	/*
	 * Called when pressing the "convert to" button
	 */
	public void convertCurrencyClick(View V) {

		final String[] currencyArrayNames = getResources().getStringArray(R.array.defaultCurrenciesNames);
		final String[] currencyArrayValues = getResources().getStringArray(R.array.defaultCurrenciesValues);

		// Lets make a dialog box
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a currency to convert to");
		builder.setItems(currencyArrayNames, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				currencyTo = currencyArrayValues[item];
				converter.setFrom(currencyFrom);
				converter.setTo(currencyTo);
				
				// Run the thread only if we're actually converting to a new currency
				if (!currencyFrom.equals(currencyTo)) {
					new CurrencyConvertTask().execute();
				}
				currencyFrom = currencyTo; // this allows new conversion on same
											// interface.
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
