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

/**  This class displays a simple histogram for test purposes
*      - package gov.noaa.noaaserver courtesy of our frineds at NOAA
*/
public class NOAAHistogramFrame extends PrintableDialog implements ActionListener {
	private Container contentPane;
	private long[] array;
	private CTOFGui theMain;
	private JButton printButton, closeButton;
	private JTextArea outputArea;
	private JPanel buttonPanel;
	private JScrollPane scrollPane;

	private JPlotLayout lpl;

	//private JPHA theMain;

	/** Send in the return array from the histogrammer, and a pointer to the GUI
	*    - also send in the filename just written
	*   - not sure if this constructor is being used anymore - depricated
	*  DEPRECATED - use the real outlining routine
	*/
	/*
	public NOAAHistogramFrame(long[] array, CTOFGui _theMain) {
		setTitle("CIS Sample Histogram");
		//super(parent, "CIS Histogram", true);
		theMain = _theMain;
		this.array = array;
		//theMain = parent;

		contentPane = getContentPane();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				setVisible(false);
			}});


		// the NOAA stuff here...
		//LineProfileLayout lpl = new LineProfileLayout("my layout",null,true);
		JPlotLayout lpl = new JPlotLayout(new SimpleLine());


		double[] ypoints = new double[array.length];
		double[] xpoints = new double[array.length];
		for (int i=0; i<array.length; i++) {
			ypoints[i] = (double)(0.0-array[i]);
			xpoints[i] = (double)i;
		}

		SimpleLine sl = new SimpleLine(xpoints, ypoints, "Histogram Line");
		sl.setXMetaData(new SGTMetaData("", ""));
		sl.setYMetaData(new SGTMetaData("Diff. EFlux", ""));

		buttonPanel = new JPanel();
		printButton = new JButton("Print");
		printButton.addActionListener(this);
		buttonPanel.add(printButton);

		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);

		outputArea = new JTextArea();
		file f = new file(theMain.user_dir + theMain.file_sep +
				theMain.outputFileField.getText());
		outputArea.append(f.readShit());
		scrollPane = new JScrollPane(outputArea);
		contentPane.add(lpl, "North");
		contentPane.add(scrollPane, "Center");
		contentPane.add(buttonPanel, "South");

		pack();
		setDefaultSizes();
		show();
	}*/

