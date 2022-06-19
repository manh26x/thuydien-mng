package com.codetreatise.thuydienapp.view;

import java.util.ResourceBundle;

public enum FxmlView {


    LOGIN {
        @Override
		public String getTitle() {
            return getStringFromResourceBundle("login.title");
        }

        @Override
		public String getFxmlFile() {
            return "/fxml/Login.fxml";
        }
    },
    TIMING_MODBUS {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("timing.modbus.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/TimingModbus.fxml";
        }
    },
    ADD_FIELD_MODAL {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("addFieldModal.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/AddFieldModal.fxml";
        }
    },
    UPDATE_FIELD_MODAL {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("updateFieldModal.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/UpdateFieldModal.fxml";
        }
    },
    FTP_CONFIG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("ftp.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/FtpSendFileConfig.fxml";
        }
    },

    ADD_FTP {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("addFtp.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/AddFtp.fxml";
        }
    },
    MODBUS_SERVER_CONFIG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("modbusServerConfig.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/ModbusServerConfig.fxml";
        }
    },
    API_CONFIG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("apiconfig.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/ApiConfig.fxml";
        }
    },
    ADD_API {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("addApi.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/AddApi.fxml";
        }
    },
    API_FIELD_CONFIG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("apiFieldConfig.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/ApiFieldConfig.fxml";
        }
    },
    DATA_ERROR {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("apiFieldConfig.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/ErrorList.fxml";
        }
    },
    FILE_EXCEL_IMPORT {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("fileimport.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/ImportFileExcel.fxml";
        }
    }
    ;
    
    public abstract String getTitle();
    public abstract String getFxmlFile();
    
    String getStringFromResourceBundle(String key){
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
