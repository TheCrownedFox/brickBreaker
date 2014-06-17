package edu.acmX.exhibit.brickbreaker;

import java.awt.geom.Rectangle2D;

import processing.core.PApplet;

public class Paddle {

	private Module parent;
	private float x;
	private float y;
	private float width;
	private float height;
	private Rectangle2D rect;
	public static int COLOR;
	public static int SPEED;
	
	public Paddle(Module parent, float x, float y, float width, float height) {
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
        parent.rect(parent.getHandX(), y, 10, 10);
	}
	
	public void update() {
		int handPosX = (int) parent.getHandX();
		if(handPosX > x + width / 3 && handPosX < x + 2 * width / 3) {
			return ;
		}
		if(x + width / 2 > handPosX) {
			x -= SPEED;
		}
		else if(x + width / 2 < handPosX) {
			x += SPEED;
		}
		
		// update rect
		rect.setRect(x, y, width, height);
	}
	
	public Rectangle2D getRect() {
		return rect;
	}
	
	public void setX(float x) {
		this.x = x;
	}
}
