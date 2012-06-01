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
import android.widget.ListView;
import android.widget.TextView;

public class TipTaxActivity extends ListActivity implements OnSharedPreferenceChangeListener {

	private static final int NEWREQ = 0;
	private static final int EDITREQ = 1;

	private ArrayList<Person> persons;
	private PersonAdapter adapter;
	private TextView totalDue, tipDue, taxDue;
	private double tipPercentage;

	private SharedPreferences prefs;

	public void addPersonClick(View v) {
		Intent i = new Intent(this, AddPersonActivity.class);
		startActivityForResult(i, NEWREQ);
	}

	public void finishClick(View v) {

		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();

		for (int i = 0; i < persons.size(); i++) {
			names.add(persons.get(i).getName());
			values.add(persons.get(i).getValue());
			System.out.println("Added: " + persons.get(i).getName());
		}
		
		double totalPersonDue = 0;
		for(int i =0 ; i < values.size();i++){
			totalPersonDue += Double.parseDouble(values.get(i));
		}
		double totalTipAndTaxDue = Double.parseDouble(tipDue.getText().toString()) + Double.parseDouble(taxDue.getText().toString());
		
		for(int i =0 ; i <values.size(); i++){
			values.set(i, Double.toString(Double.parseDouble(values.get(i))+((Double.parseDouble(values.get(i))/totalPersonDue)*totalTipAndTaxDue)));
		}

		Intent i = new Intent(this, FinishActivity.class);

		i.putStringArrayListExtra("names", names);
		i.putStringArrayListExtra("values", values);

		startActivity(i);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {

			String name = data.getStringExtra("name");
			String value = data.getStringExtra("value");

			switch (requestCode) {
			case NEWREQ:
				persons.add(new Person(name, value));
				break;
			case EDITREQ:
				int pos = data.getIntExtra("origPos", -1);
				if (pos != -1) {
					Person p = persons.get(pos);
					p.setName(name);
					p.setValue(value);
				}
				break;
			}

			adapter.notifyDataSetChanged();
			updateTotalAndTipValues(totalSumPeople(), tipPercentage);
		}
	}

	@Override
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tiptaxmenu, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(v.getContext(), AddPersonActivity.class);
		i.putExtra("name", persons.get(position).getName());
		i.putExtra("value", persons.get(position).getValue());
		i.putExtra("origPos", position);
		startActivityForResult(i, EDITREQ);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.pref_menu_btn:
			Intent i = new Intent(TipTaxActivity.this, TipTaxPreferences.class);
			startActivity(i);
			return true;
		case R.id.reset_menu_btn:
			resetAll();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		updateDefaultValues();
	}

	public void resetAll(){
		
		persons.clear();
		totalDue.setText("0");
		taxDue.setText("0");
		tipDue.setText("0");
		adapter.notifyDataSetChanged();
		
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

	private double totalSumPeople() {
		double sum = 0;
		for (Person p : persons) {
			sum += Double.valueOf(p.getValue());
		}
		return sum;
	}

	private void updateDefaultValues() {
		tipPercentage = Double.valueOf(prefs.getString("default_currency", "15"));
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

}
