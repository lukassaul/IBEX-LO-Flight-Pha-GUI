import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.sgt.swing.JClassTree;
import gov.noaa.pmel.sgt.swing.prop.GridAttributeDialog;
import gov.noaa.pmel.sgt.JPane;
import gov.noaa.pmel.sgt.GridAttribute;
import gov.noaa.pmel.sgt.ContourLevels;
import gov.noaa.pmel.sgt.CartesianRenderer;
import gov.noaa.pmel.sgt.CartesianGraph;
import gov.noaa.pmel.sgt.GridCartesianRenderer;
import gov.noaa.pmel.sgt.IndexedColorMap;
import gov.noaa.pmel.sgt.ColorMap;
import gov.noaa.pmel.sgt.LinearTransform;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.util.GeoDate;
import gov.noaa.pmel.util.TimeRange;
import gov.noaa.pmel.util.Range2D;
import gov.noaa.pmel.util.Dimension2D;
import gov.noaa.pmel.util.Rectangle2D;
import gov.noaa.pmel.util.Point2D;
import gov.noaa.pmel.util.IllegalTimeValue;
import gov.noaa.pmel.sgt.dm.SimpleGrid;
import gov.noaa.pmel.sgt.dm.*;
import gov.noaa.pmel.sgt.SGLabel;

import java.awt.event.*;
import java.awt.print.*;
import java.awt.image.*;


import net.jmge.gif.*;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

/**
* Let's use this to take a look at 3D data with color...
*
* Put new code in makeGraph() routine
*
* Added to:
* @author Donald Denbo
* @version $Revision: 1.8 $, $Date: 2001/02/05 23:28:46 $
* @since 2.0
*
* Lukas Saul summer 2002
*/
public class JColorGraph extends PrintableDialog implements ActionListener{
  public JPlotLayout rpl_;
  public JPane gridKeyPane;
  private GridAttribute gridAttr_;
  JButton edit_;
  JButton space_ = null;
  JButton tree_;
  JButton print_;
  JButton save_;
  JButton color_;
  JPanel main;
  Range2D datar;
  ContourLevels clevels;
  ColorMap cmap;
  SGTData newData;

  // defaults
  private String unitString = "Diff. EFlux (1/cm^2/s/sr)";
  private String label1 = "SOHO CTOF HE+";
  private String label2 = "1996";

  public double[] xValues;
  public double[] yValues;
  public double[] zValues;
  public float xMax, xMin, yMax, yMin, zMax, zMin;
  public float cMin, cMax, cDelta;

  public boolean b_color;

  public JColorGraph() {
	  b_color = true;
	 }

  public JColorGraph(double[] x, double[] y, double[] z) {
	 this(x,y,z,true);
  }
  public JColorGraph(double[] x, double[] y, double[] z, boolean b) {
	  b_color = b;

	  xValues=x;
	  yValues=y;
	  zValues=z;


		// assume here we are going to have input the data already
		xMax = (float)xValues[0]; xMin = (float)xValues[0];
		yMax = (float)yValues[0]; yMin = (float)yValues[0];
		zMax = (float)zValues[0]; zMin = (float)zValues[0];

		o("xMin: " + xMin);

		for (int i=0; i<xValues.length; i++) {
			if (xValues[i]<xMin) xMin = (float)xValues[i];
			if (xValues[i]>xMax) xMax = (float)xValues[i];
		}
		for (int i=0; i<yValues.length; i++) {
			if (yValues[i]<yMin) yMin = (float)yValues[i];
			if (yValues[i]>yMax) yMax = (float)yValues[i];
		}
		for (int i=0; i<zValues.length; i++) {
			if (zValues[i]<zMin) zMin = (float)zValues[i];
			if (zValues[i]>zMax) zMax = (float)zValues[i];
		}

		o("xmin: " + xMin);
		o("xmax: " + xMax);

		cMin = zMin; cMax = zMax; cDelta = (zMax-zMin)/6;

		datar = new Range2D(zMin, zMax, cDelta);
		clevels = ContourLevels.getDefault(datar);
		gridAttr_ = new GridAttribute(clevels);

		if (b_color == true) cmap = createColorMap(datar);
		else cmap = createMonochromeMap(datar);
		gridAttr_.setColorMap(cmap);
		gridAttr_.setStyle(GridAttribute.RASTER_CONTOUR);
  }

