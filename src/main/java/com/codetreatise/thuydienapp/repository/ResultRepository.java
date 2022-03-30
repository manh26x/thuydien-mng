package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.DataReceive;
import com.codetreatise.thuydienapp.bean.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findAllByApiAndTimeSendAfterAndTimeSendBefore(String apiUrl, Date fromDate, Date toDate);

    @Query("select d from DataReceive d where d.id not in" +
            " ( select r.dataReceive.id from Result r " +
            " where r.api like :apiUrl )")
    List<DataReceive> findNotSend(@Param("apiUrl") String apiUrl);
}
