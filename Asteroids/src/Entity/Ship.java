package Entity;

import java.awt.Color;
import java.awt.Graphics;

/** Represents player's ship */
public class Ship {
	/** Used in array index */
	static final int X = 0, Y = 1;

	/** Used in loops for drawing and moving */
	static final int NUM_PTS_FLAME = 3, NUM_PTS_SHIP = 4;

	/** Define the shape of the ship */
	final double shipShape[][] = {
			{ /* X1 */14, /* X2 */-10, /* X3 */-6, /* X4 */-10 },
			{ /* Y1 */0, /* Y2 */-8, /* Y3 */0, /* Y4 */8 } };

	/** Define Shape of Flame */
	final double flameShape[][] = { { /* X1 */-6, /* X2 */-23, /* X3 */-6 },
			{ /* Y1 */-3, /* Y2 */0, /* Y3 */3 } };

	/**
	 * Radius of a theoretical circle that the ship is within. This helps/
	 * cheats with rotation when changing directions
	 */
	final int radius = 6;

	/** Used for drawing ship in current location */
	int shipPtsLoc[][];

	/** Used for drawing flame in current location */
	int flamePtsLoc[][];

	/** Used to create a delay between shots */
	int shotTimer;

	/**
	 * Keeps track of whether or not ship is turning and/or accelerating. Will
	 * be 3 dimensional array 0 = Forward, 1 = Left, 2 = Right
	 */
	boolean isAccelerating[];

	/** Used in draw. If false, draw grey, if true, draw white */
	boolean gameRunning;

	// THE FOLLOWING VARIABLES ARE USED FOR TRANSFORMING THE POSITION OF THE
	// SHIP
	/** Position */
	double pos[];

	/** Velocity */
	double vel[];

	/** Acceleration */
	double accel;

	/** Rotation */
	double rot;

	/** Rotational Velocity */
	double rotVel;

	/**
	 * Velocity Decay value is a percentage, causes deceleration, limits max
	 * speed
	 */
	double velDecay;

	// FUNCTIONS
	/** Constructor */
	public Ship(double pos[], double rot, double accel, double velDecay,
			double rotVel) {
		// Setting variables based on parameters
		this.pos = pos;
		this.rot = rot;
		this.accel = accel;
		this.velDecay = velDecay;
		this.rotVel = rotVel;

		// Setting Variables
		gameRunning = false;

		// Allocating memory;
		vel = new double[] { 0, 0 };
		isAccelerating = new boolean[] { false, false, false };
		shipPtsLoc = new int[2][NUM_PTS_SHIP];
		flamePtsLoc = new int[2][NUM_PTS_FLAME];
	}

	/**
	 * Function for moving the ship, called once per frame. Handles all rotation
	 * and acceleration. NewX=X*cos(rotation)-Y*sin(rotation).
	 * NewY=X*sin(rotation)+Y*cos(rotation).
	 */
	public void move(int Screen[]) {
		// ROTATIONAL MOVEMENT
		// Backwards from normal rotation due to positive Y being down
		if (isAccelerating[1]) {
			rot -= rotVel;
		}
		if (isAccelerating[2]) {
			rot += rotVel;
		}
		// Ensures that player doesn't exceed double value limits
		if (rot > (2 * Math.PI)) {
			rot -= (2 * Math.PI);
		}
		if (rot < 0) {
			rot += (2 * Math.PI);
		}

		// Rotate ship points, translate them to the new location
		for (int i = 0; i < NUM_PTS_SHIP; ++i) {
			// Add .5 for rounding purposes
			// New ship X
			shipPtsLoc[X][i] = (int) (shipShape[X][i] * Math.cos(rot)
					- shipShape[Y][i] * Math.sin(rot) + pos[X] + .5);
			// New ship Y
			shipPtsLoc[Y][i] = (int) (shipShape[X][i] * Math.sin(rot)
					+ shipShape[Y][i] * Math.cos(rot) + pos[Y] + .5);
		}

		// Rotate flame points, translate them to the new location
		if (isAccelerating[0] /* && gameRunning */) {
			for (int i = 0; i < NUM_PTS_FLAME; ++i) {
				// Add .5 for rounding purposes
				// New flame X
				flamePtsLoc[X][i] = (int) (flameShape[X][i] * Math.cos(rot)
						- flameShape[Y][i] * Math.sin(rot) + pos[X] + .5);
				// New flame Y
				flamePtsLoc[Y][i] = (int) (flameShape[X][i] * Math.sin(rot)
						+ flameShape[Y][i] * Math.cos(rot) + pos[Y] + .5);
			}
		}

		// DIRECTIONAL MOVEMENT
		if (isAccelerating[0]) {
			// Calculates acceleration and adds it to velocity
			vel[X] += accel * Math.cos(rot);
			vel[Y] += accel * Math.sin(rot);
		}

		// Add velocity to position
		pos[X] += vel[X];
		pos[Y] += vel[Y];
		// Decay Velocity by a percentage(velDecay)
		vel[X] *= velDecay;
		vel[Y] *= velDecay;

		// LOCATION CHECKS
		// Check to see if ship goes out of bounds, if so then loop to other
		// side
		if (pos[X] < 0) // Goes too far left
			pos[X] += Screen[X];
		if (pos[Y] < 0) // Goes too far up
			pos[Y] += Screen[X];
		if (pos[X] > Screen[X]) // Goes too far right
			pos[X] -= Screen[X];
		if (pos[Y] > Screen[Y]) // Goes too far down
			pos[Y] -= Screen[Y];
	}

	/** Sole purpose is to time down shotTimer once per frame */
	public void update() {
		if (shotTimer > 0)
			--shotTimer;
		else if (shotTimer <= 0)
			shotTimer = 20;
	}

	/** Draws flame and ship to screen */
	public void draw(Graphics Gfx) {
		if (isAccelerating[0] /* && gameRunning */) { // Draw flame if
													// accelerating
			// Draw Flame
			Gfx.setColor(Color.green);
			Gfx.fillPolygon(flamePtsLoc[X], flamePtsLoc[Y], NUM_PTS_FLAME);
		}
		// Draw Ship after flame so it will be over flame at base
		if (gameRunning)
			Gfx.setColor(Color.blue);
		else
			Gfx.setColor(Color.gray);
		Gfx.fillPolygon(shipPtsLoc[X], shipPtsLoc[Y], NUM_PTS_SHIP);
	}
	
	/**Function that allows ship to shoot*/
	public Bullet Fire()	{
		return new Bullet(pos, rot);
	}

	// ACCESSORS
	public double[] getPos() {
		return pos;
	}

	public double getRadius() {
		return radius;
	}

	public boolean isRunning() {
		return gameRunning;
	}

	public boolean readyToFire() {
		if (shotTimer > 0)
			return false;
		else
			return true;
	}

	// MUTATORS
	public void setForwardAccel(boolean accel) {
		isAccelerating[0] = accel;
	}

	public void setLeftAccel(boolean accel) {
		isAccelerating[1] = accel;
	}

	public void setRightAccel(boolean accel) {
		isAccelerating[2] = accel;
	}

	public void setRunning(boolean running) {
		gameRunning = running;
	}
}
