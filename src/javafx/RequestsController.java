package javafx;

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
import javafx.scene.control.Label;
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

    @FXML
    private Label messageLabel;

    public void setService(Service service) {
        this.srv = service;
    }

    public void setUser(User loggedInUser) {
        this.user = loggedInUser;
        loadRequestsList();
    }

    private void loadRequestsList() {
        requestsList.getItems().clear(); // Golește lista înainte de populare
        friendshipsList.clear(); // Golește lista auxiliară

        // Obține lista de prietenii primite
        List<Friendship> friendships = getReceivedFriendRequests(); // Modificat pentru a obține doar cererile primite
        ObservableList<String> friendDetails = FXCollections.observableArrayList(); // Creează o listă observabilă pentru detalii

        for (Friendship friendship : friendships) {
            User friend = (friendship.getIdUser1().equals(user.getId())) ?
                    srv.find_user(friendship.getIdUser2()).get() :
                    srv.find_user(friendship.getIdUser1()).get();

            // Construiește textul pentru fiecare prietenie
            String detail = "Name: " + friend.getFirstName() + " " + friend.getLastName() +
                    ", Date: " + friendship.getDate().toLocalDate() +
                    ", Status: " + friendship.getFriendshiprequest();
            friendDetails.add(detail);

            friendshipsList.add(friendship); // Salvează obiectul Friendship în lista auxiliară
        }

        requestsList.setItems(friendDetails); // Setează lista observabilă în ListView
    }

    private List<Friendship> getReceivedFriendRequests() {
        Iterable<Friendship> iterable = srv.getFriendships();
        List<Friendship> friendships = new ArrayList<>();

        for (Friendship friendship : iterable) {
            // Verifică dacă utilizatorul logat este destinatarul cererii de prietenie (idUser2)
            if ((friendship.getIdUser2().equals(user.getId())) ) {
                friendships.add(friendship);
            }
        }

        return friendships;
    }




    private List<Friendship> getUFriends(){

        Iterable<Friendship> iterable = srv.getFriendships();

        List<Friendship> friendships = new ArrayList<>();
        for (Friendship friendship : iterable) {
            if(friendship.getIdUser1().equals(user.getId()) || friendship.getIdUser2().equals(user.getId()))
                friendships.add(friendship);
        }

        return friendships;
    }

    public void AcceptButton() {
        int selectedIndex = requestsList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) { // Verifică dacă a fost selectat un element
            Friendship selectedFriendship = friendshipsList.get(selectedIndex); // Preia obiectul Friendship
            if (selectedFriendship.getFriendshiprequest() == Friendshiprequest.PENDING) {
                srv.manageFriendRequest(selectedFriendship, Friendshiprequest.APROOVED); // Gestionează cererea
            } else {
                showAlert("The request must be PENDING in order to APPROVE it");
            }
        }
        loadRequestsList();
    }

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


    public void SendRequest() {
        String ln = last_name.getText();
        String fn = fisrt_name.getText();
        if (ln == null || ln.isEmpty() || fn == null || fn.isEmpty()) {
            showAlert("Please enter a friend's name.");
            return;
        }


        User friend = srv.findUserByName(fn,ln);

        if (friend == null) {
            showAlert("Friend not found.");
        } else {
            srv.createFriendshipRequest(user.getId(),friend.getId());
            showAlert("Friend request send successfully!");

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

    public void onProfile(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) fisrt_name.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));


            MainController mainController = loader.getController();
            mainController.setService(srv);
            mainController.setUser(user);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onFriendsButtonClicked(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FriendsView.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) fisrt_name.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(root,800,600));


            FriendsController mainController = loader.getController();
            mainController.setService(srv);
            mainController.setUser(user);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
