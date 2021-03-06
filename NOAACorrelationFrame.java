import gov.noaa.noaaserver.sgt.awt.*;
import gov.noaa.noaaserver.sgt.*;
import gov.noaa.noaaserver.sgt.datamodel.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;


/**  This class displays a simple histogram for test purposes
*      - package gov.noaa.noaaserver courtesy of our frineds at NOAA
*/
public class NOAACorrelationFrame extends PrintableDialog implements ActionListener{

	private Container contentPane;
	private long[] array;
	private JButton printButton, closeButton;
	private JTextArea outputArea;
	private JPanel buttonPanel;
	private JScrollPane scrollPane;
	private LineProfileLayout lpl;
    private SGTMetaData sgtMetaData = new SGTMetaData("", "");
    private SimpleLine sl = new SimpleLine();

	public static double DELTA = .01;
	private double newDelta = 0;

	/** Send in the return array from the histogrammer, and a pointer to the GUI
	*    - also send in the filename just written
	*/
	public NOAACorrelationFrame(Correlation s) {
		setTitle("NOAA SGT Scatter Graph");

		//super(parent, "CIS Histogram", true);
		//this.array = array;
		//theMain = parent;

		contentPane = getContentPane();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				setVisible(false);
				//System.exit(0);
			}
		});

		printButton = new JButton("Print");
		printButton.addActionListener(this);
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel = new JPanel();
		buttonPanel.add(printButton);
		buttonPanel.add(closeButton);
		contentPane.add(buttonPanel, "South");

		lpl = new LineProfileLayout("my layout",null,true);

		s.finalize(); // just in case!!
		addCorrelation(s);
		//lpl.setYRange(new Range2D(max(),0.0,1.0), true);

		//lpl.setSize(new Dimension(450,600));

		contentPane.add(lpl, "Center");
		lpl.setTitles(" " + " "+ " " + " ",
		              "Use shift to zoom- ctrl to reset-",
		              "");

		pack();
		show();
	}

	public void addCorrelation(Correlation s) {
		double[] x = new double[s.xOut.length];
		double[] y = new double[s.yOut.length]; // better be the same!
		for (int i=0; i<x.length; i++) {
			x[i] = (double)s.xOut[i];
			y[i] = (double)s.yOut[i];
		}
		addPoints(x,y);
	}

	public void addPoints(double[] x, double[] y) {
		for (int i=0; i<x.length; i++) {
			addPoint(x[i],y[i]);
		}
	}

	public void addPoint(double x, double y) {
		double[] ypoints = new double[2];
		double[] xpoints = new double[2];

		//if (newDelta==0) newDelta = x*DELTA;
		// first draw a horizontal line
		xpoints[0] = x-DELTA*x;
		xpoints[1] = x+DELTA*x;
		//xpoints[0] = x- newDelta;
		//xpoints[1] = x+ newDelta;
		ypoints[0] = y;
		ypoints[1] = y;


		SimpleLine sl = new SimpleLine(xpoints, ypoints, "");
		sl.setXMetaData(sgtMetaData);//new SGTMetaData("", ""));
		sl.setYMetaData(sgtMetaData);//(new SGTMetaData("", ""));

		lpl.addData(sl);

		/* now the vertical line
		xpoints[0] = x;
		xpoints[1] = x;
		ypoints[0] = y - DELTA*y;
		ypoints[1] = y + DELTA*y;

        //sl.setXArray(xpoints);
        //sl.setYArray(ypoints);
		SimpleLine sl = new SimpleLine(xpoints, ypoints, "");
		sl.setXMetaData(sgtMetaData);//new SGTMetaData("", ""));
		sl.setYMetaData(sgtMetaData);//new SGTMetaData("", ""));

		lpl.addData(sl);
		*/
	}


	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getActionCommand();
		if (source == "Close") {
			setVisible(false);
			//System.exit(0);
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
			System.out.println("Supposedly printed...");
			//setCursor(Cursor.getDefaultCursor());
		}
	}

	/*private double max() {
		long tbr = 0;
		for (int i=0; i<array.length; i++) {
			if (array[i] > tbr) tbr = array[i];
		}
		return tbr;
	}*/

	private void setDefaultSizes() {
		int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()*1/3;
		int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()*2/3;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Point centerPoint = new Point((screen.width-width)/2,(screen.height-height)/2);
		setLocation(centerPoint);
		setSize(width,height);
	}

	public static final void main(String[] args) {

		//nfgf.show();

	}

}




