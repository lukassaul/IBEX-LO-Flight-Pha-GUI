import java.util.StringTokenizer;
import java.util.Vector;
import gov.noaa.pmel.sgt.CartesianGraph;
import java.io.*;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;


/**
*  let's parse some gse data in PHA form
*   now we have added a gui to this class so no gui stuff here..
*
*  this class is run from inside IbexPhaGui
*
*  Ibex_time key:  913591566 = 352.97629630  2008
*
*/
public class GSE_ep {
	public int skipLines = 0;

	/**
	*
	* data to collect:
	*/
	double t0_val, t1_val, t2_val, t3_val;
	double m0, m1, m2;
	int t0_start, t1_start, t2_start, t3_start;
	int t0_stop, t1_stop, t2_stop, t3_stop;
	int t0_tof, t1_tof, t2_tof, t3_tof;
	float pac, mcp;
	long date;

	public Histogram t1h, t2h, t3h, t0h, csh;
	public Histogram massh, mass0h, mass1h, mass2h;
	public Histogram timeh;
	public Vector tof0, tof1, tof2, tof3;
	public IbexPhaGui theMain;
	public file f;
	public file outf;
	public String outfName;
	public int numBins;
	public long firstTime=-1;
	public double timeStamp=0;
	public long lastTime=0;
	public long mintime, maxtime, minelimtime, maxelimtime;

	public int rates1 = 0;
	public int rates2 = 0;
	public int rates3 = 0;
	public int[] ratesH = new int[15];
	public int[] ratesO = new int[15];

	// event types
	public int ev_trip =0;
	public int ev_not0 = 0;
	public int ev_not1 = 0;
	public int ev_not2 = 0;
	public int ev_not3 = 0;
	public int ev_t0 =0;
	public int ev_t1 =0;
	public int ev_t2 =0;
	public int ev_t3 =0;
	public int ev_0011 = 0;
	public int ev_0101 = 0;
	public int ev_0110 = 0;
	public int ev_1001 = 0;
	public int ev_1010 = 0;
	public int ev_1100 = 0;
	public int ev_junk =0;
	public int ev_other =0;

	public int t0_tally, t1_tally, t2_tally, t3_tally, silver_tally;
	public double min0,min1,min2,min3,max0,max1,max2,max3,mincs,maxcs,minpac,maxpac;
	//  for "or" criterion, to get at doubles..
	public double omin0, omin1, omin2, omin3, omax1, omax2, omax3, omax0;

	// these booleans are set by checkboxes in the gui
	// they are true if a filtering range exists for the associated parameter
	public boolean b0=false;
	public boolean b1=false;
	public boolean b2=false;
	public boolean b3=false;
	public boolean ob0=false;
	public boolean ob1=false;
	public boolean ob2=false;
	public boolean ob3=false;
	public boolean bcs=false;
	public boolean bpac=false;
	public boolean btime=false;
	public boolean belimtime=false;
	public boolean bdec=false;
	public boolean basc=false;
	public boolean bsp=false;
	public boolean bbin=false;
	public boolean bBM=false;

	public double dec,asc;
	public int ebin;

	// new HB defs
	public int count, count81, count82;
	public String ch, ty;
	public boolean trip, doub, is81, is82;
	public boolean requireTriples;

	public int numMcpBins;
	public MCPhaData[] mcpData;
	private long deltaT;
	public int sec0, sec1, sec2, sec3, sec4;

	public int eBin;
	public double minDec, maxDec, minAsc, maxAsc, minSP, maxSP;
	public HistogramVector[] timeHist;
	public HistogramNest posHist;
	public Histogram spinHist;
	public HistogramFromFile timeHist2[];
	public Histogram spinHiste[]; // make 8 spin histograms, one for each energy
	public SpinTimeHistogram spinTimeHist;
	public float spinPhase;
	public int[] ebinCounter = new int[8]; // energy bin histogram, use an array..
	public int hbH, hbO; // counters to match histogram bin triple H & O criteria
	public int totT0, totT1, totT2, totT3;
	public file[] files;
	public double massResolution;
	public boolean useMass;
	int debug1 = 0;
	public DataInputStream dis;
	public int binaryFileSize, onePercent;
	public int bmMax;
	public BMReader bmr;
	public TimeZone tz;
	public Calendar c;
	public int bmLost, bmFound;
	public HeSputterFunction hsf;
	public Histogram[] spinTof2;
	//public

	public HistogramExact t0he, t1he, t2he;
	public IbexTimesList itl;
	public boolean useTimesFile;

