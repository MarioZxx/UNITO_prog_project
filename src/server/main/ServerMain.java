package src.server.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerMain extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader serverLoader = new FXMLLoader(getClass().getResource("../resources/main/server.fxml"));
    Scene scene = new Scene(serverLoader.load());
    stage.setTitle("server");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
