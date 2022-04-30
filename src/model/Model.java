package model;

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import static java.lang.Integer.max;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Model {

    protected Player players[];
    protected int round;
    protected ArrayList<Sprite> terrain;
    private char[][] terrainElementPositions;
    protected int boardSize;
    private int activePlayer;
    private ArrayList<Sprite> selectables;
    private int castleCoordinates[][];
    private int selectedMap;
    private int mountainCount;
    private int lakeCount;
    private final Image Lake = new ImageIcon("src/res/Lake.png").getImage();
    private final Image Mountain = new ImageIcon("src/res/Mountain.png").getImage();
    private final Image Castle = new ImageIcon("src/res/Castle.png").getImage();

    public Model(int selectedMap, String p1Name, String p2Name, int boardSize) {
        /**
         * beállítjuk a játékteret és a változókat
         */
        this.terrain = new ArrayList<>();
        this.selectables = new ArrayList<>();
        this.boardSize = boardSize;
        this.terrainElementPositions = new char[30][30];

        this.players = new Player[2];
        this.players[0] = new Player(1000, p1Name);
        this.players[1] = new Player(1000, p2Name);
        this.selectedMap = selectedMap;
        this.castleCoordinates = new int[8][2];
        generateTerrain(selectedMap);
    }

    public Model(int boardSize) {
        this.terrain = new ArrayList<>();
        this.selectables = new ArrayList<>();
        this.terrainElementPositions = new char[30][30];
        this.castleCoordinates = new int[8][2];
        this.boardSize = boardSize;
        this.players = new Player[2];
    }

    /**
     * Játékteren a terep leképzése a játéktér elemeit mátrixban tároljuk: F -
     * Field (üres mező) M - Moutain (hegy) L - Lake (tó) C - Castle (kastély) T
     * - Tower (torony) D - Destroyed (lerombolt torony) az egységek nincsenek
     * itt eltárolva, mivel szimuláció során folyamatosan változik a helyük, és
     * amúgy sem minősülnek akadálynak
     */
    private void generateTerrain(int selectedMap) {
        /**
         * feltöltjük kezdetben üres mezőkkel a pályát
         */
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                terrainElementPositions[i][j] = 'F';
            }
        }

        /**
         * kastélyok generálása és hozzáadása a játéktérhez
         */
        Random rand = new Random();
        int randomMatrixPosition = rand.nextInt(25) + 2;
        terrainElementPositions[randomMatrixPosition][2] = 'C';
        terrainElementPositions[randomMatrixPosition + 1][2] = 'C';
        terrainElementPositions[randomMatrixPosition][3] = 'C';
        terrainElementPositions[randomMatrixPosition + 1][3] = 'C';

        //a kastélyok 2x2 mező méretűek
        Castle castle1 = new Castle("blue", randomMatrixPosition
                * (boardSize / 30), 2 * (boardSize / 30), (boardSize / 15), (boardSize / 15), Castle, 300);
        terrain.add(castle1);
        players[0].setCastle(castle1);

        randomMatrixPosition = rand.nextInt(25) + 2;
        terrainElementPositions[randomMatrixPosition][27] = 'C';
        terrainElementPositions[randomMatrixPosition + 1][27] = 'C';
        terrainElementPositions[randomMatrixPosition][26] = 'C';
        terrainElementPositions[randomMatrixPosition + 1][26] = 'C';

        Castle castle2 = new Castle("red", randomMatrixPosition
                * (boardSize / 30), 26 * (boardSize / 30), (boardSize / 15), (boardSize / 15), Castle, 300);
        terrain.add(castle2);
        players[1].setCastle(castle2);

        //a kastélyok koordinátáit tároljuk el
        setCastleCords(castle1, castle2);

        //pálya generálása: hegyek, tavak pálya típusától függően, megfelelő távolságra a kastélyoktól
        if (selectedMap == 1) //hegyek és tavak egyforma mértékben
        {
            mountainCount = rand.nextInt(10) + 15;
            lakeCount = rand.nextInt(10) + 15;
        } else if (selectedMap == 2) {  //főleg hegyek
            mountainCount = rand.nextInt(5) + 30;
            lakeCount = rand.nextInt(5) + 5;
        } else {                        //főleg tavak
            mountainCount = rand.nextInt(5) + 5;
            lakeCount = rand.nextInt(5) + 30;
        }

        isInRangeOfCastle(mountainCount, castle1, castle2, "mountain");
        isInRangeOfCastle(lakeCount, castle1, castle2, "lake");
    }

    /**
     * kastélyok 8 koordinátájának beállítása
     *
     * @param castle1
     * @param castle2
     */
    public void setCastleCords(Castle castle1, Castle castle2) {
        castleCoordinates[0][0] = castle1.x / (boardSize / 30);
        castleCoordinates[0][1] = castle1.y / (boardSize / 30);
        castleCoordinates[1][0] = (castle1.x / (boardSize / 30)) + 1;
        castleCoordinates[1][1] = castle1.y / (boardSize / 30);
        castleCoordinates[2][0] = castle1.x / (boardSize / 30);
        castleCoordinates[2][1] = (castle1.y / (boardSize / 30)) + 1;
        castleCoordinates[3][0] = (castle1.x / (boardSize / 30)) + 1;
        castleCoordinates[3][1] = (castle1.y / (boardSize / 30)) + 1;
        castleCoordinates[4][0] = castle2.x / (boardSize / 30);
        castleCoordinates[4][1] = castle2.y / (boardSize / 30);
        castleCoordinates[5][0] = (castle2.x / (boardSize / 30)) + 1;
        castleCoordinates[5][1] = castle2.y / (boardSize / 30);
        castleCoordinates[6][0] = castle2.x / (boardSize / 30);
        castleCoordinates[6][1] = (castle2.y / (boardSize / 30)) + 1;
        castleCoordinates[7][0] = (castle2.x / (boardSize / 30)) + 1;
        castleCoordinates[7][1] = (castle2.y / (boardSize / 30)) + 1;
    }

    /**
     * kastélyok közvetlen környezetében van-e a generálandó játékelem (3
     * blokkos környezetben nem generálódhat tó/hegy) ha igen, új random
     * értékkel újrapróbálja lehelyezni minden (4+4) kastélykoordinátát
     * figyelembe veszünk
     */
    private void isInRangeOfCastle(int count, Castle castle1, Castle castle2, String terrainElementType) {
        Random randomCoordinate = new Random();
        for (int i = 0; i < count; i++) {
            int matrixPositionX, matrixPositionY;
            do {
                matrixPositionX = randomCoordinate.nextInt(30);
                matrixPositionY = randomCoordinate.nextInt(30);
            } while (terrainElementPositions[matrixPositionX][matrixPositionY] != 'F'
                    || distance(matrixPositionX, castle1.x / (boardSize / 30), matrixPositionY, castle1.y / (boardSize / 30)) < 4
                    || distance(matrixPositionX, castle1.x / (boardSize / 30) + 1, matrixPositionY, castle1.y / (boardSize / 30)) < 4
                    || distance(matrixPositionX, castle1.x / (boardSize / 30), matrixPositionY, castle1.y / (boardSize / 30) + 1) < 4
                    || distance(matrixPositionX, castle1.x / (boardSize / 30) + 1, matrixPositionY, castle1.y / (boardSize / 30) + 1) < 4
                    || distance(matrixPositionX, castle2.x / (boardSize / 30), matrixPositionY, castle2.y / (boardSize / 30)) < 4
                    || distance(matrixPositionX, castle2.x / (boardSize / 30) + 1, matrixPositionY, castle2.y / (boardSize / 30)) < 4
                    || distance(matrixPositionX, castle2.x / (boardSize / 30), matrixPositionY, castle2.y / (boardSize / 30) + 1) < 4
                    || distance(matrixPositionX, castle2.x / (boardSize / 30) + 1, matrixPositionY, castle2.y / (boardSize / 30) + 1) < 4);

            if (terrainElementType.equals("mountain")) {
                terrainElementPositions[matrixPositionX][matrixPositionY] = 'M';
                terrain.add(new Mountain(matrixPositionX * (boardSize / 30), matrixPositionY * (boardSize / 30), (boardSize / 30), (boardSize / 30), Mountain));
            } else {
                terrainElementPositions[matrixPositionX][matrixPositionY] = 'L';
                terrain.add(new Lake(matrixPositionX * (boardSize / 30), matrixPositionY * (boardSize / 30), (boardSize / 30), (boardSize / 30), Lake));
            }
        }
    }

    /**
     * két mező közötti távolságot adja vissza, blokk szinten (nem geometriai
     * távolság)
     */
    private int distance(int fromBoardPositionX, int toBoardPositionX, int fromBoardPositionY, int toBoardPositionY) {
        return max(abs(fromBoardPositionX - toBoardPositionX), abs(fromBoardPositionY - toBoardPositionY));
    }

    /**
     * visszaadja, hogy egy torony adott helyre lerakása után van-e út a
     * kastélyok között
     *
     * @param matrixPositionX
     * @param matrixPositionY
     * @param difficultyMatrix
     * @return
     */
    public boolean placable(int matrixPositionX, int matrixPositionY, int difficultyMatrix[][]) {
        int updatedDiffcultyMatrix[][] = difficultyMatrix;

        if (updatedDiffcultyMatrix[matrixPositionX][matrixPositionY] != 1 || isThereUnit(matrixPositionX, matrixPositionY)) {
            return false; //a mező nem üres
        }

        updatedDiffcultyMatrix[matrixPositionX][matrixPositionY] = 1000; // beállítjuk a lerakandó helyre a torony nehézségét

        //az összes lehetséges utat megnézzük a 4-4 kastélykoordináta között
        for (int attacker = 0; attacker < 4; attacker++) {
            for (int defender = 0; defender < 4; defender++) {
                ArrayList<String> bestWayString = players[activePlayer].findBestWay(castleCoordinates[attacker][0],
                        castleCoordinates[attacker][1],
                        castleCoordinates[defender + 4][0], castleCoordinates[defender + 4][1], updatedDiffcultyMatrix);
                if (bestWayString != null && !bestWayString.isEmpty()) {
                    return true; //létezik út
                }
            }
        }
        return false; //nem találtunk utat
    }

    /**
     * játékosok adatainak és a játéktér elemeinek elmentése txt-be
     *
     * @param filename
     * @param ticks
     */
    public void saveData(String filename, int ticks) {
        try ( Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename + ".txt"), "utf-8"))) {

            for (int i = 0; i < 30; i++) {
                for (int j = 0; j < 30; j++) {
                    writer.write(terrainElementPositions[j][i]);
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
                    for (Unit actualUnit : players[i].getUnits()) {
                        writer.write("U " + colorString(actualUnit.color) + " " + actualUnit.x + " " + actualUnit.y + " " + actualUnit.width + " " + actualUnit.height + " " + actualUnit.getType()
                                + " " + actualUnit.getHp());
                        writer.write(System.getProperty("line.separator"));
                    }
                }

                if (players[i].getTowers() != null) {
                    for (Tower t : players[i].getTowers()) {
                        //constructor: String type, String scolor, int power, int range, double attackFrequency, int hp, int level, int x, int y, int height, int width, Image img
                        writer.write("T " + t.getType() + " " + colorString(t.color) + " " + t.getPower() + " " + t.getRange() + " " + t.getAttackFrequency() + " "
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
     * szín értékből szín nevének visszaadása
     */
    private String colorString(Color colorToConvert) {
        if (colorToConvert == Color.RED) {
            return "red";
        }
        return "blue";
    }

    /**
     * visszaadja az út nehézségét
     *
     * @param actualPlayer
     * @param bestWay
     * @param unitType
     * @return
     */
    /**
     * torony lerakásának lehetséges helyei
     */
    public void setSelectables() {
        for (int i = 0; i < 30; i++) {
            for (int j = (activePlayer + 1) * 15 - 1; j >= activePlayer * 15; j--) {
                if (terrainElementPositions[i][j] == 'F' && placable(i, j, players[activePlayer].getDifficulty(this, "General"))) //generalra számoljuk, mivel így a tavat és hegyet is akadálynak veszi, hogy ne tudjunk rájuk rakni tornyot
                {
                    selectables.add(new Selectable(i * (boardSize / 30), j * (boardSize / 30),
                            (boardSize / 30), (boardSize / 30)));
                }
            }
        }
    }

    /**
     * torony lerakásának összese
     *
     * @param towerType
     * @return
     */
    public int towerBuildMoney(String towerType) {
        if (towerType.equals("Fortified")) {
            return 200;
        } else if (towerType.equals("Rapid")) {
            return 250;
        } else //towerType.equals("Sniper")
        {
            return 300;
        }
    }

    /**
     * ellenséges kastély koordinátája-e egység célba éréséhez használandó
     *
     * @param actualPlayer
     * @param matrixPositionX
     * @param matrixPositionY
     * @return
     */
    public boolean isItEnemyCastleCoordinate(int actualPlayer, int matrixPositionX, int matrixPositionY) {
        int enemy = (actualPlayer + 1) % 2;
        for (int i = enemy * 4; i < (enemy + 1) * 4; i++) {
            if (castleCoordinates[i][0] == matrixPositionX && castleCoordinates[i][1] == matrixPositionY) {
                return true;
            }
        }
        return false;
    }

    /**
     * van-e egység (bármelyik játékosé) az adott mezőn
     */
    private boolean isThereUnit(int matrixPositionX, int matrixPositionY) {
        for (int q = 0; q < 2; q++) {
            ArrayList<Unit> units = players[q].getUnits();
            for (int i = 0; i < units.size(); i++) {
                if (units.get(i).x / (boardSize / 30) == matrixPositionX && units.get(i).y / (boardSize / 30) == matrixPositionY) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * adott pénzből az aktuális játékos számára fejleszthető tornyok kijelölése
     *
     * @param money
     */
    public void setSelectableTowersToUpgrade(int money) {
        for (int i = 0; i < 30; i++) {
            for (int j = activePlayer * 15; j < activePlayer * 15 + 15; j++) {
                if (terrainElementPositions[i][j] == 'T' && getTowerFromPosition(i, j).getLevel() < 3 && money >= getTowerFromPosition(i, j).getUpgradePrice()) {
                    selectables.add(new Selectable(i * (boardSize / 30), j * (boardSize / 30),
                            (boardSize / 30), (boardSize / 30)));
                }
            }
        }
    }

    /**
     * aktuális játékos számára lerombolható tornyok kijelölése
     */
    public void setSelectableTowersToDemolish() {

        for (int i = 0; i < 30; i++) {
            for (int j = activePlayer * 15; j < activePlayer * 15 + 15; j++) {
                if (terrainElementPositions[i][j] == 'T') {
                    selectables.add(new Selectable(i * (boardSize / 30), j * (boardSize / 30),
                            (boardSize / 30), (boardSize / 30)));
                }
            }
        }
    }

    /**
     * egységek, tornyok, kastélyok adatainak visszaadása megjelenítéshez
     * ellenséges egységek adatait nem adja vissza
     *
     * @param matrixPositionX
     * @param matrixPositionY
     * @return
     */
    public String getInfo(int matrixPositionX, int matrixPositionY) {
        String information = "";
        for (int i = 0; i < players[activePlayer].getNotDemolishedTowers().size(); i++) {
            Tower t = players[activePlayer].getNotDemolishedTowers().get(i);
            if (t.x / (boardSize / 30) == matrixPositionX && t.y / (boardSize / 30) == matrixPositionY) {
                information = "<html><font face=\"sansserif\" color=\"black\">Tower type: " + t.type + "<br>level: " + t.level + "<br>hp: " + t.hp + ""
                        + "<br>attack speed: " + t.attackFrequency + "<br>power: " + t.power + "<br>range: " + t.range + "</font></html>";
                return information;
            }
        }

        ArrayList<Unit> units = new ArrayList<>();
        for (Unit actualUnit : players[activePlayer].getUnits()) {
            if (actualUnit.getX() / (boardSize / 30) == matrixPositionX && actualUnit.getY() / (boardSize / 30) == matrixPositionY) {
                units.add(actualUnit);
            }
        }

        if (units.size() == 1) {
            units.get(0).setX(matrixPositionX * (boardSize / 30)); //ha volt több unit és egyen kívül mind meghalt
            information += "<html><font face=\"sansserif\" color=\"black\">Unit type: " + units.get(0).type + "<br>hp: " + units.get(0).hp + ""
                    + "<br>distance: " + units.get(0).distance + "<br>power: " + units.get(0).power + "</font></html>";
            return information;
        } else if (units.size() > 1) {
            int showUnits = 5;
            if (units.size() < 5) {
                showUnits = units.size();
            }
            for (int i = 0; i < showUnits; i++) {
                units.get(i).setX(matrixPositionX * (boardSize / 30) + i);
            }
            ArrayList<String> unitTypes = new ArrayList<>();
            String typesString;
            int sumPower = 0;
            int sumHp = 0;
            for (Unit actualUnit : units) {
                if (!unitTypes.contains(actualUnit.type)) {
                    unitTypes.add(actualUnit.type);
                }
                sumPower += actualUnit.power;
                sumHp += actualUnit.hp;

            }
            typesString = unitTypes.get(0);
            for (int i = 1; i < unitTypes.size(); i++) {
                typesString += ", " + unitTypes.get(i);
            }
            information += "<html><font face=\"sansserif\" color=\"black\">" + units.size() + " units<br>Types: " + typesString + "<br>Sum power: "
                    + sumPower + "<br>Sum hp: " + sumHp + "</font></html>";
            return information;
        }

        for (int i = 0; i < 4; i++) {

            if (matrixPositionX == castleCoordinates[i][0] && matrixPositionY == castleCoordinates[i][1]) {
                information = "<html><font face=\"sansserif\" color=\"black\">" + players[0].getName() + "'s castle<br>hp: "
                        + players[0].getCastle().getHp() + "</font></html>";
                return information;
            } else if (matrixPositionX == castleCoordinates[i + 4][0] && matrixPositionY == castleCoordinates[i + 4][1]) {
                information = "<html><font face=\"sansserif\" color=\"black\">" + players[1].getName() + "'s castle<br>hp: "
                        + players[1].getCastle().getHp() + "</font></html>";
                return information;
            }
        }
        return information;
    }

    /**
     * megadott egység szomszédos mezőin lévő ellenséges tornyok listájának
     * visszaadása romboló egységhez kell
     *
     * @param actualPlayer
     * @param actualUnit
     * @return
     */
    public ArrayList<Tower> towersNearby(int actualPlayer, Unit actualUnit) {
        ArrayList<Tower> towersNearbyArray = new ArrayList<>();
        ArrayList<Tower> enemyTowers = players[(actualPlayer + 1) % 2].getTowers();
        if (enemyTowers.size() > 0) {
            for (Tower actualTower : enemyTowers) {
                if (distance(actualUnit.x / (boardSize / 30), actualTower.x / (boardSize / 30), actualUnit.y / (boardSize / 30),
                        actualTower.y / (boardSize / 30)) == 1 && terrainElementPositions[actualTower.x / 30][actualTower.y / 30] == 'T') {
                    towersNearbyArray.add(actualTower);
                }
            }
        }
        return towersNearbyArray;
    }

    /**
     * megadott egységgel azonos mezőn lévő ellenséges egységek listájának
     * visszaadása harcoló egységhez kell
     *
     * @param actualPlayer
     * @param actualUnit
     * @return
     */
    public ArrayList<Unit> enemyUnitsNearby(int actualPlayer, Unit actualUnit) {
        ArrayList<Unit> enemyUnitsNearbyArray = new ArrayList<>();
        ArrayList<Unit> enemyUnitsArray = players[(actualPlayer + 1) % 2].getUnits();

        if (enemyUnitsArray.size() > 0) {
            for (Unit enemyUnit : enemyUnitsArray) {
                if (distance(actualUnit.x / (boardSize / 30), enemyUnit.x / (boardSize / 30), actualUnit.y / (boardSize / 30), enemyUnit.y / (boardSize / 30)) == 0) {
                    enemyUnitsNearbyArray.add(enemyUnit);
                }
            }
        }
        return enemyUnitsNearbyArray;
    }

    /**
     * megadott torony hatótávolságán belüli ellenséges egységek listájának
     * visszaadása tornyok támadásához kell
     *
     * @param actualPlayer
     * @param attackingTower
     * @return
     */
    public ArrayList<Unit> enemyUnitsNearby(int actualPlayer, Tower attackingTower) {
        ArrayList<Unit> enemyUnitsNearbyArray = new ArrayList<>();
        ArrayList<Unit> enemyUnitsArray = players[(actualPlayer + 1) % 2].getUnits();

        if (enemyUnitsArray.size() > 0) {
            for (Unit enemyUnit : enemyUnitsArray) {
                if (distance(attackingTower.x / (boardSize / 30), enemyUnit.x / (boardSize / 30), attackingTower.y / (boardSize / 30), enemyUnit.y / (boardSize / 30)) <= attackingTower.range) {
                    enemyUnitsNearbyArray.add(enemyUnit);
                }
            }
        }
        return enemyUnitsNearbyArray;

    }

    /**
     * megadott pozícióban torony visszaadása, ha nincs torony, null érték tér
     * vissza
     *
     * @param matrixPositionX
     * @param matrixPositionY
     * @return
     */
    public Tower getTowerFromPosition(int matrixPositionX, int matrixPositionY) {
        for (int activePlayerIndex = 0; activePlayerIndex < 2; activePlayerIndex++) {
            for (Tower actualTower : players[activePlayerIndex].getTowers()) {
                if (actualTower.getX() / (boardSize / 30) == matrixPositionX && actualTower.getY() / (boardSize / 30) == matrixPositionY) {
                    return actualTower;
                }
            }
        }
        return null;
    }

    /**
     * getterek, setterek
     *
     * @return
     */
    public int[][] getCastleCoordinates() {
        return castleCoordinates;
    }

    public ArrayList<Sprite> getSelectables() {
        return selectables;
    }

    public void setSelectables(ArrayList<Sprite> selectablesArray) {
        selectables = selectablesArray;
    }

    public void setMap(int newMap) {
        selectedMap = newMap;
    }

    public int getMap() {
        return selectedMap;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
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

    public void addTerrainElement(Sprite newElement) {
        terrain.add(newElement);
    }

    public char[][] getPosition() {
        return terrainElementPositions;
    }

    public void setPosition(int matrixPositionX, int matrixPositionY, char newChar) {
        terrainElementPositions[matrixPositionX][matrixPositionY] = newChar;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getMcount() {
        return mountainCount;
    }

    public int getLcount() {
        return lakeCount;
    }

    public char[][] getTerrainElementPositions() {
        return terrainElementPositions;
    }
}
