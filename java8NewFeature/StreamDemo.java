package demo.java8NewFeature;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

/**
 * java.util.Stream 表示能应用在一组元素上一次执行的操作序列。
 * Stream 操作分为中间操作或者最终操作两种，最终操作返回一特定类型的计算结果，而中间操作返回Stream本身，
 * 这样你就可以将多个操作依次串起来。
 * Stream 的创建需要指定一个数据源，比如 java.util.Collection 的子类，List 或者 Set， Map 不支持。
 * Stream 的操作可以串行执行或者并行执行。
 *
 * Java 8扩展了集合类，可以通过 Collection.stream() 或者 Collection.parallelStream() 来创建一个Stream。
 * 所有stream操作不会影响原有的数据源，只是获取一个新的数据源
 */
public class StreamDemo {
	public static void main(String[] args) {
		List<String> stringList = new ArrayList<>();
		stringList.add("ddd2");
		stringList.add("aaa2");
		stringList.add("bbb1");
		stringList.add("aaa1");
		stringList.add("bbb3");
		stringList.add("ccc");
		stringList.add("bbb2");
		stringList.add("ddd1");


		/**
		 * Filter(过滤)
		 * 过滤通过一个predicate接口来过滤并只保留符合条件的元素，该操作属于中间操作，所以我们可以在过滤后的结果来应用其他Stream操作（比如forEach）。
		 * forEach需要一个函数来对过滤后的元素依次执行。forEach是一个最终操作，所以我们不能在forEach之后来执行其他Stream操作。
		 */
		stringList
				.stream()
				.filter((s) -> s.startsWith("a"))
				.forEach(System.out::println);//aaa2 aaa1
		/**
		 * Sorted(排序)
		 * 排序是一个 中间操作，返回的是排序好后的 Stream。如果你不指定一个自定义的 Comparator 则会使用默认排序。
		 */
		stringList
				.stream()
				.sorted()
				.filter((s) -> s.startsWith("a"))
				.forEach(System.out::println);// aaa1 aaa2
		/**
		 * Map(映射)
		 * 中间操作 map 会将元素根据指定的 Function 接口来依次将元素转成另外的对象。
		 *
		 * 下面的示例展示了将字符串转换为大写字符串。你也可以通过map来将对象转换成其他类型，map返回的Stream类型是根据你map传递进去的函数的返回值决定的。
		 */
		stringList
				.stream()
				.map(String::toUpperCase)
				.sorted((a, b) -> b.compareTo(a))
				.forEach(System.out::println);// "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"

		/**
		 * Match(匹配)
		 * Stream提供了多种匹配操作，允许检测指定的Predicate是否匹配整个Stream。所有的匹配操作都是 最终操作 ，并返回一个 boolean 类型的值。
		 * anyMatch：只要有一个符合条件的？
		 * allMatch：所有的都符合条件？
		 * noneMatch：没有符合条件的？
		 */
		// 测试 Match (匹配)操作
		boolean anyStartsWithA =
				stringList
						.stream()
						.anyMatch((s) -> s.startsWith("a"));
		System.out.println(anyStartsWithA);      // true

		boolean allStartsWithA =
				stringList
						.stream()
						.allMatch((s) -> s.startsWith("a"));

		System.out.println(allStartsWithA);      // false

		boolean noneStartsWithZ =
				stringList
						.stream()
						.noneMatch((s) -> s.startsWith("z"));

		System.out.println(noneStartsWithZ);      // true

		/**
		 *Count(计数)
		 * 计数是一个 最终操作，返回Stream中元素的个数，返回值类型是 long。
		 */
		//测试 Count (计数)操作
		long startsWithB =
				stringList
						.stream()
						.filter((s) -> s.startsWith("b"))
						.count();
		System.out.println(startsWithB);    // 3

		/**
		 * Reduce(规约)
		 * 这是一个 最终操作 ，允许通过指定的函数来讲stream中的多个元素规约为一个元素，规约后的结果是通过Optional 接口表示的：
		 *
		 */
		//测试 Reduce (规约)操作
		Optional<String> reduced =
				stringList
						.stream()
						.sorted()
						.reduce((s1, s2) -> s1 + "#" + s2);

		reduced.ifPresent(System.out::println);//aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2
		/**
		 * 		这个方法的主要作用是把 Stream 元素组合起来。它提供一个起始值（种子），然后依照运算规则（BinaryOperator），和前面 Stream 的第一个、第二个、第 n 个元素组合。
		 * 		从这个意义上说，字符串拼接、数值的 sum、min、max、average 都是特殊的 reduce。
		 * 		例如 Stream 的 sum 就相当于Integer sum = integers.reduce(0, (a, b) -> a+b);
		 * 		也有没有起始值的情况，这时会把 Stream 的前面两个元素组合起来，返回的是 Optional。
		 *
 		 */
		// 字符串连接，concat = "ABCD"
		String concat = Stream.of("A", "B", "C", "D").reduce("", String::concat);
		// 求最小值，minValue = -3.0
		double minValue = Stream.of(-1.5, 1.0, -3.0, -2.0).reduce(Double.MAX_VALUE, Double::min);
		// 求和，sumValue = 10, 有起始值
		int sumValue = Stream.of(1, 2, 3, 4).reduce(0, Integer::sum);
		// 求和，sumValue = 10, 无起始值
		sumValue = Stream.of(1, 2, 3, 4).reduce(Integer::sum).get();
		// 过滤，字符串连接，concat = "ace"
		concat = Stream.of("a", "B", "c", "D", "e", "F").
				filter(x -> x.compareTo("Z") > 0).
				reduce("", String::concat);


		/**
		 * Parallel Streams(并行流)
		 *
		 * Stream有串行和并行两种，串行Stream上的操作是在一个线程中依次完成，而并行Stream则是在多个线程上同时执行。
		 *
		 * 唯一需要做的改动就是将 stream() 改为parallelStream()。
		 * 如：long count = values.parallelStream().sorted().count();
		 *
		 */


		/**
		 * Maps
		 * 前面提到过，Map 类型不支持 streams，不过Map提供了一些新的有用的方法来处理一些日常任务。
		 * Map接口本身没有可用的 stream（）方法，但是你可以在键，值上创建专门的流或者通过 map.keySet().stream(),map.values().stream()和map.entrySet().stream()。
		 * 此外,Maps 支持各种新的和有用的方法来执行常见任务。
		 */
		Map<Integer, String> map = new HashMap<>();

		//putIfAbsent 阻止我们在null检查时写入额外的代码;forEach接受一个 consumer 来对 map 中的每个元素操作。
		for (int i = 0; i < 10; i++) {
			map.putIfAbsent(i, "val" + i);
		}
		map.forEach((id, val) -> System.out.println(val));//val0 val1 val2 val3 val4 val5 val6 val7 val8 val9

		/**
		 * Timezones(时区)
		 * 在新API中时区使用 ZoneId 来表示。时区可以很方便的使用静态方法of来获取到。
		 * 抽象类ZoneId（在java.time包中）表示一个区域标识符。 它有一个名为getAvailableZoneIds的静态方法，它返回所有区域标识符。
		 *
		 */
		//输出所有区域标识符
		System.out.println(ZoneId.getAvailableZoneIds());

		ZoneId zone1 = ZoneId.of("Europe/Berlin");
		ZoneId zone2 = ZoneId.of("Brazil/East");
		System.out.println(zone1.getRules());// ZoneRules[currentStandardOffset=+01:00]
		System.out.println(zone2.getRules());// ZoneRules[currentStandardOffset=-03:00]

		/**
		 * LocalTime(本地时间)
		 * LocalTime 定义了一个没有时区信息的时间，例如 晚上10点或者 17:30:15。
		 *
		 * 下面的例子使用前面代码创建的时区创建了两个本地时间。之后比较时间并以小时和分钟为单位计算两个时间的时间差：
		 *
		 */
		LocalTime now1 = LocalTime.now(zone1);
		LocalTime now2 = LocalTime.now(zone2);
		System.out.println(now1.isBefore(now2));  // false

		long hoursBetween = ChronoUnit.HOURS.between(now1, now2);
		long minutesBetween = ChronoUnit.MINUTES.between(now1, now2);

		System.out.println(hoursBetween);       // -3
		System.out.println(minutesBetween);     // -239

		/**
		 * LocalTime 提供了多种工厂方法来简化对象的创建，包括解析时间字符串.
		 *
		 */
		LocalTime late = LocalTime.of(23, 59, 59);
		System.out.println(late);       // 23:59:59
		DateTimeFormatter germanFormatter =
				DateTimeFormatter
						.ofLocalizedTime(FormatStyle.SHORT)
						.withLocale(Locale.GERMAN);

		LocalTime leetTime = LocalTime.parse("13:37", germanFormatter);
		System.out.println(leetTime);   // 13:37

		/**
		 *
		 */
		/**
		 *
		 */


	}
}
