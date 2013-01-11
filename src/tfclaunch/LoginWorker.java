package tfclaunch;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.swing.SwingWorker;

public class LoginWorker extends SwingWorker<LoginResponse, Void>
{
	public LoginWorker(UserInfo uinfo)
	{
		this.uinfo = uinfo;
	}
	
	@Override
	protected LoginResponse doInBackground() throws Exception
	{
		StringBuilder requestBuilder = new StringBuilder();
		requestBuilder.append("https://login.minecraft.net/?user=");
		requestBuilder.append(URLEncoder.encode(uinfo.getUsername(), "UTF-8"));
		requestBuilder.append("&password=");
		requestBuilder.append(URLEncoder.encode(uinfo.getPassword(), "UTF-8"));
		requestBuilder.append("&version=13");
		
		URL loginURL = new URL(requestBuilder.toString());
		InputStream stream = loginURL.openStream();
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
