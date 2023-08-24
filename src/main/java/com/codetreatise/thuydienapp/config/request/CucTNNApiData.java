package com.codetreatise.thuydienapp.config.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CucTNNApiData {
    @JsonProperty("MaTinh")
    private String MaTinh;
    @JsonProperty("KyHieuCongTrinh")
    private String KyHieuCongTrinh;
    @JsonProperty("KyHieuTram")
    private String KyHieuTram;
    @JsonProperty("ThoiGianGui")
    private String ThoiGianGui;
    @JsonProperty("NoiDung")
    private List<List<Object>> NoiDung;

    public CucTNNApiData() {
        NoiDung = new ArrayList<>();
    }
}
