module iwish.client {
    requires iwish.common;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    
    exports clientSide;
    
    opens clientSide to javafx.graphics, javafx.fxml;
    opens clientSide.appManger to javafx.fxml;
    opens clientSide.controllers to javafx.fxml;
    opens clientSide.helpers to javafx.fxml;
}
