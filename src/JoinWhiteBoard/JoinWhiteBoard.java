package JoinWhiteBoard;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import CreateWhiteBoard.whiteBroadGUI_create;
import whiteboard_remote.iClient;
import whiteboard_remote.iwhiteboard;

public class JoinWhiteBoard extends UnicastRemoteObject implements Serializable,iClient{
	
	private static final long serialVersionUID = 1L;
	protected String userName;
	protected String hostName;
	protected String serviceName;
	protected String clientServiceName;
	protected whiteBroadGUI_Join GUI;  
	protected iwhiteboard wb;
	
	//con
	public JoinWhiteBoard(String username,String IP,String port) throws RemoteException {
    	this.userName = username.trim();
		this.hostName  = IP + ":" + port; 
		this.serviceName = "whiteboard";
		this.clientServiceName = "Create";
		this.GUI = new whiteBroadGUI_Join();
	}
	
	public static void main(String [] args) {
		JoinWhiteBoard jwb;
		try {
			if(args[2].equals("")) {
				JOptionPane.showMessageDialog(null, "the username cannot be empty", "error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			jwb = new JoinWhiteBoard(args[2],args[0],args[1]);
			jwb.connect();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public void connect() {
		try {
			//RMI
			String[] details = {userName,hostName,clientServiceName};
			Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
			wb = (iwhiteboard) Naming.lookup("rmi://" + hostName + "/" + serviceName);
			if(!wb.check()) {
				JOptionPane.showMessageDialog(null, "empty room, connection failed", "error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			wb.isSameName(details);
			wb.registerListener(details);
			GUI.set_wb(wb);
			GUI.setUsername(userName);
			GUI.getpanel().setwb(wb);
		} catch (RemoteException | NotBoundException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "connection failed", "error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
			System.exit(0);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return userName;
	}
	//receive meassage
	@Override
	public void messageFromServer(String message) throws RemoteException {
		// TODO Auto-generated method stub
		GUI.gettextArea().append(message);
	}
	//update the Jlist
	@Override
	public void updateUserList(String[] currentUsers) throws RemoteException {
		// TODO Auto-generated method stub
		GUI.getJlist().setListData(currentUsers);
	}
	//join do not have this function
	@Override
	public boolean judge(String str) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
	//load the picture
	@Override
	public void load(byte[] b) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(b);
			BufferedImage image = ImageIO.read(in);
			GUI.getpanel().load(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//request is rejected by the manager
	@Override
	public void reject(String str) throws RemoteException {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, str + "the request has been rejected", "error", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}

	@Override
	public void info(String str) throws RemoteException {
		// TODO Auto-generated method stub
		Thread t = new Thread(()->{
		JOptionPane.showMessageDialog(null, str, "Information", JOptionPane.INFORMATION_MESSAGE);
		});
		t.start();
	}
}
