package controller;

import domain.Friendship;
import domain.User;
import enums.Friendshiprequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestsController {

    private Service srv;
    private User user;
    private final List<Friendship> friendshipsList = new ArrayList<>();

    @FXML
    private TextField fisrt_name;

    @FXML
    private TextField last_name;


    @FXML
    private ListView<String> requestsList;


    /**
     * Sets the service instance to interact with the application logic.
     * @param service the service instance
     */
    public void setService(Service service) {
        this.srv = service;
    }

    /**
     * Sets the logged-in user and loads their friend requests into the view.
     * @param loggedInUser the currently logged-in user
     */
    public void setUser(User loggedInUser) {
        this.user = loggedInUser;
        loadRequestsList();
    }

    /**
     * Loads the friend requests into the list view and clears the previous data.
     */
    private void loadRequestsList() {
        requestsList.getItems().clear();
        friendshipsList.clear();

        List<Friendship> friendships = getReceivedFriendRequests();
        ObservableList<String> friendDetails = FXCollections.observableArrayList();

        for (Friendship friendship : friendships) {
            User friend = (friendship.getIdUser1().equals(user.getId())) ?
                    srv.find_user(friendship.getIdUser2()).get() :
                    srv.find_user(friendship.getIdUser1()).get();


            String detail = "Name: " + friend.getFirstName() + " " + friend.getLastName() +
                    ", Date: " + friendship.getDate().toLocalDate() +
                    ", Status: " + friendship.getFriendshiprequest();
            friendDetails.add(detail);

            friendshipsList.add(friendship);
        }

        requestsList.setItems(friendDetails);
    }

    /**
     * Fetches the list of received friend requests for the logged-in user.
     * @return a list of pending friend requests
     */
    private List<Friendship> getReceivedFriendRequests() {
        Iterable<Friendship> iterable = srv.getFriendships();
        List<Friendship> friendships = new ArrayList<>();

        for (Friendship friendship : iterable) {

            if ((friendship.getIdUser2().equals(user.getId())) ) {
                friendships.add(friendship);
            }
        }

        return friendships;
    }


    /**
     * Approves the selected friend request if it is in a pending state.
     */
    public void AcceptButton() {
        int selectedIndex = requestsList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Friendship selectedFriendship = friendshipsList.get(selectedIndex);
            if (selectedFriendship.getFriendshiprequest() == Friendshiprequest.PENDING) {
                srv.manageFriendRequest(selectedFriendship, Friendshiprequest.APROOVED);
            } else {
                showAlert("The request must be PENDING in order to APPROVE it");
            }
        }
        loadRequestsList();
    }

    /**
     * Rejects the selected friend request if it is in a pending state.
     */
    public void RejectButton() {
        int selectedIndex = requestsList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Friendship selectedFriendship = friendshipsList.get(selectedIndex);
            if (selectedFriendship.getFriendshiprequest() == Friendshiprequest.PENDING) {
                srv.manageFriendRequest(selectedFriendship, Friendshiprequest.REJECTED);
            } else {
                showAlert("The request must be PENDING in order to REJECT it");
            }
        }
        loadRequestsList();
    }

    /**
     * Sends a friend request to a user identified by their first and last name.
     */
    public void SendRequest() {
        String ln = last_name.getText();
        String fn = fisrt_name.getText();
        if (ln == null || ln.isEmpty() || fn == null || fn.isEmpty()) {
            showAlert("Please enter a friend's name.");
            return;
        }

        User friend = srv.findUserByName(fn, ln);

        if (friend == null) {
            showAlert("Friend not found.");
        } else {
            srv.createFriendshipRequest(user.getId(), friend.getId());
            showAlert("Friend request sent successfully!");
        }
        last_name.clear();
        fisrt_name.clear();
    }

    /**
     * Shows an informational alert with the given message.
     * @param message the message to display in the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Friend Requests");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigates back to the user's profile view.
     * @param actionEvent the action event triggered by the button
     */
    public void onProfile(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) fisrt_name.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            MainController mainController = loader.getController();
            mainController.setService(srv);
            mainController.setUser(user);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the friends view to display the user's friends.
     * @param actionEvent the action event triggered by the button
     */
    public void onFriendsButtonClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FriendsView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) fisrt_name.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            FriendsController mainController = loader.getController();
            mainController.setService(srv);
            mainController.setUser(user);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the chat view to enable chatting with friends.
     * @param actionEvent the action event triggered by the button
     */
    public void onChat(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) fisrt_name.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            ChatController mainController = loader.getController();
            mainController.setService(srv);
            mainController.setUser(user);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
