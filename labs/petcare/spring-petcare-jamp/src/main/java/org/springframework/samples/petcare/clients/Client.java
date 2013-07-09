package org.springframework.samples.petcare.clients;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.samples.petcare.util.Address;

@RooJavaBean
public class Client {
	
	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	private String phone;
	
	private Address address;

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
