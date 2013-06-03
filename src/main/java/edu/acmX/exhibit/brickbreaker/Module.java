package edu.acmX.exhibit.brickbreaker;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

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
	public static float BRICK_WIDTH;
	public static float BRICK_HEIGHT;
	public static float BRICK_VERTICAL_SPACING;
	public static float BRICK_HORIZONTAL_SPACING;
	public static Rectangle2D BRICK_BOUNDS; 
	
	private List<List<Brick>> bricksList;
	private Paddle paddle;
	private Projectile projectile;
	
	public void setup() {
		generateConstants();
		size(width, height);
		BACKGROUND_COLOR = color(0, 0, 139);
		paddle = new Paddle(this, PADDLE_START_X, PADDLE_START_Y , PADDLE_WIDTH, PADDLE_HEIGHT);
		projectile = spawnProjectile();
		bricksList = new ArrayList<List<Brick>>(); 
		populateBricks();
		noCursor();
	}
	
	public void draw() {
		update();
		background(BACKGROUND_COLOR);
		paddle.draw();
		projectile.draw();
		drawBricks();
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
		for(List<Brick> list : bricksList) {
			for(Brick brick : list) {
				Rectangle2D brickIntersect = projectile.getRect().createIntersection(brick.getRect());
				if(!brickIntersect.isEmpty() && !brick.isDead()) {
					if (brickIntersect.getWidth() < brickIntersect.getHeight()) {
						projectile.reverseXDirection();
					}
					else if (brickIntersect.getWidth() > brickIntersect.getHeight()) {
						projectile.reverseYDirection();
					}
					brick.kill();
				}
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
		BRICK_WIDTH = width / 20;
		BRICK_HEIGHT = height / 40;
		BRICK_VERTICAL_SPACING = height / 40;
		BRICK_HORIZONTAL_SPACING = width / 60;
		BRICK_BOUNDS = new Rectangle2D.Float(width / 30, height / 30, width - width / 30, height / 3);
	}
	
	public void populateBricks() {
		int x = (int) BRICK_BOUNDS.getMinX();
		int y = (int) BRICK_BOUNDS.getMinY();
		while(y < BRICK_BOUNDS.getHeight() + BRICK_BOUNDS.getMinY() - BRICK_HEIGHT) {
			x = (int) BRICK_BOUNDS.getMinX();
			ArrayList<Brick> row = new ArrayList<Brick>(); 
			int color = color((int) random(256), (int) random(256), (int) random(256));
			while(x < BRICK_BOUNDS.getWidth() + BRICK_BOUNDS.getMinX()- BRICK_WIDTH) {
				row.add(new Brick(this, x, y, BRICK_WIDTH, BRICK_HEIGHT, color));
				x += BRICK_WIDTH + BRICK_HORIZONTAL_SPACING;
			}
			bricksList.add(row);
			y += BRICK_HEIGHT + BRICK_VERTICAL_SPACING;
		}
	}
	public void drawBricks() {
		for(List<Brick> list : bricksList) {
			for(Brick brick : list) {
				if(!brick.isDead()) {
					brick.draw();
				}
			}
		}
	}
}
