import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class JMorphPanel extends JPanel implements MouseMotionListener, MouseListener{

    private int row, col;
    private String type;
    private JControlPoint points[][];
    private JControlPoint midPoints[][];
    private static int MAX_DIMENSIONS = 395;
    private boolean isGenerated = false;
    private JMorphPanel other;
    private BufferedImage img;
    private boolean imgLoaded = false;
    private boolean selectingGroup = false;
    private boolean groupSelected = false;
    private boolean drawingRect = false;
    private double rectStartX, rectStartY, rectWidth, rectHeight;
    private double basePointX, basePointY;
    private boolean basePointSelected = false;
    private Rectangle2D.Double rect;

    public JMorphPanel(int row, int col, String type){
        setBackground(Color.BLACK);
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
        this.other = null;
        this.row = row;
        this.col = col;
        this.type = type;
    }

    // link panels
    public void setOther(JMorphPanel other){
        this.other = other;
    }

    // return a point in the midpoints array
    public JControlPoint getMidPoint(int i, int j) {
        JControlPoint temp = midPoints[i][j];
        return temp;
    }

    //return a point in the points array
    public JControlPoint getPoint(int i, int j) {
        JControlPoint temp = points[i][j];
        return temp;
    }

    // return the points array
    public JControlPoint[][] getAllPoints() {
        JControlPoint temp[][] = new JControlPoint[row + 1][col + 1];
        for (int i = 0; i <= row; i++) {
            for (int j = 0; j <= col; j++) {
                temp[i][j] = points[i][j];
            }
        }
        return temp;
    }

    // return the midpoints array
    public JControlPoint[][] getAllMidPoints() {
        JControlPoint temp[][] = new JControlPoint[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                temp[i][j] = midPoints[i][j];
            }
        }
        return temp;
    }

    //paint the panel
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D grid = (Graphics2D)g;
        if(!isGenerated) {
            generate();
            isGenerated = true;
        }
        if(imgLoaded){
            grid.drawImage(img, 0, 0, this);
        }
        drawPoints(grid);
        drawLines(grid);
        if(drawingRect){
            drawRect(grid);
        }
    }

    //generate the point locations
    public void generate(){
        points = new JControlPoint[row + 1][col + 1];
        for (int i = 0; i <= row; i++){
            for (int j = 0; j <= col; j++) {
                if(imgLoaded && type.equals("pre")) {
                    points[i][j] = new JControlPoint(i * img.getWidth() / row, j * img.getHeight() / col);
                    if ((i == 0) || (i == img.getWidth()) || (j == 0) || (j == img.getWidth())) {
                        points[i][j].setEdge();
                    }
                }
                else if(other.imgLoaded && type.equals("post")) {
                    points[i][j] = new JControlPoint(i * other.img.getWidth() / row, j * other.img.getHeight() / col);
                    if ((i == 0) || (i == other.img.getWidth()) || (j == 0) || (j == other.img.getWidth())) {
                        points[i][j].setEdge();
                    }
                }
                else{
                    points[i][j] = new JControlPoint(i * MAX_DIMENSIONS / row, j * MAX_DIMENSIONS / col);
                    if ((i == 0) || (i == MAX_DIMENSIONS) || (j == 0) || (j == MAX_DIMENSIONS)) {
                        points[i][j].setEdge();
                    }
                }
            }
        }
        midPoints = new JControlPoint[row][col];
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++) {
                midPoints[i][j] = new JControlPoint((int) ((points[i][j].getX() + points[i + 1][j].getX())/2), (int)((points[i][j].getY() + points[i][j + 1].getY())/2));
            }
        }
    }

    public void drawPoints(Graphics2D g){
        for (int i = 0; i <= row; i++){
            for(int j = 0; j <= col; j++){
                if (points[i][j].isDragging()){
                    g.setColor(Color.RED);
                    other.getPoint(i, j).setDragging(true);
                }
                else if(points[i][j].isBasePoint()){
                    g.setColor(Color.BLUE);
                }
                else{
                    g.setColor(Color.BLACK);
                }
                g.draw(points[i][j].getCircle());
                g.fill(points[i][j].getCircle());
            }
        }
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                if (midPoints[i][j].isDragging()){
                    g.setColor(Color.RED);
                    other.getMidPoint(i, j).setDragging(true);
                }
                else{
                    g.setColor(Color.BLACK);
                }

                g.draw(midPoints[i][j].getCircle());
                g.fill(midPoints[i][j].getCircle());
            }
        }
    }

    public void drawLines(Graphics2D g){
        g.setColor(new Color(0,0,0,0.2f));
        for(int i = 0; i < points.length - 1; i++) {
            for (int j = 0; j < points[i].length - 1; j++) {
                if (!points[i + 1][j].getEdge()) {
                    g.drawLine((int) points[i][j].getX(), (int) points[i][j].getY() + 3, (int) points[i + 1][j].getX(), (int) points[i + 1][j].getY() + 3);
                }
                if (!points[i][j + 1].getEdge()) {
                    g.drawLine((int) points[i][j].getX() + 3, (int) points[i][j].getY(), (int) points[i][j + 1].getX() + 3, (int) points[i][j + 1].getY());
                }
                if (i != 0) {
                    if (!points[i - 1][j].getEdge()) {
                        g.drawLine((int) points[i][j].getX(), (int) points[i][j].getY() + 3, (int) points[i - 1][j].getX(), (int) points[i - 1][j].getY() + 3);
                    }
                }
                if (j != 0) {
                    if (!points[i][j - 1].getEdge()) {
                        g.drawLine((int) points[i][j].getX() + 3, (int) points[i][j].getY(), (int) points[i][j - 1].getX() + 3, (int) points[i][j - 1].getY());
                    }
                }
            }
        }
        for (int i = 0; i < midPoints.length; i++){
            for (int j = 0; j < midPoints[i].length; j++){
                g.drawLine((int) midPoints[i][j].getX() + 3, (int) midPoints[i][j].getY() + 3, (int) points[i][j].getX() + 3, (int) points[i][j].getY() + 3);
                g.drawLine((int) midPoints[i][j].getX() + 3, (int) midPoints[i][j].getY() + 3, (int) points[i + 1][j].getX() + 3, (int) points[i + 1][j].getY() + 3);
                g.drawLine((int) midPoints[i][j].getX() + 3, (int) midPoints[i][j].getY() + 3, (int) points[i][j + 1].getX() + 3, (int) points[i][j + 1].getY() + 3);
                g.drawLine((int) midPoints[i][j].getX() + 3, (int) midPoints[i][j].getY() + 3, (int) points[i + 1][j + 1].getX() + 3, (int) points[i + 1][j + 1].getY() + 3);
            }
        }
    }

    // rectangle for grouping points
    public void drawRect(Graphics2D g){
        g.setColor(Color.RED);
        g.draw(rect);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    // handle start of point motion or rectangle generation for default, group select, and post group move case
    @Override
    public void mousePressed(MouseEvent e) {
        if(!selectingGroup && !groupSelected) {
            for (int i = 1; i < points.length - 1; i++) {
                for (int j = 1; j < points[i].length - 1; j++) {
                    Ellipse2D.Double test = new Ellipse2D.Double(points[i][j].getX(), points[i][j].getY(), 10,10);
                    if (test.contains(e.getPoint())) {
                        points[i][j].setDragging(true);
                    }
                }
            }
            for (int i = 0; i < midPoints.length; i++) {
                for (int j = 0; j < midPoints[i].length; j++) {
                    Ellipse2D.Double test  = new Ellipse2D.Double(midPoints[i][j].getX(), midPoints[i][j].getY(), 10,10);
                    if (test.contains(e.getPoint())) {
                        midPoints[i][j].setDragging(true);
                    }
                }
            }
        } else if(selectingGroup){
            drawingRect = true;
            rectStartX = e.getX();
            rectStartY = e.getY();
        } else if(groupSelected){
            for (int i = 1; i < points.length - 1; i++) {
                for (int j = 1; j < points[i].length - 1; j++) {
                    Ellipse2D.Double test = new Ellipse2D.Double(points[i][j].getX(), points[i][j].getY(), 10,10);
                    if (test.contains(e.getPoint())){
                        points[i][j].setBasePoint(true);
                        basePointX = e.getX();
                        basePointY = e.getY();
                        basePointSelected = true;
                    }
                }
            }
            for (int i = 0; i < midPoints.length; i++) {
                for (int j = 0; j < midPoints[i].length; j++) {
                    Ellipse2D.Double test  = new Ellipse2D.Double(midPoints[i][j].getX(), midPoints[i][j].getY(), 10,10);
                    if (test.contains(e.getPoint())) {
                        midPoints[i][j].setBasePoint(true);
                        basePointX = e.getX();
                        basePointY = e.getY();
                        basePointSelected = true;
                    }
                }
            }
        }
    }

    // handle release of point motion or rectangle generation for default, group select, and group move case
    @Override
    public void mouseReleased(MouseEvent e) {
        if(!selectingGroup && !groupSelected) {
            for (int i = 1; i < points.length - 1; i++) {
                for (int j = 1; j < points[i].length - 1; j++) {
                    if (points[i][j].isDragging()) {
                        points[i][j].setDragging(false);
                        other.getPoint(i, j).setDragging(false);
                        return;
                    }
                }
            }
            for (int i = 0; i < midPoints.length; i++) {
                for (int j = 0; j < midPoints[i].length; j++) {
                    if (midPoints[i][j].isDragging()) {
                        midPoints[i][j].setDragging(false);
                        other.getMidPoint(i, j).setDragging(false);
                        return;
                    }
                }
            }
        } else if(selectingGroup){
            // set all points in rectangle as dragging
            for (int i = 1; i < points.length - 1; i++) {
                for (int j = 1; j < points[i].length - 1; j++) {
                    if (rect.contains(points[i][j].getX(), points[i][j].getY())){
                        points[i][j].setDragging(true);
                        other.getPoint(i, j).setDragging(true);
                    }
                }
            }
            for (int i = 0; i < midPoints.length; i++) {
                for (int j = 0; j < midPoints[i].length; j++) {
                    if (rect.contains(midPoints[i][j].getX(), midPoints[i][j].getY())){
                        midPoints[i][j].setDragging(true);
                        other.getMidPoint(i, j).setDragging(true);
                    }
                }
            }
            drawingRect = false;
            selectingGroup = false;
            groupSelected = true;
            repaint();
            other.repaint();
        } else if(groupSelected && basePointSelected){
            // move all points in rectangle by change
            double dX = e.getX() - basePointX;
            double dY = e.getY() - basePointY;

            double currX;
            double currY;
            for (int i = 1; i < points.length - 1; i++) {
                for (int j = 1; j < points[i].length - 1; j++) {
                    if (points[i][j].isDragging() && !points[i][j].isBasePoint()) {
                        currX = points[i][j].getX();
                        currY = points[i][j].getY();
                        points[i][j].set((int) currX + (int) dX, (int) currY + (int) dY);
                    }
                }
            }
            for (int i = 0; i < midPoints.length; i++) {
                for (int j = 0; j < midPoints[i].length; j++) {
                    if (midPoints[i][j].isDragging() && !midPoints[i][j].isBasePoint()) {
                        currX = midPoints[i][j].getX();
                        currY = midPoints[i][j].getY();
                        midPoints[i][j].set((int) currX + (int) dX, (int) currY + (int) dY);
                    }
                }
            }
            repaint();
            for (int i = 1; i < points.length - 1; i++) {
                for (int j = 1; j < points[i].length - 1; j++) {
                    if (points[i][j].isDragging()) {
                        points[i][j].setDragging(false);
                        other.getPoint(i, j).setDragging(false);
                    }
                    if(points[i][j].isBasePoint()){
                        points[i][j].setBasePoint(false);
                    }
                }
            }
            for (int i = 0; i < midPoints.length; i++) {
                for (int j = 0; j < midPoints[i].length; j++) {
                    if (midPoints[i][j].isDragging()) {
                        midPoints[i][j].setDragging(false);
                        other.getMidPoint(i, j).setDragging(false);
                    }
                    if(midPoints[i][j].isBasePoint()){
                        midPoints[i][j].setBasePoint(false);
                    }
                }
            }
            groupSelected = false;
            basePointSelected = false;
        }
    }


    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    // handle current point motion or rectangle generation for default, group select, and post group select case
    @Override
    public void mouseDragged(MouseEvent e) {
        if(!selectingGroup && !groupSelected) {
            for (int i = 1; i < points.length - 1; i++) {
                for (int j = 1; j < points[i].length - 1; j++) {
                    if (points[i][j].isDragging()) {
                        Polygon poly = new Polygon();
                        poly.addPoint((int) midPoints[i - 1][j - 1].getX(), (int) midPoints[i - 1][j - 1].getY());
                        poly.addPoint((int) midPoints[i - 1][j].getX(), (int) midPoints[i - 1][j].getY());
                        poly.addPoint((int) midPoints[i][j].getX(), (int) midPoints[i][j].getY());
                        poly.addPoint((int) midPoints[i][j - 1].getX(), (int) midPoints[i][j - 1].getY());

                        if (poly.contains(e.getPoint())) {
                            points[i][j].set(e.getX(), e.getY());
                        } else if (poly.contains(e.getX(), (int) points[i][j].getY())) {
                            points[i][j].set(e.getX(), (int) points[i][j].getY());
                        } else if (poly.contains((int) points[i][j].getX(), e.getY())) {
                            points[i][j].set((int) points[i][j].getX(), e.getY());
                        } else {
                            return;
                        }
                        repaint();
                        other.repaint();
                        return;
                    }
                }
            }
            for (int i = 0; i < midPoints.length; i++) {
                for (int j = 0; j < midPoints[i].length; j++) {
                    if (midPoints[i][j].isDragging()) {
                        Polygon poly = new Polygon();
                        poly.addPoint((int) points[i][j].getX(), (int) points[i][j].getY());
                        poly.addPoint((int) points[i + 1][j].getX(), (int) points[i + 1][j].getY());
                        poly.addPoint((int) points[i + 1][j + 1].getX(), (int) points[i + 1][j + 1].getY());
                        poly.addPoint((int) points[i][j + 1].getX(), (int) points[i][j + 1].getY());
                        if (poly.contains(e.getPoint())) {
                            midPoints[i][j].set(e.getX(), e.getY());

                        } else if (poly.contains(e.getX(), (int) midPoints[i][j].getY())) {
                            midPoints[i][j].set(e.getX(), (int) midPoints[i][j].getY());
                        } else if (poly.contains((int) midPoints[i][j].getX(), e.getY())) {
                            midPoints[i][j].set((int) midPoints[i][j].getX(), e.getY());
                        } else {
                            return;
                        }
                        repaint();
                        other.repaint();
                        return;
                    }
                }
            }
        } else if(selectingGroup){
            rectWidth = e.getX() - rectStartX;
            rectHeight = e.getY() - rectStartY;
            rect = new Rectangle2D.Double(rectStartX, rectStartY, rectWidth, rectHeight);
            repaint();
        } else if(groupSelected) {
            // move base point
            for (int i = 1; i < points.length - 1; i++) {
                for (int j = 1; j < points[i].length - 1; j++) {
                    if (points[i][j].isDragging() && points[i][j].isBasePoint()) {
                        Polygon poly = new Polygon();
                        poly.addPoint((int) midPoints[i - 1][j - 1].getX(), (int) midPoints[i - 1][j - 1].getY());
                        poly.addPoint((int) midPoints[i - 1][j].getX(), (int) midPoints[i - 1][j].getY());
                        poly.addPoint((int) midPoints[i][j].getX(), (int) midPoints[i][j].getY());
                        poly.addPoint((int) midPoints[i][j - 1].getX(), (int) midPoints[i][j - 1].getY());
                        if (poly.contains(e.getPoint())) {
                            points[i][j].set(e.getX(), e.getY());
                        } else if (poly.contains(e.getX(), (int) points[i][j].getY())) {
                            points[i][j].set(e.getX(), (int) points[i][j].getY());
                        } else if (poly.contains((int) points[i][j].getX(), e.getY())) {
                            points[i][j].set((int) points[i][j].getX(), e.getY());
                        } else {
                            return;
                        }
                        repaint();
                        other.repaint();
                    }
                }
            }
            for (int i = 0; i < midPoints.length; i++) {
                for (int j = 0; j < midPoints[i].length; j++) {
                    if (midPoints[i][j].isDragging() && midPoints[i][j].isBasePoint()) {
                        Polygon poly = new Polygon();
                        poly.addPoint((int) points[i][j].getX(), (int) points[i][j].getY());
                        poly.addPoint((int) points[i + 1][j].getX(), (int) points[i + 1][j].getY());
                        poly.addPoint((int) points[i + 1][j + 1].getX(), (int) points[i + 1][j + 1].getY());
                        poly.addPoint((int) points[i][j + 1].getX(), (int) points[i][j + 1].getY());
                        if (poly.contains(e.getPoint())) {
                            midPoints[i][j].set(e.getX(), e.getY());
                        } else if (poly.contains(e.getX(), (int) midPoints[i][j].getY())) {
                            midPoints[i][j].set(e.getX(), (int) midPoints[i][j].getY());
                        } else if (poly.contains((int) midPoints[i][j].getX(), e.getY())) {
                            midPoints[i][j].set((int) midPoints[i][j].getX(), e.getY());
                        } else {
                            return;
                        }
                        repaint();
                        other.repaint();
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public Dimension getPreferredSize(){
        if(type.equals("pre") && imgLoaded){
            return new Dimension(img.getWidth(), img.getHeight());
        }
        else if(type.equals("post") && other.imgLoaded){
            return new Dimension(other.img.getWidth(), other.img.getHeight());
        }
        else {
            return new Dimension(400,400);
        }
    }

    // set background for panel
    public void setImage(BufferedImage imga) {
        double scaledWidth, scaledHeight, scaleFactor;
        Image tmp;
        if (imga.getHeight() == imga.getWidth()) {
            tmp = imga.getScaledInstance(MAX_DIMENSIONS, MAX_DIMENSIONS, BufferedImage.SCALE_SMOOTH);
            this.img = new BufferedImage(MAX_DIMENSIONS, MAX_DIMENSIONS, BufferedImage.TYPE_INT_RGB);

        } else if (imga.getHeight() > imga.getWidth()) {
            scaledHeight = 400;
            scaleFactor = scaledHeight / imga.getHeight();
            scaledWidth = scaleFactor * imga.getWidth();
            tmp = imga.getScaledInstance((int) scaledWidth, (int) scaledHeight, BufferedImage.SCALE_SMOOTH);
            this.img = new BufferedImage((int) scaledWidth, (int) scaledHeight, BufferedImage.TYPE_INT_RGB);
        } else {
            scaledWidth = 400;
            scaleFactor = scaledWidth / imga.getWidth();
            scaledHeight = scaleFactor * imga.getHeight();
            tmp = imga.getScaledInstance((int) scaledWidth, (int) scaledHeight, BufferedImage.SCALE_SMOOTH);
            this.img = new BufferedImage((int) scaledWidth, (int) scaledHeight, BufferedImage.TYPE_INT_RGB);
        }
        img.getGraphics().drawImage(tmp, 0, 0, null);
        imgLoaded = true;
        repaint();
        other.getPreferredSize();
        other.repaint();
    }

    public BufferedImage getImage(){
        return img;
    }

    public boolean isImgLoaded(){
        return imgLoaded;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setGroupSelected(boolean selected){
        selectingGroup = selected;
    }

    public boolean getGroupSelected(){
        return selectingGroup;
    }
}