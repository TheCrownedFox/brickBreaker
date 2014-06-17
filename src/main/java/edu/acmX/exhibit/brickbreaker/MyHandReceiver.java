package edu.acmX.exhibit.brickbreaker;

import edu.mines.acmX.exhibit.stdlib.graphics.Coordinate3D;
import edu.mines.acmX.exhibit.stdlib.graphics.HandPosition;
import edu.mines.acmX.exhibit.stdlib.input_processing.receivers.HandReceiver;



public class MyHandReceiver extends HandReceiver {

	private Coordinate3D position;
	private int handID = -1;
	private boolean hold = false;
	
	public MyHandReceiver() {
		position = new Coordinate3D(0, 0, 0);
	}
	
	public void handCreated(HandPosition pos) {
		if(hold) return;
		if (handID == -1) {
			handID = pos.getId();
		}
		position = pos.getPosition();
	}
	
	public void handUpdated(HandPosition pos) {
		if(hold) return;
		if (pos.getId() == handID) {
			position = pos.getPosition();
		}
	}	
	
	public void handDestroyed(int id) {
		if(hold) return;
		if (id == handID) {
			handID = -1;
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

	public void setHand(int id) {
		handID = id;
	}

	public void hold() {
		hold = true;
	}

	public void release() {
		hold = false;
	}
}
