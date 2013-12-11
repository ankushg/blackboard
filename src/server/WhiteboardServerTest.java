package server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import server.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Things to test:
 * first join on server
 * 
 * 
 * @author ankush
 *
 */
public class WhiteboardServerTest {

    @Before
    public void setUp() throws Exception {
        TestUtil.startServer(true);
    }

    @Test(timeout = 10000)
    public void publishedTest() throws IOException, InterruptedException {
        // Avoid race where we try to connect to server too early
        Thread.sleep(100);

        try {
            Socket sock = TestUtil.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            assertEquals("username User0", TestUtil.nextNonEmptyLine(in));
            assertEquals("joinedBoard default default", TestUtil.nextNonEmptyLine(in));
            assertEquals("userJoined User0", TestUtil.nextNonEmptyLine(in));
            out.println("getUsername");
            assertEquals("username User0", TestUtil.nextNonEmptyLine(in));
            out.println("listBoards");
            assertEquals("currentBoards default", TestUtil.nextNonEmptyLine(in));
            out.println("setUsername TestUsername");
            assertEquals("usernameChanged User0 TestUsername", TestUtil.nextNonEmptyLine(in));
            assertEquals("userQuit User0", TestUtil.nextNonEmptyLine(in));
            assertEquals("userJoined TestUsername", TestUtil.nextNonEmptyLine(in));
            out.println("changeBoard testBoard"); // Debug is true.
            assertEquals("userQuit TestUsername", TestUtil.nextNonEmptyLine(in));
            assertEquals("newBoard testBoard", TestUtil.nextNonEmptyLine(in));
            assertEquals("joinedBoard default testBoard", TestUtil.nextNonEmptyLine(in));
            assertEquals("userJoined TestUsername", TestUtil.nextNonEmptyLine(in));
            out.println("exit");
            sock.close();
        } catch (SocketTimeoutException e) {
            throw new RuntimeException(e);
        }
    }
    
    @After
    public void tearDown() throws Exception {
        TestUtil.startServer(true);
    }

}
