package com.opcoach.ecf2014.engine.core;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Alarm
	{
		private Date when; 	private String what; private int value;

		public Alarm(String what, int value)
		{
			this.what = what;
			this.value = value;
			when = new Date();
		}

		public Date getWhen() { return when; }
		public String getWhat() { return what; }
		public int getValue() { return value; 	}

		@Override public String toString()
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			return "Alarm : " + sdf.format(when) + " " + what + " : " + value;
		}
	}