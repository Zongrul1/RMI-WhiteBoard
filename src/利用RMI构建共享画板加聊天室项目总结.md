@[TOC]
# 前言
本学期项目做了一个共享画板加聊天室项目，放假之余，对项目中的难点进行总结归纳。
github地址：https://github.com/Zongrul1/RMI-WB.git
# 总体结构
系统分为四个部分，CreateWhiteBoard为房间管理员，JoinWhiteBoard为房间普通成员，Remote为RMI接口，Server为服务端管理，以下是各部分关系图，各部分具体实现将下文详细描述。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191118181847377.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zNzc4MTU3OA==,size_16,color_FFFFFF,t_70)
# Remote 模块
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191119193902112.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zNzc4MTU3OA==,size_16,color_FFFFFF,t_70)
## iClient接口
此接口主要为client类服务，使server端可以调用到client的方法。
```java
public interface iClient extends Remote{	
	public void messageFromServer(String message) throws RemoteException;
	public void updateUserList(String[] currentUsers) throws RemoteException;
	public boolean judge(String str) throws RemoteException;
	public void load(byte[] b) throws RemoteException;
	public void reject(String str)throws RemoteException;
	public void info(String str) throws RemoteException;
}

```

## iwhiteboard接口
此接口主要为=server服务，使client端可以调用到server的方法。
```java
public interface iwhiteboard extends Remote{
	public void draw(byte[] b) throws RemoteException;
	public boolean check() throws RemoteException;
	public void registerListener(String[] details)throws RemoteException;
	public boolean judge(String str) throws RemoteException;
	public void removeUser(String username) throws RemoteException;
	public void broadcast(String msg) throws RemoteException;
	public void isEmpty(String[] details) throws RemoteException;
	public void isSameName(String[] details) throws RemoteException;
	public void exit(String username) throws RemoteException;
	public void end() throws RemoteException;
}
```
# Server 模块

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191119193907878.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zNzc4MTU3OA==,size_16,color_FFFFFF,t_70)
## sever类
用于启动服务器端，生成remote object，选定端口。
```java
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

```
## user类
用于保存client信息，略

## whiteboardServer类
### protected whiteboardServer() 
构造函数生成一个arraylist用于保存进入系统的用户信息，方便回调。
```java
	protected whiteboardServer() throws RemoteException {
		super();
		users = new ArrayList<user>();
		// TODO Auto-generated constructor stub
	}
```
### public void draw(byte[] b)
绘画功能，每次画板有变动，将画板内容以字节数组形式上传到服务器端，然后服务器端调用注册了的所有用户，令所有用户下载此图覆盖于画板上，达到共享画板同步的目的。
```java
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
```
### public synchronized void registerListener(String[] details)
此方法用于对新用户加入进行注册，此方法需要进行synchronized，防止线程混乱，在此方法中会对是否符合房间的创建条件，是否允许新用户加入房间等进行判定，若全数通过，则将用户加入到arraylist中，完成注册。
```java
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
```
### private void updateUserList()
用于update 用户列表显示
```java
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
```
### public boolean check()
检查房间是否为空
```java
	public boolean check() throws RemoteException {
		// TODO Auto-generated method stub
		if(users.size() == 0) {
			return false;
		}
		else {
			return true;
		}
	}
```
### public boolean judge(String str)
判定是否允许新用户加入
```java
	public boolean judge(String str) throws RemoteException {
		// TODO Auto-generated method stub
		return users.get(0).getClient().judge(str);
	}
```
### public void removeUser(String username)
房主移除用户，重要的是移除时，主线程用于移除，同时要开一个线程用于通知用户，不然会有线程上的问题。
```java
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
```
### public void broadcast(String msg)
消息广播
```java
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
```
### public void exit(String username)
当某用户（除房主外）退出时反馈
```java
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
```
### 其余方法
其余方法不是核心内容，略
# CreateWhiteBoard 模块
CWB模块具有用户管理，绘画，以及消息发送等功能，总体设计如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191118185502543.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zNzc4MTU3OA==,size_16,color_FFFFFF,t_70)
## CreateWhiteBoard类
### public static void main(String [] args)
本方法主要创建了一个cwb对象，用于连接

