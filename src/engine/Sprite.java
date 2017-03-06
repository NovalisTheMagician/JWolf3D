package engine;

public class Sprite 
{
	public static final int SOLID = 0x01;
	public static final int TRANSLUCEND = 0x01 << 1;
	
	public double x, y;
	public double radius;
	public int tex;
	public int flags;
	public int specialData;
}
