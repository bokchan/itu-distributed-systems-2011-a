package assignment1.conference.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class EventList extends Composite {
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EventList(Composite parent, int style) {
		super(parent, style);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		text = new Text(this, SWT.BORDER);
		
	
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
