package ue.util.pathfindernew;

/**
 * 
 * @author Charlie Shin
 *
 */
public class Node
{
	private int row, col, g_val, h_val, f_val;
	
	private Node parent;
	
	public Node(int row, int col)
	{
		this.row = row;
		this.col = col;
		
		//f_val = 0;
		g_val = 0;
		h_val = 0;
	}
	
	public void debug()
	{
		System.out.println();
		System.out.println("------Node Data-------");
		System.out.println("Node Location : " + row + " " + col);
		System.out.println("G : " + g_val);
		System.out.println("H : " + h_val);
		System.out.println("F : " + f_val);
		System.out.println("--Parent--");
		if(parent != null)
		{
			System.out.println("Parent : " + parent.getRow() + " " + parent.getCol());
		}
		else
			System.out.println("No Parent");
		System.out.println("----------------------");
		System.out.println();
	}
	
	public int getRow()
	{
		return this.row;
	}
	
	public int getCol()
	{
		return this.col;
	}
	
	public int getGVal(){return this.g_val;}
	public int getHVal(){return this.h_val;}
	public int getFVal(){return this.f_val;}
	
	public void setGVal(int g_val){this.g_val = g_val;}
	public void setHVal(int h_val){this.h_val = h_val;}
	public void setFVal(int f_val){this.f_val = f_val;}
	
	public void setPos(int row, int col)
	{
		this.row = row; this.col = col;
	}
	
	public Node getParent()
	{
		return this.parent;
	}
	public void setParent(Node parent)
	{
		this.parent = parent;
	}
}
