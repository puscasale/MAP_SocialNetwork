package repository;

import domain.Message;
import domain.Page;
import domain.Pageable;
import domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageRepoBD implements MessagePagingRepo<Long, Message> {
    private static String url; // Database connection URL
    private static String username; // Database username
    private static String password; // Database password
    private final Repository<Long, User> userRepository; // Repository to manage User entities

    /**
     * Constructor for MessageRepoBD.
     * Initializes the repository with the database connection details and a user repository.
     * @param userRepository the user repository to fetch user data.
     * @param url the database connection URL.
     * @param username the database username.
     * @param password the database password.
     */
    public MessageRepoBD(Repository<Long, User> userRepository, String url, String username, String password) {
        this.userRepository = userRepository;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Finds a message by its ID without resolving its reply.
     * @param id the ID of the message to find.
     * @return an Optional containing the found Message or empty if not found.
     */
    public Optional<Message> findOneNoReply(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Messages WHERE id_message = ?")) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id_to = resultSet.getLong("id_to");
                Long id_from = resultSet.getLong("id_from");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Message messageDB = new Message(userRepository.findOne(id_from).get(),
                        Collections.singletonList(userRepository.findOne(id_to).get()),
                        message,
                        date);
                messageDB.setId(id);
                return Optional.of(messageDB);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Finds a message by its ID and resolves its reply if present.
     * @param id the ID of the message to find.
     * @return an Optional containing the found Message or empty if not found.
     */
    @Override
    public Optional<Message> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Messages WHERE id_message = ?")) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long id_to = resultSet.getLong("id_to");
                Long id_from = resultSet.getLong("id_from");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long reply_id = resultSet.getLong("reply_id");

                if (resultSet.wasNull()) {
                    reply_id = null;
                }
                Message msg = new Message(userRepository.findOne(id_from).get(),
                        Collections.singletonList(userRepository.findOne(id_to).get()),
                        message,
                        date);
                msg.setId(id);

                if (reply_id != null) {
                    findOneNoReply(reply_id).ifPresent(msg::setReply);
                }
                return Optional.of(msg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Retrieves all messages from the database.
     * Resolves replies for each message if present.
     * @return an Iterable containing all messages.
     */
    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Messages")) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id_message = resultSet.getLong("id_message");
                Long id_to = resultSet.getLong("id_to");
                Long id_from = resultSet.getLong("id_from");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long reply_id = resultSet.getLong("reply_id");

                if (resultSet.wasNull()) {
                    reply_id = null;
                }

                User from = userRepository.findOne(id_from).get();
                List<User> to = Collections.singletonList(userRepository.findOne(id_to).get());

                Message messageDB = new Message(from, to, message, date);
                messageDB.setId(id_message);

                if (reply_id != null) {
                    Message reply = findOne(reply_id).orElse(null);
                    messageDB.setReply(reply);
                }

                messages.add(messageDB);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    /**
     * Saves a new message to the database.
     * @param entity the message to save.
     * @return an Optional containing the saved message.
     */
    @Override
    public Optional<Message> save(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Messages(id_to, id_from, message, date, reply_id) VALUES (?, ?, ?, ?, ?)")) {

            statement.setLong(1, entity.getTo().get(0).getId());
            statement.setLong(2, entity.getFrom().getId());
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            if (entity.getReply() == null) {
                statement.setNull(5, Types.NULL);
            } else {
                statement.setLong(5, entity.getReply().getId());
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.of(entity);
    }

    /**
     * Deletes a message from the database by its ID.
     * @param id the ID of the message to delete.
     * @return an Optional containing the deleted message if needed.
     */
    @Override
    public Optional<Message> delete(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Messages WHERE id_message = ?")) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Updates an existing message in the database.
     * @param entity the message to update.
     * @return an Optional containing the updated message.
     */
    @Override
    public Optional<Message> update(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE Messages SET id_to = ?, id_from = ?, message = ?, date = ?, reply_id = ? WHERE id_message = ?")) {

            statement.setLong(1, entity.getTo().get(0).getId());
            statement.setLong(2, entity.getFrom().getId());
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            if (entity.getReply() == null) {
                statement.setNull(5, Types.NULL);
            } else {
                statement.setLong(5, entity.getReply().getId());
            }

            statement.setLong(6, entity.getId());
            statement.executeUpdate();

            return Optional.empty();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.of(entity);
    }

    @Override
    public Page<Message> findAll(Pageable pageable, Long user1Id, Long user2Id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement pagePreparedStatement = connection.prepareStatement
                     ("SELECT messages.*, message_recipients.id_to " +
                             "FROM messages " +
                             "LEFT JOIN message_recipients ON messages.id = message_recipients.id_message " +
                             "WHERE (messages.id_from = ? AND message_recipients.id_to = ?) " +
                             "OR (messages.id_from = ? AND message_recipients.id_to = ?) " +
                             "LIMIT ? OFFSET ?");

             PreparedStatement countPreparedStatement = connection.prepareStatement
                     ("SELECT COUNT(*) AS count " +
                             "FROM messages " +
                             "LEFT JOIN message_recipients ON messages.id = message_recipients.id_message " +
                             "WHERE (messages.id_from = ? AND message_recipients.id_to = ?) " +
                             "OR (messages.id_from = ? AND message_recipients.id_to = ?) ");

        ) {
            pagePreparedStatement.setLong(1, user1Id);
            pagePreparedStatement.setLong(2, user2Id);
            pagePreparedStatement.setLong(3, user2Id);
            pagePreparedStatement.setLong(4, user1Id);
            pagePreparedStatement.setInt(5, pageable.getPageSize());
            pagePreparedStatement.setInt(6, pageable.getPageSize() * pageable.getPageNumber());

            countPreparedStatement.setLong(1, user1Id);
            countPreparedStatement.setLong(2, user2Id);
            countPreparedStatement.setLong(3, user2Id);
            countPreparedStatement.setLong(4, user1Id);
            try (ResultSet pageResultSet = pagePreparedStatement.executeQuery();
                 ResultSet countResultSet = countPreparedStatement.executeQuery();) {

                Map<Long, Message> messageMap = new HashMap<>();

                while (pageResultSet.next()) {
                    Long messageId = pageResultSet.getLong("id");
                    messageMap.computeIfAbsent(messageId, k -> {
                        return findOne(messageId).get();
                    });

                }

                List<Message> messageList = new ArrayList<>(messageMap.values());
                int totalCount = 0;
                if (countResultSet.next()) {
                    totalCount = countResultSet.getInt("count");
                }

                return new Page<>(messageList, totalCount);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
