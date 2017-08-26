package ue.game.world.tile.terrain;

public class Void extends Terrain
{
	private static final long serialVersionUID = 1L;
	
	private final float coordX = (1f % 16f) * 0.0625f;
	private float coordY = (float) (Math.floor(1f / 16f) * 0.0625f);
	
	private final float[] textures = {coordX, coordY, 0.0f, 
			coordX + 0.0625f, coordY, 0.0f,
			coordX + 0.0625f, coordY + 0.0625f, 0.0f,
			coordX, coordY + 0.0625f, 0.0f};
	
	private float[] colors;
	
	public Void(float seed)
	{	
		colors = new float[12];
		
		for(int i = 0; i < colors.length; i++)
			colors[i] = 1.0f * seed;
	}
	
	@Override
	public float[] getTextures()
	{
		return this.textures;
	}

	@Override
	public float[] getColors()
	{
		return this.colors;
	}

	@Override
	public int getID()
	{
		return Terrain.VOID;
	}
}
