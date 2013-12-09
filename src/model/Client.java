package model;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.concurrent.atomic.AtomicLong;

public class Client {
    String username;
    String currentBoardId;
    Stroke stroke;

    // Guarantees that any client object will be unique
    static final AtomicLong NEXT_ID = new AtomicLong(0);
    final long id = NEXT_ID.getAndIncrement();

    public Client(String boardId) {
        super();
        this.username = "User" + id;
        this.currentBoardId = boardId;
        this.stroke = new BasicStroke(10);
    }

    /**
     * @return the username
     */
    public synchronized String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public synchronized void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the currentBoardId
     */
    public synchronized String getCurrentBoardId() {
        return currentBoardId;
    }

    /**
     * @param currentBoardId
     *            the currentBoardId to set
     */
    public synchronized void setCurrentBoardId(String currentBoardId) {
        this.currentBoardId = currentBoardId;
    }

    /**
     * @return the stroke
     */
    public synchronized Stroke getStroke() {
        return stroke;
    }

    /**
     * @param stroke
     *            the stroke to set
     */
    public synchronized void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    @Override
    public String toString() {
        return String.format("Client %s, connected to board %s", username, currentBoardId);
    }

}
