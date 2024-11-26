package domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Message extends Entity<Long> {
    private Long id; // Unique identifier for the message
    private User from; // The sender of the message
    private List<User> to; // List of recipients of the message
    private String message; // Content of the message
    private LocalDateTime date; // Timestamp when the message was sent
    private Message reply; // Reference to the message being replied to (if any)

    /**
     * Constructor to create a Message with specified sender, recipients, message content, and timestamp.
     * @param from the sender of the message.
     * @param to the list of recipients of the message.
     * @param message the content of the message.
     * @param date the timestamp when the message was sent.
     */
    public Message(User from, List<User> to, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = null;
    }

    /**
     * Constructor to create a Message with specified sender, recipients, and message content.
     * The timestamp is set to the current time by default.
     * @param from the sender of the message.
     * @param to the list of recipients of the message.
     * @param message the content of the message.
     */
    public Message(User from, List<User> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.reply = null;
    }

    /**
     * Retrieves the unique identifier of the message.
     * @return the message ID.
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the message.
     * @param id the message ID to set.
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the sender of the message.
     * @return the sender as a User object.
     */
    public User getFrom() {
        return from;
    }

    /**
     * Sets the sender of the message.
     * @param from the User object representing the sender.
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * Retrieves the list of recipients of the message.
     * @return the list of recipients as User objects.
     */
    public List<User> getTo() {
        return to;
    }

    /**
     * Sets the list of recipients of the message.
     * @param to the list of User objects representing the recipients.
     */
    public void setTo(List<User> to) {
        this.to = to;
    }

    /**
     * Retrieves the content of the message.
     * @return the message content as a String.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the content of the message.
     * @param message the message content as a String.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves the timestamp when the message was sent.
     * @return the timestamp as a LocalDateTime object.
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Retrieves the message being replied to, if any.
     * @return the referenced Message object or null if there's no reply.
     */
    public Message getReply() {
        return reply;
    }

    /**
     * Sets the reference to the message being replied to.
     * @param reply the referenced Message object.
     */
    public void setReply(Message reply) {
        this.reply = reply;
    }

    /**
     * Provides a string representation of the Message object.
     * The format includes the sender, the content, and the timestamp.
     * @return a formatted string representation of the message.
     */
    @Override
    public String toString() {
        return "(" + getFrom().toString() + ")\n" + message + "\n(" + date.format(DateTimeFormatter.ofPattern("hh:mm dd/MM/yy")) + ")";
    }
}
