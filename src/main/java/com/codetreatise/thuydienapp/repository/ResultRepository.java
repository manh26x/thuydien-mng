package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findAllByTimeSendAfterAndTimeSendBefore(Date fromDate, Date toDate);

}
