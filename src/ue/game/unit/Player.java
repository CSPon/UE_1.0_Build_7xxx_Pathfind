package ue.game.unit;

public class Player
{
	private float xPos, yPos;
	
	public Player(int xPos, int yPos)
	{
		this.xPos = xPos; this.yPos = yPos;
	}
	
	public void translate(float dX, float dY)
	{	
		this.xPos -= dX; this.yPos -= dY;
	}
	
	public void set(float xPos, float yPos)
	{
		this.xPos = xPos; this.yPos = yPos;
	}
	
	public float getX(){return xPos;}
	public float getY(){return yPos;}
}
