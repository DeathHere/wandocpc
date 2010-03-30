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
    private long startTime;
    // Paint vars
    private Image blkJIcon;
    private Image thievingIcon;
    private Image coinsIcon;

    /**
     * Checks and handles random events
     * @return if there is a random event
     */
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
     * Prints the xp gained and other script related info
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
     *
     * @return
     */
    @Override
    public int loop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param e
     */
    public void serverMessageRecieved(ServerMessageEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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
     * Paints the border of the paint
     * @param g graphics obj
     * @param x where
     * @param y where
     * @param width blah
     * @param height blah
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

}
