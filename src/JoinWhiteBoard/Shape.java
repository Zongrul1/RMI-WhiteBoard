package JoinWhiteBoard;

import java.awt.*;
import java.util.ArrayList;

public class Shape {
    private int x, x1, y, y1;
    private String type;
    private Color c;
    private ArrayList<Point> freedraw;
    private Stroke stroke;
    private String input;
    //This Shape is design for rect, oval, circle
    public Shape(Graphics g, int x, int y, int x1, int y1, String type, Color c,Stroke stroke)
    {
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
        this.type = type;
        this.c = c;
        this.stroke = stroke;

    }
    //This Shape is design for freedraw, erase
    public Shape(Graphics g, ArrayList<Point> s, String type, Color c,Stroke stroke)
    {
        this.freedraw = s;
        this.type = type;
        this.c = c;
        this.stroke = stroke;
    }
    //Repaint function

    public Shape(Graphics g, int x, int y, String in, String type, Color color)
    {
        this.x =x;
        this.y = y;
        this.input = in;
        this.type = type;
        this.c =color;
    }

    public void rePaint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if(type.equals("text")){
        	g.setColor(c);
            g.drawString(input,x,y);
        }
        else {
            g2.setColor(c);
            g2.setStroke(stroke);
            if (type.equals("line")) {
                g.drawLine(x, y, x1, y1);
            } else if (type.equals("rect")) {
                g.drawRect(Math.min(x, x1), Math.min(y, y1), Math.abs(x - x1), Math.abs(y - y1));
            } else if (type.equals("oval")) {
                g.drawOval(Math.min(x, x1), Math.min(y, y1), Math.abs(x - x1), Math.abs(y - y1));
            } else if (type.equals("circle")) {
            	g.drawOval(Math.min(x, x1), Math.min(y, y1), Math.max(Math.abs(x - x1),Math.abs(y - y1)),Math.max(Math.abs(x - x1),Math.abs(y - y1)));
            } else if (type.equals("freedraw")) {
                for (int i = 1; i < freedraw.size(); i++) {
                    g.drawLine(freedraw.get(i - 1).x, freedraw.get(i - 1).y, freedraw.get(i).x, freedraw.get(i).y);
                }
            } else if (type.equals("erase")) {
                for (int i = 1; i < freedraw.size(); i++) {
                    g.drawLine(freedraw.get(i - 1).x, freedraw.get(i - 1).y, freedraw.get(i).x, freedraw.get(i).y);
                }
            }
        }
    }

    public String getType(){
        return type;
    }
}
