/*******************************************************************************
 * Copyright (c) 2014 OPCoach.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     OPCoach - initial API and implementation for bug #437478
 *******************************************************************************/
package org.eclipse.e4.internal.tools.bundles.spy;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.osgi.framework.Bundle;

public class BundleDataFilter extends ViewerFilter
{
	private String pattern;

	// Implements the filter for the data table content
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		Bundle b = (Bundle) element;

		// Must only select objects matching the pattern -> get all text for one
		// element and
		// check if values are in pattern.
		TableViewer tv = (TableViewer) viewer;
		String bstring = getBundleStrings(b, tv.getTable().getColumnCount());

		return matchText(bstring);

	}

	public String getBundleStrings(Bundle b, int nbColumn)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nbColumn; i++)
			sb.append(BundleDataProvider.getText(b, i)).append("  ");

		return sb.toString();
	}

	/** Set the pattern and use it as lowercase */
	public void setPattern(String newPattern)
	{
		if ((newPattern == null) || (newPattern.length() == 0))
			pattern = null;
		else
			pattern = newPattern.toLowerCase();
	}

	public boolean matchText(String text)
	{
		return ((text == null) || (pattern == null)) ? false : text.toLowerCase().contains(pattern);
	}

}
