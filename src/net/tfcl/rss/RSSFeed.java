package net.tfcl.rss;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Class for reading info from RSS feeds.
 * 
 * @author Forkk13
 */
public class RSSFeed
{
	/**
	 * Returns the value of the first element with the given <code>subElementTagName</code>
	 * under <code>parent</code>.
	 * @param parent The parent element.
	 * @param subElementTagName The tag name of the element to look for.
	 * @param defValue Default value to return if no element with the given <code>subElementTagName</code> is found.
	 * @return The text content of the first element with the given <code>subElementTagName</code> under 
	 * <code>parent</code> or <code>defValue</code> if no elements with the given name are found.
	 */
	public static String getSubElementValue(Element parent, String subElementTagName, String defValue)
	{
		NodeList nodes = parent.getElementsByTagName(subElementTagName);
		if (nodes.getLength() <= 0)
			return defValue;
		else
			return nodes.item(0).getTextContent();
	}
	
	/**
	 * Parses the given Document into an RSS feed.
	 * @param xml RSS feed XML document to parse.
	 */
	public RSSFeed(Document xml)
	{
		Element docRoot = xml.getDocumentElement();
		Element channel = (Element)docRoot.getElementsByTagName("channel").item(0);
		
		
		// Feed info.
		title = getSubElementValue(channel, "title", "");
		description = getSubElementValue(channel, "description", "");
		language = getSubElementValue(channel, "language", "");
		
		
		// Items
		items = new ArrayList<RSSItem>();
		NodeList itemNodes = channel.getElementsByTagName("item");
		for (int i = 0; i < itemNodes.getLength(); i++)
		{
			items.add(new RSSItem((Element) itemNodes.item(i)));
		}
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	public String getLanguage()
	{
		return language;
	}
	
	public RSSItem[] getItems()
	{
		return items.toArray(new RSSItem[0]);
	}
	
	private String title;
	private String description;
	private String language;
	
	private List<RSSItem> items;
}
