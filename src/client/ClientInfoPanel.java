package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * A JPanel that manages all of the information about boards/users that are
 * retrieved from the server
 * 
 * @author kevinwen
 *
 * TODO: Fix changing whiteboards (the display of all current whiteboards, joining new whiteboards,
 * 					joining existing whiteboards)
 * TODO: Fix the way the users are displayed
 */
public class ClientInfoPanel extends JPanel{

		private static final long serialVersionUID = 1L;
		private final JLabel serverInfo;
		private final JLabel currentBoard;
		private final DefaultListModel<String> boardListModel;
		private final DefaultListModel<String> userListModel;
		private final JList<String> boardList;
		private final JList<String> userList;
		private final JTextField newBoard;
		private final ClientGUI clientGUI;
		private final JTextField changeName;
		private final JLabel userID;
		private final JLabel userInfo;
		private String username;
		
		/**
		 * Make an InfoPanel.
		 * @param clientGUI
		 * @throws IOException
		 */
	    public ClientInfoPanel(final ClientGUI clientGUI) throws IOException {
	    	this.clientGUI = clientGUI;
	    	username = "";
	    	userID = new JLabel("Your username is: " + username);
	    	changeName = new JTextField(10);
	    	serverInfo = new JLabel("Choose a whiteboard to draw on or add a new whiteboard.");
	    	userInfo = new JLabel("Connected users in your current board");
	    	currentBoard = new JLabel("");
	    	newBoard = new JTextField(10);
	    	boardListModel = new DefaultListModel<>();
	    	userListModel = new DefaultListModel<>();
	    	boardList = new JList<>(boardListModel);
	        userList = new JList<>(userListModel);
	        
	        GroupLayout groupLayout = new GroupLayout(this);
	        this.setLayout(groupLayout);
	        groupLayout.setHorizontalGroup(
	                groupLayout.createParallelGroup()
	                    .addComponent(serverInfo)
	                    .addComponent(boardList)
	                    .addComponent(newBoard)
	                    .addComponent(userInfo)
	                    .addComponent(userList)
                		.addComponent(userID)
                		.addComponent(changeName));
	            groupLayout.setVerticalGroup(
	                groupLayout.createSequentialGroup()
	                    .addComponent(serverInfo)
	                    .addComponent(boardList)
	                    .addComponent(newBoard)
	                    .addComponent(userInfo)
	                    .addComponent(userList)
                		.addComponent(userID)
                		.addComponent(changeName));
	        
	        setVisible(true);
	        
	        // This adds a new whiteboard to the server and serverlist
			newBoard.addActionListener(new ActionListener(){
	        	public void actionPerformed(ActionEvent e) {
	        		String output = null;
	        		String input = newBoard.getText();
	        		if(input != ""){
	        			output = "changeBoard " + input;
	        		}
	        		userListModel.clear();
	        		newBoard.setText("");
	        		clientGUI.sendMessage(output);
	        	}
	        });

			//This detects a single mouseclick on the board list and directs the user to that board
			boardList.addMouseListener(new MouseAdapter() {
			    public void mouseClicked(MouseEvent evt) {
			    	String output = "";
			        JList list = (JList)evt.getSource();
                    int index = list.locationToIndex(evt.getPoint());
                    output = (String) boardListModel.getElementAt(index);
                    userListModel.clear();
	        		clientGUI.sendMessage("changeBoard " + output);
			    }
			});

			// This changes the name of the user
			changeName.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					clientGUI.sendMessage("setUsername " + changeName.getText());
					System.out.println("setUsername " + changeName.getText());
					changeName.setText("");
				}
			});
			
	    }
	    
	    /**
	     * This method receives the non-drawing messages that the server sends to the
	     * client and sends it to the parseUsers method.
	     * @param message
	     */
	    protected synchronized void receiveServerMessage(String message){
	    	final String serverMessage = message;
	    	SwingUtilities.invokeLater(new Runnable() {
	    		@Override
				public void run() {
	    			parseUsers(serverMessage);
	    		}
	    	});
	    }
	    /**
	     * This method takes a server message and parses it into different
	     * categories. The UI information is updated based on the message.
	     * @param message
	     */
	    protected synchronized void parseUsers(String message){
	    	if(message.startsWith(ClientGUI.USER_JOINED)){
	    		userListModel.addElement(message.substring(11));
	    	}
	    	if(message.startsWith(ClientGUI.USER_QUIT)){
	    		userListModel.removeElement(message.substring(9));
	    	}
	    	if(message.startsWith(ClientGUI.USERNAME)){
	    		userID.setText("Your username is: " + message.substring(9));
	    	}
	    	if(message.startsWith(ClientGUI.USERNAME_CHANGED)){
	    		String names[] = message.split(" ");
				userID.setText("Your username is: " + names[2]);
	    	}
	    	if (message.startsWith(ClientGUI.NEW_BOARD)) {
    			String[] boards = message.split(" ");
    			boardListModel.addElement(boards[1]);
    		}
	    	if (message.startsWith(ClientGUI.CURRENT_BOARDS)) {
    			String[] boards = message.split(" ");
    			for (int i = 1; i < boards.length; i++) {
    			    if(!boardListModel.contains(boards[i])){
    			        boardListModel.addElement(boards[i]);
    			    }
    			}
    		}
	    }
}