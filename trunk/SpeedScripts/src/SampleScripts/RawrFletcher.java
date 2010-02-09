package SampleScripts;

import java.awt.*;
import java.util.*;

import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.event.events.ServerMessageEvent;

@ScriptManifest(authors = { "RawR" }, category = "Fletching", name = "RawR Fletcher", version = 1.24,
description =
	"<html><body><center><u><h2>RawR Fletcher</h2></u></center>" +
	"The script has the options to do Fletching or Stringing. <br />" +
	"<font size='3'>Please choose your settings below.</font> <br /> <br />" +
	"<b>Method :</b> " +
		"<select name='method'><option>Fletching</option><option>Stringing</option></select>" +
		" <b>Amount:</b> " +
			"<input name='amount' type='text' size='10' maxlength='10' value='1000' /><br /><br />" +
	"<b>Log / Bow :</b> " +
		"<select name='log' style='margin-top: 2px;'><option>Normal</option><option>Oak</option><option>Willow</option><option>Maple</option><option>Yew</option><option>Magic</option></select>" +
		"<select name='bow' style='margin-top: 2px;'><option>Shortbow</option><option>Longbow</option></select> <br /><br />" +
	"<b>Knife :</b> " +
		"<select name='knife' style='margin-top: 2px;'><option>Normal</option><option>Clay</option></select>" +
	"</body></html>")

public class RawrFletcher extends Script implements PaintListener, ServerMessageListener {

	public String Log;
	public String Bow;
	public String Method;
	public String logName = "None";
	public int amountStrung = 0;
	public int amountFletch = 0;
	public int AMOUNT = 0;

	public int KNIFE;
	public int LOG_ID;
	public int SHORT_ID;
	public int LONG_ID;
	public int SHORTBOW_ID;
	public int LONGBOW_ID;
	public int BOW_STRING = 1777;

	public int BANKBOOTH[] = { 11758, 11402, 34752, 35647, 2213,
			25808, 2213, 26972, 27663, 4483, 14367, 19230, 29085, 12759, 6084 };
	public int CHEST[] = { 27663, 4483, 12308, 21301, 42192 };
	public int BANKER[] = { 7605, 6532, 6533, 6534, 6535, 5913,
			5912, 2271, 14367, 3824, 44, 45, 2354, 2355, 499, 5488, 8948, 958,
			494, 495, 6362, 5901 };

	RSInterface INTERFACE_FLETCH = RSInterface.getInterface(513);
	RSInterfaceChild FLETCH_AREA = RSInterface.getChildInterface(513, 3);

	public RSObject bankBooth = getNearestObjectByID(BANKBOOTH);
	public RSObject bankChest = getNearestObjectByID(CHEST);
    public RSNPC banker = getNearestNPCByID(BANKER);

	//Paint INTs
	private long waitTimer;
	public long startTime = System.currentTimeMillis();
	public int startexp;
	public int startlvl;

	@Override
	public int getMouseSpeed(){
		return random(6, 7);
	}

    public void fletchBows() {
    	if (!Log.equals("Magic") && !Log.equals("Normal")) {
            if (Bow.contains("Short")) {
                moveMouse(random(52, 147), random(390, 450));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            } else {
                moveMouse(random(215, 309), random(390, 450));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            }
        } else if (Log.equals("Magic")) {
            if (Bow.contains("Short")) {
                moveMouse(random(95, 172), random(376, 462));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            } else {
                moveMouse(random(341, 432), random(391, 464));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            }
        } else if (Log.equals("Normal")) {
            if (Bow.contains("Short")) {
                moveMouse(random(170, 225), random(391, 454));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            } else {
                moveMouse(random(287, 364), random(391, 454));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            }
        }
    }

    public void openBank() {
		if(bankBooth != null && !bank.isOpen()) {
	    	atObject(bankBooth, "quickly");
	    	wait(random(800, 1000));
	    } else if (bankChest != null && !bank.isOpen()) {
	    	atObject(bankChest, "Use");
			wait(random(800, 1000));
		} else {
	    	atNPC(banker, "Bank Banker");
	    	wait(random(800, 1000));
	    }
	}

