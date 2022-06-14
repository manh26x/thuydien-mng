package com.codetreatise.thuydienapp.utils;

import com.codetreatise.thuydienapp.bean.DataError;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventObject {
    private String type;
    private String menuSuccess;
    private DataError dataError;
}
