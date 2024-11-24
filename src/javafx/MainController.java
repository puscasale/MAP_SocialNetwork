package javafx;

import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;
import java.util.List;

public class MainController {

    private Service service;
    private User loggedInUser;



    @FXML
    private TextField text_field;

    @FXML
    private Label first_name;

    @FXML
    private Label last_name;

    @FXML
    private Label email;




    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Sets the logged-in user and loads their friends list.
     * @param user the logged-in user
     */
    public void setUser(User user) {
        this.loggedInUser = user;
        initialize();

    }

    public void initialize() {
        // Presupunem că loggedInUser este deja setat, de exemplu, printr-o sesiune sau după logare
        if (loggedInUser != null) {
            // Setează valorile în câmpurile de text
            first_name.setText(loggedInUser.getFirstName());
            last_name.setText(loggedInUser.getLastName());
            email.setText(loggedInUser.getEmail());
        }
    }

    /**
     * Shows an informational alert with the given message.
     * @param message the message to display in the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Friends List");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onFriendRequestButton(){
        openMainScene();
    }

    private void openMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RequestsView.fxml"));
            Parent root = loader.load();  // Încarcă FXML-ul pentru noua scenă

            Stage stage = (Stage) text_field.getScene().getWindow();  // Folosește orice element valid din scenă curentă
            stage.setTitle("Friend Requests");
            stage.setScene(new Scene(root,800,600));

            // Setează controller-ul pentru RequestsView
            RequestsController requestsController = loader.getController();
            requestsController.setService(service);
            requestsController.setUser(loggedInUser);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads the main view (FXML file) of the application.
     * @return the Parent node loaded from the MainView.fxml file, or null if an error occurs
     */
    public Parent getMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

/*
    @FXML
    private void onMessagesButtonClicked() {

        String firstName = text_field.getText();
        String lastName = last_name.getText();

        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            showAlert("Please enter both first name and last name to open chat.");
            return;
        }

        try {
            User friend = service.findUserByName(firstName, lastName);
            if (friend == null) {
                showAlert("Friend not found.");
            } else {
                // Navigare către pagina de mesaje
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessageView.fxml"));
                Parent root = loader.load();

                MessageController messageController = loader.getController();
                messageController.setService(service);
                messageController.setUser(loggedInUser);
                messageController.setFriend(friend);

                Stage stage = (Stage) text_field.getScene().getWindow();
                stage.setTitle("Messages with " + friend.getFirstName());
                stage.setScene(new Scene(root,800,600));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("An error occurred while opening the chat.");
        }
    }


 */
    @FXML
    private void onBackButtonClicked() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();  // Încarcă FXML-ul pentru pagina principală

            // Folosește un element existent din scena curentă pentru a obține Stage (de exemplu, requestsList)
            Stage stage = (Stage) text_field.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));

            // Setează controller-ul pentru MainView
            LoginController mainController = loader.getController();
            mainController.setService(service);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void onFriendsButtonClicked(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FriendsView.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) text_field.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));


            FriendsController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(loggedInUser);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleChangeFirstName(ActionEvent actionEvent) {
        String newFirstName = text_field.getText();

        if (newFirstName != null && !newFirstName.isEmpty()) {
            // Get the current user details
            String lastName = loggedInUser.getLastName();
            String email = loggedInUser.getEmail();
            String password = loggedInUser.getPassword();
            Long id = loggedInUser.getId();

            // Create a new User object with the updated first name
            User u = new User(newFirstName, lastName, email, password);
            u.setId(id);

            // Update the user in the database or system
            service.update_user(u);

            // Update the loggedInUser object to reflect the change
            //loggedInUser.setFirstName(newFirstName);

            // Optionally, update the UI with the new first name
            first_name.setText(newFirstName);

            // Clear the text field
            text_field.clear();
        } else {
            // Handle empty first name case (optional)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("First name cannot be empty");
            alert.showAndWait();
        }
    }

    public void handleChangeLastName(ActionEvent actionEvent) {
        String newLastName = text_field.getText();

        if (newLastName != null && !newLastName.isEmpty()) {
            // Get the current user details
            String firstName = loggedInUser.getFirstName();
            String email = loggedInUser.getEmail();
            String password = loggedInUser.getPassword();
            Long id = loggedInUser.getId();

            // Create a new User object with the updated first name
            User u = new User(firstName, newLastName, email, password);
            u.setId(id);

            // Update the user in the database or system
            service.update_user(u);

            // Update the loggedInUser object to reflect the change
            //loggedInUser.setFirstName(newFirstName);

            // Optionally, update the UI with the new first name
            last_name.setText(newLastName);

            // Clear the text field
            text_field.clear();
        } else {
            // Handle empty first name case (optional)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Last name cannot be empty");
            alert.showAndWait();
        }
    }


    public void handleChangeEmail(ActionEvent actionEvent) {
        String newemail = text_field.getText();

        if (newemail != null && !newemail.isEmpty()) {
            // Get the current user details
            String lastName = loggedInUser.getLastName();
            String firstName = loggedInUser.getFirstName();
            String password = loggedInUser.getPassword();
            Long id = loggedInUser.getId();

            // Create a new User object with the updated first name
            User u = new User(firstName, lastName,newemail, password);
            u.setId(id);

            // Update the user in the database or system
            service.update_user(u);

            // Update the loggedInUser object to reflect the change
            //loggedInUser.setFirstName(newFirstName);

            // Optionally, update the UI with the new first name
            email.setText(newemail);

            // Clear the text field
            text_field.clear();
        } else {
            // Handle empty first name case (optional)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Email cannot be empty");
            alert.showAndWait();
        }
    }

    public void handleChangePassword(ActionEvent actionEvent) {
        String newpassword = text_field.getText();

        if (newpassword != null && !newpassword.isEmpty()) {
            // Get the current user details
            String firstName = loggedInUser.getFirstName();
            String lastName = loggedInUser.getLastName();
            String email = loggedInUser.getEmail();
            Long id = loggedInUser.getId();

            // Create a new User object with the updated first name
            User u = new User(firstName, lastName, email, newpassword);
            u.setId(id);

            // Update the user in the database or system
            service.update_user(u);

            // Clear the text field
            text_field.clear();
        } else {
            // Handle empty first name case (optional)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Password name cannot be empty");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteAccount() {
        // Show a confirmation message
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Account Deletion");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone.");

        // Check the user's response
        if (alert.showAndWait().get() == ButtonType.OK) {
            // Here you add the logic to delete the profile from the application
            service.removeUser(loggedInUser.getId());

            onBackButtonClicked();
        }
    }

    public void onChatButtonClicked(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) text_field.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));


            ChatController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(loggedInUser);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}