/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bot.spring;

/**
 *
 * @displayName OZONE
 */
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping(value = "/customers")
public class CustomerController {
	@Autowired
	private CustomerService customerService;

	@RequestMapping(value = "/add/{id}/{userId}/{displayName}/{status}")
	public Customer addCustomer(@PathVariable int id, @PathVariable String userId, @PathVariable String displayName,
			@PathVariable String status) {
		Customer customer = new Customer();
		customer.setId(id);
		customer.setUserId(userId);
		customer.setDisplayName(displayName);
		customer.setStatus(status);
		customerService.saveCustomer(customer);
		return customer;
	}
	@RequestMapping(value = "/delete/{id}")
	public void deleteCustomer(@PathVariable int id) {
		Customer customer = new Customer();
		customer.setId(id);
		customerService.delete(id);
	}
	@RequestMapping(value = "/")
	public List<Customer> getCustomers() {
		return customerService.findAll();
	}
	@RequestMapping(value = "/{id}")
	public Customer getCustomer(@PathVariable int id) {
		Customer customer = customerService.findOne(id);
		return customer;
	}
	@RequestMapping(value = "/search/userId/{userId}")
	public List<Customer> getCustomerByUserId(@PathVariable String userId) {
		List<Customer> customers = customerService.findByUserId(userId);
		return customers;
	}
	@RequestMapping(value = "/search/userId/match/{userId}")
	public List<Customer> getCustomerByUserIdMatch(@PathVariable String userId) {
		List<Customer> customers = customerService.findByUserIdMatch(userId);
		return customers;
	}
	@RequestMapping(value = "/search/param/{userId}/{displayName}/{status}")
	public List<Customer> getCustomerByUserIdParam(@PathVariable String userId, @PathVariable String displayName, @PathVariable String status) {
		List<Customer> customers = customerService.findByUserIdParam(userId, displayName, status);
		return customers;
	}
	
	@RequestMapping(value = "/search/status/{status}")
	public List<Customer> getCustomerByStatus(@PathVariable String status) {
		List<Customer> customers = customerService.findByStatus(status);
		return customers;
	}
	@RequestMapping(value = "/search/status/{status1}/{status2}")
	public List<Customer> getCustomerByStatusRange(@PathVariable String status1, @PathVariable String status2) {
		List<Customer> customers = customerService.findByPriceRange(status1, status2);
		return customers;
	}
	@RequestMapping(value = "/search/{userId}/{displayName}")
	public List<Customer> getCustomerByUserIdAndDisplayName(@PathVariable String userId, @PathVariable String displayName) {
		List<Customer> customers = customerService.findByUserIdAndDisplayName(userId, displayName);
		return customers;
	}
}