	/**
	* Construct an instance of the GSE_ep parser for pha data from IBEX-LO TOF GSE
	*  This parses the file and processes the data as outlined in IbexPhaGui options
	*/
	public GSE_ep(IbexPhaGui tm) {

		// set up for real date handling

		tz = TimeZone.getTimeZone("UTC");
		c = Calendar.getInstance();
		c.setTimeZone(tz);


		 // initialize
		t0he = new HistogramExact();
		t1he = new HistogramExact();
		t2he = new HistogramExact();
		hbH=0;
		hbO=0;
		timeHist = new HistogramVector[15];
		//timeHist2 = new HistogramFromFile[8];
		for (int i=0; i<8; i++) {
			//timeHist[i]
			//timeHist[i].name = "ebin"+i;
			//timeHist2[i]=new HistogramFromFile("c:\\flight_data\\o0020\\o20times.txt");
		}
		int numTotal = 0;
		int numAccepted = 0;
		min0=0.0;
		min1=0.0;
		min2=0.0;
		min3=0.0;
		mincs=0.0;
		max0=0.0;
		max1=0.0;
		max2=0.0;
		max3=0.0;
		maxcs=0.0;
		pac = 0.0f;
		minpac = 0.0;
		maxpac = 0.0;
		t0_tally = 0;
		t1_tally = 0;
		t2_tally = 0;
		t3_tally = 0;
		silver_tally = 0;
		mintime=Long.MAX_VALUE;
		maxtime=0;
		minelimtime=0;
		maxelimtime=0;
		sec0=0;
		sec1=0;
		sec2=0;
		sec3=0;
		sec4=0;
		totT0=0;
		totT1=0;
		totT2=0;
		totT3=0;
		useMass=false;
		useTimesFile = false;
		for (int i=0; i<ratesH.length; i++) {
			ratesH[i]=0;
			ratesO[i]=0;
		}

		// read all appropriate settings from the gui first before reading data file
		//
		// fix - should be one try-catch loop instead of dozens for clarity
		theMain = tm;

		if (theMain.filesBox.isSelected()) {
			file filefile = new file("filelist.txt");
			files = new file[filefile.readStuffNumLines()];
			filefile.initRead();
			for (int i=0; i<files.length; i++) {
				files[i]=new file(filefile.readLine());
			}
			filefile.closeRead();
		}
		else {
			files = new file[1];
			files[0] = new file(theMain.inFileField.getText());
		}


		if (theMain.timesFileCheckBox.isSelected()) {
			itl = new IbexTimesList(theMain.timesFileField.getText());
			useTimesFile= true;
		}

		outfName = theMain.outFileField.getText();


		if (theMain.mcpScanBox.isSelected()) {
			numMcpBins = 12;
			mcpData = new MCPhaData[numMcpBins];
			for (int i=0; i<numMcpBins; i++) mcpData[i]=new MCPhaData(2700+i*600/numMcpBins);
			System.out.println("Mcp_data objects created for MCP scan");
		}
		if (theMain.histogramCheckBox.isSelected()) {
			try {
				numBins = Integer.parseInt(theMain.numBinsField.getText());
			}catch (Exception e) { e.printStackTrace(); }
			t0h = new Histogram((float)0.0,(float)350.0,numBins);
			t1h = new Histogram((float)0.0,(float)350.0,numBins);
			t2h = new Histogram((float)0.0,(float)350.0,numBins);
			t3h = new Histogram((float)0.0,(float)18,50);
		//	t0h = new Histogram((float)1.1309,(float)(1.131+numBins*0.1662),numBins);
		//	t1h = new Histogram((float)1.2807,(float)(1.2808+numBins*0.1642),numBins);
		//	t2h = new Histogram((float)0.86,(float)(0.87+numBins*0.1671),numBins);
		//	t3h = new Histogram((float)0.2384,(float)(0.2385+numBins*0.1646),numBins);
			csh = new Histogram((float)-1.0,(float)1.5,50);
			massh = new Histogram((float)0.0,(float)50.0,numBins);
			mass0h = new Histogram((float)0.0,(float)50.0,numBins);
			mass1h = new Histogram((float)0.0,(float)50.0,numBins);
			mass2h = new Histogram((float)0.0,(float)50.0,numBins);
		}

		// set up pos histogram
		if (theMain.posHistBox.isSelected()) {
			posHist = new HistogramNest(0.0f,360.0f,-90.0f, 90.0f,45);
		}

		if (theMain.spinBox.isSelected()) {
	//		spinHist = new Histogram(0.0f, 360.0f, 45);
			// lets get the resolution from the gui
			float deg_res = 2;
			try {
				deg_res = Float.parseFloat(theMain.spResField.getText());
			}
			catch (Exception e) {}
			int num_bins_sp = 360/(int)deg_res;
			spinHist = new Histogram(0.0f, 1.0f, num_bins_sp);
			spinHiste = new Histogram[15];
			for (int i=0; i<15; i++) {
				spinHiste[i]=new Histogram(0.0f, 1.0f, num_bins_sp);
			}

			if (theMain.histogramCheckBox.isSelected()) {
				spinTof2 = new Histogram[num_bins_sp];
				for (int i=0; i<num_bins_sp; i++) {
					spinTof2[i] = new Histogram((float)0.0,(float)400.0,numBins);
				}
			}
		}

		if (theMain.scatterCheckBox.isSelected()) {
			tof0=new Vector();
			tof1=new Vector();
			tof2=new Vector();
			tof3=new Vector();
		}
		if (theMain.tof0CheckBox.isSelected()) try {
			min0 = Double.parseDouble(theMain.tof0MinField.getText());
			max0 = Double.parseDouble(theMain.tof0MaxField.getText());
			b0 = true;
		} catch (Exception e) {}
		else try {
			omin0 = Double.parseDouble(theMain.tof0MinField.getText());
			omax0 = Double.parseDouble(theMain.tof0MaxField.getText());
			ob0 = true;
		} catch (Exception e) {}

		if (theMain.tof1CheckBox.isSelected()) try {
			min1 = Double.parseDouble(theMain.tof1MinField.getText());
			max1 = Double.parseDouble(theMain.tof1MaxField.getText());
			b1=true;
		} catch (Exception e) {}
		else try {
			omin1 = Double.parseDouble(theMain.tof1MinField.getText());
			omax1 = Double.parseDouble(theMain.tof1MaxField.getText());
			ob1=true;
		} catch (Exception e) {}


		if (theMain.tof2CheckBox.isSelected()) try {
			min2 = Double.parseDouble(theMain.tof2MinField.getText());
			max2 = Double.parseDouble(theMain.tof2MaxField.getText());
			b2=true;
		} catch (Exception e) {}
		else try {
			omin2 = Double.parseDouble(theMain.tof2MinField.getText());
			omax2 = Double.parseDouble(theMain.tof2MaxField.getText());
			ob2=true;
		} catch (Exception e) {}

		if (theMain.tof3CheckBox.isSelected()) try {
			min3 = Double.parseDouble(theMain.tof3MinField.getText());
			max3 = Double.parseDouble(theMain.tof3MaxField.getText());
			b3=true;
		} catch (Exception e) {}
		else try {
			omin3 = Double.parseDouble(theMain.tof3MinField.getText());
			omax3 = Double.parseDouble(theMain.tof3MaxField.getText());
			ob3=true;
		} catch (Exception e) {}

		if (theMain.checkSumCheckBox.isSelected()) try {
			mincs = Double.parseDouble(theMain.checkSumMinField.getText());
			maxcs = Double.parseDouble(theMain.checkSumMaxField.getText());
			bcs=true;
		} catch (Exception e) {}
		if (theMain.pacCheckBox.isSelected()) try {
			minpac = Double.parseDouble(theMain.pacMinField.getText());
			maxpac = Double.parseDouble(theMain.pacMaxField.getText());
			bpac=true;
		} catch (Exception e) {}
		if (theMain.timeCheckBox.isSelected())
			try {
				System.out.println("trying to add start and finish time filter");
				if (Float.parseFloat(theMain.timeMinField.getText())>90000) {
					mintime = Long.parseLong(theMain.timeMinField.getText());
					maxtime = Long.parseLong(theMain.timeMaxField.getText());

				}
				else {
					mintime = (long)getGpsTime(Float.parseFloat(theMain.timeMinField.getText()));
					maxtime = (long)getGpsTime(Float.parseFloat(theMain.timeMaxField.getText()));
				}
				//mintime = Long.parseLong(theMain.timeMinField.getText());
				//maxtime = Long.parseLong(theMain.timeMaxField.getText());
				btime=true;
			} catch (Exception e) {
			e.printStackTrace();
			System.out.println("couldn't parse times!!! "+theMain.timeMaxField.getText());
		}
		if (theMain.timeElimCheckBox.isSelected())
			try {
				System.out.println("trying to add start and finish time filter");
				if (Float.parseFloat(theMain.timeElimMinField.getText())>900) {
					minelimtime = Long.parseLong(theMain.timeElimMinField.getText());
					maxelimtime = Long.parseLong(theMain.timeElimMaxField.getText());

				}
				else {
					minelimtime = (long)getGpsTime(Float.parseFloat(theMain.timeElimMinField.getText()));
					maxelimtime = (long)getGpsTime(Float.parseFloat(theMain.timeElimMaxField.getText()));
				}
				//mintime = Long.parseLong(theMain.timeMinField.getText());
				//maxtime = Long.parseLong(theMain.timeMaxField.getText());
				belimtime=true;
			} catch (Exception e) {
			e.printStackTrace();
			System.out.println("couldn't parse elim times!!! "+theMain.timeElimMaxField.getText());
		}
		if (theMain.ascCheckBox.isSelected()) try {
			minAsc = Double.parseDouble(theMain.ascMinField.getText());
			maxAsc = Double.parseDouble(theMain.ascMaxField.getText());
			basc=true;
		} catch (Exception e) {}
		if (theMain.spinPhaseCheckBox.isSelected()) try {
			minSP = Double.parseDouble(theMain.spinPhaseMinField.getText());
			maxSP = Double.parseDouble(theMain.spinPhaseMaxField.getText());
			bsp=true;
		} catch (Exception e) {}
		if (theMain.decCheckBox.isSelected()) try {
			minDec = Double.parseDouble(theMain.decMinField.getText());
			maxDec = Double.parseDouble(theMain.decMaxField.getText());
			bdec=true;
		} catch (Exception e) {}
		if (theMain.ebinCheckBox.isSelected()) try {
			eBin = Integer.parseInt(theMain.ebinField.getText());
			bbin=true;
		} catch (Exception e) {}
		try {
			skipLines = Integer.parseInt(theMain.skipLinesField.getText());
		} catch (Exception e) {}

		if (theMain.eHistogramBox.isSelected()) {
			ebinCounter = new int[8];
			for (int i=0; i<8; i++) ebinCounter[i]=0;
		}
		if (theMain.tof0CheckBox.isSelected() &
			theMain.tof1CheckBox.isSelected() &
			theMain.tof2CheckBox.isSelected() &
			theMain.tof3CheckBox.isSelected()) {
				requireTriples = true;
		}

		if (theMain.useMassBox.isSelected()) {
			useMass = true;
			massResolution = Double.parseDouble(theMain.massResolutionField.getText());
		}

		if (theMain.binaryFileBox.isSelected()) {
			try {
				o("initialized reading binary data file");
				dis = new DataInputStream(new BufferedInputStream(new FileInputStream(theMain.inFileField.getText())));

				File f = new File(theMain.inFileField.getText());
				binaryFileSize = ((int)f.length());
				System.out.println(f.length() + " " + binaryFileSize);
				onePercent = binaryFileSize/100 + 1;
				System.out.println("one percent: " + onePercent);
			}
			catch (Exception e) {
				o("could not open bin file");
				e.printStackTrace();
			}
		}

		boolean isBinFile = theMain.binaryFileBox.isSelected();
		boolean isHBFile = theMain.hbBox.isSelected();
		boolean t0check = theMain.tof0CheckBox.isSelected();
		boolean t1check = theMain.tof1CheckBox.isSelected();
		boolean t2check = theMain.tof2CheckBox.isSelected();
		boolean t3check = theMain.tof3CheckBox.isSelected();
		boolean timeHistCheck = theMain.timeHistBox.isSelected();
		boolean posHistCheck = theMain.posHistBox.isSelected();
		boolean eHistCheck = theMain.eHistogramBox.isSelected();
		boolean spinTimeCheck = theMain.spinTimeBox.isSelected();
		boolean spinCheck = theMain.spinBox.isSelected();
		boolean scatterCheck = theMain.scatterCheckBox.isSelected();
		boolean histCheck = theMain.histogramCheckBox.isSelected();
		boolean ghostCheck = theMain.ghostCheckBox.isSelected();
		boolean firstDate = true;

		String line = "";
		String garbage = "";

		if (theMain.bmCheckBox.isSelected()) {
			bBM = true;
			bmMax = Integer.parseInt(theMain.bmMaxField.getText());
			bmr = new BMReader();
			bmLost = 0;
			bmFound = 0;
		}

		if (theMain.subtractHeBox.isSelected()) {
			hsf = new HeSputterFunction("o64_he_b1_tof2.txt",300);
		}

		// done reading from gui!  now..
        // here's where we read the data file
        for (int i=0; i<files.length; i++) {

			if (!theMain.binaryFileBox.isSelected()) {
				System.out.println("reading file: " + files[i].fileName);
				files[i].initRead();
				for (int j=0; j<skipLines; j++)	 line = files[i].readLine();
			}
			boolean moreEvents = true;
			int bytesRead = 0;
			int counter = 0;
			int eventSize = 7*4 + 1*8 + 1*2;

			while (moreEvents) {

				boolean eventOK = true;
				if (!isBinFile) {
					line=files[i].readLine();
					if (line==null) moreEvents=false;
					if (line.length()<40) moreEvents=false;
				}
				if (isHBFile && moreEvents) {
					// parse HB format
					doub = false;
					trip = false;
					is81=false;
					is82=false;
					StringTokenizer st = new StringTokenizer(line);
					timeStamp = (long)Double.parseDouble(st.nextToken());
					// comment these out for now..  speed this up
					asc = Double.parseDouble(st.nextToken());
					dec = Double.parseDouble(st.nextToken());
					//garbage = st.nextToken();
					//garbage = st.nextToken();
					ch = st.nextToken();

					ebin = Integer.parseInt(ch.substring(1,2));
					ty = st.nextToken();
					if (ty.indexOf("CE")!=-1) {
						trip=true;
						doub=true;
					}

					else if (
			//				 ty.indexOf("C4")!=-1  |
			//				 ty.indexOf("C5")!=-1  |
			//				 ty.indexOf("C6")!=-1
							 ty.indexOf("C7")!=-1
						) {
						doub=true;
					}
					count = Integer.parseInt(st.nextToken());
					garbage = st.nextToken();
					spinPhase = Float.parseFloat(st.nextToken());
					//System.out.println("ch: " + ch);
					//System.out.println("ty: " + ty);
					if (ty.equals("81")) {
						is81=true;
						count81+=count;
					}
					if (ty.equals("82")) {
						count82+=count;
						is82=true;
					}

				}
				else if (isBinFile && moreEvents) {
					try {// parse binary file format!! yay!!
						timeStamp = dis.readFloat();
						dec = dis.readInt()/1000.0; ///100.0;
						asc = dis.readInt()/1000.0; ///100.0;
						spinPhase = ((float)dis.readInt())/1000.0f; ///100.0f;
						ebin = (int)dis.readShort();
						t0_val = ((float)dis.readInt())/1000.0f;
						t1_val = ((float)dis.readInt())/1000.0f;
						t2_val = ((float)dis.readInt())/1000.0f;
						t3_val = ((float)dis.readInt())/1000.0f;
						if (t0_val==0.0f) t0_tof=0;   else t0_tof=1;
						if (t1_val==0.0f) t1_tof=0;   else t1_tof=1;
						if (t2_val==0.0f) t2_tof=0;   else t2_tof=1;
						if (t3_val==0.0f) t3_tof=0;   else t3_tof=1;
						count = 1;  // IMPORTANT <- each event is one count

						bytesRead += eventSize;
						//o("got info: " + dec + " , "  + asc + " , " + ebin + " , " + timeStamp + "\n");
						//o("got info: " + t0_val + " , "  + t1_val + " , " + t2_val + " , " + t3_val);

						if (bytesRead >= onePercent) {
							counter++;
							o("read: " + counter + "%   last date: " + timeStamp);
							bytesRead = 0;
						}

					}
					catch (Exception e) {
						o("error or end reading bin file");
						moreEvents=false;
						//e.printStackTrace();
					}
					numTotal++;
				}

				else if (moreEvents & !isBinFile & !isHBFile) {
					//
					// parsing output of: $lo_tof_de -t gseos
					// first line
					StringTokenizer st = new StringTokenizer(line);
					timeStamp = Double.parseDouble(st.nextToken());      //
					dec = Double.parseDouble(st.nextToken());			//
					asc = Double.parseDouble(st.nextToken());			//
					String bin = st.nextToken();
					//System.out.println("bin: " + bin);
					bin = bin.substring(1,2);
					//System.out.println("bin: " + bin);
					//bin = bin.substring(1,1);
					//System.out.println("bin: " + bin);
					if (bin.equals("A")) ebin = 4;
					else {
						try {
							ebin = Integer.parseInt(bin);
						}
						catch (Exception e) {
							eventOK=false;
						}
					}
					garbage = st.nextToken();
					garbage = st.nextToken();
					garbage = st.nextToken();
					spinPhase = Float.parseFloat(st.nextToken());

					// second line
					line = files[i].readLine();
					st = new StringTokenizer(line);
					t0_val = Double.parseDouble(st.nextToken());
					t0_start = Integer.parseInt(st.nextToken());  // never used
					t0_stop = Integer.parseInt(st.nextToken());
					t0_tof = Integer.parseInt(st.nextToken());
					t1_val = Double.parseDouble(st.nextToken());
					t1_start = Integer.parseInt(st.nextToken());
					t1_stop = Integer.parseInt(st.nextToken());
					t1_tof = Integer.parseInt(st.nextToken());
					t2_val = Double.parseDouble(st.nextToken());
					t2_start = Integer.parseInt(st.nextToken());
					t2_stop = Integer.parseInt(st.nextToken());
					t2_tof = Integer.parseInt(st.nextToken());
					t3_val = Double.parseDouble(st.nextToken());
					t3_start = Integer.parseInt(st.nextToken());
					t3_stop = Integer.parseInt(st.nextToken());
					t3_tof = Integer.parseInt(st.nextToken());
					//pac = (float)Double.parseDouble(st.nextToken());
					//mcp = (float)Double.parseDouble(st.nextToken());
					//String garbage = st.nextToken(); // golden
					//timeStamp = Double.parseLong(st.nextToken());

					// done reading values for this event
					//
					//
					count = 1;  // important - each DE is only one count!

					numTotal++;  // total number of events
				}

				// DONE READING EVENT

				// assume first timestamp is smallest
				if (firstDate) {
					firstTime = (long)timeStamp;
					if (theMain.timeHistBox.isSelected()) {
						for (int k=0; k<timeHist.length; k++) {
							timeHist[k]=new HistogramVector(Float.parseFloat(theMain.timeResField.getText())/24.0f/60.0f);
							timeHist[k].name = "ebin"+k;
						}
					}
					if (theMain.spinTimeBox.isSelected()) {
						spinTimeHist = new SpinTimeHistogram(getDOY(timeStamp), (getDOY(timeStamp)+7.0f), 7*24*4);
					}
					firstDate = false;
				}
				if (timeStamp>lastTime) lastTime=(long)timeStamp;
				//if (firstTime == -1 && numTotal>25) firstTime = timeStamp;
				//if (timeStamp<firstTime) firstTime = timeStamp;
				//if (timeStamp>lastTime) lastTime = timeStamp;


				// CONDITIONS!!
				// filter events here by setting eventOK = false
				//
				if (eventOK & useTimesFile) {
					if (!itl.isValidTime(timeStamp)) eventOK = false;
				}


				if (eventOK & bBM) {
					Date dd = getDate(timeStamp);
					float avg = bmr.getAvg(dd.getTime());
					if (avg>bmMax) {
						eventOK = false;
						bmLost++;
					}
					else bmFound++;
				}

				// tof DE tests - ignore for hb file
				if (!isHBFile) {


					if (!btime) {
						if (t0_tof!= 0) t0_tally++;
						if (t1_tof!= 0) t1_tally++;
						if (t2_tof!= 0) t2_tally++;
						if (t3_tof!= 0) t3_tally++;
					}
					else if (timeStamp<mintime | timeStamp>maxtime) {
						if (t0_tof!= 0) t0_tally++;
						if (t1_tof!= 0) t1_tally++;
						if (t2_tof!= 0) t2_tally++;
						if (t3_tof!= 0) t3_tally++;
					}

					// count up event types
					if (t0_tof!=0 & t1_tof!=0 & t2_tof!=0 & t3_tof!=0) 	ev_trip++;
					else if (t0_tof!=0 & t1_tof==0 & t2_tof==0 & t3_tof==0)  ev_t0++;
					else if (t0_tof==0 & t1_tof!=0 & t2_tof==0 & t3_tof==0)  ev_t1++;
					else if (t0_tof==0 & t1_tof==0 & t2_tof!=0 & t3_tof==0)  ev_t2++;
					else if (t0_tof==0 & t1_tof==0 & t2_tof==0 & t3_tof!=0)  ev_t3++;
					else if (t0_tof==0 & t1_tof==0 & t2_tof==0 & t3_tof==0)  ev_junk++;
					else if (t0_tof==0 & t1_tof!=0 & t2_tof!=0 & t3_tof!=0)  ev_not0++;
					else if (t0_tof!=0 & t1_tof==0 & t2_tof!=0 & t3_tof!=0)  ev_not1++;
					else if (t0_tof!=0 & t1_tof!=0 & t2_tof==0 & t3_tof!=0)  ev_not2++;
					else if (t0_tof!=0 & t1_tof!=0 & t2_tof!=0 & t3_tof==0)  ev_not3++;
					else if (t0_tof==0 & t1_tof==0 & t2_tof!=0 & t3_tof!=0)  ev_0011++;
					else if (t0_tof==0 & t1_tof!=0 & t2_tof==0 & t3_tof!=0)  ev_0101++;
					else if (t0_tof==0 & t1_tof!=0 & t2_tof!=0 & t3_tof==0)  ev_0110++;
					else if (t0_tof!=0 & t1_tof==0 & t2_tof==0 & t3_tof!=0)  ev_1001++;
					else if (t0_tof!=0 & t1_tof==0 & t2_tof!=0 & t3_tof==0)  ev_1010++;
					else if (t0_tof!=0 & t1_tof!=0 & t2_tof==0 & t3_tof==0)  ev_1100++;
					else ev_other++;


					// keep track of events that should be HistogramBin counted:
					if (t0_tof!=0 & t1_tof!=0 & t2_tof!=0 & t3_tof!=0) 	{
						// we have triple coincindence
						if (t2_val<=20 && t2_val>=9) hbH++;
						if (t2_val<=100 && t2_val>=50) hbO++;
					}

					// set up for time histogram
					/*if (theMain.timeHistBox.isSelected()) {
						if (timeStamp<minTime) minTime=timeStamp;
						if (timeStamp>maxTime) maxTime=timeStamp;
					}
					// set up for pos histogram
					if (theMain.posHistBox.isSelected()) {
						if (dec<minDec) minDec=dec;
						if (dec>maxDec) maxDec=dec;
						if (asc<minAsc) minAsc=asc;
						if (asc>maxAsc) maxAsc=asc;
					}*/


					if (t0check) if (t0_tof==0) eventOK = false;
					if (t1check) if (t1_tof==0) eventOK = false;
					if (t2check) if (t2_tof==0) eventOK = false;
					if (t3check) if (t3_tof==0) eventOK = false;
					//double min=0.0; double max = 0.0;
					if (eventOK & b0) {
						if (t0_val<min0 | t0_val>max0) eventOK = false;
					}
					if (eventOK & b1) {
						if (t1_val<min1 | t1_val>max1) eventOK = false;
					}
					if (eventOK & b2) {
						if (t2_val<min2 | t2_val>max2) eventOK = false;
					}
					if (eventOK & b3) {
						if (t3_val<min3 | t3_val>max3) eventOK = false;
					}
					// try the or criterion here
					if (eventOK & (ob1 | ob2 | ob3| ob0)) {
						eventOK = false;
						if (ob0 & t0_val>omin0 & t0_val<omax0) eventOK=true;
						if (ob1 & t1_val>omin1 & t1_val<omax1) eventOK=true;
						if (ob2 & t2_val>omin2 & t2_val<omax2) eventOK=true;
						if (ob3 & t3_val>omin3 & t3_val<omax3) eventOK=true;
					}


					if (eventOK & bcs) {
						double q = t1_val+t2_val-t0_val-t3_val;
						if (q<mincs || q>maxcs) eventOK = false;
					}
					if (eventOK & ghostCheck) {
						float t0 = (float)(t0_val+t3_val/2.0);
						float t1 = (float)(t1_val-t3_val/2.0);
						float t2 = (float)t2_val;

						// two lines in each Tof v Tof plot limit the real particles
						// first t1 v t0
						if (t1>t0*300.0/350.0) eventOK=false;
						if (t1<(t0*170.0/330.0-10.0)) eventOK=false;
						// now t2 v t1
						if (t2>t1*200.0/100.0) eventOK=false;
						if (t2<t1*140.0/350.0) eventOK=false;
						// noew t2 v t0
						if (t2>t0*200.0/300.0) eventOK = false;
						if (t2<t0*100.0/350.0) eventOK = false;
						//if (t2_val>40 & t2_val<46 & t1_val>60 & t0_val>100) eventOK = false; // ghost peak 1
						//else if (Math.abs(t2_val-t0_val)<5) eventOK = false; // ghost peak 2
						//else if (t1_val+t3_val-t2_val<5) eventOK = false; // ghost peak 3
						//else if (Math.abs(t1_val-t3_val)<5) eventOK = false; // ghost peak 4
					}
				}
				// END TOF ONLY EVENT CHECKING
				//if (eventOK & bpac) {
				//	if (pac<minpac | pac>maxpac) eventOK = false;
				//}
				//if (eventOK & theMain.pacCheckBox.isSelected()) {
				//	if (pac<minpac | pac>maxpac) eventOK = false;
				//}



				if (eventOK & btime) {
					if (timeStamp<mintime | timeStamp>maxtime) eventOK = false;
				}
				if (eventOK & belimtime) {
					if (timeStamp>minelimtime & timeStamp<maxelimtime) eventOK = false;
				}
				if (eventOK & bbin) {
					if (ebin!=eBin) eventOK=false;
				}
				//if (eventOK & ebin>6) eventOK = false;

				if (eventOK & basc) {
					if (asc>maxAsc || asc<minAsc) eventOK=false;
				}
				if (eventOK & bdec) {
					if (dec>maxDec || dec<minDec) eventOK=false;
				}
				if (eventOK & bsp) {
					if (theMain.excludeSPBox.isSelected()) {
						if (spinPhase>minSP && spinPhase<maxSP) eventOK = false;
					}
					else if (spinPhase<minSP || spinPhase>maxSP) eventOK = false;
				}
				// hb process..  check for 81 -82 interest otherwise assume tof event type
				if (eventOK & theMain.hb81box.isSelected()) {
					eventOK=false;
					if (is81) eventOK=true;
				}
				else if (eventOK & theMain.hb82box.isSelected()) {
					eventOK=false;
					if (is82) eventOK=true;
				}
				else if (eventOK & isHBFile & requireTriples) {
					if (!trip) eventOK = false;
				}
				else if (eventOK & isHBFile & !doub) eventOK=false;

				//int debug1 = 0;
				if (eventOK ) {
					// the formula is a + b sqrtTOF + c TOF ^ d
					// for tof0 & tof1,2 a=
					//a	5.06	-0.57
					//b	22.3	13.3
					//c	3.42	2.665
					//d	0.965	0.733

					// use quadratic fit
					//a	-1.383	-0.52
					//b	0.0655	0.0646
					//c	0.000383	0.002215

					// NEW FIT FROM SIMPLEX MAY 2011
					/*
					param_0 : -1.8364258945585545
					param_1 : 0.06406039121492375
					param_2 : 8.199967401432645E-4
					param_3 : 9.498307586623096E-6
					param_4 : -0.4966657718042762
					param_5 : 0.07230022569599451
					param_6 : 0.002532010209298256
					param_7 : 8.068746427146595E-6
					param_8 : -0.5465940522653858
					param_9 : 0.06208171061209472
					param_10 : 0.0026868089753490374
					param_11 : 1.1471837460619045E-6

					*/
					double param_0 = -1.8364258945585545;
					double param_1 = 0.06406039121492375 ;
					double param_2 = 8.199967401432645E-4;
					double param_3 = 9.498307586623096E-6;
					double param_4 = -0.4966657718042762 ;
					double param_5 = 0.07230022569599451 ;
					double param_6 = 0.002532010209298256;
					double param_7 = 8.068746427146595E-6;
					double param_8 = -0.5465940522653858 ;
					double param_9 = 0.06208171061209472 ;
					double param_10 = 0.0026868089753490374;
					double param_11 = 1.1471837460619045E-6;

					//double m0 = 5.06+  22.3*Math.sqrt(t0_val)+ 3.42*Math.pow(t0_val,0.965);
					//double m1 = -0.57+ 13.3*Math.sqrt(t1_val)+ 2.665*Math.pow(t1_val,0.733);
					//double m2 = -0.57+ 13.3*Math.sqrt(t2_val)+ 2.665*Math.pow(t2_val,0.733);
					m0 = param_0 + param_1*t0_val + param_2*t0_val*t0_val + param_3*t0_val*t0_val*t0_val;
					m1 = param_4 + param_5*t1_val + param_6*t1_val*t1_val + param_7*t1_val*t1_val*t1_val;
					m2 = param_8 + param_9*t2_val + param_10*t2_val*t2_val + param_11*t2_val*t2_val*t2_val;

					if (debug1<20) System.out.println("masses: " + m0 + " " + m1 + " " + m2);
					debug1++;
				}


				if (eventOK & useMass) {
					if (Math.abs(m0-m1)>massResolution | Math.abs(m0-m2)>massResolution | Math.abs(m1-m2)>massResolution) eventOK=false;
				}




				//
				//
				//
				// done conditions..  let's process:
				if (!isHBFile) {
					if (eventOK & histCheck) {
						//numAccepted++;
						//t0he.addEvent((float)t0_val);
						//t1he.addEvent((float)t1_val);
						//t2he.addEvent((float)t2_val);


						//double m0 = -1.383 + 0.0655*t0_val + 0.000383*t0_val*t0_val;
						//if (useMass) {
							massh.addEvent((float)(m0));
							mass0h.addEvent((float)(m0));
							//double m1 = -0.52 + 0.0646*t1_val + 0.002215*t1_val*t1_val;
							massh.addEvent((float)(m1));
							mass1h.addEvent((float)(m1));
							//double m2 = -0.52 + 0.0646*t2_val + 0.002215*t2_val*t2_val;
							massh.addEvent((float)(m2));
							mass2h.addEvent((float)(m2));
						//}

						t0h.addEvent((float)(t0_val+t3_val/2.0));
						t1h.addEvent((float)(t1_val-t3_val/2.0));
						t2h.addEvent((float)t2_val);
						t3h.addEvent((float)t3_val);
						csh.addEvent((float)(t1_val+t2_val-t0_val-t3_val));
					}

					if (eventOK) {
						if (0<t3_val   && t3_val<2.5) sec0++;
						else if (2.5<t3_val && t3_val<6) sec1++;
						else if (6<t3_val   && t3_val<10) sec2++;
						else if (10<t3_val  && t3_val<14) sec3++;
						else sec4++;
					}

					if (eventOK & scatterCheck) {

						tof0.add(new Double(t0_val+t3_val/2.0));
						tof1.add(new Double(t1_val-t3_val/2.0));
						tof2.add(new Double(t2_val));
						tof3.add(new Double(t3_val));
					}
					if (eventOK) {
						numAccepted++;
						if (isHydrogen(t0_val,t1_val,t2_val,t3_val)) {
							rates1++;
							ratesH[ebin-1]++;
						}
						if (isMassEight(t0_val,t1_val,t2_val,t3_val)) rates2++;
						if (isOxygen(t0_val,t1_val,t2_val,t3_val)) {
							rates3++;
							ratesO[ebin-1]++;
						}
					}
				}

				if (eventOK & spinCheck) {
					spinHist.addEvent(spinPhase,count);
					spinHiste[ebin-1].addEvent(spinPhase,count);
				//	spinHist.addEvent(spinPhase);
					// LETS  also add this data to our spinPhase binned Histograms (TOF2)
					// first see what bin to put it in..  there are 180
					if (theMain.histogramCheckBox.isSelected()) {
						int spTofBin = (int)(spinPhase*180);
						if (spTofBin<0 | spTofBin>179 ) System.out.println("spTofBin PROBLEM YO");
						spinTof2[spTofBin].addEvent((float)t2_val); // that should add the event..
					}
				}

				if (eventOK & spinTimeCheck) {
					spinTimeHist.addEvent((float)getDOY(timeStamp),spinPhase,count);
				}


				//if (theMain.mcpScanBox.isSelected()) {
					//System.out.println(mcpData[0].ct2);
					//System.out.println(mcpData[0].minV);
					/*int bin2 = mcpBin(mcp);
					if (t0_tof!=0) mcpData[bin2].ct0++;
					if (t1_tof!=0) mcpData[bin2].ct1++;
					if (t2_tof!=0) mcpData[bin2].ct2++;
					if (t3_tof!=0) mcpData[bin2].ct3++;

					if (t0_tof!=0 & t0_tof!=0 & t0_tof!=0 & t0_tof!=0) {
						double q = t1_val+t2_val-t0_val-t3_val;
						if (q>mincs & q<maxcs) mcpData[bin2].cTriples++;
					}

					if (timeStamp < mcpData[bin2].startTime) mcpData[bin2].startTime=timeStamp;
					if (timeStamp > mcpData[bin2].endTime) mcpData[bin2].endTime=timeStamp;
					*/
				//}

				if (eventOK & timeHistCheck) {
					timeHist[ebin-1].addEvent(getDOY(timeStamp),count);
					//timeHist2[ebin-1].addEvent(getDOY(timeStamp),count);
				}

				if (eventOK & posHistCheck) {
					posHist.addEvent((float)dec,(float)asc);
				}

				if (eventOK & eHistCheck) {
					ebinCounter[ebin-1]+=count;
				}


			}

			if (!theMain.binaryFileBox.isSelected()) files[i].closeRead();
		}

		// change all zeros in the histogram to ones - to make log plots possible
		/*if (theMain.histogramCheckBox.isSelected()) {
			for (int i=0; i<t0h.numberOfBins; i++) {
				if (t0h.data[i]==0) t0h.data[i]=1;
				if (t1h.data[i]==0) t1h.data[i]=1;
				if (t2h.data[i]==0) t2h.data[i]=1;
				if (t3h.data[i]==0) t3h.data[i]=1;
				if (csh.data[i]==0) csh.data[i]=1;
			}
		}*/

		deltaT = lastTime-firstTime;
		if (theMain.timeCheckBox.isSelected()) try {
			mintime = (long)Double.parseDouble(theMain.timeMinField.getText());
			maxtime = (long)Double.parseDouble(theMain.timeMaxField.getText());
			deltaT = maxtime-mintime;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("couldn't parse times!!! "+theMain.timeMaxField.getText());
		}



		//outputCatalog();

		//  OUTPUT FUNCTIONS NOW
		//
		// output to the file - - -
		if (!theMain.catalogCheckBox.isSelected()) {
			file outf = new file(outfName);
			outf.initWrite(false);
			outf.write("IBEX-LO TOF GSE-PHA Out Results\n");
			outf.write("Processed file: " + theMain.inFileField.getText()+"\n");
			outf.write("This file: " + theMain.outFileField.getText()+"\n");
			outf.write("LastTime: "+ lastTime+"\t"+getDOY(lastTime)+"\n");
			outf.write("FirstTime: "+ firstTime+"\t"+getDOY(firstTime)+"\n");
			outf.write("Delat T in file: " + deltaT + "\n");
			outf.write("Total Events: " + numTotal + " \nPassing filter: " + numAccepted +"\n\n");
			outf.write("Valid TOF0 \t" + t0_tally + "\trate: " + ((float)t0_tally/deltaT)+"\n");
			outf.write("Valid TOF1 \t" + t1_tally + "\trate: " + ((float)t1_tally/deltaT)+"\n");
			outf.write("Valid TOF2 \t" + t2_tally + "\trate: " + ((float)t2_tally/deltaT)+"\n");
			outf.write("Valid TOF3 \t" + t3_tally + "\trate: " + ((float)t3_tally/deltaT)+"\n");
			if (bBM) {
				outf.write("BM lost: " + bmLost + "\n");
				outf.write("BM found: " + bmFound +"\n");
				outf.write("Ratio: " + ((double)bmFound/(double)(bmLost+bmFound)) +"\n");
			}
			outf.write("Oxygen: " + rates3+"\n");
			outf.write("Approximate Counts by Species: \n");
			outf.write("Hydrogen: " + rates1+"\n");
			outf.write("Mass8: " + rates2+"\n");
			outf.write("Oxygen: " + rates3+"\n");
			outf.write("\n\n");
			outf.write("Approximate Counts by Species by E Bin \t H \t O: \n");
			for (int i=0; i<ratesH.length; i++) outf.write("EBin "+ (i+1) + "\t" + ratesH[i] + "\t" + ratesO[i] + "\n");
			outf.write("\n\n");
			outf.write("Histogram Bin criteria \n");
			outf.write("HB H: " + count81 + "\n");
			outf.write("HB O: " + count82 + "\n");
			outf.write("HB tot: "  + (count81+count82) + "\n");
			outf.write("TOF3 Sector counts: "+sec0+" - "+sec1+" - "+sec2+" - "+sec3+" - "+sec4);
			outf.write("\n\n");
			outf.write("Event types (without filter): \n");
			outf.write("(valid tof0, tof1, tof2, tof3) \n");
			outf.write("1111 \t" + ev_trip + "\n");
			outf.write("0111 \t" + ev_not0 + "\n");
			outf.write("1011 \t" + ev_not1 + "\n");
			outf.write("1101 \t" + ev_not2 + "\n");
			outf.write("1110 \t" + ev_not3 + "\n");
			outf.write("1100 \t" + ev_1100 + "\n");
			outf.write("1010 \t" + ev_1010 + "\n");
			outf.write("1001 \t" + ev_1001 + "\n");
			outf.write("0110 \t" + ev_0110 + "\n");
			outf.write("0101 \t" + ev_0101 + "\n");
			outf.write("0011 \t" + ev_0011 + "\n");
			outf.write("1000 \t" + ev_t0 + "\n");
			outf.write("0100 \t" + ev_t1 + "\n");
			outf.write("0010 \t" + ev_t2 + "\n");
			outf.write("0001 \t" + ev_t3 + "\n");
			outf.write("0000 \t" + ev_junk + "\n");
			outf.write("other \t" + ev_other + "\n\n");
			outf.write("Filter Selections: \n");
			if (theMain.tof0CheckBox.isSelected()) outf.write("TOF0 Required.  TOF0_min: " +
						theMain.tof0MinField.getText() +"\t TOF0_max: " +
						theMain.tof0MaxField.getText() + "\n");
			if (theMain.tof1CheckBox.isSelected()) outf.write("TOF1 Required.  TOF1_min: " +
						theMain.tof1MinField.getText() +"\t TOF1_max: " +
						theMain.tof1MaxField.getText() + "\n");
			if (theMain.tof2CheckBox.isSelected()) outf.write("TOF2 Required.  TOF2_min: " +
						theMain.tof2MinField.getText() +"\t TOF2_max: " +
						theMain.tof2MaxField.getText() + "\n");
			if (theMain.tof3CheckBox.isSelected()) outf.write("TOF3 Required.  TOF3_min: " +
						theMain.tof3MinField.getText() +"\t TOF3_max: " +
						theMain.tof3MaxField.getText() + "\n");
			if (theMain.checkSumCheckBox.isSelected()) outf.write("Checksum Required.  CS_min: " +
						theMain.checkSumMinField.getText() +"\t cs_max: " +
						theMain.checkSumMaxField.getText() + "\n");
			if (theMain.pacCheckBox.isSelected()) outf.write("PAC Required.  PAC_min: " +
						theMain.pacMinField.getText() +"\t PAC_max: " +
						theMain.pacMaxField.getText() + "\n");
			if (theMain.timeCheckBox.isSelected()) outf.write("TimeStamp Required.  time_min: " +
						theMain.timeMinField.getText() +"\t time_max: " +
						theMain.timeMaxField.getText() + "\n");
			outf.write("\n\n\n");
			if (theMain.histogramCheckBox.isSelected()) {
				// first lets output any subtracted HE results
				if (theMain.subtractHeBox.isSelected()) {

					// first we make arrays of data > 40ns and < 200 ns
					double[] t2labelsSub = t2h.getLabelsRange(40.0,200.0);
					double[] t2dataSub = t2h.getDatasRange(40.0,200.0);
					System.out.println("got O array size: "+ t2dataSub.length);

					// now do the fit
					CurveFitter cf = new CurveFitter(t2labelsSub,t2dataSub);
					cf.doFit(CurveFitter.HE_FIT);
					double fitParam = cf.bestParams[0];
					System.out.println("got best fit: "+ fitParam);

					// now do the subtraction
					double[] newT2data = new double[t2h.data.length];
					double[] onlyHeT2data = new double[t2h.data.length];
					for (int i=0; i<t2h.label.length; i++) {
						newT2data[i]= t2h.data[i]-fitParam*hsf.getSpec(t2h.label[i]);
						onlyHeT2data[i]= t2h.data[i]-newT2data[i];
					}

					// now output the results
					outf.write("HE SUB TOF2 SPECTRA \n" );
					outf.write("tof2 (ns) \t onlyH \t onlyHe \t allCounts" );
					for (int i=0; i<t2h.numberOfBins; i++) {
						outf.write(t2h.label[i]+"\t"+newT2data[i]+"\t"+onlyHeT2data[i]+"\t"+t2h.data[i]+"\n");
					}
					outf.write("\n\n\n");
				}

				outf.write("TOF Histogram Data\n");
				outf.write("tof (ns)\tTOF0 counts\tTOF1 counts\tTOF2 counts\tTOF3 counts\tchecksum\tcounts\n ");
				for (int i=0; i<t0h.numberOfBins; i++) {
					outf.write(t0h.label[i]+"\t"+t0h.data[i]+"\t"+
								+t1h.data[i]+"\t"+t2h.data[i]+"\t");
					if (i<t3h.data.length) outf.write(t3h.label[i]+"\t"+t3h.data[i]+"\t"+csh.label[i]+"\t"+csh.data[i]+"\n");
					else outf.write("\n");
				}
				outf.write("\n\n\n");

				//if (useMass) {
					outf.write("TOF Histogram Data by Mass \n");
					outf.write("mass (amu)\tMass0 counts\tMass1 counts\tMass2 counts\tMassTotal counts\tchecksum\tcounts\n ");
					for (int i=0; i<t0h.numberOfBins; i++) {
						outf.write(massh.label[i]+"\t"+mass0h.data[i]+"\t"+
									+mass1h.data[i]+"\t"+mass2h.data[i]+"\t"+massh.data[i]+"\n");
					}
					outf.write("\n\n\n");
				//}
				/*
				outf.write("TOF Histogram EXACT Data\n");
				outf.write("tof (ns)\tTOF0 counts\n ");
				t0he.finalize();
				t1he.finalize();
				t2he.finalize();
				for (int i=0; i<t0he.data.length; i++) outf.write(t0he.label[i]+"\t"+t0he.data[i]+"\n");
				outf.write("\n\n\n");
				outf.write("tof (ns)\tTOF1 counts\n ");
				for (int i=0; i<t1he.data.length; i++) outf.write(t1he.label[i]+"\t"+t1he.data[i]+"\n");
				outf.write("\n\n\n");
				outf.write("tof (ns)\tTOF2 counts\n ");
				for (int i=0; i<t2he.data.length; i++) outf.write(t2he.label[i]+"\t"+t2he.data[i]+"\n");
				*/
				outf.write("\n\n\n");
			}
			if (theMain.scatterCheckBox.isSelected()) {
				int q = tof0.size();
				outf.write("TOF 4D scatter data\n");
				outf.write("TOF0(ns)\tTOF1(ns)\tTOF2(ns)\tTOF3(ns)\n");
				for (int i=0; i<q; i++) {
					outf.write(tof0.elementAt(i)+"\t"+tof1.elementAt(i)+
							"\t"+tof2.elementAt(i)+"\t"+tof3.elementAt(i)+"\n");
				}
				outf.write("\n\n\n");
			}

			if (theMain.mcpScanBox.isSelected()) {
				outf.write("MCP raw restults: \n");
				outf.write("Voltage\tTOF0 rate\tTOF1 rate\tTOF2 rate\tTriples rate\tDeltaT\n");
				for (int i=0; i<numMcpBins; i++) {
					mcpData[i].finalize();
					outf.write(mcpData[i].minV+"\t"+mcpData[i].ct0+"\t"+
							+mcpData[i].ct1+"\t"+mcpData[i].ct2+"\t"+mcpData[i].ct3+
							"\t"+mcpData[i].cTriples+"\t"+mcpData[i].deltaT+"\n");
				}
				outf.write("\n\n");
				outf.write("MCP ratios: \n");
				outf.write("Voltage\tTriples/TOF0\tTriples/TOF1\tTriples/TOF2\tTriples/TOF3\n");
				for (int i=0; i<numMcpBins; i++) {
					float gold = mcpData[i].cTriples;
					outf.write(mcpData[i].minV+"\t"+gold/mcpData[i].ct0+"\t"+
							+gold/mcpData[i].ct1+"\t"+gold/mcpData[i].ct2+"\t"+
							gold/mcpData[i].ct3+"\n");
				}
			}

			if (theMain.timeHistBox.isSelected()) {
				outf.write("Time Histogram results: \n");
/*
				outf.write("Summed energies here, below for by energy \n");
				outf.write("time \t counts\n");
				for (int i=0; i<timeHist[0].numberOfBins; i++) {
					outf.write(timeHist[0].label[i]+"\t");
					int summation = 0;
					for (int j=0; j<8; j++) {
						summation+=timeHist[j].data[i];
					}
					outf.write(summation+"\t");
					outf.write("\n");
				}
*/


				outf.write("Time Histogram results: \n");
				outf.write("time \t counts\n");
				for (int j=0; j<8; j++) {
					outf.write("Now output for energy bin: " + j + "\n");
					outf.write("bin size: "+ timeHist[j].numberOfBins + "\n");
					for (int i=0; i<timeHist[j].numberOfBins; i++) {
						outf.write(timeHist[j].label[i]+"\t");
						outf.write(timeHist[j].data[i]+"\t");
						outf.write("\n");
						//outf.write(((timeHist.label[i]-timeHist.label[0])/60/60)+"\t"+timeHist.data[i]+"\n");
					}
				}
				outf.write("\n\n");
/*
				outf.write("Time Histogram 2 results: \n");
				outf.write("time \t counts\n");
				for (int i=0; i<timeHist2[0].numberOfBins; i++) {
					outf.write(timeHist2[0].label[i]+"\t");
					for (int j=0; j<8; j++) {
						outf.write(timeHist2[j].data[i]+"\t");
					}
					outf.write("\n");
				}
*/
				outf.write("\n\n");
			}

			if (theMain.spinBox.isSelected()) {

				// add new output to show TOF spectrum per spin bin...
				if (theMain.subtractHeBox.isSelected()) {
					outf.write("Here is the spin-binned TOF info"+ "\n\n");
					outf.write("SP BIN - - HE SUB TOF2 H COUNTS \n" );
					int[] spSubHCounts = new int[spinTof2.length];
					int[] spOnlyHeCounts = new int[spinTof2.length];
					for (int i=0; i<180; i++) {

						// first we make arrays of data > 40ns and < 200 ns
						double[] t2labelsSub = spinTof2[i].getLabelsRange(40.0,200.0);
						double[] t2dataSub = spinTof2[i].getDatasRange(40.0,200.0);

						System.out.println("got O array size: "+ t2dataSub.length);
						// now do the fit
						CurveFitter cf = new CurveFitter(t2labelsSub,t2dataSub);
						cf.doFit(CurveFitter.HE_FIT);
						double fitParam = cf.bestParams[0];
						System.out.println("got best fit: "+ fitParam);
						// now do the subtraction
						double[] newT2data = new double[spinTof2[i].data.length];
						double[] newT2Hedata = new double[spinTof2[i].data.length];
						for (int j=0; j<spinTof2[i].label.length; j++) {
							newT2data[j]= spinTof2[i].data[j]-fitParam*hsf.getSpec(spinTof2[i].label[j]);
							newT2Hedata[j] = spinTof2[i].data[j] - newT2data[j];
						}

						// we have the subtracted labels.. now add up the H counts..
						spSubHCounts[i] = 0;
						spOnlyHeCounts[i] = 0;
						for (int j=0; j<spinTof2[i].label.length; j++) {
							if (spinTof2[i].label[j]>7 && spinTof2[i].label[j]<30) {
								spSubHCounts[i]+=newT2data[j];
								spOnlyHeCounts[i]+=newT2Hedata[j];
							}
						}

						// ok lets output the results here
						outf.write(i+"\t"+(spOnlyHeCounts[i]+spSubHCounts[i])+"\t" + spSubHCounts[i]+"\t"+spOnlyHeCounts[i]+"\n");

					}
				}
				outf.write("\n\n\n");

				outf.write("Spin Phase Histogram Results: \n");
				for (int i=0; i<spinHist.numberOfBins; i++) {
					outf.write(spinHist.label[i]+"\t"+spinHist.data[i]+"\n");
				}
				outf.write("\n Spin Phase Histogram by Energy 1 2 3 4 5 6 7 8 \n");
				for (int i=0; i<spinHist.numberOfBins; i++) {
					outf.write(spinHist.label[i]+"\t");
					for (int j=0; j<8; j++) {
						outf.write((spinHiste[j].data[i])+"\t");
					}
					outf.write("\n");
				}
				outf.write("\n\n");

				// lets also throw a plot up
				JColorGraph jcg;
				double[] y,z;
				y=new double[spinHiste[0].data.length];
				double[] x = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};

				for (int i=0; i<y.length; i++) {
					y[i]=spinHiste[0].label[i];
				}
				z=new double[x.length*y.length];
				int indexZ = 0;
				for (int i=0; i<8; i++) {
					for (int j=0; j<spinHiste[0].data.length; j++) {
						z[indexZ]=spinHiste[i].data[j];
						indexZ++;
					}
				}
/*
				//jcg = new JColorGraph(x,y,z,false);
				jcg = new JColorGraph(x,y,z);

				String unitString = "Total Triple Coincidence";
				jcg.setLabels("IBEX-LO","2009",unitString);

				jcg.run();
				jcg.showIt();
*/
			}

			if (theMain.posHistBox.isSelected()) {
				outf.write("sky map results: \n");
				for (int i=0; i<posHist.numberOfBins; i++) {
					outf.write(posHist.labelX[i]+"\t");
				}
				outf.write("\n");
				for (int i=0; i<posHist.numberOfBins; i++) {
					outf.write(posHist.labelY[i]+"\t");
				}
				outf.write("\n");
				for (int i=0; i<posHist.numberOfBins; i++) {
					for (int j=0; j<posHist.numberOfBins; j++) {
						outf.write(posHist.data[i].data[j]+"\t");
					}
					outf.write("\n");
				}

				// lets also throw a plot up
				JColorGraph jcg;
				double[] x,y,z;
				x=new double[posHist.data.length];
				y=new double[posHist.data.length];
				z=posHist.getArray();
				for (int i=0; i<posHist.data.length; i++) {
					x[i]=(double)posHist.labelX[i];
					y[i]=(double)posHist.labelY[i];
				}
				//jcg = new JColorGraph(x,y,z,false);
				jcg = new JColorGraph(x,y,z);

				String unitString = "Total Triple Coincidence";
				jcg.setLabels("IBEX-LO","2009",unitString);

				jcg.run();
				jcg.showIt();
			}
			if (theMain.spinTimeBox.isSelected()) {
				outf.write("spin phase time results: \n");
				for (int i=0; i<spinTimeHist.numberOfBins; i++) {
					outf.write(spinTimeHist.labelX[i]+"\t");
				}
				outf.write("\n");
				for (int i=0; i<spinTimeHist.data[0].numberOfBins; i++) {
					outf.write(spinTimeHist.labelY[i]+"\t");
				}
				outf.write("\n");
				for (int i=0; i<spinTimeHist.numberOfBins; i++) {
					for (int j=0; j<spinTimeHist.data[0].numberOfBins; j++) {
						outf.write(spinTimeHist.data[i].data[j]+"\t");
					}
					outf.write("\n");
				}

				// lets also throw a plot up
				JColorGraph jcg;
				double[] x,y,z;
				x=new double[spinTimeHist.data.length];
				y=new double[spinTimeHist.data[0].numberOfBins];
				z=spinTimeHist.getArray();
				for (int i=0; i<spinTimeHist.data.length; i++) {
					x[i]=(double)spinTimeHist.labelX[i];
				}
				for (int i=0; i<spinTimeHist.data[0].numberOfBins; i++) {
					y[i]=(double)spinTimeHist.labelY[i];
				}
				//jcg = new JColorGraph(x,y,z,false);
				jcg = new JColorGraph(x,y,z);

				String unitString = "Spin Phase Histogram";
				jcg.setLabels("IBEX-LO","2009",unitString);
				jcg.xLabel = "DOY";
				jcg.yLabel = "Spin Phase";

				jcg.run();
				jcg.showIt();
			}
			if (theMain.eHistogramBox.isSelected()) {
				outf.write("\n");
				outf.write("Energy histogram results: \n");
				for (int i=0; i<ebinCounter.length; i++) {
					outf.write(i+"\t"+ebinCounter[i]+"\n");
				}
				outf.write("\n");
			}



			outf.closeWrite();
		}
		System.out.println("time passed: " + (lastTime-firstTime));
		System.out.println("total: " + numTotal + " acc.: " + numAccepted);
	}


	//		t0h.addEvent((float)(t0_val+t3_val/2.0));
	//		t1h.addEvent((float)(t1_val-t3_val/2.0));

	public boolean isHydrogen(double t0, double t1, double t2, double t3) {
		if (t0+t3/2.0   < 20 | t0+t3/2.0    > 60) return false;
		if (t1-t3/2.0 	< 8  | t1-t3/2.0	> 50) return false;
		if (t2    		< 8  | t2    		> 40) return false;
		return true;
	}

	public boolean isOxygen(double t0, double t1, double t2, double t3) {
		if (t0+t3/2.0   < 100 ) return false;
		if (t1-t3/2.0	< 50  ) return false;
		if (t2    		< 50  ) return false;
		return true;
	}

