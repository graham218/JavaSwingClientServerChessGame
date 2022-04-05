import javax.swing.*;

import java.awt.*;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ChessClient extends JFrame 
{
	private static final long serialVersionUID = 1L;// i need it for some reason
	
	//String
	public static String serverIP = "";						//String will hold IP
	
	//booleans for loop operations and method status checks
	public static boolean ipEntered = false;				// when let the client know when the user has entered the IP
	public static boolean connectionFailed = false; 		// used to control loop for connecting
	public static boolean playerTurn = false;				// lets the client know whether
	public static boolean gameSelected = false; 			// used for loop selecting game
	public static boolean pieceSelected = false;			// used for loops and methods
	public static boolean gameSuccessfullyJoined = false;  	// used for loops and methods
	public static boolean kingTaken = false;				// used for loops and methods
	public static boolean gameStarted = false;				// used for loops and methods
	public static boolean gameExited = false;
	
	//ints for storing game data
	public static int gameSessionSelected = -1; 			// will let the client know which session to interact with
	public static int playerColor = 0; 						// will let client know whether it is the white or black pieces
	public static int selectionX = -1; 						// used to pass where to move pieces to the swap method
	public static int selectionY = -1;						// used to pass where to move pieces to the swap method
	public static int moves = 0;							// keeps track of available moves
	
	// create UI JFrame objects
	private JPanel pan = new JPanel(); 								//panel that holds it all
	public static JButton[][] buttonArray = new JButton[8][8]; 		//array that stores game buttons
	private JLabel promptLab = new JLabel("Welcome to JavaChess!"); //label that prompts player
	private static JLabel playerColorLab = new JLabel(""); 			//label that tells player what color they are
	public static JButton quitBut = new JButton("Quit");  			//quit button
	
	//Image icons for all different pieces - self explanatory
	ImageIcon wpawn = new ImageIcon(getClass().getResource("wpawn.png"));
	ImageIcon bpawn = new ImageIcon(getClass().getResource("bpawn.png"));
	ImageIcon wbishop = new ImageIcon(getClass().getResource("wbishop.png"));
	ImageIcon bbishop = new ImageIcon(getClass().getResource("bbishop.png"));
	ImageIcon wrook = new ImageIcon(getClass().getResource("wrook.png"));
	ImageIcon brook = new ImageIcon(getClass().getResource("brook.png"));
	ImageIcon wqueen = new ImageIcon(getClass().getResource("wqueen.png"));
	ImageIcon bqueen = new ImageIcon(getClass().getResource("bqueen.png"));
	ImageIcon wking = new ImageIcon(getClass().getResource("wking.png"));
	ImageIcon bking = new ImageIcon(getClass().getResource("bking.png"));
	ImageIcon wknight = new ImageIcon(getClass().getResource("wknight.png"));
	ImageIcon bknight = new ImageIcon(getClass().getResource("bknight.png"));
	ImageIcon blank = new ImageIcon(getClass().getResource("blank.png"));

	//create client and other needed frames
	private static ChessClient client;
	private static AddressFrame addressFrame;
	private static SessionFrame sessionFrame;

	// create registries and sessions
	private static Registry[] reg = new Registry[3];
	private static GameSessionInterface[] sesh = new GameSessionInterface[3];

	
	public ChessClient() 
	{
		this.setSize(500, 650);
	
		// use awt.Toolkit to get screensize and set xPos and yPos to the center of the screen
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (this.getWidth() / 2);
		int yPos = (dim.height / 2) - (this.getHeight() / 2);
		
		// set location to center defined by xPos and yPos
		this.setLocation(xPos, yPos);
		
		//use removeMinMaxClose method to keep user from closing window with X but still be able to drag window
		this.setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		promptLab.setFont(new Font("Dialog", Font.BOLD, 17));
		playerColorLab.setFont(new Font("Dialog", Font.BOLD, 15));
		
		// make pan have a GridBagLayout
		pan = new JPanel();
		pan.setLayout(new GridBagLayout());

		// create variable c for constraints
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridwidth = 10;
		c.gridx = 0;
		c.gridy = 0;

		pan.add(promptLab, c);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 2;
		c.weighty = 2;
		c.gridwidth = 1;

		// initialize each button in the array and match the color accordingly
		for (int i = 0; i <= 7; i++) 
		{
			for (int j = 0; j <= 7; j++) 
			{
				buttonArray[i][j] = new JButton();
				buttonArray[i][j].addActionListener(new ButtonListener());
				if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) 
				{
					buttonArray[i][j].setBackground(Color.gray);
				} 
				else 
				{
					buttonArray[i][j].setBackground(Color.white);
				}
			}
		}

		for (int i = 1; i <= 8; i++) 
		{
			for (int j = 1; j <= 8; j++) 
			{
				c.gridx = i;
				c.gridy = j;
				pan.add(buttonArray[i - 1][j - 1], c);
			}
		}

		c.gridx = 1;
		c.gridy = 10;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.CENTER;
		quitBut.addActionListener(new ButtonListener());
		pan.add(quitBut, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridwidth = 3;
		c.gridx = 4;
		c.gridy = 10;

		pan.add(playerColorLab, c);
		
		this.add(pan);
	}
	
	public static void main(String[] args) throws InterruptedException, RemoteException 
	{
		getIPAndConnect();

		while (true) 
		{
			promptSessions();
			game();
		}
	}	

	private static void getIPAndConnect() throws InterruptedException 
	{
		// initialize addressFrame
		addressFrame = new AddressFrame();
		/*
		 * this do-while loop asks to reenter ip info if connection to the
		 * server fails
		 */
		do 
		{
			// show AddressFrame and get focus on text field
			if (connectionFailed == true) 
			{
				addressFrame.lab.setText("(Connection Failed) Enter IP Address of Server");
				addressFrame.setSize(400, 93);
			}
			addressFrame.setVisible(true);
			addressFrame.tf.requestFocus();

			/*
			 * The following loop makes the client wait until AddressFrame
			 * passes the IP info to client
			 */
			while (!ipEntered) 
			{
				Thread.sleep(50);
			}
			addressFrame.setVisible(false); // hide AddressFrame

			/*
			 * Use try-catch to try to connect to RMI objects, if it fails, then
			 * have the user reenter info
			 */
			try 
			{
				// attempt to connect and bind reg1 to sesh1
				reg[0] = LocateRegistry.getRegistry(serverIP, 4200);
				sesh[0] = (GameSessionInterface) reg[0].lookup("Session1");

				// attempt to connect and bind reg2 to sesh2
				reg[1] = LocateRegistry.getRegistry(serverIP, 4201);
				sesh[1] = (GameSessionInterface) reg[1].lookup("Session2");

				// attempt to connect and bind reg3 to sesh3
				reg[2] = LocateRegistry.getRegistry(serverIP, 4202);
				sesh[2] = (GameSessionInterface) reg[2].lookup("Session3");

				// set connectionSucessful to true since at this point no
				// exceptions were thrown
				connectionFailed = false;
			} 
			catch (RemoteException e) 
			{
				connectionFailed = true; // set connectionFailed to true if any
											// exceptions are thrown
				ipEntered = false;
			} 
			catch (NotBoundException e) 
			{
				connectionFailed = true; // set connectionFailed to true if any
											// exceptions are thrown
				ipEntered = false;
			}

		} while (connectionFailed);
	}

	private static void promptSessions() throws InterruptedException, RemoteException 
	{
		// each do is another error that could make it fail and so it loops back
		// to beginning
		sessionFrame = new SessionFrame();
		sessionFrame.setVisible(true);
		gameSuccessfullyJoined = false;
		gameSessionSelected = -1;
		do {
			do {
				/*
				 * The following loop makes the client wait until sessionPanel
				 * passes the game session number
				 */
				while (gameSessionSelected == -1) {

					// sleep for 50 ms and then check to see if a game was
					// selected, if so end loop then set session frame as
					// invisible
					Thread.sleep(50);
					sessionFrame.refresh();
					if (gameSessionSelected == 0 || gameSessionSelected == 1 || gameSessionSelected == 2) {
						gameSelected = true;
					}
				}
			} while (!gameSelected);

			gameSuccessfullyJoined = sesh[gameSessionSelected].playerJoin();

			if (gameSuccessfullyJoined == false) {
				gameSessionSelected = -1;
				sessionFrame.lab.setText("Failed to connect. Please select another session");
			}
		} while (gameSuccessfullyJoined == false);

		sessionFrame.setVisible(false);
	}

	private static void game() throws RemoteException, InterruptedException 
	{
		client = new ChessClient();
		disableButtons();
		gameStarted = false;
		kingTaken = false;
		gameExited = false;
		playerColorLab.setText("");
		client.refreshBoard();
		client.setVisible(true);

		
		
		//refresh variables that could be dirty by end of previous game;
		playerColor = 0;
		playerTurn = false;
		disableButtons();
		
		if (sesh[gameSessionSelected].getOccupancy() == 1) 
		{
			sesh[gameSessionSelected].resetBoard();
			client.refreshBoard();
			sesh[gameSessionSelected].setWinningPlayer(0);
			playerColorLab.setText("You are the White Pieces");
			sesh[gameSessionSelected].setPlayerTurn(1);
			playerColor = 1;
			playerTurn = true;
			while (sesh[gameSessionSelected].getOccupancy() < 2 && !sesh[gameSessionSelected].gameOver()) 
			{
				Thread.sleep(250);
				client.promptLab.setText("Waiting for second player to connect.");
				Thread.sleep(250);
				client.promptLab.setText("Waiting for second player to connect..");
				Thread.sleep(250);
				client.promptLab.setText("Waiting for second player to connect...");
			}
		} 
		else 
		{
			playerColorLab.setText("You are the Black Pieces");
			playerColor = 2;
		}

		
		gameStarted = true;
		while (!sesh[gameSessionSelected].gameOver()) 
		{
			client.refreshBoard();
			//set turn at each loop
			if (sesh[gameSessionSelected].getPlayerTurn() == playerColor) 
			{
				playerTurn = true;
			}
			
			//logic for each player in the loop
			if (playerTurn) 
			{
				if(sesh[gameSessionSelected].gameOver() && !gameExited)
				{
					exitGame();
				}
				else
				{
					turn();
				}
			} 
			else 
			{
				if(sesh[gameSessionSelected].gameOver() && !gameExited)
				{
					exitGame();
				}
				else
				{
					client.promptLab.setText("It is the other player's turn, please wait.");
				}
			}
		}

	}

	public static void turn() throws InterruptedException, RemoteException 
	{
		boolean movesAvailable = true;
		int tempX, tempY;
		moves = 0;
		Thread.sleep(1000);
		client.refreshBoard();
		
		client.promptLab.setText("It is your turn. please select a piece to move");
		do
		{
			if(!movesAvailable)
			{
				client.promptLab.setText("Piece has no moves. please select another piece to move");
			}
			disableButtons();
			enablePlayerButtons();
			while (!pieceSelected) 
			{
				Thread.sleep(20);
				if(sesh[gameSessionSelected].gameOver())
				{
					break;
				}
			}
			pieceSelected = false;
	
			tempX = selectionX;
			tempY = selectionY;
			disableButtons();
			enableMoves();
			if(moves > 0)
			{
				movesAvailable = true;
			}
			else
			{
				movesAvailable = false;
			}
		}
		while(!movesAvailable && !sesh[gameSessionSelected].gameOver());
		
		client.promptLab.setText("Select where to move it to.");
		
		while (!pieceSelected && !sesh[gameSessionSelected].gameOver()) 
		{
			Thread.sleep(20);
		}
		
		pieceSelected = false;
		disableButtons();
		
		if(!sesh[gameSessionSelected].gameOver())
		{
			kingTaken = sesh[gameSessionSelected].swapPiece(tempX, tempY, selectionX, selectionY, playerColor);
			if(kingTaken && !gameExited)
			{
				sesh[gameSessionSelected].setWinningPlayer(playerColor);
				Thread.sleep(2000);
				exitGame();
			}
			else
			{
				sesh[gameSessionSelected].nextPlayerTurn();
			}
		}
		else
		{
			if(!gameExited)
			{
				exitGame();
			}
		}
		
		client.refreshBoard();
		//refresh used variables and change turn
		playerTurn = false;
		selectionX = -1;
		selectionY = -1;
	}

	public void refreshBoard() throws RemoteException 
	{
		for (int i = 0; i <= 7; i++) 
		{
			for (int j = 0; j <= 7; j++) 
			{
				// check server for piece
				if (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "Pawn")) 
				{
					if (sesh[gameSessionSelected].getColorAt(i, j) == 1) 
					{
						buttonArray[i][j].setIcon(wpawn);
						buttonArray[i][j].setDisabledIcon(wpawn);
					} 
					else 
					{
						buttonArray[i][j].setIcon(bpawn);
						buttonArray[i][j].setDisabledIcon(bpawn);
					}

				}
				if (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "Rook")) 
				{
					if (sesh[gameSessionSelected].getColorAt(i, j) == 1) 
					{
						buttonArray[i][j].setIcon(wrook);
						buttonArray[i][j].setDisabledIcon(wrook);
					} 
					else 
					{
						buttonArray[i][j].setIcon(brook);
						buttonArray[i][j].setDisabledIcon(brook);

					}
				}
				if (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "Knight")) 
				{
					if (sesh[gameSessionSelected].getColorAt(i, j) == 1) 
					{
						buttonArray[i][j].setIcon(wknight);
						buttonArray[i][j].setDisabledIcon(wknight);
					} 
					else 
					{
						buttonArray[i][j].setIcon(bknight);
						buttonArray[i][j].setDisabledIcon(bknight);
					}
				}
				if (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "Bishop")) 
				{
					if (sesh[gameSessionSelected].getColorAt(i, j) == 1) 
					{
						buttonArray[i][j].setIcon(wbishop);
						buttonArray[i][j].setDisabledIcon(wbishop);
					} 
					else 
					{
						buttonArray[i][j].setIcon(bbishop);
						buttonArray[i][j].setDisabledIcon(bbishop);
					}
				}
				if (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "Queen")) 
				{
					if (sesh[gameSessionSelected].getColorAt(i, j) == 1) 
					{
						buttonArray[i][j].setIcon(wqueen);
						buttonArray[i][j].setDisabledIcon(wqueen);
					} 
					else 
					{
						buttonArray[i][j].setIcon(bqueen);
						buttonArray[i][j].setDisabledIcon(bqueen);
					}
				}
				if (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "King")) 
				{
					if (sesh[gameSessionSelected].getColorAt(i, j) == 1) 
					{
						buttonArray[i][j].setIcon(wking);
						buttonArray[i][j].setDisabledIcon(wking);
					} 
					else 
					{
						buttonArray[i][j].setIcon(bking);
						buttonArray[i][j].setDisabledIcon(bking);
					}
				}
				if (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece")) 
				{
					buttonArray[i][j].setIcon(blank);
					buttonArray[i][j].setDisabledIcon(blank);
				}
			}
		}
	}

	public static void enablePlayerButtons() throws RemoteException 
	{
		for (int i = 0; i <= 7; i++) 
		{
			for (int j = 0; j <= 7; j++) 
			{
				if (sesh[gameSessionSelected].getColorAt(i, j) == playerColor) 
				{
					buttonArray[i][j].setEnabled(true);
				}
			}
		}
	}

	public static void enableMoves() throws RemoteException 
	{
		//these are for loop operations with certain pieces
		int i = selectionX;
		int j = selectionY;
		
		if(selectionX >= 0 && selectionX <= 7 && selectionY >= 0 && selectionY <= 7)
		{
			//pawns require color to be taken into account since they only go forward
			if(playerColor == 1)
			{
				if(!(j == 0) && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(selectionX, selectionY), "Pawn"))
				{
					if(j == 6 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, (j - 1)), "BlankPiece") 
							  && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, (j - 2)), "BlankPiece"))
					{
						buttonArray[i][(j - 2)].setEnabled(true);
						moves++;
					}
					if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, (j - 1)), "BlankPiece"))
					{
						buttonArray[i][(j - 1)].setEnabled(true);
						moves++;
					}
					if(!(i == 0) && sesh[gameSessionSelected].getColorAt((i - 1), (j - 1)) == 2)
					{
						buttonArray[i - 1][(j - 1)].setEnabled(true);
						moves++;
					}
					if(!(i == 7) && sesh[gameSessionSelected].getColorAt((i + 1), (j - 1)) == 2)
					{
						buttonArray[i + 1][(j - 1)].setEnabled(true);
						moves++;
					}
				}
			}
			else if(playerColor == 2)
			{
				if(!(j == 7) && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(selectionX, selectionY), "Pawn"))
				{
					if(j == 1 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, (j + 1)), "BlankPiece") 
							  && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, (j + 2)), "BlankPiece"))
					{
						buttonArray[i][(j + 2)].setEnabled(true);
						moves++;
					}
					if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, (j + 1)), "BlankPiece"))
					{
						buttonArray[i][(j + 1)].setEnabled(true);
						moves++;
					}
					if(!(i == 0) && sesh[gameSessionSelected].getColorAt((i - 1), (j + 1)) == 1)
					{
						buttonArray[i - 1][(j + 1)].setEnabled(true);
						moves++;
					}
					if(!(i == 7) && sesh[gameSessionSelected].getColorAt((i + 1), (j + 1)) == 1)
					{
						buttonArray[i + 1][(j + 1)].setEnabled(true);
						moves++;
					}
				}
			}
			
			//ROOK
			if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(selectionX, selectionY), "Rook"))
			{
				rookEnable(i, j);
			}
			
			//BISHOP
			if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(selectionX, selectionY), "Bishop"))
			{
				bishopEnable(i,j);
			}
			
			//KNIGHT
			if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(selectionX, selectionY), "Knight"))
			{
				knightEnable(i, j);
			}
	
			//QUEEN
			if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(selectionX, selectionY), "Queen"))
			{
				rookEnable(i, j);
				i = selectionX;
				j = selectionY;
				bishopEnable(i, j);	
			}
			
			if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(selectionX, selectionY), "King"))
			{
				kingEnable(i, j);
			}
		}
	}

	public static void disableButtons() 
	{
		for (int i = 0; i <= 7; i++) 
		{
			for (int j = 0; j <= 7; j++) 
			{
				buttonArray[i][j].setEnabled(false);
			}
		}
	}

	public static int getOcFromGame(int i) throws RemoteException 
	{
		return sesh[i].getOccupancy();
	}

	public static void exitGame() throws RemoteException, InterruptedException 
	{
		disableButtons();
		if(getOcFromGame(gameSessionSelected) == 2)
		{
			if (playerColor == 1) 
			{
				sesh[gameSessionSelected].playerExit();
				if(kingTaken || gameExited)
				{
					sesh[gameSessionSelected].setWinningPlayer(2);
				}
			} 
			else 
			{
				sesh[gameSessionSelected].playerExit();
				if(kingTaken || gameExited)
				{
					sesh[gameSessionSelected].setWinningPlayer(1);
				}
			}		
			promptWinnerLoser();
			Thread.sleep(1000);
		}
		else
		{
			if(gameStarted == false)
			{
				sesh[gameSessionSelected].playerExit();
				sesh[gameSessionSelected].setWinningPlayer(2);
			}
			else
			{
				sesh[gameSessionSelected].playerExit();
				promptWinnerLoser();
				Thread.sleep(1000);
			}
		}
		client.setVisible(false);		
	}

	public static void promptWinnerLoser() throws RemoteException
	{
		if (playerColor == sesh[gameSessionSelected].getWinningPlayer()) 
		{
			client.promptLab.setText("Congrats! You Win!");
		} 
		else 
		{
			client.promptLab.setText("Sorry! You Lose!");
		}
	}

	public static void rookEnable(int i, int j) throws RemoteException
	{
		//RIGHT
		i++;
		while(i <= 7 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece"))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
			i++;
		}
		if(i <= 7 && sesh[gameSessionSelected].getColorAt(i, j) != playerColor)
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		i = selectionX; //reset i variable
		j = selectionY; //reset j variable
		
		//LEFT
		i--;
		while(i >= 0 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece"))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
			i--;
		}
		if(i >= 0 && sesh[gameSessionSelected].getColorAt(i, j) != playerColor)
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}	
		i = selectionX; //reset i variable
		j = selectionY; //reset j variable
		
		//DOWN
		j++;
		while(j <= 7 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece"))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
			j++;
		}
		if(j <= 7 && sesh[gameSessionSelected].getColorAt(i, j) != playerColor)
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		i = selectionX; //reset i variable
		j = selectionY; //reset j variable
		
		//UP
		j--;
		while(j >= 0 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece"))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
			j--;
		}
		if(j >= 0 && sesh[gameSessionSelected].getColorAt(i, j) != playerColor)
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}	
		i = selectionX; //reset i variable
		j = selectionY; //reset j variable
	}

	public static void bishopEnable(int i, int j) throws RemoteException
	{
		//SouthEast
		i++;
		j++;
		while(i <= 7 && j <= 7 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece"))
		{
			if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece"))
			{
				buttonArray[i][j].setEnabled(true);
				moves++;
			}
			i++;
			j++;
		}
		if(i <= 7 && j <= 7 && sesh[gameSessionSelected].getColorAt(i, j) != playerColor)
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		i = selectionX; //reset i variable
		j = selectionY; //reset i variable
		
		//NorthWest
		i--;
		j--;
		while(i >= 0 && j >= 0 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt((i), (j)), "BlankPiece"))
		{
			if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece"))
			{
				buttonArray[i][j].setEnabled(true);
				moves++;
			}
			i--;
			j--;
		}
		if(i >= 0 && j >= 0 && sesh[gameSessionSelected].getColorAt(i, j) != playerColor)
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}	
		i = selectionX; //reset i variable
		j = selectionY; //reset i variable
		
		//SouthWest
		i--;
		j++;
		while(i >= 0 && j <= 7 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt((i), (j)), "BlankPiece"))
		{
			if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece"))
			{
				buttonArray[i][j].setEnabled(true);
				moves++;
			}
			i--;
			j++;
		}
		if(i >= 0 && j <= 7 && sesh[gameSessionSelected].getColorAt(i, j) != playerColor)
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		i = selectionX; //reset i variable
		j = selectionY; //reset i variable
		
		//NorthEast
		i++;
		j--;
		while(i <= 7 && j >= 0 && Objects.equals(sesh[gameSessionSelected].getPieceTypeAt((i), (j)), "BlankPiece"))
		{
			if(Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece"))
			{
				buttonArray[i][j].setEnabled(true);
				moves++;
			}
			i++;
			j--;
		}
		if(i <= 7 && j >= 0 && sesh[gameSessionSelected].getColorAt(i, j) != playerColor)
		{
			buttonArray[i][j].setEnabled(true);
		}	
		i = selectionX; //reset i variable
		j = selectionY; //reset i variable
	}

	public static void knightEnable(int i, int j) throws RemoteException
	{
		//check right bottom
		i = selectionX + 2;
		j = selectionY + 1;
		if(i <= 7 && j <= 7 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
	
		//check left top
		i = selectionX - 2;
		j = selectionY - 1;
		if(i >= 0 && j >= 0 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		//check left bottom
		i = selectionX - 2;
		j = selectionY + 1;
		if(i >= 0 && j <= 7 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		//check right top
		i = selectionX + 2;
		j = selectionY - 1;
		if(i <= 7 && j >= 0 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		//check right bottom again 
		i = selectionX + 1;
		j = selectionY + 2;
		if(i <= 7 && j <= 7 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
	
		//check left top again
		i = selectionX - 1;
		j = selectionY - 2;
		if(i >= 0 && j >= 0 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		//check left bottom again
		i = selectionX - 1;
		j = selectionY + 2;
		if(i >= 0 && j <= 7 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		//check right top again
		i = selectionX + 1;
		j = selectionY - 2;
		if(i <= 7 && j >= 0 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
	}

	public static void kingEnable(int i, int j) throws RemoteException
	{
		i = selectionX + 1;
		j = selectionY;
		if(i <= 7 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		i = selectionX;
		j = selectionY + 1;
		if(j <= 7 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		i = selectionX - 1;
		j = selectionY;
		if(i >= 0 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		i = selectionX;
		j = selectionY - 1;
		if(j >= 0 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		i = selectionX + 1;
		j = selectionY + 1;
		if(i <= 7 && j <= 7 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		i = selectionX + 1;
		j = selectionY - 1;
		if(i <= 7 && j >= 0 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		i = selectionX - 1;
		j = selectionY + 1;
		if(i >= 0 && j <= 7 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
		
		i = selectionX - 1;
		j = selectionY - 1;
		if(i >= 0 && j >= 0 && (Objects.equals(sesh[gameSessionSelected].getPieceTypeAt(i, j), "BlankPiece") || sesh[gameSessionSelected].getColorAt(i, j) != playerColor))
		{
			buttonArray[i][j].setEnabled(true);
			moves++;
		}
	}
}