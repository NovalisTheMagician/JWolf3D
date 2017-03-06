package engine;

import java.util.Arrays;

public class KeyboardState
{
	private boolean[] keys = new boolean[256];
	
	public KeyboardState(boolean[] keys)
	{
		this.keys = Arrays.copyOf(keys, keys.length);
	}
	
	public boolean isKeyDown(int key)
	{
		if(key > 255 || key < 0)
			return false;
		
		return keys[key];
	}
	
	public boolean isKeyUp(int key)
	{
		if(key > 255 || key < 0)
			return false;
		
		return !keys[key];
	}
}
