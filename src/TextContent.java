import java.util.Date;
/**
 * 储存每条消息的类
 * @author william
 */
public class TextContent {
	public Date date;
	public String senderName;
	public String words;
	
	public TextContent() {
		super();
	}

	public TextContent(Date date, String senderName, String words) {
		super();
		this.date = date;
		this.senderName = senderName;
		this.words = words;
	}

	@Override
	public String toString() {
		return "TextContent [date=" + date + ", senderName=" + senderName + ", words=" + words + "]";
	}
	
}
