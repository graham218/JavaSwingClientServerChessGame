public class Piece 
{
	private int positionX;
	private int positionY;
	private int color;
	private String pieceType;
	
	//constructors and getters and setters
	Piece(int c)
	{
		this.setPositionX(-1);
		this.positionY =-1;
		this.setColor(c);
	}
	Piece(int x, int y, int c, String pT)
	{
		this.setPositionX(x);
		this.positionY = y;
		this.setColor(c);
		pieceType = pT;
	}	
	public int getColor() 
	{
		return color;
	}
	public void setColor(int color) 
	{
		this.color = color;
	}
	public int getPositionX() 
	{
		return positionX;
	}
	public void setPositionX(int x) 
	{
		this.positionX = x;
	}
	public int getPositionY() 
	{
		return positionY;
	}
	public void setPositionY(int y) 
	{
		this.positionY = y;
	}
	public Piece returnCopy()
	{
		return new Piece(positionX, positionY, color, getType());
	}
	public String getType()
	{
		return pieceType;
	}
}