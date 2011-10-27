package dk.itu.spct;

import java.io.Serializable;

import org.mt4j.components.visibleComponents.widgets.MTImage;

import processing.core.PApplet;
import processing.core.PImage;

public class GalleryImage extends MTImage implements Serializable{
	private int width;
	private int height;
	private String comments;
	private String filename; 
	
	public GalleryImage(PApplet pApplet, PImage texture, int width, int height, String comments, String filename) {
		super(pApplet, texture);
		
		this.width = width;
		this.height = height;
		this.comments = comments;
		this.filename = filename;
	}
}
