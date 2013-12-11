package client;

public class ClientInfoPanelTest {
	
	/**
	 * @author kevinwen
	 * GUI Testing:
	 * boardList testing:
	 * I made sure that, in a new server, the boardList just displays the default whiteboard.
	 * I tested that hitting the enter key in the textbox will clear the textbox.
	 * I tested that entering a new board name into the textbox creates a new board of the same name in the boardList. I tested
	 * that the canvas also redisplays this new board.
	 * I tested that if a user enters an existing board name into the textbox, instead of creating a new board, the program will
	 * redirect the user to the existing whiteboard.
	 * I tested that if a user enters a completely blank board name, or an invalid board name (i.e. containing spaces), nothing changes.
	 * I tested that new users who connect to the server will see the boardList containing all of the boards available, including
	 * the boards that were user-generated.
	 * I tested that when a user creates a new board, the boardList of other users will immediately update with the new board
	 * being available.
	 * I tested that when a user singleclicks an existing whiteboard on the boardList, the program redirects the user to that whiteboard.
	 * userList testing:
	 * I made sure that, in a new server, the userList just displays your username (defaulted to User0).
	 * I tested that hitting the enter key in the textbox will clear the textbox.
	 * I tested that entering a new username into the textbox changes the userID JLabel and the userList accordingly to the new username.
	 * I tested that entering an invalid username (i.e. containing spaces or in the form User[0-9]), nothing changes.
	 * I tested that new users who connect to the server will see the userList contain all of the users on the current default server.
	 * I tested that, when transitioning from one board to another, the userList refreshes and displays the users connected to the new board.
	 * I tested that when a user connected to the same board as you leaves the board, the userList automatically updates accordingly to not
	 * display that user.
	 * I tested that when a user connects to the same board as you, the userList automatically updates accordingly to display that user.
	 * I tested that when a user connected to the same board as you changes username, the userList automatically updates accordingly to
	 * display the new username in place of the old one.
	 * 
	 */
}
