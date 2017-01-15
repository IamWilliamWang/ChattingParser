import java.util.ArrayList;
/**
 * 储存每个人的信息类
 * @author william
 */
public class Person {
	public String 分组;
	public String 消息对象;
	public ArrayList<TextContent> textContent = new ArrayList<>();
	
	public Person() {
		super();
	}

	public Person(String 分组, String 消息对象, ArrayList<TextContent> textContent) {
		super();
		this.分组 = 分组;
		this.消息对象 = 消息对象;
		this.textContent = textContent;
	}

	@Override
	public String toString() {
		return "Person [分组=" + 分组 + ", 消息对象=" + 消息对象 + ", textContent=" + textContent + "]";
	}
	
}
