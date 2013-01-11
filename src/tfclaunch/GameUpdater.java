package tfclaunch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tfclaunch.utils.OSUtils;
import tfclaunch.utils.PathUtils;

/**
 * Worker that downloads and installs the game.
 * 
 * @author Forkk13
 */
public class GameUpdater extends SwingWorker<Void, Void>
{
	public static final String updateCheckURL = "http://localhost/tfcfiles.json";
	
	public GameUpdater(String installDir, boolean forceUpdate)
	{
		this.installDir = installDir;
		this.forceUpdate = forceUpdate;
		this.jarModURLs = new ArrayList<URL>();
		this.mlModURLs = new ArrayList<URL>();
		this.coreModURLs = new ArrayList<URL>();
	}
	
	private static void readInputStream(InputStream in, OutputStream out)
			throws IOException
	{
		try
		{
			byte[] buf = new byte[1024];
			int lastRead;
			while ((lastRead = in.read(buf)) >= 0)
				out.write(buf, 0, lastRead);
		} finally
		{
			in.close();
			out.close();
		}
	}
	
	private static String urlFilename(URL url)
	{
		String path = url.getFile();
		try
		{
			return URLDecoder.decode(path.substring(path.lastIndexOf('/') + 1), "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Failed to decode URL.");
		}
	}
	
	@Override
	protected Void doInBackground() throws Exception
	{
		binDir = new File(PathUtils.combine(installDir, "bin"));
		if (!binDir.exists())
			binDir.mkdirs();
		
		// Determine the current installed version.
		File versionFile = new File(binDir, "version");
		if (versionFile.exists())
		{
			FileInputStream stream = new FileInputStream(versionFile);
			Scanner scanner = new Scanner(stream);
			scanner.useDelimiter("\\A");
			installedVersion = scanner.hasNext() ? scanner.next() : "";
			scanner.close();
		}
		
		getFileLists();
		setProgress(10);
		
		// If the installed version isn't the latest version.
		if (forceUpdate || versionID != installedVersion)
		{
			downloadFiles();
			setProgress(75);
			downloadLWJGL();
			setProgress(100);
		}
		
		return null;
	}
	
	private void setStatus(String status)
	{
		String oldStatus = this.status;
		this.status = status;
		firePropertyChange("status", oldStatus, this.status);
	}
	
	public String getStatus()
	{
		return status;
	}
	
	private void getFileLists() throws JSONException, IOException
	{
		setStatus("Checking for updates...");
		
		URL jsonURL = null;
		try
		{
			jsonURL = new URL(updateCheckURL);
		} catch (MalformedURLException e)
		{
			throw new RuntimeException("Malformed update check URL");
		}
		
		InputStream stream = jsonURL.openStream();
		Scanner scanner = new Scanner(stream);
		scanner.useDelimiter("\\A");
		String jsonString = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		
		JSONArray fileListJSON = new JSONArray(jsonString);
		JSONObject latestUpdate = fileListJSON.getJSONObject(0);
		
		versionID = latestUpdate.getString("id");
		versionName = latestUpdate.getString("name");
		
		// Load URLs. Throw an exception if they're malformed.
		try
		{
			// Minecraft Jar URL
			mcJarURL = new URL(latestUpdate.getString("mcjar"));
			
			// Jar mods
			JSONArray jarModArray = latestUpdate.getJSONArray("jarmods");
			for (int i = 0; i < jarModArray.length(); i++)
			{
				jarModURLs.add(new URL(jarModArray.getString(i)));
			}
			
			// Core mods.
			JSONArray coreModArray = latestUpdate.getJSONArray("coremods");
			for (int i = 0; i < coreModArray.length(); i++)
			{
				coreModURLs.add(new URL(coreModArray.getString(i)));
			}
			
			// ML mods
			JSONArray mlModArray = latestUpdate.getJSONArray("mlmods");
			for (int i = 0; i < mlModArray.length(); i++)
			{
				mlModURLs.add(new URL(mlModArray.getString(i)));
			}
		} catch (MalformedURLException e)
		{
			throw new RuntimeException("One of the file list URLs is malformed.");
		}
	}
	