### public void connect()
在此方法，通过Naming.rebind先将自己进行绑定，为之后的信息回传做好准备，然后通过lookup与服务器模块进行连接，cwb连接上之后需要对房间是否为空进行判定，若为空则在服务器端进行注册，然后将remote对象赋值给GUI模块。
```java
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
```
### 	public void messageFromServer(String message) 
本方法用于聊天室内对其他用户的信息的接收
```java
	public void messageFromServer(String message) throws RemoteException {
		// TODO Auto-generated method stub
		GUI.gettextArea().append(message);
	}
```
### public void updateUserList(String[] currentUsers) 
本方法用于聊天室内对用户信息的更新
```java
public void updateUserList(String[] currentUsers) throws RemoteException {
		// TODO Auto-generated method stub
		GUI.getJlist().setListData(currentUsers);
	}
```
### public boolean judge(String str)
本方法用于判断是否让其他用户加入房间

### public void reject(String str)
本方法用于移除房间内用户

### public void load(byte[] b)
本方法用于读取图片
```java
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
```
### 其余方法
其余方法不是核心内容，略
## whiteBroadGUI_create 类
本类主要实现了界面主要显示功能（因为GUI是组里大佬做的，具体不太会哈哈，暂时不总结），在这里主要提及指定文件类型的保存和打开。
在这里用到了filechooser这个package去完成。
初始化（在这里用到了MyFileFilter这个类，这个类会在下一节解释）：

```java
		fileChooser = new JFileChooser();
		MyFileFilter jpgFilter = new MyFileFilter(".jpg", "jpg file (*.jpg)");
		MyFileFilter pngFilter = new MyFileFilter(".png", "png file (*.png)");
        fileChooser.addChoosableFileFilter(jpgFilter);
        fileChooser.addChoosableFileFilter(pngFilter);
```

文件的打开：

```java
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
					fileChooser.setCurrentDirectory(new File("."));          
	                dirpath = fileChooser.getSelectedFile().getAbsolutePath();
	                if (dirpath == null) {
	                    return;
	                }
	                else {
	                    file=new File(dirpath);
	                }                
	                try {
	                	BufferedImage bufImage = ImageIO.read(file);
	                	panel.load(bufImage);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                panel.synchronize();
				}
```

文件的保存：

```java
                	if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { 
    	                fileChooser.setCurrentDirectory(new File("."));	   
    	                String str;
    	                try {
    	                	MyFileFilter filter = (MyFileFilter)fileChooser.getFileFilter();
    	                	str = filter.getEnds(); 
    	                }
    	                catch(Exception e2) {
    	                	str = ".png";
    	                }
    	                file = fileChooser.getSelectedFile();
    	                File newFile = null;
    	                try {
    	                	  if (file.getAbsolutePath().toUpperCase().endsWith(str.toUpperCase())) {
    	                		    newFile = file;
    	                		    dirpath = file.getAbsolutePath();
    	                		  } else {
    	                		    newFile = new File(file.getAbsolutePath() + str);
    	                		    dirpath = file.getAbsolutePath() + str;
    	                		  }
    	                	str = str.substring(1);//remove the point
    						ImageIO.write(panel.save(),str, newFile);
    						JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
    					} catch (IOException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}
    				}
    			try {
                	String[] format = dirpath.split("\\.");
					ImageIO.write(panel.save(), format[format.length - 1],file);
					JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
```

##  MyFileFilter 类
此类用于文件类型筛选，相比于父类，增添了getEnds方法，用于对后缀名进行判断，使保存文件时，可以选择文件类型。
```java
package CreateWhiteBoard;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MyFileFilter extends FileFilter{
	  String ends; 
	  String description; 

	  public MyFileFilter(String ends, String description) { 
	    this.ends = ends; 
	    this.description = description; 
	  }

	  @Override
	  public boolean accept(File file) {
	    if (file.isDirectory()) return true;
	    String fileName = file.getName();
	    if (fileName.toUpperCase().endsWith(this.ends.toUpperCase())) return true;
	    return false;
	  }

	  @Override
	  public String getDescription() {
	    return this.description;
	  }
	  
	  public String getEnds() {
	    return this.ends;
	  }
}

```
## paintpanel类
本类主要实现图像绘画功能，在这里主要用上了内置的鼠标函数去实现，其中难点在于图像绘画时的拉伸显示。
### public void draw(int x,int y,int x1,int y1,String type)
本方法用于绘图，主要使用graphics2D类用于协助，详细实现如下：
```java
public void draw(int x,int y,int x1,int y1,String type) {
        Graphics2D g = (Graphics2D)getGraphics();
        g.setColor(selectColor);
        g.setStroke(selectStroke);
        if(type.equals("line")) {
            shapelist.add(new Shape(g,x,y,x1,y1,type,selectColor,selectStroke));
            g.drawLine(x,y, x1, y1);
        }
        else {
            int height = Math.abs(y1 - y);
            int width = Math.abs(x1 - x);
            if(type.equals("rect")) {
                shapelist.add(new Shape(g,x,y,x1,y1,type,selectColor,selectStroke));
                g.drawRect(Math.min(x, x1),Math.min(y, y1), width, height);
            }
            if(type.equals("oval")) {
                shapelist.add(new Shape(g,x,y,x1,y1,type,selectColor,selectStroke));
                g.drawOval(Math.min(x, x1),Math.min(y, y1), width, height);
            }
            if(type.equals("freedraw")) {
                ArrayList<Point> s = new ArrayList<Point>(1000);
                s.addAll(points);
                shapelist.add(new Shape(g,s,type,selectColor,selectStroke));
            }
            if(type.equals("erase")) {
                ArrayList<Point> s = new ArrayList<Point>(1000);
                s.addAll(points);
                shapelist.add(new Shape(g,s,type,Color.white,selectStroke));
            }
            if(type.equals("circle")) {
                shapelist.add(new Shape(g,x,y,x1,y1,type,selectColor,selectStroke));
                int round = Math.max(width, height);
                g.drawOval(Math.min(x, x1),Math.min(y, y1), round,round);
            }
            points.clear();
        }
    }
```
### public void synchronize()
在图像重新读取时，通过发送图片的方式，向全用户进行图像同步

