package repository;

import domain.Friendship;
import domain.Tuple;
import enums.Friendshiprequest;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipsRepoBD implements Repository<Tuple<Long, Long>, Friendship> {
    private static String url;
    private static String username;
    private static String password;

    /**
     * Constructor for initializing the Friendships repository with database connection details.
     * @param url the database URL
     * @param username the database username
     * @param password the database password
     */
    public FriendshipsRepoBD(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Finds a friendship between two users.
     * @param id a Tuple of the users ids
     * @return an Optional containing the Friendship if it exists, or an empty Optional if not
     */
    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        Friendship friendship = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM friendships WHERE (user_id_1 = ? AND user_id_2 = ?) OR (user_id_1 = ? AND user_id_2 = ?)")) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            statement.setLong(3, id.getRight());
            statement.setLong(4, id.getLeft());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long user1 = resultSet.getLong("user_id_1");
                Long user2 = resultSet.getLong("user_id_2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Friendshiprequest friendshiprequest = Friendshiprequest.valueOf(resultSet.getString("request"));
                friendship = new Friendship(user1, user2, date,friendshiprequest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(friendship);
    }

    /**
     * Retrieves all friendships.
     * @return a Set containing all friendships
     */
    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long user1 = resultSet.getLong("user_id_1");
                Long user2 = resultSet.getLong("user_id_2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Friendshiprequest friendshiprequest = Friendshiprequest.valueOf(resultSet.getString("request"));
                Friendship friendship = new Friendship(user1, user2, date,friendshiprequest);
                friendships.add(friendship);
                friendship.setId(new Tuple<>(user1, user2));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    /**
     * Saves a new friendship between two users.
     * @param friendship the Friendship object to save
     * @return an empty Optional if the save was successful, or the Friendship if it failed
     */
    @Override
    public Optional<Friendship> save(Friendship friendship) {
        int rowsAffected = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships (user_id_1, user_id_2,date, request) VALUES (?, ?, ?, ?)")) {

            statement.setLong(1, friendship.getIdUser1());
            statement.setLong(2, friendship.getIdUser2());
            statement.setTimestamp(3, Timestamp.valueOf(friendship.getDate()));
            statement.setString(4, friendship.getFriendshiprequest().toString());
            rowsAffected = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowsAffected > 0 ? Optional.empty() : Optional.of(friendship);
    }

    /**
     * Updates an existing friendship. Friendships typically don't need updates.
     * @param friendship the Friendship object to update
     * @return an empty Optional if the update was successful, or the Friendship if it failed
     */
    @Override
    public Optional<Friendship> update(Friendship friendship) {
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET  date = ?, request= ? WHERE user_id_1 = ? and user_id_2 = ?")) {
            statement.setTimestamp(1, Timestamp.valueOf(friendship.getDate()));
            statement.setString(2,friendship.getFriendshiprequest().toString());
            statement.setLong(3,friendship.getIdUser1());
            statement.setLong(4,friendship.getIdUser2());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
        e.printStackTrace();

            if (rez > 0) {
                return Optional.empty();
            }
        }

        return Optional.of(friendship);
    }



    /**
     * Deletes a friendship between two users.
     * @param id the Tuple of user IDs representing the friendship
     * @return an Optional containing the deleted Friendship if successful, or an empty Optional if not
     */
    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        Optional<Friendship> friendshipToDelete = findOne(id);
        int rowsAffected = -1;

        if (friendshipToDelete.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE (user_id_1 = ? AND user_id_2 = ?) OR (user_id_1 = ? AND user_id_2 = ?)")) {

                statement.setLong(1, id.getLeft());
                statement.setLong(2, id.getRight());
                statement.setLong(3, id.getRight());
                statement.setLong(4, id.getLeft());
                rowsAffected = statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rowsAffected > 0 ? friendshipToDelete : Optional.empty();
    }
}
