package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {

    @Query("select d from Data d where (:status is null or d.status = :status)")
    Page<Data> findAllByStatus(Integer status, Pageable pageable);

    List<Data> findAllByStatus(Integer status);

    @Transactional
    @Modifying
    @Query("update Data d set d.status = 0 where d.key like ?1")
    void changeDataStatus(String key);

    Data findByKey(String key);
}
