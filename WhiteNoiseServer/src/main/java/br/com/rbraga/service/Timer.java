package br.com.rbraga.service;

import java.util.TimerTask;

public class Timer {
	private java.util.Timer timer;
	private TimerTask timerTask;
	private int remainingSeconds;
	private boolean isRunning;
	private final Runnable taskStop, taskStoping;

	public Timer(Runnable taskStop, Runnable taskStoping) {
		this.taskStop = taskStop;
		this.taskStoping = taskStoping;
		timer = new java.util.Timer();
		isRunning = false;
	}

	private void scheduleTask(int delay, int period) {
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (remainingSeconds <= 0) {
					stop();
					taskStop.run();
				} else {
					remainingSeconds--;
				}
				taskStoping.run();
			}
		};
		timer.scheduleAtFixedRate(timerTask, delay, period);
	}

	public void start(int seconds) {
		if (!isRunning) {
			isRunning = true;
			remainingSeconds = seconds;

			scheduleTask(1000, 1000); // Executar a cada segundo
		}
	}

	public void stop() {
		if (isRunning) {
			timerTask.cancel();
			timer.purge();
			isRunning = false;
			remainingSeconds = 0;
			System.out.println("Timer stopped.");
		}
	}

	public void adjustTime(int newSeconds) {
		if (isRunning) {
			remainingSeconds = newSeconds;
		} else {
			start(newSeconds);
		}
	}

	public int getRemainingSeconds() {
		return remainingSeconds;
	}

	public boolean isRunning() {
		return isRunning;
	}

}
