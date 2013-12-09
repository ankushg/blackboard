package canvas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

import model.Client;

public class WhiteboardServer {

    private static String DEFAULT_BOARD = "default";
    private ServerSocket serverSocket;
    @SuppressWarnings("unused")
    private boolean debug;

    private Map<String, List<String>> boards = new HashMap<>();
    private List<WhiteboardThread> clientThreads = new ArrayList<>();

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
            final Client newClient = new Client(DEFAULT_BOARD);
            WhiteboardThread thread = new WhiteboardThread(socket, newClient);
            clientThreads.add(thread);
            // handle the client
            thread.start();
        }
    }

    /**
     * @param boardId
     * @return a List of all WhiteboardThreads working on the given boardId
     */
    private List<WhiteboardThread> getCoworkers(String boardId) {
        List<WhiteboardThread> list = new ArrayList<>();
        for (WhiteboardThread w : clientThreads) {
            if (w.client.getCurrentBoardId().equals(boardId)) {
                list.add(w);
            }
        }
        return list;
    }

    /**
     * @param client
     * @return The first WhiteboardThread found that is using Client c
     */
    private WhiteboardThread getThread(Client client) {
        for (WhiteboardThread w : clientThreads) {
            if (w.client.equals(client)) {
                return w;
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
    private boolean removeThread(Client client) {
        for (WhiteboardThread w : clientThreads) {
            if (w.client.equals(client)) {
                return clientThreads.remove(w);
            }
        }
        return false;
    }

    /**
     * Sends message to any thread working on the given board and adds it to the
     * board's transcript.
     * 
     * @param message
     * @param boardId
     */
    private void announceMessage(String message, String boardId) {
        boards.get(boardId).add(message);
        for (WhiteboardThread w : clientThreads) {
            if (w.client.getCurrentBoardId().equals(boardId)) {
                w.sendMessage(message);
            }
        }
    }

    /**
     * @param input
     * @return true of input is a client operation, false if it is a board
     *         operation (i.e., needs to be echoed to every other thread)
     */
    private static boolean isClientOperation(String input) {
        String regex = "(changeBoard [a-zA-Z0-9_]+)" + "|(setUsername [a-zA-Z0-9_]+)" + "|(listBoards)";
        return input.matches(regex);
    }

    private void handleClientOperation(String input, Client client) {
        assert (isClientOperation(input));

        WhiteboardThread thread = getThread(client);
        assert (thread != null);

        String[] args = input.split(" ");
        String command = args[0];
        switch (command) {
        case "listBoards":
            thread.sendMessages(new ArrayList<String>(boards.keySet()));
            break;
        case "changeBoard":
            String newBoard = args[1];
            announceMessage("userQuit " + client.getUsername(), client.getCurrentBoardId());
            joinBoard(client, newBoard);
            break;

        case "setUsername":
            String newName = args[1];
            announceMessage("userQuit " + client.getUsername(), client.getCurrentBoardId());
            client.setUsername(newName);
            announceMessage("userJoined " + client.getUsername(), client.getCurrentBoardId());
            break;
        }
    }

    private void joinBoard(Client client, String newBoard) {
        WhiteboardThread thread = getThread(client);
        assert (thread != null);
        
        client.setCurrentBoardId(newBoard);
        // create board if it doesn't exist
        if (!boards.containsKey(newBoard)) {
            boards.put(newBoard, new ArrayList<String>());
        }
        thread.sendMessages(boards.get(newBoard));
        announceMessage("userJoined " + client.getUsername(), newBoard);
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
                    // TODO output welcome message to client
                    out.println("");
                    joinBoard(client, "default");

                    try {
                        for (String line = in.readLine(); line != null; line = in.readLine()) {
                            if (line.equals("exit")) {
                                announceMessage("userQuit " + client.getUsername(), client.getCurrentBoardId());
                                break;
                            }
                            handleRequest(line);
                        }
                    } finally {
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
                        boards.get(client.getCurrentBoardId()).add(input);
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
                    // TODO Auto-generated method stub
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
                boards.put(client.getCurrentBoardId(), new ArrayList<String>());
            }

        }

        /**
         * Send a message to the outputstream of this thread's client
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
         * as a single message separated by newlines
         * 
         * @param messages
         *            the messages
         */
        public synchronized void sendMessages(List<String> messages) {
            StringBuilder sb = new StringBuilder();
            for (String s : messages) {
                sb.append(s + "\n");
            }
            if (sb.length() > 2) {
                sendMessage(sb.substring(0, sb.length() - 1));
            }
        }
    }

}
