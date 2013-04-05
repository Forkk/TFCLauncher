package net.tfcl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.tfcl.utils.AppUtils;
import net.tfcl.utils.GeneralException;
import net.tfcl.utils.PathUtils;


public class AppSettings extends Properties
{
	public AppSettings(File settingsFile)
	{
		this.settingsFile = settingsFile;
	}
	
	public void loadSettings() throws GeneralException
	{
		try
		{
			load(new FileInputStream(settingsFile));
		} catch (FileNotFoundException e)
		{
			// Ignore this and don't load the file.
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new GeneralException("Failed to load settings file: " + e.getMessage(), e);
		}
	}
	
	public void saveSettings() throws GeneralException
	{
		try
		{
			if (!settingsFile.exists())
				settingsFile.createNewFile();
			
			store(new FileOutputStream(settingsFile), "TFCraft Launcher Settings File");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new GeneralException("Failed to save settings: " + e.getMessage(), e);
		}
	}
	
	public boolean getForceUpdate()
	{
		return forceUpdate;
	}
	
	public void setForceUpdate(boolean value)
	{
		forceUpdate = value;
	}
	
	
	public File getInstallPath()
	{
		return new File(getProperty("installDir", PathUtils.combine(AppUtils.getAppDataDir(), "minecraft")));
	}
	
	public void setInstallPath(File path)
	{
		setProperty("installDir", path.getPath());
	}
	
	
	private File settingsFile;
	
	private boolean forceUpdate;
	
	private static final long serialVersionUID = -2015787837436453223L;
}
