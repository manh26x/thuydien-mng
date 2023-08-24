package com.codetreatise.thuydienapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDataCucTNN {
    private String thongSoDo;
    private Float value;
    private String dvt;
    private Date time;
    private String status;
    private String kyHieuTram;
    private Integer modbusId;
    private String name;
    public List<Object> toList(SimpleDateFormat dateFormat) {
        List<Object> content = new ArrayList<>();
        content.add(thongSoDo);
        content.add(Math.round(value*100) / 100.0f);
        content.add(dvt);
        content.add(dateFormat.format(time));
        content.add(status);
        return content;
    }
}
