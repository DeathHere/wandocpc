import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.event.events.ServerMessageEvent;

@ScriptManifest(authors = {"lord_qaz, Pirateblanc"}, category = "Thieving", name = "Revision MFT owned, Fixed", version = 1.2, description = "<html><head></head><body>Do NOT use if you have less than 32 hp, eats at half health. Hp fix by Pirateblanc</body></html\n")
public class SpeedDraynorFarmer extends Script implements PaintListener, ServerMessageListener {

    public String status = "--";
    public long runTime = 0, seconds = 0, minutes = 0, hours = 0;
    public long startTime;
    public int fail;
    public int startEXP;
    public int pick;
    public int foodID = -1;
    public boolean eatfoodifinvfull = false;
    public int FarmerID = 2234;
    final int[] FOOD_IDS = {1895, 1893, 1891, 4293, 2142, 291, 2140, 3228, 9980,
        7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878, 7568, 2343,
        1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339, 351, 329,
        3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397, 391, 3369,
        3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335, 7178, 7180,
        7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003, 2011, 2289,
        2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893, 1895, 1897,
        1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066, 7068, 1942,
        6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993, 1989, 1978,
        5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907, 1911, 5745,
        2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084, 2034, 2048,
        2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277, 2225, 2255,
        2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092, 2032, 2074,
        2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883, 1971, 4608,
        1883, 1885, 1942};
    RSTile bankPath[] = {new RSTile(3080, 3250), new RSTile(3092, 3244)};
    //RSTile farmerPath = reversePath(bankPath);

    public boolean onStart(Map<String, String> args) {
        foodID = Integer.parseInt(JOptionPane.showInputDialog("Enter the foodId to take from bank, common ones, trout - 333, monkfish - 7946", 333));
        eatfoodifinvfull = Boolean.parseBoolean(JOptionPane.showInputDialog("Eat food to make space if inventory is full?", false));

        if (foodID == -1) {
            return false;
        }

        log("eat - " + foodID);
        log("eat food to make space - " + eatfoodifinvfull);

        log("" + MouseHandler.DEFAULT_MOUSE_SPEED);
        startEXP = skills.getCurrentSkillExp(STAT_THIEVING);
        startTime = System.currentTimeMillis();
        return true;
    }

    private void runControl() {
        if (!isRunning() && getEnergy() > random(20, 30)) {
            setRun(true);
        }
    }

    public int loop() {

        runControl();

        if (atBank()) {
            if (isInventoryFull() || getInventoryCount(FOOD_IDS) == 0) {
                return useBank();
            } else {
                return goFarmer();
            }
        }

        if (getInventoryCount(FOOD_IDS) > 0 && (!isInventoryFull() || (isInventoryFull() && eatfoodifinvfull))) {
            if (isEatingRequired() && getInventoryCount(FOOD_IDS) >= 1) {
                status = "Eating";
                clickInventoryItem(FOOD_IDS, "Eat");
                moveAfter();
                return (random(2000, 2500) + 50);
            }

            if (eatfoodifinvfull && isInventoryFull() && inventoryContains(FOOD_IDS)) {
                status = "Eating for space";
                clickInventoryItem(FOOD_IDS, "Eat");
                moveAfter();
                return (random(2000, 2500) + 50);
            }

            RSNPC Farmer = getNearestNPCByID(FarmerID);

            if (getInventoryCount(FOOD_IDS) > 0) {
                if (tileOnScreen(Farmer.getLocation())) {
                    status = "stealing";
                    atNPC(Farmer, "Pickpocket");
                    return random(1000, 1200);
                } else {
                    status = "moving to farmer";
                    walkTileMM(randomizeTile(Farmer.getLocation(), 1, 1));
                    return random(10, 20);
                }
            }
        } else {
            return goBank();
        }

        return 100;
    }

    /**
     * Check the player hp against times failed (-30 dmg)
     * See if the player is at half hp or less
     * @return whether the player should eat a piece of food
     */
    public boolean isEatingRequired() {
        if (getMyPlayer().getHPPercent() < 40) {
            return true;
        }
        return false;
    }

    public int goBank() {
        //3092,3244
        status = "walking bank";
        if (walkTo(bankPath[1])) {
            setCameraRotation(random(1, 360));
        }
        return random(200, 300);
    }

