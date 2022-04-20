package model;

import static java.lang.Integer.max;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import javax.swing.ImageIcon;

public class Player {

    private int money;
    private String name;
    private ArrayList<Tower> towers;
    private ArrayList<Unit> units;
    private Castle castle;
    private final int[] row = {-1, 0, 0, 1};
    private final int[] col = {0, -1, 1, 0};
    private int[][] difficulty = new int[30][30];
    //how you can move from a Node

    public Castle getCastle() {
        return castle;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
    }

    public Player(int money, String name) {
        this.money = money;
        this.name = name;
        this.towers = new ArrayList<>();
        this.units = new ArrayList<>();
    }

    private void printDiffMatrices(Model model) {
        ArrayList<Unit> units1 = model.getPlayers()[0].getUnits();
        ArrayList<Unit> units2 = model.getPlayers()[1].getUnits();
        for (Unit u : units1) {
            int diffM[][] = model.getPlayers()[0].getDifficulty(model, u.getType(), 0);
            for (int i = 0; i < 30; i++) {
                for (int j = 0; j < 30; j++) {
                    System.out.print(diffM[i][j] + " ");
                }
                System.out.println();
            }
        }
        for (Unit u : units2) {
            int diffM[][] = model.getPlayers()[1].getDifficulty(model, u.getType(), 1);
            for (int i = 0; i < 30; i++) {
                for (int j = 0; j < 30; j++) {
                    System.out.print(diffM[i][j] + " ");
                }
                System.out.println();
            }
        }
    }

