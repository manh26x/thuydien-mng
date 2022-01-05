package com.codetreatise.repository;

import com.codetreatise.bean.ModbusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ModbusMasterRepository extends JpaRepository<ModbusMaster, Integer> {

    @Transactional
    @Modifying
    @Query("update ModbusMaster m set m.status = :status")
    void changeStatus(Integer status);

    ModbusMaster findByStatus(Integer status);
}
