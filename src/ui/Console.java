package ui;

import domain.Friendship;
import domain.User;
import domain.validators.ValidationException;
import javafx.fxml.FXML;
import service.Service;

import java.util.List;
import java.util.Scanner;

/**
 * Console class for user interface interaction.
 * It allows users to manage users and friendships through a command-line interface.
 */
public class Console {

    private final Service service; // Service layer for handling business logic

    /**
     * Constructor for Console class.
     * @param service the service used for managing users and friendships
     */
    public Console(Service service) {
        this.service = service;
    }

    /**
     * Prints the main menu options to the console.
     */
    void printMenu() {
        System.out.println("\t\t\tMENU\t\t\t");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Print users");
        System.out.println("6. Print friendships");
        System.out.println("7. Communities");
        System.out.println("8. Most social community");
        System.out.println("0. EXIT");
    }

    /**
     * Main method to run the console application.
     * It continuously displays the menu and processes user inputs.
     */
    public void run() {
        Scanner scan = new Scanner(System.in);
        boolean ok = true; // Control variable for the main loop
        while (ok) {
            printMenu(); // Display the menu
            String input = scan.nextLine(); // Read user input
            switch (input) {
                case "1":
                    addUser(); // Add a user
                    break;
                case "2":
                    removeUser(); // Remove a user
                    break;
                case "3":
                    addFriendship(); // Add a friendship
                    break;
                case "4":
                    removeFriendship(); // Remove a friendship
                    break;
                case "5":
                    printUsers(); // Print all users
                    break;
                case "6":
                    printFriendships(); // Print all friendships
                    break;
                case "7":
                    printNumberOfCommunities(); // Print the number of communities
                    break;
                case "8":
                    printMostSocialCommunity(); // Print the most social community
                    break;
                case "0":
                    System.out.println("Exiting..."); // Exit the application
                    ok = false;
                    break;
                default:
                    System.out.println("Invalid input!"); // Handle invalid input
                    break;
            }
        }
    }

    /**
     * Prints all users to the console.
     */
    @FXML
    void printUsers() {
        System.out.println("\t\t\tUSERS\t\t\t");
        Iterable<User> users = service.getUsers(); // Retrieve all users
        for (User u : users) {
            System.out.println(u);
        }
    }

    /**
     * Adds a new user by reading user details from the console.
     */
    void addUser() {
        System.out.println("Add user");
        Scanner scan = new Scanner(System.in);
        System.out.println("First name: ");
        String firstName = scan.nextLine(); // Read first name
        System.out.println("Last name: ");
        String lastName = scan.nextLine();// Read last name
        System.out.println("Email: ");
        String email = scan.nextLine();
        System.out.println("Password: ");
        String password = scan.nextLine();

        try {
            User user = new User(firstName, lastName,email,password); // Create a new User
            service.addUser(user); // Add the user via the service
            System.out.println("User added successfully!"); // Success message
        } catch (ValidationException e) {
            System.out.println("Invalid user: " + e.getMessage()); // Error message
            // Handle non-numeric IDs
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());  // Handle any other exceptions
        }
    }

    /**
     * Removes a user by their ID.
     */
    void removeUser() {
        printUsers(); // Display current users
        System.out.println("Remove user");
        Scanner scan = new Scanner(System.in);
        System.out.println("Id: ");

        try {
            Long id = Long.parseLong(scan.nextLine()); // Read user ID
            service.removeUser(id); // Remove the user via the service
            System.out.println("User " + id + " was removed."); // Success message
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: ID must be a number.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage()); // Error message
        }
    }

    /**
     * Prints all friendships for each user.
     */
    @FXML
    void printFriendships() {

        Iterable<Friendship> f = service.getFriendships();
        for (Friendship friendship : f) {
            System.out.println(friendship);
        }
    }

    /**
     * Adds a friendship between two users by reading their IDs from the console.
     */
    void addFriendship() {
        System.out.println("Add friendship");
        Scanner scan = new Scanner(System.in);
        System.out.println("ID of the first user: ");


        try {
            Long id1 = Long.parseLong(scan.nextLine()); // Read first user ID
            System.out.println("ID of the second user: ");
            Long id2 = Long.parseLong(scan.nextLine()); // Read second user ID
            service.addFriendship(id1, id2); // Add friendship via the service
            System.out.println("Friendship added successfully!"); // Success message
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: User IDs must be numbers.");  // Handle non-numeric IDs
        } catch (ValidationException e) {
            System.out.println("Invalid friendship: " + e.getMessage());  // Catch validation exceptions
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Removes a friendship between two users.
     */
    void removeFriendship() {
        System.out.println("Remove friendship");
        Scanner scan = new Scanner(System.in);


        try {
            System.out.println("ID of the first user: ");
            Long id1 = Long.parseLong(scan.nextLine()); // Read first user ID
            System.out.println("ID of the second user: ");
            Long id2 = Long.parseLong(scan.nextLine()); // Read second user ID
            service.removeFriendship(id1, id2); // Remove friendship via the service
            System.out.println("Friendship removed successfully!"); // Success message
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: User IDs must be numbers.");  // Handle non-numeric IDs
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prints the number of communities in the user network.
     */
    @FXML
    void printNumberOfCommunities() {
        int numCommunities = service.getNumberOfCommunities(); // Get the number of communities
        System.out.println("Number of communities: " + numCommunities); // Print the result
    }

    /**
     * Prints the most social community based on the longest friendship path.
     */
    @FXML
    void printMostSocialCommunity() {
        List<Long> mostSocialCommunity = service.getMostSocialCommunity(); // Get the most social community
        System.out.println("Most social community: " + mostSocialCommunity); // Print the result
    }
}