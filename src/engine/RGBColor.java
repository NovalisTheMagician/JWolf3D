package engine;

public class RGBColor
{
	public char r, g, b;
	
	public RGBColor()
	{
		r = g = b = 0;
	}
	
	public RGBColor Add(RGBColor c)
	{
		this.r += c.r;
		this.g += c.g;
		this.b += c.b;
		
		this.r = Clamp((char)0, (char)255, this.r);
		this.g = Clamp((char)0, (char)255, this.g);
		this.b = Clamp((char)0, (char)255, this.b);
		
		return this;
	}
	
	public RGBColor Sub(RGBColor c)
	{
		this.r -= c.r;
		this.g -= c.g;
		this.b -= c.b;
		
		this.r = Clamp((char)0, (char)255, this.r);
		this.g = Clamp((char)0, (char)255, this.g);
		this.b = Clamp((char)0, (char)255, this.b);
		
		return this;
	}
	
	public RGBColor Div(double d)
	{
		double fac = 1 / d;
		
		this.r *= fac;
		this.g *= fac;
		this.b *= fac;
		
		this.r = Clamp((char)0, (char)255, this.r);
		this.g = Clamp((char)0, (char)255, this.g);
		this.b = Clamp((char)0, (char)255, this.b);
		
		return this;
	}
	
	public RGBColor Mul(double d)
	{
		this.r *= d;
		this.g *= d;
		this.b *= d;
		
		this.r = Clamp((char)0, (char)255, this.r);
		this.g = Clamp((char)0, (char)255, this.g);
		this.b = Clamp((char)0, (char)255, this.b);
		
		return this;
	}
	
	private char Clamp(char min, char max, char val)
	{
		return ((val < min) ? min : ((val > max) ? max : val));
	}
}
