package ue.game.world.chunk;

import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import ue.game.world.tile.terrain.*;
import ue.game.world.tile.terrain.Void;
import ue.game.world.tile.Tile;

public class Chunk
{
	public static final int MAX_SIZE = 16;
	
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;
	
	private Tile[][] chunk;
	
	// Position references
	private int chunkRow, chunkCol; // cartesian row and chunk position of the chunk
	
	private int chunkBufferID, chunkBorderID;
	
	public Chunk(int chunkRow, int chunkCol)
	{
		chunk = new Tile[MAX_SIZE][MAX_SIZE];
		
		this.chunkRow = chunkRow;
		this.chunkCol = chunkCol;
	}
	
	public void render(float xOff, float yOff, int tileRadius, int texture, boolean borderlines)
	{
		float xPos = chunkRow * (MAX_SIZE * tileRadius * 2);
		float yPos = chunkCol * (MAX_SIZE * tileRadius * 2);
		
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		{
			glLoadIdentity();
			glTranslatef(xPos - xOff, yPos - yOff, 0);
			
			glBindTexture(GL_TEXTURE_2D, texture);
			glBindBuffer(GL_ARRAY_BUFFER, chunkBufferID);
			
			glVertexPointer(3, GL_FLOAT, 12, 0);
			glColorPointer(3, GL_FLOAT, 12, 48);
			glTexCoordPointer(3, GL_FLOAT, 12, 96);
			
			glDrawArrays(GL_QUADS, 0, 12 * MAX_SIZE * MAX_SIZE);
			glBindTexture(GL_TEXTURE_2D, 0);
			
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
		glDisableClientState(GL_VERTEX_ARRAY);
		
		if(borderlines)
			renderBorder();
	}
	
	public void renderBorder()
	{
		glEnableClientState(GL_VERTEX_ARRAY);
		{
			glBindBuffer(GL_ARRAY_BUFFER, chunkBorderID);
			glVertexPointer(3, GL_FLOAT, 12, 0);
			
			glColor3f(0.0f, 0.0f, 0.0f);
			glDrawArrays(GL_LINES, 0, 8 * MAX_SIZE * MAX_SIZE);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		glDisableClientState(GL_VERTEX_ARRAY);
	}
	
	public void createTiles(int radius, float[][] seed)
	{
		for(int i = 0; i < MAX_SIZE; i++)
			for(int j = 0; j < MAX_SIZE; j++)
			{
				int row = (chunkRow * MAX_SIZE) + i;
				int col = (chunkCol * MAX_SIZE) + j;
				
				float xPos = (radius * i * 2) + radius;
				float yPos = (radius * j * 2) + radius;
				
				chunk[i][j] = new Tile(xPos, yPos, 0, radius);
				/*chunk[i][j].setTerrainType(new Void(seed[i][j]));
				chunk[i][j].setSeed(seed[i][j]);*/
				chunk[i][j].setTerrainType(new Void(seed[row][col]));
				chunk[i][j].setSeed(seed[row][col]);
			}
	}
	
	public void alignTiles(int max_terrain_level, float water_mass)
	{
		for(int i = 0; i < MAX_SIZE; i++)
			for(int j = 0; j < MAX_SIZE; j++)
			{
				int max_water_level = (int) (Math.floor(water_mass / (1.0f / (float) max_terrain_level)));
				int level = (int) Math.floor(chunk[i][j].getSeed() / (1.0f / max_terrain_level));
				
				if(level >= 0 && level <= max_water_level)
					chunk[i][j].setTerrainType(new Water());
				else if(level > max_water_level && level <= (max_terrain_level - max_water_level))
					chunk[i][j].setTerrainType(new Dirt());
				else chunk[i][j].setTerrainType(new Grass());
				
				/*chunk[i][j].setTerrainType(new Dirt());*/
			}
	}
	
	public Tile getTileAt(int row, int col)
	{
		return chunk[row][col];
	}
	
	public void pushVBO(FloatBuffer buffer)
	{
		chunkBufferID = glGenBuffers();
		chunkBorderID = glGenBuffers();
		
		buffer.clear();
		
		for(Tile[] row : chunk)
			for(Tile tile : row)
			{
				for(float vertex : tile.getVerticesArray())
					buffer.put(vertex);
				for(float color : tile.getTerrainType().getColors())
					buffer.put(color);
				for(float texture : tile.getTerrainType().getTextures())
					buffer.put(texture);
			}
		
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, chunkBufferID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		buffer.clear();
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		buffer.clear();
		
		for(Tile[] row : chunk)
			for(Tile tile : row)
				for(float vertex : tile.getBoderlinesArray())
					buffer.put(vertex);
		
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, chunkBorderID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		buffer.clear();
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// Clearnup process
		//chunk = null;
	}
	
	public float[] getEdgeSeed(int direction)
	{
		float[] edge = new float[16];
		
		if(direction == NORTH)
			for(int j = 0; j < MAX_SIZE; j++)
				edge[j] = chunk[0][j].getSeed();
		else if(direction == SOUTH)
			for(int j = 0; j < MAX_SIZE; j++)
				edge[j] = chunk[15][j].getSeed();
		else if(direction == EAST)
			for(int i = 0; i < MAX_SIZE; i++)
				edge[i] = chunk[i][15].getSeed();
		else if(direction == WEST)
			for(int i = 0; i < MAX_SIZE; i++)
				edge[i] = chunk[i][0].getSeed();
		
		return edge;
	}
	
	public void clean()
	{
		glDeleteBuffers(chunkBufferID);
		glDeleteBuffers(chunkBorderID);
	}
}
