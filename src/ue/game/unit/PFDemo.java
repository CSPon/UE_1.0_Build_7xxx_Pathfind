package ue.game.unit;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Random;

import ue.game.world.UWorld;
import ue.game.world.tile.terrain.Terrain;
import ue.util.pathfinderxgen.*;

public class PFDemo
{
	private static final int NODE_THRESHOLD = 2500;
	private int[][] level;
	
	private NodeUnit[] units;
	
	private PathFinder pathFinder;
	
	public PFDemo(int numtest)
	{
		units = new NodeUnit[numtest];
		
		pathFinder = new PathFinder();
	}
	
	public void placeUnits(int limit, UWorld world)
	{	
		level = new int[world.getWorldSize()][world.getWorldSize()];
		
		for(int i = 0; i < level.length; i++)
			for(int j = 0; j < level[i].length; j++)
				level[i][j] = world.getTileAt(i, j).getTerrainType().getID();
		
		for(int i = 0; i < units.length; i++)
		{
			boolean posfound = false;
			while(!posfound)
			{
				int row = new Random().nextInt(limit);
				int col = new Random().nextInt(limit);
				
				/*int row = 255;
				int col = 255;*/
				
				if(world.getTileAt(row, col).getTerrainType().getID() == Terrain.WATER || world.getTileAt(row, col).getTerrainType().getID() == Terrain.GRASS)
					posfound = false;
				else
				{
					units[i] = new NodeUnit(row, col, pathFinder);
					posfound = true;
				}
			}
		}
	}
	
	public void render(int radius, float xOff, float yOff)
	{
		for(NodeUnit node : units)
			node.render(radius, xOff, yOff);
	}
	
	public void update(int limit, UWorld world)
	{
		for(NodeUnit node : units)
			node.update(limit, world);
	}
	
	public String devMode()
	{
		int percent = 0;
		int avg = 0;
		
		if(units[0].succeeded != 0)
		{
			percent = (int) (((float) units[0].succeeded / (float) (units[0].succeeded + units[0].failed)) * 100);
			avg = (int) ((float) units[0].avgNode / (float) units[0].succeeded);
		}
		
		if(units[0].startNode.openList != null && units[0].startNode.closeList != null)
			return "Target Node : " + units[0].endNode.x + "," + units[0].endNode.y + "\n" + 
			"Open List ; " + units[0].startNode.openList.size() + " Close List : " + units[0].startNode.closeList.size() + "\n" + 
			" Succeeded : " + units[0].succeeded + " Failed : " + units[0].failed + " (" + percent + "%)\n" +
			" Average Node : " + avg;
		
		else return "NO DATA";
	}
	
	private class NodeUnit
	{
		private int tick, succeeded, failed, avgNode;
		
		private Node startNode, endNode;
		private ArrayList<Node> path;
		private boolean hasPath;
		
		private int nodeRadius = 2;
		
		private PathFinder pathFinder;
		
		NodeUnit(int row, int colum, PathFinder pathFinder)
		{
			this.pathFinder = pathFinder;
			
			//startNode = new Node(250,50);
			startNode = new Node();
			startNode.x = row; startNode.y = colum;
			path = null;
		}
		
		public void update(int limit, UWorld world)
		{
			if(succeeded < 1000)
			{
				tick++;
				if(tick > 0)
				{
					tick = 0;
					
					if(!hasPath)
					{
						if(endNode == null || pathFinder.compareNode(startNode, endNode))
							pickEndNode(limit, world);
						
						searchPath(world);
						
						if(startNode.closeList.size() > NODE_THRESHOLD)
						{
							System.out.println("Path not found : Maximum threshhold reached");
							hasPath = false;
							startNode.openList = null;
							startNode.closeList = null;
							endNode = null;
							path = null;
							failed++;
						}
						if(path != null)
						{
							/*if(PathFinder.compareNode(endNode, path.get(path.size() - 1)))
								hasPath = true;*/
							if(!path.isEmpty() && pathFinder.isInList(endNode, startNode.closeList))
							{
								System.out.println("Path found");
								hasPath = true;
								avgNode += startNode.closeList.size();
								succeeded++;
								startNode.openList.clear();
								startNode.closeList.clear();
							}
							else if(path.isEmpty())
							{
								System.out.println("Path not found : No path to target");
								hasPath = false;
								startNode.openList = null;
								startNode.closeList = null;
								endNode = null;
								path = null;
								failed++;
							}
						}
					}
					else
					{
						if(path.isEmpty() && pathFinder.compareNode(startNode, endNode))
						{
							hasPath = false;
							endNode = null;
							startNode.openList = null;
							startNode.closeList = null;
							path = null;
						}
						else
						{
							Node nextNode = path.get(0);
							startNode.x = nextNode.x;
							startNode.y = nextNode.y;
							
							path.remove(path.get(0));
						}
					}
				}
			}
		}
		
