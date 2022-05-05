package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import model.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.NumberFormatter;
import model.Model;
import model.Player;

public class GameWindow extends JPanel {

    private Timer timer;
    private Board board;
    private JButton backToMenuButton;
    private JButton newRoundButton;
    private JButton saveButton;
    /**
     * PLAYER1 gombok
     */
    private JButton[] player1TowerButtons;
    private JSpinner[] player1UnitSpinners;
    private JButton player1UnitDeploy;
    private JButton[] player2TowerButtons;
    private JSpinner[] player2UnitSpinners;
    private JButton player2UnitDeploy;
    private JLabel player1Data;
    private JLabel player2Data;
    private JLabel timeAndRoundLabel;
    private String selectedTower;
    private String buttonAction;
    private int timerTicks;
    private boolean simulationTime = false;
    private ArrayList<Integer> player1Distances;
    private ArrayList<Integer> player2Distances;
    private final int timerInterval = 250;
    private Model model;
    private int simulationTicks;
    private int RNDPROTECTION;
    private boolean testMode = false;
    private int fieldSize;

    private int player1GeneralCount = 0;
    private int player1FighterCount = 0;
    private int player1ClimberCount = 0;
    private int player1DiverCount = 0;
    private int player1DestroyerCount = 0;

    private int player2GeneralCount = 0;
    private int player2FighterCount = 0;
    private int player2ClimberCount = 0;
    private int player2DiverCount = 0;
    private int player2DestroyerCount = 0;

    public GameWindow() {
        super();
    }

    /**
     *
     * @param gameWindowWidth
     * @param gameWindowHeight
     * @param player1Name
     * @param player2Name
     * @param selectedMap
     */
    public GameWindow(int gameWindowWidth, int gameWindowHeight, String player1Name, String player2Name, int selectedMap) {
        super();
        board = new Board(selectedMap, player1Name, player2Name, gameWindowWidth, gameWindowHeight);
        this.model = board.getModel();
        model.setRound(1);
        //50% eséllyel kezdenek a játékosok
        Random whichPlayerStarts = new Random();
        model.setActivePlayer(whichPlayerStarts.nextInt(2));

        constructor(gameWindowWidth, gameWindowHeight);
    }

    /**
     *
     * @param gameWindowWidth
     * @param gameWindowHeight
     */
    public void constructor(int gameWindowWidth, int gameWindowHeight) {
        buttonAction = "";
        RNDPROTECTION = 0;
        fieldSize = (model.getBoardSize() / 30);
        setSize(gameWindowWidth, gameWindowHeight);
        player1Distances = new ArrayList<>();
        player2Distances = new ArrayList<>();
        timeAndRoundLabel = new JLabel();
        this.setPreferredSize(new Dimension(gameWindowWidth, gameWindowHeight));
        backToMenuButton = new JButton("Back to menu");
        newRoundButton = new JButton("Finish round");
        newRoundButton.addActionListener((event) -> {
            newRound();
        });

        //kurzor utáni info megjelenítéshez
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            }

            JTextPane text = new JTextPane() {
                @Override
                public String getToolTipText() {
                    return ((JComponent) getParent()).getToolTipText();
                }

                @Override
                public String getToolTipText(MouseEvent event) {
                    return ((JComponent) getParent()).getToolTipText(event);
                }
            };

            try {
                text.getStyledDocument().insertString(0, ".", null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            ToolTipManager.sharedInstance().registerComponent(text);
        });

        saveButton = new JButton("Save game");
        saveButton.addActionListener((event) -> saveGame());

        this.setLayout(new GridBagLayout());

 	player1TowerButtons = new JButton[5];
        player1UnitSpinners = new JSpinner[5];
        player2TowerButtons = new JButton[5];
        player2UnitSpinners = new JSpinner[5];

        setPanels();

        /**
         * PLAYER1 Tower típusok
         */
        player1TowerButtons[0].addActionListener(ae -> {
            towerPlaceAction("Fortified");
        });
        player1TowerButtons[1].addActionListener(ae -> {
            towerPlaceAction("Rapid");
        });
        player1TowerButtons[2].addActionListener(ae -> {
            towerPlaceAction("Sniper");
        });

        /**
         * PLAYER1 Tower management
         */
        player1TowerButtons[3].addActionListener(ae -> {
            if (!buttonAction.equals("")) {
                model.setSelectables(new ArrayList<>());
                buttonAction = "";
            } else {
                buttonAction = "upgrade";
                model.setSelectableTowersToUpgrade(model.getPlayers()[model.getActivePlayer()].getMoney());

            }
        });
        player1TowerButtons[4].addActionListener(ae -> {
            if (!buttonAction.equals("")) {
                model.setSelectables(new ArrayList<>());
                buttonAction = "";
            } else {
                buttonAction = "demolish";
                model.setSelectableTowersToDemolish();
            }
        });

        //PLAYER1 Unit Deploy
        player1UnitDeploy.addActionListener(ae -> {
            model.getPlayers()[0].sendUnits("General", "blue", player1GeneralCount, model);
            playerDataUpdate();
            model.getPlayers()[0].sendUnits("Fighter", "blue", player1FighterCount, model);
            playerDataUpdate();
            model.getPlayers()[0].sendUnits("Climber", "blue", player1ClimberCount, model);
            playerDataUpdate();
            model.getPlayers()[0].sendUnits("Diver", "blue", player1DiverCount, model);
            playerDataUpdate();
            model.getPlayers()[0].sendUnits("Destroyer", "blue", player1DestroyerCount, model);
            playerDataUpdate();
        });
        ////////////////////////////////////////////////////////////////////////

        /**
         * PLAYER2 Tower típusok
         */
        player2TowerButtons[0].addActionListener(ae -> {
            towerPlaceAction("Fortified");
        });
        player2TowerButtons[1].addActionListener(ae -> {
            towerPlaceAction("Rapid");
        });
        player2TowerButtons[2].addActionListener(ae -> {
            towerPlaceAction("Sniper");
        });

        /**
         * PLAYER2 Tower management
         */
        player2TowerButtons[3].addActionListener(ae -> {
            if (!buttonAction.equals("")) {
                model.setSelectables(new ArrayList<>());
                buttonAction = "";
            } else {
                buttonAction = "upgrade";
                model.setSelectableTowersToUpgrade(model.getPlayers()[model.getActivePlayer()].getMoney());
            }
        });
        player2TowerButtons[4].addActionListener(ae -> {
            if (!buttonAction.equals("")) {
                model.setSelectables(new ArrayList<>());
                buttonAction = "";
            } else {
                buttonAction = "demolish";
                model.setSelectableTowersToDemolish();
            }
        });

        //PLAYER2 Unit Deploy
        player2UnitDeploy.addActionListener(ae -> {
            model.getPlayers()[1].sendUnits("General", "red", player2GeneralCount, model);
            playerDataUpdate();
            model.getPlayers()[1].sendUnits("Fighter", "red", player2FighterCount, model);
            playerDataUpdate();
            model.getPlayers()[1].sendUnits("Climber", "red", player2ClimberCount, model);
            playerDataUpdate();
            model.getPlayers()[1].sendUnits("Diver", "red", player2DiverCount, model);
            playerDataUpdate();
            model.getPlayers()[1].sendUnits("Destroyer", "red", player2DestroyerCount, model);
            playerDataUpdate();
        });

        // torony helyének meghatározása kattintással
        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                int matrixPositionX = me.getX() / fieldSize;
                int matrixPositionY = me.getY() / fieldSize;

                int activePlayerIndex = model.getActivePlayer();

