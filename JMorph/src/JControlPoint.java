import java.awt.*;
import java.awt.geom.Ellipse2D;

public class JControlPoint extends Point{

    private boolean isEdge, dragging, isBasePoint;
    private Ellipse2D.Double circle;

    public JControlPoint(int x, int y){
        super(x,y);
        isEdge = false;
        isBasePoint = false;
        circle = new Ellipse2D.Double(x, y,6,6);
        dragging = false;

    }
    public void setEdge(){
        this.isEdge = true;
    }
    public boolean getEdge(){
        return this.isEdge;
    }
    public Ellipse2D.Double getCircle(){
        return circle;
    }

    public boolean isDragging(){
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public double getX(){
        return x;
    }

    @Override
    public double getY(){
        return y;
    }

    public void set(int x, int y){
        this.x = x;
        this.y = y;
        circle = new Ellipse2D.Double(x, y,6,6);
    }

    //select base control point for group move
    public void setBasePoint(boolean isBasePoint){
        this.isBasePoint = isBasePoint;
    }

    //check if a control point is a base point for group move
    public boolean isBasePoint(){
        return isBasePoint;
    }

}