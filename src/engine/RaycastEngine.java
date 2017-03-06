package engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.MemoryImageSource;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public abstract class RaycastEngine extends JPanel implements WindowListener
{
	private static final long serialVersionUID = 7987627622151167408L;
	
	private JFrame m_pFrame;
	protected int m_nWidth, m_nHeight;
	
	private Rectangle m_pFullscreen;
	private boolean m_bIsFullscreen;
	
	private boolean m_bIsRunning;

	private VolatileImage m_pBackBuffer;
	
	private float m_fFPS;
	private int m_nCurrentFPS;
	private float m_fCurrentTime;
	
	public RaycastEngine(int nWidth, int nHeight, String szTitle)
	{
		m_nWidth = nWidth;
		m_nHeight = nHeight;
		
		this.setPreferredSize(new Dimension(m_nWidth, m_nHeight));
		this.setBackground(new Color(0x0));
		
		m_pFrame = new JFrame(szTitle);
		//m_pFrame.setUndecorated(true);
		
		m_pFrame.setResizable(false);
		
		Container pContentPane = m_pFrame.getContentPane();
		pContentPane.setLayout(new BorderLayout());
		pContentPane.add(this);
		m_pFrame.pack();
		
		m_pFrame.setLocationRelativeTo(null);
		m_pFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		m_pFrame.addWindowListener(this);
		
		m_pFrame.setVisible(true);
		
		m_bIsFullscreen = false;
	}
	
	public RaycastEngine(int nWidth, int nHeight, String szTitle, boolean undecorated)
	{
		m_nWidth = nWidth;
		m_nHeight = nHeight;
		
		this.setPreferredSize(new Dimension(m_nWidth, m_nHeight));
		this.setBackground(new Color(0x0));
		
		m_pFrame = new JFrame(szTitle);
		m_pFrame.setUndecorated(undecorated);
		
		m_pFrame.setResizable(false);
		
		Container pContentPane = m_pFrame.getContentPane();
		pContentPane.setLayout(new BorderLayout());
		pContentPane.add(this);
		m_pFrame.pack();
		
		m_pFrame.setLocationRelativeTo(null);
		m_pFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		m_pFrame.addWindowListener(this);
		
		m_pFrame.setVisible(true);
		
		m_bIsFullscreen = false;
	}
	
	final public void Run() throws InterruptedException
	{
		m_bIsRunning = true;
		
		KeyboardFocusManager kbfmgr = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kbfmgr.addKeyEventDispatcher(new Keyboard());
		
		RestoreBackBuffer();
		
		m_pFullscreen = new Rectangle();
		
		//size of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		float facA = (float)screenSize.width / (float)m_nWidth;
		float facB = (float)screenSize.height / (float)m_nHeight;
		float facC = (facA > facB) ? facB : facA;
		
		m_pFullscreen.width = (int)((float)m_nWidth * facC);
		m_pFullscreen.height = (int)((float)m_nHeight * facC);
		
		float fullHW = screenSize.width / 2;
		float fullHH = screenSize.height / 2;
		
		float windHW = m_pFullscreen.width / 2;
		float windHH = m_pFullscreen.height / 2;
		
		m_pFullscreen.x = (int)(fullHW - windHW);
		m_pFullscreen.y = (int)(fullHH - windHH);
		
		Init();
		
		float currentTime = (System.nanoTime() / (1000.f * 1000.f * 1000.f));
		float accumulator = 0;
		
		float newTime = 0;
		float fFrameTime = 0;
		
		float delta = 1.f / 60.f;
		
		m_nCurrentFPS = 0;
		m_fCurrentTime = 0;
		m_fFPS = 0;
		
		while(m_bIsRunning)
		{
			newTime = (System.nanoTime() / (1000.f * 1000.f * 1000.f));
			fFrameTime = newTime - currentTime;
			
			//m_fFPS = fFrameTime;
			m_fCurrentTime += fFrameTime;
			if(m_fCurrentTime >= 1)
			{
				m_fCurrentTime -= 1;
				m_fFPS = m_nCurrentFPS;
				m_nCurrentFPS = 0;
			}
			else
				m_nCurrentFPS++;
			
			if(fFrameTime > 0.25f)
				fFrameTime = 0.25f;
			
			currentTime = newTime;
			
			accumulator += fFrameTime;
			
			while(accumulator >= delta)
			{
				Update(delta);
				accumulator -= delta;
			}
			
			Graphics2D pGD = m_pBackBuffer.createGraphics();
			Draw(pGD);
			pGD.dispose();
			
			Thread.sleep(1);
		}
		
		m_pFrame.setVisible(false);
	}
	
	final public void RestoreBackBuffer()
	{
		GraphicsConfiguration gc = getGraphicsConfiguration();
		m_pBackBuffer = gc.createCompatibleVolatileImage(m_nWidth, m_nHeight);
	}
	
	final public void Flip()
	{
		if(m_bIsFullscreen)
			((Graphics2D)getGraphics()).drawImage(m_pBackBuffer, m_pFullscreen.x, m_pFullscreen.y, m_pFullscreen.x + m_pFullscreen.width, m_pFullscreen.y + m_pFullscreen.height, 0, 0, m_nWidth, m_nHeight, null);
		else
			((Graphics2D)getGraphics()).drawImage(m_pBackBuffer, 0, 0, null);
	}
	
	final public void Clear(int dwColor)
	{
		Graphics2D pGraphics = (Graphics2D)m_pBackBuffer.getGraphics();
		pGraphics.setColor(new Color(dwColor));
		pGraphics.fillRect(0, 0, m_nWidth, m_nHeight);
	}
	
	final public void Stop()
	{
		m_bIsRunning = false;
	}
	
	final public float GetFPSCount()
	{
		return m_fFPS;
	}
	
	final public void horLine(int y, int startDraw, int endDraw, int color)
	{
		Graphics2D g = (Graphics2D)m_pBackBuffer.getGraphics();
		g.setColor(new Color(color));
		g.drawLine(startDraw, y, endDraw, y);
	}
	
	final public void verLine(int x, int startDraw, int endDraw, int color)
	{
		Graphics2D g = (Graphics2D)m_pBackBuffer.getGraphics();
		g.setColor(new Color(color));
		g.drawLine(x, startDraw, x, endDraw);
	}
	
	final public void drawBuffer(int[] buffer)
	{
		Image img = m_pFrame.createImage(new MemoryImageSource(m_nWidth, m_nHeight, buffer, 0, m_nWidth));
		m_pBackBuffer.getGraphics().drawImage(img, 0, 0, null);
	}
	
	final public void drawBuffer(int[][] buffer)
	{
		int[] tmp = new int[m_nWidth * m_nHeight];
		for(int y = 0; y < m_nWidth; ++y)
			for(int x = 0; x < m_nHeight; ++x)
				tmp[x*m_nWidth+y] = buffer[y][x];
		drawBuffer(tmp);
	}
	
	final public void GoFullscreen(boolean b)
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device;
		device = ge.getDefaultScreenDevice();
		
		if(!b)
		{
			device.setFullScreenWindow(null);
			m_bIsFullscreen = false;
		}
		else
		{
			if(device.isFullScreenSupported())
			{
				device.setFullScreenWindow(m_pFrame);
				m_bIsFullscreen = true;
			}
		}
		
		RestoreBackBuffer();
	}
	
	public abstract void Init();
	public abstract void Update(float delta);
	public abstract void Draw(Graphics2D pGraphics);
	
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) { m_bIsRunning = false; }
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	
}
