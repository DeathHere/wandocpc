package SampleScripts;


//CHANGELOG:
// V1.0 - First stable release
// v1.1 - Fixed a few general issues, removed some non-working features and implemented my waitUntil method.
// v1.11 - (Hopefully) fixed the levelup problem, updated the paint with */hour variables and a few minor tweaks
//
// CURRENT VERSION IS 1.11, SCRIPT BY MISTERSNAPPY, BASE BY VIVALARAZA
//
//       ____  ________________________________________________      ____________________________________________________________
//      /   / /   /         /   / /   /   __    /         /   /     /         /         /         /   / /   /         /         /
//     /   / /   /___   ___/   / /   /   /_/   /   ______/   /     /   ______/___   ___/   ______/   /_/   /   ______/   ___   /
//    /   / /   /   /  /  /   / /   /         /       / /   /     /       /     /  /  /   /     /         /       / /   ______/
//   /   /_/   /___/  /__/   /_/   /   __    /   ____/ /   /_____/   ____/_    /  /  /   /_____/   __    /   ____/_/   __    /
//  /         /         /         /   / /   /   /     /         /         /   /  /  /         /   / /   /         /   / /   /
// /_________/_________/_________/___/ /___/___/     /_________/_________/   /__/  /_________/___/ /___/_________/___/ /___/
//
//
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.script.*;
import org.rsbot.bot.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.event.listeners.*;
import org.rsbot.event.events.*;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = {"Mistersnappy, Vivalaraza"}, category = "Fletching", name = "Viva Series Fletcher", version = 1.11, description = "<html><head>"
+ "<head></head>\n"
+ "<body>\n"
+ "<table><tr><td bgcolor=\"000000\"><center>"
+ "<h1><font color=\"ff00ff\">VivaFletcher</font></h1>"
+ "<font size=\"4\"><font color=\"ff00ff\">By Mistersnappy, base by Vivalaraza.</font></font><br>"
+ "</td></tr><tr><td bgcolor=\"ff00ff\"><b>Bow Stringing/Fletching:</b></td></tr><tr><td>"
+ "<select name=\"log\">"
+ "<option>Wood<option>Oak<option>Willow<option>Maple<option>Yew<option>Magic</select>"
+ "<select name=\"type\">"
+ "<option>Bow<option>Shortbow<option>Longbow</select>"
+ "<select name=\"stringing5\">"
+ "<option>String?<option>Yes<option>No</select>"
+ "<input type=\"checkbox\" name=\"sacredclay\" value=\"true\">Sacred Clay Knife?<br>"
+ "<center>BankID: <input type=\"text\" name=\"bank\" value=\"42192\">"
+ "<select name=\"banktype\">"
+ "<option>Chest<option>Booth</select><br>"
+ "<center>Fletch how many?: <input type=\"text\" name=\"left\" value=\"0\"><br>"
+ "<center>String how many?: <input type=\"text\" name=\"right\" value=\"0\"><br>"
+ "<font size=\"3\">^^ Input 0 to just string</font>"
+ "<center>Lag Addon: <input type=\"text\" name=\"lag\" value=\"0\"><br>"
+ "<font size=\"3\">^^ Add time if your a lagger (1000 = 1 second).</font>"
+ "</td></tr><tr><td bgcolor=\"ff00ff\"><b>Bolt Making(BETA):</b></td></tr><tr><td>"
+ "Would you like to make bolts?"
+ "<select name=\"bolts\">"
+ "<option>No<option>Yes</select><br>"
+ "<font size=\"3\">If you have ANY lag, the script WILL mess up.</font>"
+ "</td></tr><tr><td bgcolor=\"ff00ff\"><b>Instructions:</b></td></tr><tr><td>"
+ "<b>Fletching:</b> Have the logs in the first slot and your knife in inventory.<br>"
+ "<b>Fletching/Stringing:</b> Have the logs in the first slot, unfinished bows in the second, bowstrings in the third and your knife in inventory.<br>"
+ "<b>Stringing:</b> Have the unfinished bows in the second bank slot and bowstrings in the third.<br>"
+ "<b>Making Bolts:</b> Start anywhere with bolts in your first slot, feathers in your second and some made bolts in your third."
+ "<font size=\"4\">I apologize for having to use the clickMouse for banking, but RSBot's withdrawing methods are fucked."
+ "</td></tr></table>"
+ "</body>\n"
+ "</html>\n")
public class VivaFletcher extends Script implements ServerMessageListener, PaintListener {

