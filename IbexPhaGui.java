import java.util.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
*   Use this to access and print from IBEX - LO PHA data
*    according to analysis specifications
*
*     now requires pha exported data, not .rec files ..
*
*/
public class IbexPhaGui extends JFrame implements ActionListener{
	public String user_dir = System.getProperty("user.dir");
	public String file_sep = System.getProperty("file.separator");
	public String save_dir = user_dir; // for now...
	public String CRLF = System.getProperty("line.separator");
	public String backupFileName = ".ibexSettings";
	public String outputFileName = "";

	// GUI Objects...
	private Container contentPane;
	private JButton runButton, stopButton, clearButton, timesButton, infoButton;
	private JButton inFileButton;
	private JPanel northPanel, southPanel, eastPanel, westPanel, buttonPanel;

	public JTextField inFileField, outFileField, tof0MinField, tof0MaxField;
	public JTextField tof1MinField, tof1MaxField;
	public JTextField tof2MinField, tof2MaxField;
	public JTextField tof3MinField, tof3MaxField;
	public JTextField pacMinField, pacMaxField;
	public JTextField checkSumMinField, checkSumMaxField, numBinsField;
	public JTextField timeMinField, timeMaxField;

	public JLabel statusLabel;
	public JCheckBox tof0CheckBox, tof1CheckBox, tof2CheckBox, tof3CheckBox;
	public JCheckBox checkSumCheckBox, histogramCheckBox, scatterCheckBox;
	public JCheckBox ghostCheckBox, pacCheckBox;
	public JCheckBox mcpScanBox, timeCheckBox, catalogCheckBox;

	public JTextArea outputArea;
	public JScrollPane outputScrollPane;

	public ImageIcon ibexIcon;
	public JFileChooser fc;

	/**
	* the constructor for the GUI.  Constructing this object starts the program.
	*/
	public IbexPhaGui () {
		if (user_dir == null) user_dir = "";
		if (save_dir == null) save_dir = "";

		// Setting up GUI...
		setTitle("IBEX LO TOF GSE PHA GUI");
		contentPane = getContentPane();
		// our window listener...
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				saveSettings();
				System.exit(0);
			}
		});

