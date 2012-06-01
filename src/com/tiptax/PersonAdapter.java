package com.tiptax;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PersonAdapter extends ArrayAdapter<Person> {
	private ArrayList<Person> persons;

	public PersonAdapter(Context context, int resourceID, ArrayList<Person> persons) {
		super(context, resourceID, persons);
		this.persons = persons;
	}

	/*
	 * Returns a numberformat which formats a number to a given currency.
	 */
	public NumberFormat formatCurrencyCode(String currency) {
		NumberFormat f = NumberFormat.getCurrencyInstance();
		Currency c = Currency.getInstance(currency);
		f.setMaximumFractionDigits(c.getDefaultFractionDigits());
		f.setCurrency(c);
		return f;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.personrow, null);
		}
		Person p = persons.get(position);
		if (p != null) {
			TextView pn = (TextView) v.findViewById(R.id.personName);
			TextView pv = (TextView) v.findViewById(R.id.personValue);
			if (pn != null) {
				pn.setText(p.getName());
			}
			if (pv != null) {
				pv.setText(formatCurrencyCode(p.getCurrency()).format(p.getDoubleValue()));
			}
		}
		return v;
	}
}