	/** Send in the return array from the histogrammer, and their labels as well.
	*   - xarray is the value of the lower bin limit!!
	*    - really outline the bins in this routine!!
	*/
	public NOAAHistogramFrame(float[] xarray, float[] yarray, CTOFGui _theMain) {
		setTitle("SOHO CTOF Sample Histogram");
		//this.array = array;
		theMain = _theMain;
		contentPane = getContentPane();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				setVisible(false);
			}});

		//LineProfileLayout lpl = new LineProfileLayout("my layout",null,true);


		double[] xpoints = new double[xarray.length*2];
		double[] ypoints = new double[yarray.length*2];

		int counter = 0;
		for (int i=0; i < xarray.length-1; i++) {
			xpoints[counter] = (double)xarray[i];
			xpoints[counter+1] = (double)xarray[i+1];
			ypoints[counter] = (double)yarray[i];
			ypoints[counter+1] = (double)yarray[i];
			counter += 2;
		}
		xpoints[xpoints.length-2] = (double)xarray[xarray.length-1];
		xpoints[xpoints.length-1] = (double)xarray[xarray.length-1] + (xarray[2] - xarray[1]);
		ypoints[ypoints.length-2] = (double)yarray[yarray.length-1];
		ypoints[ypoints.length-1] = (double)yarray[yarray.length-1];

		// as long as they allow vertical lines...


		//for (int i=0; i<ypoints.length; i++) {
		//	ypoints[i] = (0.0 - ypoints[i]);
		//}

		SimpleLine sl = new SimpleLine(xpoints, ypoints, "Histogram Line");
		sl.setXMetaData(new SGTMetaData("", ""));
		sl.setYMetaData(new SGTMetaData("", ""));


		lpl=new JPlotLayout(sl);
		lpl.clear();
		lpl.setAutoRange(true,true);
		lpl.addData(sl);
		lpl.setTitles("SOHO CTOF Flux Histogram","","");
		//lpl.setTitles("Output file: " + theMain.user_dir + theMain.file_sep +
		//				theMain.outputFileField.getText(),
		//				"Use shift to zoom- ctrl to reset-", "");

		buttonPanel = new JPanel();
		printButton = new JButton("Print");
		printButton.addActionListener(this);
		buttonPanel.add(printButton);

		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);

		buttonPanel.add(theMain.nextIntervalButton);


		outputArea = new JTextArea();
		file f = new file(theMain.user_dir + theMain.file_sep +
						theMain.outputFileField.getText());
		outputArea.append(f.readShit());
		scrollPane = new JScrollPane(outputArea);
		contentPane.add(lpl, "North");
		contentPane.add(scrollPane, "Center");
		contentPane.add(buttonPanel, "South");
		pack();
		setDefaultSizes();
		show();
		lpl.draw();
	}

	/** Send in the return array from the histogrammer, and their labels as well.
	*   - xarray is the value of the lower bin limit!!
	*    - really outline the bins in this routine!!
	* this is identical exept no theMain
	*/
	/*public NOAAHistogramFrame(double[] xarray, double[] yarray) {
		setTitle("SOHO CTOF Sample Histogram");
		//this.array = array;
		//theMain = _theMain;
		contentPane = getContentPane();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				setVisible(false);
			}});

		//LineProfileLayout lpl = new LineProfileLayout("my layout",null,true);
		JPlotLayout lpl = new JPlotLayout(new SimpleLine());

		double[] xpoints = new double[xarray.length*2];
		double[] ypoints = new double[yarray.length*2];

		int counter = 0;
		for (int i=0; i < xarray.length-1; i++) {
			xpoints[counter] = (double)xarray[i];
			xpoints[counter+1] = (double)xarray[i+1];
			ypoints[counter] = (double)yarray[i];
			ypoints[counter+1] = (double)yarray[i];
			counter += 2;
		}
		xpoints[xpoints.length-2] = (double)xarray[xarray.length-1];
		xpoints[xpoints.length-1] = (double)xarray[xarray.length-1] + (xarray[2] - xarray[1]);
		ypoints[ypoints.length-2] = (double)yarray[yarray.length-1];
		ypoints[ypoints.length-1] = (double)yarray[yarray.length-1];

		// as long as they allow vertical lines...




		for (int i=0; i<ypoints.length; i++) {
			ypoints[i] = (0.0 - ypoints[i]);
		}

		SimpleLine sl = new SimpleLine(xpoints, ypoints, "Histogram Line");
		sl.setXMetaData(new SGTMetaData("", ""));
		sl.setYMetaData(new SGTMetaData("", ""));

		lpl.addData(sl);
		//lpl.setTitles("Output file: " + theMain.user_dir + theMain.file_sep +
		//				theMain.outputFileField.getText(),
		//				"Use shift to zoom- ctrl to reset-", "");

		buttonPanel = new JPanel();
		printButton = new JButton("Print");
		printButton.addActionListener(this);
		buttonPanel.add(printButton);

		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);


		outputArea = new JTextArea();
		scrollPane = new JScrollPane(outputArea);
		//file f = new file(theMain.user_dir + theMain.file_sep +
		//				theMain.outputFileField.getText());
		//outputArea.append(f.readShit());
		contentPane.add(lpl, "North");
		contentPane.add(scrollPane, "Center");
		contentPane.add(buttonPanel, "South");
		pack();
		setDefaultSizes();
		show();
	}*/

	/**
	* Use this to display another set of data on the same display (no resize!)
 	*
	*/
	public void changeData(float[] xarray, float[] yarray) {
		double[] xpoints = new double[xarray.length*2];
		double[] ypoints = new double[yarray.length*2];

		int counter = 0;
		for (int i=0; i < xarray.length-1; i++) {
			xpoints[counter] = (double)xarray[i];
			xpoints[counter+1] = (double)xarray[i+1];
			ypoints[counter] = (double)yarray[i];
			ypoints[counter+1] = (double)yarray[i];
			counter += 2;
		}
		xpoints[xpoints.length-2] = (double)xarray[xarray.length-1];
		xpoints[xpoints.length-1] = (double)xarray[xarray.length-1] + (xarray[2] - xarray[1]);
		ypoints[ypoints.length-2] = (double)yarray[yarray.length-1];
		ypoints[ypoints.length-1] = (double)yarray[yarray.length-1];

		SimpleLine sl = new SimpleLine(xpoints, ypoints, "Histogram Line");
		sl.setXMetaData(new SGTMetaData("", ""));
		sl.setYMetaData(new SGTMetaData("", ""));


		//lpl=new JPlotLayout(sl);
		Domain d = lpl.getRange();
		lpl.clear();
		//Domain d = lpl.getRange();
		lpl.setAutoRange(false,false);
		lpl.addData(sl);
		lpl.setTitles("SOHO CTOF Flux Histogram","","");
		//lpl.setTitles("Output file: " + theMain.user_dir + theMain.file_sep +
		//				theMain.outputFileField.getText(),
		//				"Use shift to zoom- ctrl to reset-", "");

		file f = new file(theMain.user_dir + theMain.file_sep +
						theMain.outputFileField.getText());
		outputArea.setText(f.readShit());
		//setDefaultSizes();
		lpl.draw();
		try {lpl.setRange(d);}
		catch (Exception e) {e.printStackTrace();}
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


	private double max() {
		long tbr = 0;
		for (int i=0; i<array.length; i++) {
			if (array[i] > tbr) tbr = array[i];
		}
		return tbr;
	}

	private void setDefaultSizes() {
		int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()*1/3;
		int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()*2/3;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Point centerPoint = new Point((screen.width-width)/2,(screen.height-height)/2);
		setLocation(centerPoint);
		setSize(width,height);
	}
}




