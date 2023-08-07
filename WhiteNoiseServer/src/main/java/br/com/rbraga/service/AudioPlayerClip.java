package br.com.rbraga.service;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayerClip {

	private static final int instanceNumber = 2;
	private static Clip[] players = new Clip[instanceNumber];
	private static FloatControl[] controls = new FloatControl[instanceNumber];
	private static Integer currentPlayerId = 0;
	private static long microsecondPosition = 0;
	private static double gain = 0.5d;
	private final static long MICROSECOND_FADE = 200000; // 100000 = 0,1s
	private final static Runnable taskStop = () -> stop(), taskStoping = () -> stoping();
	private static Timer timer = new Timer(taskStop, taskStoping);
	private static boolean fadeOut = true;

	public static void Init() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		String songName;
		if (AudioPlayerClip.isDevelopmentEnvironment()) {
			songName = "White Noise (Sleep & Relaxation Sounds) Cut.wav";
		} else {
			songName = "White Noise (Sleep & Relaxation Sounds).wav";
		}
		String pathTo = System.getProperty("user.home") + File.separator + "WhiteNoiseServerSound" + File.separator
				+ songName;

		for (int i = 0; i < players.length; i++) {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File(pathTo));
			players[i] = AudioSystem.getClip();
			players[i].open(sound);

			controls[i] = (FloatControl) players[i].getControl(FloatControl.Type.MASTER_GAIN);
		}

	}

	public static void play() {
		AudioPlayerClip.setGain(gain < 0d ? 0.01d : gain);

		players[currentPlayerId].setMicrosecondPosition(microsecondPosition);
		if (!players[currentPlayerId].isRunning())
			players[currentPlayerId].start();

		new Thread() {
			public void run() {
				AudioPlayerClip.sleep(200);
				boolean loop = false;
				while (players[currentPlayerId].isRunning() && !loop) {
					if ((players[currentPlayerId].getMicrosecondLength() - MICROSECOND_FADE) <= players[currentPlayerId]
							.getMicrosecondPosition()) {
						playLoop();
						loop = true;
					}
					AudioPlayerClip.sleep(20);
				}
			}

		}.start();
	}

	public static void pause() {
		microsecondPosition = players[currentPlayerId].getMicrosecondPosition();
		players[currentPlayerId].stop();
	}

	public static void stop() {
		microsecondPosition = 0;
		players[currentPlayerId].stop();
	}

	public static void stoping() {
		if (fadeOut && (timer.isRunning() && ((double) timer.getRemainingSeconds() / 100) <= getGain()))
			setVolume((double) timer.getRemainingSeconds() / 100);
	}

	public static void turnUpVolume() {
		gain = gain >= 1d ? 1d : gain + 0.01d;
		AudioPlayerClip.setGain(gain);
	}

	public static void setVolume(double gain) {
		AudioPlayerClip.gain = gain;
		AudioPlayerClip.setGain(gain);
	}

	public static void turnDownVolume() {
		gain = gain <= 0d ? 0d : gain - 0.01d;
		AudioPlayerClip.setGain(gain);
	}

	public static double getGain() {
		return gain;
	}

	public static void playLoop() {
		microsecondPosition = 0;
		currentPlayerId = (players.length - 1) > currentPlayerId ? currentPlayerId + 1 : 0;
		AudioPlayerClip.play();
	}

	/**
	 * Sets Gain value.<br>
	 * Line should be opened before calling this method.<br>
	 * Linear scale 0.0 <--> 1.0<br>
	 * Threshold Coef. : 1/2 to avoid saturation.<br>
	 */
	private static void setGain(double fGain) {
		double minGainDB = controls[currentPlayerId].getMinimum();
		double ampGainDB = ((10.0f / 20.0f) * controls[currentPlayerId].getMaximum())
				- controls[currentPlayerId].getMinimum();
		double cste = Math.log(10.0) / 20;
		double valueDB = minGainDB + (1 / cste) * Math.log(1 + (Math.exp(cste * ampGainDB) - 1) * fGain);
		controls[currentPlayerId].setValue((float) valueDB);
	}

	private static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} // TODO Auto-generated method stub
	}

	public static boolean status() {
		return players[currentPlayerId].isRunning();
	}

	public static void setTimer(int seconds) {
		timer.adjustTime(seconds);
	}

	public static int getTimerRemainingSeconds() {
		return timer.getRemainingSeconds();
	}

	public static boolean isTimerRunning() {
		return timer.isRunning();
	}

	public static void stopTimer() {
		timer.stop();
	}

	public static boolean isDevelopmentEnvironment() {
		String environment = System.getenv("ENVIRONMENT");
		return "development".equalsIgnoreCase(environment);
	}

	public static void setFadeOut(boolean fadeOut) {
		AudioPlayerClip.fadeOut = fadeOut;
	}

	public static boolean isFadeOut() {
		return fadeOut && isTimerRunning();
	}

}
