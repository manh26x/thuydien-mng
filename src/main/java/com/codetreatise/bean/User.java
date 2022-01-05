package com.codetreatise.bean;

import lombok.Data;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Ram Alapure
 * @since 05-04-2017
 */

@Entity
@Table(name="User")
@Data
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private long id;

	private String firstName;
	
	private String lastName;
	
	private LocalDate dob;
	
	private String gender;
	
	private String role;
	
	private String email;
	
	private String password;

	private String username;

	private Integer status;


}
