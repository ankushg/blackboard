package model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * An object representing a single Client. Rep invariant is that each client
 * generated is unique due to the id attribute.
 * 
 */
public class Client {
    /**
     * The username of the Client
     */
    String username;
    /**
     * The currentBoardId of the Client
     */
    String currentBoardId;

    @SuppressWarnings("javadoc")
    static final AtomicLong NEXT_ID = new AtomicLong(0);
    /**
     * The id of the Client. Guarantees that any Client object will be unique.
     */
    private final long id = NEXT_ID.getAndIncrement();

    /**
     * Create a new Client associated with a given boardId String
     * 
     * @param boardId
     *            the board that the client is associated with
     */
    public Client(String boardId) {
        super();
        this.username = "User" + id;
        this.currentBoardId = boardId;
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

    @Override
    public String toString() {
        return String.format("Client %s, connected to board %s", username, currentBoardId);
    }

}
