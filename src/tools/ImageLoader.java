package tools;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

final public class ImageLoader
{
	final public static int LoadBitmap(PixelBuffer pBuffer, String szPath)
	{
		if(pBuffer == null)
			return 1;
		
		final int HEADERSIZE = 14;
		
		int width, height;
		int bpp;
		
		FileInputStream in;
		try 
		{
			in = new FileInputStream(new File(szPath));
			
			byte[] sig = new byte[HEADERSIZE];
			in.read(sig, 0, HEADERSIZE);
			if(sig[0] != 'B' || sig[1] != 'M')
			{
				System.out.println("Image is not a Bitmap or the file is corrupt");
				in.close();
				return 3;
			}
			
			int size = (int)((sig[3] << 8) | sig[2]);
			System.out.println("Filesize: " + size + " bytes");
			
			int byteToStartPixels = (int)((sig[13] << 24) | (sig[12] << 16) | (sig[11] << 8) | sig[10]);
			System.out.println("Position where the pixeldata starts: " + byteToStartPixels);
			
			byte[] infoSize = new byte[4];
			in.read(infoSize, 0, 4);
			int infoSizeN = (int)((infoSize[3] << 24) | (infoSize[2] << 16) | (infoSize[1] << 8) | infoSize[0]);
			System.out.print("Size of info struct: " + infoSizeN + " bytes >>");
			
			short compr = 0;
			byte[] info = new byte[infoSizeN - 1];
			in.read(info, 0, infoSizeN - 1);
			if(infoSizeN == 12)
			{
				System.out.println(" BITMAPCOREHEADER");
				width = (int)((info[1] << 8) | info[0]);
				height = (int)((info[3] << 8) | info[2]);
				bpp = (int)((info[7] << 8) | info[6]);
			}
			else if(infoSizeN == 40)
			{
				System.out.println(" BITMAPINFOHEADER");
				width = Math.abs((int)((info[3] << 24) | (info[2] << 16) | (info[1] << 8) | info[0]));
				height = Math.abs((int)((info[7] << 24) | (info[6] << 16) | (info[5] << 8) | info[4]));
				bpp = (int)((info[11] << 8) | info[10]);
				compr = (short)((info[13] << 8) | info[12]);
			}
			else
			{
				System.out.println("Unsupportet Bitmap");
				in.close();
				return 4;
			}
			
			if(compr == 0)
			{
				int rowSize = Math.abs(((bpp * width + 31) / 32) * 4);
				int pixelArraySize = rowSize * Math.abs(height);
				byte[] pixels = new byte[pixelArraySize];
				in.read(pixels, 0, pixelArraySize);
				
				final int pixelLength = 3;
				for(int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
				{
					int argb = 0;
					argb += 0xff << 24; // alpha
					argb += ((int) pixels[pixel] & 0xff); // blue
					argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
					argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
					pBuffer.SetPixel(row * height + col, argb);
					col++;
					if (col == width)
					{
						col = 0;
						row++;
					}
				}
			}
			
			System.out.println("Width: " + width + " Height: " + height + " Bpp: " + bpp);
			
			in.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			return 2;
		}
		
		System.out.println("OK");
		
		return 0;
	}
	
	final public static int LoadImage(PixelBuffer pBuffer, String szPath)
	{
		if(pBuffer == null)
			return 1;
		
		final BufferedImage image;
		try
		{
			image = ImageIO.read(new File(szPath));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return 2;
		}
		
		final byte[] pixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final int bpp = image.getColorModel().getPixelSize();
		
		System.out.println(szPath + " BPP: " + bpp);
		
		if(bpp != 24)
		{
			System.err.println("Unsupported Pixeldepth");
			return 3;
		}
		
		pBuffer.Resize(width * height);
		
		System.out.println(szPath + " pixel dim: Width=" + width + " Height=" + height);
		System.out.println(szPath + " pixel Width * Height: " + (width * height));
		System.out.println(szPath + " bytes : " + (pixels.length));
		
		final int pixelLength = 3;
		for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
		{
			int argb = 0;
			argb += 0xff << 24; // 255 alpha
			argb += ((int) pixels[pixel] & 0xff); // blue
			argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
			argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
			pBuffer.SetPixel(row * height + col, argb);
			col++;
			if(col == width)
			{
				col = 0;
				row++;
			}
		}
		
		return 0;
	}
}
