package com.codetreatise.thuydienapp.view;

import java.util.ResourceBundle;

public enum FxmlView {

    USER {
        @Override
		public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
		public String getFxmlFile() {
            return "/fxml/User.fxml";
        }
    }, 
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
    API_CONFIG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("apiconfig.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/ApiConfig.fxml";
        }
    }
    ;
    
    public abstract String getTitle();
    public abstract String getFxmlFile();
    
    String getStringFromResourceBundle(String key){
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
