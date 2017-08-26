package ue.util.pathfindernew;

import java.util.Vector;

public class PathFinder
{
	/**
	 * Generates path from start node to goal node.
	 * @return
	 */
	public synchronized static Vector<Node> getPath4(int[][] level, Vector<Node> openList, Vector<Node> closeList, Node start, Node goal)
	{
		// If open list is empty, add start node to open list
		if(openList.isEmpty() && closeList.isEmpty())
			openList.add(start);
		// if open list is not empty, pick a node with lowest F cost.
		else if(!openList.isEmpty())
		{
			// Set lowest F cost node as current node.
			Node current;
			current = getLowestF(openList, closeList);
			
			// Add current node to close list and remove it from the open list
			closeList.add(current);
			openList.remove(current);
			
			// Fit the lists to size just in case
			openList.trimToSize();
			closeList.trimToSize();
			
			// Perform jump point search to receive successors.
			Vector<Node> successors = JumpPointSearch.getSuccessors(level, openList, closeList, current, start, goal);
				
			// Calculate g, h, f values of each successors, and set their parent node as current node.
			for(Node node : successors)
			{
				int g = getG(node, current);
				int h = getH(node, goal);
				int f = g + h;
				node.setParent(current);
				node.setFVal(f);
				node.setGVal(g);
				node.setHVal(h);
				
				if(!isInList(openList, node) && !isInList(closeList, node))
					openList.add(node);
				else if(isInList(openList, node) && !isInList(closeList, node))
					if(getNode(openList, node).getGVal() > g + current.getGVal())
					{
						System.out.println("Detected! Replacing (" + node.getRow() + "," + node.getCol() + ") Old G : " + getNode(openList, node).getGVal() + " New G : " + g);
						replaceNode(openList, node);
					}
			}
		}
		
		// Check if goal node is in close list.
		if(isInList(closeList, goal))
		{
			// Set goal node as initial reference node.
			Node reference = getNode(closeList, goal);
			// Create an empty path to start put nodes leading up to the path.
			Vector<Node> path = new Vector<Node>(0, 1);
			// Add reference node to the path and start adding path nodes
			path.add(0, reference);
			// Keep adding nodes until reference node has no parent node (meaning until it reaches to the starting node)
			while(reference.getParent() != null)
			{
				// If current node's parent node is starting node, stop adding nodes
				if(compareNode(reference.getParent(), start) || reference.getParent() == null)
					break;
				// Push vector to add parent node of current reference node.
				path.add(0, reference.getParent());
				// Set reference node as current node's parent node.
				reference = reference.getParent();
			}
			path.add(0, start);
			// Return entire path.
			return path;
		}
		// Path has not yet reached to goal node but to visualize current node
		if(closeList.size() > 0)
		{
			// Set lowest F value as end node.
			Node reference = closeList.lastElement();
			// Create an empty path to start put nodes leading up to the path.
			Vector<Node> path = new Vector<Node>(0, 1);
			// Add reference node to the path and start adding path nodes
			path.add(0, reference);
			// Keep adding nodes until reference node has no parent node (meaning until it reaches to the starting node)
			while(reference.getParent() != null)
			{
				// Push vector to add parent node of current reference node.
				path.add(0, reference.getParent());
				// Set reference node as current node's parent node.
				reference = reference.getParent();
			}
			path.add(0, start);
			// Return entire path
			return path;
		}
		// If open list is empty (Meaning path cannot be achieved), return empty path
		if(openList.size() < 1)
			return new Vector<Node>(0);
		
		// Else just return nothing.
		return null;
	}
	
	public static boolean compareNode(Node a, Node b)
	{
		if(a != null && b != null)
			if(a.getRow() == b.getRow() && a.getCol() == b.getCol())
				return true;
		return false;
	}
	
	public static boolean isInList(Vector<Node> list, Node testNode)
	{	
		for(Node node : list)
			if(compareNode(node, testNode))
				return true;
		return false;
	}
	
	private static Node getLowestF(Vector<Node> open, Vector<Node> close)
	{
		Node lowest = open.lastElement();
		
		for(Node node : open)
			if(node.getFVal() <= lowest.getFVal() && !isInList(close, node))
				lowest = node;
		
		return lowest;
	}
	
	private static Node getNode(Vector<Node> list, Node target)
	{
		for(Node node : list)
			if(compareNode(node, target))
				return node;
		return null;
	}
	
	private static void replaceNode(Vector<Node> list, Node target)
	{
		for(int i = 0; i < list.size(); i++)
			if(compareNode(list.get(i), target))
				list.set(i, target);
	}
	
	private static int getG(Node a, Node b)
	{
		float base = (float) Math.pow((float) a.getRow() - (float) b.getRow(), 2);
		float height = (float) Math.pow((float) a.getCol() - (float) b.getCol(), 2);
		
		return (int) (Math.sqrt(base + height) * 10);
	}
	
	private static int getH(Node a, Node b)
	{
		return Math.abs((a.getRow() - b.getRow()) + (a.getCol() - b.getCol())) * 10;
	}
}
