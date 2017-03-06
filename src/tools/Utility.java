package tools;

import engine.RGBColor;

final public class Utility 
{
	final public static void swap(int a, int b, int[] c)
	{
		int t = c[a];
		c[a] = c[b];
		c[b] = t;
	}
	
	final public static void swap(int a, int b, double[] c)
	{
		double t = c[a];
		c[a] = c[b];
		c[b] = t;
	}
	
	final public static RGBColor INTtoRGB(int c)
	{
		RGBColor rgb = new RGBColor();
		
		rgb.r = (char)((c >> 16) & 0xff);
		rgb.g = (char)((c >> 8) & 0xff);
		rgb.b = (char)((c) & 0xff);
		
		return rgb;
	}
	
	final public static int RGBtoINT(RGBColor rgb)
	{
		int c = 0;
		
		c |= (rgb.r << 16);
		c |= (rgb.g << 8);
		c |= (rgb.b);
		
		return c;
	}
	
	final public static void combSort(int order[], double dist[], int number)
	{
		int gap = number;
		boolean swapped = false;
		while(gap > 1 || swapped)
		{
			//shrink factor 1.3
			gap = (gap * 10) / 13;
			if(gap == 9 || gap == 10) gap = 11;
			if(gap < 1) gap = 1;
			swapped = false;
			for (int i = 0; i < number - gap; i++)
			{
				int j = i + gap;
				if (dist[i] < dist[j])
				{
					swap(i, j, dist);
					swap(i, j, order);
					swapped = true;
				}
			}
		}
	}
}
