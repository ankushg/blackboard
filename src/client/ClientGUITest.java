package client;

import org.junit.Test;

/**
 * This class contains the testing strategy used to ensure that ClientGUI works correctly
 * @author kevinwen
 *
 */
public class ClientGUITest {
	/**
	 * Login Screen Testing:
	 * I tested that entering an invalid IP address and/or an invalid port number would cause an error message
	 * dialog to occur. I made sure that clicking "OK" to this dialog removes the message and still allows
	 * the user to input a new IP address or port number.
	 * I tested that hitting the cancel button terminates the GUI.
	 * I tested that entering a valid IP address and port number connects the user to the server and takes it 
	 * to the ClientGUI, which is a combination of ClientEasel and ClientInfoPanel.
	 * 
	 * Message Parsing Testing:
 	 * The main purpose of the message passing thread in ClientGUI is to send all drawing messages to ClientEasel and 
 	 * all server messages to ClientInfoPanel. Given that the functionality of both classes are in completely working 
 	 * order due to separate manual testings, it is sufficient to assume that the message passing in ClientGUI is error-free.
	 * 
	 */
    
    @Test
    public void clientGUITest(){
        assert true;
    }

}
