package com.tiptax;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ListIterator;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FinishActivity extends ListActivity {

	private ArrayList<Person> persons;
	private PersonAdapter adapter;
	private double totalTipAndTaxDue;
	private double totalPersonDue;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finish);
		
		Intent i = getIntent();
		persons = i.getParcelableArrayListExtra("persons");
		totalTipAndTaxDue = i.getDoubleExtra("totalTipAndTaxDue", 0);
		totalPersonDue = i.getDoubleExtra("totalPersonDue", 0);

		DecimalFormat twoDForm = new DecimalFormat("#.##");
		ListIterator<Person> pi = persons.listIterator();

		while (pi.hasNext()) {
			Person p = pi.next();
			double nextVal = p.getDoubleValue();
			pi.set(new Person(p.getName(), twoDForm.format((nextVal + (nextVal / totalPersonDue) * totalTipAndTaxDue))));
		}

		adapter = new PersonAdapter(this, R.layout.personrow, persons);
		setListAdapter(adapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

}
