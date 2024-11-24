package javafx;

import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;
import java.util.List;

public class FriendsController {
    @FXML
    private ListView<String> friendsListView;

    private Service service;
    private User loggedInUser;

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

            for (User friend : friendships) {
                String fullName = friend.getFirstName() + " " + friend.getLastName();
                friendsNames.add(fullName);
            }

            friendsListView.setItems(friendsNames);
        } catch (Exception e) {
            showAlert("An error occurred while loading your friends list.");
            e.printStackTrace();
        }
    }

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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Friends List");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onProfile(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) friendsListView.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));


            MainController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(loggedInUser);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
