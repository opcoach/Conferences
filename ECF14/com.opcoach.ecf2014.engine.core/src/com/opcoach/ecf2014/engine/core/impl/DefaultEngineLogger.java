package com.opcoach.ecf2014.engine.core.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import com.opcoach.ecf2014.engine.core.IEngineLogger;

public class DefaultEngineLogger implements IEngineLogger
{
	
	private SimpleDateFormat sdf;
	
	@Inject
	public DefaultEngineLogger() {
		sdf = new SimpleDateFormat("HH:mm:ss");
	}

	@Override
	public void logMessage(String message)
	{

		System.out.println("-> Engine Logger : " + sdf.format(new Date()) + "   " + message);
	}

}
