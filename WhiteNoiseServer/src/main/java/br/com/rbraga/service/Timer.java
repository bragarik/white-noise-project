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
					taskStoping.run();
					remainingSeconds--;
				}
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

//	public static void main(String[] args) {
//		CustomTimer customTimer = new CustomTimer();
//
//		Runnable task = () -> System.out.println("Timer task executed!");
//
//		customTimer.start(60, task); // Iniciar o timer com 60 segundos
//
//		try {
//			Thread.sleep(30000); // Esperar 30 segundos
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		customTimer.adjustTime(90); // Alterar o tempo para 90 segundos
//
//		try {
//			Thread.sleep(60000); // Esperar mais 60 segundos
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		customTimer.stop(); // Parar o timer
//
//		System.out.println("Remaining seconds: " + customTimer.getRemainingSeconds());
//	}
}
