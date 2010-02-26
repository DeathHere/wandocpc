/**
 * Educational code to simulate a human hunting for birds
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
import java.util.ArrayList;
import java.util.Map;
import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"LightSpeed, Pirateblanc"}, category = "Hunter",
    name = "SpeedBirdHunting", version = 1.0,
    description = "<html>" +
    "<head>" +
    "</head>" +
    "<body>" +
    "Bird Hunting by LightSpeed and Pirateblanc" +
    "</body>" +
    "</html>")
public class SpeedBirdHunting extends Script implements ServerMessageListener, PaintListener {

    /** Script vars */
    private int xpHour = 0;
    private long startTime;
    private int startExp;
    private int startLvl;
    private RSTile initLoc;
    private final double LAG_FACTOR = 1.5;
    private ArrayList<Integer> gear = new ArrayList<Integer>();
    private final int[] KEEP_ITEMS = {
        10088, 526
    };

    private boolean p_trapsSet = false;
    private boolean p_waiting = false;

    /** Final description vars */
    private final String VERSION = "1.0";
    private final String TITLE = "SpeedBirdHunting";

    /** Final action vars */
    private final int A_WALK_AWAY = 0;
    private final int A_SET_TRAP = 1;
    private final int A_PICKUP_TRAP = 2;
    private final int A_SEARCH_TRAP = 3;
    private final int A_DROP_CRAP = 4;
    private final int A_COLLECT = 5;
    private final int A_OTHER = 100;

    /** Final item vars */
    private final int I_BD_SNARE = 10006;
    private final int I_BD_SNARE_NORMAL = 19175;
    private final int I_BD_SNARE_CAUGHT = 19180;
    private final int I_BD_SNARE_FAIL = 19174;

    // -----------------------------Actions-------------------------------------

    /**
     * Adds stuff in your current inventory, so you wont drop them later
     */
    public void updateGear() {
        gear.clear();
        // Scans inventory
        int[] inventoryArray = getInventoryArray();
        for (int i = 27; i > 0; i--) {
            if (inventoryArray[i] != -1) {
                gear.add(inventoryArray[i]);
                continue;
            }
        }
        // Adds stuff from the keep list
        for (int x : KEEP_ITEMS) {
            gear.add(x);
        }
    }

    /**
     * Finds the abs mouse position of an item in your inventory
     * @param itemID the item id to look for
     * @return mouse position of the item, if there are multiply returns the last
     * one in your inventory
     */
    public Point findPositionOfItem(int itemID) {
        int[] inventoryArray = getInventoryArray();
        int startItem = -1;
        // Scans inventory
        for (int i = 27; i > 0; i--) {
            if (inventoryArray[i] == itemID) {
                startItem = i;
                break;
            }
        }
        Point itemPos = getInventoryItemPoint(startItem);
        if (itemPos.equals(new Point(-1, -1))) {
            // What to do if not found
        }
        return itemPos;
    }

    /**
     * Checks to see if you have traps in your inven
     * @return whether you carry traps
     */
    public boolean isThereInInventoryA(int id) {
        return !(getInventoryCount(id) == 0);
    }

    /**
     * Different actions performed via the switch statment
     * @param actionID what to do, check the top for what variables to use
     * @return pause before next loop cycle
     */
    public int performAction(int actionID) {
        switch (actionID) {
            case A_SET_TRAP: {
                //log("Setting traps");
                if (isThereInInventoryA(I_BD_SNARE)) {
                    //initLoc = getLocation();
                    Point trapPos = findPositionOfItem(I_BD_SNARE);
                    moveMouse(trapPos.x + 10, trapPos.y + 10, 5, 5);
                    wait(random(500, 750));
                    clickMouse(true);
                    wait(random(2500, 2750));
                    // Handle multi traps?
                    p_trapsSet = true;
                    performAction(A_DROP_CRAP);
                    p_waiting = true;
                    return 1000;
                } else {
                    log("You do not have any bird snares!");
                    return -1;
                }
            }

            case A_SEARCH_TRAP: {
                //log("Searching traps");
                RSObject snare = getNearestObjectByID(I_BD_SNARE_FAIL);
                //log("Searching 2");
                if (snare != null && distanceTo(snare) == 1) {
                    wait(random(500, 750));
                    Point clickPos = snare.getLocation().getScreenLocation();
                    clickMouse(new Point(clickPos.x + 5, clickPos.y - 5), true);
                    wait(random(2500, 2750));
                    walkTo(initLoc);
                    wait(random(500, 750));
                    p_waiting = false;
                    p_trapsSet = false;
                }
                snare = getNearestObjectByID(I_BD_SNARE_CAUGHT);
                //log("Searching 3");
                if (snare != null && distanceTo(snare) == 1) {
                    wait(random(500, 750));
                    Point clickPos = snare.getLocation().getScreenLocation();
                    clickMouse(new Point(clickPos.x + 5, clickPos.y - 5), true);
                    wait(random(2500, 2750));
                    walkTo(initLoc);
                    wait(random(500, 750));
                    p_waiting = false;
                    p_trapsSet = false;
                }
                //log("Searching 4");
                return 1000;
            }
                
            case A_WALK_AWAY: {
                //log("Walking away");
                wait(random(500, 750));
                walkTo(new RSTile(initLoc.getX() - 10, initLoc.getY()));
                wait(random(500, 750));
                p_waiting = true;
                return 1000;
            }

            case A_DROP_CRAP: {
                wait(random(500, 750));
                int[] dontDrops = new int[gear.size()];
                for (int i = 0; i < gear.size(); i++) {
                    dontDrops[i] = gear.get(i);
                }
                /*
                for (int i = 0; i < dontDrops.length; i++) {
                    log(Integer.toString(dontDrops[i]));
                }*/
                dropAllExcept(dontDrops);
                wait(random(500, 750));
                return 1000;
            }

            default: {
                return 1000;
            }
        }
    }

    /**
     * When script loops, this is what it does
     * @return pause before next loop cycle
     */
    public int loopAction() {
        if (!isLoggedIn()) {
            Bot.disableRandoms = false;
            return 1000;
        }
        setCameraAltitude(true);
        /*
        if (p_trapsSet && !p_waiting) {
            performAction(A_WALK_AWAY);
        }*/
        if (p_waiting) {
            performAction(A_SEARCH_TRAP);
        }
        if (!p_trapsSet) {
            return performAction(A_SET_TRAP);
        }
        return 1000;
    }

    // -----------------------------Runtime-------------------------------------

    /**
     * Method that repeats over and over and...
     * @return time to wait before next loop
     */
    @Override
    public int loop() {
        try {
            return loopAction();
        }
        catch (Exception e) {
            log("Loop error: " + e.toString());
            return 1000;
        }
    }

    /**
     * What the script should take care of when it begins
     * @param args
     * @return idk what it returns
     */
    @Override
    public boolean onStart(final Map<String, String> args) {
        try {
            /** Gets the initial xp and level for hunting */
            startExp = skills.getCurrentSkillExp(Constants.STAT_HUNTER);
            startLvl = skills.getRealSkillLevel(Constants.STAT_HUNTER);

            /** Gets start tile and start time */
            initLoc = getLocation();
            initLoc = new RSTile(initLoc.getX(), initLoc.getY());
            startTime = System.currentTimeMillis();

            updateGear();
        }
        catch (Exception e) {
            log("Onstart error: " + e.toString());
        }
        return true;
    }

    /**
     * What to do when the script finishes running
     */
    @Override
    public void onFinish() {
        long timeDiff = (System.currentTimeMillis() - startTime) / 1000;
        int hours = (int) ((timeDiff) / 3600);
        int min = (int) ((timeDiff) / 60) - hours * 60;
        log("Script ran for: " + hours + " hours " + min + " min.");
        log(status());
        //logout();
    }

    /**
     * Gives output of details for the player's actions,
     * eg. exp and levels gained
     * @return
     */
    public String status() {
        String s = "";
        s += "Exp Gained: " + (skills.getCurrentSkillExp(Constants.STAT_MAGIC) - startExp);
        s += " , Lvls Gained: " + (skills.getRealSkillLevel(Constants.STAT_MAGIC) - startLvl);
        return s;
    }

    /**
     * To pause the script while stuff is being done
     * @param toSleep how long to pause
     */
    @Override
    public void wait(int toSleep) {
        super.wait((int) (toSleep * Math.pow(LAG_FACTOR, .5)));
    }

    /**
     * Deals with messages sent by RS
     * @param e the message
     */
    public void serverMessageRecieved(ServerMessageEvent e) {
        final String word = e.getMessage().toLowerCase();
        if (word.contains("fallen over")) {
            log("Retriving fallen trap");
        }
    }

    // ------------------------------PAINT--------------------------------------

    // Paint code from FoulFighter, thx
    public void onRepaint(Graphics g) {
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
        paintBar(g, x, y, TITLE + " Runtime: " + hours + " - "
                + minutes + " : " + seconds);

        g.drawString("Version " + VERSION, 436, y + 13);

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

        // Skill xp increase check
        if ((skills.getCurrentSkillExp(Constants.STAT_HUNTER) - startExp) > 0) {
            paintSkillBar(g, x, y + 15, Constants.STAT_HUNTER, startExp);
            y += 15;
        }
    }

    /**
     * Paints the colored foreground part of the skill progression
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

        xpHour = (int) (gained * 3600000.0
                / ((double) System.currentTimeMillis() - (double) startTime));
        g.drawString("/hr: " + Integer.toString(Math.round(xpHour)), 335, y + 13);
    }

    /**
     * Paints the colored bars for progression background
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
     * Gets the string of the skill
     * @param skill
     * @return
     */
    private String SkillToString(int skill) {
        return Skills.statsArray[skill];
    }

}
