package com.twozo.model;

import java.time.LocalDate;
import java.util.Objects;

import com.twozo.enums.Gender;

public final class Customer {

	private long id;
	private String name;
	private Gender gender;
	private String mobileNumber;
	private String aadharNumber;
	private String panCardNumber;
	private LocalDate dob; 
	private String address;
	private String photoUrl; 
	
	private Customer(final String name, final Gender gender,
					 final String mobileNumber, final String aadharNumber, final String panCardNumber,
					 final LocalDate dob, final String address, final String photoUrl) {
		this.name = name;
		this.gender = gender;
		this.mobileNumber = mobileNumber;
		this.aadharNumber = aadharNumber;
		this.panCardNumber = panCardNumber;
		this.dob = dob;
		this.address = address;
		this.photoUrl = photoUrl;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		return id == other.id;
	}

	public long getCustomerId() {
		return id;
	}

	public void setCustomerId(final long customerId) {
		this.id = customerId;
	}
	
	public final String getName() {
		return name;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	public final String getMobileNumber() {
		return mobileNumber;
	}

	public final void setMobileNumber(final String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public final String getAadharNumber() {
		return aadharNumber;
	}

	public final void setAadharNumber(final String aadhar) {
		this.aadharNumber = aadhar;
	}

	public final LocalDate getDob() {
		return dob;
	}

	public final void setDob(final LocalDate dob) {
		this.dob = dob;
	}

	public final String getAddress() {
		return address;
	}

	public final void setAddress(final String address) {
		this.address = address;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(final Gender gender) {
		this.gender = gender;
	}

	public String getPanCardNumber() {
		return panCardNumber;
	}

	public void setPanCardNumber(final String panCardNumber) {
		this.panCardNumber = panCardNumber;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(final String photoUrl) {
		this.photoUrl = photoUrl;
	}
	
	public Customer() {
		
	}
}
