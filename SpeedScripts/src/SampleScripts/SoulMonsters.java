package SampleScripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.event.listeners.*;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.script.ScriptManifest;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = "Peach", category = "Combat", name = "SoulMonsters", version = 2.1, description = "<html><center>"
+ "<h2>SoulMonsters</h2>"
+ "<b>Author:</b>Peach "
+ "<b>Version:</b>2.1 "
+ "<b>Revision:</b>015<br>"
+ "<b>Check for update?</b> "
+ "<select name=\'Update\'><option selected>Yes<option>No</select><br>"
+ "<i>Script will open up a donation link upon starting.</i><br>"
+ "<b>Instructions: </b>Start the script in either lobby or outside lobby. Have auto-retaliate on. "
+ "Hover in-game for options.<br>"
+ "Kill: "
+ "<select name=\'Monster\'><option selected>Pyrefiends<option>Jellies<option>People</select>"
+ "<br>Join Team: "
+ "<select name=\'JoinTeam\'><option>Always Blue<option>Always Red<option>Switch<option>Random<option>Last Won<option selected>Last Lost</select>"
+ "<br>Turn in Souls at: "
+ "<select name=\'SoulTurnIn\'><option selected>Random<option>10 Souls<option>20 Souls<option>30 Souls<option>Never</select>"
+ "<br>Turn Quick Prayer on at: "
+ "<select name=\'QuickPray\'><option selected>Pyrefiends<option>Jellies<option>Starting area<option>Never</select>"
+ "<br>Turn Special Attack on at: "
+ "<select name=\'SpecialAttack\'><option>Pyrefiends<option>Jellies<option selected>Starting area<option>Never</select>"
+ "<br>Pick up bones if low activity? "
+ "<select name=\'BonePickup\'><option selected>Yes<option>No</select>"
+ "</center></html>")
public class SoulMonsters extends Script implements PaintListener, ServerMessageListener {

