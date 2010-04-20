/**
 * Educational code to simulate a human performing pyramid plunder
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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.net.URL;
import java.util.LinkedList;
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
import org.rsbot.script.randoms.antiban.BreakHandler;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

/**
 * Manifest in html/etc... so RSBot can read our script.
 * Script was started April 10, 2010 and finished ? ?, 2010.
 * @author LightSpeed Pirateblanc
 */
@ScriptManifest(authors = {"LightSpeed, Pirateblanc"}, category = "Thieving",
name = "SpeedPlunder", version = 1.0, description = "<html><head>" +
        "<style type=\"text/css\">" +
        "body {" +
        "   color: #FFFFFF;" +
        "   background-color: #000000;" +
        "}" +
        "</style>" +
        "</head>" +
        "<body>" +
        "<center>" +
        "<img src=\"http://www.wandocpc.site90.com/images/screenshots/superheat.jpg\"" +
        "alt=\"SpeedPlunder\">" +
        "<hr>" +
        "<center>Created by LightSpeed & Pirateblanc</center>" +
        "<hr>" +
        "</center>" +
        "<p>" +
        "Script to make you gain levels in theiving." +
        "</p>" +
        "</body></html>")
/**
 * ----------------------------- AUTHOR NOTE -----------------------------------
 * This script is designed to be run when you are near the banker in the
 * bank under Sophanem in the southern edges of the Runesape desert.
 * Please do not start the script at any other location (eg. in the pyramid,
 * on the ground floor of the bank, at the temple) as this may cause the script
 * to crash.
 *
 * Also please make sure you have enough food and anti-poison potions in the
 * visible tab of your bank, preferably in the first row, if you plan to leave
 * this script on for a long period of time.
 *
 * Please visit out website at http://www.?.com 
 * and make a donation to support our efforts if you enjoy this
 * script or any other of our scripts.
 * Enjoy Speed Plundering, and thank you for using our script. ^_^
 * -----------------------------------------------------------------------------
 */
public class SpeedPlunder extends Script implements ServerMessageListener, PaintListener {

    /** ---------------------------- Logic vars ---------------------------- */
    
    private long startTime;
    /** 
     * Events is a enum that represents is updated and changed during the
     * looping structure and it tells the player which methods/actions should
     * be performed during this looping cycle
     */
    private Events action = Events.FirstStart;
    private final int[] foodIDs = {
        379, // Lobster
        
    };
    /**
     * The highest lvl of room in the pyramid you can plunder.
     * It is always best to hit all the jars in the top room and then
     * some of the jars in the second best room the player can access.
     * This creates the best experience combination
     */
    private int roomToHit = 0;
    private int thievingLvl = skills.getCurrentSkillLevel(Constants.STAT_THIEVING);
    private long startExp;
    /**
     * Did the player find the chamber with the mummy yet.
     * This variable is used to determine walk cycle attempts to the 4 sides
     * of the pyramid. North, East, and West (Random). If the mummy has
     * been found, end searching and proceed to chat with the mummy.
     */
    private boolean foundMummy = false;
    private boolean inGame = false;
    private final int npcMummyID = 4476;
    /**
     * The order the sides of pyramid should be checked for the mummy npc.
     * Note that the South side is excluded as it should always be checked first
     */
    private LinkedList<Events> checkOrder = new LinkedList<Events>();
    /**
     * Mainly used to keep track of the iteration through (checkOrder).
     * Should always contain a value from 0 to 2
     */
    private int curCheckDir = 0;

    /* ------------------------- RSTile path arrays ------------------------- */

    // This section is organized so that that variable names are in 3 parts
    // [where] [direction] [direction if reversed]
    // Eg. bankInOut = [bank][In][Out] which means the route to the bank
    // currently going into the bank, but if reversed via reversePath() it
    // exits the bank and leads to the perimeter of the pyramid
    // ! This naming is no longer used, but could be helpful !

    private RSTile[] bankInOut = {
        new RSTile(3303, 2800),
        new RSTile(3310, 2800)
    };

    private RSTile[] bankerTo = {
        new RSTile(2799, 5168)
    };

    private RSTile[] ladderTo = {
        new RSTile(2799, 5160)
    };

    private RSTile[] northTo = {
        new RSTile(3303, 2798),
        new RSTile(3289, 2802)
    };

    private RSTile[] southTo = {
        new RSTile(3303, 2798),
        new RSTile(3289, 2788)
    };

    private RSTile[] eastTo = {
        new RSTile(3303, 2798),
        new RSTile(3296, 2796)
    };

    private RSTile[] westTo = {
        new RSTile(3303, 2798),
        new RSTile(3282, 2795)
    };
    
