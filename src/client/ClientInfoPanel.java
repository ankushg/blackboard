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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A JPanel that manages all of the information about boards/users that are
 * retrieved from the server
 * 
 * Concurrency Argument:
 * All changes to the GUI components occur within the EDT, and obtaining information about a ClientInfoPanel
 * field requires synchronizing on the panel.
 * Messaging receiving from the server also occurs on the EDT, preventing any concurrency issues that may arise from
 * receiving multiple messages quickly.

 * @author kevinwen
 *
 */
public class ClientInfoPanel extends JPanel{

		private static final long serialVersionUID = 1L;
		private final JLabel serverInfo;
		private final DefaultListModel<String> boardListModel;
		private final DefaultListModel<String> userListModel;
		private final JList<String> boardList;
		private final JList<String> userList;
		private final JTextField newBoard;
		private final ClientGUI clientGUI;
		private final JTextField changeName;
		private final JLabel userID;
		private final JLabel changeNameLabel;
		private final JLabel newBoardLabel;
		private final JLabel userInfo;
		private final ListSelectionModel listSelectionModel;
		
		/**
		 * Make an InfoPanel.
		 * @param clientGUI
		 * @throws IOException
		 */
	    public ClientInfoPanel(final ClientGUI clientGUI) throws IOException {
	    	this.clientGUI = clientGUI;
	    	userID = new JLabel("Your username is: ");
	    	changeNameLabel = new JLabel("Change your username: ");
	    	changeName = new JTextField(10);
	    	serverInfo = new JLabel("Current Boards");
	    	userInfo = new JLabel("Current Users in Your Board");
	    	newBoard = new JTextField(10);
	    	newBoardLabel = new JLabel("Create a new board: ");
	    	boardListModel = new DefaultListModel<>();
	    	userListModel = new DefaultListModel<>();
	    	boardList = new JList<>(boardListModel);
	        userList = new JList<>(userListModel);
	        boardList.setLayoutOrientation(JList.VERTICAL);
	        userList.setLayoutOrientation(JList.VERTICAL);
	        listSelectionModel = boardList.getSelectionModel();
	        listSelectionModel.addListSelectionListener(
	                                new SharedListSelectionHandler());

	        JScrollPane boardListScroller = new JScrollPane(boardList);
	        boardListScroller.setSize(this.getWidth(), 200);
	        JScrollPane userListScroller = new JScrollPane(userList);
	        userListScroller.setSize(this.getWidth(), 200);
	        
	        GroupLayout layout = new GroupLayout(this);
	        this.setLayout(layout);
	        layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
	        layout.setHorizontalGroup(
	                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
	                	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		                	.addComponent(userID)
		                	.addGroup(layout.createSequentialGroup()
		                			.addComponent(changeNameLabel)
		                			.addComponent(changeName)
		                			)
		                	.addGroup(layout.createSequentialGroup()
		                			.addComponent(newBoardLabel)
		                			.addComponent(newBoard)
		                			)
		                )
	                    .addComponent(serverInfo)
	                    .addComponent(boardListScroller)
	                    .addComponent(userInfo)
	                    .addComponent(userListScroller)
            );
	        layout.setVerticalGroup(
	                layout.createSequentialGroup()
	                    .addComponent(userID)
	                	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                    		.addComponent(changeNameLabel)
	                    		.addComponent(changeName)
	                    		)
	                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                    		.addComponent(newBoardLabel)
	                    		.addComponent(newBoard)
	                    		)
	                    .addComponent(serverInfo)
	                    .addComponent(boardListScroller)
	                    .addComponent(userInfo)
	                    .addComponent(userListScroller)
            );
	        
	        setVisible(true);

	        // This adds a new whiteboard to the server and boardList
			newBoard.addActionListener(new ActionListener(){
	        	public void actionPerformed(ActionEvent e) {
	            	SwingUtilities.invokeLater(new Runnable() {
	            		public void run() {
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
	        	}
	        });

			// This changes the name of the user
			changeName.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
	            	SwingUtilities.invokeLater(new Runnable() {
	            		public void run() {
					clientGUI.sendMessage("setUsername " + changeName.getText());
					System.out.println("setUsername " + changeName.getText());
					changeName.setText("");
				}
            	});
			}
			});	
	    }
	    
	    // This class listens to any selection changes in boardList
	    class SharedListSelectionHandler implements ListSelectionListener {
	    	// This takes the String of the newly selected board and sends a message to the server to
	    	// change the canvas to the new board.
	    	public void valueChanged(ListSelectionEvent e) { 
	    		
	    		
	    		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	    		int index = 0;
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                    index = i;
	                }
	            }
	            final int outIndex = index;
	    		if (!(lsm.isSelectionEmpty())) {
	    			SwingUtilities.invokeLater(new Runnable(){
	    				public void run(){
	    					String output = "";
	    					output = "changeBoard " + (String) boardListModel.getElementAt(outIndex);
	    	    			clientGUI.sendMessage(output);
	    				}
	    			});
	    		}

	    	}
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
	    			parseMessage(serverMessage);
	    		}
	    	});
	    }
	    /**
	     * This method takes a server message and parses it into different
	     * categories. The UI information is updated based on the message.
	     * 
	     * @param message
	     */

	    protected synchronized void parseMessage(String serverMessage){
	    	final String message = serverMessage;
        	SwingUtilities.invokeLater(new Runnable() {
        		public void run() {
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
        	    	if(message.startsWith(ClientGUI.NEW_BOARD)) {
            			String[] boards = message.split(" ");
            			boardListModel.addElement(boards[1]);
            		}
        	    	if(message.startsWith(ClientGUI.BOARD_CHANGED)){
            			String[] boards = message.split(" ");
                		userListModel.clear();
            			boardList.setSelectedValue(boards[2], true);
        	    	}
        	    	if(message.startsWith(ClientGUI.CURRENT_BOARDS)) {
            			String[] boards = message.split(" ");
            			for (int i = 1; i < boards.length; i++) {
            			    if(!boardListModel.contains(boards[i])){
            			        boardListModel.addElement(boards[i]);
            			    }
            			}
            		}
        		}
        	});

	    }
}