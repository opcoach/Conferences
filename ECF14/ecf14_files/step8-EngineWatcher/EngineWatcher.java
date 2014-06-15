package com.opcoach.ecf2014.engine.core;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;

public class EngineWatcher
{
	// Define the sent topics
	public static final String ALARM_TOPIC = "Alarm/*";
	public static final String ALARM_RPM_TOO_HIGH = "Alarm/RpmTooHigh";
	public static final String ALARM_SPEED_TOO_HIGH = "Alarm/SpeedTooHigh";

	// Get the event broker by injection
	@Inject
	IEventBroker ebroker;

	@Optional
	@Inject
	public void checkRpmValue(final @Named(EngineSimulator.ECF2014_RPM_VALUE) int value)
	{
		if (value > 5000)
		{
			// Send an alarm
			Alarm a = new Alarm("rpm is too high (" + value + ")", value);
			ebroker.send(ALARM_RPM_TOO_HIGH, a);
		}
	}

	@Optional
	@Inject
	public void checkSpeedValue(final @Named(EngineSimulator.ECF2014_SPEED_VALUE) int value)
	{
		System.out.println("Check speed value for alarm : " + value);
		if (value > 160)
		{
			// Send an alarm
			Alarm a = new Alarm("speed is too high (" + value + ")", value);
			ebroker.send(ALARM_SPEED_TOO_HIGH, a);
		}
	}

}