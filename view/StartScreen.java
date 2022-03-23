package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import model.Castle;
import model.Fortified;
import model.Lake;
import model.Model;
import model.Mountain;
import model.Player;
import model.Rapid;
import model.Sniper;

public class StartScreen extends JPanel {

    private final JButton startButton;
    private final JButton loadButton;
    private final JTextField p1Name;
    private final JTextField p2Name;
    private final JRadioButton map1;
    private final JRadioButton map2;
    private final JRadioButton map3;

    private final int width;
    private final int height;
    
    private final Image Lake = new ImageIcon("src/res/Lake.png").getImage();
    private final Image Unit = new ImageIcon("src/res/Unit.png").getImage();
    private final Image Mountain = new ImageIcon("src/res/Mountain.png").getImage();
    private final Image Tower = new ImageIcon("src/res/Tower.png").getImage();
    private final Image Castle = new ImageIcon("src/res/Castle.png").getImage();

    /**
    * Játék kezdőképernyője
    * @param width
    * @param height
    * @return
    */
    public StartScreen(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        startButton = new JButton("Start Game");
        loadButton = new JButton("Load Game");
        p1Name = new JTextField(15);
        p2Name = new JTextField(15);
        map1 = new JRadioButton("Map 1 (Mountains and lakes mixed)");
        map2 = new JRadioButton("Map 2 (Mountains in majority)");
        map3 = new JRadioButton("Map 3 (Lakes in majority)");
        setPanels();
    }
    