	private void downloadFiles() throws IOException
	{
		setStatus("Downloading game...");
		
		// Download minecraft.jar
		{
			setStatus("Downloading game: minecraft.jar");
			File mcJar = new File(binDir, "minecraft.jar");
			FileOutputStream out = new FileOutputStream(mcJar);
			InputStream in = mcJarURL.openStream();
			readInputStream(in, out);
		}
		
		String statusPrefix = "Downloading " + versionName + ":";
		
		// Download jar mods. These will be loaded through a classloader on launch
		downloadFileList(jarModURLs, new File(binDir, "jarmods"), statusPrefix);
		
		// Download other mods.
		downloadFileList(mlModURLs, new File(installDir, "mods"), statusPrefix);
		downloadFileList(coreModURLs, new File(installDir, "coremods"), statusPrefix);
	}
	
	private void downloadFileList(ArrayList<URL> urls, File dest, String statusPrefix) throws IOException
	{
		if (!dest.exists())
			dest.mkdirs();
		
		for (int i = 0; i < urls.size(); i++)
		{
			setStatus(statusPrefix + urlFilename(urls.get(i)));
			File destFile = new File(dest, urlFilename(urls.get(i)));
			FileOutputStream out = new FileOutputStream(destFile);
			InputStream in = urls.get(i).openStream();
			readInputStream(in, out);
		}
	}
	
	private void downloadLWJGL() throws IOException
	{
		setStatus("Downloading LWJGL...");
		
		String urlPrefix = "http://s3.amazonaws.com/MinecraftDownload/";
		
		URL downloadURLs[] = new URL[4];
		downloadURLs[0] = new URL(urlPrefix + "lwjgl_util.jar");
		downloadURLs[1] = new URL(urlPrefix + "lwjgl.jar");
		downloadURLs[2] = new URL(urlPrefix + "jinput.jar");
		
		switch (OSUtils.getOS())
		{
		case WINDOWS:
			downloadURLs[3] = new URL(urlPrefix + "windows_natives.jar");
			break;
		
		case LINUX:
			downloadURLs[3] = new URL(urlPrefix + "linux_natives.jar");
			break;
		
		case OSX:
			downloadURLs[3] = new URL(urlPrefix + "macosx_natives.jar");
			break;
		
		default:
			throw new RuntimeException("OS not recognized.");
		}
		
		for (int i = 0; i < downloadURLs.length; i++)
		{
			setStatus("Downloading LWJGL: " + urlFilename(downloadURLs[i]));
			String dest = PathUtils.combine(binDir.getPath(),
					urlFilename(downloadURLs[i]));
			FileOutputStream out = new FileOutputStream(dest);
			InputStream in = downloadURLs[i].openStream();
			readInputStream(in, out);
		}
		
		setStatus("Extracting natives...");
		
		File nativesDir = new File(PathUtils.combine(binDir.getPath(),
				"natives"));
		if (!nativesDir.exists())
			nativesDir.mkdirs();
		
		Enumeration<? extends ZipEntry> entries;
		ZipFile nativesJar = new ZipFile(PathUtils.combine(installDir, "bin",
				urlFilename(downloadURLs[3])));
		
		entries = nativesJar.entries();
		while (entries.hasMoreElements())
		{
			ZipEntry entry = entries.nextElement();
			if (entry.isDirectory() || entry.getName().contains("/")
					|| entry.getName().contains("\\"))
				continue;
			setStatus("Extracting natives: " + entry.getName());
			
			String dest = PathUtils.combine(nativesDir.getPath(),
					entry.getName());
			FileOutputStream out = new FileOutputStream(dest);
			InputStream in = nativesJar.getInputStream(entry);
			readInputStream(in, out);
		}
		
		nativesJar.close();
	}
	
	private String installDir;
	private boolean forceUpdate;
	
	private String status;
	
	private URL mcJarURL;
	private ArrayList<URL> jarModURLs;
	private ArrayList<URL> mlModURLs;
	private ArrayList<URL> coreModURLs;
	
	private String versionID;
	private String versionName;
	
	private String installedVersion;
	
	private File binDir;
}