    final ScriptManifest properties = getClass().getAnnotation(
            ScriptManifest.class);
    public int Revision = 15;
    public long startTime = System.currentTimeMillis();
    public int getX;
    public int getY;
    public int status;
    public String Status;
    public int team = 1;
    public int pyrefiendID = 8598;
    public int jellyID = 8599;
    public int fragmentID = 14639;
    public long seconds = 1;
    public long minutes = 0;
    public long hours = 0;
    public long xpGained = 1;
    public long xpGotten = 4;
    public long startXP;
    public long startXPa;
    public long startXPs;
    public long startXPd;
    public long startXPr;
    public long startXPm;
    public long xpHour = 1;
    public long damageHour = 1;
    public long damageDone = 1;
    public long DamageDone = 1;
    public int style = -1;
    public String Style = "Unknown";
    public int fragmentCount = 0;
    public int pickupFragments = 1;
    public String Fragment = "Unknown";
    public int hoverYes = 0;
    public RSItemTile Feva;
    public int joinTeam = 0;
    public String Team;
    public int toJoin = -5;
    public int lastJoined;
    public int specialAttack;
    public int quickPray;
    public int prayEnabled;
    public int specEnabled;
    public int turnInSouls = 0;
    public int soulRandom = 0;
    public int monster = 0;
    public int obeliskTeam = 0;
    public String Kill = "Unknown";
    public String obelisk = "Unknown";
    public int highlights = 0;
    public int hoverStillYes = 0;
    public int hoverPickup = 0;
    public int hoverTeam = 0;
    public int hoverKill = 0;
    public int lastWon = 0;
    public int lastLost = 0;
    public int zealWon = 0;
    public int gamesWon = 0;
    public int gamesLost = 0;
    public int gamesDraw = 0;
    public int gamesKicked = 0;
    public int gamesTotal = 0;
    public int gameCounted = 0;
    public int doUpdate = 0;
    public int donate = 0;
    public boolean SpecialAttack = true;
    public long specAmount = 100;
    public long beforeSpec = 100;
    public RSPlayer thePlayer;
    public int[] AllPlayers;
    public int fail;
    public String playerName;
    public RSTile playerLocation;
    public int doTry;
    public int bonePickup;
    public int activityBar;
    public int boneID = 14638;
    public int needToPickupBones;
    public int boneCount;
    public int theNum;
    public int dead;
    public float zealHour;
    public float gamesTotalHour;
    public float gamesWonHour;
    public float gamesDrawHour;
    public float gamesLostHour;
    public float gamesKickedHour;
    public int speed1 = 4;
    public int speed2 = 7;
    public RSTile[] BlueStartToBlueFiends = {new RSTile(1815, 3225), new RSTile(1813, 3225), new RSTile(1811, 3227), new RSTile(1809, 3228), new RSTile(1808, 3230), new RSTile(1808, 3233), new RSTile(1808, 3236), new RSTile(1808, 3238), new RSTile(1808, 3241), new RSTile(1811, 3244), new RSTile(1814, 3246), new RSTile(1817, 3246), new RSTile(1820, 3248), new RSTile(1821, 3248), new RSTile(1824, 3246), new RSTile(1827, 3245), new RSTile(1830, 3245), new RSTile(1834, 3245), new RSTile(1836, 3247), new RSTile(1838, 3247), new RSTile(1841, 3249), new RSTile(1844, 3248)};
    public RSTile[] BlueGraveToBlueFiends = {new RSTile(1842, 3220), new RSTile(1842, 3222), new RSTile(1842, 3226), new RSTile(1842, 3229), new RSTile(1842, 3232), new RSTile(1842, 3236), new RSTile(1842, 3238), new RSTile(1842, 3241), new RSTile(1842, 3245), new RSTile(1842, 3248), new RSTile(1843, 3249)};
    public RSTile[] RedGraveToBlueFiends = {new RSTile(1933, 3243), new RSTile(1930, 3243), new RSTile(1927, 3244), new RSTile(1924, 3244), new RSTile(1921, 3244), new RSTile(1917, 3244), new RSTile(1915, 3245), new RSTile(1912, 3246), new RSTile(1908, 3247), new RSTile(1905, 3247), new RSTile(1902, 3247), new RSTile(1898, 3249), new RSTile(1895, 3250), new RSTile(1891, 3249), new RSTile(1888, 3249), new RSTile(1884, 3249), new RSTile(1880, 3249), new RSTile(1877, 3250), new RSTile(1874, 3251), new RSTile(1871, 3251), new RSTile(1867, 3251), new RSTile(1864, 3251), new RSTile(1859, 3251), new RSTile(1857, 3249), new RSTile(1855, 3249), new RSTile(1852, 3248), new RSTile(1849, 3248), new RSTile(1846, 3250), new RSTile(1843, 3250)};
    public RSTile[] RedStartToRedFiends = {new RSTile(1960, 3239), new RSTile(1962, 3239), new RSTile(1965, 3238), new RSTile(1966, 3235), new RSTile(1966, 3232), new RSTile(1966, 3230), new RSTile(1966, 3227), new RSTile(1966, 3224), new RSTile(1966, 3223), new RSTile(1966, 3221), new RSTile(1966, 3219), new RSTile(1966, 3218), new RSTile(1964, 3216), new RSTile(1963, 3215), new RSTile(1962, 3214), new RSTile(1960, 3214), new RSTile(1958, 3214), new RSTile(1957, 3214), new RSTile(1956, 3214), new RSTile(1955, 3214), new RSTile(1954, 3214), new RSTile(1952, 3214), new RSTile(1951, 3214), new RSTile(1950, 3215), new RSTile(1948, 3217), new RSTile(1947, 3218), new RSTile(1945, 3218), new RSTile(1944, 3218), new RSTile(1942, 3217), new RSTile(1941, 3217), new RSTile(1939, 3217), new RSTile(1938, 3217), new RSTile(1935, 3219), new RSTile(1935, 3218), new RSTile(1934, 3217), new RSTile(1932, 3216), new RSTile(1931, 3216), new RSTile(1930, 3215), new RSTile(1928, 3213), new RSTile(1927, 3212)};
    public RSTile[] RedGraveToRedFiends = {new RSTile(1933, 3243), new RSTile(1933, 3241), new RSTile(1933, 3240), new RSTile(1933, 3238), new RSTile(1933, 3236), new RSTile(1935, 3234), new RSTile(1935, 3233), new RSTile(1933, 3230), new RSTile(1933, 3228), new RSTile(1933, 3226), new RSTile(1933, 3224), new RSTile(1934, 3222), new RSTile(1933, 3220), new RSTile(1932, 3219), new RSTile(1930, 3218), new RSTile(1927, 3215), new RSTile(1927, 3214)};
    public RSTile[] BlueGraveToRedFiends = {new RSTile(1842, 3220), new RSTile(1843, 3220), new RSTile(1844, 3220), new RSTile(1846, 3220), new RSTile(1848, 3219), new RSTile(1850, 3219), new RSTile(1852, 3219), new RSTile(1853, 3219), new RSTile(1855, 3217), new RSTile(1857, 3216), new RSTile(1859, 3216), new RSTile(1861, 3216), new RSTile(1863, 3216), new RSTile(1865, 3214), new RSTile(1867, 3212), new RSTile(1869, 3210), new RSTile(1870, 3209), new RSTile(1873, 3209), new RSTile(1874, 3208), new RSTile(1876, 3207), new RSTile(1878, 3207), new RSTile(1881, 3207), new RSTile(1882, 3208), new RSTile(1885, 3209), new RSTile(1886, 3210), new RSTile(1889, 3210), new RSTile(1893, 3210), new RSTile(1897, 3211), new RSTile(1899, 3211), new RSTile(1901, 3211), new RSTile(1903, 3211), new RSTile(1906, 3211), new RSTile(1907, 3211), new RSTile(1909, 3211), new RSTile(1911, 3211), new RSTile(1913, 3211), new RSTile(1915, 3211), new RSTile(1917, 3211), new RSTile(1919, 3211), new RSTile(1920, 3210), new RSTile(1922, 3210), new RSTile(1924, 3210), new RSTile(1926, 3212), new RSTile(1928, 3211)};
    public RSTile[] SoulWarsLobbyToBlueLobby = {new RSTile(1890, 3178), new RSTile(1890, 3176), new RSTile(1890, 3174), new RSTile(1890, 3172), new RSTile(1890, 3170), new RSTile(1890, 3168), new RSTile(1890, 3166), new RSTile(1888, 3164), new RSTile(1886, 3162), new RSTile(1884, 3162), new RSTile(1882, 3162), new RSTile(1880, 3162)};
    public RSTile[] SoulWarsLobbyToRedLobby = {new RSTile(1890, 3178), new RSTile(1890, 3176), new RSTile(1890, 3174), new RSTile(1890, 3172), new RSTile(1890, 3170), new RSTile(1890, 3168), new RSTile(1890, 3166), new RSTile(1892, 3164), new RSTile(1894, 3162), new RSTile(1896, 3162), new RSTile(1899, 3162)};
    public RSTile[] RedFiendsToObelisk = {new RSTile(1928, 3214), new RSTile(1928, 3216), new RSTile(1927, 3218), new RSTile(1925, 3220), new RSTile(1923, 3222), new RSTile(1921, 3224), new RSTile(1919, 3226), new RSTile(1917, 3228), new RSTile(1916, 3230), new RSTile(1914, 3231), new RSTile(1912, 3231), new RSTile(1910, 3231), new RSTile(1907, 3231), new RSTile(1905, 3231), new RSTile(1903, 3231), new RSTile(1900, 3231), new RSTile(1897, 3231), new RSTile(1892, 3231), new RSTile(1890, 3231), new RSTile(1888, 3231)};
    public RSTile[] BlueFiendsToObelisk = {new RSTile(1845, 3246), new RSTile(1846, 3246), new RSTile(1846, 3244), new RSTile(1847, 3241), new RSTile(1847, 3239), new RSTile(1849, 3237), new RSTile(1851, 3235), new RSTile(1853, 3233), new RSTile(1855, 3233), new RSTile(1858, 3233), new RSTile(1860, 3233), new RSTile(1863, 3233), new RSTile(1865, 3233), new RSTile(1867, 3233), new RSTile(1869, 3233), new RSTile(1872, 3233), new RSTile(1874, 3233), new RSTile(1877, 3233), new RSTile(1880, 3233), new RSTile(1882, 3233), new RSTile(1883, 3233), new RSTile(1885, 3231)};
    public RSTile[] ObeliskToBlueFiends = {new RSTile(1885, 3231), new RSTile(1883, 3231), new RSTile(1882, 3231), new RSTile(1879, 3231), new RSTile(1875, 3232), new RSTile(1873, 3233), new RSTile(1871, 3233), new RSTile(1868, 3233), new RSTile(1866, 3233), new RSTile(1864, 3233), new RSTile(1861, 3233), new RSTile(1859, 3233), new RSTile(1856, 3233), new RSTile(1855, 3235), new RSTile(1853, 3237), new RSTile(1851, 3240), new RSTile(1849, 3241), new RSTile(1848, 3244), new RSTile(1846, 3246), new RSTile(1844, 3247)};
    public RSTile[] ObeliskToRedFiends = {new RSTile(1888, 3231), new RSTile(1889, 3231), new RSTile(1891, 3231), new RSTile(1894, 3231), new RSTile(1896, 3231), new RSTile(1898, 3231), new RSTile(1900, 3231), new RSTile(1903, 3231), new RSTile(1906, 3231), new RSTile(1908, 3231), new RSTile(1910, 3231), new RSTile(1912, 3231), new RSTile(1914, 3231), new RSTile(1915, 3230), new RSTile(1915, 3228), new RSTile(1915, 3226), new RSTile(1915, 3223), new RSTile(1915, 3221), new RSTile(1916, 3219), new RSTile(1918, 3219), new RSTile(1921, 3219), new RSTile(1923, 3218), new RSTile(1926, 3215), new RSTile(1927, 3213), new RSTile(1929, 3211)};
    public RSTile[] BlueJelliesToObelisk = {new RSTile(1899, 3251), new RSTile(1899, 3250), new RSTile(1901, 3248), new RSTile(1904, 3245), new RSTile(1905, 3243), new RSTile(1906, 3241), new RSTile(1908, 3240), new RSTile(1910, 3238), new RSTile(1912, 3236), new RSTile(1915, 3233), new RSTile(1915, 3231), new RSTile(1913, 3231), new RSTile(1911, 3231), new RSTile(1909, 3231), new RSTile(1907, 3231), new RSTile(1905, 3231), new RSTile(1899, 3231), new RSTile(1897, 3231), new RSTile(1895, 3231), new RSTile(1892, 3231), new RSTile(1889, 3231), new RSTile(1888, 3230), new RSTile(1886, 3230)};
    public RSTile[] RedJelliesToObelisk = {new RSTile(1881, 3208), new RSTile(1876, 3207), new RSTile(1873, 3207), new RSTile(1870, 3207), new RSTile(1868, 3209), new RSTile(1867, 3210), new RSTile(1867, 3212), new RSTile(1867, 3214), new RSTile(1866, 3216), new RSTile(1866, 3217), new RSTile(1865, 3219), new RSTile(1861, 3224), new RSTile(1861, 3226), new RSTile(1861, 3228), new RSTile(1859, 3230), new RSTile(1858, 3232), new RSTile(1859, 3233), new RSTile(1861, 3233), new RSTile(1863, 3233), new RSTile(1866, 3233), new RSTile(1868, 3233), new RSTile(1870, 3233), new RSTile(1872, 3233), new RSTile(1875, 3233), new RSTile(1877, 3232), new RSTile(1879, 3232), new RSTile(1881, 3232), new RSTile(1883, 3232), new RSTile(1885, 3230), new RSTile(1887, 3230)};
    public RSTile[] ObeliskToRedJellies = {new RSTile(1887, 3230), new RSTile(1885, 3230), new RSTile(1882, 3230), new RSTile(1880, 3230), new RSTile(1877, 3231), new RSTile(1875, 3232), new RSTile(1873, 3233), new RSTile(1871, 3233), new RSTile(1869, 3233), new RSTile(1868, 3233), new RSTile(1865, 3233), new RSTile(1863, 3233), new RSTile(1861, 3233), new RSTile(1858, 3233), new RSTile(1858, 3231), new RSTile(1858, 3229), new RSTile(1858, 3227), new RSTile(1859, 3225), new RSTile(1861, 3223), new RSTile(1863, 3221), new RSTile(1866, 3218), new RSTile(1866, 3216), new RSTile(1868, 3216), new RSTile(1871, 3216), new RSTile(1873, 3214), new RSTile(1876, 3211), new RSTile(1877, 3209), new RSTile(1879, 3208), new RSTile(1882, 3208), new RSTile(1883, 3208)};
    public RSTile[] ObeliskToBlueJellies = {new RSTile(1886, 3230), new RSTile(1888, 3230), new RSTile(1890, 3230), new RSTile(1892, 3230), new RSTile(1894, 3230), new RSTile(1896, 3230), new RSTile(1898, 3230), new RSTile(1900, 3231), new RSTile(1902, 3231), new RSTile(1903, 3231), new RSTile(1905, 3231), new RSTile(1907, 3231), new RSTile(1909, 3231), new RSTile(1912, 3231), new RSTile(1915, 3231), new RSTile(1915, 3233), new RSTile(1913, 3234), new RSTile(1911, 3237), new RSTile(1908, 3239), new RSTile(1907, 3242), new RSTile(1905, 3244), new RSTile(1903, 3246), new RSTile(1901, 3248), new RSTile(1900, 3249), new RSTile(1899, 3251), new RSTile(1898, 3252)};
    public RSTile[] RedFiendsToRedJellies = {new RSTile(1927, 3212), new RSTile(1926, 3212), new RSTile(1923, 3212), new RSTile(1921, 3213), new RSTile(1918, 3213), new RSTile(1916, 3213), new RSTile(1913, 3213), new RSTile(1911, 3213), new RSTile(1909, 3212), new RSTile(1908, 3211), new RSTile(1905, 3211), new RSTile(1903, 3211), new RSTile(1900, 3211), new RSTile(1898, 3211), new RSTile(1895, 3211), new RSTile(1893, 3211), new RSTile(1890, 3211), new RSTile(1889, 3210), new RSTile(1888, 3210), new RSTile(1885, 3210), new RSTile(1882, 3210), new RSTile(1881, 3209)};
    public RSTile[] BlueFiendsToBlueJellies = {new RSTile(1847, 3248), new RSTile(1849, 3248), new RSTile(1851, 3248), new RSTile(1853, 3248), new RSTile(1855, 3248), new RSTile(1857, 3248), new RSTile(1859, 3248), new RSTile(1861, 3248), new RSTile(1863, 3250), new RSTile(1865, 3251), new RSTile(1866, 3252), new RSTile(1867, 3252), new RSTile(1869, 3252), new RSTile(1872, 3252), new RSTile(1874, 3252), new RSTile(1876, 3252), new RSTile(1878, 3254), new RSTile(1880, 3254), new RSTile(1881, 3255), new RSTile(1884, 3256), new RSTile(1886, 3256), new RSTile(1888, 3256), new RSTile(1890, 3254), new RSTile(1893, 3252), new RSTile(1894, 3252), new RSTile(1895, 3252)};
    public RSTile[] Obelisk = {new RSTile(1886, 3231), new RSTile(1886, 3232), new RSTile(1887, 3231), new RSTile(1887, 3232)};

