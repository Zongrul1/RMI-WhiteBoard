package whiteboard_remote;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

public interface iClient extends Remote{	
	public void messageFromServer(String message) throws RemoteException;
	public void updateUserList(String[] currentUsers) throws RemoteException;
	public boolean judge(String str) throws RemoteException;
	public void load(byte[] b) throws RemoteException;
	public void reject(String str)throws RemoteException;
	public void info(String str) throws RemoteException;
}
