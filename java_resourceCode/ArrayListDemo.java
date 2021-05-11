package mystudy.javaResourceCode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * ArrayList 和 LinkedList的源码分析
 * https://blog.csdn.net/weixin_36378917/article/details/81812210
 *
 * linkedList:
 * https://www.cnblogs.com/developer_chan/p/11439711.html
 *
 * #1.LinkedList底层数据结构为双向链表，非同步。
 * #2.LinkedList允许null值。
 * #3.由于双向链表，顺序访问效率高，而随机访问效率较低。
 * #4.注意源码中的相关操作，主要是构建双向链表
 */
public class ArrayListDemo {
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<>();
		List<Integer> linkedList = new LinkedList<>();
	}
}
