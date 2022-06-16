import javax.imageio.ImageIO;
import javax.swing.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

public class JMorph extends JFrame implements ActionListener, ChangeListener {

    private Container c;

    private JFrame animate;

    private JAnimate morphed;

    private JPanel prePanelPad, postPanelPad;

    private JMorphPanel prePanel, postPanel;
    private int row, col, tempRow, tempCol;

    private JPanel controls, title;

    private JLabel resolutionLabel;
    private JSlider resolutionSlider;
    private boolean resolutionChange = false;

    private float preIntensity = 1f;
    private float tempPreIntensity;
    private boolean preIntensityChange = false;
    private JLabel preIntensityLabel;
    private JSlider preIntensitySlider;

    private float postIntensity = 1f;
    private float tempPostIntensity;
    private boolean postIntensityChange = false;
    private JLabel postIntensityLabel;
    private JSlider postIntensitySlider;

    private int seconds = 1;

    private JLabel secondsLabel;
    private JSlider secondsSlider;

    private JButton reset, run, apply, groupButton;

    private JMenuBar menuBar;
    private JMenu imageMenu;
    private JMenuItem loadPre, loadPost;
    private JFileChooser fc;
    private BufferedImage pre, post, preFiltered, postFiltered;
    private boolean preLoaded, postLoaded;

