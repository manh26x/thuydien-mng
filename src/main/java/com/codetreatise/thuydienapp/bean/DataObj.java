package com.codetreatise.thuydienapp.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataObj {
    Date timeSend;
    Map<String, ValueAndCode> data;

    public static class ValueAndCode {
        Double value;
        Integer codeResponse;

        public ValueAndCode(Double value, Integer codeResponse) {
            this.value = value;
            this.codeResponse = codeResponse;
        }

        public ValueAndCode() {
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public Integer getCodeResponse() {
            return codeResponse;
        }

        public void setCodeResponse(Integer codeResponse) {
            this.codeResponse = codeResponse;
        }
    }

    public DataObj() {
        this.data =new HashMap<>();
    }

    public Date getTimeSend() {
        return timeSend;
    }

    public String getTimeSendString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.format(this.timeSend);
    }

    public void setTimeSend(Date timeSend) {
        this.timeSend = timeSend;
    }

    public Map<String, ValueAndCode> getData() {
        return data;
    }

    public void setData(Map<String, ValueAndCode> data) {
        this.data = data;
    }

}
