package ue.game.world;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import ue.game.world.chunk.Chunk;
import ue.game.world.region.Region;
import ue.game.world.tile.Tile;

public class UWorld
{
	private Chunk[][] world;
	private Region[][] world2;
	
	// created : if chunks are created (empty).
	// pushed : if chunk VBO IDs are created/pushed.
	// loaded : if world generation is completed.
	private boolean created, aligned, pushed, loaded;
	
	// rowCount : current count of row.
	// colCount : current counf of col
	private int rowCount, colCount;
	
	private int max_terrain_level = 9;
	
	public UWorld(long seed, int size)
	{
		new MapWizard(seed);
		
		world2 = new Region[size][size];
		world = new Chunk[size][size];
	}
	
	public void createWorld(int radius, int octave, float persistance)
	{
		if(!created)
		{
			float[][] rawSeed = generateNoiseMap(rowCount, colCount, octave, persistance);
			
			/*for(int x = 0; x < rawSeed.length; x++)
				for(int y = 0; y < rawSeed[x].length; y++)
					rawSeed[x][y] = 0.5f;*/
			
			//world[rowCount][colCount] = new Chunk(rowCount, colCount);
			//world[rowCount][colCount].createTiles(radius, rawSeed);
			
			world2[rowCount][colCount] = new Region(rowCount, colCount);
			world2[rowCount][colCount].create(radius, rawSeed);
			
			colCount++;
			if(colCount > world.length - 1)
			{
				rowCount++;
				if(rowCount > world.length - 1)
				{
					created = true;
					rowCount = 0;
					colCount = 0;
				}
				else colCount = 0;
			}
		}
		else if(!aligned)
		{
			//world[rowCount][colCount].alignTiles(max_terrain_level);
			
			world2[rowCount][colCount].alignTiles(max_terrain_level, 0.4f);
			
			colCount++;
			if(colCount > world.length - 1)
			{
				rowCount++;
				if(rowCount > world.length - 1)
				{
					aligned = true;
					rowCount = 0;
					colCount = 0;
				}
				else colCount = 0;
			}
		}
		else if(!pushed)
		{
			FloatBuffer buffer = BufferUtils.createFloatBuffer(Chunk.MAX_SIZE * Chunk.MAX_SIZE * 3 * 4 * 5 * 3);
			
			//world[rowCount][colCount].pushVBO(buffer);
			
			world2[rowCount][colCount].pushVBO(buffer);
			
			colCount++;
			if(colCount > world.length - 1)
			{
				rowCount++;
				if(rowCount > world.length - 1)
				{
					pushed = true;
					rowCount = 0;
					colCount = 0;
				}
				else colCount = 0;
			}
		}
		else loaded = true;
	}
	
	public float[][] generateNoiseMap(int row, int col, int octave, float persistance)
	{
		float[][] rawSeed = MapWizard.generateWhiteNoise(Region.MAX_SIZE * Chunk.MAX_SIZE, Region.MAX_SIZE * Chunk.MAX_SIZE);
		
		if((row - 1) >= 0 && (row - 1) < world.length) // Upper bound
		{
			if(world[row - 1][col] != null)
				for(int j = 0; j < Chunk.MAX_SIZE; j++)
					rawSeed[0][j] = world[row - 1][col].getEdgeSeed(Chunk.SOUTH)[j];
		}
		if((row + 1) >= 0 && (row + 1) < world.length) // Lower bound
		{
			if(world[row + 1][col] != null)
				for(int j = 0; j < Chunk.MAX_SIZE; j++)
					rawSeed[15][j] = world[row + 1][col].getEdgeSeed(Chunk.NORTH)[j];
		}
		if((col - 1) >= 0 && (col - 1) < world.length) // Left bound
		{
			if(world[row][col - 1] != null)
				for(int i = 0; i < Chunk.MAX_SIZE; i++)
					rawSeed[i][0] = world[row][col - 1].getEdgeSeed(Chunk.EAST)[i];
		}
		if((col + 1) >= 0 && (col + 1) < world.length) // Right bound
		{
			if(world[row][col + 1] != null)
				for(int i = 0; i < Chunk.MAX_SIZE; i++)
					rawSeed[i][15] = world[row][col - 1].getEdgeSeed(Chunk.WEST)[i];
		}
		
		/*if(row != 0 || col != 0)
		{
			for(int j = 0; j < Chunk.MAX_SIZE; j++)
				rawSeed[15][j] = world[0][0].getEdgeSeed(Chunk.NORTH)[j];
			for(int i = 0; i < Chunk.MAX_SIZE; i++)
				rawSeed[i][15] = world[0][0].getEdgeSeed(Chunk.WEST)[i];
		}*/
		
		rawSeed = MapWizard.generatePerlinNoise(rawSeed, octave, persistance);
		
		return rawSeed;
	}
	
	public void render(float xOff, float yOff, int radius, int texture, boolean border)
	{
		for(Region[] row : world2)
			for(Region region : row)
				region.render(xOff, yOff, radius, texture, border);
		
		/*for(Chunk[] row : world)
			for(Chunk chunk : row)
				chunk.render(xOff, yOff, radius, texture, border);*/
	}
	
	public Tile getTileAt(int row, int col)
	{
		int regionRow = (int) Math.floor(row / (Chunk.MAX_SIZE * Region.MAX_SIZE));
		int regionCol = (int) Math.floor(col / (Chunk.MAX_SIZE * Region.MAX_SIZE));
		
		return world2[regionRow][regionCol].getTileAt(row, col);
	}
	
	public void clean()
	{
		for(Region[] row : world2)
			for(Region region : row)
				region.clean();
		
		/*for(Chunk[] row : world)
			for(Chunk chunk : row)
				chunk.clean();*/
	}
	
	public int getWorldSize()
	{
		return (world2.length * Region.MAX_SIZE) * Chunk.MAX_SIZE;
	}
	
	public boolean isWorldLoaded(){return loaded;}
}
