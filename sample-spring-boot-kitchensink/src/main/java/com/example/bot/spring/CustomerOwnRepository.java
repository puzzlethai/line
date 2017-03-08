/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bot.spring;

/**
 *
 * @author OZONE
 */
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;


public interface CustomerOwnRepository extends Repository<Customer,Long>{
	@Query(value="select displayName from Customer b where b.displayName=?1")
	List<Customer> findByUserId(String userId);
	List<Customer> findByUserIdAndDisplayName(String userId, String displayName);
}

