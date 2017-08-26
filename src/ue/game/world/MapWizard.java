package ue.game.world;

import java.util.Random;

public class MapWizard
{
	private static Random random;
	
	public MapWizard(long seed)
	{
		random = new Random(seed);
	}
	
	/*public static float[][] generate(int x, int y, int octave, float persistance)
	{
		float[][] base = generateNoise(0, x, y);
		
		int width = base.length;
		int height = base[0].length;
	 
		float[][][] smoothNoise = new float[octave][][]; //an array of matrix
	 
		//generate smooth noise
		for (int i = 0; i < octave; i++)
		{
			smoothNoise[i] = generateSmoothNoise(base, i);
		}
	 
	    float[][] perlinNoise = new float[width][height];
	    float amplitude = 1.0f;
	    float totalAmplitude = 0.0f;
	 
	    //blend noise together
	    for (int pass = octave - 1; pass >= 0; pass--)
	    {
	       amplitude *= persistance;
	       totalAmplitude += amplitude;
	 
	       for (int i = 0; i < width; i++)
	       {
	          for (int j = 0; j < height; j++)
	          {
	             perlinNoise[i][j] += smoothNoise[pass][i][j] * amplitude;
	          }
	       }
	    }
	 
	   //Normalization
	   for (int i = 0; i < width; i++)
	   {
	      for (int j = 0; j < height; j++)
	      {
	         perlinNoise[i][j] /= totalAmplitude;
	      }
	   }
	 
	   return perlinNoise;
	}
	
	public static float[][] normalize(float[][] noise, int octave, float persistance)
	{
	}
	
	public static float[][] generateNoises(int octave, float persistance, int width, int height)
	{
		float[][] noise = new float[width][height];
		float[][][] noises = new float[octave][width][height];
		
		for(int pass = 0; pass < octave; pass++)
		{
			noises[pass] = generateNoise(0, width, height);
		}
		
		float amplitude = 1.0f;
	    float totalAmplitude = 0.0f;
	    
	  //blend noise together
	    for (int pass = octave - 1; pass >= 0; pass--)
	    {
	    	float frequency = (float) Math.pow(2, pass);
	       amplitude *= persistance;
	       amplitude = (float) Math.pow(persistance, pass);
	       
	       totalAmplitude += amplitude;
	 
	       for (int i = 0; i < width; i++)
	       {
	          for (int j = 0; j < height; j++)
	          {
	             noise[i][j] += noises[pass][i][j] * amplitude;
	          }
	       }
	    }
	}

	//First generates rough noise.
	public static float[][] generateNoise(int start, int width, int height)
	{
		float[][] noise = new float[width][height];
		
		for(int x = start; x < noise.length; x++)
			for(int y = start; y < noise[x].length; y++)
			{
				noise[x][y] = (float)random.nextDouble() % 1;
			}
		
		return noise;
	}
	
	*//**
	 * Returns smoothed noise using interpolation
	 * @param base base matrix to start with
	 * @param octave number of passes. It is used to calculate frequency and sample period.
	 * @return
	 *//*
	private static float[][] generateSmoothNoise(float[][] base, int octave)
	{
		int width = base.length;
		int height = base[0].length;
		
		float[][] smoothnoise = new float[width][height];
		
		int sample_period = 1 << octave;
		float sample_freq = 1.0f / (float) sample_period;
		
		for(int x = 0;x < width; x++)
		{
			int sample_x0 = (int) (Math.floor(x / sample_period) * sample_period);
			int sample_x1 = (sample_x0 + sample_period) % width;
			float horiz_blend = (x - sample_x0) * sample_freq;
			
			for(int y = 0;y < height; y++)
			{
				int sample_y0 = (int) (Math.floor(y / sample_period) * sample_period);
				int sample_y1 = (sample_y0 + sample_period) % height;
				float vert_blend = (y - sample_y0) * sample_freq;
				
				float top = interpolate(base[sample_x0][sample_y0], base[sample_x1][sample_y0], horiz_blend);
				
				float bottom = interpolate(base[sample_x0][sample_y1], base[sample_x1][sample_y1], horiz_blend);
				
				smoothnoise[x][y] = interpolate(top, bottom, vert_blend);
			}
		}
		
		return smoothnoise;
	}
	
	//This method will use two points to calculate slope.
	private static float interpolate(float x0, float x1, float alpha)
	{
		return x0 * (1 - alpha) + alpha * x1;
	}*/
	
	// Below new
	public static float[][] generateWhiteNoise(int width, int height)
	{
		float[][] noise = new float[width][height];
		
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				noise[i][j] = (float) random.nextDouble() % 1;
		
		return noise;
	}
	
	public static float[][] generatePerlinNoise(float[][] baseNoise, int octaveCount, float persistence)
	{
		int width = baseNoise.length;
		int height = baseNoise[0].length;
		
		float[][][] smoothNoise = new float[octaveCount][][];
		
		//float persistence = 0.5f;
		
		for(int i = 0; i < octaveCount; i++)
			smoothNoise[i] = generateSmoothNoise(baseNoise, i);
		
		float[][] perlinNoise = new float[width][height];
		float amplitude = 1.0f;
		float totalAmplitude = 0.0f;
		
		for(int octave = octaveCount - 1; octave >= 0; octave--)
		{
			amplitude *= persistence;
			totalAmplitude += amplitude;
			
			for(int i = 0; i < width; i++)
				for(int j = 0; j < height; j++)
					perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
		}
		
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				perlinNoise[i][j] /= totalAmplitude;
		
		return perlinNoise;
	}
	
	private static float[][] generateSmoothNoise(float[][] baseNoise, int octave)
	{
		int width = baseNoise.length;
		int height = baseNoise[0].length;
		
		float[][] smoothNoise = new float[width][height];
		
		int samplePeriod = 1 << octave;
		float sampleFrequency = 1.0f / samplePeriod;
		
		for(int i = 0; i < width; i++)
		{
			int sample_i0 = (i / samplePeriod) * samplePeriod;
			int sample_i1 = (sample_i0 + samplePeriod) % width;
			float horizontal_blend = (i - sample_i0) * sampleFrequency;
			
			for(int j = 0; j < height; j++)
			{
				int sample_j0 = (j / samplePeriod) * samplePeriod;
				int sample_j1 = (sample_j0 + samplePeriod) % height;
				float vertical_blend = (j - sample_j0) * sampleFrequency;
				
				float top = interpolate(baseNoise[sample_i0][sample_j0], baseNoise[sample_i1][sample_j0], horizontal_blend);
				float bottom = interpolate(baseNoise[sample_i0][sample_j1], baseNoise[sample_i1][sample_j1], horizontal_blend);
				
				smoothNoise[i][j] = interpolate(top, bottom, vertical_blend);
			}
		}
		
		return smoothNoise;
	}
	
	private static float interpolate(float x0, float x1, float alpha)
	{
		//return (float) (x0 + ((x1 - x1) * ((1.0 + Math.cos((1.0 - alpha) * Math.PI)) * 0.5)));
		return x0 * (1 - alpha) + alpha * x1;
	}
}
