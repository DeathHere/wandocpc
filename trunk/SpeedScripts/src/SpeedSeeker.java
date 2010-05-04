/**
 * Educational code to simulate a human finding stupid RSTiles
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

public class SpeedSeeker extends Script implements ServerMessageListener, PaintListener {

    private LinkedList<RSTile> coordinates = new LinkedList<RSTile>();

    @Override
    public int loop() {
        
        return 500;
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onRepaint(Graphics g) {
        //throw new UnsupportedOperationException("Not supported yet.");
        for (int i = 0; i < coordinates.size(); i++) {
            g.drawString(coordinates.get(i).toString(), 20, 5 + 10*i);
        }
    }

}
