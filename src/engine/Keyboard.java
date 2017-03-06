package engine;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class Keyboard implements KeyEventDispatcher
{
	private static boolean[] keys = new boolean[256];
	
	static
	{
		Arrays.fill(keys, false);
	}
	
	public static KeyboardState getState()
	{
		return new KeyboardState(keys);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent ev) 
	{
		if(ev.getID() == KeyEvent.KEY_PRESSED)
			keys[ev.getKeyCode()] = true;
		if(ev.getID() == KeyEvent.KEY_RELEASED)
			keys[ev.getKeyCode()] = false;
		return true;
	}
}
