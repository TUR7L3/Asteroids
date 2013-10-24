package Main;

import java.applet.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import Entity.Asteroid;
import Entity.Bullet;
import Entity.Ship;

/** Main class that contains the game */
public class Game extends Applet implements Runnable, KeyListener {
	// ENTITIES
	/** Ship Entity */
	Ship ship;
	/** Keeps track of whether or not the ship is firing */
	boolean firing;

	/** Bullet Entity List */
	ArrayList<Bullet> bullets;

	/** Asteroid Entity List */
	ArrayList<Asteroid> asteroids;

	// GRAPHICS VARIABLES
	private static final int Screen[] = { 750, 750 };

	/** Allows task to be performed by PC */
	Thread thread;

	/** Variable used for frame rate */
	long startTime, endTime, Frame;
	static final int FPS = 60;

	/** Used for array indexes */
	static final int X = 0, Y = 1;

	/** Stores size of back buffer */
	Dimension dim;
	/** Back buffer object */
	Image image;
	/** used to draw on back buffer */
	Graphics Gfx;

	// GAME LOGIC VARIABLES
	/** if true, game is running, if false, game is paused */
	boolean gameRunning;

	/** Number of lives player has before Game Over */
	int lives;

	/** Level of difficulty player is on. Gets higher as game goes on */
	int level;

	/** Variables to pass ship constructor */
	double center[] = new double[] { Screen[X] / 2, Screen[Y] / 2 }, rot = 0,
			accel = 0.35, velDecay = 0.85, rotVel = 0.1;

	// Override functions from Applet
	@Override
	public void init() {
		this.resize(Screen[X], Screen[Y]);

		ship = new Ship(center, rot, accel, velDecay, rotVel);
		firing = false;

		gameRunning = true;
		lives = 3;
		level = 0;

		bullets = new ArrayList<Bullet>();

		asteroids = new ArrayList<Asteroid>();

		addKeyListener(this);

		startTime = 0;
		endTime = 0;
		Frame = 1000 / FPS;

		// Code to draw to back buffer.
		dim = getSize(); // set dim to size of applet
		image = createImage(dim.width, dim.height); // create back buffer
		Gfx = image.getGraphics(); // Retrieve graphics for back buffer

		thread = new Thread(this);
		thread.start();
	}

	/** Load Next Level */
	public void nextLevel() {
		level++;
		ship = new Ship(center, rot, accel, velDecay, rotVel);
		double randPos[] = new double[2];
		for (int i = 0; i < level; ++i) {
			randPos[X] = (Math.random() * Screen[X]);
			randPos[Y] = (Math.random() * Screen[Y]);
			asteroids.add(new Asteroid(randPos));
		}
	}

	@Override
	public void paint(Graphics a_Gfx) {
		// Code draws to the back buffer to prevent flashing
		Gfx.setColor(Color.black);
		Gfx.fillRect(0, 0, Screen[0], Screen[1]);
		if (lives > 0) {
			// Draw Ship
			ship.draw(Gfx);

			// Draw all bullets on the screen
			for (int i = 0; i < bullets.size(); ++i) {
				bullets.get(i).draw(Gfx);
			}
			// Draw asteroids
			for (int i = 0; i < asteroids.size(); ++i) {
				asteroids.get(i).draw(Gfx);
			}

			// Display the level number in top left corner
			Gfx.setColor(Color.cyan);
			Gfx.drawString("Level " + level, 20, 20);
		}
		if(lives <= 0)	{
			// Display GAME OVER in center of screen
						Gfx.setColor(Color.cyan);
						Gfx.drawString("GAME OVER", (int)center[X],(int)center[Y]);
		}
		// What is drawn on screen
		a_Gfx.drawImage(image, 0, 0, this);
	}

	@Override
	public void update(Graphics a_Gfx) {
		paint(a_Gfx);
		ship.update();
		for (int i = 0; i < bullets.size(); ++i) {
			bullets.get(i).update();
		}
	}

	@Override
	public void run() {
		while (true) {
			// set start time
			startTime = System.currentTimeMillis();

			if (asteroids.size() <= 0)
				nextLevel();

			if (gameRunning) {
				ship.move(Screen);
				for (int i = bullets.size() - 1; i >= 0; --i) {
					bullets.get(i).move(Screen);
					if (bullets.get(i).getLife() <= 0)
						bullets.remove(i);
				}

				asteroidUpdate();

				// Check to see if player is trying to fire and if the ship is
				// ready to fire. If so, then add a bullet to the array and
				// fire.
				if (firing && ship.readyToFire()) {
					bullets.add(ship.Fire());
				}
			}
			repaint();

			// Pause until frame time is met.
			try {
				endTime = System.currentTimeMillis();
				if (Frame - (endTime - startTime) > 0)
					Thread.sleep(Frame - (endTime - startTime));
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Up
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			ship.setForwardAccel(true);
		}
		// Left
		else if (e.getKeyCode() == KeyEvent.VK_LEFT
				|| e.getKeyCode() == KeyEvent.VK_A) {
			ship.setLeftAccel(true);
		}
		// Right
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_D) {
			ship.setRightAccel(true);
		}
		// Spacebar to Fire
		else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			firing = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Up
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			ship.setForwardAccel(false);
		}
		// Left
		else if (e.getKeyCode() == KeyEvent.VK_LEFT
				|| e.getKeyCode() == KeyEvent.VK_A) {
			ship.setLeftAccel(false);
		}
		// Right
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_D) {
			ship.setRightAccel(false);
		}
		// Spacebar to Fire
		else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			firing = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	private void asteroidUpdate() {
		for (int i = 0; i < asteroids.size(); ++i) {
			asteroids.get(i).move(Screen);
			if (asteroids.get(i).collideShip(ship)) {
				--lives;
				--level;
				asteroids.clear();
				return;
			}
			for (int j = 0; j < bullets.size(); ++j) {
				if (asteroids.get(i).collideBullet(bullets.get(j))) {
					bullets.remove(j);
					if (asteroids.get(i).getHealth() >= 1) {
						for (int k = 0; k < asteroids.get(i).asteroidSplit; ++k) {
							asteroids.add(asteroids.get(i).splitAsteroid());
						}
					}
					asteroids.remove(i);
					j = bullets.size();
					--i;
				}
			}
		}
	}
}
