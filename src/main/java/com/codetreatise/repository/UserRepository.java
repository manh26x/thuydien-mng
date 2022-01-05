package com.codetreatise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codetreatise.bean.User;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);
	@Transactional
	@Modifying
	@Query("update User u set u.status = :status where 1=1")
	void changeStatus(Integer status);

	User findUserByStatus(Integer status);

}
