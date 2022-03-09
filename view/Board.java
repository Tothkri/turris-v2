package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import model.*;

public class Board extends JPanel{
    
    private final Model model;
    private final int width;
    private final int height;
    
    public Board(int selectedMap, String p1Name, String p2Name, int width, int height){
        model=new Model(selectedMap, p1Name, p2Name,height-150);
        this.setPreferredSize(new Dimension(height-150,height-150));
        this.width=width;
        this.height=height;
    }
    public Model getModel(){
        return model;
    }
  
    
    @Override
    protected void paintComponent(Graphics grphcs) {
        
        //painting the whole board
        super.paintComponent(grphcs);
        Graphics2D g2 = (Graphics2D) grphcs;
        grphcs.drawImage(new ImageIcon("src/res/Background.png").getImage(), 0, 0, height-150,height-150, null);
        ArrayList<Sprite> terrain=model.getTerrain();
       
        for (Sprite s : terrain) {
            
            s.draw(g2);
        }
    }
}
