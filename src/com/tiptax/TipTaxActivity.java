package com.tiptax;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TipTaxActivity extends ListActivity implements OnSharedPreferenceChangeListener {

	private static final int REQCODE = 0;

	private ArrayList<Person> persons;
	private PersonAdapter adapter;
	private TextView totalDue, tipDue, taxDue;
	private double tipPercentage;

	private SharedPreferences prefs;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		updateDefaultValues();

		totalDue = (TextView) findViewById(R.id.TotalDueText);
		tipDue = (TextView) findViewById(R.id.tipInput);
		taxDue = (TextView) findViewById(R.id.taxInput);
		persons = new ArrayList<Person>();
		adapter = new PersonAdapter(this, R.layout.personrow, persons);
		setListAdapter(adapter);
	}

	public void taxInputClick(View v) {
		final Dialog taxInputDialog = new Dialog(TipTaxActivity.this);
		taxInputDialog.setContentView(R.layout.taxedit);
		taxInputDialog.setTitle("Change tax");

		Button taxOKButton = (Button) taxInputDialog.findViewById(R.id.taxOkButton);
		taxOKButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				EditText taxTotal = (EditText) taxInputDialog.findViewById(R.id.taxValueEditText);
				if (!taxTotal.getText().toString().equals("")) {
					taxDue.setText(taxTotal.getText().toString());
					updateTotalAndTipValues(totalSumPeople(), tipPercentage);
				}
				taxInputDialog.dismiss();
			}
		});
		taxInputDialog.show();
	}

	public void addPersonClick(View v) {
		Intent i = new Intent(this, AddPersonActivity.class);
		startActivityForResult(i, REQCODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			String name = data.getStringExtra("name");
			String value = data.getStringExtra("value");
			persons.add(new Person(name, value));
			adapter.notifyDataSetChanged();
			updateTotalAndTipValues(totalSumPeople(), tipPercentage);
		}
	}

	private double totalSumPeople() {
		double sum = 0;
		for (Person p : persons) {
			sum += Double.valueOf(p.getValue());
		}
		return sum;
	}

	private void updateTotalAndTipValues(double totalSumPeople, double tipPercentage) {
		// Rounding two Decimal point
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		Double taxValue = Double.parseDouble(taxDue.getText().toString());
		double result = totalSumPeople + taxValue + (tipPercentage * totalSumPeople / 100.0);
		result = Double.valueOf(twoDForm.format(result));
		tipDue.setText(Double.toString(Double.valueOf(twoDForm.format(tipPercentage * totalSumPeople / 100.0))));
		totalDue.setText(Double.toString(result));
	}

	private void updateDefaultValues() {
		tipPercentage = Double.valueOf(prefs.getString("default_currency", "15"));
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		updateDefaultValues();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tiptaxmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.pref_menu_btn:
			Intent i = new Intent(TipTaxActivity.this, TipTaxPreferences.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// public void clickingonarow(){
	// public void onClick(View v) {
	// final Dialog dialog = new Dialog(TipTaxActivity.this);
	//
	// dialog.setContentView(R.layout.add_person);
	// dialog.setTitle("Change values");
	//
	// final EditText nameDialog = (EditText)
	// dialog.findViewById(R.id.namePerson);
	// final EditText totalDialog = (EditText)
	// dialog.findViewById(R.id.totalSpent);
	// final Button okDialog = (Button) dialog.findViewById(R.id.OKinput);
	//
	// nameDialog.setText(nameDialogText);
	// totalDialog.setText(sumDialogText);
	//
	// dialog.show();
	//
	// okDialog.setOnClickListener(new View.OnClickListener() {
	//
	// public void onClick(View v) {
	//
	// dialog.dismiss();
	// if (!nameDialog.getText().toString().equals("")) {
	// // substract ols value and adds
	// // the new one
	// totalSumPeople -= Double.parseDouble(sumDialogText);
	// totalSumPeople += Double.parseDouble(totalDialog.getText().toString());
	//
	// nameOfRowEditText.setText(nameDialog.getText().toString());
	// sumOfRowEditText.setText(totalDialog.getText().toString());
	//
	// updateTotalAndTipValues(totalSumPeople, tipPercentage);
	// }
	// }
	// });
	// }

}
