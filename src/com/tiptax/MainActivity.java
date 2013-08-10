package com.tiptax;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends FragmentActivity implements OnSharedPreferenceChangeListener, MainFragment.MainListSelectedListener {


    private SharedPreferences prefs;
    private double tipPercentage, tax, tip, total;

    private static final int EDITREQ = 1;
    private static final int NEWREQ = 0;
    private NumberFormat numberFormat;
    private List<Person> mainPersonList;
    private PersonAdapter mainPersonAdapter;
    private List<Person> finishPersonList;
    private PersonAdapter finishPersonAdapter;

    private CurrencyConverter converter;
    private String currencyTo, currencyFrom;

    private ProgressDialog pdg;

    private class CurrencyConvertTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                ListIterator<Person> pi = finishPersonList.listIterator();

                while (pi.hasNext()) {
                    Person p = pi.next();
                    double amount = p.getDoubleValue();
                    converter.setAmount(amount);
                    pi.set(new Person(p.getName(), converter.convert(), currencyTo));
                }

            } catch (IOException e) {
                Log.e("CurrencyConvertTask", "IO Error! " + e);
                return false;
            } catch (JSONException e) {
                Log.e("CurrencyConvertTask", "JSON Error! " + e);
                return false;
            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                finishPersonAdapter.notifyDataSetChanged();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Something went wrong getting currency convertion data. Please check your internet connection!", Toast.LENGTH_LONG);
                toast.show();
            }
            pdg.dismiss();
        }

        @Override
        protected void onPreExecute() {
            pdg = ProgressDialog.show(MainActivity.this, "Please wait...", "Converting Currencies!", true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (pdg != null) {
            pdg.dismiss(); // without this line, everything goes to shit!
        }
    }

    public void onListItemClick(View v, int pos) {
        Intent i = new Intent(v.getContext(), AddPersonActivity.class);
        i.putExtra("name", mainPersonList.get(pos).getName());
        i.putExtra("value", mainPersonList.get(pos).getValue());
        i.putExtra("origPos", pos);
        startActivityForResult(i, EDITREQ);
    }

    /*
     * Formats a the TIP string.
     */
    private String formattedTipPctLabel() {
        return getResources().getString(R.string.tip) + " (" + tipPercentage + "%)";
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Set up preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Set up the numberformatter
        numberFormat = NumberFormat.getCurrencyInstance();
        Currency c = Currency.getInstance(prefs.getString("default_currency", "USD"));
        numberFormat.setMaximumFractionDigits(c.getDefaultFractionDigits());
        numberFormat.setCurrency(c);

        // Finish stuff
        currencyFrom = prefs.getString("default_currency", "USD");
        converter = new CurrencyConverter(MainActivity.this, currencyFrom, currencyTo, 0);

        mainPersonList = new ArrayList<Person>();
        mainPersonAdapter = new PersonAdapter(this, R.layout.personrow, mainPersonList);
        finishPersonList = new ArrayList<Person>();
        finishPersonAdapter = new PersonAdapter(this, R.layout.personrow, finishPersonList);

        updateDefaultValues();

        ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
        pager.setAdapter(new PagerAdapter(getApplicationContext(), getSupportFragmentManager()));

        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    updateFinishPage();
                }
            }
        });

    }

    private TextView getTotalDue() {
        return (TextView) findViewById(R.id.TotalDueText);
    }

    private TextView getTipDue() {
        return (TextView) findViewById(R.id.tipInput);
    }

    private TextView getTaxDue() {
        return (TextView) findViewById(R.id.taxInput);
    }

    private TextView getTipLabel() {
        return (TextView) findViewById(R.id.tipText);
    }

    public PersonAdapter getMainPersonAdapter() {
        return mainPersonAdapter;
    }

    public PersonAdapter getFinishPersonAdapter() {
        return finishPersonAdapter;
    }

    public void updateFinishPage() {
        ListIterator<Person> pi = mainPersonList.listIterator();
        finishPersonList.clear();

        while (pi.hasNext()) {
            Person p = pi.next();
            double nextVal = p.getDoubleValue();
            finishPersonList.add(new Person(p.getName(), (nextVal + (nextVal / totalSumPeople()) * (tax + tip)), p.getCurrency()));
        }

        finishPersonAdapter.notifyDataSetChanged();

        currencyFrom = prefs.getString("default_currency", "USD"); // needs to be done if we changed currencyFrom while playing about
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tiptaxmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pref_menu_btn:
                Intent i = new Intent(MainActivity.this, TipTaxPreferences.class);
                startActivity(i);
                return true;
            case R.id.reset_menu_btn:
                resetAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Run if the preferences are changed!
     */
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        updateDefaultValues();
        updateCurrency();
        updateTotalAndTipValues();
    }

    /*
     * Resets all the fields.
     */
    public void resetAll() {
        mainPersonList.clear();
        getTotalDue().setText("0");
        getTaxDue().setText("0");
        getTipDue().setText("0");
        tax = tip = total = 0;

        mainPersonAdapter.notifyDataSetChanged();
    }

    /*
     * Run when the tax input is clicked
     */
    public void taxInputClick(View v) {
        final EditText taxTotal = new EditText(this);
        taxTotal.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        taxTotal.setHint(R.string.tt_spent);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change tax");
        builder.setView(taxTotal);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (!taxTotal.getText().toString().isEmpty()) {
                    try {
                        tax = Double.parseDouble(taxTotal.getText().toString());
                    } catch (NumberFormatException nfe) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.no_number, Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    getTaxDue().setText(numberFormat.format(tax));
                    updateTotalAndTipValues();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /*
     * Returns the total amount by adding up what each person owes
     */
    private double totalSumPeople() {
        double sum = 0;
        for (Person p : mainPersonList) {
            sum += p.getDoubleValue();
        }
        return sum;
    }

    /*
     * Gets the preferences for tip and currency and updates the local variables
     */
    private void updateDefaultValues() {
        Currency c = Currency.getInstance(prefs.getString("default_currency", "USD"));
        numberFormat.setMaximumFractionDigits(c.getDefaultFractionDigits());
        numberFormat.setCurrency(c);

        tipPercentage = Double.parseDouble(prefs.getString("default_tip", "15"));
        if (getTipLabel() != null) {
            getTipLabel().setText(formattedTipPctLabel());
        }
    }

    /*
     * Updates the number formatting to the new currency
     */
    public void updateCurrency() {

        ListIterator<Person> pi = mainPersonList.listIterator();

        while (pi.hasNext()) {
            Person p = pi.next();
            pi.set(new Person(p.getName(), p.getDoubleValue(), prefs.getString("default_currency", "USD")));
        }
        mainPersonAdapter.notifyDataSetChanged();

        getTaxDue().setText(numberFormat.format(tax));
        getTipDue().setText(numberFormat.format(tip));
        getTotalDue().setText(numberFormat.format(total));

    }

    /*
     * Overloaded method
     */
    public void updateTotalAndTipValues() {
        updateTotalAndTipValues(totalSumPeople(), tipPercentage);
    }

    /*
     * Updates the textviews for Total and Tip due
     */
    private void updateTotalAndTipValues(double totalSumPeople, double tipPercentage) {
        tip = tipPercentage * totalSumPeople / 100.0;
        total = totalSumPeople + tax + (tipPercentage * totalSumPeople / 100.0);
        getTipDue().setText(numberFormat.format(tip));
        getTotalDue().setText(numberFormat.format(total));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            String name = data.getStringExtra("name");
            String value = data.getStringExtra("value");

            switch (requestCode) {
                case NEWREQ:
                    mainPersonList.add(new Person(name, value));
                    updateCurrency();
                    break;
                case EDITREQ:
                    int pos = data.getIntExtra("origPos", -1);
                    if (pos != -1) {
                        Person p = mainPersonList.get(pos);
                        p.setName(name);
                        p.setValue(value);
                    }
                    break;
            }

            mainPersonAdapter.notifyDataSetChanged();
            updateTotalAndTipValues();
        }
    }

    public boolean onContextItemSelected(int id) {
        mainPersonList.remove(id);
        finishPersonList.remove(id);
        mainPersonAdapter.notifyDataSetChanged();
        finishPersonAdapter.notifyDataSetChanged();

        updateTotalAndTipValues();

        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, int pos) {
        menu.setHeaderTitle(mainPersonList.get(pos).getName());
        menu.add(Menu.NONE, 0, 0, R.string.delete);
    }

    /*
     * Onclick for pressing add a Person
     */
    public void addPersonClick(View v) {
        Intent i = new Intent(v.getContext(), AddPersonActivity.class);
        startActivityForResult(i, NEWREQ);
    }

    /*
     * Called when pressing the "convert to" button
     */
    public void convertCurrencyClick(View V) {
        final String[] currencyArrayNames = getResources().getStringArray(R.array.defaultCurrenciesNames);
        final String[] currencyArrayValues = getResources().getStringArray(R.array.defaultCurrenciesValues);

        // Lets make a dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a currency to convert to");
        builder.setItems(currencyArrayNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                currencyTo = currencyArrayValues[item];
                converter.setFrom(currencyFrom);
                converter.setTo(currencyTo);

                // Run the thread only if we're actually converting to a new currency
                if (!currencyFrom.equals(currencyTo)) {
                    new CurrencyConvertTask().execute();
                }
                currencyFrom = currencyTo; // this allows new conversion on same interface.
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

}
