import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class JMorpherAnimatePanel extends JPanel implements ActionListener {

    private Timer time;

    private JMorphPanel prePanel, postPanel;
    private int seconds;
    private double maxFrames, elapsed;
    private double dxP[][], dyP[][], dxM[][], dyM[][];
    private JControlPoint preP[][], preM[][], duringP[][], duringM[][], postP[][], postM[][];

    private JTriangle preTriangles[],  postTriangles[], duringTriangles[];

    private float alpha;

    private BufferedImage preImage, postImage;


    public JMorpherAnimatePanel(JMorphPanel prePanel, JMorphPanel postPanel, int seconds) {
        this.prePanel = prePanel;
        this.postPanel = postPanel;
        this.seconds = seconds;
        this.preP = prePanel.getAllPoints();
        this.preM = prePanel.getAllMidPoints();
        this.postP = postPanel.getAllPoints();
        this.postM = postPanel.getAllMidPoints();
        this.preImage = prePanel.getImage();
        this.postImage = postPanel.getImage();

        this.preTriangles = new JTriangle[prePanel.getRow() * prePanel.getCol() * 4];
        this.postTriangles = new JTriangle[postPanel.getWidth() * postPanel.getHeight() * 4];
        this.duringTriangles = new JTriangle[prePanel.getRow() * prePanel.getCol() * 4];
        int iterator = 0;
        for (int i = 0; i < preM.length; i++) {
            for (int j = 0; j < preM.length; j++) {
                preTriangles[iterator] = new JTriangle((int) preP[i][j].getX(), (int) preP[i][j].getY(), (int) preM[i][j].getX(), (int) preM[i][j].getY(), (int) preP[i + 1][j].getX(), (int) preP[i + 1][j].getY());
                postTriangles[iterator] = new JTriangle((int) postP[i][j].getX(), (int) postP[i][j].getY(), (int) postM[i][j].getX(), (int) postM[i][j].getY(), (int) postP[i + 1][j].getX(), (int) postP[i + 1][j].getY());
                duringTriangles[iterator] = new JTriangle((int) preP[i][j].getX(), (int) preP[i][j].getY(), (int) preM[i][j].getX(), (int) preM[i][j].getY(), (int) preP[i + 1][j].getX(), (int) preP[i + 1][j].getY());
                preTriangles[iterator + 1] = new JTriangle((int) preP[i + 1][j].getX(), (int) preP[i + 1][j].getY(), (int) preM[i][j].getX(), (int) preM[i][j].getY(), (int) preP[i + 1][j + 1].getX(), (int) preP[i + 1][j + 1].getY());
                postTriangles[iterator + 1] = new JTriangle((int) postP[i + 1][j].getX(), (int) postP[i + 1][j].getY(), (int) postM[i][j].getX(), (int) postM[i][j].getY(), (int) postP[i + 1][j + 1].getX(), (int) postP[i + 1][j + 1].getY());
                duringTriangles[iterator + 1] = new JTriangle((int) preP[i + 1][j].getX(), (int) preP[i + 1][j].getY(), (int) preM[i][j].getX(), (int) preM[i][j].getY(), (int) preP[i + 1][j + 1].getX(), (int) preP[i + 1][j + 1].getY());
                preTriangles[iterator + 2] = new JTriangle((int) preP[i + 1][j + 1].getX(), (int) preP[i + 1][j + 1].getY(), (int) preM[i][j].getX(), (int) preM[i][j].getY(), (int) preP[i][j + 1].getX(), (int) preP[i][j + 1].getY());
                postTriangles[iterator + 2] = new JTriangle((int) postP[i + 1][j + 1].getX(), (int) postP[i + 1][j + 1].getY(), (int) postM[i][j].getX(), (int) postM[i][j].getY(), (int) postP[i][j + 1].getX(), (int) postP[i][j + 1].getY());
                duringTriangles[iterator + 2] = new JTriangle((int) preP[i + 1][j + 1].getX(), (int) preP[i + 1][j + 1].getY(), (int) preM[i][j].getX(), (int) preM[i][j].getY(), (int) preP[i][j + 1].getX(), (int) preP[i][j + 1].getY());
                preTriangles[iterator + 3] = new JTriangle((int) preP[i][j + 1].getX(), (int) preP[i][j + 1].getY(), (int) preM[i][j].getX(), (int) preM[i][j].getY(), (int) preP[i][j].getX(), (int) preP[i][j].getY());
                postTriangles[iterator + 3] = new JTriangle((int) postP[i][j + 1].getX(), (int) postP[i][j + 1].getY(), (int) postM[i][j].getX(), (int) postM[i][j].getY(), (int) postP[i][j].getX(), (int) postP[i][j].getY());
                duringTriangles[iterator + 3] = new JTriangle((int) preP[i][j + 1].getX(), (int) preP[i][j + 1].getY(), (int) preM[i][j].getX(), (int) preM[i][j].getY(), (int) preP[i][j].getX(), (int) preP[i][j].getY());
                iterator = iterator + 4;
            }
        }
        duringP = new JControlPoint[preP.length][preP.length];
        for (int i = 0; i < duringP.length; i++) {
            for (int j = 0; j < duringP.length; j++) {
                duringP[i][j] = preP[i][j];
            }
        }
        duringM = new JControlPoint[preM.length][preM.length];
        for (int i = 0; i < duringM.length; i++) {
            for (int j = 0; j < duringM.length; j++) {
                duringM[i][j] = preM[i][j];
            }
        }
        findDistances();
        elapsed = 0;
        time = new Timer(1000 / 30, this);
        maxFrames = 30 * seconds;
        alpha = 0.0f; //1.0f;
        time.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        elapsed++;
        if (elapsed >= maxFrames) {
            time.stop();
            elapsed = 0;
            alpha = 1.0f;
        } else {
            updatePoints();
            updateDuringTriangle();
            alpha = (float) elapsed / (float) (maxFrames);

            repaint();
        }
    }

    private void updateDuringTriangle(){
        int iterator = 0;
        for (int i = 0; i < duringM.length; i++){
            for (int j = 0; j < duringM.length; j++){
                duringTriangles[iterator] = new JTriangle((int) duringP[i][j].getX(), (int) duringP[i][j].getY(), (int) duringM[i][j].getX(), (int) duringM[i][j].getY(), (int) duringP[i + 1][j].getX(), (int) duringP[i + 1][j].getY());
                duringTriangles[iterator + 1] = new JTriangle((int) duringP[i + 1][j].getX(), (int) duringP[i + 1][j].getY(), (int) duringM[i][j].getX(), (int) duringM[i][j].getY(), (int) duringP[i + 1][j + 1].getX(), (int) duringP[i + 1][j + 1].getY());
                duringTriangles[iterator + 2] = new JTriangle((int) duringP[i + 1][j + 1].getX(), (int) duringP[i + 1][j + 1].getY(), (int) duringM[i][j].getX(), (int) duringM[i][j].getY(), (int) duringP[i][j + 1].getX(), (int) duringP[i][j + 1].getY());
                duringTriangles[iterator + 3] = new JTriangle((int) duringP[i][j + 1].getX(), (int) duringP[i][j + 1].getY(), (int) duringM[i][j].getX(), (int) duringM[i][j].getY(), (int) duringP[i][j].getX(), (int) duringP[i][j].getY());
                iterator = iterator + 4;
            }
        }
    }

    private void updatePoints() {
        for (int i = 0; i < duringP.length; i++) {
            for (int j = 0; j < duringP.length; j++) {
                if (dxP[i][j] != 0 && dyP[i][j] != 0) {
                    JControlPoint temp = new JControlPoint((int) (preP[i][j].getX() + (elapsed / maxFrames * dxP[i][j])), (int) (preP[i][j].getY() + (elapsed / maxFrames * dyP[i][j])));
                    duringP[i][j] = temp;

                }
            }
        }
        // int iterator = 0;
        for (int i = 0; i < duringM.length; i++) {
            for (int j = 0; j < duringM.length; j++) {
                if (dxM[i][j] != 0 && dyM[i][j] != 0) {
                    JControlPoint temp = new JControlPoint((int) (preM[i][j].getX() + (elapsed / maxFrames * dxM[i][j])), (int) (preM[i][j].getY() + (elapsed / maxFrames * dyM[i][j])));
                    duringM[i][j] = temp;
                }
            }
        }
    }

    //public void
    private void findDistances() {
        dxP = new double[preP.length][preP.length];
        dyP = new double[preP.length][preP.length];
        for (int i = 0; i < preP.length; i++) {
            for (int j = 0; j < preP.length; j++) {
                dxP[i][j] = postP[i][j].getX() - preP[i][j].getX();
                dyP[i][j] = postP[i][j].getY() - preP[i][j].getY();
            }
        }
        dxM = new double[preM.length][preM.length];
        dyM = new double[preM.length][preM.length];
        for (int i = 0; i < preM.length; i++) {
            for (int j = 0; j < preM.length; j++) {
                dxM[i][j] = postM[i][j].getX() - preM[i][j].getX();
                dyM[i][j] = postM[i][j].getY() - preM[i][j].getY();
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        BufferedImage tmp = new BufferedImage(preImage.getWidth(), preImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < preTriangles.length; i++) {
            JMorphTools.warpTriangle(preImage, tmp, preTriangles[i], duringTriangles[i], null, null  /*1f-alpha*/);
        }
        g2d.setComposite(AlphaComposite.SrcOver.derive(1f-alpha ));
        g2d.drawImage(tmp, 0, 0, this);

        BufferedImage tmp2 = new BufferedImage(postImage.getWidth(), postImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < preTriangles.length; i++){
            JMorphTools.warpTriangle(postImage, tmp2, postTriangles[i], duringTriangles[i], null, null);
        }
        g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
        g2d.drawImage(tmp2, 0, 0, this);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(prePanel.getImage().getWidth(), prePanel.getImage().getHeight());
    }
}
