package whiteboard_server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import whiteboard_remote.iwhiteboard;

public class server{
	public static void main(String args[]) {
		try {
			iwhiteboard wb = new whiteboardServer();
			int port = Integer.parseInt(args[0]);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("whiteboard", wb);            
            System.out.println("the port: "+ port + " \nserver ready");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
