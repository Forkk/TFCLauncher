package net.tfcl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.SwingWorker;

import net.tfcl.utils.GeneralException;
import net.tfcl.utils.OSUtils;
import net.tfcl.utils.PathUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Worker that downloads and installs the game.
 * 
 * @author Forkk13
 */
public class GameUpdater extends SwingWorker<Void, Void>
{
	public static final String updateCheckURL = "http://www.terrafirmacraft.com/files/tfcfiles1.json";

	public GameUpdater(File installDir, boolean forceUpdate)
	{
		this.installDir = installDir;
		this.forceUpdate = forceUpdate;
		this.forgeURL = new ArrayList<URL>();
		this.papiURL = new ArrayList<URL>();
		this.jarModURLs = new ArrayList<URL>();
		this.mlModURLs = new ArrayList<URL>();
		this.coreModURLs = new ArrayList<URL>();
	}

	private static void readInputStream(InputStream in, OutputStream out, boolean closeAfter)
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
			if (closeAfter)
			{
				in.close();
				out.close();
			}
		}
			}

	private static void readInputStream(InputStream in, OutputStream out) throws IOException
	{
		readInputStream(in, out, true);
	}

	private static String urlFilename(URL url) throws GeneralException
	{
		String path = url.getFile();
		try
		{
			return URLDecoder.decode(path.substring(path.lastIndexOf('/') + 1), "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			throw new GeneralException("Failed to decode URL.", e);
		}
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		binDir = new File(installDir, "bin");
		if (!binDir.exists())
			binDir.mkdirs();

		// Determine the current installed version.
		File versionFile = new File(binDir, "version");
		if (versionFile.exists())
		{
			FileInputStream stream = new FileInputStream(versionFile);
			Scanner scanner = new Scanner(stream);
			scanner.useDelimiter(System.getProperty("line.separator"));
			installedVersion = scanner.hasNext() ? scanner.next() : "";
			mcVersionInstalled = scanner.hasNext() ? scanner.next() : "";
			forgeVersionInstalled = scanner.hasNext() ? scanner.next() : "";
			papiVersionInstalled = scanner.hasNext() ? scanner.next() : "";
			scanner.close();
		}
		//Get versions and download links for all of the files.
		getFileLists();
		setProgress(10);

		setStatus("Downloading game...");

		String statusPrefix = "Downloading " + versionName + ":";


		if (forceUpdate || !mcVersion.equalsIgnoreCase(mcVersionInstalled))
		{
			// Download minecraft.jar
			{
				setStatus("Downloading game: minecraft.jar");
				File mcJar = new File(binDir, "minecraft.jar.tmp");
				FileOutputStream out = new FileOutputStream(mcJar);
				InputStream in = mcJarURL.openStream();
				readInputStream(in, out);
			}
			downloadLWJGL();
			removeMetaInf();
		}
		setProgress(20);
		File loadOrder = new File(binDir, "loadorder.txt");
		PrintWriter writer = null;
		if(forceUpdate || !versionID.equalsIgnoreCase(installedVersion))
		{
			writer = new PrintWriter(loadOrder);
			writer.println(urlFilename(forgeURL.get(0)));
			writer.println(urlFilename(papiURL.get(0)));
		}

		if (forceUpdate || !forgeVersion.equalsIgnoreCase(forgeVersionInstalled))
		{
			//Download Forge
			{
				deleteFileName(new File(installDir, "mods"), "minecraftforge-universal");
				setStatus("Downloading Forge: "+forgeVersion);
				/*if(writer == null)
					writer = new PrintWriter(loadOrder);*/
				downloadFileList(forgeURL, new File(binDir, "jarmods"), statusPrefix);
			}
		}
		setProgress(40);
		if (forceUpdate || !papiVersion.equalsIgnoreCase(papiVersionInstalled))
		{
			//Download PlayerAPI
			{
				deleteFileName(new File(installDir, "mods"), "Player API");
				setStatus("Downloading PlayerAPI: "+papiVersion);
				/*if(writer == null)
					writer = new PrintWriter(loadOrder);*/
				downloadFileList(papiURL, new File(binDir, "jarmods"), statusPrefix);
			}
		}
		setProgress(50);
		if (forceUpdate || !versionID.equalsIgnoreCase(installedVersion))
		{
			//Download Jar Mods
			{
				if(writer == null)
					writer = new PrintWriter(loadOrder);
				downloadFileList(jarModURLs, new File(binDir, "jarmods"),
						writer, statusPrefix);
			}
		}
		setProgress(60);
		if(writer != null)
			writer.close();
		if (forceUpdate || !versionID.equalsIgnoreCase(installedVersion))
		{
			//Download Core Mods
			{
				downloadFileList(coreModURLs, new File(installDir, "coremods"),
						writer, statusPrefix);
			}
		}
		setProgress(80);
		// If the installed version isn't the latest version.
		if (forceUpdate || !versionID.equalsIgnoreCase(installedVersion))
		{
			deleteFileName(new File(installDir, "mods"), "TFCraft-Universal-Core");
			downloadFileList(mlModURLs, new File(installDir, "mods"), statusPrefix);
		}
		// Write version file.
		try
		{
			writer = new PrintWriter(versionFile);
			writer.println(versionID);
			writer.println(mcVersion);
			writer.println(forgeVersion);
			writer.println(papiVersion);
			writer.close();
		} catch (IOException e)
		{
			throw new GeneralException("Failed to write version file. " + e.getMessage(), e);
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

	private void getFileLists() throws JSONException, IOException, GeneralException
	{
		setStatus("Checking for updates...");

		URL jsonURL = null;
		try
		{
			jsonURL = new URL(updateCheckURL);
		} catch (MalformedURLException e)
		{
			throw new GeneralException("Malformed update check URL", e);
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
		mcVersion = latestUpdate.getString("mcVersion");
		forgeVersion = latestUpdate.getString("forgeVersion");
		papiVersion = latestUpdate.getString("papiVersion");
		// Load URLs. Throw an exception if they're malformed.
		try
		{
			// Minecraft Jar URL
			mcJarURL = new URL(latestUpdate.getString("mcjar"));
			//Forge URL
			JSONArray forgeArray = latestUpdate.getJSONArray("forge");
			for (int i = 0; i < forgeArray.length(); i++)
			{
				forgeURL.add(new URL(forgeArray.getString(i)));
			}
			//PlayerAPI URL
			JSONArray papiArray = latestUpdate.getJSONArray("papi");
			for (int i = 0; i < papiArray.length(); i++)
			{
				papiURL.add(new URL(papiArray.getString(i)));
			}

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
			throw new GeneralException("One of the file list URLs is malformed.", e);
		}
	}

	@SuppressWarnings("unused")
	private void deleteRecursive(File file) throws IOException
	{
		if (file.isDirectory())
		{
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				deleteRecursive(files[i]);
			}
		}
		else
		{
			file.delete();
		}
	}

	private void deleteFileName(File directory, String filename) throws IOException
	{
		if (directory.isDirectory())
		{
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if(files[i].getName().contains(filename))
					files[i].delete();
			}
		}
	}

	/**
	 * Downloads the given list of URLs into the given folder.
	 * @param urls list of URLs to download
	 * @param dest destination folder to download to
	 * @param fileListPath if not null, saves a list of the files in the order they were
	 * downloaded to this file
	 * @param statusPrefix prefix to use for setStatus
	 * @throws IOException if an IOException is thrown
	 * @throws GeneralException If a problem occurs. Should be handled by displaying an error message to the user.
	 */
	private void downloadFileList(ArrayList<URL> urls, File dest, String statusPrefix) 
			throws IOException, GeneralException
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

	/**
	 * Downloads the given list of URLs into the given folder.
	 * @param urls list of URLs to download
	 * @param dest destination folder to download to
	 * @param loadOrderWriter if not null, saves a list of the files in the order they were
	 * downloaded to this file
	 * @param statusPrefix prefix to use for setStatus
	 * @throws IOException if an IOException is thrown
	 * @throws GeneralException If a problem occurs. Should be handled by displaying an error message to the user.
	 */
	private void downloadFileList(ArrayList<URL> urls, File dest, PrintWriter writer, String statusPrefix) throws IOException, GeneralException
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

			if (writer != null)
			{
				writer.println(urlFilename(urls.get(i)));
			}
		}
	}

	private void downloadLWJGL() throws IOException, GeneralException
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
			throw new GeneralException("OS not recognized.");
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
		ZipFile nativesJar = new ZipFile(PathUtils.combine(installDir.getPath(), "bin",
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

	private void removeMetaInf() throws GeneralException
	{
		setStatus("Removing META-INF...");

		File inFile = new File(binDir, "minecraft.jar.tmp");
		File outFile = new File(binDir, "minecraft.jar");

		try
		{
			ZipInputStream in = new ZipInputStream(new FileInputStream(inFile));
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile));

			ZipEntry entry;

			while ((entry = in.getNextEntry()) != null)
			{
				if (entry.getName().toLowerCase().contains("meta-inf"))
					continue;

				out.putNextEntry(entry);
				readInputStream(in, out, false);
				out.closeEntry();
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e)
		{
			// This doesn't seem physically possible! We just downloaded that file!
			e.printStackTrace();
			throw new GeneralException("Can't find minecraft.jar.tmp!", e);
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new GeneralException("Failed to read minecraft.jar.tmp!", e);
		} finally
		{

		}
	}

	protected File installDir;
	private boolean forceUpdate;

	private String status;

	private URL mcJarURL;
	private ArrayList<URL> forgeURL;
	private ArrayList<URL> papiURL;
	private ArrayList<URL> jarModURLs;
	private ArrayList<URL> mlModURLs;
	private ArrayList<URL> coreModURLs;

	private String versionID;
	private String versionName;
	private String mcVersion;
	private String forgeVersion;
	private String papiVersion;

	private String mcVersionInstalled;
	private String forgeVersionInstalled;
	private String papiVersionInstalled;
	private String installedVersion;

	private File binDir;
}
