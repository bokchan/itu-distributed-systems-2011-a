package dk.itu.spct;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;

import processing.core.PImage;

public class GalleryButton extends MTImageButton {
	String TARGET;
	AbstractMTApplication mtApp;

	public GalleryButton(AbstractMTApplication pApplet, PImage texture, String TARGET) {
		super(pApplet, texture);
		this.TARGET = TARGET;
		this.mtApp = pApplet;
	}
}
