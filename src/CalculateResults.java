import java.util.ArrayList;

class OnePerson_result implements Comparable<OnePerson_result> {
	public String PersonName;
	public int iSentTextsToHer = 0;
	public int iRecievedTextsFromHer = 0;
	public int[] theTextsISentEveryMonth = new int[12];
	public int[] theTextsIRecievedEveryMonth = new int[12];

	public OnePerson_result() {
		for (int i = 0; i < 12; i++) {
			theTextsISentEveryMonth[i] = 0;
			theTextsIRecievedEveryMonth[i] = 0;
		}
	}

	@Override
	public int compareTo(OnePerson_result person) {
		// TODO Auto-generated method stub
		return Integer.compare(person.iRecievedTextsFromHer + person.iSentTextsToHer,
				this.iRecievedTextsFromHer + this.iSentTextsToHer);
	}

}

public class CalculateResults {
	public int 发送总数 = 0;
	public int 接受总条数 = 0;
	ArrayList<OnePerson_result> people = new ArrayList<>();

	public CalculateResults(ArrayList<Person> personList) {
		for (Person person : personList) {
			OnePerson_result This = new OnePerson_result();
			This.PersonName = person.消息对象;

			for (TextContent textContent : person.textContent) {
				if (textContent.senderName.contains(person.消息对象) // 她发出的
						|| person.消息对象.contains(textContent.senderName)) {
					This.iRecievedTextsFromHer++;
					This.theTextsIRecievedEveryMonth[textContent.date.getMonth()]++;
				}

				else { // 我发出的
					This.theTextsISentEveryMonth[textContent.date.getMonth()]++;
					This.iSentTextsToHer++;
				}
			}

			this.发送总数 += This.iSentTextsToHer;
			this.接受总条数 += This.iRecievedTextsFromHer;

			this.people.add(This);
		}

	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("*************报告日志*************\n");
		sb.append("人数总计：" + this.people.size() + "\n");
		sb.append("总消息条数：" + (this.发送总数 + this.接受总条数) + "\n");
		sb.append("总发送条数：" + this.发送总数 + "\n");
		sb.append("总接受条数：" + this.接受总条数 + "\n");
		sb.append("\n");
		for (int nowIndex = 0; nowIndex < this.people.size(); nowIndex++) {
			OnePerson_result result = this.people.get(nowIndex);
			// for (OnePerson_result result : this.people) {
			sb.append("No." + (nowIndex + 1) + " " + result.PersonName + ":"
					+ (result.iRecievedTextsFromHer + result.iSentTextsToHer) + "\n");
			sb.append("发出" + result.iSentTextsToHer + ",接受" + result.iRecievedTextsFromHer + "条消息。发出占比："
					+ 1.0 * result.iSentTextsToHer / (result.iRecievedTextsFromHer + result.iSentTextsToHer) + "\n");
			for (int i = 0; i < 12; i++) {
				sb.append("[" + (i + 1) + "月记录"
						+ (result.theTextsIRecievedEveryMonth[i] + result.theTextsISentEveryMonth[i]) + "条(发出"
						+ result.theTextsISentEveryMonth[i] + "条，接收" + result.theTextsIRecievedEveryMonth[i] + "条，占比"
						+ (result.theTextsIRecievedEveryMonth[i] + (result.theTextsISentEveryMonth[i]) == 0 ? 0
								: 1.0 * result.theTextsISentEveryMonth[i]
										/ (result.theTextsIRecievedEveryMonth[i] + result.theTextsISentEveryMonth[i]))
						+ ")\n");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}
