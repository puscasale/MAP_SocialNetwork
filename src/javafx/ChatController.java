package javafx;

import domain.Message;
import domain.User;
import javafx.application.Platform;
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

    public void setService(Service service) {
        this.service = service;
    }

    public void setUser(User user) {
        this.user = user;
        loadChatList();
    }

    private void loadChatList() {
        // Găsește toți prietenii utilizatorului
        List<User> friends = service.getFriends(user);
        ObservableList<String> chatItems = FXCollections.observableArrayList();

        for (User friend : friends) {
            // Obține ultimul mesaj trimis între utilizator și prieten
            List<Message> messages = service.getMessagesBetween(user, friend);
            String lastMessage = (messages.isEmpty()) ? "No messages yet" : messages.get(messages.size() - 1).getMessage();

            // Construiește textul pentru ListView
            String chatText = friend.getFirstName() + " " + friend.getLastName() + ": " + lastMessage;
            chatItems.add(chatText);
        }

        chatListItems.setAll(chatItems);
        chatList.setItems(chatListItems);

        // Adaugă un event listener pentru a deschide conversația când se selectează un prieten
        chatList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openChatWithFriend(newValue);
            }
        });
    }

    private void openChatWithFriend(String chatText) {
        // Găsește numele prietenului din textul selecționat
        String[] parts = chatText.split(":");
        String[] friendName = parts[0].split(" ");
        String firstName = friendName[0];
        String lastName = friendName[1];


        // Căutăm prietenul în lista de prieteni
        User friend = service.findUserByName(firstName,lastName);
        if (friend != null) {
            // Încarcă conversația
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
    }
    public void onProfile(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) chatList.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));


            MainController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(user);
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

            Stage stage = (Stage) chatList.getScene().getWindow();  // Folosește orice element valid din scenă curentă
            stage.setTitle("Friend Requests");
            stage.setScene(new Scene(root,800,600));

            // Setează controller-ul pentru RequestsView
            RequestsController requestsController = loader.getController();
            requestsController.setService(service);
            requestsController.setUser(user);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onFriendsButtonClicked(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FriendsView.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) chatList.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));


            FriendsController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(user);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
