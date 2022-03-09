package model;

import java.awt.Image;
import static java.lang.Integer.max;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.swing.ImageIcon;

public class Model {

    protected int level;
    protected Player player1;
    protected Player player2;
    protected int round;
    protected ArrayList<Sprite> terrain;
    protected char[][] position;
    protected int size;
   
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
        this.size = size;
        position = new char[30][30];
        this.player1=new Player(1000,p1Name);
        this.player2=new Player(1000,p2Name);  
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
        Castle c1 = new Castle(randPos * (size / 30), 2 * (size / 30), (size / 15), (size / 15), Castle);
        terrain.add(c1);
        player1.setCastle(c1);

        randPos = rand.nextInt(25) + 2;
        position[randPos][27] = 'C';
        position[randPos + 1][27] = 'C';
        position[randPos][26] = 'C';
        position[randPos + 1][26] = 'C';
        Castle c2 = new Castle(randPos * (size / 30), 26 * (size / 30), (size / 15), (size / 15), Castle);
        terrain.add(c2);
        player2.setCastle(c2);

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
        
        isInRangeOfCastle(mcount,c1,c2,"mountain");
        isInRangeOfCastle(lcount,c1,c2,"lake");

    }
    
    private void isInRangeOfCastle(int count, Castle c1, Castle c2, String tr){
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
                
                if(tr.equals("mountain")){
                    position[x][y] = 'M';
                    terrain.add(new Mountain(x * (size / 30), y * (size / 30), (size / 30), (size / 30), Mountain));
                }
                else{
                    position[x][y] = 'L';
                    terrain.add(new Lake(x * (size / 30), y * (size / 30), (size / 30), (size / 30), Lake));
                }
        }
    }

    private int distance(int x1, int x2, int y1, int y2) {
        //distance is calculated by the block-distance, not the absolute
        return max(abs(x1 - x2), abs(y1 - y2));
    }
    
    private ArrayList<HashMap<Integer,Integer>> findWay(Castle from, Castle to) 
        /* 
            stores the best way from the home castle to the enemy castle step to step as (x, y) pairs
            (units can't move in any diagonal directions)

            */
    
   {
        int minDistance=1000; // define it as a larger number than the count of fields
        ArrayList<HashMap<Integer,Integer>> bestway=new ArrayList<HashMap<Integer,Integer>>();
        
        int fromx=from.x; int fromy=from.y; //left top corner of the home castle
        
        
        
        return bestway;
    }
    
   
    
    public boolean isOver() {
        return false;
    }

    public boolean roundOver() {
        return false;
    }

    public void unitAttack() {

    }

    public void towerDefense() {

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public ArrayList<Sprite> getTerrain() {
        return terrain;
    }

    public void setTerrain(ArrayList<Sprite> terrain) {
        this.terrain = terrain;
    }
}
