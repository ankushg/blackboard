package canvas;

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
		
	    public ClientInfoPanel(final ClientGUI clientGUI) throws IOException {
	    	this.clientGUI = clientGUI;
	    	username = "";
	    	userID = new JLabel("Your username is: " + username + " change username:");
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
	        		String input = newBoard.getText();
	        		if(input != ""){
	        			output = "changeBoard " + input;
	        		}

	        		userListModel.clear();
	        		newBoard.setText("");
	        		clientGUI.sendMessage(output);
	        	}
	        });

			boardList.addMouseListener(new MouseAdapter() {
			    public void mouseClicked(MouseEvent evt) {
			    	String output = "";
			        JList list = (JList)evt.getSource();
			        if (evt.getClickCount() == 2) {
			            int index = list.locationToIndex(evt.getPoint());
				        output = (String) boardListModel.getElementAt(index);
			        } else if (evt.getClickCount() == 3) {   // Triple-click
			            int index = list.locationToIndex(evt.getPoint());
				        output = (String) boardListModel.getElementAt(index);
			        }
	        		userListModel.clear();
	        		clientGUI.sendMessage("changeBoard " + output);
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
	    
	    protected synchronized void receiveServerMessage(String message){
	    	final String serverMessage = message;
	    	SwingUtilities.invokeLater(new Runnable() {
	    		@Override
				public void run() {
	    			parseUsers(serverMessage);
	    		}
	    	});
	    }

	    protected synchronized void parseUsers(String message){
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
	    
    		if (message.startsWith(ClientGUI.BOARD_CHANGED)) {
    			// TODO: clear the actual drawing
    		}
    		if (message.startsWith(ClientGUI.NEW_BOARD)) {
    			String[] boards = message.split(" ");
    			boardListModel.addElement(boards[1]);
    		}
    		if (message.startsWith(ClientGUI.CURRENT_BOARDS)) {
    			String[] boards = message.split(" ");
    			for (int i = 1; i < boards.length; i++) {
    				boardListModel.addElement(boards[i]);
    			}
    		}
	    }
	    
	    public boolean isNewBoard(String board){
	    	for(int i=0; i<boardListModel.getSize(); i++){
	    		if(board.equals(boardListModel.getElementAt(i))){
	    			return false;
	    		}
	    	}
	    	return true;
	    }
}