    /* ----------------------------- Paint vars ----------------------------- */

    private Image blkJIcon;
    private Image thievingIcon;
    private Image coinsIcon;

    /* ----------------------------- Action vars ----------------------------- */

    /**
     * Lists all the possible actions performed by the player's character.
     * These are modified in the loop and used in the loop's switch statement
     */
    private enum Events {
        Bank,               // Talk and interact with the banker
        ToBankNpc,          // Walks from the bottom of the ladder to the banker
        ToLadder,           // Walks from the banker to the ladder
        ClimbUp,            // Climbs...
        ClimbDown,          // Go down...
        ToBank,             // Returns from the pyramid to the ladder
        GoEast,             // Go the the pyramid in the direction specified
        GoNorth,            // -> Look at previous entry
        GoWest,             // -> Look at previous entry
        GoSouth,            // -> Look at previous entry
        //EnterPyramid,(deprecated)       // Enters the pyramid via the nearest entrance
        OutPyramid,         // Exits the pyramid. This can be used in many places
        CheckMummy,         // Checks the area for the mummy npc
        ToMummy,            // Walks 5 coordinates north, now next to the mummy
        ChatMummy,          // Starts the minigame via mummy
        ToSpears,           // Walks to the traps in the start of the level
        DisTrap,            // Disarms the trap (Might have some issues)
        SearchJars,         // Searches a number of jars 
        CheckDoors,         // Searches all 4 doors in each room for passage
        OpenChest,          // Searches the golden chest in the middle of the room
        Eat,                // Eat 1 piece of food
        PotAnti,            // Drinks some anti-poison potion, normal + super
        AttackNpc,          // If player is attacked, attack back
        Wait,               //
        FirstStart          // Initial value when program starts
    }

    //--------------------------- SCRIPT HELPERS -------------------------------

