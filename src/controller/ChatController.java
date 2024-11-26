package controller;

import domain.Message;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;
import java.util.List;

public class ChatController {
    private Service service;
    private User user;

    private ObservableList<String> chatListItems = FXCollections.observableArrayList();

    @FXML
    private ListView<String> chatList;

    /**
     * Sets the service instance used for operations.
     * @param service the service instance
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Sets the logged-in user and loads their chat list.
     * @param user the logged-in user
     */
    public void setUser(User user) {
        this.user = user;
        loadChatList();
    }

    /**
     * Loads the chat list for the logged-in user.
     * It retrieves friends, their latest message, and populates the chat list.
     */
    private void loadChatList() {
        List<User> friends = service.getFriends(user);
        ObservableList<String> chatItems = FXCollections.observableArrayList();

        for (User friend : friends) {
            String chatText = friend.getFirstName() + " " + friend.getLastName();
            chatItems.add(chatText);
        }

        chatListItems.setAll(chatItems);
        chatList.setItems(chatListItems);

        chatList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openChatWithFriend(newValue);
            }
        });
    }


    /**
     * Opens a chat window with the selected friend.
     * It navigates to the MessageView.fxml and passes the selected friend's details.
     * @param chatText the selected chat text containing the friend's name
     */
    private void openChatWithFriend(String chatText) {

        String[] parts = chatText.split(":");
        String[] friendName = parts[0].split(" ");
        String firstName = friendName[0];
        String lastName = friendName[1];


        User friend = service.findUserByName(firstName, lastName);
        if (friend != null) {
            onMessage(friend);

        }
    }

    public void onMessage(User friend) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessageView.fxml"));
            Parent root = loader.load();


            MessageController messageController = loader.getController();
            messageController.setService(service);
            messageController.setUser(user);
            messageController.setFriend(friend);


            Stage stage = (Stage) chatList.getScene().getWindow();
            stage.setTitle("Chat with " + friend.getFirstName() + " " + friend.getLastName());
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the profile view.
     * It switches the current scene to the MainView.fxml.
     */
    public void onProfile(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) chatList.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            MainController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(user);
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

            Stage stage = (Stage) chatList.getScene().getWindow();
            stage.setTitle("Friend Requests");
            stage.setScene(new Scene(root, 800, 600));

            RequestsController requestsController = loader.getController();
            requestsController.setService(service);
            requestsController.setUser(user);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the friends view.
     * It switches the current scene to the FriendsView.fxml.
     * @param actionEvent the action event triggered by the button click
     */
    public void onFriendsButtonClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FriendsView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) chatList.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            FriendsController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(user);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

