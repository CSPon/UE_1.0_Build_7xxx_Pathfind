package ue.game.world.tile;

import org.lwjgl.util.vector.Vector3f;

import ue.game.world.tile.terrain.Terrain;

public class Tile
{
	protected float xPos, yPos, height, seed;
	
	private Vector3f[] vertices, border;
	
	private Terrain terrainType;
	
	private boolean flat;
	
	public Tile(float xPos, float yPos, float height, float radius)
	{
		vertices = new Vector3f[4];
		border = new Vector3f[4];
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.height = height;
		seed = 0;
		
		vertices = new Vector3f[4];
		
		vertices[0] = new Vector3f(-radius, -radius, 0);
		vertices[1] = new Vector3f(radius, -radius, 0);
		vertices[2] = new Vector3f(radius, radius, 0);
		vertices[3] = new Vector3f(-radius, radius, 0);
		
		border = new Vector3f[8];
		
		border[0] = new Vector3f(-radius, -radius, 0); border[1] = new Vector3f(radius, -radius, 0);
		border[2] = new Vector3f(radius, -radius, 0); 	border[3] = new Vector3f(radius, radius, 0);
		border[4] = new Vector3f(radius, radius, 0); 	border[5] = new Vector3f(-radius, radius, 0);
		border[6] = new Vector3f(-radius, radius, 0);  border[7] = new Vector3f(-radius, -radius, 0);
		
		for(Vector3f vertex : vertices)
			vertex.translate(xPos, yPos, 0);
		
		for(Vector3f vertex : border)
			vertex.translate(xPos, yPos, 0);
	}
	
	public void createVertices(float radius)
	{
		vertices = new Vector3f[4];
		
		vertices[0] = new Vector3f(-radius, -radius, 0);
		vertices[1] = new Vector3f(radius, -radius, 0);
		vertices[2] = new Vector3f(radius, radius, 0);
		vertices[3] = new Vector3f(-radius, radius, 0);
		
		border = new Vector3f[8];
		
		border[0] = new Vector3f(-radius, -radius, 0); border[1] = new Vector3f(radius, -radius, 0);
		border[2] = new Vector3f(radius, -radius, 0); 	border[3] = new Vector3f(radius, radius, 0);
		border[4] = new Vector3f(radius, radius, 0); 	border[5] = new Vector3f(-radius, radius, 0);
		border[6] = new Vector3f(-radius, radius, 0);  border[7] = new Vector3f(-radius, -radius, 0);
		
		height = 0;
	}
	
	public float[] getVerticesArray()
	{
		return new float[]{vertices[0].getX(), vertices[0].getY(), vertices[0].getZ(),
				vertices[1].getX(), vertices[1].getY(), vertices[1].getZ(),
				vertices[2].getX(), vertices[2].getY(), vertices[2].getZ(),
				vertices[3].getX(), vertices[3].getY(), vertices[3].getZ()};
	}
	
	public float[] getBoderlinesArray()
	{
		return new float[]{border[0].getX(), border[0].getY(), border[0].getZ(),
				border[1].getX(), border[1].getY(), border[1].getZ(),
				border[2].getX(), border[2].getY(), border[2].getZ(),
				border[3].getX(), border[3].getY(), border[3].getZ(),
				border[4].getX(), border[4].getY(), border[4].getZ(),
				border[5].getX(), border[5].getY(), border[5].getZ(),
				border[6].getX(), border[6].getY(), border[6].getZ(),
				border[7].getX(), border[7].getY(), border[7].getZ()};
	}
	
	public void cleanUp()
	{
		vertices = null; border = null;
		terrainType = null;
	}
	
	public float getX(){return xPos;}
	public float getY(){return yPos;}
	
	public void setX(float xPos){this.xPos = xPos;}
	public void setY(float yPos){this.yPos = yPos;}
	
	public float getHeight(){return height;}
	public void setHeight(float height){this.height = height;}
	
	public float getSeed(){return this.seed;}
	public void setSeed(float seed){this.seed = seed;}
	
	public Terrain getTerrainType(){return this.terrainType;}
	public void setTerrainType(Terrain terrainType){this.terrainType = terrainType;}
	
	public boolean isFlat(){return flat;}
	public void setFlat(boolean flat){this.flat = flat;}
}
