package ue.util.pathfinder;

import java.util.Vector;

/**
 * <b>CURRENT PATHFINDER VERSION IS OUT-OF-DATE. DO NOT USE THERE IS AN APPROPRIATE UPDATE.</b><br>
 * Jump-point search utility for faster pathfinding.
 * @author Charlie Shin
 *
 */
public class JumpPoint
{
	/**
	 * Goes through jump-point search algorithm to find optimized path, instead of checking every nodes in open list.
	 * @param level Matrix of byte representing heightmap.
	 * @param openList Vector of nodes(open list).
	 * @param closeList Vector of nodes(close list).
	 * @param current Current node to check.
	 * @param start Starting node. That is, the original starting point.
	 * @param end Final end point.
	 * @return Vector of nodes with optimized path.
	 */
	public static Vector<Node> jumpTest(int[][] level, Vector<Node> openList, Vector<Node> closeList, Node current, Node start, Node end)
	{
		Vector<Node> successors = new Vector<Node>(0, 1);
		
		Vector<Node> adjacent = PathFinder.getAdjacentNode(level, openList, closeList, current);
		
		for(Node testNode : adjacent)
		{
			byte dX = getDirection(testNode.row, current.row);
			byte dY = getDirection(testNode.colum, current.colum);
			
			Node successor = jumpCheck(level, testNode, dX, dY, start, end);
			if(successor != null)
				successors.add(successor);
		}
		
		return successors;
	}
	
	/**
	 * Goes through jump check.<br>
	 * Compare to jumpTest(), this method utilizes only one node to get optimized path throughout the map.
	 * @param level Matrix of byte representing heightmap.
	 * @param testNode Testing node.
	 * @param dX Horizontal direction(-1 ~ 1).
	 * @param dY Vertical direction(-1 ~ 1).
	 * @param start Starting node. That is, the original starting point.
	 * @param target Final end point.
	 * @return A node that must go through pathfind check.
	 */
	public static Node jumpCheck(int[][] level, Node testNode, byte dX, byte dY, Node start, Node target)
	{
		if(PathFinder.terrainCheck(level, testNode.row, testNode.colum) < 0)
			return null;
		if(PathFinder.compareNode(testNode, target))
			return testNode;
		
		if(dY == 0)
			if(horizontalCheck(level, testNode, target, dX) != null)
				return horizontalCheck(level, testNode, target, dX);
		if(dX == 0)
			if(verticalCheck(level, testNode, target, dY) != null)
				return verticalCheck(level, testNode, target, dY);
		if(dX != 0 && dY != 0) // if going diagonal
			if(diagonalCheck(level, testNode, target, dX, dY) != null)
				return diagonalCheck(level, testNode, target, dX, dY);
		
		return null;
	}
	
	/**
	 * Checks diagonal blocks for a path.
	 * @param level
	 * @param testNode
	 * @param targetNode
	 * @param dX
	 * @param dY
	 * @return A node to be added to open list.
	 */
	public static Node diagonalCheck(int[][] level, Node testNode, Node targetNode, byte dX, byte dY)
	{
		// If horizontal or vertical check returns a jump point, return this node
		if(horizontalCheck(level, testNode, targetNode, dX) != null || verticalCheck(level, testNode, targetNode, dY) != null)
			return testNode;
		// If current node is target node, return this node
		else if(PathFinder.compareNode(testNode, targetNode))
			return testNode;
		// If node in front of the current node is blocked, return failed node check
		else if(PathFinder.terrainCheck(level, testNode.row + dX, testNode.colum + dY) < 0)
			return null;
		else if(PathFinder.terrainCheck(level, testNode.row + dX, testNode.colum) < 0 && PathFinder.terrainCheck(level, testNode.row, testNode.colum + dY) < 0)
			return null;
		// If node behind or next to the current node is blocked, return forced neighbor check
		else if(PathFinder.terrainCheck(level, testNode.row - dX, testNode.colum) < 0 || PathFinder.terrainCheck(level, testNode.row, testNode.colum - dY) < 0)
			return testNode;
		// Else continue to next diagonal node
		else return diagonalCheck(level, new Node(testNode.row + dX, testNode.colum + dY), targetNode, dX, dY);
	}
	
	/**
	 * Checks horizontal blocks for path.
	 * @param level
	 * @param testNode
	 * @param targetNode
	 * @param dir
	 * @return A node to be added to open list.
	 */
	public static Node horizontalCheck(int[][] level, Node testNode, Node targetNode, byte dir)
	{
		// If node in front of the current position is blocked, return failed node check
		if(PathFinder.terrainCheck(level, testNode.row + dir, testNode.colum) < 0)
			return null;
		// If node next to the current position is blocked, return forced neighbor node check 
		if(PathFinder.terrainCheck(level, testNode.row, testNode.colum + 1) < 0 || PathFinder.terrainCheck(level, testNode.row, testNode.colum - 1) < 0)
			return testNode;
		// If current position is the target node, return this node
		if(PathFinder.compareNode(testNode, targetNode))
			return testNode;
		// Else move onto next node and continue
		else return horizontalCheck(level, new Node(testNode.row + dir, testNode.colum), targetNode, dir);
	}
	
	/**
	 * Checks vertical blocks for path.
	 * @param level
	 * @param testNode
	 * @param targetNode
	 * @param dir
	 * @return A node to be added to open list.
	 */
	public static Node verticalCheck(int[][] level, Node testNode, Node targetNode, byte dir)
	{
		// If tile in front of the tested node is blocked, return failed node check
		if(PathFinder.terrainCheck(level, testNode.row, testNode.colum + dir) < 0)
			return null;
		// If tested node is the target node, return this node
		if(PathFinder.compareNode(testNode, targetNode))
			return testNode;
		// If tile next to tested node is blocked, return forced neighbor check
		if(PathFinder.terrainCheck(level, testNode.row, testNode.colum + 1) < 0 || PathFinder.terrainCheck(level, testNode.row, testNode.colum - 1) < 0)
			return testNode;
		// Else move onto next node
		else return verticalCheck(level, new Node(testNode.row, testNode.colum + dir), targetNode, dir);
	}
	
	/**
	 * Gets direction of path.
	 * @param s Starting position.
	 * @param e End position.
	 * @return byte value representing one-dimensional direction.
	 */
	public static byte getDirection(int s, int e)
	{
		if((s - e) >= 1)
			return 1;
		else if((s - e) <= -1)
			return -1;
		
		return 0;
	}
}
