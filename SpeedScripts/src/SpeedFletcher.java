/**
 * Educational code to simulate a human performing the fletching animations
 * Copyright (C) 2010 LightSpeed, Pirateblanc
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Educational code to simulate a human performing the superheat spell
 * Copyright (C) 2010 LightSpeed, Pirateblanc
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Map;
import org.rsbot.accessors.RSItem;
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
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = {"LightSpeed, Pirateblanc"}, category = "Fletching",
name = "SpeedFletcher", version = 1.0, description = "<html><head>"
+ "<style type=\"text/css\">"
+ "body {"
+ "   color: #FFFFFF;"
+ "   background-color: #000000;"
+ "}"
+ "</style>"
+ "</head>"
+ "<body>"
+ "<center>"
+ "<img src=\"http://www.wandocpc.site90.com/images/screenshots/fletcher.jpg\""
+ "alt=\"SpeedFletcher\">"
+ "</center>"
+ "<hr>"
+ "<center>Created by LightSpeed & Pirateblanc</center>"
+ "<hr><br>"
+ "<form>"
+ "Pick the type to fletch: "
+ "<select name=\"log\">"
+ "   <option>Yew<option>Magic<option>Maple<option>Willow<option>Oak"
+ "</select>"
+ "<br>"
+ "Pick the bow to fletch: "
+ "<select name=\"type\">"
+ "   <option>Longbow<option>Shortbow"
+ "</select>"
+ "<br>"
+ "String bows? <input type=\"checkbox\" name=\"string\" value=\"true\">"
+ "<br>"
+ "Logout On Crash/Finished Fletching? <input type=\"checkbox\" name=\"logout\" value=\"true\">"
+ "<br>"
+ "Lag Time For Banking & Mouse (sec): <select name=\"lag\">"
+ "   <option>1.0<option>2.0"
+ "</input>"
+ "</form><br>"
+ "<p>Fletcher Instructions: Have character near a bank " +
        "(good spot is soul wars due to no random zone) with the " +
        "bank tab set to the tab with logs, bowstring, " +
        "and uncut bows in the first 4 rows and visible. " +
        "To cut bows you must have a knife in the inventory. " +
        "If stringing is selected, it will begin when there is only 1 log left in the bank. " +
        "Clay knives can be used. " +
        "(Once the knife is used up it will try to find a normal " +
        "knife in the inventory. If none avaliable it will " +
        "switch to stringing or end).</p>"
+ "</p>"
+ "</body>"
+ "</html>")
public class SpeedFletcher extends Script implements PaintListener, ServerMessageListener {

    private boolean recordInitial = false;
    private int errors = 0;
    private RSTile loc;
    private int errorCounter = 0;
    private int strung = 0;
    private int bowsMade = 0;
    private int logsLeft = 0;
    private int unstrungLeft = 0;
    private int logID = 0;
    private long startTime;
    private boolean logOutDone = false;
    private int startExp;
    private int startLvl;
    private boolean fletch = true;
    private boolean string = false;
    private String woodType = "Maple";
    private String bowType = "Longbow";
    private int longbowID = 0;
    private int shortbowID = 0;
    private int bowID = 0;
    private final int sacredKnifeID = 14111;
    private final int knifeID = 946;
    private final int bowstringID = 1777;
    private int x = 0;
    private int y = 0;
    private int xpHour;
    private String version = "1.0";
    private int[] startExpArry;
    private int stringLeft = 0;
    RSInterface INTERFACE_FLETCH = RSInterface.getInterface(513);
    RSInterfaceChild FLETCH_AREA = RSInterface.getChildInterface(513, 3);
    private double lagFactor;

    @Override
    public int loop() {
        try {
            if (!isLoggedIn()) {
                Bot.disableRandoms = false;
                return 1000;
            }
            if (recordInitial) {
                if (!initialized()) {
                    log("Error: log identification failure");
                    return -1;
                } else {
                    recordInitial = false;
                }
            }
            if (distanceBetween(loc, getLocation()) > 10) {
                log("Location changed...checking...");
                if (checkForRandoms()) {
                    loc = getLocation();
                    return 1;
                }
            } else {
                Bot.disableRandoms = true;
            }
            antiBan();
            setCameraAltitude(true);
            if (!checkFletch()) {
                errors = 0;
                if (!fletch && !string) {
                    log("Fletching complete");
                    return -1;
                }
                if (isItemSelected()) {
                    wait(750);
                    moveMouse(656, 186, 5, 5);
                    wait(150);
                    clickMouse(true);
                }
                if (!bank.open()) {
                    while (!bank.isOpen()) {
                        wait(random(650, 850));
                        int menu = getMenuIndex("Knife");
                        if (menu != -1) {
                            moveMouse(535, 250, 5, 50);
                            clickMouse(true);
                            return 500;
                        }
                        (new BankPins()).runRandom();
                        errors++;
                        if (errors > 6) {
                            Bot.disableRandoms = false;
                            return 1;
                        }
                    }
                }
                if (errorCounter > 6) {
                    log("Too many errors...Shutting down");
                    return -1;
                }
                if (!deposit()) {
                    Bot.disableRandoms = false;
                    errorCounter++;
                    return 1;
                }
                if (!withdraw()) {
                    Bot.disableRandoms = false;
                    errorCounter++;
                    return 1;
                }
            }
            bank.close();
            if (!fletch()) {
                Bot.disableRandoms = false;
                errorCounter++;
                return 1;
            }
            errorCounter = 0;
            return 500;
        } catch (NullPointerException e) {
            Bot.disableRandoms = false;
            log("Something really fucked up:" + e.getMessage());
            errors++;
            if (errors > 3) {
                log("Sry not my fault blame the chinese guy.");
                return -1;
            }
            return 2000;
        }
    }

    @Override
    public boolean onStart(Map<String, String> map) {
        /** Reading html inputs */
        logOutDone = (map.get("logout") != null) ? true : false;
        log("Logout when done: " + logOutDone);
        string = (map.get("string") != null) ? true : false;
        log("String bows: " + string);
        woodType = map.get("log");
        log("Wood type: " + woodType);
        bowType = map.get("type");
        log("Bow type: " + bowType);
        lagFactor = Double.parseDouble(map.get("lag"));
        log("Lag Factor: " + lagFactor);

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
        ScreenshotUtil.takeScreenshot(true);
        if (logOutDone) {
            logout();
        }
    }

    public String status() {
        String s = "";
        s += "Exp Gained: " + (skills.getCurrentSkillExp(Constants.STAT_FLETCHING) - startExp);
        s += " , Lvls Gained: " + (skills.getRealSkillLevel(Constants.STAT_FLETCHING) - startLvl);
        return s;
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        String word = e.getMessage().toLowerCase();
        if (word.contains("cut")) {
            bowsMade++;
            if (logsLeft > 1) {
                logsLeft--;
            }
            unstrungLeft++;
        }
        if (word.contains("string")) {
            strung++;
            if (unstrungLeft > 1) {
                unstrungLeft--;
            }
        }

        if (word.contains("You've just advanced".toLowerCase())) {
            clickContinue();
        }
    }

    private boolean initialized() {
        startExp = skills.getCurrentSkillExp(Constants.STAT_FLETCHING); //save the initial exp and lvl
        startLvl = skills.getRealSkillLevel(Constants.STAT_FLETCHING);
        loc = getLocation();
        startTime = System.currentTimeMillis();
        startExpArry = new int[30];
        for (int i = 0; i < 20; i++) {
            startExpArry[i] = skills.getCurrentSkillExp(i);
        }
        return fletchIntialize();
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

    private void antiBan() {
        switch (random(0, 500)) {
            case 6: {
                openTab(TAB_STATS);
                moveMouse(633, 406, 5, 5);
                wait(random(750, 1000));
                break;
            }
            default: {
                break;
            }
        }
    }

    private boolean checkFletch() {
        return (inventoryContains(logID)
                && inventoryContainsOneOf(knifeID, sacredKnifeID))
                || (inventoryContains(bowID)
                && inventoryContains(bowstringID));
    }

    private boolean fletch() {
        if (!checkFletch()) {
            return false;
        }
        while (animationIs(1248, 7211, 6685, 6686, 6687, 6688, 6689)) {
            wait(750);
            return true;
        }
        if (fletch && logsLeft > 1 && inventoryContainsOneOf(knifeID, sacredKnifeID)) {
            Point itemPos = getInventoryLocation(logID);
            Point knifePos = getInventoryLocation(sacredKnifeID);
            if (knifePos.equals(new Point(-1, -1))) {
                knifePos = getInventoryLocation(knifeID);
            }
            if (itemPos.equals(new Point(-1, -1)) || knifePos.equals(new Point(-1, -1))) {
                return false;
            }
            knifePos.translate(15, 15);
            itemPos.translate(15, 15);
            moveMouse(itemPos, 5, 5);
            wait(random(500, 750));
            atMenu("Use");
            moveMouse(knifePos, 5, 5);
            wait(random(500, 750));
            atMenu("nife");
            errors = 0;
            moveMouse(x, y, 25, 25);
            wait(random(1500, 2000));
            while (INTERFACE_FLETCH.isValid()) {
                if (errors > 0) {
                    moveMouse(258, 354, 25, 25);
                    moveMouse(x, y, 25, 25);
                    wait(random(1500, 2000));
                    if (isItemSelected()) {
                        wait(750);
                        moveMouse(656, 186, 5, 5);
                        wait(150);
                        clickMouse(true);
                    }
                }
                if (errors > 5) {
                    return false;
                }
                atMenu("Make X");
                wait(random(1300, 1800));
                sendText("" + (random(3, 9) * 11), true);
                wait(random(1300, 1800));
                errors++;
            }
            while (animationIs(1248, 7211)) {
                antiBan();
                wait(random(500, 750));
            }
        } else if (string && unstrungLeft > 1) {
            Point bow = getInventoryLocation(bowID);
            Point string = getInventoryLocation(bowstringID);
            fletch = false;
            if (bow.equals(new Point(-1, -1)) || string.equals(new Point(-1, -1))) {
                return false;
            }
            moveMouse(bow.x + 15, bow.y + 15, 5, 5);
            wait(random(500, 750));
            atMenu("(u)");
            moveMouse(string.x + 15, string.y + 15, 5, 5);
            wait(random(500, 750));
            atMenu("string");
            wait(random(1300, 1800));
            errors = 0;
            while (FLETCH_AREA.isValid()) {
                if (errors > 0) {
                    moveMouse(258, 354, 25, 25);
                    moveMouse(x, y, 25, 25);
                    wait(random(1500, 2000));
                    if (isItemSelected()) {
                        wait(750);
                        moveMouse(656, 186, 5, 5);
                        wait(150);
                        clickMouse(true);
                    }
                }
                if (errors > 5) {
                    return false;
                }
                atInterface(FLETCH_AREA, "Make All");
                wait(random(1300, 1800));
                errors++;
            }
            int before = unstrungLeft;
            int after = 0;
            while (animationIs(6685, 6686, 6687, 6688, 6689) || before > after || checkStringing()) {
                antiBan();
                wait(random(75, 100));
                after = unstrungLeft;
                if (after != 0) {
                    before = after;
                }
            }
        } else {
            fletch = false;
            string = false;
            return false;
        }
        return true;
    }

    private Point getInventoryLocation(int ID) {
        int[] inventoryArray = getInventoryArray();
        int startItem = -1;
        for (int i = 0; i < 28; i++) {
            if (inventoryArray[i] == ID) {
                startItem = i;
                break;
            }
        }
        return getInventoryItemPoint(startItem);
    }

    private boolean deposit() {
        wait(random(500, 750));
        if (!bank.isOpen()) {
            wait(1500);
            if (!bank.open()) {
                wait(1500);
                return false;
            }
        }
        if (fletch) {
            return  bank.depositAllExcept(knifeID, sacredKnifeID);
        } else if (string) {
            return bank.depositAll();
        } else {
            return false;
        }
    }

    private boolean withdraw() {
        wait(random(500, 750));
        if (!bank.isOpen()) {
            wait(1500);
            if (!bank.open()) {
                wait(500);
                return false;
            }
        }
        logsLeft = bank.getCount(logID);
        unstrungLeft = bank.getCount(bowID);
        stringLeft = bank.getCount(bowstringID);
        wait(random(750, 1250));
        if (fletch && inventoryContainsOneOf(knifeID, sacredKnifeID)) {
            logsLeft = bank.getCount(logID);
            if (logsLeft > 28) {
                int withdrawlFactor = 10;
                int errCount = 0;
                withdraw(logID, 0);
                wait(random(1000, 1500));
                int logs = getInventoryCount(logID);
                while (logs < 1 && bank.isOpen()) {
                    if (logs < withdrawlFactor) {
                        withdraw(logID, 0);
                    }
                    wait(random(1000, 1500));
                    logs = getInventoryCount(logID);
                    errCount++;
                    if (errCount > 3 || isPaused) {
                        return false;
                    }
                }
                return true;
            } else if (logsLeft <= 28 && logsLeft > 1) {
                withdraw(logID, logsLeft - 1);
                wait(random(1000, 1500));
                return true;
            } else {
                log("Out of logs.");
                fletch = false;
                return false;
            }
        } else if (string && inventoryEmptyExcept()) {
            int withdrawlFactor = 14;
            int errCount = 0;
            unstrungLeft = bank.getCount(bowID);
            stringLeft = bank.getCount(bowstringID);
            if (unstrungLeft < withdrawlFactor) {
                withdrawlFactor = unstrungLeft - 1;
            }
            if (stringLeft < withdrawlFactor) {
                withdrawlFactor = stringLeft - 1;
            }
            if (unstrungLeft <= 1) {
                log("Out of bows to string.");
                string = false;
                return false;
            }
            if (stringLeft <= 1) {
                log("Out of string.");
                string = false;
                return false;
            }
            int bow = getInventoryCount(bowID);
            while (bow != withdrawlFactor && bank.isOpen()) {
                if (bow > withdrawlFactor) {
                    bank.deposit(bowID, bow - withdrawlFactor);
                } else if (bow < withdrawlFactor) {
                    withdraw(bowID, withdrawlFactor - bow);
                }
                wait(random(1000, 1500));
                bow = getInventoryCount(bowID);
                errCount++;
                if (errCount > 3 || isPaused) {
                    return false;
                }
            }
            int bowstring = getInventoryCount(bowstringID);
            while (bowstring != withdrawlFactor && bank.isOpen()) {
                if (bowstring > withdrawlFactor) {
                    bank.deposit(bowstringID, bowstring - withdrawlFactor);
                } else if (bowstring < withdrawlFactor) {
                    withdraw(bowstringID, withdrawlFactor - bowstring);
                }
                wait(random(1000, 1500));
                bowstring = getInventoryCount(bowstringID);
                errCount++;
                if (errCount > 3 || isPaused) {
                    return false;
                }
            }
            return true;
        } else if (!fletch && !string) {
            log("Finished tasks.");
            stopScript();
            return false;
        } else {
            return false;
        }
    }

    /**
     * Tries to withdraw an item.
     *
     * 0 is All. 1,5,10 use Withdraw 1,5,10 while other numbers Withdraw X.
     *
     * @param itemID
     *            The ID of the item.
     * @param count
     *            The number to withdraw.
     * @return <tt>true</tt> on success.
     */
    public boolean withdraw(final int itemID, final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count < 0 (" + count + ")");
        }
        if (!bank.isOpen()) {
            return false;
        }
        final RSInterfaceComponent item = bank.getItemByID(itemID);
        if ((item == null) || !item.isValid()) {
            return false;
        }
        final int inventoryCount = getInventoryCount(true);
        switch (count) {
            case 0: // Withdraw All
                atInterface(item, "Withdraw-All");
                break;
            case 1: // Withdraw 1
            case 5: // Withdraw 5
            case 10: // Withdraw 10
                atInterface(item, "Withdraw-" + count);
                break;
            default: // Withdraw x
                if (atInterface(item, false)) {
                    wait(random(600, 900));
                    java.util.ArrayList<String> mactions = getMenuActions();
                    boolean found = false;
                    for (int i = 0; i < mactions.size(); i++) {
                        if (mactions.get(i).equalsIgnoreCase("Withdraw-" + count)) {
                            found = true;
                            atMenu("Withdraw-" + count);
                            break;
                        }
                    }
                    if (!found && atInterface(item, "Withdraw-X")) {
                        wait(random(1000, 1300));
                        Bot.getInputManager().sendKeys("" + count, true);
                    }
                }
                break;
        }
        if ((getInventoryCount(true) > inventoryCount) || (getInventoryCount(true) == 28)) {
            return true;
        }
        return false;
    }

    private boolean fletchIntialize() {
        while (true) {
            if (errors > 10) {
                return false;
            } else if (fletch && inventoryContainsOneOf(sacredKnifeID, knifeID)) {
                break;
            } else if (string) {
                fletch = false;
                break;
            } else {
                errors++;
            }
            wait(random(500, 750));
        }
        errors = 0;
        if (woodType.equals("Wood")) {
            logID = 1511;
            longbowID = 48;
            shortbowID = 50;
        } else if (woodType.equals("Oak")) {
            logID = 1521;
            longbowID = 56;
            shortbowID = 54;
        } else if (woodType.equals("Willow")) {
            logID = 1519;
            longbowID = 58;
            shortbowID = 60;
        } else if (woodType.equals("Maple")) {
            logID = 1517;
            longbowID = 62;
            shortbowID = 64;
        } else if (woodType.equals("Yew")) {
            logID = 1515;
            longbowID = 66;
            shortbowID = 68;
        } else if (woodType.equals("Magic")) {
            logID = 1513;
            longbowID = 70;
            shortbowID = 72;
        } else {
            return false;
        }
        if (bowType.equals("Shortbow")) {
            x = 107;
            y = 410;
            if (woodType.equals("Magic")) {
                x = 130;
                y = 414;
            }
            bowID = shortbowID;
        } else if (bowType.equals("Longbow")) {
            x = 248;
            y = 406;
            if (woodType.equals("Magic")) {
                x = 400;
                y = 414;
            }
            bowID = longbowID;
        } else {
            return false;
        }
        return true;
    }

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
        paintBar(g, x, y, "SpeedFletcher Total Runtime: " + hours + " - "
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
        paintFletch(g, x, y + 15);
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
        xpHour = (int) (gained * 3600000.0
                / ((double) System.currentTimeMillis() - (double) startTime));
        //refreshCounter = 0;
        //}
        g.drawString("/hr: " + Integer.toString(Math.round(xpHour)), 335, y + 13);
    }

    private void paintFletch(Graphics g, int x, int y) {
        if (recordInitial) {
            return;
        }
        g.setFont(new Font("Century Gothic", Font.PLAIN, 13));

        if (bowsMade + logsLeft == 0) {
            logsLeft = (2 < getInventoryCountExcept(logID)) ? getInventoryCount(logID) : 2;
        }
        int percent = Math.round(((float) bowsMade * 100) / (bowsMade + logsLeft));

        g.setColor(new Color(255, 0, 0, 90));
        g.fillRoundRect(416, y + 3, 100, 9, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(416, y + 3, 100, 9, 10, 10);
        g.setColor(new Color(0, 255, 0, 255));
        g.fillRoundRect(416, y + 3, percent, 9,
                10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(416, y + 3, percent, 9,
                10, 10);

        String s = "Bows cut: " + bowsMade;
        g.setColor(new Color(0, 200, 255));
        paintBar(g, x, y, s);
        g.drawString("Logs left: " + logsLeft, 200, y + 13);
        g.drawString(percent + "%", 458, y + 13);
        xpHour = (int) (bowsMade * 3600000.0
                / ((double) System.currentTimeMillis() - (double) startTime));
        g.drawString("/hr: " + Integer.toString(Math.round(xpHour)), 335, y + 13);
        y += 15;

        if (strung + unstrungLeft == 0) {
            unstrungLeft = (2 < getInventoryCount(bowID)) ? getInventoryCount(bowID) : 2;
        }
        percent = Math.round(((float) strung * 100) / (strung + unstrungLeft));
        g.setFont(new Font("Century Gothic", Font.PLAIN, 13));

        g.setColor(new Color(255, 0, 0, 90));
        g.fillRoundRect(416, y + 3, 100, 9, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(416, y + 3, 100, 9, 10, 10);
        g.setColor(new Color(0, 255, 0, 255));
        g.fillRoundRect(416, y + 3, percent, 9,
                10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(416, y + 3, percent, 9,
                10, 10);


        String s2 = "Bows strung: " + strung;
        g.setColor(new Color(0, 200, 255));
        paintBar(g, x, y, s2);
        g.drawString(percent + "%", 458, y + 13);
        g.drawString("Unstrung left: " + unstrungLeft, 200, y + 13);

        xpHour = (int) (strung * 3600000.0
                / ((double) System.currentTimeMillis() - (double) startTime));

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

    /**
     *
     * @return
     */
    @Override
    protected int getMouseSpeed() {
        return (int) (random(8, 10) * Math.pow(lagFactor, .30));
    }

    /**
     *
     * @param toSleep
     */
    @Override
    public void wait(int toSleep) {
        super.wait((int) (toSleep * Math.pow(lagFactor, .5)));
    }

    private boolean checkStringing()
    {
        int locErrors = 0;
        while(locErrors < 8)
        {
            if(animationIs(6685, 6686, 6687, 6688, 6689))
                return true;
            locErrors++;
            wait(random(50,150));
        }
        return false;
    }

}
