package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

import model.Client;

/**
 * WhiteboardServer manages everything.
 * 
 * @author ankush
 * 
 */
public class WhiteboardServer {

    /**
     * The default board that the user connects to upon joining the server
     */
    private static String DEFAULT_BOARD = "default";
    private static String NO_BOARD = "";

    private ServerSocket serverSocket;

    @SuppressWarnings("unused")
    private boolean debug;

    /**
     * Store ALL the whiteboards! Boards are represented by lists of string
     * outputs that construct them (they're constructed on the client-side).
     * Stored as a synchronized Map to make operations atomic.
     */
    private Map<String, List<String>> boards = Collections.synchronizedMap(new HashMap<String, List<String>>());

    /**
     * Store references to all WhiteboardThreads that are connected to the
     * server. Stored as a synchronized List to make operations atomic.
     */
    private List<WhiteboardThread> clientThreads = Collections.synchronizedList(new ArrayList<WhiteboardThread>());

    /**
     * Make a WhiteboardServer that listens for connections on port.
     * 
     * @param port
     *            port number, requires 0 <= port <= 65535
     * @param debug
     *            whether debug mode is on on or not
     * @throws IOException
     */
    public WhiteboardServer(int port, boolean debug) throws IOException {
        serverSocket = new ServerSocket(port);
        this.debug = debug;
    }