    /**
     * Checks and handles random events
     * @return if there is a random event
     */
    public boolean checkForRandoms() {
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

    /**
     * Gives output of details for the player's actions,
     * eg. exp and levels gained
     * @return
     */
    public String status() {
        String s = "";
        s += "Exp Gained: " +
                (skills.getCurrentSkillExp(Constants.STAT_THIEVING) - startExp);
        return s;
    }

    /**
     * Moves the player 3~5 spaces up the inside of the pyramid start chamber,
     * so we can speak to the mummy in an unbot like fasion.
     */
    public void walkToMummy() {
        RSTile curTile = getMyPlayer().getLocation();
        walkToTile(new RSTile(curTile.getX(), curTile.getY() + random(3, 5)));
        wait(2000);
    }

    /**
     * Walks the path designated by the current value in (action).
     * Look at the loop() to see how it is used.
     */
    public void walkDesignatedPath() {
        RSTile[] usedPath = null;
        boolean reverse = false;
        switch (action) {
            case ToBank:
                usedPath = bankInOut;
                break;
            case ToBankNpc:
                usedPath = bankerTo;
                break;
            case ToLadder:
                usedPath = ladderTo;
                break;
            case GoEast:
                usedPath = eastTo;
                break;
            case GoNorth:
                usedPath = northTo;
                break;
            case GoSouth:
                usedPath = southTo;
                break;
            case GoWest:
                usedPath = westTo;
                break;
            default:
                log("Something wrong in walkDesignatedPath()");
                break;
        }

        // Performs the walking required by using the preestablished (usedPath)
        if (reverse) {
            walkPath(reversePath(usedPath));
        }
        else {
            walkPath(usedPath);
        }
        log("Ending Walk");
        wait(random(1000, 2000));
    }

    /**
     * Eats the first food of any type according to the foodIDs in the player's
     * inventory
     * @return if a food was eaten, false means out of food
     */
    public boolean eat() {
        Point p = null;
        for (int food : foodIDs) {
            int[] inventory = getInventoryArray();
            for (int i = 0; i < 28; i++) {
                int inv = inventory[i];
                if (inv == food) {
                    p = getInventoryItemPoint(i);
                    break;
                }
            }
        }
        p.translate(15, 15);
        if (p != null) {
            moveMouse(p);
            String[] options = {"Drink", "Eat"};
            wait(random(250, 500));
            if (!atMenu(options)) {
                clickMouse(true);
            }
            wait(random(500, 1500));
            return true;
        }
        return false;
    }
    
    public boolean isBankingNeeded() {
        return false;
    }

    /**
     * This method should bank intelligently
     */
    public void bank() {
        log("Banking");
        wait(2000);
    }

    /**
     * Changes the order the player checks the chambers of the pyramid for the
     * mummy. This however does not affect the South chamber, as that room should
     * always be checked first. For anti-ban purposes only, no real value.
     */
    public void shuffleCheckOrder() {
        for (int i = 0; i < 30; i++) {
            checkOrder.add(checkOrder.remove(checkOrder.size() - 1));
        }
    }

    /**
     * Generic method to perform an action on an object
     * @param objID ID of the nearest obj to look for
     * @param action String command to execute at the object
     */
    public void interactWith(int objID, String action) {
        log("Doing: " + action + " to " + Integer.toString(objID));
        atObject(getNearestObjectByID(objID), action);
        wait(random(1000, 3000));
    }

    //------------------------ OVERRIDES & IMPLEMENTS --------------------------

    /**
     * Performs operations like fetching pictures and initialize values
     * @param map idk
     * @return idk
     */
    @Override
    public boolean onStart(Map<String, String> map) {
        startTime = System.currentTimeMillis();
        startExp = skills.getCurrentSkillExp(Constants.STAT_THIEVING);
        
        // Check some player stats to determine how the script should run
        // in the pyramid and if extra food is required
        if (thievingLvl > 90)
            roomToHit = 8;
        else if (thievingLvl > 80)
            roomToHit = 7;
        else if (thievingLvl > 70)
            roomToHit = 6;
        else if (thievingLvl > 60)
            roomToHit = 5;
        else if (thievingLvl > 50)
            roomToHit = 4;
        else if (thievingLvl > 40)
            roomToHit = 3;
        else if (thievingLvl > 30)
            roomToHit = 2;
        else if (thievingLvl > 20)
            roomToHit = 1;

        // Initialize the checkOrder and shuffle it for use
        checkOrder.add(Events.GoEast);
        checkOrder.add(Events.GoWest);
        checkOrder.add(Events.GoNorth);
        shuffleCheckOrder();

        /** Fetching the images for the various icons in paint */
        /*try {
            thievingIcon = Toolkit.getDefaultToolkit().getImage(
         new URL("http://www.wandocpc.site90.com/images/icons/thieving.png"));
            coinsIcon = Toolkit.getDefaultToolkit().getImage(
         new URL("http://www.wandocpc.site90.com/images/icons/coins.png"));
            blkJIcon = Toolkit.getDefaultToolkit().getImage(
         new URL("http://www.wandocpc.site90.com/images/icons/blackjack.png"));
        }
        catch(Exception e) {
            log("Unable to load picture icons");
        }
         */
        return true;
    }

    /**
     * Prints the xp gained and other script related info when script exiting
     */
    @Override
    public void onFinish() {
        long timeDiff = (System.currentTimeMillis() - startTime) / 1000;
        int hours = (int) ((timeDiff) / 3600);
        int min = (int) ((timeDiff) / 60) - hours * 60;
        log("Script Ran for: " + hours + " hours " + min + " min.");
        log(status());
        super.onFinish();
    }

    /**
     * Runs the main looping structure of the script
     * @return time in msec to next loop cycle
     */
    @Override
    public int loop() {
        // Default return time
        int retTime = 100;

        // Checks if the player is logged in
        if (!isLoggedIn()) {
            log("Please login first! And be in the underground bank.");
            return -1;
        }

        /** Checks important stuff before the 2 main switches */
        if (getMyPlayer().getHPPercent() < random(35, 55))
            action = Events.Eat;

        if (foundMummy) {
            action = Events.ChatMummy;
        }

        // --- Logic switch ---
        // Checks the previous action and increment the value to the next
        // correct action accordingly
        switch(action) {
            case FirstStart:
                action = Events.Bank;
                break;
            case Bank:
                action = Events.ToLadder;
                break;
            case ToLadder:
                action = Events.ClimbUp;
                break;
            case ClimbUp:
                action = Events.GoSouth;
                break;
            case GoSouth:
                action = Events.CheckMummy;
                break;
            case GoNorth:
                action = Events.CheckMummy;
                break;
            case GoEast:
                action = Events.CheckMummy;
                break;
            case GoWest:
                action = Events.CheckMummy;
                break;
            case CheckMummy:
                action = Events.OutPyramid;
                break;
            case ChatMummy:
                action = Events.ToSpears;
                break;
            case OutPyramid:
                // If the player needs to resupply
                if (isBankingNeeded()) {
                    action = Events.ToBank;
                    break;
                }
                // If the player has just exited a game and no need to bank
                if (inGame) {
                    curCheckDir = 0;
                    shuffleCheckOrder();
                    action = Events.GoSouth;
                    break;
                }
                // If the player is still searching for the mummy chamber,
                // proceed with the checkOrder
                action = checkOrder.get(curCheckDir);
                curCheckDir++;
                break;
            default:
                break;
        }

        // --- Perform action switch ---
        // For what each actions mean, check the declaration of (action)
        // for further documentation
        switch(action) {
            case AttackNpc:
                break;
            case Bank:
                bank();
                break;
            case CheckMummy:
                foundMummy = false;
                RSNPC mummy = getNearestNPCByID(npcMummyID);
                if (mummy != null) {
                    foundMummy = true;
                }
                wait(random(500, 3000));
                break;
            case ChatMummy:
                log("Chatting with mummy");
                break;
            case CheckDoors:
                break;
            case ClimbDown:
                log("Climbing Down");
                break;
            case ClimbUp:
                log("Climbing Up");
                interactWith(20277, "Climb-up");
                break;
            case DisTrap:
                break;
            case Eat:
                log("Eating");
                eat();
                break;
            case GoEast:
                log("East");
                walkDesignatedPath();
                interactWith(16546, "Search");
                break;
            case GoWest:
                log("West");
                walkDesignatedPath();
                interactWith(16544, "Search");
                break;
            case GoSouth:
                log("South");
                walkDesignatedPath();
                interactWith(16545, "Search");
                break;
            case GoNorth:
                log("North");
                walkDesignatedPath();
                interactWith(16543, "Search");
                break;
            case ToBank:
                log("ToBank");
                walkDesignatedPath();
                break;
            case ToBankNpc:
                log("ToBanker");
                walkDesignatedPath();
                break;
            case ToLadder:
                walkDesignatedPath();
                break;
            case ToSpears:
                log("ToSpears");
                break;
            default:
                break;
        }
        return retTime;
    }

    /**
     * Receives msgs from the server and do stuff accordingly
     * @param e stuff the server sends in its broadcast
     */
    public void serverMessageRecieved(ServerMessageEvent e) {
        log("Received Msg: " + e.getMessage());
    }

    //------------------------------ PAINT -------------------------------------

    /**
     * Paints the good looking and informative stuff on screen
     * @param g graphics obj
     */
    public void onRepaint(Graphics g) {

        // Gets mouse
        final Mouse mouse = Bot.getClient().getMouse();
        final int mouse_x = mouse.getMouseX();
        final int mouse_y = mouse.getMouseY();
        final int mouse_press_x = mouse.getMousePressX();
        final int mouse_press_y = mouse.getMousePressY();
        final long mouse_press_time = mouse.getMousePressTime();

        // Draws mouse
        Polygon po = new Polygon();
        po.addPoint(mouse_x, mouse_y);
        po.addPoint(mouse_x, mouse_y + 15);
        po.addPoint(mouse_x + 10, mouse_y + 10);
        g.setColor(new Color(110, 9, 128, 180));
        g.fillPolygon(po);
        g.drawPolygon(po);
        
    }

    /**
     * Paints the icons in the rect area
     * @param g graphics obj
     * @param x where
     * @param y where
     */
    public void paintIcons(Graphics g, int x, int y) {
        g.drawImage(thievingIcon, x + 20, y + 20, null);
        g.drawImage(blkJIcon, x + 100, y + 20, null);
        g.drawImage(coinsIcon, x + 200, y + 20, null);
    }

    /**
     * Paints the border of the paint dynamically via the input values
     * @param g graphics obj
     * @param x where on screen
     * @param y where on screen
     * @param width width of the border
     * @param height height of the border
     */
    public void paintRect(Graphics g, int x, int y, int width, int height) {
        // Top left corner
        g.setColor(new Color(155, 152, 146));
        g.fillRect(x + 2, y - 0, 5, 4);
        // Top border
        g.setColor(new Color(148, 145, 138));
        g.drawLine(x + 6, y, x + width, y);
        g.setColor(new Color(138, 135, 128));
        g.drawLine(x + 6, y + 1, x + width - 1, y + 1);
        g.setColor(new Color(128, 125, 118));
        g.drawLine(x + 6, y + 2, x + width - 2, y + 2);
        g.setColor(new Color(118, 115, 108));
        g.drawLine(x + 6, y + 3, x + width - 3, y + 3);
        g.setColor(new Color(65, 62, 55));
        g.drawLine(x + 6, y + 4, x + width - 4, y + 4);
        // Fill black center
        g.setColor(new Color(0, 0, 0, 210));
        g.fillRect(x + 6, y + 5, width - 8, height - 8);
        // Left border
        g.setColor(new Color(148, 145, 138));
        g.drawLine(x + 2, y + 4, x + 2, y + height - 1);
        g.setColor(new Color(138, 135, 128));
        g.drawLine(x + 3, y + 4, x + 3, y + height - 2);
        g.setColor(new Color(128, 125, 118));
        g.drawLine(x + 4, y + 4, x + 4, y + height - 3);
        g.setColor(new Color(118, 115, 108));
        g.drawLine(x + 5, y + 4, x + 5, y + height - 4);
        // Right border
        g.setColor(new Color(47, 45, 35));
        g.drawLine(x + width - 0, y + 1, x + width - 0, y + height);
        g.drawLine(x + width - 1, y + 2, x + width - 1, y + height - 1);
        g.drawLine(x + width - 2, y + 3, x + width - 2, y + height - 2);
        g.drawLine(x + width - 3, y + 4, x + width - 3, y + height - 3);
        // Bottom border
        g.setColor(new Color(47, 45, 35));
        g.drawLine(x + 2, y + height + 0, x + width - 1, y + height + 0);
        g.drawLine(x + 2, y + height - 1, x + width - 2, y + height - 1);
        g.drawLine(x + 3, y + height - 2, x + width - 3, y + height - 2);
        g.drawLine(x + 4, y + height - 3, x + width - 4, y + height - 3);
        // Icons
        paintIcons(g, x, y);
    }

    //------------------------------- WALKING ----------------------------------

    /**
     * Recursive algorithm to find where to click to hit the direction of a tile
     * off screen. It takes the average of the X and Y of the player's position
     * and the RSTile to walk to, and if the average (midpoint) is on the screen
     * then move. If the midpoint is not on the screen, it takes the midpoint
     * again and see if that midpoint is on screen. Move if it is. Or do...
     * @param tile where to move to
     * @return the place to click on minimap that is towards the direction of
     * the tile you want to go to
     */
    public RSTile checkTile(final RSTile tile) {
        if (tileOnMap(tile)) {
            return tile;
        }
        final RSTile loc = getMyPlayer().getLocation();
        final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2,
                (loc.getY() + tile.getY()) / 2);
        return tileOnMap(walk) ? walk : checkTile(walk);
    }

