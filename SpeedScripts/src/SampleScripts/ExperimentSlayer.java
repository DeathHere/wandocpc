package SampleScripts;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = { "Pervy Shuya" }, category = "Combat", name = "ExperimentSlayer Pro", version = 3.8, description = "<style type='text/css'>body {background:url('http://img339.imageshack.us/img339/6561/11tf6.gif') no-repeat}</style><html><head><center><head></td><td><center>"
		+ "<html><head>"
		+ "</head><body>"
		+ "<center>"
		+ "<b><font size=\"6\" color=\"red\">"
		+ "Experiment Slayer Pro"
		+ "</font></b><br>"
		+ "<font size=\"3\" color=\"white\">Please start at Experiments, with armour.<br></font>"
		+ "<body style='font-family: Calibri; color:white; padding: 0px; text-align: center; background-color: black;'>"
		+ "</center>"
		+ "<center><h2>All settings can be set on the GUI, select your character and Start!</h2></center></td></tr><tr><td colspan=\"4\"><hr></td></tr>"
		+ "<b>Features:</b><br>"
		+ "<tr><td> Saves screenshot upon ending script"
		+ "<tr><td> Added garrent new awesome paint along with pray support"
		+ "<tr><td>New emergency teleport if Hp drops to 5 or below!"
		+ "<tr><td><b>System Update Detection!</b>"
		+ "<tr><td><b>New built-in AntiBan woot woot!</b>"
		+ "<tr><td><b> New intergrated Quick chat responder =D</b>"
		+ "<tr><td> -Script more flawless :D"
		+ "<tr><td> -Eats and logsout if no food in bag!"
		+ "<tr><td> -Added run control and the special attack! woop woop =)"
		+ "<tr><td> -Super smooth Npc attacking method"
		+ "<tr><td> -100% supports range!"
		+ "<tr><td> -Supports B2p now :D"
		+ "<tr><td> -Brand new updater! autoupdates itself asking for your permission</td></tr>"
		+ "<br>"
		+ "</table>"
		+ "<tr><td colspan=2><b><font size=\"3\" color=\"FF6600\">&nbsp&nbsp Thanks From Garrent for his new awesome paint design <3<3 and Vavenger1989 for the pickup ranging method, Credits to Taha for the awesome script update with the new gui and cleaner script code, thanks for Austin  for helping me with the bug of bot standing around not searching the npc's. Many Thanks to Myr for the speed and flawless changes in the attacking and searching of npc, (99% flawless) and iscream for the QC methods cheers buddies ;)</font></b></td></tr></table>"
		+ "</center>" + "</body></html>")
