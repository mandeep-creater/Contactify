package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

// contact repository ki help se fetch krenge user ke contacts ko


public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	
	
	@Query("from Contact as c where c.user.id=:userId")

	public List<Contact> findContactsByUser(@Param("userId")int userId );
	
	// for search bar by using find by name where name and user are like this
	public List<Contact> findByNameContainingAndUser(String name , User user);
	
	
	

}
