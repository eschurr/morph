import javax.swing.*;
import java.awt.*;

public class JAnimate extends JFrame{

    private Container c;
    JAnimatePanel animatePanel;
    private JMorphPanel prePanel;

    public JAnimate(JMorphPanel prePanel, JMorphPanel postPanel, int seconds){
        super("JMorph");
        this.prePanel = prePanel;
        animatePanel = new JAnimatePanel(prePanel, postPanel, seconds);

        c = getContentPane();
        c.add(animatePanel);
        setVisible(true);
        setResizable(false);
        pack();
    }
}
