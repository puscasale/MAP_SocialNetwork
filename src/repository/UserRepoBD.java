package repository;

import domain.Page;
import domain.Pageable;
import domain.User;
import domain.validators.Validator;

import java.sql.*;
import java.util.*;

public class UserRepoBD implements PagingRepo<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;
    Map<Long, User> users = new HashMap<>();

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
        loadData();
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
                String email = resultSet.getString("email");
                String password = resultSet.getString("pasword");
                user = new User(firstName, lastName, email, password);
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
                String email = resultSet.getString("email");
                String password = resultSet.getString("pasword");
                User u = new User(firstName, lastName, email, password);
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
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (firstname, lastname,email, pasword) VALUES (?, ?,?,?)")) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

       if( rez > 0){
           users.put(entity.getId(), entity);
           loadData();
           return Optional.empty();
       }
       else return Optional.of(entity);
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
        validator.validate(entity);
        Optional<User> existingUser = Optional.ofNullable(users.get(entity.getId()));
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET firstname = ?, lastname = ?, email = ?, pasword = ? WHERE user_id = ?")) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());
            statement.setLong(5, entity.getId());

            rowsAffected = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if( rowsAffected > 0){
            users.put(entity.getId(), entity);
            loadData();
            return Optional.empty();
        }
        else return Optional.of(entity);
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
        Optional<User> userToDelete = Optional.ofNullable(users.get(id));
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

        if( rowsAffected > 0){
            users.remove(id);
            loadData();
            return Optional.empty();

        }
        else return Optional.of(userToDelete.get());
    }

    private void loadData(){
        findAll().forEach(user -> {
            users.put(user.getId(), user);
        });
    }

    /**
     * Retrieves a paginated list of users from the database.
     * The method fetches a subset of users based on the specified page number and page size.
     * It also retrieves the total count of users in the database to help with pagination.
     *
     * @param pageable the pagination details (page number and page size)
     * @return a Page object containing the list of users for the current page and the total number of users
     */
    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        List<User> users = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement pageStatement = connection.prepareStatement("SELECT * FROM users " + "LIMIT ? OFFSET ?");
            PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(*) AS conut FROM users")
        ){
            pageStatement.setInt(1,pageable.getPageSize());
            pageStatement.setInt(2,pageable.getPageNumber()*pageable.getPageSize());
            try(ResultSet pageResultSet = pageStatement.executeQuery();
                ResultSet countResultSet = countStatement.executeQuery()){

                while(pageResultSet.next()){
                    Long id = pageResultSet.getLong("user_id");
                    String firstname = pageResultSet.getString("firstname");
                    String lastname = pageResultSet.getString("lastname");
                    String email = pageResultSet.getString("email");
                    String password = pageResultSet.getString("pasword");

                    User u = new User(firstname, lastname, email, password);
                    u.setId(id);
                    users.add(u);
                }
                int count = 0;
                if(countResultSet.next()){
                    count = countResultSet.getInt("count");
                }
                return new Page<>(users, count);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
