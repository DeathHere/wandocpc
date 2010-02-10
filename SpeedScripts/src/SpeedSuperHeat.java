
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;
import javax.swing.JOptionPane;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Random;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.randoms.BankPins;
import org.rsbot.script.randoms.antiban.BreakHandler;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSTile;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Sunny
 */
@ScriptManifest(authors = {"LightSpeed, Pirateblanc"}, category = "Magic",
        name = "SpeedSuperHeat", version = 1.0, description = "<html><head>" +
        "<style type=\"text/css\">" +
        "body {" +
        "   color: #FFFFFF;" +
        "   background-color: #000000;" +
        "}" +
        "select {" +
        "   margin-left: 10px;" +
        "   padding: 100px;" +
        "   background: #000000;" +
        "   color: #FFFFFF;" +
        "}" +
        "</style>" +
        "</head>" +
        "<body>" +
        "<center>" +
        "<img src=\"http://wandohigh.com/clubs/cpc/website/files/rsbot-logo.jpg\"" +
        "alt=\"SpeedSuperHeat\">" +
        "</center>" +
        "<hr><br>" +
        "<center>Created by LightSpeed & Pirateblanc</center>" +
        "<hr>" +
        "<form>" +
        "Select Your Ore: " +
        "<select name=\"ore\">" +
        "   <option>Runite<option>Adamantite<option>Mithril<option>Gold" +
        "   <option>Silver<option>Steel<option>Iron" +
        "</select>" +
        "<br>" +
        "Logout On Crash? <input type=\"checkbox\" name=\"logout\" value=\"true\">" +
        "<br>" +
        "Lag Time For Banking (sec): <select name=\"lag\">" +
        "   <option>0.5<option>1.0<option>1.5<option>2.0" +
        "</input>" +
        "</form>" +
        "</body>" +
        "</html>")

public class SpeedSuperHeat extends Script implements ServerMessageListener, PaintListener {

    private int oreID;
    private boolean recordInitial = false;
    private long startTime;
    private int startExp;
    private int startLvl;
    private RSTile loc;
    private int coalRatio = 0;
    private final int coalID = 453;
    private int barID = 0;
    private int errorCounter = 0;
    private boolean logOutDone = false;
    private double lagFactor = 1.0;

    public boolean initialized() {
        startExp = skills.getCurrentSkillExp(Constants.STAT_MAGIC); //save the initial exp and lvl
        startLvl = skills.getRealSkillLevel(Constants.STAT_MAGIC);
        loc = getLocation();
        loc = new RSTile(loc.getX(), loc.getY()); //save initial location
        startTime = System.currentTimeMillis();
        return oreInitialize(); //get coal ratio
    }

    public boolean oreInitialize() {
        switch (barID) {
            case 2363: //runite
            {
                oreID = 451;
                coalRatio = 8;
                break;
            }
            case 2361: //adamantite
            {
                oreID = 449;
                coalRatio = 6;
                break;
            }
            case 2359: //mithril
            {
                oreID = 447;
                coalRatio = 4;
                break;
            }
            case 2357: //gold
            {
                oreID = 444;
                coalRatio = 0;
                break;
            }
            case 2355: //silver
            {
                oreID = 442;
                coalRatio = 0;
                break;
            }
            case 2353: //steel
            {
                oreID = 440;
                coalRatio = 2;
                break;
            }
            case 2351: //iron
            {
                oreID = 440;
                coalRatio = 0;
                break;
            }
            default: {
                log("Invalid ID. Please try again...");
                stopScript();
                return false;
            }
        }
        return true;
    }

    public boolean superHeat() {
        if (getCurrentTab() != Constants.TAB_MAGIC
                && !RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()
                && !RSInterface.getInterface(Constants.INTERFACE_STORE).isValid()) {
            openTab(Constants.TAB_MAGIC);
        }
        Bot.disableRandoms = true;
        while (true) { //ends when you can't find the item
            if (!castSpell(Constants.SPELL_SUPERHEAT_ITEM)) //cast the spell
            {
                return false;
            }
            int waitCheck = 0;
            while (getCurrentTab() != Constants.TAB_INVENTORY) { //wait for the inventory to open
                wait(random(50, 150));
                if (waitCheck > 100) {
                    wait(750);
                    moveMouse(578, 405);
                    wait(150);
                    clickMouse(true);
                    return false;
                }
                waitCheck++;
            }
            int[] inventoryArray = getInventoryArray(); //get the last position of the ore
            int startItem = -1;
            for (int i = 27; i > 0; i--) {
                if (inventoryArray[i] == oreID) {
                    startItem = i;
                    break;
                }
            }
            Point itemPos = getInventoryItemPoint(startItem);
            if (itemPos.equals(new Point(-1, -1))) { //end method no more items
                wait(750);
                moveMouse(578, 405, 10, 10);
                wait(150);
                clickMouse(true); //cast spell at empty space
                return true;
            } else {
                do {
                    if (waitCheck > 10) {
                        return false;
                    }
                    wait(random(500, 750));
                    moveMouse(itemPos.x + 10, itemPos.y + 10, 5, 5); //mouse mouse to ore
                    if (getMenuActions().get(0).contains("Cast")) { //if first option cast then cast
                        wait(random(500, 750));
                        if (getCurrentTab() == Constants.TAB_MAGIC) {
                            break;
                        } else if (getInventoryCount(oreID) <= 1) {
                            clickMouse(true);
                            return true;
                        } else {
                            clickMouse(true);
                        }
                    } else {
                        atMenu("Cast"); //otherwise open menu and cast
                    }
                    wait(random(1250, 1500));
                    waitCheck++;
                } while (getCurrentTab() != Constants.TAB_MAGIC);
            }
        }

    }

