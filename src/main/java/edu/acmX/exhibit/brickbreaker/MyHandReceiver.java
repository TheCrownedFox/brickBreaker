package edu.acmX.exhibit.brickbreaker;

import edu.mines.acmX.exhibit.stdlib.graphics.Coordinate3D;
import edu.mines.acmX.exhibit.stdlib.graphics.HandPosition;
import edu.mines.acmX.exhibit.stdlib.input_processing.receivers.HandReceiver;



public class MyHandReceiver extends HandReceiver {
	
	public static int NO_HAND = -1;

	private Coordinate3D position;
	int handID = NO_HAND;
	
	public MyHandReceiver() {
		position = new Coordinate3D(0, 0, 0);
	}
	
	public void handCreated(HandPosition pos) {
		if (handID == NO_HAND) {
			handID = pos.getId();
		}
		position = pos.getPosition();
	}
	
	public void handUpdated(HandPosition pos) {
		if (pos.getId() == handID) {
			position = pos.getPosition();
		}
	}	
	
	public void handDestroyed(int id) {
		if (id == handID) {
			handID = NO_HAND;
		}
	}
	
	public int whichHand() {
		return handID;
	}
	
	public float getX() {
		return position.getX();
	}
	
	public float getY() {
		return position.getY();
	}
	
	public float getZ() {
		return position.getZ();
	}
}