    /**
    * Játék betöltése
    * @param gw
    * @return
    */
    public void loadGame(GameWindow gw){
        File userDir = new File(System.getProperty("user.dir"));
        File[] allfile = userDir.listFiles();
        String fileName = "";
        int size = height - 150;
        Model model = new Model(size);

        for (int i = 0; i < allfile.length; i++) {
            if(allfile[i].getName().contains("txt")){
                fileName = allfile[i].getName();
            }
        }

        try{
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            
            Castle c1 = new Castle();
            Castle c2 = new Castle();
            int castleCount = 0;
            for(int i = 0; i < 30;i++){
                String data = myReader.nextLine();
                for (int j = 0; j < 30; j++) {
                    model.getPosition()[j][i] = data.charAt(i);
                    if(data.charAt(j) == 'C'){
                        castleCount++;
                        model.getPosition()[j][i] = 'C';
                    }
                    if(castleCount == 3){
                        c1 = new Castle(j * (size / 30), 2 * (size / 30), (size / 15), (size / 15), Castle, 300);
                    }

                    if(castleCount == 7){
                        c2 = new Castle(j * (size / 30), 26 * (size / 30), (size / 15), (size / 15), Castle, 300);
                    }

                    if(data.charAt(j) == 'F'){
                        model.getPosition()[j][i] = 'F';
                    } else if(data.charAt(j) == 'M') {
                        model.getPosition()[j][i] = 'M';
                        model.addTerrainElement(new Mountain(j * (size / 30), i * (size / 30), (size / 30), (size / 30), Mountain));
                    } else if(data.charAt(j) == 'L') {
                        model.getPosition()[j][i] = 'L';
                        model.addTerrainElement(new Lake(j * (size / 30), i * (size / 30), (size / 30), (size / 30), Lake));
                    } else if(data.charAt(j) == 'T') {
                        model.getPosition()[j][i] = 'T';
                    }
                }
            }

            /**
            * Térkép kiválasztása
            * kezdő játékos kiválasztása
            * játékosok neveinek beállítása
            * játékosok pénzeinek kiosztása
            * kastélyok lehelyezése
            * terepakadályok lehelyezése
            * jelenlegi kör beállítása
            */
            model.setMap(Integer.parseInt(myReader.nextLine()));
            model.setActivePlayer(Integer.parseInt(myReader.nextLine()));
            model.setRound(Integer.parseInt(myReader.nextLine()));
            model.setCastleCords(c1,c2);
            model.addTerrainElement(c1);
            model.addTerrainElement(c2);
            
            myReader.nextLine();
            String name = myReader.nextLine();
            int money = Integer.parseInt(myReader.nextLine());
            model.getPlayers()[0] = new Player(money,name);
            model.getPlayers()[0].setCastle(c1);
            
            String data = myReader.nextLine();
            while(!data.equals("p:")){
                if(!data.equals("")){
                    String[] arr = new String[12];
                    arr = data.split(" ");
                    if(arr[5].equals("Fortified")){
                        String png = "Tower";
                        
                        if(!arr[11].equals("-1")) png = "destroyed";
                        
                        Fortified f = new Fortified(arr[5], Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Double.parseDouble(arr[8]),
                        Integer.parseInt(arr[9]),Integer.parseInt(arr[10]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),Integer.parseInt(arr[3]),
                        Integer.parseInt(arr[4]),new ImageIcon("src/res/"+png+".png").getImage());
                        
                        if(Integer.parseInt(arr[11]) == -1) f.setDemolishedIn(0);//-1
                        else f.setDemolishedIn((Integer.parseInt(arr[11]) + 1) * -1);//can change
                        
                        model.getPlayers()[0].addTower(f);
                        model.addTerrainElement(f);
                    } else if(arr[5].equals("Rapid")) {
                        String png = "Tower";
                        
                        if(!arr[11].equals("-1")) png = "destroyed";
                        Rapid f = new Rapid(arr[5], Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Double.parseDouble(arr[8]),
                        Integer.parseInt(arr[9]),Integer.parseInt(arr[10]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),Integer.parseInt(arr[3]),
                        Integer.parseInt(arr[4]),new ImageIcon("src/res/"+png+".png").getImage());
                        
                        if(Integer.parseInt(arr[11]) == -1) f.setDemolishedIn(0);//-1
                        else f.setDemolishedIn((Integer.parseInt(arr[11]) + 1) * -1);//can change
                        
                        model.getPlayers()[0].addTower(f);
                        model.addTerrainElement(f);
                    } else if(arr[5].equals("Sniper")) {
                        String png = "Tower";
                        
                        if(!arr[11].equals("-1")) png = "destroyed";
                        Sniper f = new Sniper(arr[5], Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Double.parseDouble(arr[8]),
                        Integer.parseInt(arr[9]),Integer.parseInt(arr[10]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),Integer.parseInt(arr[3]),
                        Integer.parseInt(arr[4]),new ImageIcon("src/res/"+png+".png").getImage());
                        
                        if(Integer.parseInt(arr[11]) == -1) f.setDemolishedIn(0);//-1
                        else f.setDemolishedIn((Integer.parseInt(arr[11]) + 1) * -1);//can change
                        
                        model.getPlayers()[0].addTower(f);
                        model.addTerrainElement(f);
                    }
                }
                data = myReader.nextLine();
            }
            name = myReader.nextLine();
            
            money = Integer.parseInt(myReader.nextLine());
            data = myReader.nextLine();
            model.getPlayers()[1] = new Player(money,name);
            model.getPlayers()[1].setCastle(c2);
            while(!data.equals("")){
                String[] arr = new String[12];
                arr = data.split(" ");
                if(arr[5].equals("Fortified")){
                        String png = "Tower";
                        
                        if(!arr[11].equals("-1")) png = "destroyed";
                        
                        Fortified f = new Fortified(arr[5], Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Double.parseDouble(arr[8]),
                        Integer.parseInt(arr[9]),Integer.parseInt(arr[10]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),Integer.parseInt(arr[3]),
                        Integer.parseInt(arr[4]),new ImageIcon("src/res/"+ png +".png").getImage());
                        
                        if(Integer.parseInt(arr[11]) == -1) f.setDemolishedIn(0);//-1
                        else f.setDemolishedIn((Integer.parseInt(arr[11]) + 1) * -1);//can change
                        
                        model.getPlayers()[1].addTower(f);
                        model.addTerrainElement(f);
                    }else if(arr[5].equals("Rapid")){
                        String png = "Tower";
                        
                        if(!arr[11].equals("-1")) png = "destroyed";
                        
                        Rapid f = new Rapid(arr[5], Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Double.parseDouble(arr[8]),
                        Integer.parseInt(arr[9]),Integer.parseInt(arr[10]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),Integer.parseInt(arr[3]),
                        Integer.parseInt(arr[4]),new ImageIcon("src/res/Tower.png").getImage());
                        
                        if(Integer.parseInt(arr[11]) == -1) f.setDemolishedIn(0);//-1
                        else f.setDemolishedIn((Integer.parseInt(arr[11]) + 1) * -1);//can change

                        model.getPlayers()[1].addTower(f);
                        model.addTerrainElement(f);
                    }else if(arr[5].equals("Sniper")){
                        String png = "Tower";
                        
                        if(!arr[11].equals("-1")) png = "destroyed";
                        
                        Sniper f = new Sniper(arr[5], Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Double.parseDouble(arr[8]),
                        Integer.parseInt(arr[9]),Integer.parseInt(arr[10]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),Integer.parseInt(arr[3]),
                        Integer.parseInt(arr[4]),new ImageIcon("src/res/Tower.png").getImage());
                        
                        if(Integer.parseInt(arr[11]) == -1) f.setDemolishedIn(0);//-1
                        else f.setDemolishedIn((Integer.parseInt(arr[11]) + 1) * -1);//can change
                        
                        model.getPlayers()[1].addTower(f);
                        model.addTerrainElement(f);
                    }
                data = myReader.nextLine();
            }
            Board bd = new Board(width,height);
            bd.setModel(model);
            gw.setBoard(bd);

        }catch(IOException e){
            JOptionPane.showMessageDialog(null, "No saved games found!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
    * Játék kezdőoldalának vizuális beállítása
    */
    public void setPanels() {

        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel gameName         = new JLabel("Tower Defense Game");
        JLabel playerNames      = new JLabel("Your Player Names:");
        JLabel p1NameLabel      = new JLabel("Player 1:   ");
        JLabel p2NameLabel      = new JLabel("Player 2:   ");
        JLabel pickMap          = new JLabel("Pick a map!");
        JLabel loadGameLabel    = new JLabel("Load a saved game!");
        JLabel startGameLabel   = new JLabel("Start the game!");

        JPanel gameNameRow          = new JPanel();
        JPanel playerNamesRow       = new JPanel();
        JPanel p1NameRow            = new JPanel();
        JPanel p2NameRow            = new JPanel();
        JPanel pickMapLabelRow      = new JPanel();
        JPanel loadGameLabelRow     = new JPanel();
        JPanel loadGameButtonRow    = new JPanel();
        JPanel startGameLabelRow    = new JPanel();
        JPanel startGameButtonRow   = new JPanel();

        p1NameRow.setLayout(new FlowLayout());
        p2NameRow.setLayout(new FlowLayout());
        
        
        /**
        * játék oldalán használt színek
        */
        Color hunterGreen   = new Color(63, 122, 77);
        Color algaeGreen    = new Color(105, 168, 120);
        Color veryLightGray = new Color(220, 220, 220);
        
        this.setBackground(algaeGreen);
        

        /**
        * Game Name panel
        */
        gameNameRow.add(gameName);
        gameNameRow.setBorder(new EmptyBorder(30, 10, 10, 10));
        gameNameRow.setBackground(Color.LIGHT_GRAY);
        
        gameName.setFont(new Font("Calibri", Font.BOLD, 80));
        
        gbc.insets = new Insets(0, 50, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        this.add(gameNameRow, gbc);
        

        /**
        * Player Name
        */
        playerNames.setFont             (new Font("Calibri", Font.PLAIN, 70));
        
        playerNamesRow.add(playerNames);
        playerNamesRow.setPreferredSize (new Dimension(700, 100));
        playerNamesRow.setBorder        (new EmptyBorder(14, 0, 0, 105));
        playerNamesRow.setBackground    (Color.LIGHT_GRAY);
        
        gbc.insets = new Insets(0, -800, -200, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(playerNamesRow, gbc);
        
        
        /**
        * Player1 name input
        */
        p1NameLabel.setFont             (new Font("Calibri", Font.PLAIN, 50));
        
        p1NameRow.add(p1NameLabel);
        p1NameRow.add(p1Name);
        p1NameRow.setPreferredSize      (new Dimension(700, 80));
        p1NameRow.setBorder             (new EmptyBorder(10, 0, 0, 105));
        p1NameRow.setBackground         (veryLightGray);
        
        p1Name.setFont                  (new Font("Calibri", Font.PLAIN, 24));
        p1Name.setPreferredSize         (new Dimension(300, 30));
        
        gbc.insets = new Insets(150, -800, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(p1NameRow, gbc);
        
        
        /**
        * Player2 name input
        */
        p2NameLabel.setFont             (new Font("Calibri", Font.PLAIN, 50));
        
        p2NameRow.add(p2NameLabel);
        p2NameRow.add(p2Name);
        p2NameRow.setPreferredSize      (new Dimension(700, 80));
        p2NameRow.setBorder             (new EmptyBorder(10, 0, 0, 105));
        p2NameRow.setBackground         (veryLightGray);
        
        p2Name.setFont                  (new Font("Calibri", Font.PLAIN, 24));
        p2Name.setPreferredSize         (new Dimension(300, 30));

        gbc.insets = new Insets(0, -800, -80, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;
        this.add(p2NameRow, gbc);
        
        
        /**
        * Map label
        */
        pickMap.setFont                     (new Font("Calibri", Font.PLAIN, 70));
        
        pickMapLabelRow.add(pickMap);
        pickMapLabelRow.setPreferredSize    (new Dimension(700, 100));
        pickMapLabelRow.setBorder           (new EmptyBorder(14, 0, 0, 325));
        pickMapLabelRow.setBackground       (Color.LIGHT_GRAY);
        
        gbc.insets = new Insets(100, -800, -40, 0);
        gbc.gridx = 0;
        gbc.gridy = 4;
        this.add(pickMapLabelRow, gbc);
        

        /**
        * Map selection
        */
        ButtonGroup group = new ButtonGroup();
        map1.setSelected(true);
        
        group.add(map1);
        group.add(map2);
        group.add(map3);
        
        /**
        * Map 1
        */
        map1.setFont               (new Font("Calibri", Font.PLAIN, 50));
        map1.setPreferredSize      (new Dimension(700, 80));
        map1.setBorder             (new EmptyBorder(20, 40, 0, 0));
        map1.setBackground         (veryLightGray);
        
        gbc.insets = new Insets(0, -800, -150, 0);
        gbc.gridx = 0;
        gbc.gridy = 5;
        this.add(map1, gbc);

        /**
        * Map 2
        */
        map2.setFont               (new Font("Calibri", Font.PLAIN, 50));
        map2.setPreferredSize      (new Dimension(700, 80));
        map2.setBorder             (new EmptyBorder(20, 40, 0, 0));
        map2.setBackground         (veryLightGray);
            
        gbc.insets = new Insets(70, -800, -240, 0);
        gbc.gridx = 0;
        gbc.gridy = 6;
        this.add(map2, gbc);

        /**
        * Map 3
        */
        map3.setFont               (new Font("Calibri", Font.PLAIN, 50));
        map3.setPreferredSize      (new Dimension(700, 80));
        map3.setBorder             (new EmptyBorder(20, 40, 0, 0));
        map3.setBackground         (veryLightGray);
        
        gbc.insets = new Insets(190, -800, -150, 0);
        gbc.gridx = 0;
        gbc.gridy = 7;
        this.add(map3, gbc);

        
        /**
        * Load game
        */
        loadGameLabel.setFont                  (new Font("Calibri", Font.PLAIN, 70));
        
        loadGameLabelRow.setPreferredSize      (new Dimension(700, 100));
        loadGameLabelRow.setBorder             (new EmptyBorder(13, 0, 0, 95));
        loadGameLabelRow.setBackground         (Color.LIGHT_GRAY);
        loadGameLabelRow.add(loadGameLabel);

        gbc.insets = new Insets(160, -800, -280, 0);
        gbc.gridx = 0;
        gbc.gridy = 8;
        this.add(loadGameLabelRow, gbc);
        

        /**
        * Load game button
        */
        loadButton.setFont                  (new Font("Calibri", Font.PLAIN, 35));
        loadButton.setPreferredSize         (new Dimension(400, 55));
        loadButton.setBorder                (new EmptyBorder(13, 0, 0, 0));
        
        loadGameButtonRow.setPreferredSize  (new Dimension(700, 90));
        loadGameButtonRow.setBackground     (veryLightGray);
        loadGameButtonRow.setBorder         (new EmptyBorder(18, 0, 0, 0));
        loadGameButtonRow.add(loadButton);
        
        gbc.insets = new Insets(260, -800, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 9;
        this.add(loadGameButtonRow, gbc);
        
        
        /**
        * Start button label
        */
        startGameLabel.setFont                  (new Font("Calibri", Font.PLAIN, 70));
        
        startGameLabelRow.setPreferredSize      (new Dimension(700, 100));
        startGameLabelRow.setBorder             (new EmptyBorder(13, 155, 0, 150));
        startGameLabelRow.setBackground         (Color.LIGHT_GRAY);
        startGameLabelRow.add(startGameLabel);
        
        gbc.insets = new Insets(0, -300, -450, -300);
        gbc.gridx = 1;
        gbc.gridy = 8;
        this.add(startGameLabelRow, gbc);
        
        
        /**
        * Start button
        */
        startButton.setFont                     (new Font("Calibri", Font.PLAIN, 35));
        startButton.setPreferredSize            (new Dimension(400, 55));
        startButton.setBorder                   (new EmptyBorder(13, 0, 0, 0));
        
        startGameButtonRow.setPreferredSize     (new Dimension(700, 90));
        startGameButtonRow.setBackground        (veryLightGray);
        startGameButtonRow.setBorder            (new EmptyBorder(18, 0, 0, 0));
        startGameButtonRow.add(startButton);
        
        gbc.insets = new Insets(0, -300, -270, -300);
        gbc.gridx = 1;
        gbc.gridy = 9;
        this.add(startGameButtonRow, gbc);
    }


    /**
     * Checks if the names are correct or not
     * @param
     * @return correct or not
     */
    public boolean isCorrect() {
        boolean f = true;
        if (p1Name.getText().equals("")) {
            p1Name.setBackground(Color.red);
            p1Name.setText("");
            f = false;
        } else {
            p1Name.setBackground(Color.white);
        }

        if (p2Name.getText().equals("")) {
            p2Name.setBackground(Color.red);
            p2Name.setText("");
            f = false;
        } else if (p1Name.getText().equals(p2Name.getText())) {
            p1Name.setBackground(Color.red);
            p2Name.setBackground(Color.red);
            return false;
        } else {
            p2Name.setBackground(Color.white);
        }

        return f;
    }


    /**
     * Getterek, setterek
     */
    public JButton getStartButton() {
        return startButton;
    }
    public JButton getLoadButton(){
        return loadButton;
    }

    public JTextField getP1Name() {
        return p1Name;
    }

    public JTextField getP2Name() {
        return p2Name;
    }


    /**
     * Rádiógombok beállítása
     */
    public int getSelectedRadioButton() {
        if (map1.isSelected()) {
            return 1;
        } else if (map2.isSelected()) {
            return 2;
        }
        return 3;
    }
}
