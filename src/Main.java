import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.FriendshipRepository;
import repository.FriendshipsRepoBD;
import repository.UserRepoBD;
import repository.Repository;
import service.Service;
import ui.Console;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;


public class Main {
    public static void main(String[] args) {
        //UserRepository repoUser = new UserRepository( new UserValidator(), "C:\\Users\\Ale\\Desktop\\MAP\\Social_Network\\src\\data\\users.txt");
        //FriendshipRepository repoFriendship = new FriendshipRepository(new FriendshipValidator(),"C:\\Users\\Ale\\Desktop\\MAP\\Social_Network\\src\\data\\friendship.txt");

        String username="postgres";
        String pasword="alesefa";
        String url="jdbc:postgresql://localhost:5432/postgres";
        Repository<Long, User> userRepoBD = new UserRepoBD(url,username, pasword,  new UserValidator());
        Repository<Tuple<Long,Long>, Friendship> friendshipRepoBD = new FriendshipsRepoBD(url, username, pasword);
        Service srv = new Service(userRepoBD, friendshipRepoBD);
        Console ui = new Console(srv);
        ui.run();


    }


}