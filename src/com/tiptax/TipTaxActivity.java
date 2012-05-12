package com.tiptax;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TipTaxActivity extends Activity {

	private TableLayout mainTable;
	private Button addPerson;
	private double totalSum, totalSumPeople;
	private double tipPercentage, taxPercentage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		addPerson = (Button) findViewById(R.id.addPerson);
		mainTable = (TableLayout) findViewById(R.id.mainTable);
		tipPercentage = 20;
		taxPercentage = 15;

		addPerson.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// create the dialog box
				final Dialog dialog = new Dialog(TipTaxActivity.this);

				totalSum=0;

				dialog.setContentView(R.layout.add_person);
				dialog.setTitle("Add you desired Item");

				dialog.show();

				final EditText name = (EditText) dialog
						.findViewById(R.id.namePerson);
				final EditText totalPerson = (EditText) dialog
						.findViewById(R.id.totalSpent);
				final TextView totalDue = (TextView) findViewById(R.id.TotalDueText);
				final TextView tipDue = (TextView) findViewById(R.id.tipInput);
				final TextView taxDue = (TextView) findViewById(R.id.taxInput);
				Button okInput = (Button) dialog.findViewById(R.id.OKinput);

				okInput.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if(!totalPerson.getText().toString().equals("")){//safety of no input
							
							//add a new row to the table
							TableRow tr1 = new TableRow(TipTaxActivity.this);
							TextView textviewName = new TextView(
									TipTaxActivity.this);
							textviewName.setText(name.getText().toString());
							textviewName.setTextSize(25);
							textviewName.setGravity(Gravity.LEFT);
							textviewName.setPadding(10, 10, 10, 10);
							TextView textviewTotal = new TextView(
									TipTaxActivity.this);
							textviewTotal.setText(totalPerson.getText().toString());
							textviewTotal.setTextSize(25);
							textviewTotal.setGravity(Gravity.RIGHT);
							textviewName.setPadding(10, 10, 10, 10);
							tr1.addView(textviewName);
							tr1.addView(textviewTotal);
							mainTable.addView(tr1);



							//calculations to get the total sum
							totalSumPeople += Double.parseDouble(totalPerson.getText()
									.toString());


							double tipToPay = tipPercentage*totalSumPeople/100;
							double taxToPay = taxPercentage*totalSumPeople/100;

							totalSum = totalSumPeople + taxToPay;
							totalSum += tipToPay;
							
							//Rounding two Decimal point
							DecimalFormat twoDForm = new DecimalFormat("#.##");
							totalSum = Double.valueOf(twoDForm.format(totalSum));
							tipToPay = Double.valueOf(twoDForm.format(tipToPay));
							taxToPay = Double.valueOf(twoDForm.format(taxToPay));

							//sets the values to the textEdits
							totalDue.setText(Double.toString(totalSum));
							tipDue.setText(Double.toString(tipToPay));
							taxDue.setText(Double.toString(taxToPay));
						}
					}
				});



			}
		});

	}

}
