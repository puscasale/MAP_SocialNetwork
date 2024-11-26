package controller;

import domain.Friendship;
import domain.User;
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
import java.util.Optional;

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


    /**
     * Sets the service
     * @param service the service
     */
    public void setService(Service service) {

        this.service = service;
    }

    /**
     * Sets the logged-in user
     * @param user the logged-in user
     */
    public void setUser(User user) {
        this.loggedInUser = user;
        initialize();

    }


    /**
     * Initializes the controller by populating the user's details in the UI fields.
     * If the user is logged in, their first name, last name, and email are displayed.
     */
    public void initialize() {
        if (loggedInUser != null) {
            first_name.setText(loggedInUser.getFirstName());
            last_name.setText(loggedInUser.getLastName());
            email.setText(loggedInUser.getEmail());
            checkNewFriendRequests();
        }
    }

    private void checkNewFriendRequests() {
        List<Friendship> newRequests = service.getPendingFriendships(loggedInUser.getId());

        if (!newRequests.isEmpty()) {
            showNewFriendRequestNotification(newRequests);
        }
    }

    private void showNewFriendRequestNotification(List<Friendship> newRequests) {
        StringBuilder notificationMessage = new StringBuilder("From:\n");

        for (Friendship request : newRequests) {
            Optional<User> u = service.find_user(request.getIdUser1());
            if (u.isPresent()) {

                User user = u.get();
                notificationMessage.append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("New Friend Requests");
        alert.setHeaderText("You have new friend requests");
        alert.setContentText(notificationMessage.toString());
        alert.showAndWait();

    }


    /**
     * Handles the event when the "Friend Request" button is clicked.
     * Opens the scene displaying friend requests.
     */
    public void onFriendRequestButton() {
        openMainScene();
    }


    /**
     * Loads and displays the "Friend Requests" view.
     * Sets up the associated controller with the current user and service instance.
     */
    private void openMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RequestsView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) text_field.getScene().getWindow();
            stage.setTitle("Friend Requests");
            stage.setScene(new Scene(root, 800, 600));

            RequestsController requestsController = loader.getController();
            requestsController.setService(service);
            requestsController.setUser(loggedInUser);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Handles the event when the "Back" button is clicked.
     * Returns the user to the login screen.
     */
    @FXML
    private void onBackButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) text_field.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            LoginController mainController = loader.getController();
            mainController.setService(service);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * Handles the event when the "Friends" button is clicked.
     * Opens the scene displaying the user's friends.
     *
     * @param actionEvent The action event triggered by clicking the button.
     */
    public void onFriendsButtonClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FriendsView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) text_field.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            FriendsController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(loggedInUser);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles the event to update the user's first name.
     * Validates the input and updates the logged-in user's details.
     *
     * @param actionEvent The action event triggered by clicking the update button.
     */
    public void handleChangeFirstName(ActionEvent actionEvent) {
        String newFirstName = text_field.getText();

        if (newFirstName != null && !newFirstName.isEmpty()) {
            User u = new User(newFirstName, loggedInUser.getLastName(), loggedInUser.getEmail(), loggedInUser.getPassword());
            u.setId(loggedInUser.getId());

            service.update_user(u);
            first_name.setText(newFirstName);
            text_field.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("First name cannot be empty");
            alert.showAndWait();
        }
    }


    /**
     * Handles the event to update the user's last name.
     * Validates the input and updates the logged-in user's details.
     *
     * @param actionEvent The action event triggered by clicking the update button.
     */
    public void handleChangeLastName(ActionEvent actionEvent) {
        String newLastName = text_field.getText();

        if (newLastName != null && !newLastName.isEmpty()) {
            User u = new User(loggedInUser.getFirstName(), newLastName, loggedInUser.getEmail(), loggedInUser.getPassword());
            u.setId(loggedInUser.getId());

            service.update_user(u);
            last_name.setText(newLastName);
            text_field.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Last name cannot be empty");
            alert.showAndWait();
        }
    }



    /**
     * Handles the event to update the user's email address.
     * Validates the input and updates the logged-in user's details.
     *
     * @param actionEvent The action event triggered by clicking the update button.
     */
    public void handleChangeEmail(ActionEvent actionEvent) {
        String newEmail = text_field.getText();

        if (newEmail != null && !newEmail.isEmpty()) {
            User u = new User(loggedInUser.getFirstName(), loggedInUser.getLastName(), newEmail, loggedInUser.getPassword());
            u.setId(loggedInUser.getId());

            service.update_user(u);
            email.setText(newEmail);
            text_field.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Email cannot be empty");
            alert.showAndWait();
        }
    }


    /**
     * Handles the event to update the user's password.
     * Validates the input and updates the logged-in user's details.
     *
     * @param actionEvent The action event triggered by clicking the update button.
     */
    public void handleChangePassword(ActionEvent actionEvent) {
        String newPassword = text_field.getText();

        if (newPassword != null && !newPassword.isEmpty()) {
            User u = new User(loggedInUser.getFirstName(), loggedInUser.getLastName(), loggedInUser.getEmail(), newPassword);
            u.setId(loggedInUser.getId());

            service.update_user(u);
            text_field.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Password cannot be empty");
            alert.showAndWait();
        }
    }


    /**
     * Handles the event to delete the logged-in user's account.
     * Asks for confirmation and deletes the account if confirmed.
     */
    @FXML
    private void handleDeleteAccount() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Account Deletion");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            service.removeUser(loggedInUser.getId());
            onBackButtonClicked();
        }
    }


    /**
     * Handles the event when the "Chat" button is clicked.
     * Opens the scene for user chats.
     *
     * @param actionEvent The action event triggered by clicking the button.
     */
    public void onChatButtonClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) text_field.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            ChatController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(loggedInUser);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}