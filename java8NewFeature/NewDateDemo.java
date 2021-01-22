package demo.java8NewFeature;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class NewDateDemo {
	public static void main(String[] args) {
		/**
		 * Timezones时区
		 */
		//输出所有区域标识符
		System.out.println(ZoneId.getAvailableZoneIds());

		ZoneId zoneId1 = ZoneId.of("Europe/Berlin");
		ZoneId zoneId2 = ZoneId.of("Brazil/East");
		System.out.println(zoneId1.getRules());
		System.out.println(zoneId2.getRules());

		/**
		 * LocalTime本地时间
		 *
		 * LocalTime 定义了一个没有时区信息的时间，例如 晚上10点或者 17:30:15。
		 * 下面的例子使用前面代码创建的时区创建了两个本地时间。之后比较时间并以小时和分钟为单位计算两个时间的时间差：
		 */
		LocalTime now1 = LocalTime.now(zoneId1);
		LocalTime now2 = LocalTime.now(zoneId2);
		System.out.println(now1.isBefore(now2)); //判断时间1是否早于时间2

		long hoursBetween = ChronoUnit.HOURS.between(now1, now2);
		long minutesBetween = ChronoUnit.MINUTES.between(now1, now2);
		System.out.println(hoursBetween);
		System.out.println(minutesBetween);

		//LocalTime 提供了多种工厂方法来简化对象的创建，包括解析时间字符串.
		LocalTime late = LocalTime.of(23, 59, 59);
		System.out.println(late);
		DateTimeFormatter germanFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
				.withLocale(Locale.GERMAN);
		/*LocalTime leetTime = LocalTime.parse("13.37", germanFormatter);
		System.out.println(leetTime);*/

		/**
		 * LocalDate本地日期
		 * LocalDate 表示了一个确切的日期，比如 2014-03-11。
		 * 该对象值是不可变的，用起来和LocalTime基本一致。
		 * 下面的例子展示了如何给Date对象加减天/月/年。
		 * 另外要注意的是这些对象是不可变的，操作返回的总是一个新实例。
		 */
		LocalDate today = LocalDate.now();
		System.out.println("今天的日期： " + today);
		LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);
		System.out.println("明天的日期： " +  tomorrow);
		LocalDate yesterday = tomorrow.minus(2, ChronoUnit.DAYS);
		System.out.println("昨天的日期：" + yesterday);

		LocalDate independenceDay = LocalDate.of(2019, Month.MARCH, 12);
		DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();
		System.out.println("今天是周几： " + dayOfWeek);

		//从字符串解析一个LocalDate类型，使用DateTimeFormatter
		String string = "2014==04==12 01时06分09秒";
		//根据需要解析的日期，时间字符串定义解析所有的格式器
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy==MM==dd HH时mm分ss秒");
		LocalDateTime dateTime = LocalDateTime.parse(string, formatter1);
		System.out.println(dateTime);
		String s2 = "2014$$$四月$$$13 20小时";
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyy$$$MMM$$$dd HH小时");
		LocalDateTime dateTime1 = LocalDateTime.parse(s2, formatter2);
		System.out.println(dateTime1);

		LocalDateTime rightNow=LocalDateTime.now();
		String date=DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(rightNow);
		System.out.println(date);//2019-03-12T16:26:48.29
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
		System.out.println(formatter.format(rightNow));//2019-03-12 16:26:48

		/**
		 * LocalDateTime(本地日期时间)
		 * LocalDateTime 同时表示了时间和日期，相当于前两节内容合并到一个对象上了。
		 * LocalDateTime 和 LocalTime还有 LocalDate 一样，都是不可变的。
		 * LocalDateTime 提供了一些能访问具体字段的方法。
		 */
		LocalDateTime sylvester = LocalDateTime.of(2014,Month.DECEMBER, 31,23,59,59);
		DayOfWeek dayOfWeek1 = sylvester.getDayOfWeek();
		System.out.println(dayOfWeek1);

		Month month = sylvester.getMonth();
		System.out.println(month);

		long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
		System.out.println(minuteOfDay);

		//格式化LocalDateTime和格式化时间和日期一样的，除了使用预定义好的格式外，我们也可以自己定义格式：
		// 和java.text.NumberFormat不一样的是新版的DateTimeFormatter是不可变的，所以它是线程安全的
		DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("MMM dd, yyy - HH:mm");
		LocalDateTime parsed = LocalDateTime.parse("Nov o3, 2014 - 07:12", formatter3);
		String str1 = formatter3.format(parsed);
		System.out.println(str1);
	}
}



































