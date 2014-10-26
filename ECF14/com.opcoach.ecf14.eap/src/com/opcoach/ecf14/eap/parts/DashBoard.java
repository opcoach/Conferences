package com.opcoach.ecf14.eap.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.nebula.visualization.widgets.figures.GaugeFigure;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.opcoach.ecf2014.engine.core.EngineSimulator;
import com.opcoach.ecf2014.engine.core.EngineWatcher;

public class DashBoard
{

	private GaugeFigure speedCounter;
	private GaugeFigure rpmCounter;
	private XYGraphMediaFactory gmfactory;
	
	private static final int COUNTER_SIZE = 200;
	private static final int COUNTER_MARGIN = 15;

	private Canvas canvas;

	@Inject
	public DashBoard(MApplication appli)
	{
		// We will use the application context to store and inject values.
		IEclipseContext appliContext = appli.getContext();
		
		// We also need an ImageRegistry for the application
		appliContext.set(ImageRegistry.class, new ImageRegistry());
		
		// Step 5 : create and start Engine.
		EngineSimulator simu = ContextInjectionFactory.make(EngineSimulator.class, appliContext);
		appliContext.set(EngineSimulator.class, simu);
		
		// Step 8 : create the engine alarm watcher and keep a reference on it ! 
		EngineWatcher watcher = ContextInjectionFactory.make(EngineWatcher.class, appliContext);
		appliContext.set(EngineWatcher.class, watcher);
	}

	@Inject @Optional
	public void listenToRpmValue(final @Named(EngineSimulator.ECF2014_RPM_VALUE) int value, UISynchronize sync)
	{
		if (rpmCounter != null)
			sync.asyncExec(new Runnable()
				{
					@Override public void run()
					{
						rpmCounter.setValue(value);
					}
				});
	}

	@Optional
	@Inject
	public void listenToSpeedValue(final @Named(EngineSimulator.ECF2014_SPEED_VALUE) int value, UISynchronize sync)
	{
		if (speedCounter != null)
			sync.asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						speedCounter.setValue(value);
					}
				});
	}

	@PostConstruct
	public void postConstruct(Composite parent, IEclipseContext ctx)
	{

		// Create the canvas and the related LightweightSystem for Draw2D
		canvas = new Canvas(parent, SWT.NONE);
		final LightweightSystem lws = new LightweightSystem(canvas);

		// Create a main figure to contain the 2 counters and the 2 labels
		IFigure root = new Figure();
		root.setLayoutManager(new XYLayout());
		root.setBackgroundColor(ColorConstants.lightBlue);

		rpmCounter = createRpmCounter();
		rpmCounter.setBounds(new Rectangle(COUNTER_MARGIN,COUNTER_MARGIN,COUNTER_SIZE, COUNTER_SIZE));
		root.add(rpmCounter);

		speedCounter = createSpeedCounter();
		speedCounter.setBounds(new Rectangle(COUNTER_MARGIN*2 + COUNTER_SIZE, COUNTER_MARGIN,COUNTER_SIZE, COUNTER_SIZE));
		root.add(speedCounter);

		// Add two titles under the counters.
		Label rpmTitle = new Label();
		rpmTitle.setText("Rpm");
		rpmTitle.setLabelAlignment(PositionConstants.CENTER);
		rpmTitle.setBounds(new Rectangle(COUNTER_MARGIN, COUNTER_SIZE + COUNTER_MARGIN,COUNTER_SIZE, 20));
		root.add(rpmTitle);

		Label speedTitle = new Label();
		speedTitle.setText("Speed");
		speedTitle.setLabelAlignment(PositionConstants.CENTER);
		speedTitle.setBounds(new Rectangle(COUNTER_SIZE + 2*COUNTER_MARGIN, COUNTER_SIZE + COUNTER_MARGIN,COUNTER_SIZE, 20));
		root.add(speedTitle);

		// Set the root figure.
		lws.setContents(root);

	}

	private GaugeFigure createSpeedCounter()
	{
		final GaugeFigure gaugeFigure = new GaugeFigure();

		// Init gauge
		gaugeFigure.setBackgroundColor(XYGraphMediaFactory.getInstance().getColor(255, 255, 255));
		gaugeFigure.setForegroundColor(XYGraphMediaFactory.getInstance().getColor(0, 0, 0));

		gaugeFigure.setRange(0, 220);
		gaugeFigure.setLoLevel(10);
		gaugeFigure.setLoloLevel(0);
		gaugeFigure.setHiLevel(130);
		gaugeFigure.setHihiLevel(180);
		gaugeFigure.setMajorTickMarkStepHint(50);
		return gaugeFigure;
	}

	private GaugeFigure createRpmCounter()
	{
		final GaugeFigure rpmFigure = new GaugeFigure();

		gmfactory = XYGraphMediaFactory.getInstance();
		rpmFigure.setBackgroundColor(gmfactory.getColor(0, 0, 0));
		rpmFigure.setForegroundColor(gmfactory.getColor(255, 255, 255));

		rpmFigure.setRange(0, 8000);
		rpmFigure.setLoLevel(1000);
		rpmFigure.setLoloLevel(500);
		rpmFigure.setHiLevel(4000);
		rpmFigure.setHiColor(gmfactory.getColor(128, 255, 255));
		rpmFigure.setHihiColor(gmfactory.getColor(128, 255, 255));
		rpmFigure.setHihiLevel(5000);
		rpmFigure.setMajorTickMarkStepHint(100);
		rpmFigure.setEffect3D(true);
		rpmFigure.setNeedleColor(gmfactory.getColor(50, 50, 255));

		return rpmFigure;
	}

	@Focus
	public void onFocus()
	{
		canvas.setFocus();
	}

}
