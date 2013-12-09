package canvas;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

public class ClientGUI extends JFrame{

	private String userID;
	private String portNo;
	private String ipNo;
	private final JButton usernameButton;
	private final JTextField newUsername;
	private final JLabel username;
	private final JLabel port;
	private final JTextField newPort;
	private final JButton portButton;
	private final JLabel ip;
	private final JTextField newIp;
	private final JButton ipButton;
	
	
	public ClientGUI(){
		username = new JLabel("Please enter your username");
		usernameButton = new JButton("Set username");
		newUsername = new JTextField();
		port = new JLabel("Please enter your port number");
		newPort = new JTextField();
		portButton = new JButton("Set port number");
		ip = new JLabel("Please enter your IP address");
		newIp = new JTextField();
		ipButton = new JButton("Set IP address");
        Container initialPanel = this.getContentPane();
        GroupLayout layout = new GroupLayout(initialPanel);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(port))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(username))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(ip)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(newIp)
                        .addComponent(newPort)
                        .addComponent(newUsername, 48, 48, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                            .addComponent(ipButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(portButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(usernameButton, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(username)
                        .addComponent(usernameButton)
                        .addComponent(newUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(port)
                        .addComponent(newPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(portButton))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(ip)
                        .addComponent(newIp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(ipButton)))
        );
        initialPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        this.pack();
		usernameButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		userID = newUsername.getText();
        		newUsername.setText("");
        	}
        });
		portButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		portNo = newPort.getText();
        		newPort.setText("");
        	}
        });
		ipButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		ipNo = newIp.getText();
        		newIp.setText("");
        	}
        });

	}
	
	public class mainFrame extends JFrame
	{
		private final JLabel serverInfo;
		private final JLabel currentBoard;
		private final DefaultListModel listModel;
		private final JList boardList;
		private final JTable userList;

	    public mainFrame()
	    {
	    	serverInfo = new JLabel("Choose a whiteboard to draw on.");
	    	currentBoard = new JLabel("");
	    	listModel = new DefaultListModel();
	    	boardList = new JList(listModel);
	        userList = new JTable(new DefaultTableModel());
			final DefaultTableModel users = (DefaultTableModel) userList.getModel();
			users.addColumn("");
	        this.pack();
	        setVisible(true);
	    }
	}

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClientGUI main = new ClientGUI();
                main.setVisible(true);
                
            }
        });
    }

}