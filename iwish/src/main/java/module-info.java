module edu.iti.javaii.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens ClientSide.project to javafx.fxml;
    exports ClientSide.project;
}
