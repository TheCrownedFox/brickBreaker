package edu.acmX.exhibit.brickbreaker;

import edu.mines.acmX.exhibit.module_manager.ProcessingModule;

public class Module extends ProcessingModule {

	public static int BACKGROUND_COLOR;
	
	private Paddle paddle;
	
	public void setup() {
		size(width, height);
		BACKGROUND_COLOR = color(0, 0, 139);
		paddle = new Paddle(this, width / 2, height - (height / 20), width / 12, height / 25);
		noCursor();
	}
	
	public void draw() {
		update();
		background(BACKGROUND_COLOR);
		paddle.draw();
	}
	
	public void update() {
		paddle.update();
	}
}
