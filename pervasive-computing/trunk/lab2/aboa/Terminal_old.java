package lab2;

import java.util.HashSet;


public class Terminal_old {
	static HashSet<String> terminals = new HashSet<String>();
	
	public static boolean add(String terminal_id) {
		return terminals.add(terminal_id);
	} 
	
	public static boolean remove(String terminal_id) {
		return terminals.remove(terminal_id);
	}
	
	public static void main(String[] args) {
		
	}
}
