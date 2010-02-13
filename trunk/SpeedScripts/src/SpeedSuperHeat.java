
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Map;
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
import org.rsbot.script.randoms.BankPins;
import org.rsbot.script.randoms.antiban.BreakHandler;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSTile;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Sunny
 */
@ScriptManifest(authors = {"LightSpeed, Pirateblanc"}, category = "Magic",
name = "SpeedSuperHeat", version = 1.0, description = "<html><head>"
+ "<style type=\"text/css\">"
+ "body {"
+ "   color: #FFFFFF;"
+ "   background-color: #000000;"
+ "}"
+ "</style>"
+ "</head>"
+ "<body>"
+ "<center>"
+ "<img src=\"http://wandohigh.com/clubs/cpc/website/files/rsbot-logo.jpg\""
+ "alt=\"SpeedSuperHeat\">"
+ "</center>"
+ "<hr>"
+ "<center>Created by LightSpeed & Pirateblanc</center>"
+ "<hr><br>"
+ "<form>"
+ "Select Your Ore: "
+ "<select name=\"ore\">"
+ "   <option>Steel<option>Runite<option>Adamantite<option>Mithril<option>Gold"
+ "   <option>Silver<option>Iron"
+ "</select>"
+ "<br>"
+ "Logout On Crash? <input type=\"checkbox\" name=\"logout\" value=\"true\">"
+ "<br>"
+ "Lag Time For Banking (sec): <select name=\"lag\">"
+ "   <option>1.0<option>2.0"
+ "</input>"
+ "</form><br>"
+ "<p>For the script to work, you must have your ore, coal, coins, and bars "
+ "in the first tab of your bank. They must also be on the first row!</p>"
+ "<p>You must make sure nature runes are in your inventory, and "
+ "that you are near a bank :)</p>"
+ "</body>"
+ "</html>")
public class SpeedSuperHeat extends Script implements ServerMessageListener, PaintListener {

    private int oreID;
    private boolean recordInitial = false;
    private long startTime;
    private int startExp;
    private int startLvl;
    private RSTile loc;
    private int coalRatio = 0;
    private final int coalID = 453;
    private int barID = 0;
    private int errorCounter = 0;
    private boolean logOutDone = false;
    private double lagFactor = 1.0;
    private final double version = 1.0;
    private int[] startExpArry = null;

    public boolean initialized() {
        startExp = skills.getCurrentSkillExp(Constants.STAT_MAGIC); //save the initial exp and lvl
        startLvl = skills.getRealSkillLevel(Constants.STAT_MAGIC);
        loc = getLocation();
        loc = new RSTile(loc.getX(), loc.getY()); //save initial location
        startTime = System.currentTimeMillis();
        return oreInitialize(); //get coal ratio
    }

    public boolean oreInitialize() {
        switch (barID) {
            case 2363: //runite
            {
                oreID = 451;
                coalRatio = 8;
                break;
            }
            case 2361: //adamantite
            {
                oreID = 449;
                coalRatio = 6;
                break;
            }
            case 2359: //mithril
            {
                oreID = 447;
                coalRatio = 4;
                break;
            }
            case 2357: //gold
            {
                oreID = 444;
                coalRatio = 0;
                break;
            }
            case 2355: //silver
            {
                oreID = 442;
                coalRatio = 0;
                break;
            }
            case 2353: //steel
            {
                oreID = 440;
                coalRatio = 2;
                break;
            }
            case 2351: //iron
            {
                oreID = 440;
                coalRatio = 0;
                break;
            }
            default: {
                log("Invalid ID. Please try again...");
                stopScript();
                return false;
            }
        }
        return true;
    }

