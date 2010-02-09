
import java.awt.Point;
import java.util.Map;
import javax.swing.JOptionPane;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Random;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.randoms.antiban.BreakHandler;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"LightSpeed"}, category = "Magic", name = "SpeedHighAlch", version = 1.0, description = "<html><head></head><body>Have items in inventory in noted form with item ID ready. \n This script will keep using alchemy until 10 items or less remain. \n Paint will be added later.</body></html>")
public class SpeedHighAlch extends Script implements ServerMessageListener {

    private int startExp = 1;
    private int startLvl = 1;
    private int alchItemID = 856;
    private Point itemPos = null;
    private boolean recordInitial = false;
    public long startTime = System.currentTimeMillis();
    public int errorCounter = 0;
    RSItem[] invItems = null;
    RSTile loc = null;

    private boolean checks() {
        if (recordInitial) {
            startExp = skills.getCurrentSkillExp(Constants.STAT_MAGIC);
            startLvl = skills.getCurrentSkillLevel(Constants.STAT_MAGIC);
            itemPos = getItemPos();
            recordInitial = false;
            errorCounter = 0;
            loc = getLocation();
        }
        if (distanceBetween(loc, getLocation()) > 10) {
            if (checkForRandoms()) {
                loc = getLocation();
            }
            return false;
        } else {
            Bot.disableRandoms = true;
        }
        antiBan();
        if (checkItemAmount() <= 10) {
            log("Items amount is <= 10. Stopping in 5-10 seconds.");
            wait(random(4500, 10500));
            stopScript();
        }
        return true;
    }

    private int checkItemAmount() {
        if (invItems == null) {
            invItems = getInventoryItems();
        }
        for (RSItem i : invItems) {
            if (i.getID() == alchItemID) {
                return i.getStackSize();
            }
        }
        return 0;
    }

    private boolean alchemy() {
        if (getCurrentTab() != Constants.TAB_MAGIC
                && !RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()
                && !RSInterface.getInterface(Constants.INTERFACE_STORE).isValid()) {
            openTab(Constants.TAB_MAGIC);
        }
        Bot.disableRandoms = true;
        castSpell(Constants.SPELL_HIGH_LEVEL_ALCHEMY);
        while (getCurrentTab() != Constants.TAB_INVENTORY) {
            wait(random(50, 150));
        }
        while (itemPos.equals(new Point(-1, -1))) {
            checks();
            errorCounter++;
            if (errorCounter >= 30) {
                log("Can't find item...we fail...");
                return false;
            }
        }
        if (random(0, 10) == 5) {
            invItems = getInventoryItems();
        }
        moveMouse(itemPos.x + 10, itemPos.y + 10, 5, 5);
        if (getMenuActions().get(0).contains("Cast")) {
            clickMouse(true);
            return true;
        } else {
            return atMenu("Cast");
        }
    }

    public Point getItemPos() {
        final int[] items = getInventoryArray();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == alchItemID) {
                //log("Item in slot: " + i);
                return getInventoryItemPoint(i);
            }
        }
        return (new Point(-1, -1));
    }

    public void antiBan() {
        switch (random(0, 50)) {
            case 1:
            case 2: {
                setCameraRotation(random(1, 270));
                break;
            }
            case 3:
            case 4:
            case 5:
            case 6: {
                openTab(TAB_STATS);
                //log("Opening stats page.");
                moveMouse(578, 405);
                wait(3500);
                break;
            }
            case 20: {
                itemPos = getItemPos();
                //log("Item found at: " + itemPos.x + " , " + itemPos.y);
                break;
            }
            default: {
                break;
            }
        }
    }

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

    @Override
    public int loop() {
        if (RSInterface.getInterface(399).isValid() || !isLoggedIn() || skills.getRealSkillLevel(Constants.STAT_MAGIC) <= 54) {
            return random(900, 1000);
        }
        if (recordInitial || random(0, 10) == 5) {
            if (!checks()) {
                recordInitial = true;
                return 10000;
            }
        }
        alchemy();
        return 1500;
    }

    @Override
    public boolean onStart(Map<String, String> map) {
        alchItemID = Integer.parseInt(JOptionPane.showInputDialog("Enter the Id of the item to alchemize", 856));
        log("Item ID: " + alchItemID);
        if (alchItemID < 0) {
            return false;
        }
        recordInitial = true;
        Bot.disableRandoms = false;
        return true;
    }

    @Override
    public void onFinish() {
        long timeDiff = (System.currentTimeMillis() - startTime) / 1000;
        int hours = (int) ((timeDiff) / 3600);
        int min = (int) ((timeDiff) / 60) - hours * 60;
        log("Script Ran for: " + hours + " hours " + min + " min.");
        log(status());
    }

    public String status() {
        String s = "";
        s += "Exp Gained: " + (skills.getCurrentSkillExp(Constants.STAT_MAGIC) - startExp);
        s += "Lvls Gained: " + (skills.getRealSkillLevel(Constants.STAT_MAGIC) - startLvl);
        return s;
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        String messageEvent = e.getMessage();
        if (messageEvent.contains("You do not have enough Fire Runes")) {
            log("No Fire Runes - Shutting Down in 5-10 Seconds");
            wait(random(4500, 10500));
            stopScript();
        } else if (messageEvent.contains("You do not have enough Nature Runes")) {
            log("No Nature Runes - Shutting Down in 5-10 Seconds");
            wait(random(4500, 10500));
            stopScript();
        }
    }

    @Override
    protected int getMouseSpeed() {
        return random(8, 12);
    }

    public boolean isDoingSomething() {
        if (getMyPlayer().isMoving()) {
            return true;
        }
        if (isPaused) {
            return true;
        }
        return false;
    }
}
