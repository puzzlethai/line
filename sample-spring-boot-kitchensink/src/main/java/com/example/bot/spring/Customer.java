/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bot.spring;
import javax.persistence.Column;
import javax.persistence.Entity;
/**
 *
 * @author OZONE
 */
@Entity
public class Customer {
    @Column
    private String userId;  
    @Column
private String displayName;
@Column    
private String status;  
  
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
                "Customer[id=%s, displayName=%s, status=%s]",
                userId, displayName, status);
    }
}
