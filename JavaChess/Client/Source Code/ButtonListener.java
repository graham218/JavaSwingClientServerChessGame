import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class ButtonListener implements ActionListener 
{
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		//for SessionFrame
		if(e.getSource() == SessionFrame.game1JoinButton)
		{	
			ChessClient.gameSessionSelected = 0;
		}
		if(e.getSource() == SessionFrame.game2JoinButton)
		{	
			ChessClient.gameSessionSelected = 1;
		}
		if(e.getSource() == SessionFrame.game3JoinButton)
		{	
			ChessClient.gameSessionSelected = 2;
		}
		
		
		//for ChessClient
		if (e.getSource() == ChessClient.quitBut) 
		{
			try 
			{
				ChessClient.gameExited = true;
				ChessClient.exitGame();	
			} 
			catch (RemoteException e1) 
			{
				e1.printStackTrace();
			} 
			catch (InterruptedException e1) 
			{
				e1.printStackTrace();
			}
		}
		
		for (int i = 0; i <= 7; i++) 
		{
			for (int j = 0; j <= 7; j++) 
			{
				if (e.getSource() == ChessClient.buttonArray[i][j]) 
				{
					ChessClient.selectionX = i;
					ChessClient.selectionY = j;
					ChessClient.pieceSelected = true;
				}
			}

		}
	}
}