package client;

/**
 * This class contains the testing strategy used to ensure that ClientCanvas works correctly
 * @author jlmart88
 *
 */
public class ClientCanvasTest {
	
	/**
	 * Manual Tests Performed:
	 * 
	 * These tests were performed while connected to a server with multiple users connected
	 * to various whiteboards
	 * 
	 * - Drawings do not update to the server until the mouse has been released
	 * - Drawing squares/circles locks the aspect ratio of the shape appropriately
	 * - Each of the drawing tools/features exhibit the desired behavior
	 * - Each of the drawing operations populate to other connected users quickly and unmutated
	 * - Receiving a drawing message from another user while currently drawing does not 
	 * 		cancel your current drawing
	 * - Using eraseAll clears the board for all connected users
	 * - Changing boards clears your board and appropriately updates the board with the current state 
	 * 		of the new board
	 * 
	 */
}
