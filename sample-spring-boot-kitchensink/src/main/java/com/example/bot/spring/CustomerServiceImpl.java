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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private CustomerOwnRepository customerOwnRepository;
//	@Autowired
//	private CustomerQueryRepositoryExample bookQueryRepository;
//	@Autowired
//	private CustomerNamedQueryRepositoryExample bookNamedQueryRepository;	

	public List<Customer> findAll() {
		return customerRepository.findAll();
	}

	public List<Customer> findByUserId(String userId) {
		return customerOwnRepository.findByUserId(userId);
	}

//	public List<Customer> findByNameMatch(String userId) {
//		return customerRepository.findByNameMatch(userId);
//	}

//	public List<Customer> findByNamedParam(String userId, String author, long price) {
//		return customerRepository.findByNamedParam(userId, author, price);
//	}

//	public List<Customer> findByPriceRange(long price1, long price2) {
//		return customerRepository.findByPriceRange(price1, price2);
//	}

//	public List<Customer> findByPrice(long price) {
//		return customerRepository.findByPrice(price);
//	}

//	public List<Customer> findByNameAndAuthor(String userId, String author) {
//		return customerRepository.findByNameAndAuthor(userId, author);
//	}

	public void saveCustomer(Customer book) {
		customerRepository.save(book);
	}

	public Customer findOne(long id) {
		System.out.println("Cached Pages");
		return customerRepository.findOne(id);
	}

	public void delete(long id) {
		customerRepository.delete(id);
	}

    @Override
    public List<Customer> findByUserIdAndDisplayName(String userId, String displayName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Customer> findByStatus(String status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Customer> findByPriceRange(String status1, String status2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Customer> findByUserIdMatch(String userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Customer> findByUserIdParam(String userId, String displayName, String status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

