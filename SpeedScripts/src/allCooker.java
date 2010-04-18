import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.awt.*;
import java.util.*;
import java.applet.*;
import java.awt.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.lang.Object.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.LineBorder.*;
import javax.swing.border.*;
import java.net.*; 
import javax.swing.JOptionPane; 
import javax.swing.JFileChooser;  
import java.net.URI;
import java.awt.Desktop;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import java.awt.*;
import java.util.*;
import java.applet.*;
import java.awt.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.lang.Object.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.LineBorder.*;
import javax.swing.border.*;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.*;
import org.rsbot.bot.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.event.listeners.*;
import org.rsbot.event.events.*;
import org.rsbot.script.Random;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.script.ScriptManifest;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = "Peach", category = "Cooking", name = "All Cooker", version = 1.7, description = "<html><center>"
+ "<h2>All Cooker</h2><br />"
+ "<b>Author:</b>Peach<br />"
+ "<b>Version:</b>1.7f<br>"
+ "<b>Revision:</b>002<br>"
+ "<b>Check for update?</b> "
+ "<select name=\'Update\'><option selected>Yes<option>No</select><br>"
+ "<i>Script will open up a donation link upon starting.</i><br>"
+ "<b>Instructions:</b>"
+ "<br>Start anywhere near fire or bank in Rouge's Den or Al Kharid."
+ "<br>Have bank scrolled all the way up with raw food visible."
+ "<br>Have your inventory full of your raw food. Works with universally ANY food, just start with it in your inventory.<br>"
//+ "Language? "
//+ "<select name=\"language\"><option selected>English<option>German</select>"
+ "</center></html>")
public class allCooker extends Script implements PaintListener {
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	public int Revision = 2;


	public long startTime = System.currentTimeMillis();
	public long seconds = 1;
	public long minutes = 0;
	public long hours = 0;
	public long wineHour = 1;
	public long xpHour = 1;
	public int startXP = 0;
	public long lvlHour = 0;
	public long lvlMin = 0;
	public long lvlSec = 1;
	public long lvlError = 0;
	public long toNextLvl = 1;
	public long millisBefore = 0;
	public long millisNow = 0;
	public long cookError = 0;
	public long foodBefore = 0;
	public long foodNow = 0;
	public long beforeXP;
	public long noXpMillis;
	public long beforeTime;
	public long bankLocation;
	public long tryAmount;

	public int status = 0;
	public String Status;
	public int xpGotten = 0;
	public int xpGained = 1;
	public int speed;
	public int getX;
	public int getY;

	public int rawID;

	public int bankID = 2271;
	public int fireID = 2732;

	public int weirdError = 0;
	public long wineMade = 0;

	public int language;
	public int food;
	public String fireString;

	RSTile fireTile;
	RSTile walkTile;
	RSTile walkBankTile;
	RSTile[] bankTile = {new RSTile(3268, 3169), new RSTile(3268, 3168)};
	public int donate;
	public int doUpdate;

