import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

/**
 * Class to represent the game screen
 * */
public class MainWindow
{
	public class updateThread implements Runnable
	{
		private final Object pauseLock = new Object();
		
		KeyListener keyOver = new KeyListener() {
			/**
			* Method called when key is pressed
			* @param KeyEvent Object 
			* @Override
			* */
			public void keyPressed(KeyEvent e)
			{
				background.removeAll();		//remove all elements on background display (game-over and score JLabel)
				Insets inset = background.getInsets();
				Dimension size = userIcon.getPreferredSize();
				
				//re-insets user Icon JLabel
				userIcon.setBounds(inset.left, inset.right, size.width, size.height);
				userIcon.setLocation((Game.getWidth()/2) - (userIcon.getWidth()/2), (Game.getHeight()/2) + (userIcon.getHeight()/2));
				
				//starts the thread again
				synchronized(pauseLock) { pauseLock.notifyAll(); }
				
				background.add(userIcon);			//user icon added back
				background.repaint();
				Game.addKeyListener(keyMain);		//add main key-listener
				Game.removeKeyListener(keyOver);	//remove current key-listener
			}
			/**
			* Method not used
			* @Override
			* */
			public void keyReleased(KeyEvent e) {}
			/**
			* Method not used
			* @Override
			* */
			public void keyTyped(KeyEvent e) {}
							
			};
		
		/**
		 * Method to constantly update figures on screen
		 * */
		public void run()
		{
			int x = 0;	//counter
			while(true) {
				//cause a delay in loop, pauses thread
				synchronized(this) {
					try { this.wait(20); } 
					catch (InterruptedException e) {
						Thread.currentThread().interrupt();				//catcher
						System.out.println("Thread interrupted: " + e);	//system output thread interrupted
					}
				}
				hit();			// call method  to keep track of when a bullet hits an icon
				
				//causes delay in enemy spawning
				if(x == 120) { addEnemy(); x = 0; }
				else {x++;}
				update();	//method call to move all JLabels
			}
		}
		

		/**
		 * method to check for if bullet has hit an enemy
		 * */
		public void hit()
		{
			//for each enemy on screen
			for(int i = 0; i < enyAL.size(); i++) {
				Area AE = new Area(enyAL.get(i).getBounds());		//create an area of the enemy
				//for each bullet fired
				for(int l = 0; l < bltAL.size(); l++) {
					Area AB = new Area(bltAL.get(l).getBounds());	//create an area of the bullet
					
					//if the two area's intersect, the bullet collided with the enemy
					if(AE.intersects(AB.getBounds2D()) == true) {
						background.remove(enyAL.get(i));			//remove enemy
						background.remove(bltAL.get(l));			//remove bullet
						background.repaint();		
						enyAL.remove(i);							//remove enemy from array list
						bltAL.remove(l);							//remove bullet from array list
						score++;									//update score
					}
				}
			}
		}
		
		/**
		 * Method to track bullet movement
		 * @Override
		 * */
		public void update()
		{
			//moves bullet JLabels
			for(int i = 0; i < bltAL.size(); i++) 
			{
				bltAL.get(i).setLocation(bltAL.get(i).getX(), bltAL.get(i).getY() - 30);
				
				// checks if bullets have gone off-screen
				if (bltAL.get(i).getY() <= 10)
				{
					background.remove(bltAL.get(i));	// remove image from background
					bltAL.remove(i);					// remove bullet image from list
				}
			}
			
			//moves enemy JLabels and checks for game loss scenario
			for(int i = 0; i < enyAL.size(); i++) {
				enyAL.get(i).setLocation(enyAL.get(i).getX(), enyAL.get(i).getY() + 2);
				
				//if game enemy passes boarder, display lose screen
				if(enyAL.get(i).getY() >= Game.getHeight() - 110) {
					lose();			//calls helper
				}
			}
		}
		
