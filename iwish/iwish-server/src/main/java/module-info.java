module iwish.server {
    requires iwish.common;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    
    exports serverSide;

    opens serverSide to javafx.graphics, javafx.fxml;
}
