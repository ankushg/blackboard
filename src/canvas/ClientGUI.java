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

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

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
        initialPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                		.addComponent(username)
                		.addComponent(newUsername)
                		.addComponent(usernameButton))
                .addGroup(layout.createSequentialGroup()
                		.addComponent(port)
                		.addComponent(newPort)
                		.addComponent(portButton))
                .addGroup(layout.createSequentialGroup()
                		.addComponent(ip)
                		.addComponent(newIp)
                		.addComponent(ipButton))
                		);
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(username)
                    .addComponent(port)
                    .addComponent(ip))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(newUsername)
                    .addComponent(newPort)
                    .addComponent(newIp))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                	.addComponent(usernameButton)
                	.addComponent(portButton)
                	.addComponent(ipButton))
        			);
        this.pack();
		usernameButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		userID = newUsername.getText();
        	}
        });
		portButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		portNo = newPort.getText();
        	}
        });
		ipButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		ipNo = newIp.getText();
        	}
        });

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