    public int goFarmer() {
        //3080,3250
        status = "walking to farmer";
        walkTileMM(bankPath[0], 5, 5);
        return random(200, 300);
    }

    public boolean atBank() {
        RSObject bankBooth = findObject(bank.BankBooths);
        if (bankBooth != null && distanceTo(bankBooth.getLocation()) < 5) {
            return true;
        }
        return false;
    }

    public int useBank() {
        try {
            if (bank.isOpen()) {
                if (getInventoryCount() > 0) {
                    status = "putting shit in";
                    bank.depositAll();
                } else {
                    status = "Taking out food";
                    bank.atItem(foodID, "Withdraw-10");
                    return random(1000, 2000);
                }
            } else {
                status = "Opening bank";
                RSObject booth = findObject(bank.BankBooths);
                if (booth != null) {
                    atObject(booth, "Use-quickly");
                    return random(1000, 2000);
                } else {
                    status = "Cannot find bank booth wtf???";
                }
            }
        } catch (final Exception e) {
            log("useBank() error");
            e.printStackTrace();
        }

        return random(400, 500);
    }

    public boolean moveAfter() {
        Point p = getMouseLocation();
        moveMouse(p, random(-2, 2), random(-2, 2));
        return false;
    }

    public void onRepaint(Graphics g) {
        if (!isLoggedIn() || isLoginScreen()) {
            return;
        }

        long millis = System.currentTimeMillis() - startTime;
        hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        seconds = millis / 1000;
        int gained = skills.getCurrentSkillExp(STAT_THIEVING) - startEXP;
        float secExp = 0;
        if ((minutes > 0 || hours > 0 || seconds > 0) && gained > 0) {
            secExp = ((float) gained)
                    / (float) (seconds + (minutes * 60) + (hours * 60 * 60));
        }
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(3, 60, 145, 143 - 60);
        g.setColor(Color.WHITE);
        g.drawRect(3, 60, 145, 143 - 60);
        g.setColor(Color.RED);
        g.drawString("By lord qaz, RevMFT the improved version", 5, 50);
        g.setColor(Color.WHITE);
        g.drawString("Ran for: " + hours + "h " + minutes + "m " + seconds + "s", 5, 72);
        g.drawString("Status: " + status, 5, 84);
        g.drawString("Times picked: " + pick, 5, 95);
        g.drawString("Times Failed: " + fail, 5, 106);
        g.drawString("EXP Earned: " + gained, 5, 117);
        g.drawString("" + (secExp * 60) * 60 + " EXP/H", 5, 128);
        g.drawString("EXP until lvl: " + skills.getXPToNextLevel(STAT_THIEVING), 5, 139);
        g.drawString("HP: " + getMyPlayer().getHPPercent() + "%", 5, 160);
    }

    public void serverMessageRecieved(ServerMessageEvent arg0) {
        if (arg0.getMessage().contains("You steal ")) {
            pick++;
        }

        if (arg0.getMessage().contains("You fail")) {
            fail++;
        }
    }

    private boolean clickInventoryItem(final int[] ids, final String command) {
        try {
            if (getCurrentTab() != Constants.TAB_INVENTORY
                    && !RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()
                    && !RSInterface.getInterface(Constants.INTERFACE_STORE).isValid()) {
                openTab(Constants.TAB_INVENTORY);
            }
            final int[] items = getInventoryArray();
            final java.util.List<Integer> possible = new ArrayList<Integer>();
            for (int i = 0; i < items.length; i++) {
                for (final int item : ids) {
                    if (items[i] == item) {
                        possible.add(i);
                    }
                }
            }
            if (possible.size() == 0) {
                return false;
            }
            final int idx = possible.get(random(0, possible.size()));
            final Point t = getInventoryItemPoint(idx);
            moveMouse(t, 5, 5);
            wait(random(100, 290));
            if (getMenuActions().get(0).equals(command)) {
                clickMouse(true);
                return true;
            } else {
                // clickMouse(false);
                return atMenu(command);
            }
        } catch (final Exception e) {
            log("clickInventoryFood(int...) error");
            return false;
        }
    }
}
