package Main;

import java.applet.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import Entity.Asteroid;
import Entity.Bullet;
import Entity.Ship;

/** Main class that contains the game */
public class Game extends Applet implements Runnable, KeyListener {

	/** Ship Entity */
	Ship ship;
	/** Keeps track of whether or not the ship is firing */
	boolean firing;

	/** Bullet Entity List */
	Bullet bullets[];
	/** Keeps track of number of bullets on screen */
	int bulletsOnScreen;

	/** Asteroid Entity List */
	Asteroid asteroids[];
	/** Keeps track of number of asteroids on screen */
	int asteroidsOnScreen;

	private static final int screenSize[] = { 750, 750 };

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

	/** if true, game is running, if false, game is paused */
	boolean gameRunning;

	// Override functions from Applet
	@Override
	public void init() {
		this.resize(screenSize[X], screenSize[Y]);

		/** Variables to pass ship constructor */
		double center[] = new double[] { screenSize[X] / 2, screenSize[Y] / 2 }, rot = 0, accel = 0.35, velDecay = 0.85, rotVel = 0.1;
		ship = new Ship(center, rot, accel, velDecay, rotVel);
		firing = false;

		gameRunning = true;

		// No more than one bullet can be fired per frame and lifespan is 50.
		// This means it is impossible for there to be more than 50 bullets on
		// screen at one time.
		bullets = new Bullet[51];
		bulletsOnScreen = 0;

		asteroidsOnScreen = 0;

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

	@Override
	public void paint(Graphics a_Gfx) {
		// Code draws to the back buffer to prevent flashing
		Gfx.setColor(Color.black);
		Gfx.fillRect(0, 0, screenSize[0], screenSize[1]);

		// Draw Ship
		ship.draw(Gfx);

		// Draw all bullets on the screen
		for (int i = 0; i < bulletsOnScreen; ++i) {
			bullets[i].draw(Gfx);
		}

		// What is drawn on screen
		a_Gfx.drawImage(image, 0, 0, this);
	}

	@Override
	public void update(Graphics a_Gfx) {
		paint(a_Gfx);
		ship.update();
		for (int i = 0; i < bulletsOnScreen; ++i)
			bullets[i].update();
	}

	@Override
	public void run() {
		while (true) {
			// set start time
			startTime = System.currentTimeMillis();
			if (gameRunning) {
				ship.move(screenSize);
				for (int i = 0; i < bulletsOnScreen; ++i) {
					bullets[i].move(screenSize);
					if (bullets[i].getLife() <= 0) {
						deleteBullet(i);
						--i;
					}
				}
				// Check to see if player is trying to fire and if the ship is
				// ready to fire. If so, then add a bullet to the array and
				// fire.
				if (firing && ship.readyToFire()) {
					bullets[bulletsOnScreen] = ship.Fire();
					++bulletsOnScreen;
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

	/** Delete oldest bullet. Move rest of bullets up in array */
	public void deleteBullet(int index) {
		--bulletsOnScreen;
		for (int i = 0; i < bulletsOnScreen; ++i)
			bullets[i] = bullets[i + 1];
		bullets[bulletsOnScreen] = null;
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
}
