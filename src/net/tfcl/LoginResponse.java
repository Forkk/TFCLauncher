package net.tfcl;

/**
 * Class representing a response from the login servers.
 * 
 * @author Forkk13
 */
public class LoginResponse
{
	public LoginResponse(String username, String sessionID)
	{
		this.username = username;
		this.sessionID = sessionID;
		this.errorMsg = "";
	}
	
	public LoginResponse(String errorMsg)
	{
		this.username = "";
		this.sessionID = "";
		this.errorMsg = errorMsg;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getSessionID()
	{
		return sessionID;
	}
	
	public String getErrorMsg()
	{
		return errorMsg;
	}
	
	public boolean succeeded()
	{
		return getErrorMsg().isEmpty();
	}

	private String username;
	private String sessionID;
	private String errorMsg;
}
