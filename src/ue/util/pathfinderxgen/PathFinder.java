/**
 * PathFinder class is for handling major path finding sequences.
 * 
 * First incorporate basic A* Path find here, then synthesize the code with jump point search algorithm
 */

package ue.util.pathfinderxgen;

import java.util.ArrayList;

import ue.game.world.tile.terrain.Terrain;

public class PathFinder
{
	public ArrayList<Node> findASPath(int[][] terrain, Node start, Node goal)
	{
		if(start.openList == null && start.closeList == null)
		{
			start.goalValue = 0;
			start.hueValue = 0;
			start.fieldValue = start.goalValue + start.hueValue;
			
			start.openList = new ArrayList<Node>();
			start.closeList = new ArrayList<Node>();
			start.openList.add(start);
		}
		
		Node parent = getParentNode(start.openList);
		
		removeNode(parent, start.openList);
		addNode(parent, start.closeList);
		
		for(Node node : getAdjacent(terrain, parent, start.closeList))
		{ 
			// If surrounding node is already in the open list, check if it is much faster to go to the surrounding node by using the current parent node.
			// If the surrounding node has less goal value when going through the current parent node, switch surrounding node's parent node to current parent node.
			// If surrounding node is not in the open list, add the node to the open list and set it's parent to current parent.
			if(isInList(node, start.openList))
			{
				Node current = getNodeFromList(node, start.openList);
				float testGoalValue = parent.goalValue + getGoalValue(parent, current);
				if(testGoalValue < current.goalValue)
				{
					//System.out.println("Detected! Replacing (" + node.x + "," + node.y + ") Old G : " + getNodeFromList(node, start.openList).goalValue + " New G : " + testGoalValue);
					current.parentNode = parent;
					current.goalValue = testGoalValue;
					current.fieldValue = current.goalValue + current.hueValue;
				}
				
				start.openList.set(start.openList.indexOf(current), current);
			}
			if(!isInList(node, start.openList))
			{
				node.parentNode = parent;
				
				node.goalValue = parent.goalValue + getGoalValue(parent, node);
				node.hueValue = getDiagonalHueValue(node, goal);
				node.fieldValue = node.goalValue + node.hueValue;
				
				start.openList.add(node);
			}
		}
		
		return finalizePath(start, goal);
	}
	
	public ArrayList<Node> findAJPSPath(int[][] terrain, Node start, Node goal)
	{
		if(start.openList == null && start.closeList == null)
		{
			start.goalValue = 0;
			start.hueValue = 0;
			start.fieldValue = start.goalValue + start.hueValue;
			
			start.openList = new ArrayList<Node>();
			start.closeList = new ArrayList<Node>();
			start.openList.add(start);
		}
		
		Node parent = getParentNode(start.openList);
		
		removeNode(parent, start.openList);
		addNode(parent, start.closeList);
		
		for(Node node : new JumpPointSearch().getSuccessors(terrain, parent, start, goal))
		{
			if(isInList(node, start.openList))
			{
				Node current = getNodeFromList(node, start.openList);
				float testGoalValue = parent.goalValue + getGoalValue(parent, current);
				if(testGoalValue < current.goalValue)
				{
					System.out.println("Detected! Replacing (" + node.x + "," + node.y + ") Old G : " + getNodeFromList(node, start.openList).goalValue + " New G : " + testGoalValue);
					current.parentNode = parent;
					current.goalValue = testGoalValue;
					current.fieldValue = current.goalValue + current.hueValue;
				}
				
				start.openList.set(start.openList.indexOf(current), current);
			}
			else if(!isInList(node, start.openList))
			{
				node.goalValue = parent.goalValue + getGoalValue(parent, node);
				node.hueValue = getManhattanHueValue(node, goal);
				node.fieldValue = node.goalValue + node.hueValue;
				
				start.openList.add(node);
			}
		}
		
		return finalizePath(start, goal);
	}
	
