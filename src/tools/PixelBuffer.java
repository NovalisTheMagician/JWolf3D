package tools;

public class PixelBuffer 
{
	private int[] m_pBuffer;
	private int m_nSize;
	
	public PixelBuffer()
	{
		m_pBuffer = null;
		m_nSize = 0;
	}
	
	public PixelBuffer(int nSize)
	{
		m_pBuffer = new int[nSize];
		m_nSize = nSize;
	}
	
	public void Resize(int nSize)
	{
		m_pBuffer = new int[nSize];
		m_nSize = nSize;
	}
	
	public int GetPixel(int n)
	{
		if(n >= m_nSize || n < 0)
			return 0;
		
		return m_pBuffer[n];
	}
	
	public void SetPixel(int n, int pixel)
	{
		if(n >= m_nSize || n < 0)
			return;
		
		m_pBuffer[n] = pixel;
	}
	
	public void SetPixles(int pBuffer[])
	{
		int l = m_nSize;
		
		if(pBuffer.length < m_nSize)
			l = pBuffer.length;
		
		System.arraycopy(pBuffer, 0, m_pBuffer, 0, l);
	}
	
	public int GetSize()
	{
		return m_nSize;
	}
	
	public int[] GetBuffer()
	{
		return m_pBuffer;
	}
}