/*	public boolean isOxygen(double t0, double t1, double t2, double t3) {
		if (t0+t3/2.0   < 100 | t0+t3/2.0   > 300) return false;
		if (t1-t3/2.0	< 50  | t1-t3/2.0 	> 300) return false;
		if (t2    		< 50  | t2    		> 300) return false;
		return true;
	}
*/

	public boolean isMassEight(double t0, double t1, double t2, double t3) {
		if (t0    < 70 | t0    > 100) return false;
		if (t1-t3 < 40 | t1-t3 > 60) return false;
		if (t2    < 30 | t2    > 50) return false;
		return true;
	}

	/**
	*  This determines the appropriate index of the mcp bin for the given (float) mcp voltage
	*/
	public int mcpBin(float v) {
		float mcp_min = 2700.0f;
		float mcp_max = 3300.0f;
		if (v>mcp_max) return numMcpBins-1;
		if (v<mcp_min) return 0;
		float tbr = (v-mcp_min)/(mcp_max - mcp_min);
		//System.out.println("called mcpBin( " + v + ") yielding: " + (tbr*numMcpBins));
		return (int)(tbr * numMcpBins);
	}


// LAUSANNE-

 // loft
 // D
 // the cult


	/**
	* Here create the pages of the catalog from the arrays we've made
	*
	*  Some problem here, we may need to do some manual garbage collection
	*/
	public String catalogFilePrefix = "c:\\0ibex\\catalog2\\";
	public int catalogPageNumber=0;
	private NOAAPanelPlotFrame tbr;
	private NOAALineFrame nlf, nlf2, nlf3, nlf4;
	private void outputCatalog() {
		System.gc();
		o("starting outputCatlog()");
		String pageFileName = catalogFilePrefix + "_" + catalogPageNumber;
		catalogPageNumber++; // might as well do it now

		// each page will have 5 panels, 1 a color time
		tbr = new NOAAPanelPlotFrame();

		// build the arrays, to avoid Vectors
		double[] ca_tof0 = new double[t0h.numberOfBins];
		double[] ca_tof1 = new double[t1h.numberOfBins];
		double[] ca_tof2 = new double[t2h.numberOfBins];
		double[] ca_tof3 = new double[t3h.numberOfBins];
		double[] ca_tof0_label = new double[t0h.numberOfBins];
		double[] ca_tof1_label = new double[t1h.numberOfBins];
		double[] ca_tof2_label = new double[t2h.numberOfBins];
		double[] ca_tof3_label = new double[t3h.numberOfBins];
		for (int i=0; i<ca_tof0.length; i++) ca_tof0[i]=(double)(t0h.data[i]);
		for (int i=0; i<ca_tof1.length; i++) ca_tof1[i]=(double)(t1h.data[i]);
		for (int i=0; i<ca_tof2.length; i++) ca_tof2[i]=(double)(t2h.data[i]);
		for (int i=0; i<ca_tof3.length; i++) ca_tof3[i]=(double)(t3h.data[i]);
		for (int i=0; i<ca_tof0_label.length; i++) ca_tof0_label[i]=(double)(t0h.label[i]);
		for (int i=0; i<ca_tof1_label.length; i++) ca_tof1_label[i]=(double)(t1h.label[i]);
		for (int i=0; i<ca_tof2_label.length; i++) ca_tof2_label[i]=(double)(t2h.label[i]);
		for (int i=0; i<ca_tof3_label.length; i++) ca_tof3_label[i]=(double)(t3h.label[i]);

		// OK, let's make some line plots here for y'all
		//CartesianGraph cg;
		//NOAALineFrame nlf;
		nlf = new NOAALineFrame(ca_tof0_label,ca_tof0,theMain.inFileField.getText(),"","TOF0 Counts","");
		CartesianGraph cg = (CartesianGraph)nlf.lpl.getFirstLayer().getGraph();
		//cg.removeAllXAxes();
		tbr.addPanel(cg, 2.0f);

		nlf2 = new NOAALineFrame(ca_tof1_label,ca_tof1,"TOF1","ns","Counts","");
		CartesianGraph cg2 = (CartesianGraph)nlf2.lpl.getFirstLayer().getGraph();
		//cg2.removeAllXAxes();
		tbr.addPanel(cg2, 2.0f);

		nlf3 = new NOAALineFrame(ca_tof2_label,ca_tof2,"TOF2","ns","Counts","");
		CartesianGraph cg3 = (CartesianGraph)nlf3.lpl.getFirstLayer().getGraph();
		//cg3.removeAllXAxes();
		tbr.addPanel(cg3, 2.0f);

		nlf4 = new NOAALineFrame(ca_tof3_label,ca_tof3,"TOF3","elapsed time: "+deltaT,"Counts","");
		CartesianGraph cg4 = (CartesianGraph)nlf4.lpl.getFirstLayer().getGraph();
		//cg3.removeAllXAxes();
		tbr.addPanel(cg4, 2.0f);



		// third thing added goes to the same graph as 2nd w/ no new axis
	/*	try {cg = (CartesianGraph)nlf.lpl.getLayerFromDataId("Line 2").getGraph();}
		catch(Exception e){e.printStackTrace();}
		cg.removeAllXAxes();
		cg.removeAllYAxes();
		tbr.addPanel(cg, 0.0f);

		// this one is just the zero line for reference in the double line plot
		try {cg = (CartesianGraph)nlf.lpl.getLayerFromDataId("Line 3").getGraph();}
		catch(Exception e){e.printStackTrace();}
		sgtdata = nlf.lpl.getData("Line 3");
		la = new LineAttribute(LineAttribute.DASHED, Color.blue);
		cg.setData(sgtdata,la);
		cg.removeAllXAxes();
		cg.removeAllYAxes();
		tbr.addPanel(cg, 0.0f);
		nlf.dispose();
	//	nlf.finalize();

		nlf = new NOAALineFrame(ca_date,ca_bangle,"","","B ^","deg");
		cg = (CartesianGraph)nlf.lpl.getFirstLayer().getGraph();
		cg.removeAllXAxes();
		tbr.addPanel(cg, 1.0f);
		nlf.dispose();
	//	nlf.finalize();

		nlf = new NOAALineFrame(ca_date,ca_vth,"","","V_T","km/s");
		cg = (CartesianGraph)nlf.lpl.getFirstLayer().getGraph();
		sgtdata = nlf.lpl.getData("Line 1");
		la = new LineAttribute(LineAttribute.SOLID, Color.red);
		cg.setData(sgtdata,la);
		cg.removeAllXAxes();
		tbr.addPanel(cg, 1.0f);
		nlf.dispose();

		// let's make the mtof data red for the catalog
		nlf = new NOAALineFrame(ca_date,ca_np,"","","Np","cm^-3");
		cg = (CartesianGraph)nlf.lpl.getFirstLayer().getGraph();
		// we need the SGTData
		sgtdata = nlf.lpl.getData("Line 1");
		la = new LineAttribute(LineAttribute.SOLID, Color.red);
		cg.setData(sgtdata,la);
		cg.removeAllXAxes();
		tbr.addPanel(cg, 1.0f);
		nlf.dispose();

		nlf = new NOAALineFrame(ca_date,ca_vsw,"","","Vsw","km/s");
		cg = (CartesianGraph)nlf.lpl.getFirstLayer().getGraph();
		sgtdata = nlf.lpl.getData("Line 1");
		la = new LineAttribute(LineAttribute.SOLID, Color.red);
		cg.setData(sgtdata,la);
		cg.removeAllXAxes();
		tbr.addPanel(cg, 1.0f);
		nlf.dispose();
		*/

		//nlf.dispose();
		tbr.build();
		tbr.pack();


		if (theMain.catalogCheckBox.isSelected()) {
			tbr.save(theMain.inFileField.getText()+"_ql");
			nlf.dispose();
			nlf2.dispose();
			nlf3.dispose();
			nlf4.dispose();
		}

		//try {tbr.finalize();}
		//catch (Throwable e) {e.printStackTrace();}
		// that should also dispose tbr after saving - now recoup memory if nec.
		System.gc();
		o("done outputCatalog");
	}

	/*private void setupTimeProfile() {
		// we need to work in DOY here
		//int secsInDay = 60*60*24;
		//System.out.println(secsInDay);
		o("starting setupTimePRofile");
		try {
			tpStart = (int)( 24*60*60*Float.parseFloat(theMain.timeStartField.getText()) )+ GOODS;
			tpFinish = (int)( 24*60*60*Float.parseFloat(theMain.timeEndField.getText()) )+ GOODS;
		}
		catch (Exception e) {
			o("using all data for time profile");
			tpStart = (int)( 24*60*60*81 )+ GOODS;
			tpFinish = (int)( 24*60*60*240 )+ GOODS;
		}
	}*/

	public static final void o(String s) {
		System.out.println(s);
	}

	public float getDOY(double gp_t) {
		return getDOY((float)gp_t);
	}

	public float getDOY(float gpsTime) {
		float tbr = 0.0f;
		float numSecs = gpsTime - 913591566;
		float numDays = numSecs/24/60/60;
		tbr = 352.97629630f + numDays;
		//if (tbr>366) tbr=tbr-366;
		return tbr;
	}

	public Date getDate(double gpsTime) {
		c = Calendar.getInstance();
		c.setTimeZone(tz);
		c.set(Calendar.YEAR, 2008);
		c.set(Calendar.DAY_OF_YEAR, 352);
		double hour = 0.97629360/24.0;
		c.set(Calendar.HOUR, getHour(hour));
		c.set(Calendar.MINUTE, getMinute(hour));
		c.set(Calendar.SECOND, getSecond(hour));
		//352.97629630  2008 = 913591566
		Date d = c.getTime();
		long tbr = d.getTime();
		tbr += (gpsTime-913591566.0)*1000;
		d.setTime(tbr);
		return d;
	}

	public float getGpsTime(float doy) {
		float tbr =0.0f;
		float dif = doy-(float)352.97629630; // 2008
		//if (doy<300)	dif+=366;  // we are in 2009
		dif = dif*24*60*60;
		tbr = dif + 913591566;
		o("tried to convert: " + doy + " to " + tbr);
		return tbr;

		//352.97629630  2008
	}

	public static int getHour(double hour) {
			return (int)(hour);
		}

	public static int getMinute(double hour) {
		double minutes = hour*60.0;
		// subtract hours
		double iHour = getHour(hour)*60.0;
		minutes = minutes - iHour;
		return (int)(minutes);
	}


	public static int getSecond(double hour) {
		double seconds = hour*3600.0;
		double iHour = getHour(hour)*3600.0;
		double iMinute = getMinute(hour)*60.0;
		// subtract hours and minutes
		seconds = seconds - iHour - iMinute;
		//System.out.println("rem: "+ rem);
		return (int)(seconds);
	}


	public static final void main(String[] args) {
		// for testing...
	   // GSE_ep ge = new GSE_ep();
	}
}


