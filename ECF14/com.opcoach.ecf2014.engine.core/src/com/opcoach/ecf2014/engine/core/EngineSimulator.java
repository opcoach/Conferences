package com.opcoach.ecf2014.engine.core;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;

/** This class simulate engine dynamic values */
public class EngineSimulator {

	// Define the constants to get/set values from context
	public static final String ENGINE_SPEED_VALUE = "engine.speedValue";
	public static final String ENGINE_RPM_VALUE = "engine.rpmValue";
	public static final String ENGINE_TANK_VALUE = "engine.tankValue";

	@Inject
	IEclipseContext ctx; // The context where values will be injected

	@Inject
	private IEngineLogger logger;

	// Physical values
	int speed, rpm, tankLevel;
	int acceleration = 0;

	Timer timer = null;

	@Inject
	EngineSimulator() {
		this(65);
	}

	EngineSimulator(int tankInit) {
		tankLevel = tankInit;

	}

	public void start() {
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new EngineTimerTask(), 1000, 1500);
		}
	}
	
	public boolean isStarted()
	{
		return timer != null;
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			speed = 0;
			rpm = 0;
			ctx.set(ENGINE_SPEED_VALUE, 0);
			ctx.set(ENGINE_RPM_VALUE, 0);

		}
		timer = null;
	}


	private class EngineTimerTask extends TimerTask {
	
		@Override
		public void run() {
			speed = speed + acceleration;
			rpm = speed * 75;

			if (speed < 0)
				speed = 0;
			if (rpm < 0)
				rpm = 0;

			if (logger != null)
			logger.logMessage("New value for speed : " + speed + " and rpm : "+ rpm);
			else
				System.out.println("Logger is null");
			ctx.set(ENGINE_SPEED_VALUE, speed);
			ctx.set(ENGINE_RPM_VALUE, rpm);
		}
	}

	/**
	 * accelerate or brake
	 * 
	 * @param a
	 *            acceleration in m/s2
	 */
	public void accelerate(int a) {
		acceleration = a;
	}

}
