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

	// Game Entities
	private Ship ship;
	private Bullet bullets[];
	private Asteroid asteroids[];

	private static final int screenSize[] = { 750, 750 };

	/** Allows task to be performed by PC */
	Thread thread;

	/** Variable used for frame rate */
	long startTime, endTime, Frame;
	static final int FPS = 60;

	// Used for transform arrays
	static final int X = 0;
	static final int Y = 1;

	/** Position */
	int pos[];
	/** Velocity */
	float vel[];

	/** Stores size of back buffer */
	Dimension dim;
	/** Back buffer object */
	Image image;
	/** used to draw on back buffer */
	Graphics Gfx;

	// Override functions from Applet
	@Override
	public void init() {
		this.resize(screenSize[X], screenSize[Y]);
		
		pos = new int[]{0,0};
		vel = new float[]{0,0};

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
		Gfx.setColor(Color.black);
		Gfx.fillRect(0, 0, screenSize[0], screenSize[1]);
		Gfx.setColor(Color.white);
		Gfx.fillOval(pos[X], pos[Y], 30, 30);
		a_Gfx.drawImage(image, 0, 0, this);
	}

	@Override
	public void update(Graphics a_Gfx) {
		paint(a_Gfx);
	}

	@Override
	public void run() {
		while (true) {
			// set start time
			startTime = System.currentTimeMillis();
			
			pos[X] += vel[X];
			pos[Y] += vel[Y];
			
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
		if (e.getKeyCode() == KeyEvent.VK_UP
				|| e.getKeyCode() == KeyEvent.VK_W) {
			vel[Y] = -1;
		}
		// Down
		else if (e.getKeyCode() == KeyEvent.VK_DOWN
				|| e.getKeyCode() == KeyEvent.VK_S) {
			vel[Y] = 1;
		}
		// Left
		else if (e.getKeyCode() == KeyEvent.VK_LEFT
				|| e.getKeyCode() == KeyEvent.VK_A) {
			vel[X] = -1;
		}
		// Right
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_D) {
			vel[X] = 1;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Up
		if (e.getKeyCode() == KeyEvent.VK_UP
				|| e.getKeyCode() == KeyEvent.VK_W) {
			vel[Y] = 0;
		}
		// Down
		else if (e.getKeyCode() == KeyEvent.VK_DOWN
				|| e.getKeyCode() == KeyEvent.VK_S) {
			vel[Y] = 0;
		}
		// Left
		else if (e.getKeyCode() == KeyEvent.VK_LEFT
				|| e.getKeyCode() == KeyEvent.VK_A) {
			vel[X] = 0;
		}
		// Right
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_D) {
			vel[X] = 0;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
