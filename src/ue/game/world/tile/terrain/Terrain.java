package ue.game.world.tile.terrain;

import java.io.Serializable;

public abstract class Terrain implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final byte VOID = -1;
	public static final byte AIR = 0;
	public static final byte WATER = 1;
	public static final byte SAND = 2;
	public static final byte DIRT = 3;
	public static final byte GRASS = 4;
	
	/**
	 * Gets texture vertex position data as an array.
	 * @return An array of floats containing texture vertex position.
	 */
	public abstract float[] getTextures();
	/**
	 * Gets color data as an array.
	 * @return An array of floats containing color data.
	 */
	public abstract float[] getColors();
	/**
	 * Gets Blocks's ID.
	 * @return Integer value of this block object.
	 */
	public abstract int getID();
}
