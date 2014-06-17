package edu.acmX.exhibit.brickbreaker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import edu.mines.acmX.exhibit.input_services.hardware.*;
import edu.mines.acmX.exhibit.stdlib.scoring.ScoreSaver;
import processing.core.PImage;
import edu.mines.acmX.exhibit.input_services.events.EventManager;
import edu.mines.acmX.exhibit.input_services.events.EventType;
import edu.mines.acmX.exhibit.input_services.hardware.devicedata.HandTrackerInterface;
import edu.mines.acmX.exhibit.input_services.hardware.drivers.InvalidConfigurationFileException;
import edu.mines.acmX.exhibit.module_management.modules.ProcessingModule;
import edu.mines.acmX.exhibit.stdlib.input_processing.tracking.HandTrackingUtilities;

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
	public static float LIVES_DISP_X;
	public static float LIVES_DISP_Y;
	public static float LIVES_SPACING;
	public static final int START_LIVES = 3;
	public static final int BRICK_POINT = 10;
	
	private List<List<Brick>> bricksList;
	private Paddle paddle;
	private Projectile projectile;
	private int lives;
	private int aliveBricks;
	private VirtualRectClick end;
	private VirtualRectClick playAgain;
    private VirtualRectClick submitScore;
	private VirtualRectClick end_s;
	private VirtualRectClick playAgain_s;
	private boolean gamePaused;
	private ScoreSaver saver;
    private boolean gameOver;
	private boolean submitted;
	
	private static EventManager eventManager;
	private HandTrackerInterface driver;
	private MyHandReceiver receiver;
	
	private static final String GAME_RESTART_TEXT = "Replay";
	private static final String GAME_END_TEXT = "Exit";
    private static final String GAME_SUBMIT_TEXT = "Submit";
	
	private float handX;
	private float handY;
	
	public static final String CURSOR_FILENAME = "hand_cursor.png";
	private PImage cursor_image;
	
	private int points;
	
	public void setup() {
		generateConstants();
		size(width, height);
		BACKGROUND_COLOR = color(0, 0, 139);
		paddle = new Paddle(this, PADDLE_START_X, PADDLE_START_Y , PADDLE_WIDTH, PADDLE_HEIGHT);
		handX = PADDLE_START_X;
		projectile = spawnProjectile();
		lives = START_LIVES;
		bricksList = populateBricks();
		end = new VirtualRectClick(1000, width / 10, 5 * height/ 7, width / 5, height /5);
		playAgain = new VirtualRectClick(1000, 2 * width / 5, 5 * height / 7, width / 5, height / 5);
        submitScore = new VirtualRectClick(1000, 7 * width / 10, 5 * height / 7, width / 5, height / 5);
		end_s = new VirtualRectClick(1000, width / 5, 5 * height/ 7, width / 5, height /5);
		playAgain_s = new VirtualRectClick(1000, 3 * width / 5, 5 * height / 7, width / 5, height / 5);
		noCursor();
		frameRate(45);
		cursor_image = loadImage(CURSOR_FILENAME);
		cursor_image.resize(32, 32);
		points = 0;
		saver = new ScoreSaver("BrickBreaker");
		registerTracking();
        gameOver = false;
		submitted = false;
		gamePaused = true;
	}
	
	public void update() {
		// update hands
		driver.updateDriver();
		if (receiver.whichHand() != -1) {	
			gamePaused = false;
			float marginFraction = (float) 1/6;
			handX = HandTrackingUtilities.getScaledHandX(receiver.getX(), 
					driver.getHandTrackingWidth(), width, marginFraction);
			handY = HandTrackingUtilities.getScaledHandY(receiver.getY(), 
					driver.getHandTrackingHeight(), height, marginFraction);
			// TODO find a better place for this function vvvv
			if (projectile.getVelocityX() == 0 && projectile.getVelocityY() == 0) {
				projectile.startMoving(); 
			}
		}
		else if (receiver.whichHand() == -1 && projectile.getVelocityX() != 0 && projectile.getVelocityY() != 0) {
			projectile.stopMoving();
			gamePaused = true;
		}
        if(!gameOver) {
            paddle.update();
            projectile.update();
            checkCollisions();
            checkForDeadProjectiles();

            if (aliveBricks <= 0) {
                ++lives;
                projectile = spawnProjectile();
                bricksList = populateBricks();
            }
        } else if(!submitted) {
			end.update((int) handX, (int) handY, millis());
			playAgain.update((int) handX, (int) handY, millis());
            submitScore.update((int) handX, (int) handY, millis());
			if(end.durationCompleted(millis())) {
				destroy();
				driver.clearAllHands();
			} else if(playAgain.durationCompleted(millis())) {
				noCursor();
				lives = START_LIVES;
				bricksList = populateBricks();
				projectile = spawnProjectile();
				paddle.setX(PADDLE_START_X);
				points = 0;
                gameOver = false;
			} else if(submitScore.durationCompleted(millis())) {
                //saver.addNewScore(points);
				receiver.hold();
				handX = handY = 0;
				saver.showPanel(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						receiver.release();
						submitted = true;
					}
				}, points, receiver.whichHand(), driver);
				receiver.setHand(-1);
            }
		} else {
			end_s.update((int) handX, (int) handY, millis());
			playAgain_s.update((int) handX, (int) handY, millis());
			if(end_s.durationCompleted(millis())) {
				destroy();
				driver.clearAllHands();
			} else if(playAgain_s.durationCompleted(millis())) {
				noCursor();
				lives = START_LIVES;
				bricksList = populateBricks();
				projectile = spawnProjectile();
				paddle.setX(PADDLE_START_X);
				points = 0;
				gameOver = false;
				submitted = false;
			}
		}
	}
	
	public void draw() {
		update();
        if (lives < 0) {
            gameOver = true;
            drawGameOver();
            return;
        }
		background(BACKGROUND_COLOR);
		if (gamePaused) {
			textAlign(CENTER, CENTER);
			textSize(96);
			fill(255, 255, 255);
			text("Wave to Continue", width / 2, height / 2);
			textAlign(LEFT, TOP);
		}
		paddle.draw();
		projectile.draw();
		drawBricks();
		drawLives();
		drawScore();
	}
	
	// checks all collisions against the projectile
	public void checkCollisions() {
		// check collision with paddle
		Rectangle2D intersect = projectile.getRect().createIntersection(paddle.getRect());
		if (!intersect.isEmpty()) {
			if (intersect.getWidth() < intersect.getHeight()) {
				projectile.reverseXDirection();
			}
			else if (intersect.getWidth() > intersect.getHeight()) {
				projectile.reverseYDirection();
				projectile.setY(PADDLE_START_Y - PROJECTILE_LENGTH - 1);
			}
		}
		// check collisions with all bricks
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
					--aliveBricks;
					points += BRICK_POINT;
				}
			}
		}
	}
	
	public void checkForDeadProjectiles() {
		if (projectile.isDead()) {
			projectile = spawnProjectile();
			--lives;
		}
	}
	
	public Projectile spawnProjectile() {
		return new Projectile(this, PROJECTILE_START_X, PROJECTILE_START_Y, PROJECTILE_LENGTH);
	}
	
	public void generateConstants() {
		PADDLE_START_X =  width / 2;
		PADDLE_START_Y = height - (height / 20);
		PADDLE_WIDTH = width / 8;
		PADDLE_HEIGHT = height / 25;
		PROJECTILE_START_X =  width / 30;
		PROJECTILE_START_Y = height / 2;
		PROJECTILE_LENGTH = width / 40;
		BRICK_WIDTH = width / 20;
		BRICK_HEIGHT = height / 40;
		BRICK_VERTICAL_SPACING = height / 40;
		BRICK_HORIZONTAL_SPACING = width / 60;
		BRICK_BOUNDS = new Rectangle2D.Float(width / 30, height / 30, width - width / 30, height / 3);
		LIVES_DISP_X = 0;
		LIVES_DISP_Y = 0;
		LIVES_SPACING = height / 60;
	}
	
	public List<List<Brick>> populateBricks() {
		aliveBricks = 0;
		ArrayList<List<Brick>> listOfLists = new ArrayList<List<Brick>>(); 
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
			aliveBricks += row.size();
			listOfLists.add(row);
			y += BRICK_HEIGHT + BRICK_VERTICAL_SPACING;
		}
		return listOfLists;
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
	
	public void drawLives() { //TODO make this clearer on screen
		fill(Projectile.COLOR);
		for(int i = 0; i < lives; i++) {
			rect(LIVES_DISP_X, LIVES_DISP_Y + (i * (LIVES_SPACING + (projectile.getLength() / 2))), projectile.getLength() / 2, projectile.getLength() / 2);
		}
	}
	
	public void drawGameOver() {
		if(submitted) {
			drawGameOverSubmitted();
			return;
		}
		fill(projectile.getColor());
		rect(0, 0, width, height);
		fill(255, 69, 0);
		textSize(min(width / 8, height / 6));
		//rectMode(CENTER);
		textAlign(CENTER, CENTER);
		text("GAME OVER", width / 2, height / 6);
		textSize(min(width / 12, height / 9));
		text("YOUR SCORE: " + points, width / 2, height / 3);
		//textAlign(LEFT, TOP);
		textSize(min(width / 16, height / 12));
		text("HIGH SCORE:", width / 2, height / 2);
		text(saver.getBestScoreString(ScoreSaver.ScorePattern.HIGH_BEST), width / 2, 3 * height / 5);

		stroke(0);
		strokeWeight(4);

		fill(255, 0, 0);
		rect(end.getX(), end.getY(), end.getWidth(), end.getHeight(), end.getWidth() / 6);

		//draw text for end game box
		textAlign(CENTER, CENTER);
		textSize(end.getWidth()/10);
		fill(0,0,0);
		text(GAME_END_TEXT, end.getX(), end.getY(), end.getWidth(), end.getHeight());

		fill(50, 205, 50);
		rect(playAgain.getX(), playAgain.getY(), playAgain.getWidth(), playAgain.getHeight(), playAgain.getWidth() / 6);

		//draw text for new game box
	    textAlign(CENTER, CENTER);
		textSize(playAgain.getWidth()/10);
		fill(0,0,0);
		text(GAME_RESTART_TEXT, playAgain.getX(), playAgain.getY(), playAgain.getWidth(), playAgain.getHeight());

        fill(0, 0, 255);
        rect(submitScore.getX(), submitScore.getY(), submitScore.getWidth(), submitScore.getHeight(), submitScore.getWidth() / 6);

        //draw text for submit score box
        textAlign(CENTER, CENTER);
        textSize(submitScore.getWidth()/10);
        fill(0,0,0);
        text(GAME_SUBMIT_TEXT, submitScore.getX(), submitScore.getY(), submitScore.getWidth(), submitScore.getHeight());

		noStroke();
		image(cursor_image, handX, handY);

	}

	private void drawGameOverSubmitted() {
		fill(projectile.getColor());
		rect(0, 0, width, height);
		fill(255, 69, 0);
		textSize(min(width / 8, height / 6));
		//rectMode(CENTER);
		textAlign(CENTER, CENTER);
		text("YOUR SCORE", width / 2, height / 6);
		textSize(min(width / 12, height / 9));
		text("" + saver.getLastSubmission(), width / 2, height / 3);
		//textAlign(LEFT, TOP);
		textSize(min(width / 16, height / 12));
		text("HIGH SCORE:", width / 2, height / 2);
		text(saver.getBestScoreString(ScoreSaver.ScorePattern.HIGH_BEST), width / 2, 3 * height / 5);

		stroke(0);
		strokeWeight(4);

		fill(255, 0, 0);
		rect(end_s.getX(), end_s.getY(), end_s.getWidth(), end_s.getHeight(), end_s.getWidth() / 6);

		//draw text for end game box
		textAlign(CENTER, CENTER);
		textSize(end_s.getWidth()/10);
		fill(0,0,0);
		text(GAME_END_TEXT, end_s.getX(), end_s.getY(), end_s.getWidth(), end_s.getHeight());

		fill(50, 205, 50);
		rect(playAgain_s.getX(), playAgain_s.getY(), playAgain_s.getWidth(), playAgain_s.getHeight(), playAgain_s.getWidth() / 6);

		//draw text for new game box
		textAlign(CENTER, CENTER);
		textSize(playAgain_s.getWidth()/10);
		fill(0,0,0);
		text(GAME_RESTART_TEXT, playAgain_s.getX(), playAgain_s.getY(), playAgain_s.getWidth(), playAgain_s.getHeight());

		noStroke();
		image(cursor_image, handX, handY);
	}
	
	public void drawScore() {
		fill(255, 215, 0);
		textSize(32);
		text("" + points, 19 * width / 20, height / 20);
	}
	
	public void registerTracking() {
		try {
			driver = (HandTrackerInterface) getInitialDriver("handtracking");
		} catch (BadFunctionalityRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownDriverRequest e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( RemoteException e ) {
			e.printStackTrace();
		} catch ( BadDeviceFunctionalityRequestException e ) {
			e.printStackTrace();
		}

		receiver = new MyHandReceiver();
		driver.registerHandCreated(receiver);
		driver.registerHandUpdated(receiver);
		driver.registerHandDestroyed(receiver);
	}
	
	public float getHandX() {
		return handX;
	}
	
	public float getHandY() {
		return handY;
	}
}
