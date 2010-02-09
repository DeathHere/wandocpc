package SampleScripts;

/**            RougeCooker by Speed
 *
 *	A Speed's Scripting Production...
 *
 *
 *	Support & help can be found at: http://www.rsbot.org/vb/showthread.php?t=152146
 *
 *
 *	Made on 15th October 2009.
 *	Updated to Version 1.02 on 7th November 2009.
 *
 *	Thanks to Exempt for some variables and IDs (couldn't be bothered to get my own).
 *
 *
 *	Version 1.00 - Script made.
 *	Version 1.01 - Updated antiban.
 *	Version 1.02 - Made more efficient. Redid some methods.
 */

import java.awt.Color;
import java.awt.Graphics;
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
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Speed" }, category = "Cooking", name = "Rouge Cooker", version = 1.02, description = "<html>"
		+ "<head></head><body>"
		+ "<h1> Rouge Cooker by Speed </h1>"
		+ "<br><br>Run in Rouge's Den. Enter ID of Raw material here:"
		+ "<br>Food ID: <input type=\"text\" name=\"foodID\"><br><br>"
		+ "<b> Sponsored by: <a href = http://scapemarket.info>http://scapemarket.info</a></body></html>")
public class RougeCooker extends Script implements PaintListener,
		ServerMessageListener {
	int tries = 0;
	final int BANK_ID = 2271;
	final int FIRE_ID = 2732;
	int antibans;
	long seconds;
	long runTime;
	long minutes;
	long hours;
	int LevelChange;
	int GambleInt;
	int foodCooked = 0;
	long curTime = System.currentTimeMillis();
	int XpGained;
	int startExp = skills.getCurrentSkillExp(STAT_COOKING);
	int startStatLvl = skills.getCurrentSkillLevel(STAT_COOKING);
	RSTile fireTile = new RSTile(3043, 4972);
	public long startTime = System.currentTimeMillis();
	int foodID;
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	public double getVersion() {
		return properties.version();
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		startExp = skills.getCurrentSkillExp(STAT_COOKING);
		startStatLvl = skills.getCurrentSkillLevel(STAT_COOKING);
		startTime = System.currentTimeMillis();
		if (args.get("foodID") != null) {
			foodID = Integer.parseInt(args.get("foodID"));
			return true;
		} else {
			return false;
		}
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

	public boolean useItem(final int item, final RSObject targetObject) {
		if (getCurrentTab() != Constants.TAB_INVENTORY) {
			openTab(Constants.TAB_INVENTORY);
		}
		atInventoryItem(item, "Use");
		return atObject(targetObject, "Fire");
	}

	boolean useFire() {
		if (getMyPlayer().getAnimation() != -1) {
			curTime = System.currentTimeMillis();
		}
		final RSObject range = getNearestObjectByID(FIRE_ID);
		final RSInterfaceChild LOL_AREA = RSInterface.getChildInterface(513, 3);
		if (range != null && !getMyPlayer().isMoving()
				&& System.currentTimeMillis() - curTime > 3500
				&& !LOL_AREA.isValid()) {
			if (useItem(foodID, range)) {
				waitForIface(LOL_AREA, 1000);
			}
		}
		if (LOL_AREA.isValid()) {
			atInterface(LOL_AREA, "All");
		}
		return true;
	}

	public void antiban() {
		if (antibans < 5) {
			GambleInt = random(0, 12);
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
				turnCamera();
				wait(random(500, 1750));
				antibans++;
			}

			if (GambleInt == 9) {
				turnCamera();
				openTab(random(0, 13));
				turnCamera();
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
			if (GambleInt == 9) {
				openTab(random(0, 14));
				turnCamera();
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

	boolean isEmpty() {
		if (getInventoryCount() > 0) {
			return false;
		}
		if (getInventoryCount() == 0) {
			return true;
		}
		return true;
	}

	public void useBank() {
		final RSNPC Banker = getNearestNPCByID(BANK_ID);
		if (Banker != null) {
			if (!RSInterface.getInterface(INTERFACE_BANK).isValid()) {
				final RSTile bankerT = Banker.getLocation();
				if (distanceTo(bankerT) < 3) {
					atNPC(Banker, "Bank");
				}
				if (distanceTo(bankerT) > 3) {
					walkTileMM(bankerT);
				}
			} else {
				if (tries < 3) {
					if (!isEmpty()) {
						bank.depositAll();
					}
					if (!inventoryContains(foodID)) {
						bank.withdraw(foodID, 0);
					}
					antibans = 0;
					bank.close();
					if (!inventoryContains(foodID)) {
						tries++;
					} else {
						tries = 0;
					}
				} else {
					if (!RSInterface.getInterface(INTERFACE_BANK).isValid()) {
						log("Out of food or failed banking more then 5 times.");
						logout();
						stopScript();
					} else {
						bank.close();
						log("Out of food or failed banking more then 5 times.");
						logout();
						stopScript();
					}
				}
			}
		} else {
			wait(random(50, 100));
		}
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String word = e.getMessage().toLowerCase();
		if (word.contains("cook") || word.contains("burn")
				|| word.contains("roast")) {
			foodCooked++;
		}
	}

	@Override
	public int loop() {
		if (getCurrentTab() != Constants.TAB_INVENTORY) {
			openTab(Constants.TAB_INVENTORY);
		}
		if (!isBusy() && inventoryContains(foodID)) {
			useFire();
		}
		if (!isBusy() && !inventoryContains(foodID)) {
			useBank();
		}
		if (isBusy()) {
			antiban();
		}
		return 500;

	}

	public void onRepaint(final Graphics g) {
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

		XpGained = skills.getCurrentSkillExp(STAT_COOKING) - startExp;
		g.setColor(new Color(0, 0, 0, 50));
		g.fillRoundRect(3, 180, 155, 130, 5, 5);

		// Calculate levels gained
		LevelChange = skills.getCurrentSkillLevel(STAT_COOKING) - startStatLvl;

		g.setColor(Color.WHITE);
		g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12,
				216);
		g.drawString("Cooked " + foodCooked + " raw food.", 12, 232);
		g.drawString("XP Gained: " + XpGained, 12, 248);
		g.drawString("Levels Gained: " + LevelChange, 12, 264);
		g.drawString("Percent to next level: "
				+ skills.getPercentToNextLevel(STAT_COOKING), 12, 280);

	}

}