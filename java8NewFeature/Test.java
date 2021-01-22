package demo.java8NewFeature;

import java.util.Objects;
import java.util.function.Predicate;

public class Test {
	public static void main(String[] args) {
		// 将数字字符串转换为整数类型
		Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
		Converter<String, Integer> converter1 = (from -> Integer.valueOf(from));
		int a  = converter.converter("123");
		System.out.println(a);
		int b = converter1.converter("456");
		System.out.println(b);
		//通过静态方法引用来表示
		Converter<String, Integer> converter2 = Integer::valueOf;
		int c = converter2.converter("789");
		System.out.println(c);


		//Java 8允许您通过::关键字传递方法或构造函数的引用, 也可以引用对象方法
		SomeThing someThing = new SomeThing();
		Converter<String, String> converter3 = someThing::startsWith;
		String d = converter3.converter("java");
		System.out.println(d);

		//使用构造函数引用来将他们关联起来，而不是手动实现一个完整的工厂：
		PersonFactory<Person> personPersonFactory = Person::new;
		Person person = personPersonFactory.create("peter", "Parker");

		/**
		 * 断言型 接口:Predicate 接口是只有一个参数的返回布尔类型值的 断言型 接口。
		 * 该接口包含多种默认方法来将 Predicate 组合成其他复杂的逻辑（比如：与，或，非）
		 */
		Predicate<String> predicate = (str) -> str.length() > 0;
		predicate.test("foo");
		predicate.negate().test("foo");
		Predicate<Boolean> predicate1 = Objects::isNull;
		Predicate<Boolean> predicate2 = Objects::nonNull;

		Predicate<String> predicate3 = String::isEmpty;
		Predicate<String> predicate4 = predicate3.negate();

		/**
		 * Function 接口接受一个参数并生成结果。默认方法可用于将多个函数链接在一起（compose, andThen）：
		 */
	}



}

class SomeThing{
	String startsWith(String s) {
		return String.valueOf(s.charAt(0));
	}
}
//构造函数是如何使用::关键字来引用的
class Person {
	String firstName;
	String lastName;

	Person() {}

	Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
}
interface PersonFactory<P extends Person>{
	P create(String firstName, String lastName);
}
