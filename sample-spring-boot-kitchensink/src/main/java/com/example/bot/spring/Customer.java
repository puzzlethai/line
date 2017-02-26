/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bot.spring;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author OZONE
 */
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String userId;
    private String displayName;

    protected Customer() {}

    public Customer(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }
public int getId() {  
    return id;  
}  
public void setId(int id) {  
    this.id = id;  
}  
public String getFirstName() {  
    return userId;  
}  
public void setFirstName(String userId) {  
    this.userId = userId;  
}  
public String getLastName() {  
    return displayName;  
}  
public void setLastName(String displayName) {  
    this.displayName = displayName;  
}
    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, userId='%s', displayName='%s']",
                id, userId, displayName);
    }

}
