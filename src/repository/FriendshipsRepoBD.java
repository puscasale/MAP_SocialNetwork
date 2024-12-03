package repository;

import domain.*;
import enums.Friendshiprequest;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipsRepoBD implements FriendshipPagingRepo<Tuple<Long, Long>, Friendship> {
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

    /**
     * Retrieves a paginated list of friendships for a specific user from the database.
     * The method fetches a subset of friendships based on the specified page number and page size,
     * filtering for friendships that are approved and belong to the given user.
     * It also retrieves the total count of such friendships to help with pagination.
     *
     * @param pageable the pagination details (page number and page size)
     * @param user the user whose friendships are to be fetched
     * @return a Page object containing the list of friendships for the current page and the total number of friendships for the user
     */
    @Override
    public Page<Friendship> getUsersFriends(Pageable pageable, User user) {
        List<Friendship> friendshipList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement pageStatement = connection.prepareStatement("SELECT * FROM friendships WHERE ( request LIKE 'APROOVED' AND user_id_1 = ? or user_id_2 = ?)" + "LIMIT ? OFFSET ?");
            PreparedStatement countStatemnt = connection.prepareStatement("SELECT COUNT(*) AS count FROM friendships WHERE request LIKE 'APROOVED' AND user_id_1 = ? or user_id_2 = ?")
        ){
            pageStatement.setLong(1,user.getId());
            pageStatement.setLong(2,user.getId());
            pageStatement.setInt(3,pageable.getPageSize());
            pageStatement.setInt(4,pageable.getPageSize()*pageable.getPageNumber());
            countStatemnt.setLong(1,user.getId());
            countStatemnt.setLong(2,user.getId());

            try(ResultSet pageResultSet = pageStatement.executeQuery();
                ResultSet countResultSet = countStatemnt.executeQuery()
            ){
                while(pageResultSet.next()){
                    Long id1 = pageResultSet.getLong("user_id_1");
                    Long id2 = pageResultSet.getLong("user_id_2");
                    LocalDateTime date = pageResultSet.getTimestamp("date").toLocalDateTime();
                    Friendshiprequest friend_req_status = Friendshiprequest.valueOf(pageResultSet.getString("request"));

                    Friendship friendship = new Friendship(id1, id2, date, friend_req_status);
                    friendship.setId(new Tuple<>(id1, id2));
                    friendshipList.add(friendship);

                }
                int count = 0;
                if(countResultSet.next()){
                    count = countResultSet.getInt("count");
                }
                return new Page<>(friendshipList, count);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a paginated list of all approved friendships from the database.
     * The method fetches a subset of friendships based on the specified page number and page size,
     * filtering for friendships that are approved.
     * It also retrieves the total count of approved friendships to help with pagination.
     *
     * @param pageable the pagination details (page number and page size)
     * @return a Page object containing the list of friendships for the current page and the total number of approved friendships
     */
    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        List<Friendship> friendshipList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement pageStatement = connection.prepareStatement("SELECT * FROM friendships WHERE request LIKE 'APROOVED'" + "LIMIT ? OFFSET ?");
            PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM friendships WHERE request LIKE 'APROOVED'")
        ){
            pageStatement.setInt(1,pageable.getPageSize());
            pageStatement.setInt(2,pageable.getPageNumber()*pageable.getPageSize());

            try(ResultSet pageResultSet = pageStatement.executeQuery();
                ResultSet countResultSet = countStatement.executeQuery()
            ){
                while(pageResultSet.next()){
                    Long id1 = pageResultSet.getLong("user_id_1");
                    Long id2 = pageResultSet.getLong("user_id_2");
                    LocalDateTime date = pageResultSet.getTimestamp("date").toLocalDateTime();
                    Friendshiprequest friend_req_status = Friendshiprequest.valueOf(pageResultSet.getString("request"));

                    Friendship friendship = new Friendship(id1, id2, date, friend_req_status);
                    friendship.setId(new Tuple<>(id1, id2));
                    friendshipList.add(friendship);
                }
                int count = 0;
                if(countResultSet.next()){
                    count = countResultSet.getInt("count");
                }
                return new Page<>(friendshipList, count);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
