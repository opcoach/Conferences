package com.opcoach.ecf2014.engine.ui.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.nebula.visualization.widgets.datadefinition.IManualValueChangeListener;
import org.eclipse.nebula.visualization.widgets.figures.ScaledSliderFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.opcoach.ecf2014.engine.core.EngineSimulator;

public class EngineControlPart
{

	private static final String IMG_START = "icons/startButton.png";
	private static final String IMG_STOP = "icons/stopButton.png";
	// private static final String IMG_FUNCTION = "icons/functionButton.png";

	@Inject
	private EngineSimulator engineSimu;


	@Inject
	public EngineControlPart()
	{

	}

	@PostConstruct
	public void postConstruct(Composite parent, final ImageRegistry reg)
	{
		parent.setLayout(new GridLayout(1, true));

		Canvas canvas = new Canvas(parent, SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final LightweightSystem lws = new LightweightSystem(canvas);

		// Create Scaled Slider
		final ScaledSliderFigure slider = new ScaledSliderFigure();
		slider.setHorizontal(true);
		// Init Scaled Slider
		slider.setRange(-10, 10); // can brake or accelerate from -10 to 10 m/s2
		slider.setValue(0);
		slider.setLoLevel(-5);
		slider.setLoloLevel(-8);
		slider.setHiLevel(6);
		slider.setHihiLevel(8);
		slider.setThumbColor(ColorConstants.gray);
		slider.setEffect3D(true);
		slider.setShowMinorTicks(false);
		slider.addManualValueChangeListener(new IManualValueChangeListener()
			{
				public void manualValueChanged(double newValue)
				{
					if (engineSimu != null)
						engineSimu.accelerate((int) newValue);
				}
			});

		lws.setContents(slider);

		// Initialize needed images
		Bundle b = FrameworkUtil.getBundle(getClass());
		reg.put(IMG_START, ImageDescriptor.createFromURL(b.getEntry(IMG_START)));
		reg.put(IMG_STOP, ImageDescriptor.createFromURL(b.getEntry(IMG_STOP)));
		// reg.put(IMG_FUNCTION,
		// ImageDescriptor.createFromURL(b.getEntry(IMG_FUNCTION)));

		// Create only 1 button for start and stop.
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

		buttonComposite.setLayout(new GridLayout(1, true));
		final Label startButton = new Label(buttonComposite, SWT.NONE);
		startButton.setImage(reg.get(IMG_START));
		startButton.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					if (engineSimu != null)
					{
						if (engineSimu.isStarted())
						{
							engineSimu.stop();
							startButton.setImage(reg.get(IMG_START));
						} else
						{
							engineSimu.start();
							startButton.setImage(reg.get(IMG_STOP));
						}
					}
				}

			});

	}

}