public class ExperimentSlayer extends Script implements PaintListener,
		ServerMessageListener {
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	final GarrettsPaint thePainter = new GarrettsPaint();
	ExperimentSlayerGUI gui;
	ExperimentSlayerAntiBan antiban;
	Thread t;
	String LOC;
	String LOG;
	private int[] ExperimentID = { 1678, 1677 };
	private RSTile[] Path3 = { new RSTile(3553, 9941), new RSTile(3549, 9932) },
			Path4 = reversePath(Path3);
	private long startTime = System.currentTimeMillis(), nextSpecialTime,
			lastAnti = 0;
	private String status = "Starting";
	private int ArrowID = -1, bronzeArrow = 882, ironArrow = 884,
			steelArrow = 886, mithrilArrow = 888, adamantArrow = 890,
			runeArrow = 892, bronzeBolt = 877, blueriteBolt = 9139,
			ironBolt = 9140, steelBolt = 9141, blackBolt = 13083,
			mithrilBolt = 9142, adamantBolt = 9143, runeBolt = 9144,
			broadBolt = 13280, bronzeKnife = 864, ironKnife = 863,
			steelKnife = 865, blackKnife = 869, mithrilKnife = 866,
			adamantKnife = 867, runeKnife = 868;
	private boolean doSpec, run4, run5, GetBones, check = true, guiWait = true,
			guiExit;
	String Eating, secondname, name;
	private final Rectangle ATTACK = new Rectangle(600, 273, 5, 5);
	private final Rectangle STRENGTH = new Rectangle(685, 275, 5, 5);
	private final Rectangle DEFENCE = new Rectangle(685, 326, 5, 5);
	private final Rectangle RANGE = new Rectangle(685, 275, 5, 5);
	private int[] FoodID = { 1895, 1893, 1891, 4293, 2142, 291, 2140, 3228,
			9980, 7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878, 7568,
			2343, 1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339, 351,
			329, 3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397, 391,
			3369, 3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335, 7178,
			7180, 7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003, 2011,
			2289, 2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893, 1895,
			1897, 1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066, 7068,
			1942, 6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993, 1989,
			1978, 5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907, 1911,
			5745, 2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084, 2034,
			2048, 2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277, 2225,
			2255, 2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092, 2032,
			2074, 2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883, 1971,
			4608, 1883, 1885 };
	private int EatatHP, bones = 526, /* Bones */ITPIDs = 526, count = 0,
			ExperimentKilled, ExperimentPerHour, hplvl, atkExp, atklvl, defExp,
			deflvl, startatkExp, startdefExp, startstrExp, startrangedExp,
			starthpExp, strExp, rangedExp, strlvl, rangedlvl, PaintStyle,
			attackStyle = -1, nextAnti = random(2000, 12000), STRgained,
			ATKgained, RGEgained, DEFgained, HPgained, hpExp;
	private int[] TeletabIDs = { 8007, 8008, 8009, 8010, 8011 }, /* Tele tabs */
	peach = { 6883, 1971, 1969 }, /* Peach */tabID = { 8015, 8015 }; // B2P tabs
	private int specialCost = 0, lastSpecialValue = 0;

	private double getVersion() {
		return getClass().getAnnotation(ScriptManifest.class).version();
	}

	/************************************************************
	 * ON START
	 ************************************************************/
	public boolean onStart(final Map<String, String> args) {
		gui = new ExperimentSlayerGUI();
		gui.setVisible(true);
		while (guiWait) {
			wait(100);
		}
		gui.setVisible(false);
		URLConnection url = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		if (JOptionPane
				.showConfirmDialog(
						null,
						"Would you like to check for updates?\nPlease Note this requires an internet connection and the script will write files to your harddrive!") == 0) {
			try {
				url = new URL(
						"http://www.rs2sell.co.cc/scripts/ExperimentSlayerVERSION.txt")
						.openConnection();
				in = new BufferedReader(new InputStreamReader(url
						.getInputStream()));
				if (Double.parseDouble(in.readLine()) > getVersion()) {
					if (JOptionPane.showConfirmDialog(null,
							"Update found. Do you want to update?") == 0) {
						JOptionPane
								.showMessageDialog(null,
										"Please choose 'ExperimentSlayer.java' in your scripts folder and hit 'Open'");
						JFileChooser fc = new JFileChooser();
						if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							url = new URL(
									"http://www.rs2sell.co.cc/scripts/ExperimentSlayer.java")
									.openConnection();
							in = new BufferedReader(new InputStreamReader(url
									.getInputStream()));
							out = new BufferedWriter(new FileWriter(fc
									.getSelectedFile().getPath()));
							String inp;
							while ((inp = in.readLine()) != null) {
								out.write(inp);
								out.newLine();
								out.flush();
							}
							log("Script successfully downloaded. Please recompile and reload your scripts!");
							return false;
						} else
							log("Update canceled");
					} else
						log("Update canceled");
				} else
					JOptionPane.showMessageDialog(null,
							"You have the latest version. :)");
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				log("Problem getting version :/");
				return false;
			}
		}
		final int ads = JOptionPane.showConfirmDialog(null,
				"Would you like to support me by donating?", "Starting Up",
				JOptionPane.YES_NO_OPTION);
		if (ads == 0) {
			final String message = "<html><b>Thank you for your support!</b><br/>"
					+ "<p>This will give me encouragement to write more scripts.<br/>"
					+ "This is a BIG help.  Thank you.<br/>"
					+ "(You will be redirected to the donate page)<br/>"
					+ "</html>";
			JOptionPane.showMessageDialog(null, message);
			openURL("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=XPQ2H6AUTAKXU&lc=GB&item_name=ExperimentSlayez%20Script%20Service"
					+ "&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest");
		}
		antiban = new ExperimentSlayerAntiBan();
		t = new Thread(antiban);
		if (isLoggedIn()) {
			setAttackStyle();
			wait(random(500, 700));
			openTab(Constants.TAB_INVENTORY);
		}
		if (isLoggedIn()) {
			return true && !guiExit;
		} else {
			log("You must be logged in to START this script.");
			return false;
		}
	}

	private void setAttackStyle() {// Credits to Hatred for this method
		status = "Prefrences";
		switch (attackStyle) {
		case 0: // Attack
			openTab(Constants.TAB_ATTACK);
			wait(random(400, 600));
			clickMouse(ATTACK.x, ATTACK.y, ATTACK.width, ATTACK.height, true);
			wait(random(400, 600));
			break;
		case 1: // Strength
			openTab(Constants.TAB_ATTACK);
			wait(random(400, 600));
			clickMouse(STRENGTH.x, STRENGTH.y, STRENGTH.width, STRENGTH.height,
					true);
			wait(random(400, 600));
			break;
		case 2: // Defence
			openTab(Constants.TAB_ATTACK);
			wait(random(400, 600));
			clickMouse(DEFENCE.x, DEFENCE.y, DEFENCE.width, DEFENCE.height,
					true);
			wait(random(400, 600));
			break;
		case 3: // Range
			openTab(Constants.TAB_ATTACK);
			wait(random(400, 600));
			clickMouse(RANGE.x, RANGE.y, RANGE.width, RANGE.height, true);
			wait(random(400, 600));
			break;
		}
		openTab(Constants.TAB_INVENTORY);
		wait(random(600, 700));
	}

	private void openURL(final String url) {
		final String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				final Class<?> fileMgr = Class
						.forName("com.apple.eio.FileManager");
				final Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			} else {
				final String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "google chrome", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(
							new String[] { "which", browsers[count] })
							.waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else {
					Runtime.getRuntime().exec(new String[] { browser, url });
				}
			}
		} catch (final Exception e) {
		}
	}

	private void CheckExperimentKC() {
		if (ExperimentKilled == 1)
			log("First Blood! One down many to fuck XD");
		if (ExperimentKilled == 5)
			log("Our Trainging begins :3");
		if (ExperimentKilled == 10)
			log("10 dead... how many to to rape?");
		if (ExperimentKilled == 50)
			log("50 slayed woot! experiment rage ^^");
		if (ExperimentKilled == 100)
			log("Monster kill!! 100 Experiments dead, Nice one!");
		if (ExperimentKilled == 250)
			log("Killing spree! 250 Experiments slaughter, Alright owning!");
		if (ExperimentKilled == 500)
			log("Boom rapeshot!!, you already killed 500 Experiments!");
		if (ExperimentKilled == 999)
			log("Holy shit! you just raped your 999th Experiment!");
		if (ExperimentKilled == 1500)
			log("Godlike! 1500 Experiment raped and bang up!");
		if (ExperimentKilled == 2000)
			log("Dominating! 2000 Experiment pwned and owned!");
		if (ExperimentKilled == 5000)
			log("Ludichris Kill! 5000 Experiment wasted!");
	}

	private void HandleArrows() {
		if (ArrowID != -1) {
			RSItemTile ARROW_TILE = getGroundItemByID(ArrowID);
			status = "Picking up Arrows";
			log("Picking up Arrows");
			atTile(ARROW_TILE, "Take");
			wait(random(400, 600));
		}
	}

	private void EquipArrows() {
		if (ArrowID != -1 && inventoryContains(ArrowID)) {
			if (getCurrentTab() != TAB_INVENTORY) {
				openTab(TAB_INVENTORY);
			}
			status = "Equiping Arrows";
			log("Equiping Arrows");
			atInventoryItem(ArrowID, "Wield");
		}
	}

	private void pickOI() { // made by Jummainen
		getInventoryItemByID(ITPIDs);
		int OldOitemCount = getInventoryCount(ITPIDs);
		RSItemTile TItem = getGroundItemByID(4, ITPIDs);
		if (TItem != null && getInventoryCount() < 26
				&& canPickUpItem(TItem.getItem().getID())) {
			atTile(TItem, "Take");
			while (getMyPlayer().isMoving()) {
				wait(15);
			}
			if (getInventoryCount(ITPIDs) > OldOitemCount) {
				count = count + 1;
				log("Picked up item.");
				return;
			} else {
				log("No item found.");
				return;
			}
		}
		return;
	}

	private void checkForFood() {
		if (skills.getCurrentSkillLevel(3) <= EatatHP) {
			log("Food time");
			if (getInventoryCount(peach) >= 1) {
				clickInventoryItem(peach, "Eat");
				wait(random(1000, 2000));
			} else {
				log("Emergency tab time");
				clickInventoryItem(tabID, "Break");
				wait(random(2000, 3000));
			}
			if (getInventoryCount(bones) >= 20) {
				if (getInventoryCount(tabID) >= 1) {
					clickInventoryItem(tabID, "Break");
					log("Made more peaches");
					wait(random(2000, 3000));
				} else {
					log("We are out of tablets!");
					wait(5000);
					logout();
					stopScript();
				}
			}
		}
	}

	private int clickSpec() {
		status = " Doing Special attack";
		openTab(Constants.TAB_ATTACK);
		wait(random(200, 500));
		clickMouse(650, 423, 20, 5, true);
		openTab(Constants.TAB_INVENTORY);
		return random(200, 300);
	}

	private boolean canUseSpecial() {
		final int SPECIAL_BAR_VAL = getSetting(300);
		final int SPECIAL_IS_ON = getSetting(301);
		switch (SPECIAL_IS_ON) {
		case 0:
			if (specialCost == 0) {
				if (SPECIAL_BAR_VAL < lastSpecialValue) {
					specialCost = lastSpecialValue - SPECIAL_BAR_VAL;
					return false;
				}
			}
			if (SPECIAL_BAR_VAL >= specialCost
					&& System.currentTimeMillis() >= nextSpecialTime) {
				nextSpecialTime = System.currentTimeMillis()
						+ random(5000, 45000);
				return true;
			}
		case 1:
			lastSpecialValue = SPECIAL_BAR_VAL;
			return false;
		}
		return false;
	}

	private int speed;

	protected int getMouseSpeed() {
		return speed;
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

	private void drawPlayer(final Graphics g) {
		final RSTile t = getMyPlayer().getLocation();
		Calculations.tileToScreen(t);
		final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
		final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
				0, 0);
		final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
				0, 0);
		final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
				0, 0, 0);
		getMyPlayer().getHeight();
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
		g.setColor(new Color(240, 240, 240, 75));
		g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
	}

	private void overlayTile(final Graphics g, final RSTile t, final Color c) {
		final Point p = Calculations.tileToScreen(t);
		final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
		final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
				0, 0);
		final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
				0, 0);
		final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
				0, 0, 0);
		final Point[] points = { p, pn, px, py, pxy };
		for (final Point point : points) {
			if (!pointOnScreen(point)) {
				return;
			}
		}
		g.setColor(c);
		g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
		g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
	}

	private int mouseAntiban() {
		if (lastAnti == 0) {
			lastAnti = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() - lastAnti > nextAnti) {
			final int r = random(0, 2);
			status = "Moving mouse around";
			if (r == 1 || r == 2) {
				moveMouse(random(0, 763), random(0, 503));
			}

			lastAnti = System.currentTimeMillis();
			nextAnti = random(2000, 15000);
		}
		return random(100, 600);
	}

	private int camaraAntiBan() {
		final int randomAction = random(0, 6);
		status = "Moving camara around";
		switch (randomAction) {
		case 0:
			final int currentAngle = getCameraAngle();
			switch (random(0, 1)) {
			case 0:
				setCameraRotation(currentAngle + random(0, 650));
				return random(710, 1700);
			case 1:
				setCameraRotation(currentAngle - random(0, 650));
				return random(710, 1700);
			}
		case 1:
			final int currentAlt = Bot.getClient().getCamPosZ();
			final int randomz = random(0, 10);
			if (randomz <= 7) {
				setCameraAltitude(currentAlt - random(0, 100));
				return random(410, 2130);
			} else {
				setCameraAltitude(currentAlt + random(0, 100));
				return random(410, 2130);
			}
		case 2:
			int currentAngle2 = getCameraAngle();
			Bot.getClient().getCamPosZ();
			switch (random(0, 1)) {
			case 0:
				setCameraRotation(currentAngle2 + random(0, 650));
				setCameraAltitude(random(80, 100));
				return random(410, 2130);
			case 1:
				setCameraRotation(currentAngle2 - random(0, 650));
				setCameraAltitude(random(40, 80));
				return random(410, 2130);
			}
		case 3:
			if (ExperimentID != null && getMyPlayer().getInteracting() != null) {
				turnToTile(getNearestFreeNPCByID(ExperimentID).getLocation());
				return random(120, 2600);
			} else {
				currentAngle2 = getCameraAngle();
				Bot.getClient().getCamPosZ();
				switch (random(0, 1)) {
				case 0:
					setCameraRotation(currentAngle2 + random(0, 650));
					setCameraAltitude(random(80, 100));
					return random(410, 2130);
				case 1:
					setCameraRotation(currentAngle2 - random(0, 650));
					setCameraAltitude(random(40, 80));
					return random(410, 2130);
				}
			}

		}
		return random(100, 200);
	}

	private boolean clickInventoryItem(final int[] ids, final String command) {
		try {
			if (getCurrentTab() != Constants.TAB_INVENTORY
					&& !RSInterface.getInterface(Constants.INTERFACE_BANK)
							.isValid()
					&& !RSInterface.getInterface(Constants.INTERFACE_STORE)
							.isValid()) {
				openTab(Constants.TAB_INVENTORY);
			}
			final int[] items = getInventoryArray();
			final java.util.List<Integer> possible = new ArrayList<Integer>();
			for (int i = 0; i < items.length; i++) {
				for (final int item : ids) {
					if (items[i] == item) {
						possible.add(i);
					}
				}
			}
			if (possible.size() == 0) {
				return false;
			}
			final int idx = possible.get(random(0, possible.size()));
			final Point t = getInventoryItemPoint(idx);
			moveMouse(t, 5, 5);
			wait(random(100, 290));
			if (getMenuActions().get(0).equals(command)) {
				clickMouse(true);
				return true;
			} else {
				// clickMouse(false);
				return atMenu(command);
			}
		} catch (final Exception e) {
			log.log(Level.SEVERE, "clickInventoryFood(int...) error: ", e);
			return false;
		}
	}

	private boolean canPickUpItem(int itemID) {
		if (getInventoryCount() < 28) {
			return true;
		} else {
			if (getInventoryCount(itemID) > 0
					&& getInventoryItemByID(itemID).getStackSize() > 1) {
				return true;
			}
		}
		return false;
	}

	private void run4() {
		// Attack

		if (RSInterface.getInterface(137).containsText(
				name + "My Attack level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Attack?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("Z", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Attack level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137)
					.containsText("My Attack level is")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Attack level.")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		// Defence>

		if (RSInterface.getInterface(137).containsText(
				name + "My Defence level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Defence?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("D", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Defence level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"My Defence level is")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Defence level.")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		// Hitpoints>

		if (RSInterface.getInterface(137).containsText(
				name + "My Hitpoints level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Hitpoints?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("L", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Hitpoints level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"My Hitpoints level is")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Hitpoints level.")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		// Magic>

		if (RSInterface.getInterface(137).containsText(
				name + "My Magic level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Magic?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("M", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Magic level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText("My Magic level is")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Magic level.")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		// Prayer>

		if (RSInterface.getInterface(137).containsText(
				name + "My Prayer level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Prayer?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("y", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Prayer level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137)
					.containsText("My Prayer level is")) {
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Prayer level.")) {
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}

		// Ranged>

		if (RSInterface.getInterface(137).containsText(
				name + "My Ranged level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Ranged?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("r", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Ranged level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137)
					.containsText("My Ranged level is")) {
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Ranged level.")) {
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}

		// Strength>

		if (RSInterface.getInterface(137).containsText(
				name + "My Strength level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Strength?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Strength level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"My Strength level is")) {
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Strength level.")) {
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}
		check = true;
	}

	private void run5() {
		// Agility

		if (RSInterface.getInterface(137).containsText(
				name + "My Agility level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Agility?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("A", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Agility level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"My Agility level is")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("G", false);
				wait(random(400, 800));
				sendText("C", false);
				wait(random(400, 800));
				sendText("G", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "nty")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"Let's go to Agility course")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("n", false);
				wait(random(400, 800));
				sendText("j", false);
				wait(random(400, 800));
				sendText("5", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "nty")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText("Race you!")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("n", false);
				wait(random(400, 800));
				sendText("j", false);
				wait(random(400, 800));
				sendText("5", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "idk")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"Are there any Agility shortcuts near here?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("n", false);
				wait(random(400, 800));
				sendText("a", false);
				wait(random(400, 800));
				sendText("5", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "idk")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"Where's a good place for me to train Agility?")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("n", false);
				wait(random(400, 800));
				sendText("a", false);
				wait(random(400, 800));
				sendText("5", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Agility level.")) {
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		// Hunter>

		if (RSInterface.getInterface(137).containsText(
				name + "My Hunter level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Hunter?")) {
				check = false;
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("U", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Hunter level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137)
					.containsText("My Hunter level is")) {
				check = false;
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Hunter level.")) {
				check = false;
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("c", false);
				wait(random(400, 800));
				sendText("g", false);
				wait(random(400, 800));
				sendText("1", false);
				wait(random(400, 800));
			}
		}

		// Slayer>

		if (RSInterface.getInterface(137).containsText(
				name + "My Slayer level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Slayer?")) {
				check = false;
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("s", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Slayer level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137)
					.containsText("My Slayer level is")) {
				check = false;
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Slayer level.")) {
				check = false;
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}

		// Summoning>

		if (RSInterface.getInterface(137).containsText(
				name + "My Summoning level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"What is your level in Summoning?")) {
				check = false;
				check = false;
				sendText("", true);
				wait(random(400, 800));
				sendText("S", false);
				wait(random(400, 800));
				sendText("o", false);
				wait(random(400, 800));
				sendText("2", false);
				wait(random(400, 800));
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")
				|| RSInterface.getInterface(137).containsText(
						name + "My Summoning level is")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"My Summoning level is")) {
				check = false;
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}

		if (RSInterface.getInterface(137).containsText(name + "That rocks!")
				|| RSInterface.getInterface(137).containsText(
						name + "That's good.")) {
			// using for later

		} else {
			if (RSInterface.getInterface(137).containsText(
					"experience to get my next Summoning level.")) {
				check = false;
				if (r == 1) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("1", false);
					wait(random(400, 800));

				}
				if (r == 2) {
					check = false;
					sendText("", true);
					wait(random(400, 800));
					sendText("g", false);
					wait(random(400, 800));
					sendText("c", false);
					wait(random(400, 800));
					sendText("e", false);
					wait(random(400, 800));
					sendText("9", false);
					wait(random(400, 800));
				}
			}
		}

		check = true;
	}

	/************************************************************
	 * MAIN LOOP
	 ************************************************************/
	private int r;

	public int loop() {
		r = random(1, 2);
		if (!t.isAlive()) {
			t.start();
			log("Fagex Antiban Hammer initialized!");
		}
		if (RSInterface.getInterface(137).containsText("Search for")) {
			log("To Prevent Interruption or Ban We Have Paused the Responder.");

			check = false;
			return -1;
		}

		if (isLoggedIn()) {
			secondname = getMyPlayer().getName();
			name = secondname + ": <col=0000ff>";

			if (check == false) {
				wait(1000);
			} else {

				if (run4 && check == true) {
					run4();
				}
				if (run5 && check == true) {
					run5();
				}
			}
		}
		int random10 = random(1, 11);
		int HP = skills.getCurrentSkillLevel(STAT_HITPOINTS);
		if (GetBones) {
			checkForFood();
			pickOI();
		}
		thePainter.scriptRunning = true;

		if (!thePainter.savedStats)
			thePainter.saveStats();
		if (HP <= EatatHP) {
			if (getInventoryCount(FoodID) >= 1) {
				status = "Eating food";
				clickInventoryItem(FoodID, "Eat");
				return (random(450, 650));
			} else {
				if (getInventoryCount(FoodID) == 0) {
					status = "Out of food! shutting down";
					log("We are out of food! logging out");
					wait(8000);
					logout();
					stopScript();
				}
			}
		}
		if (HP <= 5) {
			if (getInventoryCount(TeletabIDs) >= 1) {
				status = "Teleporting";
				clickInventoryItem(TeletabIDs, "Break");
				log("Emergency teleporting.....Now logging out");
				wait(5000);
				logout();
				stopScript();
			}
		}
		if (getEnergy() > random(60, 100))
			setRun(true);
		if (getMyPlayer().isMoving())
			return random(1, 2);
		HandleArrows();
		RSItemTile ARROW_TILE = getGroundItemByID(ArrowID);
		if (ARROW_TILE != null) {
			return 100;
		}
		if (random10 == 2) {
			EquipArrows();
		}
		if (doSpec) {
			if (canUseSpecial()) {
				clickSpec();
			}
		}
		if (!getMyPlayer().isInCombat() && getNearestNPCByName("Evil") == null) {
			if (getMyPlayer().getInteracting() != null) {
				return random(1, 2);
			}

			if (getNearestNPCByName("Evil") != null
					|| getNearestNPCByName("Swarm") != null) {
				walkPathMM(randomizePath(Path4, 2, 2), 20);
			}

			if (getMyPlayer().isIdle()) {
				final int r = random(0, 4);
				status = "We are Idle";
				if (r == 1 || r == 3) {
					camaraAntiBan();
				} else if (r == 2 || r == 4) {
					mouseAntiban();
				}
			}

			if (getNearestFreeNPCByID(ExperimentID) != null) {
				Point location = Calculations
						.tileToScreen(getNearestFreeNPCByID(ExperimentID)
								.getLocation());
				if (location != null) {
					if (pointOnScreen(location)) {
						status = "Killing experiment";
						atNPC(getNearestFreeNPCByID(ExperimentID), "Attack");
						wait(random(1, 50));
						return 1;
					}
				}
			}

			if (getMyPlayer().isMoving()) {
				wait(50);
				CheckExperimentKC();
				return 1;
			}
		}

		if (getMyPlayer().getInteracting() == null
				&& getNearestFreeNPCByID(ExperimentID) != null) {
			status = "Finding experiments";
			walkTo(getNearestFreeNPCByID(ExperimentID).getLocation());
			wait(20);
			return 1;
		}
		return (random(20, 30));
	}

	public void serverMessageRecieved(ServerMessageEvent arg0) {
		String serverString = arg0.getMessage();

		if (serverString.contains("<col=ffff00>System update in")) {
			log("There will be a system update soon, so we logged out");
			logout();
			stopScript();
		}
		if (serverString.contains("Oh dear, you are dead!")) {
			status = "Dead";
			log("We somehow died :S, shutting down");
			logout();
			stopScript();
		}
		if (serverString.contains("There is no ammo left in your quiver.")) {
			log("We have no arrows left, shutting down!");
			logout();
			stopScript();
		}
		if (serverString.contains("You've just advanced")) {
			log("Woo a level! hurray 4 pwning experiments");
			atklvl++;
			deflvl++;
			strlvl++;
			hplvl++;
			rangedlvl++;
			wait(random(1500, 2500));
			if (canContinue()) {
				clickContinue();
			}

		}
	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(true);
		log.info(": You have gained " + hplvl + " hp levels + " + HPgained
				+ " hp experience.");
		if (PaintStyle == 1) {
			log.info(": You have gained " + atklvl + " attack levels + "
					+ ATKgained + " attack experience.");
		}
		if (PaintStyle == 3) {
			log.info(": You have gained " + deflvl + " defense levels + "
					+ DEFgained + " defense experience.");
		}
		if (PaintStyle == 2) {
			log.info(": You have gained " + strlvl + " strength levels + "
					+ STRgained + " strength experience.");
		}
		if (PaintStyle == 4) {
			log.info(": You have gained " + rangedlvl + " range levels + "
					+ RGEgained + " range experience.");
		}
		log("You Raped " + ExperimentKilled + " Experiments");
		log("\u2020\u2020\u2020\u2020\u2020\u2020Stopping Experiment Slayer\u2020\u2020\u2020\u2020\u2020\u2020");
		log("<3<3\u2020Bye Bye Mr Experiment Rapist\u2020<3<3");
		antiban.stopThread = true;
		logout();
	}

	/************************************************************
	 * PAINT
	 ************************************************************/
	public void onRepaint(Graphics g) {
		thePainter.paint(g);
		drawPlayer(g);
		drawMouse(g);
		// Credits to RcZhang for the Paint mouse and the Paint tiles
		if (getNearestFreeNPCByID(ExperimentID) != null) {
			if (tileOnScreen(getNearestFreeNPCByID(ExperimentID).getLocation())) {
				overlayTile(g, getNearestFreeNPCByID(ExperimentID)
						.getLocation(), new Color(200, 0, 0, 75));
			}
			if (tileOnMap(getNearestFreeNPCByID(ExperimentID).getLocation())) {
				g.setColor(new Color(0, 255, 0, 255));
				g.fillOval(tileToMinimap(getNearestFreeNPCByID(ExperimentID)
						.getLocation()).x - 3,
						tileToMinimap(getNearestFreeNPCByID(ExperimentID)
								.getLocation()).y - 3, 6, 6);
			}
		}
	}

	// Credits to Garrent for his paint
	class GarrettsPaint {

		final Rectangle r = new Rectangle(7, 345, 408, 114);
		final Rectangle r1 = new Rectangle(420, 345, 77, 25);
		final Rectangle r2 = new Rectangle(420, 374, 77, 26);
		final Rectangle r3 = new Rectangle(420, 404, 77, 26);
		final Rectangle r4 = new Rectangle(420, 434, 77, 25);
		final Rectangle r2c = new Rectangle(415, 374, 5, 26);
		final Rectangle r3c = new Rectangle(415, 404, 5, 26);
		final Rectangle r4c = new Rectangle(415, 434, 5, 25);
		final Rectangle sb1 = new Rectangle(12, 350, 398, 12);
		final Rectangle sb2 = new Rectangle(12, 363, 398, 12);
		final Rectangle sb3 = new Rectangle(12, 376, 398, 12);
		final Rectangle sb4 = new Rectangle(12, 389, 398, 12);
		final Rectangle sb5 = new Rectangle(12, 402, 398, 12);
		final Rectangle sb6 = new Rectangle(12, 415, 398, 12);
		final Rectangle sb7 = new Rectangle(12, 428, 398, 12);
		final Rectangle sb8 = new Rectangle(12, 441, 398, 12);
		final Rectangle sb1s = new Rectangle(12, 350, 196, 12);
		final Rectangle sb2s = new Rectangle(12, 363, 196, 12);
		final Rectangle sb3s = new Rectangle(12, 376, 196, 12);
		final Rectangle sb4s = new Rectangle(12, 389, 196, 12);
		final Rectangle sb5s = new Rectangle(12, 402, 196, 12);
		final Rectangle sb6s = new Rectangle(12, 415, 196, 12);
		final Rectangle sb7s = new Rectangle(12, 428, 196, 12);
		final Rectangle sb8s = new Rectangle(12, 441, 196, 12);
		final Rectangle sb9s = new Rectangle(213, 350, 196, 12);
		final Rectangle sb10s = new Rectangle(213, 363, 196, 12);
		final Rectangle sb11s = new Rectangle(213, 376, 196, 12);
		final Rectangle sb12s = new Rectangle(213, 389, 196, 12);
		final Rectangle sb13s = new Rectangle(213, 402, 196, 12);
		final Rectangle sb14s = new Rectangle(213, 415, 196, 12);
		final Rectangle sb15s = new Rectangle(213, 428, 196, 12);
		final Rectangle sb16s = new Rectangle(213, 441, 196, 12);
		Rectangle[] skillBars = new Rectangle[] { sb1, sb2, sb3, sb4, sb5, sb6,
				sb7, sb8 };
		boolean savedStats = false;
		boolean scriptRunning = false;
		boolean checkedCount = false;
		int currentTab = 0;
		int lastTab = 0;
		int[] barIndex = new int[16];
		int[] start_exp = null;
		int[] start_lvl = null;
		int[] gained_exp = null;
		int[] gained_lvl = null;

		Thread mouseWatcher = new Thread();
		final NumberFormat nf = NumberFormat.getInstance();

		final long time_ScriptStart = System.currentTimeMillis();
		long runTime = System.currentTimeMillis() - time_ScriptStart;

		int sine = 0;
		int sineM = 1;

		void paint(final Graphics g) {
			if (!isLoggedIn() || !scriptRunning)
				return;

			// credits to Jacmob for the pulsing
			if (sine >= 84) {
				sine = 84;
				sineM *= -1;
			} else if (sine <= 1) {
				sine = 1;
				sineM *= -1;
			}
			sine += sineM;

			runTime = System.currentTimeMillis() - time_ScriptStart;
			final String formattedTime = formatTime((int) runTime);

			currentTab = paintTab();

			switch (currentTab) {
			case -1: // PAINT OFF
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(r1.x, r1.y, r1.width, r1.height);
				g.setColor(Color.WHITE);
				drawString(g, "Show Paint", r1, 5);
				break;
			case 0: // DEFAULT TAB - MAIN
				drawPaint(g, r2c);
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(Color.WHITE);
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				drawString(g, properties.name(), r, -40);
				g.setFont(new Font("sansserif", Font.PLAIN, 12));
				drawStringMain(g, "Runtime: ", formattedTime, r, 20, 35, 0,
						true);
				long millis = System.currentTimeMillis() - startTime;
				long hours = millis / (1000 * 60 * 60);
				millis -= hours * (1000 * 60 * 60);
				long minutes = millis / (1000 * 60);
				millis -= minutes * (1000 * 60);
				long seconds = millis / 1000;
				final int XPgained;
				if (startatkExp == 0)
					startatkExp = skills.getCurrentSkillExp(STAT_ATTACK);
				if (startstrExp == 0)
					startstrExp = skills.getCurrentSkillExp(STAT_STRENGTH);
				if (startdefExp == 0)
					startdefExp = skills.getCurrentSkillExp(STAT_DEFENSE);
				if (startrangedExp == 0)
					startrangedExp = skills.getCurrentSkillExp(STAT_RANGE);
				if (starthpExp == 0)
					starthpExp = skills.getCurrentSkillExp(STAT_HITPOINTS);
				XPgained = (atkExp - startatkExp) + (strExp - startstrExp)
						+ (defExp - startdefExp) + (rangedExp - startrangedExp);
				ATKgained = (atkExp - startatkExp);
				STRgained = (strExp - startstrExp);
				RGEgained = (defExp - startdefExp);
				HPgained = (hpExp - starthpExp);
				ExperimentKilled = (XPgained / 400);
				final int xpHour = ((int) ((3600000.0 / (double) runTime) * XPgained));
				ExperimentPerHour = (xpHour / 400);
				atkExp = skills.getCurrentSkillExp(STAT_ATTACK);
				strExp = skills.getCurrentSkillExp(STAT_STRENGTH);
				defExp = skills.getCurrentSkillExp(STAT_DEFENSE);
				rangedExp = skills.getCurrentSkillExp(STAT_RANGE);
				hpExp = skills.getCurrentSkillExp(STAT_HITPOINTS);
				float xpsec = 0;
				if ((minutes > 0 || hours > 0 || seconds > 0) && XPgained > 0) {
					xpsec = ((float) XPgained)
							/ (float) (seconds + (minutes * 60) + (hours * 60 * 60));
				}
				float xpmin = xpsec * 60;
				float xphour = xpmin * 60;
				drawStringMain(g, "Experiments Raped: ", Integer
						.toString(ExperimentKilled), r, 20, 35, 2, true);
				drawStringMain(g, "Status: ", status, r, 20, 35, 2, false);
				drawStringMain(g, "Experiments Xp / Hour: ", Integer
						.toString((int) xphour), r, 20, 35, 3, true);
				drawStringMain(g, "Total XP Gained: ", Integer
						.toString(XPgained), r, 20, 35, 3, false);
				drawStringMain(g, "Experiments Kills/ Hour: ", Integer
						.toString(ExperimentPerHour), r, 20, 35, 4, true);
				drawStringMain(g, "Location: ", "Experiments Cave", r, 20, 35,
						4, false);
				break;
			case 1: // INFO
				drawPaint(g, r3c);
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(Color.WHITE);
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				drawString(g, properties.name(), r, -40);
				g.setFont(new Font("sansserif", Font.PLAIN, 12));
				drawStringMain(g, "Version: ", Double.toString(properties
						.version()), r, 20, 35, 0, true);
				break;
			case 2: // STATS
				drawPaint(g, r4c);
				drawStats(g);
				hoverMenu(g);
				break;
			}
		}

		void saveStats() {
			nf.setMinimumIntegerDigits(2);
			final String[] stats = Skills.statsArray;
			start_exp = new int[stats.length];
			start_lvl = new int[stats.length];
			for (int i = 0; i < stats.length; i++) {
				start_exp[i] = skills.getCurrentSkillExp(i);
				start_lvl[i] = skills.getCurrentSkillLevel(i);
			}
			for (int i = 0; i < barIndex.length; i++) {
				barIndex[i] = -1;
			}
			savedStats = true;
		}

		int paintTab() {
			final Point mouse = new Point(Bot.getClient().getMouse().x, Bot
					.getClient().getMouse().y);
			if (mouseWatcher.isAlive())
				return currentTab;
			if (r1.contains(mouse)) {
				mouseWatcher = new Thread(new MouseWatcher(r1));
				mouseWatcher.start();
				if (currentTab == -1) {
					return lastTab;
				} else {
					lastTab = currentTab;
					return -1;
				}
			}
			if (currentTab == -1)
				return currentTab;
			if (r2.contains(mouse))
				return 0;
			if (r3.contains(mouse))
				return 1;
			if (r4.contains(mouse))
				return 2;
			return currentTab;
		}

		void drawPaint(final Graphics g, final Rectangle rect) {
			g.setColor(new Color(0, 0, 0, 230));
			g.fillRect(r1.x, r1.y, r1.width, r1.height);
			g.fillRect(r2.x, r2.y, r2.width, r2.height);
			g.fillRect(r3.x, r3.y, r3.width, r3.height);
			g.fillRect(r4.x, r4.y, r4.width, r4.height);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			g.fillRect(r.x, r.y, r.width, r.height);
			g.setColor(Color.WHITE);
			drawString(g, "Hide Paint", r1, 5);
			drawString(g, "MAIN", r2, 5);
			drawString(g, "INFO", r3, 5);
			drawString(g, "STATS", r4, 5);
			g.setColor(new Color(0, 0, 0, 230));
		}

		void drawStat(final Graphics g, final int index, final int count) {
			if (count >= skillBars.length && !checkedCount) {
				skillBars = new Rectangle[] { sb1s, sb2s, sb3s, sb4s, sb5s,
						sb6s, sb7s, sb8s, sb9s, sb10s, sb11s, sb12s, sb13s,
						sb14s, sb15s, sb16s };
				checkedCount = true;
			}
			if (count >= skillBars.length)
				return;
			g.setFont(new Font("serif", Font.PLAIN, 11));
			g.setColor(new Color(100, 100, 100, 150));
			g.fillRect(skillBars[count].x, skillBars[count].y,
					skillBars[count].width, skillBars[count].height);
			final int percent = skills.getPercentToNextLevel(index);
			g.setColor(new Color(255 - 2 * percent,
					(int) (1.7 * percent + sine), 0, 150));
			g
					.fillRect(
							skillBars[count].x,
							skillBars[count].y,
							(int) (((double) skillBars[count].width / 100.0) * (double) percent),
							skillBars[count].height);
			g.setColor(Color.WHITE);
			final String name = Skills.statsArray[index];
			final String capitalized = name.substring(0, 1).toUpperCase()
					+ name.substring(1);
			g.drawString(capitalized, skillBars[count].x + 2,
					skillBars[count].y + 10);
			drawStringEnd(g, percent + "%", skillBars[count], -2, 4);
			barIndex[count] = index;
		}

		void drawStats(final Graphics g) {
			final String[] stats = Skills.statsArray;
			int count = 0;
			gained_exp = new int[stats.length];
			gained_lvl = new int[stats.length];
			for (int i = 0; i < stats.length; i++) {
				gained_exp[i] = skills.getCurrentSkillExp(i) - start_exp[i];
				gained_lvl[i] = skills.getCurrentSkillLevel(i) - start_lvl[i];
				if (gained_exp[i] > 0) {
					drawStat(g, i, count);
					count++;
				}
			}
		}

		void hoverMenu(final Graphics g) {
			final Point mouse = new Point(Bot.getClient().getMouse().x, Bot
					.getClient().getMouse().y);
			final Rectangle r_main = new Rectangle(mouse.x, mouse.y - 150, 300,
					150);
			for (int i = 0; i < barIndex.length; i++) {
				if (barIndex[i] > -1) {
					if (skillBars[i].contains(mouse)) {
						final int xpTL = skills.getXPToNextLevel(barIndex[i]);
						final int xpHour = ((int) ((3600000.0 / (double) runTime) * gained_exp[barIndex[i]]));
						final int TTL = (int) (((double) xpTL / (double) xpHour) * 3600000);
						g.setColor(new Color(50, 50, 50, 240));
						g.fillRect(r_main.x, r_main.y, r_main.width,
								r_main.height);
						g.setColor(Color.WHITE);
						g.setFont(new Font("sansserif", Font.BOLD, 15));
						drawString(g, Skills.statsArray[barIndex[i]]
								.toUpperCase(), r_main, -58);
						g.setFont(new Font("sansserif", Font.PLAIN, 12));
						hoverDrawString(g, "Current Level: ", skills
								.getCurrentSkillLevel(barIndex[i])
								+ "", r_main, 40, 0);
						hoverDrawString(g, "XP Gained: ",
								gained_exp[barIndex[i]] + "xp", r_main, 40, 1);
						hoverDrawString(g, "XP / Hour: ", xpHour + "xp",
								r_main, 40, 2);
						hoverDrawString(g, "LVL Gained: ",
								gained_lvl[barIndex[i]] + " lvls", r_main, 40,
								3);
						hoverDrawString(g, "XPTL: ", xpTL + "xp", r_main, 40, 4);
						hoverDrawString(g, "TTL: ", formatTime(TTL), r_main,
								40, 5);
					}
				}
			}
		}

		void hoverDrawString(final Graphics g, final String str,
				final String val, final Rectangle rect, final int offset,
				final int index) {
			g.setColor(Color.WHITE);
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(val, g);
			final int width = (int) bounds.getWidth();
			final int y = rect.y + offset + (20 * index);
			g.drawString(str, rect.x + 5, y);
			g.drawString(val, (rect.x + rect.width) - width - 5, y);
			if (index < 5) {
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(rect.x + 5, y + 5, rect.x + rect.width - 5, y + 5);
			}
		}

		void drawString(final Graphics g, final String str,
				final Rectangle rect, final int offset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, rect.x + ((rect.width - width) / 2), rect.y
					+ ((rect.height / 2) + offset));
		}

		void drawStringEnd(final Graphics g, final String str,
				final Rectangle rect, final int xOffset, final int yOffset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, (rect.x + rect.width) - width + xOffset, rect.y
					+ ((rect.height / 2) + yOffset));
		}

		void drawStringMain(final Graphics g, final String str,
				final String val, final Rectangle rect, final int xOffset,
				final int yOffset, final int index, final boolean leftSide) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(val, g);
			final int indexMult = 17;
			final int width = (int) bounds.getWidth();
			if (leftSide) {
				g.drawString(str, rect.x + xOffset, rect.y + yOffset
						+ (index * indexMult));
				g.drawString(val, rect.x + (rect.width / 2) - width - xOffset,
						rect.y + yOffset + (index * indexMult));
			} else {
				g.drawString(str, rect.x + (rect.width / 2) + xOffset, rect.y
						+ yOffset + (index * indexMult));
				g.drawString(val, rect.x + rect.width - width - xOffset, rect.y
						+ yOffset + (index * indexMult));
			}
		}

		String formatTime(final int milliseconds) {
			final long t_seconds = milliseconds / 1000;
			final long t_minutes = t_seconds / 60;
			final long t_hours = t_minutes / 60;
			final int seconds = (int) (t_seconds % 60);
			final int minutes = (int) (t_minutes % 60);
			final int hours = (int) (t_hours % 60);
			return (nf.format(hours) + ":" + nf.format(minutes) + ":" + nf
					.format(seconds));
		}

		class MouseWatcher implements Runnable {

			Rectangle rect = null;

			MouseWatcher(final Rectangle rect) {
				this.rect = rect;
			}

			public void run() {
				Point mouse = new Point(Bot.getClient().getMouse().x, Bot
						.getClient().getMouse().y);
				while (rect.contains(mouse)) {
					try {
						mouse = new Point(Bot.getClient().getMouse().x, Bot
								.getClient().getMouse().y);
						Thread.sleep(50);
					} catch (Exception e) {
					}
				}
			}
		}

	}

	private class ExperimentSlayerAntiBan implements Runnable {
		private boolean stopThread;

		public void run() {
			Random random = new Random();
			while (!stopThread) {
				try {
					if (random.nextInt(Math.abs(15 - 0)) == 0) {
						final char[] LR = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT };
						final char[] UD = new char[] { KeyEvent.VK_DOWN,
								KeyEvent.VK_UP };
						final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP };
						final int random2 = random.nextInt(Math.abs(2 - 0));
						final int random1 = random.nextInt(Math.abs(2 - 0));
						final int random4 = random.nextInt(Math.abs(4 - 0));

						if (random.nextInt(Math.abs(3 - 0)) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random.nextInt(Math.abs(400 - 100)));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random.nextInt(Math.abs(600 - 300)));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random.nextInt(Math.abs(400 - 100)));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random
										.nextInt(Math.abs(600 - 300)));
							} else {
								Thread.sleep(random
										.nextInt(Math.abs(900 - 500)));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random.nextInt(Math.abs(2000 - 200)));
					}
				} catch (Exception e) {
					System.out.println("AntiBan error detected!");
				}
			}
		}
	}

	public class ExperimentSlayerGUI extends JFrame {

		private static final long serialVersionUID = 1L;

		public ExperimentSlayerGUI() {
			initComponents();
		}

		private void endButtonActionPerformed(ActionEvent e) {
			guiWait = false;
			guiExit = true;
			dispose();
		}

		private void startButtonActionPerformed(ActionEvent e) {

			startTime = System.currentTimeMillis();
			status = "Setting up Gui";

			doSpec = checkBox10.isSelected();
			run4 = checkBox13.isSelected();
			run5 = checkBox14.isSelected();
			GetBones = checkBox9.isSelected();

			if (attackcomboBox1.getSelectedItem().equals("Attack")) {
				attackStyle = 0;
			} else if (attackcomboBox1.getSelectedItem().equals("Strength")) {
				attackStyle = 1;
			} else if (attackcomboBox1.getSelectedItem().equals("Defence")) {
				attackStyle = 2;
			} else if (attackcomboBox1.getSelectedItem().equals("Range")) {
				attackStyle = 3;
			}

			if (!textField1.getText().isEmpty()) {
				EatatHP = Integer.parseInt(textField1.getText());
				log("Eating at Hp " + EatatHP);
				if (textField1.getText() == null) {
					log("Invalid Amount");
					log("If not eating, enter a value of 0");
					log("In the What HP to eat at? box");
				}
				if (!textField2.getText().isEmpty()) {
					speed = Integer.parseInt(textField2.getText());
					log("Getting MouseSpeed at " + speed);
					if (textField2.getText() == null) {
						log("Invalid MouseSpeed");
						log("Please leave the value in this box default");
						log("If you do not know what to do here");
					}

					if (paintcomboBox3.getSelectedItem().equals("AttackPaint")) {
						PaintStyle = 1;
					}
					if (paintcomboBox3.getSelectedItem()
							.equals("StrengthPaint")) {
						PaintStyle = 2;
					}
					if (paintcomboBox3.getSelectedItem().equals("DefensePaint")) {
						PaintStyle = 3;
					}
					if (paintcomboBox3.getSelectedItem().equals("RangePaint")) {
						PaintStyle = 4;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Bronze Arrow")) {
						ArrowID = bronzeArrow;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Iron Arrow")) {
						ArrowID = ironArrow;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Steel Arrow")) {
						ArrowID = steelArrow;
					}
					if (arrowcomboBox2.getSelectedItem()
							.equals("Mithril Arrow")) {
						ArrowID = mithrilArrow;
					}
					if (arrowcomboBox2.getSelectedItem()
							.equals("Adamant Arrow")) {
						ArrowID = adamantArrow;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Rune Arrow")) {
						ArrowID = runeArrow;
					}
					if (checkBox10.isSelected()) {
						doSpec = true;
					}
					if (checkBox13.isSelected()) {
						run4 = true;
					}
					if (checkBox14.isSelected()) {
						run5 = true;
					}
					if (checkBox9.isSelected()) {
						bones = 526;
						GetBones = true;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Bronze Bolt")) {
						ArrowID = bronzeBolt;
					}
					if (arrowcomboBox2.getSelectedItem()
							.equals("Bluerite Bolt")) {
						ArrowID = blueriteBolt;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Iron Bolt")) {
						ArrowID = ironBolt;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Steel Bolt")) {
						ArrowID = steelBolt;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Black Bolt")) {
						ArrowID = blackBolt;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Mithril Bolt")) {
						ArrowID = mithrilBolt;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Adamant Bolt")) {
						ArrowID = adamantBolt;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Rune Bolt")) {
						ArrowID = runeBolt;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Broad Bolt")) {
						ArrowID = broadBolt;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Bronze Knife")) {
						ArrowID = bronzeKnife;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Iron Knife")) {
						ArrowID = ironKnife;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Steel Knife")) {
						ArrowID = steelKnife;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Black Knife")) {
						ArrowID = blackKnife;
					}
					if (arrowcomboBox2.getSelectedItem()
							.equals("Mithril Knife")) {
						ArrowID = mithrilKnife;
					}
					if (arrowcomboBox2.getSelectedItem()
							.equals("Adamant Knife")) {
						ArrowID = adamantKnife;
					}
					if (arrowcomboBox2.getSelectedItem().equals("Rune Knife")) {
						ArrowID = runeKnife;
					}

					guiWait = false;
				}
			}
		}

		private void initComponents() {
			// GEN-BEGIN:initComponents
			attackLabel = new JLabel();
			arrowLabel = new JLabel();
			paintLabel = new JLabel();
			attackcomboBox1 = new JComboBox();
			arrowcomboBox2 = new JComboBox();
			paintcomboBox3 = new JComboBox();
			b2pLabel19 = new JLabel();
			specLabel20 = new JLabel();
			quickchatp2pLabel21 = new JLabel();
			quickchatf2pLabel22 = new JLabel();
			checkBox13 = new JCheckBox();
			checkBox14 = new JCheckBox();
			checkBox9 = new JCheckBox();
			checkBox10 = new JCheckBox();
			hpeatatLabel4 = new JLabel();
			textField1 = new JTextField();
			mousespeedLabel5 = new JLabel();
			textField2 = new JTextField();
			startButton = new JButton();
			endButton = new JButton();
			label1 = new JLabel();

			// ======== this ========
			setTitle("ExperimentSlayer Pro");
			setResizable(false);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ---- attackLabel ----
			attackLabel.setText("Attack Style:");
			attackLabel.setFont(new Font("Algerian", Font.PLAIN, 14));
			contentPane.add(attackLabel);
			attackLabel.setBounds(new Rectangle(new Point(25, 55), attackLabel
					.getPreferredSize()));

			// ---- arrowLabel ----
			arrowLabel.setText("Arrow Pickup:");
			arrowLabel.setFont(new Font("Algerian", Font.PLAIN, 14));
			contentPane.add(arrowLabel);
			arrowLabel.setBounds(new Rectangle(new Point(25, 85), arrowLabel
					.getPreferredSize()));

			// ---- paintLabel ----
			paintLabel.setText("Paint Options:");
			paintLabel.setFont(new Font("Algerian", Font.PLAIN, 14));
			contentPane.add(paintLabel);
			paintLabel.setBounds(new Rectangle(new Point(25, 115), paintLabel
					.getPreferredSize()));

			// ---- attackcomboBox1 ----
			attackcomboBox1.setModel(new DefaultComboBoxModel(new String[] {
					"Attack", "Strength", "Defence", "Range" }));
			contentPane.add(attackcomboBox1);
			attackcomboBox1.setBounds(215, 55, 130, attackcomboBox1
					.getPreferredSize().height);

			// ---- arrowcomboBox2 ----
			arrowcomboBox2.setModel(new DefaultComboBoxModel(new String[] {
					"None", "Bronze Arrow", "Iron Arrow", "Steel Arrow",
					"Mithril Arrow", "Adamant Arrow", "Rune Arrow",
					"Bronze Bolt", "Bluerite Bolt", "Iron Bolt", "Steel Bolt",
					"Black Bolt", "Mithril Bolt", "Adamant Bolt", "Rune Bolt",
					"Broad Bolt", "Bronze Knife", "Iron Knife", "Steel Knife",
					"Black Knife", "Mithril Knife", "Adamant Knife",
					"Rune Knife" }));
			contentPane.add(arrowcomboBox2);
			arrowcomboBox2.setBounds(215, 85, 130, arrowcomboBox2
					.getPreferredSize().height);

			// ---- paintcomboBox3 ----
			paintcomboBox3.setModel(new DefaultComboBoxModel(new String[] {
					"AttackPaint", "StrengthPaint", "DefensePaint",
					"RangePaint" }));
			contentPane.add(paintcomboBox3);
			paintcomboBox3.setBounds(215, 115, 130, paintcomboBox3
					.getPreferredSize().height);

			// ---- b2pLabel19 ----
			b2pLabel19.setText("Use Bones2peaches");
			b2pLabel19.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
			contentPane.add(b2pLabel19);
			b2pLabel19.setBounds(10, 245, 120,
					b2pLabel19.getPreferredSize().height);

			// ---- specLabel20 ----
			specLabel20.setText("Use Special Attacks");
			specLabel20
					.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
			contentPane.add(specLabel20);
			specLabel20.setBounds(10, 215, 120,
					specLabel20.getPreferredSize().height);

			// ---- quickchatp2pLabel21 ----
			quickchatp2pLabel21.setText("Quick Chat Skills P2P");
			quickchatp2pLabel21.setFont(new Font("Dialog", Font.BOLD
					| Font.ITALIC, 12));
			contentPane.add(quickchatp2pLabel21);
			quickchatp2pLabel21.setBounds(200, 245, 125, quickchatp2pLabel21
					.getPreferredSize().height);

			// ---- quickchatf2pLabel22 ----
			quickchatf2pLabel22.setText("Quick Chat Skills F2P");
			quickchatf2pLabel22.setFont(new Font("Dialog", Font.BOLD
					| Font.ITALIC, 12));
			contentPane.add(quickchatf2pLabel22);
			quickchatf2pLabel22.setBounds(200, 215, 125, quickchatf2pLabel22
					.getPreferredSize().height);

			// ---- checkBox13 ----
			checkBox13.setText("Yes");
			contentPane.add(checkBox13);
			checkBox13.setBounds(new Rectangle(new Point(340, 210), checkBox13
					.getPreferredSize()));

			// ---- checkBox14 ----
			checkBox14.setText("Yes");
			contentPane.add(checkBox14);
			checkBox14.setBounds(340, 240, 43, 23);

			// ---- checkBox9 ----
			checkBox9.setText("Yes");
			contentPane.add(checkBox9);
			checkBox9.setBounds(140, 240, 43, 23);

			// ---- checkBox10 ----
			checkBox10.setText("Yes");
			contentPane.add(checkBox10);
			checkBox10.setBounds(new Rectangle(new Point(140, 210), checkBox10
					.getPreferredSize()));

			// ---- hpeatatLabel4 ----
			hpeatatLabel4.setText("HP to eat at?");
			hpeatatLabel4.setFont(new Font("Engravers MT", Font.PLAIN, 12));
			contentPane.add(hpeatatLabel4);
			hpeatatLabel4.setBounds(25, 145,
					hpeatatLabel4.getPreferredSize().width, 20);

			// ---- textField1 ----
			textField1.setText("10");
			contentPane.add(textField1);
			textField1.setBounds(255, 145, 30,
					textField1.getPreferredSize().height);

			// ---- mousespeedLabel5 ----
			mousespeedLabel5.setText("Adjust Mouse Speed:");
			mousespeedLabel5.setFont(new Font("Engravers MT", Font.PLAIN, 12));
			contentPane.add(mousespeedLabel5);
			mousespeedLabel5.setBounds(10, 180, mousespeedLabel5
					.getPreferredSize().width, 20);

			// ---- textField2 ----
			textField2.setText("4");
			contentPane.add(textField2);
			textField2.setBounds(255, 180, 30,
					textField2.getPreferredSize().height);

			// ---- startButton ----
			startButton.setText("Start Script");
			startButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startButtonActionPerformed(e);
				}
			});
			contentPane.add(startButton);
			startButton.setBounds(50, 285, 120, 35);

			// ---- endButton ----
			endButton.setText("End Script");
			endButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					endButtonActionPerformed(e);
				}
			});
			contentPane.add(endButton);
			endButton.setBounds(215, 285, 120, 35);

			// ---- label1 ----
			label1.setText("ExperimentSlayer Pro Settings");
			label1.setFont(new Font("Britannic Bold", Font.PLAIN, 24));
			contentPane.add(label1);
			label1.setBackground(new Color(102, 102, 102));
			label1.setBounds(new Rectangle(new Point(40, 10), label1
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
			setSize(400, 365);
			setLocationRelativeTo(getOwner());
			// GEN-END:initComponents
		}

		// GEN-BEGIN:variables
		private JLabel attackLabel;
		private JLabel arrowLabel;
		private JLabel paintLabel;
		private JComboBox attackcomboBox1;
		private JComboBox arrowcomboBox2;
		private JComboBox paintcomboBox3;
		private JLabel b2pLabel19;
		private JLabel specLabel20;
		private JLabel quickchatp2pLabel21;
		private JLabel quickchatf2pLabel22;
		private JCheckBox checkBox13;
		private JCheckBox checkBox14;
		private JCheckBox checkBox9;
		private JCheckBox checkBox10;
		private JLabel hpeatatLabel4;
		private JTextField textField1;
		private JLabel mousespeedLabel5;
		private JTextField textField2;
		private JButton startButton;
		private JButton endButton;
		private JLabel label1;
		// GEN-END:variables
	}
}
