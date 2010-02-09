package SampleScripts;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"BinaryX, XScripting Inc"},
		category = "Combat",
		name = "MossGiantsKiller",
		version = 6.7,
		description = "  <html>"
				+ "<body>"
				+ "Click OK to start the GUI."
				+ "</body>" + "</html>"

)
public class MossGiantsKiller extends Script implements PaintListener,
		ServerMessageListener {

	XAntiBan antiban;
	Thread t;
	private final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	public int SELECTED_STAT, stopAtLvl, actualmousespeed, back2zeroatk, back2zerodef, back2zerostr, back2zerorng,
			Atk1, Atk2, Atk3, Attack1, Attack2, def1, def2, def3, Defence1, Defence2, rng1,
			rng2, rng3, range1, range2, str1, str2, str3, Strength1, Strength2,
			AtkHit, DefHit, RngHit, StrHit, startexp, STRENGTHstartexp,
			ItemPrice, DEFENSEstartexp, HITPOINTSstartexp, RANGEstartexp, MAGICstartexp, MoneyGained;

	private long startTime;
	private BufferedImage image;
	public String status = "Starting Up..";

	public boolean stopAtLVL, AdvancedPaint, Busy, AttackNext, PickupCustom, BuryBones,
			anti_ban, PickupBones, PickupItems, eat_food, B2P_Tab, inDungeon,
			HitPredict;
	// ---------------------------------------------------//
	public int ItemArray[] = {560, 563, 565, 562, 561, 564, 5303, 5299, 5302,
			5295, 5300, 5304, 5316, 5321, 985, 987, 12158, 12159, 12160, 12163,
			5323};
	// ---------------------------------------------------//
	public String ItemNames[] = {"Death rune", "Law rune", "Blood rune",
			"Chaos rune", "Nature rune", "Cosmic rune", "Dwarf weed seed",
			"Kwuarm seed", "Lantadyme seed", "Ranarr seed", "Snapdragon seed",
			"Torstol seed", "Magic seed", "Watermelon seed",
			"Tooth half of a key", "Loop half of a key", "Gold charm",
			"Green charm", "Crimson charm", "Blue charm", "Strawberry seed"};
	// ---------------------------------------------------//

	public String SELECTED_STAT_NAME;
	// ---------------------------------------------------//

	public int TAB_ID = 8015;
	final int[] BONE_ID = {526, 532, 530, 528, 3183, 2859};
	public int PEACH_ID = 6883;

	// ---------------------------------------------------//
	public int[] CUSTOM_ID = {};
	// ---------------------------------------------------//
	final RSTile pathToDungeon = new RSTile(3107, 3640);
	// ---------------------------------------------------//
	RSItemTile tile;
	public int food_id;
	@SuppressWarnings("unchecked")
	public ArrayList list;
	int[] prices = new int[ItemArray.length];
	int[] ItemPrices;
	// ---------------------------------------------------//
	public final int[] FAIL_AREA = {3251, 3257, 5542, 5547}; //fire giants
	public final int[] FAIL_AREA2 = {3255, 3265, 5560, 5565}; //spiders
	private boolean guiWait = true, guiExit;

	public boolean onStart(Map<String, String> args) {
		MossXGui gui = new MossXGui();
		if (needToUpdate()) {
			log.info("A newer version is available!");
		}
		gui.setVisible(true);
		while (guiWait) {
			wait(100);
		}
		startTime = System.currentTimeMillis();
		antiban = new XAntiBan();
		t = new Thread(antiban);
		log("#########################");
		log("XSCRIPTING - MossGiantsKiller " + properties.version());
		log("#########################");
		/* CUSTOM METHOD */
		if (PickupCustom) {
			String temp = gui.txtCustom.getText();
			if (temp != null) {
				String temp2[] = temp.split(",");
				CUSTOM_ID = new int[temp2.length];
				CUSTOM_ID = extractIntegers(temp);
			} else {
				log("Please enter a custom item id to pickup. ");
				stopScript();
			}
		}
		if (PickupItems) {
			log("Loading all item prices......");
			try {
				for (int i = 0; i < ItemArray.length; i++) {
					prices[i] = getMarketPriceByID(ItemArray[i]);
					if (i == ItemArray.length) {
						log("All item prices have been loaded.");
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				log("Failed to load item prices.");
			}
		}
		ItemPrices = prices;
		AttackNext = true;
		if (stopAtLVL) {
			log("We will stop the script when " + SELECTED_STAT_NAME + " reaches level " + stopAtLvl);
		}
		return guiExit;
	}

	public void onFinish() {

		Bot.getEventManager().removeListener(PaintListener.class, this);
		log("Thanks for using XScripting - MossGiantsKiller!");
		antiban.stopThread = true;

	}

	protected int getMouseSpeed() {
		actualmousespeed = random(4 - 1, 5 + 1);
		return actualmousespeed;
	}

	private int getMarketPriceByID(final int ID) {
		GEItemInfo i = grandExchange.loadItemInfo(ID);
		return i.getMarketPrice();
	}

	public int[] extractIntegers(String text) {
		int[] ints = null;
		try {
			text = text.replaceAll(" ", ",");
			final String[] strInts = text.split(",");
			ints = new int[strInts.length];
			for (int a = 0; a < strInts.length; a++) {
				ints[a] = Integer.parseInt(strInts[a]);
			}
		} catch (final Exception e) {
			log.log(Level.SEVERE, "extractIntegers(String) error: ", e);
		}
		return ints;
	}

	private boolean playerIsInArea(final int[] bounds) {
		final RSTile pos = getMyPlayer().getLocation();
		return pos.getX() >= bounds[0] && pos.getX() <= bounds[1]
				&& pos.getY() >= bounds[2] && pos.getY() <= bounds[3];
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		String serverString = e.getMessage();
		if (serverString.contains("chaos") || serverString.contains("unknown")) {
			log("Failsafe activated.");
			stopScript();
		}
	}

	public int loop() {
		if (stopAtLVL) {
			if (skills.getCurrentSkillLevel(SELECTED_STAT) >= stopAtLvl) {
				log("Desired level has been reached.");
				stopScript(true);
			}
		}
		getMouseSpeed();
		while (getMyPlayer().getAnimation() == 829) {
			wait(random(50, 120));
		}
		if (playerIsInArea(FAIL_AREA)) {
			log("Activating failsafe.");
			try {
				RSObject Portal = getNearestObjectByID(28779);
				RSTile Location = Portal.getLocation();
				walkTo(Location);
				atObject(Portal, "Enter Portal");
				return (random(1000, 1500));
			} catch (NullPointerException e) {
				return random(50, 120);
			}
		}
		if (playerIsInArea(FAIL_AREA2)) {
			log("Activating failsafe.");
			try {
				RSObject Portal = getNearestObjectByID(28779);
				RSTile Location = Portal.getLocation();
				walkTo(Location);
				atObject(Portal, "Enter Portal");
				return (random(1000, 1500));
			} catch (NullPointerException e) {
				return random(50, 120);
			}
		}
		setRun();
		inDungeon = getNearestObjectByID(28797, 28798, 28794, 28793) != null;
		int CurrHP = skills.getCurrentSkillLevel(STAT_HITPOINTS);
		int RealHP = skills.getRealSkillLevel(STAT_HITPOINTS);
		if (eat_food && CurrHP <= random(RealHP / 2, RealHP / 1.5)) {
			EAT_FOOD();
			return random(1000, 1500);
		}
		if (anti_ban) {
			if (!t.isAlive()) {
				t.start();
				log("AntiBan initialized!");
			}
		}
		if (!inDungeon) {
			if (distanceTo(pathToDungeon) > 15) { // not in dungeon, let's walk.
				walkTo(pathToDungeon);
				status = "Walking to dungeon";
				inDungeon = false;
				return random(50, 120);
			} else if (distanceTo(pathToDungeon) < 15) { // we are near the
				// dungeon, lets
				// enter.
				try {
					final RSObject Rift = getNearestObjectByID(28893);
					if (atObject(Rift, "Enter")) {
						status = "Entering dungeon.";
						inDungeon = true;
						return 1000;
					}
				} catch (NullPointerException e) {
					log("Cannot find the dungeon entrance.");
					inDungeon = false;
				}
			}
		}
		if (inDungeon) {
			if (AttackNext && getMyPlayer().getAnimation() == -1 && getMyPlayer().getInteracting() == null) {
				Busy = false;
				if (PickupItems) {
					try {
						for (int i = 0; i < ItemArray.length; i++) {
							while ((tile = getGroundItemByID(ItemArray[i])) != null) {
								Busy = true;
								if (!tileOnScreen(tile)) {
									break;
								}
								if (isInventoryFull()) {
									if (getInventoryCount(tile.getItem().getID()) == 0
											|| getInventoryItemByID(
											tile.getItem().getID())
											.getStackSize() == 1) {

										if (B2P_Tab && getInventoryCount(BONE_ID) > 0) {
											status = "Dropping bones to make space.";
											doInventoryItem(BONE_ID, "Drop");
											Busy = true;
											wait(random(750, 1000));
										} else {
											if (eat_food
													&& getInventoryCount(food_id) > 0) {
												atInventoryItem(food_id, "Eat");
												Busy = true;
												status = "Eating food to make space.";
												wait(random(750, 1000));
											} else {
												return random(50, 120);
											}
										}
									}
								}
								if (atTile(tile, "Take " + ItemNames[i])) {
									Busy = true;
									log("Picking up: " + ItemNames[i]);
									status = "Picking up items...";
									ItemPrice = getMarketPriceByID(ItemArray[i]);
									MoneyGained = MoneyGained + ItemPrice;
									wait(random(750, 1000));
								} else {
									wait(random(1250, 1980));
									return random(50, 120);
								}
								while (getMyPlayer().isMoving() || getMyPlayer().getAnimation() != -1) {
									wait(random(20, 60));
								}
							}
						}
					}
					// THIS IS VERY BAD CODE...
					catch (NullPointerException ignored) {
					}
					catch (ArrayIndexOutOfBoundsException ignored) {
					}
				}
				if (PickupBones || B2P_Tab && getGroundItemByID(BONE_ID) != null
						&& !isInventoryFull()) {
					RSItemTile tile3;
					while ((tile3 = getNearestGroundItemByID(BONE_ID)) != null) {
						Busy = true;
						if (getInventoryCount() >= 26
								|| getInventoryCount(BONE_ID) > 10) {
							Busy = false;
							break;
						}
						if (getInventoryCount(PEACH_ID) >= 10 && !BuryBones) {
							Busy = false;
							break;
						}
						if (!tileOnScreen(tile3)) {
							break;
						}
						String action = "ones";
						atTile(tile3, action);
						Busy = true;
						status = "Picking up bones.";
						while (getMyPlayer().isMoving() || getMyPlayer().getAnimation() != -1) {
							wait(random(20, 60));
						}
						wait(random(750, 1000));
					}
					Busy = false;
				}
				if (PickupCustom && getGroundItemByID(CUSTOM_ID) != null) {
					RSItemTile tile3;
					try {
						for (int aCUSTOM_ID : CUSTOM_ID) {
							while ((tile3 = getNearestGroundItemByID(aCUSTOM_ID)) != null) {
								if (!tileOnScreen(tile3)) {
									break;
								}
								if (isInventoryFull()
										&& getInventoryCount(aCUSTOM_ID) > 0) {
									String action = "Take";
									atTile(tile3, action);
									Busy = true;
									status = "Picking up custom items...";
									while (getMyPlayer().isMoving() || getMyPlayer().getAnimation() != -1) {
										wait(random(20, 60));
									}
									wait(random(750, 1000));
								} else {
									String action = "Take";
									atTile(tile3, action);
									Busy = true;
									status = "Picking up custom items...";
									while (getMyPlayer().isMoving() || getMyPlayer().getAnimation() != -1) {
										wait(random(20, 60));
									}
									wait(random(750, 1000));
								}
							}
						}
					}
					// THIS IS VERY BAD CODE...
					catch (NullPointerException ignored) {
					}
					catch (ArrayIndexOutOfBoundsException ignored) {
					}
				}
				if (BuryBones) {
					for (int aBONE_ID : BONE_ID) {
						if (getInventoryCount(aBONE_ID) >= 4) {
							atInventoryItem(aBONE_ID, "Bury");
							Busy = true;
							status = "Burying bones...";
						}
					}
				}
				if (!Busy) {
					try {

						RSNPC SelectedNPC = getNearestFreeNPCToAttackByName("Moss giant");
						final RSTile locNPC = SelectedNPC.getLocation();
						if (!tileOnScreen(locNPC)) {
							walkTo(locNPC, 2, 2);
							status = "Walking to NPC.";
						}
						while (getMyPlayer().isMoving() && !tileOnScreen(locNPC)) {
							wait(random(50, 120));
						}
						if (!SelectedNPC.isInteractingWithLocalPlayer()) {
							if (clickNPC(SelectedNPC)) {
								if (SelectedNPC.getHPPercent() == 0) {
									AttackNext = true;
									return random(1250, 1980);
								}
								while (SelectedNPC.getHPPercent() > 0 && SelectedNPC.isInteractingWithLocalPlayer()) {
									AttackNext = false;
									wait(random(50, 120));
								}
								status = "In Combat";
							}
						}
					}
					catch (NullPointerException e) {
						log("Could not find moss giant");
					}
				}
			}
		}
		return random(1250, 1980);
	}

	public void setRun() {
		if (getMyPlayer().isMoving()) {
			if (!isRunning() && getEnergy() > 50) {
				setRun(true);
			}
		}
	}

	public void EAT_FOOD() {
		if (getCurrentTab() != TAB_INVENTORY) {
			openTab(TAB_INVENTORY);
		}
		if (getInventoryCount(food_id) >= 1) {
			atInventoryItem(food_id, "Eat");
			status = "Eating food.";
		}
		if (!inventoryContains(food_id) && !B2P_Tab) {
			log("You are out of food.");
			stopScript(true);
		}
		if (B2P_Tab) {
			if (getInventoryCount(BONE_ID) == 0
					&& getInventoryCount(PEACH_ID) == 0
					&& getInventoryCount(TAB_ID) == 0) {
				log("Out of bones,peaches & tabs!");
				stopScript(true);
			}

			if (getInventoryCount(TAB_ID) == 0) {
				log("You are out of B2P Tablets.");
				stopScript(true);
			}
			if (getInventoryCount(BONE_ID) > 0
					&& getInventoryCount(PEACH_ID) == 0) {// we have
				// tabs+bones
				// but no
				// peaches.
				atInventoryItem(TAB_ID, "Break");
				status = "Breaking a B2P Tab.";
				wait(4000);
			}
			if (getInventoryCount(PEACH_ID) > 0) {
				atInventoryItem(PEACH_ID, "Eat");
				status = "Eating a peach.";
			}
		}
	}

	public boolean doInventoryItem(int[] ids, String action) {

		ArrayList<RSInterfaceComponent> possible = new ArrayList<RSInterfaceComponent>();
		for (RSInterfaceComponent com : getInventoryInterface().getComponents()) {
			for (int i : ids) {
				if (i == com.getComponentID())
					possible.add(com);
			}
		}
		if (possible.size() == 0)
			return false;
		RSInterfaceComponent winner = possible.get(random(0,
				possible.size() - 1));
		Rectangle loc = winner.getArea();
		moveMouse(loc);
		String top = getMenuItems().get(0).toLowerCase();
		if (isItemSelected()) {
			moveMouse(random(0, 492), random(340, 450));
			clickMouse(true);
			wait(random(300, 600));
		}
		if (top.contains(action.toLowerCase())) {
			clickMouse(true);
			return true;
		} else if (menuContains(action)) {
			wait(random(400, 700));
			return atMenu(action);
		}
		return false;
	}

	public void moveMouse(Rectangle r) {
		int x = (r.x + 1) + random(0, r.width - 1);
		int y = (r.y + 1) + random(0, r.height - 1);
		moveMouse(x, y);
	}

	public boolean menuContains(String item) {
		try {
			for (String s : getMenuItems()) {
				if (s.toLowerCase().contains(item.toLowerCase()))
					return true;
			}
		} catch (Exception e) {
			return menuContains(item);
		}
		return false;
	}

	private void drawMouse(final Graphics g) {
		final Point loc = getMouseLocation();
		if (System.currentTimeMillis()
				- Bot.getClient().getMouse().getMousePressTime() < 500) {
			g.setColor(new Color(0, 0, 0, 50));
			g.fillOval(loc.x - 5, loc.y - 5, 10, 10);
		} else {
			g.setColor(Color.BLACK);
		}
		g.drawLine(0, loc.y, 766, loc.y);
		g.drawLine(loc.x, 0, loc.x, 505);
	}


	public void onRepaint(Graphics g) {
		if (isLoggedIn()) {
			try {
				RSNPC NextMonster = getNearestFreeNPCByName("Moss giant");
				Point p = NextMonster.getLocation().getScreenLocation();
				g.setFont(new Font("Arial", Font.PLAIN, 11));
				g.setColor(Color.red);
				g.drawString("NEXT TARGET", p.x, p.y);
			}
			// BAD! use if(monster != null)
			catch (NullPointerException ignored) {
			}

			if (AdvancedPaint) {
				drawMouse(g);
			}
			Mouse m = Bot.getClient().getMouse();
			// Attack Calculating
			if (Attack1 == 0) {
				Attack1 = skills.getCurrentSkillExp(STAT_ATTACK);
				Attack2 = 0;
				Atk1 = Attack1;
				back2zeroatk = 40; // This is all the math crap to find next
				// hit.
			}
			if (Attack2 == 0) {
				if ((Attack1 != 0)
						&& (Attack1 != skills.getCurrentSkillExp(STAT_ATTACK))) {
					Attack2 = skills.getCurrentSkillExp(STAT_ATTACK);
					Atk2 = Attack2;
					Attack1 = 0;
					Attack2 = 0;
					back2zeroatk = 40;
				}
			}

			if ((Atk2 - Atk1 != 0)) {
				Atk3 = Atk2 - Atk1;
				AtkHit = Atk3 / 4;
				AtkHit = Math.round(AtkHit);
				back2zeroatk = 40;
			}
			if (back2zeroatk <= 0) {
				AtkHit = 0;
			}
			back2zeroatk--;

			// Defence Calculating

			if (Defence1 == 0) {
				Defence1 = skills.getCurrentSkillExp(STAT_DEFENSE);
				Defence2 = 0;
				def1 = Defence1;
				back2zerodef = 40;
			}
			if (Defence2 == 0) {
				if ((Defence1 != 0)
						&& (Defence1 != skills.getCurrentSkillExp(STAT_DEFENSE))) {
					Defence2 = skills.getCurrentSkillExp(STAT_DEFENSE);
					def2 = Defence2;
					Defence1 = 0;
					Defence2 = 0;
					back2zerodef = 40;
				}
			}

			if ((def2 - def1 != 0)) {
				def3 = def2 - def1;
				DefHit = def3 / 4;
				DefHit = Math.round(DefHit);
				back2zerodef = 40;
			}
			if (back2zerodef <= 0) {
				DefHit = 0;
			}
			back2zerodef--;

			// Range Calculating

			if (range1 == 0) {
				range1 = skills.getCurrentSkillExp(STAT_RANGE);
				range2 = 0;
				rng1 = range1;
				back2zerorng = 40;
			}
			if (range2 == 0) {
				if ((range1 != 0)
						&& (range1 != skills.getCurrentSkillExp(STAT_RANGE))) {
					range2 = skills.getCurrentSkillExp(STAT_RANGE);
					rng2 = range2;
					range1 = 0;
					range2 = 0;
					back2zerorng = 40;
				}
			}

			if ((rng2 - rng1 != 0)) {
				rng3 = rng2 - rng1;
				RngHit = rng3 / 4;
				RngHit = Math.round(RngHit);
				back2zerorng = 40;
			}
			if (back2zerorng <= 0) {
				RngHit = 0;
			}
			back2zerorng--;

			// Strength Calculating

			if (Strength1 == 0) {
				Strength1 = skills.getCurrentSkillExp(STAT_STRENGTH);
				Strength2 = 0;
				str1 = Strength1;
				back2zerostr = 40;
			}
			if (Strength2 == 0) {
				if ((Strength1 != 0)
						&& (Strength1 != skills
						.getCurrentSkillExp(STAT_STRENGTH))) {
					Strength2 = skills.getCurrentSkillExp(STAT_STRENGTH);
					str2 = Strength2;
					Strength1 = 0;
					Strength2 = 0;
					back2zerostr = 40;
				}
			}

			if ((str2 - str1 != 0)) {
				str3 = str2 - str1;
				StrHit = str3 / 4;
				StrHit = Math.round(StrHit);
				back2zerostr = 40;
			}
			if (back2zerostr <= 0) {
				StrHit = 0;
			}
			back2zerostr--;

			/* ATTACK */
			int xpGained;
			if (startexp == 0) {
				startexp = skills.getCurrentSkillExp(STAT_ATTACK);
			}
			xpGained = skills.getCurrentSkillExp(STAT_ATTACK) - startexp;

			/* STRENGTH */
			int STRENGTHxpGained;
			if (STRENGTHstartexp == 0) {
				STRENGTHstartexp = skills.getCurrentSkillExp(STAT_STRENGTH);
			}
			STRENGTHxpGained = skills.getCurrentSkillExp(STAT_STRENGTH)
					- STRENGTHstartexp;

			/* DEFENSE */
			int DEFENSExpGained;
			if (DEFENSEstartexp == 0) {
				DEFENSEstartexp = skills.getCurrentSkillExp(STAT_DEFENSE);
			}
			DEFENSExpGained = skills.getCurrentSkillExp(STAT_DEFENSE)
					- DEFENSEstartexp;

			/* HITPOINTS */
			int HITPOINTSxpGained;
			if (HITPOINTSstartexp == 0) {
				HITPOINTSstartexp = skills.getCurrentSkillExp(STAT_HITPOINTS);
			}
			HITPOINTSxpGained = skills.getCurrentSkillExp(STAT_HITPOINTS)
					- HITPOINTSstartexp;

			/* RANGE */
			int RANGExpGained;
			if (RANGEstartexp == 0) {
				RANGEstartexp = skills.getCurrentSkillExp(STAT_RANGE);
			}
			RANGExpGained = skills.getCurrentSkillExp(STAT_RANGE)
					- RANGEstartexp;

			/* MAGIC */
			int MAGICxpGained;
			if (MAGICstartexp == 0) {
				MAGICstartexp = skills.getCurrentSkillExp(STAT_MAGIC);
			}
			MAGICxpGained = skills.getCurrentSkillExp(STAT_MAGIC)
					- MAGICstartexp;

			int x = 285;
			int y = 6;
			g.setFont(new Font("sansserif", Font.BOLD, 20));
			g.setColor(new Color(255, 140, 0));
			g.fillRoundRect(x, y, 180, 30, 20, 20);
			g.setColor(new Color(26, 36, 162, 255));
			g.drawRoundRect(x, y, 180, 30, 20, 20);
			g.setColor(new Color(255, 255, 255, 255));
			g.drawString("SHOW PAINT", x + 7, y + 20);
			long millis = System.currentTimeMillis() - startTime;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			long seconds = millis / 1000;
			float xpsec;
			if (m.x >= 286 && m.x < 467 && m.y >= 0 && m.y < 41) {
				g.drawImage(ImageResource(), 289, 236, 190, 95, null);
				g.setFont(new Font("Arial", Font.PLAIN, 11));
				g.setColor(new Color(33, 46, 207, 100));
				x = 285;
				y = 44;
				g.fillRoundRect(x, y, 230, 130, 20, 20);
				g.setColor(Color.red);
				g.drawRoundRect(x, y, 230, 130, 20, 20);
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString(properties.name() + " Version "
						+ properties.version(), x + 10, y += 15);
				g.drawString("Time running: " + hours + ":" + minutes + ":"
						+ seconds, x + 10, y += 15);
				if (xpGained != 0) {
					g.drawString("Attack XP Gained:" + xpGained, x + 10,
							y += 15);
				}
				if (STRENGTHxpGained != 0) {
					g.drawString("Strength XP Gained:" + STRENGTHxpGained,
							x + 10, y += 15);
				}
				if (DEFENSExpGained != 0) {
					g.drawString("Defense XP Gained:" + DEFENSExpGained,
							x + 10, y += 15);
				}
				if (HITPOINTSxpGained != 0) {
					g.drawString("Hitpoints XP Gained:" + HITPOINTSxpGained,
							x + 10, y += 15);
				}
				if (RANGExpGained != 0) {
					g.drawString("Range XP Gained:" + RANGExpGained, x + 10,
							y += 15);
				}
				if (MAGICxpGained != 0) {
					g.drawString("Magic XP Gained:" + MAGICxpGained, x + 10,
							y += 15);
				}
				int TotalXP = xpGained + STRENGTHxpGained + DEFENSExpGained
						+ RANGExpGained + MAGICxpGained;
				xpsec = ((float) TotalXP)
						/ (float) (seconds + (minutes * 60) + (hours * 60 * 60));
				float perHourXP = (xpsec * 60) * 60;
				if (TotalXP != 0) {
					g.drawString("Xp/Hour:" + (float) Math.round(perHourXP), x + 10,
							y += 15);
				}
				if (PickupItems) {
					float MoneyPerSec = ((float) MoneyGained) / (float) (seconds + (minutes * 60) + (hours * 60 * 60));
					float MoneyPerHour = MoneyPerSec * 60 * 60;

					g.drawString("GP Gain/Hr:" + Math.round(MoneyPerHour), x + 10, y += 15);
					g.drawString("Total GP Gained:" + Math.round(MoneyGained), x + 10, y += 15);
				}
				g.drawString("Status:" + status, x + 10, y += 15);

				/* AUTHOR */
				x = 285;
				y = 177;
				g.setColor(new Color(33, 46, 207, 100));
				g.fillRoundRect(x, y, 230, 25, 30, 30);
				g.setColor(Color.red);
				g.drawRoundRect(x, y, 230, 25, 30, 30);
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString("Script Author: BinaryX - XScripting Inc.",
						x + 10, y += 15);
			}
			/* HIT PREDICTOR PAINT */
			if (HitPredict) {
				x = 285;
				y = 372;
				if (STRENGTHxpGained != 0 || DEFENSExpGained != 0
						|| xpGained != 0) {
					g.setColor(Color.BLACK);
					g.setFont(new Font("sansserif", Font.BOLD, 20));
					g.drawString("HIT PREDICTER", 285, 365);
					g.setFont(new Font("Arial", Font.PLAIN, 12));
					g.setColor(new Color(255, 140, 0));
					g.fillRoundRect(x, y, 180, 60, 20, 20);
					g.setColor(new Color(26, 36, 162, 255));
					g.drawRoundRect(x, y, 180, 60, 20, 20);
					g.setColor(new Color(255, 255, 255, 255));
					if (xpGained != 0) {
						g.drawString("Next Hit(Attack):" + AtkHit, x + 10,
								y += 15);
					}
					if (STRENGTHxpGained != 0) {
						g.drawString("Next Hit(Strength):" + StrHit, x + 10,
								y += 15);
					}
					if (DEFENSExpGained != 0) {
						g.drawString("Next Hit(Defense):" + DefHit, x + 10,
								y += 15);
					}
				}
			}

		}
	}

	public boolean isItemSelected() {
		for (RSInterfaceComponent com : getInventoryInterface().getComponents()) {
			if (com.getBorderThickness() == 2)
				return true;
		}
		return false;
	}

	boolean clickNPC(RSNPC n) {
		try {
			Point p;
			while ((p = n.getScreenLocation()) != null && p.x != -1
					&& getMouseLocation().distance(n.getScreenLocation()) > 8) {
				moveMouse(p, 5, 5);
			}
			if (!pointOnScreen(getMouseLocation())) {
				return false;
			}
			clickMouse(true);
			return true;
		} catch (Exception ignored) {// BAD
		}
		return true;
	}

	private BufferedImage ImageResource() {
		if (image == null)
			try {
				URL url = new URL("http://i46.tinypic.com/s15mv8.jpg");
				image = ImageIO.read(url);
				return image;
			} catch (IOException ex) {
				log("Could not find the image file.");
				image = null;
			}
		return image;
	}

	private class XAntiBan implements Runnable {
		public boolean stopThread;

		public void run() {
			while (!stopThread) {
				try {
					if (random(0, 15) == 0) {
						final char[] LR = new char[]{KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT};
						final char[] UD = new char[]{KeyEvent.VK_DOWN,
								KeyEvent.VK_UP};
						final char[] LRUD = new char[]{KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP};
						final int random2 = random(0, 2);
						final int random1 = random(0, 2);
						final int random4 = random(0, 4);

						if (random(0, 3) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random(300, 600));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random(300, 600));
							} else {
								Thread.sleep(random(500, 900));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random(200, 2000));
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Boolean needToUpdate() {
		try {
			URL versionAdress = new URL(
					"http://binaryx.nl/xscripting/binaryx/mossgiantskiller/version.txt");
			versionAdress.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					versionAdress.openStream()));
			String line;
			if ((line = reader.readLine()) != null) {
				if (line.contains(Double.toString(properties.version()))) {
					log("You have the latest version.");
					return false; // we are up to date.
				} else {
					JOptionPane.showMessageDialog(null,
							"A new update has been found, please check the thread for more information.");
					return true;
				}
			}
		} catch (MalformedURLException e) {
			log("MailformedURLException.");
			return true;
		} catch (UnknownHostException e) {
			log("UnknownHostException.");
			return true;
		} catch (IOException e) {
			log("IOException.");
			return true;
		}
		return true;
	}

	@SuppressWarnings("serial")
	class MossXGui extends JFrame implements ListSelectionListener,
			ActionListener {
		// JFormDesigner - Variables declaration - DO NOT MODIFY
		// //GEN-BEGIN:variables
		private JTabbedPane tabMain;
		private JPanel pnlSetup;
		private JTabbedPane tabSetup;
		private JPanel pnlFood;
		private JCheckBox chkEat;
		private JLabel lblFoodID;
		private JTextField txtFoodID;
		private JRadioButton radioB2P;
		private JPanel pnlItem;
		private JCheckBox chkPickup;
		private JCheckBox chkCustom;
		private JTextField txtCustom;
		private JLabel lblCustomID;
		private JCheckBox chkBones;
		private JPanel pnlOther;
		private JCheckBox chkAB;
		private JCheckBox chkBury;
		private JCheckBox chkPredict;
		private JLabel lblGiant;
		private JLabel lblStopScriptWhen;
		private JComboBox cbStat;
		private JTextField txtEndLVL;
		private JCheckBox chkAPaint;
		private JPanel pnlLicense;
		private JScrollPane scrollPane1;
		private JTextArea txtLicense;
		private JPanel pnlCredits;
		private JLabel lblCredits;
		private JLabel label1;
		private JLabel label2;
		private JButton btnStart;
		private JButton btnExit;
		private JButton btnForum;
		private JLabel lblLogo;
		private JPanel panel3;
		private JCheckBox chkEnable;


		// JFormDesigner - End of variables declaration //GEN-END:variables
		public MossXGui() {

			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents
			tabMain = new JTabbedPane();
			pnlSetup = new JPanel();
			tabSetup = new JTabbedPane();
			pnlFood = new JPanel();
			chkEat = new JCheckBox();
			lblFoodID = new JLabel();
			txtFoodID = new JTextField();
			radioB2P = new JRadioButton();
			pnlItem = new JPanel();
			chkPickup = new JCheckBox();
			chkCustom = new JCheckBox();
			txtCustom = new JTextField();
			lblCustomID = new JLabel();
			chkBones = new JCheckBox();
			pnlOther = new JPanel();
			chkAB = new JCheckBox();
			chkBury = new JCheckBox();
			chkPredict = new JCheckBox();
			lblGiant = new JLabel();
			lblStopScriptWhen = new JLabel();
			cbStat = new JComboBox();
			txtEndLVL = new JTextField();
			chkAPaint = new JCheckBox();
			pnlLicense = new JPanel();
			scrollPane1 = new JScrollPane();
			txtLicense = new JTextArea();
			pnlCredits = new JPanel();
			lblCredits = new JLabel();
			label1 = new JLabel();
			label2 = new JLabel();
			btnStart = new JButton();
			btnExit = new JButton();
			btnForum = new JButton();
			lblLogo = new JLabel();
			panel3 = new JPanel();
			chkEnable = new JCheckBox();

			// ======== this ========
			setResizable(false);
			setIconImage(new ImageIcon("http://binaryx.nl/xscripting/binaryx/mossgiantskiller/x.jpg").getImage());
			Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ======== tabMain ========
			{

				// ======== pnlSetup ========
				{
					pnlSetup.setLayout(null);

					// ======== tabSetup ========
					{

						// ======== pnlFood ========
						{
							pnlFood.setLayout(null);

							// ---- chkEat ----
							chkEat.setText("Eat Food");
							chkEat.setSelected(true);
							pnlFood.add(chkEat);
							chkEat.setBounds(0, 5, 75, chkEat
									.getPreferredSize().height);

							//---- lblStopScriptWhen ----
							lblStopScriptWhen.setText("Stop script when");
							pnlOther.add(lblStopScriptWhen);
							lblStopScriptWhen.setBounds(new Rectangle(new Point(120, 10), lblStopScriptWhen.getPreferredSize()));

							//---- cbStat ----
							cbStat.setSelectedIndex(-1);
							cbStat.setModel(new DefaultComboBoxModel(new String[]{
									"Attack",
									"Strength",
									"Defence",
									"Hitpoints",
									"Ranged ",
									"Magic"
							}));
							pnlOther.add(cbStat);
							cbStat.setBounds(120, 25, 70, cbStat.getPreferredSize().height);

							//---- txtEndLVL ----
							txtEndLVL.setText("");
							pnlOther.add(txtEndLVL);
							txtEndLVL.setBounds(195, 25, 40, txtEndLVL.getPreferredSize().height);

							//---- chkAPaint ----
							chkAPaint.setText("Advanced Paint");
							pnlOther.add(chkAPaint);
							chkAPaint.setBounds(new Rectangle(new Point(115, 45), chkAPaint.getPreferredSize()));
							chkAPaint.setSelected(true);
							pnlOther.add(chkEnable);
							chkEnable.setBounds(new Rectangle(new Point(160, 5), chkEnable.getPreferredSize()));

							// ---- lblFoodID ----
							lblFoodID.setText("Food ID:");
							pnlFood.add(lblFoodID);
							lblFoodID.setBounds(new Rectangle(
									new Point(225, 10), lblFoodID
											.getPreferredSize()));

							// ---- txtFoodID ----
							txtFoodID.setText("333");
							pnlFood.add(txtFoodID);
							txtFoodID.setBounds(270, 10, 60, 15);

							// ---- radioB2P ----
							radioB2P.setText("Bones to Peaches");
							pnlFood.add(radioB2P);
							radioB2P.setBounds(new Rectangle(new Point(0, 50),
									radioB2P.getPreferredSize()));

							{ // compute preferred size
								Dimension preferredSize = new Dimension();
								for (int i = 0; i < pnlFood.getComponentCount(); i++) {
									Rectangle bounds = pnlFood.getComponent(i)
											.getBounds();
									preferredSize.width = Math
											.max(bounds.x + bounds.width,
													preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height,
											preferredSize.height);
								}
								Insets insets = pnlFood.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								pnlFood.setMinimumSize(preferredSize);
								pnlFood.setPreferredSize(preferredSize);
							}
						}
						tabSetup.addTab("Food Settings", pnlFood);

						// ======== pnlItem ========
						{
							pnlItem.setLayout(null);

							// ---- chkPickup ----
							chkPickup.setText("Pickup Items");
							chkPickup.setSelected(true);
							pnlItem.add(chkPickup);
							chkPickup.setBounds(new Rectangle(new Point(5, 10),
									chkPickup.getPreferredSize()));

							// ---- chkCustom ----
							chkCustom.setText("Pickup Custom Items");
							pnlItem.add(chkCustom);
							chkCustom.setBounds(new Rectangle(new Point(5, 30),
									chkCustom.getPreferredSize()));

							// ---- txtCustom ----
							txtCustom.setText("Seperate ids by a space.");
							pnlItem.add(txtCustom);
							txtCustom.setBounds(65, 55, 145, 25);

							// ---- lblCustomID ----
							lblCustomID.setText("Item ID(s):");
							pnlItem.add(lblCustomID);
							lblCustomID.setBounds(new Rectangle(new Point(10,
									60), lblCustomID.getPreferredSize()));

							// ---- chkBones ----
							chkBones.setText("Pickup Bones");
							pnlItem.add(chkBones);
							chkBones.setBounds(new Rectangle(
									new Point(255, 10), chkBones
											.getPreferredSize()));

							{ // compute preferred size
								Dimension preferredSize = new Dimension();
								for (int i = 0; i < pnlItem.getComponentCount(); i++) {
									Rectangle bounds = pnlItem.getComponent(i)
											.getBounds();
									preferredSize.width = Math
											.max(bounds.x + bounds.width,
													preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height,
											preferredSize.height);
								}
								Insets insets = pnlItem.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								pnlItem.setMinimumSize(preferredSize);
								pnlItem.setPreferredSize(preferredSize);
							}
						}
						tabSetup.addTab("Item Pickup", pnlItem);

						// ======== pnlOther ========
						{
							pnlOther.setLayout(null);

							// ---- chkAB ----
							chkAB.setText("Use Antiban");
							pnlOther.add(chkAB);
							chkAB.setBounds(new Rectangle(new Point(5, 5),
									chkAB.getPreferredSize()));

							// ---- chkBury ----
							chkBury.setText("Bury Bones");
							pnlOther.add(chkBury);
							chkBury.setBounds(new Rectangle(new Point(5, 45),
									chkBury.getPreferredSize()));

							// ---- chkPredict ----
							chkPredict.setText("Use Hitpredicter");
							pnlOther.add(chkPredict);
							chkPredict.setBounds(new Rectangle(
									new Point(5, 25), chkPredict
											.getPreferredSize()));

							// ---- lblGiant ----
							lblGiant.setIcon(new ImageIcon(
									"http://binaryx.nl/xscripting/binaryx/mossgiantskiller/Mossgiantgui.png"));
							pnlOther.add(lblGiant);
							lblGiant.setBounds(265, 0, 120, 165);

							{ // compute preferred size
								Dimension preferredSize = new Dimension();
								for (int i = 0; i < pnlOther
										.getComponentCount(); i++) {
									Rectangle bounds = pnlOther.getComponent(i)
											.getBounds();
									preferredSize.width = Math
											.max(bounds.x + bounds.width,
													preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height,
											preferredSize.height);
								}
								Insets insets = pnlOther.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								pnlOther.setMinimumSize(preferredSize);
								pnlOther.setPreferredSize(preferredSize);
							}
						}
						tabSetup.addTab("Other Options", pnlOther);

					}
					pnlSetup.add(tabSetup);
					tabSetup.setBounds(10, 5, 370, 140);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < pnlSetup.getComponentCount(); i++) {
							Rectangle bounds = pnlSetup.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = pnlSetup.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						pnlSetup.setMinimumSize(preferredSize);
						pnlSetup.setPreferredSize(preferredSize);
					}
				}
				tabMain.addTab("Script Setup", pnlSetup);

				// ======== pnlLicense ========
				{
					pnlLicense.setLayout(null);

					// ======== scrollPane1 ========
					{

						// ---- txtLicense ----
						txtLicense.setBackground(new Color(204, 204, 204));
						txtLicense.setWrapStyleWord(true);
						txtLicense.setLineWrap(true);
						txtLicense.setEditable(false);
						scrollPane1.setViewportView(txtLicense);
					}
					pnlLicense.add(scrollPane1);
					scrollPane1.setBounds(5, 10, 365, 120);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < pnlLicense.getComponentCount(); i++) {
							Rectangle bounds = pnlLicense.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = pnlLicense.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						pnlLicense.setMinimumSize(preferredSize);
						pnlLicense.setPreferredSize(preferredSize);
					}
				}
				tabMain.addTab("Script License", pnlLicense);

				// ======== pnlCredits ========
				{
					pnlCredits.setLayout(null);

					// ---- lblCredits ----
					lblCredits.setText("BinaryX ~ Initial Script Development.");
					pnlCredits.add(lblCredits);
					lblCredits.setBounds(new Rectangle(new Point(10, 20),
							lblCredits.getPreferredSize()));

					// ---- label1 ----
					label1.setText("Antic - GFX");
					pnlCredits.add(label1);
					label1.setBounds(new Rectangle(new Point(10, 45), label1
							.getPreferredSize()));

					// ---- label2 ----
					label2
							.setText("SPECIAL THANKS TO ALL XSCRIPTING MEMBERS FOR HELPING.");
					pnlCredits.add(label2);
					label2.setBounds(new Rectangle(new Point(10, 85), label2
							.getPreferredSize()));

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < pnlCredits.getComponentCount(); i++) {
							Rectangle bounds = pnlCredits.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = pnlCredits.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						pnlCredits.setMinimumSize(preferredSize);
						pnlCredits.setPreferredSize(preferredSize);
					}
				}
				tabMain.addTab("Credits", pnlCredits);

			}
			contentPane.add(tabMain);
			tabMain.setBounds(5, 65, 385, 180);

			// ---- btnStart ----
			btnStart.addActionListener(this);
			btnStart.setText("Start");
			contentPane.add(btnStart);
			btnStart
					.setBounds(30, 250, 100, btnStart.getPreferredSize().height);

			// ---- btnExit ----
			btnExit.addActionListener(this);
			btnExit.setText("Exit");
			contentPane.add(btnExit);
			btnExit.setBounds(145, 250, 100, btnExit.getPreferredSize().height);

			// ---- btnForum ----
			btnForum.addActionListener(this);
			btnForum.setText("Forums");
			contentPane.add(btnForum);
			btnForum.setBounds(260, 250, 100,
					btnForum.getPreferredSize().height);

			// ---- lblLogo ----
			lblLogo.setIcon(new ImageIcon("http://binaryx.nl/xscripting/binaryx/mossgiantskiller/MossPic.png"));
			contentPane.add(lblLogo);
			lblLogo.setBounds(35, 5, 360, lblLogo.getPreferredSize().height);

			// ======== panel3 ========
			{
				panel3.setLayout(null);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for (int i = 0; i < panel3.getComponentCount(); i++) {
						Rectangle bounds = panel3.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width,
								preferredSize.width);
						preferredSize.height = Math.max(bounds.y
								+ bounds.height, preferredSize.height);
					}
					Insets insets = panel3.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel3.setMinimumSize(preferredSize);
					panel3.setPreferredSize(preferredSize);
				}
			}
			contentPane.add(panel3);
			panel3.setBounds(new Rectangle(new Point(0, 95), panel3
					.getPreferredSize()));

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width,
							preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height,
							preferredSize.height);
				}
				Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			pack();
			setLocationRelativeTo(getOwner());

			// ---- buttonGroup1 ----
			ButtonGroup buttonGroup1 = new ButtonGroup();
			buttonGroup1.add(radioB2P);
			// JFormDesigner - End of component initialization
			// //GEN-END:initComponents
		}

		public void actionPerformed(final ActionEvent arg0) {
			if (chkEat.isEnabled()) {
				eat_food = chkEat.isSelected();
			}
			if (chkPickup.isEnabled()) {
				PickupItems = chkPickup.isSelected();
			}
			if (chkAB.isEnabled()) {
				anti_ban = chkAB.isSelected();
			}
			if (chkCustom.isEnabled()) {
				PickupCustom = chkCustom.isSelected();
			}
			if (chkBones.isEnabled()) {
				PickupBones = chkBones.isSelected();
			}
			if (chkBury.isEnabled()) {
				BuryBones = chkBury.isSelected();
			}
			if (chkPredict.isEnabled()) {
				HitPredict = chkPredict.isSelected();
			}
			if (radioB2P.isEnabled()) {
				B2P_Tab = radioB2P.isSelected();
			}
			if (chkAPaint.isEnabled()) {
				AdvancedPaint = chkAPaint.isSelected();
			}
			if (eat_food) {
				food_id = Integer.parseInt(txtFoodID.getText());
			}
			if (chkEnable.isEnabled()) {
				stopAtLVL = chkEnable.isSelected();
			}
			if (stopAtLVL) {
				try {
					stopAtLvl = Integer.parseInt(txtEndLVL.getText());
					if (cbStat.getSelectedIndex() == 0) {
						SELECTED_STAT = STAT_ATTACK;
						SELECTED_STAT_NAME = "Attack";
					} else if (cbStat.getSelectedIndex() == 1) {
						SELECTED_STAT = STAT_STRENGTH;
						SELECTED_STAT_NAME = "Strength";
					} else if (cbStat.getSelectedIndex() == 2) {
						SELECTED_STAT = STAT_DEFENSE;
						SELECTED_STAT_NAME = "Defense";
					} else if (cbStat.getSelectedIndex() == 3) {
						SELECTED_STAT = STAT_HITPOINTS;
						SELECTED_STAT_NAME = "Hitpoints";
					} else if (cbStat.getSelectedIndex() == 4) {
						SELECTED_STAT = STAT_RANGE;
						SELECTED_STAT_NAME = "Range";
					} else if (cbStat.getSelectedIndex() == 5) {
						SELECTED_STAT = STAT_MAGIC;
						SELECTED_STAT_NAME = "Magic";
					}
				}
				// BAD
				catch (ArrayIndexOutOfBoundsException ignored) {
				}
				catch (NumberFormatException ignored) {
				}
			}
			if (arg0.getSource() == btnStart) {
				guiWait = false;
				guiExit = true;
				dispose();
			}
			if (arg0.getSource() == btnExit) {
				guiWait = true;
				guiExit = false;
				stopScript(true);
				dispose();
			}
			if (arg0.getSource() == btnForum) {
				launchURL("http://z10.invisionfree.com/xscripting/index.php?");
			}

		}

		public void valueChanged(ListSelectionEvent arg0) {

		}

	}

	@SuppressWarnings("unchecked")
	public void launchURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[]{String.class});
				openURL.invoke(null, url);
			} else if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);

			else { // assume Unix or Linux
				String[] browsers = {"firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape", "safari"};
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime().exec(
							new String[]{"which", browsers[count]})
							.waitFor() == 0)
						browser = browsers[count];
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else
					Runtime.getRuntime().exec(new String[]{browser, url});
			}
		} catch (Exception e) {
			log("Failed to open URL");
		}
	}

}