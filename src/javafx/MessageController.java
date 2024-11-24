package javafx;

import domain.Message;
import domain.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;
import java.time.LocalDateTime;
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

    public void setService(Service service) {
        this.service = service;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFriend(User friend) {
        this.friend = friend;
        loadMessages();
    }

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

    @FXML
    private void initialize() {
        // Configurează ListView să afișeze mesajele
        messageListView.setItems(messages);
    }

    public void onSendButtonClicked() {
        String text = messageTextField.getText();
        if (!text.isEmpty()) {
            // Trimite mesajul folosind service-ul
            boolean success = service.addMessage(user, friend, text);

            if (success) {
                // Dacă mesajul a fost trimis cu succes, adaugă-l în ListView
                String sender = "You: " + text;
                Platform.runLater(() -> {
                    messages.add(sender);
                    messageTextField.clear();  // Curăță câmpul de text
                });
            } else {
                showAlert("Message could not be sent.");
            }
        }
    }

    /**
     * Shows an informational alert with the given message.
     * @param message the message to display in the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onBackButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
            Parent root = loader.load();  // Încarcă FXML-ul pentru pagina principală

            Stage stage = (Stage) messageListView.getScene().getWindow();  // Obține fereastra curentă
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));

            // Setează controller-ul pentru MainView
            ChatController mainController = loader.getController();
            mainController.setService(service);
            mainController.setUser(user);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}