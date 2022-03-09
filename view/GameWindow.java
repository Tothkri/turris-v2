package view;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameWindow extends  JPanel {
    
    private final Timer timer;
    private Board board;
    
    
    public GameWindow(int width, int height, String p1Name, String p2Name, int selectedMap)
    {
        super();
        
        board=new Board(selectedMap,p1Name,p2Name,width,height);
        
        this.add(board);
        
        setSize(width,height);
        this.setPreferredSize(new Dimension(width,height));
        
         timer = new Timer(250, (ActionEvent ae) -> {
          board.repaint();
         });
        
    }
    
    public void simulation(){
        
    }
    public void newRound(){
        
    }
    public void gameOver(){
        
    }
    public void saveGame(){
        
    }
    
}
