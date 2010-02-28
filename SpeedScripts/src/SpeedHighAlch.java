
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Map;
import javax.swing.JOptionPane;
import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Random;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.randoms.antiban.BreakHandler;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"LightSpeed"}, category = "Magic", name = "SpeedHighAlch", version = 1.0, description = "<html><head></head><body>Have items in inventory in noted form with item ID ready. \n This script will keep using alchemy until 10 items or less remain. \n Paint will be added later.</body></html>")
public class SpeedHighAlch extends Script implements ServerMessageListener, PaintListener {

    private int startExp = 1;
    private int startLvl = 1;
    private int alchItemID = 856;
    private Point itemPos = null;
    private boolean recordInitial = false;
    public long startTime = System.currentTimeMillis();
    public int errorCounter = 0;
    RSItem[] invItems = null;
    RSTile loc = null;
    private String version = "1.0";
    private int[] startExpArry;

    private boolean initialize() {
        startExp = skills.getCurrentSkillExp(Constants.STAT_MAGIC);
        startLvl = skills.getCurrentSkillLevel(Constants.STAT_MAGIC);
        itemPos = getItemPos();
        recordInitial = false;
        errorCounter = 0;
        loc = getLocation();
        startExpArry = new int[30];
        for (int i = 0; i < 20; i++) {
            startExpArry[i] = skills.getCurrentSkillExp(i);
        }
        return true;
    }

    private boolean checks() {
        if (recordInitial) {
            initialize();
        }
        if (distanceBetween(loc, getLocation()) > 10) {
            if (checkForRandoms()) {
                loc = getLocation();
            }
            return false;
        } else {
            Bot.disableRandoms = true;
        }
        antiBan();
        if (checkItemAmount() <= 1) {
            log("Items amount is <= 10. Stopping in 5-10 seconds.");
            wait(random(4500, 10500));
            stopScript();
        }
        return true;
    }

    private int checkItemAmount() {
        if (invItems == null) {
            invItems = getInventoryItems();
        }
        for (RSItem i : invItems) {
            if (i.getID() == alchItemID) {
                return i.getStackSize();
            }
        }
        return 0;
    }

    private boolean alchemy() {
        if (getCurrentTab() != Constants.TAB_MAGIC) {
            openTab(Constants.TAB_MAGIC);
        }
        while (!isPaused && checks()) {
            int waitCheck = 0;
            while (getCurrentTab() != Constants.TAB_MAGIC) {
                wait(random(50, 150));
                if (waitCheck > 20) {
                    wait(750);
                    moveMouse(578, 405);
                    wait(150);
                    clickMouse(true);
                    return false;
                }
                waitCheck++;
            }
            Bot.disableRandoms = true;
            if (!castSpell(Constants.SPELL_HIGH_LEVEL_ALCHEMY)) {
                return false;
            }
            waitCheck = 0;
            // Wait for the inventory to open
            while (getCurrentTab() != Constants.TAB_INVENTORY) {
                wait(random(50, 150));
                if (waitCheck > 20) {
                    wait(750);
                    moveMouse(578, 405);
                    wait(150);
                    clickMouse(true);
                    return false;
                }
                waitCheck++;
            }
            itemPos = getItemPos();
            while (itemPos.equals(new Point(-1, -1))) {
                if (!checks()) {
                    return false;
                }
                errorCounter++;
                if (errorCounter >= 30) {
                    log("Can't find item...");
                    return false;
                }
                itemPos = getItemPos();
            }
            if (itemPos.distance(getMouseLocation()) < 30 && getMenuIndex("Cast") != -1) {
                
            } else {
                moveMouse(itemPos, 5, 5);
            }
            if (!atMenu("Cast")) {
                return false;
            }
        }
        return false;
    }

