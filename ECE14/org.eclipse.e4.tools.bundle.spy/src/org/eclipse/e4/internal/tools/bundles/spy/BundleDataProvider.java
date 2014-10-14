/*******************************************************************************
 * Copyright (c) 2013 OPCoach.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     OPCoach - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.internal.tools.bundles.spy;

import javax.inject.Inject;

import org.eclipse.e4.tools.bundles.spy.BundleSpyPart;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * The column Label and content Provider used to display information in context
 * data TreeViewer. Two instances for label provider are created : one for key,
 * one for values
 * 
 * @see ContextDataPart
 */
public class BundleDataProvider extends ColumnLabelProvider
{

	public static final int COL_NAME = 0;
	public static final int COL_VERSION = 1;
	public static final int COL_STATE = 2;

	private static final String NO_VALUE_COULD_BE_COMPUTED = "No value could be yet computed";
	private static final Color COLOR_IF_FOUND = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	private static final Color COLOR_IF_NOT_COMPUTED = Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA);
	private static final Object[] EMPTY_RESULT = new Object[0];
	static final String LOCAL_VALUE_NODE = "Local values managed  by this context";
	static final String INHERITED_INJECTED_VALUE_NODE = "Inherited values injected or updated using this context";

	private static final String NO_VALUES_FOUND = "No values found";
	private static final String UPDATED_IN_CLASS = "Updated in class :";
	private static final String INJECTED_IN_FIELD = "Injected in field :";
	private static final String INJECTED_IN_METHOD = "Injected in method :";

	@Inject
	private ImageRegistry imgReg;

	// Only one bundle filter, injected for all columns.
	@Inject
	private BundleDataFilter bundleFilter;

	// The column number this provider manages.
	private int column;

	@Inject
	public BundleDataProvider()
	{
		super();
		initializeImageRegistry();
	}

	@Override
	public String getText(Object element)
	{
		// Received element is a bundle...Text depends on column.
		Bundle b = (Bundle) element;
		String result = getText(b, column);
		return (result == null) ? super.getText(element) : result;

	}

	public static String getText(Bundle b, int col)
	{
		switch (col)
		{
		case COL_NAME:
			return b.getSymbolicName();
		case COL_VERSION:
			return b.getVersion().toString();
		case COL_STATE: return ""; // No text for state (see tooltip)
		
		}
		return null;
	}

	@Override
	public Color getForeground(Object element)
	{
		// Return magenta color if the value could not be yet computed (for
		// context functions)
		String s = getText(element);

		// Return blue color if the string matches the search
		return ((bundleFilter != null) && (bundleFilter.matchText(s))) ? COLOR_IF_FOUND : null;
	}

	@Override
	public Image getImage(Object element)
	{
		Bundle b = (Bundle) element;
		if (column == COL_STATE)
		{
			if (b.getState() == Bundle.ACTIVE)
				return imgReg.get(BundleSpyPart.ICON_STATE_ACTIVE);
			else if (b.getState() == Bundle.RESOLVED)
				return imgReg.get(BundleSpyPart.ICON_STATE_RESOLVED);

		}
		return null;

	}

	@Override
	public String getToolTipText(Object element)
	{
		Bundle b = (Bundle) element;
		
		if (b.getState() == Bundle.ACTIVE)
			return "This bundle is Active";
		else if (b.getState() == Bundle.RESOLVED)
			return "This bundle is Resolved";
		
		return "Tooltip text to be defined";

	}

	@Override
	public Image getToolTipImage(Object object)
	{
		return getImage(object);
	}

	@Override
	public int getToolTipStyle(Object object)
	{
		return SWT.SHADOW_OUT;
	}

	private void initializeImageRegistry()
	{
		if (imgReg != null)
			return;

		Bundle b = FrameworkUtil.getBundle(this.getClass());
		imgReg = new ImageRegistry();

	}

	public void setColumn(int col)
	{
		column = col;

	}

}
