package com.tiptax;

import java.util.ArrayList;

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
				pv.setText("$" + p.getValue()); // WE WILL FIND THE CURRENCY FOR
												// THE SETTINGS AT A LATER
												// STAGE!!
			}
		}
		return v;
	}
}
