import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Collections;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"LightSpeed, Pirateblanc"}, category = "Aaaaaaa",
name = "SpeedPlankMake", version = 1.0, description = "<html><head>" +
        "<style type=\"text/css\">" +
        "body {" +
        "   color: #FFFFFF;" +
        "   background-color: #000000;" +
        "}" +
        "</style>" +
        "</head>" +
        "<body>" +
        "<center>Created by LightSpeed & Pirateblanc</center>" +
        "" +
        "</body>" +
        "</html>")
        
public class SpeedPlankMake extends Script implements PaintListener {

    /** Script vars */
    private int coinsID = 995;
    private int mahLogID = 6332;
    private int mahPlankID = 8782;
    private int runEnergy = random(40, 95);

    private int tolerance = 3;
    private RSTile walkToPath[] = {
        new RSTile(3253, 3421), // 0
        new RSTile(3260, 3428), // 1
        new RSTile(3269, 3428), // 2
        new RSTile(3276, 3430), // 3
        new RSTile(3275, 3439), // 4
        new RSTile(3282, 3444), // 5
        new RSTile(3285, 3456), // 6
        new RSTile(3295, 3462), // 7
        new RSTile(3295, 3470), // 8
        new RSTile(3300, 3479), // 9
        new RSTile(3302, 3489), // 10
    };
    private RSTile walkBackPath[] = {
        new RSTile(3302, 3489), // 10
        new RSTile(3300, 3479), // 9
        new RSTile(3295, 3470), // 8
        new RSTile(3295, 3462), // 7
        new RSTile(3285, 3456), // 6
        new RSTile(3282, 3444), // 5
        new RSTile(3275, 3439), // 4
        new RSTile(3276, 3430), // 3
        new RSTile(3269, 3428), // 2
        new RSTile(3260, 3428), // 1
        new RSTile(3253, 3421), // 0
    };
    int curTile = 0;

    public enum State {
        walkTo, walkBack, operate, bank, running, error;
    }

    public void antiBan() {
        final int random = random(1, 24);

        switch (random) {
            case 1:
                if (random(1, 3) == 1) {
                    moveMouseRandomly(300);
                }
                return;

            case 2:
                if (random(1, 10) == 5) {
                    if (getCurrentTab() != Constants.TAB_INVENTORY) {
                        openTab(Constants.TAB_INVENTORY);
                    }
                }
                return;

            case 3:
                /**
                if (random(1, 20) == 10) {
                    int angle = getCameraAngle() + random(-90, 90);
                    if (angle < 0) {
                        angle = 0;
                    }
                    if (angle > 359) {
                        angle = 0;
                    }
                    setCameraRotation(angle);
                }*/
                return;
            default:
                return;
        }
    }

    public void doBank() {
        log("Banking right now");
    }

    public void makePlanks() {
        log("Making planks");
    }

    public void walk(int num) {
        
        if (num == 1) {
            if (walkTileMM(walkToPath[curTile])) {
                curTile++;
            }
        }
        else if (num == 2) {
            if (walkTileMM(walkBackPath[curTile])) {
                curTile++;
                
            }
        }
    }

    public State getState() {
        if (inventoryContains(mahPlankID) &&
                distanceBetween(walkToPath[0], getLocation()) <= tolerance) {
            curTile = 0;
            return State.bank;
        }
        if (inventoryContains(mahLogID) &&
                distanceBetween(walkToPath[6], getLocation()) <= tolerance) {
            curTile = 0;
            return State.operate;
        }
        if (getInventoryCount(coinsID) >= getInventoryCount(mahLogID) * 1500
                && inventoryContains(mahLogID)) {
            return State.walkTo;
        }

        return State.walkBack;
    }

    @Override
    public int loop() {
        if (!isLoggedIn()) {
            return 1000;
        }

        setCameraAltitude(true);
        startRunning(runEnergy);

        switch (getState()) {
            case walkTo:
                walk(1);
                return 1000;
            case operate:
                makePlanks();
                return 1000;
            case walkBack:
                walk(2);
                return 1000;
            case bank:
                doBank();
                return 1000;
            case error:
                return -1;
        }

        return 50;
    }

    @Override
    public void onFinish() {
        Bot.getEventManager().removeListener(PaintListener.class, this);
    }

    public void onRepaint(final Graphics g) {
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
    }

    @Override
    public boolean onStart(final Map<String, String> args) {


        return true;
    }

    public void startRunning(final int energy) {
        if (getEnergy() >= energy && !isRunning()) {
            runEnergy = random(40, 95);
            setRun(true);
            wait(random(500, 750));
        }
    }


}
