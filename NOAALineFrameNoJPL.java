import gov.noaa.pmel.sgt.swing.*;
import gov.noaa.pmel.sgt.dm.*;
import gov.noaa.pmel.sgt.*;
import gov.noaa.pmel.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.Random; // for testing

/**
*   This class displays a simple histogram for test purposes
*      - package gov.noaa.noaaserver courtesy of our frineds at NOAA
*/
public class NOAALineFrameNoJPL extends PrintableDialog implements ActionListener {
	private Container contentPane;
	private long[] array;
	private JButton printButton, closeButton;
	private JPanel buttonPanel;
	private JScrollPane scrollPane;
	public Layer layer;
	private JPane jpane;

	private boolean do2Lines = true; // just to stay awake and get this code done

	public NOAALineFrameNoJPL(double[] xarray, double[] yarray,
			String xLable, String xUnit, String yLabel, String yUnit) {
		this (xarray,yarray,new double[0],xLable,xUnit,yLabel,yUnit);
	}

	public NOAALineFrameNoJPL(double[] xarray, double[] yarray, double[] yarray2,
			String xLabel, String xUnit, String yLabel, String yUnit) {

		setTitle("Sample Line Plot");
		if (yarray2.length<2) do2Lines = false;

		contentPane = getContentPane();
		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e){
					setVisible(false);
				}});

		SimpleLine sl = new SimpleLine(xarray, yarray, "Histogram Line");
		sl.setXMetaData(new SGTMetaData(xLabel, xUnit));
		sl.setYMetaData(new SGTMetaData(yLabel, yUnit));

		SimpleLine sl2 = null;
		if (do2Lines) {
			sl2 = new SimpleLine(xarray, yarray2, "Histogram Line");
			sl2.setXMetaData(new SGTMetaData("",""));
			sl2.setYMetaData(new SGTMetaData("",""));
		}

		// set the ranges
		// first find the limits
		double xmax = Double.MIN_VALUE;
		double xmin = Double.MAX_VALUE;
		for (int i=0; i<xarray.length; i++) {
			if (xarray[i]>xmax) xmax = xarray[i];
			if (xarray[i]<xmin) xmin = xarray[i];
		}
		double ymax = Double.MIN_VALUE;
		double ymin = Double.MAX_VALUE;
		for (int i=0; i<yarray.length; i++) {
			if (yarray[i]>ymax) ymax = yarray[i];
			if (yarray[i]<ymin) ymin = yarray[i];
		}
		for (int i=0; i<yarray2.length; i++) {
			if (yarray2[i]>ymax) ymax = yarray2[i];
			if (yarray2[i]<ymin) ymin = yarray2[i];
		}
		Range2D xrange = new Range2D(xmin, xmax);
		Range2D yrange = new Range2D(ymin, ymax);

		double xsize  = 4.0;
		double xstart = 0.6;
		double xend   = 3.25;
		double ysize  = 3.0;
		double ystart = 0.6;
   		double yend   = 4.0;

		layer = new Layer("First Layer", new Dimension2D(xsize, ysize));
		layer.setBackground(Color.white);

		// Setup XY transformations
		LinearTransform xTransform = new LinearTransform(xstart, xend,
			  	xrange.start, xrange.end);
		LinearTransform yTransform = new LinearTransform(ystart, yend,
			  	yrange.start, yrange.end);

		// create graph
		CartesianGraph cg = new CartesianGraph("Line Graph");
		cg.setXTransform(xTransform);
		cg.setYTransform(yTransform);

		// Create the left axis, set its range in user units
		// and its origin. Add the axis to the graph.
		Point2D.Double origin = new Point2D.Double(xrange.start, yrange.start);
		PlainAxis yAxis = new PlainAxis("Y Axis");
		yAxis.setRangeU(yrange);
		yAxis.setLocationU(origin);
		Font yAxisFont = new Font("Helvetica", Font.PLAIN, 14);
		yAxis.setLabelFont(yAxisFont);
		cg.addYAxis(yAxis);

		LineAttribute lineAtr = new LineAttribute();
		cg.setData(sl,lineAtr);

		layer.setGraph(cg);
		jpane = new JPane("Top Pane", new Dimension(500,500));
		jpane.setBatch(true);
		jpane.setBackground(Color.white);
		jpane.setForeground(Color.black);
    	jpane.setLayout(new StackedLayout());
		jpane.add(layer);
		//jpane.setBatch(false);

		buttonPanel = new JPanel();
		printButton = new JButton("Print");
		printButton.addActionListener(this);
		buttonPanel.add(printButton);

		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);

		contentPane.add(jpane, "North");
		contentPane.add(buttonPanel, "South");
		pack();
		//setDefaultSizes();
	//	jpane.draw();
		//show();
		//lpl.draw();
	}

	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getActionCommand();
		if (source == "Close") {
			setVisible(false);
		}
		else if (source == "Print") {
			System.out.println("Trying to print...");
			//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPrintable(this);
			if (job.printDialog()) {
				try {
					job.print();
				}
				catch (Exception ex) {
			   		ex.printStackTrace();
			   	}
			}
			//setCursor(Cursor.getDefaultCursor());
		}
	}

	private void setDefaultSizes() {
		int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()*1/3;
		int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()*2/3;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Point centerPoint = new Point((screen.width-width)/2,(screen.height-height)/2);
		setLocation(centerPoint);
		setSize(width,height);
	}

	public static void main(String[] args) {
		System.out.println("building data");
		Random r = new Random();
		double[] x = new double[98];
		double[] y = new double[98];
		double[] y2 = new double[98];
		for (int i=0; i<98; i++) {
			x[i]=i+1;
			y[i]=100*r.nextDouble();
			y2[i]=100*r.nextDouble();
		}
		// ok, lets try to set the range here...
		//Range2D xrange = new Range2D(1.0,99.0);
		//Range2D yrange = new Range2D(.01,.99);
		//Domain d = new Domain(xrange, yrange);
		System.out.println("creating NOAAshita..");
		NOAALineFrameNoJPL nlf = new NOAALineFrameNoJPL(x,y,y2,"a","b","c","d");
		nlf.show();
		nlf.jpane.draw();
		System.out.println("should see it now...");

		//nlf.lpl.draw();
	}
}




