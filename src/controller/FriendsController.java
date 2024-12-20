package controller;

import domain.Friendship;
import domain.Page;
import domain.Pageable;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class FriendsController {
    @FXML
    private ListView<String> friendsListView;

    private Service service;
    private User loggedInUser;

    private int currentPage = 0;

    private int pageSize = 5;

    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;

    @FXML
    private Label pageNumber;

    /**
     * Sets the service instance used for operations.
     * @param service the service instance
     */
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
     * It retrieves the list of friends from the service and displays their names.
     */
    private void loadFriendsList() {
        try {
            Page<Friendship> pageFriends = service.findUsersFriends(new Pageable(pageSize, currentPage), loggedInUser);

            int totalFriends = pageFriends.getTotalElementCount();
            int maximumFriends = (int) Math.ceil((double) totalFriends / pageSize) - 1;
            if(maximumFriends == -1){
                showAlert("You have no friends");
                maximumFriends = 0;
            }

            if (currentPage > maximumFriends) {
                currentPage = maximumFriends;
                pageFriends = service.findUsersFriends(new Pageable(pageSize, currentPage), loggedInUser);
            }

            int nrOfElements = pageFriends.getTotalElementCount();
            previousButton.setDisable(currentPage == 0);
            nextButton.setDisable((currentPage + 1) * pageSize >= nrOfElements);

            ObservableList<String> friends = FXCollections.observableArrayList();
            for (Friendship friendship : pageFriends.getElementsOnPage()) {
                User friend = friendship.getIdUser1().equals(loggedInUser.getId())
                        ? service.find_user(friendship.getIdUser2()).get()
                        : service.find_user(friendship.getIdUser1()).get();
                String fullName = friend.getFirstName() + " " + friend.getLastName();
                friends.add(fullName);
            }

            friendsListView.setItems(friends);
            pageNumber.setText((currentPage + 1) + " / " + (maximumFriends + 1));

        } catch (Exception e) {
            showAlert("An error occurred while loading your friends list.");
            e.printStackTrace();
        }
    }


    /**
     * Handles the action when the "Next" button is clicked.
     * Increases the current page number and reloads the friends list for the next page.
     *
     * @return void
     */
    public void onButtonNextClicked(){
        currentPage++;
        loadFriendsList();
    }

    /**
     * Handles the action when the "Previous" button is clicked.
     * Decreases the current page number and reloads the friends list for the previous page.
     *
     * @return void
     */
    public void onButtonPreviousClicked(){
        currentPage--;
        loadFriendsList();
    }


    /**
     * Removes a selected friend from the friends list.
     * It checks the selected friend in the ListView and removes their friendship.
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


            User friend = service.findUserByName(firstName, lastName);

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

    /**
     * Navigates to the profile view.
     * It switches the current scene to the profile view (MainView.fxml).
     */
    public void onProfile(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) friendsListView.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            MainController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(loggedInUser);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the friend requests view.
     * It switches the current scene to the RequestsView.fxml.
     */
    public void onFriendRequestButton() {
        openMainScene();
    }

    /**
     * Opens the friend requests scene.
     * Loads the RequestsView.fxml and switches the current view.
     */
    private void openMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RequestsView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) friendsListView.getScene().getWindow();
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
     * Navigates to the chat view.
     * It switches the current scene to the ChatView.fxml.
     */
    public void onChat(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) friendsListView.getScene().getWindow();
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