    public JMorph() {

        super("JMorph");

        // initialize image panels

        row = 10;
        col = 10;

        prePanelPad = new JPanel();
        postPanelPad = new JPanel();

        prePanel = new JMorphPanel(row, col, "pre");
        prePanel.setBackground(Color.WHITE);

        prePanelPad.add(prePanel);
        prePanelPad.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        postPanel = new JMorphPanel(row, col, "post");
        postPanel.setBackground(Color.WHITE);
        postPanelPad.add(postPanel);
        postPanelPad.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        prePanel.setOther(postPanel);
        postPanel.setOther(prePanel);

        prePanel.addMouseListener(prePanel);
        prePanel.addMouseMotionListener(prePanel);

        postPanel.addMouseMotionListener(postPanel);
        postPanel.addMouseListener(postPanel);

        title = new JPanel();
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // control panel

        controls = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;

        resolutionLabel = new JLabel("Resolution", SwingConstants.CENTER);
        controls.add(resolutionLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        resolutionSlider = new JSlider(JSlider.HORIZONTAL, 5, 20, 10);
        resolutionSlider.addChangeListener(this);
        resolutionSlider.setName("resolution");
        resolutionSlider.setMajorTickSpacing(5);
        resolutionSlider.setPaintTicks(true);
        resolutionSlider.setPaintLabels(true);
        controls.add(resolutionSlider, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;

        secondsLabel = new JLabel("Morph Time", SwingConstants.CENTER);
        controls.add(secondsLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;

        secondsSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        secondsSlider.addChangeListener(this);
        secondsSlider.setName("seconds");
        secondsSlider.setSnapToTicks(true);
        secondsSlider.setMajorTickSpacing(1);
        secondsSlider.setPaintTicks(true);
        secondsSlider.setPaintLabels(true);
        controls.add(secondsSlider, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;

        preIntensityLabel = new JLabel("Pre-Brightness", SwingConstants.CENTER);
        controls.add(preIntensityLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;

        preIntensitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        preIntensitySlider.addChangeListener(this);
        preIntensitySlider.setName("pre-intensity");
        preIntensitySlider.setMajorTickSpacing(20);
        preIntensitySlider.setPaintTicks(true);
        preIntensitySlider.setPaintLabels(true);
        controls.add(preIntensitySlider, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;

        postIntensityLabel = new JLabel("Post-Brightness", SwingConstants.CENTER);
        controls.add(postIntensityLabel, constraints);

        constraints.gridx = 4;

        groupButton = new JButton("Control Point Group Move");
        groupButton.setActionCommand("group");
        groupButton.addActionListener(this);
        controls.add(groupButton, constraints);

        constraints.gridx = 2;
        constraints.gridy = 3;

        postIntensitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        postIntensitySlider.addChangeListener(this);
        postIntensitySlider.setName("post-intensity");
        postIntensitySlider.setMajorTickSpacing(20);
        postIntensitySlider.setPaintTicks(true);
        postIntensitySlider.setPaintLabels(true);
        controls.add(postIntensitySlider, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;

        run = new JButton("Run");
        run.setActionCommand("run");
        run.addActionListener(this);
        controls.add(run, constraints);

        constraints.gridx = 1;
        constraints.gridy = 4;

        reset = new JButton("Reset");
        reset.setActionCommand("reset");
        reset.addActionListener(this);
        controls.add(reset, constraints);

        constraints.gridx = 2;
        constraints.gridy = 4;

        apply = new JButton("Apply");
        apply.setActionCommand("apply");
        apply.addActionListener(this);
        controls.add(apply, constraints);

        constraints.gridx = 3;
        constraints.gridy = 4;

        controls.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // menu bar

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        imageMenu = new JMenu("Image");
        menuBar.add(imageMenu);

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "tif"));
        fc.setAcceptAllFileFilterUsed(false);

        preLoaded = false;
        loadPre = new JMenuItem("Load Preimage");
        loadPre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(JMorph.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        pre = ImageIO.read(file);
                    } catch (IOException e1){};
                    prePanelPad.remove(prePanel);
                    prePanelPad.repaint();
                    prePanel = new JMorphPanel(row, col, "pre");
                    prePanel.setOther(postPanel);
                    postPanel.setOther(prePanel);
                    prePanel.addMouseListener(prePanel);
                    prePanel.addMouseMotionListener(prePanel);
                    prePanel.setImage(pre);
                    prePanel.getPreferredSize();
                    prePanel.repaint();
                    prePanelPad.add(prePanel);
                    setVisible(true);
                    preIntensity = 50;
                    preIntensitySlider.setValue((int)preIntensity);
                    postPanelPad.remove(postPanel);
                    postPanelPad.repaint();
                    postPanel.getPreferredSize();
                    postPanel.repaint();
                    postPanelPad.add(postPanel);
                    setVisible(true);
                    preLoaded = true;

                }
            }
        });
        imageMenu.add(loadPre);

        postLoaded = false;

        loadPost = new JMenuItem("Load Postimage");
        loadPost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(prePanel.isImgLoaded()) {
                    int returnVal = fc.showOpenDialog(JMorph.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            post = ImageIO.read(file);
                        } catch (IOException e1) {
                        }
                        postPanelPad.remove(postPanel);
                        prePanelPad.repaint();
                        postPanel = new JMorphPanel(row, col, "post");
                        postPanel.setOther(prePanel);
                        prePanel.setOther(postPanel);
                        postPanel.addMouseListener(postPanel);
                        postPanel.addMouseMotionListener(postPanel);
                        postPanel.setImage(post);
                        postPanel.getPreferredSize();
                        postPanel.repaint();
                        postPanelPad.add(postPanel);
                        setVisible(true);
                        postIntensity = 50;
                        postIntensitySlider.setValue((int) postIntensity);
                        postLoaded = true;

                    }
                    else{
                        JOptionPane.showMessageDialog( JMorph.this,"Preimage must be loaded before postimage");
                    }
                }
            }
        });
        imageMenu.add(loadPost);

        c = getContentPane();
        c.add(prePanelPad, BorderLayout.WEST);
        c.add(postPanelPad, BorderLayout.EAST);
        c.add(title, BorderLayout.NORTH);
        c.add(controls, BorderLayout.SOUTH);

        setBackground(Color.LIGHT_GRAY);
        setResizable(false);
        pack();
        setVisible(true);
    }

    // button control

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("run")) {
            if (preLoaded && postLoaded) {
                morphed = new JAnimate(prePanel, postPanel, seconds);
            }
            else{
                JOptionPane.showMessageDialog( JMorph.this,"Must load both images!");
            }
        }
        if (e.getActionCommand().equals("reset")) {
            int n = JOptionPane.showConfirmDialog( this,"Are you sure you want to reset?\n"
                            + "All Progress will be lost!",
                    "Confirm Reset",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_NO_OPTION){
                prePanelPad.removeAll();
                postPanelPad.removeAll();
                prePanel = new JMorphPanel(row, col, "pre");
                prePanel.setOther(postPanel);
                postPanel.setOther(prePanel);
                prePanel.addMouseListener(prePanel);
                prePanel.addMouseMotionListener(prePanel);
                prePanel.setBackground(Color.WHITE);
                prePanel.getPreferredSize();
                prePanelPad.add(prePanel);
                setVisible(true);
                pre = null;

                postPanel = new JMorphPanel(row,col, "post");
                postPanel.setOther(prePanel);
                prePanel.setOther(postPanel);
                postPanel.addMouseListener(postPanel);
                postPanel.addMouseMotionListener(postPanel);
                postPanel.setBackground(Color.WHITE);
                postPanel.getPreferredSize();
                postPanelPad.add(postPanel);
                setVisible(true);
                post = null;

                preIntensity = 50;
                preIntensitySlider.setValue((int)preIntensity);

                postIntensity = 50;
                postIntensitySlider.setValue((int)postIntensity);

            }
        }
        if (e.getActionCommand().equals("group")) {
            if(prePanel.getGroupSelected() && postPanel.getGroupSelected()) {
                prePanel.setGroupSelected(false);
                postPanel.setGroupSelected(false);
            }
            else{
                prePanel.setGroupSelected(true);
                postPanel.setGroupSelected(true);
            }
        }
        if(e.getActionCommand().equals("apply")){

            if(resolutionChange) {
                resolutionChange = false;
                row = tempRow;
                col = tempCol;
                prePanelPad.remove(prePanel);
                prePanel = new JMorphPanel(row, col, "pre");
                prePanel.setOther(postPanel);
                postPanel.setOther(prePanel);
                prePanel.addMouseListener(prePanel);
                prePanel.addMouseMotionListener(prePanel);
                if (pre != null) {
                    prePanel.setImage(pre);
                } else {
                    prePanel.setBackground(Color.WHITE);
                }
                prePanel.getPreferredSize();
                prePanel.repaint();
                prePanelPad.add(prePanel);
                setVisible(true);

                postPanelPad.remove(postPanel);
                postPanel = new JMorphPanel(row, col, "post");
                postPanel.setOther(prePanel);
                prePanel.setOther(postPanel);
                postPanel.addMouseListener(postPanel);
                postPanel.addMouseMotionListener(postPanel);
                if (post != null) {
                    postPanel.setImage(post);
                } else {
                    postPanel.setBackground(Color.WHITE);
                }
                postPanel.getPreferredSize();
                postPanel.repaint();
                postPanelPad.add(postPanel);
                setVisible(true);
                resolutionChange = false;
            }
            if(preIntensityChange) {
                preIntensityChange = false;
                float val = tempPreIntensity;
                preIntensity = val / 50;
                if (preIntensity == 0.0f) {
                    preIntensity = 0.01f;
                }
                preFiltered = new BufferedImage(pre.getWidth(), pre.getHeight(), BufferedImage.TYPE_INT_RGB);
                Kernel k = new Kernel(1, 1, new float[]{preIntensity});
                ConvolveOp op = new ConvolveOp(k);
                preFiltered = op.filter(pre, null);
                prePanel.setImage(preFiltered);
                preIntensityChange = false;
            }
            if(postIntensityChange) {
                postIntensityChange = false;
                float val = tempPostIntensity;
                postIntensity = val / 50;
                if (postIntensity == 0.0f) {
                    postIntensity = 0.01f;
                }
                postFiltered = new BufferedImage(post.getWidth(), post.getHeight(), BufferedImage.TYPE_INT_RGB);
                Kernel k = new Kernel(1, 1, new float[]{postIntensity});
                ConvolveOp op = new ConvolveOp(k);
                postFiltered = op.filter(post, null);
                postPanel.setImage(postFiltered);
                postPanel.repaint();
                postIntensityChange = false;
            }
        }
    }

    // slider control

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (source.getValueIsAdjusting()) {
            if(source.getName().equals("resolution")) {
                tempRow = source.getValue();
                tempCol = source.getValue();
                resolutionChange = true;
            }
            if(source.getName().equals("pre-intensity")){
                if (pre != null) {

                    tempPreIntensity = source.getValue();
                    preIntensityChange = true;

                }
            }
            if(source.getName().equals("post-intensity")){
                if (post != null) {

                    tempPostIntensity = source.getValue();
                    postIntensityChange = true;

                }

            }
            if(source.getName().equals("seconds")) {
                seconds = source.getValue();
            }
        }
    }

    public static void main(String[] args) {
        JMorph J = new JMorph();
        J.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}