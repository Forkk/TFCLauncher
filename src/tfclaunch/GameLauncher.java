package tfclaunch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Scanner;

import tfclaunch.utils.GeneralException;

/**
 * Launches the game.
 * 
 * @author Forkk13
 */
public class GameLauncher
{
	public GameLauncher(String installDir, LoginResponse sessionInfo)
	{
		this.installDir = installDir;
		this.sessionInfo = sessionInfo;
		
		this.binDir = new File(this.installDir, "bin");
	}
	
	/**
	 * Launches the game.
	 * @throws GeneralException If a problem occurs when launching the game. This should be handled by
	 * displaying an error message to the user.
	 */
	public void launch() throws GeneralException
	{
		// Load jars
		ArrayList<URL> jars = new ArrayList<URL>();
		
		// Order is important. We must load our mods onto the classpath *first* and 
		// they must be loaded in the order specified in loadorder.txt
		try
		{
			File jarModsDir = new File(binDir, "jarmods");
			File loadOrderFile = new File(binDir, "loadorder.txt");
			FileInputStream stream = new FileInputStream(loadOrderFile);
			Scanner scanner = new Scanner(stream);
			while (scanner.hasNext())
			{
				File jarFile = new File(jarModsDir, scanner.next());
				
				// Add them to the beginning of the list.
				// Mods higher on the list load last.
				jars.add(0, jarFile.toURI().toURL());
			}
			
			scanner.close();
		} catch (FileNotFoundException e)
		{
			throw new GeneralException("Load order file not found.", e);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
			throw new GeneralException("Load order file is invalid.", e);
		}
		
		// Now load Minecraft and LWJGL.
		try
		{
			String[] filenames = new String[] { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
			for (int i = 0; i < filenames.length; i++)
			{
				File jarFile = new File(binDir, filenames[i]);
				jars.add(jarFile.toURI().toURL());
			}
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
			throw new GeneralException("Failed to load jars due to malformed URL.", e);
		}
		
		
		// Load natives.
		File nativesDir = new File(binDir, "natives");
		
		System.setProperty("org.lwjgl.librarypath", nativesDir.getAbsolutePath());
		System.setProperty("net.java.games.input.librarypath", nativesDir.getAbsolutePath());
		
		
		// Set the target directory. FML / Forge should change it for us. (Thanks, CPW!)
		System.setProperty("minecraft.applet.TargetDirectory", installDir);
		
		
		// Now we load classes.
		ClassLoader loader = new URLClassLoader(jars.toArray(new URL[0]));
		
		Class<?> mainClass = null;
		try
		{
			mainClass = loader.loadClass("net.minecraft.client.Minecraft");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			throw new GeneralException("An error occurred when loading the game. " +
					"Couldn't find the main class.", e);
		}
		
		String args[] = new String[2];
		args[0] = sessionInfo.getUsername();
		args[1] = sessionInfo.getSessionID();
		
		try
		{
			mainClass.getMethod("main", String[].class).invoke(null, (Object) args);
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
			throw new GeneralException("An error occurred when loading the game. " +
					"Illegal access exception when calling main method.", e);
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			throw new GeneralException("An error occurred when loading the game. " +
					"Illegal argument passed.", e);
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
			
			System.err.println("Caused by:");
			e.getCause().printStackTrace();
			
			throw new GeneralException("An error occurred when loading the game. " +
					"Minecraft threw an exception: " + e.getCause().getClass().getName(), e);
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
			throw new GeneralException("An error occurred when loading the game. " +
					"Main method not found.", e);
		} catch (SecurityException e)
		{
			e.printStackTrace();
			throw new GeneralException("An error occurred when loading the game. " +
					"Security error: " + e.getMessage(), e);
		}
	}
	
	private String installDir;
	private LoginResponse sessionInfo;
	
	private File binDir;
}
