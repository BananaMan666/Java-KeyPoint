package demo.java8NewFeature;

/**
 * “函数式接口”是指仅仅只包含一个抽象方法,但是可以有多个非抽象方法(也就是上面提到的默认方法)的接口。
 * 像这样的接口，可以被隐式转换为lambda表达式。
 * @param <F>
 * @param <T>
 */
@FunctionalInterface
public interface Converter<F, T> {
	T converter(F from);
}
