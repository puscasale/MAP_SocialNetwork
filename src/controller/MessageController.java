package controller;

import domain.Message;
import domain.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;
import java.util.List;

public class MessageController {
    private Service service;
    private User user;
    private User friend;

    private ObservableList<String> messages = FXCollections.observableArrayList();

    @FXML
    private ListView<String> messageListView;

    @FXML
    private TextField messageTextField;

    /**
     * Sets the service instance used for operations.
     * @param service the service instance
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Sets the logged-in user.
     * @param user the logged-in user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Sets the friend with whom the chat is taking place.
     * Loads the existing messages between the user and this friend.
     * @param friend the friend user object
     */
    public void setFriend(User friend) {
        this.friend = friend;
        loadMessages();
    }

    /**
     * Loads the messages between the logged-in user and the selected friend.
     * Retrieves messages using the service and populates the ListView.
     */
    private void loadMessages() {
        if (user != null && friend != null) {

            List<Message> messageList = service.getMessagesBetween(user, friend);
            ObservableList<String> messageDetails = FXCollections.observableArrayList();


            for (Message message : messageList) {
                String sender = message.getFrom().equals(user) ? "You" : friend.getFirstName();
                String messageText = sender + ": " + message.getMessage();
                messageDetails.add(messageText);
            }


            messages.setAll(messageDetails);
            messageListView.setItems(messages);
        }
    }

    /**
     * Initializes the controller.
     * Configures the ListView to display messages.
     */
    @FXML
    private void initialize() {

        messageListView.setItems(messages);
    }

    /**
     * Handles the action of clicking the "Send" button.
     * Sends a message to the friend, updates the ListView if successful,
     * and clears the input field.
     */
    public void onSendButtonClicked() {
        String text = messageTextField.getText();
        if (!text.isEmpty()) {
            Task<Boolean> sendMessageTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return service.addMessage(user, friend, text);
                }
            };

            sendMessageTask.setOnSucceeded(event -> {
                boolean success = sendMessageTask.getValue();
                if (success) {
                    String sender = "You: " + text;
                    messages.add(sender);
                    messageTextField.clear();
                } else {
                    showAlert("Message could not be sent.");
                }
            });

            sendMessageTask.setOnFailed(event -> showAlert("An error occurred while sending the message."));

            // Rulează task-ul pe un alt thread
            new Thread(sendMessageTask).start();
        }
    }


    /**
     * Displays an informational alert with the provided message.
     * @param message the message to display in the alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the action of clicking the "Back" button.
     * Navigates back to the ChatView scene and sets the user and service.
     */
    public void onBackButtonClicked() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) messageListView.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root, 800, 600));

            ChatController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(user);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
