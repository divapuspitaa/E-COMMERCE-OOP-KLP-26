module com.nusantarashop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.nusantarashop to javafx.fxml;
    opens com.nusantarashop.controller to javafx.fxml;
    opens com.nusantarashop.model to javafx.base;
    opens com.nusantarashop.util to javafx.fxml;

    exports com.nusantarashop;
    exports com.nusantarashop.controller;
    exports com.nusantarashop.model;
    exports com.nusantarashop.service;
    exports com.nusantarashop.dao;
    exports com.nusantarashop.util;
}
