package JoinWhiteBoard;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.*;

import CreateWhiteBoard.Shape;
import whiteboard_remote.iwhiteboard;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class paintpanel extends JPanel{
	//paint-type
	private String type = "line";
	//axis
	private int x,y;
	//color
	private Color selectColor = Color.BLACK;
	private Stroke selectStroke = new BasicStroke(1.0f);
	private iwhiteboard wb;
	private BufferedImage image;
	
	//20191003
    //Points record the trace of freedraw and erase
    private static ArrayList<Point> points = new ArrayList<Point>();
    //shapelist record all shapes in whiteboard
    private static ArrayList<Shape> shapelist = new ArrayList<Shape>();
    
   //setters
    public void setwb(iwhiteboard wb) {
    	this.wb = wb;
    }
	public void setType(String type) {
		this.type = type;
	}
	public void setStroke(Stroke stroke) {
		this.selectStroke = stroke;
	}
	public void setColor(Color color){
		this.selectColor = color;
	}
    //new
    public void clear() {
    	shapelist = new ArrayList<Shape>();
    	image = null;
    }
    //save the image
    public BufferedImage save() {    	
    	Dimension imagesize = this.getSize();
		BufferedImage image = new BufferedImage(imagesize.width,imagesize.height,BufferedImage.TYPE_INT_BGR);	
		Graphics2D graphics = image.createGraphics();//draw the image
        this.paint(graphics);
        graphics.dispose();
        return image;
    }
    //load the image from other resource
    public void load(BufferedImage image) {
    	clear();
    	repaint();
    	this.image = image;
    }

    //This paint method is to keep shapes on graphic
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
    //draw a image
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
//synchronize the board with other users  
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

    public paintpanel() {
        addMouseListener(new MouseListener() {

    
            @Override
            public void mousePressed(MouseEvent e) {
            	//press,get start position
                x = e.getX();
                y = e.getY();
				if(wb!=null&&type.equals("text")){
					Graphics2D g = (Graphics2D)getGraphics();
					String input;
					input = JOptionPane.showInputDialog(
							"Please input the text you want!");
					if(input != null) {
						g.setColor(selectColor);
						g.drawString(input,x,y);
						shapelist.add(new Shape(g,x,y,input,type,selectColor));
						synchronize();
					}
				}
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            	if(wb != null) {
	                int x1 = e.getX();
	                int y1 = e.getY();
	                String s = (x + " " + y + " " + x1 + " " + y1 + " " + type);
	                draw(x, y, x1, y1, type);
	                synchronize();
            	}
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

        }
        );
        addMouseMotionListener(new MouseMotionListener() {
            @Override
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
            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub
            }

        }
        );
    }
}
