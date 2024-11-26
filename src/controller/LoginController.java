package controller;

import domain.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;

public class LoginController {

    private Service srv;
    private User loggedInUser;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink signUpLink;

    public LoginController() {
    }

    /**
     * This method is called when the "Login" button is pressed.
     * It retrieves the email and password from the input fields,
     * checks the login credentials using the service, and opens the main menu if successful.
     * If login fails, an error message is shown.
     */
    @FXML
    private void handleLogin() {

        String email = emailField.getText();
        String password = passwordField.getText();

        loggedInUser = srv.login(email, password);

        if (loggedInUser != null) {

            System.out.println("User logged in: " + loggedInUser.getEmail());
            showMainMenu();
        } else {

            showAlert("Login failed, please check your credentials.");
        }
    }


    /**
     * Displays an alert with an error message.
     * This method is used to show error messages when login fails.
     * @param message the error message to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Loads and opens the main menu window of the application.
     * It loads the FXML file for the main scene and sets the logged-in user in the main controller.
     */
    private void showMainMenu() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));


            Stage stage = (Stage) emailField.getScene().getWindow();


            Scene scene = new Scene(loader.load(), 800, 600);

            stage.setScene(scene);


            MainController mainController = loader.getController();
            mainController.setService(srv);
            mainController.setUser(loggedInUser);

            stage.setTitle("Social Network");
            stage.show();

            System.out.println("Login successful, opening main menu...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the login service.
     * This method is used to inject the service used for login operations.
     * @param srv the service used for login
     */
    public void setService(Service srv) {
        this.srv = srv;  // Set the login service
    }

    /**
     * This method is triggered when the "Don't have an account? Sign up" hyperlink is clicked.
     * It navigates to the Sign-Up page by loading the corresponding FXML file.
     */
    @FXML
    private void handleSignUpRedirect() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUpView.fxml"));

            Stage stage = (Stage) signUpLink.getScene().getWindow();

            Scene scene = new Scene(loader.load(), 800, 600);

            SignUpController signUpController = loader.getController();
            signUpController.setService(srv);

            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is triggered when the "Exit" action is performed.
     * It closes the application.
     */
    @FXML
    private void handleExit() {
        Platform.exit();
    }

}

