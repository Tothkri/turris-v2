package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

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
    
    public StartScreen(int width, int height){
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
    public void loadGame(){
        
    }
    
    public void setPanels(){
        
        this.setPreferredSize(new Dimension(width,height));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel gameName = new JLabel("Tower Defense Game");
        JLabel playerNames = new JLabel("Your Player Names");
        JLabel p1NameLabel = new JLabel("Player 1:");
        JLabel p2NameLabel = new JLabel("Player 2:");
        JLabel pickMap = new JLabel("Pick a map!");
        JLabel loadGameLabel = new JLabel("Load game! (If you have any...)");
  
        JPanel gameNameRow = new JPanel();
        JPanel playerNamesRow = new JPanel();
        JPanel p1NameRow = new JPanel();
        JPanel p2NameRow = new JPanel();
        JPanel pickMapLabelRow = new JPanel();
        JPanel loadGameLabelRow = new JPanel();
        
        p1NameRow.setLayout(new FlowLayout());
        p2NameRow.setLayout(new FlowLayout());
        
        gameNameRow.add(gameName);
        playerNamesRow.add(playerNames);
        p1NameRow.add(p1NameLabel);
        p1NameRow.add(p1Name);
        p2NameRow.add(p2NameLabel);
        p2NameRow.add(p2Name);
        p2NameRow.add(p2Name);
        pickMapLabelRow.add(pickMap);
        loadGameLabelRow.add(loadGameLabel);
        
        ButtonGroup group = new ButtonGroup();
        map1.setSelected(true);
        
        group.add(map1);
        group.add(map2);
        group.add(map3);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,0,150,0);
        this.add(gameNameRow,gbc);
        
        gbc.insets = new Insets(0,0,0,200);
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(playerNamesRow,gbc);
       
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(p1NameRow,gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        this.add(p2NameRow,gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        this.add(pickMapLabelRow,gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        this.add(map1,gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        this.add(map2,gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        this.add(map3,gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        this.add(loadGameLabelRow,gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 9;
        this.add(loadButton,gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 9;
        this.add(startButton,gbc);
    }
    /**
     * Checks if the names are correct or not
     * @return correct or not
     */
    public boolean isCorrect(){
        boolean f = true;
        if(p1Name.getText().equals("")){
            p1Name.setBackground(Color.red);
            p1Name.setText("");
            f = false;
        }else p1Name.setBackground(Color.white);
        if(p2Name.getText().equals("")){
            p2Name.setBackground(Color.red);
            p2Name.setText("");
            f = false;
        }else if(p1Name.getText().equals(p2Name.getText())){
            p1Name.setBackground(Color.red);
            p2Name.setBackground(Color.red);
            return false;
        }else p2Name.setBackground(Color.white);
        
        return f;
    }
    public JButton getStartButton(){return startButton;}
    public JTextField getP1Name(){return p1Name;}
    public JTextField getP2Name(){return p2Name;}
    public int getSelectedRadioButton(){
        if(map1.isSelected()) return 1;
        else if(map2.isSelected()) return 2;
        return 3;
    }
}
