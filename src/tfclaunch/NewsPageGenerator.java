package tfclaunch;

import org.w3c.dom.Document;

import tfclaunch.rss.RSSFeed;
import tfclaunch.rss.RSSItem;


/**
 * Generates an HTML document from an RSS feed to display in a JTextPane.
 *  
 * @author Forkk13
 */
public class NewsPageGenerator
{
	public static String generateNewsPage(Document rssFeedXML)
	{
		RSSFeed feed = new RSSFeed(rssFeedXML);
		
		StringBuilder docBuilder = new StringBuilder();
		
		docBuilder.append("<html>");
		
		
		// Head
		docBuilder.append("<head>");
		docBuilder.append("<title>" + feed.getTitle() + "</title>");
		docBuilder.append("</head>");
		
		
		// Body
		docBuilder.append("<body>");
		docBuilder.append("<h1>" + feed.getTitle() + "</h1>");
		docBuilder.append("<h3>" + feed.getDescription() + "</h3>");
		
		RSSItem[] items = feed.getItems();
		for (int i = 0; i < items.length; i++)
		{
			RSSItem item = items[i];
			
			docBuilder.append("<hr />");
			docBuilder.append("<h2>" + item.getTitle() + "</h2>");
			docBuilder.append("<p>" + item.getContent() + "</p>");
		}
		
		docBuilder.append("</body>");
		
		
		docBuilder.append("</html>");
		
		return docBuilder.toString();
	}
}
