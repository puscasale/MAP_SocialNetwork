# MAP_SocialNetwork
# CorgiNetwork 

Welcome to the CorgiNetwork! This is a JavaFX-based app that allows users to interact with their friends through real-time messaging. The application is designed with a user-friendly interface and includes features such as managing friends, sending messages, and navigating between different views like the user profile, friends list, and friend requests.

Whether you are looking to stay connected with your friends or just explore a simple chat app, this project provides a great foundation for messaging systems with JavaFX.

## Features

The Chat Application comes packed with a variety of features to make communication with friends smooth and enjoyable:

- **User Authentication**: Log in with your account or create an account to start using the app .
- **Friend Management**: Add, remove, and view your friends in a dedicated friend list.
- **Messaging**: Send and receive messages to and from your friends. Messages are displayed in a sleek chat window.
- **Profile View**: View and edit your profile with basic details.
- **Friend Requests**: Send and accept friend requests to build your social network.
- **Navigation**: Easily switch between the chat screen, profile, friends, and friend requests view.

## How It Works

### 1. **Login Screen**
The application starts with a login screen where users can input their credentials. Once authenticated, users can proceed to the main chat interface.

### 2. **Chat View**
Once logged in, the user is presented with a list of their friends in the `ChatView`. Each friend’s name is displayed, and by clicking on a friend, a dedicated chat window is opened to send messages.

### 3. **Sending and Receiving Messages**
In the chat window, users can send text messages and view messages from their friends. Messages appear in chronological order, providing a seamless chat experience.

### 4. **Profile Management**
Users can access their profile, which displays their personal details such as name, photo, and status. From here, users can update their information and view their friends list.

### 5. **Friend Requests**
Users can manage incoming and outgoing friend requests through the `RequestsView`. This allows users to accept or reject requests and build their social network.

### 6. **Friends List**
The `FriendsView` allows users to see all their friends in one place. Each friend can be selected to open a chat window or view more details.

## App Layout

### Screenshots

Here are some screenshots of the application to give you an idea of what the user interface looks like:

1. **Login Screen**
   ![login](https://github.com/user-attachments/assets/fbfe157b-6f41-40e1-8bb8-f87517a122c7)

   ![singup](https://github.com/user-attachments/assets/c2f27314-a87b-42fa-b95c-b41b3d2830e9)



3. **Chat View**
   ![chat](https://github.com/user-attachments/assets/7891ced1-191c-4753-982e-76e9e1891c85)

4. **Chat with Friend**
  ![mess](https://github.com/user-attachments/assets/0953210d-1b50-4079-9151-a452944889c3)

5. **Profile View**
   ![main](https://github.com/user-attachments/assets/1aae22a5-045a-4135-84cb-10f3c73a5c35)

6. **Friend list**
   ![freinds](https://github.com/user-attachments/assets/2c70f6b9-53bc-4287-97b0-e4081008fc12)

7. **Friend requests**
   ![request](https://github.com/user-attachments/assets/68e634b0-f676-44bc-9d2a-0f5479b46792)



## Technical Details

### Architecture
The app follows a modular architecture with the following components:

- **Model**: Represents the data (e.g., `User`, `Message`).
- **Service**: A service layer responsible for managing business logic like sending messages, fetching friends, and handling friend requests.
- **Controller**: The JavaFX controllers that handle user interactions and UI updates (e.g., `ChatController`, `MessageController`, `MainController`).
- **View**: The JavaFX FXML files that define the UI layout (e.g., `ChatView.fxml`, `MessageView.fxml`).

### Technologies Used
- **Java 11**: The primary programming language.
- **JavaFX**: For building the GUI and handling events.
- **FXML**: For defining the UI structure and layout.
- **ObservableList**: For managing dynamic lists of chat items.
- **Threads**: For handling background tasks like sending messages asynchronously.

## Requirements

To run this project on your machine, make sure you have the following:

- **Java 11** or higher.
- **JavaFX SDK** (configured in the project).
- An IDE that supports JavaFX (such as IntelliJ IDEA, Eclipse, or NetBeans).
  

