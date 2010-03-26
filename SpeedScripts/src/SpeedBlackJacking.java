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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Map;
import java.util.TreeMap;
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
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"LightSpeed, Pirateblanc"}, category = "Thieving",
name = "SpeedSuperHeat", version = 1.0, description = "<html><head>" +
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

    protected final int banditID = 1879;
    /**
     * 1993 - Wine
     * 1994 - Noted Wine
     */
    protected final int[] foods = {
        1993
    };
    // Find these in server msg
    protected final String hit = "unconscious";
    protected final String fail = "glances";
    protected final String totalFail = "stunned";

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
     *
     * @param render
     */
    public void onRepaint(Graphics render) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @param map
     * @return
     */
    @Override
    public boolean onStart(Map<String, String> map) {

        return true;
    }
    
}
