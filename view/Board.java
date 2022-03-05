package view;

import model.Model;

public class Board {
    
    private Model model;
    
    public Board(int selectedMap, String p1Name, String p2Name)
    {
        model = new Model(selectedMap, p1Name, p2Name);
    }
    public Model getModel()
    {
        return model;
    }
}
