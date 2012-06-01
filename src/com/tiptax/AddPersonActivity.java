package com.tiptax;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddPersonActivity extends Activity {

	private String name;
	private int origPos;
	private String value;

	public void addPersonOKClick(View v) {
		name = ((EditText) findViewById(R.id.namePersonET)).getText().toString();
		value = ((EditText) findViewById(R.id.valuePersonET)).getText().toString();

		if (name.equals("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter a name!", Toast.LENGTH_LONG);
			toast.show();
			return;
		} else if (value.equals("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter a value!", Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		Intent in = new Intent();
		in.putExtra("name", name);
		in.putExtra("value", value);
		in.putExtra("origPos", origPos);
		setResult(Activity.RESULT_OK, in);
		finish();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_person);
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent i = getIntent();
		name = i.getStringExtra("name");
		value = i.getStringExtra("value");
		origPos = i.getIntExtra("origPos", -1);

		((EditText) findViewById(R.id.namePersonET)).setText(name);
		((EditText) findViewById(R.id.valuePersonET)).setText(value);
	}

}
