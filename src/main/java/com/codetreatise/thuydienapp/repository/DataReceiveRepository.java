package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.DataReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DataReceiveRepository extends JpaRepository<DataReceive, Long> {


}
