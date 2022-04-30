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

    private int playerMoney;
    private String playerName;
    private ArrayList<Tower> playerTowers;
    private ArrayList<Unit> playerUnits;
    private Castle playerCastle;
    private final int[] rowIndexes = {-1, 0, 0, 1};
    private final int[] columnIndexes = {0, -1, 1, 0};
    private int[][] difficultyMatrix;

    public Player(int playerMoney, String playerName) {
        this.playerMoney = playerMoney;
        this.playerName = playerName;
        this.playerTowers = new ArrayList<>();
        this.playerUnits = new ArrayList<>();
        this.difficultyMatrix = new int[30][30];
    }

    /**
     * torony lerakása adott koordinátán (adott típussal)
     *
     * @param matrixPositionX
     * @param matrixPositionY
     * @param towerType
     * @param model
     */
    public void build(int matrixPositionX, int matrixPositionY, String towerType, Model model) {

        int fieldSize = model.getBoardSize() / 30;
        model.setSelectables(new ArrayList<>());
        difficultyMatrix = getDifficulty(model, "General");
        if (isValid(matrixPositionX, matrixPositionY) && matrixPositionY >= model.getActivePlayer() * 15
                && matrixPositionY < (model.getActivePlayer() + 1) * 15) /*
                        annak eldöntése, hogy a kattintott mező rajta van-e a játéktéren
                        és a megfelelő térfelen van-e
                        player 1 területe (y): 0-14
                         player 2 területe (y): 15-29
         */ {

            if (model.getPosition()[matrixPositionX][matrixPositionY] == 'F' && model.placable(matrixPositionX, matrixPositionY, difficultyMatrix)) {
                //kiválasztott torony létrehozása és hozzáadása a toronylistához

                if (!towerType.equals("")) {
                    Tower newTower;
                    String color;
                    if (model.getActivePlayer() == 0) {
                        color = "blue";
                    } else {
                        color = "red";
                    }

                    if (towerType.equals("Fortified")) {
                        newTower = new Fortified(color, matrixPositionX * fieldSize, matrixPositionY * fieldSize, fieldSize, fieldSize,
                                new ImageIcon("src/res/" + towerType + "1" + color + ".png").getImage());
                    } else if (towerType.equals("Sniper")) {
                        newTower = new Sniper(color, matrixPositionX * fieldSize, matrixPositionY * fieldSize, fieldSize, fieldSize,
                                new ImageIcon("src/res/" + towerType + "1" + color + ".png").getImage());
                    } else //Rapid
                    {
                        newTower = new Rapid(color, matrixPositionX * fieldSize, matrixPositionY * fieldSize, fieldSize, fieldSize,
                                new ImageIcon("src/res/" + towerType + "1" + color + ".png").getImage());
                    }

                    //ellenőrzés, hogy van-e elegendő pénz
                    if (newTower.price <= playerMoney) {
                        playerMoney -= newTower.price;

                        model.setPosition(matrixPositionX, matrixPositionY, 'T');
                        model.addTerrainElement(newTower);
                        addTower(newTower);

                        //legjobb út kiszámítása minden egység számára
                        for (int actualPlayerIndex = 0; actualPlayerIndex < 2; actualPlayerIndex++) {
                            Player actualPlayer = model.getPlayers()[actualPlayerIndex];
                            int defender = Math.abs(actualPlayerIndex * 4 - 4);
                            ArrayList<Unit> updateUnits = model.getPlayers()[actualPlayerIndex].getUnits();
                            for (Unit u : updateUnits) {
                                int minWayDifficulty = 10000;
                                ArrayList<Node> bestWayNode = new ArrayList<>();

                                for (int i = 0; i < 4; i++) {
                                    ArrayList<String> bestWayString = actualPlayer.findBestWay(u.getX() / fieldSize,
                                            u.getY() / (fieldSize), model.getCastleCoordinates()[i + defender][0],
                                            model.getCastleCoordinates()[i + defender][1], model.getPlayers()[actualPlayerIndex].getDifficulty(model, u.getType()));
                                    int sizeOfNodeWay=actualPlayer.convertWay(bestWayString).size();

                                    if (minWayDifficulty > sizeOfNodeWay) {
                                        minWayDifficulty = sizeOfNodeWay;
                                        bestWayNode = actualPlayer.convertWay(bestWayString);
                                    }
                                }
                                u.setWay(bestWayNode);

                                model.getPlayers()[actualPlayerIndex].setUnits(updateUnits);
                            }

                        }
                    }

                }
            }

        }

    }

    /**
     * torony fejlesztése adott koordinátán
     *
     * @param matrixPositionX
     * @param matrixPositionY
     * @param size
     */
    public void upgrade(int matrixPositionX, int matrixPositionY, int size) {
        int boardPositionX = matrixPositionX * (size / 30);
        int boardPositionY = matrixPositionY * (size / 30);
        for (Tower t : playerTowers) {
            if (t.getX() == boardPositionX && t.getY() == boardPositionY) //kattintott torony kiválasztása
            {
                if ((playerMoney >= t.getUpgradePrice() && t.getLevel() < 3)&&t.demolishedIn==-1) //annak ellenőrzése, hogy fejleszthető-e
                {
                    //torony fejlesztése
                    playerMoney -= t.getUpgradePrice();
                    t.upgrade();
                    String towerColor = "red";
                    if (t.getY() < 15 * (size / 30)) {
                        towerColor = "blue";
                    }
                    t.setImg(new ImageIcon("src/res/" + t.type + t.level + towerColor + ".png").getImage());
                }
                break;
            }

        }
    }

    /**
     * torony lerombolása adott koordinátán
     *
     * @param matrixPositionX
     * @param matrixPositionY
     * @param size
     */
    public void demolish(int matrixPositionX, int matrixPositionY, int size) {
        int boardPositionX = matrixPositionX * (size / 30);
        int boardPositionY = matrixPositionY * (size / 30);
        for (Tower t : playerTowers) {
            if (t.getX() == boardPositionX && t.getY() == boardPositionY) {
                if (t.demolishedIn == -1) {
                    playerMoney += t.getMoneySpentOn() / 2; //játékos visszakapja az összes toronyba költött pénz felét
                    t.demolish();
                }
                break;
            }
        }
    }

    /**
     * annak eldöntése, hogy a játéktéren van-e egy mező
     */
    private boolean isValid(int matrixPositionX, int matrixPositionY) {
        return (matrixPositionX >= 0 && matrixPositionX < 30) && (matrixPositionY >= 0 && matrixPositionY < 30);
    }

    /**
     * egységek küldése
     *
     * @param type
     * @param playerColor
     * @param amount
     * @param model
     * @return
     */
    public void sendUnits(String type, String playerColor, int amount, Model model) {
        int fieldSize = model.getBoardSize() / 30;
        if (!((type.equals("General") || type.equals("Diver") || type.equals("Climber")) && amount * 20 > playerMoney) && !((type.equals("Fighter") || type.equals("Destroyer")) && amount * 40 > playerMoney)) {
            int minWayDifficulty = 10000;
            ArrayList<Node> bestWayNode = new ArrayList<>();

            //minden lehetséges (4*4) kastélykoordináta között kiszámoljuk a legjobb utak, és azok közül is a legjobbat kiválasztjuk
            difficultyMatrix = getDifficulty(model, type);
            //annak eltárolása, hogy át tud-e menni az adott mezőkön az egység

            for (int i = 0; i < amount; i++) {

                int attacker = model.getActivePlayer() * 4;
                int defender = Math.abs(model.getActivePlayer() * 4 - 4);
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < 4; k++) {
                        ArrayList<String> bestWayString = findBestWay(model.getCastleCoordinates()[j + attacker][0], model.getCastleCoordinates()[j + attacker][1],
                                model.getCastleCoordinates()[k + defender][0], model.getCastleCoordinates()[k + defender][1], difficultyMatrix);
                        int sizeOfNodeWay=convertWay(bestWayString).size();
                                    if (minWayDifficulty > sizeOfNodeWay) {
                                        minWayDifficulty = sizeOfNodeWay;
                                        bestWayNode = convertWay(bestWayString);
                                    }
                    }
                }

                if (bestWayNode != null && !bestWayNode.isEmpty()) {

                    Unit newUnit;
                    if (type.equals("General")) {
                        newUnit = new General(playerColor, playerCastle.x, playerCastle.y, fieldSize, fieldSize,
                                new ImageIcon("src/res/" + type + playerColor + ".png").getImage(), bestWayNode);
                    } else if (type.equals("Climber")) {
                        newUnit = new Climber(playerColor, playerCastle.x, playerCastle.y, fieldSize, fieldSize,
                                new ImageIcon("src/res/" + type + playerColor + ".png").getImage(), bestWayNode);
                    } else if (type.equals("Diver")) {
                        newUnit = new Diver(playerColor, playerCastle.x, playerCastle.y, fieldSize, fieldSize,
                                new ImageIcon("src/res/" + type + playerColor + ".png").getImage(), bestWayNode);
                    } else if (type.equals("Fighter")) {
                        newUnit = new Fighter(playerColor, playerCastle.x, playerCastle.y, fieldSize, fieldSize,
                                new ImageIcon("src/res/" + type + playerColor + ".png").getImage(), bestWayNode);
                    } else //Destroyer
                    {
                        newUnit = new Destroyer(playerColor, playerCastle.x, playerCastle.y, fieldSize, fieldSize,
                                new ImageIcon("src/res/" + type + playerColor + ".png").getImage(), bestWayNode);
                    }
                    playerMoney -= newUnit.price;
                    addUnits(newUnit);
                }
            }

        }

    }

    /**
     * kezdő és cél mezők között legjobb út kiszámítása ez a legjobb út
     * fordulópontjait adja vissza
     *
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param difficultyMatrix
     * @return
     */
    public ArrayList<String> findBestWay(int fromX, int fromY, int toX, int toY, int difficultyMatrix[][]) {

        ArrayList<String> bestWayString = new ArrayList<>();
        int currentX = fromX;
        int currentY = fromY;

        Queue<Node> queue = new ArrayDeque<>();
        Node from = new Node(currentX, currentY, null);
        queue.add(from);

        Set<String> visited = new HashSet<>();
        visited.add(from.toString()); //eltároljuk, hogy melyik mezőkön jártunk eddig
        while (!queue.isEmpty()) {
            Node current = queue.poll(); //kiválasztjuk a sorban elöl lévő mezőt
            int i = current.getX();
            int j = current.getY();

            if (i == toX && j == toY) //megtaláltuk a cél mezőt, visszatérünk az úttal
            {
                findNodeWay(current, bestWayString);

                return bestWayString;
            }
            int currentValue = difficultyMatrix[i][j];

            for (int k = 0; k < rowIndexes.length; k++) {
                //szomszédos mezők vizsgálata, jelenlegihez viszonyítva : -1;0, 1;0, 0;1, 0;-1
                currentX = i + (rowIndexes[k] * currentValue);
                currentY = j + (columnIndexes[k] * currentValue);
                //ha a nehézség 1000 egy szomszédos mezőnek, akkor kiindexel a pályáról, és elveti azt az irányt
                //ha a nehézség 1, továbbmegy

                if (isValid(currentX, currentY)) //kiindexeltünk-e a pályáról
                {
                    Node next = new Node(currentX, currentY, current);

                    if (!visited.contains(next.toString())) {
                        //ha még nem jártunk a mezőn, hozzáadjuk azokhoz, amin már jártunk + a sorhoz is
                        queue.add(next);
                        visited.add(next.toString());
                    }
                }
            }
        }
        return bestWayString;
    }

    /**
     * String típusú legjobb utat Node típusban adjuk vissza
     *
     * @param bestWayString
     * @return
     */
    public ArrayList<Node> convertWay(ArrayList<String> bestWayString) {

        ArrayList<Node> bestWayNode = new ArrayList<>();

        if (bestWayString.isEmpty() || bestWayString == null) {
            return new ArrayList<>();
        }

        for (int i = 1; i < bestWayString.size(); i++) {
            int firstCoordinateX = parseInt(bestWayString.get(i - 1).split(";")[0]);
            int firstCoordinateY = parseInt(bestWayString.get(i - 1).split(";")[1]);
            int secondCoordinateX = parseInt(bestWayString.get(i).split(";")[0]);
            int secondCoordinateY = parseInt(bestWayString.get(i).split(";")[1]);

            if (max(abs(firstCoordinateX - secondCoordinateX), abs(firstCoordinateY - secondCoordinateY)) > 1) //fordulópontok közötti mezők hozzáadása az úthoz
            {

                if (firstCoordinateX > secondCoordinateX) {
                    for (int j = firstCoordinateX - 1; j >= secondCoordinateX; j--) {
                        bestWayNode.add(new Node(j, firstCoordinateY));
                    }
                } else if (secondCoordinateX > firstCoordinateX) {
                    for (int j = firstCoordinateX + 1; j <= secondCoordinateX; j++) {
                        bestWayNode.add(new Node(j, firstCoordinateY));
                    }
                } else if (secondCoordinateY > firstCoordinateY) {
                    for (int j = firstCoordinateY + 1; j <= secondCoordinateY; j++) {
                        bestWayNode.add(new Node(firstCoordinateX, j));
                    }
                } else {
                    for (int j = firstCoordinateY - 1; j >= secondCoordinateY; j--) {
                        bestWayNode.add(new Node(firstCoordinateX, j));
                    }
                }

            } else {
                bestWayNode.add(new Node(secondCoordinateX, secondCoordinateY));
            }

        }

        //ha egymás után azonos mező jön, eltávolítjuk az egyiket
        for (int j = 1; j < bestWayNode.size(); j++) {
            if (bestWayNode.get(j - 1).equals(bestWayNode.get(j))) {
                bestWayNode.remove(j);
                j--;
            }
        }

        return bestWayNode;
    }

    /**
     * segédfüggvény, rekurzívan hozzáadjuk a mezők szülőjét (előző mező) az
     * úthoz
     *
     */
    private void findNodeWay(Node currentNode, ArrayList<String> bestWayNode) {
        if (currentNode != null) //ha nincs szülő, akkor visszakaptuk a kezdő mezőt
        {
            findNodeWay(currentNode.getParent(), bestWayNode);
            bestWayNode.add(currentNode.toString());
        }
    }

    /**
     * a megadott torony toronylista indexének visszaadása lerombolásnál
     * használatos
     *
     * @param t
     * @return
     */
    public int getTowerIndex(Tower towerToFind) {
        int index = 0;
        for (Tower actualTower : playerTowers) {
            if (actualTower.getX() == towerToFind.getX()
                    && actualTower.getY() == towerToFind.getY()) {
                return index;
            }
            index++;
        }
        return -1; //nincs ilyen torony a listában
    }

    /**
     * egység törlése
     *
     * @param unitToDelete
     */
    public void deleteUnit(Unit unitToDelete) {
        playerUnits.remove(unitToDelete);
    }

    /**
     * nehézségi mátrix visszaadása adott játékos adott típusú egységére
     *
     * @param model
     * @param UnitType
     * @param activePlayer
     * @return
     */
    public int[][] getDifficulty(Model model, String UnitType) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (UnitType.equals("Diver")) //átmehet a tavakon
                {
                    if (model.getTerrainElementPositions()[i][j] == 'F' || model.getTerrainElementPositions()[i][j] == 'C' || model.getTerrainElementPositions()[i][j] == 'L') {
                        difficultyMatrix[i][j] = 1;
                    } else //nem mehet át, ezért egy nagy számot adunk meg, hogy a legjobb út során kiindexeljen viszgálatkor
                    {
                        difficultyMatrix[i][j] = 1000;
                    }
                } else if (UnitType.equals("Climber")) //átmehet a hegyeken
                {
                    if (model.getTerrainElementPositions()[i][j] == 'F' || model.getTerrainElementPositions()[i][j] == 'C' || model.getTerrainElementPositions()[i][j] == 'M') {
                        difficultyMatrix[i][j] = 1;
                    } else {
                        difficultyMatrix[i][j] = 1000;
                    }
                } else //normal unit
                {
                    if (model.getTerrainElementPositions()[i][j] == 'F' || model.getTerrainElementPositions()[i][j] == 'C') {
                        difficultyMatrix[i][j] = 1;
                    } else {
                        difficultyMatrix[i][j] = 1000;
                    }
                }
            }

        }
        return difficultyMatrix;
    }

    /**
     * még nem lerombolt tornyok listájának visszaadása
     *
     * @return
     */
    public ArrayList<Tower> getNotDemolishedTowers() {
        ArrayList<Tower> notDemolished = new ArrayList<Tower>();
        for (Tower t : playerTowers) {
            if (t.demolishedIn == -1) {
                notDemolished.add(t);
            }

        }
        return notDemolished;
    }

    /**
     * új torony hozzáadása
     *
     * @param newTower
     */
    public void addTower(Tower newTower) {
        playerTowers.add(newTower);
    }

    /**
     * getterek, setterek
     *
     * @return
     */
    public Castle getCastle() {
        return playerCastle;
    }

    public int getMoney() {
        return playerMoney;
    }

    public void setMoney(int playerMoney) {
        this.playerMoney = playerMoney;
    }

    public String getName() {
        return playerName;
    }

    public void setName(String playerName) {
        this.playerName = playerName;
    }

    public void setCastle(Castle playerCastle) {
        this.playerCastle = playerCastle;
    }

    public ArrayList<Tower> getTowers() {
        return playerTowers;
    }

    public ArrayList<Unit> getUnits() {
        return playerUnits;
    }

    public void addUnits(Unit newUnit) {
        playerUnits.add(newUnit);
    }

    public void setTowers(ArrayList<Tower> newTowerList) {
        playerTowers = newTowerList;
    }

    public void setUnits(ArrayList<Unit> newUnitList) {
        playerUnits = newUnitList;
    }

}
