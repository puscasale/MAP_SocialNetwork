package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import repository.Repository;

import java.util.*;

/**
 * Service class for managing User and Friendship entities.
 * It provides methods to add, remove, and query users and friendships.
 */
public class Service {
    private final Repository<Long, User> userRepo; // Repository for User entities
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepo; // Repository for Friendship entities
    private final Map<Long, List<Long>> adjList = new HashMap<>(); // Adjacency list for friendships
    private final UserValidator userValidator = new UserValidator();
    private final FriendshipValidator friendshipValidator = new FriendshipValidator();

    /**
     * Constructor for Service class.
     * @param userRepo the user repository
     * @param friendshipRepo the friendship repository
     */
    public Service(Repository<Long, User> userRepo, Repository<Tuple<Long, Long>, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        //this.populate(); // Populate users' friends from existing friendships
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

            Friendship f = new Friendship(userId1, userId2);
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
     * Populates users' friend lists based on existing friendships.
     */
    public void populate() {
        //TODO
        for (Friendship f : friendshipRepo.findAll()) {
            Long userId1 = f.getIdUser1(); // Get the first user ID
            Long userId2 = f.getIdUser2(); // Get the second user ID
            for (User u : userRepo.findAll()) {
                if (u.getId().equals(userId1)) {
                    Optional<User> fr = userRepo.findOne(userId2);
                    fr.ifPresent(u::addFriend);// Add friend if it matches
                } else if (u.getId().equals(userId2)) {
                    Optional<User> fr = userRepo.findOne(userId1);
                    fr.ifPresent(u::addFriend);// Add friend if it matches
                }
            }
        }
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
}
