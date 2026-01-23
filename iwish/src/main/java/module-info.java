module edu.iti.javaii.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;

    exports clientSide;
    opens clientSide;
    opens clientSide.appManger;
    opens clientSide.controllers to javafx.fxml;

}
