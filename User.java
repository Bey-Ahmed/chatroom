import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class User extends UnicastRemoteObject implements ChatUser, Runnable {
    
    private ChatRoom aChatRoom;

    private String pseudo;

    public static Scanner scan; // To avoid declaring multiple System.in variable (handling and closing more "clean" and easy)

    public String getPseudo() throws RemoteException {
        return pseudo;
    }

    public User(ChatRoom aChatRoom, String pseudo) throws RemoteException {
        this.aChatRoom = aChatRoom;
        this.pseudo = pseudo;
        // Pseudo will be checked before creating the User
        this.aChatRoom.subscribe(this);
    }

    /* 
    * We need to synchronize the shared resources to ensure that at a time,
    * only one thread is able to access the shared resource
    * (here the server / ChatRoom)
    */
    @Override
    public synchronized void displayMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    /*
    * Thread launched by the call of start() function
    */
    @Override
    public void run() {
        String message; // Message the User wants to display

        while (true) {
            try {
                if (scan.hasNextLine()) {
                    System.out.print(">>>>> ");
                    message = scan.nextLine().trim();
                    // [Username] : message of the user
                    message = '[' + this.getPseudo() + "] : " + message;
                    this.aChatRoom.postMessage(this.getPseudo(), message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String url = "rmi://localhost/ChatRoom";
        try {
            // Running a ChatRoom (server)
            ChatRoom aChatRoom = (ChatRoom) Naming.lookup(url);

            // Retrieval of the username and non-repetition check
            scan = new Scanner(System.in);
            String pseudo = "";
            do {
                System.out.print("Pseudo : ");
                pseudo = scan.nextLine().trim();
                if (aChatRoom.hasUser(pseudo))
                    System.out.println("Username already chosen!");
            }
            while (aChatRoom.hasUser(pseudo) || pseudo.isEmpty());

            ChatUser client = new User(aChatRoom, pseudo);

            // Advising the other Users (if any) that the actual new User is connected
            String message = "[" + client.getPseudo() + "] connected";
            aChatRoom.postMessage(client.getPseudo(), message);

            // Notifying the actual User that the ChatRoom (server) is available for communications
            System.out.println("[System] Welcome to the ChatRoom!");

            // Creating and starting a new thread with the actual User information
            Thread aThread = new Thread((Runnable) client);
            aThread.start();

            // Listening for an exit attempt of the actual User in order to unregister him before he leaves
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        aChatRoom.unsubscribe(client.getPseudo());

                        // Advising the other Users (if any) that this actual User is disconnected
                        String message = "[" + client.getPseudo() + "] disconnected";
                        aChatRoom.postMessage(client.getPseudo(), message);
                        
                        System.out.println("[System] Goodbye!");

                        scan.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}