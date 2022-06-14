package com.codetreatise.thuydienapp.bean;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Data
@Builder
public class DataError {

    private Long id;
    private String type;
    private LocalDateTime createTime;
    private String message;
    private String menuName;
    private String title;
    private Byte isRead;
}
