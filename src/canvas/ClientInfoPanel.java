package canvas;

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

	    public ClientInfoPanel(final ClientGUI clientGUI) {
	    	this.clientGUI = clientGUI;
	    	serverInfo = new JLabel("Choose a whiteboard to draw on or add a new whiteboard.");
	    	enterBoard = new JButton("OK");
	    	currentBoard = new JLabel("");
	    	newBoard = new JTextField();
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
	                    .addComponent(enterBoard)
	            );
	            groupLayout.setVerticalGroup(
	                groupLayout.createSequentialGroup()
	                    .addComponent(serverInfo)
	                    .addComponent(boardList)
	                    .addComponent(userList)
	                    .addComponent(newBoard)
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

	    }
	    
        public void getMessage(String output){
        	listModel.addElement(output);
        }


}