package canvas;

import java.awt.BasicStroke;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import model.Board;
import model.Client;

public class WhiteboardServer {

    private ServerSocket serverSocket;
    private boolean debug;
    
    private List<Board> boards;
    private List<WhiteboardThread> clientThreads;

    /**
     * Make a WhiteboardServer that listens for connections on port.
     * 
     * @param port
     *            port number, requires 0 <= port <= 65535
     * @param debug
     *            whether debug mode is on on or not
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
     * "MinesweeperServer --port 1234" starts the server listening on port 1234.
     */
    public static void main(@SuppressWarnings("javadoc") String[] args) {
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
            System.err.println("usage: MinesweeperServer [--debug] [--port PORT] [--size SIZE | --file FILE]");
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
        // TODO Auto-generated method stub
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();
            final Client newClient = new Client("", 0, new BasicStroke(10));
            WhiteboardThread thread = new WhiteboardThread(socket, newClient);
            clientThreads.add(thread);
            // handle the client
            thread.start();
        }
    }
    
    private List<WhiteboardThread> getCoworkers(int mapId){
        List<WhiteboardThread> list = new ArrayList<>();
        for(WhiteboardThread w : clientThreads){
            if(w.client.getCurrentBoardId() == mapId){
                list.add(w);
            }
        }
        return list;
    }

    
    
    private class WhiteboardThread extends Thread {
        private final Client client;

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
                    try {
                        for (String line = in.readLine(); line != null; line = in.readLine()) {
                            String output = handleRequest(line);
                            if (output != null) {
                                if (output.equals("")) {
                                    out.close();
                                    in.close();
                                    return;
                                } else {
                                    out.println(output);
                                }
                            }
                        }
                    } finally {
                        out.close();
                        in.close();
                    }
                }

                /**
                 * Handler for client input, performing requested operations and
                 * returning an output message.
                 * 
                 * @param input
                 *            message from client
                 * @return message to client
                 */
                private String handleRequest(String input) {
                    // TODO: handle requests from client

                    throw new UnsupportedOperationException();
                }

                /**
                 * Disconnect the socket in this thread and decrement the
                 * sockets counter
                 */
                private void disconnectSocket() {
                    try {
                        socket.close();
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
                        disconnectSocket();
                    }
                }
            });
            
            this.client = client;

        }
    }

}
