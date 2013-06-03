package edu.acmX.exhibit.brickbreaker;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import processing.core.PApplet;
	
public class Projectile {
	
	private PApplet parent;
	private float x;
	private float y;
	private float width;
	private float height;
	private float velocityX;
	private float velocityY;
	private Rectangle2D rect;
	private boolean dead;
	private static int COLOR;
	
	public Projectile(PApplet parent, float x, float y, float length) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.height = length;
		this.width = length;
		this.velocityX = parent.width / 100;
		this.velocityY = parent.height / 100;
		this.rect = new Rectangle.Float(x, y, width, height);
		this.dead = false;
		COLOR = parent.color(240,255,255);
	}
	
	public void draw() {
		parent.fill(COLOR);
		parent.rect(x, y, width, height);
	}
	
	public void update() {
		// check against left edge
		if (x <= 0) {
			velocityX *= -1;
		}
		// check against the right edge
		if (x + width >= parent.width) {
			velocityX *= -1;
		}
		// check against the top edge
		if (y <= 0) {
			velocityY *= -1;
		}
		// check against the bottom edge
		if (y + height >= parent.height) {
			dead = true;
		}
		
		// move
		x += velocityX;
		y += velocityY;
		// update rect
		rect.setRect(x, y, width, height);
	}
	
	public void reverseXDirection() {
		velocityX *= -1;
	}
	
	public void reverseYDirection() {
		velocityY *= -1;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public Rectangle2D getRect() {
		return rect;
	}
}
