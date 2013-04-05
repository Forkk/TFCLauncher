package net.tfcl.utils;

/**
 * An exception thrown by back-end code when it encounters an error. Should be handled
 * by displaying an error message to the user.
 * 
 * @author Forkk13
 */
public class GeneralException extends Exception
{
	public GeneralException(String message)
	{
		super(message);
	}
	
	public GeneralException(String message, Throwable cause)
	{
		super(message, cause);
	}

	private static final long serialVersionUID = 6899165103716575101L;
}
