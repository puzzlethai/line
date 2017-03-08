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

import org.springframework.cache.annotation.Cacheable;



public interface CustomerService {
	public List<Customer> findAll();
	public void saveCustomer(Customer customer);
	
	@Cacheable ("customers")
	public Customer findOne(long id);
	public void delete(long id);
	public List<Customer> findByUserId(String userId);
	public List<Customer> findByUserIdAndDisplayName(String userId, String displayName);
	public List<Customer> findByStatus(String status);
	List<Customer> findByPriceRange(String status1, String status2);
	List<Customer> findByUserIdMatch(String userId);
	List<Customer> findByUserIdParam(String userId, String displayName, String status);
}

