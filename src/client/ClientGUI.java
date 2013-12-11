package client;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.DrawingOperationProtocol;


public class ClientGUI extends JFrame {

	private PrintWriter w;
	private BufferedReader r;
	private Socket socket;
	public static final String CURRENT_BOARDS = "currentBoards ";
	public static final String USER_QUIT = "userQuit ";
	public static final String USER_JOINED = "userJoined "; 
	public static final String USERNAME = "username ";
	public static final String USERNAME_CHANGED = "usernameChanged ";
	public static final String BOARD_CHANGED = "boardChanged ";
	public static final String NEW_BOARD = "newBoard ";

	public static final String[] SERVER_MESSAGE_LIST = { CURRENT_BOARDS, USER_QUIT, USER_JOINED, USERNAME,
			USERNAME_CHANGED, BOARD_CHANGED, NEW_BOARD};
	private final ClientEasel easel;
	private final ClientInfoPanel infoPanel;

	public ClientGUI() throws IOException {
		easel = new ClientEasel(ClientCanvas.DEFAULT_WIDTH,
				ClientCanvas.DEFAULT_HEIGHT, this);
		infoPanel = new ClientInfoPanel(this);
		this.setTitle("Powerboard 3000");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		// set up layout
		Container mainPanel = this.getContentPane();
		GroupLayout layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(easel).addComponent(infoPanel));
		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(easel).addComponent(infoPanel));
		this.pack();
		this.setVisible(true);

		showLoginScreen();

		// set up a thread to listen for and handle incoming server messages
		Thread clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					for (String line = r.readLine(); line != null; line = r
							.readLine()) {
						for (String drawingMessage : DrawingOperationProtocol.DRAWING_MESSAGE_LIST) {
							// if we have a drawing message, send it only to the
							// easel
							if (line.startsWith(drawingMessage)) {
								easel.receiveDrawingMessage(line);
							}
							// otherwise, it may need to go to easel or
							// infoPanel
						}

						if (line.startsWith(BOARD_CHANGED) || line.startsWith(USERNAME_CHANGED) 
								|| line.startsWith(USERNAME)) {
							easel.receiveServerMessage(line);
						}
						for (String serverMessage : SERVER_MESSAGE_LIST)
						if (line.startsWith(serverMessage)) {
							infoPanel.receiveServerMessage(line);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {

				}

			}

		});
		clientThread.start();

		// ping the server for the current boards
		getBoardList();
	}

	/**
	 * Prompts the user with a dialog for server info, then initializes a socket
	 * connection with the server
	 * 
	 * @throws IOException
	 */
	private void showLoginScreen() throws IOException {
		// create the text fields
		JTextField IPAddress = new JTextField(5);
		IPAddress.setText("localhost");
		JTextField portNum = new JTextField(5);
		portNum.setText("4500");

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Server IP Address:"));
		myPanel.add(IPAddress);
		myPanel.add(Box.createHorizontalStrut(15)); // a spacer
		myPanel.add(new JLabel("Server Port #:"));
		myPanel.add(portNum);

		int result = JOptionPane.showConfirmDialog(null, myPanel,
				"Please enter the server information:",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				socket = new Socket(IPAddress.getText(),
						Integer.parseInt(portNum.getText()));
				w = new PrintWriter(socket.getOutputStream(), true);
				r = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (result == JOptionPane.CANCEL_OPTION) {
			this.dispose();
		}
	}

	/**
	 * Send a message to the server to retrieve the list of current boards
	 * 
	 * The return message will come in through the clientThread
	 */
	public synchronized void getBoardList() {
		this.sendMessage("listBoards");
	}

	/**
	 * This method sends a message to the server in a threadsafe manner
	 * 
	 * @param output
	 *            the String to send to the server
	 */
	public synchronized void sendMessage(String output) {
		w.println(output);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ClientGUI main;
				try {
					main = new ClientGUI();
					main.setVisible(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}