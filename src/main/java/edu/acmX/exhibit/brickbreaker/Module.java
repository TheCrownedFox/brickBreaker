package edu.acmX.exhibit.brickbreaker;

import java.awt.geom.Rectangle2D;

import edu.mines.acmX.exhibit.module_manager.ProcessingModule;

public class Module extends ProcessingModule {

	public static int BACKGROUND_COLOR;
	
	private Paddle paddle;
	private Projectile projectile;
	
	public void setup() {
		size(width, height);
		BACKGROUND_COLOR = color(0, 0, 139);
		paddle = new Paddle(this, width / 2, height - (height / 20), width / 12, height / 25);
		projectile = spawnProjectile();
		noCursor();
	}
	
	public void draw() {
		update();
		background(BACKGROUND_COLOR);
		paddle.draw();
		projectile.draw();
	}
	
	public void update() {
		paddle.update();
		projectile.update();
		checkCollisions();
		checkForDeadProjectiles();
	}
	
	public void checkCollisions() {
		Rectangle2D intersect = projectile.getRect().createIntersection(paddle.getRect());
		if (!intersect.isEmpty()) {
			if (intersect.getWidth() < intersect.getHeight()) {
				projectile.reverseXDirection();
			}
			else if (intersect.getWidth() > intersect.getHeight()) {
				projectile.reverseYDirection();
			}
		}
	}
	
	public void checkForDeadProjectiles() {
		if (projectile.isDead()) {
			projectile = spawnProjectile();
		}
	}
	
	public Projectile spawnProjectile() {
		return new Projectile(this, width / 30, height / 2, width / 40);
	}
}
