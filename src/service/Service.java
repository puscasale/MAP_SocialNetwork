package service;

import domain.*;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import domain.validators.ValidationException;
import enums.Friendshiprequest;
import repository.FriendshipPagingRepo;
import repository.Repository;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing User and Friendship entities.
 * It provides methods to add, remove, and query users and friendships.
 */
public class Service {
    private final Repository<Long, User> userRepo; // Repository for User entities
    private final FriendshipPagingRepo<Tuple<Long, Long>, Friendship> friendshipRepo;// Repository for Friendship entities
    private final Repository<Long, Message> messageRepo;
    private final Map<Long, List<Long>> adjList = new HashMap<>(); // Adjacency list for friendships
    private final UserValidator userValidator = new UserValidator();
    private final FriendshipValidator friendshipValidator = new FriendshipValidator();

    /**
     * Constructor for Service class.
     * @param userRepo the user repository
     * @param friendshipRepo the friendship repository
     */
    public Service(Repository<Long, User> userRepo, FriendshipPagingRepo<Tuple<Long, Long>, Friendship> friendshipRepo, Repository<Long, Message> messageRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.messageRepo = messageRepo;

        buildAdjacencyList(); // Build the adjacency list for friendship connections
    }

    /**
     * Gets all users from the user repository.
     * @return an iterable collection of User entities
     */
    public Iterable<User> getUsers() {
        return userRepo.findAll();
    }

    /**
     * Gets all friendships from the friendship repository.
     * @return an iterable collection of Friendship entities
     */
    public Iterable<Friendship> getFriendships() {
        return friendshipRepo.findAll();
    }

    /**
     * Builds the adjacency list from existing friendships.
     * This method populates the adjList with user connections.
     */
    private void buildAdjacencyList() {
        for (Friendship friendship : friendshipRepo.findAll()) {
            Long userId1 = friendship.getIdUser1(); // Get the first user's ID
            Long userId2 = friendship.getIdUser2(); // Get the second user's ID
            adjList.computeIfAbsent(userId1, k -> new ArrayList<>()).add(userId2); // Add user2 to user1's list
            adjList.computeIfAbsent(userId2, k -> new ArrayList<>()).add(userId1); // Add user1 to user2's list
        }
    }

    /**
     * Adds a new user to the user repository.
     *
     * @param user the User entity to be added
     */
    public void addUser(User user) {
        userValidator.validate(user);
        userRepo.save(user);
    }

    /**
     * Removes a user and all their friendships from the repository.
     * @param id the ID of the user to be removed
     */
    public void removeUser(Long id) {
        Optional<User> u = userRepo.findOne(id);

        u.ifPresent(user -> {
            List<Tuple<Long,Long>> lst = new ArrayList<>();

            user.getFriends().forEach(f ->
                    lst.add(new Tuple<>(f.getId(), id)));
            lst.forEach(tuple ->
                    removeFriendship(tuple.getLeft(), tuple.getRight()));
            adjList.remove(id);
            userRepo.delete(id);
        });

    }



    /**
     * Adds a friendship between two users.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     */
    public void addFriendship(Long userId1, Long userId2) {
        Optional<User> u1 = userRepo.findOne(userId1);
        Optional<User> u2 = userRepo.findOne(userId2);

        u1.ifPresent(user1 -> u2.ifPresent(user2 -> {
            user1.addFriend(user2);
            user2.addFriend(user1);

            Friendship f = new Friendship(userId1, userId2, LocalDateTime.now());
            f.setId(new Tuple<>(userId1, userId2));

            friendshipRepo.save(f);

            // Update the adjacency list
            adjList.computeIfAbsent(userId1, k -> new ArrayList<>()).add(userId2);
            adjList.computeIfAbsent(userId2, k -> new ArrayList<>()).add(userId1);
        }));
    }


    /**
     * Removes the friendship between two users.
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     */
    public void removeFriendship(Long userId1, Long userId2) {
        Optional<User> u1 = userRepo.findOne(userId1);
        Optional<User> u2 = userRepo.findOne(userId2);
        u1.ifPresent(user1 -> u2.ifPresent(user2 -> {
            user2.removeFriend(user1);
            user1.removeFriend(user2);

            friendshipRepo.delete(new Tuple<>(userId1, userId2));

            adjList.getOrDefault(userId1, new ArrayList<>()).remove(userId2);
            adjList.getOrDefault(userId2, new ArrayList<>()).remove(userId1);

            System.out.println("Friendship removed between " + userId1 + " and " + userId2);
        }));
    }


    /**
     * Counts the number of connected components (communities) in the friendship graph.
     * @return the number of communities
     */
    public int getNumberOfCommunities() {
        Set<Long> visited = new HashSet<>(); // To keep track of visited users
        int numComponents = 0; // Counter for number of components

        for (User user : userRepo.findAll()) {
            if (!visited.contains(user.getId())) { // If user is not visited, it starts a new component
                numComponents++;
                dfs(user.getId(), visited); // Perform DFS to mark all reachable users
            }
        }
        return numComponents; // Return the number of connected components
    }