    public int getKnife() {
    	openBank();
    	if (bank.isOpen()) {
    	   	bank.depositAll();
    	   	wait(random(400, 600));
    	   	bank.withdraw(KNIFE, 1);
    	   }
    	return random(100, 200);
    }

	public void doBankFletching() {
		openBank();
	    if (bank.isOpen()) {
	    	bank.depositAllExcept(KNIFE);
	    	wait(random(800, 1000));
	    	bank.withdraw(LOG_ID, 27);
	    	wait(random(800, 1000));
	    }
    }

    public void doBankStringing() {
    	openBank();
    	if (Bow.equals("Shortbow")) {
	        if (bank.isOpen()) {
	        	bank.depositAll();
	        	wait(random(800, 1100));
	        }
	        if (bank.isOpen() && getInventoryCount(SHORT_ID) == 0) {
    	    	bank.withdraw(SHORT_ID, 14);
    	    	wait(random(800, 1000));
			}
			if (bank.isOpen() && getInventoryCount(BOW_STRING) == 0) {
    	    	bank.withdraw(BOW_STRING, 14);
    	    	wait(random(800, 1000));
			}
    	} else {
	        if (bank.isOpen()) {
	        	bank.depositAll();
	        	wait(random(800, 1000));
	        }
	        if (bank.isOpen() && getInventoryCount(LONG_ID) == 0) {
    	    	bank.withdraw(LONG_ID, 14);
    	    	wait(random(800, 1000));
			}
			if (bank.isOpen() && getInventoryCount(BOW_STRING) == 0) {
				bank.withdraw(BOW_STRING, 14);
    	    	wait(random(800, 1000));
			}
    	}
    }

    public int loop() {
    	if(amountFletch == AMOUNT || amountStrung == AMOUNT) {
    		stopScript();
    	}
    	////////////////
    	// Fletching  //
    	////////////////
    	if (Method.equals("Fletching")) {
    		if (inventoryContainsOneOf(KNIFE) && inventoryContains(LOG_ID) && (System.currentTimeMillis() - waitTimer) > 2000 && !INTERFACE_FLETCH.isValid() && !bank.isOpen()) {
    			atInventoryItem(KNIFE, "Use");
    			wait(random(500, 600));
    			if (isItemSelected()) {
	    			atInventoryItem(LOG_ID, "Use");
	    			wait(random(1000, 1300));
    			} else {
    				log("Doing Failsafe.");
        			moveMouse(random(650, 660), random(180, 190));
        			clickMouse(true);
    			}
    		}
    		if (INTERFACE_FLETCH.isValid()) {
    			fletchBows();
    		}
    		if (!inventoryContains(LOG_ID)) {
    			doBankFletching();
    		}
    		if (inventoryContains(LOG_ID) && bank.isOpen()) {
    	    	bank.close();
    	    }
    		if (!inventoryContains(KNIFE)) {
    			log("You don't have a knife, getting one.");
    			getKnife();
    		}
    		if (isItemSelected() && (System.currentTimeMillis() - waitTimer) > random(3500, 4000)) {
    			log("Doing Failsafe.");
    			moveMouse(random(650, 660), random(180, 190));
    			clickMouse(true);
    		}
			if (getMyPlayer().getAnimation() != -1) {
				waitTimer = System.currentTimeMillis();
				antiBan();
			}
    	}
    	///////////////
    	// Stringing //
    	///////////////
    	if (Method.equals("Stringing")) {
    		if (Bow.equals("Shortbow")) {
    			if (inventoryContains(SHORT_ID) && inventoryContains(BOW_STRING) && (System.currentTimeMillis() - waitTimer) > 2000 && !FLETCH_AREA.isValid() && !bank.isOpen()) {
    				atInventoryItem(SHORT_ID, "Use");
    				wait(random(500, 600));
    				if (isItemSelected()) {
    					atInventoryItem(BOW_STRING, "Use");
    					wait(random(1000, 1200));
    				} else {
    					log("Doing Failsafe.");
            			moveMouse(random(650, 660), random(180, 190));
            			clickMouse(true);
    				}
    			}
    			if (FLETCH_AREA.isValid()) {
    				atInterface(FLETCH_AREA, "Make All");
					wait(random(3000, 3500));
    			}
    			if (!inventoryContains(SHORT_ID) || !inventoryContains(BOW_STRING)) {
    				doBankStringing();
    			}

    			if (inventoryContains(SHORT_ID) && inventoryContains(BOW_STRING) && bank.isOpen()) {
	    	    	bank.close();
	    	    }

    			if (isItemSelected() && (System.currentTimeMillis() - waitTimer) > random(3500, 4000)) {
        			log("Doing Failsafe.");
        			moveMouse(random(650, 660), random(180, 190));
        			clickMouse(true);
        		}

				if (getMyPlayer().getAnimation() != -1) {
					waitTimer = System.currentTimeMillis();
					antiBan();
				}
    		} else {
    			if (inventoryContains(LONG_ID) && inventoryContains(BOW_STRING) && (System.currentTimeMillis() - waitTimer) > 2000 && !FLETCH_AREA.isValid() && !bank.isOpen()) {
    				atInventoryItem(LONG_ID, "Use");
    				wait(random(500, 600));
    				if (isItemSelected()) {
    					atInventoryItem(BOW_STRING, "Use");
    					wait(random(800, 1000));
    				} else {
    					log("Doing Failsafe.");
            			moveMouse(random(650, 660), random(180, 190));
            			clickMouse(true);
    				}
    			}
    			if (FLETCH_AREA.isValid()) {
    				atInterface(FLETCH_AREA, "Make All");
    				wait(random(3000, 3500));
    			}
    			if (!inventoryContains(LONG_ID) || !inventoryContains(BOW_STRING)) {
    				doBankStringing();
    			}
    			if (inventoryContains(LONG_ID) && inventoryContains(BOW_STRING) && bank.isOpen()) {
	    	    	bank.close();
	    	    }
    			if (isItemSelected() && (System.currentTimeMillis() - waitTimer) > random(3500, 4000)) {
    				log("Doing Failsafe.");
        			moveMouse(random(650, 660), random(180, 190));
        			clickMouse(true);
        		}
				if(getMyPlayer().getAnimation() != -1) {
					waitTimer = System.currentTimeMillis();
					antiBan();
				}
    		}
    	}
        return random(100, 200);
    }