    public Point getItemPos() {
        final int[] items = getInventoryArray();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == alchItemID) {
                //log("Item in slot: " + i);
                Point temp = getInventoryItemPoint(i);
                temp.translate(15, 15);
                return temp;
            }
        }
        return (new Point(-1, -1));
    }

    public void antiBan() {
        switch (random(0, 50)) {
            case 1:
            case 5:
            case 6: {
                openTab(TAB_STATS);
                //log("Opening stats page.");
                moveMouse(578, 405);
                wait(3500);
                break;
            }
            default: {
                break;
            }
        }
    }

    private boolean checkForRandoms() {
        for (final Random random : Bot.getScriptHandler().getRandoms()) {
            if (Bot.disableBreakHandler && (random instanceof BreakHandler)) {
                continue;
            }
            if (random.runRandom()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int loop() {
        if (RSInterface.getInterface(399).isValid() || !isLoggedIn() || skills.getRealSkillLevel(Constants.STAT_MAGIC) <= 54) {
            return random(900, 1000);
        }
        if (!isLoggedIn()) {
            Bot.disableRandoms = false;
            return 1000;
        }
        if (recordInitial || random(0, 10) == 5) {
            if (!checks()) {
                recordInitial = true;
                return 10000;
            }
        }
        while (alchemy()) {
            wait(random(200, 500));
        }
        Bot.disableRandoms = false;
        return 1500;
    }

    @Override
    public boolean onStart(Map<String, String> map) {
        alchItemID = Integer.parseInt(JOptionPane.showInputDialog("Enter the Id of the item to alchemize", 856));
        log("Item ID: " + alchItemID);
        if (alchItemID < 0) {
            return false;
        }
        recordInitial = true;
        Bot.disableRandoms = false;
        return true;
    }

    @Override
    public void onFinish() {
        long timeDiff = (System.currentTimeMillis() - startTime) / 1000;
        int hours = (int) ((timeDiff) / 3600);
        int min = (int) ((timeDiff) / 60) - hours * 60;
        log("Script Ran for: " + hours + " hours " + min + " min.");
        log(status());
        Bot.disableRandoms = false;
    }

    public String status() {
        String s = "";
        s += "Exp Gained: " + (skills.getCurrentSkillExp(Constants.STAT_MAGIC) - startExp);
        s += "Lvls Gained: " + (skills.getRealSkillLevel(Constants.STAT_MAGIC) - startLvl);
        return s;
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        String messageEvent = e.getMessage();
        if (messageEvent.contains("You do not have enough Fire Runes")) {
            log("No Fire Runes - Shutting Down in 5-10 Seconds");
            wait(random(4500, 10500));
            stopScript();
        } else if (messageEvent.contains("You do not have enough Nature Runes")) {
            log("No Nature Runes - Shutting Down in 5-10 Seconds");
            wait(random(4500, 10500));
            stopScript();
        }
    }

    @Override
    protected int getMouseSpeed() {
        return random(11, 13);
    }

    // ------------------------------PAINT--------------------------------------
    // Paint code from FoulFighter, thx
    public void onRepaint(Graphics g) {
        if (recordInitial) {
            return;
        }
        //refreshCounter++;
        // Font setting
        g.setFont(new Font("Century Gothic", Font.BOLD, 13));

        // Position reference var set
        int x = 0;
        int y = 28;

        // Run time calculation
        long millis = System.currentTimeMillis() - startTime;
        final long hours = millis / (1000 * 60 * 60);
        millis -= hours * 1000 * 60 * 60;
        final long minutes = millis / (1000 * 60);
        millis -= minutes * 1000 * 60;
        final long seconds = millis / 1000;
        paintBar(g, x, y, "SpeedHighAlch Total Runtime: " + hours + " - "
                + minutes + " : " + seconds);

        g.drawString("Version " + version, 436, y + 13);

        // Get mouse
        final Mouse mouse = Bot.getClient().getMouse();
        final int mouse_x = mouse.getMouseX();
        final int mouse_y = mouse.getMouseY();
        final int mouse_press_x = mouse.getMousePressX();
        final int mouse_press_y = mouse.getMousePressY();
        final long mouse_press_time = mouse.getMousePressTime();

        // Draw mouse
        Polygon po = new Polygon();
        po.addPoint(mouse_x, mouse_y);
        po.addPoint(mouse_x, mouse_y + 15);
        po.addPoint(mouse_x + 10, mouse_y + 10);
        g.setColor(new Color(180, 70, 70, 180));
        g.fillPolygon(po);
        g.drawPolygon(po);

        //Skill xp increase check
        for (int i = 0; i < 20; i++) {
            if ((startExpArry != null)
                    && ((skills.getCurrentSkillExp(i) - startExpArry[i]) > 0)) {
                paintSkillBar(g, x, y + 15, i, startExpArry[i]);
                y += 15;
            }
        }
    }

    /**
     *
     * @param g
     * @param x
     * @param y
     * @param skill
     * @param start
     */
    public void paintSkillBar(Graphics g, int x, int y, int skill, int start) {
        g.setFont(new Font("Century Gothic", Font.PLAIN, 13));

        int gained = (skills.getCurrentSkillExp(skill) - start);
        String s = SkillToString(skill) + " gained: " + gained;

        String firstLetter = s.substring(0, 1);
        String remainder = s.substring(1);
        String capitalized = firstLetter.toUpperCase() + remainder;
        String exp = Integer.toString(skills.getXPToNextLevel(skill));

        g.setColor(new Color(255, 0, 0, 90));
        g.fillRoundRect(416, y + 3, 100, 9, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(416, y + 3, 100, 9, 10, 10);
        g.setColor(new Color(0, 255, 0, 255));
        g.fillRoundRect(416, y + 3, skills.getPercentToNextLevel(skill), 9,
                10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(416, y + 3, skills.getPercentToNextLevel(skill), 9,
                10, 10);
        g.setColor(new Color(0, 200, 255));
        paintBar(g, x, y, capitalized);

        g.drawString(Integer.toString(skills.getPercentToNextLevel(skill)) + "%",
                458, y + 13);
        g.drawString("To lvl: " + exp, 200, y + 13);
        //if (refreshCounter > xpHourRefreshRate) {
        int xpHour = (int) (gained * 3600000.0
                / ((double) System.currentTimeMillis() - (double) startTime));
        //refreshCounter = 0;
        //}
        g.drawString("/hr: " + Integer.toString(Math.round(xpHour)), 335, y + 13);
    }

    /**
     *
     * @param g
     * @param x
     * @param y
     * @param s
     */
    public void paintBar(Graphics g, int x, int y, String s) {
        g.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        int width = 516;
        int height = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, y, width, height, 10, 10);

        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(0, y, width, height, 10, 10);

        g.setColor(new Color(255, 255, 255));
        g.drawString(s, x + 7, y + height - 2);
    }

    /**
     *
     * @param skill
     * @return
     */
    private String SkillToString(int skill) {
        return Skills.statsArray[skill];
    }
}
