package SampleScripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Nobody" }, category = "Combat", name = "Ogre Cage Ranger", version = 2.5, description = "<html><body bgcolor =\"#AAAAAAAA\"><font color =\"#3333FF\"><center><h2>Nobody's Ogre Cage Ranger</h2><BR>"
		+ "<font size =\"3\">Start in Traning Ground with Range Equipment,airs & laws<br />"
		+ "<tr><td>Tele - Grab Seeds: </td><td><center><input type='checkbox' name='Seed' value='true'></font><br />"
		+ "</font></body></html>")
public class OgreCageRanger extends Script implements ServerMessageListener,
		PaintListener {

	long startTime;
	long minutes;
	long seconds;
	long hours;
	long runTime;

	RSTile StartSpot;

	public int speed = 300;
	public int walkSpeed = 300;

	public long lastCheck = -1, lastcheck1 = -1, lastCheck2,
			lastCheck3 = System.currentTimeMillis();

	public Point mousePos;

	boolean Seed = false;

	int seed;
	int Ogre = 2801;
	int LawRune = 563;
	int AirRune = 556;
	int expGained;
	int levelsGained;
	int startXp;
	int startLevel;
	int amountleft;
	int RanarrID = 5295;
	int WaterID = 5321;
	int SnapeID = 5300;
	int KwuarmID = 5299;
	int LimpID = 5100;
	int StrawbID = 5323;
	int LoopkID = 987;
	int ToothkID = 985;
	int ToadfID = 5296;
	int height;

	int[] totalSeeds = { RanarrID, WaterID, SnapeID, KwuarmID, LimpID,
			StrawbID, LoopkID, ToothkID, ToadfID };

	@Override
	protected int getMouseSpeed() {
		return random(5, 7);
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		StartSpot = getMyPlayer().getLocation();
		if (args.get("Seed") != null && inventoryContains(LawRune)
				&& inventoryContains(AirRune)) {
			Seed = true;
		}
		seed = 0;
		startTime = System.currentTimeMillis();
		startXp = skills.getCurrentSkillExp(STAT_RANGE);
		startLevel = skills.getCurrentSkillLevel(STAT_RANGE);
		return true;
	}

	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
		Bot.getEventManager().removeListener(ServerMessageListener.class, this);
	}

	@Override
	public RSInterfaceChild getInventoryInterface() {
		if (getInterface(Constants.INVENTORY_COM_X).isValid()) {
			return RSInterface.getChildInterface(Constants.INVENTORY_COM_X,
					Constants.INVENTORY_COM_Y);
		}

		return RSInterface.getChildInterface(Constants.INVENTORY_X,
				Constants.INVENTORY_Y);
	}

	public void checkForLevelUpMessage() {
		if (RSInterface.getInterface(INTERFACE_LEVELUP).isValid()) {
			wait(random(800, 2000));
			atInterface(INTERFACE_LEVELUP, 3);
			wait(random(1000, 2000));
		}
	}

	public boolean CheckSeed() {
		final RSTile Ranarr = getGroundItemByID(5295);
		final RSTile Water = getGroundItemByID(5321);
		final RSTile Snape = getGroundItemByID(5300);
		final RSTile Kwuarm = getGroundItemByID(5299);
		final RSTile Strawb = getGroundItemByID(5323);
		final RSTile Toothk = getGroundItemByID(985);
		final RSTile Loopk = getGroundItemByID(987);
		final RSTile Limp = getGroundItemByID(5100);
		final RSTile Toadf = getGroundItemByID(5296);

		if (Seed) {
			if (Ranarr != null || Water != null || Snape != null
					|| Kwuarm != null || Limp != null || Strawb != null
					|| Toothk != null || Loopk != null || Toadf != null) {
				return false;
			}
		}
		return true;
	}

	public boolean rightClickTile(final RSTile tile, final String action) {
		final Point p = Calculations.tileToScreen(tile);
		clickMouse(p, false);
		wait(random(500, 800));
		return atMenu(action);
	}

	@Override
	public int loop() {
		if (distanceTo(StartSpot) > 3) {
			walkTileMM(StartSpot);
		}

		{
			checkForLevelUpMessage();
		}
		if (Seed) {
			final RSTile Ranarr = getGroundItemByID(RanarrID);
			final RSTile Water = getGroundItemByID(WaterID);
			final RSTile Snape = getGroundItemByID(SnapeID);
			final RSTile Kwuarm = getGroundItemByID(KwuarmID);
			final RSTile Limp = getGroundItemByID(LimpID);
			final RSTile Strawb = getGroundItemByID(StrawbID);
			final RSTile Toothk = getGroundItemByID(ToothkID);
			final RSTile Loopk = getGroundItemByID(LoopkID);
			final RSTile Toadf = getGroundItemByID(ToadfID);

			if (Toadf != null) {
				openTab(Constants.TAB_MAGIC);
				if (getCurrentTab() == Constants.TAB_MAGIC) {
					castSpell(Constants.SPELL_TELEKINETIC_GRAB);
					rightClickTile(Toadf, "Grab -> Toadflax");
					seed++;
					openTab(Constants.TAB_INVENTORY);
					wait(random(1400, 2200));
				}
			}
			if (Ranarr != null) {
				openTab(Constants.TAB_MAGIC);
				if (getCurrentTab() == Constants.TAB_MAGIC) {
					castSpell(Constants.SPELL_TELEKINETIC_GRAB);
					rightClickTile(Ranarr, "Grab -> Ranarr");
					seed++;
					openTab(Constants.TAB_INVENTORY);
					wait(random(1400, 2200));
				}
			}
			if (Loopk != null) {
				openTab(Constants.TAB_MAGIC);
				if (getCurrentTab() == Constants.TAB_MAGIC) {
					castSpell(Constants.SPELL_TELEKINETIC_GRAB);
					rightClickTile(Loopk, "Grab -> Loop");
					seed++;
					openTab(Constants.TAB_INVENTORY);
					wait(random(1400, 2200));
				}
			}
			if (Toothk != null) {
				openTab(Constants.TAB_MAGIC);
				if (getCurrentTab() == Constants.TAB_MAGIC) {
					castSpell(Constants.SPELL_TELEKINETIC_GRAB);
					rightClickTile(Toothk, "Grab -> Tooth");
					seed++;
					openTab(Constants.TAB_INVENTORY);
					wait(random(1400, 2200));
				}
			}
			if (Strawb != null) {
				openTab(Constants.TAB_MAGIC);
				if (getCurrentTab() == Constants.TAB_MAGIC) {
					castSpell(Constants.SPELL_TELEKINETIC_GRAB);
					rightClickTile(Strawb, "Grab -> Strawberry");
					seed++;
					openTab(Constants.TAB_INVENTORY);
					wait(random(1400, 2200));
				}
			}
			if (Water != null) {
				openTab(Constants.TAB_MAGIC);
				if (getCurrentTab() == Constants.TAB_MAGIC) {
					castSpell(Constants.SPELL_TELEKINETIC_GRAB);
					rightClickTile(Water, "Grab -> Watermelon");
					seed++;
					openTab(Constants.TAB_INVENTORY);
					wait(random(1400, 2200));
				}
			}
			if (Snape != null) {
				openTab(Constants.TAB_MAGIC);
				if (getCurrentTab() == Constants.TAB_MAGIC) {
					castSpell(Constants.SPELL_TELEKINETIC_GRAB);
					rightClickTile(Snape, "Grab -> Snapdragon");
					seed++;
					openTab(Constants.TAB_INVENTORY);
					wait(random(1400, 2200));
				}
			}
			if (Kwuarm != null) {
				openTab(Constants.TAB_MAGIC);
				if (getCurrentTab() == Constants.TAB_MAGIC) {
					castSpell(Constants.SPELL_TELEKINETIC_GRAB);
					rightClickTile(Kwuarm, "Grab -> Kwuarm");
					seed++;
					openTab(Constants.TAB_INVENTORY);
					wait(random(1400, 2200));
				}
			}
			if (Limp != null) {
				openTab(Constants.TAB_MAGIC);
				if (getCurrentTab() == Constants.TAB_MAGIC) {
					castSpell(Constants.SPELL_TELEKINETIC_GRAB);
					rightClickTile(Limp, "Grab -> Limp");
					seed++;
					openTab(Constants.TAB_INVENTORY);
					wait(random(1400, 2200));
				}
			}
		}
		if (CheckSeed() && getMyPlayer().getInteracting() == null
				&& getNearestFreeNPCByID(Ogre) != null) {
			clickNPC(getNearestFreeNPCByID(Ogre), "Attack");
		}/*
		 * else { if (npc.getInteracting() != null &&
		 * !npc.isInteractingWithLocalPlayer() && getMyPlayer().getInteracting()
		 * == null) atNPC(getNearestNPCByID(MONSTER), "Attack"); }
		 */
		return 800;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String message = e.getMessage();
		if (message.contains("There is no ammo left in your quiver.")) {
			log("No Arrows!!");
			stopScript();
		}
		if (message.contains("That was your last one!")) {
			log("No knives!!");
			stopScript();
		}
	}

	public boolean activateCondition() {
		if (getMyPlayer().isMoving()) {
			return random(1, walkSpeed) == 1;
		} else {
			return random(1, speed) == 1;
		}
	}

	public boolean someNPC(final RSNPC npc, final String action) {
		try {
			int a;
			final StringBuffer npcCommandBuf = new StringBuffer();
			npcCommandBuf.append(action);
			npcCommandBuf.append(" ");
			npcCommandBuf.append(npc.getName());
			final String npcCommand = npcCommandBuf.toString();
			for (a = 10; a-- >= 0;) {
				if (npc.getInteracting() != null
						&& !npc.isInteractingWithLocalPlayer()) {
					return false;
				}
				final List<String> menuItems = getMenuItems();
				if (menuItems.size() > 1) {
					if (listContainsString(menuItems, npcCommand)) {
						if (menuItems.get(0).contains(npcCommand)) {
							clickMouse(true);
							return true;
						} else {
							clickMouse(false);
							wait(random(230, 520));
							return atMenu(npcCommand);
						}
					}
				}
				final Point screenLoc = npc.getScreenLocation();
				if (!pointOnScreen(screenLoc)) {
					return false;
				}
				final Point randomP = new Point(random(screenLoc.x - 15,
						screenLoc.x + 15), random(screenLoc.y - 15,
						screenLoc.y + 15));
				if (randomP.x >= 0 && randomP.y >= 0) {
					moveMouse(randomP);
				}
			}
			return false;
		} catch (final Exception e) {
			System.out.print("clickNPC(RSNPC, String) error: " + e);
			return false;
		}
	}

	public boolean listContainsString(final List<String> list,
			final String string) {
		try {
			int a;
			for (a = list.size() - 1; a-- >= 0;) {
				if (list.get(a).contains(string)) {
					return true;
				}
			}
		} catch (final Exception ignored) {
		}
		return false;
	}

	// GOD DAMIT DO NOT OVERRIDE IMPLEMENTED METHODS
	// JUST LEAVE THE GOD DAMN ANNOTATION AT ALL!!
	// ONLY USE IT IF YOU KNOW WHAT IT IS.
	// sorry for freaking out, hate having to fix this every time :P
	public void onRepaint(final Graphics g) {
		final Mouse m = Bot.getClient().getMouse();
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
		final int x = 3;
		int y = 180;
		expGained = skills.getCurrentSkillExp(STAT_RANGE) - startXp;
		g.setColor(new Color(0, 0, 0, 50));
		g.fillRoundRect(x, y, 155, height, 5, 5);

		// Calculate levels gained
		levelsGained = skills.getCurrentSkillLevel(STAT_RANGE) - startLevel;

		g.setColor(Color.WHITE);
		if (m.x > x + 155 || m.x < x || m.y > height + 180 || m.y < y) {
			g.drawString("Ogre Cage Ranger v2.5", x + 9, y += 36);
		} else {
			g.drawString("Ogre Cage Ranger v2.5", x + 9, y += 36);
			g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds,
					x + 9, y += 16);
			g
					.drawString("Seeds Collected: " + seed + " seeds.", x + 9,
							y += 16);
			g.drawString("XP Gained: " + expGained, x + 9, y += 16);
			g.drawString("Levels Gained: " + levelsGained, x + 9, y += 16);
			g.drawString("Percent to next level: "
					+ skills.getPercentToNextLevel(STAT_RANGE), x + 9, y += 16);
		}
		height = y - 173;
	}

	public boolean isRanging() {
		final int id = getMyPlayer().getAnimation();
		return id == 426 || id == 4230 || id == 1074;
	}

	private boolean clickNPC(final RSNPC npc, final String action) {
		final RSTile tile = npc.getLocation();
		tile.randomizeTile(1, 1);
		try {
			final int hoverRand = random(8, 13);
			for (int i = 0; i < hoverRand; i++) {
				final Point screenLoc = npc.getScreenLocation();
				if (!pointOnScreen(screenLoc)) {
					setCameraRotation(getCameraAngle() + random(-35, 150));
					return true;
				}

				moveMouse(screenLoc, 15, 15);

				final List<String> menuItems = getMenuItems();
				if (menuItems.isEmpty() || menuItems.size() <= 1) {
					continue;
				}
				if (menuItems.get(0).toLowerCase().contains(
						npc.getName().toLowerCase())
						&& getMyPlayer().getInteracting() == null) {
					clickMouse(true);
					return true;
				} else {
					for (int a = 1; a < menuItems.size(); a++) {
						if (menuItems.get(a).toLowerCase().contains(
								npc.getName().toLowerCase())
								&& getMyPlayer().getInteracting() == null) {
							clickMouse(false);
							return atMenu(action);
						}
					}
				}
			}

		} catch (final Exception e) {
			log.warning("ClickNPC error: " + e);
			return false;
		}
		return false;
	}
}