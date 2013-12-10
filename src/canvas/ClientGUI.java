package canvas;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class ClientGUI extends JFrame{

	private PrintWriter w;
	private BufferedReader r;
	private Socket socket;
	
	
	private final ClientCanvas canvas;
	private final ClientInfoPanel infoPanel;
	
	

	public ClientGUI() throws IOException{
		canvas = new ClientCanvas(ClientCanvasPanel.DEFAULT_WIDTH,ClientCanvasPanel.DEFAULT_HEIGHT, this);
		infoPanel = new ClientInfoPanel(this);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		//set up layout
		Container mainPanel = this.getContentPane();
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(
        		layout.createSequentialGroup()
        			.addComponent(canvas)
        			.addComponent(infoPanel)
        );
        layout.setVerticalGroup(
        		layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addComponent(canvas)
        			.addComponent(infoPanel)
        );
        this.pack();
        this.setVisible(true);
        
        showLoginScreen();
        
        
        Thread clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
                    for (String line = r.readLine(); line != null; line = r.readLine()) {
                        for (String drawingMessage: MessageProtocol.DRAWING_MESSAGE_LIST){
                        	if (line.startsWith(drawingMessage)){
                        		canvas.receiveDrawingMessage(line);
                        	}
                        }
                        if (line.startsWith("joinedBoard")){
                    		canvas.receiveDrawingMessage(line);
                    	}
                    }
                } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {

                }
				
			}
        	
        });
        clientThread.start();
	}
	

	/**
	 * Prompts the user with a dialog for server info, then initializes a socket connection
	 * with the server
	 * @throws IOException 
	 */
	private void showLoginScreen() throws IOException {
		//create the text fields
        JTextField IPAddress= new JTextField(5);
        IPAddress.setText("localhost");
        JTextField portNum = new JTextField(5);
        portNum.setText("4500");

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Server IP Address:"));
        myPanel.add(IPAddress);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Server Port #:"));
        myPanel.add(portNum);

        int result = JOptionPane.showConfirmDialog(null, myPanel, 
                 "Please enter the server information:", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
        	try {
				socket = new Socket(IPAddress.getText(), Integer.parseInt(portNum.getText()));
				w = new PrintWriter(socket.getOutputStream(),true);
				r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				getBoardList();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        if (result == JOptionPane.CANCEL_OPTION) {
        	this.dispose();
        }
    }
        
        
    public synchronized void getBoardList() throws IOException{
        w.println("listBoards");
//       	while((r.readLine()) != null){
        	infoPanel.getMessage(r.readLine());
//        }	
	}

	public synchronized void sendMessage(String output){
		w.println(output);
	}
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClientGUI main;
				try {
					main = new ClientGUI();
	                main.setVisible(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        });
    }



}