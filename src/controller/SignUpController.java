package controller;

import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;

public class SignUpController {

    private Service srv;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    /**
     * Sets the service for this controller.
     * This method allows the injection of the service to interact with the user data.
     * @param service the service used for handling user data operations
     */
    public void setService(Service service) {
        this.srv = service;
    }

    /**
     * This method is called when the "Sign Up" button is pressed.
     * It retrieves the user input, checks for empty fields and email uniqueness,
     * and creates a new user if all conditions are met.
     * If the sign-up is successful, it loads the main view of the application.
     * If there are issues (like empty fields or an existing email), it shows an alert.
     */
    @FXML
    private void handleSignUp(ActionEvent event) {
        String fn = firstName.getText();
        String ln = lastName.getText();
        String emailInput = email.getText();
        System.out.println(emailInput);
        String pass = password.getText();

        if (fn.isEmpty() || ln.isEmpty() || emailInput.isEmpty() || pass.isEmpty()) {
            showAlert("All fields must be filled!");
            return;
        }

        if (srv.findUserByEmail(emailInput) != null) {
            showAlert("Email already exists!");
            return;
        }


        User newUser = new User(fn, ln, emailInput, pass);
        srv.addUser(newUser);

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) firstName.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            MainController mainController = loader.getController();
            mainController.setService(srv);
            mainController.setUser(newUser);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the "Back" button is pressed.
     * It navigates the user back to the login page by loading the LoginView.fxml file.
     */
    @FXML
    public void onButtonBackClicked() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) lastName.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));


            LoginController loginController = loader.getController();
            loginController.setService(srv);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert with the provided message.
     * This method is used to display error or informational messages to the user.
     * @param message the message to display in the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Up");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

