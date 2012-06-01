package com.tiptax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrencyConverter {

	private static final String HOST = "http://www.google.com/ig/calculator?";
	private static final HttpClient httpclient = new DefaultHttpClient();
	private final String from;
	private final String to;
	private final double amount;

	public CurrencyConverter(String from, String to, double amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	private HttpPost makeQuery() {
		String query = HOST + "hl=en&q=" + amount + from + "%3D%3F" + to;
		return new HttpPost(query);
	}

	public double convert() throws IllegalStateException, IOException, JSONException {

		HttpResponse response = httpclient.execute(makeQuery());
		HttpEntity entity = response.getEntity();
		String stripedRes = null;

		if (entity != null) {

			InputStream instream = entity.getContent();
			String result = isToString(instream);
			instream.close();

			JSONObject json = new JSONObject(result);
			// Google returns the currency name, so we need to get rid of that
			stripedRes = json.getString("rhs").replaceAll("[^\\d.]", "");

		}
		return Double.parseDouble(stripedRes);
	}

	/*
	 * Converts an inputStream into a String.
	 */
	private String isToString(InputStream is) throws IOException {
		String line = "";
		StringBuilder total = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		while ((line = rd.readLine()) != null) {
			total.append(line);
		}

		return total.toString();
	}

}
