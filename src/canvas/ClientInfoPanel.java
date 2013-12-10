package canvas;

import server.WhiteboardServer;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

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
		private final DefaultListModel listModel;
		private final DefaultListModel userListModel;
		private final JList boardList;
		private final JList userList;
		private final JTextField newBoard;
		private final ClientGUI clientGUI;
		private final JTextField changeName;
		private final JLabel userID;
		private final JLabel userInfo;
		private String username;
		
	    public ClientInfoPanel(final ClientGUI clientGUI) throws IOException {
	    	this.clientGUI = clientGUI;
	    	username = "";
	    	userID = new JLabel("Your username is: " + username + " change username:");
	    	changeName = new JTextField(10);
	    	serverInfo = new JLabel("Choose a whiteboard to draw on or add a new whiteboard.");
	    	userInfo = new JLabel("Connected users in your current board");
	    	currentBoard = new JLabel("");
	    	newBoard = new JTextField(10);
	    	listModel = new DefaultListModel();
	    	userListModel = new DefaultListModel();
	    	boardList = new JList(listModel);
	    	
	        userList = new JList(userListModel);
	        GroupLayout groupLayout = new GroupLayout(this);
	        this.setLayout(groupLayout);
	        groupLayout.setHorizontalGroup(
	                groupLayout.createParallelGroup()
	                    .addComponent(serverInfo)
	                    .addComponent(boardList)
	                    .addComponent(userInfo)
	                    .addComponent(userList)
	                    .addComponent(newBoard)
	                    .addGroup(groupLayout.createSequentialGroup()
	                    		.addComponent(userID)
	                    		.addComponent(changeName))
	            );
	            groupLayout.setVerticalGroup(
	                groupLayout.createSequentialGroup()
	                    .addComponent(serverInfo)
	                    .addComponent(boardList)
	                    .addComponent(userInfo)
	                    .addComponent(userList)
	                    .addComponent(newBoard)
	                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                    		.addComponent(userID)
	                    		.addComponent(changeName))
	            );
	        
	        setVisible(true);
	        
			newBoard.addActionListener(new ActionListener(){
	        	public void actionPerformed(ActionEvent e) {
	        		String output = null;
	        		if(boardList.isSelectionEmpty() && newBoard.getText() != ""){
	        			output = "changeBoard " + newBoard.getText();
	        			listModel.addElement(newBoard.getText());
	        		}
	        		else{
	        			output = "changeBoard " + boardList.getSelectedValue();
	        			boardList.clearSelection();
	        		}
	        		userListModel.clear();
	        		newBoard.setText("");
	        		clientGUI.sendMessage(output);
	        	}
	        });

			changeName.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					clientGUI.sendMessage("setUsername " + changeName.getText());
					System.out.println("setUsername " + changeName.getText());
//					userID.setText("Your username is: " + changeName.getText() + " change username: ");
					changeName.setText("");
				}
			});
			
			
//			userListModel.addElement()
	    }
	    
	    public synchronized void receiveServerMessage(String message){
	    	final String serverMessage = message;
	    	SwingUtilities.invokeLater(new Runnable() {
	    		@Override
				public void run() {
	    			parseUsers(serverMessage);
	    		}
	    	});
	    }

	    public synchronized void parseUsers(String message){
	    	if(message.startsWith(ClientGUI.USER_JOINED)){
	    		userListModel.addElement(message.substring(11));
	    		System.out.println(message);
	    	}
	    	if(message.startsWith(ClientGUI.USER_QUIT)){
	    		userListModel.removeElement(message.substring(9));
	    		System.out.println(message);
	    	}
	    	if(message.startsWith(ClientGUI.USERNAME)){
	    		userID.setText("Your username is: " + message.substring(9) + " change username: ");
	    		System.out.println(message);
	    	}
	    	if(message.startsWith(ClientGUI.USERNAME_CHANGED)){
	    		String names[] = message.split(" ");
				userID.setText("Your username is: " + names[2] + " change username: ");
	    		System.out.println(message);
	    	}
	    	if(message.startsWith(ClientGUI.BOARD_CHANGED)){
	    		String[] boards = message.split(" ");
	    		for(int i=1; i<boards.length; i++)
	    			listModel.addElement(boards[i]);
	    	}
	    }

}