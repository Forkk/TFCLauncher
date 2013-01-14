package tfclaunch.utils;


public class AppUtils
{
	public static String getAppDataDir()
	{
		String dir = PathUtils.combine(System.getProperty("user.home"), "tfcraft");
		
		switch (OSUtils.getOS())
		{
		case WINDOWS:
			dir = PathUtils.combine(System.getenv("APPDATA"), "tfcraft");
			break;
			
		case LINUX:
			dir = PathUtils.combine(System.getProperty("user.home"), ".tfcraft");
			break;
			
		case OSX:
			dir = PathUtils.combine(System.getProperty("user.home"), "Library", 
					"Application Support", "tfcraft");
			break;
			
		default:
			break;
		}
		
		return dir;
	}
}