		/**
		 * helper method called whenever user loses game (enemy reaches end of screen)
		 * */
		public void lose()
		{
			Insets inset = background.getInsets();
			Dimension size = GameOver.getPreferredSize();
			
			//set bounds for game over JLabel
			GameOver.setBounds(inset.left, inset.right, size.width, size.height);
			GameOver.setLocation((Game.getWidth()/2) - (GameOver.getWidth()/2), (Game.getHeight()/2) - (GameOver.getHeight()/2));
			
			//set text area
			displayScore.setText("Score: " + score);
			size = displayScore.getPreferredSize();
			
			//set bounds for text area
			displayScore.setBounds(inset.left, inset.right, size.width, size.height);
			displayScore.setLocation((Game.getWidth()/2) - (displayScore.getWidth()/2), Game.getHeight() - (displayScore.getHeight() + 100));
			
			//remove all previous components to get clear screen and data
			bltAL.removeAll(bltAL);
			enyAL.removeAll(enyAL);
			background.removeAll();
			
			//add game over JLabel and display score text-area
			background.add(GameOver);
			background.add(displayScore);
			background.repaint();
			Game.removeKeyListener(keyMain);	//remove main key listener
			Game.addKeyListener(keyOver);		//add game over key listener
			
			score = 0;	//reset score
			
			synchronized(pauseLock) {
				try {
					pauseLock.wait();	//tell all threads to wait
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();				//catcher
					System.out.println("Thread interrupted: " + e);	//system output thread interrupted
				}
			}
		}
		
		/**
		 * Helper Method to add enemies to the screen
		 * */
		public void addEnemy()
		{
			Random rnd = new Random();
			int r = -1;	//random number holder
			int x = 40;	//x-coordinate holder
			for(int i = 0; i < 10; i++) {
				//spawn random integer 0-1
				r = rnd.nextInt(2);
				
				//50% chance for enemy to spawn in certain location
				//increase x-holder to set location for next enemy
				if(r == 1) { addEnemy_helper(x); x += 155; }
				else { x += 155; }
			}
		}
		
		/**
		 * Helper Method for addEnemy() method
		 * */
		public void addEnemy_helper(int _x)
		{
			Enemy Eny = new Enemy();	//create new enemy instance to get it's enemy JLabel
			enyAL.add(Eny.label);		//add enemy JLabel to array list 
			background.add(Eny.label);	//add JLabel to background for it to be displayed
			
			Insets inset = background.getInsets();
			Dimension size = Eny.label.getPreferredSize();
			
			//set bounds for label (size and location)
			Eny.label.setBounds(_x + inset.left, inset.right, size.width, size.height);
			
		}
		
		
	}
	
	updateThread t1;			//thread tracks updates
	Thread thread;
	JFrame Game;				//main display
	
	JLabel background;			//display game background
	JLabel userIcon;			//display user icon
	JLabel GameOver;			//display game over display
	
	JTextArea displayScore;		//Score text
		
	KeyListener keyMain;		//key-listener to track key inputs (while game is running)
	KeyListener keyOver;
	
	ArrayList<JLabel> bltAL;	//array list to hold bullet JLabels
	ArrayList<JLabel> enyAL;	//array list to hold enemy JLabels
	
	int score;					//score tracker
	
	/**
	 * Default Constructor
	 * */
	public MainWindow()
	{
		t1 = new updateThread();
		thread = new Thread(t1);
		Game = new JFrame();
		background = new JLabel();
		userIcon = new JLabel();
		displayScore = new JTextArea();
		Font font = new Font("Copperplate Gothic Bold", Font.BOLD, 55);
		displayScore.setFont(font);
		bltAL = new ArrayList<JLabel>();
		enyAL = new ArrayList<JLabel>();
		score = 0;
	}
	
