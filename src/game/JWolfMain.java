package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import tools.PixelBuffer;
import static tools.ImageLoader.*;
import static tools.Utility.*;

import engine.Keyboard;
import engine.KeyboardState;
import engine.RaycastEngine;
import engine.Sprite;
import engine.Wall;

public class JWolfMain extends RaycastEngine
{
	
	private BufferedImage sky;
	
	//private int mapWidth = 24, mapHeight = 24;
	private int texWidth = 64, texHeight = 64;
	
	private double playerRadius = 5.0;
	
	private float m_fAvgFPS;
	private float fps = 0;
	private int frameCount;
	
	private final int FRAMETOCOUNT = 60;
	
	private List<WallInfo> specialWalls = new ArrayList<WallInfo>();
	
	private int numSprites = 19;
	private Sprite sprites[] = 
			{
				// LIGHTS
				new Sprite() {{ x = 20.5; y = 11.5; tex = 11; }},
				new Sprite() {{ x = 18.5; y = 4.5; 	tex = 11; }},
				new Sprite() {{ x = 10.0; y = 4.5; 	tex = 11; }},
				new Sprite() {{ x = 10.0; y = 12.5; tex = 11; }},
				new Sprite() {{ x = 3.5; y = 6.5; 	tex = 11; }},
				new Sprite() {{ x = 3.5; y = 20.5; 	tex = 11; }},
				new Sprite() {{ x = 3.5; y = 14.5; 	tex = 11; }},
				new Sprite() {{ x = 14.5; y = 20.5; tex = 11; }},
				
				// PILLARS
				new Sprite() {{ x = 18.5; y = 10.5; tex = 10; flags = Sprite.TRANSLUCEND; specialData = 128; }},
				new Sprite() {{ x = 18.5; y = 11.5; tex = 10; }},
				new Sprite() {{ x = 18.5; y = 12.5; tex = 10; }},
				
				// BARRELS
				new Sprite() {{ x = 21.5; y = 1.5; 	tex = 9; }},
				new Sprite() {{ x = 15.5; y = 1.5; 	tex = 9; }},
				new Sprite() {{ x = 16.5; y = 1.5; 	tex = 9; }},
				new Sprite() {{ x = 16.5; y = 1.5; 	tex = 9; }},
				new Sprite() {{ x = 3.5; y = 2.5; 	tex = 9; }},
				new Sprite() {{ x = 9.5; y = 15.5; 	tex = 9; }},
				new Sprite() {{ x = 10.5; y = 15.5; tex = 9; }},
				new Sprite() {{ x = 10.5; y = 15.5; tex = 9; }}
			};
	private int spriteOrder[];
	private double spriteDistance[];
	
	private Wall wallTable[] = 
		{
			new Wall() {{ tex = 0; flags = Wall.SOLID; }},
			new Wall() {{ tex = 1; flags = Wall.SOLID; }},
			new Wall() {{ tex = 2; flags = Wall.SOLID; }},
			new Wall() {{ tex = 3; flags = Wall.SOLID; }},
			new Wall() {{ tex = 4; flags = Wall.SOLID; }},
			new Wall() {{ tex = 5; flags = Wall.SOLID; }},
			new Wall() {{ tex = 6; flags = Wall.SOLID; }},
			new Wall() {{ tex = 7; flags = Wall.SOLID; }},
			new Wall() {{ tex = 8; flags = Wall.TRANSLUCEND | Wall.SOLID | Wall.THIN; specialData = 190; }}
		};
	
	private PixelBuffer textures[];
	private int buffer[][];
	private double zBuffer[];
	
