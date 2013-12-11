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

            assertEquals(true, TestUtil.nextNonEmptyLine(in).startsWith("joinedBoard default default"));
            // This particular test ignores extraneous newlines; other tests may
            // not.
//            out.println("look");
//
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//
//            out.println("dig 3 1");
//
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - 1 - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- - - - - - -", TestUtil.nextNonEmptyLine(in));
//
//            out.println("dig 4 1");
//
//            assertEquals("BOOM!", TestUtil.nextNonEmptyLine(in));
//
//            out.println("look"); // Debug is true.
//
//            assertEquals("             ", TestUtil.nextNonEmptyLine(in));
//            assertEquals("             ", TestUtil.nextNonEmptyLine(in));
//            assertEquals("             ", TestUtil.nextNonEmptyLine(in));
//            assertEquals("             ", TestUtil.nextNonEmptyLine(in));
//            assertEquals("             ", TestUtil.nextNonEmptyLine(in));
//            assertEquals("1 1          ", TestUtil.nextNonEmptyLine(in));
//            assertEquals("- 1          ", TestUtil.nextNonEmptyLine(in));

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
