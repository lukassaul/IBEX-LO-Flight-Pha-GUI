//import gov.noaa.noaaserver.sgt.awt.*;
//import gov.noaa.noaaserver.sgt.*;
//import gov.noaa.noaaserver.sgt.datamodel.*;
import gov.noaa.pmel.sgt.swing.*;
import gov.noaa.pmel.sgt.dm.*;
import gov.noaa.pmel.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.Random; // for testing

/**  This class displays a simple histogram for test purposes
*      - package gov.noaa.noaaserver courtesy of our frineds at NOAA
*/
public class NOAALineFrame extends PrintableDialog implements ActionListener {
	private Container contentPane;
	private long[] array;
	private JButton printButton, closeButton;
	private JPanel buttonPanel;
	private JScrollPane scrollPane;

	public JPlotLayout lpl;
	//private JPHA theMain;
	private boolean do2Lines = true; // just to stay awake and get this code done
	private boolean do3Lines = true;

	public NOAALineFrame(double[] xarray, double[] yarray,
			String xLable, String xUnit, String yLabel, String yUnit) {
		this (xarray,yarray,new double[0],new double[0],xLable,xUnit,yLabel,yUnit);
	}

	/**
	*  Send in the return array from the histogrammer, and their labels as well.
	*
	*/
	public NOAALineFrame(double[] xarray, double[] yarray, double[] yarray2, double[] yarray3,
			String xLabel, String xUnit, String yLabel, String yUnit) {
		setTitle("SOHO CTOF Sample Line Plot");
		//this.array = array;
		if (yarray2.length<2) do2Lines = false;
		if (yarray3.length<2) do3Lines = false;
		contentPane = getContentPane();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				setVisible(false);
			}});

		//LineProfileLayout lpl = new LineProfileLayout("my layout",null,true);

		SimpleLine sl = new SimpleLine(xarray, yarray, "Line 1");
		sl.setXMetaData(new SGTMetaData(xLabel, xUnit));
		sl.setYMetaData(new SGTMetaData(yLabel, yUnit));
		sl.setId("Line 1"); // important - we like handles.

		SimpleLine sl2 = null;
		SimpleLine sl3 = null;
		if (do2Lines) {
			sl2 = new SimpleLine(xarray, yarray2, "Line 2");
			sl2.setXMetaData(new SGTMetaData("",""));
			sl2.setYMetaData(new SGTMetaData("",""));
			sl2.setId("Line 2");
		}
		if (do3Lines) {
			sl3 = new SimpleLine(xarray, yarray2, "Line 2");
			sl3.setXMetaData(new SGTMetaData("",""));
			sl3.setYMetaData(new SGTMetaData("",""));
			sl3.setId("Line 2");
		}

		//SimpleLine sl3 = null;
		//if (do2Lines) {
		//	double[] yarray0 = {0.0,0.0};
		//	double[] xarray0 = {xarray[0],xarray[xarray.length-1]};
		//	sl3 = new SimpleLine(xarray0, yarray0, "Line 3");
		//	sl3.setXMetaData(new SGTMetaData("",""));
		//	sl3.setYMetaData(new SGTMetaData("",""));
		//	sl3.setId("Line 3");
		//}


		lpl=new JPlotLayout(sl);
		lpl.clear();
		//lpl.setAutoRange(true,true);
		lpl.addData(sl);
		if (do2Lines)	lpl.addData(sl2);
		if (do3Lines)	lpl.addData(sl3);

		lpl.setTitles("","","");
		//lpl.setTitles("Output file: " + theMain.user_dir + theMain.file_sep +
		//				theMain.outputFileField.getText(),
		//				"Use shift to zoom- ctrl to reset-", "");
		//try {lpl.setRangeNoVeto(theDomain);}
		//catch (Exception e) {
		//	System.out.println("this sucks i couldn't set the domain");
		//}
		lpl.setYAutoRange(false);
		lpl.setKeyBoundsP(new Rectangle2D.Double(0.0,0.0));
		lpl.setTitleHeightP(0.0,0.0);

		buttonPanel = new JPanel();
		printButton = new JButton("Print");
		printButton.addActionListener(this);
		buttonPanel.add(printButton);

		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);

		contentPane.add(lpl, "North");
		contentPane.add(buttonPanel, "South");
		pack();
		setDefaultSizes();
		//show();
		lpl.draw();
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
		Random r = new Random();
		double[] x = new double[98];
		double[] y = new double[98];
		double[] y2 = new double[98];
		for (int i=0; i<98; i++) {
			x[i]=i+1;
			y[i]=r.nextDouble();
			y2[i]=r.nextDouble();
		}
		// ok, lets try to set the range here...
		//Range2D xrange = new Range2D(1.0,99.0);
		//Range2D yrange = new Range2D(.01,.99);
		//Domain d = new Domain(xrange, yrange);

		NOAALineFrame nlf = new NOAALineFrame(x,y,"a","b","c","d");
		nlf.show();
		nlf.lpl.draw();
	}
}




