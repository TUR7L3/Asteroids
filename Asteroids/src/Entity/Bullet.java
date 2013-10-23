package Entity;

import java.awt.Color;
import java.awt.Graphics;

/** Represents bullets fired from player's ship */
public class Bullet {
	/** Used for array index */
	static final int X = 0, Y = 1;
	/** How fast the bullet will move */
	final double bulletVel = 15;
	/** Bullet Position */
	double pos[];
	/** Bullet Velocity */
	double vel[];
	/**
	 * How long the bullet will last before it disappears without hitting
	 * anything
	 */
	int lifeSpan;

	public Bullet(double pos[], double rot) {
		this.pos = pos;
		// Makes the bullet leave at the same angle the ship is flying
		vel = new double[2];
		vel[X] = bulletVel * Math.cos(rot);
		vel[Y] = bulletVel * Math.sin(rot);
		lifeSpan = 50;
	}
	/**Similar to Ship move function. Only adds vel to pos and checks to see if it needs to loop around the screen*/
	public void move(int Screen[])	{
		//Add velocity to position to move the shot
		pos[X] += vel[X];
		pos[Y] += vel[Y];
		
		// LOCATION CHECKS
		// Check to see if bullet goes out of bounds, if so then loop to other
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

	public void update() {
		if (lifeSpan > 0) {
			--lifeSpan;
		}
	}

	public void draw(Graphics Gfx) {
		Gfx.setColor(Color.red);

		Gfx.fillOval((int) (pos[X] - 0.5), (int) (pos[Y] - 0.5), 2, 2);
	}
	
	public double[] getPos() {
		return pos;
	}

	public int getLife() {
		return lifeSpan;
	}
}