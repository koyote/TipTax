package com.tiptax;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TipTaxActivity extends ListActivity {

	private static final int REQCODE = 0;

	private ArrayList<Person> persons;
	private PersonAdapter adapter;
	private TextView totalDue, tipDue, taxDue;
	private double tipPercentage = 20;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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
