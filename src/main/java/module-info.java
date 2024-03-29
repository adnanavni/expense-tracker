module fi.metropolia.expensetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.json;


    opens fi.metropolia.expensetracker to javafx.fxml;
    exports fi.metropolia.expensetracker;
    exports fi.metropolia.expensetracker.controller;
    opens fi.metropolia.expensetracker.controller to javafx.fxml;
    opens fi.metropolia.expensetracker.datasource;
    opens fi.metropolia.expensetracker.module;
}