package ue.game.world.region;

import java.nio.FloatBuffer;

import ue.game.world.chunk.Chunk;
import ue.game.world.tile.Tile;

public class Region
{
	public static final int MAX_SIZE = 16; // Default 16
	
	private Chunk[][] region;
	
	private int row, col;
	
	public Region(int row, int col)
	{
		this.row = row; this.col = col;
		
		region = new Chunk[MAX_SIZE][MAX_SIZE];
	}
	
	public void create(int radius, float[][] rawSeed)
	{
		for(int i = 0; i < MAX_SIZE; i++)
			for(int j = 0; j < MAX_SIZE; j++)
			{
				region[i][j] = new Chunk((row * MAX_SIZE) + i, (col * MAX_SIZE) + j);
				region[i][j].createTiles(radius, rawSeed);
			}
	}
	
	public void alignTiles(int max_terrain_level, float water_mass)
	{
		for(Chunk[] row : region)
			for(Chunk chunk : row)
				chunk.alignTiles(max_terrain_level, water_mass);
	}
	
	public void pushVBO(FloatBuffer buffer)
	{
		for(Chunk[] row : region)
			for(Chunk chunk : row)
				chunk.pushVBO(buffer);
	}
	
	public void render(float xOff, float yOff, int tileRadius, int texture, boolean borderlines)
	{
		for(int i = 0; i < MAX_SIZE; i++)
			for(int j = 0; j < MAX_SIZE; j++)
				region[i][j].render(xOff, yOff, tileRadius, texture, borderlines);
	}
	
	public void clean()
	{
		for(Chunk[] row : region)
			for(Chunk chunk : row)
				chunk.clean();
	}
	
	public Tile getTileAt(int row, int col)
	{
		int chunkRow = (int) Math.floor(row / Chunk.MAX_SIZE);
		int chunkCol = (int) Math.floor(col / Chunk.MAX_SIZE);
		
		return region[chunkRow][chunkCol].getTileAt(row - (chunkRow * 16), col - (chunkCol * 16));
	}
}