    /**
     * Checks the tiles in a path to find the closest, and therefore that
     * closest tile is the findStartTile location
     * @param path RSTile path to search through for findStartTile
     * @return index of the findStartTile location in the RSTile path
     */
    public int findStartTile(final RSTile[] path) {
        int start = 0;
        for (int a = path.length - 1; a > 0; a--) {
            if (tileOnMinimap(path[a])) {
                start = a;
                break;
            }
        }
        return start;
    }

    /**
     * Checks to see if the tile in question is on the minimap
     * @param tile
     * @return if the tile is on the minimap
     */
    public boolean tileOnMinimap(final RSTile tile) {
        final Point p = tileToMinimap(tile);
        return Math.sqrt(Math.pow(627 - p.x, 2) + Math.pow(85 - p.y, 2))
                < random(60, 74);
    }

    /**
     * Gets the player to walk a specified RSTile[] pathway
     * @param path the points to walk
     * @return if the walk cycle has been completed
     */
    public boolean walkPath(final RSTile[] path) {
        for (int i = findStartTile(path); i < path.length; i++) {
            // If there is enough energy, hit the run button
            if (!isRunning() && getEnergy() > random(40, 60)) {
                clickMouse(random(707, 762), random(90, 121), true);
            }
            // Walk to the current point on path
            walkTo(randomizeTile(path[i], 1, 1));
            waitToMove(2000);
            while (getMyPlayer().isMoving()) {
                wait(3000);
            }
            // If you are already at the last path point, stop
            if (path[i] == path[path.length - 1]) {
                break;
            }
            // If the point is off screen, move to it via the minimap
            while (!tileOnMinimap(path[i + 1])) {
                if (!getMyPlayer().isMoving()) {
                    walkTo(checkTile(randomizeTile(path[i + 1], 1, 1)));
                }
                /**
                if (antiban) {
                    Antiban.run();
                }
                 */
            }
        }
        // If the player is still moving, perform some antiban actions
        while (getMyPlayer().isMoving()) {
            //Antiban.run();
        }
        return distanceTo(path[path.length - 1]) <= 4;
    }

    /**
     * Walks to a specified tile in an orderly manner
     * @param tile where to move to
     * @return whether the movement has been completed
     */
    public boolean walkToTile(final RSTile tile) {
        if (!tileOnMap(tile)) {
            return false;
        }
        walkTo(tile);
        if (waitToMove(1500)) {
            while (getMyPlayer().isMoving()) {
                wait(15);
            }
            return true;
        }
        return false;
    }
}