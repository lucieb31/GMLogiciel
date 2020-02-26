package fr.toulousescape.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.toulousescape.util.listeners.TimerListener;

public class Chrono {
	
	private int finalTime = 3600;
	
	private int currentTime;
	
	private List<TimerListener> listeners;

	private Timer timer;
	
	private int extraTime;
	
	public Chrono(int extraTime) {
		this.extraTime = extraTime;
		listeners = new ArrayList<>();
		finalTime = finalTime + extraTime;
	}

	public void start()
	{
		if (currentTime == 0) {
			currentTime = finalTime;
		}
		
		timer = new Timer();

		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				currentTime--;
				fireTimeChanged(currentTime);
				if (currentTime == 0) {
					timer.cancel();
				}
			}
		};
		
		timer.schedule(task, 1000, 1000);
	}
	
	public void addTimerListener(TimerListener tListener)
	{
		listeners.add(tListener);
	}
	
	public void removeTimerListener(TimerListener tListener)
	{
		listeners.remove(tListener);
	}
	
	public void fireTimeChanged(int newTime)
	{
		for(TimerListener tl : listeners)
		{
			tl.timeChanged(newTime);
		}
	}

	public void setTime(int time) {
		finalTime = time * 60;
		fireTimeChanged(finalTime);
	}

	public void pause() {
		timer.cancel();
	}

	public void stop() {
		timer.cancel();
		currentTime = 0;
		fireTimeChanged(finalTime);
	}

	public void removeTime(Integer valueOf) {
		if (currentTime != 0)
		{
			currentTime -= (valueOf * 60);
			fireTimeChanged(currentTime);
		}
		else
		{
			finalTime -= (valueOf * 60);
			fireTimeChanged(finalTime);
		}
	}
	
	public void addTime(Integer value)
	{
		if (currentTime != 0)
		{
			currentTime += (value * 60);
			fireTimeChanged(currentTime);
		}
		else
		{
			finalTime += (value * 60);
			fireTimeChanged(finalTime);
		}
	}

	public int getCurrentTime() {
		return currentTime;
	}
	
	public int getExtraTime() {
		return extraTime;
	}

	public static String formatTime(int timeToFormat) {
		// Calculate current time
		int hour = timeToFormat / 3600;
		int minutes = (timeToFormat % 3600) / 60;
		int secondes = ((timeToFormat - minutes * 60) % 60);

		String s = String.valueOf(secondes);

		if (secondes < 10) {
			s = "0" + s;
		}

		String m = String.valueOf(minutes);
		if (minutes < 10) {
			m = "0" + m;
		}

		if (hour > 0) {
			return hour + ":" + m + ":" + s;
		} else if (minutes > 0) {
			return m + ":" + s;
		} else {
			return s + "'";
		}
	}
}