    /**
     * Performs Depth First Search to visit all users in the same connected component.
     * @param userId the starting user ID
     * @param visited the set of visited user IDs
     */
    private void dfs(Long userId, Set<Long> visited) {
        visited.add(userId); // Mark the current user as visited
        for (Long friendId : adjList.getOrDefault(userId, new ArrayList<>())) {
            if (!visited.contains(friendId)) {
                dfs(friendId, visited); // Visit the friend if not visited
            }
        }
    }

    /**
     * Finds the most social community (the connected component with the longest path).
     * @return a list of user IDs in the most social community
     */
    public List<Long> getMostSocialCommunity() {
        List<Long> longestPath = new ArrayList<>(); // To store the longest path found
        Set<Long> visited = new HashSet<>(); // To keep track of visited users

        for (User user : userRepo.findAll()) {
            if (user != null && !visited.contains(user.getId())) { // Check if the user is valid and not visited
                List<Long> currentPath = findLongestPath(user.getId()); // Find the longest path from this user
                if (currentPath.size() > longestPath.size()) { // Update the longest path if a longer one is found
                    longestPath = currentPath;
                }
                visited.addAll(currentPath); // Mark all nodes in this component as visited
            }
        }
        return longestPath; // Return the longest path found
    }

    /**
     * Finds the longest path in a connected component using Breadth First Search.
     * @param startNode the starting user ID for the search
     * @return a list of user IDs in the longest path
     */
    private List<Long> findLongestPath(Long startNode) {
        Queue<Long> queue = new LinkedList<>(); // Queue for BFS
        Map<Long, Long> distances = new HashMap<>(); // Distance from the start node
        Map<Long, Long> predecessors = new HashMap<>(); // Predecessors to reconstruct the path

        queue.add(startNode); // Start with the initial node
        distances.put(startNode, 0L); // Distance to itself is zero
        Long farthestNode = startNode; // Track the farthest node found

        // BFS to find the farthest node
        while (!queue.isEmpty()) {
            Long currentNode = queue.poll(); // Get the current node from the queue
            for (Long neighbor : adjList.getOrDefault(currentNode, new ArrayList<>())) {
                if (!distances.containsKey(neighbor)) { // If neighbor has not been visited
                    distances.put(neighbor, distances.get(currentNode) + 1); // Set its distance
                    predecessors.put(neighbor, currentNode); // Set its predecessor
                    queue.add(neighbor); // Add to the queue for further exploration

                    // Update the farthest node if needed
                    if (distances.get(neighbor) > distances.get(farthestNode)) {
                        farthestNode = neighbor;
                    }
                }
            }
        }

        // Reconstruct the path from the farthest node to the start node
        List<Long> path = new ArrayList<>();
        Long node = farthestNode; // Start from the farthest node
        while (node != null) {
            path.add(node); // Add the node to the path
            node = predecessors.get(node); // Move to the predecessor
        }
        Collections.reverse(path); // Reverse the path to get from start to end
        return path; // Return the reconstructed path
    }

