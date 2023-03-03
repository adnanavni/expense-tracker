package fi.metropolia.expensetracker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login_form-view.fxml"));

        stage.setTitle("Expense Tracker");
        stage.setScene(new Scene(root,  700, 800));
        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("images/logo.png" )));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}