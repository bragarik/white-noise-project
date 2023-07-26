package br.com.rbraga.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

@Deprecated
public class AudioPlayerBasicListener implements BasicPlayerListener {
	private final Logger log = LoggerFactory.getLogger(AudioPlayerBasicListener.class);

	@Override
	public void opened(Object stream, @SuppressWarnings("rawtypes") Map properties) {
	}

	@Override
	public void progress(int bytesread, long microseconds, byte[] pcmdata,
			@SuppressWarnings("rawtypes") Map properties) {

		if (AudioPlayerBasic.available() < 500) {
			try {
				AudioPlayerBasic.playLoop();
			} catch (BasicPlayerException e) {
				log.error(e.getMessage());
			}
		}
	}

	@Override
	public void stateUpdated(BasicPlayerEvent event) {
	}

	@Override
	public void setController(BasicController controller) {
	}

}