	private int worldMap[][] = new int[][]
		{
			{8,8,8,8,8,8,8,8,8,8,8,4,4,6,4,4,6,4,6,4,4,4,6,4},
			{8,0,0,0,0,0,0,0,0,0,8,4,0,0,0,0,0,0,0,0,0,0,0,4},
			{8,0,3,3,0,0,0,0,0,8,8,4,0,0,0,0,0,0,0,0,0,0,0,6},
			{8,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6},
			{8,0,3,3,0,0,0,0,0,8,8,4,0,0,0,0,0,0,0,0,0,0,0,4},
			{8,0,0,0,0,0,0,0,0,0,8,4,0,0,0,0,0,6,6,6,0,6,4,6},
			{8,8,8,8,0,8,8,8,8,8,8,4,4,4,4,4,4,6,0,0,0,0,0,6},
			{7,7,7,7,-6,7,7,7,7,0,8,0,8,0,8,0,8,4,0,4,0,6,0,6},
			{7,7,-6,-6,-6,-6,-6,-6,7,8,0,8,0,8,0,8,8,6,0,0,0,0,0,6},
			{7,-6,-6,-6,-6,-6,-6,-6,-6,0,0,0,0,0,0,0,8,6,0,0,0,0,0,4},
			{7,-6,-6,-6,-6,-6,-6,-6,-6,0,0,0,0,0,0,0,8,6,0,6,0,6,0,6},
			{7,7,-6,-6,-6,-6,-6,-6,7,8,0,8,0,8,0,8,8,6,4,6,0,6,6,6},
			{7,7,7,7,-6,7,7,7,7,8,8,4,0,6,8,4,8,3,3,3,0,3,3,3},
			{2,2,2,2,9,2,2,2,2,4,6,4,0,0,6,0,6,3,0,0,0,0,0,3},
			{2,2,0,0,0,0,0,2,2,4,0,0,0,0,0,0,4,3,0,0,0,0,0,3},
			{2,0,0,0,0,0,0,0,2,4,0,0,0,0,0,0,4,3,0,0,0,0,0,3},
			{1,0,0,0,0,0,0,0,1,4,4,4,4,4,6,0,6,3,3,0,0,0,3,3},
			{2,0,0,0,0,0,0,0,2,2,2,1,2,2,2,6,6,0,0,5,0,5,0,5},
			{2,2,0,0,0,0,0,2,2,2,0,0,0,2,2,0,5,0,5,0,0,0,5,5},
			{2,0,0,0,0,0,0,0,2,0,0,0,0,0,2,5,0,5,0,5,0,5,0,5},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5},
			{2,0,0,0,0,0,0,0,2,0,0,0,0,0,2,5,0,5,0,5,0,5,0,5},
			{2,2,0,0,0,0,0,2,2,2,0,0,0,2,2,0,5,0,5,0,0,0,5,5},
			{2,2,2,2,1,2,2,2,2,2,2,1,2,2,2,5,5,5,5,5,5,5,5,5}
		};
	
	private static final long serialVersionUID = 534020810310915575L;

	private double m_fX, m_fY, m_fDX, m_fDY, m_fPlaneX, m_fPlaneY;
	
	public JWolfMain(int nWidth, int nHeight, String szTitle)
	{
		super(nWidth, nHeight, szTitle, true);
	}
	
	@Override
	public void Init()
	{	
		//GoFullscreen(true);
		
		m_fX = 22; m_fY = 11.5;
		m_fDX = -1; m_fDY = 0;
		m_fPlaneX = 0; m_fPlaneY = 0.66;
		
		try 
		{
			sky = ImageIO.read(new File("res/textures/sky.jpg"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		zBuffer = new double[m_nWidth];
		buffer = new int[m_nWidth][m_nHeight];
		
		spriteOrder = new int[numSprites];
		spriteDistance = new double[numSprites];
		
		textures = new PixelBuffer[12];
		for(int i = 0; i < 12; ++i)
			textures[i] = new PixelBuffer(texWidth * texHeight);
		
		int error = 0;
		error |= LoadImage(textures[0], "res/textures/eagle.png");
		error |= LoadImage(textures[1], "res/textures/redbrick.png");
		error |= LoadImage(textures[2], "res/textures/purplestone.png");
		error |= LoadImage(textures[3], "res/textures/greystone.png");
		error |= LoadImage(textures[4], "res/textures/bluestone.png");
		error |= LoadImage(textures[5], "res/textures/mossy.png");
		error |= LoadImage(textures[6], "res/textures/wood.png");
		error |= LoadImage(textures[7], "res/textures/colorstone.png");
		error |= LoadImage(textures[8], "res/textures/mosaic.png");
		
		error |= LoadImage(textures[9], "res/sprites/barrel.png");
		error |= LoadImage(textures[10], "res/sprites/pillar.png");
		error |= LoadImage(textures[11], "res/sprites/greenlight.png");
		if(error > 0) { System.out.println("Failed to load images! Exiting..."); Stop(); }
		
		frameCount = 0;
		
		fullscreen = false;
	}
	
	KeyboardState oldState;
	boolean fullscreen;
	
	@Override
	public void Update(float delta)
	{
		KeyboardState kb = Keyboard.getState();
		
		if(kb.isKeyDown('F') && oldState.isKeyUp('F'))
		{
			fullscreen = !fullscreen;
			GoFullscreen(fullscreen);
		}
		
		frameCount++;
		if(frameCount < FRAMETOCOUNT)
		{
			fps += GetFPSCount();
		}
		else
		{
			m_fAvgFPS = fps / FRAMETOCOUNT - 1;
			fps = 0;
			frameCount = 0;
		}
		
		double moveSpeed = delta * 3.5; //the constant value is in squares/second
	    double rotSpeed = delta * 2; //the constant value is in radians/second
		
		if(Keyboard.getState().isKeyDown(KeyEvent.VK_ESCAPE))
			Stop();
		
		if(Keyboard.getState().isKeyDown('R'))
		{
			m_fX = 22; m_fY = 11.5;
			m_fDX = -1; m_fDY = 0;
			m_fPlaneX = 0; m_fPlaneY = 0.66;
		}
		
		if(Keyboard.getState().isKeyDown(KeyEvent.VK_UP))
		{
			int wx = worldMap[(int)(m_fX + (m_fDX * playerRadius) * moveSpeed)][(int)(m_fY)];
			int wy = worldMap[(int)(m_fX)][(int)(m_fY + (m_fDY * playerRadius) * moveSpeed)];
			
			if(wx <= 0) m_fX += m_fDX * moveSpeed;
			else
				if((wallTable[wx - 1].flags & Wall.SOLID) == 0)
					m_fX += m_fDX * moveSpeed;
			
			if(wy <= 0) m_fY += m_fDY * moveSpeed;
			else
				if((wallTable[wy - 1].flags & Wall.SOLID) == 0)
					m_fY += m_fDY * moveSpeed;
		}
		if(Keyboard.getState().isKeyDown(KeyEvent.VK_DOWN))
		{
			int wx = worldMap[(int)(m_fX - (m_fDX * playerRadius) * moveSpeed)][(int)(m_fY)];
			int wy = worldMap[(int)(m_fX)][(int)(m_fY - (m_fDY * playerRadius) * moveSpeed)];
			
			if(wx <= 0) m_fX -= m_fDX * moveSpeed;
			else
				if((wallTable[wx - 1].flags & Wall.SOLID) == 0)
					m_fX -= m_fDX * moveSpeed;
			
			if(wy <= 0) m_fY -= m_fDY * moveSpeed;
			else
				if((wallTable[wy - 1].flags & Wall.SOLID) == 0)
					m_fY -= m_fDY * moveSpeed;
		}
		if(Keyboard.getState().isKeyDown(KeyEvent.VK_D))
		{
			int wx = worldMap[(int)(m_fX + (m_fPlaneX * playerRadius) * moveSpeed)][(int)(m_fY)];
			int wy = worldMap[(int)(m_fX)][(int)(m_fY + (m_fPlaneY * playerRadius) * moveSpeed)];
			
			if(wx <= 0) m_fX += m_fPlaneX * moveSpeed;
			else
				if((wallTable[wx - 1].flags & Wall.SOLID) == 0)
					m_fX += m_fPlaneX * moveSpeed;
			
			if(wy <= 0) m_fY += m_fPlaneY * moveSpeed;
			else
				if((wallTable[wy - 1].flags & Wall.SOLID) == 0)
					m_fY += m_fPlaneY * moveSpeed;
		}
		if(Keyboard.getState().isKeyDown(KeyEvent.VK_A))
		{
			int wx = worldMap[(int)(m_fX - (m_fPlaneX * playerRadius) * moveSpeed)][(int)(m_fY)];
			int wy = worldMap[(int)(m_fX)][(int)(m_fY - (m_fPlaneY * playerRadius) * moveSpeed)];
			
			if(wx <= 0) m_fX -= m_fPlaneX * moveSpeed;
			else
				if((wallTable[wx - 1].flags & Wall.SOLID) == 0)
					m_fX -= m_fPlaneX * moveSpeed;
			
			if(wy <= 0) m_fY -= m_fPlaneY * moveSpeed;
			else
				if((wallTable[wy - 1].flags & Wall.SOLID) == 0)
					m_fY -= m_fPlaneY * moveSpeed;
		}
		if(Keyboard.getState().isKeyDown(KeyEvent.VK_RIGHT))
		{
			//both camera direction and camera plane must be rotated
			double oldDirX = m_fDX;
			m_fDX = m_fDX * Math.cos(-rotSpeed) - m_fDY * Math.sin(-rotSpeed);
			m_fDY = oldDirX * Math.sin(-rotSpeed) + m_fDY * Math.cos(-rotSpeed);
			double oldPlaneX = m_fPlaneX;
			m_fPlaneX = m_fPlaneX * Math.cos(-rotSpeed) - m_fPlaneY * Math.sin(-rotSpeed);
			m_fPlaneY = oldPlaneX * Math.sin(-rotSpeed) + m_fPlaneY * Math.cos(-rotSpeed);
		}
		if(Keyboard.getState().isKeyDown(KeyEvent.VK_LEFT))
		{
			//both camera direction and camera plane must be rotated
			double oldDirX = m_fDX;
			m_fDX = m_fDX * Math.cos(rotSpeed) - m_fDY * Math.sin(rotSpeed);
			m_fDY = oldDirX * Math.sin(rotSpeed) + m_fDY * Math.cos(rotSpeed);
			double oldPlaneX = m_fPlaneX;
			m_fPlaneX = m_fPlaneX * Math.cos(rotSpeed) - m_fPlaneY * Math.sin(rotSpeed);
			m_fPlaneY = oldPlaneX * Math.sin(rotSpeed) + m_fPlaneY * Math.cos(rotSpeed);
		}
		
		oldState = kb;
	}

	@Override
	public void Draw(Graphics2D pGraphics) 
	{
		Clear(0x0);
		for(int x = 0; x < m_nWidth; x++) for(int y = 0; y < m_nHeight; ++y) buffer[x][y] = 0; //clear the buffer instead of Clear()
		
		pGraphics.drawImage(sky, 0, 0, null);
		
		for(int x = 0; x < m_nWidth; ++x)
		{
			double cameraX = 2 * x / (double)m_nWidth - 1; //x-coordinate in camera space
			double rayPosX = m_fX;
			double rayPosY = m_fY;
			double rayDirX = m_fDX + m_fPlaneX * cameraX;
			double rayDirY = m_fDY + m_fPlaneY * cameraX;
			
			//which box of the map we're in  
			int mapX = (int)rayPosX;
			int mapY = (int)rayPosY;
					       
			//length of ray from current position to next x or y-side
			double sideDistX;
			double sideDistY;
					       
			//length of ray from one x or y-side to next x or y-side
			double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
			double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
			double perpWallDist;
			
			//what direction to step in x or y-direction (either +1 or -1)
			int stepX;
			int stepY;
			
			int hit = 0; //was there a wall hit?
			int side = 0; //was a NS or a EW wall hit?
			
			//calculate step and initial sideDist
			if (rayDirX < 0)
			{
				stepX = -1;
				sideDistX = (rayPosX - mapX) * deltaDistX;
			}
			else
			{
				stepX = 1;
				sideDistX = (mapX + 1.0 - rayPosX) * deltaDistX;
			}
			if (rayDirY < 0)
			{
				stepY = -1;
				sideDistY = (rayPosY - mapY) * deltaDistY;
			}
			else
			{
				stepY = 1;
				sideDistY = (mapY + 1.0 - rayPosY) * deltaDistY;
			}
			
			//perform DDA
			while (hit == 0)
			{
				//jump to next map square, OR in x-direction, OR in y-direction
				if (sideDistX < sideDistY)
				{
					sideDistX += deltaDistX;
					mapX += stepX;
					side = 0;
				}
				else
				{
					sideDistY += deltaDistY;
					mapY += stepY;
					side = 1;
				}
				//Check if ray has hit a wall
				if (worldMap[mapX][mapY] > 0) 
				{
					if((wallTable[worldMap[mapX][mapY] - 1].flags) > 1)
					//if((wallTable[worldMap[mapX][mapY] - 1].flags & Wall.TRANSLUCEND) > 0)
					{
						WallInfo inf = new WallInfo();
						
						inf.rayPosX = rayPosX;
						inf.side = side;
						inf.rayPosY = rayPosY;
						inf.rayDirX = rayDirX;
						inf.rayDirY = rayDirY;
						inf.mapX = mapX;
						inf.mapY = mapY;
						inf.stepX = stepX;
						inf.stepY = stepY;
						inf.wallIndex = worldMap[mapX][mapY] - 1;
						inf.x = x;
						
						specialWalls.add(inf);
					}
					else
						hit = 1;
				}
			}
			
			//Calculate distance projected on camera direction (oblique distance will give fisheye effect!)
			if (side == 0)
				perpWallDist = Math.abs((mapX - rayPosX + (1 - stepX) / 2) / rayDirX);
			else
				perpWallDist = Math.abs((mapY - rayPosY + (1 - stepY) / 2) / rayDirY);
			
			//Calculate height of line to draw on screen
			int lineHeight = Math.abs((int)(m_nHeight / perpWallDist));
			
			//calculate lowest and highest pixel to fill in current stripe
			int drawStart = -lineHeight / 2 + m_nHeight / 2;
			if(drawStart < 0) drawStart = 0;
			int drawEnd = lineHeight / 2 + m_nHeight / 2;
			if(drawEnd >= m_nHeight) drawEnd = m_nHeight - 1;
			
			//texturing calculations
			int texNum = wallTable[worldMap[mapX][mapY] - 1].tex; //1 subtracted from it so that texture 0 can be used!
			
			//calculate value of wallX
			double wallX; //where exactly the wall was hit
			if (side == 1) wallX = rayPosX + ((mapY - rayPosY + (1 - stepY) / 2) / rayDirY) * rayDirX;
			else wallX = rayPosY + ((mapX - rayPosX + (1 - stepX) / 2) / rayDirX) * rayDirY;
			wallX -= Math.floor((wallX));
			
			//x coordinate on the texture
			int texX = (int)(wallX * (double)(texWidth));
			if(side == 0 && rayDirX > 0) texX = texWidth - texX - 1;
			if(side == 1 && rayDirY < 0) texX = texWidth - texX - 1;

			for(int y = drawStart; y<drawEnd; y++)
			{
				int d = y * 256 - m_nHeight * 128 + lineHeight * 128;  //256 and 128 factors to avoid floats
				int texY = ((d * texHeight) / lineHeight) / 256;
				int color = textures[texNum].GetPixel(texHeight * texY + texX);
				//make color darker for y-sides: R, G and B byte each divided through two with a "shift" and an "and"
				if(side == 1) color = (color >> 1) & 8355711;
				buffer[x][y] = color | (255 << 24);
			} 
			
			//SET THE ZBUFFER FOR THE SPRITE CASTING
			zBuffer[x] = perpWallDist; //perpendicular distance is used
			
			//FLOOR CASTING
			double floorXWall, floorYWall; //x, y position of the floor texel at the bottom of the wall
			
			//4 different wall directions possible
			if(side == 0 && rayDirX > 0)
			{
				floorXWall = mapX;
				floorYWall = mapY + wallX;
			}
			else if(side == 0 && rayDirX < 0)
			{
				floorXWall = mapX + 1.0;
				floorYWall = mapY + wallX;
			}
			else if(side == 1 && rayDirY > 0)
			{
				floorXWall = mapX + wallX;
				floorYWall = mapY;
			}
			else
			{
				floorXWall = mapX + wallX;
				floorYWall = mapY + 1.0;
			} 
			
			double distWall, distPlayer, currentDist;  
			
			distWall = perpWallDist;
			distPlayer = 0.0;
			
			if (drawEnd < 0) drawEnd = m_nHeight; //becomes < 0 when the integer overflows
			
			//draw the floor from drawEnd to the bottom of the screen
			for(int y = drawEnd; y < m_nHeight; y++)
			{
				currentDist = m_nHeight / (2.0 * y - m_nHeight); //you could make a small lookup table for this instead
				
				double weight = (currentDist - distPlayer) / (distWall - distPlayer);
				 
				double currentFloorX = Math.min(weight * floorXWall + (1.0 - weight) * m_fX, 23);
				double currentFloorY = Math.min(weight * floorYWall + (1.0 - weight) * m_fY, 23);
				
				int floorTexX, floorTexY;
		        floorTexX = (int)(currentFloorX * texWidth / 1) % texWidth;
		        floorTexY = (int)(currentFloorY * texHeight / 1) % texHeight;
		        
		        int ceilingTex = worldMap[(int)currentFloorX][(int)currentFloorY];
		        
				//floor
				buffer[x][y] = textures[3].GetPixel(texWidth * floorTexY + floorTexX) | (255 << 24);
				//ceiling (symmetrical!)
				if(ceilingTex < 0)
					buffer[x][m_nHeight - y] = ((textures[Math.abs(ceilingTex)].GetPixel(texWidth * floorTexY + floorTexX) >> 1) & 8355711) | (255 << 24);
			}
		}
		
		//SPRITE CASTING
		//sort sprites from far to close
		for(int i = 0; i < numSprites; i++)
		{
			spriteOrder[i] = i;
			spriteDistance[i] = ((m_fX - sprites[i].x) * (m_fX - sprites[i].x) + (m_fY - sprites[i].y) * (m_fY - sprites[i].y)); //sqrt not taken, unneeded
		}
		combSort(spriteOrder, spriteDistance, numSprites);
		 
		//after sorting the sprites, do the projection and draw them
		for(int i = 0; i < numSprites; i++)
		{
			//translate sprite position to relative to camera
			double spriteX = sprites[spriteOrder[i]].x - m_fX;
			double spriteY = sprites[spriteOrder[i]].y - m_fY;
			
			//transform sprite with the inverse camera matrix
			// [ planeX   dirX ] -1                                       [ dirY      -dirX ]
			// [               ]       =  1/(planeX*dirY-dirX*planeY) *   [                 ]
			// [ planeY   dirY ]                                          [ -planeY  planeX ]
			
			double invDet = 1.0 / (m_fPlaneX * m_fDY - m_fDX * m_fPlaneY); //required for correct matrix multiplication
			
			double transformX = invDet * (m_fDY * spriteX - m_fDX * spriteY);
			double transformY = invDet * (-m_fPlaneY * spriteX + m_fPlaneX * spriteY); //this is actually the depth inside the screen, that what Z is in 3D       
			    
			int spriteScreenX = (int)((m_nWidth / 2) * (1 + transformX / transformY));
			
			//calculate height of the sprite on screen
			int spriteHeight = Math.abs((int)(m_nHeight / (transformY))); //using "transformY" instead of the real distance prevents fisheye
			//calculate lowest and highest pixel to fill in current stripe
			int drawStartY = -spriteHeight / 2 + m_nHeight / 2;
			if(drawStartY < 0) drawStartY = 0;
			int drawEndY = spriteHeight / 2 + m_nHeight / 2;
			if(drawEndY >= m_nHeight) drawEndY = m_nHeight - 1;
			  
			//calculate width of the sprite
			int spriteWidth = Math.abs((int)(m_nHeight / (transformY)));
			int drawStartX = -spriteWidth / 2 + spriteScreenX;
			if(drawStartX < 0) drawStartX = 0;
			int drawEndX = spriteWidth / 2 + spriteScreenX;
			if(drawEndX >= m_nWidth) drawEndX = m_nWidth - 1;
			
			//loop through every vertical stripe of the sprite on screen
			for(int stripe = drawStartX; stripe < drawEndX; stripe++)
			{
				int texX = (int)(256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * texWidth / spriteWidth) / 256;
				//the conditions in the if are:
				//1) it's in front of camera plane so you don't see things behind you
				//2) it's on the screen (left)
				//3) it's on the screen (right)
				//4) ZBuffer, with perpendicular distance
				if(transformY > 0 && stripe > 0 && stripe < m_nWidth && transformY < zBuffer[stripe]) 
					for(int y = drawStartY; y < drawEndY; y++) //for every pixel of the current stripe
					{
						int d = (y) * 256 - m_nHeight * 128 + spriteHeight * 128; //256 and 128 factors to avoid floats
						int texY = ((d * texHeight) / spriteHeight) / 256;
						int color = textures[sprites[spriteOrder[i]].tex].GetPixel(texWidth * texY + texX); //get current color from the texture
						if((sprites[spriteOrder[i]].flags & Sprite.TRANSLUCEND) > 0)
						{
							float alphaFac = (float)sprites[spriteOrder[i]].specialData / 255.0f;
							if((color & 0x00FFFFFF) != 0) buffer[stripe][y] = RGBtoINT(INTtoRGB(buffer[stripe][y]).Mul(1 - alphaFac).Add(INTtoRGB(color).Mul(alphaFac))) | (255 << 24); //paint pixel if it isn't black, black is the invisible color
						}
						else
							if((color & 0x00FFFFFF) != 0) buffer[stripe][y] = color;
					}
			}
		}
		

		for(int i = specialWalls.size() - 1; i >= 0; --i)
		{
			WallInfo info = specialWalls.get(i);
			
			double perpWallDist;
			
			//Calculate distance projected on camera direction (oblique distance will give fisheye effect!)
			if (info.side == 0)
			{
				if((wallTable[info.wallIndex].flags & Wall.THIN) > 0)
					perpWallDist = Math.abs((info.mapX - info.rayPosX + (1 - info.stepX) / 2) / info.rayDirX + info.stepX / 2);
				else
					perpWallDist = Math.abs((info.mapX - info.rayPosX + (1 - info.stepX) / 2) / info.rayDirX);
			}
			else
			{
				if((wallTable[info.wallIndex].flags & Wall.THIN) > 0)
					perpWallDist = Math.abs((info.mapY - info.rayPosY + (1 - info.stepY) / 2) / info.rayDirY + info.stepY / 2);
				else
					perpWallDist = Math.abs((info.mapY - info.rayPosY + (1 - info.stepY) / 2) / info.rayDirY);
			}
			
			//Calculate height of line to draw on screen
			int lineHeight_ = Math.abs((int)(m_nHeight / perpWallDist));
			
			//calculate lowest and highest pixel to fill in current stripe
			int drawStart_ = -lineHeight_ / 2 + m_nHeight / 2;
			if(drawStart_ < 0) drawStart_ = 0;
			int drawEnd_ = lineHeight_ / 2 + m_nHeight / 2;
			if(drawEnd_ >= m_nHeight) drawEnd_ = m_nHeight - 1;
			
			//texturing calculations
			int texNum_ = wallTable[info.wallIndex].tex;
			
			//calculate value of wallX
			double wallX_; //where exactly the wall was hit
			if (info.side == 1) wallX_ = info.rayPosX + ((info.mapY - info.rayPosY + (1 - info.stepY) / 2) / info.rayDirY) * info.rayDirX;
			else wallX_ = info.rayPosY + ((info.mapX - info.rayPosX + (1 - info.stepX) / 2) / info.rayDirX) * info.rayDirY;
			wallX_ -= Math.floor((wallX_));
			
			//x coordinate on the texture
			int texX_ = (int)(wallX_ * (double)(texWidth));
			if(info.side == 0 && info.rayDirX > 0) texX_ = texWidth - texX_ - 1;
			if(info.side == 1 && info.rayDirY < 0) texX_ = texWidth - texX_ - 1;

			for(int y = drawStart_; y < drawEnd_; y++)
			{
				int d = y * 256 - m_nHeight * 128 + lineHeight_ * 128;  //256 and 128 factors to avoid floats
				int texY_ = ((d * texHeight) / lineHeight_) / 256;
				int color = textures[texNum_].GetPixel(texHeight * texY_ + texX_);
				//make color darker for y-sides: R, G and B byte each divided through two with a "shift" and an "and"
				//if(info.side == 1) color = (color >> 1) & 8355711;
				if((wallTable[info.wallIndex].flags & Wall.TRANSLUCEND) > 0)
				{
					float alphaFac = (float)wallTable[info.wallIndex].specialData / 255.0f;
					buffer[info.x][y] = RGBtoINT(INTtoRGB(buffer[info.x][y]).Mul(1 - alphaFac).Add(INTtoRGB(color).Mul(alphaFac))) | (255 << 24);
				}
				else
					buffer[info.x][y] = color | (255 << 24);
			} 
			
			specialWalls.remove(i);
		}
		
		drawBuffer(buffer);
		
		pGraphics.setColor(new Color(0xffffffff));
		pGraphics.drawString("FPS: " + m_fAvgFPS, 0, 12);
		
		Flip();
	}
}

class WallInfo
{
	int side;
	double rayPosX, rayPosY, rayDirX, rayDirY;
	int mapX, mapY;
	int wallIndex;
	int stepX, stepY;
	int x;
}