		public void pickEndNode(int limit, UWorld world)
		{
			boolean posfound = false;
			
			while(!posfound)
			{
				int row = new Random().nextInt(limit);
				int col = new Random().nextInt(limit);
				
				if(world.getTileAt(row, col).getTerrainType().getID() == Terrain.WATER || world.getTileAt(row, col).getTerrainType().getID() == Terrain.GRASS)
					posfound = false;
				else
				{
					endNode = new Node();
					endNode.x = row;
					endNode.y = col;
					posfound = true;
				}
				
				/*endNode = new Node(0, 15);
				posfound = true;*/
			}
		}
		
		public void searchPath(UWorld world)
		{
			path = pathFinder.findAJPSPath(level, startNode, endNode);
			//path = PathFinder.getPath4(level, openList, closeList, startNode, endNode);
		}
		
		public void render(int radius, float xOff, float yOff)
		{
			if(startNode.openList != null)
				for(Node node : startNode.openList)
				{
					glLoadIdentity();
					glTranslatef((node.x * radius * 2) - xOff + radius, (node.y * radius * 2) - yOff + radius, 0);
					
					glColor3f(0.0f, 1.0f, 0.0f);
					
					glBegin(GL_QUADS);
					{
						glVertex2i(0, -nodeRadius);
						glVertex2i(nodeRadius, 0);
						glVertex2i(0, nodeRadius);
						glVertex2i(-nodeRadius, 0);
					}
					glEnd();
				}
			
			if(startNode.closeList != null)
				for(Node node : startNode.closeList)
				{
					glLoadIdentity();
					glTranslatef((node.x * radius * 2) - xOff + radius, (node.y * radius * 2) - yOff + radius, 0);
					
					glColor3f(1.0f, 1.0f, 0.0f);
					
					glBegin(GL_QUADS);
					{
						glVertex2i(0, -nodeRadius);
						glVertex2i(nodeRadius, 0);
						glVertex2i(0, nodeRadius);
						glVertex2i(-nodeRadius, 0);
					}
					glEnd();
				}
			
			if(path != null)
			{
				for(Node node : path)
				{
					glLoadIdentity();
					glTranslatef((node.x * radius * 2) - xOff + radius, (node.y * radius * 2) - yOff + radius, 0);
					
					glColor3f(1.0f, 1.0f, 0.0f);
					
					glBegin(GL_QUADS);
					{
						glVertex2i(0, -nodeRadius);
						glVertex2i(nodeRadius, 0);
						glVertex2i(0, nodeRadius);
						glVertex2i(-nodeRadius, 0);
					}
					glEnd();
				}
				
				for(int i = 0; i < path.size() - 1; i++)
				{
					glLoadIdentity();
					glTranslatef((path.get(i).x * radius * 2) - xOff + radius, (path.get(i).y * radius * 2) - yOff + radius, 0);
					
					int x1 = (int) ((path.get(i).x * radius * 2) - xOff + radius);
					int y1 = (int) ((path.get(i).y * radius * 2) - yOff + radius); 
					
					int x2 = (int) ((path.get(i + 1).x * radius * 2) - xOff + radius);
					int y2 = (int) ((path.get(i + 1).y * radius * 2) - yOff + radius);
					
					glColor3f(1.0f, 0.0f, 1.0f);
					
					glBegin(GL_LINES);
					{
						glVertex2i(0, 0);
						glVertex2i(x2 - x1, y2 - y1);
					}
					glEnd();
				}
			}
			
			glLoadIdentity();
			glTranslatef((startNode.x * radius * 2) - xOff + radius, (startNode.y * radius * 2) - yOff + radius, 0);
			
			glColor3f(0.0f, 0.0f, 1.0f);
			
			glBegin(GL_QUADS);
			{
				glVertex2i(0, -nodeRadius);
				glVertex2i(nodeRadius, 0);
				glVertex2i(0, nodeRadius);
				glVertex2i(-nodeRadius, 0);
			}
			glEnd();
			
			if(endNode != null)
			{
				glLoadIdentity();
				glTranslatef((endNode.x * radius * 2) - xOff + radius, (endNode.y * radius * 2) - yOff + radius, 0);
				
				glColor3f(1.0f, 0.0f, 0.0f);
				
				glBegin(GL_QUADS);
				{
					glVertex2i(0, -nodeRadius);
					glVertex2i(nodeRadius, 0);
					glVertex2i(0, nodeRadius);
					glVertex2i(-nodeRadius, 0);
				}
				glEnd();
			}
		}
	}
}
