module game {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.protobuf;

    opens game to javafx.fxml;
    exports game;
}