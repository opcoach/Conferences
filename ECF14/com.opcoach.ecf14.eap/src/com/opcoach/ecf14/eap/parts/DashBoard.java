package com.opcoach.ecf14.eap.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.nebula.visualization.widgets.figures.GaugeFigure;
import org.eclipse.nebula.visualization.widgets.figures.TankFigure;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class DashBoard {
	public static final String ECF2014_SPEED_VALUE = "ecf2014.speedValue";
	public static final String ECF2014_RPM_VALUE = "ecf2014.rpmValue";
	private static final String IMG_ALU = "images/Fond_Alu.jpg";
	private GaugeFigure speedCounter;
	private Label alarmLabel;
	private GaugeFigure rpmCounter;
	private XYGraphMediaFactory gmfactory;
	
	private ImageRegistry imgRegistry;
	private Canvas canvas;


	@Inject
	public DashBoard() {

	}

	@Optional
	@Inject
	public void listenToRpmValue(final @Named(ECF2014_RPM_VALUE) int value,
			UISynchronize sync) {
		if (rpmCounter != null)
			sync.asyncExec(new Runnable() {

				@Override
				public void run() {
					rpmCounter.setValue(value);
				}
			});
	}

	@Optional
	@Inject
	public void listenToSpeedValue(final @Named(ECF2014_SPEED_VALUE) int value,
			UISynchronize sync) {
		if (speedCounter != null)
			sync.asyncExec(new Runnable() {

				@Override
				public void run() {
					speedCounter.setValue(value);
				}
			});
	}

	@PostConstruct
	public void postConstruct(Composite parent, IEclipseContext ctx) {

		// parent.setLayout(new org.eclipse.swt.layout.GridLayout(1,true));

		Composite canvasParent = new Composite(parent, SWT.BORDER);
		canvasParent.setLayout(new FillLayout());
		canvasParent
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		canvas = new Canvas(canvasParent, SWT.NONE);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// canvas.setSize(700, 600);
		canvas.setBackgroundImage(getImage(IMG_ALU));
		final LightweightSystem lws = new LightweightSystem(canvas);

		IFigure root = new Figure();
		root.setLayoutManager(new GridLayout(2, false));

		rpmCounter = createRpmCounter();
		root.add(rpmCounter);
		//root.add(createTankFigure());

		speedCounter = createSpeedCounter();
		root.add(speedCounter);
		
		Label rpmTitle = new Label();
		rpmTitle.setText("                 Rpm");
		rpmTitle.setLabelAlignment(PositionConstants.CENTER);
		root.add(rpmTitle);
		
		Label speedTitle = new Label();
		speedTitle.setText("                Speed");
		speedTitle.setLabelAlignment(PositionConstants.CENTER);
		root.add(speedTitle);
		
		
		lws.setContents(root);

	}

	
	private Image getImage(String imgKey) {
		if (imgRegistry == null) {
			imgRegistry = new ImageRegistry();
			Bundle b = FrameworkUtil.getBundle(getClass());
			imgRegistry.put(IMG_ALU,
					ImageDescriptor.createFromURL(b.getEntry(IMG_ALU)));

		}
		return imgRegistry.get(imgKey);
	}

	

	private GaugeFigure createSpeedCounter() {
		final GaugeFigure gaugeFigure = new GaugeFigure();

		// Init gauge
		gaugeFigure.setBackgroundColor(XYGraphMediaFactory.getInstance()
				.getColor(255,255,255));
		gaugeFigure.setForegroundColor(XYGraphMediaFactory.getInstance()
				.getColor(0,0,0));

		gaugeFigure.setRange(0, 220);
		gaugeFigure.setLoLevel(10);
		gaugeFigure.setLoloLevel(0);
		gaugeFigure.setHiLevel(130);
		gaugeFigure.setHihiLevel(180);
		gaugeFigure.setMajorTickMarkStepHint(50);
		return gaugeFigure;
	}

	private GaugeFigure createRpmCounter() {
		final GaugeFigure rpmFigure = new GaugeFigure();

		gmfactory = XYGraphMediaFactory.getInstance();
		rpmFigure.setBackgroundColor(gmfactory.getColor(0, 0, 0));
		rpmFigure.setForegroundColor(gmfactory.getColor(255, 255, 255));

		rpmFigure.setRange(0, 7000);
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

	private TankFigure createTankFigure() {
		// Create widget
		final TankFigure tank = new TankFigure();

		// Init widget
		tank.setBackgroundColor(XYGraphMediaFactory.getInstance().getColor(255,
				255, 255));

		tank.setBorder(new SchemeBorder(SchemeBorder.SCHEMES.ETCHED));

		tank.setRange(-100, 100);
		tank.setLoLevel(-50);
		tank.setLoloLevel(-80);
		tank.setHiLevel(60);
		tank.setHihiLevel(80);
		tank.setMajorTickMarkStepHint(50);

		/*
		 * // Update the widget in another thread. ScheduledExecutorService
		 * scheduler = Executors .newScheduledThreadPool(1); ScheduledFuture<?>
		 * future = scheduler.scheduleAtFixedRate( new Runnable() {
		 * 
		 * public void run() { Display.getDefault().asyncExec(new Runnable() {
		 * private int counter;
		 * 
		 * public void run() { tank.setValue(Math.sin(counter++ / 10.0) * 100);
		 * } }); } }, 100, 100, TimeUnit.MILLISECONDS);
		 */

		return tank;
	}

	@Focus
	public void onFocus() {
		canvas.setFocus();
	}

}