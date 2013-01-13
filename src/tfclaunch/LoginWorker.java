package tfclaunch;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.swing.SwingWorker;

import tfclaunch.utils.GeneralException;

public class LoginWorker extends SwingWorker<LoginResponse, Void>
{
	public LoginWorker(UserInfo uinfo)
	{
		this.uinfo = uinfo;
	}
	
	@Override
	protected LoginResponse doInBackground() throws GeneralException
	{
		StringBuilder requestBuilder = null;
		try
		{
			requestBuilder = new StringBuilder();
			requestBuilder.append("https://login.minecraft.net/?user=");
			requestBuilder.append(URLEncoder.encode(uinfo.getUsername(), "UTF-8"));
			requestBuilder.append("&password=");
			requestBuilder.append(URLEncoder.encode(uinfo.getPassword(), "UTF-8"));
			requestBuilder.append("&version=13");
		} catch (UnsupportedEncodingException e)
		{
			throw new GeneralException("Encoding format unsupported.", e);
		}
		
		URL loginURL = null;
		InputStream stream = null;
		try
		{
			loginURL = new URL(requestBuilder.toString());
			stream = loginURL.openStream();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
			throw new GeneralException("Login URL is malformed.", e);
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new GeneralException("Error connecting to login servers.", e);
		}
		Scanner scanner = new Scanner(stream);
		scanner.useDelimiter("\\A");
		String responseStr = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		
		if (!responseStr.contains(":"))
		{
			if (responseStr.equalsIgnoreCase("bad login"))
			{
				return new LoginResponse("Invalid username or password.");
			}
			else if (responseStr.equalsIgnoreCase("old version"))
			{
				return new LoginResponse("Outdated launcher.");
			}
			else
			{
				return new LoginResponse("Login failed: " + responseStr);
			}
		}
		
		String[] vals = responseStr.split(":");
		if (vals.length >= 4)
		{
			// Return the session ID and username.
			return new LoginResponse(vals[2], vals[3]);
		}
		else
		{
			return new LoginResponse("Received invalid response from server.");
		}
	}
	
	private UserInfo uinfo;
}
