
import java.awt.Graphics;
import java.util.Map;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"LightSpeed, Pirateblanc"}, category = "Combat", name = "SpeedSoulWars", version = 1.0, description = "")
public class SpeedSoulWars extends Script implements ServerMessageListener, PaintListener {

    //initialization variables
    protected boolean recordInitial = false;
    protected long startTime;
    protected RSTile loc;
    protected int errorCounter = 0;
    protected final String version = "1.0";
    //game status variables
    protected boolean inGame = false;
    protected int team = 0;
    protected final int BLUE = 1;
    protected final int RED = 2;
    protected int gamesWon = 0;
    protected int gamesLost = 0;
    protected int gamesTied = 0;
    protected int gamesKicked = 0;
    protected int lastWon = 0;
    protected boolean dead = false;
    protected int obeliskTeam = 0;
    protected int eastTeam = 0;
    protected int westTeam = 0;
    protected int activity = 100;
    //Monster and Item IDs
    protected int pyrefiendID = 8598;
    protected int jellyID = 8599;
    protected int fragmentID = 14639;
    protected int bonesID = 14638;
    //Bot Conditions
    protected int status = 0;
    protected boolean pickupFragments = false;
    protected boolean specialAttack = false;
    protected int specialAttackPer = 101;
    protected boolean pickBones = false;
    protected boolean usePrayer = false;
    protected boolean useCC = false;
    //Locations
    protected final Area blueLobby = new Area(1870, 3158, 9, 8);
    protected final Area blueGameLobby = new Area(0, 0, 0, 0);
    protected final Area redLobby = new Area(0, 0, 0, 0);
    protected final Area redGameLobby = new Area(0, 0, 0, 0);
    protected final Area arenaBlueHalf = new Area(0, 0, 0, 0);
    protected final Area arenaRedHalf = new Area(0, 0, 0, 0);
    protected final Area eastGrave = new Area(0, 0, 0, 0);
    protected final Area westGrave = new Area(0, 0, 0, 0);
    protected final Area lobby = new Area(0, 0, 0, 0);
    protected final Area obelisk = new Area(0, 0, 0, 0);
    protected final RSTile blueCenter = new RSTile(0, 0);
    protected final RSTile redCenter = new RSTile(0, 0);
    protected final RSTile blueLobbyExit = new RSTile(1880, 3162);
    protected final RSTile redLobbyExit = new RSTile(0, 0);

    public int locate() {
        loc = getLocation();
        return -1;
    }

    public boolean initialize() {
        return true;
    }

    @Override
    public int loop() {
        if (isLoggedIn() && recordInitial) {
            if (!initialize()) {
                return -1;
            }
            recordInitial = false;
        }
        if (!isLoggedIn()) {
            return 1000;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean onStart(Map<String, String> map) {
        return super.onStart(map);
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        String message = e.getMessage();
        if (message.contains("You receive 1 Zeal")) {
            inGame = false;
            gamesLost++;
            if (team == BLUE) {
                lastWon = RED;
            } else if (team == RED) {
                lastWon = BLUE;
            }
        }
        if (message.contains("You receive 2 Zeal")) {
            inGame = false;
            gamesTied++;
            lastWon = 0;
        }
        if (message.contains("You receive 3 Zeal")) {
            inGame = false;
            gamesWon++;
            if (team == BLUE) {
                lastWon = BLUE;
            } else if (team == RED) {
                lastWon = RED;
            }
        }
        if (message.contains(("You receive 0 Zeal"))) {
            inGame = false;
            gamesKicked++;
            lastWon = 0;
        }
        if (message.contains("getting low")) {
            pickBones = true;
        }
        if (message.contains("Oh dear")) {
            pickBones = false;
            dead = true;
        }
        if (message.contains("lost control of the soul")) {
            obeliskTeam = 0;
        }
        if (message.contains("blue</col> team has taken control of the soul")) {
            obeliskTeam = BLUE;
        }
        if (message.contains("red</col> team has taken control of the soul")) {
            obeliskTeam = RED;
        }
        if (message.contains("blue</col> team has taken control of the eastern")) {
            eastTeam = BLUE;
        }
        if (message.contains("red</col> team has taken control of the eastern")) {
            eastTeam = RED;
        }
        if (message.contains("blue</col> team has taken control of the west")) {
            westTeam = BLUE;
        }
        if (message.contains("red</col> team has taken control of the west")) {
            westTeam = RED;
        }
        if (message.contains("team has lost control of the eastern")) {
            eastTeam = 0;
        }
        if (message.contains("team has lost control of the west")) {
            westTeam = 0;
        }
    }

    public void onRepaint(Graphics render) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected class Area {

        int x, y, w, h;

        public Area(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public boolean contains(int X, int Y) {
            if (X >= x && X <= x + w) {
                if (Y >= y && Y <= y + h) {
                    return true;
                }
            }
            return false;
        }

        public boolean contains(RSTile r) {
            return contains(r.getX(), r.getY());
        }
    }

    protected class ChatListener implements Runnable {

        public void run() {
            while (useCC) {
            }
        }
    }
}
