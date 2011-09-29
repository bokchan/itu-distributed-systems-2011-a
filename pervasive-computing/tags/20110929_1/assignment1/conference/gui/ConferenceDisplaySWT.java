package assignment1.conference.gui;

import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import swing2swt.layout.FlowLayout;

import com.ibm.icu.text.SimpleDateFormat;

public class ConferenceDisplaySWT {
	protected Shell shell;
	private Label eventlist;
	public static Button btnNewButton;
	ArrayList<String[]> events = new ArrayList<String[]>();
	Calendar cal = new java.util.GregorianCalendar(); 
	SimpleDateFormat format = new  SimpleDateFormat("dd-MMMM");
	String event1[] = {"Ubicomp conference", "13th International Conference on Ubiquitous Computing (UbiComp 2011) at ITU Copenhagen", format.format(cal.getTime())};		
	String event4[] = {"Second International Workshop on Ubiquitous Crowdsourcing: Towards a Platform for Crowd Computing", "", format.format(cal.getTime())};
	String event5[] = {"Trajectory Data Mining and Analysis", "Some content", format.format(cal.getTime())};		
	String event2[] = {"Annual party 2011", "Students and employees we hope to see you at this festive occasion. Invitation is sent to your mailbox. Buy tickets now at the Information desk.", format.format(cal.getTime())};
	String event3[] = {"ITU.Film and Analog present: True Grit at 14:00 in Analog", "Drop by Analog at 14:00 and watch this great western from the Coen Brothers (Big Lebowski, Fargo). All are welcome, and there will be free popcorn as well!", format.format(cal.getTime())};

	public ConferenceDisplaySWT() {
		
		events.add(event1);
		events.add(event2);
		events.add(event3);
		events.add(event4);
		events.add(event5);
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ConferenceDisplaySWT window = new ConferenceDisplaySWT();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		
		createContents();
		shell.open();
		shell.pack();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	public void update() {
		Display.getDefault().update();
		shell.pack();
		shell.layout();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setText("New Button");
		btnNewButton.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
					System.out.println("Button click ");
				
			}
		});
		
		eventlist = new Label(shell, SWT.BOLD);
		eventlist.setSize(450, 300);
		String strlist = ""; 
		for (String[] event : events) {
			strlist += event[0] + "\n";
		}
		System.out.println(strlist);
		eventlist.setText(strlist);
	}
	
	public void ShowEvents() {
		System.out.println("Show Events");
		String strlist = ""; 
		strlist += event4[0] + "\n" + event4[1];
		strlist += event5[0] + "\n" + event5[1];
		eventlist.setText(strlist);
		update();
	}
	
}