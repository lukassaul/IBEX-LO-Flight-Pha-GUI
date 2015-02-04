//import gov.noaa.noaaserver.sgt.awt.*;
//import gov.noaa.noaaserver.sgt.*;
//import gov.noaa.noaaserver.sgt.datamodel.*;
import gov.noaa.pmel.sgt.swing.*;
import gov.noaa.pmel.sgt.dm.*;
import gov.noaa.pmel.util.*;
import gov.noaa.pmel.sgt.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.event.*;
import java.awt.image.*;
//import net.jmge.gif.*;
import java.io.*;
import javax.imageio.*;
import java.util.Vector;

/**
*
*  OK, time to make a panel plot composer, unfortunately we're not using IDL here...
*    the goal is for making a catalog of prime parameters of the CTOF dataset
*      The components will be instances of gov.noaa.pmel.sgt.CartesianGraph
*
*     of course this can be extended to include other SGT objects
*     PASS IN GRAPHS that works better than layers, panes, or (heaven forbit)
*        - jplotlayouts.
*   @author lukas saul Jan. 2003
*/
public class NOAAPanelPlotFrame extends PrintableDialog {
	private Container contentPane;
	//private JButton printButton, closeButton;
	//private JPanel buttonPanel;
	private CartesianGraph[] theGraphs;
	private double[] theHeights;
	private double keyHeight;
	private Vector v_heights, v_graphs;
	private JPane jpane;
	private ColorKey colorKey;
	//private JPHA theMain;