/*
		//This code is to override the system.out.println to logfile.txt
		System.out.println("Trying to convert output to log file...");
		try {
			FileOutputStream str = new FileOutputStream(new File(user_dir + file_sep + ".ctoflog.txt"));
		    System.setOut(new PrintStream(str));
	        System.setErr(new PrintStream(str));
        }
        catch (Exception ex) { ex.printStackTrace(); }
        System.out.println("Output log for SohoCTOF.java");
*/
		//**** --------------------- Done Initialization----------------------------------------****/

		// Create GUI Objects...
		fc = new JFileChooser();

		boolean haveIcon = true;
		try{
			ibexIcon = FileFinder.getImageIcon("ibexlogo.gif",getClass());
		}
		catch (Exception eee) {haveIcon=false;}
		if (!haveIcon | ibexIcon==null) ibexIcon=new ImageIcon("ibexlogo.gif");
		try {setIconImage(ibexIcon.getImage());}
		catch (Exception eee) {}

		outputArea = new JTextArea();
		outputScrollPane = new JScrollPane(outputArea);
		statusLabel = new JLabel("");

		inFileField = new JTextField(25);
		outFileField = new JTextField("");
		tof0MinField = new JTextField("");
		tof0MaxField = new JTextField("");
		tof1MinField = new JTextField("");
		tof1MaxField = new JTextField("");
		tof2MinField = new JTextField("");
		tof2MaxField = new JTextField("");
		tof3MinField = new JTextField("");
		tof3MaxField = new JTextField("");
		pacMinField = new JTextField("");
		pacMaxField = new JTextField("");
		checkSumMinField = new JTextField("-5");
		checkSumMaxField = new JTextField("5");
		numBinsField = new JTextField("300");
		timeMinField = new JTextField("");
		timeMaxField = new JTextField("");

		tof0CheckBox = new JCheckBox("Req. TOF0",true);
		tof1CheckBox = new JCheckBox("Req. TOF1",true);
		tof2CheckBox = new JCheckBox("Req. TOF2",true);
		tof3CheckBox = new JCheckBox("Req. TOF3",true);
		pacCheckBox = new JCheckBox("PAC range",false);
		checkSumCheckBox = new JCheckBox("Req. Checksum",true);
		histogramCheckBox = new JCheckBox("Make TOF Histograms",true);
		scatterCheckBox = new JCheckBox("Make TOF Scatterplots",true);
		ghostCheckBox = new JCheckBox("Remove Ghosts");
		mcpScanBox = new JCheckBox("Process MCP Scan");
		timeCheckBox = new JCheckBox("Req. Time Range",false);
		catalogCheckBox = new JCheckBox("Catalog",false);


		runButton = new JButton("RUN");
		runButton.addActionListener(this);
		infoButton = new JButton("Show Info");
		infoButton.addActionListener(this);
		inFileButton = new JButton("In File... :");
		inFileButton.addActionListener(this);

		// note - "westpanel" is now really northpanel.. it will go on top
		westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(11,3));
		westPanel.add(new JLabel(""));
		westPanel.add(new JLabel(""));
		westPanel.add(new JLabel(""));
		JLabel bLabel = new JLabel("Filter Selections:");
		bLabel.setForeground(Color.red);
		westPanel.add(bLabel);
		westPanel.add(new JLabel("Min"));
		westPanel.add(new JLabel("Max"));
		westPanel.add(tof0CheckBox);
		westPanel.add(tof0MinField);
		westPanel.add(tof0MaxField);
		westPanel.add(tof1CheckBox);
		westPanel.add(tof1MinField);
		westPanel.add(tof1MaxField);
		westPanel.add(tof2CheckBox);
		westPanel.add(tof2MinField);
		westPanel.add(tof2MaxField);
		westPanel.add(tof3CheckBox);
		westPanel.add(tof3MinField);
		westPanel.add(tof3MaxField);
		westPanel.add(checkSumCheckBox);
		westPanel.add(checkSumMinField);
		westPanel.add(checkSumMaxField);
		westPanel.add(pacCheckBox);
		westPanel.add(pacMinField);
		westPanel.add(pacMaxField);
		westPanel.add(timeCheckBox);
		westPanel.add(timeMinField);
		westPanel.add(timeMaxField);
		westPanel.add(ghostCheckBox);

		// note - "northpanel" is now really center panel, it goes in middle
		northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(7,2));
		JLabel aLabel = new JLabel("In / Out options:");
		aLabel.setForeground(Color.red);
		northPanel.add(aLabel);
		northPanel.add(new JLabel(""));
		northPanel.add(inFileButton);
		northPanel.add(inFileField);
		northPanel.add(new JLabel("Out file: "));
		northPanel.add(outFileField);
		northPanel.add(new JLabel("# of histogram bins"));
		northPanel.add(numBinsField);
		northPanel.add(histogramCheckBox);
		northPanel.add(scatterCheckBox);
		northPanel.add(mcpScanBox);
		northPanel.add(catalogCheckBox);
		northPanel.add(runButton);
		northPanel.add(infoButton);
		//westPanel.add(timesButton);

		//eastPanel = new JPanel(); // empty for now...

		buttonPanel = new JPanel();
		buttonPanel.add(outputScrollPane);
		//buttonPanel.add(runButton);;

		outputArea.append("This is a test string \n\n\n\n\n\n Hello");

		JPanel testPanel = new JPanel();
		testPanel.add(new JLabel(ibexIcon));

		contentPane.add(northPanel, "Center");
		contentPane.add(testPanel, "South");
		contentPane.add(westPanel, "North");
		//contentPane.add(testPanel, "West");
		//contentPane.add(eastPanel, "East");

		//initialize time segments:
		pack();
		setVisible(true);

		file f = new file(user_dir + file_sep + backupFileName);
		if (f.exists()) {
			loadSettings();
		}
	}

	/** Here's where all the button presses and actions go...
	*
	*/
	public void actionPerformed(ActionEvent e) {
		Object source = e.getActionCommand();
		//System.out.println("processing event " + e);

		if (source == "RUN") {
			if (!catalogCheckBox.isSelected()) {
				System.out.println("Trying to run.. ");
				GSE_ep g = new GSE_ep(this);
				System.out.println("Finished running");

				JDialog jd = new JDialog(this, "PHA Analysis Results", false);
				Container bai = jd.getContentPane();
				file f_out = new file(outFileField.getText());
				JTextArea outArea = new JTextArea(f_out.readStuff());
				outArea.setLineWrap(false);
				bai.add(new JScrollPane(outArea));
				jd.setSize(500,500);
				jd.show();
			}
			else {
				File fsel = new File(inFileField.getText());
				File dir = fsel.getParentFile();
				File[] goods = dir.listFiles();
				GSE_ep g;
				for (int i=0; i<goods.length; i++) {
					if (goods[i].getName().contains("_de.txt") &
						!goods[i].getName().contains(".png")
						) {

						File f = new File(goods[i].getAbsolutePath()+"_ql.png");
						if (!f.exists()) {
							System.out.println("file: " + goods[i].getAbsolutePath());
							inFileField.setText(goods[i].getAbsolutePath());
							g = new GSE_ep(this);
							System.gc();
						}
					}
				}
			}
		}


		else if (source == "Show Info") {
			JDialog jd = new JDialog(this, "IBEX-LO TOF PHA Data Filter Help", false);
			Container bai = jd.getContentPane();
			String stuff = FileFinder.getTextFromResourceFile("help.txt",getClass());
			file f_help = null;
			JTextArea helpArea;
			if (stuff.length()<5) {
				f_help = new file("help.txt");
				stuff = f_help.readStuff();
				helpArea = new JTextArea(f_help.readStuff());
			}
			else helpArea = new JTextArea(stuff);
			helpArea.setLineWrap(true);
			bai.add(new JScrollPane(helpArea));
			jd.setSize(500,500);
			jd.show();

        }

        else if (source == "In File... :") {
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File einfile = fc.getSelectedFile();
				String s = einfile.getPath();
				inFileField.setText(s);
			}
        }
	}

	public void loadSettings() {
		file f = new file(backupFileName);
		f.initRead();
		inFileField.setText(f.readLine());
		outFileField.setText(f.readLine());
		f.closeRead();
	}

	public void saveSettings() {
		file f = new file(backupFileName);
		f.initWrite(false);
		f.write(inFileField.getText()+"\n");
		f.write(outFileField.getText()+"\n");
		f.closeWrite();
	}

	/**
	* For our thermometer.  Only updates if the string is new.
	*/
	public void updateStatus(String s) {
		if (!s.equals(statusLabel.getText())) {
			statusLabel.setText(s);
			// this takes time!
		}
	}

	/**
	*  use this method to run this shit
	*/
	public static void main(String[] args) {
		IbexPhaGui pr = new IbexPhaGui();
	}


}
