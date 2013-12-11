package client;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.DrawingOperationProtocol;

/**
 * A GUI to support drawing by multiple users on a shared whiteboard. This holds 
 * both a ClientEasel and a ClientInfoPanel, and maintains the connection between
 * the client and the WhiteboardServer. The ClientGUI handles all of the message 
 * passing between the server and its two major sub-components.
 *
 * Concurrency Argument:
 * The only method for the ClientGUI to run into concurrency issues is due to the fact that
 * 		it spawns a separate thread to listen to messages from the WhiteboardServer. This
 * 		thread is made safe because ClientGUI never actually handles the messages, they are 
 * 		passed to the Easel/InfoPanel, who handle the messages through the EDT. To avoid issues
 * 		while sending messages, sending a message requires synchronizing on the GUI itself.
 * 
 * @author jlmart88
 *
 */
public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
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
		this.setLocationRelativeTo(null);
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
	 */
	private void showLoginScreen() {
		// create the text fields
		final JTextField IPAddress = new JTextField(10);
		IPAddress.setText("localhost");
		final JTextField portNum = new JTextField(10);
		portNum.setText("4500");
		JLabel IPLabel = new JLabel("Server IP Address:");
		JLabel portLabel = new JLabel("Server Port #: ");
		JButton login = new JButton("Login");
		JButton cancel = new JButton("Cancel");
		

		final JPanel loginPanel = new JPanel();
		
		GroupLayout layout = new GroupLayout(loginPanel);
		loginPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				   layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				      .addGroup(layout.createSequentialGroup()
				    		  .addComponent(IPLabel)
				    		  .addComponent(IPAddress)
				    		  )
				      .addGroup(layout.createSequentialGroup()
				    		  .addComponent(portLabel)
				    		  .addComponent(portNum)
				    		  )
				      .addGroup(layout.createSequentialGroup()
				    		  .addComponent(login)
				    		  .addComponent(cancel)
				    		  )
				);
				layout.setVerticalGroup(
				   layout.createSequentialGroup()
				      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    		  .addComponent(IPLabel)
				    		  .addComponent(IPAddress)
				    		  )
				      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    		  .addComponent(portLabel)
				    		  .addComponent(portNum)
				    		  )
				      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    		  .addComponent(login)
				    		  .addComponent(cancel)
				    		  )
				);

		final JDialog dialog = new  JDialog(this, "Login to Powerboard 3000", true);
		dialog.setContentPane(loginPanel);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		login.addActionListener(
			    new ActionListener() {
			        public void actionPerformed(ActionEvent e) {
			        	boolean loginSuccessful = true;
			        	try {
							socket = new Socket(IPAddress.getText(),
									Integer.parseInt(portNum.getText()));
							w = new PrintWriter(socket.getOutputStream(), true);
							r = new BufferedReader(new InputStreamReader(
									socket.getInputStream()));
						} catch (IOException e1) {
							loginSuccessful = false;
							e1.printStackTrace();
							JOptionPane.showMessageDialog(dialog,
								    "Login failed, please try again.", 
								    "Error",
								    JOptionPane.ERROR_MESSAGE);
						} finally {
							if (loginSuccessful) dialog.setVisible(false);
						}
			        }
			    });
		
		cancel.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	System.exit(0);
	        }
	    });
		
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(this);
		
		dialog.setVisible(true);
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
	
	/**
	 * Run an instance of the ClientGUI
	 */
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