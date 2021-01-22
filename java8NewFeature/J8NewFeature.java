package demo.java8NewFeature;

import java.util.*;

public class J8NewFeature {


	interface Formula{
		double calculate(Integer a);
		default double sqrt(int a){
			return Math.sqrt(new Double(a));
		}
	}

	public static void main(String[] args) {
		/*Formula formula = new Formula() {
			@Override
			public double calculate(Integer a) {
				return sqrt(a * 100);
			}
		};
		System.out.println(formula.calculate(100));
		System.out.println(formula.sqrt(16));*/

		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		/*Collections.sort(names, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});*/
		//升序 a -> z
		Collections.sort(names, (a, b) -> a.compareTo(b) );
		//降序 z -> a
		Collections.sort(names, (a, b) -> b.compareTo(a) );
		names.sort((a, b) -> a.compareTo(b));
		System.out.println(names);


		List<Integer> vals = new ArrayList<Integer>();
		vals.add(1);
		vals.add(4);
		vals.add(3);
		vals.add(2);
		/*Collections.reverse(vals);
		System.out.println(vals);*/

		/*Collections.sort(vals, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2 > 0 ? 1:0;
			}
		});
		System.out.println(vals);*/
	}
}



