```java
	public void synchronize() {
		try {	        
			//wb.draw(list);
			BufferedImage image = save();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image,"png", out);
			byte[] b = out.toByteArray();
			wb.draw(b);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "the manager has left the room", "error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
	}
```
### public void load(BufferedImage image)
本方法用于图像绘画时的同步
```java
    public void load(BufferedImage image) {
    	clear();
    	repaint();
    	this.image = image;
    }
```
### 拉伸效果
在拉伸时一直进行绘画操作
```java
public void mouseDragged(MouseEvent e) {
                // TODO Auto-generated method stub
            	if(wb != null) {
	                int x2 = e.getX();
	                int y2 = e.getY();
	                int x3;
	                int y3;
	                Graphics2D g = (Graphics2D)getGraphics();
	                g.setColor(selectColor);
	                g.setStroke(selectStroke);
	                //Freedraw and erase
	                if(type.equals("freedraw")){
	                    if(points.size()!=0){
	                        x3=points.get(points.size()-1).x;
	                        y3=points.get(points.size()-1).y;}
	                    else{
	                        x3=x;
	                        y3=y;
	                    }
	                    g.drawLine(x3,y3,x2,y2);
	                    points.add(new Point(x2,y2));
	                }
	                else if(type.equals("erase")){
	                    if(points.size()!=0){
	                        x3=points.get(points.size()-1).x;
	                        y3=points.get(points.size()-1).y;}
	                    else{
	                        x3=x;
	                        y3=y;
	                    }
	                    Color c = new Color(selectColor.getRGB());
	                    g.setColor(Color.WHITE);
	                    g.drawLine(x3,y3,x2,y2);
	                    points.add(new Point(x2,y2));
	                    g.setColor(c);
	                }
	                //Other shapes
	                else {
	                    if (type.equals("line")) {
	                        g.drawLine(x, y, x2, y2);
	                    }
	                    else {
	                        int height = Math.abs(y2 - y);
	                        int width = Math.abs(x2 - x);
	                        if (type.equals("rect")) {
	                            g.drawRect(Math.min(x, x2), Math.min(y, y2), width, height);
	                        }
	                        if (type.equals("oval")) {
	                            g.drawOval(Math.min(x, x2), Math.min(y, y2), width, height);
	                        }
	                        if (type.equals("circle")) {
	                        	int round = Math.max(width, height);
	                            g.drawOval(Math.min(x, x2),Math.min(y, y2), round,round);
	                        }
	                    }
	                    repaint();
	                }
            	}
            }
```
### 防止窗口最小化后消失
使用了shape类的arraylist去保存，并在paint进行调用（实际上这里原理不太懂）
```java
    public void paint(Graphics g){
        super.paint(g);
        if(image != null) {
        	g.drawImage(image, 0, 0, this);
        }
        for (int i = 0; i < shapelist.size(); i++) {
            if (shapelist.get(i) == null) {break;}
                shapelist.get(i).rePaint(g);
        }
    }
```

## Shape类
用于辅助防止窗口最小化后消失功能的实现。
# JoinWhiteBoard模块
除没有文件保存功能以及成员管理功能外，其余与cwb模块相同。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191119193924968.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zNzc4MTU3OA==,size_16,color_FFFFFF,t_70)