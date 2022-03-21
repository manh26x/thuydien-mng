package com.codetreatise.thuydienapp.config.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjectSendApi  implements Serializable {
    private String username;
    private String pass;
    private List<DataCallApi> datas;


}
