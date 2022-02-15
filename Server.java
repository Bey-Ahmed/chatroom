import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends UnicastRemoteObject implements ChatRoom {
    
    // Database of the Users for a server instance to keep track of them
    private ArrayList<ChatUser> users;

    public Server() throws RemoteException {
        users = new ArrayList<ChatUser>();
    }

    @Override
    public void subscribe(ChatUser user) throws RemoteException {
        users.add(user);
    }

    @Override
    public void unsubscribe(String pseudo) throws RemoteException {
        for (ChatUser user : users) {
            if (user.getPseudo().equals(pseudo)) {
                users.remove(user);
                break;
            }
        }
    }

    @Override
    public boolean hasUser(String pseudo) throws RemoteException {
        for (ChatUser user : users) {
            if (user.getPseudo().equals(pseudo))
                return true;
        }
        return false;
    }
    
    /* 
    * We need to synchronize the shared resources to ensure that at a time,
    * only one thread is able to access the shared resource
    * (here the server / ChatRoom)
    */
    @Override
    public synchronized void postMessage(String pseudo, String message) throws RemoteException {
        for (ChatUser user : users) {
            if (!user.getPseudo().equals(pseudo))
                user.displayMessage(message);
        }
    }

    public static void main(String[] args) {
        try {
            // Rebinding to be able to have rmi://localhost/ChatRoom
            Naming.rebind("ChatRoom", new Server());
            
            System.out.println("Server ready");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
