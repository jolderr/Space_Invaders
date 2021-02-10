import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Class to represent bullets shot from user
 * */
public class Bullet {
	JLabel label;	//JLabel to represent bullet on background
	
	/**
	 * Default Constructor
	 * */
	public Bullet() {
		setImage();	//calls helper to set JLabel
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
