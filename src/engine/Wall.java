package engine;

public class Wall 
{
	public static final int SOLID = 0x1;
	public static final int TRANSLUCEND = 0x1 << 1;
	public static final int THIN = 0x1 << 2;
	public static final int DOOR_CLOSED = 0x1 << 3 | THIN | SOLID;
	public static final int DOOR_OPEN = 0x1 << 4 | THIN;
	
	public static final int SD_ALIGN_HOR = 0xff;
	public static final int SD_ALIGN_VER = 0xff << 8;
	
	public int tex;
	public int flags;
	
	public int specialData;
}
