package mystudy.javaResourceCode;

/**
 * https://blog.csdn.net/itchuxuezhe_yang/article/details/89966303
 *
 * 总结：
 *  (1) 在编译阶段就能够确定的字符串常量，完全没有必要创建String或StringBuffer对象。直接使用字符串常量的"+"连接操作效率最高。
 *  (2) StringBuffer对象的append效率要高于String对象的"+"连接操作。
 *  (3) 不停的创建对象是程序低效的一个重要原因。那么相同的字符串值能否在堆中只创建一个String对象那。
 *  显然拘留字符串能够做到这一点，除了程序中的字符串常量会被JVM自动创建拘留字符串之外，调用String的intern()方法也能做到这一点。
 *  当调用intern()时，如果常量池中已经有了当前String的值，那么返回这个常量指向拘留对象的地址。
 *  如果没有，则将String值加入常量池中，并创建一个新的拘留字符串对象。
 */
public class StringDemo {
	public static void main(String[] args) {
		String s1 = "abc";
		String s2 = "abc";
		String s3 = new String("abc");
		StringBuilder builder = new StringBuilder("abc");
		StringBuffer buffer = new StringBuffer("abc");

//		s1.intern();
		System.out.println(s1==s2);
		System.out.println(s1==s3);

		/**
		 * 关于字符串相等关系的争论
		 * 个人理解：
		 * 第一次比较sa==sb，通过构造器的方式创建字符串常量，他们的"Hello world"都存放在了方法栈中，然后将对应的内存地址赋值给sa和sb，
		 * 因为不是拘留字符串（存在字符串常量池并且唯一），所以sa==sb false
		 * 第二次比较sc==sd,先创建到常量池中一个"Hello world",然后将常量池地址赋值给sc和sd，因为sc和sd同时指向常量池中的"Hello world"，
		 * 所以sc==sd true
		 * 文章理解：
		 *   代码1中局部变量sa,sb中存储的是JVM在堆中new出来的两个String对象的内存地址。
		 *   虽然这两个String对象的值(char[]存放的字符序列)都是"Hello world"。 因此"=="比较的是两个不同的堆地址。
		 *   代码2中局部变量sc,sd中存储的也是地址，但却都是常量池中"Hello world"指向的堆的唯一的那个拘留字符串对象的地址 。自然相等了。
		 *
		 */
		//代码1
		String sa = new String("Hello world");
		String sb = new String("Hello world");
		System.out.println(sa==sb);
		//代码2
		String sc = "Hello world";
		String sd = "Hello world";
		System.out.println(sc==sd);

		/**
		 * 字符串“+”操作的内幕
		 * 个人理解：
		 * str1+ str2的叠加过程，其实现实创建一个StringBuilder（存放在栈中），里面的存储元素是str的元素（“ab”），然后StringBuilder调用append（）方法，
		 * 将str2中的元素进行合并，合并之后将StringBuilder地址赋值给str3，s则是在常量池中创建一个"abcd"拘留字符串，
		 * str3==s，自然结果为false
		 * str4 = "ab" + "cd"，其实jvm编译的时候会将它看做是str4 = ”abcd“，会在字符串常量池中创建一个拘留字符串”abcd“，
		 * 所以str4==s，结果为true
		 * 文章理解：
		 * 代码1中局部变量sa,sb存储的是堆中两个拘留字符串对象的地址。
		 * 而当执行sa+sb时，JVM首先会在堆中创建一个StringBuilder类，同时用sa指向的拘留字符串对象完成初始化，
		 * 然后调用append方法完成对sb所指向的拘留字符串的合并操作，
		 * 接着调用StringBuilder的toString()方法在堆中创建一个String对象，
		 * 最后将刚生成的String对象的堆地址存放在局部变量sab中。
		 * 而局部变量s存储的是常量池中"abcd"所对应的拘留字符串对象的地址。 sab与s地址当然不一样了。
		 * 这里要注意了，代码1的堆中实际上有五个字符串对象：三个拘留字符串对象、一个String对象和一个StringBuilder对象。
		 *
		 * 代码2中"ab"+"cd"会直接在编译期就合并成常量"abcd"， 因此相同字面值常量"abcd"所对应的是同一个拘留字符串对象，自然地址也就相同。
		 *
		 */
		//代码1
		String str1 = "ab";
		String str2 = "cd";
		String str3 = str1 + str2;
		String s = "abcd";
		System.out.println(str3 == s);
		//代码2
		String str4 = "ab" + "cd";
		System.out.println(str4 == s);

		/**
		 * StringBuffer与String的可变性问题。
		 *
		 * String和StringBuffer中的value[]都用于存储字符序列。但是,
		 * (1) String中的是常量(final)数组，只能被赋值一次。
		 * 比如：new String("abc")使得value[]={'a','b','c'}(查看jdk String 就是这么实现的)，之后这个String对象中的value[]再也不能改变了。
		 * 这也正是大家常说的，String是不可变的原因 。
		 * 注意：这个对初学者来说有个误区，有人说String str1=new String("abc"); str1=new String("cba");不是改变了字符串str1吗？
		 * 那么你有必要先搞懂对象引用和对象本身的区别。这里我简单的说明一下，对象本身指的是存放在堆空间中的该对象的实例数据(非静态非常量字段)。
		 * 而对象引用指的是堆中对象本身所存放的地址，一般方法区和Java栈中存储的都是对象引用，而非对象本身的数据。
		 *  (2) StringBuffer中的value[]就是一个很普通的数组，而且可以通过append()方法将新字符串加入value[]末尾。这样也就改变了value[]的内容和大小了。
		 *  比如：new StringBuffer("abc")使得value[]={'a','b','c','',''...}(注意构造的长度是str.length()+16)。
		 *  如果再将这个对象append("abc")，那么这个对象中的value[]={'a','b','c','a','b','c',''....}。
		 *  这也就是为什么大家说 StringBuffer是可变字符串 的涵义了。
		 *  从这一点也可以看出，StringBuffer中的value[]完全可以作为字符串的缓冲区功能。其累加性能是很不错的，在后面我们会进行比较。
		 *
		 * 总结，讨论String和StringBuffer可不可变。本质上是指对象中的value[]字符数组可不可变，而不是对象引用可不可变。
		 */

		/**
		 * StringBuffer与StringBuilder的线程安全性问题
		 * 在线程安全性方面，StringBuffer允许多线程进行字符操作。这是因为在源代码中StringBuffer的很多方法都被关键字synchronized 修饰了，而StringBuilder没有。
		 *
		 * 是不是String也不安全呢？事实上不存在这个问题，String是不可变的。线程对于堆中指定的一个String对象只能读取，无法修改。试问：还有什么不安全的呢？
		 */
	}
}