	public ArrayList<Node> finalizePath(Node start, Node goal)
	{
		ArrayList<Node> path;
		if(isInList(goal, start.closeList))
		{
			path = new ArrayList<Node>();
			Node pathParent = getNodeFromList(goal, start.closeList);
			path.add(0, pathParent);
			do
			{
				pathParent = pathParent.parentNode;
				path.add(0, pathParent);
			}
			while(pathParent.parentNode != null);
			
			path.add(0, start);
			
			return path;
		}
		else if(start.openList.size() > 0 && start.closeList.size() > 0)
		{
			path = new ArrayList<Node>();
			Node pathParent = start.closeList.get(start.closeList.size() - 1);
			while(pathParent.parentNode != null)
			{
				path.add(0, pathParent);
				pathParent = pathParent.parentNode;
			}
			
			path.add(0, start);
			
			return path;
		}
		else if(start.openList.isEmpty())
			return path = new ArrayList<Node>();
		else return null;
	}
	
	public Node getParentNode(ArrayList<Node> openList)
	{
		// If there are no other nodes other than the start node in the open list, select start node itself as parent node.
		// If there are other nodes in the open list, find the one with lowest field value. If two field values are equal, select the last one.
		Node parent = openList.get(openList.size() - 1);
		
		for(Node node : openList)
			if(node.fieldValue <= parent.fieldValue)
				if(node.hueValue <= parent.hueValue)
					parent = node;
		
		return parent;
	}
	
	public ArrayList<Node> getAdjacent(int[][] terrain, Node parent, ArrayList<Node> closedList)
	{
		// For each surrounding node, if it is obstruct or impassable via parent node, exclude it from the list.
		// If surrounding node is already in the closed list, exclude it as well.
		ArrayList<Node> adjacent = new ArrayList<Node>();
		
		for(int x = parent.x - 1; x <= parent.x + 1; x++)
			for(int y = parent.y - 1; y <= parent.y + 1; y++)
			{
				Node test = new Node();
				test.x = x; test.y = y;
				if(!compareNode(parent, test))
					if(!isInList(test, closedList) && !isObstruct(terrain, test))
					{
						test.parentNode = parent;
						adjacent.add(test);
					}
			}
		
		return adjacent;
	}
	
	public boolean isInList(Node target, ArrayList<Node> list)
	{
		for(Node node : list)
			if(compareNode(target, node))
				return true;
		
		return false;
	}
	
	public Node getNodeFromList(Node target, ArrayList<Node> list)
	{
		for(Node node : list)
			if(compareNode(target, node))
				return node;
		
		return null;
	}
	
	public boolean compareNode(Node a, Node b)
	{
		if(a != null && b != null)
			if(a.x == b.x && a.y == b.y)
				return true;
		
		return false;
	}
	
	public boolean addNode(Node target, ArrayList<Node> list)
	{
		for(Node node : list)
			if(compareNode(target, node))
				return true;
		
		list.add(target);
		return false;
	}
	
	public boolean removeNode(Node target, ArrayList<Node> list)
	{	
		list.remove(target);
		
		return false;
	}
	
	public float getGoalValue(Node a, Node b)
	{
		float dX = b.x - a.x;
		float dY = b.y - a.y;
		
		float distance = Math.round(Math.sqrt(Math.abs(dX) + Math.abs(dY)));
		
		if(dX != 0 && dY != 0)
			return 14 * distance;
		else return 10 * distance;
	}
	
	public float getManhattanHueValue(Node target, Node goal)
	{
		float dX = Math.abs(goal.x - target.x);
		float dY = Math.abs(goal.y - target.y);
		
		return 10 * (dX + dY);
	}
	
	public float getManhattan2HueValue(Node target, Node goal)
	{
		float dX = Math.abs(goal.x - target.x);
		float dY = Math.abs(goal.y - target.y);
		
		if(dX > dY)
			return (14 * dY) + 10 * (dX - dY);
		else
			return (14 * dX) + 10 * (dY - dX);
	}
	
	public float getDiagonalHueValue(Node target, Node goal)
	{
		float dX = Math.abs(goal.x - target.x);
		float dY = Math.abs(goal.y - target.y);
		
		return 10 * (dX + dY) + (14 - 2 * 10) * Math.min(dX, dY);
	}
	
