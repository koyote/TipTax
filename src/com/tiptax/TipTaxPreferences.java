package com.tiptax;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class TipTaxPreferences extends PreferenceActivity {
	
	
	private Button customizeCurrenciesButton;
	private Spinner defaultTip;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        
        defaultTip = (Spinner) findViewById(R.id.defaultTipSpinner);
        customizeCurrenciesButton = (Button) findViewById(R.id.customizeCurrencies);
        
        populateSpinners();
        
        customizeCurrenciesButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
        
        
        
    }
    
    
    
  //fills the spinner with its values
  	private void populateSpinners() {

  		ArrayAdapter<CharSequence> fAdapter = ArrayAdapter.createFromResource(
  				this, R.array.defaultTipValues,
  				android.R.layout.simple_spinner_item);
  		int spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
  		fAdapter.setDropDownViewResource(spinner_dd_item);
  		defaultTip.setAdapter(fAdapter);

  	}

}