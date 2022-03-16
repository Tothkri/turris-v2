package model;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import static java.lang.Integer.max;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
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

    /*
    Storing the field components in a matrix in order to make it easier to 
    decide which component is at a given index
    F - field
    C - Castle
    L - Lake
    M - Mountain
    T - Tower
    U - Unit
     */
    private final Image Lake = new ImageIcon("src/res/Lake.png").getImage();
    private final Image Unit = new ImageIcon("src/res/Unit.png").getImage();
    private final Image Mountain = new ImageIcon("src/res/Mountain.png").getImage();
    private final Image Tower = new ImageIcon("src/res/Tower.png").getImage();
    private final Image Castle = new ImageIcon("src/res/Castle.png").getImage();

    public Model(int selectedMap, String p1Name, String p2Name, int size) {
        /*
        Setting up terrain and variables
         */
        this.terrain = new ArrayList<>();
        this.selectables = new ArrayList<>();
        this.size = size;
        position = new char[30][30];

        this.players = new Player[2];
        this.players[0] = new Player(1000, p1Name);
        this.players[1] = new Player(1000, p2Name);
        generateTerrain(selectedMap);

    }

    private void generateTerrain(int selectedMap) {
        /*
        Filling matrix with Field type Sprites
         */
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                position[i][j] = 'F';
            }
        }

        /*
        Generate the position of the castles and add them to the terrain
         */
        Random rand = new Random();
        int randPos = rand.nextInt(25) + 2;
        position[randPos][2] = 'C';
        position[randPos + 1][2] = 'C';
        position[randPos][3] = 'C';
        position[randPos + 1][3] = 'C';
        //castles' size is 2x2
        Castle c1 = new Castle(randPos * (size / 30), 2 * (size / 30), (size / 15), (size / 15), Castle, 300);
        terrain.add(c1);
        players[0].setCastle(c1);

        randPos = rand.nextInt(25) + 2;
        position[randPos][27] = 'C';
        position[randPos + 1][27] = 'C';
        position[randPos][26] = 'C';
        position[randPos + 1][26] = 'C';
        Castle c2 = new Castle(randPos * (size / 30), 26 * (size / 30), (size / 15), (size / 15), Castle, 300);
        terrain.add(c2);
        players[1].setCastle(c2);

        /*
        Generate other terrain components depending on given map index
        
        1 - Mountain and lakes mixed
        2 - Mountains in majority
        3 - Lakes in majority
         */
        int mcount, lcount; //amount of mountains and lakes

        if (selectedMap == 1) //mountains and lakes in the same amount
        {
            mcount = rand.nextInt(10) + 15;
            lcount = rand.nextInt(10) + 15;
        } else if (selectedMap == 2) //mountains in majority
        {
            mcount = rand.nextInt(5) + 30;
            lcount = rand.nextInt(5) + 5;
        } else //lakes in majority
        {
            mcount = rand.nextInt(5) + 5;
            lcount = rand.nextInt(5) + 30;
        }

        isInRangeOfCastle(mcount, c1, c2, "mountain");
        isInRangeOfCastle(lcount, c1, c2, "lake");

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

            /*
                lakes and mountains can't be generated in the 3 block radius of the castles
                no component can be generated into a location where there's a component already
                
                castles' coordinates only show the left top corner, so the other 3 neighbour 
                coordinates' distance need to be calculated too
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

    private int distance(int x1, int x2, int y1, int y2) {
        //distance is calculated by the block-distance, not the absolute
        return max(abs(x1 - x2), abs(y1 - y2));
    }

    private HashMap<Integer, Integer> findWay(Castle from, Castle to) /* 
            stores the best way from the home castle to the enemy castle step to step as (x, y) pairs
            (units can't move in any diagonal directions)

     */ {
        int minDistance = 1000; // define it as a larger number than the count of fields
        HashMap<Integer, Integer> bestway = new HashMap<>();

        int fromx = from.x;
        int fromy = from.y; //left top corner of the home castle

        if (from.y == 2) //player 1 (upper)
        {

        } else //player2 (lower)
        {

        }

        return bestway;
    }

    public void saveData(String filename) /*
            save terrain ans players' data to txt line by line with separator
            
            the way of units won't be saved, it will re-generated after loading the save
     */ {
        try ( Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename + ".txt"), "utf-8"))) {

            for (int i = 0; i < 30; i++) {
                for (int j = 0; j < 30; j++) {
                    writer.write(position[j][i] + " ");

                }

                writer.write(System.getProperty("line.separator"));
            }

            writer.write("----------------");
            writer.write(System.getProperty("line.separator"));

            for (int i = 0; i < 2; i++) {
                writer.write(players[i].getName() + " " + players[i].getMoney());
                writer.write(System.getProperty("line.separator"));

                if (players[i].getUnits() != null) {
                    for (Unit u : players[i].getUnits()) {
                        writer.write("U " + u.x + " " + u.y + " " + u.width + " " + u.height + " " + u.getType() + " " + u.getDistance()
                                + " " + u.getPower() + " " + u.getHp() + " " + u.getPrice());
                        writer.write(System.getProperty("line.separator"));
                    }
                }
                if (players[i].getTowers() != null) {
                    for (Tower t : players[i].getTowers()) {
                        writer.write("T " + t.x + " " + t.y + " " + t.width + " " + t.height + " " + t.getType() + " "
                                + t.getPower() + " " + t.getRange() + " " + t.getAttack_speed() + " " + t.getHp() + " " + t.getPrice());
                        writer.write(System.getProperty("line.separator"));
                    }
                }

                writer.write("----------------");
                writer.write(System.getProperty("line.separator"));

            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File not found!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }

    }
    public void setSelectables(){
        for(int i = 0; i < 30;i++){
            for(int j = 0; j < 30; j++){
                if (j >= activePlayer * 15 && j < ((activePlayer + 1) * 15)
                        && position[i][j] == 'F')
                {
                     selectables.add(new Selectable(i * (size / 30),j * (size / 30),
                        (size / 30),(size / 30)));
                }
            }
        }
    }
    
    public ArrayList<Sprite> getSelectables(){return selectables;}
    public void setSelectables(ArrayList<Sprite> sc){selectables = sc;}
    public boolean isOver(){return false;}
    public boolean roundOver(){return false;}
    public void unitAttack(){ }
    public void towerDefense() {}
    public int getLevel(){return level;}
    public void setLevel(int level){this.level = level;}
    public Player[] getPlayers(){return players;}
    public int getActivePlayer(){return activePlayer;}
    public void setActivePlayer(int activePlayer){this.activePlayer = activePlayer;}
    public int getRound(){return round;}
    public void setRound(int round){this.round = round;}
    public ArrayList<Sprite> getTerrain(){return terrain;}
    public void addTerrainElement(Sprite newElement){terrain.add(newElement);}
    public char[][] getPosition(){return position;}
    public void setPosition(int x, int y, char newChar){position[x][y] = newChar;}
    public int getSize(){return size;}
}
