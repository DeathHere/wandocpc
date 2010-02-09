package SampleScripts;

/**	       AIOStringer by Speed
 *
 *	A Speed's Scripting Production...
 *
 *	Support & help can be found at: http://www.rsbot.org/vb/showthread.php?t=134704
 *
 *	Made on 19th September 2009.
 *	Updated to Version 1.25 on 22nd November 2009.
 *
 *	Thanks to Dave, for letting me use some of his paint. (I disliked the one that I used in the Super Series).
 *	Thanks to Durka Durka Mahn for update system.
 *
 *	Version 1.26 - Fixed some misclicks, almost flawless!
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;

@ScriptManifest(authors = { "Speed" }, category = "Fletching", name = "AIO Stringer", version = 1.26, description = "<html>"
		+ "<head></head><body>"
		+ "<font color='red'><h1><center><b>AIO Stringer</b></h1><br></font><font size='4'><center>By Speed<br><br></center></center></h2><br></font>"
		+ "<center><b><font size ='3'>What type of bow would you like to string?:</b><br>"
		+ "<select name='whatFletching'><option>Shortbow</option><option>Longbow</option><option>Oak shortbow</option><option>Oak longbow</option><option>Willow shortbow</option><option>Willow longbow</option><option>Maple shortbow</option><option>Maple longbow</option><option>Yew shortbow</option><option>Yew longbow</option><option>Magic shortbow</option><option>Magic longbow</option></select><br></font>"
		+ "<font size ='4'><b><br> Speed's Scripting: <a href = http://scapemarket.info/blog> http://scapemarket.info/blog</a>.<br></b><br><b>Works at almost ANY bank!</b><br><Run in SD and fixed screen<br>Thanks for using AIOStringer by Speed.</center></font></body></html\n")
public class AIOStringer extends Script implements PaintListener,
		ServerMessageListener {
	int ubowID;
	int bowID;
	int amountFletched;
	long startTime;
	int startLevel;
	int startXp;
	int currentXp;
	int currentLevel;
	int gainedXp;
	int GambleInt;
	int randomInt;
	long hours;
	int amountneeded = 1000;
	long minutes;
	boolean runOnceDone = false;
	int antibans;
	int expGained;
	long seconds;
	long runTime;
	int tries;
	int antibanInt;
	int levelsGained;
	final int bowstring = 1777;
	final Point STRING_BOW = new Point(260, 420);
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	public double getVersion() {
		return properties.version();
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		startXp = skills.getCurrentSkillExp(Constants.STAT_FLETCHING);
		startLevel = skills.getCurrentSkillLevel(Constants.STAT_FLETCHING);
		amountFletched = 0;
		if (args.get("whatFletching").equals("Shortbow"))// by speed
		{
			ubowID = 50;
		}
		if (args.get("whatFletching").equals("Longbow"))// by speed
		{
			ubowID = 48;
		}
		if (args.get("whatFletching").equals("Oak shortbow")) {
			ubowID = 54;
		}
		if (args.get("whatFletching").equals("Oak longbow")) {
			ubowID = 56;
		}
		if (args.get("whatFletching").equals("Willow shortbow")) {
			ubowID = 60;
		}
		if (args.get("whatFletching").equals("Willow longbow")) {
			ubowID = 58;
		}
		if (args.get("whatFletching").equals("Maple shortbow")) {
			ubowID = 64;
		}
		if (args.get("whatFletching").equals("Maple longbow")) {
			ubowID = 62;
		}
		if (args.get("whatFletching").equals("Yew shortbow")) {
			ubowID = 68;
		}
		if (args.get("whatFletching").equals("Yew longbow")) {
			ubowID = 66;
		}
		if (args.get("whatFletching").equals("Magic shortbow")) {
			ubowID = 72;
		}
		if (args.get("whatFletching").equals("Magic longbow")) {
			ubowID = 70;
		}
		startTime = System.currentTimeMillis();
		log("Thanks for using AIOStringer, you have version "
				+ properties.version() + ".");
		return true;
	}

	@Override
	public void onFinish() {
		log("Bows Strung: " + amountFletched + ".");
		log("Time Taken: " + hours + ":" + minutes + ":" + seconds + ".");
		log("Thanks for using AIOStringer by Speed.");
	}

	@Override
	protected int getMouseSpeed() {
		return random(4, 6);
	}

	boolean closeBank() {
		final RSInterfaceChild closebutton = RSInterface.getChildInterface(
				INTERFACE_BANK, INTERFACE_BANK_BUTTON_CLOSE);
		if (closebutton.isValid()) {
			atInterface(closebutton);
			return !bank.isOpen();
		}
		return closebutton.isValid();
	}

	public boolean isBusy() {
		boolean flag = false;
		for (int i = 0; i < 4; i++) {
			if (getMyPlayer().getAnimation() != -1) {
				flag = true;
				break;
			}
			wait(random(200, 250));
		}
		return flag;
	}

	private boolean clickInventoryItem(final int itemID, final boolean click) { // Unknown
		// author
		if (getCurrentTab() != TAB_INVENTORY
				&& !RSInterface.getInterface(INTERFACE_BANK).isValid()
				&& !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
			openTab(TAB_INVENTORY);
		}
		final int[] items = getInventoryArray();
		int slot = -1;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == itemID) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return false;
		}
		final Point t = getInventoryItemPoint(slot);
		clickMouse(t, 5, 5, click);
		return true;
	}

	void fletching() {
		final RSInterfaceChild FLETCH_AREA = RSInterface.getChildInterface(513,
				3);
		if (!isBusy() && inventoryContains(ubowID)
				&& inventoryContains(bowstring)) {
			if (!FLETCH_AREA.isValid()) {
				wait(random(300, 900));
				atInventoryItem(ubowID, "Use");
				wait(random(300, 900));
				atInventoryItem(bowstring, "Use");
				wait(random(300, 900));
			}
			if (FLETCH_AREA.isValid()) {
				if (!atInterface(FLETCH_AREA, "Make All")) {
					clickInventoryItem(bowstring, true);
				}
			}
		}
	}

	boolean isEmpty() {
		if (getInventoryCount() > 0) {
			return false;
		}
		if (getInventoryCount() == 0) {
			return true;
		}
		return true;
	}

	boolean withdrawal(final int item, final int amount) {
		if (bank.isOpen()) {
			if (!isThere(item)) {
				if (!bank.atItem(item, "" + amount)) {
					bank.atItem(item, "X");
					wait(random(600, 900));
					sendText("" + amount, true);
					return true;
				}
			} else {
				return false;
			}
		}
		return false;
	}

	boolean isThere(final int item) {
		return getInventoryCount(item) != 0 && getInventoryCount(item) != 0;
	}

	void banking() {
		if (bank.isOpen()) {
			if (!isEmpty()) {
				bank.depositAll();
			}
			if (!isThere(ubowID) && isEmpty()) {
				amountneeded = bank.getCount(ubowID);
				wait(random(1000, 1300));
				if (!isThere(ubowID)) {
					withdrawal(ubowID, 14);
				}
			}
			if (!isThere(bowstring) && isEmpty()) {
				wait(random(1000, 1300));
				if (!isThere(bowstring)) {
					withdrawal(bowstring, 14);
				}
			}
			if (getInventoryCount(ubowID) > 14
					|| getInventoryCount(bowstring) > 14) {
				bank.depositAll();
			}
		}
	}

	@Override
	public int loop() {
		final RSInterfaceChild FLETCH_ARE = RSInterface.getChildInterface(513,
				3);
		if (amountneeded < 20) {
			log("Finished script, cannot find more than 20 unstrungs in bank.");
			stopScript();
			logout();
		}
		if (!inventoryContains(ubowID) && !inventoryContains(bowstring)
				&& !isBusy() && !bank.isOpen()) {
			bank.open();
		}
		if (bank.isOpen() && !inventoryContains(bowstring)
				&& !inventoryContains(ubowID)) {
			banking();
		}
		if (inventoryContains(ubowID) && inventoryContains(bowstring)
				&& getInterface(Constants.INTERFACE_BANK).isValid()) {
			closeBank();
		}
		if (inventoryContains(ubowID) && inventoryContains(bowstring)
				&& !isBusy() && !bank.isOpen()) {
			tries = 0;
			fletching();
		}
		if (isBusy()) {
			antiban();
		}
		if (inventoryContains(ubowID) && !inventoryContains(bowstring)
				&& !bank.isOpen() || !bank.isOpen()
				&& inventoryContains(bowstring) && !inventoryContains(ubowID)) {
			bank.open();
		}
		if (inventoryContains(ubowID) && !inventoryContains(bowstring)
				&& bank.isOpen()) {
			withdrawal(bowstring, 14);
		}
		if (!inventoryContains(ubowID) && inventoryContains(bowstring)
				&& bank.isOpen()) {
			withdrawal(ubowID, 14);
		}
		if (Bot.getClient().isItemSelected() == bowstring
				&& FLETCH_ARE.isValid()
				|| Bot.getClient().isItemSelected() == ubowID
				&& FLETCH_ARE.isValid()) {
			clickInventoryItem(Bot.getClient().isItemSelected(), true);
			wait(random(300, 900));
		}
		return 100;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String word = e.getMessage().toLowerCase();
		if (word.contains("string")) {
			amountFletched++;
			amountneeded--;
		}
	}

	public void onRepaint(final Graphics g) { // needs updating, looks nerdy.
		runTime = System.currentTimeMillis() - startTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}

		expGained = skills.getCurrentSkillExp(STAT_FLETCHING) - startXp;
		g.setColor(new Color(0, 0, 0, 50));
		g.fillRoundRect(3, 180, 155, 130, 5, 5);

		// Calculate levels gained
		levelsGained = skills.getCurrentSkillLevel(STAT_FLETCHING) - startLevel;

		g.setColor(Color.WHITE);
		g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12,
				216);
		g.drawString("Strung " + amountFletched + " bows.", 12, 232);
		g.drawString("XP Gained: " + expGained, 12, 248);
		g.drawString("Levels Gained: " + levelsGained, 12, 264);
		g.drawString("Percent to next level: "
				+ skills.getPercentToNextLevel(STAT_FLETCHING), 12, 280);
		g.drawString("Amount left to fletch: " + amountneeded, 12, 296);

	}

	public void antiban() {
		antibanInt = random(2, 7);
		if (antibans < antibanInt) {
			GambleInt = random(1, 11);
			if (GambleInt == 1) {
				turnCamera();
				antibans++;
			}

			if (GambleInt == 2) {
				final int xA = random(0, 750);
				final int yA = random(0, 500);
				moveMouse(xA, yA);
				turnCamera();
				antibans++;
			}

			if (GambleInt == 3) {
				if (getCurrentTab() != Constants.TAB_INVENTORY) {
					openTab(Constants.TAB_INVENTORY);
					turnCamera();
					antibans++;
				}
			}

			if (GambleInt == 4) {
				clickCharacter(getNearestPlayerByLevel(1, 130), "Cancel");
				wait(random(500, 1750));
				antibans++;
			}

			if (GambleInt == 9) {
				turnCamera();
				openTab(random(0, 13));
				wait(random(1000, 1200));
				turnCamera();
				antibans++;
			}

			if (GambleInt == 10) {
				turnCamera();
				moveMouse(random(0, 450), random(0, 450));
				antibans++;
			}
			if (GambleInt == 5) {
				turnCamera();
				final int xA = random(0, 750);
				final int yA = random(0, 500);
				moveMouse(xA, yA);
				antibans++;
			}

			if (GambleInt == 6) {
				turnCamera();
				antibans++;
			}

			if (GambleInt == 7) {
				openTab(random(0, 13));
				antibans++;
			}

			if (GambleInt == 8) {
				moveMouse(random(0, 450), random(0, 450));
				antibans++;
			}
		}
	}

	// credits WarXperiment
	public void turnCamera() {
		final char[] LR = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT };
		final char[] UD = new char[] { KeyEvent.VK_UP, KeyEvent.VK_DOWN };
		final char[] LRUD = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
				KeyEvent.VK_UP, KeyEvent.VK_DOWN };
		final int random2 = random(0, 2);
		final int random1 = random(0, 2);
		final int random4 = random(0, 4);

		if (random(0, 3) == 0) {
			Bot.getInputManager().pressKey(LR[random1]);
			try {
				wait(random(100, 400));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().pressKey(UD[random2]);
			try {
				wait(random(300, 600));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(UD[random2]);
			try {
				wait(random(100, 400));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(LR[random1]);
		} else {
			Bot.getInputManager().pressKey(LRUD[random4]);
			if (random4 > 1) {
				try {
					wait(random(300, 600));
				} catch (final Exception ignored) {
				}
			} else {
				try {
					wait(random(500, 900));
				} catch (final Exception ignored) {
				}
			}
			Bot.getInputManager().releaseKey(LRUD[random4]);
		}
	}
}