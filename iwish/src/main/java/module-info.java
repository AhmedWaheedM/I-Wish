module edu.iti.javaii.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports clientSide;
    opens clientSide;
    opens clientSide.appManger;
    opens clientSide.controllers;
}
