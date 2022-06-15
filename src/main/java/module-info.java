module com.codetreatise.thuydienapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.commons.net;
    requires com.fasterxml.jackson.core;
    requires spring.web;
    requires com.fasterxml.jackson.databind;
    requires EasyModbusJava;
    requires org.slf4j;
    requires logback.classic;
    requires java.desktop;
    requires lombok;

    opens com.codetreatise.thuydienapp.controller;
    opens com.codetreatise.thuydienapp.bean;
    opens com.codetreatise.thuydienapp.event;
    opens com.codetreatise.thuydienapp.repository;
    opens com.codetreatise.thuydienapp.utils;
    opens com.codetreatise.thuydienapp.config;
    opens com.codetreatise.thuydienapp.config.request;
    opens com.codetreatise.thuydienapp.config.database;
    opens com.codetreatise.thuydienapp.config.ftp;
    opens com.codetreatise.thuydienapp.config.modbus.master;
    opens com.codetreatise.thuydienapp.config.modbus.slave;
    opens com.codetreatise.thuydienapp.config.login;
    exports com.codetreatise.thuydienapp;
    exports com.codetreatise.thuydienapp.controller;
}
