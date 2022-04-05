JavaChess is a Java swing GUI chess game using
RMI and a client/server distribution to allow
up to 6 players to play chess simultaneously

How to run the program:
Step 1:
	Install Java Version 8u92 or newer (32 or 64 bit)

Step 2:
	Navigate to \<this folder>\JavaChess\Server\

Step 3:
	Run JavaChessServer.jar

Step 4:
	Navigate to \<this folder>\JavaChess\Client\

Step 5:
	Run 2 or more instances of JavaChessClient.jar
	on any PC(s) connected to the same network as
	the PC running JavaChessServer.jar

Step 6:
	Each client will have you enter an IP address to connect. 
	Enter the IPv4 address of the PC running JavaChessServer.jar
	and click "Enter"

Step 7:
	Both clients will open a windows that will show hosted game 
	sessions. Click "Join Game 1" on both clients and both 
	clients will be able to play chess. Just click exit or win
	the game and you'll be brought back to the session window 
	where you can close the program or play another game.

NOTE:
	If you'd like to compile this code yourself
	you'll need to make sure you put the .png
	image files in the same folder as ChessClient.class
	or the program will not run