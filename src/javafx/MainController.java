package javafx;

import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;
import java.util.List;

public class MainController {

    private Service service;
    private User loggedInUser;

    @FXML
    private ListView<String> friendsListView;

    @FXML
    private TextField fisrt_name;

    @FXML
    private TextField last_name;

    @FXML
    private Label messageLabel;


    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Sets the logged-in user and loads their friends list.
     * @param user the logged-in user
     */
    public void setUser(User user) {
        this.loggedInUser = user;
        loadFriendsList();
    }

    /**
     * Loads the list of friends for the logged-in user into the ListView.
     */
    private void loadFriendsList() {
        try {

            List<User> friendships = service.getFriends(loggedInUser);

            ObservableList<String> friendsNames = FXCollections.observableArrayList();

            if (friendships.isEmpty() || friendships == null) {
                showAlert("You have no friends yet!");
            } else {

                for (User friend : friendships) {
                    String fullName = friend.getFirstName() + " " + friend.getLastName();
                    friendsNames.add(fullName);
                }

            }


            friendsListView.setItems(friendsNames);
        } catch (Exception e) {
            showAlert("An error occurred while loading your friends list.");
            e.printStackTrace();
        }
    }


    /**
     * Adds a new friend to the logged-in user's friend list.
     * It uses the first and last name entered in the text fields to find the user and add them.

    @FXML
    private void addFriend() {
        String ln = last_name.getText();
        String fn = fisrt_name.getText();
        if (ln == null || ln.isEmpty() || fn == null || fn.isEmpty()) {
            showAlert("Please enter a friend's name.");
            return;
        }

        try {

            User friend = service.findUserByName(fn,ln);

            if (friend == null) {
                showAlert("Friend not found.");
            } else {

                service.addFriendship(loggedInUser.getId(), friend.getId());
                showAlert("Friend added successfully!");
                loadFriendsList();
            }
        } catch (Exception e) {
            showAlert("An error occurred while adding the friend.");
            e.printStackTrace();
        }
    }
     */
    /**
     * Removes a selected friend from the logged-in user's friend list.
     * It looks for the selected friend by their name and removes them.
     */
    @FXML
    private void removeFriend() {
        String selectedFriend = friendsListView.getSelectionModel().getSelectedItem();
        if (selectedFriend == null) {
            showAlert("Please select a friend to remove.");
            return;
        }

        try {

            String[] nameParts = selectedFriend.split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts[1];

            User friend = service.findUserByName(firstName,lastName);

            if (friend != null) {
                service.removeFriendship(loggedInUser.getId(), friend.getId());
                showAlert("Friend removed successfully!");
                loadFriendsList();
            } else {
                showAlert("Friend not found.");
            }
        } catch (Exception e) {
            showAlert("An error occurred while removing the friend.");
            e.printStackTrace();
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

            Stage stage = (Stage) friendsListView.getScene().getWindow();  // Folosește orice element valid din scenă curentă
            stage.setTitle("Friend Requests");
            stage.setScene(new Scene(root));

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
}