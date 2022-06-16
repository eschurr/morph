import javax.swing.*;
import java.awt.*;

public class JMorphAnimate extends JFrame{

    private Container c;

    JMorpherAnimatePanel animatePanel;

    private JMorphPanel prePanel;

    public JMorphAnimate(JMorphPanel prePanel, JMorphPanel postPanel, int seconds){
        super("JMorph");
        this.prePanel = prePanel;
        animatePanel = new JMorpherAnimatePanel(prePanel, postPanel, seconds);

        c = getContentPane();
        c.add(animatePanel);
        setVisible(true);
        setResizable(false);
        pack();
    }
}