package com.tiptax;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {

	private String name;
	private String value;
	private String currency;

	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
		public Person createFromParcel(Parcel in) {
			return new Person(in);
		}

		public Person[] newArray(int size) {
			return new Person[size];
		}
	};

	public Person(Parcel in) {
		readFromParcel(in);
	}

	public Person(String name, double value) {
		this.name = name;
		this.value = String.valueOf(value);
		this.currency = "USD";
	}

	public Person(String name, String value) {
		this.name = name;
		this.value = value;
		this.currency = "USD";
	}
	
	public Person(String name, double value, String currency) {
		this.name = name;
		this.value = String.valueOf(value);
		this.currency = currency;
	}

	public int describeContents() {
		return 0;
	}

	public double getDoubleValue() {
		return Double.valueOf(value);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	private void readFromParcel(Parcel in) {
		name = in.readString();
		value = in.readString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(value);
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
