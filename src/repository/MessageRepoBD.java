package repository;

import domain.Message;
import domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MessageRepoBD implements Repository<Long, Message>{
    private static String url;
    private static String username;
    private static String password;
    private final Repository<Long, User> userRepository;

    public MessageRepoBD(Repository<Long, User> userRepository, String url, String username, String password) {
        this.userRepository = userRepository;
        this.url = url;
        this.username = username;
        this.password = password;
    }

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

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Message> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Messages WHERE id_message = ?")) {
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Long id_to = resultSet.getLong("id_to");
                Long id_from = resultSet.getLong("id_from");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long reply_id = resultSet.getLong("reply_id");

                if(resultSet.wasNull()){
                    reply_id = null;
                }
                Message msg = new Message(userRepository.findOne(id_from).get(), Collections.singletonList(userRepository.findOne(id_to).get()),message,date);
                msg.setId(id);

                if(reply_id != null){
                    findOneNoReply(reply_id).ifPresent(msg::setReply);
                }
                return Optional.of(msg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();

    }

    @Override
    public Iterable<Message> findAll() {

        List<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Messages");) {

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

                if(reply_id != null) {
                    Message reply = findOne(reply_id).orElse(null);
                    messageDB.setReply(reply);
                }

                messages.add(messageDB);

            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Messages(id_to, id_from, message, date, reply_id) VALUES (?, ?, ?, ?, ?)");) {

            statement.setLong(1, entity.getTo().getFirst().getId());
            statement.setLong(2, entity.getFrom().getId());
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            if(entity.getReply() == null){
                statement.setNull(5, Types.NULL);
            } else{
                statement.setLong(5, entity.getReply().getId());
            }

            statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.of(entity);

    }

    @Override
    public Optional<Message> delete(Long id) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Messages WHERE id_message = ?");) {

            statement.setLong(1, id);
            statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();
    }


    @Override
    public Optional<Message> update(Message entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE Messages SET id_to = ?, id_from = ?, message = ?, date = ?, reply_id = ? WHERE id_message = ?");) {

            statement.setLong(1, entity.getTo().getFirst().getId());
            statement.setLong(2, entity.getFrom().getId());
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            if(entity.getReply() == null){
                statement.setNull(5, Types.NULL);
            }else{
                statement.setLong(5, entity.getReply().getId());
            }

            statement.setLong(6, entity.getId());

            statement.executeUpdate();
            return Optional.empty();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.of(entity);
    }

}
