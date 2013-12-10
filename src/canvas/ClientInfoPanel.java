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
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

public class ClientInfoPanel extends JPanel{

		private static final long serialVersionUID = 1L;
		private final JLabel serverInfo;
		private final JLabel currentBoard;
		private final DefaultListModel listModel;
		private final JList boardList;
		private final JTable userList;
		private final JTextField newBoard;
		private final JButton enterBoard;

	    public ClientInfoPanel() {
	    	serverInfo = new JLabel("Choose a whiteboard to draw on or add a new whiteboard.");
	    	enterBoard = new JButton("OK");
	    	currentBoard = new JLabel("");
	    	newBoard = new JTextField();
	    	listModel = new DefaultListModel();
	    	boardList = new JList(listModel);
	        userList = new JTable(new DefaultTableModel());
	        
			final DefaultTableModel users = (DefaultTableModel) userList.getModel();
			users.addColumn("");

	        GroupLayout groupLayout = new GroupLayout(this);
	        this.setLayout(groupLayout);
	        groupLayout.setHorizontalGroup(
	                groupLayout.createParallelGroup()
	                    .addGroup(groupLayout.createSequentialGroup()
	                        .addComponent(serverInfo))
	                    .addGroup(groupLayout.createSequentialGroup()
	                        .addComponent(boardList)
	                        .addComponent(userList))
	                    .addGroup(groupLayout.createSequentialGroup()
	                        .addComponent(enterBoard))
	            );
	            groupLayout.setVerticalGroup(
	                groupLayout.createSequentialGroup()
	                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                        .addComponent(serverInfo))
	                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                        .addComponent(boardList)
	                        .addComponent(userList))
	                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                        .addComponent(enterBoard))
	            );
	        
	        setVisible(true);
	        
			enterBoard.addActionListener(new ActionListener(){
	        	public void actionPerformed(ActionEvent e) {

	        	}
	        });

	    }

}