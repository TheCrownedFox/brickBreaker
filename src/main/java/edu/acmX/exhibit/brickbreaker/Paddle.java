package edu.acmX.exhibit.brickbreaker;

import java.awt.geom.Rectangle2D;

import processing.core.PApplet;

public class Paddle {

	private PApplet parent;
	private float x;
	private float y;
	private float width;
	private float height;
	private Rectangle2D rect;
	public static int COLOR;
	public static int SPEED;
	
	public Paddle(PApplet parent, float x, float y, float width, float height) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		rect = new Rectangle2D.Float(x, y, width, height);
		COLOR = parent.color(176, 196, 222);
		SPEED = parent.width/60;
	}
	
	
	public void draw() {
		parent.fill(COLOR);
		parent.rect(x, y, width, height);
	}
	
	public void update() {
		int mousePosX = parent.mouseX;
		if(parent.mouseX > x + width / 3 && parent.mouseX < x + 2 * width / 3) {
			return;
		}
		if(x + width / 2 > mousePosX) {
			x -= SPEED;
		}
		else if(x + width / 2 < mousePosX) {
			x += SPEED;
		}
		
		// update rect
		rect.setRect(x, y, width, height);
	}
	
	public Rectangle2D getRect() {
		return rect;
	}
}
