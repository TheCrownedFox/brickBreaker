package edu.acmX.exhibit.brickbreaker;

import java.awt.geom.Rectangle2D;

import processing.core.PApplet;

public class Brick {

	private PApplet parent;
	private float x;
	private float y;
	private float width;
	private float height;
	private Rectangle2D rect;
	private int color;
	private boolean dead;
	
	public Brick(PApplet parent, float x, float y, float width, float height, int color) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		dead = false;
		rect = new Rectangle2D.Float(x, y, width, height);
		
	}
	
	public void draw() {
		parent.fill(color);
		parent.rect(x, y, width, height);
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void kill() {
		dead = true;
	}
	
	public Rectangle2D getRect() {
		return rect;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
}
