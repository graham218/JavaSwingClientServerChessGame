import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GameSession extends UnicastRemoteObject implements GameSessionInterface
{
	//not sure what this does, but it's required to not give warnings
	private static final long serialVersionUID = 1L;
	private int playersInGame = 0;
	private Board board = new Board();
	
	public GameSession() throws RemoteException 
	{

	}
	
	@Override
	public int getOccupancy() throws RemoteException
	{
		return playersInGame;
	}
	
	@Override
	public String getPieceTypeAt(int x, int y) throws RemoteException
	{
		return board.getTypeAt(x, y);
	}
	
	@Override
	public boolean playerJoin() throws RemoteException
	{
		if(playersInGame == 0)
		{
			playersInGame++;
			return true;
		}
		else if(playersInGame == 1)
		{
			playersInGame++;
			board.setWinningPlayer(0);
			return true;
		}
		else{return false;}
	}
	
	@Override
	public int getColorAt(int x, int y) throws RemoteException
	{
		return board.getColorAt(x, y);
	}
	
	@Override
	public boolean gameOver() throws RemoteException
	{
		return board.gameOver();
	}
	
	@Override
	public int getWinningPlayer() throws RemoteException
	{
		return board.getWinningPlayer();
	}
	
	@Override
	public int getPlayerTurn() throws RemoteException
	{
		return board.getPlayerTurn();
	}
	
	@Override
	public void nextPlayerTurn() throws RemoteException
	{
		board.nextPlayerTurn();
	}
	
	@Override
	public void playerExit() throws RemoteException
	{
		if(playersInGame == 1)
		{
			playersInGame--;

		}
		else if(playersInGame == 2)
		{
			playersInGame--;
		}
		else
		{
			playersInGame = 0;
		}

	}
	
	@Override
	public void resetBoard() throws RemoteException
	{
		board.populateBoard();
	}
	
	@Override
	public void setWinningPlayer(int p) throws RemoteException
	{
		board.setWinningPlayer(p);
	
	}
	
	@Override
	public boolean swapPiece(int x1, int y1, int x2, int y2, int c) throws RemoteException
	{
		return board.swap(x1, y1, x2, y2, c);
	}

	@Override
	public void setPlayerTurn(int t) throws RemoteException
	{
		board.setPlayerTurn(t);
	}
}