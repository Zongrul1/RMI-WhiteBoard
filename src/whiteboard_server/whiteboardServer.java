package whiteboard_server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import whiteboard_remote.iClient;
import whiteboard_remote.iwhiteboard;

public class whiteboardServer extends UnicastRemoteObject implements iwhiteboard{
	private ArrayList<user> users;
	private byte[] b;
	protected whiteboardServer() throws RemoteException {
		super();
		users = new ArrayList<user>();
		// TODO Auto-generated constructor stub
	}	
	@Override
	public void draw(byte[] b) throws RemoteException {
		// TODO Auto-generated method stub
		this.b = b;
		for(user c : users){
			try {
				c.getClient().load(b);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}
	@Override
	public synchronized void registerListener(String[] details) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println(details[0] + " has joined the chat session");
		System.out.println(details[0] + "'s hostname : " + details[1]);
		System.out.println(details[0] + "'sRMI service : " + details[2]);
		//reverse link
		try {
			iClient nextClient = (iClient)Naming.lookup("rmi://" + details[1] + "/" + details[2]);
			users.add(new user(details[0], nextClient));
			if(users.size() > 1) {
				if(judge(users.get(users.size() - 1).getName())) {
					users.remove(users.size() - 1); // remove before send the message to the client
					nextClient.reject("the manager does not approved your request\n");
					return ;
				}
			}
			if(b != null) {//synchronize the board with new user
				nextClient.load(b);
			}
			nextClient.messageFromServer("[Server] : Hello " + details[0] + " you are now free to chat.\n");			
			broadcast("[Server] : " + details[0] + " has joined the group.\n");			
			updateUserList();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//update user list
	private void updateUserList() {
		//get the list
		String[] currentUsers = new String[users.size()];
		for(int i = 0; i< currentUsers.length; i++){
			currentUsers[i] = users.get(i).getName();
		}
		//update the list 
		for(user c : users){
			try {
				c.getClient().updateUserList(currentUsers);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}
	@Override
	public boolean check() throws RemoteException {
		// TODO Auto-generated method stub
		if(users.size() == 0) {
			return false;
		}
		else {
			return true;
		}
	}
	//approve whether the user join the board or not
	@Override
	public boolean judge(String str) throws RemoteException {
		// TODO Auto-generated method stub
		return users.get(0).getClient().judge(str);
	}
	//remove
	@Override
	public void removeUser(String username) throws RemoteException {
		// TODO Auto-generated method stub
		int index = 0;
		for(int i = 0;i < users.size();i++) {
			if(users.get(i).getName().equals(username)) {
				index = i;
			}
		}
		if(index == 0) {
			users.get(0).getClient().info("Do not have this username");
			//users.get(0).getClient().messageFromServer("Do not have this username");
		}
		else {
			iClient temp = users.get(index).getClient();
			String str = users.get(index).getName();
			users.remove(index);
			users.get(0).getClient().info("remove success!");
			broadcast("[Server] :" + str + " has been removed!\n");
			updateUserList();
			Thread t = new Thread(()->{
				try {
					temp.reject("sorry, you have been kicked out by the manager\n");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				});
				t.start();
		}
	}
	//send to all
	@Override
	public void broadcast(String msg) {
		// TODO Auto-generated method stub
		for(user c : users){
			   try {
			    c.getClient().messageFromServer(msg + "\n");
			   } 
			   catch (RemoteException e) {
			    e.printStackTrace();
			   }
		  } 
	}
	@Override
	public void isEmpty(String[] details) throws RemoteException {
		// TODO Auto-generated method stub
		if(users.size() > 0) {
			try {
				iClient nextClient = (iClient)Naming.lookup("rmi://" + details[1] + "/" + details[2]);
				nextClient.reject("the room has been created\n");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void isSameName(String[] details) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			iClient nextClient = (iClient)Naming.lookup("rmi://" + details[1] + "/" + details[2]);
			for(user c : users){
				if(c.getName().equals(details[0])) {
					nextClient.reject("the username has been taken\n");
				}
			}
		} catch (MalformedURLException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void exit(String username) throws RemoteException {
		// TODO Auto-generated method stub
		int index = 0;
		for(int i = 0;i < users.size();i++) {
			if(users.get(i).getName().equals(username)) {
				index = i;
			}
		}
		users.remove(index);
		broadcast("[Server] :" + username + " has exitd!\n");
		updateUserList();
	}
	//end the board
	@Override
	public void end() throws RemoteException {
		// TODO Auto-generated method stub
		System.exit(0);
	}

}
