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

    exports com.JavaPoker.javapoker.CardsLogic;
    opens com.JavaPoker.javapoker.CardsLogic to javafx.fxml;
    exports com.JavaPoker.javapoker.Graphics;
    opens com.JavaPoker.javapoker.Graphics to javafx.fxml;
    exports com.JavaPoker.javapoker.PlayerObject;
    opens com.JavaPoker.javapoker.PlayerObject to javafx.fxml;
    exports com.JavaPoker.javapoker.GameLogic;
    opens com.JavaPoker.javapoker.GameLogic to javafx.fxml;
}