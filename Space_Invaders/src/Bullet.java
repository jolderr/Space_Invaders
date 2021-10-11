import java.awt.Image;
import java.awt.Toolkit;
//import java.io.InputStream;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.io.File; 
import java.io.IOException; 

import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Clip;  
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Class to represent bullets shot from user
 * */
public class Bullet {
	JLabel label;	//JLabel to represent bullet on background
	AudioInputStream audioIn;
	Clip clip;
	/**
	 * Default Constructor
	 * @throws IOException, LineUnavaliableException 
	 * @throws UnsupportedAudioFileException 
	 * */
	public Bullet()
	{
		setImage();	//calls helper to set JLabel
		audioIn = null;
		clip = null;
	}
	
	public void play() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		audioIn = AudioSystem.getAudioInputStream(new File("bin/resources/shoot.wav").getAbsoluteFile());
		clip = AudioSystem.getClip();
		clip.open(audioIn);
		clip.start();
	}
	
	/**
	 * Helper method for setting class's JLabel label
	 * */
	public void setImage() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		URL imgURL = getClass().getResource("/resources/bullet.png");	//grabs image from resources folder
		Image image = toolkit.getImage(imgURL);
		ImageIcon imageicon = new ImageIcon(image);
		label = new JLabel(imageicon);	//create user icon using image in resource file
	}
}
