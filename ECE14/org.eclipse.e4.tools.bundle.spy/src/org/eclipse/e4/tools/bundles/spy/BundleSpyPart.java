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
package org.eclipse.e4.tools.bundles.spy;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.internal.tools.bundles.spy.BundleDataFilter;
import org.eclipse.e4.internal.tools.bundles.spy.BundleDataProvider;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

/**
 * This class is the main part of the bundle spy. It displays a tableviewer with
 * all bundles
 */
public class BundleSpyPart {

	private static final String ICON_REFRESH = "icons/refresh.gif";
	public static final String ICON_STATE_ACTIVE = "icons/state_active.gif";
	public static final String ICON_STATE_STARTING = "icons/state_starting.gif";
	public static final String ICON_STATE_STOPPING = "icons/state_stopping.gif";
	public static final String ICON_STATE_RESOLVED = "icons/state_resolved.gif";
	public static final String ICON_STATE_INSTALLED = "icons/state_installed.gif";
	public static final String ICON_STATE_UNINSTALLED = "icons/state_uninstalled.gif";
	public static final String ICON_START = "icons/start.gif";
	public static final String ICON_STOP = "icons/stop.gif";

	private TableViewer bundlesTableViewer;

	private Text filterText;

	private Button showOnlyFilteredElements;

	private BundleDataFilter bundleFilter;

	@Inject
	private IEclipseContext ctx;

	/** Store the values to set it when it is reopened */
	private static String lastFilterText = null;
	private static boolean lastShowFiltered = false;

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		ImageRegistry imgReg = initializeImageRegistry();

		// Set a filter in context (-> null at the begining).
		bundleFilter = new BundleDataFilter();
		ctx.set(BundleDataFilter.class, bundleFilter);

