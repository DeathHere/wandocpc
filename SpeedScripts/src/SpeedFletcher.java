
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;
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
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"LightSpeed, Pirateblanc"}, category = "Magic",
name = "SpeedSuperHeat", version = 1.0, description = "")
public class SpeedFletcher extends Script implements PaintListener, ServerMessageListener {

    private boolean recordInitial = false;

    ;
    private int errors = 0;
    private RSTile loc;
    private int errorCounter = 0;
    private int strung = 0;
    private int bowsMade = 0;
    private int logsLeft = 0;
    private int unstrungLeft = 0;
    private int logID = 0;
    private long startTime;
    private boolean logOutDone = false;
    private int startExp;
    private int startLvl;
    private boolean fletch = false;
    private boolean string = false;
    private String woodType = "";
    private String bowType = "";
    private int longbowID = 0;
    private int shortbowID = 0;
    private int bowID = 0;
    private final int sacredKnifeID = 14111;
    private final int knifeID = 946;
    private final int bowstringID = 1777;

    @Override
    public int loop() {
        try {
            if (!isLoggedIn()) {
                Bot.disableRandoms = false;
                return 1000;
            }
            if (recordInitial) {
                if (!initialized()) {
                    log("Error: log identification failure");
                    return -1;
                } else {
                    recordInitial = false;
                }
            }
            if (distanceBetween(loc, getLocation()) > 10) {
                log("Location changed...checking...");
                if (checkForRandoms()) {
                    loc = getLocation();
                    return 1;
                }
            } else {
                Bot.disableRandoms = true;
            }
            antiBan();
            setCameraAltitude(true);
            if (!checkFletch()) {
                if (!bank.open()) {
                    while (!bank.isOpen()) {
                        wait(random(500, 750));
                        errors++;
                        if (errors > 6) {
                            return 1;
                        }
                    }
                }
                errors = 0;
                if ((new BankPins()).runRandom()) {
                    wait(1000);
                }
                if (errorCounter > 6) {
                    return -1;
                }
                if (!deposit()) {
                    Bot.disableRandoms = false;
                    errorCounter++;
                    return 1;
                }
                if (!withdraw()) {
                    Bot.disableRandoms = false;
                    errorCounter++;
                    return 1;
                }
            }
            bank.close();
            if (!fletch()) {
                Bot.disableRandoms = false;
                errorCounter++;
            }
            errorCounter = 0;
            return 500;
        } catch (NullPointerException e) {
            Bot.disableRandoms = false;
            log("Something really fucked up:" + e.getMessage());
            errors++;
            if (errors > 3) {
                log("Sry not my fault blame the chinese guy.");
                return -1;
            }
            return 2000;
        }
    }

    @Override
    public boolean onStart(Map<String, String> map) {
        return super.onStart(map);
    }

    @Override
    public void onFinish() {
        long timeDiff = (System.currentTimeMillis() - startTime) / 1000;
        int hours = (int) ((timeDiff) / 3600);
        int min = (int) ((timeDiff) / 60) - hours * 60;
        log("Script Ran for: " + hours + " hours " + min + " min.");
        log(status());
        Bot.disableRandoms = false;
        if (logOutDone) {
            logout();
        }
    }

    public String status() {
        String s = "";
        s += "Exp Gained: " + (skills.getCurrentSkillExp(Constants.STAT_FLETCHING) - startExp);
        s += " , Lvls Gained: " + (skills.getRealSkillLevel(Constants.STAT_FLETCHING) - startLvl);
        return s;
    }

