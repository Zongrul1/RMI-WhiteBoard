package whiteboard_server;

import whiteboard_remote.iClient;

public class user {
	public String name;
	public iClient client;
	
	//constructor
	public user(String name, iClient client){
		this.name = name;
		this.client = client;
	}	
	//getters and setters
	public String getName(){
		return name;
	}
	public iClient getClient(){
		return client;
	}
}
