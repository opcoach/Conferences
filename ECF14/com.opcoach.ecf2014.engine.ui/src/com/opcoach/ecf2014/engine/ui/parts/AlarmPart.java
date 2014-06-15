package com.opcoach.ecf2014.engine.ui.parts;

import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import com.opcoach.ecf2014.engine.core.Alarm;
import com.opcoach.ecf2014.engine.core.EngineWatcher;

public class AlarmPart
{
	@Optional
	@Inject
	private EngineWatcher engineWatcher;

	Vector<Alarm> alarms = new Vector<Alarm>();
	private TableViewer viewer;

	@Inject
	public AlarmPart()
	{

	}

	@PostConstruct
	public void postConstruct(Composite parent)
	{
		viewer = new TableViewer(parent);
		final Table cTable = viewer.getTable();
		cTable.setHeaderVisible(true);
		cTable.setLinesVisible(true);
		GridData gd_cTable = new GridData(SWT.FILL, SWT.FILL);
		gd_cTable.verticalAlignment = SWT.TOP;
		cTable.setLayoutData(gd_cTable);

		// Create the first column for date
		TableViewerColumn dateCol = new TableViewerColumn(viewer, SWT.CENTER);
		dateCol.getColumn().setWidth(80);
		dateCol.getColumn().setText("Date");
		dateCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					return sdf.format(((Alarm) element).getWhen());
				}
			});

		// Create the second column for hour
		TableViewerColumn hourCol = new TableViewerColumn(viewer, SWT.NONE);
		hourCol.getColumn().setWidth(80);
		hourCol.getColumn().setText("Hour");
		hourCol.getColumn().setAlignment(SWT.CENTER);
		hourCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					return sdf.format(((Alarm) element).getWhen());
				}
			});
		
		// Create the third column for details
		TableViewerColumn whatCol = new TableViewerColumn(viewer, SWT.NONE);
		whatCol.getColumn().setWidth(200);
		whatCol.getColumn().setText("What happened ?");
		whatCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					return ((Alarm)element).getWhat();
				}
			});

		// Set input data and content provider (default ArrayContentProvider)
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(alarms);
		
		// Add a button to clear the viewer
		Composite buttonContainer = new Composite(parent, SWT.BORDER);
		buttonContainer.setLayout(new GridLayout(1, true));
		Button clearButton = new Button(buttonContainer, SWT.PUSH);
		clearButton.setText("Clear alarms");
		clearButton.addSelectionListener(new SelectionListener()
			{
				@Override public void widgetSelected(SelectionEvent e)
				{ 
					alarms.clear();
					viewer.refresh();					
				}
				
				@Override public void widgetDefaultSelected(SelectionEvent e) { }
			});

	}

	@Inject @Optional
	public void listenToAlarms(@UIEventTopic(EngineWatcher.ALARM_TOPIC) Alarm a)
	{
		alarms.insertElementAt(a, 0);
		if (viewer != null)
		{
			viewer.refresh();
			viewer.setSelection(new StructuredSelection(a));
		}
	}

	@Focus
	public void onFocus()
	{
		viewer.getControl().setFocus();
	}

}