    public boolean superHeat() {
        if (getCurrentTab() != Constants.TAB_MAGIC
                && !RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()
                && !RSInterface.getInterface(Constants.INTERFACE_STORE).isValid()) {
            openTab(Constants.TAB_MAGIC);
        }
        Bot.disableRandoms = true;
        while (true) { //ends when you can't find the item
            if (isPaused) {
                return true;
            }
            if (!castSpell(Constants.SPELL_SUPERHEAT_ITEM)) //cast the spell
            {
                return false;
            }
            int waitCheck = 0;
            while (getCurrentTab() != Constants.TAB_INVENTORY) { //wait for the inventory to open
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
            int[] inventoryArray = getInventoryArray(); //get the last position of the ore
            int startItem = -1;
            for (int i = 27; i > 0; i--) {
                if (inventoryArray[i] == oreID) {
                    startItem = i;
                    break;
                }
            }
            Point itemPos = getInventoryItemPoint(startItem);
            if (itemPos.equals(new Point(-1, -1))) { //end method no more items
                wait(750);
                moveMouse(578, 405, 10, 10);
                wait(150);
                clickMouse(true); //cast spell at empty space
                return true;
            } else {
                do {
                    if (waitCheck > 3) {
                        return false;
                    }
                    wait(random(500, 750));
                    moveMouse(itemPos.x + 10, itemPos.y + 10, 5, 5); //mouse mouse to ore
                    wait(random(500, 750));
                    if (getMenuActions().get(0).contains("Cast")) { //if first option cast then cast
                        log("Amount of ore left: " + getInventoryCount(oreID));
                        if (getCurrentTab() == Constants.TAB_MAGIC) {
                            break;
                        } else if (getInventoryCount(oreID) <= 1) {
                            clickMouse(true);
                            return true;
                        } else {
                            clickMouse(true);
                        }
                    } else {
                        atMenu("Cast"); //otherwise open menu and cast
                    }
                    wait(random(750, 1250));
                    waitCheck++;
                } while (getCurrentTab() != Constants.TAB_MAGIC);
            }
        }

    }

    public boolean withdraw() {
        if (!bank.isOpen()) {
            wait(1500);
            if (!bank.open()) {
                wait(1500);
                log("Error: can't open bank");
                return false;
            }
        }
        double speedfactor = lagFactor;
        lagFactor = 2.0;
        int counter = 0; //to find maximum withdrawl size
        int[] inventoryArray = getInventoryArray();
        for (int item : inventoryArray) {
            if (item == -1) {
                counter++;
            }
        }
        int withdrawlFactor = counter / (coalRatio + 1); //how many ores can we withdraw
        //log("Withdrawl Factor: " + withdrawlFactor);
        if (bank.getCount(oreID) < withdrawlFactor + 1) {
            log("Error: out of ores");
            stopScript();
            return false;
        }
        if (bank.getCount(coalID) < coalRatio * withdrawlFactor + 1) {
            log("Error: out of coal");
            stopScript();
            return false;
        }
        wait(250);
        bank.withdraw(oreID, withdrawlFactor);
        wait(random(750, 1250));
        int ore = getInventoryCount(oreID);
        if (ore > withdrawlFactor) {
            bank.deposit(oreID, ore - withdrawlFactor);
        }
        if (coalRatio > 0 && oreID == 440) {
            bank.withdraw(coalID, withdrawlFactor);//withdraw coal
            bank.withdraw(coalID, withdrawlFactor);
        } else if (coalRatio > 0) {
            bank.withdraw(coalID, withdrawlFactor * coalRatio);
        }
        wait(1500);
        int coal = getInventoryCount(coalID);
        ore = getInventoryCount(oreID);
        lagFactor = speedfactor;
        if (coal < withdrawlFactor * coalRatio) {
            log("Coal counted: " + coal);
            log("Withdrawl neccessary: " + withdrawlFactor * coalRatio);
            bank.withdraw(coalID, withdrawlFactor * coalRatio - coal);
            bank.close();
            return false;
        } else if (ore < withdrawlFactor) {
            log("Ores counted: " + ore);
            log("Withdrawl neccessary: " + withdrawlFactor);
            bank.withdraw(oreID, withdrawlFactor - ore);
            bank.close();
            return false;
        }
        return true;
    }

    public boolean withdrawBKIron()
    {
        if (!bank.isOpen()) {
            wait(1500);
            if (!bank.open()) {
                wait(1500);
                log("Error: can't open bank");
                return false;
            }
        }
        double speedfactor = lagFactor;
        lagFactor = 2.0;

        int withdrawlFactor = 9;
        //log("Withdrawl Factor: " + withdrawlFactor);
        if (bank.getCount(oreID) < withdrawlFactor + 1) {
            log("Error: out of ores");
            stopScript();
            return false;
        }
        if (bank.getCount(coalID) < coalRatio * withdrawlFactor + 1) {
            log("Error: out of coal");
            stopScript();
            return false;
        }
        wait(250);
        bank.withdraw(oreID, 10);
        wait(random(750, 1250));
        bank.deposit(oreID, 1);
        wait(random(750, 1250));
        int ore = getInventoryCount(oreID);
        if (ore > withdrawlFactor) {
            bank.deposit(oreID, ore - withdrawlFactor);
        }
        if (coalRatio > 0 && oreID == 440) {
            bank.withdraw(coalID, 0);//withdraw coal
            //bank.withdraw(coalID, withdrawlFactor);
        } else if (coalRatio > 0) {
            bank.withdraw(coalID, withdrawlFactor * coalRatio);
        }
        wait(1500);
        int coal = getInventoryCount(coalID);
        ore = getInventoryCount(oreID);
        lagFactor = speedfactor;
        if (coal < withdrawlFactor * coalRatio) {
            log("Coal counted: " + coal);
            log("Withdrawl neccessary: " + withdrawlFactor * coalRatio);
            bank.withdraw(coalID, withdrawlFactor * coalRatio - coal);
            bank.close();
            return false;
        } else if (ore < withdrawlFactor) {
            log("Ores counted: " + ore);
            log("Withdrawl neccessary: " + withdrawlFactor);
            bank.withdraw(oreID, withdrawlFactor - ore);
            bank.close();
            return false;
        }
        return true;
    }


    public boolean deposit() {
        if (!bank.isOpen()) {
            wait(1500);
            if (!bank.open()) {
                wait(1500);
                log("Error: can't open bank");
                return false;
            }
        }
        if (bank.depositAllExcept(561, 554)) { //deposit all but runes
            return true;
        } else {
            log("Error: depositing items problem");
            return false;
        }
    }

    public boolean checkOres() {
        int coal = getInventoryCount(coalID);
        int ore = getInventoryCount(oreID);
        if (ore == 0) {
            return false;
        } else if (coal / ore >= coalRatio) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int loop() {
        try {
            if (!isLoggedIn()) {
                Bot.disableRandoms = false;
                wait(1000);
            }
            if (recordInitial) {
                if (!initialized()) {
                    log("Error: bar identification failure");
                    return -1;
                } else {
                    recordInitial = false;
                }
            }
            if (distanceBetween(loc, getLocation()) > 10) {
                if (checkForRandoms()) {
                    loc = getLocation();
                }
            } else {
                Bot.disableRandoms = true;
            }
            antiBan();
            setCameraAltitude(true);
            if (!checkOres()) {
                bank.open();
                wait(2000);
                if ((new BankPins()).runRandom()) {
                    wait(1000);
                }
                if (errorCounter > 10) {
                    return -1;
                }
                if (!deposit()) {
                    Bot.disableRandoms = false;
                    return 1;
                }
                if (!withdrawBKIron()) {
                    Bot.disableRandoms = false;
                    return 1;
                }
            }
            bank.close();
            if (!superHeat()) {
                Bot.disableRandoms = false;
            }
            errorCounter = 0;
            return 500;
        } catch (NullPointerException e) {
            Bot.disableRandoms = false;
            log("Something really fucked up:" + e.getMessage());
            return 2000;
        }
    }

    @Override
    public boolean onStart(Map<String, String> map) {
        /** Reading html inputs */
        lagFactor = Double.parseDouble(map.get("lag"));
        log("Lag Factor: " + lagFactor);

        logOutDone = (map.get("logout") != null) ? true : false;
        log("Logout Debug: " + logOutDone);

        String ore = map.get("ore");
        if (ore.equalsIgnoreCase("runite")) {
            barID = 2363;
        } else if (ore.equalsIgnoreCase("adamantite")) {
            barID = 2361;
        } else if (ore.equalsIgnoreCase("mithril")) {
            barID = 2359;
        } else if (ore.equalsIgnoreCase("gold")) {
            barID = 2357;
        } else if (ore.equalsIgnoreCase("silver")) {
            barID = 2355;
        } else if (ore.equalsIgnoreCase("steel")) {
            barID = 2353;
        } else if (ore.equalsIgnoreCase("iron")) {
            barID = 2351;
        }
        log("Item ID: " + barID);
        if (barID <= 0) {
            return false;
        }
        recordInitial = true;
        Bot.disableRandoms = false;

        /** Sets the initial values for all the skill exp counters */
        startExpArry = new int[30];
        for (int i = 0; i < 20; i++) {
            startExpArry[i] = skills.getCurrentSkillExp(i);
        }

        return true;
    }

    @Override
    public void onFinish() {
        long timeDiff = (System.currentTimeMillis() - startTime) / 1000;
        int hours = (int) ((timeDiff) / 3600);
        int min = (int) ((timeDiff) / 60) - hours * 60;
        log("Script Ran for: " + hours + " hours " + min + " min.");
        log(status());
        if (logOutDone) {
            logout();
        }
    }

    public String status() {
        String s = "";
        s += "Exp Gained: " + (skills.getCurrentSkillExp(Constants.STAT_MAGIC) - startExp);
        s += " , Lvls Gained: " + (skills.getRealSkillLevel(Constants.STAT_MAGIC) - startLvl);
        return s;
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

    public void antiBan() {
        switch (random(0, 50)) {
            case 1:
            case 2: {
                //setCameraRotation(random(1, 270));
                break;
            }
            case 3:
            case 4:
            case 5:
            case 6: {
                openTab(TAB_STATS);
                //log("Opening stats page.");
                moveMouse(578, 405);
                wait(3500);
                break;
            }
            case 20: {
                //itemPos = getItemPos();
                //log("Item found at: " + itemPos.x + " , " + itemPos.y);
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected int getMouseSpeed() {
        return (int) (random(10, 12) * lagFactor);
    }

    @Override
    public void wait(int toSleep) {
        super.wait((int) (toSleep * lagFactor));
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
        } else if (messageEvent.contains("You need a smithing")) {
            log("Your Smithing level is too low for this bar - Shutting Down in 5-10 Seconds");
            wait(random(4500, 10500));
            stopScript();
        } else if (messageEvent.contains("Your Magic level") && skills.getCurrentSkillLevel(Constants.STAT_MAGIC) < 43) {
            log("Your Magic level is too low for this spell - Shutting Down in 5-10 Seconds");
            wait(random(4500, 10500));
            stopScript();
        }
    }

    // Paint code from FoulFighter, thx
    public void onRepaint(Graphics g) {
        // Font setting
        g.setFont(new Font("Century Gothic", Font.BOLD, 13));
        int x = 0;
        int y = 28;

        // Run time
        long millis = System.currentTimeMillis() - startTime;
        final long hours = millis / (1000 * 60 * 60);
        millis -= hours * 1000 * 60 * 60;
        final long minutes = millis / (1000 * 60);
        millis -= minutes * 1000 * 60;
        final long seconds = millis / 1000;
        paintBar(g, x, y, "SpeedSuperHeat Total Runtime: " + hours + ":"
                + minutes + ":" + seconds);

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
        g.setColor(new Color(70, 130, 180, 125));
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

        /*
        g.setColor(new Color(255, 0, 0, 90));
        g.fillRoundRect(416, y + 3, 100, 9, 10, 10);
        g.setColor(Color.GREEN);
        g.fillRoundRect(416, y + 3, getSetting(300) / 10, 9, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(380, y, 136, 15, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(416, y + 3, getSetting(300) / 10, 9, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(416, y + 3, 100, 9, 10, 10);
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(380, y, 136, 15, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString(Integer.toString(getSetting(300) / 10) + "%", 385, y + 13);*/
    }

    public void paintSkillBar(Graphics g, int x, int y, int skill, int start) {
        g.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        int gained = (skills.getCurrentSkillExp(skill) - start);
        String s = SkillToString(skill) + " Exp Gained: " + gained;
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
        g.drawString("Exp To Level: " + exp, 240, y + 13);
    }

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

    private String SkillToString(int skill) {
        return Skills.statsArray[skill];
    }
}
