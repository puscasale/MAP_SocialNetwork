package repository;

import domain.User;
import domain.validators.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserRepoBD implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;

    /**
     * Constructor for initializing the repository with database connection details and a user validator.
     * @param url the database URL
     * @param username the database username
     * @param password the database password
     * @param validator the validator for user objects
     */
    public UserRepoBD(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     * Finds a user by their ID.
     * If no user is found, an empty Optional is returned.
     * @param id the ID of the user to find
     * @return an Optional containing the found user, or an empty Optional if no user is found
     */
    @Override
    public Optional<User> findOne(Long id) {
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE user_id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                user = new User(firstName, lastName);
                user.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(user);
    }

    /**
     * Retrieves all users from the database.
     * If no users are found, an empty set is returned.
     * @return a set containing all users
     */
    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("user_id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");

                User u = new User(firstName, lastName);
                u.setId(id);
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Saves a new user to the database.
     * If the insertion is successful, returns an empty Optional.
     * If the insertion fails, returns the user that was attempted to be saved.
     * @param entity the user to save
     * @return an empty Optional if the save was successful, or the user if it failed
     */
    @Override
    public Optional<User> save(User entity) {
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (firstname, lastname) VALUES (?, ?)")) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rez > 0 ? Optional.of(entity) : Optional.empty();
    }

    /**
     * Updates an existing user in the database.
     * If the update is successful, returns an empty Optional.
     * If the update fails, returns the user that was attempted to be updated.
     * @param entity the user to update
     * @return an empty Optional if the update was successful, or the user if it failed
     */
    @Override
    public Optional<User> update(User entity) {
        int rowsAffected = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET firstname = ?, lastname = ? WHERE user_id = ?")) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());

            rowsAffected = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowsAffected > 0 ? Optional.empty() : Optional.of(entity);
    }

    /**
     * Deletes a user from the database by their ID.
     * If the user exists and the deletion is successful, returns the deleted user.
     * If the user does not exist or the deletion fails, returns an empty Optional.
     * @param id the ID of the user to delete
     * @return an Optional containing the deleted user, or an empty Optional if the deletion failed
     */
    @Override
    public Optional<User> delete(Long id) {
        Optional<User> userToDelete = findOne(id);
        int rowsAffected = -1;

        if (userToDelete.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                statement.setLong(1, id);
                rowsAffected = statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rowsAffected > 0 ? userToDelete : Optional.empty();
    }

}
