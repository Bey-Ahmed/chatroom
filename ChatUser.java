import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatUser extends Remote {
    /**
     * @return the pseudo of the current user
     * @throws RemoteException
     */
    public String getPseudo() throws RemoteException;

    /**
     * Display the message from another user sent by the server (ChatRoom)
     * @param message
     * @throws RemoteException
     */
    public void displayMessage(String message) throws RemoteException;
}
