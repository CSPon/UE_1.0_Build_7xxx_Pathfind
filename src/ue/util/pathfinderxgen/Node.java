/**
 * Node is specifically used for path finding system.
 */

package ue.util.pathfinderxgen;

import java.util.ArrayList;

public class Node
{
	public int x = -1;
	public int y = -1;
	
	// Some values for basic path finding system
	public float goalValue;
	public float hueValue;
	public float fieldValue;
	
	public Node parentNode;
	public Node forcedNeighbor1;
	public Node forcedNeighbor2;
	
	// These lists are to be used with start node ONLY
	public ArrayList<Node> openList;
	public ArrayList<Node> closeList;
	
	public boolean isObstruct;
}
