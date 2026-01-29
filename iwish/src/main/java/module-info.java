module edu.iti.javaii.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;

    exports clientSide;
    exports serverSide;

    opens clientSide;
    opens clientSide.appManger;
    opens clientSide.controllers to javafx.fxml;
    opens serverSide to javafx.graphics, javafx.fxml;
}
