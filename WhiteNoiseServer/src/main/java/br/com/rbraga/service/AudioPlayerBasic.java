package br.com.rbraga.service;

import java.io.File;

import javax.sound.sampled.SourceDataLine;

import javazoom.jlgui.basicplayer.BasicPlayerException;

@Deprecated
public class AudioPlayerBasic {

	static class BasicPlayer extends javazoom.jlgui.basicplayer.BasicPlayer {
		public SourceDataLine getLine() {
			return super.m_line;
		}
	}

	private final static BasicPlayer[] players = { new BasicPlayer(), new BasicPlayer() };
	protected static Integer currentPlayerId = 0;
	private static float gain = 0.5f;

	private AudioPlayerBasic() {
		// static class
	}

	public static void Init() throws BasicPlayerException {
		String songName;
		if (AudioPlayerBasic.isDevelopmentEnvironment()) {
			songName = "spotifydown.com - White Noise (Sleep & Relaxation Sounds), Pt. 02 Cut.wav";
		} else {
			songName = "spotifydown.com - White Noise (Sleep & Relaxation Sounds), Pt. 02.wav";
		}

		String pathToMp3 = System.getProperty("user.home") + File.separator + "WhiteNoiseServerSound" + File.separator
				+ songName;

		initPlayers(pathToMp3);

	}

	private static void initPlayers(String pathToMp3) throws BasicPlayerException {
		for (int i = 0; i < players.length; i++) {
			players[i].open(new File(pathToMp3));
			players[i].addBasicPlayerListener(new AudioPlayerBasicListener());
		}
	}

	public static void resume() throws BasicPlayerException {
		players[currentPlayerId].resume();
	}

	public static void play() throws BasicPlayerException {
		players[currentPlayerId].play();
		players[currentPlayerId].setGain(gain);
	}

	public static void pause() throws BasicPlayerException {
		players[currentPlayerId].pause();
	}

	public static void stop() throws BasicPlayerException {
		for (int i = 0; i < players.length; i++) {
			players[i].stop();
		}
	}

	public static void turnUpVolume() throws BasicPlayerException {
		gain = gain >= 1f ? 1f : gain + 0.05f;
		players[currentPlayerId].setGain(gain);
	}

	public static void setVolume(float gain) throws BasicPlayerException {
		AudioPlayerBasic.gain = gain;
		players[currentPlayerId].setGain(gain);
	}

	public static void turnDownVolume() throws BasicPlayerException {
		gain = gain <= 0f ? 0f : gain - 0.05f;
		players[currentPlayerId].setGain(gain);
	}

	public static void playLoop() throws BasicPlayerException {
		currentPlayerId = (players.length - 1) > currentPlayerId ? currentPlayerId + 1 : 0;
		AudioPlayerBasic.play();
	}

	public static int available() {
		return players[currentPlayerId].getLine().available();
	}

	public static boolean isDevelopmentEnvironment() {
		String environment = System.getenv("ENVIRONMENT");
		return "development".equalsIgnoreCase(environment);
	}

}