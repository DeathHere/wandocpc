package SampleScripts;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Afflicted H4x"}, category = "Combat", name = "Yak Attack", version = 1.5, description = "<html><body bgcolor = Black><font color = White><center><h2> WupDummyCurser</h2>"
		+ "<h2>" + "Yak Attack" + " version 1.5</h2><br>\n"
		+ "Author: " + "Afflicted H4x" + "<br><br>\n"
		+ "Start at the Yaks on Neitiznot"
		+ "<br>Based off of xX Nicole Xx's Cow Own3r."
		+ "<br>We do not guarantee no-bans."
		+ "<br>Use your own Anti-Ban with this script."
		+ "<br>Enter the ID of food Or just leave blank if you Don't Want to eat<br>"
		+ "ID of food: <input type=\"text\" name=\"eatsies\"><br>")
public class YakAttack extends Script implements PaintListener {

	private final int KILLYAKS = 0;
	private final int KILLSCRIPT = 1;
	private int action = 0;

	private final int[] YAKID = {5529};
	private final int[] BRONZEKNIFE = {864};

	public int hpexp;
	public int hpExp;
	int checkTime;
	public int startLevel = 0;
	public int startXP = 0;
	final int XPChange = skills.getCurrentSkillExp(14) - startXP;
	final int LevelChange = skills.getCurrentSkillLevel(14) - startLevel;
	public int atkexp;
	public int atkExp;
	public int defexp;
	public int defExp;
	public long starttime;
	public long startTime = System.currentTimeMillis();
	public int strexp;
	public int strExp;
	public int rangedexp;
	public int rangedExp;
	public int startatkExp;
	public int startdefExp;
	public int starthpExp;
	public int startrangedExp;
	public int startstrExp;
	public int oldatkExp;
	public int olddefExp;
	public int oldhpExp;
	public int oldrangedExp;
	public int oldstrExp;
	public long time = System.currentTimeMillis();
	int hour;
	public long hours;
	int minute;
	public long minutes;
	int second;
	public long seconds;

	private final RSTile YakTile = new RSTile(2324, 3792);

	public boolean wants2Eat;

	public int food;

	public void checkEat() {
		final int cHealth = skills
				.getCurrentSkillLevel(Constants.STAT_HITPOINTS);
		final int randomInt = random(27, 30);
		if (cHealth <= randomInt) {
			atInventoryItem(food, "Eat");

		}

	}

