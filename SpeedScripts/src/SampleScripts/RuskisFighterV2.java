package SampleScripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.GlobalConfiguration;

@ScriptManifest(authors = { "Ruski" }, category = "Combat", name = "RuskisFighterV2", version = 2.0, description = "<hmtl><center><h2>All settings can be set on the GUI, select your character and Start!</h2></center></html>")
public class RuskisFighterV2 extends Script implements ActionListener,
		PaintListener {

	/*****************************************************************************
	 * \ CONSTANTS \
	 *****************************************************************************/
	private enum Action {

		ANTIBAN, ATTACK, KILLSCRIPT, PICKUP, PICKUPBONE, USE_INVENTORY, USE_MAGIC, USE_SPECIAL, WAIT, WALK
	}

	/*****************************************************************************
	 * \ Experienced Gained class store \
	 *****************************************************************************/
	class RFV2_ExperienceHandler {

		int[][] skillExp = new int[24][1];

		public RFV2_ExperienceHandler() {
		}

		public int getSkillExp(final int skill) {
			return skillExp[skill][0];
		}

		public void setExperiencePoints(final int skill, final int exp) {
			skillExp[skill][0] = exp;
		}
	}

	class RFV2_ItemPickUpThread extends Thread {

		RSItemTile item;
		String[] itemNames;
		private volatile boolean stop = false;

		public RFV2_ItemPickUpThread(final RSItemTile item,
				final String[] itemNames) {
			this.item = item;
			this.itemNames = itemNames;
		}

		@Override
		public void run() {
			while (!stop) {
				try {
					int a;
					final int hoverRand = random(6, 12);
					final Point itemScreen = Calculations.tileToScreen(item);
					for (a = hoverRand; a-- >= 0;) {
						if (stop || item == null) {
							break;
						}
						final List<String> menuItems = getMenuItems();
						if (menuItems.size() > 1) {
							int b;
							final int itemLength = itemNames.length;
							for (b = 0; b < itemLength; b++) {
								if (listContainsString(menuItems, itemNames[b])) {
									final StringBuffer fullItemCommandBuf = new StringBuffer();
									fullItemCommandBuf.append("Take ");
									fullItemCommandBuf.append(itemNames[b]);
									final String fullItemCommand = fullItemCommandBuf
											.toString();
									if (menuItems.get(0)
											.equals(fullItemCommand)) {
										stopThread();
										clickMouse(true);
										Thread.sleep(320, 620);
									} else {
										stopThread();
										// clickMouse(false);
										Thread.sleep(120, 510);
										atMenu(fullItemCommand);
									}
								}
							}
						}
						final Point randomP = new Point(random(
								itemScreen.x - 5, itemScreen.x + 5), random(
								itemScreen.y - 5, itemScreen.y + 5));
						moveMouse(randomP);
					}
					stopThread();
				} catch (final Exception e) {
					log.log(Level.SEVERE, "menuListener error: ", e);
				}
			}
		}

		public void stopThread() {
			stop = true;
		}
	}

	static class RFV2_PotionConstants {

		final static String ATTACK_POTION = "Regular Attack Potion";
		final static int[] ATTACK_POTION_ID = { 2428, 121, 123, 125 };
		final static String COMBAT_POTION = "Combat Potion";
		final static int[] COMBAT_POTION_ID = { 9739, 9741, 9743, 9745 };
		final static String MAGIC_POTION = "Magic Potion";
		final static int[] MAGIC_POTION_ID = { 3040, 3042, 3044, 3046 };
		final static String RANGING_POTION = "Ranging Potion";
		final static int[] RANGING_POTION_ID = { 2444, 169, 171, 173 };
		final static String STRENGTH_POTION = "Regular Strength Potion";
		final static int[] STRENGTH_POTION_ID = { 113, 115, 117, 119 };
		final static String SUPER_ATTACK_POTION = "Super Attack Potion";
		final static int[] SUPER_ATTACK_POTION_ID = { 2436, 145, 147, 149 };
		final static String SUPER_DEFENSE_POTION = "Super Defense Potion";
		final static int[] SUPER_DEFENSE_POTION_ID = { 2442, 163, 165, 167 };
		final static String SUPER_STRENGTH_POTION = "Super Strength Potion";
		final static int[] SUPER_STRENGTH_POTION_ID = { 2440, 157, 159, 161 };

		public static int getAffectedStat(final String pot) {
			if (pot.equals(RFV2_PotionConstants.ATTACK_POTION)) {
				return Constants.STAT_ATTACK;
			}
			if (pot.equals(RFV2_PotionConstants.STRENGTH_POTION)) {
				return Constants.STAT_STRENGTH;
			}
			if (pot.equals(RFV2_PotionConstants.COMBAT_POTION)) {
				return Constants.STAT_STRENGTH;
			}
			if (pot.equals(RFV2_PotionConstants.SUPER_ATTACK_POTION)) {
				return Constants.STAT_ATTACK;
			}
			if (pot.equals(RFV2_PotionConstants.SUPER_STRENGTH_POTION)) {
				return Constants.STAT_STRENGTH;
			}
			if (pot.equals(RFV2_PotionConstants.SUPER_DEFENSE_POTION)) {
				return Constants.STAT_DEFENSE;
			}
			if (pot.equals(RFV2_PotionConstants.MAGIC_POTION)) {
				return Constants.STAT_MAGIC;
			}
			if (pot.equals(RFV2_PotionConstants.RANGING_POTION)) {
				return Constants.STAT_RANGE;
			}
			return -1;
		}

		public RFV2_PotionConstants() {
		}
	}

	class RFV2_PotionSet {

		ArrayList<Integer> affectedStats;
		String[] potionNames;
		ArrayList<int[]> potions;

		public RFV2_PotionSet(final String[] potionNames,
				final ArrayList<int[]> potions,
				final ArrayList<Integer> affectedStats) {
			this.potionNames = potionNames;
			this.potions = potions;
			this.affectedStats = affectedStats;
		}

		public int getAffectedStat(final String potionName) {
			return affectedStats.get(getPotionIndex(potionName));
		}

		public int[] getPotionIDs(final String potionName) {
			return potions.get(getPotionIndex(potionName));
		}

		public int getPotionIndex(final String potionName) {
			for (int a = 0; a < potionNames.length; a++) {
				if (potionNames[a].equals(potionName)) {
					return a;
				}
			}
			return -1;
		}
	}

	class RFV2_Settings {
		final File settingsFile = new File(new File(GlobalConfiguration.Paths
				.getSettingsDirectory()), SETTINGS_FILE_NAME);

		int[] alchItemIDs = new int[50];
		boolean antiBanCheck = false;
		boolean bonesToPeachesCheck = false;
		boolean buryBonesCheck = false;
		String buryBonesSpeed = "";
		boolean eatFood = false;
		int equipArrowID;
		boolean equipArrowsCheck = false;
		boolean itemAlchCheck = false;
		int[] itemIDs = new int[50];
		String[] itemNames = new String[50];
		boolean moveMouseToNextNPC = false;
		int[] npcIDs = new int[50];
		boolean npcIsInCage = false;
		boolean pickUpItems = false;
		String pickUpSpeed = "";
		String[] potionList = new String[8];
		String reportSetting = "";
		boolean stopScriptWhenNoFood = false;
		boolean usePotionCheck = false;
		boolean useSpecialCheck = false;

		public RFV2_Settings() {
		}

		public String booleanToString(final boolean a) {
			if (a) {
				return "true";
			} else {
				return "false";
			}
		}

		public boolean extractBoolean(final String text) {
			return text.equals("true");
		}

		public int[] extractIntegers(String text) {
			int[] ints = null;
			try {
				text = text.replaceAll(" ", "");
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

		public String[] extractStrings(final String text) {
			return text.split(",");
		}

		public String getSetting(final String settingName) {
			try {
				final Properties p = new Properties();
				p.load(new FileInputStream(settingsFile));
				return p.getProperty(settingName);
			} catch (final IOException ioe) {
				log.log(Level.SEVERE, "loadSettings(String) error: ", ioe);
				return "";
			}
		}

		public String[][] getSettingsArray() {
			final ArrayList<String[]> settingsArray = new ArrayList<String[]>();

			settingsArray
					.add(new String[] { "NPCIDS", intArrayToString(npcIDs) });
			settingsArray.add(new String[] { "NPCISINCAGE",
					booleanToString(npcIsInCage) });
			settingsArray.add(new String[] { "PICKUPITEMS",
					booleanToString(pickUpItems) });
			if (pickUpItems) {
				settingsArray.add(new String[] { "ITEMIDS",
						intArrayToString(itemIDs) });
				settingsArray.add(new String[] { "ITEMNAMES",
						stringArrayToString(itemNames) });
				settingsArray.add(new String[] { "PICKUPSPEED", pickUpSpeed });
			}
			settingsArray.add(new String[] { "ITEMALCH",
					booleanToString(itemAlchCheck) });
			if (itemAlchCheck) {
				settingsArray.add(new String[] { "ALCHITEMIDS",
						intArrayToString(alchItemIDs) });
			}
			settingsArray.add(new String[] { "BURYBONES",
					booleanToString(buryBonesCheck) });
			if (buryBonesCheck) {
				settingsArray.add(new String[] { "BURYBONESPEED",
						buryBonesSpeed });
			}
			settingsArray.add(new String[] { "USEPOTION",
					booleanToString(usePotionCheck) });
			if (usePotionCheck) {
				settingsArray.add(new String[] { "POTIONLIST",
						stringArrayToString(potionList) });
			}
			settingsArray.add(new String[] { "EQUIPARROWS",
					booleanToString(equipArrowsCheck) });
			if (equipArrowsCheck) {
				settingsArray.add(new String[] { "EQUIPARROWID",
						"" + equipArrowID });
			}

			settingsArray.add(new String[] { "EATFOOD",
					booleanToString(eatFood) });
			settingsArray.add(new String[] { "STOPSCRIPTWHENNOFOOD",
					booleanToString(stopScriptWhenNoFood) });
			settingsArray.add(new String[] { "BONESTOPEACHES",
					booleanToString(bonesToPeachesCheck) });
			settingsArray.add(new String[] { "USESPECIAL",
					booleanToString(useSpecialCheck) });
			settingsArray.add(new String[] { "ANTIBAN",
					booleanToString(antiBanCheck) });
			settingsArray.add(new String[] { "MOVEMOUSENPC",
					booleanToString(moveMouseToNextNPC) });
			settingsArray.add(new String[] { "REPORTSETTING", reportSetting });

			final String[][] stringArray = new String[settingsArray.size()][2];
			for (int a = 0; a < settingsArray.size(); a++) {
				stringArray[a][0] = settingsArray.get(a)[0];
				stringArray[a][1] = settingsArray.get(a)[1];
			}

			return stringArray;
		}

		public String intArrayToString(final int[] array) {
			String intArray = null;
			try {
				if (array.length > 0) {
					intArray = "";
					for (int a = 0; a < array.length; a++) {
						if (array[a] != 0) {
							intArray += array[a];
							if (a != array.length - 1) {
								intArray += ",";
							}
						}
					}
					return intArray;
				}
			} catch (final Exception e) {
			}
			return "";
		}

		public void saveSettings(final String[][] settings) {
			try {
				final Properties p = new Properties();

				settingsFile.createNewFile();
				p.load(new FileInputStream(settingsFile));
				for (final String[] setting : settings) {
					p.setProperty(setting[0], setting[1]);
				}
				final FileOutputStream out = new FileOutputStream(settingsFile);
				p.store(out, "");
			} catch (final IOException ioe) {
				log.log(Level.SEVERE, "saveSettings(String[][]) error: ", ioe);
			}
		}

		public void setSettings() {
			try {
				npcIDs = extractIntegers(getSetting("NPCIDS"));
				npcIsInCage = extractBoolean(getSetting("NPCISINCAGE"));
				eatFood = extractBoolean(getSetting("EATFOOD"));
				pickUpItems = extractBoolean(getSetting("PICKUPITEMS"));
				stopScriptWhenNoFood = extractBoolean(getSetting("STOPSCRIPTWHENNOFOOD"));
				bonesToPeachesCheck = extractBoolean(getSetting("BONESTOPEACHES"));
				if (pickUpItems) {
					itemIDs = extractIntegers(getSetting("ITEMIDS"));
					itemNames = extractStrings(getSetting("ITEMNAMES"));
					pickUpSpeed = getSetting("PICKUPSPEED");
				}
				buryBonesCheck = extractBoolean(getSetting("BURYBONES"));
				if (buryBonesCheck) {
					buryBonesSpeed = getSetting("BURYBONESPEED");
				}
				usePotionCheck = extractBoolean(getSetting("USEPOTION"));
				if (usePotionCheck) {
					potionList = extractStrings(getSetting("POTIONLIST"));
				}
				equipArrowsCheck = extractBoolean(getSetting("EQUIPARROWS"));
				if (equipArrowsCheck) {
					equipArrowID = Integer.parseInt(getSetting("EQUIPARROWID"));
				}
				itemAlchCheck = extractBoolean(getSetting("ITEMALCH"));
				if (itemAlchCheck) {
					alchItemIDs = extractIntegers(getSetting("ALCHITEMIDS"));
				}
				antiBanCheck = extractBoolean(getSetting("ANTIBAN"));
				moveMouseToNextNPC = extractBoolean(getSetting("MOVEMOUSENPC"));
				reportSetting = getSetting("REPORTSETTING");
				useSpecialCheck = extractBoolean(getSetting("USESPECIAL"));
			} catch (final Exception e) {
				log.log(Level.SEVERE, "setSettings error: ", e);
			}
		}

		public boolean settingsExist() {
			return settingsFile.exists();
		}

		public String stringArrayToString(final String[] array) {
			String strArray = null;
			try {
				strArray = "";
				if (array.length <= 0) {
					return "";
				}
				for (int a = 0; a < array.length; a++) {
					if (!array[a].equals(null) && !array[a].equals("")) {
						strArray += array[a].trim();
						if (a != array.length - 1) {
							strArray += ",";
						}
					}
				}
			} catch (final Exception e) {
			}
			return strArray;
		}
	}

	private static final int[] BAD_IDS = { 421 };

	/*****************************************************************************
	 * \ CUSTOM ANTI-RANDOMS \
	 *****************************************************************************/
	private static final String[] BAD_MONSTERS = { "Rock Golem", "Tree Spirit",
			"Shade", "Evil Chicken", "Swarm", "River Troll", "Strange plant" };
	private javax.swing.JButton addPotionButton;

	private javax.swing.JTextField alchItemIDsText;

	private javax.swing.JCheckBox antibanCheck;

	private final String[] BONE_NAMES = { "Bones", "Big bones", "Wolf bones",
			"Bat bones", "Jogre bones", "Dragon bones", "Monkey bones",
			"Shaikahan bones", "Zogre bones", "Dagannoth bones" };

	private javax.swing.JCheckBox bonesCheck;

	private final int[] BONESID = { 534, 530, 532, 526, 536 };

	private final int BONESPEACHTABID = 8015;

	private boolean buryBonesTillNone = false;

	private javax.swing.JComboBox burySpeedCombo;
	private int[] currentInventoryIDs = null;

	private String currentInventoryString = "";

	private RSItemTile currentItemTile = null;

	private int currentMagicSpell;

	private RSNPC currentNPC = null;;

	private RFV2_PotionSet currentPotionSet = null;

	private RSTile currentWalkTile = null;

	private javax.swing.JCheckBox eatFoodCheck;

	private javax.swing.JCheckBox equipArrowsCheck;

	// Thanks to aftermath for method

	private javax.swing.JTextField equipArrowText;

	private final RFV2_ExperienceHandler experienceHandler = new RFV2_ExperienceHandler();

	private final int FIRE_ID = 554;

	private final int[] FOODID = { 1895, 1893, 1891, 4293, 2142, 291, 2140,
			3228, 9980, 7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878,
			7568, 2343, 1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339,
			351, 329, 3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397,
			391, 3369, 3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335,
			7178, 7180, 7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003,
			2011, 2289, 2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893,
			1895, 1897, 1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066,
			7068, 1942, 6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993,
			1989, 1978, 5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907,
			1911, 5745, 2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084,
			2034, 2048, 2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277,
			2225, 2255, 2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092,
			2032, 2074, 2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883,
			1971, 4608, 1883, 1885 };

	long healthRandomTime = 0;

	int hpToHealAt = 10;
	private final int INTERFACE_HIGH_ALCH = 34;
	private final int INTERFACE_LOW_ACLH = 13;

	private final int INTERFACE_MAGIC = 192;

	private javax.swing.JTextField itemIDs;

	private javax.swing.JTextField itemNamesText;
	private RFV2_ItemPickUpThread itemPickUpThread;

	private javax.swing.JLabel jLabel10;

	private javax.swing.JLabel jLabel11;

	public javax.swing.JLabel jLabel111;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JLabel jLabel14;
	private javax.swing.JLabel jLabel15;
	private javax.swing.JLabel jLabel16;
	private javax.swing.JLabel jLabel17;
	private javax.swing.JLabel jLabel18;
	private javax.swing.JLabel jLabel19;
	private javax.swing.JLabel jLabel20;
	private javax.swing.JLabel jLabel21;
	public javax.swing.JLabel jLabel222;
	private javax.swing.JLabel jLabel23;
	private javax.swing.JLabel jLabel24;
	private javax.swing.JLabel jLabel25;

	private javax.swing.JLabel jLabel26;;

	private javax.swing.JLabel jLabel27;
	private javax.swing.JLabel jLabel28;
	public javax.swing.JLabel jLabel333;
	public javax.swing.JLabel jLabel444;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;

	private javax.swing.JPanel jPanel4;

	private javax.swing.JPanel jPanel5;

	private javax.swing.JPanel jPanel6;

	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private javax.swing.JTabbedPane jTabbedPane1;
	private final int[] JUNKIDS = { 229 };
	private long lastNPCCheck = 0;
	int lastSpecialValue = 0;
	private javax.swing.JFrame mainFrame;
	private final int NATURE_ID = 561;
	private long nextABTime = 0;
	private javax.swing.JCheckBox nextNPCCheck;
	long nextSpecialTime = 0;
	private boolean npcCheck = false;
	private javax.swing.JTextField npcIDs;
	private javax.swing.JCheckBox npcsInCageCheck;
	private javax.swing.JCheckBox peachesCheck;
	int pickUpBoneFail = 0;
	private javax.swing.JCheckBox pickUpCheck;
	/*****************************************************************************
	 * \ STATE-MACHINE \
	 *****************************************************************************/
	int pickUpFail = 0;
	private javax.swing.JComboBox pickUpSpeedCombo;
	private javax.swing.JComboBox potionCombo;
	private javax.swing.JList potionList;
	private javax.swing.JScrollPane potionScroll;
	private javax.swing.JButton removePotionButton;
	private javax.swing.JComboBox reportCombo;
	int runEnergy = random(15, 50);
	private javax.swing.JCheckBox saveFormSettingsCheck;
	private final RFV2_Settings settings = new RFV2_Settings();
	private final String SETTINGS_FILE_NAME = "RuskisFighterV2Settings.txt";
	private javax.swing.JCheckBox specialCheck;
	int specialCost = 0;
	private boolean START_SCRIPT = false;
	private javax.swing.JButton startButton;
	/*****************************************************************************
	 * \ VARIABLES \
	 *****************************************************************************/
	private long startTime = 0;
	private javax.swing.JCheckBox stopScriptWhenNoFoodCheck;
	private javax.swing.JCheckBox useItemAlchCheck;
	private javax.swing.JCheckBox usePotionsCheck;
	private int[] waitRandom = { 200, 800 };

	public void actionPerformed(final ActionEvent action) {
		final Object event = action.getSource();
		if (event.equals(startButton)) {
			settings.npcIDs = settings.extractIntegers(npcIDs.getText());
			settings.npcIsInCage = npcsInCageCheck.isSelected();
			settings.eatFood = eatFoodCheck.isSelected();
			settings.stopScriptWhenNoFood = stopScriptWhenNoFoodCheck
					.isSelected();
			settings.bonesToPeachesCheck = peachesCheck.isSelected();
			settings.pickUpItems = pickUpCheck.isSelected();
			if (settings.pickUpItems) {
				settings.itemIDs = settings.extractIntegers(itemIDs.getText());
				settings.itemNames = settings.extractStrings(itemNamesText
						.getText());
				settings.pickUpSpeed = pickUpSpeedCombo.getSelectedItem()
						.toString();
			}
			settings.itemAlchCheck = useItemAlchCheck.isSelected();
			if (settings.itemAlchCheck) {
				settings.alchItemIDs = settings.extractIntegers(alchItemIDsText
						.getText());
			}
			settings.buryBonesCheck = bonesCheck.isSelected();
			if (settings.buryBonesCheck) {
				settings.buryBonesSpeed = burySpeedCombo.getSelectedItem()
						.toString();
			}
			settings.usePotionCheck = usePotionsCheck.isSelected();
			if (settings.usePotionCheck) {
				final ArrayList<String> potionStringList = new ArrayList<String>();
				for (int a = 0; a < potionList.getModel().getSize(); a++) {
					potionStringList.add(potionList.getModel().getElementAt(a)
							.toString());
				}
				final String[] potionArrayList = new String[potionStringList
						.size()];
				for (int a = 0; a < potionArrayList.length; a++) {
					potionArrayList[a] = potionStringList.get(a);
				}
				settings.potionList = potionArrayList;
				final ArrayList<int[]> newPotionList = new ArrayList<int[]>();
				final ArrayList<Integer> newAffectedStats = new ArrayList<Integer>();
				for (final String potion : settings.potionList) {
					if (!potion.equals("")) {
						if (potion.equals(RFV2_PotionConstants.ATTACK_POTION)) {
							newPotionList
									.add(RFV2_PotionConstants.ATTACK_POTION_ID);
							newAffectedStats
									.add(RFV2_PotionConstants
											.getAffectedStat(RFV2_PotionConstants.ATTACK_POTION));
						}
						if (potion.equals(RFV2_PotionConstants.COMBAT_POTION)) {
							newPotionList
									.add(RFV2_PotionConstants.COMBAT_POTION_ID);
							newAffectedStats
									.add(RFV2_PotionConstants
											.getAffectedStat(RFV2_PotionConstants.COMBAT_POTION));
						}
						if (potion.equals(RFV2_PotionConstants.MAGIC_POTION)) {
							newPotionList
									.add(RFV2_PotionConstants.MAGIC_POTION_ID);
							newAffectedStats
									.add(RFV2_PotionConstants
											.getAffectedStat(RFV2_PotionConstants.MAGIC_POTION));
						}
						if (potion.equals(RFV2_PotionConstants.RANGING_POTION)) {
							newPotionList
									.add(RFV2_PotionConstants.RANGING_POTION_ID);
							newAffectedStats
									.add(RFV2_PotionConstants
											.getAffectedStat(RFV2_PotionConstants.RANGING_POTION));
						}
						if (potion.equals(RFV2_PotionConstants.STRENGTH_POTION)) {
							newPotionList
									.add(RFV2_PotionConstants.STRENGTH_POTION_ID);
							newAffectedStats
									.add(RFV2_PotionConstants
											.getAffectedStat(RFV2_PotionConstants.STRENGTH_POTION));
						}
						if (potion
								.equals(RFV2_PotionConstants.SUPER_ATTACK_POTION)) {
							newPotionList
									.add(RFV2_PotionConstants.SUPER_ATTACK_POTION_ID);
							newAffectedStats
									.add(RFV2_PotionConstants
											.getAffectedStat(RFV2_PotionConstants.SUPER_ATTACK_POTION));
						}
						if (potion
								.equals(RFV2_PotionConstants.SUPER_DEFENSE_POTION)) {
							newPotionList
									.add(RFV2_PotionConstants.SUPER_DEFENSE_POTION_ID);
							newAffectedStats
									.add(RFV2_PotionConstants
											.getAffectedStat(RFV2_PotionConstants.SUPER_DEFENSE_POTION));
						}
						if (potion
								.equals(RFV2_PotionConstants.SUPER_STRENGTH_POTION)) {
							newPotionList
									.add(RFV2_PotionConstants.SUPER_STRENGTH_POTION_ID);
							newAffectedStats
									.add(RFV2_PotionConstants
											.getAffectedStat(RFV2_PotionConstants.SUPER_STRENGTH_POTION));
						}
					}
				}
				if (newPotionList.size() > 0) {
					currentPotionSet = new RFV2_PotionSet(settings.potionList,
							newPotionList, newAffectedStats);
				}
			}
			settings.equipArrowsCheck = equipArrowsCheck.isSelected();
			if (settings.equipArrowsCheck) {
				settings.equipArrowID = Integer.parseInt(equipArrowText
						.getText());
			}
			settings.antiBanCheck = antibanCheck.isSelected();
			settings.moveMouseToNextNPC = nextNPCCheck.isSelected();
			settings.reportSetting = reportCombo.getSelectedItem().toString();
			settings.useSpecialCheck = specialCheck.isSelected();

			if (!saveFormSettingsCheck.isSelected()) {
				;
			}
			try {
				settings.saveSettings(settings.getSettingsArray());
			} catch (final Exception e) {
				log.log(Level.SEVERE, "saving settings error: ", e);
			}

			mainFrame.setVisible(false);
			mainFrame.dispose();
			START_SCRIPT = true;
		}
		if (event.equals(addPotionButton)) {
			final String potionName = potionCombo.getSelectedItem().toString();
			final ArrayList<String> newPotionList = new ArrayList<String>();
			for (int a = 0; a < potionList.getModel().getSize(); a++) {
				newPotionList.add(potionList.getModel().getElementAt(a)
						.toString());
			}
			if (!newPotionList.contains(potionName)) {
				newPotionList.add(potionName);
				potionList.setListData(newPotionList.toArray());
			}
		}
		if (event.equals(removePotionButton)) {
			final int selectedPotion = potionList.getSelectedIndex();
			final ArrayList<String> newPotionList = new ArrayList<String>();
			for (int a = 0; a < potionList.getModel().getSize(); a++) {
				newPotionList.add(potionList.getModel().getElementAt(a)
						.toString());
			}
			if (selectedPotion >= 0) {
				newPotionList.remove(selectedPotion);
				potionList.setListData(newPotionList.toArray());
			}

		}
	}

	private int antiBan() {
		nextABTime = System.currentTimeMillis()
				+ (1000 * random(2, 40) + random(0, 1000));
		final int randomAction = random(0, 6);
		if (itemPickUpThread != null) {
			if (itemPickUpThread.isAlive()) {
				return 1;
			}
		}
		switch (randomAction) {
		case 0:
			if (getCurrentTab() != Constants.TAB_INVENTORY) {
				openTab(Constants.TAB_INVENTORY);
				wait(random(310, 610));
				final Point invPoint = new Point(random(558, 723), random(215,
						440));
				moveMouse(invPoint);
				return random(510, 2300);
			} else {
				final int random = random(0, 10);
				final Point rndPoint = new Point(random(558, 723), random(215,
						440));
				if (random == 0) {
					if (getCurrentTab() != Constants.TAB_STATS) {
						openTab(Constants.TAB_STATS);
						wait(random(310, 610));
					}
					moveMouse(rndPoint);
					return random(200, 2300);
				} else if (random >= 1 && random <= 6) {
					final int randomTab = random(0, 13);
					if (randomTab != getCurrentTab()) {
						openTab(random(0, 13));
						wait(random(310, 610));
					}
					moveMouse(rndPoint);
					return random(300, 1600);
				} else {
					final int randomTab = random(0, 13);
					if (randomTab != getCurrentTab()) {
						openTab(random(0, 13));
						return random(310, 2000);
					}
				}
			}
		case 1:
			Point randomMouse = null;
			final int rndMovement = random(1, 5);
			for (int a = 0; a < rndMovement; a++) {
				randomMouse = new Point(random(15, 730), random(15, 465));
				moveMouse(randomMouse);
				wait(random(50, 800));
			}
			return random(130, 810);
		case 2:
			final int currentAngle = getCameraAngle();
			switch (random(0, 1)) {
			case 0:
				setCameraRotation(currentAngle + random(0, 650));
				return random(710, 1700);
			case 1:
				setCameraRotation(currentAngle - random(0, 650));
				return random(710, 1700);
			}
		case 3:
			final int currentAlt = Bot.getClient().getCamPosZ();
			final int random = random(0, 10);
			if (random <= 7) {
				setCameraAltitude(currentAlt - random(0, 100));
				return random(410, 2130);
			} else {
				setCameraAltitude(currentAlt + random(0, 100));
				return random(410, 2130);
			}
		case 4:
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
		case 5:
			return random(310, 2400);

		case 6:
			if (currentNPC != null && getMyPlayer().getInteracting() != null) {
				turnToTile(currentNPC.getLocation());
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

	private boolean canPickUpItem(final int itemID, final boolean priority) {
		if (getInventoryCount() < 27) {
			return true;
		} else if (settings.itemAlchCheck && getInventoryCount() < 27
				|| settings.itemAlchCheck && getInventoryCount() == 27
				&& priority) {
			return true;
		} else if (!settings.itemAlchCheck && getInventoryCount() < 28) {
			return true;
		} else {
			if (getInventoryCount(itemID) > 0
					&& getInventoryItemByID(itemID).getStackSize() > 1) {
				return true;
			}
		}
		return false;
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

	private boolean clickNPC(final RSNPC npc, final String action) {
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
							// clickMouse(false);
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
			log.log(Level.SEVERE, "clickNPC(RSNPC, String) error: ", e);
			return false;
		}
	}

	public int distanceBetweenPoints(final Point p1, final Point p2) {
		return (int) Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y)
				* (p2.y - p1.y));
	}

	private RSTile[] generatePath(final int destinationX, final int destinationY) {
		int startX = getMyPlayer().getLocation().getX();
		int startY = getMyPlayer().getLocation().getY();
		double dx, dy;
		final ArrayList<RSTile> list = new ArrayList<RSTile>();
		list.add(new RSTile(startX, startY));
		while (Math.hypot(destinationY - startY, destinationX - startX) > 8) {
			dx = destinationX - startX;
			dy = destinationY - startY;
			final int gamble = random(10, 14);
			while (Math.hypot(dx, dy) > gamble) {
				dx *= .95;
				dy *= .95;
			}
			startX += (int) dx;
			startY += (int) dy;
			list.add(new RSTile(startX, startY));
		}
		list.add(new RSTile(destinationX, destinationY));
		return list.toArray(new RSTile[list.size()]);

	}

	private RSTile[] generatePath(final RSTile tile) {
		return generatePath(tile.getX(), tile.getY());
	}

	/*****************************************************************************
	 * \ FUNCTIONS \
	 *****************************************************************************/
	private Action getAction() {

		/**
		 * Variable reset
		 */
		resetVariables();

		/**
		 * Dynamic variable setting to skill -magic alchmy spell low/high
		 */
		setVariablesToSkill();

		/**
		 * Alch spell selected -Checks to see if a spell has been selected
		 * -Clicks alch item
		 */
		if (settings.itemAlchCheck) {
			if (Bot.getClient().isSpellSelected()) {
				currentInventoryString = "Cast";
				currentInventoryIDs = settings.alchItemIDs;
				return Action.USE_INVENTORY;
			}
		}

		/**
		 * Eating procedure: -turns bones into peaches if peach count is
		 * random(0,1) -if health is below the require eating level, will eat
		 * -will stop script if so requested by 'stopScriptWhenNoFood'
		 */
		if (settings.eatFood) {
			if (healthCheck()) {
				if (settings.bonesToPeachesCheck) {
					if (getInventoryCount(FOODID) == random(0, 1)) {
						if (getInventoryCount(BONESPEACHTABID) == 0) {
							log("Run out of Bones to Peaches Tabs, shutting down!");
							return Action.KILLSCRIPT;
						} else {
							if (getInventoryCount(BONESID) > 0) {
								currentInventoryString = "Break";
								currentInventoryIDs = new int[] { BONESPEACHTABID };
								return Action.USE_INVENTORY;
							} else {
								log("We are low on health and no bones, shutting down!");
								return Action.KILLSCRIPT;
							}
						}
					}
				}
				if (getInventoryCount(FOODID) == 0
						&& settings.stopScriptWhenNoFood
						&& RSInterface.getInterface(149).isValid()) {
					log("Can't find any food inside inventory, shutting down!");
					return Action.KILLSCRIPT;
				} else {
					currentInventoryString = "Eat";
					currentInventoryIDs = FOODID;
					return Action.USE_INVENTORY;
				}
			}
		}

		/**
		 * Anti-random custom checks -Checks for bad monsters, i disabled the
		 * default one.
		 */
		if (randomCombatCheck()) {
			log("We are being attacked by a bad monster, running away!");
			final RSTile runAwayTile = getAvailableTile(getLocation());
			if (runAwayTile != null) {
				runEnergy = 5;
				currentWalkTile = runAwayTile;
				return Action.WALK;
			} else {
				return Action.WAIT;
			}
		}

		/**
		 * Arrow equip check -if inventory arrow count is between(100 - 200) it
		 * will equip them
		 */
		if (settings.equipArrowsCheck) {
			if (getInventoryCount(settings.equipArrowID) >= random(100, 200)) {
				currentInventoryString = "Wield";
				currentInventoryIDs = new int[] { settings.equipArrowID };
				return Action.USE_INVENTORY;
			}
		}

		/**
		 * Potions check -Checks to see if the selected potion effect has run
		 * out, uses selected potion if affected skill level is random 2 - 3
		 * levels above real level
		 */
		if (settings.usePotionCheck && currentPotionSet != null) {
			for (int a = 0; a < currentPotionSet.potions.size(); a++) {
				final int affectedStat = currentPotionSet
						.getAffectedStat(currentPotionSet.potionNames[a]);
				if (skills.getCurrentSkillLevel(affectedStat)
						- skills.getRealSkillLevel(affectedStat) <= random(2, 3)) {
					final int[] currentPotionIds = currentPotionSet
							.getPotionIDs(currentPotionSet.potionNames[a]);
					if (getInventoryCount(currentPotionIds) > 0) {
						currentInventoryString = "Drink";
						currentInventoryIDs = currentPotionIds;
						return Action.USE_INVENTORY;
					}
				}
			}
		}

		/**
		 * NPC Interactions Check -will pause if both the character and npc are
		 * interacting with each other -moves mouse to next npc is enabled -does
		 * antiban
		 */
		final RSNPC[] interNPCs = getInteractingNPCs();
		if (getMyPlayer().getInteracting() != null && interNPCs.length > 0) {
			if (interNPCs[0].getHPPercent() != 0) {
				if (settings.antiBanCheck) {
					if (System.currentTimeMillis() >= nextABTime) {
						return Action.ANTIBAN;
					}
				}
				if (settings.moveMouseToNextNPC && getMyPlayer().isInCombat()) {
					if (random(0, 5) == 3) {
						final RSNPC nextNPC = getNearestNPC(settings.npcIDs,
								settings.npcIsInCage, false);
						if (nextNPC != null) {
							final Point npcScreen = nextNPC.getScreenLocation();
							if (pointOnScreen(npcScreen)) {
								final Point randomP = new Point(
										random(npcScreen.x - 5, npcScreen.x + 5),
										random(npcScreen.y - 5, npcScreen.y + 5));
								moveMouse(randomP);
								waitRandom = new int[] { 300, 900 };
							}
						}
					}
				}
				return Action.WAIT;
			}
		}

		/**
		 * Player is moving check -wait untill player reaches around 3 tiles
		 * away from the destination
		 */
		if (getMyPlayer().isMoving() && distanceTo(getDestination()) > 2) {
			if (settings.antiBanCheck) {
				if (System.currentTimeMillis() >= nextABTime) {
					return Action.ANTIBAN;
				}
			}
			waitRandom = new int[] { 200, 600 };
			return Action.WAIT;
		}

		/**
		 * Item alching check -if alching item id found inside inventory, the
		 * spell will be cast -checks for nature runes and fire runes
		 */
		if (settings.itemAlchCheck) {
			if (getInventoryCount(NATURE_ID) == 0
					|| getInventoryCount(FIRE_ID) == 0) {
				log("Run out of alchemy runes, disabling auto alch!");
				settings.itemAlchCheck = false;
				waitRandom = new int[] { 300, 900 };
				return Action.WAIT;
			}
			if (getInventoryCount(settings.alchItemIDs) > 0) {
				return Action.USE_MAGIC;
			}
		}

		/**
		 * Drop junk -just drops empty vials
		 */
		if (getInventoryCount(JUNKIDS) > 0) {
			currentInventoryString = "Drop";
			currentInventoryIDs = JUNKIDS;
			return Action.USE_INVENTORY;
		}

		/**
		 * Bury bones check -if set amount of bones is reached(according to bury
		 * bones speed) the bones inside inventory are buried *
		 */
		if (settings.buryBonesCheck) {
			if (buryBonesTillNone && getInventoryCount(BONESID) == 0) {
				buryBonesTillNone = false;
			}
			if (buryBonesTillNone && getInventoryCount(BONESID) > 0) {
				currentInventoryString = "Bury";
				currentInventoryIDs = BONESID;
				return Action.USE_INVENTORY;
			}
			if (settings.buryBonesSpeed.equals("After picking up one")) {
				if (getInventoryCount(BONESID) > 0) {
					currentInventoryString = "Bury";
					currentInventoryIDs = BONESID;
					return Action.USE_INVENTORY;
				}
			}
			if (settings.buryBonesSpeed
					.equals("After picking up a random amount")) {
				if (getInventoryCount(BONESID) >= random(5, 15)) {
					buryBonesTillNone = true;
					currentInventoryString = "Bury";
					currentInventoryIDs = BONESID;
					return Action.USE_INVENTORY;
				}
			}
			if (settings.buryBonesSpeed.equals("When inventory is full")) {
				if (getInventoryCount() == 28 && getInventoryCount(BONESID) > 0) {
					buryBonesTillNone = true;
					currentInventoryString = "Bury";
					currentInventoryIDs = BONESID;
					return Action.USE_INVENTORY;
				}
			}
		}

		/**
		 * Special attack check -checks to see if there is enough special points
		 * to use special attack
		 */
		if (settings.useSpecialCheck) {
			if (canUseSpecial()) {
				return Action.USE_SPECIAL;
			}
		}

		/**
		 * Pick up items thread -makes sure the thread is not started again
		 */
		if (itemPickUpThread != null) {
			if (itemPickUpThread.isAlive()) {
				return Action.WAIT;
			}
		}

		/**
		 * Pick up items check -picks up items to the set speed
		 */
		if (settings.pickUpItems) {
			final String pickUpSpeed = settings.pickUpSpeed;
			RSItemTile itemTile = null;
			if (pickUpSpeed.equals("After each kill")) {
				itemTile = getNearestGroundItemByID(10, settings.itemIDs);
			}
			if (pickUpSpeed.equals("Random Occasions")) {
				if (random(0, 100) <= 10) {
					itemTile = getNearestGroundItemByID(10, settings.itemIDs);
				}
			}
			if (pickUpSpeed.equals("When no monsters around")) {
				if (npcCheck) {
					itemTile = getNearestGroundItemByID(10, settings.itemIDs);
				}
			}
			if (itemTile != null) {
				if (canPickUpItem(itemTile.getItem().getID(), true)) {
					try {
						final Point itemScreen = Calculations
								.tileToScreen(itemTile);
						if (pointOnScreen(itemScreen)
								|| distanceTo(itemTile) <= 1) {
							currentItemTile = itemTile;
							return Action.PICKUP;
						} else {
							currentWalkTile = itemTile.randomizeTile(2, 2);
							return Action.WALK;
						}
					} catch (final Exception e) {
						log.log(Level.SEVERE, "picking up item error: ", e);
						waitRandom = new int[] { 700, 1500 };
						return Action.WAIT;
					}
				}
			}
		}
		if (settings.bonesToPeachesCheck || settings.buryBonesCheck) {
			final RSItemTile boneTile = getNearestGroundItemByID(5, BONESID);
			if (boneTile != null) {
				if (canPickUpItem(boneTile.getItem().getID(), false)) {
					try {
						final Point boneScreen = Calculations
								.tileToScreen(boneTile);
						if (pointOnScreen(boneScreen)) {
							currentItemTile = boneTile;
							return Action.PICKUPBONE;
						} else {
							currentWalkTile = boneTile.randomizeTile(2, 2);
							return Action.WALK;
						}
					} catch (final Exception e) {
						log.log(Level.SEVERE, "picking up bones error: ", e);
						waitRandom = new int[] { 1000, 1900 };
						return Action.WAIT;
					}
				}
			}
		}

		/**
		 * Attacking npc -Searches for nearest npc and attacks it -Shuts down
		 * script if no npcs around for random 1min - 3min
		 */
		final RSNPC npc = getNearestNPC(settings.npcIDs, settings.npcIsInCage,
				true);
		if (npc != null) {
			npcCheck = false;
			try {
				final Point npcScreen = npc.getScreenLocation();
				if (pointOnScreen(npcScreen)
						|| distanceTo(npc.getLocation()) <= 1) {
					currentNPC = npc;
					return Action.ATTACK;
				} else {
					currentWalkTile = npc.getLocation().randomizeTile(2, 2);
					return Action.WALK;
				}
			} catch (final Exception e) {
				log.log(Level.SEVERE, "attacking npc error: ", e);
				waitRandom = new int[] { 1300, 1900 };
				return Action.WAIT;
			}
		} else {
			if (!npcCheck) {
				lastNPCCheck = System.currentTimeMillis();
				npcCheck = true;
			}
			if (npcCheck) {
				if (System.currentTimeMillis() - lastNPCCheck >= random(60000,
						180000)) {
					log("No npcs around for a while, shutting down script!");
					return Action.KILLSCRIPT;
				}
			}
		}
		return null;
	}

	/*****************************************************************************
	 * \ SCRIPT DECLARATION \
	 *****************************************************************************/

	private RSTile getAvailableTile(final RSTile playerLoc) {
		if (playerLoc == null) {
			return null;
		} else {
			final RSTile tile1 = new RSTile(playerLoc.getX() - random(9, 13),
					playerLoc.getY());
			final RSTile tile2 = new RSTile(playerLoc.getX(), playerLoc.getY()
					- random(9, 13));
			final RSTile tile3 = new RSTile(playerLoc.getX() + random(9, 13),
					playerLoc.getY());
			final RSTile tile4 = new RSTile(playerLoc.getX(), playerLoc.getY()
					+ random(9, 13));
			if (canReach(tile1, true)) {
				return tile1;
			} else if (canReach(tile2, true)) {
				return tile2;
			} else if (canReach(tile3, true)) {
				return tile3;
			} else if (canReach(tile4, true)) {
				return tile4;
			} else {
				return null;
			}
		}
	}

	private RSNPC[] getInteractingNPCs() {
		final ArrayList<RSNPC> npcList = new ArrayList<RSNPC>();
		final org.rsbot.accessors.RSNPC[] npcs = Bot.getClient()
				.getRSNPCArray();
		for (final org.rsbot.accessors.RSNPC npc2 : npcs) {
			final RSNPC npc = new RSNPC(npc2);
			if (!npc.isValid()) {
				continue;
			}
			if (npc.isInteractingWithLocalPlayer()) {
				npcList.add(npc);
			}
		}
		return npcList.toArray(new RSNPC[npcList.size()]);
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

	public RSNPC getNearestNPC(final int[] ids, final boolean inCage,
			final boolean isInteracting) {
		int Dist = 99999;
		RSNPC closest = null;
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();
		final org.rsbot.accessors.RSNPC[] npcs = Bot.getClient()
				.getRSNPCArray();

		for (final int element : validNPCs) {
			if (npcs[element] == null) {
				continue;
			}
			final RSNPC Monster = new RSNPC(npcs[element]);
			if (Monster.getHPPercent() == 0) {
				continue;
			}
			if (isInteracting && Monster.isInteractingWithLocalPlayer()
					&& canReach(Monster.getLocation(), false)) {
				return Monster;
			}
			if (!inCage && !canReach(Monster.getLocation(), false)) {
				continue;
			}
			try {
				for (final int id : ids) {
					if (id != Monster.getID() || Monster.isInCombat()) {
						continue;
					}
					final int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (final Exception e) {
			}
		}
		return closest;
	}

	private boolean healthCheck() {
		try {
			if (System.currentTimeMillis() - healthRandomTime > 120000) {
				healthRandomTime = System.currentTimeMillis();
				hpToHealAt = (int) random(skills.getRealSkillLevel(3) / 3,
						skills.getRealSkillLevel(3) / 1.5);
			}
			return skills.getCurrentSkillLevel(3) <= hpToHealAt;
		} catch (final Exception e) {
			log.log(Level.SEVERE, "healthCheck() error: ", e);
		}
		return false;
	}

	// Optimized version of array contains string
	private boolean listContainsString(final List<String> list,
			final String string) {
		try {
			int a;
			for (a = list.size() - 1; a-- >= 0;) {
				if (list.get(a).contains(string)) {
					return true;
				}
			}
		} catch (final Exception e) {
		}
		return false;
	}

	@Override
	public int loop() {

		/**
		 * Check to see if player is fully logged in -checks login index -checks
		 * to see that inventory has been loaded -checks to see if welcome
		 * screen is up
		 */
		if (!isLoggedIn()
				|| RSInterface.getInterface(378).getChild(45).getAbsoluteX() > 20
				&& !RSInterface.getInterface(149).isValid()) {
			return random(1200, 3100);
		}

		if (canContinue()) {
			clickContinue();
			return random(544, 1200);
		}

		/**
		 * Run check -turns run on when not running and energy is above a random
		 * amount
		 */
		if (runCheck()) {
			setRun(true);
			return random(450, 950);
		}

		final Action action = getAction();
		if (action != null) {
			switch (action) {
			case ATTACK:
				if (currentNPC != null) {
					if (!clickNPC(currentNPC, "Attack")) {
						return random(800, 1600);
					} else {
						return random(1000, 2500);
					}
				} else {
					return random(310, 510);
				}
			case WALK:
				if (currentWalkTile != null) {
					if (tileOnMap(currentWalkTile)) {
						walkTo(currentWalkTile);
						return random(431, 910);
					}
					final RSTile[] path = generatePath(currentWalkTile);
					if (path.length > 1) {
						final RSTile tile = nextTile(path, 10);
						if (tile != null) {
							walkTo(tile);
							return random(410, 800);
						}
					} else if (path.length == 1) {
						walkTo(currentWalkTile);
						return random(410, 910);
					} else {
						if (tileOnMap(currentWalkTile)) {
							walkTo(currentWalkTile);
							return random(610, 1200);
						} else {
							return random(410, 500);
						}
					}
				} else {
					return random(410, 510);
				}
			case PICKUP:
				if (currentItemTile != null) {
					if (!pickUpItem(currentItemTile, settings.itemNames)) {
						pickUpFail++;
						if (pickUpFail >= 10) {
							turnToTile(currentItemTile);
							pickUpFail = 0;
							return random(310, 910);
						}
						return random(140, 510);
					} else {
						pickUpFail = 0;
						return random(410, 870);
					}
				} else {
					return random(200, 500);
				}
			case PICKUPBONE:
				if (currentItemTile != null) {
					if (!pickUpItem(currentItemTile, BONE_NAMES)) {
						pickUpBoneFail++;
						if (pickUpBoneFail >= 10) {
							turnToTile(currentItemTile);
							return random(310, 510);
						}
						return random(140, 510);
					} else {
						pickUpBoneFail = 0;
						return random(410, 870);
					}
				} else {
					return random(200, 500);
				}
			case USE_INVENTORY:
				if (currentInventoryIDs.length > 0) {
					if (!clickInventoryItem(currentInventoryIDs,
							currentInventoryString)) {
						return random(180, 410);
					} else {
						return random(924, 2111);
					}
				} else {
					return random(200, 500);
				}
			case USE_SPECIAL:
				if (getCurrentTab() != Constants.TAB_ATTACK) {
					openTab(Constants.TAB_ATTACK);
					wait(random(300, 900));
				}
				clickMouse(random(578, 705), random(414, 426), true);
				return random(200, 1300);
			case USE_MAGIC:
				if (getCurrentTab() != Constants.TAB_MAGIC) {
					openTab(Constants.TAB_MAGIC);
					wait(random(300, 1400));
				}
				try {
					atInterface(INTERFACE_MAGIC, currentMagicSpell);
					return random(1200, 2400);
				} catch (final Exception e) {
					log.log(Level.SEVERE, "error using spell: ", e);
					return random(100, 200);
				}
			case WAIT:
				return random(waitRandom[0], waitRandom[1]);

			case KILLSCRIPT:
				log("Killing script...");
				stopScript();
				return -1;
			case ANTIBAN:
				return antiBan();
			}
		}

		return random(340, 800);
	}

	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
	}

	/*****************************************************************************
	 * \ GRAPHICAL PAINT \
	 *****************************************************************************/
	public void onRepaint(final Graphics g) {
		int startAttackExp = 0, startStrengthExp = 0, startDefenseExp = 0, startHitpointsExp = 0, startRangeExp = 0, startMagicExp = 0, startPrayerExp = 0;
		if (settings.reportSetting.equals("Paint") && isLoggedIn()) {
			startAttackExp = experienceHandler
					.getSkillExp(Constants.STAT_ATTACK);
			startStrengthExp = experienceHandler
					.getSkillExp(Constants.STAT_STRENGTH);
			startDefenseExp = experienceHandler
					.getSkillExp(Constants.STAT_DEFENSE);
			startHitpointsExp = experienceHandler
					.getSkillExp(Constants.STAT_HITPOINTS);
			startRangeExp = experienceHandler.getSkillExp(Constants.STAT_RANGE);
			startMagicExp = experienceHandler.getSkillExp(Constants.STAT_MAGIC);
			startPrayerExp = experienceHandler
					.getSkillExp(Constants.STAT_PRAYER);
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}
			if (startAttackExp == 0) {
				experienceHandler.setExperiencePoints(Constants.STAT_ATTACK,
						skills.getCurrentSkillExp(Constants.STAT_ATTACK));
			}
			if (startStrengthExp == 0) {
				experienceHandler.setExperiencePoints(Constants.STAT_STRENGTH,
						skills.getCurrentSkillExp(Constants.STAT_STRENGTH));
			}
			if (startDefenseExp == 0) {
				experienceHandler.setExperiencePoints(Constants.STAT_DEFENSE,
						skills.getCurrentSkillExp(Constants.STAT_DEFENSE));
			}
			if (startHitpointsExp == 0) {
				experienceHandler.setExperiencePoints(Constants.STAT_HITPOINTS,
						skills.getCurrentSkillExp(Constants.STAT_HITPOINTS));
			}
			if (startRangeExp == 0) {
				experienceHandler.setExperiencePoints(Constants.STAT_RANGE,
						skills.getCurrentSkillExp(Constants.STAT_RANGE));
			}
			if (startMagicExp == 0) {
				experienceHandler.setExperiencePoints(Constants.STAT_MAGIC,
						skills.getCurrentSkillExp(Constants.STAT_MAGIC));
			}
			if (startPrayerExp == 0) {
				experienceHandler.setExperiencePoints(Constants.STAT_PRAYER,
						skills.getCurrentSkillExp(Constants.STAT_PRAYER));
			}

			final ArrayList<String> reportList = new ArrayList<String>();
			reportList.add(getClass().getAnnotation(ScriptManifest.class)
					.name()
					+ " V"
					+ getClass().getAnnotation(ScriptManifest.class).version());
			final int currentAttackExp = skills
					.getCurrentSkillExp(Constants.STAT_ATTACK);
			final int currentStrengthExp = skills
					.getCurrentSkillExp(Constants.STAT_STRENGTH);
			final int currentDefenseExp = skills
					.getCurrentSkillExp(Constants.STAT_DEFENSE);
			final int currentHitpointsExp = skills
					.getCurrentSkillExp(Constants.STAT_HITPOINTS);
			final int currentRangeExp = skills
					.getCurrentSkillExp(Constants.STAT_RANGE);
			final int currentMagicExp = skills
					.getCurrentSkillExp(Constants.STAT_MAGIC);
			final int currentPrayerExp = skills
					.getCurrentSkillExp(Constants.STAT_PRAYER);
			final int attackExpChange = currentAttackExp - startAttackExp;
			if (attackExpChange > 0) {
				reportList.add("Attack Exp: " + attackExpChange);
			}
			final int strengthExpChange = currentStrengthExp - startStrengthExp;
			if (strengthExpChange > 0) {
				reportList.add("Strength Exp: " + strengthExpChange);
			}
			final int defenseExpChange = currentDefenseExp - startDefenseExp;
			if (defenseExpChange > 0) {
				reportList.add("Defense Exp: " + defenseExpChange);
			}
			final int hitpointsExpChange = currentHitpointsExp
					- startHitpointsExp;
			if (hitpointsExpChange > 0) {
				reportList.add("Hitpoints Exp: " + hitpointsExpChange);
			}
			final int rangeExpChange = currentRangeExp - startRangeExp;
			if (rangeExpChange > 0) {
				reportList.add("Range Exp: " + rangeExpChange);
			}
			final int magicExpChange = currentMagicExp - startMagicExp;
			if (magicExpChange > 0) {
				reportList.add("Magic Exp: " + magicExpChange);
			}
			final int prayerExpChange = currentPrayerExp - startPrayerExp;
			if (prayerExpChange > 0) {
				reportList.add("Prayer Exp: " + prayerExpChange);
			}

			long millis = System.currentTimeMillis() - startTime;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;
			reportList.add("Runtime: " + hours + ":" + minutes + ":" + seconds);

			Point str = new Point(350, 25);
			g.setColor(new Color(60, 140, 200, 50));
			g.fill3DRect(340, 10, 165, reportList.size() * 20 + 6, true);
			g.setColor(new Color(60, 155, 159, 50));
			g.fill3DRect(341, 11, 163, reportList.size() * 20 + 4, true);
			g.setColor(Color.WHITE);
			for (int a = 0; a < reportList.size(); a++) {
				g.drawString(reportList.get(a), str.x, str.y);
				str = new Point(str.x, str.y + 20);
			}
		}
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		final int welcome = JOptionPane
				.showConfirmDialog(
						null,
						"Before using my script, would you like to thank me\nby clicking some adverts?",
						"Welcome", JOptionPane.YES_NO_OPTION);
		if (welcome == 0) {
			final String message = "<html><h1>Thank you for your support!</h1><br/>"
					+ "<p>You will now be redirected to my adverts page. <br/>"
					+ "Click the adverts on the page few times a day if you can.</p>"
					+ "</html>";
			JOptionPane.showMessageDialog(null, message);
		}

		if (settings.settingsExist()) {
			showSettingsInterface();
			try {
				settings.setSettings();
				npcIDs.setText(settings.intArrayToString(settings.npcIDs));
				specialCheck.setSelected(settings.useSpecialCheck);
				npcsInCageCheck.setSelected(settings.npcIsInCage);
				pickUpCheck.setSelected(settings.pickUpItems);
				if (settings.pickUpItems) {
					itemIDs
							.setText(settings
									.intArrayToString(settings.itemIDs));
					itemNamesText.setText(settings
							.stringArrayToString(settings.itemNames));
					pickUpSpeedCombo.setSelectedItem(settings.pickUpSpeed);
				}
				equipArrowsCheck.setSelected(settings.equipArrowsCheck);
				if (settings.equipArrowsCheck) {
					equipArrowText.setText("" + settings.equipArrowID);
				}
				bonesCheck.setSelected(settings.buryBonesCheck);
				if (settings.buryBonesCheck) {
					burySpeedCombo.setSelectedItem(settings.buryBonesSpeed);
				}
				usePotionsCheck.setSelected(settings.usePotionCheck);
				if (settings.usePotionCheck) {
					potionList.setListData(settings.potionList);
				}
				useItemAlchCheck.setSelected(settings.itemAlchCheck);
				if (settings.itemAlchCheck) {
					alchItemIDsText.setText(settings
							.intArrayToString(settings.alchItemIDs));
				}
				eatFoodCheck.setSelected(settings.eatFood);
				stopScriptWhenNoFoodCheck
						.setSelected(settings.stopScriptWhenNoFood);
				peachesCheck.setSelected(settings.bonesToPeachesCheck);
				reportCombo.setSelectedItem(settings.reportSetting);
				nextNPCCheck.setSelected(settings.moveMouseToNextNPC);
				specialCheck.setSelected(settings.useSpecialCheck);
				antibanCheck.setSelected(settings.antiBanCheck);
			} catch (final Exception e) {
				log.log(Level.SEVERE, "error getting settings: ", e);
			}
		} else {
			showSettingsInterface();
		}
		while (!START_SCRIPT) {
			wait(10);
		}

		log("*******************************************");
		log("Ruskis Fighter V"
				+ getClass().getAnnotation(ScriptManifest.class).version()
				+ " started!");
		log("*******************************************");
		START_SCRIPT = false;
		return true;
	}

	public void openURL(final String url) { // Credits to Dave who gave credits
		// to
		// some guy who made this.
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
			} else { // assume Unix or Linux
				final String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape" };
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

	private boolean pickUpItem(final RSItemTile item, final String[] itemNames) {
		try {
			itemPickUpThread = new RFV2_ItemPickUpThread(item, itemNames);
			itemPickUpThread.start();
			return true;
		} catch (final Exception e) {
			log.log(Level.SEVERE, "pickUPItem(RSItemTile, String[]): ", e);
			return false;
		}
	}

	private boolean randomCombatCheck() {
		try {
			final RSCharacter interact = getMyPlayer().getInteracting();
			if (interact instanceof RSNPC) {
				final RSNPC npc = (RSNPC) interact;
				final String name = npc.getName();
				for (final int n : RuskisFighterV2.BAD_IDS) {
					if (npc.getID() == n) {
						return true;
					}
				}
				for (final String n : RuskisFighterV2.BAD_MONSTERS) {
					if (n.equals(name)) {
						return true;
					}
				}
			}
		} catch (final Exception e) {
			log.log(Level.SEVERE, "randomCombatCheck() error: ", e);
		}
		return false;
	}

	private void resetVariables() {
		currentNPC = null;
		currentItemTile = null;
		currentInventoryIDs = null;
		currentInventoryString = "";
		currentMagicSpell = 0;
		currentWalkTile = null;
	}

	private boolean runCheck() {
		try {
			if (getEnergy() >= runEnergy && !isRunning()) {
				runEnergy = random(15, 50);
				return true;
			} else {
				return false;
			}
		} catch (final Exception e) {
			log.log(Level.SEVERE, "runCheck() error: ", e);
			return false;
		}
	}

	private void setVariablesToSkill() {
		if (skills.getRealSkillLevel(Constants.STAT_MAGIC) >= 55) {
			currentMagicSpell = INTERFACE_HIGH_ALCH;
		} else {
			currentMagicSpell = INTERFACE_LOW_ACLH;
		}
	}

	/*****************************************************************************
	 * \ USER INTERFACE - Generate by Netbeans IDE 6.1 - edited by Ruski \
	 *****************************************************************************/
	private void showSettingsInterface() {

		mainFrame = new javax.swing.JFrame("Ruskis Fighter V"
				+ getClass().getAnnotation(ScriptManifest.class).version());
		jPanel1 = new javax.swing.JPanel();
		jLabel111 = new javax.swing.JLabel();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel2 = new javax.swing.JPanel();
		jLabel222 = new javax.swing.JLabel();
		jLabel333 = new javax.swing.JLabel();
		npcIDs = new javax.swing.JTextField();
		jLabel444 = new javax.swing.JLabel();
		specialCheck = new javax.swing.JCheckBox();
		npcsInCageCheck = new javax.swing.JCheckBox();
		jPanel3 = new javax.swing.JPanel();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		itemIDs = new javax.swing.JTextField();
		jLabel10 = new javax.swing.JLabel();
		itemNamesText = new javax.swing.JTextField();
		pickUpCheck = new javax.swing.JCheckBox();
		jLabel12 = new javax.swing.JLabel();
		pickUpSpeedCombo = new javax.swing.JComboBox();
		jLabel9 = new javax.swing.JLabel();
		jLabel11 = new javax.swing.JLabel();
		jPanel4 = new javax.swing.JPanel();
		eatFoodCheck = new javax.swing.JCheckBox();
		jLabel14 = new javax.swing.JLabel();
		stopScriptWhenNoFoodCheck = new javax.swing.JCheckBox();
		peachesCheck = new javax.swing.JCheckBox();
		jLabel15 = new javax.swing.JLabel();
		jLabel16 = new javax.swing.JLabel();
		jPanel5 = new javax.swing.JPanel();
		bonesCheck = new javax.swing.JCheckBox();
		jLabel17 = new javax.swing.JLabel();
		jLabel18 = new javax.swing.JLabel();
		burySpeedCombo = new javax.swing.JComboBox();
		jLabel19 = new javax.swing.JLabel();
		jPanel6 = new javax.swing.JPanel();
		usePotionsCheck = new javax.swing.JCheckBox();
		jLabel20 = new javax.swing.JLabel();
		jLabel21 = new javax.swing.JLabel();
		potionCombo = new javax.swing.JComboBox();
		addPotionButton = new javax.swing.JButton();
		potionScroll = new javax.swing.JScrollPane();
		potionList = new javax.swing.JList();
		removePotionButton = new javax.swing.JButton();
		jPanel8 = new javax.swing.JPanel();
		useItemAlchCheck = new javax.swing.JCheckBox();
		jLabel23 = new javax.swing.JLabel();
		jLabel24 = new javax.swing.JLabel();
		alchItemIDsText = new javax.swing.JTextField();
		jLabel25 = new javax.swing.JLabel();
		jLabel26 = new javax.swing.JLabel();
		jPanel9 = new javax.swing.JPanel();
		jLabel6 = new javax.swing.JLabel();
		equipArrowsCheck = new javax.swing.JCheckBox();
		jLabel13 = new javax.swing.JLabel();
		equipArrowText = new javax.swing.JTextField();
		jPanel7 = new javax.swing.JPanel();
		jLabel27 = new javax.swing.JLabel();
		antibanCheck = new javax.swing.JCheckBox();
		nextNPCCheck = new javax.swing.JCheckBox();
		jLabel28 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		reportCombo = new javax.swing.JComboBox();
		startButton = new javax.swing.JButton();
		saveFormSettingsCheck = new javax.swing.JCheckBox();

		mainFrame
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				"Welcome",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		jLabel111
				.setText("<html>Fill out the below form to start auto fighting. You can also click on \"Save Settings\" to save the form input for future use.</html>");

		final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout.createSequentialGroup().addContainerGap()
						.addComponent(jLabel111,
								javax.swing.GroupLayout.PREFERRED_SIZE, 415,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(110, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout.createSequentialGroup().addComponent(jLabel111,
						javax.swing.GroupLayout.DEFAULT_SIZE, 28,
						Short.MAX_VALUE).addContainerGap()));

		jLabel222
				.setText("<html>Put in the monster IDs you would like to auto fight. Separate each ID with a comma, as shown in the example below.</html>");

		jLabel333.setText("NPC IDs:");

		jLabel444.setText("(separate each ID with a comma, e.g 123,23,12)");

		specialCheck.setText("Use Special Attack?");

		npcsInCageCheck.setText("NPCs are in cage (range, magic)");

		final javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addGap(10, 10, 10)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel333)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								npcIDs,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								236,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addGroup(
																								jPanel2Layout
																										.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.TRAILING,
																												false)
																										.addGroup(
																												javax.swing.GroupLayout.Alignment.LEADING,
																												jPanel2Layout
																														.createSequentialGroup()
																														.addComponent(
																																specialCheck)
																														.addPreferredGap(
																																javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																Short.MAX_VALUE)
																														.addComponent(
																																npcsInCageCheck))
																										.addComponent(
																												jLabel444,
																												javax.swing.GroupLayout.Alignment.LEADING))))
														.addComponent(
																jLabel222,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																429,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(103, Short.MAX_VALUE)));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel222)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel333)
														.addComponent(
																npcIDs,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel444)
										.addGap(18, 18, 18)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																specialCheck)
														.addComponent(
																npcsInCageCheck))
										.addContainerGap(93, Short.MAX_VALUE)));

		jTabbedPane1.addTab("Monsters", jPanel2);

		jLabel7
				.setText("<html>Enter the IDs and the names of the items you wish to pick up. Please make sure you put in both the ID and the name for the item and the spelling of the item name is exactly as in the game.</html>");

		jLabel8.setText("Item IDs:");

		jLabel10.setText("Item Names:");

		pickUpCheck.setText("Use Item Pick Up?");

		jLabel12.setText("Pick Up Speed:");

		pickUpSpeedCombo.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "After each kill", "Random Occasions",
						"When no monsters around" }));

		jLabel9.setText("(separate IDs with a comma, e.g 123,21,31)");

		jLabel11.setText("(separate names with a comma, e.g Coins,Iron arrow)");

		final javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel3Layout
																		.createSequentialGroup()
																		.addComponent(
																				pickUpCheck)
																		.addContainerGap())
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																jPanel3Layout
																		.createSequentialGroup()
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel3Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								jPanel3Layout
																										.createSequentialGroup()
																										.addGroup(
																												jPanel3Layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addGroup(
																																jPanel3Layout
																																		.createSequentialGroup()
																																		.addGap(
																																				8,
																																				8,
																																				8)
																																		.addComponent(
																																				jLabel10))
																														.addGroup(
																																jPanel3Layout
																																		.createSequentialGroup()
																																		.addGap(
																																				10,
																																				10,
																																				10)
																																		.addComponent(
																																				jLabel8)))
																										.addGap(
																												18,
																												18,
																												18)
																										.addGroup(
																												jPanel3Layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.TRAILING,
																																false)
																														.addComponent(
																																itemNamesText,
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addComponent(
																																jLabel9,
																																javax.swing.GroupLayout.Alignment.LEADING,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																Short.MAX_VALUE)
																														.addComponent(
																																jLabel11,
																																javax.swing.GroupLayout.Alignment.LEADING,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																Short.MAX_VALUE)
																														.addComponent(
																																itemIDs,
																																javax.swing.GroupLayout.Alignment.LEADING,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																258,
																																javax.swing.GroupLayout.PREFERRED_SIZE)))
																						.addGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								jPanel3Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel12)
																										.addGap(
																												18,
																												18,
																												18)
																										.addComponent(
																												pickUpSpeedCombo,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addComponent(
																								jLabel7,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								421,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addContainerGap()))));
		jPanel3Layout
				.setVerticalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(pickUpCheck)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jLabel7,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												47,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel8)
														.addComponent(
																itemIDs,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel9)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel10)
														.addComponent(
																itemNamesText,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel11)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel12)
														.addComponent(
																pickUpSpeedCombo,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(27, Short.MAX_VALUE)));

		jTabbedPane1.addTab("Item Pick Up", jPanel3);

		eatFoodCheck.setText("Eat Food?");

		jLabel14
				.setText("<html>Food is automatically found in your inventory and eaten at a random hp. You can select \"Stop script when no food\", so when you run out of food, the script is ended.</html>");

		stopScriptWhenNoFoodCheck.setText("Stop script when no food?");

		peachesCheck.setText("Use bones to peaches?");

		jLabel15
				.setText("<html>Bones to peaches useus \"Bones to Peaches\" tabs. The script will automatically pick up bones and when inventory full or more food is needed, it will turn the bones into peaches.</html>");

		jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
		jLabel16.setForeground(new java.awt.Color(204, 0, 0));
		jLabel16
				.setText("<html>WARNING: \"Bury Bones\" function is automally disabled when this feature is used.</html>");

		final javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(
				jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout
				.setHorizontalGroup(jPanel4Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel4Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel4Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																peachesCheck)
														.addComponent(
																jLabel14,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																522,
																Short.MAX_VALUE)
														.addGroup(
																jPanel4Layout
																		.createSequentialGroup()
																		.addComponent(
																				eatFoodCheck)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				stopScriptWhenNoFoodCheck))
														.addComponent(
																jLabel15,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																522,
																Short.MAX_VALUE)
														.addComponent(
																jLabel16,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																423,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));
		jPanel4Layout
				.setVerticalGroup(jPanel4Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel4Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel4Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																eatFoodCheck)
														.addComponent(
																stopScriptWhenNoFoodCheck))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jLabel14)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(peachesCheck)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel15)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel16)
										.addContainerGap(65, Short.MAX_VALUE)));

		jTabbedPane1.addTab("Food/Eating", jPanel4);

		bonesCheck.setText("Collect and Bury Bones?");

		jLabel17
				.setText("<html>Will collect all types of bones and bury them according to \"When to bury\" is specified. Cannot be used with \"Bones to peaches\" functions.</html>");

		jLabel18.setText("When to bury:");

		burySpeedCombo.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "After picking up one",
						"After picking up a random amount",
						"When inventory is full" }));

		jLabel19
				.setText("<html>When random is selected, the script will bury bones when the bone count inside the inventory is between 5 - 15 bones.</html> ");

		final javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(
				jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout
				.setHorizontalGroup(jPanel5Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel5Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel5Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																bonesCheck)
														.addGroup(
																jPanel5Layout
																		.createSequentialGroup()
																		.addGap(
																				10,
																				10,
																				10)
																		.addComponent(
																				jLabel18)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				burySpeedCombo,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(
																jLabel17,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																428,
																Short.MAX_VALUE)
														.addComponent(jLabel19,
																0, 0,
																Short.MAX_VALUE))
										.addContainerGap(104, Short.MAX_VALUE)));
		jPanel5Layout
				.setVerticalGroup(jPanel5Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel5Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(bonesCheck)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jLabel17)
										.addGap(11, 11, 11)
										.addGroup(
												jPanel5Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel18)
														.addComponent(
																burySpeedCombo,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jLabel19,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												41,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(81, Short.MAX_VALUE)));

		jTabbedPane1.addTab("Bury Bones", jPanel5);

		usePotionsCheck.setText("Use Potions?");

		jLabel20
				.setText("<html>When enabled, the script will automatically drink the desired potion(s) when the affected skill is only 2-3 above the actual skill level. Use the combo list of potions to selected the required potion and press add to add it to the potion list. To remove a potion from the list, select the potion in the list and press remove.</html>");

		jLabel21.setText("Potion Combo:");

		potionCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"Regular Attack Potion", "Regular Strength Potion",
				"Super Strength Potion", "Super Defense Potion",
				"Super Attack Potion", "Magic Potion", "Ranging Potion" }));

		addPotionButton.setText("ADD");
		addPotionButton.addActionListener(this);
		potionList
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		potionScroll.setViewportView(potionList);

		removePotionButton.setText("REMOVE");
		removePotionButton.addActionListener(this);
		final javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(
				jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout
				.setHorizontalGroup(jPanel6Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel6Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel6Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																usePotionsCheck)
														.addGroup(
																jPanel6Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel6Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								false)
																						.addGroup(
																								jPanel6Layout
																										.createSequentialGroup()
																										.addGap(
																												10,
																												10,
																												10)
																										.addComponent(
																												jLabel21)
																										.addGap(
																												18,
																												18,
																												18)
																										.addComponent(
																												potionCombo,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addComponent(
																								potionScroll,
																								0,
																								0,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel6Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								false)
																						.addComponent(
																								removePotionButton,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								addPotionButton,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								100,
																								Short.MAX_VALUE)))
														.addComponent(
																jLabel20,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																428,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(104, Short.MAX_VALUE)));
		jPanel6Layout
				.setVerticalGroup(jPanel6Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel6Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(usePotionsCheck)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel20)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel6Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																jPanel6Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				potionCombo,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				addPotionButton))
														.addComponent(jLabel21))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												jPanel6Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																potionScroll,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																85,
																Short.MAX_VALUE)
														.addComponent(
																removePotionButton))
										.addContainerGap()));

		jTabbedPane1.addTab("Potions", jPanel6);

		useItemAlchCheck.setText("Use Item Alching?");

		jLabel23
				.setText("<html>To use this function, make sure that you have some kind of item(s) that you are picking to be alched. Also have the required runes. Below you can enter the item IDs that you wish to be alched, make sure they are also in the \"Item Pick Up\" functions.</html>");

		jLabel24.setText("Alch Item IDs:");

		jLabel25.setText("(separate each ID with a comma, e.g 123,23,12)");

		jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11));
		jLabel26.setForeground(new java.awt.Color(204, 0, 0));
		jLabel26
				.setText("WARNING: Make sure you have enough inventory space for coins.");

		final javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(
				jPanel8);
		jPanel8.setLayout(jPanel8Layout);
		jPanel8Layout
				.setHorizontalGroup(jPanel8Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel8Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel8Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel8Layout
																		.createSequentialGroup()
																		.addGap(
																				22,
																				22,
																				22)
																		.addGroup(
																				jPanel8Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jLabel26)
																						.addGroup(
																								jPanel8Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel24)
																										.addGap(
																												18,
																												18,
																												18)
																										.addGroup(
																												jPanel8Layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING,
																																false)
																														.addComponent(
																																alchItemIDsText)
																														.addComponent(
																																jLabel25,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																Short.MAX_VALUE)))))
														.addComponent(
																useItemAlchCheck)
														.addGroup(
																jPanel8Layout
																		.createSequentialGroup()
																		.addGap(
																				12,
																				12,
																				12)
																		.addComponent(
																				jLabel23,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				461,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(63, Short.MAX_VALUE)));
		jPanel8Layout
				.setVerticalGroup(jPanel8Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel8Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(useItemAlchCheck)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jLabel23)
										.addGap(11, 11, 11)
										.addGroup(
												jPanel8Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel24)
														.addComponent(
																alchItemIDsText,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jLabel25).addGap(18, 18,
												18).addComponent(jLabel26)
										.addContainerGap(85, Short.MAX_VALUE)));

		jTabbedPane1.addTab("Item alching", jPanel8);

		jLabel6
				.setText("<html>Here you can specify if you would like to equip arrows or not, it will equip when there are random 100 - 200 arrows in the inventory. Also put in arrow id you would like to equip.</html>");

		equipArrowsCheck.setText("Equip Arrows?");

		jLabel13.setText("Arrow ID:");

		final javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(
				jPanel9);
		jPanel9.setLayout(jPanel9Layout);
		jPanel9Layout
				.setHorizontalGroup(jPanel9Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel9Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel9Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																equipArrowsCheck)
														.addGroup(
																jPanel9Layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel13)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				equipArrowText,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				227,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(
																jLabel6,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																457,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(75, Short.MAX_VALUE)));
		jPanel9Layout
				.setVerticalGroup(jPanel9Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel9Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel6)
										.addGap(18, 18, 18)
										.addComponent(equipArrowsCheck)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												jPanel9Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel13)
														.addComponent(
																equipArrowText,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(117, Short.MAX_VALUE)));

		jTabbedPane1.addTab("Arrows", jPanel9);

		jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11));
		jLabel27.setForeground(new java.awt.Color(255, 0, 0));
		jLabel27
				.setText("WARNING: Do not use this anti ban with any other anti ban scripts.");

		antibanCheck.setText("Use Anti Ban?");

		nextNPCCheck.setText("Move mouse to next NPC?");

		jLabel28
				.setText("<html>When enabled the script will move the mouse to the next monster that you are going to attack. It will hover over the monster while you in combat with the current one. Advantage is that it saved mouse movement time between attacks.</html>");

		jLabel5.setText("Report view:");

		reportCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"Disabled", "Paint" }));

		final javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(
				jPanel7);
		jPanel7.setLayout(jPanel7Layout);
		jPanel7Layout
				.setHorizontalGroup(jPanel7Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel7Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel7Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																nextNPCCheck)
														.addGroup(
																jPanel7Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING,
																				false)
																		.addComponent(
																				antibanCheck)
																		.addComponent(
																				jLabel27,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				jLabel28,
																				0,
																				0,
																				Short.MAX_VALUE))
														.addGroup(
																jPanel7Layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel5)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				reportCombo,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(161, Short.MAX_VALUE)));
		jPanel7Layout
				.setVerticalGroup(jPanel7Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel7Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(antibanCheck)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jLabel27)
										.addGap(18, 18, 18)
										.addComponent(nextNPCCheck)
										.addGap(7, 7, 7)
										.addComponent(jLabel28)
										.addGap(18, 18, 18)
										.addGroup(
												jPanel7Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel5)
														.addComponent(
																reportCombo,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(31, Short.MAX_VALUE)));

		jTabbedPane1.addTab("Other Options", jPanel7);

		startButton.setText("START SCRIPT!");
		startButton.addActionListener(this);
		saveFormSettingsCheck.setText("Don't Save Form Settings?");

		final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				mainFrame.getContentPane());
		mainFrame.getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																jTabbedPane1,
																javax.swing.GroupLayout.Alignment.LEADING,
																0, 0,
																Short.MAX_VALUE)
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																layout
																		.createSequentialGroup()
																		.addComponent(
																				saveFormSettingsCheck)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				255,
																				Short.MAX_VALUE)
																		.addComponent(
																				startButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				169,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(
																jPanel1,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jPanel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jTabbedPane1,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												252, Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																saveFormSettingsCheck)
														.addComponent(
																startButton))
										.addContainerGap()));

		mainFrame.pack();
		mainFrame.setVisible(true);
	}
}
