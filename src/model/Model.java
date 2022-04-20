package model;

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import static java.lang.Integer.max;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Model {

    protected int level;
    protected Player players[];
    protected int round;
    protected ArrayList<Sprite> terrain;
    protected char[][] position;
    protected int size;
    private int activePlayer;
    private ArrayList<Sprite> selectables;
    private int castleCoordinates[][] = new int[8][2];
    private int selectedMap;
    private int mcount;
    private int lcount;

    /**
     * Storing the field components in a matrix in order to make it easier to
     * decide which component is at a given index F - field C - Castle L - Lake
     * M - Mountain T - Tower U - Unit
     */
    private final Image Lake = new ImageIcon("src/res/Lake.png").getImage();
    private final Image Mountain = new ImageIcon("src/res/Mountain.png").getImage();
    private final Image Castle = new ImageIcon("src/res/Castle.png").getImage();

    public Model(int selectedMap, String p1Name, String p2Name, int size) {
        /**
         * Setting up terrain and variables
         */
        this.terrain = new ArrayList<>();
        this.selectables = new ArrayList<>();
        this.size = size;
        position = new char[30][30];

        this.players = new Player[2];
        this.players[0] = new Player(1000, p1Name);
        this.players[1] = new Player(1000, p2Name);
        this.selectedMap = selectedMap;
        generateTerrain(selectedMap);
    }

    public Model() {
        this.terrain = new ArrayList<>();
        this.selectables = new ArrayList<>();
        position = new char[30][30];
        this.players = new Player[2];
    }

    public Model(int size) {
        this.terrain = new ArrayList<>();
        this.selectables = new ArrayList<>();
        position = new char[30][30];
        this.size = size;
        this.players = new Player[2];
    }

    /**
     * Játékteren a terep leképzése
     */
    private void generateTerrain(int selectedMap) {
        /**
         * Filling matrix with Field type Sprites
         */
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                position[i][j] = 'F';
            }
        }

        /**
         * Generate the position of the castles and add them to the terrain
         */
        Random rand = new Random();
        int randPos = rand.nextInt(25) + 2;
        position[randPos][2] = 'C';
        position[randPos + 1][2] = 'C';
        position[randPos][3] = 'C';
        position[randPos + 1][3] = 'C';

        //castles' size is 2x2
        Castle c1 = new Castle("blue", randPos * (size / 30), 2 * (size / 30), (size / 15), (size / 15), Castle, 300);
        terrain.add(c1);
        players[0].setCastle(c1);

        randPos = rand.nextInt(25) + 2;
        position[randPos][27] = 'C';
        position[randPos + 1][27] = 'C';
        position[randPos][26] = 'C';
        position[randPos + 1][26] = 'C';

        Castle c2 = new Castle("red", randPos * (size / 30), 26 * (size / 30), (size / 15), (size / 15), Castle, 300);
        terrain.add(c2);
        players[1].setCastle(c2);

        //store the coordinates of the castles
        setCastleCords(c1, c2);

        /**
         * Generate other terrain components depending on given map index 1 -
         * Mountain and lakes mixed 2 - Mountains in majority 3 - Lakes in
         * majority
         */
        /**
         * Amount of mountains and lakes
         */

        if (selectedMap == 1) {         //Mountains and lakes in the same amount
            mcount = rand.nextInt(10) + 15;
            lcount = rand.nextInt(10) + 15;
        } else if (selectedMap == 2) {  //Mountains in majority
            mcount = rand.nextInt(5) + 30;
            lcount = rand.nextInt(5) + 5;
        } else {                        //Lakes in majority
            mcount = rand.nextInt(5) + 5;
            lcount = rand.nextInt(5) + 30;
        }

        isInRangeOfCastle(mcount, c1, c2, "mountain");
        isInRangeOfCastle(lcount, c1, c2, "lake");
    }

    public void setCastleCords(Castle c1, Castle c2) {
        castleCoordinates[0][0] = c1.x / (size / 30);
        castleCoordinates[0][1] = c1.y / (size / 30);
        castleCoordinates[1][0] = (c1.x / (size / 30)) + 1;
        castleCoordinates[1][1] = c1.y / (size / 30);
        castleCoordinates[2][0] = c1.x / (size / 30);
        castleCoordinates[2][1] = (c1.y / (size / 30)) + 1;
        castleCoordinates[3][0] = (c1.x / (size / 30)) + 1;
        castleCoordinates[3][1] = (c1.y / (size / 30)) + 1;
        castleCoordinates[4][0] = c2.x / (size / 30);
        castleCoordinates[4][1] = c2.y / (size / 30);
        castleCoordinates[5][0] = (c2.x / (size / 30)) + 1;
        castleCoordinates[5][1] = c2.y / (size / 30);
        castleCoordinates[6][0] = c2.x / (size / 30);
        castleCoordinates[6][1] = (c2.y / (size / 30)) + 1;
        castleCoordinates[7][0] = (c2.x / (size / 30)) + 1;
        castleCoordinates[7][1] = (c2.y / (size / 30)) + 1;
    }

    public int[][] getCastleCoordinates() {
        return castleCoordinates;
    }

    private void isInRangeOfCastle(int count, Castle c1, Castle c2, String tr) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int x, y;
            do {
                x = rand.nextInt(30);
                y = rand.nextInt(30);
            } while (position[x][y] != 'F'
                    || distance(x, c1.x / (size / 30), y, c1.y / (size / 30)) < 4
                    || distance(x, c1.x / (size / 30) + 1, y, c1.y / (size / 30)) < 4
                    || distance(x, c1.x / (size / 30), y, c1.y / (size / 30) + 1) < 4
                    || distance(x, c1.x / (size / 30) + 1, y, c1.y / (size / 30) + 1) < 4
                    || distance(x, c2.x / (size / 30), y, c2.y / (size / 30)) < 4
                    || distance(x, c2.x / (size / 30) + 1, y, c2.y / (size / 30)) < 4
                    || distance(x, c2.x / (size / 30), y, c2.y / (size / 30) + 1) < 4
                    || distance(x, c2.x / (size / 30) + 1, y, c2.y / (size / 30) + 1) < 4);

            /**
             * lakes and mountains can't be generated in the 3 block radius of
             * the castles no component can be generated into a location where
             * there's a component already
             *
             * castles' coordinates only show the left top corner, so the other
             * 3 neighbour coordinates' distance need to be calculated too
             */
            if (tr.equals("mountain")) {
                position[x][y] = 'M';
                terrain.add(new Mountain(x * (size / 30), y * (size / 30), (size / 30), (size / 30), Mountain));
            } else {
                position[x][y] = 'L';
                terrain.add(new Lake(x * (size / 30), y * (size / 30), (size / 30), (size / 30), Lake));
            }
        }
    }

    /**
     * distance is calculated by the block-distance, not the absolute
     */
    private int distance(int x1, int x2, int y1, int y2) {
        return max(abs(x1 - x2), abs(y1 - y2));
    }

    /**
     * returns how much damage would be dealt to the Unit by enemy Towers at the
     * (x,y) position returns 0 if there's no enemy Tower in range
     */
    //simulates if Tower is placed -> is there a way between the two Castles or not & is there a way from units to castles
    public boolean placable(int x, int y, int matrix[][]) {
        int newMatrix[][] = matrix;

        //there's something already in this position
        if (newMatrix[x][y] != 1 || isThereUnit(x, y)) {
            return false;
        }

        newMatrix[x][y] = 1000;

        ArrayList<String> wayString;
        /*for (int q = 0; q < 2; q++) {
            ArrayList<Unit> units = players[q].getUnits();
            for (int i = 0; i < 4; i++) {
                for (Unit u : units) {
                    wayString = players[activePlayer].findWay(u.x / (size / 30),
                            u.y / (size / 30),
                            castleCoordinates[activePlayer * 4 + i][0],
                            castleCoordinates[activePlayer * 4 + i][1], newMatrix);
                    if (wayString == null || wayString.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        */

        //check all possible start and destination coordinates (Castle1, Castle2)
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                wayString = players[activePlayer].findWay(castleCoordinates[j][0],
                        castleCoordinates[j][1],
                        castleCoordinates[k + 4][0], castleCoordinates[k + 4][1], newMatrix);
                if (wayString != null && wayString.size() > 0) {
                    return true; //we found a way
                }
            }
        }

        return false;
    }

    /**
     * save terrain ans players' data to txt line by line with separator the way
     * of the units won't be saved, it will re-generated after loading the save
     */
    public void saveData(String filename, int ticks) {
        try ( Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename + ".txt"), "utf-8"))) {

            for (int i = 0; i < 30; i++) {
                for (int j = 0; j < 30; j++) {
                    writer.write(position[j][i]);
                }
                writer.write(System.getProperty("line.separator"));
            }

            writer.write("" + selectedMap + "\n");
            writer.write("" + activePlayer + "\n");
            writer.write("" + round + "\n");
            writer.write("" + ticks);
            writer.write(System.getProperty("line.separator"));

            for (int i = 0; i < 2; i++) {
                writer.write("p:\n");
                writer.write(players[i].getName() + "\n");
                writer.write("" + players[i].getMoney() + "\n");
                writer.write("" + players[i].getCastle().getHp());

                writer.write(System.getProperty("line.separator"));

                if (players[i].getUnits() != null) {
                    for (Unit u : players[i].getUnits()) {
                        writer.write("U " + colorString(u.color) + " " + u.x + " " + u.y + " " + u.width + " " + u.height + " " + u.getType()
                                + " " + u.getHp());
                        writer.write(System.getProperty("line.separator"));
                    }
                }

                if (players[i].getTowers() != null) {
                    for (Tower t : players[i].getTowers()) {
                        //constructor: String type, String scolor, int power, int range, double attack_speed, int hp, int level, int x, int y, int height, int width, Image img
                        writer.write("T " + t.getType() + " " + colorString(t.color) + " " + t.getPower() + " " + t.getRange() + " " + t.getAttack_speed() + " "
                                + t.getHp() + " " + t.getLevel() + " " + t.x + " " + t.y + " " + t.height + " " + t.width + " " + t.getDemolishedIn());
                        writer.write(System.getProperty("line.separator"));
                    }
                }
                writer.write(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File not found!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    /**
     * options are only red and blue
     */
    private String colorString(Color c) {
        if (c == Color.RED) {
            return "red";
        }
        return "blue";
    }

    //return the difficulty of the whole way
    public int wayDiff(int actPl, ArrayList<String> al, String type) {
        int d = 0;
        int diff[][] = players[actPl].getDifficulty(this, type, actPl);

        for (String s : al) {
            int x = parseInt(s.split(";")[0]);
            int y = parseInt(s.split(";")[0]);
            d += diff[x][y];
        }

        return d;
    }

    public void setSelectables() {
        for (int i = 0; i < 30; i++) {
            for (int j = (activePlayer + 1) * 15 - 1; j >= activePlayer * 15; j--) {
                if (position[i][j] == 'F' && placable(i, j, players[activePlayer].getDifficulty(this, "General", activePlayer))) {
                    selectables.add(new Selectable(i * (size / 30), j * (size / 30),
                            (size / 30), (size / 30)));
                }
            }
        }
    }
    
    public boolean isItEnemyCastleCoordinate(int actPl,int x, int y)////0<=x,y<30
    {
        int enemy=(actPl+1)%2;
        for(int i=enemy*4;i<(enemy+1)*4;i++){
            if(castleCoordinates[i][0]==x&&castleCoordinates[i][1]==y){
                return true;
            }
        }
            
        return false;
    }

    private boolean isThereUnit(int x, int y) ////0<=x,y<30
    {
        for (int q = 0; q < 2; q++) {
            ArrayList<Unit> units = players[q].getUnits();
            for (int i = 0; i < units.size(); i++) {
                if (units.get(i).x / (size / 30) == x && units.get(i).y / (size / 30) == y) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean upgradable(int x, int y) ////0<=x,y<30
    {
        return getTowerFromPosition(x, y).getLevel() < 3;
    }

    public void setSelectableTowersToUpgrade(int money) {
        for (int i = 0; i < 30; i++) {
            for (int j = activePlayer * 15; j < activePlayer * 15 + 15; j++) {
                if (position[i][j] == 'T' && upgradable(i, j) && money >= getTowerFromPosition(i, j).getUpgradePrice()) {
                    //System.out.println(money+", "+getTowerFromPosition(i,j).getPrice());
                    selectables.add(new Selectable(i * (size / 30), j * (size / 30),
                            (size / 30), (size / 30)));
                }
            }
        }
    }

    public void setSelectableTowersToDemolish() {

        for (int i = 0; i < 30; i++) {
            for (int j = activePlayer * 15; j < activePlayer * 15 + 15; j++) {
                if (position[i][j] == 'T') {
                    selectables.add(new Selectable(i * (size / 30), j * (size / 30),
                            (size / 30), (size / 30)));
                }
            }
        }
    }
    
  

    public String getInfo(int x, int y) {
        String info = "";
        boolean found = false;

        for (int i = 0; i < players[activePlayer].getNotDemolishedTowers().size() && !found; i++) {
            Tower t = players[activePlayer].getNotDemolishedTowers().get(i);
            if (t.x / (size / 30) == x && t.y / (size / 30) == y) {
                info = "<html><font face=\"sansserif\" color=\"black\">Tower type: " + t.type + "<br>level: " + t.level + "<br>hp: " + t.hp + ""
                        + "<br>attack speed: " + t.attack_speed + "<br>power: " + t.power + "<br>range: " + t.range + "</font></html>";
                found = true;

            }
        }

        ArrayList<Unit> units = new ArrayList<Unit>();
        for (Unit u : players[activePlayer].getUnits()) {
            if (u.getX() / (size / 30) == x && u.getY() / (size / 30) == y) {
                units.add(u);
            }
        }

        if (units.size() == 1) {
            units.get(0).setX(x * (size / 30));
            info += "<html><font face=\"sansserif\" color=\"black\">Unit type: " + units.get(0).type + "<br>hp: " + units.get(0).hp + ""
                    + "<br>distance: " + units.get(0).distance + "<br>power: " + units.get(0).power + "</font></html>";
            return info;
        } else if (units.size() > 1) {
            int showUnits = 5;
            if (units.size() < 5) {
                showUnits = units.size();
            }
            for (int i = 0; i < showUnits; i++) {
                units.get(i).setX(x * (size / 30) + i);
            }
            ArrayList<String> types = new ArrayList<String>();
            String typesStr = "";
            int sumPower = 0;
            int sumHp = 0;
            for (Unit u : units) {
                if (!types.contains(u.type)) {
                    types.add(u.type);
                }
                sumPower += u.power;
                sumHp += u.hp;

            }
            typesStr = types.get(0);
            for (int i = 1; i < types.size(); i++) {
                typesStr += ", " + types.get(i);
            }
            info += "<html><font face=\"sansserif\" color=\"black\">" + units.size() + " units<br>Types: " + typesStr + "<br>Sum power: " + sumPower + "<br>Sum hp: " + sumHp + "</font></html>";
            return info;
        }

        for (int i = 0; i < 4; i++) {

            if (x == castleCoordinates[i][0] && y == castleCoordinates[i][1]) {
                info = "<html><font face=\"sansserif\" color=\"black\">" + players[0].getName() + "'s castle<br>hp: "
                        + players[0].getCastle().getHp() + "</font></html>";
            } else if (x == castleCoordinates[i + 4][0] && y == castleCoordinates[i + 4][1]) {
                info = "<html><font face=\"sansserif\" color=\"black\">" + players[1].getName() + "'s castle<br>hp: "
                        + players[1].getCastle().getHp() + "</font></html>";
            }
        }

        return info;
    }

    public ArrayList<Tower> towersNearby(int actPlayer, Unit u) {
        ArrayList<Tower> towersNB = new ArrayList<>();
        ArrayList<Tower> enemyTowers = players[(actPlayer + 1) % 2].getTowers();
        //System.out.println("enemy towers: "+enemyTowers.toString());
        if (enemyTowers.size() > 0) {
            for (Tower t : enemyTowers) {
                            //System.out.println(t.getDemolishedIn());

               /* if (distance(u.x / (size / 30), t.x / (size / 30), u.y / (size / 30), t.y / (size / 30)) == 1 && t.demolishedIn == -1) {
                    towersNB.add(t);
                }*/
               if (distance(u.x / (size / 30), t.x / (size / 30), u.y / (size / 30), t.y / (size / 30)) == 1 && position[t.x / 30][t.y / 30] == 'T') {
                    towersNB.add(t);
               }
            }
        }

        return towersNB;
    }

    public ArrayList<Unit> enemyUnitsNearby(int actPlayer, Unit u) {
        ArrayList<Unit> enemyUnitsNB = new ArrayList<>();
        ArrayList<Unit> enemyUnits = players[(actPlayer + 1) % 2].getUnits();

        if (enemyUnits.size() > 0) {
            for (Unit eu : enemyUnits) {
                //System.out.println(distance(u.x/(size/30),t.x/(size/30),u.y/(size/30),t.y/(size/30)));
                if (distance(u.x / (size / 30), eu.x / (size / 30), u.y / (size / 30), eu.y / (size / 30)) == 0) {
                    enemyUnitsNB.add(eu);
                }
            }
        }
        return enemyUnitsNB;
    }

    public ArrayList<Unit> enemyUnitsNearby(int actPlayer, Tower t) {
        ArrayList<Unit> enemyUnitsNB = new ArrayList<>();
        ArrayList<Unit> enemyUnits = players[(actPlayer + 1) % 2].getUnits();

        if (enemyUnits.size() > 0) {
            for (Unit eu : enemyUnits) {
                //System.out.println(distance(u.x/(size/30),t.x/(size/30),u.y/(size/30),t.y/(size/30)));
                if (distance(t.x / (size / 30), eu.x / (size / 30), t.y / (size / 30), eu.y / (size / 30)) <= t.range) {
                    enemyUnitsNB.add(eu);
                }
            }
        }
        return enemyUnitsNB;

    }

    public Tower getTowerFromPosition(int x, int y) {
        //0<=x,y<30
        for (int q = 0; q < 2; q++) {
            for (Tower t : players[q].getTowers()) {
                if (t.getX() / (size / 30) == x && t.getY() / (size / 30) == y) {
                    return t;
                }
            }
        }
        return null;
    }

    public ArrayList<Sprite> getSelectables()           {return selectables; }
    public void setSelectables(ArrayList<Sprite> sc)    { selectables = sc; }
    public void setMap(int sc)                          { selectedMap = sc; }
    public int getMap()                                 { return selectedMap; }
    public boolean roundOver()                          { return false; }
    public void unitAttack()                            {  }
    public void towerDefense()                          {  }
    public int getLevel()                               { return level; }
    public void setLevel(int level)                     { this.level = level; }
    public Player[] getPlayers()                        { return players; }
    public int getActivePlayer()                        { return activePlayer; }
    public void setActivePlayer(int activePlayer)       { this.activePlayer = activePlayer; }
    public int getRound()                               { return round; }
    public void setRound(int round)                     { this.round = round; }
    public ArrayList<Sprite> getTerrain()               { return terrain; }
    public void addTerrainElement(Sprite newElement)    { terrain.add(newElement); }
    public char[][] getPosition()                       { return position; }
    public void setPosition(int x, int y, char newChar) { position[x][y] = newChar; }
    public int getSize()                                { return size; }
    public int getMcount()                              { return mcount; }
    public int getLcount()                              { return lcount; }
}
