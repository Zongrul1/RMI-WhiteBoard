package CreateWhiteBoard;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import whiteboard_remote.iClient;
import whiteboard_remote.iwhiteboard;

public class CreateWhiteBoard extends UnicastRemoteObject implements Serializable,iClient{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String userName;
	protected String hostName;
	protected String serviceName;
	protected String clientServiceName;
	protected whiteBroadGUI_create GUI;  
	protected iwhiteboard wb;
	
	//constructor
    public CreateWhiteBoard(String username,String IP,String port) throws RemoteException{
    	this.userName = username.trim();
		this.hostName  = IP + ":" + port; 
		this.serviceName = "whiteboard";
		this.clientServiceName = "Create";
		this.GUI = new whiteBroadGUI_create();
    }
	
	public static void main(String [] args) {
		CreateWhiteBoard cwb;
		try {
			if(args[2].equals("")) {
				JOptionPane.showMessageDialog(null, "the username cannot be empty", "error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			cwb = new CreateWhiteBoard(args[2],args[0],args[1]);
			cwb.connect();
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
			wb.isEmpty(details);
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
	//determine whether the user can join or not
	@Override
	public boolean judge(String str) throws RemoteException {
		// TODO Auto-generated method stub
		int flag = JOptionPane.showConfirmDialog(null,str + " want to join the whiteboard\n" + "apporve or not?","Judge", JOptionPane.YES_NO_OPTION);
		if(flag == 1) {
			return true;
		}
		else {
			return false;
		}
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
