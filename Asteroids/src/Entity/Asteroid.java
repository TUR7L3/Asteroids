package Entity;

import java.awt.Color;
import java.awt.Graphics;

/** Represents the asteroids */
public class Asteroid {
	/** Variable for array index */
	static final int X = 0, Y = 1;

	/** Amount of times asteroid can be shot before being completely destroyed */
	int health;

	/** Number of asteroids that large asteroid will split into after shot */
	public final int asteroidSplit = 2;

	/** Asteroid Position */
	double pos[];
	/** Asteroid Velocity */
	double vel[];
	/** Minimum Velocity */
	final double minVel = 2;
	/** Maximum Velocity */
	final double maxVel = 5;

	/** Asteroid Radius */
	double radius;

	/** Constructor used in game */
	public Asteroid(double pos[]) {
		this.pos = new double[2];
		this.pos[X] = pos[X];
		this.pos[Y] = pos[Y];

		vel = new double[2];
		
		health = 2;
		radius = 20;
		
		/** Random Velocity */
		double randVel = minVel + Math.random() * (maxVel - minVel);
		/** Random Direction */
		double randDir = 2 * Math.PI * Math.random();
		vel[X] = randVel*Math.cos(randDir);
		vel[Y] = randVel*Math.cos(randDir);
	}

	/** Constructor used in split asteroid */
	public Asteroid(double pos[], int health, double radius) {
		this.pos = new double[2];
		this.pos[X] = pos[X];
		this.pos[Y] = pos[Y];

		vel = new double[2];
				
		this.health = health;
		
		this.radius = (radius/Math.sqrt(asteroidSplit));

		/** Random Velocity */
		double randVel = minVel + Math.random() * (maxVel - minVel);
		/** Random Direction */
		double randDir = 2 * Math.PI * Math.random();
		vel[X] = randVel*Math.cos(randDir);
		vel[Y] = randVel*Math.cos(randDir);
	}

	public void move(int Screen[]) {
		// Add velocity to position
		pos[X] += vel[X];
		pos[Y] += vel[Y];

		// Check to see if asteroid has completely gone out of bounds
		if (pos[X] < 0 - radius)
			pos[X] += Screen[X] + (2 * radius);
		if (pos[X] > Screen[X] + radius)
			pos[X] -= Screen[X] + (2 * radius);
		if (pos[Y] < 0 - radius)
			pos[Y] += Screen[Y] + (2 * radius);
		if (pos[Y] > Screen[Y] + radius)
			pos[Y] -= Screen[Y] + (2 * radius);
	}

	public void draw(Graphics Gfx) {
		Gfx.setColor(Color.lightGray);
		Gfx.fillOval((int) (pos[X] - radius + 0.5),
				(int) (pos[Y] - radius + 0.5), (int) (radius * 2),
				(int) (radius * 2));
	}

	public int getHealth() {
		return health;
	}

	/** Collision detection between asteroid and ship */
	public boolean collideShip(Ship ship) {
		/**
		 * If the sum of the radius from the ship and asteroid are greater than
		 * the distance between them, return true
		 */
		if (Math.pow(radius + ship.getRadius(), 2) > Math.pow(ship.getPos()[X]
				- pos[X], 2)
				+ Math.pow(ship.getPos()[Y] - pos[Y], 2)) {
			return true;
		} else
			return false;
	}

	/** Collision detection between asteroid and bullet */
	public boolean collideBullet(Bullet bullet) {
		/**
		 * If the sum of the radius from the ship and asteroid are greater than
		 * the distance between them, return true
		 */
		if (Math.pow(radius, 2) > Math.pow(bullet.getPos()[X] - pos[X], 2)
				+ Math.pow(bullet.getPos()[Y] - pos[Y], 2)) {
			return true;
		} else
			return false;
	}
	
	/** This creates 2 new asteroids. Used after collision with a bullet */
	public Asteroid splitAsteroid()	{
		return new Asteroid(pos, (health - 1), this.radius);
	}
	
	
}
