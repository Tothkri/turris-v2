package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import model.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import model.Model;
import model.Player;

public class GameWindow extends JPanel {

    private Timer timer;
    private Board board;
    private JButton exitButton;
    private JButton newRoundButton;
    private JButton saveButton;
    private JButton[] player1TowerButtons;
    private JButton[] player1UnitButtons;
    private JButton[] player2TowerButtons;
    private JButton[] player2UnitButtons;
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

    public GameWindow() {
        super();
    }

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

    public void constructor(int gameWindowWidth, int gameWindowHeight) {
        buttonAction = "";
        RNDPROTECTION = 0;
        fieldSize = (model.getBoardSize() / 30);
        setSize(gameWindowWidth, gameWindowHeight);
        player1Distances = new ArrayList<>();
        player2Distances = new ArrayList<>();
        timeAndRoundLabel = new JLabel();
        this.setPreferredSize(new Dimension(gameWindowWidth, gameWindowHeight));
        exitButton = new JButton("Exit game");
        exitButton.addActionListener((event) -> System.exit(0));
        newRoundButton = new JButton("Finish round");
        newRoundButton.addActionListener((event) -> {
            newRound();
        });

        //kurzor utáni info megjelenítéshez
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });

        saveButton = new JButton("Save game");
        saveButton.addActionListener((event) -> saveGame());

        this.setLayout(new GridBagLayout());

        player1TowerButtons = new JButton[5];
        player1UnitButtons = new JButton[5];
        player2TowerButtons = new JButton[5];
        player2UnitButtons = new JButton[5];

        setPanels();

        //PLAYER BUTTONS
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

        /**
         * PLAYER1 Unit típusok
         */
        player1UnitButtons[0].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("General", "blue", 1, model));
            playerDataUpdate();
        });
        player1UnitButtons[1].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("Fighter", "blue", 1, model));
            playerDataUpdate();
        });
        player1UnitButtons[2].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("Climber", "blue", 1, model));
            playerDataUpdate();
        });
        player1UnitButtons[3].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("Diver", "blue", 1, model));
            playerDataUpdate();
        });
        player1UnitButtons[4].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("Destroyer", "blue", 1, model));
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

        /**
         * PLAYER2 Unit típusok
         */
        player2UnitButtons[0].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("General", "red", 1, model));
            playerDataUpdate();
        });
        player2UnitButtons[1].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("Fighter", "red", 1, model));
            playerDataUpdate();
        });
        player2UnitButtons[2].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("Climber", "red", 1, model));
            playerDataUpdate();
        });
        player2UnitButtons[3].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("Diver", "red", 1, model));
            playerDataUpdate();
        });
        player2UnitButtons[4].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("Destroyer", "red", 1, model));
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

            int fieldSize = model.getBoardSize() / 30;

            int boardPositionX = (int) hoveredPoint.getX() - 500;
            int matrixPositionX = boardPositionX / fieldSize;
            int boardPositionY = (int) hoveredPoint.getY() - 75;
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
                        player1UnitButtons[buttonIndex].setEnabled(true);
                    } else {
                        player2TowerButtons[buttonIndex].setEnabled(true);
                        player2UnitButtons[buttonIndex].setEnabled(true);
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
                                    model.getCastleCoordinates()[castleCoordinateIndex + defendingPlayer][1], actualPlayer.getDifficulty(model, u.getType(), playerIndex));
                            int actualWayDifficulty = model.wayDifficulty(playerIndex, actualPlayer.convertWay(bestWayString), u.getType());
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

    public boolean simulation() {
        //először az egységek mozognak

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
                        units.get(unitIndex).setX(next.getX() * fieldSize);
                        units.get(unitIndex).setY(next.getY() * fieldSize);
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
                    //Fighter sebzi az elhaladó ellenséges egységeket, ha azonos mezőre értek
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
                    }

                    ArrayList<Tower> towersNearby = model.towersNearby(playerIndex, units.get(unitIndex));

                    //Destroyer támadja a mellette lévő tornyot
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

                                if (towersNearby.get(towerIndex).getHp() > 50) {
                                    towersNearby.get(towerIndex).setHp(towersNearby.get(towerIndex).getHp() - 50);
                                    int towerToDemolishIndex = model.getPlayers()[Math.abs(playerIndex - 1)].getTowerIndex(towersNearby.get(towerIndex));
                                    if (towerToDemolishIndex != -1) {
                                        model.getPlayers()[Math.abs(playerIndex - 1)].getTowers().get(towerToDemolishIndex).setExploded(true);
                                    }
                                    model.getPlayers()[playerIndex].deleteUnit(units.get(unitIndex));
                                    if (unitIndex > 0) {
                                        unitIndex--;
                                    }
                                    break;
                                } else if (towersNearby.get(towerIndex).getHp() > 0) //torony megsemmisül
                                {
                                    towersNearby.get(towerIndex).setHp(0);
                                    towersNearby.get(towerIndex).setPower(0);
                                    towersNearby.get(towerIndex).setRange(0);
                                    towersNearby.get(towerIndex).setAttackFrequency(0);
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

    public void towerPlaceAction(String towerType) {
        if (!buttonAction.equals("")) {
            buttonAction = "";
            model.setSelectables(new ArrayList<>());
        } else {
            selectedTower = towerType;
            buttonAction = "placeTower";
            model.setSelectables();
        }
    }

    //játékosok körönként váltják egymást, minden szimuláció 2 körönként történik - ezután kap mindkét játékos 100-100 aranyat
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
                player1UnitButtons[buttonIndex].setEnabled(false);
                player2TowerButtons[buttonIndex].setEnabled(false);
                player2UnitButtons[buttonIndex].setEnabled(false);
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

    public void gameOver() //játék végén felugró üzenet
    {
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

    public void saveGame() //játék mentése txt fájlba
    {
        String filename;
        filename = JOptionPane.showInputDialog("Filename:");
        if (filename == null || filename.length() == 0) {
            return;
        }
        model.saveData(filename, timerTicks);
    }

    /**
     * Outlines
     */
    @Override
    protected void paintComponent(Graphics grph) {
        super.paintComponent(grph);
        Graphics2D grphcs2 = (Graphics2D) grph;
        grph.drawImage(new ImageIcon("src/res/StartScreen Background.png").getImage(), 0, 0, 1920, 1080, null);

        Color sandBrown = new Color(210, 180, 140);
        Color lightGray = new Color(214, 196, 194);
        Color cinnabar = new Color(227, 66, 52);      //red: ON round
        Color pastelRed = new Color(255, 105, 97);    //red: NOT ON round
        Color royalBlue = new Color(65, 105, 225);    //blue: ON round
        Color blueGray = new Color(102, 153, 204);    //blue: NOT ON round
        Color darkHoneyBrown = new Color(184, 151, 128);

        //1st layer UI (bottom)
        grph.setColor(Color.DARK_GRAY);
        grphcs2.fillRoundRect(27, 113, 437, 891, 25, 25);   //left
        grphcs2.fillRoundRect(1456, 113, 437, 891, 25, 25); //right
        grphcs2.fillRoundRect(480, 10, 959, 1062, 22, 22);  //center
        grphcs2.fillRoundRect(27, 23, 437, 70, 25, 25);     //save
        grphcs2.fillRoundRect(1456, 23, 437, 70, 25, 25);   //exit

        //2nd layer UI (middle - coloring)
        if (model.getActivePlayer() == 0) {
            grph.setColor(royalBlue);
            grphcs2.fillRoundRect(35, 121, 421, 875, 25, 25);   //left
            grph.setColor(pastelRed);
            grphcs2.fillRoundRect(1465, 121, 421, 875, 25, 25); //right
        } else {
            grph.setColor(blueGray);
            grphcs2.fillRoundRect(35, 121, 421, 875, 25, 25);   //left
            grph.setColor(cinnabar);
            grphcs2.fillRoundRect(1465, 121, 421, 875, 25, 25); //right
        }
        grph.setColor(sandBrown);
        grphcs2.fillRoundRect(34, 30, 423, 56, 22, 22);     //save
        grphcs2.fillRoundRect(1463, 30, 423, 56, 22, 22);   //exit
        grphcs2.fillRoundRect(489, 18, 942, 985, 22, 22);  //center

        //3rd layer UI (top)
        grph.setColor(Color.DARK_GRAY);
        grphcs2.fillRoundRect(41, 126, 408, 865, 22, 22);   //left
        grphcs2.fillRoundRect(1471, 126, 408, 865, 22, 22); //right
        grphcs2.fillRect(114, 35, 266, 46);                 //save
        grphcs2.fillRect(1539, 35, 266, 46);                //exit
    }

    /**
     * UI-on megjelenő gombok elhelyezése
     */
    public final void setPanels() {
        Color algaeGreen = new Color(105, 168, 120);
        Color veryLightGray = new Color(220, 220, 220);
        Color transp = new Color(1f, 0f, 0f, .5f);

        JPanel player1Panel = new JPanel(); //whole panel
        JPanel player1Stats = new JPanel(); //legfelső (name/cas hp/money)
        JPanel player1TwrLabelRow = new JPanel(); //tower text
        JPanel player1TowerOptionRow = new JPanel(); //tower button options
        JPanel player1TwrManLabelRow = new JPanel(); //tower management text
        JPanel player1TowerManagementOptionRow = new JPanel(); //tower management button options
        JPanel player1UnitLabelRow = new JPanel(); //unit text
        JPanel player1UnitOptRow = new JPanel(); //unit button options

        player1Data = new JLabel();
        JLabel player1Twr = new JLabel("Towers");
        JLabel player1FortStat = new JLabel("200 coins");
        JLabel player1RapStat = new JLabel("250 coins");
        JLabel player1SnipStat = new JLabel("300 coins");
        JLabel player1TwrMan = new JLabel("Tower Management");
        JLabel player1UnitLab = new JLabel("Units");
        JLabel player1GenStat = new JLabel("20 coins");
        JLabel player1DivStat = new JLabel("20 coins");
        JLabel player1ClimStat = new JLabel("20 coins");
        JLabel player1FigStat = new JLabel("40 coins");
        JLabel player1DesStat = new JLabel("40 coins");

        JPanel player2Panel = new JPanel();
        JPanel player2Stats = new JPanel();
        JPanel player2TwrLabelRow = new JPanel();
        JPanel player2TowerOptionRow = new JPanel();
        JPanel player2TwrManLabelRow = new JPanel();
        JPanel player2TowerManagementOptionRow = new JPanel();
        JPanel player2UnitLabelRow = new JPanel();
        JPanel player2UnitOptRow = new JPanel();

        player2Data = new JLabel();
        JLabel player2Twr = new JLabel("Towers");
        JLabel player2FortStat = new JLabel("200 coins");
        JLabel player2RapStat = new JLabel("250 coins");
        JLabel player2SnipStat = new JLabel("300 coins");
        JLabel player2TwrMan = new JLabel("Tower Management");
        JLabel player2UnitLab = new JLabel("Units");
        JLabel player2GenStat = new JLabel("20 coins");
        JLabel player2DivStat = new JLabel("20 coins");
        JLabel player2ClimStat = new JLabel("20 coins");
        JLabel player2FigStat = new JLabel("40 coins");
        JLabel player2DesStat = new JLabel("40 coins");

        playerDataUpdate();

        player1TowerButtons[0] = new JButton("Fortified");
        player1TowerButtons[1] = new JButton("Rapid");
        player1TowerButtons[2] = new JButton("Sniper");
        player1TowerButtons[3] = new JButton("Upgrade");
        player1TowerButtons[4] = new JButton("Demolish");
        player1UnitButtons[0] = new JButton("General");
        player1UnitButtons[1] = new JButton("Fighter");
        player1UnitButtons[2] = new JButton("Climber");
        player1UnitButtons[3] = new JButton("Diver");
        player1UnitButtons[4] = new JButton("Destroyer");

        player2TowerButtons[0] = new JButton("Fortified");
        player2TowerButtons[1] = new JButton("Rapid");
        player2TowerButtons[2] = new JButton("Sniper");
        player2TowerButtons[3] = new JButton("Upgrade");
        player2TowerButtons[4] = new JButton("Demolish");
        player2UnitButtons[0] = new JButton("General");
        player2UnitButtons[1] = new JButton("Fighter");
        player2UnitButtons[2] = new JButton("Climber");
        player2UnitButtons[3] = new JButton("Diver");
        player2UnitButtons[4] = new JButton("Destroyer");

        this.setBackground(algaeGreen);

        //player1 panels
        player1Panel.setLayout(new GridBagLayout());
        player1TowerOptionRow.setLayout(new GridBagLayout());
        player1TowerManagementOptionRow.setLayout(new GridBagLayout());
        player1UnitOptRow.setLayout(new GridBagLayout());

        //player2 panels
        player2Panel.setLayout(new GridBagLayout());
        player2TowerOptionRow.setLayout(new GridBagLayout());
        player2TowerManagementOptionRow.setLayout(new GridBagLayout());
        player2UnitOptRow.setLayout(new GridBagLayout());

        //Elhelyezés
        GridBagConstraints gbc = new GridBagConstraints();

        GridBagConstraints gbl = new GridBagConstraints();
        GridBagConstraints gbtl = new GridBagConstraints();
        GridBagConstraints gbml = new GridBagConstraints();
        GridBagConstraints gbul = new GridBagConstraints();

        GridBagConstraints gbr = new GridBagConstraints();
        GridBagConstraints gbtr = new GridBagConstraints();
        GridBagConstraints gbmr = new GridBagConstraints();
        GridBagConstraints gbur = new GridBagConstraints();

        /**
         * Játéktér
         */
        gbc.anchor = GridBagConstraints.CENTER; //nem csinál semmit..
        gbc.insets = new Insets(0, 45, 0, 45);
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(board, gbc);

        /**
         * Save game BUTTON
         */
        saveButton.setPreferredSize(new Dimension(260, 40));
        saveButton.setFont(new Font("Calibri", Font.PLAIN, 25));
        saveButton.setMargin(new Insets(8, 0, 0, 0));
        saveButton.setFocusPainted(false);
        gbc.insets = new Insets(0, 0, -22, 0);    //0, 0, 0, 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(saveButton, gbc);

        /**
         * Exit game BUTTON
         */
        exitButton.setPreferredSize(new Dimension(260, 40));
        exitButton.setFont(new Font("Calibri", Font.PLAIN, 25));
        exitButton.setMargin(new Insets(8, 0, 0, 0));
        exitButton.setFocusPainted(false);
        gbc.insets = new Insets(0, 0, -22, 0);
        gbc.gridx = 2;
        gbc.gridy = 0;
        this.add(exitButton, gbc);

        /**
         * Finish round BUTTON
         */
        newRoundButton.setPreferredSize(new Dimension(932, 50));
        newRoundButton.setFont(new Font("Calibri", Font.PLAIN, 32));
        newRoundButton.setMargin(new Insets(12, 0, 0, 0));
        newRoundButton.setFocusPainted(false);
        gbc.insets = new Insets(15, 0, -10, 0);  //10, 0, -5, 0
        gbc.gridx = 1;
        gbc.gridy = 2;
        this.add(newRoundButton, gbc);

        /**
         * Round time & count LABEL
         */
        timeAndRoundLabel.setFont(new Font("Calibri", Font.BOLD, 30));
        gbc.insets = new Insets(2, 0, 0, 0);    //5, 0, 8, 0
        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(timeAndRoundLabel, gbc);

        /**
         * Player 1 UI - player1Panel MAIN
         */
        player1Panel.setPreferredSize(new Dimension(400, 858));
        player1Panel.setBackground(Color.DARK_GRAY);

        //STATOK: név + pénz + kastély hp
        player1Stats.add(player1Data);
        player1Stats.setBackground(veryLightGray);

        player1Data.setPreferredSize(new Dimension(380, 72));
        player1Data.setFont(new Font("Calibri", Font.PLAIN, 35));
        player1Data.setBorder(new EmptyBorder(0, 5, 8, 0));

        gbl.insets = new Insets(-210, 0, 75, 0);   //-210, 0, 100, 0
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
        player1TowerButtons[0].setPreferredSize(new Dimension(180, 40)); //260, 40
        player1TowerButtons[0].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1TowerButtons[0].setMargin(new Insets(8, 0, 0, 0));
        player1TowerButtons[0].setFocusPainted(false);
        gbtl.insets = new Insets(2, -170, 3, 0);    //2, -220, 3, 0
        gbtl.gridx = 0;
        gbtl.gridy = 0;
        player1TowerOptionRow.add(player1TowerButtons[0], gbtl);

        player1FortStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtl.insets = new Insets(8, 12, 0, -180);
        gbtl.gridx = 1;
        gbtl.gridy = 0;
        player1TowerOptionRow.add(player1FortStat, gbtl);

        player1TowerButtons[1].setPreferredSize(new Dimension(180, 40));
        player1TowerButtons[1].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1TowerButtons[1].setMargin(new Insets(8, 0, 0, 0));
        player1TowerButtons[1].setFocusPainted(false);
        gbtl.insets = new Insets(2, -170, 3, 0);
        gbtl.gridx = 0;
        gbtl.gridy = 1;
        player1TowerOptionRow.add(player1TowerButtons[1], gbtl);

        player1RapStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtl.insets = new Insets(8, 12, 0, -180);
        gbtl.gridx = 1;
        gbtl.gridy = 1;
        player1TowerOptionRow.add(player1RapStat, gbtl);

        player1TowerButtons[2].setPreferredSize(new Dimension(180, 40));
        player1TowerButtons[2].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1TowerButtons[2].setMargin(new Insets(8, 0, 0, 0));
        player1TowerButtons[2].setFocusPainted(false);
        gbtl.insets = new Insets(2, -170, 3, 0);
        gbtl.gridx = 0;
        gbtl.gridy = 2;
        player1TowerOptionRow.add(player1TowerButtons[2], gbtl);

        player1SnipStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtl.insets = new Insets(8, 12, 0, -180);
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
        player1TowerButtons[3].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1TowerButtons[3].setMargin(new Insets(8, 0, 0, 0));
        player1TowerButtons[3].setFocusPainted(false);
        gbml.insets = new Insets(4, 0, 3, 0);
        gbml.gridx = 0;
        gbml.gridy = 0;
        player1TowerManagementOptionRow.add(player1TowerButtons[3], gbml);

        player1TowerButtons[4].setPreferredSize(new Dimension(260, 40));
        player1TowerButtons[4].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1TowerButtons[4].setMargin(new Insets(8, 0, 0, 0));
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

        gbl.insets = new Insets(0, 0, 0, 0);    //másik: -100, 0, 0, 0
        gbl.gridx = 0;
        gbl.gridy = 5;  //5
        player1Panel.add(player1UnitLabelRow, gbl);

        //Units BUTTONS
        player1UnitButtons[0].setPreferredSize(new Dimension(180, 40));
        player1UnitButtons[0].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1UnitButtons[0].setMargin(new Insets(8, 0, 0, 0));
        player1UnitButtons[0].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);   //3, -20, 3, 0
        gbul.gridx = 0;
        gbul.gridy = 0;
        player1UnitOptRow.add(player1UnitButtons[0], gbul);

        player1GenStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 0;
        player1UnitOptRow.add(player1GenStat, gbul);

        player1UnitButtons[1].setPreferredSize(new Dimension(180, 40));
        player1UnitButtons[1].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1UnitButtons[1].setMargin(new Insets(8, 0, 0, 0));
        player1UnitButtons[1].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);
        gbul.gridx = 0;
        gbul.gridy = 1;
        player1UnitOptRow.add(player1UnitButtons[1], gbul);

        player1FigStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 1;
        player1UnitOptRow.add(player1FigStat, gbul);

        player1UnitButtons[2].setPreferredSize(new Dimension(180, 40));
        player1UnitButtons[2].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1UnitButtons[2].setMargin(new Insets(8, 0, 0, 0));
        player1UnitButtons[2].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);
        gbul.gridx = 0;
        gbul.gridy = 2;
        player1UnitOptRow.add(player1UnitButtons[2], gbul);

        player1ClimStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 2;
        player1UnitOptRow.add(player1ClimStat, gbul);

        player1UnitButtons[3].setPreferredSize(new Dimension(180, 40));
        player1UnitButtons[3].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1UnitButtons[3].setMargin(new Insets(8, 0, 0, 0));
        player1UnitButtons[3].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);
        gbul.gridx = 0;
        gbul.gridy = 3;
        player1UnitOptRow.add(player1UnitButtons[3], gbul);

        player1DivStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 3;
        player1UnitOptRow.add(player1DivStat, gbul);

        player1UnitButtons[4].setPreferredSize(new Dimension(180, 40));
        player1UnitButtons[4].setFont(new Font("Calibri", Font.PLAIN, 25));
        player1UnitButtons[4].setMargin(new Insets(8, 0, 0, 0));
        player1UnitButtons[4].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);
        gbul.gridx = 0;
        gbul.gridy = 4;
        player1UnitOptRow.add(player1UnitButtons[4], gbul);

        player1DesStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 4;
        player1UnitOptRow.add(player1DesStat, gbul);

        player1UnitOptRow.setBackground(Color.LIGHT_GRAY);
        player1UnitOptRow.setPreferredSize(new Dimension(390, 250));

        gbl.insets = new Insets(0, 0, -185, 0); //0, 0, -210, 0
        gbl.gridx = 0;
        gbl.gridy = 6;  //6
        player1Panel.add(player1UnitOptRow, gbl);

        //player1Panel elhelyezkedése
        gbc.insets = new Insets(0, 0, -53, 5);    //0, 0, -40, 5
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(player1Panel, gbc);

        /**
         * Player 2 UI
         */
        player2Panel.setPreferredSize(new Dimension(400, 858)); //420, 390
        player2Panel.setBackground(Color.DARK_GRAY);

        //STATOK: név + pénz + kastély hp
        player2Stats.add(player2Data);
        player2Stats.setBackground(veryLightGray);

        player2Data.setPreferredSize(new Dimension(380, 72));
        player2Data.setFont(new Font("Calibri", Font.PLAIN, 35));
        player2Data.setBorder(new EmptyBorder(0, 5, 8, 0));

        gbr.insets = new Insets(-210, 0, 75, 0);
        gbr.gridx = 0;
        gbr.gridy = 0;
        player2Panel.add(player2Stats, gbr);

        //Tower LABEL
        player2TwrLabelRow.add(player2Twr);
        player2TwrLabelRow.setBorder(new EmptyBorder(8, -245, 0, 0));    //8, -245, 0, 0
        player2TwrLabelRow.setBackground(veryLightGray);
        player2TwrLabelRow.setPreferredSize(new Dimension(390, 60));

        player2Twr.setFont(new Font("Calibri", Font.PLAIN, 40));

        gbr.insets = new Insets(-100, 0, 0, 0);
        gbr.gridx = 0;
        gbr.gridy = 1;
        player2Panel.add(player2TwrLabelRow, gbr);

        //Tower BUTTONS
        player2TowerButtons[0].setPreferredSize(new Dimension(180, 40));
        player2TowerButtons[0].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2TowerButtons[0].setMargin(new Insets(8, 0, 0, 0));
        player2TowerButtons[0].setFocusPainted(false);
        gbtr.insets = new Insets(2, -170, 3, 0);
        gbtr.gridx = 0;
        gbtr.gridy = 0;
        player2TowerOptionRow.add(player2TowerButtons[0], gbtr);

        player2FortStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtr.insets = new Insets(8, 12, 0, -180);
        gbtr.gridx = 1;
        gbtr.gridy = 0;
        player2TowerOptionRow.add(player2FortStat, gbtr);

        player2TowerButtons[1].setPreferredSize(new Dimension(180, 40));
        player2TowerButtons[1].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2TowerButtons[1].setMargin(new Insets(8, 0, 0, 0));
        player2TowerButtons[1].setFocusPainted(false);
        gbtr.insets = new Insets(2, -170, 3, 0);
        gbtr.gridx = 0;
        gbtr.gridy = 1;
        player2TowerOptionRow.add(player2TowerButtons[1], gbtr);

        player2RapStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtr.insets = new Insets(8, 12, 0, -180);
        gbtr.gridx = 1;
        gbtr.gridy = 1;
        player2TowerOptionRow.add(player2RapStat, gbtr);

        player2TowerButtons[2].setPreferredSize(new Dimension(180, 40));
        player2TowerButtons[2].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2TowerButtons[2].setMargin(new Insets(8, 0, 0, 0));
        player2TowerButtons[2].setFocusPainted(false);
        gbtr.insets = new Insets(2, -170, 3, 0);
        gbtr.gridx = 0;
        gbtr.gridy = 2;
        player2TowerOptionRow.add(player2TowerButtons[2], gbtr);

        player2SnipStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtr.insets = new Insets(8, 12, 0, -180);
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
        player2TowerButtons[3].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2TowerButtons[3].setMargin(new Insets(8, 0, 0, 0));
        player2TowerButtons[3].setFocusPainted(false);
        gbmr.insets = new Insets(4, 0, 3, 0);
        gbmr.gridx = 0;
        gbmr.gridy = 0;
        player2TowerManagementOptionRow.add(player2TowerButtons[3], gbmr);

        player2TowerButtons[4].setPreferredSize(new Dimension(260, 40));
        player2TowerButtons[4].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2TowerButtons[4].setMargin(new Insets(8, 0, 0, 0));
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
        player2UnitButtons[0].setPreferredSize(new Dimension(180, 40));
        player2UnitButtons[0].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2UnitButtons[0].setMargin(new Insets(8, 0, 0, 0));
        player2UnitButtons[0].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 0;
        player2UnitOptRow.add(player2UnitButtons[0], gbur);

        player2GenStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 0;
        player2UnitOptRow.add(player2GenStat, gbur);

        player2UnitButtons[1].setPreferredSize(new Dimension(180, 40));
        player2UnitButtons[1].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2UnitButtons[1].setMargin(new Insets(8, 0, 0, 0));
        player2UnitButtons[1].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 1;
        player2UnitOptRow.add(player2UnitButtons[1], gbur);

        player2FigStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 1;
        player2UnitOptRow.add(player2FigStat, gbur);

        player2UnitButtons[2].setPreferredSize(new Dimension(180, 40));
        player2UnitButtons[2].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2UnitButtons[2].setMargin(new Insets(8, 0, 0, 0));
        player2UnitButtons[2].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 2;
        player2UnitOptRow.add(player2UnitButtons[2], gbur);

        player2ClimStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 2;
        player2UnitOptRow.add(player2ClimStat, gbur);

        player2UnitButtons[3].setPreferredSize(new Dimension(180, 40));
        player2UnitButtons[3].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2UnitButtons[3].setMargin(new Insets(8, 0, 0, 0));
        player2UnitButtons[3].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 3;
        player2UnitOptRow.add(player2UnitButtons[3], gbur);

        player2DivStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 3;
        player2UnitOptRow.add(player2DivStat, gbur);

        player2UnitButtons[4].setPreferredSize(new Dimension(180, 40));
        player2UnitButtons[4].setFont(new Font("Calibri", Font.PLAIN, 25));
        player2UnitButtons[4].setMargin(new Insets(8, 0, 0, 0));
        player2UnitButtons[4].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 4;
        player2UnitOptRow.add(player2UnitButtons[4], gbur);

        player2DesStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 4;
        player2UnitOptRow.add(player2DesStat, gbur);

        player2UnitOptRow.setBackground(Color.LIGHT_GRAY);
        player2UnitOptRow.setPreferredSize(new Dimension(390, 250));

        gbr.insets = new Insets(0, 0, -185, 0);
        gbr.gridx = 0;
        gbr.gridy = 6;  //6
        player2Panel.add(player2UnitOptRow, gbr);

        //player2Panel elhelyezkedése
        gbc.insets = new Insets(0, 5, -53, 0);  //0, 5, -40, 0
        gbc.gridx = 2;
        gbc.gridy = 1;
        this.add(player2Panel, gbc);
    }

    /**
     * Aktiválja a soron levő játékos gombjait
     */
    public final void activePlayerPanelSetter() {
        if (board.getModel().getActivePlayer() == 0) {
            for (var button : player2TowerButtons) {
                button.setEnabled(false);
            }
            for (var button : player2UnitButtons) {
                button.setEnabled(false);
            }
            for (var button : player1TowerButtons) {
                button.setEnabled(true);
            }
            for (var button : player1UnitButtons) {
                button.setEnabled(true);
            }
        } else {
            for (var button : player1TowerButtons) {
                button.setEnabled(false);
            }
            for (var button : player1UnitButtons) {
                button.setEnabled(false);
            }
            for (var button : player2TowerButtons) {
                button.setEnabled(true);
            }
            for (var button : player2UnitButtons) {
                button.setEnabled(true);
            }
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
                + "<br>Money // " + player1.getMoney()
                + "</html>"
        );

        player2Data.setText(
                "<html>"
                + player2.getName()
                + "<br>Money // " + player2.getMoney()
                + "</html>"
        );
    }

    /**
     * Getterek, setterek
     */
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board bd) {
        board = bd;
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

    public JButton[] getP1TowerButtons() {
        return player1TowerButtons;
    }

    public JButton[] getP1UnitButtons() {
        return player1UnitButtons;
    }

    public JButton[] getP2TowerButtons() {
        return player2TowerButtons;
    }

    public JButton[] getP2UnitButtons() {
        return player2UnitButtons;
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

    public void setTestMode(boolean b) {
        testMode = b;
    }
}
