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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class ClientGUI extends JFrame{

	private String userID = "kevin";
	private String portNo = "4500";
	private int portNumber = 4500;
	private String ipNo = "localhost";
	private final JButton usernameButton;
	private final JTextField newUsername;
	private final JLabel username;
	private final JLabel port;
	private final JTextField newPort;
	private final JButton portButton;
	private final JLabel ip;
	private final JTextField newIp;
	private final JButton ipButton;
	private final JButton connectServer;
	private PrintWriter w;
	private BufferedReader r;
	private Socket socket;
	
	
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
		connectServer = new JButton("Connect to server");
		
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
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(connectServer))
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
                        .addComponent(ipButton))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    	.addComponent(connectServer)))
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
        		portNumber = Integer.parseInt(portNo);
        		newPort.setText("");
        	}
        });
		ipButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		ipNo = newIp.getText();
        		newIp.setText("");
        	}
        });
		connectServer.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		try {
					socket = new Socket(ipNo, portNumber);
					w = new PrintWriter(socket.getOutputStream(),true);
					r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        		w.print("setUsername " + userID);
        		MainFrame main;
				try {
					main = new MainFrame();
	        		main.setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        });

	}
	
	public class MainFrame extends JFrame
	{
		private final JLabel serverInfo;
		private final JLabel currentBoard;
		private final DefaultListModel listModel, userListModel;
		private final JList boardList;
		private final JList userList;
		private final JTextField newBoard;
		private final JButton enterBoard;
		
	    public MainFrame() throws IOException
	    {
	    	serverInfo = new JLabel("Choose a whiteboard to draw on or add a new whiteboard.");
	    	enterBoard = new JButton("OK");
	    	currentBoard = new JLabel("");
	    	newBoard = new JTextField();
	    	listModel = new DefaultListModel();
	    	userListModel = new DefaultListModel();
	    	boardList = new JList(listModel);
	        userList = new JList(userListModel);
	        Container initialPanel = this.getContentPane();
	        GroupLayout groupLayout = new GroupLayout(initialPanel);
	        groupLayout.setHorizontalGroup(
	                groupLayout.createParallelGroup()
	                    .addGroup(groupLayout.createSequentialGroup()
	                        .addComponent(serverInfo))
	                    .addGroup(groupLayout.createSequentialGroup()
	                        .addComponent(boardList)
	                        .addComponent(userList)
	                    	.addComponent(newBoard)
	                        .addComponent(enterBoard))
	            );
	            groupLayout.setVerticalGroup(
	                groupLayout.createSequentialGroup()
	                	    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                        .addComponent(serverInfo))
	                        .addComponent(boardList)
	                        .addComponent(userList)
	                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                    .addComponent(newBoard)
	                        .addComponent(enterBoard))
	            );
	        this.pack();
	        setVisible(true);
	    	SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
	    		@Override
	    		protected Void doInBackground(){
	    	    	w.print("listBoards");
	    	    	try {
						for (String line = r.readLine(); line != null; line = r.readLine()) {
							listModel.addElement(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
	    		}
	    	};
    		worker.execute();
			enterBoard.addActionListener(new ActionListener(){
	        	public void actionPerformed(ActionEvent e) {
	        		if(boardList.isSelectionEmpty()){
	        			String boardName = newBoard.getText();
	        			w.print("changeBoard " + boardName);
	        			listModel.addElement(boardName);
	        		}
	        		else{
	        			String board = (String)boardList.getSelectedValue();
	        			w.print("changeBoard " + board);
	        		}
	        	}
	        });

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