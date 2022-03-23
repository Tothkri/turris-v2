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
import javax.swing.JOptionPane;

public class Player {

    private int money;
    private String name;
    private ArrayList<Tower> towers;
    private ArrayList<Unit> units;
    private Castle castle;
    private final int[] row = {-1, 0, 0, 1};
    private final int[] col = {0, -1, 1, 0};
    private int[][] difficulty = new int[30][30];
    //hogy mozoghatsz egy Node-tól

    public Player(int money, String name) {
        this.money = money;
        this.name = name;
        this.towers = new ArrayList<>();
        this.units = new ArrayList<>();
    }

    public Model build(int x, int y, String type, Model model) {

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                    difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                } else {    //cant go through so we set a large number
                    difficulty[i][j] = 1000;
                }
            }

        }

        model.setSelectables(new ArrayList<>());

        if (isValid(x, y) && y >= model.getActivePlayer() * 15 && y < (model.getActivePlayer() + 1) * 15) {
            /*
            * eldönti, hogy a player rákattintott-e a boardra
            * player 1's y területe: 0-14
            * player 2's y területe: 15-29
            */

            if (model.getPosition()[x][y] == 'F' && model.placable(x, y, difficulty)) //deciding if player clicked to a Field or not
            {
                /**
                * kiválasztott torony elkészítése és hozzáadása a játékhoz
                * (ha van kiválasztott torony)
                */
                if (!type.equals("")) {
                    Tower newTower;
                    if (type.equals("Fortified")) {
                        newTower = new Fortified("Fortified", 2, 1, 0.5, 100, 200, x * (model.getSize() / 30),
                                y * (model.getSize() / 30),
                                model.getSize() / 30, model.getSize() / 30,
                                new ImageIcon("src/res/Tower.png").getImage());
                    } else if (type.equals("Sniper")) {
                        newTower = new Sniper("Sniper", 9, 1, 1.0, 25, 300, x * (model.getSize() / 30),
                                y * (model.getSize() / 30),
                                model.getSize() / 30, model.getSize() / 30,
                                new ImageIcon("src/res/Tower.png").getImage());
                    } else {
                        newTower = new Rapid("Rapid", 2, 1, 0.33, 40, 250, x * (model.getSize() / 30),
                                y * (model.getSize() / 30),
                                model.getSize() / 30, model.getSize() / 30,
                                new ImageIcon("src/res/Tower.png").getImage());
                    }

                    /**
                    * megnézi hogy a játékosnak van elég pénze, hogy megvegye
                    */
                    if (newTower.price <= money) {
                        money -= newTower.price;

                        model.setPosition(x, y, 'T');
                        model.addTerrainElement(newTower);
                        addTower(newTower);
                    } else {    //player gets a warning 
                        JOptionPane.showMessageDialog(null, "You don't have enough money to build this tower!", "Warning", JOptionPane.INFORMATION_MESSAGE);
                    }

                }
            } else {    //player gets a warning 
                JOptionPane.showMessageDialog(null, "You can't place a tower here!", "Warning", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {        //player gets a warning 
            JOptionPane.showMessageDialog(null, "You can't place a tower here!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }

        return model;

    }

    /**
    * tower fejlesztése
    * @param x
    * @param y
    * @param size
    */
    public void upgrade(int x, int y, int size) {
        x *= (size / 30);
        y *= (size / 30);
        money -= 100;//will change
        for(var t : towers){
            if(t.getX() == x && t.getY() == y){
                t.upgrade();
            }
        }
    }

    /**
    * tower lerombolása
    * @param x
    * @param y
    * @param size
    */
    public void demolish(int x, int y, int size) {
        x *= (size / 30);
        y *= (size / 30);
        for(var t : towers){
            if(t.getX() == x && t.getY() == y){
                t.demolish();
            }
        }
    }

    /**
    * eldönti, hogy rajta van-e a board-on vagy sem
    */
    private boolean isValid(int x, int y) {
        return (x >= 0 && x < 30) && (y >= 0 && y < 30);
    }

    public Model sendUnits(String type, int amount, Model model) {

        int minDistance = 10000;
        ArrayList<Node> bestWay = new ArrayList<>();

        /**
        * mind a 4 kastély koordinátából ki kell számolni a legjobb utat
        * a másik kastély 4 koordinátájába, hogy megtaláljuk a legrövidebb utat
        */

        /*
        * eltárolja, hogy milyen nehéz mindegyiken átmenni
        * 1 - field
        * 2 - mountain és lake ha a unit Climber/Diver
        */
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (type.equals("Diver")) { //they can go through lakes
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else if (model.position[i][j] == 'L') {
                        difficulty[i][j] = 2 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else {    //cant go through so we set a large number
                        difficulty[i][j] = 1000;
                    }
                } else if (type.equals("Climber")) {    //they can go through mountains
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else if (model.position[i][j] == 'M') {
                        difficulty[i][j] = 2 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else {    //cant go through so we set a large number
                        difficulty[i][j] = 1000;
                    }
                } else {        //normal unit
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else {    //cant go through so we set a large number
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
                    if (model.wayDiff(model.getActivePlayer(), wayString, type) < minDistance) {
                        bestWay = nodeWay;
                        minDistance = model.wayDiff(model.getActivePlayer(), wayString, type);
                    }
                }
            }

            ArrayList<Node> way = bestWay;

            if (way != null && way.size() > 0) {
                Unit newUnit;

                if (type.equals("General")) {
                    newUnit = new General("General", 5, 3, 10, 20, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                } else if (type.equals("Climber")) {
                    newUnit = new Climber("Climber", 3, 2, 10, 30, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                } else if (type.equals("Diver")) {
                    newUnit = new Diver("Diver", 3, 2, 10, 30, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                } else if (type.equals("Fighter")) {
                    newUnit = new Fighter("Fighter", 4, 5, 15, 30, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                } else {    //Destroyer
                    newUnit = new Destroyer("Destroyer", 2, 5, 15, 30, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                }
                
                if (newUnit.price <= money) {   //megnézi, hogy a jétékosnak van-e elég pénze, hogy megvegye
                    money -= newUnit.price;
                    addUnits(newUnit);
                } else {                        //játékos figyelmeztetést kap
                    JOptionPane.showMessageDialog(null, "You don't have enough money to send this unit!", "Warning", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        }
        return model;
    }

    /* 
    * string-ként eltárolja a saját kastélytól 
    * az ellenfél kastélyáig vezető legjobb utat 
    * a lépések (x, y) páronként lesznek eltárolva
    * az egységek átlósan nem mozoghatnak
    *
    * stores the best way as String from the home castle to the enemy castle
    * step to step as (x, y) pairs
    * (units can't move in any diagonal directions)
    */
    public ArrayList<String> findWay(int fromX, int fromY, int toX, int toY, int difficulty[][]) {
        ArrayList<String> bestway = new ArrayList<>();

        int currentX = fromX;
        int currentY = fromY;
        //begin from own castle

        Queue<Node> q = new ArrayDeque<>();
        Node from = new Node(currentX, currentY, null);
        q.add(from);

        Set<String> visited = new HashSet<>();

        /**
        * megtudjunk, hogy átmentünk-e egy Node-on vagy sem
        * to know if we went across a Node or not
        */
        visited.add(from.toString());
        while (!q.isEmpty()) {
            Node current = q.poll();
            int i = current.getX();
            int j = current.getY();

            /**
            * célpont megtalálva
            * destination found
            */
            if (i == toX && j == toY) {
                findNodeWay(current, bestway);
                return bestway;
            }

            int currentValue = difficulty[i][j];
            
            for (int k = 0; k < row.length; k++) {
                currentX = i + (row[k] * currentValue);
                currentY = j + (col[k] * currentValue);

                /**
                * megnézi, hogy a boardban vagyunk-e
                * check if we're in the board
                */
                if (isValid(currentX, currentY)) {
                    Node next = new Node(currentX, currentY, current);

                    /**
                    * ha a Node még nem volt meglátogatva, hozzáadjuk az úthoz
                    * és megflageljük meglátogatottként
                    * if node is not visited yet, we add it to the way and flag as visited
                    */
                    if (!visited.contains(next.toString())) {
                        q.add(next);
                        visited.add(next.toString());
                    }
                }
            }
        }

        return bestway;
    }

    /**
    * Node-ok közötti utak átalakítása
    * @param src
    */
    public ArrayList<Node> convertWay(ArrayList<String> src) {
        ArrayList<Node> way = new ArrayList<Node>();
        
        if(src.size() == 0 || src == null) {
            return new ArrayList<Node>();
        }

        way.add(new Node(parseInt(src.get(0).split(";")[0]),
                parseInt(src.get(0).split(";")[1])));

        for (int i = 1; i < src.size(); i++) {
            int x1 = parseInt(src.get(i - 1).split(";")[0]);
            int y1 = parseInt(src.get(i - 1).split(";")[1]);
            int x2 = parseInt(src.get(i).split(";")[0]);
            int y2 = parseInt(src.get(i).split(";")[1]);

            if (max(abs(x1 - x2), abs(y1 - y2)) > 1) {

                if (x1 > x2) {
                    for (int j = x1-1; j > x2; j--) {
                        way.add(new Node(j, y1));
                    }
                } else if (x2 > x1) {
                    for (int j = x1+1; j < x2; j++) {
                        way.add(new Node(j, y1));
                    }
                } else if (y2 > y1) {
                    for (int j = y1+1; j < y2; j++) {
                        way.add(new Node(x1, j));
                    }
                } else {
                    for (int j = y1-1; j < y2; j--) {
                        way.add(new Node(x1, j));
                    }
                }

            } else {
                way.add(new Node(x2, y2));
            }

        }
        
        /**
        * megnézi, hogy vannak-e ugyanolyan pozíciók egymás után
        * checks if there are same positions after each other
        * @param n
        * @param way
        */
        for(int j = 1;j < way.size(); j++){
            if(way.get(j - 1).equals(way.get(j))) {
                way.remove(j);
                j--;
            }
        }
        
        return way;
    }

    /**
    * megkeresi az utat két Node között
    * @param n
    * @param way
    */
    private void findNodeWay(Node n, ArrayList<String> way) {
        if (n != null) {
            findNodeWay(n.parent, way);
            way.add(n.toString());
        }
    }

    /**
    * unit törlése
    * @param unitToDelete
    */
    public void deleteUnit(Unit unitToDelete) {
        units.remove(unitToDelete);
    }

    /**
    * getDifficulty
    * @param model
    * @param type
    */
    public int[][] getDifficulty(Model model, String type) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (type.equals("Diver")) { //they can go through lakes
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else if (model.position[i][j] == 'L') {
                        difficulty[i][j] = 2 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else {                //cant go through so we set a large number
                        difficulty[i][j] = 1000;
                    }
                } else if (type.equals("Climber")) {    //they can go through mountains
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else if (model.position[i][j] == 'M') {
                        difficulty[i][j] = 2 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else {                            //cant go through so we set a large number
                        difficulty[i][j] = 1000;
                    }
                } else {        //normal unit
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else {    //cant go through so we set a large number
                        difficulty[i][j] = 1000;
                    }
                }
            }

        }
        return difficulty;
    }

    /**
    * Getterek, setterek
    */
    public Castle getCastle() {
        return castle;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
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

    public void setTowers(ArrayList<Tower> t){
        towers = t;
    }

}