  private JPanel makeButtonPanel(boolean mark) {
    JPanel button = new JPanel();
    button.setLayout(new FlowLayout());
    tree_ = new JButton("Tree View");
    //MyAction myAction = new MyAction();
    tree_.addActionListener(this);
    button.add(tree_);
    edit_ = new JButton("Edit GridAttribute");
    edit_.addActionListener(this);
    button.add(edit_);
    print_= new JButton("Print");
    print_.addActionListener(this);
    button.add(print_);
    save_= new JButton("Save");
	save_.addActionListener(this);
    button.add(save_);
    color_= new JButton("Colors...");
	color_.addActionListener(this);
    button.add(color_);

     // Optionally leave the "mark" button out of the button panel

    if(mark) {
      space_ = new JButton("Add Mark");
      space_.addActionListener(this);
      button.add(space_);
    }
    return button;
  }
	/**
	* more hacking here
	*/
	 public void showIt() {
		 frame.setVisible(true);
	 }
	 private JFrame frame = null;

 public void run() {
      /*
       * Create a new JFrame to contain the demo.
       */
      frame = new JFrame("Grid Demo");
      main = new JPanel();
      main.setLayout(new BorderLayout());
      frame.setSize(600,500);
      frame.getContentPane().setLayout(new BorderLayout());
      /*
       * Listen for windowClosing events and dispose of JFrame
       */
      frame.addWindowListener(new java.awt.event.WindowAdapter() {
        public void windowClosing(java.awt.event.WindowEvent event) {
  	JFrame fr = (JFrame)event.getSource();
  	fr.setVisible(false);
        }
        public void windowOpened(java.awt.event.WindowEvent event) {
  	rpl_.getKeyPane().draw();
        }
      });
      /*
       * Create button panel with "mark" button
       */
      JPanel button = makeButtonPanel(true);
      /*
       * Create JPlotLayout and turn batching on.  With batching on the
       * plot will not be updated as components are modified or added to
       * the plot tree.
       */
      rpl_ = makeGraph();
      rpl_.setBatch(true);
      /*
       * Layout the plot, key, and buttons.
       */
      main.add(rpl_, BorderLayout.CENTER);
      gridKeyPane = rpl_.getKeyPane();
      gridKeyPane.setSize(new Dimension(600,100));
      rpl_.setKeyLayerSizeP(new Dimension2D(6.0, 1.0));
      rpl_.setKeyBoundsP(new Rectangle2D.Double(0.0, 1.0, 6.0, 1.0));
      main.add(gridKeyPane, BorderLayout.SOUTH);
      frame.getContentPane().add(main, BorderLayout.CENTER);
      frame.getContentPane().add(button, BorderLayout.SOUTH);
      frame.pack();
     // frame.setVisible(true);
      /*
       * Turn batching off. JPlotLayout will redraw if it has been
       * modified since batching was turned on.
       */
      rpl_.setBatch(false);
  }

  void edit_actionPerformed(java.awt.event.ActionEvent e) {
    /*
     * Create a GridAttributeDialog and set the renderer.
     */
    GridAttributeDialog gad = new GridAttributeDialog();
    gad.setJPane(rpl_);
    CartesianRenderer rend = ((CartesianGraph)rpl_.getFirstLayer().getGraph()).getRenderer();
    gad.setGridCartesianRenderer((GridCartesianRenderer)rend);
    //        gad.setGridAttribute(gridAttr_);
    gad.setVisible(true);
  }