    public boolean onStart(Map<String, String> args) {
    	startTime = System.currentTimeMillis();
    	waitTimer = System.currentTimeMillis();
    	Log = args.get("log");
    	Bow = args.get("bow");
    	Method = args.get("method");
    	AMOUNT = Integer.parseInt(args.get("amount"));
    	///////////////////
    	if (args.get("knife").equals("Normal")){
    		KNIFE = 946;
    	} else if (args.get("knife").equals("Clay")){
    		KNIFE = 14111;
    	}
    	///////////////////
    	if (args.get("log").equals("Normal")) {
    		LOG_ID = 1511;
    		SHORT_ID = 50;
    		SHORTBOW_ID = 841;
    		LONG_ID = 48;
    		LONGBOW_ID = 839;
    		logName = "Normal";
    	} else if (args.get("log").equals("Oak")) {
    		LOG_ID = 1521;
    		SHORT_ID = 54;
    		SHORTBOW_ID = 843;
    		LONG_ID = 56;
    		LONGBOW_ID = 845;
    		logName = "Oak";
    	} else if (args.get("log").equals("Willow")) {
    		LOG_ID = 1519;
    		SHORT_ID = 60;
    		SHORTBOW_ID = 849;
    		LONG_ID = 58;
    		LONGBOW_ID = 847;
    		logName = "Willow";
    	} else if (args.get("log").equals("Maple")) {
    		LOG_ID = 1517;
    		SHORT_ID = 64;
    		SHORTBOW_ID = 853;
    		LONG_ID = 62;
    		LONGBOW_ID = 851;
    		logName = "Maple";
    	} else if (args.get("log").equals("Yew")) {
    		LOG_ID = 1515;
    		SHORT_ID = 68;
    		SHORTBOW_ID = 857;
    		LONG_ID = 66;
    		LONGBOW_ID = 855;
    		logName = "Yew";
    	} else {
    		LOG_ID = 1513;
    		SHORT_ID = 72;
    		SHORTBOW_ID = 861;
    		LONG_ID = 70;
    		LONGBOW_ID = 859;
    		logName = "Magic";
    	}
    	/////////////////
		setCameraAltitude(true);
        return true;
    }

