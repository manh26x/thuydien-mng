package com.codetreatise.config.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataCallApi {
    private Float value;
    private String key;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date thoigian;
    private String nguon;
}
