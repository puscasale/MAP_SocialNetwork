package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSignUp(ActionEvent event) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Poți adăuga logica de validare și înregistrare a utilizatorului aici

        // Dacă totul este corect, redirecționează către MainController
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText("Account successfully created!");
        alert.showAndWait();

        // După crearea contului, navighează la pagina principală (MainController)
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        MainController mainController = new MainController();
        Scene mainScene = new Scene(mainController.getMainView());
        stage.setScene(mainScene);
        stage.show();
    }

    public Parent getSignUpView() {
        try {
            // Încarcă fișierul FXML asociat paginii de înregistrare
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUpView.fxml"));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
