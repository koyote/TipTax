package com.tiptax;

import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FinishActivity extends ListActivity {
	
	private ArrayList<Person> persons;

	ArrayList<String> names;
	ArrayList<String> values;
	private PersonAdapter adapter;
	
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finish);
		Intent i = getIntent();
		persons = new ArrayList<Person>();
		names = i.getStringArrayListExtra("names");
		values = i.getStringArrayListExtra("values");
		
		for(int n =0 ; n < names.size() ; n++){
			persons.add(new Person(names.get(n),values.get(n)));
		}
		
		adapter = new PersonAdapter(this, R.layout.personrow, persons);
		setListAdapter(adapter);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

}