    public int[] keep = new int[]{946, 14111};
    public int longbowID;
    public int shortbowID;
    public int x;
    public boolean sacred = false;
    public int y;
    public int r = 111 + (random(100, 500));
    public int z = 124 + (random(200, 600));
    public int antiBan = 0;
    public String woodtype = "";
    public String bolts = "";
    public String bowtype = "";
    public String stringin = "";
    public String banktype5 = "";
    public String status = "Starting Up...";
    public int bankType;
    public int booth;
    public int bolts5;
    public int count;
    public int chest;
    public int pertolvl;
    public int pertolvl2;
    public int perleft;
    public int fletchtotal;
    public int stringtotal;
    public int bankid;
    public int stringing;
    public int bowID;
    public int lagdelay;
    public int logID;
    public int bowsmade = 0;
    public int strung = 0;
    public int logsleft;
    public int startlvl = 0;
    public int startxp = 0;
    public int bowstring = 1777;
    public int tostring;
    public long startTime = System.currentTimeMillis();

    public String getName() {
        return "Fletcher";
    }

    public double getVersion() {
        return 1.11;
    }

    public String getAuthor() {
        return "VivaLaRaza";
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        String word = e.getMessage().toLowerCase();
        if (word.contains("cut")) {
            bowsmade++;
            logsleft--;
        }
        if (word.contains("string")) {
            strung++;
            tostring--;
        }
        if (word.contains("You've just advanced")) {
            ScreenshotUtil.takeScreenshot(isLoggedIn());
            clickContinue();
            handleBank();
        }
    }

    public boolean onStart(Map<String, String> args) {
        //########## ENTER QC SO NUMBERS ARE NOT TALKED ##########//
        sendText("", true);
        woodtype = args.get("log");
        stringin = args.get("stringing5");
        banktype5 = args.get("banktype");
        bolts = args.get("bolts");
        bowtype = args.get("type");
        if (args.get("sacredclay") != null) {
            log("Using Sacred Clay Knife");
            sacred = true;
        } else {
            log("Using Regular Knife");
            sacred = false;

        }
        if (woodtype.equals("Wood")) {
            logID = 1511;
            longbowID = 48;
            shortbowID = 50;
        } else if (woodtype.equals("Oak")) {
            logID = 1521;
            longbowID = 56;
            shortbowID = 54;
        } else if (woodtype.equals("Willow")) {
            logID = 1519;
            longbowID = 58;
            shortbowID = 60;
        } else if (woodtype.equals("Maple")) {
            logID = 1517;
            longbowID = 62;
            shortbowID = 64;
        } else if (woodtype.equals("Yew")) {
            logID = 1515;
            longbowID = 66;
            shortbowID = 68;
        } else if (woodtype.equals("Magic")) {
            logID = 1515;
            longbowID = 66;
            shortbowID = 68;
        }
        if (bowtype.equals("Shortbow")) {
            x = 107;
            y = 410;
            bowID = shortbowID;
        } else if (bowtype.equals("Longbow")) {
            x = 248;
            y = 406;
            bowID = longbowID;
        }
        if (stringin.equals("Yes")) {
            stringing = 1;
        }
        if (stringin.equals("No")) {
            stringing = 0;
        }
        if (banktype5.equals("Chest")) {
            bankType = chest;
        }
        if (banktype5.equals("Booth")) {
            bankType = booth;
        }
        if (bolts.equals("No")) {
            bolts5 = 0;
        }
        if (bolts.equals("Yes")) {
            bolts5 = 1;
        }
        bankid = Integer.parseInt(args.get("bank"));
        lagdelay = Integer.parseInt(args.get("lag"));
        logsleft = Integer.parseInt(args.get("left"));
        tostring = Integer.parseInt(args.get("right"));
        startlvl = skills.getCurrentSkillLevel(9);
        startTime = System.currentTimeMillis();
        startxp = skills.getCurrentSkillExp(9);
        Bot.getEventManager().addListener(PaintListener.class, this);
        return true;
    }

    public void checkFletch() {
        openTab(TAB_STATS);
        moveMouse(633, 406);
    }

