package mystudy.javaResourceCode;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

/**
 * hashMap源码解析：
 * https://blog.csdn.net/m0_37914588/article/details/82287191
 *
 *
 * 位移运算：
 * 000000000011
 * 000000110000
 * 16 + 32  = 48
 * 000011 >>1
 * 48>>4
 */
public class HashMapDemo {
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		HashMap<Object, Object> objectObjectHashMap = new HashMap<>(100);
		objectObjectHashMap.put(1,2);
		System.out.println("1 " + objectObjectHashMap.size());
		Hashtable<Object, Object> objectObjectHashtable = new Hashtable<>();

		Map<String, Object> treeMap = new TreeMap<>();
		System.out.println(1<<4);	//16 0000001 左移 4位 -》0010000 =
		System.out.println(3<<4);	//48
		System.out.println(3>>1); 	//1
		System.out.println(48>>4); //3
	}
}