                if (buttonAction.equals("placeTower")) {
                    model.getPlayers()[activePlayerIndex].build(matrixPositionX, matrixPositionY, selectedTower, model);
                    buttonAction = "";
                } else if (buttonAction.equals("upgrade")) {
                    if (model.getTowerFromPosition(matrixPositionX, matrixPositionY) != null && model.getPosition()[matrixPositionX][matrixPositionY] == 'T') {
                        model.getPlayers()[activePlayerIndex].upgrade(matrixPositionX, matrixPositionY, model.getBoardSize());
                        model.setSelectables(new ArrayList<>());
                    }
                    buttonAction = "";
                } else if (buttonAction.equals("demolish")) {
                    if (model.getTowerFromPosition(matrixPositionX, matrixPositionY) != null) {
                        model.getPlayers()[activePlayerIndex].demolish(matrixPositionX, matrixPositionY, model.getBoardSize());
                        model.setPosition(matrixPositionX, matrixPositionY, 'D');
                        model.setSelectables(new ArrayList<>());
                    }
                    buttonAction = "";
                }
                board.repaint();
                playerDataUpdate();
            }
        });

        //timerTicks-et betöltés közben állítjuk be
        if (!(timerTicks > 0)) {
            timerTicks = (1000 / timerInterval) * 60;
        }

        timer = new Timer(timerInterval, (ActionEvent ae) -> {
            PointerInfo hover = MouseInfo.getPointerInfo();
            Point hoveredPoint = hover.getLocation();

            int boardPositionX = (int) hoveredPoint.getX() - 500;
            int matrixPositionX = boardPositionX / fieldSize;
            int boardPositionY = (int) hoveredPoint.getY() - 65;
            int matrixPositionY = boardPositionY / fieldSize;

            if (!model.getInfo(matrixPositionX, matrixPositionY).equals("")) {
                ToolTipManager.sharedInstance().setEnabled(true);
                board.setToolTipText(model.getInfo(matrixPositionX, matrixPositionY));

            } else {
                ToolTipManager.sharedInstance().setEnabled(false);
            }

            if (simulationTime) {
                simulationTime = simulation();

                for (int playerIndex = 0; playerIndex < 2; playerIndex++) {
                    ArrayList<Tower> towers = model.getPlayers()[playerIndex].getNotDemolishedTowers();
                    for (int towerIndex = 0; towerIndex < towers.size(); towerIndex++) {
                        double tickPerAttack = towers.get(towerIndex).getAttackFrequency() / 0.25;
                        if (simulationTicks % tickPerAttack == 0) {
                            ArrayList<Unit> enemyUnitsNearby = model.enemyUnitsNearby(playerIndex, towers.get(towerIndex));
                            if (enemyUnitsNearby.size() > 0) {
                                int randomEnemyUnit = (int) (Math.random() * enemyUnitsNearby.size());
                                towers.get(towerIndex).setShootCords(enemyUnitsNearby.get(randomEnemyUnit).getX(), enemyUnitsNearby.get(randomEnemyUnit).getY());
                                if (enemyUnitsNearby.get(randomEnemyUnit).getHp() > towers.get(towerIndex).getPower()) {
                                    enemyUnitsNearby.get(randomEnemyUnit).setHp(enemyUnitsNearby.get(randomEnemyUnit).getHp() - towers.get(towerIndex).getPower());
                                } else {
                                    model.getPlayers()[playerIndex].setMoney(model.getPlayers()[playerIndex].getMoney() + enemyUnitsNearby.get(randomEnemyUnit).getMaxHp() * 2);
                                    model.getPlayers()[(playerIndex + 1) % 2].deleteUnit(enemyUnitsNearby.get(randomEnemyUnit));
                                    playerDataUpdate();
                                }
                            }
                        }
                    }
                }
                simulationTicks++;
                if (model.getRound() == 1 || model.getRound() == 2) {
                    timerTicks = (1000 / timerInterval) * 60;
                } else {
                    timerTicks = (1000 / timerInterval) * 30;
                }
            } else {
                for (int buttonIndex = 0; buttonIndex < 5; buttonIndex++) {
                    if (model.getActivePlayer() == 0) {
                        player1TowerButtons[buttonIndex].setEnabled(true);
                        player1UnitSpinners[buttonIndex].setEnabled(true);
                        player1UnitDeploy.setEnabled(true);
                    } else {
                        player2TowerButtons[buttonIndex].setEnabled(true);
                        player2UnitSpinners[buttonIndex].setEnabled(true);
                        player2UnitDeploy.setEnabled(true);
                    }
                }
                newRoundButton.setEnabled(true);
                player1Distances.clear();
                player2Distances.clear();

                for (int playerIndex = 0; playerIndex < 2; playerIndex++) {
                    int defendingPlayer = Math.abs(playerIndex * 4 - 4);
                    ArrayList<Unit> updateUnits = model.getPlayers()[playerIndex].getUnits();
                    for (Unit u : updateUnits) {
                        if (playerIndex == 0) {
                            player1Distances.add(u.getDistance());
                        } else {
                            player2Distances.add(u.getDistance());
                        }
                        int minWayDifficulty = 10000;
                        ArrayList<Node> bestWayNode = new ArrayList<>();

                        for (int castleCoordinateIndex = 0; castleCoordinateIndex < 4; castleCoordinateIndex++) {
                            Player actualPlayer = model.getPlayers()[playerIndex];
                            ArrayList<String> bestWayString = actualPlayer.findBestWay(u.getX() / fieldSize, u.getY() / fieldSize,
                                    model.getCastleCoordinates()[castleCoordinateIndex + defendingPlayer][0],
                                    model.getCastleCoordinates()[castleCoordinateIndex + defendingPlayer][1], actualPlayer.getDifficulty(model, u.getType()));
                            int actualWayDifficulty = actualPlayer.convertWay(bestWayString).size();
                            if (minWayDifficulty > actualWayDifficulty) {
                                minWayDifficulty = actualWayDifficulty;
                                bestWayNode = actualPlayer.convertWay(bestWayString);
                            }
                        }
                        if (!bestWayNode.isEmpty()) {
                            u.setWay(bestWayNode);
                        }
                        model.getPlayers()[playerIndex].setUnits(updateUnits);
                    }
                }
            }

            timerTicks--;

            int newTimerTicks = (timerTicks + 1) / 2;
            if (newTimerTicks == 0) {
                newRound();
            } else {
                timeAndRoundLabel.setText("Time left: " + (timerTicks + 1) / (1000 / timerInterval)
                        + " sec // Round: " + (model.getRound() + 1) / 2);
            }
            board.repaint();

        }
        );

        timer.start();
        activePlayerPanelSetter();
    }

    /**
     * 1 szimulációs menet: egységek lépnek, harcos sebez, romboló rombol,
     * tornyok sebeznek
     *
     * @return
     */
    public boolean simulation() {
        boolean moreDistance = false;
        boolean isOver = false;
        for (int playerIndex = 0; playerIndex < 2; playerIndex++) {
            if (!model.getPlayers()[playerIndex].getUnits().isEmpty()) {
                ArrayList<Unit> units = model.getPlayers()[playerIndex].getUnits();
                for (int unitIndex = 0; unitIndex < units.size(); unitIndex++) {
                    if (playerIndex == 0) {
                        if (player1Distances.get(unitIndex) <= 0) {
                            continue;
                        }
                    } else {
                        if (player2Distances.get(unitIndex) <= 0) {
                            continue;
                        }
                    }

                    if (units.get(unitIndex).getWay() != null && !units.get(unitIndex).getWay().isEmpty()) {
                        ArrayList<Node> unitWay = units.get(unitIndex).getWay();
                        Node next = unitWay.get(0);
                        units.get(unitIndex).move(next.getX() * fieldSize, next.getY() * fieldSize);
                        board.repaint();
                        unitWay.remove(0);
                        units.get(unitIndex).setWay(unitWay);
                    }

                    if (playerIndex == 0) {
                        player1Distances.set(unitIndex, player1Distances.get(unitIndex) - 1);
                        if (player1Distances.get(unitIndex) > 0) {
                            moreDistance = true;
                        }
                    } else {
                        player2Distances.set(unitIndex, player2Distances.get(unitIndex) - 1);
                        if (player2Distances.get(unitIndex) > 0) {
                            moreDistance = true;
                        }
                    }
                    //Fighter sebez egy elhaladó ellenséges egységet, ha azonos mezőre értek
                    ArrayList<Unit> enemyUnitsNearby = model.enemyUnitsNearby(playerIndex, units.get(unitIndex));
                    if ("Fighter".equals(units.get(unitIndex).getType()) && enemyUnitsNearby.size() > 0) {
                        int randomEnemyUnit = (int) (Math.random() * enemyUnitsNearby.size());
                        units.get(unitIndex).setBlood(true);
                        if (enemyUnitsNearby.get(randomEnemyUnit).getHp() > units.get(unitIndex).getPower()) {
                            enemyUnitsNearby.get(randomEnemyUnit).setHp(enemyUnitsNearby.get(randomEnemyUnit).getHp() - units.get(unitIndex).getPower());
                            if (units.get(unitIndex).getHp() > enemyUnitsNearby.get(randomEnemyUnit).getPower() && "Fighter".equals(enemyUnitsNearby.get(randomEnemyUnit).getType())) {
                                units.get(unitIndex).setHp(units.get(unitIndex).getHp() - enemyUnitsNearby.get(randomEnemyUnit).getPower());
                            } else if ("Fighter".equals(enemyUnitsNearby.get(randomEnemyUnit).getType())) {
                                model.getPlayers()[playerIndex].deleteUnit(units.get(unitIndex));
                                if (unitIndex > 0) {
                                    unitIndex--;
                                } else {
                                    break;
                                }
                                model.getPlayers()[Math.abs(playerIndex - 1)].setMoney(model.getPlayers()[Math.abs(playerIndex - 1)].getMoney() + units.get(unitIndex).getMaxHp() * 2);
                            }
                        } else {
                            model.getPlayers()[playerIndex].setMoney(model.getPlayers()[playerIndex].getMoney() + enemyUnitsNearby.get(randomEnemyUnit).getMaxHp() * 2);
                            if (units.get(unitIndex).getHp() > enemyUnitsNearby.get(randomEnemyUnit).getPower() && "Fighter".equals(enemyUnitsNearby.get(randomEnemyUnit).getType())) {
                                units.get(unitIndex).setHp(units.get(unitIndex).getHp() - enemyUnitsNearby.get(randomEnemyUnit).getPower());
                            } else if ("Fighter".equals(enemyUnitsNearby.get(randomEnemyUnit).getType())) {
                                model.getPlayers()[playerIndex].deleteUnit(units.get(unitIndex));
                                if (unitIndex > 0) {
                                    unitIndex--;
                                } else {
                                    break;
                                }
                                model.getPlayers()[Math.abs(playerIndex - 1)].setMoney(model.getPlayers()[Math.abs(playerIndex - 1)].getMoney() + units.get(unitIndex).getMaxHp() * 2);
                            }
                            model.getPlayers()[(playerIndex + 1) % 2].deleteUnit(enemyUnitsNearby.get(randomEnemyUnit));
                            if (unitIndex > 0) {
                                unitIndex--;
                            } else {
                                break;
                            }
                        }
                    } else if (!enemyUnitsNearby.isEmpty()) {
                        for (int enemyUnitIndex = 0; enemyUnitIndex < enemyUnitsNearby.size(); enemyUnitIndex++) {
                            Unit enemyUnit = enemyUnitsNearby.get(enemyUnitIndex);
                            if (enemyUnit.getType().equals("Fighter")) {
                                units.get(unitIndex).setBlood(true);
                                if (units.get(unitIndex).getHp() < enemyUnit.getPower()) {
                                    model.getPlayers()[Math.abs(playerIndex - 1)].setMoney(model.getPlayers()[Math.abs(playerIndex - 1)].getMoney() + units.get(unitIndex).getMaxHp() * 2);
                                    model.getPlayers()[playerIndex].deleteUnit(units.get(unitIndex));
                                    if (unitIndex > 0) {
                                        unitIndex--;
                                    } else {
                                        break;
                                    }
                                    model.getPlayers()[Math.abs(playerIndex - 1)].setMoney(model.getPlayers()[Math.abs(playerIndex - 1)].getMoney() + units.get(unitIndex).getMaxHp() * 2);
                                } else {
                                    units.get(unitIndex).setHp(units.get(unitIndex).getHp() - enemyUnit.getPower());
                                }

                            }
                        }
                    }
                    if (units.size() > 0) {
                        //Destroyer támadja a mellette lévő tornyot
                        ArrayList<Tower> towersNearby = model.towersNearby(playerIndex, units.get(unitIndex));
                        if ("Destroyer".equals(units.get(unitIndex).getType()) && towersNearby.size() > 0) {
                            int attackTower = (int) (Math.random() * 2); //a támadás esélye 50% (randomizált)
                            if (RNDPROTECTION != 0) {
                                attackTower = RNDPROTECTION;
                            }
                            if (attackTower == 1) {
                                //Destroyer 50-et sebez a toronyba, majd mesemmisül
                                for (int towerIndex = 0; towerIndex < towersNearby.size(); towerIndex++) {

                                    if (towersNearby.get(towerIndex).getDemolishedIn() != -1) {
                                        continue;
                                    }
                                    Tower towerToDemolish = towersNearby.get(towerIndex);
                                    if (towerToDemolish.getHp() > 50) {
                                        towerToDemolish.setHp(towersNearby.get(towerIndex).getHp() - 50);
                                        int towerToDemolishIndex = model.getPlayers()[Math.abs(playerIndex - 1)].getTowerIndex(towersNearby.get(towerIndex));
                                        if (towerToDemolishIndex != -1) {
                                            model.getPlayers()[Math.abs(playerIndex - 1)].getTowers().get(towerToDemolishIndex).setExploded(true);
                                        }
                                        model.getPlayers()[playerIndex].deleteUnit(units.get(unitIndex));
                                        if (unitIndex > 0) {
                                            unitIndex--;
                                        }
                                        break;
                                    } else //torony megsemmisül
                                    {
                                        towerToDemolish.setHp(0);
                                        towerToDemolish.setPower(0);
                                        towerToDemolish.setRange(0);
                                        towerToDemolish.setAttackFrequency(0);
                                        model.setPosition(towerToDemolish.getX() / fieldSize, towerToDemolish.getY() / fieldSize, 'D');
                                        model.getPlayers()[(playerIndex + 1) % 2].demolish(towersNearby.get(towerIndex).getX() / fieldSize,
                                                towersNearby.get(towerIndex).getY() / fieldSize, model.getBoardSize());
                                        model.getPlayers()[playerIndex].setMoney(model.getPlayers()[playerIndex].getMoney() + towersNearby.get(towerIndex).getMaxHp() * 3);
                                        int towerToDemolishIndex = model.getPlayers()[playerIndex].getTowerIndex(towersNearby.get(towerIndex));

                                        if (towerToDemolishIndex != -1) {
                                            model.getPlayers()[playerIndex].getTowers().get(towerToDemolishIndex).setExploded(true);
                                        }
                                        model.getPlayers()[playerIndex].deleteUnit(units.get(unitIndex));
                                        if (unitIndex > 0) {
                                            unitIndex--;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    board.repaint();
                }
            }

            Player currentPlayer = model.getPlayers()[playerIndex];
            ArrayList<Unit> currentPlayerUnits = currentPlayer.getUnits();
            Player enemyPlayer = model.getPlayers()[Math.abs(playerIndex - 1)];

            for (int unitIndex = 0; unitIndex < currentPlayerUnits.size();) {
                Unit cpu = currentPlayerUnits.get(unitIndex);
                if (model.isItEnemyCastleCoordinate(playerIndex, cpu.getX() / fieldSize, cpu.getY() / fieldSize)) {
                    if (enemyPlayer.getCastle().getHp() - cpu.getPower() > 0) {
                        enemyPlayer.getCastle().setHp(enemyPlayer.getCastle().getHp() - cpu.getPower());
                    } else {
                        enemyPlayer.getCastle().setHp(0);
                        isOver = true;
                    }
                    model.getPlayers()[playerIndex].deleteUnit(cpu);
                    board.repaint();
                    //egységek sebzik a kastélyt miután beértek, majd megsemmisülnek
                } else {
                    unitIndex++;
                }
            }
        }
        if (isOver) {
            simulationTime = false;
            board.repaint();
            if (!testMode) {
                gameOver();
            }
        }

        return moreDistance;
    }

    /**
     * torony építés során elérhető helyek megjelenítése, ha van elég pénz a
     * toronyra
     *
     * @param towerType
     */
    public void towerPlaceAction(String towerType) {
        if (!buttonAction.equals("")) {
            buttonAction = "";
            model.setSelectables(new ArrayList<>());
        } else {
            selectedTower = towerType;
            buttonAction = "placeTower";
            if (model.getPlayers()[model.getActivePlayer()].getMoney() >= model.towerBuildMoney(towerType)) {
                model.setSelectables();
            }
        }
    }

    /**
     * játékosok körönként váltják egymást, minden szimuláció 2 körönként
     * történik - ezután kap mindkét játékos 100-100 aranyat
     */
    public void newRound() {
        model.setSelectables(new ArrayList<>());
        model.setRound(model.getRound() + 1);

        if (model.getRound() == 1 || model.getRound() == 2) {
            timerTicks = (1000 / timerInterval) * 60;
        } else {
            timerTicks = (1000 / timerInterval) * 30;
        }

        model.setActivePlayer((1 + model.getActivePlayer()) % 2);
        repaint();
        playerDataUpdate();
        activePlayerPanelSetter();

        if (model.getRound() % 2 == 1) {
            for (int buttonIndex = 0; buttonIndex < 5; buttonIndex++) {
                player1TowerButtons[buttonIndex].setEnabled(false);
                player1UnitSpinners[buttonIndex].setEnabled(false);
                player1UnitDeploy.setEnabled(false);    //for-on kívülre?
                player2TowerButtons[buttonIndex].setEnabled(false);
                player2UnitSpinners[buttonIndex].setEnabled(false);
                player2UnitDeploy.setEnabled(false);    //for-on kívülre?
            }
            newRoundButton.setEnabled(false);
            simulationTicks = 0;
            simulationTime = true;

            model.getPlayers()[0].setMoney(model.getPlayers()[0].getMoney() + 100);
            model.getPlayers()[1].setMoney(model.getPlayers()[1].getMoney() + 100);
            playerDataUpdate();

        }

        ArrayList<Tower> player1Towers = model.getPlayers()[0].getTowers();
        for (int towerIndex = player1Towers.size() - 1; towerIndex >= 0; towerIndex--) {
            if (player1Towers.get(towerIndex).getDemolishedIn() != -1) {
                player1Towers.get(towerIndex).setDemolishedIn(1);//demolishedIn - 1
                if (player1Towers.get(towerIndex).getDemolishedIn() == 0) {
                    model.getTerrain().remove(player1Towers.get(towerIndex));
                    model.setPosition(player1Towers.get(towerIndex).getX() / 30, player1Towers.get(towerIndex).getY() / 30, 'F');
                    player1Towers.remove(towerIndex);
                }
            }
        }

        model.getPlayers()[0].setTowers(player1Towers);
        ArrayList<Tower> player2Towers = model.getPlayers()[1].getTowers();
        for (int towerIndex = player2Towers.size() - 1; towerIndex >= 0; towerIndex--) {
            if (player2Towers.get(towerIndex).getDemolishedIn() != -1) {
                player2Towers.get(towerIndex).setDemolishedIn(1);//demolishedIn - 1
                if (player2Towers.get(towerIndex).getDemolishedIn() == 0) {
                    model.getTerrain().remove(player2Towers.get(towerIndex));
                    model.setPosition(player2Towers.get(towerIndex).getX() / 30, player2Towers.get(towerIndex).getY() / 30, 'F');
                    player2Towers.remove(towerIndex);
                }
            }
        }
        model.getPlayers()[1].setTowers(player2Towers);
    }

    /**
     * játék végén felugró üzenet
     */
    public void gameOver() {
        JFrame newFrame = new JFrame();
        if (model.getPlayers()[0].getCastle().getHp() == 0 && model.getPlayers()[1].getCastle().getHp() == 0) {
            JOptionPane.showMessageDialog(newFrame, "The game ended in a draw.");
        } else {
            if (model.getPlayers()[0].getCastle().getHp() == 0) {
                JOptionPane.showMessageDialog(newFrame, model.getPlayers()[0].getName() + " won, congratulations!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(newFrame, model.getPlayers()[1].getName() + " won, congratulations!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        System.exit(0);
    }

    /**
     * játék mentése txt fájlba
     */
    public void saveGame() {
        String filename;
        filename = JOptionPane.showInputDialog(null, "Filename: ", "Save to file (click cancel to not save)", JOptionPane.QUESTION_MESSAGE);
        if (filename == null || filename.length() == 0) {
            return;
        }
        model.saveData(filename, timerTicks);
    }

    /**
     * grafikus rész
     *
     * @param grph
     */
    @Override
    protected void paintComponent(Graphics grph) {
        super.paintComponent(grph);
        Graphics2D grphcs2 = (Graphics2D) grph;
        grph.drawImage(new ImageIcon("src/res/StartScreen Background.png").getImage(), 0, 0, 1920, 1080, null);

        Color sandBrown = new Color(210, 180, 140);
        Color brightCinnabar = new Color(247, 73, 54);  //red: ON round
        Color dullPastelRed = new Color(255, 172, 166); //red: NOT ON round
        Color brightRoyalBlue = new Color(71, 112, 249);//blue: ON round
        Color dullBlueGray = new Color(154, 180, 206);  //blue: NOT ON round

        //1st layer UI (bottom)
        grph.setColor(Color.DARK_GRAY);
        grphcs2.fillRoundRect(27, 110, 437, 941, 25, 25);   //left
        grphcs2.fillRoundRect(1456, 110, 437, 941, 25, 25); //right
        grphcs2.fillRoundRect(480, 10, 959, 1062, 22, 22);  //center
        grphcs2.fillRoundRect(27, 23, 437, 70, 25, 25);     //save
        grphcs2.fillRoundRect(1456, 23, 437, 70, 25, 25);   //exit

        //2nd layer UI (middle - coloring)
        if (model.getActivePlayer() == 0) {
            grph.setColor(brightRoyalBlue);
            grphcs2.fillRoundRect(35, 118, 421, 925, 25, 25);   //left
            grph.setColor(dullPastelRed);
            grphcs2.fillRoundRect(1465, 118, 421, 925, 25, 25); //right
        } else {
            grph.setColor(dullBlueGray);
            grphcs2.fillRoundRect(35, 118, 421, 925, 25, 25);   //left
            grph.setColor(brightCinnabar);
            grphcs2.fillRoundRect(1465, 118, 421, 925, 25, 25); //right
        }
        grph.setColor(sandBrown);
        grphcs2.fillRoundRect(34, 30, 423, 56, 22, 22);     //save
        grphcs2.fillRoundRect(1463, 30, 423, 56, 22, 22);   //exit
        grphcs2.fillRoundRect(489, 18, 942, 985, 22, 22);   //center

        //3rd layer UI (top)
        grph.setColor(Color.DARK_GRAY);
        grphcs2.fillRoundRect(41, 123, 408, 915, 22, 22);   //left
        grphcs2.fillRoundRect(1471, 123, 408, 915, 22, 22); //right
        grphcs2.fillRect(114, 35, 266, 46);                 //save
        grphcs2.fillRect(1539, 35, 266, 46);                //exit
    }

    /**
     * UI-on megjelenő gombok elhelyezése
     */
    public final void setPanels() {
        Color algaeGreen = new Color(105, 168, 120);
        Color veryLightGray = new Color(220, 220, 220);

        JPanel player1Panel = new JPanel(); //whole panel
        JPanel player1Stats = new JPanel(); //legfelső (name/cas hp/money)
        JPanel player1TwrLabelRow = new JPanel(); //tower text
        JPanel player1TowerOptionRow = new JPanel(); //tower button options
        JPanel player1TwrManLabelRow = new JPanel(); //tower management text
        JPanel player1TowerManagementOptionRow = new JPanel(); //tower management button options
        JPanel player1UnitLabelRow = new JPanel(); //unit text
        JPanel player1UnitOptRow = new JPanel(); //unit button options
        JPanel player1UnitDepRow = new JPanel(); //unit deploy button

        player1Data = new JLabel();
        JLabel player1Twr = new JLabel("Towers");
        JLabel player1FortStat = new JLabel("200 c / tower");
        JLabel player1RapStat = new JLabel("250 c / tower");
        JLabel player1SnipStat = new JLabel("300 c / tower");
        JLabel player1TwrMan = new JLabel("Tower Management");
        JLabel player1UnitLab = new JLabel("Units");
        JLabel player1GenStat = new JLabel("0 coins");
        JLabel player1FigStat = new JLabel("0 coins");
        JLabel player1ClimStat = new JLabel("0 coins");
        JLabel player1DivStat = new JLabel("0 coins");
        JLabel player1DesStat = new JLabel("0 coins");

        JPanel player2Panel = new JPanel();
        JPanel player2Stats = new JPanel();
        JPanel player2TwrLabelRow = new JPanel();
        JPanel player2TowerOptionRow = new JPanel();
        JPanel player2TwrManLabelRow = new JPanel();
        JPanel player2TowerManagementOptionRow = new JPanel();
        JPanel player2UnitLabelRow = new JPanel();
        JPanel player2UnitOptRow = new JPanel();
        JPanel player2UnitDepRow = new JPanel();

        player2Data = new JLabel();
        JLabel player2Twr = new JLabel("Towers");
        JLabel player2FortStat = new JLabel("200 c / tower");
        JLabel player2RapStat = new JLabel("250 c / tower");
        JLabel player2SnipStat = new JLabel("300 c / tower");
        JLabel player2TwrMan = new JLabel("Tower Management");
        JLabel player2UnitLab = new JLabel("Units");
        JLabel player2GenStat = new JLabel("0 coins");
        JLabel player2DivStat = new JLabel("0 coins");
        JLabel player2ClimStat = new JLabel("0 coins");
        JLabel player2FigStat = new JLabel("0 coins");
        JLabel player2DesStat = new JLabel("0 coins");

        playerDataUpdate();

        player1TowerButtons[0] = new JButton("Fortified");
        player1TowerButtons[1] = new JButton("Rapid");
        player1TowerButtons[2] = new JButton("Sniper");
        player1TowerButtons[3] = new JButton("Upgrade");
        player1TowerButtons[4] = new JButton("Demolish");

        JLabel player1General = new JLabel("General");
        JLabel player1Fighter = new JLabel("Fighter");
        JLabel player1Climber = new JLabel("Climber");
        JLabel player1Diver = new JLabel("Diver");
        JLabel player1Destroyer = new JLabel("Destroyer");

        player1UnitDeploy = new JButton("Deploy");
        JLabel player1UnitTotal = new JLabel("Total: 0");

        player2TowerButtons[0] = new JButton("Fortified");
        player2TowerButtons[1] = new JButton("Rapid");
        player2TowerButtons[2] = new JButton("Sniper");
        player2TowerButtons[3] = new JButton("Upgrade");
        player2TowerButtons[4] = new JButton("Demolish");

        JLabel player2General = new JLabel("General");
        JLabel player2Fighter = new JLabel("Fighter");
        JLabel player2Climber = new JLabel("Climber");
        JLabel player2Diver = new JLabel("Diver");
        JLabel player2Destroyer = new JLabel("Destroyer");

        player2UnitDeploy = new JButton("Deploy");
        JLabel player2UnitTotal = new JLabel("Total: 0");

        this.setBackground(algaeGreen);

        //player1 panels
        player1Panel.setLayout(new GridBagLayout());
        player1TowerOptionRow.setLayout(new GridBagLayout());
        player1TowerManagementOptionRow.setLayout(new GridBagLayout());
        player1UnitOptRow.setLayout(new GridBagLayout());
        player1UnitDepRow.setLayout(new GridBagLayout());

        //player2 panels
        player2Panel.setLayout(new GridBagLayout());
        player2TowerOptionRow.setLayout(new GridBagLayout());
        player2TowerManagementOptionRow.setLayout(new GridBagLayout());
        player2UnitOptRow.setLayout(new GridBagLayout());
        player2UnitDepRow.setLayout(new GridBagLayout());

        //Elhelyezés
        GridBagConstraints gbc = new GridBagConstraints();

        GridBagConstraints gbl = new GridBagConstraints();
        GridBagConstraints gbtl = new GridBagConstraints();
        GridBagConstraints gbml = new GridBagConstraints();
        GridBagConstraints gbul = new GridBagConstraints();
        GridBagConstraints gbudl = new GridBagConstraints();

        GridBagConstraints gbr = new GridBagConstraints();
        GridBagConstraints gbtr = new GridBagConstraints();
        GridBagConstraints gbmr = new GridBagConstraints();
        GridBagConstraints gbur = new GridBagConstraints();
        GridBagConstraints gbudr = new GridBagConstraints();

        try {
            //számoknak: poppins
            //szavaknak: nunito
            Font poppinsLight = Font.createFont(Font.TRUETYPE_FONT, new File("src/res/fonts/Poppins-Light.ttf"));
            Font nunitoLight = Font.createFont(Font.TRUETYPE_FONT, new File("src/res/fonts/Nunito-Light.ttf"));

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/res/fonts/Poppins-Light.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/res/fonts/Nunito-Light.ttf")));

            //Játéktér
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0, 45, 0, 45);
            gbc.gridx = 1;
            gbc.gridy = 1;
            this.add(board, gbc);

            //Save game BUTTON
            saveButton.setPreferredSize(new Dimension(260, 40));
            saveButton.setFont(nunitoLight.deriveFont(25f));
            saveButton.setMargin(new Insets(0, 0, 0, 0));
            saveButton.setFocusPainted(false);
            gbc.insets = new Insets(0, 0, -22, 0);
            gbc.gridx = 0;
            gbc.gridy = 0;
            this.add(saveButton, gbc);

            //Exit game BUTTON
            backToMenuButton.setPreferredSize(new Dimension(260, 40));
            backToMenuButton.setFont(nunitoLight.deriveFont(25f));
            backToMenuButton.setMargin(new Insets(0, 0, 0, 0));
            backToMenuButton.setFocusPainted(false);
            gbc.insets = new Insets(0, 0, -22, 0);
            gbc.gridx = 2;
            gbc.gridy = 0;
            this.add(backToMenuButton, gbc);

            //Finish round BUTTON
            newRoundButton.setPreferredSize(new Dimension(932, 50));
            newRoundButton.setFont(nunitoLight.deriveFont(32f));
            newRoundButton.setMargin(new Insets(0, 0, 0, 0));
            newRoundButton.setFocusPainted(false);

            newRoundButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < 5; i++) {
                        player1UnitSpinners[i].setValue(0);
                    }
                    player1GenStat.setText("0 coins");
                    player1FigStat.setText("0 coins");
                    player1ClimStat.setText("0 coins");
                    player1DivStat.setText("0 coins");
                    player1DesStat.setText("0 coins");
                    player1UnitTotal.setText("Total: 0");
                }
            });

            gbc.insets = new Insets(15, 0, -10, 0);
            gbc.gridx = 1;
            gbc.gridy = 2;
            this.add(newRoundButton, gbc);

            //Round time & count LABEL
            timeAndRoundLabel.setFont(new Font("Calibri", Font.BOLD, 30));
            gbc.insets = new Insets(2, 0, 0, 0);
            gbc.gridx = 1;
            gbc.gridy = 0;
            this.add(timeAndRoundLabel, gbc);

            /**
             * Player 1 UI - player1Panel MAIN
             */
            player1Panel.setPreferredSize(new Dimension(400, 908));
            player1Panel.setBackground(Color.DARK_GRAY);

            //STATOK: név + pénz + kastély hp
            player1Stats.add(player1Data);
            player1Stats.setBackground(veryLightGray);

            player1Data.setPreferredSize(new Dimension(380, 72));
            player1Data.setFont(poppinsLight.deriveFont(28f));
            player1Data.setBorder(new EmptyBorder(0, 5, 8, 0));

            gbl.insets = new Insets(-250, 0, 40, 0);
            gbl.gridx = 0;
            gbl.gridy = 0;
            player1Panel.add(player1Stats, gbl);

            //Tower LABEL
            player1TwrLabelRow.add(player1Twr);
            player1TwrLabelRow.setBorder(new EmptyBorder(8, -245, 0, 0));
            player1TwrLabelRow.setBackground(veryLightGray);
            player1TwrLabelRow.setPreferredSize(new Dimension(390, 60));

            player1Twr.setFont(new Font("Calibri", Font.PLAIN, 40));

            gbl.insets = new Insets(-100, 0, 0, 0);
            gbl.gridx = 0;
            gbl.gridy = 1;
            player1Panel.add(player1TwrLabelRow, gbl);

            //Tower BUTTONS
            player1TowerButtons[0].setPreferredSize(new Dimension(180, 40));
            player1TowerButtons[0].setFont(nunitoLight.deriveFont(25f));
            player1TowerButtons[0].setMargin(new Insets(0, 0, 0, 0));
            player1TowerButtons[0].setFocusPainted(false);
            gbtl.insets = new Insets(2, -170, 3, 0);
            gbtl.gridx = 0;
            gbtl.gridy = 0;
            player1TowerOptionRow.add(player1TowerButtons[0], gbtl);

            player1FortStat.setFont(poppinsLight.deriveFont(26f));
            gbtl.insets = new Insets(5, 12, 0, -180);
            gbtl.gridx = 1;
            gbtl.gridy = 0;
            player1TowerOptionRow.add(player1FortStat, gbtl);

            player1TowerButtons[1].setPreferredSize(new Dimension(180, 40));
            player1TowerButtons[1].setFont(nunitoLight.deriveFont(25f));
            player1TowerButtons[1].setMargin(new Insets(0, 0, 0, 0));
            player1TowerButtons[1].setFocusPainted(false);
            gbtl.insets = new Insets(2, -170, 3, 0);
            gbtl.gridx = 0;
            gbtl.gridy = 1;
            player1TowerOptionRow.add(player1TowerButtons[1], gbtl);

            player1RapStat.setFont(poppinsLight.deriveFont(26f));
            gbtl.insets = new Insets(5, 12, 0, -180);
            gbtl.gridx = 1;
            gbtl.gridy = 1;
            player1TowerOptionRow.add(player1RapStat, gbtl);

            player1TowerButtons[2].setPreferredSize(new Dimension(180, 40));
            player1TowerButtons[2].setFont(nunitoLight.deriveFont(25f));
            player1TowerButtons[2].setMargin(new Insets(0, 0, 0, 0));
            player1TowerButtons[2].setFocusPainted(false);
            gbtl.insets = new Insets(2, -170, 3, 0);
            gbtl.gridx = 0;
            gbtl.gridy = 2;
            player1TowerOptionRow.add(player1TowerButtons[2], gbtl);

            player1SnipStat.setFont(poppinsLight.deriveFont(26f));
            gbtl.insets = new Insets(5, 12, 0, -180);
            gbtl.gridx = 1;
            gbtl.gridy = 2;
            player1TowerOptionRow.add(player1SnipStat, gbtl);

            player1TowerOptionRow.setBackground(Color.LIGHT_GRAY);
            player1TowerOptionRow.setPreferredSize(new Dimension(390, 160));

            gbl.insets = new Insets(-20, 0, 20, 0);
            gbl.gridx = 0;
            gbl.gridy = 2;
            player1Panel.add(player1TowerOptionRow, gbl);

            //Tower management LABEL
            player1TwrManLabelRow.add(player1TwrMan);
            player1TwrManLabelRow.setBorder(new EmptyBorder(8, 0, 0, 30));
            player1TwrManLabelRow.setBackground(veryLightGray);
            player1TwrManLabelRow.setPreferredSize(new Dimension(390, 60));

            player1TwrMan.setFont(new Font("Calibri", Font.PLAIN, 40));

            gbl.insets = new Insets(0, 0, 0, 0);
            gbl.gridx = 0;
            gbl.gridy = 3;
            player1Panel.add(player1TwrManLabelRow, gbl);

            //Tower management BUTTONS
            player1TowerButtons[3].setPreferredSize(new Dimension(260, 40));
            player1TowerButtons[3].setFont(nunitoLight.deriveFont(25f));
            player1TowerButtons[3].setMargin(new Insets(0, 0, 0, 0));
            player1TowerButtons[3].setFocusPainted(false);
            gbml.insets = new Insets(4, 0, 3, 0);
            gbml.gridx = 0;
            gbml.gridy = 0;
            player1TowerManagementOptionRow.add(player1TowerButtons[3], gbml);

            player1TowerButtons[4].setPreferredSize(new Dimension(260, 40));
            player1TowerButtons[4].setFont(nunitoLight.deriveFont(25f));
            player1TowerButtons[4].setMargin(new Insets(0, 0, 0, 0));
            player1TowerButtons[4].setFocusPainted(false);
            gbml.insets = new Insets(3, 0, 4, 0);
            gbml.gridx = 0;
            gbml.gridy = 1;
            player1TowerManagementOptionRow.add(player1TowerButtons[4], gbml);

            player1TowerManagementOptionRow.setBackground(Color.LIGHT_GRAY);
            player1TowerManagementOptionRow.setPreferredSize(new Dimension(390, 110));

            gbl.insets = new Insets(0, 0, 20, 0);
            gbl.gridx = 0;
            gbl.gridy = 4;
            player1Panel.add(player1TowerManagementOptionRow, gbl);

            //Units LABEL
            player1UnitLabelRow.add(player1UnitLab);
            player1UnitLabelRow.setBorder(new EmptyBorder(8, -280, 0, 0));
            player1UnitLabelRow.setBackground(veryLightGray);
            player1UnitLabelRow.setPreferredSize(new Dimension(390, 60));

            player1UnitLab.setFont(new Font("Calibri", Font.PLAIN, 40));

            gbl.insets = new Insets(0, 0, 0, 0);
            gbl.gridx = 0;
            gbl.gridy = 5;
            player1Panel.add(player1UnitLabelRow, gbl);

            //Units BUTTONS
            //General
            player1General.setFont(nunitoLight.deriveFont(30f));
            gbul.insets = new Insets(0, -180, 0, 0);
            gbul.gridx = 0;
            gbul.gridy = 0;
            player1UnitOptRow.add(player1General, gbul);

            player1UnitSpinners[0] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player1UnitSpinners[0].setPreferredSize(new Dimension(50, 30));
            player1UnitSpinners[0].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p1GenTxt = ((JSpinner.NumberEditor) player1UnitSpinners[0].getEditor()).getTextField();
            ((NumberFormatter) p1GenTxt.getFormatter()).setAllowsInvalid(false);
            gbul.insets = new Insets(0, 0, 0, 0);
            gbul.gridx = 1;
            gbul.gridy = 0;
            player1UnitOptRow.add(player1UnitSpinners[0], gbul);

            player1GenStat.setFont(poppinsLight.deriveFont(26f));
            player1UnitSpinners[0].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player1GenStat.setFont(poppinsLight.deriveFont(26f));
                    player1GenStat.setText(Integer.toString((Integer) player1UnitSpinners[0].getValue() * 20) + " coins");
                    player1UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player1UnitSpinners[0].getValue() * 20
                                    + (Integer) player1UnitSpinners[1].getValue() * 40
                                    + (Integer) player1UnitSpinners[2].getValue() * 20
                                    + (Integer) player1UnitSpinners[3].getValue() * 20
                                    + (Integer) player1UnitSpinners[4].getValue() * 40
                            )
                    );
                    player1GeneralCount = (Integer) player1UnitSpinners[0].getValue();
                }
            });
            gbul.insets = new Insets(5, 0, 0, -170);
            gbul.gridx = 2;
            gbul.gridy = 0;
            player1UnitOptRow.add(player1GenStat, gbul);

            //Fighter
            player1Fighter.setFont(nunitoLight.deriveFont(30f));
            gbul.insets = new Insets(0, -192, 0, 0);
            gbul.gridx = 0;
            gbul.gridy = 1;
            player1UnitOptRow.add(player1Fighter, gbul);

            player1UnitSpinners[1] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player1UnitSpinners[1].setPreferredSize(new Dimension(50, 30));
            player1UnitSpinners[1].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p1FigTxt = ((JSpinner.NumberEditor) player1UnitSpinners[1].getEditor()).getTextField();
            ((NumberFormatter) p1FigTxt.getFormatter()).setAllowsInvalid(false);
            gbul.insets = new Insets(0, 0, 0, 0);
            gbul.gridx = 1;
            gbul.gridy = 1;
            player1UnitOptRow.add(player1UnitSpinners[1], gbul);

            player1FigStat.setFont(poppinsLight.deriveFont(26f));
            player1UnitSpinners[1].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player1FigStat.setFont(poppinsLight.deriveFont(26f));
                    player1FigStat.setText(Integer.toString((Integer) player1UnitSpinners[1].getValue() * 40) + " coins");
                    player1UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player1UnitSpinners[0].getValue() * 20
                                    + (Integer) player1UnitSpinners[1].getValue() * 40
                                    + (Integer) player1UnitSpinners[2].getValue() * 20
                                    + (Integer) player1UnitSpinners[3].getValue() * 20
                                    + (Integer) player1UnitSpinners[4].getValue() * 40
                            )
                    );
                    player1FighterCount = (Integer) player1UnitSpinners[1].getValue();
                }
            });
            gbul.insets = new Insets(5, 0, 0, -170);
            gbul.gridx = 2;
            gbul.gridy = 1;
            player1UnitOptRow.add(player1FigStat, gbul);

            //Climber
            player1Climber.setFont(nunitoLight.deriveFont(30f));
            gbul.insets = new Insets(0, -185, 0, 0);
            gbul.gridx = 0;
            gbul.gridy = 2;
            player1UnitOptRow.add(player1Climber, gbul);

            player1UnitSpinners[2] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player1UnitSpinners[2].setPreferredSize(new Dimension(50, 30));
            player1UnitSpinners[2].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p1CliTxt = ((JSpinner.NumberEditor) player1UnitSpinners[2].getEditor()).getTextField();
            ((NumberFormatter) p1CliTxt.getFormatter()).setAllowsInvalid(false);
            gbul.insets = new Insets(0, 0, 0, 0);
            gbul.gridx = 1;
            gbul.gridy = 2;
            player1UnitOptRow.add(player1UnitSpinners[2], gbul);

            player1ClimStat.setFont(poppinsLight.deriveFont(26f));
            player1UnitSpinners[2].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player1ClimStat.setFont(poppinsLight.deriveFont(26f));
                    player1ClimStat.setText(Integer.toString((Integer) player1UnitSpinners[2].getValue() * 20) + " coins");
                    player1UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player1UnitSpinners[0].getValue() * 20
                                    + (Integer) player1UnitSpinners[1].getValue() * 40
                                    + (Integer) player1UnitSpinners[2].getValue() * 20
                                    + (Integer) player1UnitSpinners[3].getValue() * 20
                                    + (Integer) player1UnitSpinners[4].getValue() * 40
                            )
                    );
                    player1ClimberCount = (Integer) player1UnitSpinners[2].getValue();
                }
            });
            gbul.insets = new Insets(5, 0, 0, -170);
            gbul.gridx = 2;
            gbul.gridy = 2;
            player1UnitOptRow.add(player1ClimStat, gbul);

            //Diver
            player1Diver.setFont(nunitoLight.deriveFont(30f));
            gbul.insets = new Insets(0, -220, 0, 0);
            gbul.gridx = 0;
            gbul.gridy = 3;
            player1UnitOptRow.add(player1Diver, gbul);

            player1UnitSpinners[3] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player1UnitSpinners[3].setPreferredSize(new Dimension(50, 30));
            player1UnitSpinners[3].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p1DivTxt = ((JSpinner.NumberEditor) player1UnitSpinners[3].getEditor()).getTextField();
            ((NumberFormatter) p1DivTxt.getFormatter()).setAllowsInvalid(false);
            gbul.insets = new Insets(0, 0, 0, 0);
            gbul.gridx = 1;
            gbul.gridy = 3;
            player1UnitOptRow.add(player1UnitSpinners[3], gbul);

            player1DivStat.setFont(poppinsLight.deriveFont(26f));
            player1UnitSpinners[3].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player1DivStat.setFont(poppinsLight.deriveFont(26f));
                    player1DivStat.setText(Integer.toString((Integer) player1UnitSpinners[3].getValue() * 20) + " coins");
                    player1UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player1UnitSpinners[0].getValue() * 20
                                    + (Integer) player1UnitSpinners[1].getValue() * 40
                                    + (Integer) player1UnitSpinners[2].getValue() * 20
                                    + (Integer) player1UnitSpinners[3].getValue() * 20
                                    + (Integer) player1UnitSpinners[4].getValue() * 40
                            )
                    );
                    player1DiverCount = (Integer) player1UnitSpinners[3].getValue();
                }
            });
            gbul.insets = new Insets(5, 0, 0, -170);
            gbul.gridx = 2;
            gbul.gridy = 3;
            player1UnitOptRow.add(player1DivStat, gbul);

            //Destroyer
            player1Destroyer.setFont(nunitoLight.deriveFont(30f));
            gbul.insets = new Insets(0, -160, 0, 0);
            gbul.gridx = 0;
            gbul.gridy = 4;
            player1UnitOptRow.add(player1Destroyer, gbul);

            player1UnitSpinners[4] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player1UnitSpinners[4].setPreferredSize(new Dimension(50, 30));
            player1UnitSpinners[4].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p1DesTxt = ((JSpinner.NumberEditor) player1UnitSpinners[4].getEditor()).getTextField();
            ((NumberFormatter) p1DesTxt.getFormatter()).setAllowsInvalid(false);
            gbul.insets = new Insets(0, 0, 0, 0);
            gbul.gridx = 1;
            gbul.gridy = 4;
            player1UnitOptRow.add(player1UnitSpinners[4], gbul);

            player1DesStat.setFont(poppinsLight.deriveFont(26f));
            player1UnitSpinners[4].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player1DesStat.setFont(poppinsLight.deriveFont(26f));
                    player1DesStat.setText(Integer.toString((Integer) player1UnitSpinners[4].getValue() * 40) + " coins");
                    player1UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player1UnitSpinners[0].getValue() * 20
                                    + (Integer) player1UnitSpinners[1].getValue() * 40
                                    + (Integer) player1UnitSpinners[2].getValue() * 20
                                    + (Integer) player1UnitSpinners[3].getValue() * 20
                                    + (Integer) player1UnitSpinners[4].getValue() * 40
                            )
                    );
                    player1DestroyerCount = (Integer) player1UnitSpinners[4].getValue();
                }
            });
            gbul.insets = new Insets(5, 0, 0, -170);
            gbul.gridx = 2;
            gbul.gridy = 4;
            player1UnitOptRow.add(player1DesStat, gbul);

            player1UnitOptRow.setBackground(Color.LIGHT_GRAY);
            player1UnitOptRow.setPreferredSize(new Dimension(390, 250));

            gbl.insets = new Insets(0, 0, -140, 0);
            gbl.gridx = 0;
            gbl.gridy = 6;
            player1Panel.add(player1UnitOptRow, gbl);

            //Unit deploy
            player1UnitDepRow.setBackground(veryLightGray);
            player1UnitDepRow.setPreferredSize(new Dimension(390, 50));

            player1UnitDeploy.setPreferredSize(new Dimension(140, 30));
            player1UnitDeploy.setFont(nunitoLight.deriveFont(22f));
            player1UnitDeploy.setMargin(new Insets(0, 0, 0, 0));
            player1UnitDeploy.setFocusPainted(false);

            player1UnitDeploy.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < 5; i++) {
                        player1UnitSpinners[i].setValue(0);
                    }
                    player1GenStat.setText("0 coins");
                    player1FigStat.setText("0 coins");
                    player1ClimStat.setText("0 coins");
                    player1DivStat.setText("0 coins");
                    player1DesStat.setText("0 coins");
                    player1UnitTotal.setText("Total: 0");
                }
            });

            gbudl.insets = new Insets(0, -90, 0, 10);
            gbudl.gridx = 0;
            gbudl.gridy = 0;
            player1UnitDepRow.add(player1UnitDeploy, gbudl);

            player1UnitTotal.setFont(poppinsLight.deriveFont(26f));
            gbudl.insets = new Insets(3, 10, 0, -50);
            gbudl.gridx = 1;
            gbudl.gridy = 0;
            player1UnitDepRow.add(player1UnitTotal, gbudl);

            gbl.insets = new Insets(30, 0, -300, 0);
            gbl.gridx = 0;
            gbl.gridy = 7;
            player1Panel.add(player1UnitDepRow, gbl);

            //player1Panel elhelyezkedése
            gbc.insets = new Insets(0, 0, -95, 5);
            gbc.gridx = 0;
            gbc.gridy = 1;
            this.add(player1Panel, gbc);

            //Player 2 UI
            player2Panel.setPreferredSize(new Dimension(400, 908));
            player2Panel.setBackground(Color.DARK_GRAY);

            //STATOK: név + pénz + kastély hp
            player2Stats.add(player2Data);
            player2Stats.setBackground(veryLightGray);

            player2Data.setPreferredSize(new Dimension(380, 72));
            player2Data.setFont(poppinsLight.deriveFont(28f));
            player2Data.setBorder(new EmptyBorder(0, 5, 8, 0));

            gbr.insets = new Insets(-250, 0, 40, 0);
            gbr.gridx = 0;
            gbr.gridy = 0;
            player2Panel.add(player2Stats, gbr);

            //Tower LABEL
            player2TwrLabelRow.add(player2Twr);
            player2TwrLabelRow.setBorder(new EmptyBorder(8, -245, 0, 0));
            player2TwrLabelRow.setBackground(veryLightGray);
            player2TwrLabelRow.setPreferredSize(new Dimension(390, 60));

            player2Twr.setFont(new Font("Calibri", Font.PLAIN, 40));

            gbr.insets = new Insets(-100, 0, 0, 0);
            gbr.gridx = 0;
            gbr.gridy = 1;
            player2Panel.add(player2TwrLabelRow, gbr);

            //Tower BUTTONS
            player2TowerButtons[0].setPreferredSize(new Dimension(180, 40));
            player2TowerButtons[0].setFont(nunitoLight.deriveFont(25f));
            player2TowerButtons[0].setMargin(new Insets(0, 0, 0, 0));
            player2TowerButtons[0].setFocusPainted(false);
            gbtr.insets = new Insets(2, -170, 3, 0);
            gbtr.gridx = 0;
            gbtr.gridy = 0;
            player2TowerOptionRow.add(player2TowerButtons[0], gbtr);

            player2FortStat.setFont(poppinsLight.deriveFont(26f));
            gbtr.insets = new Insets(5, 12, 0, -180);
            gbtr.gridx = 1;
            gbtr.gridy = 0;
            player2TowerOptionRow.add(player2FortStat, gbtr);

            player2TowerButtons[1].setPreferredSize(new Dimension(180, 40));
            player2TowerButtons[1].setFont(nunitoLight.deriveFont(25f));
            player2TowerButtons[1].setMargin(new Insets(0, 0, 0, 0));
            player2TowerButtons[1].setFocusPainted(false);
            gbtr.insets = new Insets(2, -170, 3, 0);
            gbtr.gridx = 0;
            gbtr.gridy = 1;
            player2TowerOptionRow.add(player2TowerButtons[1], gbtr);

            player2RapStat.setFont(poppinsLight.deriveFont(26f));
            gbtr.insets = new Insets(5, 12, 0, -180);
            gbtr.gridx = 1;
            gbtr.gridy = 1;
            player2TowerOptionRow.add(player2RapStat, gbtr);

            player2TowerButtons[2].setPreferredSize(new Dimension(180, 40));
            player2TowerButtons[2].setFont(nunitoLight.deriveFont(25f));
            player2TowerButtons[2].setMargin(new Insets(0, 0, 0, 0));
            player2TowerButtons[2].setFocusPainted(false);
            gbtr.insets = new Insets(2, -170, 3, 0);
            gbtr.gridx = 0;
            gbtr.gridy = 2;
            player2TowerOptionRow.add(player2TowerButtons[2], gbtr);

            player2SnipStat.setFont(poppinsLight.deriveFont(26f));
            gbtr.insets = new Insets(5, 12, 0, -180);
            gbtr.gridx = 1;
            gbtr.gridy = 2;
            player2TowerOptionRow.add(player2SnipStat, gbtr);

            player2TowerOptionRow.setBackground(Color.LIGHT_GRAY);
            player2TowerOptionRow.setPreferredSize(new Dimension(390, 160));

            gbr.insets = new Insets(-20, 0, 20, 0);
            gbr.gridx = 0;
            gbr.gridy = 2;
            player2Panel.add(player2TowerOptionRow, gbr);

            //Tower management LABEL
            player2TwrManLabelRow.add(player2TwrMan);
            player2TwrManLabelRow.setBorder(new EmptyBorder(8, 0, 0, 30));
            player2TwrManLabelRow.setBackground(veryLightGray);
            player2TwrManLabelRow.setPreferredSize(new Dimension(390, 60));

            player2TwrMan.setFont(new Font("Calibri", Font.PLAIN, 40));

            gbr.insets = new Insets(0, 0, 0, 0);
            gbr.gridx = 0;
            gbr.gridy = 3;
            player2Panel.add(player2TwrManLabelRow, gbr);

            //Tower management BUTTONS
            player2TowerButtons[3].setPreferredSize(new Dimension(260, 40));
            player2TowerButtons[3].setFont(nunitoLight.deriveFont(25f));
            player2TowerButtons[3].setMargin(new Insets(0, 0, 0, 0));
            player2TowerButtons[3].setFocusPainted(false);
            gbmr.insets = new Insets(4, 0, 3, 0);
            gbmr.gridx = 0;
            gbmr.gridy = 0;
            player2TowerManagementOptionRow.add(player2TowerButtons[3], gbmr);

            player2TowerButtons[4].setPreferredSize(new Dimension(260, 40));
            player2TowerButtons[4].setFont(nunitoLight.deriveFont(25f));
            player2TowerButtons[4].setMargin(new Insets(0, 0, 0, 0));
            player2TowerButtons[4].setFocusPainted(false);
            gbmr.insets = new Insets(3, 0, 4, 0);
            gbmr.gridx = 0;
            gbmr.gridy = 1;
            player2TowerManagementOptionRow.add(player2TowerButtons[4], gbmr);

            player2TowerManagementOptionRow.setBackground(Color.LIGHT_GRAY);
            player2TowerManagementOptionRow.setPreferredSize(new Dimension(390, 110));

            gbr.insets = new Insets(0, 0, 20, 0);
            gbr.gridx = 0;
            gbr.gridy = 4;
            player2Panel.add(player2TowerManagementOptionRow, gbr);

            //Units LABEL
            player2UnitLabelRow.add(player2UnitLab);
            player2UnitLabelRow.setBorder(new EmptyBorder(8, -280, 0, 0));
            player2UnitLabelRow.setBackground(veryLightGray);
            player2UnitLabelRow.setPreferredSize(new Dimension(390, 60));

            player2UnitLab.setFont(new Font("Calibri", Font.PLAIN, 40));

            gbr.insets = new Insets(0, 0, 0, 0);
            gbr.gridx = 0;
            gbr.gridy = 5;
            player2Panel.add(player2UnitLabelRow, gbr);

            //Units BUTTONS
            //General
            player2General.setFont(nunitoLight.deriveFont(30f));
            gbur.insets = new Insets(0, -180, 0, 0);
            gbur.gridx = 0;
            gbur.gridy = 0;
            player2UnitOptRow.add(player2General, gbur);

            player2UnitSpinners[0] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player2UnitSpinners[0].setPreferredSize(new Dimension(50, 30));
            player2UnitSpinners[0].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p2GenTxt = ((JSpinner.NumberEditor) player2UnitSpinners[0].getEditor()).getTextField();
            ((NumberFormatter) p2GenTxt.getFormatter()).setAllowsInvalid(false);
            gbur.insets = new Insets(0, 0, 0, 0);
            gbur.gridx = 1;
            gbur.gridy = 0;
            player2UnitOptRow.add(player2UnitSpinners[0], gbur);

            player2GenStat.setFont(poppinsLight.deriveFont(26f));
            player2UnitSpinners[0].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player2GenStat.setFont(poppinsLight.deriveFont(26f));
                    player2GenStat.setText(Integer.toString((Integer) player2UnitSpinners[0].getValue() * 20) + " coins");
                    player2UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player2UnitSpinners[0].getValue() * 20
                                    + (Integer) player2UnitSpinners[1].getValue() * 40
                                    + (Integer) player2UnitSpinners[2].getValue() * 20
                                    + (Integer) player2UnitSpinners[3].getValue() * 20
                                    + (Integer) player2UnitSpinners[4].getValue() * 40
                            )
                    );
                    player2GeneralCount = (Integer) player2UnitSpinners[0].getValue();
                }
            });
            gbur.insets = new Insets(5, 0, 0, -170);
            gbur.gridx = 2;
            gbur.gridy = 0;
            player2UnitOptRow.add(player2GenStat, gbur);

            //Fighter
            player2Fighter.setFont(nunitoLight.deriveFont(30f));
            gbur.insets = new Insets(0, -192, 0, 0);
            gbur.gridx = 0;
            gbur.gridy = 1;
            player2UnitOptRow.add(player2Fighter, gbur);

            player2UnitSpinners[1] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player2UnitSpinners[1].setPreferredSize(new Dimension(50, 30));
            player2UnitSpinners[1].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p2FigTxt = ((JSpinner.NumberEditor) player2UnitSpinners[1].getEditor()).getTextField();
            ((NumberFormatter) p2FigTxt.getFormatter()).setAllowsInvalid(false);
            gbur.insets = new Insets(0, 0, 0, 0);
            gbur.gridx = 1;
            gbur.gridy = 1;
            player2UnitOptRow.add(player2UnitSpinners[1], gbur);

            player2FigStat.setFont(poppinsLight.deriveFont(26f));
            player2UnitSpinners[1].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player2FigStat.setFont(poppinsLight.deriveFont(26f));
                    player2FigStat.setText(Integer.toString((Integer) player2UnitSpinners[1].getValue() * 40) + " coins");
                    player2UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player2UnitSpinners[0].getValue() * 20
                                    + (Integer) player2UnitSpinners[1].getValue() * 40
                                    + (Integer) player2UnitSpinners[2].getValue() * 20
                                    + (Integer) player2UnitSpinners[3].getValue() * 20
                                    + (Integer) player2UnitSpinners[4].getValue() * 40
                            )
                    );
                    player2FighterCount = (Integer) player2UnitSpinners[1].getValue();
                }
            });
            gbur.insets = new Insets(5, 0, 0, -170);
            gbur.gridx = 2;
            gbur.gridy = 1;
            player2UnitOptRow.add(player2FigStat, gbur);

            //Climber
            player2Climber.setFont(nunitoLight.deriveFont(30f));
            gbur.insets = new Insets(0, -185, 0, 0);
            gbur.gridx = 0;
            gbur.gridy = 2;
            player2UnitOptRow.add(player2Climber, gbur);

            player2UnitSpinners[2] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player2UnitSpinners[2].setPreferredSize(new Dimension(50, 30));
            player2UnitSpinners[2].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p2CliTxt = ((JSpinner.NumberEditor) player2UnitSpinners[2].getEditor()).getTextField();
            ((NumberFormatter) p2CliTxt.getFormatter()).setAllowsInvalid(false);
            gbur.insets = new Insets(0, 0, 0, 0);
            gbur.gridx = 1;
            gbur.gridy = 2;
            player2UnitOptRow.add(player2UnitSpinners[2], gbur);

            player2ClimStat.setFont(poppinsLight.deriveFont(26f));
            player2UnitSpinners[2].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player2ClimStat.setFont(poppinsLight.deriveFont(26f));
                    player2ClimStat.setText(Integer.toString((Integer) player2UnitSpinners[2].getValue() * 20) + " coins");
                    player2UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player2UnitSpinners[0].getValue() * 20
                                    + (Integer) player2UnitSpinners[1].getValue() * 40
                                    + (Integer) player2UnitSpinners[2].getValue() * 20
                                    + (Integer) player2UnitSpinners[3].getValue() * 20
                                    + (Integer) player2UnitSpinners[4].getValue() * 40
                            )
                    );
                    player2ClimberCount = (Integer) player2UnitSpinners[2].getValue();
                }
            });
            gbur.insets = new Insets(5, 0, 0, -170);
            gbur.gridx = 2;
            gbur.gridy = 2;
            player2UnitOptRow.add(player2ClimStat, gbur);

            //Diver
            player2Diver.setFont(nunitoLight.deriveFont(30f));
            gbur.insets = new Insets(0, -220, 0, 0);
            gbur.gridx = 0;
            gbur.gridy = 3;
            player2UnitOptRow.add(player2Diver, gbur);

            player2UnitSpinners[3] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player2UnitSpinners[3].setPreferredSize(new Dimension(50, 30));
            player2UnitSpinners[3].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p2DivTxt = ((JSpinner.NumberEditor) player2UnitSpinners[3].getEditor()).getTextField();
            ((NumberFormatter) p2DivTxt.getFormatter()).setAllowsInvalid(false);
            gbur.insets = new Insets(0, 0, 0, 0);
            gbur.gridx = 1;
            gbur.gridy = 3;
            player2UnitOptRow.add(player2UnitSpinners[3], gbur);

            player2DivStat.setFont(poppinsLight.deriveFont(26f));
            player2UnitSpinners[3].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player2DivStat.setFont(poppinsLight.deriveFont(26f));
                    player2DivStat.setText(Integer.toString((Integer) player2UnitSpinners[3].getValue() * 20) + " coins");
                    player2UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player2UnitSpinners[0].getValue() * 20
                                    + (Integer) player2UnitSpinners[1].getValue() * 40
                                    + (Integer) player2UnitSpinners[2].getValue() * 20
                                    + (Integer) player2UnitSpinners[3].getValue() * 20
                                    + (Integer) player2UnitSpinners[4].getValue() * 40
                            )
                    );
                    player2DiverCount = (Integer) player2UnitSpinners[3].getValue();
                }
            });
            gbur.insets = new Insets(5, 0, 0, -170);
            gbur.gridx = 2;
            gbur.gridy = 3;
            player2UnitOptRow.add(player2DivStat, gbur);

            //Destroyer
            player2Destroyer.setFont(nunitoLight.deriveFont(30f));
            gbur.insets = new Insets(0, -160, 0, 0);
            gbur.gridx = 0;
            gbur.gridy = 4;
            player2UnitOptRow.add(player2Destroyer, gbur);

            player2UnitSpinners[4] = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            player2UnitSpinners[4].setPreferredSize(new Dimension(50, 30));
            player2UnitSpinners[4].setFont(poppinsLight.deriveFont(15f));
            JFormattedTextField p2DesTxt = ((JSpinner.NumberEditor) player2UnitSpinners[4].getEditor()).getTextField();
            ((NumberFormatter) p2DesTxt.getFormatter()).setAllowsInvalid(false);
            gbur.insets = new Insets(0, 0, 0, 0);
            gbur.gridx = 1;
            gbur.gridy = 4;
            player2UnitOptRow.add(player2UnitSpinners[4], gbur);

            player2DesStat.setFont(poppinsLight.deriveFont(26f));
            player2UnitSpinners[4].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    player2DesStat.setFont(poppinsLight.deriveFont(26f));
                    player2DesStat.setText(Integer.toString((Integer) player2UnitSpinners[4].getValue() * 40) + " coins");
                    player2UnitTotal.setText(
                            "Total: "
                            + Integer.toString(
                                    (Integer) player2UnitSpinners[0].getValue() * 20
                                    + (Integer) player2UnitSpinners[1].getValue() * 40
                                    + (Integer) player2UnitSpinners[2].getValue() * 20
                                    + (Integer) player2UnitSpinners[3].getValue() * 20
                                    + (Integer) player2UnitSpinners[4].getValue() * 40
                            )
                    );
                    player2DestroyerCount = (Integer) player2UnitSpinners[4].getValue();
                }
            });
            gbur.insets = new Insets(5, 0, 0, -170);
            gbur.gridx = 2;
            gbur.gridy = 4;
            player2UnitOptRow.add(player2DesStat, gbur);

            player2UnitOptRow.setBackground(Color.LIGHT_GRAY);
            player2UnitOptRow.setPreferredSize(new Dimension(390, 250));

            gbr.insets = new Insets(0, 0, -140, 0);
            gbr.gridx = 0;
            gbr.gridy = 6;
            player2Panel.add(player2UnitOptRow, gbr);

            //Unit deploy
            player2UnitDepRow.setBackground(veryLightGray);
            player2UnitDepRow.setPreferredSize(new Dimension(390, 50));

            player2UnitDeploy.setPreferredSize(new Dimension(140, 30));
            player2UnitDeploy.setFont(nunitoLight.deriveFont(22f));
            player2UnitDeploy.setMargin(new Insets(0, 0, 0, 0));
            player2UnitDeploy.setFocusPainted(false);

            player2UnitDeploy.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < 5; i++) {
                        player2UnitSpinners[i].setValue(0);
                    }
                    player2GenStat.setText("0 coins");
                    player2FigStat.setText("0 coins");
                    player2ClimStat.setText("0 coins");
                    player2DivStat.setText("0 coins");
                    player2DesStat.setText("0 coins");
                    player2UnitTotal.setText("Total: 0");
                }
            });

            gbudr.insets = new Insets(0, -90, 0, 10);
            gbudr.gridx = 0;
            gbudr.gridy = 0;
            player2UnitDepRow.add(player2UnitDeploy, gbudr);

            player2UnitTotal.setFont(poppinsLight.deriveFont(26f));
            gbudr.insets = new Insets(3, 10, 0, -50);
            gbudr.gridx = 1;
            gbudr.gridy = 0;
            player2UnitDepRow.add(player2UnitTotal, gbudr);

            gbr.insets = new Insets(30, 0, -300, 0);
            gbr.gridx = 0;
            gbr.gridy = 7;
            player2Panel.add(player2UnitDepRow, gbr);

            //player2Panel elhelyezkedése
            gbc.insets = new Insets(0, 5, -95, 0);
            gbc.gridx = 2;
            gbc.gridy = 1;
            this.add(player2Panel, gbc);
        } catch (IOException | FontFormatException e) {
        }
    }

    /**
     * Aktiválja a soron levő játékos gombjait
     */
    public final void activePlayerPanelSetter() {
        if (board.getModel().getActivePlayer() == 0) {
            for (var button : player2TowerButtons) {
                button.setEnabled(false);
            }
            for (var spinner : player2UnitSpinners) {
                spinner.setEnabled(false);
            }
            player2UnitDeploy.setEnabled(false);

            for (var button : player1TowerButtons) {
                button.setEnabled(true);
            }
            for (var spinner : player1UnitSpinners) {
                spinner.setEnabled(true);
            }
            player1UnitDeploy.setEnabled(true);
        } else {
            for (var button : player1TowerButtons) {
                button.setEnabled(false);
            }
            for (var spinner : player1UnitSpinners) {
                spinner.setEnabled(false);
            }
            player1UnitDeploy.setEnabled(false);

            for (var button : player2TowerButtons) {
                button.setEnabled(true);
            }
            for (var spinner : player2UnitSpinners) {
                spinner.setEnabled(true);
            }
            player2UnitDeploy.setEnabled(true);
        }
    }

    /**
     * Frissíti a játékos UI-on megjelenő adatait
     */
    public void playerDataUpdate() {
        Player player1 = board.getModel().getPlayers()[0];
        Player player2 = board.getModel().getPlayers()[1];
        player1Data.setText(
                "<html>"
                + player1.getName()
                + "<br>Coins // " + player1.getMoney()
                + "</html>"
        );

        player2Data.setText(
                "<html>"
                + player2.getName()
                + "<br>Coins // " + player2.getMoney()
                + "</html>"
        );
    }

    /**
     * Getterek, setterek
     *
     * @return
     */
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board newBoard) {
        board = newBoard;
    }

    public int getTicks() {
        return timerTicks;
    }

    public void setTicks(int timerTicks) {
        this.timerTicks = timerTicks;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public JLabel getTimeAndRoundLabel() {
        return timeAndRoundLabel;
    }

    public JSpinner[] getPlayer1UnitSpinners() {
        return player1UnitSpinners;
    }

    public JSpinner[] getPlayer2UnitSpinners() {
        return player2UnitSpinners;
    }

    public JButton[] getPlayer1TowerButtons() {
        return player1TowerButtons;
    }

    public JButton[] getPlayer2TowerButtons() {
        return player2TowerButtons;
    }

    public ArrayList<Integer> getPlayer1distances() {
        return player1Distances;
    }

    public ArrayList<Integer> getPlayer2distances() {
        return player2Distances;
    }

    public void setRNDPROTECTION(int RNDPROTECTION) {
        this.RNDPROTECTION = RNDPROTECTION;
    }

    public void setTestMode(boolean isTestMode) {
        testMode = isTestMode;
    }

    public JButton getBackToMenuButton() {
        return backToMenuButton;
    }
}
