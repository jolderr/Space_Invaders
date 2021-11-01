import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Class to represent the game screen
 * */
public class MainWindow
{
	updateThread t1;				//update thread instance
	Thread thread;					//
	static JFrame Game;				//main display
	
	static JLabel background;		//display game background
	static JLabel userIcon;			//display user icon
	static JLabel GameOver;			//display game over display
	
	static JTextArea displayScore;	//Score text
		
	static KeyListener keyMain;		//key-listener to track key inputs (while game is running)
	
	static ArrayList<JLabel> bltAL;	//array list to hold bullet JLabels
	static ArrayList<JLabel> enyAL;	//array list to hold enemy JLabels
	
	static Sounds SoundEF;			//sound class object for playing sound ef
	
	static int score;					//score tracker
	
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
		SoundEF = new Sounds();
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
						//user hits the "up-arrow" or W key
						//user icon moves accordingly
						if(KeyEvent.getKeyText(e.getKeyCode()) == "Up" || e.getKeyCode() == KeyEvent.VK_W) { 
							if(userIcon.getY() != 7)	//checker to make sure player icon does not go outside bounds of frame
								userIcon.setLocation(userIcon.getX(), userIcon.getY() - 25);
						}
						
						//user hits the "down-arrow" or S key
						//user icon moves accordingly
						else if(KeyEvent.getKeyText(e.getKeyCode()) == "Down" || e.getKeyCode() == KeyEvent.VK_S) { 
							if(userIcon.getY() != 832) 	//checker to make sure player icon does not go outside bounds of frame
								userIcon.setLocation(userIcon.getX(), userIcon.getY() + 25);
						}
						
						//user hits the "left-arrow" or A key
						//user icon moves accordingly
						else if(KeyEvent.getKeyText(e.getKeyCode()) == "Left" || e.getKeyCode() == KeyEvent.VK_A) {
							if(userIcon.getX() != 25)	//checker to make sure player icon does not go outside bounds of frame
								userIcon.setLocation(userIcon.getX() - 50, userIcon.getY());
						}
						
						//user hits the "right-arrow" of D key
						//user icon moves accordingly
						else if(KeyEvent.getKeyText(e.getKeyCode()) == "Right" || e.getKeyCode() == KeyEvent.VK_D) {
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
							
							//try/catch to play bullet firing noise
							try { SoundEF.BulletSound(); }
							catch (Exception e1)
							{
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
		Game.addKeyListener(keyMain);	//add key listener to game JFrame to recognize key inputs
		Game.add(background);			//add JLabel for it to be displayed on game main frame
		Game.setResizable(false);		//make-sure window size cannot be altered
		Game.setVisible(true);			//set game to visible
		thread.start();					//call the next tread to update system commands
	}
}