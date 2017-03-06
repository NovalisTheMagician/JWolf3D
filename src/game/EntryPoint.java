package game;

public class EntryPoint 
{
	public static void main(String[] args) throws InterruptedException 
	{
		new JWolfMain(640, 480, "JWolf3D").Run();
		System.exit(0);
	}
}
