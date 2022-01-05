package com.codetreatise.repository;

import com.codetreatise.bean.DataReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface DataReceiveRepository extends JpaRepository<DataReceive, Long> {
    List<DataReceive> findAllByStatus(Integer status);

    @Modifying
    @Transactional
    @Query("update DataReceive d set d.status = ?1")
    void updateStatus(Integer status);
}
