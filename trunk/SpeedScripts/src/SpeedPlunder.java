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
import java.util.Map;
import org.rsbot.bot.Bot;
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
        "alt=\"SpeedSuperHeat\">" +
        "<hr>" +
        "<center>Created by LightSpeed & Pirateblanc</center>" +
        "<hr>" +
        "</center>" +
        "</body></html>")
public class SpeedPlunder extends Script implements ServerMessageListener, PaintListener {

    // Logic vars
    public long startTime;
    public Events action = Events.Wait;
    // Paint vars
    public Image blkJIcon;
    public Image thievingIcon;
    public Image coinsIcon;

    /**
     * Lists all the possible actions performed by the player's character
     */
    public enum Events {
        Bank, ToBankNpc, ToLadder, ClimbUp, ClimbDown, ToBank, GoEast, GoNorth,
        GoWest, GoSouth, IntoPyramid, OutPyramid, ToMummy, ChatMummy,
        ToSpears, DisTrap, SearchJars, CheckDoors, OpenChest, Eat,
        PotAnti, AttackNpc, Wait
    }

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
        //s += "Exp Gained: " + (skills.getCurrentSkillExp(Constants.STAT_THIEVING) - startExp);
        return s;
    }

    //-------------------------OVERRIDES & IMPLEMENTS---------------------------

    /**
     * Performs operations like fetching pictures and initialize values
     * @param map
     * @return
     */
    @Override
    public boolean onStart(Map<String, String> map) {
        startTime = System.currentTimeMillis();
        /** Getting the images for the various icons in paint */
        /*try {
            thievingIcon = Toolkit.getDefaultToolkit().getImage(new URL("http://www.wandocpc.site90.com/images/icons/thieving.png"));
            coinsIcon = Toolkit.getDefaultToolkit().getImage(new URL("http://www.wandocpc.site90.com/images/icons/coins.png"));
            blkJIcon = Toolkit.getDefaultToolkit().getImage(new URL("http://www.wandocpc.site90.com/images/icons/blackjack.png"));
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Receives msgs from the server and do stuff accordingly
     * @param e
     */
    public void serverMessageRecieved(ServerMessageEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //-------------------------------PAINT--------------------------------------

    /**
     * Paints the good looking and informative stuff on screen
     * @param render
     */
    public void onRepaint(Graphics render) {
        throw new UnsupportedOperationException("Not supported yet.");
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

    //--------------------------------WALKING-----------------------------------

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