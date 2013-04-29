package net.tfcl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class StreamListener extends Thread
{
	public StreamListener(InputStream stream)
	{
		this.stream = stream;
	}
	
	@Override
	public void run()
	{
//		try
//		{
			Scanner scan = new Scanner(stream);
			while (scan.hasNextLine())
				System.out.println("Instance: " + scan.nextLine());
			
//			InputStreamReader streamReader = new InputStreamReader(stream);
//			BufferedReader reader = new BufferedReader(streamReader);
//			
//			String line = null;
//			while ((line = reader.readLine()) != null)
//				System.out.println(line);
			
//		} 
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
	}
	
	private InputStream stream;
}
