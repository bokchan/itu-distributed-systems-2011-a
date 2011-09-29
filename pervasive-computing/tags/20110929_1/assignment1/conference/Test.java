package assignment1.conference;

import java.util.Arrays;
import java.util.HashSet;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		String[] s = {"one", "two"};
		set1.addAll(Arrays.asList(s));
		set2.addAll(Arrays.asList(s));
		
		set1.removeAll(set2);
		
		System.out.println(set1.toString());

	}

}