		parent.setLayout(new GridLayout(1, false));

		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(5, false));

		Button refreshButton = new Button(comp, SWT.FLAT);
		refreshButton.setImage(imgReg.get(ICON_REFRESH));
		refreshButton.setToolTipText("Refresh the contexts");
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bundlesTableViewer.refresh(true);
			}
		});

		filterText = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH);
		GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT)
				.applyTo(filterText);
		filterText.setMessage("Search data");
		filterText
				.setToolTipText("Highlight the bundles where the contained objects contains this string.\n"
						+ "Case is ignored.");
		if (lastFilterText != null)
			filterText.setText(lastFilterText);
		bundleFilter.setPattern(lastFilterText);
		filterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String textToSearch = filterText.getText();
				lastFilterText = textToSearch;
				boolean enableButton = textToSearch.length() > 0;
				// Enable/disable button for filtering
				showOnlyFilteredElements.setEnabled(enableButton);

				// Then update filters and viewers
				bundleFilter.setPattern(textToSearch);
				setFilter();
				bundlesTableViewer.refresh(true);
			}

		});

		showOnlyFilteredElements = new Button(comp, SWT.CHECK);
		showOnlyFilteredElements.setText("Show Only Filtered");
		showOnlyFilteredElements
				.setToolTipText("Show only the filtered items in the bundle table ");
		showOnlyFilteredElements.setEnabled((lastFilterText != null)
				&& (lastFilterText.length() > 0));
		showOnlyFilteredElements.setSelection(lastShowFiltered);
		showOnlyFilteredElements.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lastShowFiltered = showOnlyFilteredElements.getSelection();
				setFilter();
			}
		});

		startButton = new Button(comp, SWT.FLAT);
		startButton.setImage(imgReg.get(ICON_START));
		startButton
				.setToolTipText("Start the selected bundles not yet started");
		startButton.setEnabled(false);
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) bundlesTableViewer
						.getSelection();
				Iterator<?> iter = sel.iterator();
				while (iter.hasNext()) {
					Bundle b = (Bundle) iter.next();
					try {
						b.start();
					} catch (BundleException e1) {
						e1.printStackTrace();
					}
				}
				bundlesTableViewer.refresh();
				updateButtonStatuses(sel);
			}
		});

		stopButton = new Button(comp, SWT.FLAT);
		stopButton.setImage(imgReg.get(ICON_STOP));
		stopButton.setToolTipText("Stop the selected bundles not yet stopped");
		stopButton.setEnabled(false);
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openConfirm(
						((Control) e.getSource()).getShell(),
						"Confirm Bundle Stop",
						"Stopping a bundle may cause problems in your current application.\nUse this button only for your bundles under testing\n\nDo you confirm you want to stop the selected started bundle(s) ? ")) {
					IStructuredSelection sel = (IStructuredSelection) bundlesTableViewer
							.getSelection();
					Iterator<?> iter = sel.iterator();
					while (iter.hasNext()) {
						Bundle b = (Bundle) iter.next();
						try {
							b.stop();
						} catch (BundleException e1) {
							e1.printStackTrace();
						}
					}
					bundlesTableViewer.refresh();
					updateButtonStatuses(sel);
				}
			}
		});

		// Create the customer table with 2 columns: firstname and name
		bundlesTableViewer = new TableViewer(parent);
		final Table cTable = bundlesTableViewer.getTable();
		cTable.setHeaderVisible(true);
		cTable.setLinesVisible(true);
		GridData gd_cTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gd_cTable.verticalAlignment = SWT.TOP;
		cTable.setLayoutData(gd_cTable);

		// Create the first column for bundle name
		addColumn(bundlesTableViewer, 35, "State", BundleDataProvider.COL_STATE);
		addColumn(bundlesTableViewer, 200, "Bundle Name",
				BundleDataProvider.COL_NAME);
		addColumn(bundlesTableViewer, 200, "Version",
				BundleDataProvider.COL_VERSION);

		// Set input data and content provider (default ArrayContentProvider)
		bundlesTableViewer.setContentProvider(ArrayContentProvider
				.getInstance());

		// Get the list of bundles in platform using bundle context...
		BundleContext bc = BundleSpyActivator.getContext();
		bundlesTableViewer.setInput(bc.getBundles());

		bundlesTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						updateButtonStatuses((IStructuredSelection) event
								.getSelection());
					}
				});

		ColumnViewerToolTipSupport.enableFor(bundlesTableViewer);

	}

	/** Update the stop and start buttons depending on current selection */
	protected void updateButtonStatuses(IStructuredSelection selection) {
		// startButton is enabled if at least one bundle is not active
		// stopButton is enabled if at least one bundle is active
		boolean oneBundleIsActive = false;
		boolean oneBundleIsNotActive = false;

		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			Bundle b = (Bundle) iter.next();
			oneBundleIsActive = oneBundleIsActive
					|| (b.getState() == Bundle.ACTIVE);
			oneBundleIsNotActive = oneBundleIsNotActive
					|| (b.getState() != Bundle.ACTIVE);
		}
		startButton.setEnabled(oneBundleIsNotActive);
		stopButton.setEnabled(oneBundleIsActive);

	}

	private void addColumn(TableViewer parentTable, int width, String title,
			int colnum) {
		TableViewerColumn col = new TableViewerColumn(bundlesTableViewer,
				SWT.NONE);
		col.getColumn().setWidth(width);
		col.getColumn().setText(title);

		BundleDataProvider bdp = ContextInjectionFactory.make(
				BundleDataProvider.class, ctx);
		bdp.setColumn(colnum);
		col.setLabelProvider(bdp);

	}

	private static final ViewerFilter[] NO_FILTER = new ViewerFilter[0];
	private Button stopButton;
	private Button startButton;

	/** Set the filter on table */
	public void setFilter() {

		if (showOnlyFilteredElements.isEnabled()
				&& showOnlyFilteredElements.getSelection()) {
			bundlesTableViewer.setFilters(new ViewerFilter[]{bundleFilter});
		} else {
			bundlesTableViewer.setFilters(NO_FILTER);
		}
	}

	@Focus
	public void setFocus() {
		bundlesTableViewer.getControl().setFocus();
	}

	private ImageRegistry initializeImageRegistry() {
		Bundle b = FrameworkUtil.getBundle(this.getClass());
		ImageRegistry imgReg = new ImageRegistry();
		imgReg.put(ICON_REFRESH,
				ImageDescriptor.createFromURL(b.getEntry(ICON_REFRESH)));
		imgReg.put(ICON_STATE_ACTIVE,
				ImageDescriptor.createFromURL(b.getEntry(ICON_STATE_ACTIVE)));
		imgReg.put(ICON_STATE_RESOLVED,
				ImageDescriptor.createFromURL(b.getEntry(ICON_STATE_RESOLVED)));
		imgReg.put(ICON_STATE_STARTING,
				ImageDescriptor.createFromURL(b.getEntry(ICON_STATE_STARTING)));
		imgReg.put(ICON_STATE_STOPPING,
				ImageDescriptor.createFromURL(b.getEntry(ICON_STATE_STOPPING)));
		imgReg.put(ICON_STATE_INSTALLED,
				ImageDescriptor.createFromURL(b.getEntry(ICON_STATE_INSTALLED)));
		imgReg.put(ICON_STATE_UNINSTALLED, ImageDescriptor.createFromURL(b
				.getEntry(ICON_STATE_UNINSTALLED)));
		imgReg.put(ICON_START,
				ImageDescriptor.createFromURL(b.getEntry(ICON_START)));
		imgReg.put(ICON_STOP,
				ImageDescriptor.createFromURL(b.getEntry(ICON_STOP)));

		ctx.set(ImageRegistry.class, imgReg);

		return imgReg;
	}

}
