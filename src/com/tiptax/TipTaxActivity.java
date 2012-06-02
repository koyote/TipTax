package com.tiptax;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class TipTaxActivity extends ListActivity implements OnSharedPreferenceChangeListener {

	private static final int EDITREQ = 1;
	private static final int NEWREQ = 0;

	private PersonAdapter adapter;
	private ArrayList<Person> persons;
	private SharedPreferences prefs;

	private double tipPercentage, tax, tip, total;
	private TextView totalDue, tipDue, taxDue, tipLabel;
	private NumberFormat nf;

	public void addPersonClick(View v) {
		Intent i = new Intent(this, AddPersonActivity.class);
		startActivityForResult(i, NEWREQ);
	}

	public void finishClick(View v) {

		Intent i = new Intent(this, FinishActivity.class);

		i.putExtra("persons", persons);
		i.putExtra("totalTipAndTaxDue", tax + tip);
		i.putExtra("totalPersonDue", totalSumPeople());

		startActivity(i);
	}

	private String formattedTipPctLabel() {
		return new String("TIP (" + tipPercentage + "%)");
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
			updateTotalAndTipValues();
		}
	}

	/*
	 * Called when pressing delete on an item.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int id = (int) getListAdapter().getItemId(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);

		persons.remove(id);
		adapter.notifyDataSetChanged();
		updateTotalAndTipValues();

		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		registerForContextMenu(this.getListView());

		totalDue = (TextView) findViewById(R.id.TotalDueText);
		tipDue = (TextView) findViewById(R.id.tipInput);
		taxDue = (TextView) findViewById(R.id.taxInput);
		tipLabel = (TextView) findViewById(R.id.tipText);

		nf = NumberFormat.getCurrencyInstance();
		Currency c = Currency.getInstance(prefs.getString("default_currency", "USD"));
		nf.setMaximumFractionDigits(c.getDefaultFractionDigits());
		nf.setCurrency(c);

		updateDefaultValues();

		persons = new ArrayList<Person>();
		adapter = new PersonAdapter(this, R.layout.personrow, persons);
		setListAdapter(adapter);
	}

	/*
	 * Context menu setup for the delete command.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		menu.setHeaderTitle(persons.get(info.position).getName());
		menu.add(Menu.NONE, 0, 0, R.string.delete);
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

	public void resetAll() {
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
					tax = Float.parseFloat(taxTotal.getText().toString());
					taxDue.setText(nf.format(tax));
					updateTotalAndTipValues();
				}
				taxInputDialog.dismiss();
			}
		});
		taxInputDialog.show();
	}

	private double totalSumPeople() {
		double sum = 0;
		for (Person p : persons) {
			sum += p.getDoubleValue();
		}
		return sum;
	}

	private void updateDefaultValues() {
		Currency c = Currency.getInstance(prefs.getString("default_currency", "USD"));
		nf.setMaximumFractionDigits(c.getDefaultFractionDigits());
		nf.setCurrency(c);

		tipPercentage = Float.valueOf(prefs.getString("default_tip", "15"));
		if (tipLabel != null) {
			tipLabel.setText(formattedTipPctLabel());
		}
	}

	private void updateTotalAndTipValues() {
		updateTotalAndTipValues(totalSumPeople(), tipPercentage);
	}

	private void updateTotalAndTipValues(double totalSumPeople, double tipPercentage) {
		tip = tipPercentage * totalSumPeople / 100.0;
		total = totalSumPeople + tax + (tipPercentage * totalSumPeople / 100.0);
		tipDue.setText(nf.format(tip));
		totalDue.setText(nf.format(total));
	}

}
