import repository.FriendshipRepository;
import repository.UserRepository;
import service.Service;
import ui.Console;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;


public class Main {
    public static void main(String[] args) {
        UserRepository repoUser = new UserRepository( new UserValidator(), "C:\\Users\\Ale\\Desktop\\MAP\\Social_Network\\src\\data\\users.txt");
        FriendshipRepository repoFriendship = new FriendshipRepository(new FriendshipValidator(),"C:\\Users\\Ale\\Desktop\\MAP\\Social_Network\\src\\data\\friendship.txt");

        Service srv = new Service(repoUser, repoFriendship);
        Console ui = new Console(srv);
        ui.run();
    }


}