module test.woi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens test.woi to javafx.fxml;
    opens test.woi.controller to javafx.fxml;
    opens test.woi.model to javafx.base;
    opens test.woi.util to javafx.fxml;

    exports test.woi;
    exports test.woi.controller;
    exports test.woi.model;
    exports test.woi.service;
    exports test.woi.dao;
    exports test.woi.util;
}
