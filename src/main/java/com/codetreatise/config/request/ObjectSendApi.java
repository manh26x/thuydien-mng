package com.codetreatise.config.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjectSendApi {
    private String username;
    private String password;
    private List<DataCallApi> datas;
}