/*
  0123                                             good bits per event
    ----                                             -------------------
    0000     Triple (everything is good)                          42 +
    1000  -  Triple (=> a,b0,b3,c; TOF0 invalid)                  32
    0100  -  Triple (=> a,b0,b3,c; TOF1 invalid)                  33
    0010  -  Triple (=> a,b0,b3,c; TOF2 invalid)                  33
    0001  *  Triple (=> a,b0,   c; TOF3 invalid)                  40
    1100  -  Triple (=> a,b0,b3,c; TOF0 & TOF1 invalid)           23
    1010     Double (=>   b0,b3,c; TOF0 & TOF2 invalid)           23 +
    1001  *  Triple (=> a,b0,   c; TOF0 & TOF3 invalid)           30
    0110     Double (=> a,b0,b3  ; TOF1 & TOF2 invalid)           24 +
    0101  *  Triple (=> a,b0,   c; TOF1 & TOF3 invalid)           31
    0011  *  Triple (=> a,b0,   c; TOF2 & TOF3 invalid)           31
    1110  x  Single (=>   b0,b3  ; only TOF3 valid)               14
    1101     Double (=> a,      c; only TOF2 valid)               21 +
    1011  *  Double (=>   b0,   c; only TOF1 valid)               21
    0111  *  Double (=> a,b0     ; only TOF0 valid)               22
    1111  x  Junk   (nothing valid)                               12


*/