	private boolean clickNPC(final RSNPC npc, final String action) {
		if (npc == null) {
			return false;
		}
		final RSTile tile = npc.getLocation();
		if (!tile.isValid()) {
			return false;
		}

		try {
			Point screenLoc = npc.getScreenLocation();
			if (distanceTo(tile) > 6 || !pointOnScreen(screenLoc)) {
				turnToTile(tile);
			}
			if (distanceTo(tile) > 20) {
				walkTileMM(tile);
				return false;
			}
			for (int i = 0; i < 12; i++) {
				screenLoc = npc.getScreenLocation();
				if (!npc.isValid() || !pointOnScreen(screenLoc)) {
					return false;
				}
				moveMouse(screenLoc, 5, 5);
				if (getMenuItems().get(0).toLowerCase().contains(
						npc.getName().toLowerCase())) {
					break;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
			}
			final List<String> menuItems = getMenuItems();
			if (menuItems.isEmpty()) {
				return false;
			}
			for (String menuItem : menuItems) {
				if (menuItem.toLowerCase().contains(
						npc.getName().toLowerCase())) {
					if (menuItems.get(0).toLowerCase().contains(
							action.toLowerCase())) {
						clickMouse(true);
						return true;
					} else {
						clickMouse(false);
						return atMenu(action);
					}
				}
			}
		} catch (final Exception e) {
			log.log(Level.SEVERE, "clickNPC(RSNPC, String) error: ", e);
			return false;
		}
		return false;
	}

	private int getAction() {
		if (distanceTo(YakTile) < 50) {
			return KILLYAKS;
		} else {
			return KILLSCRIPT;
		}
	}

	public String getName() {
		return "Yak Attack";
	}

	public RSItemTile getNearestGroundItemByID(final int range, final int[] ids) {
		final int pX = getMyPlayer().getLocation().getX();
		final int pY = getMyPlayer().getLocation().getY();
		final int minX = pX - range;
		final int minY = pY - range;
		final int maxX = pX + range;
		final int maxY = pY + range;
		int dist = 100;
		RSItemTile nItem = null;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				if (Calculations.canReach(new RSTile(x, y), false)) {
					final RSItemTile[] items = getGroundItemsAt(x, y);
					for (final RSItemTile item : items) {
						final int iId = item.getItem().getID();
						for (final int id : ids) {
							if (iId == id) {
								// log(""+id+" at "+x+","+y);
								if (distanceTo(new RSTile(x, y)) < dist) {
									dist = distanceTo(new RSTile(x, y));
									nItem = item;
								}

							}
						}
					}
				}
			}
		}
		return nItem;
	}

	private RSNPC getNearestNextNPCByID(final int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();
		final org.rsbot.accessors.RSNPC[] npcs = Bot.getClient().getRSNPCArray();

		for (final int element : validNPCs) {
			if (npcs[element] == null) {
				continue;
			}
			final RSNPC Monster = new RSNPC(npcs[element]);
			try {
				for (final int id : ids) {
					if (id != Monster.getID() || Monster.isInCombat()
							|| Monster.getInteracting() != null) {
						continue;
					}
					final int distance = getRealDistanceTo(Monster
							.getLocation(), false);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (final Exception ignored) {
			}
		}
		return closest;
	}

	public int loop() {
		if (wants2Eat) {
			if (inventoryContains(food)) {
				checkEat();
			}
		}

		action = getAction();
		switch (action) {
			case KILLYAKS:
				runControl();
				final RSItemTile knife = getNearestGroundItemByID(5, BRONZEKNIFE);
				final int randomNum = random(0, 5);
				if (randomNum > 4 && knife != null) {
					atTile(knife, "Bronze knife");
					return random(900, 1400);
				}
				if (getMyPlayer().getInteracting() != null) {
					return random(300, 450);
				}

				final RSNPC yak = getNearestNextNPCByID(YAKID);
				if (yak != null) {
					if (yak.getInteracting() != null
							&& getMyPlayer().getInteracting() == null) {
						return random(100, 200);
					}

					if (getMyPlayer().getInteracting() == null) {
						clickNPC(yak, "attack");
						return random(800, 1400);
					}
					return random(200, 400);
				}
				return random(500, 1000);

			case KILLSCRIPT:
				log("Stopping script get to the Yak Pen on Neitiznot.");
				stopScript();
				return random(100, 200);
		}

		return random(400, 800);
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		log("Started Yak Attack");
		try {
			if (args.get("eatsies").equals("")) {
				log("Not Eating");
				wants2Eat = false;

			} else {
				food = Integer.parseInt(args.get("eatsies"));
				log("Eating");
				wants2Eat = true;
			}

		} catch (final Exception ignored) {

		}
		return true;
	}

	private void runControl() {
		if (!isRunning() && getEnergy() > random(20, 30)) {
			setRun(true);
		}
	}

	//*******************************************************//
	// PAINT SCREEN
	//*******************************************************//
	public void onRepaint(Graphics g) {
		atkexp = skills.getCurrentSkillExp(Constants.STAT_ATTACK) - startatkExp;
		strexp = skills.getCurrentSkillExp(Constants.STAT_STRENGTH)
				- startstrExp;
		defexp = skills.getCurrentSkillExp(Constants.STAT_DEFENSE)
				- startdefExp;
		hpexp = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS)
				- starthpExp;
		rangedexp = skills.getCurrentSkillExp(Constants.STAT_RANGE)
				- startrangedExp;
		time = System.currentTimeMillis() - startTime;
		seconds = time / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}
		if (startatkExp == 0) {
			startatkExp = skills.getCurrentSkillExp(Constants.STAT_ATTACK);
			oldatkExp = 0;
		}
		if (startstrExp == 0) {
			startstrExp = skills.getCurrentSkillExp(Constants.STAT_STRENGTH);
			oldstrExp = 0;
		}
		if (startdefExp == 0) {
			startdefExp = skills.getCurrentSkillExp(Constants.STAT_DEFENSE);
			olddefExp = 0;
		}
		if (starthpExp == 0) {
			starthpExp = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS);
			oldhpExp = 0;
		}
		if (startrangedExp == 0) {
			startrangedExp = skills.getCurrentSkillExp(Constants.STAT_RANGE);
			oldrangedExp = 0;
		}

		if (getCurrentTab() == TAB_INVENTORY) {
			g.setColor(new Color(0, 0, 0, 175));
			g.fillRoundRect(555, 210, 175, 250, 10, 10);
			g.setColor(Color.WHITE);
			int[] coords = new int[]{225, 240, 255, 270, 285, 300, 315,
					330, 345, 360, 375, 390, 405, 420, 435, 450};
			g.drawString(getName(), 561, coords[0]);
			g.drawString("Version: 1.5", 561, coords[1]);
			g.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds, 561, coords[3]);
			g.drawString("Attack exp gained: " + atkexp, 561, coords[5]);
			g.drawString("strength exp gained: " + strexp, 561, coords[6]);
			g.drawString("defence exp gained: " + defexp, 561, coords[7]);
			g.drawString("HP exp gained: " + hpexp, 561, coords[8]);
			g.drawString("ranged exp gained: " + rangedexp, 561, coords[9]);
		}
	}

}