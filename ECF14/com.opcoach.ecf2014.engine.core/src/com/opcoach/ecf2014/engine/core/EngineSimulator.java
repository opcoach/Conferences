package com.opcoach.ecf2014.engine.core;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;

/** This class simulate engine dynamic values */
public class EngineSimulator {

	// Define the constants to get/set values from context
	public static final String ECF2014_SPEED_VALUE = "ecf2014.speedValue";
	public static final String ECF2014_RPM_VALUE = "ecf2014.rpmValue";
	public static final String ECF2014_TANK_VALUE = "ecf2014.tankValue";

	@Inject
	IEclipseContext ctx; // The context where values will be injected

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
			ctx.set(ECF2014_SPEED_VALUE, 0);
			ctx.set(ECF2014_RPM_VALUE, 0);

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

			System.out.println("New value for speed : " + speed + " and rpm : "+ rpm);
			ctx.set(ECF2014_SPEED_VALUE, speed);
			ctx.set(ECF2014_RPM_VALUE, rpm);
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