	public float getEuclideanHueValue(Node target, Node goal)
	{
		float dX = Math.abs(goal.x - target.x);
		float dY = Math.abs(goal.y - target.y);
		
		return 10 * (float) Math.sqrt(dX * dX + dY * dY);
	}
	
	public boolean isObstruct(int[][] terrain, Node target)
	{
		if(target.x < 0 || target.y < 0)
			return true;
		else if(target.x > terrain.length - 1 || target.y > terrain[0].length - 1)
			return true;
		else if(terrain[target.x][target.y] == Terrain.WATER)
			return true;
		
		else return false;
	}
	
	private class JumpPointSearch
	{
		// Method jump point search returns a node from selected node
		public ArrayList<Node> getSuccessors(int[][] terrain, Node parent, Node start, Node goal)
		{
			ArrayList<Node> neighbors = new ArrayList<Node>();
			if(parent.parentNode == null)
				neighbors = getAdjacent(terrain, parent, start.closeList);
			else if(parent.parentNode != null)
				neighbors = getDirectionalNodes(terrain, parent);
			
			//System.out.println("Found total " + neighbors.size() + " neighbors");
			
			ArrayList<Node> successors = new ArrayList<Node>();
			for(Node node : neighbors)
			{
				if(!isInList(node, start.closeList) && !isInList(node, start.openList))
				{
					int dX = getDirection(parent.x, node.x);
					int dY = getDirection(parent.y, node.y);
					Node successor = jumpPointSearch(terrain, node, goal, dX, dY);
					
					if(successor != null)
						successors.add(successor);
				}
			}
			
			return successors;
		}
		
		private Node jumpPointSearch(int[][] terrain, Node test, Node goal, int dX, int dY)
		{
			/*System.out.println("Testing " + test.x + " " + test.y);
			System.out.println("Goal Node : " + goal.x + " " + goal.y);*/
			if(isObstruct(terrain, test))
				return null;
			
			if(compareNode(test, goal))
				return test;
			
			if(dX != 0 && dY != 0)
			{
				if(!checkForcedNeighbors(terrain, test, dX, dY).isEmpty())
					return test;
				
				// Fix this so the diagonal fn check will be continuous
				if(diagonalCheck(terrain, test, goal, dX, 0) != null || diagonalCheck(terrain, test, goal, 0, dY) != null)
					return test;
			}
			else
			{
				if(dX != 0)
					if(!checkForcedNeighbors(terrain, test, dX, 0).isEmpty())
						return test;
				if(dY != 0)
					if(!checkForcedNeighbors(terrain, test, 0, dY).isEmpty())
						return test;
			}
			
			test.x += dX; test.y += dY;
			//System.out.println("Moving to next node");
			return jumpPointSearch(terrain, test, goal, dX, dY);
		}
		
		private Node diagonalCheck(int[][] terrain, Node test, Node goal, int dX, int dY)
		{
			if(isObstruct(terrain, test))
				return null;
			
			if(compareNode(test, goal))
				return test;
			
			if(!checkForcedNeighbors(terrain, test, dX, dY).isEmpty())
				return test;
			
			Node next = new Node();
			next.parentNode = test.parentNode;
			next.x = test.x + dX; next.y = test.y + dY;
			return diagonalCheck(terrain, next, goal, dX, dY);
		}
		
