package model;

import java.awt.Stroke;

public class Client {
    String username;
    int currentBoardId;
    Stroke stroke;
    
    public Client(String username, int currentBoardId, Stroke stroke) {
        super();
        this.username = username;
        this.currentBoardId = currentBoardId;
        this.stroke = stroke;
    }
    
    public int getCurrentBoardId(){
        return currentBoardId;
    }
}
