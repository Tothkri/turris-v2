package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameWindow extends JPanel {
    
    private final JButton teszt;
    private final Timer timer;
    private Board board;
    
    
    public GameWindow(int width, int height)
    {
        super();
        
        teszt = new JButton("GameWindow button");
        this.add(teszt);
        timer = new Timer(1000 / 100, (ActionEvent ae) ->
        {
            
        });
    }
    
    public void setBoard(int selectedMap, String p1Name, String p2Name)
    {
        board = new Board(selectedMap, p1Name, p2Name);
    }
    
    public JButton getTeszt()
    {
        return teszt;
    }
    
    public void simulation()
    {
        
    }
    public void newRound()
    {
        
    }
    public void gameOver()
    {
        
    }
    public void saveGame()
    {
        
    }
    
}