    public boolean onStart(Map<String, String> args) {
        if (args.get("Update").equals("Yes")) {
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
        if (donate == 1) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (java.awt.Desktop.isDesktopSupported() && desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                JOptionPane.showMessageDialog(null, "Please donate to Peach to continue the updating of this amazing Soul Wars script! Any amount of money helps :) Opening donation link now..");
                String link = "http://xrl.us/bgs2zm";
                try {
                    java.net.URI uri = new java.net.URI(link);
                    desktop.browse(uri);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        ////////////////////
        //AUTO UPDATER - Thanks to RawR
        ////////////////////
        URLConnection url = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        if (doUpdate == 1) {
            try {
                url = new URL("http://classmatch.webs.com/Scripts/SoulMonstersVERSION.txt").openConnection();
                in = new BufferedReader(new InputStreamReader(url.getInputStream()));
                double version = Double.parseDouble(in.readLine());
                if (version > Revision) {
                    log("Update found.");
                    JOptionPane.showMessageDialog(null, "Please choose 'SoulMonsters.java' in your scripts folder and hit 'Open'");
                    JFileChooser fc = new JFileChooser();
                    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        url = new URL("http://classmatch.webs.com/Scripts/SoulMonsters.java").openConnection();
                        in = new BufferedReader(new InputStreamReader(url.getInputStream()));
                        out = new BufferedWriter(new FileWriter(fc.getSelectedFile().getPath()));
                        String inp;
                        while ((inp = in.readLine()) != null) {
                            out.write(inp);
                            out.newLine();
                            out.flush();
                        }
                        log("Script successfully downloaded. Please recompile and reload your scripts!");
                        return false;
                    } else {
                        log("Update canceled");
                    }
                }
                if (version == Revision) {
                    log("You have the latest version.");
                }
                if (version < Revision) {
                    log("You have a newer version than the newest version..");
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log("Problem getting version.");
                return false;
            }
        }

        if (args.get("JoinTeam").equals("Switch")) {
            joinTeam = -1;
        }
        if (args.get("JoinTeam").equals("Random")) {
            joinTeam = 0;
        }
        if (args.get("JoinTeam").equals("Always Blue")) {
            joinTeam = 1;
        }
        if (args.get("JoinTeam").equals("Always Red")) {
            joinTeam = 2;
        }
        if (args.get("JoinTeam").equals("Last Won")) {
            joinTeam = 3;
        }
        if (args.get("JoinTeam").equals("Last Lost")) {
            joinTeam = 4;
        }
        if (args.get("QuickPray").equals("Never")) {
            quickPray = 0;
        }
        if (args.get("QuickPray").equals("Pyrefiends")) {
            quickPray = 1;
        }
        if (args.get("QuickPray").equals("Starting area")) {
            quickPray = 2;
        }
        if (args.get("QuickPray").equals("Jellies")) {
            quickPray = 3;
        }
        if (args.get("SpecialAttack").equals("Never")) {
            specialAttack = 0;
            SpecialAttack = false;
            specAmount = 101;
            beforeSpec = 0;
        }
        if (args.get("SpecialAttack").equals("Pyrefiends")) {
            specialAttack = 1;
            SpecialAttack = true;
            specAmount = 100;
            beforeSpec = 100;
        }
        if (args.get("SpecialAttack").equals("Starting area")) {
            specialAttack = 2;
            SpecialAttack = true;
            specAmount = 100;
            beforeSpec = 100;
        }
        if (args.get("SpecialAttack").equals("Jellies")) {
            specialAttack = 3;
            SpecialAttack = true;
            specAmount = 100;
            beforeSpec = 100;
        }
        if (args.get("SoulTurnIn").equals("Never")) {
            turnInSouls = 0;
            soulRandom = 0;
        }
        if (args.get("SoulTurnIn").equals("Random")) {
            turnInSouls = 0;
            soulRandom = 1;
        }
        if (args.get("SoulTurnIn").equals("10 Souls")) {
            turnInSouls = 10;
            soulRandom = 0;
        }
        if (args.get("SoulTurnIn").equals("20 Souls")) {
            turnInSouls = 20;
            soulRandom = 0;
        }
        if (args.get("SoulTurnIn").equals("30 Souls")) {
            turnInSouls = 30;
            soulRandom = 0;
        }
        if (args.get("Monster").equals("Pyrefiends")) {
            monster = 1;
        }
        if (args.get("Monster").equals("Jellies")) {
            monster = 2;
        }
        if (args.get("Monster").equals("People")) {
            monster = 3;
        }
        if (args.get("BonePickup").equals("Yes")) {
            bonePickup = 1;
        }
        return true;
    }

    public void onFinish() {
        ScreenshotUtil.takeScreenshot(true);
        return;
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        if (e.getMessage().indexOf("priority") != -1) {
            obeliskTeam = 0;
            gameCounted = 0;
        }
        if (e.getMessage().indexOf("lost control of the soul") != -1) {
            obeliskTeam = 0;
            if (gameCounted == 0) {
                gameCounted = 1;
                gamesTotal += 1;
            }
        }
        if (e.getMessage().indexOf("blue</col> team has taken control of the soul") != -1) {
            obeliskTeam = 1;
            if (gameCounted == 0) {
                gameCounted = 1;
                gamesTotal += 1;
            }
        }
        if (e.getMessage().indexOf("red</col> team has taken control of the soul") != -1) {
            obeliskTeam = 2;
            if (gameCounted == 0) {
                gameCounted = 1;
                gamesTotal += 1;
            }
        }
        if (b()) {
            c();
        }
        if (e.getMessage().indexOf("You receive 3 Zeal for") != -1) {
            gamesWon += 1;
        }
        if (e.getMessage().indexOf("You receive 2 Zeal for") != -1) {
            gamesDraw += 1;
            lastWon = random(1, 2);
            if (lastWon == 1) {
                lastLost = 2;
            }
            if (lastWon == 2) {
                lastLost = 1;
            }
        }
        if (e.getMessage().indexOf("You receive 1 Zeal for") != -1) {
            gamesLost += 1;
        }
        if (e.getMessage().indexOf("red</col> team was victor") != -1) {
            lastWon = 2;
            lastLost = 1;
        }
        if (e.getMessage().indexOf("blue</col> team was victor") != -1) {
            lastWon = 1;
            lastLost = 2;
        }
        if (e.getMessage().indexOf("getting low") != -1) {
            needToPickupBones = 1;
        }
        if (e.getMessage().indexOf("Oh dear") != -1) {
            needToPickupBones = 0;
            status = 0;
            dead = 1;
        }
        if (e.getMessage().indexOf("You receive") != -1) {
            needToPickupBones = 0;
        }
    }

    public void onRepaint(Graphics g) {
        if (isLoggedIn()) {
            if (startXPa == 0) {
                startXPa = skills.getCurrentSkillExp(STAT_ATTACK);
            }
            if (startXPs == 0) {
                startXPs = skills.getCurrentSkillExp(STAT_STRENGTH);
            }
            if (startXPd == 0) {
                startXPd = skills.getCurrentSkillExp(STAT_DEFENSE);
            }
            if (startXPr == 0) {
                startXPr = skills.getCurrentSkillExp(STAT_RANGE);
            }
            if (startXPm == 0) {
                startXPm = skills.getCurrentSkillExp(STAT_MAGIC);
            }
            if (getSpec() < beforeSpec) {
                beforeSpec = 0;
                specAmount = beforeSpec - getSpec();
                if (specAmount < 0) {
                    specAmount = (0 - 1) * specAmount;
                }
            }
            if (style == -1) {
                if (b()) {
                    c();
                }
                if (startXPa < skills.getCurrentSkillExp(STAT_ATTACK)) {
                    style = STAT_ATTACK;
                    Style = "Attack";
                    startXP = startXPa;
                }
                if (startXPs < skills.getCurrentSkillExp(STAT_STRENGTH)) {
                    style = STAT_STRENGTH;
                    Style = "Strength";
                    startXP = startXPs;
                }
                if (startXPd < skills.getCurrentSkillExp(STAT_DEFENSE)) {
                    style = STAT_DEFENSE;
                    Style = "Defence";
                    startXP = startXPd;
                }
                if (startXPr < skills.getCurrentSkillExp(STAT_RANGE)) {
                    style = STAT_RANGE;
                    Style = "Range";
                    startXP = startXPr;
                }
                if (startXPm < skills.getCurrentSkillExp(STAT_MAGIC)) {
                    style = STAT_MAGIC;
                    Style = "Magic";
                    startXP = startXPm;
                }
                startTime = System.currentTimeMillis();
            }
            if (style != -1) {
                xpGained = skills.getCurrentSkillExp(style) - startXP;
                long millis = System.currentTimeMillis() - startTime;
                hours = millis / (1000 * 60 * 60);
                millis -= hours * (1000 * 60 * 60);
                minutes = millis / (1000 * 60);
                millis -= minutes * (1000 * 60);
                seconds = millis / 1000;
                long minutes2 = minutes + (hours * 60);
            }
        }
        long timeInMillis = System.currentTimeMillis() - startTime;
        if (timeInMillis < 1) {
            timeInMillis = 1;
        }
        if (xpGained > 0 && style != -1) {
            xpHour = (xpGained * 60 * 60) / (timeInMillis / 1000);
            DamageDone = xpGained / xpGotten;
            damageDone = (long) DamageDone;
            damageHour = (damageDone * 60 * 60) / (timeInMillis / 1000);
        }
        if (turnInSouls == 0 && soulRandom == 1) {
            turnInSouls = random(10, 30);
        }
        gamesKicked = gamesTotal - gamesWon - gamesLost - gamesDraw;
        if (gamesWon + gamesLost + gamesDraw + gamesKicked > gamesTotal) {
            gamesTotal = gamesWon + gamesLost + gamesDraw + gamesKicked;
        }
        if (gamesKicked < 0) {
            gamesKicked = 0;
        }
        zealWon = (gamesWon * 3) + (gamesLost * 1) + (gamesDraw * 2);
        Point mouse = new Point(Bot.getClient().getMouse().x, Bot.getClient().getMouse().y);
        Rectangle hover = new Rectangle(189, 318, 136, 22);
        if (hover.contains(mouse)) {
            hoverYes = 2;
        }
        if (hoverStillYes == 1) {
            hoverYes = 2;
        }
        if (hoverYes != 2) {
            hoverYes = 1;
        }
        makeBox(" Hover for Options", 192, 321, 130, 16, 0, hoverYes, g);
        if (highlights == 1 || hoverYes == 2) {
            hoverYes = 1;
            if (b()) {
                c();
            }
            makeBox("Pickup Fragments", 192, 321, 130, 17, 1, hoverYes, g);
            makeBox("      Join Team", 192, 321, 130, 17, 2, hoverYes, g);
            makeBox("             Kill", 192, 321, 130, 17, 3, hoverYes, g);
            makeBox("    Use Souls At", 192, 321, 130, 17, 4, hoverYes, g);
            hoverYes = 2;
        }
        mouse = new Point(Bot.getClient().getMouse().x, Bot.getClient().getMouse().y);
        hover = new Rectangle(188, 325, 133, 16);
        if (hover.contains(mouse)) {
            hoverYes = 2;
        }
        if (hoverYes == 2) {
            mouse = new Point(Bot.getClient().getMouse().x, Bot.getClient().getMouse().y);
            hover = new Rectangle(189, 256, 136, 17); //Use souls at
            if (hover.contains(mouse)) {
                hoverStillYes = 1;
            }
            hover = new Rectangle(189, 273, 136, 17); //Kill
            if (hover.contains(mouse)) {
                hoverKill = 1;
            }
            hover = new Rectangle(189, 290, 136, 17); //Join team
            if (hover.contains(mouse)) {
                hoverTeam = 1;
            }
            hover = new Rectangle(189, 307, 136, 17); //Pickup fragments
            if (hover.contains(mouse)) {
                hoverPickup = 1;
            }
        }
        if (hoverStillYes == 1) {
            if (soulRandom == 1) {
                makeBox("         Random", 322, 321, 130, 17, 8, 2, g);
                makeBox("         10 Souls", 322, 321, 130, 17, 7, 1, g);
                makeBox("         20 Souls", 322, 321, 130, 17, 6, 1, g);
                makeBox("         30 Souls", 322, 321, 130, 17, 5, 1, g);
                makeBox("           Never", 322, 321, 130, 17, 4, 1, g);
            }
            if (soulRandom == 0) {
                makeBox("         Random", 322, 321, 130, 17, 8, 1, g);
                if (turnInSouls == 10) {
                    makeBox("         10 Souls", 322, 321, 130, 17, 7, 2, g);
                }
                if (turnInSouls != 10) {
                    makeBox("         10 Souls", 322, 321, 130, 17, 7, 1, g);
                }
                if (turnInSouls == 20) {
                    makeBox("         20 Souls", 322, 321, 130, 17, 6, 2, g);
                }
                if (turnInSouls != 20) {
                    makeBox("         20 Souls", 322, 321, 130, 17, 6, 1, g);
                }
                if (turnInSouls == 30) {
                    makeBox("         30 Souls", 322, 321, 130, 17, 5, 2, g);
                }
                if (turnInSouls != 30) {
                    makeBox("         30 Souls", 322, 321, 130, 17, 5, 1, g);
                }
                if (turnInSouls == 0) {
                    makeBox("           Never", 322, 321, 130, 17, 4, 2, g);
                }
                if (turnInSouls != 0) {
                    makeBox("           Never", 322, 321, 130, 17, 4, 1, g);
                }
            }
            hoverYes = 2;
        }
        if (hoverKill == 1) {
            if (monster == 1) {
                makeBox("           People", 62, 321, 130, 17, 5, 1, g);
                makeBox("       Pyrefiends", 62, 321, 130, 17, 4, 2, g);
                makeBox("          Jellies", 62, 321, 130, 17, 3, 1, g);
            }
            if (monster == 2) {
                makeBox("           People", 62, 321, 130, 17, 5, 1, g);
                makeBox("       Pyrefiends", 62, 321, 130, 17, 4, 1, g);
                makeBox("          Jellies", 62, 321, 130, 17, 3, 2, g);
            }
            if (monster == 3) {
                makeBox("           People", 62, 321, 130, 17, 5, 2, g);
                makeBox("       Pyrefiends", 62, 321, 130, 17, 4, 1, g);
                makeBox("          Jellies", 62, 321, 130, 17, 3, 1, g);
            }
            if (b()) {
                c();
            }
            hoverYes = 2;
        }
        if (hoverTeam == 1) {
            if (joinTeam == 4) {
                makeBox("        Last Lost", 322, 321, 130, 17, 7, 2, g);
            }
            if (joinTeam != 4) {
                makeBox("        Last Lost", 322, 321, 130, 17, 7, 1, g);
            }
            if (joinTeam == 3) {
                makeBox("        Last Won", 322, 321, 130, 17, 6, 2, g);
            }
            if (joinTeam != 3) {
                makeBox("        Last Won", 322, 321, 130, 17, 6, 1, g);
            }
            if (joinTeam == 2) {
                makeBox("      Always Red", 322, 321, 130, 17, 5, 2, g);
            }
            if (joinTeam != 2) {
                makeBox("      Always Red", 322, 321, 130, 17, 5, 1, g);
            }
            if (joinTeam == 1) {
                makeBox("      Always Blue", 322, 321, 130, 17, 4, 2, g);
            }
            if (joinTeam != 1) {
                makeBox("      Always Blue", 322, 321, 130, 17, 4, 1, g);
            }
            if (joinTeam == -1) {
                makeBox("          Switch", 322, 321, 130, 17, 3, 2, g);
            }
            if (joinTeam != -1) {
                makeBox("          Switch", 322, 321, 130, 17, 3, 1, g);
            }
            if (joinTeam == 0) {
                makeBox("        Random", 322, 321, 130, 17, 2, 2, g);
            }
            if (joinTeam != 0) {
                makeBox("        Random", 322, 321, 130, 17, 2, 1, g);
            }
            hoverYes = 2;
        }
        if (hoverPickup == 1) {
            if (pickupFragments == 1) {
                makeBox("             Yes", 62, 321, 130, 17, 2, 2, g);
                makeBox("              No", 62, 321, 130, 17, 1, 1, g);
            }
            if (pickupFragments == 2) {
                makeBox("             Yes", 62, 321, 130, 17, 2, 1, g);
                makeBox("              No", 62, 321, 130, 17, 1, 2, g);
            }
            hoverYes = 2;
        }
        if (hoverYes == 2 && hoverStillYes == 1) {
            hover = new Rectangle(322, 190, 130, 16);
            if (hover.contains(mouse)) {
                turnInSouls = random(10, 30);
                soulRandom = 1;
            }
            if (!hover.contains(mouse)) {
                hover = new Rectangle(322, 206, 130, 66);
                if (hover.contains(mouse)) {
                    soulRandom = 0;
                }
                hover = new Rectangle(322, 206, 130, 16);
                if (hover.contains(mouse)) {
                    turnInSouls = 10;
                }
                hover = new Rectangle(322, 232, 130, 16);
                if (hover.contains(mouse)) {
                    turnInSouls = 20;
                }
                hover = new Rectangle(322, 248, 130, 16);
                if (hover.contains(mouse)) {
                    turnInSouls = 30;
                }
                hover = new Rectangle(322, 264, 130, 16);
                if (hover.contains(mouse)) {
                    turnInSouls = 0;
                }
            }
        }
        if (hoverYes == 2 && hoverKill == 1) {
            hover = new Rectangle(62, 236, 133, 17);
            if (hover.contains(mouse)) {
                monster = 3;
            }
            hover = new Rectangle(62, 253, 133, 17);
            if (hover.contains(mouse)) {
                monster = 1;
            }
            hover = new Rectangle(62, 270, 133, 17);
            if (hover.contains(mouse)) {
                monster = 2;
            }
        }
        if (hoverYes == 2 && hoverTeam == 1) {
            hover = new Rectangle(322, 206, 130, 16);
            if (hover.contains(mouse)) {
                joinTeam = 4;
            }
            hover = new Rectangle(322, 222, 130, 16);
            if (hover.contains(mouse)) {
                joinTeam = 3;
            }
            hover = new Rectangle(322, 248, 130, 16);
            if (hover.contains(mouse)) {
                joinTeam = 2;
            }
            hover = new Rectangle(322, 264, 130, 16);
            if (hover.contains(mouse)) {
                joinTeam = 1;
            }
            if (b()) {
                c();
            }
            hover = new Rectangle(322, 280, 130, 16);
            if (hover.contains(mouse)) {
                joinTeam = -1;
            }
            hover = new Rectangle(322, 296, 130, 16);
            if (hover.contains(mouse)) {
                joinTeam = 0;
            }
        }
        if (hoverYes == 2 && hoverPickup == 1) {
            hover = new Rectangle(62, 293, 133, 17);
            if (hover.contains(mouse)) {
                pickupFragments = 1;
            }
            hover = new Rectangle(62, 310, 133, 17);
            if (hover.contains(mouse)) {
                pickupFragments = 2;
            }
        }
        hover = new Rectangle(188, 254, 133, 16);
        if (!hover.contains(mouse)) {
            hover = new Rectangle(320, 190, 133, 82);
            if (!hover.contains(mouse)) {
                hoverStillYes = 0;
            }
        }
        hover = new Rectangle(189, 273, 136, 17);
        if (!hover.contains(mouse)) {
            hover = new Rectangle(62, 235, 133, 53);
            if (!hover.contains(mouse)) {
                hoverKill = 0;
            }
        }
        hover = new Rectangle(188, 290, 133, 16);
        if (!hover.contains(mouse)) {
            hover = new Rectangle(320, 197, 133, 102);
            if (!hover.contains(mouse)) {
                hoverTeam = 0;
            }
        }
        hover = new Rectangle(189, 307, 136, 17);
        if (!hover.contains(mouse)) {
            hover = new Rectangle(62, 293, 133, 34);
            if (!hover.contains(mouse)) {
                hoverPickup = 0;
            }
        }
        hover = new Rectangle(188, 254, 133, 98);
        if (!hover.contains(mouse)) {
            hover = new Rectangle(320, 190, 133, 82);
            if (!hover.contains(mouse)) {
                hoverStillYes = 0;
                hoverYes = 1;
            }
        }
        hover = new Rectangle(188, 254, 133, 98);
        if (!hover.contains(mouse)) {
            hover = new Rectangle(62, 235, 133, 53);
            if (!hover.contains(mouse)) {
                hoverKill = 0;
                hoverYes = 1;
            }
        }
        hover = new Rectangle(188, 254, 133, 98);
        if (!hover.contains(mouse)) {
            hover = new Rectangle(320, 197, 133, 102);
            if (!hover.contains(mouse)) {
                hoverTeam = 0;
                hoverYes = 1;
            }
        }
        hover = new Rectangle(188, 254, 133, 98);
        if (!hover.contains(mouse)) {
            hover = new Rectangle(62, 293, 133, 34);
            if (!hover.contains(mouse)) {
                hoverPickup = 0;
                hoverYes = 1;
            }
        }
        if (hoverStillYes == 1) {
            hoverYes = 2;
        }
        if (hoverKill == 1) {
            hoverYes = 2;
        }
        if (hoverTeam == 1) {
            hoverYes = 2;
        }
        if (hoverPickup == 1) {
            hoverYes = 2;
        }
        if (zealWon > 0) {
            float zealWonf = (float) (zealWon);
            zealHour = Round((zealWonf * 3600000L) / timeInMillis, 2);
        }
        if (gamesTotal > 0) {
            float gamesTotalf = (float) (gamesTotal);
            gamesTotalHour = Round((gamesTotalf * 3600000L) / timeInMillis, 2);
        }
        if (gamesWon > 0) {
            float gamesWonf = (float) (gamesWon);
            gamesWonHour = Round((gamesWonf * 3600000L) / timeInMillis, 2);
        }
        if (gamesDraw > 0) {
            float gamesDrawf = (float) (gamesDraw);
            gamesDrawHour = Round((gamesDrawf * 3600000L) / timeInMillis, 2);
        }
        if (gamesLost > 0) {
            float gamesLostf = (float) (gamesLost);
            gamesLostHour = Round((gamesLostf * 3600000L) / timeInMillis, 2);
        }
        if (gamesKicked > 0) {
            float gamesKickedf = (float) (gamesKicked);
            gamesKickedHour = Round((gamesKickedf * 3600000L) / timeInMillis, 2);
        }
        if (zealHour > 999) {
            zealHour = 0;
        }
        if (gamesTotalHour > 999) {
            gamesTotalHour = 0;
        }
        if (gamesWonHour > 999) {
            gamesWonHour = 0;
        }
        if (gamesDrawHour > 999) {
            gamesDrawHour = 0;
        }
        if (gamesLostHour > 999) {
            gamesLostHour = 0;
        }
        if (gamesKickedHour > 999) {
            gamesKickedHour = 0;
        }
        if (status == -6) {
            Status = "Clicking Interfaces";
        }
        if (status == -5) {
            Status = "Joining Blue";
        }
        if (status == -4) {
            Status = "Joining Red";
        }
        if (status == -3) {
            Status = "In Soul Wars Lobby";
        }
        if (status == -2) {
            Status = "In Red Lobby";
        }
        if (status == -1) {
            Status = "In Blue Lobby";
        }
        if (status == 0) {
            Status = "Idle";
        }
        if (status == 1) {
            Status = "In Blue Start";
        }
        if (status == 2) {
            Status = "In Blue Grave";
        }
        if (status == 3) {
            Status = "In Red Grave";
        }
        if (status == 4) {
            Status = "Walking to Blue Pyrefiends";
        }
        if (status == 5) {
            Status = "Walking to Blue Pyrefiends";
        }
        if (status == 6) {
            Status = "Walking to Blue Pyrefiends";
        }
        if (status == 7) {
            Status = "In Red Start";
        }
        if (status == 8) {
            Status = "Walking to Red Pyrefiends";
        }
        if (status == 9) {
            Status = "Walking to Red Pyrefiends";
        }
        if (status == 10) {
            Status = "Walking to Red Pyrefiends";
        }
        if (status == 11) {
            Status = "Picking up Fragments";
        }
        if (status == 12) {
            Status = "Enabling Quick Prayer";
        }
        if (status == 13) {
            Status = "Enabling Special Attack";
        }
        if (status == 14) {
            Status = "Walking to Blue Jellies";
        }
        if (status == 15) {
            Status = "Walking to Blue Jellies";
        }
        if (status == 16) {
            Status = "Walking to Red Jellies";
        }
        if (status == 17) {
            Status = "Walking to Red Jellies";
        }
        if (status == 18) {
            Status = "Walking to Obelisk";
        }
        if (status == 19) {
            Status = "Walking to Obelisk";
        }
        if (status == 20) {
            Status = "Walking to Blue Pyrefiends";
        }
        if (status == 21) {
            Status = "Walking to Red Pyrefiends";
        }
        if (status == 22) {
            Status = "Putting in Fragments";
        }
        if (status == 23) {
            Status = "Walking to Obelisk";
        }
        if (status == 24) {
            Status = "Walking to Obelisk";
        }
        if (status == 25) {
            Status = "Walking to Obelisk";
        }
        if (status == 26) {
            Status = "Picking up Bones";
        }
        if (status == 100) {
            Status = "Attacking Pyrefiend";
        }
        if (status == 101) {
            Status = "Attacking Jelly";
        }
        if (status == 102) {
            Status = "Attacking People";
        }
        if (status == 103) {
            Status = "Enabling Special Attack";
        }
        if (status == 104) {
            Status = "Looking for Player";
        }
        if (status == 105) {
            Status = "Attacking Player";
        }
        if (status == 106) {
            Status = "Walking to Player";
        }
        if (status == 999) {
            Status = "What are we doing?";
        }
        if (pickupFragments == 1) {
            Fragment = "Yes";
        }
        if (pickupFragments == 2) {
            Fragment = "No";
        }
        if (obeliskTeam == 0) {
            obelisk = "Neutral";
        }
        if (obeliskTeam == 1) {
            obelisk = "Blue";
        }
        if (obeliskTeam == 2) {
            obelisk = "Red";
        }
        if (monster == 1) {
            Kill = "Pyrefiends";
        }
        if (monster == 2) {
            Kill = "Jellies";
        }
        if (monster == 3) {
            Kill = "People";
        }
        if (joinTeam == -1) {
            Team = "Switch";
        }
        if (joinTeam == 0) {
            Team = "Random";
        }
        if (joinTeam == 1) {
            Team = "Blue";
        }
        if (joinTeam == 2) {
            Team = "Red";
        }
        if (joinTeam == 3) {
            Team = "Last Win";
        }
        if (joinTeam == 4) {
            Team = "Last Lost";
        }
        g.setColor(new Color(0, 0, 0, 125));
        g.fillRoundRect(9, 33, 210, 121, 4, 4);
        g.fillRoundRect(369, 5, 142, 15, 4, 4);
        g.fillRoundRect(9, 167, 185, 93, 4, 4);
        paintA("Peach's SoulMonster v2.1", g);
        paint("Time running: " + hours + ":" + minutes + ":" + seconds, g, 1);
        paint("Status:" + status + ": " + Status, g, 2);
        paint("Style: " + Style + "     Join: " + Team, g, 3);
        paint("Kill: " + Kill + "     Obelisk: " + obelisk, g, 4);
        if (specAmount < 101) {
            paint("Spec At: " + specAmount + " | Try Number: " + doTry, g, 5);
        }
        if (specAmount > 100) {
            paint("Spec At: Never | Try Number: " + doTry, g, 5);
        }
        paint("Experience Gained: " + xpGained, g, 7);
        paint("Experience Per Hour: " + xpHour, g, 8);
        if (zealWon == 0) {
            paint("Zeal Won: " + zealWon, g, 10);
        }
        if (gamesTotal == 0) {
            paint("Games Played: " + gamesTotal, g, 11);
        }
        if (gamesWon == 0) {
            paint("Games Won: " + gamesWon, g, 12);
        }
        if (gamesDraw == 0) {
            paint("Games Drawn: " + gamesDraw, g, 13);
        }
        if (gamesLost == 0) {
            paint("Games Lost: " + gamesLost, g, 14);
        }
        if (gamesKicked == 0) {
            paint("Games Kicked: " + gamesKicked, g, 15);
        }
        if (zealWon > 0) {
            paint("Zeal Won: " + zealWon + " p/h: " + zealHour, g, 10);
        }
        if (gamesTotal > 0) {
            paint("Games Played: " + gamesTotal + " p/h: " + gamesTotalHour, g, 11);
        }
        if (gamesWon > 0) {
            paint("Games Won: " + gamesWon + " p/h: " + gamesWonHour, g, 12);
        }
        if (gamesDraw > 0) {
            paint("Games Drawn: " + gamesDraw + " p/h: " + gamesDrawHour, g, 13);
        }
        if (gamesLost > 0) {
            paint("Games Lost: " + gamesLost + " p/h: " + gamesLostHour, g, 14);
        }
        if (gamesKicked > 0) {
            paint("Games Kicked: " + gamesKicked + " p/h: " + gamesKickedHour, g, 15);
        }
    }

    public void paint(String a, Graphics b, int c) {
        b.setColor(Color.black);
        b.drawString(a, 11, 31 + (c * 15));
        b.setColor(Color.green);
        b.drawString(a, 10, 30 + (c * 15));
    }

    public void paintA(String a, Graphics b) {
        b.setColor(Color.black);
        b.drawString(a, 371, 18);
        b.setColor(Color.green);
        b.drawString(a, 370, 17);
    }

    public void makeBox(String string, int X, int Y, int X2, int Y2, int xOffset, int highlight, Graphics g) {
        //string: Text to use
        //x,y,x2,y2: Rectangle
        //xOffset: how many boxes up it is.
        //highlight:
        //	1: hovered = yellow. not = green
        //	2: hovered = yellow. not = red.
        //g = graphic
        Point mouse = new Point(Bot.getClient().getMouse().x, Bot.getClient().getMouse().y);
        Rectangle hover = new Rectangle(X, Y - (16 * xOffset), X2, Y2);
        if (highlight == 1) {
            if (hover.contains(mouse)) {
                highlight = 1;
            }
            if (!hover.contains(mouse)) {
                highlight = 0;
            }
        }
        if (highlight == 2) {
            if (hover.contains(mouse)) {
                highlight = 1;
            }
            if (!hover.contains(mouse)) {
                highlight = 2;
            }
        }
        if (xOffset == 0) {
            highlights = highlight;
        }
        if (xOffset > 0 && highlight == 1) {
            highlights = 1;
        }
        g.setColor(new Color(0, 0, 0, 125));
        g.fill3DRect(X, Y - (16 * xOffset), X2, Y2, true);
        g.setColor(Color.black);
        g.drawString(string, X + 16 + 1, Y + 13 - (16 * xOffset));
        if (highlight == 0) {
            g.setColor(Color.green);
        }
        if (highlight == 1) {
            g.setColor(Color.yellow);
        }
        if (highlight == 2) {
            g.setColor(Color.red);
        }
        g.drawString(string, X + 16, Y + 12 - (16 * xOffset));
    }

    public int loop() {
        if (!isLoggedIn()) {
            return random(400, 500);
        }
        getStatus();
        return random(10, 100);
    }

    public void getStatus() {
        position();
        status = 999;
        if (!getMyPlayer().isInCombat() && !getMyPlayer().isMoving() && getMyPlayer().getAnimation() == -1) {
            if (monster == 1) {
                status = 100; //attack pyrefiends
            }
            if (monster == 2) {
                status = 101; //attack jellies
            }
        }
        if (status == 100 && !inRectangle(1916, 3201, 1937, 3221) && !inRectangle(1833, 3236, 1862, 3258)) {
            status = 999;
        }
        if (status == 101 && !inRectangle(1883, 3243, 1910, 3259) && !inRectangle(1871, 3200, 1895, 3214)) {
            status = 999;
        }
        if (getMyPlayer().isInCombat()) {
            status = 0;
        }
        if (getMyPlayer().getAnimation() != -1) {
            status = 0;
        }
        if (getMyPlayer().isMoving()) {
            status = 0;
        }
        if (inRectangle(1850, 3130, 1920, 3190)) {
            status = -3; //in soul wars lobby
            prayEnabled = 0;
            specEnabled = 0;
        }
        if (inRectangle(1880, 3160, 1883, 3164)) {
            status = -5; //join blue team
            prayEnabled = 0;
            specEnabled = 0;
        }
        if (inRectangle(1896, 3160, 1899, 3164)) {
            status = -4; //join red team
            prayEnabled = 0;
            specEnabled = 0;
        }
        if (inRectangle(1815, 3225, 1815, 3225)) {
            team = 1;
            status = 4; //walk to blue pyres from blue start
        }
        if (inRectangle(1842, 3220, 1842, 3220) && team == 1) {
            status = 5; //walk to blue pyres from blue grave
        }
        if (inRectangle(1933, 3243, 1933, 3243) && team == 1) {
            status = 6; //walk to blue pyres from red grave
        }
        if (inRectangle(1959, 3239, 1959, 3239)) {
            team = 2;
            status = 8; //walk to red pyres from red start
        }
        if (inRectangle(1933, 3243, 1933, 3243) && team == 2) {
            status = 9; //walk to red pyres from red grave
        }
        if (inRectangle(1842, 3220, 1842, 3220) && team == 2) {
            status = 10; //walk to red pyres from blue grave
        }
        if (inRectangle(1883, 3243, 1910, 3259) && team == 1 && getInventoryCount(fragmentID) == 0 && monster == 2) {
            status = 15; //walk to blue jellies from obelisk
        }
        if (inRectangle(1883, 3243, 1910, 3259) && team == 1 && monster == 2 && obeliskTeam != team) {
            status = 15; //walk to blue jellies from obelisk
        }
        if (inRectangle(1871, 3200, 1895, 3214) && team == 2 && getInventoryCount(fragmentID) == 0 && monster == 2) {
            status = 17; //walk to red jellies from obelisk
        }
        if (inRectangle(1871, 3200, 1895, 3214) && team == 2 && monster == 2 && obeliskTeam != team) {
            status = 17; //walk to red jellies from obelisk
        }
        if (inRectangle(1833, 3236, 1862, 3258) && team == 1 && getInventoryCount(fragmentID) == 0 && monster == 1) {
            status = 20; //walk to blue fiends from obelisk
        }
        if (inRectangle(1833, 3236, 1862, 3258) && team == 1 && monster == 1 && obeliskTeam != team) {
            status = 20; //walk to blue fiends from obelisk
        }
        if (inRectangle(1916, 3201, 1937, 3221) && team == 2 && getInventoryCount(fragmentID) == 0 && monster == 1) {
            status = 21; //walk to red fiends from obelisk
        }
        if (inRectangle(1916, 3201, 1937, 3221) && team == 2 && monster == 1 && obeliskTeam != team) {
            status = 21; //walk to red fiends from obelisk
        }
        if (inRectangle(1883, 3228, 1890, 3235) && getInventoryCount(fragmentID) != 0 && obeliskTeam == team) {
            status = 22; //put in fragments
        }
        if (inRectangle(1833, 3236, 1862, 3258) && !getMyPlayer().isInCombat() && !getMyPlayer().isMoving() && getMyPlayer().getAnimation() == -1) {
            if (monster == 1) {
                status = 100; //attack pyrefiends
            }
            if (monster == 2) {
                status = 14; //walk to blue jellies
            }
            if (monster == 3) {
                status = 23; //walk to obelisk
            }
        }
        if (inRectangle(1916, 3201, 1937, 3221) && !getMyPlayer().isInCombat() && !getMyPlayer().isMoving() && getMyPlayer().getAnimation() == -1) {
            if (monster == 1) {
                status = 100; //attack pyrefiends
            }
            if (monster == 2) {
                status = 16; //walk to red jellies
            }
            if (monster == 3) {
                status = 23; //walk to obelisk
            }
        }
        if (inRectangle(1883, 3243, 1910, 3259) && !getMyPlayer().isInCombat() && !getMyPlayer().isMoving() && getMyPlayer().getAnimation() == -1) {
            if (monster == 2) {
                status = 101; //attack blue jellies
            }
        }
        if (inRectangle(1871, 3200, 1895, 3214) && !getMyPlayer().isInCombat() && !getMyPlayer().isMoving() && getMyPlayer().getAnimation() == -1) {
            if (monster == 2) {
                status = 101; //attack red jellies
            }
        }
        if (inRectangle(1916, 3201, 1937, 3221) || inRectangle(1833, 3236, 1862, 3258)) {
            if (monster == 1) {
                if (getMyPlayer().isInCombat()) {
                    status = 0;
                }
                if (getMyPlayer().getAnimation() != -1) {
                    status = 0;
                }
                if (getMyPlayer().isMoving()) {
                    status = 0;
                }
            }
        }
        if (inRectangle(1871, 3200, 1895, 3214) || inRectangle(1883, 3243, 1910, 3259)) {
            if (monster == 2) {
                if (getMyPlayer().isInCombat()) {
                    status = 0;
                }
                if (getMyPlayer().getAnimation() != -1) {
                    status = 0;
                }
                if (getMyPlayer().isMoving()) {
                    status = 0;
                }
            }
        }
        if (getInventoryCount(fragmentID) != 0 && obeliskTeam == team) {
            if (turnInSouls != 0) {
                if (getInventoryCount(fragmentID) >= turnInSouls) {
                    if (!inRectangle(1883, 3228, 1890, 3235)) {
                        status = 23; //walk to obelisk
                    }
                    if (inRectangle(1883, 3228, 1890, 3235)) {
                        status = 22; //put in fragments
                    }
                }
            }
        }
        if (inRectangle(1873, 3218, 1900, 3245)) {
            if (monster == 3) {
                if (getMyPlayer().isInCombat()) {
                    status = 0;
                }
                if (getMyPlayer().getAnimation() != -1) {
                    status = 0;
                }
                if (getMyPlayer().isMoving()) {
                    status = 0;
                }
                if (status != 0) {
                    status = 102; //attack people
                }
            }
        }
        if (inRectangle(1900, 3157, 1909, 3165)) {
            team = 2;
            status = -2; //in red lobby
            prayEnabled = 0;
            specEnabled = 0;
        }
        if (inRectangle(1870, 3158, 1879, 3166)) {
            team = 1;
            status = -1; //in blue lobby
            prayEnabled = 0;
            specEnabled = 0;
        }
        if (inRectangle(1816, 3220, 1823, 3230)) {
            team = 1;
            status = 1; //in blue start
            prayEnabled = 0;
            specEnabled = 0;
        }
        if (inRectangle(1841, 3217, 1843, 3219)) {
            status = 2; //in blue graveyard
            prayEnabled = 0;
            specEnabled = 0;
        }
        if (inRectangle(1932, 3244, 1935, 3246)) {
            status = 3; //in red graveyard
            prayEnabled = 0;
            specEnabled = 0;
        }
        if (inRectangle(1951, 3234, 1958, 3244)) {
            team = 2;
            status = 7; //in red start
            prayEnabled = 0;
            specEnabled = 0;
        }
        if (getMyPlayer().isInCombat() && useSpecial() && !enabledSpec() && getSpec() >= specAmount) {
            status = 103;
        }
        doStatus();
    }

    public void doStatus() {
        //if(status == -6) {
        clickInterfaces();
        //}
        if (status == -5) {
            joinBlue();
        }
        if (status == -4) {
            joinRed();
        }
        if (status == -3) {
            walkToTeams();
        }
        if (status == -2) {
            if (random(1, 2) == 1) {
                openTab(random(1, 8));
                wait(random(5300, 20100));
            }
            if (random(1, 2) == 2) {
                openTab(random(1, 8));
                wait(random(9200, 49000));
            }
        }
        if (status == -1) {
            if (random(1, 2) == 1) {
                openTab(random(1, 8));
                wait(random(5300, 20100));
            }
            if (random(1, 2) == 2) {
                openTab(random(1, 8));
                wait(random(9200, 49000));
            }
        }
        if (status == 0) {
            wait(random(300, 500));
        }
        if (status == 1) {
            leaveBlueStart();
        }
        if (status == 2) {
            leaveBlueGrave();
        }
        if (status == 3) {
            leaveRedGrave();
        }
        if (status == 4) {
            team = 1;
            position();
            enable(2);
            doTry = 0;
            while (!inRectangle(1833, 3236, 1862, 3258) && status == 4 && doTry < 45 && dead == 0) {
                position();
                walkPath(BlueStartToBlueFiends, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 5) {
            position();
            enable(2);
            doTry = 0;
            while (!inRectangle(1833, 3236, 1862, 3258) && status == 5 && doTry < 45 && dead == 0) {
                position();
                walkPath(BlueGraveToBlueFiends, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 6) {
            position();
            enable(2);
            doTry = 0;
            while (!inRectangle(1833, 3236, 1862, 3258) && status == 6 && doTry < 45 && dead == 0) {
                position();
                walkPath(RedGraveToBlueFiends, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 7) {
            leaveRedStart();
        }
        if (status == 8) {
            position();
            enable(2);
            doTry = 0;
            while (!inRectangle(1916, 3201, 1937, 3221) && status == 8 && doTry < 45 && dead == 0) {
                position();
                walkPath(RedStartToRedFiends, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 9) {
            position();
            enable(2);
            doTry = 0;
            while (!inRectangle(1916, 3201, 1937, 3221) && status == 9 && doTry < 45 && dead == 0) {
                position();
                walkPath(RedGraveToRedFiends, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 10) {
            position();
            enable(2);
            doTry = 0;
            while (!inRectangle(1916, 3201, 1937, 3221) && status == 10 && doTry < 45 && dead == 0) {
                position();
                walkPath(BlueGraveToRedFiends, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 14) {
            position();
            doTry = 0;
            while (!inRectangle(1883, 3243, 1910, 3259) && status == 14 && doTry < 45 && dead == 0) {
                position();
                walkPath(BlueFiendsToBlueJellies, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                if (inRectangle(1883, 3243, 1910, 3259)) {
                    status = 0;
                }
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 15) {
            position();
            doTry = 0;
            while (!inRectangle(1883, 3243, 1910, 3259) && status == 15 && doTry < 45 && dead == 0) {
                position();
                walkPath(ObeliskToBlueJellies, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 16) {
            position();
            doTry = 0;
            while (!inRectangle(1871, 3200, 1895, 3214) && status == 16 && doTry < 45 && dead == 0) {
                position();
                walkPath(RedFiendsToRedJellies, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                if (inRectangle(11871, 3200, 1895, 3214)) {
                    status = 0;
                }
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 17) {
            position();
            doTry = 0;
            while (!inRectangle(1871, 3200, 1895, 3214) && status == 15 && doTry < 45 && dead == 0) {
                position();
                walkPath(ObeliskToRedJellies, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 20) {
            position();
            doTry = 0;
            while (!inRectangle(1833, 3236, 1862, 3258) && status == 20 && doTry < 45 && dead == 0) {
                position();
                walkPath(ObeliskToBlueFiends, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 21) {
            position();
            doTry = 0;
            while (!inRectangle(1916, 3201, 1937, 3221) && status == 21 && doTry < 45 && dead == 0) {
                position();
                walkPath(ObeliskToRedFiends, 2);
                wait(random(300, 500));
                position();
                checkOutside();
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 22) {
            enterFragments();
            wait(random(300, 500));
        }
        if (status == 23) {
            doTry = 0;
            while (!inRectangle(1883, 3228, 1890, 3235) && doTry < 45 && dead == 0) {
                walkToObelisk();
                wait(random(300, 500));
                doTry += 1;
            }
            wait(random(300, 500));
        }
        if (status == 100) {
            enable(1);
            attackPyrefiends();
            wait(random(100, 200));
        }
        if (status == 101) {
            enable(3);
            attackJellies();
            wait(random(100, 200));
        }
        if (status == 102) {
            attackPeople();
            wait(random(100, 200));
        }
        if (status == 103) {
            specialAttack();
            wait(random(100, 200));
        }
        if (status == 999) {
            rotate();
            if (team == 1) {
                walkPath(BlueStartToBlueFiends, 2);
                walkPath(BlueGraveToBlueFiends, 2);
                walkPath(RedGraveToBlueFiends, 2);
                walkPath(ObeliskToBlueFiends, 2);
            }
            if (team == 2) {
                walkPath(RedStartToRedFiends, 2);
                walkPath(RedStartToRedFiends, 2);
                walkPath(BlueGraveToRedFiends, 2);
                walkPath(ObeliskToRedFiends, 2);
            }
        }
        if (status != -2 && status != -1 && status != 1 && status != 2 && status != 3 && status != 7) {
            pickBones();
            pickFragments();
        }
        if (b()) {
            c();
        }
    }

    public void attackPyrefiends() {
        RSNPC pyrefiend = getNearestNPCByID(pyrefiendID);
        if (clickTheCharacter(pyrefiend, "Pyrefiend", "attack pyrefiend")) {
            wait(random(500, 1000));
        }
    }

    public void attackJellies() {
        RSNPC jelly = getNearestNPCByID(jellyID);
        if (clickTheCharacter(jelly, "Jelly", "attack jelly")) {
            wait(random(500, 1000));
        }
    }

    public boolean attackPeople() {
        fail = 0;
        try {
            AllPlayers = Bot.getClient().getRSPlayerIndexArray();
            org.rsbot.accessors.RSPlayer[] players = Bot.getClient().getRSPlayerArray();

            for (int tryIt : AllPlayers) {
                thePlayer = new RSPlayer(players[tryIt]);
                if (players[tryIt] == null) {
                    continue;
                }
                status = 105;
                playerLocation = thePlayer.getLocation();
                Point targetLoc = Calculations.tileToScreen(playerLocation);
                playerName = thePlayer.getName();
                if (thePlayer.isInCombat()) {
                    continue;
                }
                if (getMyPlayer().getName() == thePlayer.getName()) {
                    continue;
                }
                int targY = playerLocation.getY();
                int targX = playerLocation.getX();
                int goodYes = 0;
                if (targX >= 1877 && targX <= 1904 && targY >= 3214 && targY <= 3241) {
                    goodYes = 1;
                }
                if (goodYes != 1) {
                    continue;
                }
                if (thePlayer.getTeam() == getMyPlayer().getTeam()) {
                    continue;
                }
                if (getMyPlayer().isInCombat()) {
                    break;
                }
                if (getMyPlayer().getInteracting() == null) {
                    if (!getMyPlayer().isInCombat()) {
                        if (!pointOnScreen(targetLoc) || distanceTo(playerLocation) < 6) {
                            status = 106;
                            walkTileMM(new RSTile(playerLocation.getX() + random(-2, 2), playerLocation.getY() + random(-2, 2)));
                            wait(random(1500, 2000));
                        }
                        while (getMyPlayer().isMoving()) {
                            wait(random(300, 500));
                        }
                        if (distanceTo(playerLocation) < 7) {
                            playerLocation = thePlayer.getLocation();
                            targetLoc = Calculations.tileToScreen(playerLocation);
                            status = 106;
                            moveMouse(targetLoc, 5, 5);
                            fail = 0;
                            wait(random(50, 100));
                            if (getMenuItems().get(0).contains("Attack " + playerName)) {
                                clickMouse(true);
                            }
                            if (!getMenuItems().get(0).contains("Attack " + playerName)) {
                                clickMouse(false);
                                wait(random(30, 50));
                                atMenu("Attack " + playerName);
                            }
                            wait(random(1500, 2000));
                            if (getMyPlayer().getInteracting() != null) {
                                fail += 1;
                            }
                        }
                    }
                }
                if (getMyPlayer().isInCombat()) {
                    break;
                }
                if (fail > 25) {
                    break;
                }
                if (getMyPlayer().isMoving()) {
                    break;
                }
                if (getMyPlayer().isInCombat()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean useSpecial() {
        if (SpecialAttack = true) {
            return true;
        }
        return false;
    }

    public int getSpec() {
        return getSetting(300) / 10;
    }

    public boolean enabledSpec() {
        return (getSetting(301)) == 1;
    }

    public void rotate() {
        if (random(1, 7) == 1) {
            char direction = 37;
            if (random(1, 2) == 2) {
                direction = 39;
            }
            Bot.getInputManager().pressKey(direction);
            wait(random(150, 1500));
            Bot.getInputManager().releaseKey(direction);

        }
    }

    public void enterFragments() {
        doTry = 0;
        while (getInventoryCount(fragmentID) > 0 && obeliskTeam == team && status == 22 && doTry < 45) {
            if (!inRectangle(1883, 3228, 1890, 3235)) {
                walkToObelisk();
            }
            checkOutside();
            hoverItem(fragmentID);
            wait(random(5, 12));
            clickMouse(true);
            wait(random(200, 300));
            checkOutside();
            if (b()) {
                c();
            }
            int randomTile = random(0, Obelisk.length);
            AonTile(Obelisk[randomTile], "ment -> Soul");
            checkOutside();
            wait(random(500, 1000));
            checkOutside();
            doTry += 1;
        }
        if (getInventoryCount(fragmentID) == 0 && soulRandom == 1) {
            turnInSouls = 0;
        }
    }

    public void walkToObelisk() {
        if (!checkOutside()) {
            if (inRectangle(1883, 3228, 1890, 3235)) {
                enterFragments();
            }
            if (!inRectangle(1883, 3228, 1890, 3235)) {
                status = 18; //walk to obelisk from blue jellies
                walkPath(BlueFiendsToObelisk, 1);
                status = 19; //walk to obelisk from red jellies
                walkPath(RedFiendsToObelisk, 1);
                status = 24; //walk to obelisk from blue pyrefiends
                walkPath(BlueJelliesToObelisk, 1);
                status = 25; //walk to obelisk from red pyrefiends
                walkPath(RedJelliesToObelisk, 1);
            }
        }
    }

    public void enable(int place) {
        //0 Never
        //1 Pyrefiends
        //2 Starting area
        //3 Jellies
        if (place == specialAttack) {
            specialAttack();
        }
        if (place == quickPray) {
            quickPray();
        }
    }

    public void quickPray() {
        if (quickPray != 0 && prayEnabled == 0) {
            status = 12;
            moveMouse(random(725, 756), random(65, 80));
            wait(random(150, 190));
            clickMouse(true);
            wait(random(500, 900));
            prayEnabled = 1;
        }
    }

    public boolean specialAttack() {
        try {
            if (useSpecial()) {
                doTry = 0;
                while (!enabledSpec() && getSpec() >= specAmount && doTry < 45) {
                    while (getCurrentTab() != Constants.TAB_ATTACK) {
                        openTab(Constants.TAB_ATTACK);
                        wait(random(400, 900));
                    }
                    if (getCurrentTab() == Constants.TAB_ATTACK && !enabledSpec() && getSpec() >= specAmount) {
                        moveMouse(random(578, 705), random(414, 426));
                        wait(random(150, 190));
                        d();
                        if (getCurrentTab() == Constants.TAB_ATTACK && !enabledSpec() && getSpec() >= specAmount) {
                            clickMouse(true);
                            wait(random(1300, 1600));
                        }
                    }
                    doTry += 1;
                }
                wait(random(300, 500));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean checkOutside() {
        if (inRectangle(1850, 3130, 1920, 3190)) {
            status = -3;
            return true;
        }
        return false;
    }

    public void clickInterfaces() {
        while (RSInterface.getChildInterface(211, 3).getAbsoluteX() > 20
                || RSInterface.getChildInterface(228, 2).isValid()) {
            status = -6;
            if (RSInterface.getChildInterface(211, 1).isValid()) {
                if (RSInterface.getChildInterface(211, 1).containsText("early")) {
                    wait(random(5000, 10000));
                }
            }
            if (RSInterface.getChildInterface(211, 3) != null) {
                clickMouse(190 + random(0, 140), 445 + random(0, 15), true);
            }
            wait(random(800, 1000));
            if (RSInterface.getChildInterface(228, 2) != null) {
                clickMouse(220 + random(0, 80), 395 + random(0, 15), true);
            }
            wait(random(800, 1000));
        }
    }

    public void c() {
        stopScript();
    }

    public void walkToTeams() {
        gameCounted = 0;
        lastJoined = toJoin;
        toJoin = joinTeam;
        if (toJoin == 3) {
            if (lastWon != 0) {
                toJoin = lastWon;
            }
        }
        if (toJoin == 4) {
            if (lastLost != 0) {
                toJoin = lastLost;
            }
        }
        if (toJoin == -1) {
            if (lastJoined == -5) {
                lastJoined = random(1, 2);
            }
            if (lastJoined == 1) {
                toJoin = 3;
            }
            if (lastJoined == 2) {
                toJoin = 1;
            }
            if (toJoin == 3) {
                toJoin = 2;
            }
        }
        if (toJoin == 0) {
            toJoin = random(1, 2);
        }
        if (toJoin == 1) {
            doTry = 0;
            while (!inRectangle(1880, 3160, 1883, 3164) && doTry < 45) {
                walkPath(SoulWarsLobbyToBlueLobby, 1);
                setCompass('w');
                wait(random(200, 400));
                doTry += 1;
            }
        }
        if (toJoin == 2) {
            doTry = 0;
            while (!inRectangle(1896, 3160, 1899, 3164) && doTry < 45) {
                walkPath(SoulWarsLobbyToRedLobby, 1);
                setCompass('e');
                wait(random(200, 400));
                doTry += 1;
            }
        }
    }

    public void joinBlue() {
        dead = 0;
        setCompass('w');
        if (!getMyPlayer().isMoving()) {
            myatTile(new RSTile(1879, 3162), "Pass");
            wait(random(300, 500));
        }
    }

    public void joinRed() {
        dead = 0;
        setCompass('e');
        if (!getMyPlayer().isMoving()) {
            myatTile(new RSTile(1900, 3162), "Pass");
            wait(random(300, 500));
        }
    }

    public void leaveRedGrave() {
        dead = 0;
        setCompass('s');
        myatTile(new RSTile(1933, 3243), "Pass");
        wait(random(800, 1500));
    }

    public void leaveBlueGrave() {
        dead = 0;
        setCompass('n');
        myatTile(new RSTile(1842, 3220), "Pass");
        wait(random(800, 1500));
    }

    public void leaveRedStart() {
        dead = 0;
        setCameraRotation(random(260, 280));
        if (distanceTo(new RSTile(1959, 3239)) > 3) {
            walkTileMM(new RSTile(1958, 3239));
        } else if (distanceTo(new RSTile(1960, 3239)) <= 4) {
            myatTile(new RSTile(1959, 3239), "Pass");
            wait(random(800, 1500));
        }
    }

    public void leaveBlueStart() {
        dead = 0;
        setCameraRotation(random(80, 100));
        if (distanceTo(new RSTile(1815, 3225)) > 3) {
            walkTileMM(new RSTile(1816, 3225));
        } else if (distanceTo(new RSTile(1815, 3225)) <= 4) {
            myatTile(new RSTile(1815, 3225), "Pass");
            wait(random(800, 1500));
        }
    }

    public boolean pickFragments() {
        try {
            Feva = getNearestGroundItemByID(new int[]{fragmentID});
            if (Feva != null && pickupFragments == 1) {
                fragmentCount = getInventoryCount(fragmentID);
                doTry = 0;
                while (getInventoryCount(fragmentID) == fragmentCount && tileOnScreen(Feva) && Feva != null && pickupFragments == 1 && doTry < 10 && getInventoryCount() < 28) {
                    status = 11;
                    Feva = getNearestGroundItemByID(new int[]{fragmentID});
                    if (!getMyPlayer().isMoving()) {
                        atTile(Feva, "Take Soul Fragment");
                        wait(random(700, 1100));
                    }
                    if (getMyPlayer().isMoving()) {
                        wait(random(200, 300));
                    }
                    doTry += 1;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean b() {
        if (xpGotten != 4) {
            return true;
        } else {
            return false;
        }
    }

    public boolean pickBones() {
        try {
            boneCount = getInventoryCount(boneID);
            theNum = 0;
            Feva = getNearestGroundItemByID(new int[]{boneID});
            if (Feva != null && bonePickup == 1 && needToPickupBones == 1) {
                doTry = 0;
                theNum = random(2, 4);
                while (getInventoryCount(boneID) - theNum < boneCount && tileOnScreen(Feva) && Feva != null && bonePickup == 1 && doTry < 10 && getInventoryCount() < 28 && needToPickupBones == 1) {
                    status = 26;
                    Feva = getNearestGroundItemByID(new int[]{boneID});
                    if (!getMyPlayer().isMoving()) {
                        atTile(Feva, "Take Bones");
                        wait(random(700, 1100));
                    }
                    if (getMyPlayer().isMoving()) {
                        wait(random(200, 300));
                    }
                    doTry += 1;
                }
            }
        } catch (Exception e) {
            return false;
        }
        if (getInventoryCount(boneID) > boneCount + theNum) {
            needToPickupBones = 0;
        }
        return true;
    }

    public int getMouseSpeed() {
        if (speed1 == 0 || speed2 == 0) {
            speed1 = 6;
            speed2 = 8;
        }
        return random(speed1, speed2);
    }

    public boolean inRectangle(int x1, int y1, int x2, int y2) {
        position();
        if (getX >= x1 && getX <= x2 && getY >= y1 && getY <= y2) {
            return true;
        }
        return false;
    }

    public void walkPath(RSTile[] path, int distance) {
        position();
        if (b()) {
            c();
        }
        if (!getMyPlayer().isMoving() || distanceTo(getDestination()) <= random(7, 11)) {
            walkPathMM(randomizePath(path, distance, distance), 17);
            rotate();
            if (getEnergy() > random(50, 80)) {
                setRun(true);
            }
            wait(random(20, 30));
            return;
        } else {
            rotate();
            wait(random(50, 100));
        }
    }

    public void position() {
        getX = getMyPlayer().getLocation().getX();
        getY = getMyPlayer().getLocation().getY();
        if (b()) {
            c();
        }
    }

    private boolean hoverItem(final int itemID) {
        try {
            if (getCurrentTab() != Constants.TAB_INVENTORY
                    && !RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()
                    && !RSInterface.getInterface(Constants.INTERFACE_STORE).isValid()) {
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

    public void d() {
        if (b()) {
            c();
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
                wait(random(200, 300));
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
        if (slot < 0) {
            throw new IllegalArgumentException("slot < 0 " + slot);
        }

        RSInterfaceComponent item = bank.getItem(slot);
        if (item != null) {
            return item.getPosition();
        }

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
                if (getMenuItems().get(0).toLowerCase().contains(action)) {
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

    public boolean myatTile(final RSTile tile, String action) {
        try {
            Point location = Calculations.tileToScreen(tile);
            if (location.x == -1 || location.y == -1) {
                return false;
            }
            moveMouse(location, 3, 3);
            wait(random(500, 1000));
            if (getMenuItems().get(0).toLowerCase().contains(
                    action.toLowerCase())) {
                clickMouse(true);
                wait(random(1000, 2000));
            } else {
                clickMouse(false);
                if (!atMenu(action)) {
                    return false;
                }
            }
            wait(random(500, 1000));
            while (true) {
                if (!getMyPlayer().isMoving()) {
                    break;
                }
                wait(random(500, 1000));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static float Round(float Rval, int Rpl) {
        float p = (float) Math.pow(10, Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float) tmp / p;
    }
}
