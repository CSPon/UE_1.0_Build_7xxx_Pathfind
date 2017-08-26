package ue.util.pathfindernew;

import java.util.Vector;

import ue.game.world.tile.terrain.Terrain;

public class JumpPointSearch
{
	public static Vector<Node> getSuccessors(int[][] level, Vector<Node> openList, Vector<Node> closeList, Node current, Node start, Node goal)
	{
		// Get adjacent nodes from the test node.
		Vector<Node> adjacent = getAdjacentNode(level, openList, closeList, current);
		Vector<Node> successors = new Vector<Node>(0, 1);
		
		// Perform jump point check to see if these adjacent nodes leads to jump node. We call these nodes successors.
		for(Node checker : adjacent)
		{
			// Get direction of the node by subtracting row and column of checker and current
			int dX = getDirection(checker.getRow(), current.getRow());
			int dY = getDirection(checker.getCol(), current.getCol());
			
			// If checking nodes returns a jumping node, put it into successor vector.
			Node node = jumpPointSearch(level, checker, goal, dX, dY);
			if(node != null)
				successors.add(node);
		}
		
		return successors;
	}
	
	private static Node jumpPointSearch(int[][] level, Node current, Node goal, int dX, int dY)
	{
		if(terrainCheck(level, current.getRow(), current.getCol()) <= 0)
			return null;
		else if(PathFinder.compareNode(current, goal))
			return current;
		// If it's going horizontal, perform horizontal check and see if there is any point to jump to.
		if(dY == 0)
			return horizontalCheck(level, current, goal, dX);
		// If it's going vertical, perform vertical check and see if there is any point to jump to.
		if(dX == 0)
			return verticalCheck(level, current, goal, dY);
		// If it's going diagonal perform diagonal check and see if there is nay point to jump to.
		if(dX != 0 && dY != 0)
			return diagonalCheck(level, current, goal, dX, dY);
		// else it counts as not working node.
		return null;
	}
	
	private static Node horizontalCheck(int[][] level, Node current, Node goal, int dX)
	{
		// If current node is blocked or out of bound, return failed check.
		if(terrainCheck(level, current.getRow(), current.getCol()) <= 0)
			return null;
		// If node on either side of the checking node is blocked, return this node as jump node.
		if(terrainCheck(level, current.getRow(), current.getCol() + 1) == 0 || terrainCheck(level, current.getRow(), current.getCol() - 1) == 0)
			if(terrainCheck(level, current.getRow() + dX, current.getCol()) > 0)
				return current;
		// If node itself is the goal node, return this node as jump node.
		if(PathFinder.compareNode(current, goal))
			return current;
		// If node on either side of the checking node is empty, return next node and continue horizontal checking.
		return horizontalCheck(level, new Node(current.getRow() + dX, current.getCol()), goal, dX);
	}
	
	private static Node verticalCheck(int[][] level, Node current, Node goal, int dY)
	{
		// If current node is blocked or out of bound, return failed check.
		if(terrainCheck(level, current.getRow(), current.getCol()) <= 0)
			return null;
		// If node on either side of the checking node is blocked, return this node as jump node.
		if(terrainCheck(level, current.getRow() + 1, current.getCol()) == 0 || terrainCheck(level, current.getRow() - 1, current.getCol()) == 0)
			if(terrainCheck(level, current.getRow(), current.getCol() + dY) > 0)
				return current;
		// If node itself is the goal node, return this node as jump node.
		if(PathFinder.compareNode(current, goal))
			return current;
		// If node on either side of the checking node is empty, return next node and continue horizontal checking.
		return verticalCheck(level, new Node(current.getRow(), current.getCol() + dY), goal, dY);
	}
	
	private static Node diagonalCheck(int[][] level, Node current, Node goal, int dX, int dY)
	{
		// If current node is the goal node, return this node.
		if(PathFinder.compareNode(current, goal))
			return current;
		// If either above or side or the checking node is blocked, return this node as jump node.
		if(terrainCheck(level, current.getRow() + dX, current.getCol()) == 0 || terrainCheck(level, current.getRow(), current.getCol() + dY) == 0)
			if(terrainCheck(level, current.getRow() + dX, current.getCol() + dY) > 0)
				return current;
		// If horizontal or vertical check from current node returns a jump node, return this node as jump node.
		if(horizontalCheck(level, current, goal, dX) != null || verticalCheck(level, current, goal, dY) != null)
			return current;
		// If current node is blocked, return failed check.
		if(terrainCheck(level, current.getRow(), current.getCol()) <= 0)
			return null;
		// If both above or side or the checking node is blocked, return this node as jump node.
		if(terrainCheck(level, current.getRow() + dX, current.getCol()) <= 0 && terrainCheck(level, current.getRow(), current.getCol() + dY) <= 0)
			return null;
		// If node on either side of the checking node is empty, return next node and continue diagonal checking.
		return diagonalCheck(level, new Node(current.getRow() + dX, current.getCol() + dY), goal, dX, dY);
	}
	
	private static byte getDirection(int s, int e)
	{
		if((s - e) >= 1)
			return 1;
		else if((s - e) <= -1)
			return -1;
		
		return 0;
	}
	
	public static Vector<Node> getAdjacentNode(int[][] level, Vector<Node> open, Vector<Node> close, Node center)
	{
		Vector<Node> list = new Vector<Node>(0, 1);
		
		for(int row = center.getRow() - 1; row <= center.getRow() + 1; row++)
			for(int col = center.getCol() - 1; col <= center.getCol() + 1; col++)
			{
				if(terrainCheck(level, row, col) > 0 && !PathFinder.compareNode(new Node(row, col), center))
				{
					if(!PathFinder.isInList(close, new Node(row, col)) && !PathFinder.isInList(open, new Node(row, col)))
					{
						Node newNode = new Node(row, col);
						list.add(0, newNode);
					}
				}
			}
		
		return list;
	}
	
	public static int terrainCheck(int[][] level, int row, int col)
	{
		if(row > level.length - 1 || row < 0)
			return -1;
		else if(col > level.length - 1 || col < 0)
			return -1;
		else if(level[row][col] == Terrain.WATER)
			return 0;
		else if(level[row][col] == Terrain.GRASS)
			return 0;
		
		return level[row][col];
	}
}