    public void onRepaint(Graphics render) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        String word = e.getMessage().toLowerCase();
        if (word.contains("cut")) {
            bowsMade++;
            logsLeft--;
        }
        if (word.contains("string")) {
            strung++;
            unstrungLeft--;
        }
        if (word.contains("You've just advanced".toLowerCase())) {
            clickContinue();
        }
    }

    private boolean initialized() {
        startExp = skills.getCurrentSkillExp(Constants.STAT_MAGIC); //save the initial exp and lvl
        startLvl = skills.getRealSkillLevel(Constants.STAT_MAGIC);
        loc = getLocation();
        startTime = System.currentTimeMillis();
        return fletchIntialize();
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

    private void antiBan() {
        switch (random(0, 50)) {
            case 4:
            case 5:
            case 6: {
                openTab(TAB_STATS);
                moveMouse(633, 406, 5, 5);
                wait(random(750, 1000));
                break;
            }
            default: {
                break;
            }
        }
    }

    private boolean checkFletch() {
        return (inventoryContains(logID)
                && inventoryContainsOneOf(knifeID, sacredKnifeID))
                || (inventoryContains(bowID)
                && inventoryContains(bowstringID));
    }

    private boolean fletch() {
        if(!checkFletch())
            return false;
        if (fletch && logsLeft > 0) {
            int[] inventoryArray = getInventoryArray();
            int startItem = -1;
            for (int i = 0; i < 28; i++) {
                if (inventoryArray[i] == bowID) {
                    startItem = i;
                    break;
                }
            }
            Point itemPos = getInventoryItemPoint(startItem);
            if(itemPos.equals(new Point(-1, -1)))
                return false;
            
        } else if (string && unstrungLeft > 0) {
        } else {
            return false;
        }
        return true;
    }

    private boolean deposit() {
        if (!bank.isOpen()) {
            wait(1500);
            if (!bank.open()) {
                wait(1500);
                return false;
            }
        }
        if (fletch && bank.depositAllExcept(knifeID, sacredKnifeID)) {
            return true;
        } else if (string && bank.depositAll()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean withdraw() {
        if (fletch && inventoryContainsOneOf(knifeID, sacredKnifeID)) {
            return withdraw(bowID, 0);
        } else if (string && inventoryEmptyExcept()) {
            int withdrawlFactor = 14;
            int errCount = 0;
            int bow = getInventoryCount(bowID);
            while (bow != withdrawlFactor && bank.isOpen()) {
                if (bow > withdrawlFactor) {
                    bank.deposit(bowID, bow - withdrawlFactor);
                } else if (bow < withdrawlFactor) {
                    withdraw(bow, withdrawlFactor - bow);
                }
                wait(random(700, 1000));
                bow = getInventoryCount(bowID);
                errCount++;
                if (errCount > 3 || isPaused) {
                    return false;
                }
            }
            int bowstring = getInventoryCount(bowstringID);
            while (bowstring != withdrawlFactor && bank.isOpen()) {
                if (bowstring > withdrawlFactor) {
                    bank.deposit(bowstringID, bowstring - withdrawlFactor);
                } else if (bowstring < withdrawlFactor) {
                    withdraw(bowstring, withdrawlFactor - bowstring);
                }
                wait(random(700, 1000));
                bowstring = getInventoryCount(bowstringID);
                errCount++;
                if (errCount > 3 || isPaused) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tries to withdraw an item.
     *
     * 0 is All. 1,5,10 use Withdraw 1,5,10 while other numbers Withdraw X.
     *
     * @param itemID
     *            The ID of the item.
     * @param count
     *            The number to withdraw.
     * @return <tt>true</tt> on success.
     */
    public boolean withdraw(final int itemID, final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count < 0 (" + count + ")");
        }
        if (!bank.isOpen()) {
            return false;
        }
        final RSInterfaceComponent item = bank.getItemByID(itemID);
        if ((item == null) || !item.isValid()) {
            return false;
        }
        final int inventoryCount = getInventoryCount(true);
        switch (count) {
            case 0: // Withdraw All
                atInterface(item, "Withdraw-All");
                break;
            case 1: // Withdraw 1
            case 5: // Withdraw 5
            case 10: // Withdraw 10
                atInterface(item, "Withdraw-" + count);
                break;
            default: // Withdraw x
                if (atInterface(item, false)) {
                    wait(random(600, 900));
                    java.util.ArrayList<String> mactions = getMenuActions();
                    boolean found = false;
                    for (int i = 0; i < mactions.size(); i++) {
                        if (mactions.get(i).equalsIgnoreCase("Withdraw-" + count)) {
                            found = true;
                            atMenu("Withdraw-" + count);
                            break;
                        }
                    }
                    if (!found && atInterface(item, "Withdraw-X")) {
                        wait(random(1000, 1300));
                        Bot.getInputManager().sendKeys("" + count, true);
                    }
                }
                break;
        }
        if ((getInventoryCount(true) > inventoryCount) || (getInventoryCount(true) == 28)) {
            return true;
        }
        return false;
    }

    private boolean fletchIntialize() {
        while (true) {
            if (errors > 10) {
                return false;
            } else if (!inventoryContainsOneOf(sacredKnifeID, knifeID)) {
                break;
            } else {
                errors++;
            }
            wait(random(500, 750));
        }
        errors = 0;
        if (woodType.equals("Wood")) {
            logID = 1511;
            longbowID = 48;
            shortbowID = 50;
        } else if (woodType.equals("Oak")) {
            logID = 1521;
            longbowID = 56;
            shortbowID = 54;
        } else if (woodType.equals("Willow")) {
            logID = 1519;
            longbowID = 58;
            shortbowID = 60;
        } else if (woodType.equals("Maple")) {
            logID = 1517;
            longbowID = 62;
            shortbowID = 64;
        } else if (woodType.equals("Yew")) {
            logID = 1515;
            longbowID = 66;
            shortbowID = 68;
        } else if (woodType.equals("Magic")) {
            logID = 1515;
            longbowID = 66;
            shortbowID = 68;
        } else {
            return false;
        }
        if (bowType.equals("Shortbow")) {
            bowID = shortbowID;
        } else if (bowType.equals("Longbow")) {
            bowID = longbowID;
        } else {
            return false;
        }
        return true;
    }
}
