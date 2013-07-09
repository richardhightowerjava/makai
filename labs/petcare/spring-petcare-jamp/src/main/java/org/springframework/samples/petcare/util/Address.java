package org.springframework.samples.petcare.util;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class Address {
	
	private String street;
	
	private String city;
	
	private String state;
	
	private String zip;

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
