package assignment1.conference.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoDisplay {

	private JFrame frame;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InfoDisplay window = new InfoDisplay();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InfoDisplay() {
		initialize();

	}

	public InfoDisplay(boolean b) {

		InfoDisplay window = new InfoDisplay();
		initialize();
		window.frame.setVisible(true);

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		contentPane = new JPanel();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(contentPane);
	}

	public void change() {
		System.out.println("change");
		JLabel lbl = new JLabel("hello");
		lbl.setText("sdfsfsdfsdf");
		contentPane.add(lbl);
		contentPane.revalidate();
		frame.repaint();
		System.out.println("change");
	}

}
