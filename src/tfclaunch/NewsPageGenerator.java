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
		
		docBuilder.append("<link href='http://fonts.googleapis.com/css?family=PT+Sans:regular,italic,bold,bolditalic' rel='stylesheet' type='text/css'>");
		
		docBuilder.append("<style type='text/css'>" +
				"p { margin-top: 4px; margin-bottom: 4px; font-size: 14pt; }" +
				"ul { list-style: inside disc; font-size: 14pt; margin-top: 0px; }" +
				"ol { list-style: inside decimal; font-size: 14pt; margin-bottom: 0px; }" +
				
				"a:hover, .entry-content a, .dropcap.colored, .source, input[type=submit]:hover { color: #33cc33; }" +
				".entry-title a { color: #666666; text-decoration: none; }" +
				
				".entry-title { font-size: 22pt; font-weight: bold; }" +
				
				"h1, h2, h3, h4, h5, h6 { font-weight: bold; } " +
				
				"h1 { font-size: 30pt; } h2 { font-size: 26pt; } h3 { font-size: 22pt; } " +
				"h4 { font-size: 18pt; } h5 { font-size: 14pt; } h6 { font-size: 10pt; }" +
				
				"body { " +
					"font-family: 'PT Sans', arial, serif; " +
					"color: #666666;" +
					"background: #eeeeee;" +
					"text-shadow: 0px 1px 0px #fff;" +
					"font-size: 14px;" +
					"line-height: 1.5; " +
					"margin-left: 64px;" +
					"margin-right: 64px;" +
				"}" +
				"</style>");
		
		docBuilder.append("</head>");
		
		
		// Body
		docBuilder.append("<body>");
		docBuilder.append("<center><img style='margin-bottom: 0px;' src='http://www.terrafirmacraft.com/logo-blog.png' alt='logo' id='logo'></center>");
		docBuilder.append("<h1 style='text-align: center; margin-top: 0px; font-size: 10px;'>" + feed.getDescription() + "</h3>");
		
		RSSItem[] items = feed.getItems();
		for (int i = 0; i < items.length; i++)
		{
			RSSItem item = items[i];
			
			docBuilder.append("<hr />");
			
			docBuilder.append("<div class='entry'>");
			docBuilder.append("<h3 class='entry-title'>" +
					"<a href='" + item.getLink() + "'>" + item.getTitle() + "</a></h1>");
			
			docBuilder.append("<div class='entry-content'>");
			docBuilder.append(item.getContent());
			docBuilder.append("</div>");
			
			docBuilder.append("</div>");
		}
		
		docBuilder.append("</body>");
		
		
		docBuilder.append("</html>");
		
		return docBuilder.toString();
	}
}
