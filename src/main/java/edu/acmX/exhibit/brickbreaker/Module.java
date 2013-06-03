package edu.acmX.exhibit.brickbreaker;

import java.awt.geom.Rectangle2D;

import edu.mines.acmX.exhibit.module_manager.ProcessingModule;

public class Module extends ProcessingModule {

	public static int BACKGROUND_COLOR;
	public static float PADDLE_START_X;
	public static float PADDLE_START_Y;
	public static float PADDLE_WIDTH;
	public static float PADDLE_HEIGHT;
	public static float PROJECTILE_START_X;
	public static float PROJECTILE_START_Y;
	public static float PROJECTILE_LENGTH;
	
	private Paddle paddle;
	private Projectile projectile;
	
	public void setup() {
		generateConstants();
		size(width, height);
		BACKGROUND_COLOR = color(0, 0, 139);
		paddle = new Paddle(this, PADDLE_START_X, PADDLE_START_Y , PADDLE_WIDTH, PADDLE_HEIGHT);
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
		return new Projectile(this, PROJECTILE_START_X, PROJECTILE_START_Y, PROJECTILE_LENGTH);
	}
	
	public void generateConstants() {
		PADDLE_START_X =  width / 2;
		PADDLE_START_Y = height - (height / 20);
		PADDLE_WIDTH = width / 12;
		PADDLE_HEIGHT = height / 25;
		PROJECTILE_START_X =  width / 30;
		PROJECTILE_START_Y = height / 2;
		PROJECTILE_LENGTH = width / 40;
	}
}
