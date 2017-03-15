/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bot.spring;


;
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
    private Long id;    

    private String userId;  
 
private String displayName;
  private String status;  

protected Customer() {
    }
  
    public Customer(String UserId, String DisplayName,String Status) {
        this.userId = UserId;
        this.displayName = DisplayName;
        this.status = Status;
    }
    
public String getUserId() {  
    return userId;  
}  
public void setUserId(String userId) {  
    this.userId = userId;  
}  
public String getDisplayName() {  
    return displayName;  
}  
public void setDisplayName(String displayName) {  
    this.displayName = displayName;  
}  
public String getStatus() {  
    return status;  
}  
public void setStatus(String status) {  
    this.status = status;  
}  
 @Override
    public String toString() {
        return String.format(
                "Customer[id=%d,userid=%s, displayName=%s, status=%s]",
                id,userId, displayName, status);
    }
}