    public void onRepaint(Graphics g) {
        if (isLoggedIn()) {
            long millis = System.currentTimeMillis() - startTime;
            long hours = millis / (1000 * 60 * 60);
            millis -= hours * (1000 * 60 * 60);
            long minutes = millis / (1000 * 60);
            millis -= minutes * (1000 * 60);
            long seconds = millis / 1000;
            int LevelChange = skills.getCurrentSkillLevel(STAT_FLETCHING) - startlvl;
            int XPChange = skills.getCurrentSkillExp(STAT_FLETCHING) - startxp;
            int pertolvl = skills.getPercentToNextLevel(STAT_FLETCHING) * 2;
            int pertolvl2 = skills.getPercentToNextLevel(STAT_FLETCHING);
            int perleft = 200 - pertolvl;
            fletchtotal = bowsmade + logsleft;
            stringtotal = strung + tostring;
            //########## MOUSE FOLLOWER ##########//
            g.setColor(Color.red);
            g.fill3DRect(getMouseLocation().x - 3, getMouseLocation().y - 3, 7, 7, true);
            g.setColor(new Color(0, 0, 0, 125));
            g.drawLine(getMouseLocation().x, 0, getMouseLocation().x, 500);
            g.drawLine(0, getMouseLocation().y, 762, getMouseLocation().y);
            //####################################//
            g.setColor(new Color(0, 100, 0, 150));
            g.fill3DRect(528, 260, 16, 200, true);
            g.setColor(new Color(100, 0, 0, 150));
            g.fill3DRect(528, 260, 16, perleft, true);
            g.setColor(Color.white);
            g.drawString(+pertolvl2 + "%", 529, 451);
            if (getCurrentTab() == TAB_INVENTORY) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fill3DRect(552, 210, 735 - 552, 461 - 210, true);//main rectangle
                g.setColor(new Color(255, 20, 147));
                g.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
                g.drawString("VivaFletcher", 555, 230);
                g.setColor(Color.white);
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.drawString("Runtime: " + hours + "h " + minutes
                        + "m " + seconds + "s.", 555, 250);
                g.drawString("Levels Gained: " + LevelChange, 555, 265);
                g.drawString("EXP Gained: " + XPChange, 555, 280);
                g.drawString("EXP/Hour: " + (int) ((skills.getCurrentSkillExp(Constants.STAT_FLETCHING) - startxp) * 3600000D / ((double) System.currentTimeMillis() - (double) startTime)), 555, 295);
                if (logsleft >= 1) {
                    g.drawString("Fletched: " + bowsmade + "/" + logsleft, 555, 310);
                    g.drawString("Bows/Hour: " + (int) (bowsmade * 3600000D / ((double) System.currentTimeMillis() - (double) startTime)), 555, 325);
                }
                if (logsleft == 0 && stringing == 1) {
                    g.drawString("Strung: " + strung + "/" + tostring, 555, 310);
                    g.drawString("Bows/Hour: " + (int) (strung * 3600000D / ((double) System.currentTimeMillis() - (double) startTime)), 555, 325);
                }
                if (logsleft == 0 && stringing == 1 && bowsmade >= 1) {
                    g.drawString("Fletched: " + bowsmade + "/" + logsleft, 555, 310);
                    g.drawString("Strung: " + strung + "/" + tostring, 555, 325);
                    g.drawString("Bows/Hour: " + (int) (strung * 3600000D / ((double) System.currentTimeMillis() - (double) startTime)), 555, 340);
                }
                g.setColor(Color.red);
                g.drawString("Status: " + status, 555, 455);
            }
        }
    }

    public void onFinish() {
        Bot.getEventManager().removeListener(PaintListener.class, this);
        sacred = false;
        log("Thank you for using VivaFletch by Mistersnappy, base by Vivalaraza...");
        log("PROGRESS REPORT:");
        log("You fletched " + bowsmade + " bows and strung " + strung + " bows.");
        log("Taking screenshot...");
        ScreenshotUtil.takeScreenshot(true);
        log("A screenshot has been saved to your RSBot/screenshots folder, be sure to post it as a proggie:)");
    }
