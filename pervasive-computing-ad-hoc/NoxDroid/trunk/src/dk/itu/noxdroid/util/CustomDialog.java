package dk.itu.noxdroid.util;

import android.app.Dialog;
import android.content.Context;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);
		setContentView(dk.itu.noxdroid.R.layout.custom_dialog);
	}	
}