/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bot.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author OZONE
 */

@RestController
public class WebController {
    @Autowired
    CustomerRepository repository;
     
    @RequestMapping("/save")
    public String process(){
        repository.save(new Customer("1", "Ozone"));
        repository.save(new Customer("2", "Focus"));
        return "Done";
    }
     
     
    @RequestMapping("/findall")
    public String findAll(){
        String result = "<html>";
         
        for(Customer cust : repository.findAll()){
            result += "<div>" + cust.toString() + "</div>";
        }
         
        return result + "</html>";
    }
     
    @RequestMapping("/findbyid")
    public String findById(@RequestParam("id") long id){
        String result = "";
        result = repository.findOne(id).toString();
        return result;
    }
     
    @RequestMapping("/findbylastname")
    public String fetchDataByLastName(@RequestParam("lastname") String lastName){
        String result = "<html>";
         
        for(Customer cust: repository.findByLastName(lastName)){
            result += "<div>" + cust.toString() + "</div>"; 
        }
         
        return result + "</html>";
    }
}