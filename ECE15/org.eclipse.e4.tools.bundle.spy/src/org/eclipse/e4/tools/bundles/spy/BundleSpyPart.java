/*******************************************************************************
 * Copyright (c) 2013 OPCoach.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olivier Prouvost <olivier.prouvost@opcoach.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.bundles.spy;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * This class is the main part of the bundle spy. It displays a tableviewer with all bundles
 */
public class BundleSpyPart
{

	private static final String ICON_COLLAPSEALL = "icons/collapseall.gif";
	private static final String ICON_EXPANDALL = "icons/expandall.gif";
	private static final String ICON_REFRESH = "icons/refresh.gif";

	// The ID for this part descriptor
	static final String CONTEXT_SPY_VIEW_DESC = "org.eclipse.e4.tools.context.spy.view";

	private TableViewer bundlesTableViewer;


	private ImageRegistry imgReg;

/*	@Inject
	private void initializeImageRegistry()
	{
		Bundle b = FrameworkUtil.getBundle(this.getClass());
		imgReg = new ImageRegistry();
		imgReg.put(ICON_COLLAPSEALL, ImageDescriptor.createFromURL(b.getEntry(ICON_COLLAPSEALL)));
		imgReg.put(ICON_EXPANDALL, ImageDescriptor.createFromURL(b.getEntry(ICON_EXPANDALL)));
		imgReg.put(ICON_REFRESH, ImageDescriptor.createFromURL(b.getEntry(ICON_REFRESH)));
	} */

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent, MApplication a, IEclipseContext ctx)
	{
		// Create the customer table with 2 columns: firstname and name
		bundlesTableViewer = new TableViewer(parent);
		final Table cTable = bundlesTableViewer.getTable();
		cTable.setHeaderVisible(true);
		cTable.setLinesVisible(true);
		GridData gd_cTable = new GridData(SWT.FILL);
		gd_cTable.verticalAlignment = SWT.TOP;
		cTable.setLayoutData(gd_cTable);
		
		// Create the first column for firstname
		TableViewerColumn bundleNameCol = new TableViewerColumn(bundlesTableViewer, SWT.NONE);
		bundleNameCol.getColumn().setWidth(200);
		bundleNameCol.getColumn().setText("Bundle Name");
		bundleNameCol.setLabelProvider(new ColumnLabelProvider() {@Override
		public String getText(Object element)
		{
			return ((Bundle)element).getSymbolicName();
		}});
		
		// Create the second column for name
		TableViewerColumn nameCol = new TableViewerColumn(bundlesTableViewer, SWT.NONE);
		nameCol.getColumn().setWidth(200);
		nameCol.getColumn().setText("Version");
		nameCol.setLabelProvider(new ColumnLabelProvider() {@Override
		public String getText(Object element)
		{
			return ((Bundle)element).getVersion().toString();
		}});
		
		// Set input data and content provider (default ArrayContentProvider)
		bundlesTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		// Get the list of bundles in platform using bundle context...
		BundleContext bc = BundleSpyActivator.getContext();
		bundlesTableViewer.setInput(bc.getBundles());
		

	}

	

	@PreDestroy
	public void dispose()
	{
	}

	@Focus
	public void setFocus()
	{
		bundlesTableViewer.getControl().setFocus();
	}

}
