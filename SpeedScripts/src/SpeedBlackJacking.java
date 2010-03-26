/**
 * Educational code to simulate a human performing blackjacking
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
name = "SpeedBlackJacking", version = 1.0, description = "<html><head>" +
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
public class SpeedBlackJacking extends Script implements ServerMessageListener, PaintListener {

    protected final int[] npcIDs = {1895, 7478, 1879};
    /**
     * 1993 - Wine
     * 1994 - Noted Wine
     */
    protected final int[] foods = {
        1993
    };
    protected final int[] blackJack = {
        4600
    };
    // Find these in server msg
    protected final String serMsg[] = {" "};
    protected final String hit = "unconscious";
    protected final String fail = "glances";
    protected final String totalFail = "stunned";
    protected final String combat = "combat";
    private RSTile loc;
    private boolean recordInitial = false;
    private int[] startExpArry;
    
    protected boolean initialized() {
        loc = getLocation();
        startExpArry = new int[30];
        for (int i = 0; i < 20; i++) {
            startExpArry[i] = skills.getCurrentSkillExp(i);
        }
        return equipmentContainsOneOf(blackJack);
    }

    // Paint vars
    private Image blkJIcon;
    private Image thievingIcon;
    private Image coinsIcon;

    /**
     * 
     * @return
     */
    @Override
    public int loop() {
        if (!isLoggedIn()) {
            Bot.disableRandoms = false;
            return 1000;
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
                return 1;
            }
        }
        setCameraAltitude(true);
        rob();
        return 1;
    }

    protected boolean rob() {
        int npcID = 0;
        int level = skills.getCurrentSkillLevel(Constants.STAT_THIEVING);
        if (level < 45) {
            npcID = npcIDs[0];
        } else if (level < 55) {
            npcID = npcIDs[1];
        } else {
            npcID = npcIDs[2];
        }
        while (!isPaused && !checkForRandoms()) {
            serMsg[0] = " ";
            if (getMyPlayer().getHPPercent() < random(45, 60)) {
                Point p = null;
                for (int food : foods) {
                    int[] inventory = getInventoryArray();
                    for (int i = 0; i < 28; i++) {
                        int inv = inventory[i];
                        if (inv == food) {
                            p = getInventoryItemPoint(i);
                        }
                    }
                }
                p.translate(15, 15);
                if (p != null) {
                    moveMouse(p);
                    String[] options = {"Drink", "Eat"};
                    wait(random(250,500));
                    if(!atMenu(options))
                    clickMouse(true);
                    wait(random(250,500));
                } else {
                    serMsg[0] = combat;
                }
            }
            RSNPC npc = getNearestNPCByID(npcID);
            if (npc == null) {
                return false;
            }


            atNPC(npc, "Knock");
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 1000) {
                if (serMsg.length > 2) {
                    break;
                }
            }
            if (serMsg[0].contains("Perhaps")) {
                log("Other bandit too close. Please move.");
                wait(random(5000, 6000));
                return false;
            } else if (serMsg[0].contains(totalFail)) {
                wait(random(2500, 3500));
                continue;
            } else if (serMsg[0].contains(combat)) {
                atObject(getNearestObjectByID(6261), "Climb-up");
                start = System.currentTimeMillis();
                while (System.currentTimeMillis() - start < 5000) {
                    if (animationIs(828)) {
                        break;
                    }
                }
                wait(random(2000, 4000));
                atObject(getNearestObjectByID(6260), "Climb-down");
                wait(random(1000, 3000));
                continue;
            }
            int animation = getFirstNpcAnim(npc);
            atNPC(npc, "Pick");
            if (animation == 12413 || serMsg[0].contains(hit)) {
                atNPC(npc, "Pick");
                wait(random(250, 750));
            }
            wait(random(750, 1500));
        }
        return true;
    }

    public int getFirstNpcAnim(RSNPC npc) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 1000) {
            if (npc.getAnimation() != -1) {
                return npc.getAnimation();
            }
        }
        return -1;
    }

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
     *
     * @param e
     */
    public void serverMessageRecieved(ServerMessageEvent e) {
        synchronized (serMsg) {
            String msg = e.getMessage();
            if(msg.contains(hit) || msg.contains(fail) || msg.contains(combat) || msg.contains(totalFail) || msg.contains("Perhaps"))
            serMsg[0] = e.getMessage();
        }
    }

    /**
     * 
     * @param map
     * @return
     */
    @Override
    public boolean onStart(Map<String, String> map) {
        /** Getting the images for the various icons in paint */
        try {
            thievingIcon = Toolkit.getDefaultToolkit().getImage(new URL("http://www.wandocpc.site90.com/images/icons/thieving.png"));
            coinsIcon = Toolkit.getDefaultToolkit().getImage(new URL("http://www.wandocpc.site90.com/images/icons/coins.png"));
            blkJIcon = Toolkit.getDefaultToolkit().getImage(new URL("http://www.wandocpc.site90.com/images/icons/blackjack.png"));
        }
        catch(Exception e) {
            log("Unable to load picture icons");
        }
        recordInitial = true;
        return true;
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }

    public void paintIcons(Graphics g, int x, int y) {
        g.drawImage(thievingIcon, x + 20, y + 20, null);
        g.drawImage(blkJIcon, x + 100, y + 20, null);
        g.drawImage(coinsIcon, x + 200, y + 20, null);
    }

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

    /**
     * Paints stuff on screen
     * @param render
     */
    public void onRepaint(Graphics g) {
        //paintRect(g, 5, 500, 600, 150);
    }
    
}
