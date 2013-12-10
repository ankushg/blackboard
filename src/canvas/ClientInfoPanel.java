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

public class ClientInfoPanel extends JPanel{

		private static final long serialVersionUID = 1L;
		private final JLabel serverInfo;
		private final JLabel currentBoard;
		private final DefaultListModel listModel;
		private final DefaultListModel userListModel;
		private final JList boardList;
		private final JList userList;
		private final JTextField newBoard;
		private final JButton enterBoard;
		private final ClientGUI clientGUI;
		private final JTextField changeName;
		private final JLabel userID;
		private String username;
		
	    public ClientInfoPanel(final ClientGUI clientGUI) throws IOException {
	    	this.clientGUI = clientGUI;
	    	username = "hi";
	    	userID = new JLabel("Your username is: " + username + " change username:");
	    	changeName = new JTextField(10);
	    	serverInfo = new JLabel("Choose a whiteboard to draw on or add a new whiteboard.");
	    	enterBoard = new JButton("OK");
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
	                    .addComponent(userList)
	                    .addComponent(newBoard)
	                    .addGroup(groupLayout.createSequentialGroup()
	                    		.addComponent(userID)
	                    		.addComponent(changeName))
	                    .addComponent(enterBoard)
	            );
	            groupLayout.setVerticalGroup(
	                groupLayout.createSequentialGroup()
	                    .addComponent(serverInfo)
	                    .addComponent(boardList)
	                    .addComponent(userList)
	                    .addComponent(newBoard)
	                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                    		.addComponent(userID)
	                    		.addComponent(changeName))
	                    .addComponent(enterBoard)
	            );
	        
	        setVisible(true);
	        
	        
			enterBoard.addActionListener(new ActionListener(){
	        	public void actionPerformed(ActionEvent e) {
	        		String output = null;
	        		if(boardList.isSelectionEmpty()){
	        			output = "changeBoard " + newBoard.getText();
	        			listModel.addElement(newBoard.getText());
	        			System.out.println(userList.isSelectionEmpty());
	        		}
	        		else{
	        			output = "changeBoard " + userList.getSelectedValue();
	        			boardList.clearSelection();
	        		}
	        		newBoard.setText("");
	        		clientGUI.sendMessage(output);
	        	}
	        });

			changeName.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					clientGUI.sendMessage("setUsername " + changeName.getText());
					userID.setText("Your username is: " + changeName.getText() + " change username: ");
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
	    	if(message.startsWith("userJoined")){
	    		userListModel.addElement(message.substring(11));
	    	}
	    	if(message.startsWith("userQuit")){
	    		userListModel.removeElement(message.substring(9));
	    	}
	    }
        public void addToBoardList(String output){
        	listModel.addElement(output);
        }
        


}