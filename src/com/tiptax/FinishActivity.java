package com.tiptax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class FinishActivity extends ListActivity {

	private class CurrencyConvertTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				ListIterator<Person> pi = persons.listIterator();

				while (pi.hasNext()) {
					Person p = pi.next();
					double amount = p.getDoubleValue();
					converter.setAmount(amount);
					pi.set(new Person(p.getName(), converter.convert(), currency));
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
				adapter.notifyDataSetChanged();
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
	private PersonAdapter adapter;
	private ArrayList<Person> persons;
	private double totalPersonDue, totalTipAndTaxDue;
	private CurrencyConverter converter;
	private SharedPreferences prefs;
	private String currency;

	private ProgressDialog pdg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finish);

		Intent i = getIntent();
		persons = i.getParcelableArrayListExtra("persons");
		totalTipAndTaxDue = i.getDoubleExtra("totalTipAndTaxDue", 0);
		totalPersonDue = i.getDoubleExtra("totalPersonDue", 0);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		currency = prefs.getString("default_currency", "EUR");
		converter = new CurrencyConverter("USD", currency, 0);

		ListIterator<Person> pi = persons.listIterator();

		while (pi.hasNext()) {
			Person p = pi.next();
			double nextVal = p.getDoubleValue();
			pi.set(new Person(p.getName(), (nextVal + (nextVal / totalPersonDue) * totalTipAndTaxDue)));
		}

		adapter = new PersonAdapter(this, R.layout.personrow, persons);
		setListAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.finishmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.currconvert_menu_btn:
			new CurrencyConvertTask().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
