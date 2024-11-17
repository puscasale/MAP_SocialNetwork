package javafx;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.FriendshipsRepoBD;
import repository.Repository;
import repository.UserRepoBD;
import service.Service;

public class HelloAplication extends Application {

    /**
     * This method is called when the JavaFX application is launched.
     * It sets up the database connection, initializes repositories, and loads the login view.
     * @param primaryStage the primary stage (window) for the application
     * @throws Exception if there is an error during loading or setting up the application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        String username = "postgres";
        String password = "alesefa";
        String url = "jdbc:postgresql://localhost:5432/postgres";

        Repository<Long, User> userRepoBD = new UserRepoBD(url, username, password, new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipRepoBD = new FriendshipsRepoBD(url, username, password);
        Service srv = new Service(userRepoBD, friendshipRepoBD);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        Parent root = loader.load();


        LoginController loginController = loader.getController();
        loginController.setService(srv);


        Scene scene = new Scene(root, 320, 240);
        primaryStage.setTitle("Social Network");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     * The main method that launches the JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

