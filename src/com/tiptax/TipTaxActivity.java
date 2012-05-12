package com.tiptax;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TipTaxActivity extends Activity {

	private TableLayout mainTable;
	private Button addPerson;
	private TextView totalDue, tipDue, taxDue;
	private double totalSumPeople;
	private double tipPercentage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		addPerson = (Button) findViewById(R.id.addPerson);
		mainTable = (TableLayout) findViewById(R.id.mainTable);
		totalDue = (TextView) findViewById(R.id.TotalDueText);
		tipDue = (TextView) findViewById(R.id.tipInput);
		taxDue = (TextView) findViewById(R.id.taxInput);
		tipPercentage = 20;

		addPerson.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// create the dialog box
				final Dialog dialog = new Dialog(TipTaxActivity.this);

				dialog.setContentView(R.layout.add_person);
				dialog.setTitle("Add you desired Item");

				dialog.show();

				final EditText nameDialog = (EditText) dialog.findViewById(R.id.namePerson);
				final EditText totalDialog = (EditText) dialog.findViewById(R.id.totalSpent);
				final Button okDialog = (Button) dialog.findViewById(R.id.OKinput);

				okDialog.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						// safety if no input
						if (!totalDialog.getText().toString().equals("")) {
							final String nameDialogText = nameDialog.getText().toString();
							final String sumDialogText = totalDialog.getText().toString();

							final TextView nameOfRowEditText = new TextView(TipTaxActivity.this);
							final TextView sumOfRowEditText = new TextView(TipTaxActivity.this);

							nameOfRowEditText.setText(nameDialogText);
							sumOfRowEditText.setText(sumDialogText);
							TableRow newRow = createRow(nameOfRowEditText, sumOfRowEditText);

							mainTable.addView(newRow);

							// Listener for every row added
							newRow.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
									// TODO Auto-generated method stub
									final Dialog dialog = new Dialog(TipTaxActivity.this);

									dialog.setContentView(R.layout.add_person);
									dialog.setTitle("Change values");

									final EditText nameDialog = (EditText) dialog.findViewById(R.id.namePerson);
									final EditText totalDialog = (EditText) dialog.findViewById(R.id.totalSpent);
									final Button okDialog = (Button) dialog.findViewById(R.id.OKinput);

									nameDialog.setText(nameDialogText);
									totalDialog.setText(sumDialogText);

									dialog.show();

									okDialog.setOnClickListener(new View.OnClickListener() {

										public void onClick(View v) {
											// TODO Auto-generated method stub

											dialog.dismiss();

											if (!nameDialog.getText().toString().equals("")) {
												// substract ols value and adds
												// the new one
												totalSumPeople -= Double.parseDouble(sumDialogText);
												totalSumPeople += Double.parseDouble(totalDialog.getText().toString());

												nameOfRowEditText.setText(nameDialog.getText().toString());
												sumOfRowEditText.setText(totalDialog.getText().toString());

												updateTotalAndTipValues(totalSumPeople, tipPercentage);
											}
										}
									});

								}
							});

							// calculations to get the total sum
							totalSumPeople += Double.parseDouble(sumDialogText);

							// updates the total values
							updateTotalAndTipValues(totalSumPeople, tipPercentage);

						}
					}
				});

			}
		});

		// listener to change the taxRow value
		taxDue.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(TipTaxActivity.this);
				dialog.setContentView(R.layout.add_person);
				dialog.setTitle("Change values");

				final EditText name = (EditText) dialog.findViewById(R.id.namePerson);
				name.setText("TAX");

				final EditText taxTotal = (EditText) dialog.findViewById(R.id.totalSpent);
				final Button okButton = (Button) dialog.findViewById(R.id.OKinput);

				dialog.show();

				okButton.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (!taxTotal.getText().toString().equals("")) {
							taxDue.setText(taxTotal.getText().toString());
							updateTotalAndTipValues(totalSumPeople, tipPercentage);
						}
					}
				});
			}
		});

	}

	private TableRow createRow(TextView name, TextView sum) {

		// add a new row to the table
		TableRow tr1 = new TableRow(TipTaxActivity.this);
		name.setTextSize(25);
		name.setGravity(Gravity.LEFT);
		name.setPadding(10, 10, 10, 10);
		sum.setTextSize(25);
		sum.setGravity(Gravity.RIGHT);
		sum.setPadding(10, 10, 10, 10);
		tr1.addView(name);
		tr1.addView(sum);
		return tr1;

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