    void tree_actionPerformed(java.awt.event.ActionEvent e) {
      /*
       * Create a JClassTree for the JPlotLayout objects
       */
        JClassTree ct = new JClassTree();
        ct.setModal(false);
        ct.setJPane(rpl_);
        ct.show();
    }
	/**
	* This example uses a pre-created "Layout" for raster time
	* series to simplify the construction of a plot. The
	* JPlotLayout can plot a single grid with
	* a ColorKey, time series with a LineKey, point collection with a
	* PointCollectionKey, and general X-Y plots with a
	* LineKey. JPlotLayout supports zooming, object selection, and
	* object editing.
	*/
	private JPlotLayout makeGraph() {

		o("inside makeGraph");

		//SimpleGrid sg;
		SimpleGrid sg;
		//TestData td;
		JPlotLayout rpl;



		//Range2D xr = new Range2D(190.0f, 250.0f, 1.0f);
		//Range2D yr = new Range2D(0.0f, 45.0f, 1.0f);
		//td = new TestData(TestData.XY_GRID, xr, yr,
		//		  TestData.SINE_RAMP, 12.0f, 30.f, 5.0f);
		//newData = td.getSGTData();

		sg = new SimpleGrid(zValues, xValues, yValues, "Title");
		sg.setXMetaData(new SGTMetaData("DOY", "days"));
		sg.setYMetaData(new SGTMetaData("V/Vsw", "1"));
		sg.setZMetaData(new SGTMetaData(unitString, ""));

		//	newData.setKeyTitle(new SGLabel("a","test",new Point2D.Double(0.0,0.0)) );
		//	newData.setTimeArray(GeoDate[] tloc)
		//	 newData.setTimeEdges(GeoDate[] edge);
		sg.setTitle("tttest");
			// newData.setXEdges(double[] edge);
			// newData.setYEdges(double[] edge);

		newData = sg;



		//newData = sg.copy();


		 //Create the layout without a Logo image and with the
		 //ColorKey on a separate Pane object.

		rpl = new JPlotLayout(true, false, false, "test layout", null, true);
		rpl.setEditClasses(false);

		 //Create a GridAttribute for CONTOUR style.

		//datar = new Range2D(zMin, zMax, (zMax-zMin)/6);
		//clevels = ContourLevels.getDefault(datar);
		//gridAttr_ = new GridAttribute(clevels);


		 //Create a ColorMap and change the style to RASTER_CONTOUR.

		//ColorMap cmap = createColorMap(datar);
		//gridAttr_.setColorMap(cmap);
		//gridAttr_.setStyle(GridAttribute.RASTER_CONTOUR);

		 //Add the grid to the layout and give a label for
		 //the ColorKey.


		//rpl.addData(newData, gridAttr_, "Diff. EFlux (1/cm^2/s/sr)");
		rpl.addData(newData, gridAttr_, unitString);

	//	rpl.addData(newData);
		/*
		 * Change the layout's three title lines.
		 */
		rpl.setTitles(label1,label2,"");
		/*
		 * Resize the graph  and place in the "Center" of the frame.
		 */
		rpl.setSize(new Dimension(600, 400));
		/*
		 * Resize the key Pane, both the device size and the physical
		 * size. Set the size of the key in physical units and place
		 * the key pane at the "South" of the frame.
		 */
		rpl.setKeyLayerSizeP(new Dimension2D(6.0, 1.02));
		rpl.setKeyBoundsP(new Rectangle2D.Double(0.01, 1.01, 5.98, 1.0));

		return rpl;
  }


  private ColorMap createColorMap(Range2D datar) {
    int[] red =
    {  255,  0,  0,  0,  0,  0,  0,  0,
       0,  0,  0,  0,  0,  0,  0,  0,
       0,  0,  0,  0,  0,  0,  0,  0,
       0,  7, 23, 39, 55, 71, 87,103,
       119,135,151,167,183,199,215,231,
       247,255,255,255,255,255,255,255,
       255,255,255,255,255,255,255,255,
       255,246,228,211,193,175,158,140};
    int[] green =
    {  255,  0,  0,  0,  0,  0,  0,  0,
       0, 11, 27, 43, 59, 75, 91,107,
       123,139,155,171,187,203,219,235,
       251,255,255,255,255,255,255,255,
       255,255,255,255,255,255,255,255,
       255,247,231,215,199,183,167,151,
       135,119,103, 87, 71, 55, 39, 23,
       7,  0,  0,  0,  0,  0,  0,  0};
    int[] blue =
    {  255,143,159,175,191,207,223,239,
       255,255,255,255,255,255,255,255,
       255,255,255,255,255,255,255,255,
       255,247,231,215,199,183,167,151,
       135,119,103, 87, 71, 55, 39, 23,
       7,  0,  0,  0,  0,  0,  0,  0,
       0,  0,  0,  0,  0,  0,  0,  0,
       0,  0,  0,  0,  0,  0,  0,  0};

    IndexedColorMap cmap = new IndexedColorMap(red, green, blue);
    cmap.setTransform(new LinearTransform(0.0, (double)red.length,
					  datar.start, datar.end));
    return cmap;
  }

