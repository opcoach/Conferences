package com.opcoach.ecf2014.engine.core;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;

import com.opcoach.ecf2014.engine.core.impl.DefaultEngineLogger;

public class EngineContextFunction extends ContextFunction
{

	@Override
	public Object compute(IEclipseContext context, String contextKey)
	{
		System.out.println("---> Enter in EngineContextFunction");
		IEngineLogger result = ContextInjectionFactory.make(DefaultEngineLogger.class, context);

		// Put this Engine in the application context
		MApplication appli = context.get(MApplication.class);
		appli.getContext().set(IEngineLogger.class, result);

		return result;
	}
}
