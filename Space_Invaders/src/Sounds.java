import java.io.IOException;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sounds implements Runnable {
	
	AudioInputStream audioIn;
	Clip clip;
	
	/**
	 * Default constructor
	 * */
	public Sounds(){
		audioIn = null;
		clip = null;
	}
	
	
	/**
	 * Method to constantly play background music
	 * */
	public void run() {
		// TODO Implement background music functionality
	}
	
	/**
	 * Method to play bullet firing sound effect
	 * @throws UnsupportedAudioFileException, IOException, LineUnavailableException
	 * */
	public void BulletSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		audioIn = AudioSystem.getAudioInputStream(new File("bin/resources/shoot.wav").getAbsoluteFile());
		play();
	}
	
	/**
	 * Method to play hit sound effect whenever user destroys enemy
	 * @throws UnsupportedAudioFileException, IOException, LineUnavailableException
	 * */
	public void HitSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		audioIn = AudioSystem.getAudioInputStream(new File("bin/resources/hit.wav").getAbsoluteFile());
		play();
	}
	
	/**
	 * Method to play sound effect when user loses
	 * @throws UnsupportedAudioFileException, IOException, LineUnavailableException
	 * */
	public void gameOverSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		audioIn = AudioSystem.getAudioInputStream(new File("bin/resources/gameOver.wav").getAbsoluteFile());
		play();
	}
	
	/**
	 * Helper method to play sound effects
	 * @throws LineUnavailableException, IOException
	 * */
	public void play() throws LineUnavailableException, IOException
	{
		clip = AudioSystem.getClip();
		clip.open(audioIn);
		clip.start();
	}
}
