package ue.util.game;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

import ue.UniformEngine;
import ue.game.unit.PFDemo;
import ue.game.unit.Player;
import ue.game.world.UWorld;
import ue.util.tex.UTexture;

/**
 * TestBuild creates one world, either isometric or top-down view, then user can interact with basic map movement.
 * @author Charlie Shin
 *
 */
public class TestBuild extends UniformEngine
{
	private String devstat = "";
	
	private UTexture textures;
	
	private UWorld world;
	
	private Player camera;
	
	private boolean devmode;
	
	private boolean border;
	
	private boolean paused;
	
	private PFDemo pfd;
	
	private int globalRadius = 2;
	
	public static void main(String[] args)
	{
		new TestBuild(args);
	}
	
	public TestBuild(String[] args)
	{
		init(args);
		create();
		
		border = true;
		textures = new UTexture(); textures.targetTexturePack("lib/tex/", "texturepack", "png", 16);
		textures.loadFont();
		camera = new Player(0, 0);
		world = new UWorld(0L, 1);
		pfd = new PFDemo(10);
		
		run();
	}

	@Override
	public void cleanup()
	{
		textures.cleanup();
		world.clean();
	}

	@Override
	public void keyboard()
	{
		camera.translate(getInput().getKeyboardX() * 10, getInput().getKeyboardY() * 10);
		
		while(getInput().hasKeyNext())
		{
			if(getInput().getKeys()[Keyboard.KEY_SPACE])
				if(!paused)
					paused = true;
				else paused = false;
			if(getInput().getKeys()[Keyboard.KEY_ESCAPE])
				stop();
			if(getInput().getKeys()[Keyboard.KEY_G])
				if(!border)
					border = true;
				else border = false;
			if(getInput().getKeys()[Keyboard.KEY_F11])
				if(!devmode)
					devmode = true;
				else devmode = false;
		}
	}

	@Override
	public void mouse()
	{
	}

	@Override
	public void update()
	{
		if(!paused)
		{
			if(!world.isWorldLoaded())
			{
				world.createWorld(globalRadius, 5, 0.3f);
				if(world.isWorldLoaded())
					pfd.placeUnits(world.getWorldSize(), world);
			}
			else if(world.isWorldLoaded())
				pfd.update(world.getWorldSize(), world);
			
			devstat = "*Developer Mode(Demonstration)*\n" +
					"" + getFPS() + "\n" + 
					"" + getUsage() + "\n";
			if(world.isWorldLoaded())
				devstat += "" + pfd.devMode();
		}
	}

	@Override
	public void render()
	{
		if(world.isWorldLoaded())
		{
			world.render(camera.getX(), camera.getY(), globalRadius, textures.getTexturePack(), border);
			pfd.render(globalRadius, camera.getX(), camera.getY());
		}
		
		renderPointer(1.0f, 1.0f, 1.0f);
		
		glColor3f(1.0f, 1.0f, 1.0f);
		drawString(textures.getFont(), devstat, 5, 5, 16);
	}

	@Override
	public void logic()
	{
		update();
	}
}
