package org.sample;

import java.time.ZonedDateTime;

public class Customer {

	private String name;
	private ZonedDateTime birthDate;
	
	public Customer() {
	}
	
	public Customer(String name, ZonedDateTime birthDate) {
		this.name = name;
		this.birthDate = birthDate;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public ZonedDateTime getBirthDate() {
		return birthDate;
	}
	
	public void setBirthDate(ZonedDateTime birthDate) {
		this.birthDate = birthDate;
	}
}
