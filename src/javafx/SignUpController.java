package javafx;

import domain.User;
import javafx.LoginController;
import javafx.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

    public void setService(Service service) {
        this.srv = service;
    }

    // Handle sign-up button click
    @FXML
    private void handleSignUp(ActionEvent event) {
        String fn = firstName.getText();
        String ln = lastName.getText();
        String emailInput = email.getText();
        System.out.println(emailInput);
        String pass = password.getText();

        // Check if fields are empty
        if (fn.isEmpty() || ln.isEmpty() || emailInput.isEmpty() || pass.isEmpty()) {
            showAlert("All fields must be filled!");
            return;
        }

        // Check if the email already exists
        if (srv.findUserByEmail(emailInput) != null) {
            showAlert("Email already exists!");
            return;
        }

        // Create new user
        User newUser = new User(fn, ln, emailInput, pass);
        srv.addUser(newUser);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();  // Încarcă FXML-ul pentru pagina principală

            // Folosește un element existent din scena curentă pentru a obține Stage (de exemplu, requestsList)
            Stage stage = (Stage) firstName.getScene().getWindow();  // Poți folosi requestsList sau orice alt element valid
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));

            // Setează controller-ul pentru MainView
            MainController mainController = loader.getController();
            mainController.setService(srv);
            mainController.setUser(newUser);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle back button click
    @FXML
    public void onButtonBackClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();  // Încarcă FXML-ul pentru pagina principală

            // Folosește un element existent din scena curentă pentru a obține Stage (de exemplu, requestsList)
            Stage stage = (Stage) lastName.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));

            // Setează controller-ul pentru MainView
            LoginController mainController = loader.getController();
            mainController.setService(srv);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Show alert with the given message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Up");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
