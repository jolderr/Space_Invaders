import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;
import java.util.Random;

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
				if(KeyEvent.getKeyText(e.getKeyCode()) == "Enter")
				{
					MainWindow.background.removeAll();		//remove all elements on background display (game-over and score JLabel)
					Insets inset = MainWindow.background.getInsets();
					Dimension size = MainWindow.userIcon.getPreferredSize();
					
					//re-insets user Icon JLabel
					MainWindow.userIcon.setBounds(inset.left, inset.right, size.width, size.height);
					MainWindow.userIcon.setLocation((MainWindow.Game.getWidth()/2) - (MainWindow.userIcon.getWidth()/2), (MainWindow.Game.getHeight()/2) + (MainWindow.userIcon.getHeight()/2));
					
					//starts the thread again
					synchronized(pauseLock) { pauseLock.notifyAll(); }
					
					MainWindow.background.add(MainWindow.userIcon);			//user icon added back
					MainWindow.background.repaint();
					MainWindow.Game.addKeyListener(MainWindow.keyMain);		//add main key-listener
					MainWindow.Game.removeKeyListener(keyOver);	//remove current key-listener
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
		 * TODO: Implement more efficient hit detection
		 * */
		public void hit()
		{
			//for each enemy on screen
			for(int i = 0; i < MainWindow.enyAL.size(); i++) {
				Area AE = new Area(MainWindow.enyAL.get(i).getBounds());		//create an area of the enemy
				//for each bullet fired
				for(int l = 0; l < MainWindow.bltAL.size(); l++) {
					Area AB = new Area(MainWindow.bltAL.get(l).getBounds());	//create an area of the bullet
					
					//if the two area's intersect, the bullet collided with the enemy
					if(AE.intersects(AB.getBounds2D()) == true) {
						//try/catch to play hit sound effect
						try { MainWindow.SoundEF.HitSound(); }
						catch (Exception e1)
						{
							System.out.println("Error with playing hit sound");
							e1.printStackTrace();
						}
						MainWindow.background.remove(MainWindow.enyAL.get(i));			//remove enemy
						MainWindow.background.remove(MainWindow.bltAL.get(l));			//remove bullet
						MainWindow.background.repaint();		
						MainWindow.enyAL.remove(i);							//remove enemy from array list
						MainWindow.bltAL.remove(l);							//remove bullet from array list
						MainWindow.score++;									//update score
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
			for(int i = 0; i < MainWindow.bltAL.size(); i++) 
			{
				MainWindow.bltAL.get(i).setLocation(MainWindow.bltAL.get(i).getX(), MainWindow.bltAL.get(i).getY() - 30);
				
				// checks if bullets have gone off-screen
				if (MainWindow.bltAL.get(i).getY() <= 10)
				{
					MainWindow.background.remove(MainWindow.bltAL.get(i));	// remove image from background
					MainWindow.bltAL.remove(i);					// remove bullet image from list
				}
			}
			
			//moves enemy JLabels and checks for game loss scenario
			for(int i = 0; i < MainWindow.enyAL.size(); i++) {
				MainWindow.enyAL.get(i).setLocation(MainWindow.enyAL.get(i).getX(), MainWindow.enyAL.get(i).getY() + 2);
				
				//if game enemy passes boarder, display lose screen
				if(MainWindow.enyAL.get(i).getY() >= MainWindow.Game.getHeight() - 110) {
					lose();			//calls helper
				}
			}
		}
		
		/**
		 * helper method called whenever user loses game (enemy reaches end of screen)
		 * */
		public void lose()
		{
			Insets inset = MainWindow.background.getInsets();
			Dimension size = MainWindow.GameOver.getPreferredSize();
			
			//set bounds for game over JLabel
			MainWindow.GameOver.setBounds(inset.left, inset.right, size.width, size.height);
			MainWindow.GameOver.setLocation((MainWindow.Game.getWidth()/2) - (MainWindow.GameOver.getWidth()/2), (MainWindow.Game.getHeight()/2) - (MainWindow.GameOver.getHeight()/2));
			
			//set text area
			MainWindow.displayScore.setText("Score: " + MainWindow.score);
			size = MainWindow.displayScore.getPreferredSize();
			
			//set bounds for text area
			MainWindow.displayScore.setBounds(inset.left, inset.right, size.width, size.height);
			MainWindow.displayScore.setLocation((MainWindow.Game.getWidth()/2) - (MainWindow.displayScore.getWidth()/2), MainWindow.Game.getHeight() - (MainWindow.displayScore.getHeight() + 100));
			
			//remove all previous components to get clear screen and data
			MainWindow.bltAL.removeAll(MainWindow.bltAL);
			MainWindow.enyAL.removeAll(MainWindow.enyAL);
			MainWindow.background.removeAll();
			
			//add game over JLabel and display score text-area
			MainWindow.background.add(MainWindow.GameOver);
			MainWindow.background.add(MainWindow.displayScore);
			MainWindow.background.repaint();
			MainWindow.Game.removeKeyListener(MainWindow.keyMain);	//remove main key listener
			MainWindow.Game.addKeyListener(keyOver);		//add game over key listener
			
			MainWindow.score = 0;	//reset score
			
			//play game over sound
			try { MainWindow.SoundEF.gameOverSound(); }
			catch (Exception e1)
			{
				System.out.println("Error with playing game Over sound");
				e1.printStackTrace();
			}
			
			synchronized(pauseLock) {
				try {
					pauseLock.wait();	//tell all threads to wait
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();				//catcher
					System.out.println("Thread interrupted: " + e.getMessage() + "  "+ e.getStackTrace());	//system output thread interrupted
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
			MainWindow.enyAL.add(Eny.label);		//add enemy JLabel to array list 
			MainWindow.background.add(Eny.label);	//add JLabel to background for it to be displayed
			
			Insets inset = MainWindow.background.getInsets();
			Dimension size = Eny.label.getPreferredSize();
			
			//set bounds for label (size and location)
			Eny.label.setBounds(_x + inset.left, inset.right, size.width, size.height);
		}
	}