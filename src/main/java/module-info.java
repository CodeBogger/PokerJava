module com.JavaPoker.javapoker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires annotations;

    exports com.example.javapoker.CardsLogic;
    opens com.example.javapoker.CardsLogic to javafx.fxml;
    exports com.example.javapoker.Graphics;
    opens com.example.javapoker.Graphics to javafx.fxml;
    exports com.example.javapoker.PlayerObject;
    opens com.example.javapoker.PlayerObject to javafx.fxml;
    exports com.example.javapoker.GameLogic;
    opens com.example.javapoker.GameLogic to javafx.fxml;
}