    public Model build(int x, int y, String type, Model model) {

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                    difficulty[i][j] = 1;
                } else //cant go through so we set a large number
                {
                    difficulty[i][j] = 1000;
                }
            }

        }

        model.setSelectables(new ArrayList<>());

        if (isValid(x, y) && y >= model.getActivePlayer() * 15
                && y < (model.getActivePlayer() + 1) * 15) /*
                        deciding if the player clicked to the board
                        player 1's y area: 0-14
                         player 2's y area: 15-29
         */ {
            if (model.getPosition()[x][y] == 'F' && model.placable(x, y, difficulty)) //deciding if player clicked to a Field or not
            {
                //creating chosen tower and adding it to the game (if there's chosen tower)

                if (!type.equals("")) {
                    Tower newTower;
                    String color;
                    if (model.getActivePlayer() == 0) {
                        color = "blue";
                    } else {
                        color = "red";
                    }
                    int towerSize = (model.getSize() / 30);

                    if (type.equals("Fortified")) {
                        newTower = new Fortified(color, x * towerSize, y * towerSize, towerSize, towerSize, new ImageIcon("src/res/Tower.png").getImage());
                    } else if (type.equals("Sniper")) {
                        newTower = new Sniper(color, x * towerSize, y * towerSize, towerSize, towerSize,
                                new ImageIcon("src/res/Tower.png").getImage());
                    } else //type.equals("Rapid")
                    {
                        newTower = new Rapid(color, x * towerSize, y * towerSize, towerSize, towerSize, new ImageIcon("src/res/Tower.png").getImage());
                    }

                    //checking if player's money is enough to buy it
                    if (newTower.price <= money) {
                        money -= newTower.price;

                        model.setPosition(x, y, 'T');
                        model.addTerrainElement(newTower);
                        addTower(newTower);
                    }

                }
            }

        }
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = model.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = model.getPlayers()[q].findWay(u.getX() / (model.getSize() / 30),
                            u.getY() / (model.getSize() / 30), model.getCastleCoordinates()[i + defender][0],
                            model.getCastleCoordinates()[i + defender][1], model.getPlayers()[q].getDifficulty(model, u.getType(), q));

                    if (minWayDiff > model.wayDiff(q, wayString, u.getType())) {
                        bestway = model.getPlayers()[q].convertWay(wayString);
                        minWayDiff = model.wayDiff(q, wayString, u.getType());

                    }
                }
                u.setWay(bestway);
                 
                model.getPlayers()[q].setUnits(updateUnits);
            }

        }
       
        return model;
         

    }

    public void upgrade(int x, int y, int size, char c) {
        if(c != 'T') return;
        x *= (size / 30);
        y *= (size / 30);
        boolean canUpgrade = false;
        for (var t : towers) {
            if (t.getX() == x && t.getY() == y) {

                switch (t.getType()) {
                    case "Fortified":
                        if (t.getLevel() < 3) {
                            canUpgrade = enoughMoney(t.getLevel(), 350, 550);
                        } else {
                            canUpgrade = false;
                        }
                        break;
                    case "Sniper":
                        if (t.getLevel() < 3) {
                            canUpgrade = enoughMoney(t.getLevel(), 500, 700);
                        } else {
                            canUpgrade = false;
                        }
                        break;
                    case "Rapid":
                        if (t.getLevel() < 3) {
                            canUpgrade = enoughMoney(t.getLevel(), 400, 550);
                        } else {
                            canUpgrade = false;
                        }
                        break;
                    default:
                        break;
                }
                if (canUpgrade) {
                    t.upgrade();
                }
            }
        }
    }

    public boolean enoughMoney(int currLvl, int lvl2, int lvl3) {
        if (currLvl == 1) {
            if (money - lvl2 >= 0) {
                money -= lvl2;
                return true;
            } else {
                return false;
            }
        } else {
            if (money - lvl3 >= 0) {
                money -= lvl3;
                return true;
            } else {
                return false;
            }
        }
    }

    public void demolish(int x, int y, int size, char c) {
        if(c != 'T') return;
        x *= (size / 30);
        y *= (size / 30);
        for (var t : towers) {
            if (t.getX() == x && t.getY() == y) {
                money += t.getPrice() / 2;
                t.demolish();
            }
        }
    }

    private boolean isValid(int x, int y) //determine if it's on the board or not
    {
        return (x >= 0 && x < 30) && (y >= 0 && y < 30);
    }

    public Model sendUnits(String type, String color, int amount, Model model) {
        if((type.equals("General") || type.equals("Diver") || type.equals("Climber")) && amount * 20 > money){
            return model;
        }else if((type.equals("Fighter") || type.equals("Destroyer")) && amount * 40 > money){
            return model;
        }
        int minDistance=0;
        ArrayList<Node> bestWay = new ArrayList<>();

        /*
        we need to calculate all best ways from each of 4 castle coordinates 
        to the other 4 castle coordinates and find the shortest of them
         */
 /*
            stores how difficult to go through each 
            1 - field
            2 - mountain and lake if unit is Climber/Diver
         */
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (type.equals("Diver")) //they can go through lakes
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 ;
                    } else if (model.position[i][j] == 'L') {
                        difficulty[i][j] = 2 ;
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                } else if (type.equals("Climber")) //they can go through mountains
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 ;
                    } else if (model.position[i][j] == 'M') {
                        difficulty[i][j] = 2 ;
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                } else //normal unit
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 ;
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                }

            }
        }

        for (int i = 0; i < amount; i++) {
            

            int attacker = model.getActivePlayer() * 4;
            int defender = Math.abs(model.getActivePlayer() * 4 - 4);
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    ArrayList<String> wayString = findWay(model.getCastleCoordinates()[j + attacker][0], model.getCastleCoordinates()[j + attacker][1],
                            model.getCastleCoordinates()[k + defender][0], model.getCastleCoordinates()[k + defender][1], difficulty);
                   
                    ArrayList<Node> nodeWay = convertWay(wayString);
                    if (minDistance==0||model.wayDiff(model.getActivePlayer(), wayString, type) < minDistance) {
                        bestWay = nodeWay;
                        minDistance = model.wayDiff(model.getActivePlayer(), wayString, type);
                    }
                }
            }
            
            if (bestWay != null && !bestWay.isEmpty()) {
                
                Unit newUnit;
                int unitSize = model.getSize() / 30;
                if (type.equals("General")) {
                    newUnit = new General(color, castle.x, castle.y, unitSize, unitSize, new ImageIcon("src/res/Unit.png").getImage(), bestWay);
                } else if (type.equals("Climber")) {
                    newUnit = new Climber(color, castle.x, castle.y, unitSize, unitSize, new ImageIcon("src/res/Unit.png").getImage(), bestWay);
                } else if (type.equals("Diver")) {
                    newUnit = new Diver(color, castle.x, castle.y, unitSize, unitSize, new ImageIcon("src/res/Unit.png").getImage(), bestWay);
                } else if (type.equals("Fighter")) {
                    newUnit = new Fighter(color, castle.x, castle.y, unitSize, unitSize, new ImageIcon("src/res/Unit.png").getImage(), bestWay);
                } else //Destroyer
                {
                    newUnit = new Destroyer(color, castle.x, castle.y, unitSize, unitSize, new ImageIcon("src/res/Unit.png").getImage(), bestWay);
                }
                //checking if player's money is enough to buy it
                if (newUnit.price <= money) {
                    money -= newUnit.price;
                    addUnits(newUnit);
                }

            }
        }

        return model;

    }

    public ArrayList<String> findWay(int fromX, int fromY, int toX, int toY, int difficulty[][]) /* 
            stores the best way as String from the home castle to the enemy castle
            step to step as (x, y) pairs
            (units can't move in any diagonal directions)

     */ {

        ArrayList<String> bestway = new ArrayList<>();
        int currentX = fromX;
        int currentY = fromY;
        //begin from own castle

        Queue<Node> q = new ArrayDeque<>();
        Node from = new Node(currentX, currentY, null);
        q.add(from);

        Set<String> visited = new HashSet<>();
        // to know if we went across a Node or not
        visited.add(from.toString());
        while (!q.isEmpty()) {
            Node current = q.poll();
            int i = current.getX();
            int j = current.getY();

            if (i == toX && j == toY) //destination found
            {
                findNodeWay(current, bestway);

                return bestway;
            }
            int currentValue = 0;
            try{
                currentValue = difficulty[i][j];
            }catch(Exception e){
                
            }

            for (int k = 0; k < row.length; k++) {
                currentX = i + (row[k] * currentValue);
                currentY = j + (col[k] * currentValue);

                if (isValid(currentX, currentY)) //check if we're in the board
                {
                    Node next = new Node(currentX, currentY, current);

                    if (!visited.contains(next.toString())) //if node is not visited yet, we add it to the way and flag as visited
                    {
                        q.add(next);
                        visited.add(next.toString());
                    }
                }
            }
        }

        return bestway;
    }

    public int getTowerIndex(Tower t){
        int i = 0;
        //System.out.println(towers.size());
         for(Tower x : towers){
             if(x.getX() == t.getX() && x.getY() == t.getY()){
                 return i;
             }
             i++;
        }
         return -1;
    }

    public ArrayList<Node> convertWay(ArrayList<String> src) {

        ArrayList<Node> way = new ArrayList<Node>();

        if (src.isEmpty() || src == null) {
            return new ArrayList<Node>();
        }

        for (int i = 1; i < src.size(); i++) {
            int x1 = parseInt(src.get(i - 1).split(";")[0]);
            int y1 = parseInt(src.get(i - 1).split(";")[1]);
            int x2 = parseInt(src.get(i).split(";")[0]);
            int y2 = parseInt(src.get(i).split(";")[1]);

            if (max(abs(x1 - x2), abs(y1 - y2)) > 1) {

                if (x1 > x2) {
                    for (int j = x1 - 1; j >= x2; j--) {
                        way.add(new Node(j, y1));
                    }
                } else if (x2 > x1) {
                    for (int j = x1 + 1; j <= x2; j++) {
                        way.add(new Node(j, y1));
                    }
                } else if (y2 > y1) {
                    for (int j = y1 + 1; j <= y2; j++) {
                        way.add(new Node(x1, j));
                    }
                } else {
                    for (int j = y1 - 1; j >= y2; j--) {
                        way.add(new Node(x1, j));
                    }
                }

            } else {
                way.add(new Node(x2, y2));
            }

        }

        //check if there are same positions after each other
        for (int j = 1; j < way.size(); j++) {
            if (way.get(j - 1).equals(way.get(j))) {
                way.remove(j);
                j--;
            }
        }

        return way;
    }

    private void findNodeWay(Node n, ArrayList<String> way) //find the way between two Nodes
    {
        if (n != null) {
            findNodeWay(n.parent, way);
            way.add(n.toString());
        }
    }

    public void deleteUnit(Unit unitToDelete) {
       units.remove(unitToDelete);
    }

    public int[][] getDifficulty(Model model, String type, int activePlayer) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (type.equals("Diver")) //they can go through lakes
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1;
                    } else if (model.position[i][j] == 'L') {
                        difficulty[i][j] = 2;
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                } else if (type.equals("Climber")) //they can go through mountains
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1;
                    } else if (model.position[i][j] == 'M') {
                        difficulty[i][j] = 2;
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                } else //normal unit
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 ;
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                }
            }

        }
        return difficulty;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public ArrayList<Tower> getNotDemolishedTowers() {
        ArrayList<Tower> notDemolished=new ArrayList<Tower>();
        for(Tower t : towers){
            if(t.demolishedIn==-1){
                 notDemolished.add(t);
            }
           
        }
        return notDemolished;
    }

    public ArrayList<Tower> getTowers() {
        return towers;
    }

    public void addTower(Tower newTower) {
        towers.add(newTower);
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void addUnits(Unit newUnit) {
        units.add(newUnit);
    }

    public void setTowers(ArrayList<Tower> t) {
        towers = t;
    }

    public void setUnits(ArrayList<Unit> u) {
        units = u;
    }

}
