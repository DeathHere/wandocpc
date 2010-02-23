
import java.awt.Graphics;
import java.util.Map;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
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
    protected int depositCount = 20;
    protected boolean specialAttack = false;
    protected int specialAttackPer = 101;
    protected boolean pickBones = false;
    protected boolean usePrayer = false;
    protected boolean useCC = false;
    protected boolean attackPlayers = false;
    protected boolean attackSlayer = false;
    //Locations
    protected final Area blueLobby = new Area(1870, 3158, 9, 8);
    protected final Area blueGameLobby = new Area(1816, 3220, 1823, 3230, true);
    protected final Area redLobby = new Area(1900, 3157, 1909, 3165, true);
    protected final Area redGameLobby = new Area(1951, 3234, 1958, 3244, true);
    protected final Area arenaBlueHalf = new Area(0, 0, 0, 0);
    protected final Area arenaRedHalf = new Area(0, 0, 0, 0);
    protected final Area eastGrave = new Area(1932, 3244, 1935, 3246, true);
    protected final Area westGrave = new Area(1841, 3217, 1843, 3219, true);
    protected final Area lobby = new Area(1850, 3130, 1920, 3190,true);
    protected final Area obelisk = new Area(1884, 3229, 5, 5);
    protected final Area obeliskArea = new Area(0, 0, 0, 0);
    protected final RSTile blueCenter = new RSTile(0, 0);
    protected final RSTile redCenter = new RSTile(1931, 3230);
    protected final RSTile blueLobbyExit = new RSTile(1880, 3162);
    protected final RSTile redLobbyExit = new RSTile(1960, 3239);
    public RSTile[] RedStartToRedFiends = {new RSTile(1960, 3239), new RSTile(1962, 3239), new RSTile(1965, 3238), new RSTile(1966, 3235), new RSTile(1966, 3232), new RSTile(1966, 3230), new RSTile(1966, 3227), new RSTile(1966, 3224), new RSTile(1966, 3223), new RSTile(1966, 3221), new RSTile(1966, 3219), new RSTile(1966, 3218), new RSTile(1964, 3216), new RSTile(1963, 3215), new RSTile(1962, 3214), new RSTile(1960, 3214), new RSTile(1958, 3214), new RSTile(1957, 3214), new RSTile(1956, 3214), new RSTile(1955, 3214), new RSTile(1954, 3214), new RSTile(1952, 3214), new RSTile(1951, 3214), new RSTile(1950, 3215), new RSTile(1948, 3217), new RSTile(1947, 3218), new RSTile(1945, 3218), new RSTile(1944, 3218), new RSTile(1942, 3217), new RSTile(1941, 3217), new RSTile(1939, 3217), new RSTile(1938, 3217), new RSTile(1935, 3219), new RSTile(1935, 3218), new RSTile(1934, 3217), new RSTile(1932, 3216), new RSTile(1931, 3216), new RSTile(1930, 3215), new RSTile(1928, 3213), new RSTile(1927, 3212)};

    public int locate() {
        loc = getLocation();
        status = checkNearTask(loc);
        if (status != -1) {
            return status;
        }
        if (blueLobby.contains(loc)) {
            status = 1;
            inGame = false;
            obeliskTeam = 0;
            westTeam = 0;
            eastTeam = 0;
        } else if (redLobby.contains(loc)) {
            status = 2;
            inGame = false;
            obeliskTeam = 0;
            westTeam = 0;
            eastTeam = 0;
        } else if (lobby.contains(loc)) {
            status = 0;
            inGame = false;
            obeliskTeam = 0;
            westTeam = 0;
            eastTeam = 0;
            joinTeam();
        } else if (blueGameLobby.contains(loc)) {
            status = 3;
            inGame = true;
            team = BLUE;
            dead = false;
            pickBones = false;
        } else if (redGameLobby.contains(loc)) {
            status = 4;
            inGame = true;
            team = RED;
            pickBones = false;
            dead = false;
        } else if (westGrave.contains(loc)) {
            status = 5;
            inGame = true;
            if (westTeam != 0) {
                team = westTeam;
            }
            pickBones = false;
            dead = false;
            if (leaveGrave()) {
                moveToBlueCenter();
            }
        } else if (eastGrave.contains(loc)) {
            status = 6;
            inGame = true;
            if (eastTeam != 0) {
                team = eastTeam;
            }
            pickBones = false;
            dead = false;
            if (leaveGrave()) {
                moveToRedCenter();
            }
        } else if (distanceBetween(loc, blueCenter) < 3) {
            status = doTaskBlueSide();
        } else if (distanceBetween(loc, redCenter) < 3) {
            status = doTaskRedSide();
        } else if (arenaBlueHalf.contains(loc)) {
            if (moveToBlueCenter()) {
                status = locate();
            }
        } else if (arenaRedHalf.contains(loc)) {
            if (moveToRedCenter()) {
                status = locate();
            }
        }
        return status;
    }

    public boolean joinTeam() {
        return false;
    }

    public int checkNearTask(RSTile loc) {
        return -1;
    }

    public int doTaskRedSide() {
        return -1;
    }

    public int doTaskBlueSide() {
        return -1;
    }

    public boolean leaveGrave() {
        return false;
    }

    public boolean moveToRedCenter() {
        return false;
    }

    public boolean moveToBlueCenter() {
        return false;
    }

    public boolean initialize() {
        //use GUI here
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
        locate();
        if (status == 1 || status == 2) {
            return 2000;
        }
        return 500;
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
        if (message.contains("power left.")) {
            specialAttackPer += 5;
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

        public Area(int x, int y, int a, int b, boolean c) {
            this.x = x;
            this.y = y;
            this.w = a - x;
            this.h = b - y;
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

        public RSTile randomPoint() {
            return new RSTile(x + random(0, w), y + random(0, h));
        }

        public RSTile center() {
            return new RSTile(x + w / 2, y + h / 2);
        }
    }

    protected class ChatListener implements Runnable {

        public void run() {
            while (useCC) {
            }
        }
    }

    public String status(int c) {
        switch (c) {
            case 0:
                return "In Soul Lobby";
            case 1:
                return "In Blue Waiting";
            case 2:
                return "In Red Waiting";
            case 3:
                return "In Blue Start";
            case 4:
                return "In Red Start";
            case 5:
                return "In Western Grave";
            case 6:
                return "In Eastern Grave";
            case 7:
                return "At Blue Center";
            case 8:
                return "At Red Center";
            case 9:
                return "At Obelisk";
            case 10:
                return "Near Obelisk";
            case 11:
                return "Moving to Blue Center";
            case 12:
                return "Moving to Red Center";
            default:
                return "Unknown";
        }
    }

    public int getActivityBarPercent() {
        RSInterfaceChild c = RSInterface.getInterface(836).getChild(56);
        if (c != null && c.isValid() && c.getAbsoluteX() > -1) {
            return ((c.getArea().height * 100) / 141) - 2;
        }
        return -1;
    }
}