//#define ME_MTYPE_LO_START_A     0xC0    /* start/stop rates */
//#define ME_MTYPE_LO_START_C     0xC1    /* start/stop rates */
//#define ME_MTYPE_LO_POS_B0      0xC2    /* start/stop rates */
//#define ME_MTYPE_LO_POS_B3      0xC3    /* start/stop rates */
//#define ME_MTYPE_LO_TOF_AB_0    0xC4    /* tof_* rates */
//#define ME_MTYPE_LO_TOF_CB_1    0xC5    /* tof_* rates */
//#define ME_MTYPE_LO_TOF_AC_2    0xC6    /* tof_* rates */
//#define ME_MTYPE_LO_TOF_BB_3    0xC7    /* tof_* rates */
//#define ME_MTYPE_LO_ANODE_B0    0xC8    /* ion_anode_* rates*/
//#define ME_MTYPE_LO_ANODE_B1    0xC9    /* ion_anode_* rates*/
//#define ME_MTYPE_LO_ANODE_B2    0xCA    /* ion_anode_* rates*/
//#define ME_MTYPE_LO_ANODE_B3    0xCB    /* ion_anode_* rates*/
//#define ME_MTYPE_LO_VAL_AB_POS  0xCC    /* valid_* rates */
//#define ME_MTYPE_LO_VAL_CB_POS  0xCD    /* valid_* rates */
//#define ME_MTYPE_LO_VAL_ALL     0xCE    /* valid_* rates */









