package br.com.rbraga.service;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayerClip {

	private static final int instanceNumber = 2;
	private static Clip[] playersNoise = new Clip[instanceNumber];
	private static Clip playerRelax;
	private static FloatControl[] controls = new FloatControl[instanceNumber];
	private static FloatControl controlRelax;
	private static boolean stoppingPlayerRelax = false;

	private static Integer currentPlayerId = 0;
	private static boolean noiseMode = true;
	private static long microsecondPosition = 0;

	private static double gain = 0.5d;
	private final static long MICROSECOND_FADE = 200000; // 100000 = 0,1s
	private final static int SLEEP_TIME = 500; // 100000 = 0,1s
	private final static Runnable taskStop = () -> stop(), taskStoping = () -> stoping();
	private static Timer timer = new Timer(taskStop, taskStoping);
	private static boolean fadeOut = true;

	private static final String PATCH = System.getProperty("user.home") + File.separator + "WhiteNoiseServerSound";

	public static void Init() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		String songName;
		if (AudioPlayerClip.isDevelopmentEnvironment()) {
			songName = "White Noise (Sleep & Relaxation Sounds) Cut.wav";
		} else {
			songName = "White Noise (Sleep & Relaxation Sounds).wav";
		}
		String pathTo = PATCH + File.separator + songName;

		for (int i = 0; i < playersNoise.length; i++) {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File(pathTo));
			playersNoise[i] = AudioSystem.getClip();
			playersNoise[i].open(sound);

			controls[i] = (FloatControl) playersNoise[i].getControl(FloatControl.Type.MASTER_GAIN);
		}

		AudioInputStream sound = AudioSystem.getAudioInputStream(getFileRelax());
		playerRelax = AudioSystem.getClip();
		playerRelax.open(sound);

		controlRelax = (FloatControl) playerRelax.getControl(FloatControl.Type.MASTER_GAIN);

	}

	public static void play() {
		if (noiseMode) {
			playNoise();
		} else {
			playRelax();
		}
	}

	private static void playNoise() {
		AudioPlayerClip.setGain(gain < 0d ? 0.01d : gain);

		playersNoise[currentPlayerId].setMicrosecondPosition(microsecondPosition);
		if (!playersNoise[currentPlayerId].isRunning())
			playersNoise[currentPlayerId].start();

		new Thread() {
			public void run() {
				AudioPlayerClip.sleep(SLEEP_TIME);
				boolean loop = false;
				while (playersNoise[currentPlayerId].isRunning() && !loop) {
					System.out.println(playersNoise[currentPlayerId].getMicrosecondPosition() + " "
							+ playersNoise[currentPlayerId].getMicrosecondLength());
					if ((playersNoise[currentPlayerId].getMicrosecondLength()
							- MICROSECOND_FADE) <= playersNoise[currentPlayerId].getMicrosecondPosition()) {
						playLoop();
						loop = true;
					} else
						AudioPlayerClip.sleep(100);
				}
			}

		}.start();
	}

	private static void playRelax() {
		setGain(gain < 0d ? 0.01d : gain);

		if (!playerRelax.isRunning())
			playerRelax.start();

		new Thread() {
			public void run() {
				AudioPlayerClip.sleep(SLEEP_TIME);
				while (playerRelax.isRunning()) {
					AudioPlayerClip.sleep(SLEEP_TIME);
				}
				if (!stoppingPlayerRelax) {
					try {
						playerRelax.stop();
						playerRelax.close();

						AudioPlayerClip.sleep(SLEEP_TIME);
						System.gc();
						AudioPlayerClip.sleep(SLEEP_TIME);

						AudioInputStream sound = AudioSystem.getAudioInputStream(getFileRelax());
						playerRelax = AudioSystem.getClip();
						playerRelax.open(sound);

						controlRelax = (FloatControl) playerRelax.getControl(FloatControl.Type.MASTER_GAIN);
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						e.printStackTrace();
					}
					play();
				} else {
					stoppingPlayerRelax = false;
				}
			}

		}.start();
	}

	private static File getFileRelax() {
		final File folder = new File(PATCH);
		if (folder.isDirectory()) {
			File[] files = folder.listFiles((dir, name) -> name.startsWith("Relax"));
			return files[new Random().nextInt(files.length - 1)];
		}

		return null;
	}

	public static void pause() {
		if (AudioPlayerClip.noiseMode)
			pauseNoise();
		else
			pauseRelax();
	}

	public static void pauseNoise() {
		microsecondPosition = playersNoise[currentPlayerId].getMicrosecondPosition();
		playersNoise[currentPlayerId].stop();
	}

	public static void pauseRelax() {
		stopRelax();
	}

	public static void stop() {
		if (noiseMode)
			stopNoise();
		else
			stopRelax();
	}

	public static void stopNoise() {
		microsecondPosition = 0;
		playersNoise[currentPlayerId].stop();
	}

	public static void stopRelax() {
		stoppingPlayerRelax = true;
		playerRelax.stop();
	}

	public static void stoping() {
		if (fadeOut && (timer.isRunning() && ((double) timer.getRemainingSeconds() / 100) <= getGain()))
			setVolume((double) timer.getRemainingSeconds() / 100);
	}

	public static void turnUpVolume() {
		gain = gain >= 1d ? 1d : gain + 0.01d;
		setGain(gain);
	}

	public static void setVolume(double gain) {
		AudioPlayerClip.gain = gain;
		setGain(gain);
	}

	public static void turnDownVolume() {
		gain = gain <= 0d ? 0d : gain - 0.01d;
		setGain(gain);
	}

	public static double getGain() {
		return gain;
	}

	public static void playLoop() {
		microsecondPosition = 0;
		currentPlayerId = (playersNoise.length - 1) > currentPlayerId ? currentPlayerId + 1 : 0;
		play();
	}

	/**
	 * Sets Gain value.<br>
	 * Line should be opened before calling this method.<br>
	 * Linear scale 0.0 <--> 1.0<br>
	 * Threshold Coef. : 1/2 to avoid saturation.<br>
	 */
	private static void setGain(double fGain) {

		final FloatControl control = noiseMode ? controls[currentPlayerId] : controlRelax;

		double minGainDB = control.getMinimum();
		double ampGainDB = ((10.0f / 20.0f) * control.getMaximum()) - control.getMinimum();
		double cste = Math.log(10.0) / 20;
		double valueDB = minGainDB + (1 / cste) * Math.log(1 + (Math.exp(cste * ampGainDB) - 1) * fGain);
		control.setValue((float) valueDB);
	}

	private static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean status() {
		if (noiseMode) {
			return statusNoise();
		} else {
			return statusRelax();
		}
	}

	public static boolean statusNoise() {
		return playersNoise[currentPlayerId].isRunning();
	}

	public static boolean statusRelax() {
		return playerRelax.isRunning();
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

	public static void setMode(boolean modeNoise) {
		if (status()) {
			stop();
			AudioPlayerClip.noiseMode = modeNoise;
			play();
		} else {
			AudioPlayerClip.noiseMode = modeNoise;
		}
	}

	public static boolean isNoiseMode() {
		return noiseMode;
	}

	public static void close() {
		for (Clip player : playersNoise) {
			player.stop();
			player.close();
		}

		playerRelax.stop();
		playerRelax.close();
	}

}
