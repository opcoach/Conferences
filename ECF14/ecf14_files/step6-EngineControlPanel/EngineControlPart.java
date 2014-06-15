package com.opcoach.ecf2014.engine.ui.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.nebula.visualization.widgets.datadefinition.IManualValueChangeListener;
import org.eclipse.nebula.visualization.widgets.figures.ScaledSliderFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.opcoach.ecf2014.engine.core.EngineSimulator;

public class EngineControlPart
{

	@Inject
	@Optional
	EngineSimulator engineSimu;

	@PostConstruct
	public void postConstruct(Composite parent)
	{
		parent.setLayout(new GridLayout(1, true));

		Canvas canvas = new Canvas(parent, SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final LightweightSystem lws = new LightweightSystem(canvas);

		//Create Scaled Slider
		final ScaledSliderFigure slider = new ScaledSliderFigure();
		slider.setHorizontal(true);
		//Init Scaled Slider
		slider.setRange(-10, 10);   // can brake or accelerate from -10 to 10 m/s2
		slider.setValue(0);
		slider.setLoLevel(-5);
		slider.setLoloLevel(-8);
		slider.setHiLevel(6);
		slider.setHihiLevel(8);
		slider.setThumbColor(ColorConstants.gray);
		slider.setEffect3D(true);
		slider.setShowMinorTicks(false);
		slider.addManualValueChangeListener(new IManualValueChangeListener() {			
			public void manualValueChanged(double newValue) {
				if (engineSimu != null)
					engineSimu.accelerate((int) newValue);
			}
		});
				
		lws.setContents(slider);

		// Create the 2 swt buttons for start and stop.
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, true));
		final Button startButton = new Button(buttonComposite, SWT.NONE);
		startButton.setText("Start Engine");
		startButton.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));

		final Button stopButton = new Button(buttonComposite, SWT.NONE);
		stopButton.setText("Stop Engine");
		stopButton.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
		stopButton.setEnabled(false);

		startButton.addSelectionListener(new SelectionListener()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					if (engineSimu != null)
						engineSimu.start();
					stopButton.setEnabled(true);
					startButton.setEnabled(false);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		stopButton.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					if (engineSimu != null)
						engineSimu.stop();
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		
	}

}