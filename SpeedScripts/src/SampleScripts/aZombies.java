package SampleScripts;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "VitalIC" }, category = "Combat", name = "aZombies", version = 7.0, description = "<style type='text/css'>body</ style=\"font-family: Arial; padding: 7px;\">"
        + "<center><strong>aZombies</strong><br />by VitalIC from rsbot.org</center><p>"
        + "<center><strong>Take loot must be checked in the gui. </center><p>"
        + "<tr><td>Take charms: </td><td><center><input type='checkbox' name='charms' value='true'></font><br />"
        + "<tr><td>Take pure-essence: </td><td><center><input type='checkbox' name='pures' value='true'></font><br />"
        + "<tr><td>Take rannars: </td><td><center><input type='checkbox' name='rannars' value='true'></font><br />"
        + "<tr><td>Take planks: </td><td><center><input type='checkbox' name='planks' value='true'></font><br />"
        + "<tr><td>Take zombie champion scrools: </td><td><center><input type='checkbox' name='champs' value='true'></font><br />")
public class aZombies extends Script implements PaintListener,
        ServerMessageListener {
    public class aZombiesGUI extends JFrame {

        private static final long serialVersionUID = 1L;

        public aZombiesGUI() {
            initComponents();
        }

        private void button2ActionPerformed() {
            guiWait = false;
            guiExit = true;
            dispose();
        }

        private void button1ActionPerformed() {
            if (gui.comboBox1.getSelectedItem() == "Shark") {
                FOODID = 385;
            }
            if (gui.comboBox1.getSelectedItem() == "Monkfish") {
                FOODID = 7946;
            }
            if (gui.comboBox1.getSelectedItem() == "Lobster") {
                FOODID = 379;
            }
            if (gui.comboBox2.getSelectedItem() == "Combat") {
                usecombat = true;
            }
            if (gui.comboBox2.getSelectedItem() == "Super set") {
                usesuper = true;
            }
            if (gui.checkBox1.isSelected()) {
                takeLoot = true;
            }
            if (gui.checkBox3.isSelected()) {
                usebunny = true;
            }
            if (gui.comboBox3.getSelectedItem() == "Bank") {
                bankas = true;
            }
            if (gui.comboBox7.getSelectedItem() == "NOT") {
                spec = 1100;
            }
            if (gui.comboBox7.getSelectedItem() == "25") {
                spec = 250;
            }
            if (gui.comboBox7.getSelectedItem() == "50") {
                spec = 500;
            }
            if (gui.comboBox7.getSelectedItem() == "100") {
                spec = 1000;
            }
            if (gui.comboBox3.getSelectedItem() == "Bank") {
                bankas = true;
            }

            takes = Integer.parseInt(textField10.getText());
            takea = Integer.parseInt(textField11.getText());
            scount = Integer.parseInt(textField9.getText());

            potaath = Integer.parseInt(textField2.getText());
            potaat = Integer.parseInt(textField3.getText());
            potaats = Integer.parseInt(textField4.getText());

            guiWait = false;
            dispose();

        }

        private void initComponents() {
            // GEN-BEGIN:initComponents
            label1 = new JLabel();
            tabbedPane1 = new JTabbedPane();
            panel3 = new JPanel();
            comboBox1 = new JComboBox();
            label5 = new JLabel();
            label7 = new JLabel();
            textField2 = new JTextField();
            panel4 = new JPanel();
            label4 = new JLabel();
            comboBox2 = new JComboBox();
            label8 = new JLabel();
            label9 = new JLabel();
            textField3 = new JTextField();
            textField4 = new JTextField();
            panel5 = new JPanel();
            label10 = new JLabel();
            checkBox1 = new JCheckBox();
            panel6 = new JPanel();
            label11 = new JLabel();
            comboBox3 = new JComboBox();
            label21 = new JLabel();
            textField9 = new JTextField();
            label22 = new JLabel();
            label23 = new JLabel();
            textField10 = new JTextField();
            textField11 = new JTextField();
            panel11 = new JPanel();
            label20 = new JLabel();
            comboBox7 = new JComboBox();
            panel12 = new JPanel();
            label24 = new JLabel();
            checkBox3 = new JCheckBox();
            layeredPane1 = new JLayeredPane();
            button1 = new JButton();
            button2 = new JButton();
            label12 = new JLabel();
            new JLabel();

            // ======== this ========
            setTitle("Settings");
            setResizable(false);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setBackground(new Color(150, 102, 102));
            setAlwaysOnTop(true);
            Container contentPane = getContentPane();
            contentPane.setLayout(null);

            // ---- label1 ----
            label1.setText("aZombies");
            label1.setForeground(Color.WHITE);
            label1.setFont(new Font("Script MT Bold", Font.BOLD, 26));
            contentPane.add(label1);
            label1.setBounds(5, 5, 120, 35);

            // ======== tabbedPane1 ========
            {

                // ======== panel3 ========
                {
                    panel3.setLayout(null);

                    // ---- comboBox1 ----
                    comboBox1.setModel(new DefaultComboBoxModel(new String[] {
                            "Shark", "Monkfish", "Lobster" }));
                    panel3.add(comboBox1);
                    comboBox1.setBounds(new Rectangle(new Point(195, 30),
                            comboBox1.getPreferredSize()));

                    // ---- label5 ----
                    label5.setText("What food should bot use?");
                    label5.setFont(new Font("Tahoma", Font.PLAIN, 12));
                    panel3.add(label5);
                    label5.setBounds(10, 30, 160, 20);

                    // ---- label7 ----
                    label7.setText("When should bot eat food?");
                    label7.setFont(new Font("Tahoma", Font.PLAIN, 12));
                    panel3.add(label7);
                    label7.setBounds(10, 70, 160, 20);
                    panel3.add(textField2);
                    textField2.setBounds(195, 70, 40, textField2
                            .getPreferredSize().height);

                    { // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for (int i = 0; i < panel3.getComponentCount(); i++) {
                            Rectangle bounds = panel3.getComponent(i)
                                    .getBounds();
                            preferredSize.width = Math.max(bounds.x
                                    + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y
                                    + bounds.height, preferredSize.height);
                        }
                        Insets insets = panel3.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        panel3.setMinimumSize(preferredSize);
                        panel3.setPreferredSize(preferredSize);
                    }
                }
                tabbedPane1.addTab("Food", panel3);

                // ======== panel4 ========
                {
                    panel4.setLayout(null);

                    // ---- label4 ----
                    label4.setText("What pots do you want to use?");
                    panel4.add(label4);
                    label4.setBounds(new Rectangle(new Point(10, 20), label4
                            .getPreferredSize()));

                    // ---- comboBox2 ----
                    comboBox2.setModel(new DefaultComboBoxModel(new String[] {
                            "Super set", "Combat" }));
                    panel4.add(comboBox2);
                    comboBox2.setBounds(180, 15, 90, 22);

                    // ---- label8 ----
                    label8.setText("When to pot attack/combat pot  ?");
                    panel4.add(label8);
                    label8.setBounds(10, 60, 210, 14);

                    // ---- label9 ----
                    label9.setText("When to pot strenght ?");
                    panel4.add(label9);
                    label9.setBounds(10, 105, 151, 14);
                    panel4.add(textField3);
                    textField3.setBounds(180, 55, 40, 20);
                    panel4.add(textField4);
                    textField4.setBounds(180, 100, 40, 20);

                    { // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for (int i = 0; i < panel4.getComponentCount(); i++) {
                            Rectangle bounds = panel4.getComponent(i)
                                    .getBounds();
                            preferredSize.width = Math.max(bounds.x
                                    + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y
                                    + bounds.height, preferredSize.height);
                        }
                        Insets insets = panel4.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        panel4.setMinimumSize(preferredSize);
                        panel4.setPreferredSize(preferredSize);
                    }
                }
                tabbedPane1.addTab("Pots", panel4);

                // ======== panel5 ========
                {
                    panel5.setLayout(null);

                    // ---- label10 ----
                    label10.setText("Do you want to take loot?");
                    label10.setFont(new Font("Tahoma", Font.PLAIN, 12));
                    panel5.add(label10);
                    label10.setBounds(5, 20, 160, 20);

                    // ---- checkBox1 ----
                    checkBox1.setText("Yes");
                    panel5.add(checkBox1);
                    checkBox1.setBounds(new Rectangle(new Point(5, 65),
                            checkBox1.getPreferredSize()));

                    { // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for (int i = 0; i < panel5.getComponentCount(); i++) {
                            Rectangle bounds = panel5.getComponent(i)
                                    .getBounds();
                            preferredSize.width = Math.max(bounds.x
                                    + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y
                                    + bounds.height, preferredSize.height);
                        }
                        Insets insets = panel5.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        panel5.setMinimumSize(preferredSize);
                        panel5.setPreferredSize(preferredSize);
                    }
                }
                tabbedPane1.addTab("Loot", panel5);

                // ======== panel6 ========
                {
                    panel6.setLayout(null);

                    // ---- label11 ----
                    label11.setText("What to do when out of food?");
                    label11.setFont(new Font("Tahoma", Font.PLAIN, 12));
                    panel6.add(label11);
                    label11.setBounds(5, 5, 190, 20);

                    // ---- comboBox3 ----
                    comboBox3.setModel(new DefaultComboBoxModel(new String[] {
                            "Bank", "LogOut" }));
                    panel6.add(comboBox3);
                    comboBox3.setBounds(195, 5, 65, 22);

                    // ---- label21 ----
                    label21.setText("How many food to take from bank?");
                    label21.setFont(new Font("Tahoma", Font.PLAIN, 12));
                    panel6.add(label21);
                    label21.setBounds(5, 45, 200, 20);
                    panel6.add(textField9);
                    textField9.setBounds(210, 45, 40, 20);

                    // ---- label22 ----
                    label22.setText("How many str/cmb pots to take ?");
                    label22.setFont(new Font("Tahoma", Font.PLAIN, 12));
                    panel6.add(label22);
                    label22.setBounds(5, 75, 200, 20);

                    // ---- label23 ----
                    label23.setText("How many att pots to take ?");
                    label23.setFont(new Font("Tahoma", Font.PLAIN, 12));
                    panel6.add(label23);
                    label23.setBounds(5, 105, 200, 20);
                    panel6.add(textField10);
                    textField10.setBounds(210, 75, 40, 20);
                    panel6.add(textField11);
                    textField11.setBounds(210, 100, 40, 20);

                    { // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for (int i = 0; i < panel6.getComponentCount(); i++) {
                            Rectangle bounds = panel6.getComponent(i)
                                    .getBounds();
                            preferredSize.width = Math.max(bounds.x
                                    + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y
                                    + bounds.height, preferredSize.height);
                        }
                        Insets insets = panel6.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        panel6.setMinimumSize(preferredSize);
                        panel6.setPreferredSize(preferredSize);
                    }
                }
                tabbedPane1.addTab("Bank", panel6);

                // ======== panel11 ========
                {
                    panel11.setLayout(null);

                    // ---- label20 ----
                    label20.setText("When to use special ?");
                    label20.setFont(new Font("Tahoma", Font.PLAIN, 12));
                    panel11.add(label20);
                    label20.setBounds(15, 25, 160, 20);

                    // ---- comboBox7 ----
                    comboBox7.setModel(new DefaultComboBoxModel(new String[] {
                            "NOT", "25", "50", "100" }));
                    panel11.add(comboBox7);
                    comboBox7.setBounds(180, 25, 55, 22);

                    { // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for (int i = 0; i < panel11.getComponentCount(); i++) {
                            Rectangle bounds = panel11.getComponent(i)
                                    .getBounds();
                            preferredSize.width = Math.max(bounds.x
                                    + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y
                                    + bounds.height, preferredSize.height);
                        }
                        Insets insets = panel11.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        panel11.setMinimumSize(preferredSize);
                        panel11.setPreferredSize(preferredSize);
                    }
                }
                tabbedPane1.addTab("Special", panel11);

                // ======== panel12 ========
                {
                    panel12.setLayout(null);

                    // ---- label24 ----
                    label24.setText("Use bunyimp");
                    panel12.add(label24);
                    label24.setBounds(15, 10, 150, 40);

                    // ---- checkBox3 ----
                    checkBox3.setText("Yes");
                    panel12.add(checkBox3);
                    checkBox3.setBounds(15, 50, 43, 23);

                    { // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for (int i = 0; i < panel12.getComponentCount(); i++) {
                            Rectangle bounds = panel12.getComponent(i)
                                    .getBounds();
                            preferredSize.width = Math.max(bounds.x
                                    + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y
                                    + bounds.height, preferredSize.height);
                        }
                        Insets insets = panel12.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        panel12.setMinimumSize(preferredSize);
                        panel12.setPreferredSize(preferredSize);
                    }
                }
                tabbedPane1.addTab("Bunyimp", panel12);

            }
            contentPane.add(tabbedPane1);
            tabbedPane1.setBounds(10, 45, 280, 175);
            contentPane.add(layeredPane1);
            layeredPane1.setBounds(new Rectangle(new Point(45, 50),
                    layeredPane1.getPreferredSize()));

            // ---- button1 ----
            button1.setText("Start");
            button1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    button1ActionPerformed();
                }
            });
            contentPane.add(button1);
            button1.setBounds(new Rectangle(new Point(120, 230), button1
                    .getPreferredSize()));

            // ---- button2 ----
            button2.setText("Cancel");
            button2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    button2ActionPerformed();
                }
            });
            contentPane.add(button2);
            button2.setBounds(new Rectangle(new Point(205, 230), button2
                    .getPreferredSize()));

            // ---- label12 ----
            label12.setText("v7");
            label12.setForeground(Color.BLACK);
            label12.setFont(new Font("Script MT Bold", Font.BOLD, 26));
            label12.setBackground(new Color(51, 51, 51));
            contentPane.add(label12);
            label12.setBounds(310, 200, 40, 35);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for (int i = 0; i < contentPane.getComponentCount(); i++) {
                    Rectangle bounds = contentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width,
                            preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height,
                            preferredSize.height);
                }
                Insets insets = contentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                contentPane.setMinimumSize(preferredSize);
                contentPane.setPreferredSize(preferredSize);
            }
            setSize(355, 420);
            setLocationRelativeTo(getOwner());
            // GEN-END:initComponents
        }

        // GEN-BEGIN:variables
        private JLabel label1;
        private JTabbedPane tabbedPane1;
        private JPanel panel3;
        private JComboBox comboBox1;
        private JLabel label5;
        private JLabel label7;
        private JTextField textField2;
        private JPanel panel4;
        private JLabel label4;
        private JComboBox comboBox2;
        private JLabel label8;
        private JLabel label9;
        private JTextField textField3;
        private JTextField textField4;
        private JPanel panel5;
        private JLabel label10;
        private JCheckBox checkBox1;
        private JPanel panel6;
        private JLabel label11;
        private JComboBox comboBox3;
        private JLabel label21;
        private JTextField textField9;
        private JLabel label22;
        private JLabel label23;
        private JTextField textField10;
        private JTextField textField11;
        private JPanel panel11;
        private JLabel label20;
        private JComboBox comboBox7;
        private JPanel panel12;
        private JLabel label24;
        private JCheckBox checkBox3;
        private JLayeredPane layeredPane1;
        private JButton button1;
        private JButton button2;
        private JLabel label12;

        // GEN-END:variables
    }

    /*****************************************************
     * Imports.
     *****************************************************/

    final ScriptManifest properties = getClass().getAnnotation(
            ScriptManifest.class);
    int[] sss = {};
    int startXpq;
    boolean usesuper = false;
    private boolean guiWait = true;
    private boolean guiExit;
    boolean bankas = false;
    boolean usecombat = false;
    boolean usebunny = false;
    boolean takeLoot = false;
    private boolean takeplanks = false;
    private boolean takecharms = false;
    private boolean takepures = false;
    private boolean takerannar = false;
    private boolean takechamps = false;
    long startTime;
    long minutes;
    long seconds;
    long hours;
    long runTime;
    int sexpGained;
    int xexpGained;
    int qexpGained;
    int aexpGained;
    int rexpGained;
    int cmb1 = 9739;
    int cmb2 = 9741;
    int cmb3 = 9743;
    int cmb4 = 9745;
    int startXpa;
    int startXpr;
    int levelsGained;
    int startXp;
    int startLevel;
    int height;
    aZombiesGUI gui;
    public int coordsX = 0;
    public int coordsY = 0;
    int spot;
    int spot1 = 12140;
    int spot2 = 12142;
    int spot3 = 12146;
    int spot4 = 12144;
    int pouch = 12029;
    int spec;
    int scount;
    int takes;
    int takea;
    int potaat;
    int potaats;
    int fb = 11095;
    int potaath;
    public static final int Bankers = 6538;
    int vial = 229;
    int rock = 39153;
    int att = 145;
    int att1 = 147;
    int str2 = 157;
    int str1 = 159;
    int str = 161;
    int att2 = 149;
    int att3 = 2436;
    int str3 = 2440;
    int poting = 829;
    int[] strpots = { 157, 159, 161, 2440 };
    int[] attpots = { 145, 147, 149, 2436 };
    int[] cmbpots = { 9745, 9743, 9741, 9739 };
    int teleanim = 9603;
    final boolean maxAltitude = true;
    private final RSTile[] toZombz = { new RSTile(3265, 3674),
            new RSTile(3258, 3665), new RSTile(3265, 3653),
            new RSTile(3255, 3643), new RSTile(3254, 3631),
            new RSTile(3242, 3636), new RSTile(3236, 3624),
            new RSTile(3240, 3612), new RSTile(3241, 3609),
            new RSTile(3242, 3608) };
    private final RSTile[] toBank = { new RSTile(3162, 3671),
            new RSTile(3162, 3678), new RSTile(3170, 3682),
            new RSTile(3177, 3688), new RSTile(3183, 3692),
            new RSTile(3186, 3694) };
    private final RSTile zpmbie = new RSTile(3241, 9991);
    private final RSTile banker = new RSTile(3186, 3692);
    public static final int[] zombie = { 8149, 8150, 8151, 8152, 8153 };
    int ladder = 39191;
    public static final int[] trapd = { 39190 };
    int altar = 37990;
    int FOODID;
    int[] charms = { 12163, 12158, 12159, 12160 };
    int[] rannars = { 207, 000000 };
    int[] planks = { 8781, 8779, 961 };
    int[] pures = { 7937, 000000 };
    int amy;
    int amy1 = 3855;
    int amy2 = 3857;
    int amy3 = 3859;
    int amy4 = 3861;
    int amy5 = 3863;
    int amy6 = 3865;
    int amy8 = 3853;
    int zchampscrl [] =  {6807};

    /*****************************************************
     * Methods.
     *****************************************************/
    private boolean clickNPC(final RSNPC npc, final String action) {
        final RSTile tile = npc.getLocation();
        tile.randomizeTile(1, 1);
        try {
            final int hoverRand = random(8, 13);
            for (int i = 0; i < hoverRand; i++) {
                final Point screenLoc = npc.getScreenLocation();
                if (!pointOnScreen(screenLoc)) {
                    setCameraRotation(getCameraAngle() + random(-35, 150));
                    return true;
                }

                moveMouse(screenLoc, 15, 15);

                final List<String> menuItems = getMenuItems();
                if (menuItems.isEmpty() || menuItems.size() <= 1) {
                    continue;
                }
                if (menuItems.get(0).toLowerCase().contains(
                        npc.getName().toLowerCase())
                        && getMyPlayer().getInteracting() == null) {
                    clickMouse(true);
                    return true;
                } else {
                    for (int a = 1; a < menuItems.size(); a++) {
                        if (menuItems.get(a).toLowerCase().contains(
                                npc.getName().toLowerCase())
                                && getMyPlayer().getInteracting() == null) {
                            clickMouse(false);
                            return atMenu(action);
                        }
                    }
                }
            }

        } catch (final Exception e) {
            log.warning("ClickNPC error: " + e);
            return false;
        }
        return false;
    }

    public RSNPC getInteractingNPC() {
        final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();
        final org.rsbot.accessors.RSNPC[] npcs = Bot.getClient()
                .getRSNPCArray();

        for (final int element : validNPCs) {
            if (npcs[element] == null) {
                continue;
            }
            final RSNPC Monster = new RSNPC(npcs[element]);
            if (Monster.getInteracting() != null) {
                if (Monster.getInteracting().equals(getMyPlayer())) {
                    return Monster;
                }
            }
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // /Took from FoulFighter.

    private boolean listContainsString(List<String> list, String string) {
        try {
            int a;
            for (a = list.size() - 1; a-- >= 0;) {
                if (list.get(a).contains(string)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private boolean pickup(int[] id, String itemName) {
        boolean back = false;
        try {
            RSItemTile loots = getGroundItemByID(17, id);
            Point toscreen = Calculations.tileToScreen(loots);
            if (loots != null && !getMyPlayer().isMoving()) {
                back = true;
                if (pointOnScreen(toscreen)) {
                    moveMouse(toscreen, 3, 3);
                    wait(random(100, 200));
                    if (getMenuItems().size() > 1) {
                        if (listContainsString(getMenuItems(), itemName)) {
                            if (getMenuItems().get(0).contains(itemName)) {
                                clickMouse(true);
                                wait(random(750, 1000));
                            } else {
                                clickMouse(false);
                                wait(random(500, 750));
                                atMenu(itemName);
                                wait(random(750, 1000));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return back;
    }

    /*****************************************************
     * Booleans.
     *****************************************************/
    public boolean takecharms() {
        RSItemTile loot = getNearestGroundItemByID(charms);
        return loot != null && pickup(charms, "charm");
    }

    public boolean takerannar() {
        RSItemTile loot = getNearestGroundItemByID(rannars);
        return loot != null && pickup(rannars, "ranarr");
    }

    public boolean takeplanks() {
        RSItemTile loot = getNearestGroundItemByID(planks);
        return loot != null && pickup(planks, "lank");
    }

    public boolean takepures() {
        RSItemTile loot = getNearestGroundItemByID(pures);
        return loot != null && pickup(pures, "Pure");
    }

    public boolean takechamps() {
        RSItemTile loot = getNearestGroundItemByID(zchampscrl);
        return loot != null && pickup(zchampscrl, "Cham");
    }

    public boolean needPray() {
        return (skills.getCurrentSkillLevel(STAT_PRAYER) < 6);
    }

    public boolean goLadder() {
        if (distanceTo(getDestination()) < random(5, 12)) {
            if (!walkTileMM(zpmbie)) {
                walkTo(zpmbie);
            }
        }
        RSObject ladderd = getNearestObjectByID(ladder);
        return ladderd != null && atObject(ladderd, "Climb");
    }

    public boolean atZombz() {
        RSObject rck = getNearestObjectByID(rock);
        return rck != null && tileOnScreen(rck.getLocation());
    }

    public boolean bkill() {
        RSNPC zbz = getNearestFreeNPCByID(zombie);
        return zbz != null && clickNPC(zbz, "Attack");
    }

    public boolean gogogo() {
        RSObject trapdd = getNearestObjectByID(trapd);
        return trapdd != null && atObject(trapdd, "Enter");
    }

    public boolean atPray() {
        RSObject atlarr = getNearestObjectByID(altar);
        return atlarr != null && tileOnScreen(atlarr.getLocation());
    }

    public boolean weHaveNoFood() {
        return getInventoryCount(FOODID) == 0;
    }

    public boolean needToEat() {
        final int hp = skills.getCurrentSkillLevel(Constants.STAT_HITPOINTS);
        if (hp < potaath + random(+3, -3)) {
            return atInventoryItem(FOODID, "Eat");
        }
        return false;
    }

    public boolean weHaveFood() {
        return getInventoryCount(FOODID) != 0;
    }

    public boolean atBank() {
        RSNPC bnk = getNearestNPCByID(Bankers);
        return bnk != null && tileOnScreen(bnk.getLocation());
    }

    public boolean dontseeBanker() {
        return distanceTo(banker) < 20;
    }

    public boolean Pray() {
        RSObject altarr = getNearestObjectByID(altar);
        return altarr != null && atObject(altarr, "Pray");
    }

    public boolean PrayerEnabled() {
        return getSetting(1395) > 0;
    }

    public boolean SpecialEnabled() {
        return getSetting(301) == 1;
    }

    public boolean SummEnabled() {
        return getSetting(448) != -1;
    }

    /*****************************************************
     * Voids.
     *****************************************************/
    public void Banking() {
        final RSNPC Banker = getNearestNPCByID(Bankers);
        if (Banker != null) {
            if (!RSInterface.getInterface(INTERFACE_BANK).isValid()) {
                final RSTile bankerT = Banker.getLocation();
                if (distanceTo(bankerT) < 10) {
                    atNPC(Banker, "Bank ");
                }
                if (distanceTo(bankerT) > 10) {
                    wait(500);
                }
            } else {
                if (RSInterface.getInterface(INTERFACE_BANK).isValid()) {
                    if (getInventoryCount(amy) == 0) {
                        bank.depositAll();
                    }
                    if (getInventoryCount(amy) != 0) {
                        bank.depositAllExcept(amy);
                    }
                    wait(1000);
                    bank.withdraw(FOODID, scount);
                    wait(1000);
                    if (getInventoryCount(amy) == 0) {
                        bank.withdraw(3853, 1);
                        wait(1000);
                    }
                    if (usecombat) {
                        bank.withdraw(cmb1, takes);
                        wait(1000);
                    }
                    if (usesuper) {
                        bank.withdraw(att3, takea);
                        wait(1000);
                        bank.withdraw(str3, takes);
                        wait(1000);
                    }
                    if (usebunny) {
                        bank.withdraw(pouch, 3);
                        wait(1000);
                        bank.withdraw(spot1, 1);
                        wait(1000);
                    }
                    if (getInventoryCount(FOODID) != scount) {
                        wait(1000);
                        bank.deposit(FOODID, 20);
                        wait(1000);
                        bank.withdraw(FOODID, scount);
                    }
                    if (getInventoryCount(cmb1) != takes && (usecombat)) {
                        wait(1000);
                        bank.deposit(cmb1, 20);
                        wait(1000);
                        bank.withdraw(cmb1, takes);
                    }
                    if (getInventoryCount(str3) != takes && (usesuper)) {
                        wait(1000);
                        bank.deposit(str3, 20);
                        wait(1000);
                        bank.withdraw(str3, takes);
                    }
                    if (getInventoryCount(att3) != takea && (usesuper)) {
                        wait(1000);
                        bank.deposit(att3, 20);
                        wait(1000);
                        bank.withdraw(att3, takea);
                    }
                    if (getInventoryCount(spot) != 1 && (usebunny)) {
                        wait(1000);
                        bank.deposit(spot, 20);
                        wait(1000);
                        bank.withdraw(spot, 1);
                    }

                    if (getInventoryCount(pouch) != 3 && (usebunny)) {
                        wait(1000);
                        bank.deposit(spot, 10);
                        wait(1000);
                        bank.deposit(pouch, 10);
                        wait(1000);
                        bank.withdraw(pouch, 3);
                        wait(1000);
                        bank.withdraw(spot, 1);
                    }
                    bank.close();
                }
            }
        }
    }

    public void checkAmytobank() {
        if (inventoryContains(amy)) {
            atInventoryItem(amy, "Rub");
            clickMouse(random(202, 305), random(436, 433), true);
        }
    }

    public void checksafetobank() {
        if (inventoryContains(amy) && equipmentContains(fb)) {
            atInventoryItem(amy, "Rub");
            clickMouse(random(202, 305), random(436, 433), true);
        }
    }

    public void checkAmytowild() {
        if (inventoryContains(amy)) {
            atInventoryItem(amy, "Rub");
            clickMouse(random(234, 276), random(412, 419), true);
        }
    }

    private void run() {
        if (!isRunning()) {
            if (getEnergy() > 20) {
                setRun(true);
                wait(random(800, 1200));
            }
        }
    }

    public void needspot() {
        final int hp = skills.getCurrentSkillLevel(Constants.STAT_SUMMONING);
        if (hp < 7) {
            atInventoryItem(spot, "Drink");
            wait(random(2000, 4000));
        }
    }

    public void onPrayer() {
        if (!PrayerEnabled()) {
            clickMouse(random(715, 752), random(60, 80), true);

        }
    }

    public void offPrayer() {
        if (PrayerEnabled()) {
            clickMouse(random(715, 752), random(60, 80), true);
        }
    }

    public void usespec() {
        if (getCurrentTab() != Constants.TAB_ATTACK) {
            openTab(Constants.TAB_ATTACK);
            wait(random(300, 900));
        }
        if (getCurrentTab() == Constants.TAB_ATTACK) {
        clickMouse(random(578, 705), random(414, 426), true);
        }
    }

    public void summon() {
        if (inventoryContains(pouch)) {
            atInventoryItem(pouch, "Summon");
        }
    }

    /*****************************************************
     * Loop.
     *****************************************************/
    public int loop() {
        if (inventoryContains(FOODID)) {
            if (needToEat())
                return random(1000, 2000);
        }
        setCameraAltitude(maxAltitude);
        run();
        if (getMyPlayer().isMoving())
            return random(100, 200);
        if (getMyPlayer().getAnimation() == teleanim)
            return random(1000, 2000);
        if (getMyPlayer().getAnimation() == poting)
            return random(1000, 2000);

        if (inventoryContains(526)) {
            atInventoryItem(526, "Bury");
            return random(1000, 2000);
        }

        if (inventoryContains(vial)) {
            atInventoryItem(vial, "Drop");
            return random(1000, 2000);
        }

        if (isInventoryFull()) {
            atInventoryItem(FOODID, "Eat");
            return random(1000, 2000);
        }

        /*****************************************************
         * For items that i have 1 of each(yeah silly way to do it).
         *****************************************************/
        if (inventoryContains(amy1)) {
            amy = amy1;
        }
        if (inventoryContains(amy2)) {
            amy = amy2;
        }
        if (inventoryContains(amy3)) {
            amy = amy3;
        }
        if (inventoryContains(amy4)) {
            amy = amy4;
        }
        if (inventoryContains(amy5)) {
            amy = amy5;
        }
        if (inventoryContains(amy6)) {
            amy = amy6;
        }

        if (inventoryContains(amy8)) {
            amy = amy8;
        }

        if (inventoryContains(spot1)) {
            spot = spot1;
        }
        if (inventoryContains(spot2)) {
            spot = spot2;
        }
        if (inventoryContains(spot3)) {
            spot = spot3;
        }
        if (inventoryContains(spot4)) {
            spot = spot4;
        }
        /*****************************************************
         * Lol?.
         *****************************************************/
        if (weHaveFood()) {
            if (!atBank() && !atPray() && !atZombz() && !dontseeBanker()) {
                walkToZ();
                if (getMyPlayer().isInCombat() == true
                        && getInteractingNPC() != null) {
                    checksafetobank();
                    log("Revenant detection activated");
                    return random(100, 200);

                }
                return random(200, 500);

            } else {
                if (atBank()) {
                    if (getInventoryCount(amy) != 0) {
                        checkAmytowild();
                        return random(800, 1000);
                    } else {
                        log("No amulets left!!!");
                        stopScript();
                    }
                } else {
                    if (dontseeBanker()) {
                        checkAmytowild();
                        return random(800, 1000);
                    }
                }
            }
            if (needPray()) {
                if (atPray()) {
                    if (Pray())
                        setCameraRotation(random(-90, -150));
                    onPrayer();
                    return random(400, 500);

                } else {
                    if (goLadder()) {
                        setCameraRotation(getCameraAngle() + random(-90, -150));
                        return random(400, 500);
                    }
                }
            } else {
                if (atZombz()) {
                    onPrayer();
                    if (usesuper) {
                        if (inventoryContains(att)) {
                            checkPot();
                        }
                        if (inventoryContains(att1)) {
                            checkPot1();
                        }
                        if (inventoryContains(att2)) {
                            checkPot2();
                        }
                        if (inventoryContains(str)) {
                            checkPot3();
                        }
                        if (inventoryContains(str1)) {
                            checkPot4();
                        }
                        if (inventoryContains(str2)) {
                            checkPot5();
                        }
                        if (inventoryContains(att3)) {
                            checkPot6();
                        }
                        if (inventoryContains(str3)) {
                            checkPot7();
                        }
                    }
                    if (usecombat) {

                        if (inventoryContains(cmb4)) {
                            checkPot14();
                        }
                        if (inventoryContains(cmb3)) {
                            checkPot13();
                        }
                        if (inventoryContains(cmb2)) {
                            checkPot12();
                        }
                        if (inventoryContains(cmb1)) {
                            checkPot10();
                        }
                    }
                    if (!SpecialEnabled() && getSetting(300) >= spec) {
                        usespec();
                        return random(400, 500);
                    }
                    if (takeLoot) {
                        if (takecharms) {
                            if (takecharms())
                                return random(100, 200);
                        }
                        if (takerannar) {
                            if (takechamps())
                                return random(100, 200);
                        }
                        if (takerannar) {
                            if (takerannar())
                                return random(100, 200);
                        }
                        if (takeplanks) {
                            if (takeplanks())
                                return random(100, 200);
                        }
                        if (takechamps) {
                            if (takechamps())
                                return random(100, 200);
                        }
                        if (takepures) {
                            if (takepures())
                                return random(100, 200);
                        }

                    }
                    if (usebunny) {
                        if (inventoryContains(pouch) && !SummEnabled()) {
                            needspot();
                            summon();
                            return random(500, 1000);
                        }
                        if (getMyPlayer().getInteracting() != null)
                            return random(500, 1000);
                        if (bkill())
                            return random(2000, 2200);
                    }

                    if (getMyPlayer().getInteracting() != null)
                        return random(1000, 500);
                    if (!usebunny) {
                        if (getMyPlayer().getInteracting() != null)
                            return random(500, 1000);
                        if (bkill())
                            return random(2000, 2200);
                    }
                } else {
                    if (gogogo())
                        setCameraRotation(random(-90, -150));
                    return random(400, 500);
                }
            }
        }
        if (weHaveNoFood()) {
            if (!atBank() && !atPray() && !atZombz()) {
                offPrayer();
                walkToBank();
                return random(800, 1000);
            } else {
                if (atBank()) {
                    Banking();
                    return random(400, 500);
                } else {
                    if (atPray()) {
                        if (bankas) {
                            if (inventoryContains(3867)) {
                                amy = 3867;
                            }
                            checkAmytobank();
                            return random(400, 500);
                        }
                        if (!bankas) {
                            wait(random(4000, 5000));
                            stopScript();
                            logout();
                        }
                        return random(100, 200);
                    } else {
                        if (atZombz()) {
                            if (goLadder()) {
                                setCameraRotation(getCameraAngle()
                                        + random(-90, -150));
                                return random(400, 500);
                            }
                        }
                    }
                }
            }
        }
        return 800;
    }

    /***********************************************************************************************
     * On start and others..Yeah potting is messed up. Why didnt i just did
     * (pots: id)?
     ***********************************************************************************************/
    public void checkPot() {
        final int attx = skills.getCurrentSkillLevel(Constants.STAT_ATTACK);
        if (attx <= potaat + random(+1, -1)) {
            atInventoryItem(att, "Drink");
            wait(random(2000, 4000));
        }
    }

    public void checkPot1() {
        final int attx = skills.getCurrentSkillLevel(Constants.STAT_ATTACK);
        if (attx <= potaat + random(+1, -1)) {
            atInventoryItem(att1, "Drink");
            wait(random(2000, 4000));
        }
    }

    public void checkPot2() {
        final int attx = skills.getCurrentSkillLevel(Constants.STAT_ATTACK);
        if (attx <= potaat + random(+1, -1)) {
            atInventoryItem(att2, "Drink");
            wait(random(2000, 4000));
        }
    }

    public void checkPot3() {
        final int strx = skills.getCurrentSkillLevel(Constants.STAT_STRENGTH);
        if (strx <= potaats + random(+1, -1)) {
            atInventoryItem(str, "Drink");
            wait(random(2000, 4000));
        }
    }

    public void checkPot4() {
        final int strx = skills.getCurrentSkillLevel(Constants.STAT_STRENGTH);
        if (strx <= potaats + random(+1, -1)) {
            atInventoryItem(str1, "Drink");
            wait(random(2000, 4000));
        }
    }

    public void checkPot5() {
        final int strx = skills.getCurrentSkillLevel(Constants.STAT_STRENGTH);
        if (strx <= potaats + random(+1, -1)) {
            atInventoryItem(str2, "Drink");
            wait(random(2000, 4000));
        }
    }

    public void checkPot6() {

        final int attx = skills.getCurrentSkillLevel(Constants.STAT_ATTACK);
        if (attx <= potaat + random(+1, -1)) {
            atInventoryItem(att3, "Drink");
            wait(random(2000, 4000));
        }
    }

    public void checkPot7() {
        final int strx = skills.getCurrentSkillLevel(Constants.STAT_STRENGTH);
        if (strx <= potaats + random(+1, -1)) {
            atInventoryItem(str3, "Drink");
            wait(random(2000, 4000));
        }
    }

    private void checkPot13() {
        final int attx = skills.getCurrentSkillLevel(Constants.STAT_ATTACK);
        if (attx <= potaat + random(+1, -1)) {
            atInventoryItem(cmb3, "Drink");
            wait(random(2000, 4000));
        }
    }

    private void checkPot12() {
        final int attx = skills.getCurrentSkillLevel(Constants.STAT_ATTACK);
        if (attx <= potaat + random(+1, -1)) {
            atInventoryItem(cmb2, "Drink");
            wait(random(2000, 4000));
        }
    }

    private void checkPot14() {
        final int attx = skills.getCurrentSkillLevel(Constants.STAT_ATTACK);
        if (attx <= potaat + random(+1, -1)) {
            atInventoryItem(cmb4, "Drink");
            wait(random(2000, 4000));
        }
    }

    private void checkPot10() {
        final int attx = skills.getCurrentSkillLevel(Constants.STAT_ATTACK);
        if (attx <= potaat + random(+1, -1)) {
            atInventoryItem(cmb1, "Drink");
            wait(random(2000, 4000));
        }
    }

    public boolean onStart(Map<String, String> args) {
        startTime = System.currentTimeMillis();
        startXp = skills.getCurrentSkillExp(STAT_STRENGTH);
        startXpa = skills.getCurrentSkillExp(STAT_ATTACK);
        startXpr = skills.getCurrentSkillExp(STAT_DEFENSE);
        startXpq = skills.getCurrentSkillExp(STAT_HITPOINTS);
        if (args.get("charms") != null) {
            takecharms = true;
        }
        if (args.get("champs") != null) {
            takechamps = true;
        }
        if (args.get("pures") != null) {
            takepures = true;
        }
        if (args.get("planks") != null) {
            takeplanks = true;
        }
        if (args.get("rannars") != null) {
            takerannar = true;
        }
        gui = new aZombiesGUI();
        gui.setVisible(true);
        while (guiWait) {
            wait(100);
        }
        return !guiExit;
    }

    private int walkToBank() {
        try {
            if (distanceTo(getDestination()) > 2) {
            }
            walkTo(banker);
        } catch (final Exception ignored) {
        }
        return 50;
    }

    private int walkToZ() {
        if (distanceTo(getDestination()) < random(3, 4)
                || distanceTo(getDestination()) > 40) {
            if (!walkPathMM(fixPath(toZombz))) {
                walkToClosestTile(toZombz, 2, 2);
            } else {
                if (walkPathMM(fixPath(toZombz)));
            }
        }
        return 0;
    }

    public void onFinish() {

    }

    public void serverMessageRecieved(
            final ServerMessageEvent servermessageevent) {
        final String s = servermessageevent.getMessage().toLowerCase();
        if (s.contains("space in your")) {
            log("Bank is Full STOP");
            stopScript();

        }
    }

    public void onRepaint(final Graphics g) {
        final Mouse m = Bot.getClient().getMouse();
        runTime = System.currentTimeMillis() - startTime;
        seconds = runTime / 1000;
        if (seconds >= 60) {
            minutes = seconds / 60;
            seconds -= minutes * 60;
        }
        if (minutes >= 60) {
            hours = minutes / 60;
            minutes -= hours * 60;
        }
        final int x = 3;
        int y = 200;
        final int xpHoura = ((int) ((3600000.0 / (double) runTime) * qexpGained));
        final int xpHourb = ((int) ((3600000.0 / (double) runTime) * sexpGained));
        final int xpHourc = ((int) ((3600000.0 / (double) runTime) * aexpGained));
        final int xpHourd = ((int) ((3600000.0 / (double) runTime) * rexpGained));
        sexpGained = skills.getCurrentSkillExp(STAT_STRENGTH) - startXp;
        aexpGained = skills.getCurrentSkillExp(STAT_ATTACK) - startXpa;
        rexpGained = skills.getCurrentSkillExp(STAT_DEFENSE) - startXpr;
        xexpGained = skills.getCurrentSkillExp(STAT_HITPOINTS) - startXpq;
        qexpGained = sexpGained + aexpGained + rexpGained + xexpGained;
        g.setColor(new Color(0, 0, 0, 50));
        g.fillRoundRect(x, y, 200, height, 5, 5);
        if (m.x > x + 200 || m.x < x || m.y > height + 250 || m.y < y) {
            g.setColor(new Color(0, 50, 0, 0));
            g.fillRoundRect(x, y, 200, height, 5, 5);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Italic", Font.BOLD, 14));
            g.drawString("aZombies", x + 9, y += 36);
        } else {
            g.setColor(new Color(0, 0, 0, 50));
            g.fillRoundRect(x, y, 200, height, 5, 5);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("aZombies", x + 9, y += 36);
            g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds,
                    x + 9, y += 16);
            g.drawString(" Strenght XP Gained: " + sexpGained, x + 9, y += 16);
            g.drawString(" Attack XP Gained: " + aexpGained, x + 9, y += 16);
            g.drawString(" Deffence XP Gained: " + rexpGained, x + 9, y += 16);
            g.drawString(" HitPoints XP Gained: " + xexpGained, x + 9, y += 16);
            g.drawString(" Total XP Gained: " + qexpGained, x + 9, y += 16);
            g.drawString(" Total xp per hour : " + xpHoura, x + 9, y += 16);
            g.drawString(" Strenght xp per hour : " + xpHourb, x + 9, y += 16);
            g.drawString(" Attack xp per hour : " + xpHourc, x + 9, y += 16);
            g.drawString(" Defence xp per hour : " + xpHourd, x + 9, y += 16);
        }
        height = y - 200;
        if (toZombz != null) {
            for (int i = 0; i < toZombz.length - 1; i++) {
                final Point P = tileToMinimap(toZombz[i]);
                final Point p = tileToMinimap(toZombz[i + 1]);
                if (P.x != -1 && P.y != -1 && p.x != -1 && p.y != -1) {
                    g.drawLine(p.x, p.y, P.x, P.y);
                } else {
                    if (toBank != null) {
                        for (int a = 0; a < toBank.length - 1; a++) {
                            final Point s = tileToMinimap(toBank[a]);
                            final Point d = tileToMinimap(toBank[a + 1]);
                            if (s.x != -1 && s.y != -1 && d.x != -1
                                    && d.y != -1) {
                                g.drawLine(d.x, d.y, s.x, s.y);
                            }
                        }
                    }
                }
            }
        }
    }
}