	/**
	 * Method to initialize game window
	 * */
	public void init()
	{
		Game.setSize(1600, 1047);	//set window dimensions
		Game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		URL imgURL = getClass().getResource("/resources/space.gif");	//grabs image from resources folder
		Image image = toolkit.getImage(imgURL);
		background = new JLabel(new ImageIcon(image));	//create background using gif in resource file
		background.setLayout(null);		//background null so JLabels can be set specifically
		
		imgURL = getClass().getResource("/resources/user.png");		//grabs image from resources folder
		image = toolkit.getImage(imgURL);
		userIcon = new JLabel(new ImageIcon(image));	//create user icon using image in resource file
		background.add(userIcon);	//add user icon to background for display
		
		imgURL = getClass().getResource("/resources/gameover.png");
		image = toolkit.getImage(imgURL);
		GameOver = new JLabel(new ImageIcon(image));

		Insets inset = background.getInsets();
		Dimension size = userIcon.getPreferredSize();
		
		//set bounds for label (size and location)
		userIcon.setBounds(inset.left, inset.right, size.width, size.height);
		userIcon.setLocation((Game.getWidth()/2) - (userIcon.getWidth()/2), (Game.getHeight()/2) + (userIcon.getHeight()/2));
		
		displayScore.setEditable(false);
		displayScore.setOpaque(false);
		displayScore.setForeground(Color.MAGENTA);
		
		//key listener to take in keyboard commands while game is running
		keyMain = new KeyListener()
		{
			/**
			 * Method called when key is pressed
			 * @param KeyEvent Object 
			 * @Override
			 * */
				public void keyPressed(KeyEvent e)
				{
						//user hits the "up-arrow" key
						//user icon moves accordingly
						if(KeyEvent.getKeyText(e.getKeyCode()) == "Up") { 
							if(userIcon.getY() != 7)	//checker to make sure player icon does not go outside bounds of frame
								userIcon.setLocation(userIcon.getX(), userIcon.getY() - 25);
						}
						
						//user hits the "down-arrow" key
						//user icon moves accordingly
						else if(KeyEvent.getKeyText(e.getKeyCode()) == "Down") { 
							if(userIcon.getY() != 832) 	//checker to make sure player icon does not go outside bounds of frame
								userIcon.setLocation(userIcon.getX(), userIcon.getY() + 25);
						}
						
						//user hits the "left-arrow" key
						//user icon moves accordingly
						else if(KeyEvent.getKeyText(e.getKeyCode()) == "Left") {
							if(userIcon.getX() != 25)	//checker to make sure player icon does not go outside bounds of frame
								userIcon.setLocation(userIcon.getX() - 50, userIcon.getY());
						}
						
						//user hits the "right-arrow" key
						//user icon moves accordingly
						else if(KeyEvent.getKeyText(e.getKeyCode()) == "Right") {
							if(userIcon.getX() != 1425)	//checker to make sure player icon does not go outside bounds of frame
								userIcon.setLocation(userIcon.getX() + 50, userIcon.getY());
						}
						
						//user hits the "space" key
						//user icon creates bullet (moves using update thread)
						else if(KeyEvent.getKeyText(e.getKeyCode()) == "Space") {
							Bullet bullet = new Bullet();	//create bullet instance
							bltAL.add(bullet.label);		//add bullet JLabel to Array List
							background.add(bullet.label);	//add bullet JLabel to background for it to be displayed

							Insets inset = background.getInsets();
							Dimension size = bullet.label.getPreferredSize();
							
							//set bounds for label (size and location)
							bullet.label.setBounds(userIcon.getX() + (userIcon.getWidth()/2) - 11 + inset.left, (userIcon.getY() - 113) + inset.right, size.width, size.height);
							
							try {
								bullet.play();
							} catch (Exception e1) {
								System.out.println("Error with playing bullet sound");
								e1.printStackTrace();
							}

						}
		}
			/**
			 * Method not used
			 * @Override
			 * */
			public void keyReleased(KeyEvent e) {}
			/**
			 * Method not used
			 * @Override
			 * */
			public void keyTyped(KeyEvent e) {}
		};
		//Game.addKeyListener(keyMain);	//add key listener to game JFrame to recognize key inputs
		Game.addKeyListener(keyMain);
		Game.add(background);				//add JLabel for it to be displayed on game main frame
		Game.setResizable(false);
		Game.setVisible(true);				//set game to visible
		thread.start();						//call the next tread to update system commands
	}
}