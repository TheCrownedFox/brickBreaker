package edu.acmX.exhibit.brickbreaker;

import processing.core.PApplet;
	
public class Projectile {
	
	private PApplet parent;
	private float x;
	private float y;
	private float width;
	private float height;
	private float velocityX;
	private float velocityY;
	private static int COLOR;
	
	public Projectile(PApplet parent, int x, int y, int length) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.height = length;
		this.width = length;
		this.velocityX = parent.width / 100;
		this.velocityY = -parent.height / 100;
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
			velocityY *= -1;
		}
		
		// move
		x += velocityX;
		y += velocityY;
	}
}
