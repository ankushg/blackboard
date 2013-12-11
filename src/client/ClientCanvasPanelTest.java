package client;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.Test;


public class ClientCanvasPanelTest {
	

	/**
	 * Ensures that students got the sense of X,Y directions right.
	 */
	public class PublishedTest {
	    @Before
	    public void setUp() {
	        TestUtil.startServer();
	    }

	    @Test(timeout = 10000)
	    public void publishedTest() throws IOException, InterruptedException {
	        // Avoid race where we try to connect to server too early
	        Thread.sleep(100);

	        try {
	            Socket sock = TestUtil.connect();
	            BufferedReader in = new BufferedReader(new InputStreamReader(
	                    sock.getInputStream()));
	            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);

	            out.println("getUsername");
	            assertEquals("username User0", TestUtil.nextNonEmptyLine(in));
	            out.println("listBoards");
	            assertEquals("currentBoards default", TestUtil.nextNonEmptyLine(in));
	            out.println("setUsername TestUsername");
	            assertEquals("usernameChanged User0 TestUsername", TestUtil.nextNonEmptyLine(in));
	            out.println("changeBoard testBoard"); // Debug is true.
	            assertEquals("newBoard testBoard", TestUtil.nextNonEmptyLine(in));
	            out.println("exit");
	            sock.close();
	        } catch (SocketTimeoutException e) {
	            throw new RuntimeException(e);
	        }
	    }
	}
}
