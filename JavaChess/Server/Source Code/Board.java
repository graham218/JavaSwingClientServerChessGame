import java.util.Objects;

public class Board 
{
	private int colorTurn = 1;
	private boolean gameOver = false;
	private int winningPlayer = 0;
	private Piece[][] board = new Piece[8][8];
	
	Board()
	{
		this.populateBoard();
	}
	
	public void populateBoard() //2 is black 1 is white
	{
		//set black back row
		gameOver = false;
		winningPlayer = 0;
		board[0][0] = new Piece(0,0,2, "Rook");
		board[1][0] = new Piece(1,0,2, "Knight");
		board[2][0] = new Piece(2,0,2, "Bishop");
		board[3][0] = new Piece(3,0,2, "Queen");
		board[4][0] = new Piece(4,0,2, "King");
		board[5][0] = new Piece(5,0,2, "Bishop");
		board[6][0] = new Piece(6,0,2, "Knight");
		board[7][0] = new Piece(7,0,2, "Rook");
		
		//set row of black pawns
		for(int i = 0; i < 8; i++)
		{
			board[i][1] = new Piece(i,1,2, "Pawn");
		}
		
		//set black space in middle
		for(int i = 0; i <= 7; i++)
		{
			for(int j = 2; j <= 5; j++)
			{
				board[i][j] = new Piece(i,j,0, "BlankPiece");
			}
		}
		
		//set row of white pawns
		for(int i = 0; i < 8; i++)
		{
			board[i][6] = new Piece(i,6,1, "Pawn");
		}
		
		//set black back row
		board[0][7] = new Piece(0,7,1, "Rook");
		board[1][7] = new Piece(1,7,1, "Knight");
		board[2][7] = new Piece(2,7,1, "Bishop");
		board[3][7] = new Piece(3,7,1, "Queen");
		board[4][7] = new Piece(4,7,1, "King");
		board[5][7] = new Piece(5,7,1, "Bishop");
		board[6][7] = new Piece(6,7,1, "Knight");
		board[7][7] = new Piece(7,7,1, "Rook");
	}
	
	public boolean swap(int x1, int y1, int x2, int y2, int colorTurn)  //not working
	{
		boolean kingTaken = false;
		//if neither of the pieces is a blank piece then replace the opponents piece with a blank piece
		if(!Objects.equals(this.getTypeAt(x1,y1), "BlankPiece") && !Objects.equals(this.getTypeAt(x2,y2), "BlankPiece"))
		{
			if(colorTurn == 1)
			{
				
				if(Objects.equals(this.getTypeAt(x2,y2), "King"))
				{
					kingTaken = true;
				}
				board[x2][y2] = this.getPieceAt(x1, y1);
				board[x2][y2].setPositionX(x2);
				board[x2][y2].setPositionY(y2);
				board[x1][y1] = new Piece(x1, y1, 0, "BlankPiece");
				
			}
			else if(colorTurn == 2)
			{
				if(Objects.equals(this.getTypeAt(x2,y2), "King"))
				{
					kingTaken = true;
				}
				board[x2][y2] = this.getPieceAt(x1, y1);
				board[x2][y2].setPositionX(x2);
				board[x2][y2].setPositionY(y2);
				board[x1][y1] = new Piece(x1, y1, 0, "BlankPiece");

			}
			
		}
		//if one of them is a blank piece just swap them and change the piece's as well as boards positions
		else
		{
			Piece temp = board[x1][y1].returnCopy();
			board[x1][y1] = this.getPieceAt(x2, y2);
			board[x2][y2] = temp;
			board[x1][y1].setPositionX(x1);
			board[x1][y1].setPositionY(y1);
			board[x2][y2].setPositionX(x2);
			board[x2][y2].setPositionY(y2);
		}
		return kingTaken;
	}	
	public Piece getPieceAt(int x, int y)
	{
		return board[x][y];
	}
	public String getTypeAt(int x, int y)
	{
		return board[x][y].getType();
	}
	public String toString()  //this is mostly for testing purposes
	{
		String str = "";
		
		for(int j = 0; j <= 7; j++)
		{
			for(int i = 0; i <= 7; i++)
			{
				str = str + this.getTypeAt(i, j) + "\t";
				if(i == 7)
					str = str + "\n";
			}
		}

		return str;
	}
	public int getColorAt(int x, int y)
	{
		return board[x][y].getColor();
	}
	public boolean gameOver()
	{
		return gameOver;
	}
	public int getWinningPlayer()
	{
		return winningPlayer;
	}
	public int getPlayerTurn()
	{
		return colorTurn;
	}
	public void nextPlayerTurn()
	{
		
		if(colorTurn == 1)
		{
			colorTurn = 2;
		}
		else
		{
			colorTurn = 1;
		}
	}
	public void setWinningPlayer(int p)
	{
		if(p == 0)
		{
			winningPlayer = p;
			gameOver = false;
		}
		else
		{
			winningPlayer = p;
			gameOver = true;
		}
	}
	public void setPlayerTurn(int t)
	{
		colorTurn = t;
	}
}