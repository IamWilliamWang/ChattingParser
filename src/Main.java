import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class LogUtil {

	public static void print(String print) {
		printAndLog(print,false);
	}
	
	public static void println() {
		printAndLog("",true);
	}
	
	public static void println(String print) {
		printAndLog(print,true);
	}
	
	public static void Log(String string,String fileName) {
		// TODO Auto-generated method stub
		Log(string,fileName,false);
	}
	
	public static void Log(String logContent,String fileName,boolean printNextLine) {
		try {
			FileWriter fileWriter = new FileWriter(fileName,true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(logContent+(printNextLine?"\n":""));
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void printAndLog(String print,boolean printNextLine) {
		Log(print,Main.logFileName,printNextLine);
		if(printNextLine)
			System.out.println(print);
		else
			System.out.print(print);
	}
}

/**
 * 分析QQ聊天导出的记录
 * @author william
 */
public class Main {

	private static String readFileName = "friends.txt"; //文件名
	public static String logFileName = "Log.txt"; //日志文件
	private static String logFileName2 = "FullLog.txt"; //日志文件
	private ArrayList<Person> personList = new ArrayList<>(); //人物列表
	private Person thisPerson = new Person(); //此刻操作的人物
	
	/**
	 * 读取文件头
	 * @param bufferedReader 读取的文件流
	 * @throws IOException 读到文件末尾时产生
	 */
	private void readHead(BufferedReader bufferedReader) throws IOException {
		
//		for(int 扫描到的等号数 = 0; 扫描到的等号数 < 3;) {
		while(true) {
			String buffer = bufferedReader.readLine();
			
			if(buffer==null) //防止BufferedReader已经到结尾
				throw new IOException("end of file.");
				
			if(buffer.equals("")) //不处理所有的换行
				continue;
//			if(buffer.startsWith("============================================================")) {
//				扫描到的等号数++;
//				continue;
//			}
			if(buffer.startsWith("消息分组:")) {
				thisPerson.分组 = buffer.replace("消息分组:", "");
				continue;
			}
			if(buffer.startsWith("消息对象:")) {
				thisPerson.消息对象 = buffer.replace("消息对象:", "");
				bufferedReader.readLine();
				break;
			}
			
		}

	}
	
	/**
	 * 读取中间的消息部分
	 * @param bufferedReader 输入流
	 * @throws IOException 读到文件末尾
	 * @throws ParseException 日期解析失败
	 */
	private void readContentText(BufferedReader bufferedReader) throws IOException, ParseException {
		boolean isContent = false; //此条是否为内容片段
		TextContent thisTextContent = new TextContent(); //当前操作的TextContent
		while(true) {
			String buffer = bufferedReader.readLine();
			
			if(buffer==null) {//防止BufferedReader已经到结尾
				this.personList.add(thisPerson);
				throw new IOException("end of file.");
			}
			if(buffer.equals("")) 
				continue;
			if(buffer.startsWith("201") && !isContent) { //检测到是时间
				SimpleDateFormat generateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				if(buffer.lastIndexOf(" ")==-1) { //something wrong
					thisTextContent = new TextContent();
					continue;
				}
				Date date = generateFormatter.parse(buffer.substring(0, 18));
				thisTextContent.date = date;
				thisTextContent.senderName = buffer.substring(buffer.indexOf(" ",17)+1);
				isContent = true;
			}
			else if(isContent) { //是内容
				thisTextContent.words = buffer;
				isContent = false;
				if(thisTextContent.date!=null && !thisTextContent.senderName.isEmpty() && !thisTextContent.words.isEmpty())
					thisPerson.textContent.add(thisTextContent);
				thisTextContent = new TextContent();
			}
			else if(buffer.startsWith("============================================================")) //读到下一个人的开头就中断
				break;
		}
	}
	
	/**
	 * 结果报告
	 * @param percent 发占比
	 * @return 报告子串
	 */
	private String relationShipInformation(Double percent) {
		String result = percent.toString();
		if(percent > 0.9)
			result+="(程序可能漏计了一些)";
		else if(percent > 0.75)
			result+="(关系：过度关心，但不领情)";
		else if(percent > 0.6)
			result+="(关系：你很在乎ta)";
		else if(percent > 0.5)
			result+="(关系：平等)";
		else 
			result+="(关系：ta关心你)";
		return result;
	}
	
	/**
	 * 计算比例
	 * @param thisPerson
	 */
	private void calculateRecieveAndSend(Person thisPerson) {
		//计算收发比例
		int iSend = 0,iRecieve = 0;
		for(TextContent content : thisPerson.textContent) {
			if(content.senderName.contains(thisPerson.消息对象)
					|| thisPerson.消息对象.equals(content.senderName))
				iRecieve++;
			else
				iSend++;
		}
		LogUtil.println("发出"+iSend+",接受"+iRecieve+"条消息。发出占比："+relationShipInformation(1.0*iSend/(iSend+iRecieve)));
		
		//计算每月记录
		int[] counts = new int[12];
		for(int i=0;i<12;i++)
			counts[i] = 0;
		for(TextContent textContent : this.thisPerson.textContent) {
			counts[textContent.date.getMonth()]++;
		}
		for(int i=0;i<12;i++)
			LogUtil.print("["+(i+1)+"月记录"+counts[i]+"条]");
		LogUtil.println();
	}
	
	/**
	 * 统计ThisPerson，子过程
	 */
	private void calculateThisPerson() {
		LogUtil.println(this.thisPerson.消息对象+":"+this.thisPerson.textContent.size());
		calculateRecieveAndSend(this.thisPerson);
		
	}
	
	private void clearThisPersonRecord() {
		thisPerson = new Person();
	}
	
	/**
	 * 统计单个人
	 * @param bufferedReader
	 * @throws Exception
	 */
	public void readAndCalculateOnePerson(BufferedReader bufferedReader) throws Exception {
		this.readHead(bufferedReader);
		try {
		this.readContentText(bufferedReader);
		}
		catch(IOException e) {}
		finally {
			this.personList.add(thisPerson);
		}
		this.calculateThisPerson();
		this.clearThisPersonRecord();
		LogUtil.println("--------------------------------------------------");
//		this.personList.sort(new Comparator<Person>() {
//
//			@Override
//			public int compare(Person o1, Person o2) {
//				// TODO Auto-generated method stub
//				return o2.textContent.size()-o1.textContent.size();
//			}
//			
//		});
	}
	
	private void calculateAll() {
//		for (Person person : personList) {
//			System.out.println(this.thisPerson.消息对象+":"+this.thisPerson.textContent.size());
//		}
		LogUtil.println("一共"+this.personList.size()+"个人的记录。");
	}
	
	private void calculateAll(ArrayList<Person> personList) {
		// TODO Auto-generated method stub
		CalculateResults calculateResult = new CalculateResults(personList);
		calculateResult.people.sort(null);
		LogUtil.Log(calculateResult.toString(),logFileName2);
	}
	
	/**
	 * 主函数
	 * @param bufferedReader
	 */
	public void readAllAndCalculate(BufferedReader bufferedReader) {
		//装载
		while(true)
		{
			try {
				readAndCalculateOnePerson(bufferedReader);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				break;
//				e.printStackTrace();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//分析
		calculateAll();
		calculateAll(this.personList);
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
		if(args.length==1)	logFileName = args[0];
		try {
			FileReader fileReader = new FileReader(readFileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			main.readAllAndCalculate(bufferedReader);
			bufferedReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
