package javafx;

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
     * It retrieves the email and password, checks the login credentials, and opens the main menu if successful.
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
     * Method to retrieve the currently logged-in user.
     * @return the logged-in user or null if no user is logged in
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * This method shows an error message in an alert dialog.
     * @param message the error message
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * This method loads and opens the main menu window of the application.
     * It loads the FXML file for the main scene and sets the logged-in user.
     */
    private void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));

            Stage stage = (Stage) emailField.getScene().getWindow();

            Scene scene = new Scene(loader.load(),800,600);

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
     * @param srv the login service
     */
    public void setService(Service srv) {
        this.srv = srv;
    }

    /**
     * This method is triggered when the "Don't have an account? Sign up" link is clicked.
     * It navigates to the Sign-Up page.
     */
    @FXML
    private void handleSignUpRedirect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUpView.fxml"));
            Stage stage = (Stage) signUpLink.getScene().getWindow();
            Scene scene = new Scene(loader.load(),800,600);

            SignUpController signUpController = loader.getController();
            signUpController.setService(srv);

            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        Platform.exit();  // Închide aplicația
    }

}