    public void onFinish() {
        log("Thanks for using RawR Fletcher!");
		if (Method.equals("Fletching")) {
		log("Logs Fletched: " + amountFletch);
		} else {
		log("Bows Strung: " + amountStrung);
		}
    }

    public int antiBan() {
	final int r = random(1, 10);
	final int ranNum = random(1, 20);
		if (r == 6) {
			if (ranNum == 1) {
				setCameraRotation(random(100, 360));
				return random(200, 400);
			} else if (ranNum == 2) {
				moveMouse(random(0, 700), random(0, 500));
				return random(200, 400);
			} else if (ranNum == 3) {
				moveMouse(random(0, 450), random(0, 400));
				return random(200, 400);
			}
		}
		return random(200, 450);
	}

    public void onRepaint(Graphics g) {
		//////////////
		//TIMER INTs
		//////////////
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		//////////////
		//COLOR INTs
		//////////////
		// Color BG = new Color(0, 139, 0, 75);
		Color RED = new Color(255, 0, 0, 255);
		Color GREEN = new Color(0, 255, 0, 255);
		Color BLACK = new Color(0, 0, 0, 255);
		if(isLoggedIn()){
		//////////////
		//EXP INTs
		//////////////
		int expGained = 0;
		if (startexp == 0){
			startexp = skills.getCurrentSkillExp(STAT_FLETCHING);
		}
		expGained = skills.getCurrentSkillExp(STAT_FLETCHING) - startexp;
		int lvlsGained = 0;
		if (startlvl == 0){
			startlvl = skills.getCurrentSkillLevel(STAT_FLETCHING);
		}
		lvlsGained = skills.getCurrentSkillLevel(STAT_FLETCHING) - startlvl;
		int xpToLvl = skills.getXPToNextLevel(STAT_FLETCHING);
		///////////////////////////
		int XPHour = expGained > 0 ? (int) ((expGained * 60 * 60) / ((System.currentTimeMillis() - startTime) / 1000)) : 0;
		////////////////////////////
			//background
			g.setColor(new Color(72, 61, 139, 125));
			g.fillRoundRect(5, 137, 165, 200, 15, 15);
			g.setColor(new Color(47, 47, 89, 200));
			g.fillRoundRect(15, 147, 145, 180, 15, 15);
			//% bar
			g.setColor(RED);
			g.fill3DRect(20, 230, 100, 11, true);
			g.setColor(GREEN);
			g.fill3DRect(20, 230, skills.getPercentToNextLevel(Constants.STAT_FLETCHING), 11, true);
			g.setColor(BLACK);
			g.drawString(skills.getPercentToNextLevel(Constants.STAT_FLETCHING) + "%  to " + (skills.getCurrentSkillLevel(Constants.STAT_FLETCHING) + 1), 41, 240);
			//others
			g.setColor(Color.WHITE);
			g.setFont(new Font("Palatino Linotype", Font.BOLD, 16));
			g.drawString("RawR Fletcher", 40, 159);
			g.setFont(new Font("Palatino Linotype", Font.ITALIC, 11));
			g.drawString("by RawR" , 79, 171);
			g.setFont(new Font("Trajan Pro", Font.PLAIN, 13));
			g.drawString("XP to level: " + xpToLvl, 20, 294);
			g.drawString("XP / HR: " + XPHour, 20, 276);
			g.drawString("Time running: " + hours + ":" + minutes + ":" + seconds, 20, 186); //240
			g.drawString("Log Type: " + logName, 20, 204);
			if (Method.equals("Fletching")) {
				g.drawString(logName + "'s " + "Fletched: "+ amountFletch, 20, 222);
			} else {
				g.drawString(logName + "'s " + "Strung: "+ amountStrung, 20, 222);
			}
			g.drawString("XP Gained: "+ expGained, 20, 257);
			g.drawString("Levels Gained: " + lvlsGained, 20, 312);
        }
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        String word = e.getMessage().toLowerCase();
        if (word.contains("shortbow") || word.contains("longbow")) {
            amountFletch++;
        }
        if (word.contains("string")) {
        	amountStrung++;
        }
    }
}