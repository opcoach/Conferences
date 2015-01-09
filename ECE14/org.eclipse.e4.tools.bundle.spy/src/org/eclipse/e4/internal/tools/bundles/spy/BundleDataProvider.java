/*******************************************************************************
 * Copyright (c) 2015 OPCoach.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olivier Prouvost <olivier.prouvost@opcoach.com> - initial API and implementation (bug #451116)
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

/**
 * The column Label and content Provider used to display information in context
 * data TreeViewer. Two instances for label provider are created : one for key,
 * one for values
 * 
 * @see ContextDataPart
 */
public class BundleDataProvider extends ColumnLabelProvider {

	public static final int COL_NAME = 0;
	public static final int COL_VERSION = 1;
	public static final int COL_STATE = 2;

	private static final Color COLOR_IF_FOUND = Display.getCurrent()
			.getSystemColor(SWT.COLOR_BLUE);

	@Inject
	private ImageRegistry imgReg;

	// Only one bundle filter, injected for all columns.
	@Inject
	private BundleDataFilter bundleFilter;

	// The column number this provider manages.
	private int column;

	@Inject
	public BundleDataProvider() {
		super();
	}

	@Override
	public String getText(Object element) {
		// Received element is a bundle...Text depends on column.
		Bundle b = (Bundle) element;
		String result = getText(b, column);
		return (result == null) ? super.getText(element) : result;

	}

	public static String getText(Bundle b, int col) {
		switch (col) {
			case COL_NAME :
				return b.getSymbolicName();
			case COL_VERSION :
				return b.getVersion().toString();
			case COL_STATE :
				return ""; // No text for state (see tooltip)

		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		// Return magenta color if the value could not be yet computed (for
		// context functions)
		String s = getText(element);

		// Return blue color if the string matches the search
		return ((bundleFilter != null) && (bundleFilter.matchText(s)))
				? COLOR_IF_FOUND
				: null;
	}

	@Override
	public Image getImage(Object element) {
		Bundle b = (Bundle) element;
		if (column == COL_STATE) {

			switch (b.getState()) {
				case Bundle.ACTIVE :
					return imgReg.get(BundleSpyPart.ICON_STATE_ACTIVE);
				case Bundle.INSTALLED :
					return imgReg.get(BundleSpyPart.ICON_STATE_INSTALLED);
				case Bundle.RESOLVED :
					return imgReg.get(BundleSpyPart.ICON_STATE_RESOLVED);
				case Bundle.STARTING :
					return imgReg.get(BundleSpyPart.ICON_STATE_STARTING);
				case Bundle.STOPPING :
					return imgReg.get(BundleSpyPart.ICON_STATE_STOPPING);
				case Bundle.UNINSTALLED :
					return imgReg.get(BundleSpyPart.ICON_STATE_UNINSTALLED);

			}
		}
		return null;

	}

	@Override
	public String getToolTipText(Object element) {
		Bundle b = (Bundle) element;

		switch (b.getState()) {
			case Bundle.ACTIVE :
				return "This bundle is Active";
			case Bundle.INSTALLED :
				return "This bundle is Installed";
			case Bundle.RESOLVED :
				return "This bundle is Resolved";
			case Bundle.STARTING :
				return "This bundle is Starting";
			case Bundle.STOPPING :
				return "This bundle is Stopping";
			case Bundle.UNINSTALLED :
				return "This bundle is Uninstalled";

		}

		return "This bundle is in state : " + b.getState();

	}

	@Override
	public Image getToolTipImage(Object object) {
		return getImage(object);
	}

	@Override
	public int getToolTipStyle(Object object) {
		return SWT.SHADOW_OUT;
	}


	public void setColumn(int col) {
		column = col;

	}

}