    /**
     * Start a WhiteboardServer using the given arguments.
     * 
     * Usage: WhiteboardServer [--debug] [--port PORT]
     * 
     * The --debug argument means the server should run in debug mode.
     * 
     * PORT is an optional integer in the range 0 to 65535 inclusive, specifying
     * the port the server should be listening on for incoming connections. E.g.
     * "WhiteboardServer --port 1234" starts the server listening on port 1234.
     * Defaults to port 4500.
     * 
     * @param args
     *            arguments
     */
    public static void main(String[] args) {
        // default values
        int port = 4500;
        boolean debug = true;

        // parse args
        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while (!arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--debug")) {
                        debug = true;
                    } else if (flag.equals("--no-debug")) {
                        debug = false;
                    } else if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > 65535) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            return;
        }

        // run the server with the given params
        try {
            runServer(port, debug);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start a WhiteboardServer running on the specified port and with the given
     * debug setting.
     * 
     * @param debug
     *            The server will send debug messages only if this is true
     * @param port
     *            The network port on which the server should listen.
     * @throws IOException
     */
    private static void runServer(int port, boolean debug) throws IOException {
        WhiteboardServer whiteboardServer = new WhiteboardServer(port, debug);
        whiteboardServer.serve();
    }

    /**
     * Run the server, listening for client connections and handling them. Never
     * returns unless an exception is thrown.
     * 
     * @throws IOException
     *             if the main server socket is broken (IOExceptions from
     *             individual clients do *not* terminate serve())
     */
    private void serve() throws IOException {
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();
            final Client newClient = new Client(NO_BOARD);
            WhiteboardThread thread = new WhiteboardThread(socket, newClient);
            clientThreads.add(thread);
            // handle the client
            thread.start();
        }
    }

    /**
     * @param client
     * @return The first WhiteboardThread found that is using Client c
     */
    private synchronized WhiteboardThread getThread(Client client) {
        synchronized (clientThreads) {
            for (WhiteboardThread w : clientThreads) {
                if (w.client.equals(client)) {
                    return w;
                }
            }
        }
        return null;
    }

    /**
     * Removes any Thread with the given client
     * 
     * @param client
     * @return true if such a Thread was found and removed
     */
    private synchronized boolean removeThread(Client client) {
        synchronized (clientThreads) {
            for (WhiteboardThread w : clientThreads) {
                if (w.client.equals(client)) {
                    return clientThreads.remove(w);
                }
            }
        }
        return false;
    }

    /**
     * Sends message to ALL threads (but adds it to no transcript)
     * 
     * @param message
     */
    private synchronized void globalMessage(String message) {
        synchronized (clientThreads) {
            for (WhiteboardThread w : clientThreads) {
                w.sendMessage(message);
            }
        }
    }

    /**
     * Sends message to any thread working on the given board and adds it to the
     * board's transcript.
     * 
     * @param message
     * @param boardId
     */
    private synchronized void announceMessage(String message, String boardId) {
        synchronized (clientThreads) {
        	synchronized (boards) {
        		boards.get(boardId).add(message);
            }
            for (WhiteboardThread w : clientThreads) {
                if (w.client.getCurrentBoardId().equals(boardId)) {
                    w.sendMessage(message);
                }
            }
        }
    }

    /**
     * @param input
     * @return true of input is a client operation, false if it is a board
     *         operation (i.e., needs to be echoed to every other thread)
     */
    private static boolean isClientOperation(String input) {
        String regex = "(changeBoard [a-zA-Z0-9_]+)" + "|(setUsername [a-zA-Z0-9_]+)" + "|(listBoards)"
                + "|(getUsername)";
        return input.matches(regex);
    }

    /**
     * Handles input from a client. Thread-safe because the server only handles
     * one client operation at a time.
     * 
     * @param input
     *            the string to react to
     * @param client
     *            the client from which the input originates
     */
    private synchronized void handleClientOperation(String input, Client client) {
//        assert isClientOperation(input);
        WhiteboardThread thread = getThread(client);
        assert thread != null;

        String[] args = input.split(" ");
        String command = args[0];
        switch (command) {
        case "listBoards":
            StringBuilder sb = new StringBuilder();
            sb.append("currentBoards");
            for (String s : boards.keySet()) {
                sb.append(" " + s);
            }
            thread.sendMessage(sb.toString());
            break;
        case "changeBoard":
            String oldBoard = client.getCurrentBoardId();
            String newBoard = args[1];
            joinBoard(client, newBoard);
            break;
        case "setUsername":
            String newName = args[1];
            setUsername(client, newName);
            break;
        case "getUsername":
            thread.sendMessage("username " + client.getUsername());
            break;
        }
    }

    private boolean isUniqueUsername(String name) {
        synchronized (clientThreads) {
            for (WhiteboardThread t : clientThreads) {
                if (t.client.getUsername().equals(name)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setUsername(Client client, String newName) {
        // synchronized on client so that more than one joinBoard command isn't
        // run at a time for a given client
        synchronized (client) {
            WhiteboardThread thread = getThread(client);
            assert thread != null;

            if (isUniqueUsername(newName) && !newName.matches("User[0-9]+")) {
                thread.sendMessage(String.format("usernameChanged %s %s", client.getUsername(), newName));
                announceMessage("userQuit " + client.getUsername(), client.getCurrentBoardId());
                client.setUsername(newName);
                announceMessage("userJoined " + client.getUsername(), client.getCurrentBoardId());
            } else {
                // non-unique username or username in format "User[0-9]+"
                thread.sendMessage(String.format("usernameChanged %s %s", client.getUsername(), client.getUsername()));
            }
        }
    }

    /**
     * Helper method that allows the given client to join a board with the given
     * id. Creates the board if it does not exist. Also sends the board
     * transcript to the client and sends the appropriate userJoined message to
     * everyone.
     * 
     * @param client
     * @param newBoard
     */
    private void joinBoard(Client client, String newBoard) {
        // synchronized on client so that more than one joinBoard command isn't
        // run at a time for a given client
        synchronized (client) {
            WhiteboardThread thread = getThread(client);
            assert thread != null;
            
            String oldBoard = client.getCurrentBoardId();
            
            if (!oldBoard.equals(newBoard)){
	
	            // create board if it doesn't exist. synchronized on boards in case
	            // multiple users join the same board at the same time
	            synchronized (boards) {
	                if (!boards.containsKey(newBoard)) {
	                    boards.put(newBoard, new ArrayList<String>());
	                    globalMessage("newBoard " + newBoard);
	                }
	            }
	            announceMessage("userQuit " + client.getUsername(), oldBoard);
	            announceMessage("userJoined " + client.getUsername(), newBoard);
	            thread.sendMessage(String.format("boardChanged %s %s", oldBoard, newBoard));
	            client.setCurrentBoardId(newBoard);
	            thread.sendMessages(boards.get(newBoard));
	            
            }
        }
    }

    /**
     * The Thread responsible for a single Client's interactions with the
     * server.
     * 
     */
    private class WhiteboardThread extends Thread {
        private final Client client;
        private final Socket socket;

        /**
         * Creates a WhiteboardThread with the given Client and Socket
         * 
         * @param socket
         * @param client
         */
        private WhiteboardThread(final Socket socket, final Client client) {
            super(new Runnable() {
                /**
                 * Handle a single client connection. Returns when client
                 * disconnects.
                 * 
                 * @param socket
                 *            socket where the client is connected
                 * @throws IOException
                 *             if connection has an error or terminates
                 *             unexpectedly
                 */
                private void handleConnection(Socket socket) throws IOException {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    handleClientOperation("getUsername", client);
                    handleClientOperation("listBoards", client);
                    joinBoard(client, DEFAULT_BOARD);

                    try {
                        for (String line = in.readLine(); line != null; line = in.readLine()) {
                            if (line.equals("exit")) {
                                break;
                            }
                            handleRequest(line);
                        }
                    } finally {
                        announceMessage("userQuit " + client.getUsername(), client.getCurrentBoardId());
                        out.close();
                        in.close();
                    }
                }

                /**
                 * Handler for client input, performing requested operations and
                 * sending an output message.
                 * 
                 * @param input
                 *            message from client
                 */
                private void handleRequest(String input) {
                    if (isClientOperation(input)) {
                        handleClientOperation(input, client);
                    } else {
                        announceMessage(input, client.getCurrentBoardId());
                    }
                }

                /**
                 * Disconnect the socket in this thread and removes from the
                 * list of active threads.
                 */
                private void disconnectSocket(final Socket socket) {
                    try {
                        socket.close();
                        removeThread(client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void run() {
                    try {
                        handleConnection(socket);
                    } catch (IOException e) {
                        e.printStackTrace(); // but don't terminate serve()
                    } finally {
                        disconnectSocket(socket);
                    }
                }
            });

            this.client = client;
            this.socket = socket;
            if (!boards.containsKey(client.getCurrentBoardId())) {
                boards.put(client.getCurrentBoardId(), Collections.synchronizedList(new ArrayList<String>()));
            }

        }

        /**
         * Send a message to the outputstream of this thread's client.
         * Threadsafe because it is locked
         * 
         * @param string
         *            the message
         */
        public synchronized void sendMessage(String string) {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Send a list of messages to the outputstream of this thread's client
         * as a single message separated by newlines. This ensures that all
         * messages will be sent in order without interleaving.
         * 
         * Threadsafe because it is locked on the WhiteboardThread responsible
         * for a single client.
         * 
         * @param messages
         *            the messages
         */
        public synchronized void sendMessages(final List<String> messages) {
            for (String s : messages) {
                sendMessage(s);
            }
        }
    }

}
