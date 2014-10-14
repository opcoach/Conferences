package org.eclipse.e4.tools.bundles.spy;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator of this bundle to be sure to have an bundle context (see
 * BundleSpyPart)
 */
public class BundleSpyActivator implements BundleActivator
{

	private static BundleContext bContext;

	public static BundleContext getContext()
	{
		return bContext;
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		bContext = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		bContext = null;

	}

}