	/**
	* Just construct an instance here,
	* we build it later after adding the plots with build()
 	*
 	*  do other gui stuff here
	*/
	public NOAAPanelPlotFrame() {
		setTitle("SOHO CTOF Panel Plot - NOAA/JAVA/SGT meets UNH/ESA/NASA");
		v_heights = new Vector();
		v_graphs = new Vector();
		keyHeight = 0.50;

		jpane = new JPane("Top Pane", new Dimension(500,500));
		jpane.setBatch(true);
		jpane.setBackground(Color.white);
		jpane.setForeground(Color.black);
		jpane.setLayout(new StackedLayout());
		// ok, pane is ready for some layers

		contentPane = getContentPane();
		contentPane.setBackground(Color.white);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				setVisible(false);
			}});

		//keyPane = new JPane();
		/*buttonPanel = new JPanel();
		printButton = new JButton("Print");
		printButton.addActionListener(this);
		buttonPanel.add(printButton);

		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);*/

		contentPane.add(jpane, "Center");
		//contentPane.add(keyPane, "South");
	}

	public void addKey(ColorKey ck, float relativeHeight) {
		colorKey = ck;
		keyHeight = (double)relativeHeight;
		//contentPane.add(keyPane, "South");
	}

	/*
	*  Send in a jpl to add a layer - layer is resized later.
	*/
	public void addPanel(JPlotLayout jpl, float relativeHeight) {
		addPanel((CartesianGraph)jpl.getFirstLayer().getGraph(), relativeHeight);
	}

	/**
	* Just add the layer in - we resize it later
 	*/
	public void addPanel(Layer l, float relativeHeight) {
		addPanel((CartesianGraph)l.getGraph(), relativeHeight);
	}

	/*
	*  Add a cartesianGraph at appropriate relative height
	*/
	public void addPanel(CartesianGraph cg, float relativeHeight) {
		try {
			//SpaceAxis pa = (SpaceAxis)cg.getYAxis("Left Axis");
			//pa.setLabelInterval(3);
			//Font yAxisFont = new Font("Helvetica", Font.BOLD, 20);
			//pa.setLabelFont(yAxisFont);
			//pa.setThickTicWidthP(relativeHeight/8.0);
			//cg.removeAllYAxes();
			//cg.addYAxis(pa);
		}
		catch (Exception e) {e.printStackTrace();}
		v_graphs.add(cg);
		v_heights.add(new Float(relativeHeight));
	}

	// go from vectors to arrays for ease of use
	private void createArrays() {
		theGraphs = new CartesianGraph[v_graphs.size()];
		theHeights = new double[v_heights.size()];
		for (int i=0; i<v_graphs.size(); i++) {
			theGraphs[i] = (CartesianGraph)v_graphs.elementAt(i);
			theHeights[i] = ((Float)v_heights.elementAt(i)).doubleValue();
		}
	}

	/**
	* Here's where the panel plot is built and drawn -
	*  call it after adding all the JPlotLayouts (thats where the real work is, creating them)
	*  We are going to need to resize the layers - redo the transformations even...
	*/
	public void build() {
		createArrays();
		// OK, what kind of height we got here
		float tot =0;
		for (int i=0; i<theHeights.length; i++) tot+=theHeights[i];
		tot += keyHeight;
		// that's because we have two spaces at twice a clorkey size each, plus the key

		System.out.println("created arrays in build.. tot height: " + tot);
		// size entire pane here
		double ysize = (double)tot - keyHeight;// - keyHeight;
		double xsize = 8.0/11.0*ysize; // 8x11 paper?

		// add the key layer first
		//Layer keyLayer = new Layer("",new Dimension2D(xsize,ysize));
		//colorKey.setBoundsP(new Rectangle2D.Double(0.0,keyHeight*2,xsize-xsize/15,keyHeight));
		//keyLayer.addChild(colorKey);
		//keyLayer.setBackground(Color.white);
		//jpane.add(keyLayer);

		double currentBottom = keyHeight;
		double currentTop = theHeights[0];

		// build the rest from the ground up
		for (int i=0; i<theGraphs.length; i++) {
			// for VSW we need to adjust the axis labels
			if (i==5 | i==6 | i==7) {
				try {
					SpaceAxis pa = (SpaceAxis)theGraphs[i].getYAxis("Left Axis");
					pa.setLabelInterval(6);
				}
				catch (Exception e) {e.printStackTrace();}
			}

			Layer layer = new Layer("", new Dimension2D(xsize, ysize));
			AxisTransform xtransform = theGraphs[i].getXTransform();
			AxisTransform ytransform = theGraphs[i].getYTransform();
			xtransform.setRangeP(xsize/10,xsize-xsize/10);
			ytransform.setRangeP(currentBottom+theHeights[i]/10, currentTop-theHeights[i]/10);
			// does that set the transform of the graph?  With true pointers it should..
			// but Vectors could have f-ed them up
			layer.setGraph(theGraphs[i]);
			layer.setBackground(Color.white);
			jpane.add(layer);

			// increment for next one if we expect another in new location
			//if (i<theGraphs.length-1  && i!=1 && i!=2) {
			currentBottom = currentTop;
			currentTop = currentTop+theHeights[i];
			//}
		}
		setDefaultSizes();
		//pack();
		show();
		jpane.setBackground(Color.white);
		jpane.draw();
	}

	/*public void actionPerformed(ActionEvent evt) {
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
	}*/

	private void setDefaultSizes() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int)screen.getWidth()*1/2;
		int height = (int)screen.getHeight()*3/4;
		Point centerPoint = new Point((screen.width-width)/2,(screen.height-height)/2);
		//setLocation(centerPoint);
		setSize(width,height);
	}

	/*
	*  Save the image of this panel plot to a png file
	*/
	private BufferedImage ii;
	public void save(String fileName) {
		ii = getImage();
		try {
			ImageIO.write(ii,"png",new File(fileName+".png"));
		}
		catch (Exception e) {
			System.out.println("image io probs in PanelPlot.save() : ");
			e.printStackTrace();
		}
		setVisible(false);
		try {dispose(); this.finalize();}
		catch (Throwable e) {e.printStackTrace();}


	}

	/*
	*  Get a BufferedImage of the panel plot
	*/
	public BufferedImage getImage() {
		try {
			//rpl_.draw();

			Robot r = new Robot();

			Point p = jpane.getLocationOnScreen();

			Dimension d = new Dimension(jpane.getSize());
			//d.setSize( d.getWidth()+d.getWidth()/2,
			//				d.getHeight()+d.getHeight()/2 );
			Rectangle rect = new Rectangle(p,d);
			//BufferedImage bi = r.createScreenCapture(rect);

			//ImageIcon i = new ImageIcon(r.createScreenCapture(rect));

			BufferedImage tbr = r.createScreenCapture(rect);
			//setVisible(false);
			//return i.getImage();
			contentPane.remove(jpane);
			return tbr;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}




