package net.tfcl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

/**
 * Class for storing a user's username and password.
 * 
 * @author Forkk13
 */
public class UserInfo
{
	public UserInfo(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void writeFile(File file, boolean savePassword) throws IOException
	{
		String str = getUsername();
		if (savePassword)
			str = str + "=" + getPassword();
		
		// Encode the string into bytes using utf-8
		byte[] utf8 = str.getBytes("UTF8");
		
		// Encode bytes to base64
		String base64 = DatatypeConverter.printBase64Binary(utf8);
		
		PrintWriter writer = new PrintWriter(file);
		writer.println(base64);
		writer.close();
	}
	
	public void readFile(File file)
	{
		Scanner scanner = null;
		try
		{
			scanner = new Scanner(file);
		} catch (FileNotFoundException e)
		{
			return;
		}
		scanner.useDelimiter("\\A");
		
		if (scanner.hasNext())
		{
			String base64 = scanner.next();
			
			byte[] data = DatatypeConverter.parseBase64Binary(base64);
			
			String str = new String(data);
			String[] parts = str.split("=");
			if (parts.length >= 1)
			{
				setUsername(parts[0]);
				if (parts.length >= 2)
					setPassword(parts[1]);
			}
		}
		
		scanner.close();
	}
	
	private String username;
	private String password;
}