//################################################################################################//
//########## SPEED'S METHODS #####################################################################//
//################################################################################################//

    boolean withdrawal(final int item, final int amount) {
        if (bank.isOpen()) {
            if (!isThere(item)) {
                bank.withdraw(item, amount);
                wait(610);
                if (getInventoryCount(item) == 0) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    boolean isThere(final int item) {
        if (getInventoryCount(item) == 0) {
            return false;
        }
        if (getInventoryCount(item) != 0) {
            return true;
        }
        return false;
    }

    boolean waitUntil(final int item, final int amount) {
        if (getInventoryCount(item) > amount || getInventoryCount(item) < amount) {
            wait(random(200, 300));
            waitUntil(item, amount);
        }
        if (getInventoryCount(item) == amount) {
            return true;
        }
        return false;
    }

//################################################################################################//
    public boolean handleBank() { //checks which banking type to use and uses it.
        if (logsleft == 0 && stringing == 1) {
            bank2();
        }
        if (logsleft >= 1) {
            bank();
        }
        return true;
    }

    public void bank() {
        try {
            RSObject bank = findObject(bankid);
            if (bankType == chest) {
                atObject(bank, "Use");
            }
            if (bankType == booth) {
                atObject(bank, "Use-quickly");
            }
            wait(1500);
        } catch (final Exception e) {
            log("Small problem encountered, meltdown averted =P");
        }
        if (RSInterface.getInterface(762).isValid()) {
            clickInventoryItem(longbowID, false);
            atMenu("Deposit-All");
            clickInventoryItem(shortbowID, false);
            atMenu("Deposit-All");
            wait(1500);
            clickMouse(52, 106, false);
            atMenu("All");
            wait(1000);
            clickMouse(490, 37, true);
            wait(lagdelay);
        } else {
            bank();
        }
    }

    public void bank2() {
        try {
            RSObject bank = findObject(bankid);
            if (bankType == chest) {
                atObject(bank, "Use");
            }
            if (bankType == booth) {
                atObject(bank, "Use-quickly");
            }
            wait(1500);
        } catch (final Exception e) {
            log("Small problem encountered, meltdown averted =P");
        }
        if (RSInterface.getInterface(762).isValid()) {
            clickMouse(401 + random(-3, 3), 306 + random(-4, 4), true);
            wait(1200);
            clickMouse(100 + random(-3, 3), 106 + random(-4, 4), false);
            atMenu("Withdraw-14");
            wait(500);
            clickMouse(140 + random(-3, 3), 106 + random(-4, 4), false);
            atMenu("Withdraw-14");
            wait(1001);
            clickMouse(490, 37, true);
            wait(lagdelay);
        } else {
            bank2();
        }
    }

    public boolean stringBows() {
        clickInventoryItem(bowID, true);
        wait(random(20 + lagdelay, 30 + lagdelay));
        clickInventoryItem(bowstring, true);
        wait(random(20 + lagdelay, 30 + lagdelay));

        wait(random(120 + lagdelay, 200 + lagdelay));
        clickMouse(261 + random(-20, 20), 420 + random(-18, 19), false);
        atMenu("Make All");
        wait(random(1000 + lagdelay, 3000 + lagdelay));
        while (getMyPlayer().getAnimation() == 6688) {
            checkFletch();
            wait(random(500, 800));
        }
        waitUntil(bowstring, 0);
        return true;
    }

    public void switchToString() {
        try {
            RSObject bank = findObject(bankid);
            if (bankType == chest) {
                atObject(bank, "Use");
            }
            if (bankType == booth) {
                atObject(bank, "Use-quickly");
            }
            wait(1500);
        } catch (final Exception e) {
            log("Small problem encountered, meltdown averted =P");
        }
        if (RSInterface.getInterface(762).isValid()) {
            clickMouse(402, 309, true);
            clickMouse(402, 309, true);
            clickMouse(402, 309, true);
            log("Fletched " + bowsmade + " Unstrungs, and gained " + (skills.getCurrentSkillExp(9) - startxp) + "XP.");
            log("Switching to stringing mode...");
            clickMouse(490, 37, true);
            wait(1000);
            bank2();
        } else {
            switchToString();
        }
    }

    public boolean isDoingSomething() {
        if (getMyPlayer().getAnimation() != -1) {
            return true;
        }
        if (getMyPlayer().isMoving()) {
            return true;
        }
        return false;
    }

    public int loop() {
//################################################################################################//
//########## CHECKPOINTS #########################################################################//
//################################################################################################//
        if (getCurrentTab() == TAB_INVENTORY) {
            clickMouse(372, 434, true);
        }
        if (logsleft == 0 && stringing == 1 && inventoryContains(946)) {
            switchToString();
        }
        if (logsleft == 0 && stringing == 0) {
            log("Finished what was asked for, treminating script...");
            stopScript();//stops script after user defined number has been reached//
        }
        if (logsleft == 0 && tostring == 0) {
            log("Finished what was asked for, treminating script...");
            stopScript();//stops script after user defined number has been reached//
        }
//################################################################################################//
//########## MAKING BOLTS ########################################################################//
//################################################################################################//
        if (bolts5 == 1) {
            count = getInventoryCount(); // finds out how many items are in your inventory
            if (count == 3) {
                wait(random(20, 30));
                clickMouse(578, 228, 3, 3, true);
                wait(random(20, 30));
                clickMouse(620, 230, 4, 4, true);
                wait(random(20, 30));
                clickMouse(620, 230, 4, 4, true);
                wait(random(20, 30));
                clickMouse(578, 228, 3, 3, true);
                return 15;
            } else {
                log("No bolts(unf) left");
                wait(random(500, 1000));
                stopScript();
            }
        }
//################################################################################################//
//########## STRINGING ###########################################################################//
//################################################################################################//
        if (logsleft == 0 && stringing == 1) {
            status = "Stringing Bows";
            if (getMyPlayer().getAnimation() == 6688) {
                status = "Stringing Bows";
                return 700;
            }
            if (getInventoryCount(946) >= 1) {
                switchToString();
            }
            if (getInventoryCount(bowID) >= 1 && getInventoryCount(bowstring) >= 1 && getInventoryCount(bowID) <= 13 && getInventoryCount(bowstring) <= 13 && getMyPlayer().getAnimation() == 6688) {
                wait(random(100, 200));
            }
            while (getMyPlayer().getAnimation() == 6688 || getInventoryCount(bowID) <= 13 && getInventoryCount(bowID) >= 1) {
                wait(1000);
            }
            if (getInventoryCount(bowID) == 14 && getInventoryCount(bowstring) == 14) {
                stringBows();
            }
            if (getInventoryCount(bowID) < 14 && getInventoryCount(bowID) > 1 && getInventoryCount(bowstring) < 14 && getInventoryCount(bowstring) > 14) {
                log("Bad amount of bows or strings left, out of supplies?? TERMINATING!");
                stopScript();
            }
            if (getInventoryCount(bowID) == 14 && getInventoryCount(bowstring) == 0) {
                bank2();
            }
            if (getInventoryCount(bowID) == 0 && getInventoryCount(bowstring) == 14) {
                bank2();
            }
            if (getInventoryCount(bowID) == 0 && getInventoryCount(bowstring) == 0) {
                bank2();
            }
        }
//################################################################################################//
//########## FLETCHING ###########################################################################//
//################################################################################################//
        if (logsleft >= 1) {
            status = "Fletching Bows";
            if (sacred) {
                if (getInventoryCount(logID) >= 1 && getInventoryCount(bowID) >= 1 && getInventoryCount(logID) <= 26 && getInventoryCount(bowID) < 27 && getMyPlayer().getAnimation() == 1248) {
                    wait(random(100, 200));
                }
                while (getMyPlayer().getAnimation() == 1248 || getInventoryCount(bowID) <= 26 && getInventoryCount(bowID) >= 1) {
                    wait(1000);
                }
                if (getInventoryCount(logID) == 0 && getInventoryCount(bowID) == 27) {
                    bank();
                }
                if (!inventoryContains(logID)) {
                    bank();
                }
                atInventoryItem(14111, "Use");
                wait(random(300, 1000));
                clickInventoryItem(logID, true);
                wait(2000);
                clickMouse(x + random(-5, 5), y + random(-5, 5), false);
                atMenu("X");
                wait(random(1300 + lagdelay, 1800 + lagdelay));
                int numba = random(29, 100);
                sendText("" + numba, true);
                wait(random(75000, 84000));
            } else {
                if (getInventoryCount(logID) >= 1 && getInventoryCount(bowID) >= 1 && getInventoryCount(logID) <= 26 && getInventoryCount(bowID) < 27 && getMyPlayer().getAnimation() == 1248) {
                    wait(random(100, 200));
                }
                while (getMyPlayer().getAnimation() == 1248 || getInventoryCount(bowID) <= 26 && getInventoryCount(bowID) >= 1) {
                    wait(1000);
                }
                if (getInventoryCount(logID) == 0 && getInventoryCount(bowID) == 27) {
                    bank();
                }
                if (!inventoryContains(logID)) {
                    bank();
                }
                clickInventoryItem(946, true);
                wait(random(300, 1000));
                clickInventoryItem(logID, true);
                wait(2000);
                clickMouse(x + random(-5, 5), y + random(-5, 5), false);
                atMenu("X");
                wait(random(1300 + lagdelay, 1800 + lagdelay));
                int numba = random(29, 100);
                sendText("" + numba, true);
                wait(random(10000, 12000));
            }
            return 500;
        }
        return 520;
    }

    public boolean clickInventoryItem(int itemID, boolean click) {
        if (getCurrentTab() != TAB_INVENTORY
                && !RSInterface.getInterface(INTERFACE_BANK).isValid()
                && !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
            openTab(TAB_INVENTORY);
        }
        int[] items = getInventoryArray();
        java.util.List<Integer> possible = new ArrayList<Integer>();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == itemID) {
                possible.add(i);
            }
        }
        if (possible.size() == 0) {
            return false;
        }
        int idx = possible.get(random(0, possible.size()));
        Point t = getInventoryItemPoint(idx);
        clickMouse(t, 5, 5, click);
        return true;
    }
}
