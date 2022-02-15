import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatRoom extends Remote {
    /**
     * Register an user
     * (Check if username not already chosen before the subscription)
     * @param user java.rmi.remote object to store a user
     * @throws RemoteException
     */
    public void subscribe(ChatUser user) throws RemoteException;

    /**
     * Unregister an user using the username since it has to be unique
     * @param pseudo unicity has to be checked before the subscription
     * @throws RemoteException
     */
    public void unsubscribe(String pseudo) throws RemoteException;

    /**
     * Check if given username not already chosen
     * @param pseudo username to compare with the ones existing
     * @return true if the username is already chosen
     * @throws RemoteException
     */
    public boolean hasUser(String pseudo) throws RemoteException;

    /**
     * Broadcast message received from one client to all registered clients except themself
     * @param pseudo username of the user sending the message
     * @param message the message sent
     * @throws RemoteException
     */
    public void postMessage(String pseudo, String message) throws RemoteException;
}
