package com.opcoach.ecf14.eap.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.nebula.visualization.widgets.figures.GaugeFigure;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class DashBoard {

	private GaugeFigure speedCounter;
    private GaugeFigure rpmCounter;
	private XYGraphMediaFactory gmfactory;
	
	private Canvas canvas;

	@Inject
	public DashBoard() {

	}

	@PostConstruct
	public void postConstruct(Composite parent, IEclipseContext ctx) {

		// Create the canvas and the related LightweightSystem for Draw2D
		canvas = new Canvas(parent, SWT.NONE);
		final LightweightSystem lws = new LightweightSystem(canvas);

		// Create a main figure to contain the 2 counters and the 2 labels
		IFigure root = new Figure();
		root.setLayoutManager(new GridLayout(2, false));

		rpmCounter = createRpmCounter();
		root.add(rpmCounter);

		speedCounter = createSpeedCounter();
		root.add(speedCounter);
		
		// Add two titles under the counters.
		Label rpmTitle = new Label();
		rpmTitle.setText("                 Rpm");
		rpmTitle.setLabelAlignment(PositionConstants.CENTER);
		root.add(rpmTitle);
		
		Label speedTitle = new Label();
		speedTitle.setText("                Speed");
		speedTitle.setLabelAlignment(PositionConstants.CENTER);
		root.add(speedTitle);
		
		// Set the root figure.		
		lws.setContents(root);

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


	@Focus
	public void onFocus() {
		canvas.setFocus();
	}

}