    public boolean withdraw() {
        if (!bank.isOpen()) {
            wait(1000);
            if (!bank.open()) {
                wait(1000);
                log("Error: can't open bank");
                return false;
            }
        }
        int counter = 0; //to find maximum withdrawl size
        int[] inventoryArray = getInventoryArray();
        for (int item : inventoryArray) {
            if (item == -1) {
                counter++;
            }
        }
        int withdrawlFactor = counter / (coalRatio + 1); //how many ores can we withdraw
        if (bank.getCount(oreID) < withdrawlFactor + 1) {
            log("Error: out of ores");
            stopScript();
            return false;
        }
        if (bank.getCount(coalID) < coalRatio * withdrawlFactor + 1) {
            log("Error: out of coal");
            stopScript();
            return false;
        }
        bank.withdraw(oreID, withdrawlFactor);
        wait(random(750, 1250));
        if (coalRatio > 0 && oreID == 440) {
            bank.withdraw(coalID, withdrawlFactor);//withdraw coal
            bank.withdraw(coalID, withdrawlFactor);
        } else if (coalRatio > 0) {
            bank.withdraw(coalID, withdrawlFactor * coalRatio);
        }
        wait(1500);
        if (getInventoryCount(coalID) < withdrawlFactor * coalRatio) {
            log("Ores counted: " + getInventoryCount(oreID));
            log("Coal counted: " + getInventoryCount(coalID));
            log("Withdrawl neccessary: " + withdrawlFactor);
            return false;
        }
        return true;
    }

    public boolean deposit() {
        if (!bank.isOpen()) {
            wait(1000);
            if (!bank.open()) {
                wait(1000);
                log("Error: can't open bank");
                return false;
            }
        }
        if (bank.depositAllExcept(561, 554)) { //deposit all but runes
            return true;
        } else {
            log("Error: depositing items problem");
            return false;
        }
    }

    public boolean checkOres() {
        int coal = getInventoryCount(coalID);
        int ore = getInventoryCount(oreID);
        if (ore == 0) {
            return false;
        } else if (coal / ore == coalRatio) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int loop() {
        if (!isLoggedIn()) {
            Bot.disableRandoms = false;
            wait(1000);
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
            }
        } else {
            Bot.disableRandoms = true;
        }
        antiBan();
        setCameraAltitude(true);
        if (!checkOres()) {
            bank.open();
            wait(2000);
            if ((new BankPins()).runRandom()) {
                wait(1000);
            }
            if (errorCounter > 10) {
                return -1;
            }
            if (!deposit()) {
                Bot.disableRandoms = false;
                return 1;
            }
            if (!withdraw()) {
                Bot.disableRandoms = false;
                return 1;
            }
        }
        bank.close();
        if (!superHeat()) {
            Bot.disableRandoms = false;
        }
        errorCounter = 0;
        return 500;
    }

    @Override
    public boolean onStart(Map<String, String> map) {

        /** Reading html inputs */
        lagFactor = Double.parseDouble(map.get("lag"));
        log("Lag Factor: " + lagFactor);

        logOutDone = (map.get("logout") != null) ? true : false ;
        log("Logout Debug: " + logOutDone);

        String ore = map.get("ore");
        if (ore.equalsIgnoreCase("runite")) {
            barID = 2363;
        }
        else if (ore.equalsIgnoreCase("adamantite")) {
            barID = 2361;
        }
        else if (ore.equalsIgnoreCase("mithril")) {
            barID = 2359;
        }
        else if (ore.equalsIgnoreCase("gold")) {
            barID = 2357;
        }
        else if (ore.equalsIgnoreCase("silver")) {
            barID = 2355;
        }
        else if (ore.equalsIgnoreCase("steel")) {
            barID = 2353;
        }
        else if (ore.equalsIgnoreCase("iron")) {
            barID = 2351;
        }
        log("Item ID: " + barID);
        if (barID <= 0) {
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
        if(logOutDone)
        {
            logout();
        }
    }

    public String status() {
        String s = "";
        s += "Exp Gained: " + (skills.getCurrentSkillExp(Constants.STAT_MAGIC) - startExp);
        s += " , Lvls Gained: " + (skills.getRealSkillLevel(Constants.STAT_MAGIC) - startLvl);
        return s;
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
                //itemPos = getItemPos();
                //log("Item found at: " + itemPos.x + " , " + itemPos.y);
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected int getMouseSpeed() {
        return (int) (random(10, 12) * lagFactor);
    }

    @Override
    public void wait(int toSleep) {
        super.wait((int)(toSleep * lagFactor));
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
        } else if(messageEvent.contains("You need a smithing"))
        {
            log("Your Smithing level is too low for this bar - Shutting Down in 5-10 Seconds");
            wait(random(4500, 10500));
            stopScript();
        } else if(messageEvent.contains("Your Magic level") && skills.getCurrentSkillLevel(Constants.STAT_MAGIC) < 43)
        {
            log("Your Magic level is too low for this spell - Shutting Down in 5-10 Seconds");
            wait(random(4500, 10500));
            stopScript();
        }
    }

    public void onRepaint(Graphics render) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public int input(Map<String, String> map) {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Enter the bar id for super heat", 2353));
        return id; //return the barID from the user. Check oreInitialize() for ids to make menu.
    }
}
