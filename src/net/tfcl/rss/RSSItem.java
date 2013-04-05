package net.tfcl.rss;

import org.w3c.dom.Element;

/**
 * Represents an item in an RSS feed.
 * 
 * @author Forkk13
 */
public class RSSItem
{
	/**
	 * Constructs a new RSS item from the given XML element.
	 * @param element The item's XML element.
	 */
	public RSSItem(Element element)
	{
		title = RSSFeed.getSubElementValue(element, "title", "");
		link = RSSFeed.getSubElementValue(element, "link", "");
		description = RSSFeed.getSubElementValue(element, "description", "");
		author = RSSFeed.getSubElementValue(element, "author", "");
		category = RSSFeed.getSubElementValue(element, "category", "");
		comments = RSSFeed.getSubElementValue(element, "comments", "");
		guid = RSSFeed.getSubElementValue(element, "guid", "");
		date = RSSFeed.getSubElementValue(element, "pubDate", "");
		content = RSSFeed.getSubElementValue(element, "content:encoded", "");
	}
	
	public String getTitle()
	{
		return title;
	}
	public String getLink()
	{
		return link;
	}
	public String getDescription()
	{
		return description;
	}
	public String getAuthor()
	{
		return author;
	}
	public String getCategory()
	{
		return category;
	}
	public String getComments()
	{
		return comments;
	}
	public String getGuid()
	{
		return guid;
	}
	public String getDate()
	{
		return date;
	}
	public String getContent()
	{
		return content;
	}

	private String title;
	private String link;
	private String description;
	private String author;
	private String category;
	private String comments;
	private String guid;
	private String date;
	private String content;
}