  /**
  * here's a simpler map...
  */
  private ColorMap createMonochromeMap(Range2D datar) {
      int[] red = new int[200];
      int[] green = new int [200];
      int[] blue = new int [200];
      red[0]=255;	//red[1]=255;
      green[0]=255;	///green[1]=255;
      blue[0]=255;	//lue[1]=255;

      for (int i=1; i<red.length; i++) {
		  red[i]=230-i;
		  green[i]=230-i;
		  blue[i]=230-i;
	  }

      IndexedColorMap cmap = new IndexedColorMap(red, green, blue);
      cmap.setTransform(new LinearTransform(0.0, (double)red.length,
  					  datar.start, datar.end));
      return cmap;
    }




  public void actionPerformed(java.awt.event.ActionEvent event) {
		 Object source = event.getActionCommand();
		 Object obj = event.getSource();
			if(obj == edit_)
			edit_actionPerformed(event);
		else if(obj == space_) {
			System.out.println("  <<Mark>>");
		}
		else if(obj == tree_)
			tree_actionPerformed(event);

		else if(obj == save_) {
			try {
				BufferedImage ii = getImage();
				String s = JOptionPane.showInputDialog("enter file name");
				ImageIO.write(ii,"png",new File(s+".png"));
				//File outputFile = new File("gifOutput_" +1+".gif");
				//FileOutputStream fos = new FileOutputStream(outputFile);
				//o("starting to write animated gif...");
				////writeAnimatedGIF(ii,"CTOF He+", true, 3.0, fos);
				//writeNormalGIF(ii, "MFI PSD", -1, false, fos);
				//fos.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}


		else if (obj == print_) {
			System.out.println("Trying to print...");
			//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPrintable(this);
			if (job.printDialog()) {
				try {
					o("printing...");
					job.print();
					o("done...");
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
			//setCursor(Cursor.getDefaultCursor());

		else if (obj == color_) {
			try {
				System.out.println("changing color scale");
				rpl_.setVisible(false);
				rpl_.clear();
				cMin = Float.parseFloat(
						JOptionPane.showInputDialog(this,"Min. Color Value: ",cMin+""));
				cMax = Float.parseFloat(
						JOptionPane.showInputDialog(this,"Max. Color Value: ",cMax+""));
				cDelta = Float.parseFloat(
						JOptionPane.showInputDialog(this,"Delta. Color Value: ",cDelta+""));
				rpl_.addData(newData, gridAttr_, "EFlux");
				rpl_.setVisible(true);
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}

	private static void o(String s) {
			System.out.println(s);
	}

	private void writeNormalGIF(Image img,
					  String annotation,
					  int transparent_index,  // pass -1 for none
					  boolean interlaced,
					  OutputStream out) throws IOException   {
		Gif89Encoder gifenc = new Gif89Encoder(img);
		gifenc.setComments(annotation);
		gifenc.setTransparentIndex(transparent_index);
		gifenc.getFrameAt(0).setInterlaced(interlaced);
		gifenc.encode(out);
  	}

  	/**
  	*  Use this to set the labels that will appear on the graph when you call "run()".
  	*
  	*  This first is the big title, second subtitle, third units on color scale (z axis)
  	*/
  	public void setLabels(String l1, String l2, String l3) {
		unitString = l3;
		label1 = l1;
		label2 = l2;
	}

	public BufferedImage getImage() {
		//Image ii = rpl_.getIconImage();
		//if (ii==null) {
		//	System.out.println ("lpl ain't got no image");
		//}
		//else {
			//ImageIcon i = new ImageIcon(ii);
		//	return ii;
		//}

		try {
		//show();
		//setDefaultSizes();
		//rpl.setAutoRange(false,false);
		//rpl.setRange(new Domain(new Range2D(1.3, 4.0), new Range2D(0.0, 35000.0)));
		rpl_.draw();
		//show();

		Robot r = new Robot();

		Point p = main.getLocationOnScreen();

		Dimension d = new Dimension(main.getSize());
		//d.setSize( d.getWidth()+d.getWidth()/2,
		//				d.getHeight()+d.getHeight()/2 );
		Rectangle rect = new Rectangle(p,d);
		//BufferedImage bi = r.createScreenCapture(rect);

		//ImageIcon i = new ImageIcon(r.createScreenCapture(rect));

		return r.createScreenCapture(rect);
		//setVisible(false);
		//return i.getImage();
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}

// some code from original below
/*
  public static void main(String[] args) {

     // Create the demo as an application

    JColorGraph gd = new JColorGraph();

    // Create a new JFrame to contain the demo.
    //
    JFrame frame = new JFrame("Grid Demo");
    JPanel main = new JPanel();
    main.setLayout(new BorderLayout());
    frame.setSize(600,500);
    frame.getContentPane().setLayout(new BorderLayout());

   // Listen for windowClosing events and dispose of JFrame

    frame.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent event) {
	JFrame fr = (JFrame)event.getSource();
	fr.setVisible(false);
	fr.dispose();
	System.exit(0);
      }
      public void windowOpened(java.awt.event.WindowEvent event) {
	rpl_.getKeyPane().draw();
      }
    });

     //Create button panel with "mark" button

    JPanel button = gd.makeButtonPanel(true);

    // Create JPlotLayout and turn batching on.  With batching on the
    // plot will not be updated as components are modified or added to
    // the plot tree.

    rpl_ = gd.makeGraph();
    rpl_.setBatch(true);

    // Layout the plot, key, and buttons.

    main.add(rpl_, BorderLayout.CENTER);
    JPane gridKeyPane = rpl_.getKeyPane();
    gridKeyPane.setSize(new Dimension(600,100));
    rpl_.setKeyLayerSizeP(new Dimension2D(6.0, 1.0));
    rpl_.setKeyBoundsP(new Rectangle2D.Double(0.0, 1.0, 6.0, 1.0));
    main.add(gridKeyPane, BorderLayout.SOUTH);
    frame.getContentPane().add(main, BorderLayout.CENTER);
    frame.getContentPane().add(button, BorderLayout.SOUTH);
    frame.pack();
    frame.setVisible(true);

    //  Turn batching off. JPlotLayout will redraw if it has been
    //  modified since batching was turned on.

    rpl_.setBatch(false);
  }
  */


 /* class MyAction implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent event) {
           Object source = event.getActionCommand();
           Object obj = event.getSource();
           	if(obj == edit_)
              edit_actionPerformed(event);
	   		else if(obj == space_) {
	     		System.out.println("  <<Mark>>");
	   		}
	   		else if(obj == tree_)
	    	   	tree_actionPerformed(event);

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
			}
				//setCursor(Cursor.getDefaultCursor());
        }
    }*/

/*
  public void init() {

    // Create the demo in the JApplet environment.

    getContentPane().setLayout(new BorderLayout(0,0));
    setBackground(java.awt.Color.white);
    setSize(600,550);
    JPanel main = new JPanel();
    rpl_ = makeGraph();
    JPanel button = makeButtonPanel(false);
    rpl_.setBatch(true);
    main.add(rpl_, BorderLayout.CENTER);
    JPane gridKeyPane = rpl_.getKeyPane();
    gridKeyPane.setSize(new Dimension(600,100));
    main.add(gridKeyPane, BorderLayout.SOUTH);
    getContentPane().add(main, "Center");
    getContentPane().add(button, "South");
    rpl_.setBatch(false);
  }
  */