    /**
     * Validates login credentials.
     * @param email the user's email
     * @param password the user's password
     * @return the User object if credentials are valid, null otherwise
     */
    public User login(String email, String password) {
        for (User user : userRepo.findAll()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user; // Return user if email and password match
            }
        }
        return null; // Return null if no matching user is found
    }

    /**
     * Retrieves the list of friends for a given user.
     * A user can be added as a friend by checking all friendships and matching user IDs.
     * @param user the user whose friends are to be retrieved
     * @return a list of friends of the given user
     */
    public List<User> getFriends(User user){

        List<User> friends = new ArrayList<>();

        for (Friendship f : friendshipRepo.findAll()) {
            if(f.getIdUser1().equals(user.getId()) && f.getFriendshiprequest().name().equals("APROOVED")){ // adauga de doua or
                friends.add(userRepo.findOne(f.getIdUser2()).get());
                User utilizator = userRepo.findOne(f.getIdUser1()).get();
                userRepo.findOne(f.getIdUser2()).get().addFriend(utilizator);

            }
            else if(f.getIdUser2().equals(user.getId()) && f.getFriendshiprequest().name().equals("APROOVED")){
                friends.add(userRepo.findOne(f.getIdUser1()).get());
                User utilizator = userRepo.findOne(f.getIdUser2()).get();
                userRepo.findOne(f.getIdUser1()).get().addFriend(utilizator);
            }
        }
        return friends;

    }

    /**
     * Searches for a user by their first and last name.
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @return the User object if found, null otherwise
     */
    public User findUserByName(String firstName, String lastName) {
        for(User u : userRepo.findAll()){
            if(u.getFirstName().equals(firstName) && u.getLastName().equals(lastName)){
                return u;
            }
        }
        return null;
    }

    /**
     * Finds a user in the database by their ID.
     *
     * @param idUser The ID of the user to be searched for.
     * @return An `Optional` containing the found user, or empty if the user does not exist.
     */
    public Optional<User> find_user(Long idUser) {
        return userRepo.findOne(idUser);
    }

    /**
     * Manages a friend request by updating its status.
     *
     * @param friendship The friendship object that needs to be managed.
     * @param friendshipRequest The new status of the friendship request (e.g., ACCEPTED, REJECTED).
     * @throws RuntimeException If the friendship does not exist or is not in a PENDING state.
     */
    public void manageFriendRequest(Friendship friendship, Friendshiprequest friendshipRequest) {
        try {
            if (!friendshipRepo.findOne(friendship.getId()).isPresent()) {
                throw new Exception("Friendship doesn't exist!");
            } else if (friendship.getFriendshiprequest() != Friendshiprequest.PENDING) {
                throw new Exception("Friendship is not PENDING!");
            }
            friendship.setFriendshiprequest(friendshipRequest);
            friendshipRepo.update(friendship);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Creates a new friend request between two specified users by their IDs.
     *
     * @param id1 The ID of the first user.
     * @param id2 The ID of the second user.
     */
    public void createFriendshipRequest(Long id1, Long id2) {
        addFriendship(id1, id2);
    }


    /**
     * Retrieves all messages exchanged between two users, sorted chronologically.
     *
     * @param user The user whose messages are being queried.
     * @param friend The friend with whom the messages were exchanged.
     * @return A list of messages between the user and the friend, sorted by date.
     */
    public List<Message> getMessagesBetween(User user, User friend) {
        Collection<Message> messages = (Collection<Message>) messageRepo.findAll();
        return messages.stream()
                .filter(m -> (m.getFrom().equals(user) && m.getTo().contains(userRepo.findOne(friend.getId()).get()))
                        || (m.getFrom().equals(friend) && m.getTo().contains(userRepo.findOne(user.getId()).get())))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Adds a new message from one user to another and updates replies if applicable.
     *
     * @param from The user sending the message.
     * @param to The user receiving the message.
     * @param msg The content of the message.
     * @return `true` if the message was successfully added, `false` otherwise.
     */
    public boolean addMessage(User from, User to, String msg) {
        try {
            Message message = new Message(from, Collections.singletonList(to), msg);
            messageRepo.save(message);

            List<Message> messagesBetweenUsers = getMessagesBetween(from, to);
            if (messagesBetweenUsers.size() > 1) {
                Message oldReplyMessage = messagesBetweenUsers.get(messagesBetweenUsers.size() - 2);
                oldReplyMessage.setReply(messagesBetweenUsers.get(messagesBetweenUsers.size() - 1));
                messageRepo.update(oldReplyMessage);
            }

            return true;
        } catch (ValidationException ve) {
            System.out.println("User error");
        } catch (Exception ex) {
            System.out.println("Message error");
        }

        return false;
    }


    /**
     * Finds a user in the database by their email address.
     *
     * @param emailInput The email address of the user to search for.
     * @return The user with the specified email, or `null` if no such user exists.
     */
    public User findUserByEmail(String emailInput) {
        for (User u : userRepo.findAll()) {
            if (u.getEmail().equals(emailInput)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Updates the data of an existing user in the database.
     *
     * @param user The user object with the updated information.
     * @return An `Optional` containing the user before the update, or empty if the update failed.
     */
    public Optional<User> update_user(User user) {
        Optional<User> oldUser = userRepo.findOne(user.getId());
        if (oldUser.isPresent()) {
            Optional<User> newUser = userRepo.update(user);
            if (newUser.isEmpty()) {
                return newUser;
            }
        }

        return oldUser;
    }


    /**
     * Retrieves a list of pending friendships for a specific user.
     * The method fetches all friendships and filters them to find those where the user is the second participant
     * and the friendship request status is "PENDING".
     *
     * @param userId the ID of the user whose pending friendships are to be fetched
     * @return a List of pending friendships for the specified user
     */
    public List<Friendship> getPendingFriendships(Long userId) {

        Iterable<Friendship> allFriendships = friendshipRepo.findAll();

        List<Friendship> pendingFriendships = new ArrayList<>();
        for (Friendship friendship : allFriendships) {
            if ( friendship.getIdUser2().equals(userId)&& friendship.getFriendshiprequest().name().equals("PENDING")) {
                pendingFriendships.add(friendship);
            }
        }

        return pendingFriendships;
    }

    /**
     * Retrieves a paginated list of all friendships from the database
     * @param pageable the pagination details (page number and page size)
     * @return a Page object containing the list of friendships for the current page and the total number of friendships
     */
    public Page<Friendship> getAllFriendships(Pageable pageable){

        return friendshipRepo.findAllOnPage(pageable);
    }

    /**
     * Retrieves a paginated list of friendships for a specific user
     * @param pageable the pagination details (page number and page size)
     * @param user the user whose friendships are to be fetched
     * @return a Page object containing the list of friendships for the user for the current page and the total number of friendships
     */
    public Page<Friendship> findUsersFriends(Pageable pageable, User user){
        return friendshipRepo.getUsersFriends(pageable, user);

    }

}