		private ArrayList<Node> getDirectionalNodes(int[][] terrain, Node parent)
		{
			int dX = getDirection(parent.parentNode.x, parent.x);
			int dY = getDirection(parent.parentNode.y, parent.y);
			
			ArrayList<Node> neighbors = new ArrayList<Node>();
			
			if(dX != 0 && dY != 0)
			{
				Node test = new Node();
				test.parentNode = parent;
				test.x = parent.x + dX; test.y = parent.y;
				if(!isObstruct(terrain, test))
					neighbors.add(test);
				
				test = new Node();
				test.parentNode = parent;
				test.x = parent.x; test.y = parent.y + dY;
				if(!isObstruct(terrain, test))
					neighbors.add(test);
				
				test = new Node();
				test.parentNode = parent;
				test.x = parent.x + dX; test.y = parent.y + dY;
				if(!isObstruct(terrain, test))
					neighbors.add(test);
				
				for(Node node : checkForcedNeighbors(terrain, parent, dX, dY))
				{
					node.parentNode = parent;
					neighbors.add(node);
				}
			}
			else
			{	
				if(dX != 0)
				{
					Node test = new Node();
					test.parentNode = parent;
					test.x = parent.x + dX; test.y = parent.y;
					if(!isObstruct(terrain, test))
						neighbors.add(test);
					
					for(Node node : checkForcedNeighbors(terrain, parent, dX, 0))
					{
						node.parentNode = parent;
						neighbors.add(node);
					}
				}
				if(dY != 0)
				{
					Node test = new Node();
					test.parentNode = parent;
					test.x = parent.x; test.y = parent.y + dY;
					if(!isObstruct(terrain, test))
						neighbors.add(test);
					
					for(Node node : checkForcedNeighbors(terrain, parent, 0, dY))
					{
						node.parentNode = parent;
						neighbors.add(node);
					}
				}
			}
			
			return neighbors;
		}
		
		private ArrayList<Node> checkForcedNeighbors(int[][] terrain, Node test, int dX, int dY)
		{
			ArrayList<Node> forcedNeighbors = new ArrayList<Node>();
			
			if(dX != 0 && dY != 0)
			{
				// Diagonal neighbor check
				Node neighbor;
				Node hNode = new Node();
				hNode.x = test.x - dX; hNode.y = test.y;
				Node vNode = new Node();
				vNode.x = test.x; vNode.y = test.y - dY;
				
				if(isObstruct(terrain, hNode))
				{
					neighbor = new Node();
					neighbor.x = hNode.x; neighbor.y = hNode.y + dY;
					if(!isObstruct(terrain, neighbor))
						forcedNeighbors.add(neighbor);
				}
				else if(isObstruct(terrain, vNode))
				{
					neighbor = new Node();
					neighbor.x = vNode.x + dX; neighbor.y = vNode.y;
					if(!isObstruct(terrain, neighbor))
						forcedNeighbors.add(neighbor);
				}
			}
			else
			{
				if(dX != 0)
				{
					// Horizontal neighbor check
					Node neighbor;
					Node side = new Node();
					side.x = test.x; side.y = test.y + 1;
					if(isObstruct(terrain, side))
					{
						neighbor = new Node();
						neighbor.parentNode = test;
						neighbor.x = test.x + dX; neighbor.y = side.y;
						if(!isObstruct(terrain, neighbor))
							forcedNeighbors.add(neighbor);
					}
					
					side = new Node();
					side.x = test.x; side.y = test.y - 1;
					if(isObstruct(terrain, side))
					{
						neighbor = new Node();
						neighbor.parentNode = test;
						neighbor.x = test.x + dX; neighbor.y = side.y;
						if(!isObstruct(terrain, neighbor))
							forcedNeighbors.add(neighbor);
					}
				}
				if(dY != 0)
				{
					// Vertical neighbor check
					Node side = new Node();
					Node neighbor;
					side.x = test.x + 1; side.y = test.y;
					if(isObstruct(terrain, side))
					{
						neighbor = new Node();
						neighbor.parentNode = test;
						neighbor.x = side.x; neighbor.y = test.y + dY;
						if(!isObstruct(terrain, neighbor))
							forcedNeighbors.add(neighbor);
					}
					
					side = new Node();
					side.x = test.x - 1; side.y = test.y;
					if(isObstruct(terrain, side))
					{
						neighbor = new Node();
						neighbor.parentNode = test;
						neighbor.x = side.x; neighbor.y = test.y + dY;
						if(!isObstruct(terrain, neighbor))
							forcedNeighbors.add(neighbor);
					}
				}
			}
			
			//System.out.println("Added " + forcedNeighbors.size() + " fns");
			
			return forcedNeighbors;
		}
		
		public int getDirection(int from, int to)
		{
			if((to - from) > 0)
				return 1;
			else if((to - from) < 0)
				return -1;
			
			return 0;
		}
	}
}
