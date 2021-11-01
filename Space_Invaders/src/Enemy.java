import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Class to represent enemy ships
 * */
public class Enemy {
	JLabel label;	//JLabel to represent enemy on background
	
	/**
	 * Default Constructor
	 * */
	public Enemy() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		URL imgURL = getClass().getResource("/resources/enemy.png");	//grabs image from resources folder
		Image image = toolkit.getImage(imgURL);
		ImageIcon imageicon = new ImageIcon(image);
		label = new JLabel(imageicon);	//create user icon using image in resource file
	}
}