	public boolean onStart(Map<String, String> args) {
		if(args.get("Update").equals("Yes")) {
			doUpdate = 1;
		}
	////////////////////
	//ASK FOR DONATIONS
	//Please don't edit this if you haven't donated
	//Donations are what keep the script alive!
	donate = 1;
	//1: Ask for donations
	//0: Do nothing
	////////////////////
	if(donate == 1) {
		java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
		if(java.awt.Desktop.isDesktopSupported() && desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
			JOptionPane.showMessageDialog(null, "Please donate to Peach to continue the updating of this amazing Cooking script! Any amount of money helps :) Opening donation link now..");
     			String link = "http://xrl.us/bgs2zm";
			try {
                		java.net.URI uri = new java.net.URI(link);
                		desktop.browse(uri);
			}
            		catch ( Exception e ) {
               			System.err.println( e.getMessage() );
            		}
		}
	}
	////////////////////
	//AUTO UPDATER - Thanks to RawR
	////////////////////
	URLConnection url = null; 
    	BufferedReader in = null; 
    	BufferedWriter out = null; 
	if(doUpdate == 1) {
        try{ 
            url = new URL("http://classmatch.webs.com/Scripts/allCookerVERSION.txt").openConnection(); 
            in = new BufferedReader(new InputStreamReader(url.getInputStream())); 
	    double version = Double.parseDouble(in.readLine());
            if(version > Revision) { 
                log("Update found.");
		JOptionPane.showMessageDialog(null, "Please choose 'allCooker.java' in your scripts folder and hit 'Open'"); 
		JFileChooser fc = new JFileChooser(); 
		if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){  
			url = new URL("http://classmatch.webs.com/Scripts/allCooker.java").openConnection(); 
			in = new BufferedReader(new InputStreamReader(url.getInputStream())); 
                        out = new BufferedWriter(new FileWriter(fc.getSelectedFile().getPath())); 
                        String inp; 
                        while((inp = in.readLine()) != null){ 
				out.write(inp); 
                                out.newLine(); 
                                out.flush(); 
                        }  
                        log("Script successfully downloaded. Please recompile and reload your scripts!"); 
                        return false; 
                } 
		else log("Update canceled"); 
		} 
		if(version == Revision) log("You have the latest version.");
		if(version < Revision) log("You have a newer version than the newest version..");
                if(in != null) 
                    in.close(); 
                if(out != null) 
                    out.close(); 
            } catch (IOException e){ 
                log("Problem getting version."); 
                return false;
            } 
        }  
		RSItem[] items = getInventoryItems();
		rawID = items[1].getID();
		return true;
	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(true);
		return;
	}

	@Override
	protected int getMouseSpeed() {
		if(speed == 0) speed = random(6,9);
		return speed;
	}

	public void onRepaint(Graphics g) {
		if (isLoggedIn()) {
			if(beforeXP == 0) 
				beforeXP = skills.getXPToNextLevel(STAT_COOKING);
			if(bankLocation == 0) {
				if(inRectangle(3039,4961,3064,4991))
					bankLocation = 1;
				if(inRectangle(3260,3156,3287,3191))
					bankLocation = 2;
			}
			if(bankLocation == 1) {
				fireString = "-> Fire";
				fireTile = new RSTile(3043,4973);
				walkTile = new RSTile(3043,4972);
				walkBankTile = new RSTile(3043,4972);
			}
			if(bankLocation == 2) {
				fireString = "-> Range";
				fireTile = new RSTile(3271,3181);
				walkTile = new RSTile(3273,3180);
				walkBankTile = new RSTile(3270,3168);
			}
         		long millis = System.currentTimeMillis() - startTime;
			millisNow = millis;
         		hours = millis / (1000 * 60 * 60);
         		millis -= hours * (1000 * 60 * 60);
         		minutes = millis / (1000 * 60);
         		millis -= minutes * (1000 * 60);
         		seconds = millis / 1000;
         		long minutes2 = minutes + (hours * 60);
			toNextLvl = skills.getXPToNextLevel(STAT_COOKING);
			if (startXP == 0) {
				startXP = skills.getCurrentSkillExp(STAT_COOKING);
			}
			if(skills.getCurrentSkillExp(STAT_COOKING) - startXP > xpGained) {
				beforeXP = skills.getXPToNextLevel(STAT_COOKING);
				beforeTime = System.currentTimeMillis();
				foodBefore = foodNow;
				noXpMillis = 0;
			}
			xpGained = skills.getCurrentSkillExp(STAT_COOKING) - startXP;
			if(xpGotten == 0 && xpGained > 1)
				xpGotten = xpGained;
			if(beforeTime == 0) beforeTime = System.currentTimeMillis();
			if(beforeXP < skills.getXPToNextLevel(STAT_COOKING)) {
				beforeTime = System.currentTimeMillis();
				foodBefore = foodNow;
			}
			beforeXP = skills.getXPToNextLevel(STAT_COOKING);
			noXpMillis = System.currentTimeMillis() - beforeTime;
			if(noXpMillis > random(15000,19000) && foodBefore >= foodNow) {
				cookError = 1;
				beforeTime = System.currentTimeMillis();
				foodBefore = foodNow;
			}
			if(noXpMillis > 240000) {
				log("No xp change in 4 minutes. Shutting down.");
				stopAllScripts();
			}
		}
		long timeInMillis = System.currentTimeMillis() - startTime;
		if(timeInMillis < 1) timeInMillis = 1;
		if(xpGained > 0) {
			xpHour = (xpGained * 3600000L) / timeInMillis;
			if(xpGotten > 0)
				wineMade = xpGained / xpGotten;
			if(xpGotten == 0)
				wineMade = 0;
			wineMade = (long)wineMade;
			wineHour = (wineMade * 3600000L) / timeInMillis;
			if(weirdError == 1) {
				xpHour = wineHour * xpGotten;
			}
			lvlSec = toNextLvl * 3600L / xpHour;
			lvlMin = 0;
			lvlHour = 0;
			lvlError = 0;
			if(lvlSec < 1) lvlError = 1;
			while(lvlSec >= 60) {
				lvlSec -= 60;
				lvlMin += 1;
			}
			while(lvlMin >= 60) {
				lvlMin -= 60;
				lvlHour += 1;
			}
			if(lvlHour > 150) {
				lvlSec = 0;
				lvlMin = 0;
				lvlHour = 0;
			}
		}
		
		if(status == 0) Status = "Idle";
		if(status == 6) Status = "Clicking fire";
		if(status == 7) Status = "Cooking all";
		if(status == 8) Status = "Opening bank";
		if(status == 9) Status = "Depositing all";
		if(status == 11) Status = "Closing bank";
		if(status == 12) Status = "Withdrawing";
		if(status == 13) Status = "Stopped cooking";
		if(status == 14) Status = "Not near Fire";
		if(status == 15) Status = "Waiting for Location";
		if(status == 16) Status = "Walking to Fire";
		if(status == 17) Status = "Walking to Bank";

		g.setColor(new Color(0, 0, 0, 175));
		g.fillRoundRect(9 ,33 ,206 ,120 ,4 ,4);
		g.fillRoundRect(369, 321, 127, 15, 4, 4);
		paintA("Peach's allCooker v1.7", g);
		paint("Time running: " + hours + ":" + minutes + ":" + seconds, g, 1);
		paint("Status:" + status + ":" + Status,g,2);
		if(bankLocation == 0)
			paint("Location: Unknown",g,3);
		if(bankLocation == 1)
			paint("Location: Rouge's Den",g,3);
		if(bankLocation == 2)
			paint("Location: Al Kharid",g,3);
		paint("Food Cooked: " + wineMade,g,4);
		paint("Food Cooked Per Hour: " + wineHour,g,5);
		paint("Experience Gained: " + xpGained,g,6);
		paint("Experience Per Hour: " + xpHour,g,7);
		if(lvlError != 1) paint("Time until levelup: " + lvlHour + ":" + lvlMin + ":" + lvlSec,g,8);
		if(lvlError == 1) paint("Time until levelup: ?",g,8);
		//paint("Debug",g,10);
		//paint("Time Since XP: " + noXpMillis,g,11);
		//paint("Before Food: " + foodBefore,g,12);
		//paint("Now Food: " + foodNow,g,13);
	}

	public void paint(String a, Graphics b, int c) {
		b.setColor(Color.black);
		b.drawString(a, 11, 31 + (c * 15));
		b.setColor(Color.green);
		b.drawString(a, 10, 30 + (c * 15));
	}
	public void paintA(String a, Graphics b) {
		b.setColor(Color.black);
		b.drawString(a, 371, 334);
		b.setColor(Color.green);
		b.drawString(a, 370, 333);
	}

	public void getStatus() {
		status = 14;
		if (bankLocation == 1 && inRectangle(3039,4961,3064,4991)) {
			findStatus();
		}
		if (bankLocation == 2 && inRectangle(3260,3156,3287,3191)) {
			findStatus();
		}
		if (bankLocation == 0)
			status = 15;
	}
	public void findStatus() {
		foodNow = getInventoryCount(rawID);
		status = 0;
		if (getInventoryCount(rawID) == 28) status = 6;
		if (cookError == 1) status = 13;
		if (status == 13 && bank.isOpen()) status = 11;
		if (status == 13 && getInventoryCount(rawID) == 0) status = 12;
		if (getInventoryCount() == 0) status = 12;
		if (status == 12 && !bank.isOpen()) status = 8;
		if (getInventoryCount(rawID) == 0 && getInventoryCount() == 28) status = 9;
		if (getInventoryCount() > 0 && getInventoryCount() < 28) status = 9;
		if (status == 9 && !bank.isOpen()) status = 8;
		if (RSInterface.getInterface(513).isValid()) status = 7;
		if (status == 6 && bank.isOpen()) status = 11;
		if (getInventoryCount(rawID) == 0) status = 12;
		if (status == 12 && !bank.isOpen()) status = 8;
		if (status == 8 && getInventoryCount(rawID) != 28 && getInventoryCount(rawID) != 0 && getInventoryCount() == 28) status = 0;
		if (status == 8 && getInventoryCount(rawID) > 0) status = 0;
		if (status == 6 && distanceTo(fireTile) > 7) status = 16;
		if (status == 13 && distanceTo(fireTile) > 7) status = 16;
		if (status == 8 && distanceTo(walkBankTile) > 7 && bankLocation == 2) status = 17;
		if (status == 12 && getInventoryCount() == 28) status = 9;
		if (getMyPlayer().isMoving()) status = 0;
		if (bank.isOpen() && getInventoryCount() == 28 && getInventoryCount(rawID) > 0 && getInventoryCount(rawID) < 28) status = 9;
	}

	public void position() {
		getX = getMyPlayer().getLocation().getX();
		getY = getMyPlayer().getLocation().getY();
	}

	public void doStatus() {
		if(status == 0) idle();
		if(status == 6) clickFire();
		if(status == 7) cookAll();
		if(status == 8) openBank();
		if(status == 9) depositAll();
		if(status == 11) closeBank();
		if(status == 12) withdraw();
		if(status == 13) clickFire();
		if(status == 16) walkToFire();
		if(status == 17) walkToBank();
			
	}
	
	public void idle() {
		if(clickInterface("continue",true)) {
			wait(random(50,60));
		}
		else {
			status = 0;
			wait(random(50,60));
		}
	}

	public void walkToFire() {
		status = 0;
		if(!getMyPlayer().isMoving()) {
			status = 16;
			walkTileMM(walkTile);
			status = 0;
		}
		wait(random(400,600));
	}

	public void walkToBank() {
		status = 0;
		if(!getMyPlayer().isMoving() && distanceTo(walkBankTile) > 7) {
			status = 17;
			walkTileMM(walkBankTile);
			status = 0;
		}
		wait(random(400,600));
	}

	public void clickFire() {
		beforeXP = skills.getXPToNextLevel(STAT_COOKING);
		beforeTime = System.currentTimeMillis();
		foodBefore = foodNow;
		noXpMillis = 0;
		RSObject Fire = findObject(fireID);
		if(!getMyPlayer().isMoving()) {
			if (!RSInterface.getInterface(513).isValid()){
			hoverItem(rawID);
			wait(random(5,12));
			if (!RSInterface.getInterface(513).isValid()){
			if (!clickInterface("->",true)) {
			if (!checkInterface("eat")) {
			clickMouse(true);
			if (!RSInterface.getInterface(513).isValid()){
			AonTile(fireTile, fireString);
			status = 0;
			if (!RSInterface.getInterface(513).isValid()){
			wait(random(1900,2000));
			} } } } } }
			if (RSInterface.getInterface(513).isValid())
				cookAll();
		}
	}


	public void cookAll() {
		foodBefore = foodNow;
		if (RSInterface.getInterface(513).isValid()){
			status = 7;
			if(clickInterface(220,385,300,460,"->",false)) {
				atMenu("Cancel");
				status = 0;
				wait(random(1700,2900));
			}
			if(clickInterface(220,385,300,460,"cook 1",false)) {
				atMenu("Cook All");
				status = 0;
				cookError = 0;
				millisBefore = millisNow;
				wait(random(2700,3900));
			}
                }
	}

	public void openBank() {
		status = 8;
		if(!bank.isOpen() && !getMyPlayer().isMoving()) {
			if (bankLocation == 1) {
				RSNPC abank = getNearestNPCByID(bankID);
				if (abank != null) {
					clickTheCharacter(abank, "Emerald Benedict", "Bank");
				}
			}
			if (bankLocation == 2) {
				int randomTile = random(0, bankTile.length);
				AonTile(bankTile[randomTile], "quickly");
			}
			status = 0;
			wait(random(1000,1400));
		}
	}

	public void depositAll() {
		status = 9;
		if(clickInterface(381,296,409,317,"deposit",true)) {
			status = 0;
			wait(random(200,300));
			status = 12;
			if(getInventoryCount(rawID) != 28) {
				bank.atItem(rawID, "Withdraw-All");
				status = 0;
				wait(random(1050,1200));
			}
		}
	}

	public void closeBank() {
		tryAmount = 0;
		status = 11;
		clickInterface(481,30,492,38,"close",true);
		status = 0;
		wait(random(500,800));
	}

	public void withdraw() {
		if(getInventoryCount(rawID) == 0) {
			wait(random(200,300));
			status = 12;
			bank.atItem(rawID, "Withdraw-All");
			status = 0;
			tryAmount += 1;
			wait(random(1050,1200));
		}
		if(tryAmount > 10) {
			log("Failed tryAmounting to get food. Logging out.");
			stopAllScripts();
		}
	}


	public int loop() {
		if(inRectangle(3039,4961,3064,4991) || inRectangle(3260,3156,3287,3191)) {
			getStatus();
			doStatus();
		}
		return random(10, 100);
	}



	public boolean clickInterface(int x1, int y1, int x2, int y2, String action, boolean click) {
		int previousSpeed = speed;
		speed = random(6,9);
		moveMouse(random(x1,x2),random(y1,y2));
		wait(random(100,200));
		speed = previousSpeed;
            	if (getMenuItems().size() <= 1) {
                	return false;
            	}
		if (!getMenuItems().get(0).toLowerCase().contains(action)) {
			return false;
		}
		clickMouse(click);
		wait(random(300,500));
		return true;
	}

	public boolean clickInterface(String action, boolean click) {
            	if (getMenuItems().size() <= 1) {
                	return false;
            	}
		if (!getMenuItems().get(0).toLowerCase().contains(action)) {
			return false;
		}
		clickMouse(click);
		wait(random(300,500));
		return true;
	}

	public boolean checkInterface(String action) {
            	if (getMenuItems().size() <= 1) {
                	return false;
            	}
		if (!getMenuItems().get(0).toLowerCase().contains(action)) {
			return false;
		}
		return true;
	}

	public boolean inRectangle(int x1, int y1, int x2, int y2) {
		int getX = getMyPlayer().getLocation().getX();
		int getY = getMyPlayer().getLocation().getY();
		if(getX >= x1 && getX <= x2 && getY >= y1 && getY <= y2)
			return true;
		return false;
	}

	private boolean hoverItem(final int itemID) {
		try {
			if (getCurrentTab() != Constants.TAB_INVENTORY
					&& !RSInterface.getInterface(Constants.INTERFACE_BANK)
							.isValid()
					&& !RSInterface.getInterface(Constants.INTERFACE_STORE)
							.isValid()) {
				openTab(Constants.TAB_INVENTORY);
			}

			final RSInterfaceChild inventory = getInventoryInterface();
			if (inventory == null || inventory.getComponents() == null) {
				return false;
			}

			final java.util.List<RSInterfaceComponent> possible = new ArrayList<RSInterfaceComponent>();
			for (final RSInterfaceComponent item : inventory.getComponents()) {
				if (item != null && item.getComponentID() == itemID) {
					possible.add(item);
				}
			}

			if (possible.size() == 0) {
				return false;
			}

			final RSInterfaceComponent item = possible.get(0);
			return hoverInterface(item);
		} catch (final Exception e) {
			log("atInventoryItem(final int itemID, final String option) Error: "
					+ e);
			return false;
		}
	}
	private boolean hoverInterface(final RSInterfaceChild i) {
		if (!i.isValid()) {
			return false;
		}
		final Rectangle pos = i.getArea();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return false;
		}

		// zzSleepzz - Base the randomization on the center of the area rather
		// then
		// the upper left edge. This provides more room for lag-induced
		// mousing errors.
		final int dx = (int) (pos.getWidth() - 4) / 2;
		final int dy = (int) (pos.getHeight() - 4) / 2;
		final int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		final int midy = (int) (pos.getMinY() + pos.getHeight() / 2);

		moveMouse(midx + random(-dx, dx), midy + random(-dy, dy));
		wait(random(50, 60));
		return true;
	}
                public boolean clickTheCharacter(RSCharacter npc, String npcName, String action) {
                        if (npc == null) {
                                        return false;
                                }

                        RSTile tile = npc.getLocation();

                        if (!tile.isValid()) {
                                        return false;
                                }

                        if (distanceTo(tile) > 6) {
                                        walkTileMM(tile);
                                        wait(random(340, 1310));
                                }

                        try {
                                        Point screenLoc = null;

                                        for (int i = 0; i < 30; i++) {
                                                        screenLoc = npc.getScreenLocation();

                                                        if (!npc.isValid() || !pointOnScreen(screenLoc)) {
                                                                        //not on screen
                                                                        return false;
                                                                }

                                                        if (getMenuItems().get(0).toLowerCase().contains(npcName)) {
                                                                        break;
                                                                }

                                                        if (getMouseLocation().equals(screenLoc)) {
                                                                        break;
                                                                }

                                                        moveMouse(screenLoc);
                                                }

                                        screenLoc = npc.getScreenLocation();

                                        if (getMenuItems().size() <= 1) {
                                                        return false;
                                                }

                                        if (getMenuItems().get(0).toLowerCase().contains(action)) {
                                                        clickMouse(true);
							wait(random(200,300));
                                                        return true;

                                                } else {
                                                        clickMouse(false);
                                                        return atMenu(action);
                                                }

                                } catch (Exception e) {
                                        e.printStackTrace();
                                        return false;
                                }
                }
	public Point getItemPoint(int slot) {
		if (slot < 0)
			throw new IllegalArgumentException("slot < 0 " + slot);
		
		RSInterfaceComponent item = bank.getItem(slot);
		if(item != null)
			return item.getPosition();
		
		return new Point(-1, -1);
	}
    public boolean AonTile(RSTile tile, String action) {
        if (!tile.isValid()) {
            return false;
        }
        if (distanceTo(tile) > 5) {
            walkTileMM(tile);
            wait(random(340, 1310));
        }

        try {
            Point screenLoc = null;
            for (int i = 0; i < 30; i++) {
                screenLoc = Calculations.tileToScreen(tile);
                if (!pointOnScreen(screenLoc)) {
                    return false;
                }               
                if(getMenuItems().get(0).toLowerCase().contains(action)) {
                        break;                 
                }                  
                if (getMouseLocation().equals(screenLoc)) {
                    break;
                }
                moveMouse(screenLoc);
            }
            screenLoc = Calculations.tileToScreen(tile);
            if (getMenuItems().size() <= 1) {
                return false;
            }
            if (getMenuItems().get(0).toLowerCase().contains(action)) {
                clickMouse(true);
                return true;
            } else {
                clickMouse(false);
                return atMenu(action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}