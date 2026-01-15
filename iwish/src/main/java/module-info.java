module edu.iti.javaii.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens edu.iti.javaii.project to javafx.fxml;
    exports edu.iti.javaii.project;
}
