

import java.util.StringTokenizer;
import java.util.Vector;
import gov.noaa.pmel.sgt.CartesianGraph;

/**
*  let's parse some gse data in PHA form
*   now we have added a gui to this class so no gui stuff here..
*
*  this class is run from inside IbexPhaGui
*
*/
public class GSE_ep {
	public static int skipLines = 33;

	/**
	*
	* data to collect:
	*/
	double t0_val, t1_val, t2_val, t3_val;
	int t0_start, t1_start, t2_start, t3_start;
	int t0_stop, t1_stop, t2_stop, t3_stop;
	int t0_tof, t1_tof, t2_tof, t3_tof;
	float pac, mcp;
	long date;

	public Histogram t1h, t2h, t3h, t0h, csh;
	public Vector tof0, tof1, tof2, tof3;
	public IbexPhaGui theMain;
	public file f;
	public file outf;
	public String outfName;
	public int numBins;
	public long firstTime=-1;
	public long timeStamp=0;
	public long lastTime=0;
	public long mintime, maxtime;

	public int rates1 = 0;
	public int rates2 = 0;
	public int rates3 = 0;

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
	// these booleans are set by checkboxes in the gui
	// they are true if a filtering range exists for the associated parameter
	public boolean b0=false;
	public boolean b1=false;
	public boolean b2=false;
	public boolean b3=false;
	public boolean bcs=false;
	public boolean bpac=false;
	public boolean btime=false;

	public int numMcpBins;
	public MCPhaData[] mcpData;
	private long deltaT;

	public int sec0, sec1, sec2, sec3, sec4;

	/**
	* Construct an instance of the GSE_ep parser for pha data from IBEX-LO TOF GSE
	*  This parses the file and processes the data as outlined in IbexPhaGui options
	*/
	public GSE_ep(IbexPhaGui tm) {
		 // initialize
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
		mintime=0;
		maxtime=0;
		sec0=0;
		sec1=0;
		sec2=0;
		sec3=0;
		sec4=0;

		// read all appropriate settings from the gui first before reading data file
		theMain = tm;
		file f = new file(theMain.inFileField.getText());
		f.initRead();
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
			t0h = new Histogram((float)0.0,(float)220.0,numBins);
			t1h = new Histogram((float)0.0,(float)220.0,numBins);
			t2h = new Histogram((float)0.0,(float)220.0,numBins);
			t3h = new Histogram((float)0.0,(float)18,100);
		//	t0h = new Histogram((float)1.1309,(float)(1.131+numBins*0.1662),numBins);
		//	t1h = new Histogram((float)1.2807,(float)(1.2808+numBins*0.1642),numBins);
		//	t2h = new Histogram((float)0.86,(float)(0.87+numBins*0.1671),numBins);
		//	t3h = new Histogram((float)0.2384,(float)(0.2385+numBins*0.1646),numBins);
			csh = new Histogram((float)-7.5,(float)7.5,100);
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
		if (theMain.tof1CheckBox.isSelected()) try {
			min1 = Double.parseDouble(theMain.tof1MinField.getText());
			max1 = Double.parseDouble(theMain.tof1MaxField.getText());
			b1=true;
		} catch (Exception e) {}
		if (theMain.tof2CheckBox.isSelected()) try {
			min2 = Double.parseDouble(theMain.tof2MinField.getText());
			max2 = Double.parseDouble(theMain.tof2MaxField.getText());
			b2=true;
		} catch (Exception e) {}
		if (theMain.tof3CheckBox.isSelected()) try {
			min3 = Double.parseDouble(theMain.tof3MinField.getText());
			max3 = Double.parseDouble(theMain.tof3MaxField.getText());
			b3=true;
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
		if (theMain.timeCheckBox.isSelected()) try {
			mintime = Long.parseLong(theMain.timeMinField.getText());
			maxtime = Long.parseLong(theMain.timeMaxField.getText());
			btime=true;
		} catch (Exception e) {}

        // here's where we read the data file!!

		String line = "";
		for (int i=0; i<skipLines; i++)	 line = f.readLine();
		while ((line=f.readLine())!=null && line.length()>5) {  // until end of file

			StringTokenizer st = new StringTokenizer(line);
			t0_val = Double.parseDouble(st.nextToken());
			t0_start = Integer.parseInt(st.nextToken());
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
			pac = (float)Double.parseDouble(st.nextToken());
			mcp = (float)Double.parseDouble(st.nextToken());
			//String garbage = st.nextToken(); // golden
			timeStamp = Long.parseLong(st.nextToken());
			// done reading values for this line

			numTotal++;  // total number of events
			if (firstTime == -1 && numTotal>25) firstTime = timeStamp;
			if (timeStamp<firstTime) firstTime = timeStamp;
			if (timeStamp>lastTime) lastTime = timeStamp;

			// tally for rates before filtering..
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

			// conditions!!
			boolean eventOK = true;
			if (theMain.tof0CheckBox.isSelected()) if (t0_tof==0) eventOK = false;
			if (theMain.tof1CheckBox.isSelected()) if (t1_tof==0) eventOK = false;
			if (theMain.tof2CheckBox.isSelected()) if (t2_tof==0) eventOK = false;
			if (theMain.tof3CheckBox.isSelected()) if (t3_tof==0) eventOK = false;
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
			if (eventOK & bcs) {
				double q = t1_val+t2_val-t0_val-t3_val;
				if (q<mincs || q>maxcs) eventOK = false;
			}
			if (eventOK & bpac) {
				if (pac<minpac | pac>maxpac) eventOK = false;
			}
			if (eventOK & btime) {
				if (timeStamp<mintime | timeStamp>maxtime) eventOK = false;
			}
			if (eventOK & theMain.ghostCheckBox.isSelected()) {
				if (t2_val>40 & t2_val<46 & t1_val>60 & t0_val>100) eventOK = false; // ghost peak 1
				//else if (Math.abs(t2_val-t0_val)<5) eventOK = false; // ghost peak 2
				//else if (t1_val+t3_val-t2_val<5) eventOK = false; // ghost peak 3
				//else if (Math.abs(t1_val-t3_val)<5) eventOK = false; // ghost peak 4
			}
			if (eventOK & theMain.pacCheckBox.isSelected()) {
				if (pac<minpac | pac>maxpac) eventOK = false;
			}


			// done conditions..  let's process:
			if (eventOK & theMain.histogramCheckBox.isSelected()) {
				//numAccepted++;
				t0h.addEvent((float)t0_val);
				t1h.addEvent((float)(t1_val-t3_val));
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

			if (eventOK & theMain.scatterCheckBox.isSelected()) {

				tof0.add(new Double(t0_val));
				tof1.add(new Double(t1_val));
				tof2.add(new Double(t2_val));
				tof3.add(new Double(t3_val));
			}

			if (theMain.mcpScanBox.isSelected()) {
				//System.out.println(mcpData[0].ct2);
				//System.out.println(mcpData[0].minV);
				int bin = mcpBin(mcp);
				if (t0_tof!=0) mcpData[bin].ct0++;
				if (t1_tof!=0) mcpData[bin].ct1++;
				if (t2_tof!=0) mcpData[bin].ct2++;
				if (t3_tof!=0) mcpData[bin].ct3++;

				if (t0_tof!=0 & t0_tof!=0 & t0_tof!=0 & t0_tof!=0) {
					double q = t1_val+t2_val-t0_val-t3_val;
					if (q>mincs & q<maxcs) mcpData[bin].cTriples++;
				}

				if (timeStamp < mcpData[bin].startTime) mcpData[bin].startTime=timeStamp;
				if (timeStamp > mcpData[bin].endTime) mcpData[bin].endTime=timeStamp;
			}

			if (eventOK) {
				numAccepted++;
				if (isHydrogen(t0_val,t1_val,t2_val,t3_val)) rates1++;
				if (isMassEight(t0_val,t1_val,t2_val,t3_val)) rates2++;
				if (isOxygen(t0_val,t1_val,t2_val,t3_val)) rates3++;
			}

		}
		f.closeRead();

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
		//outputCatalog();

		// output to the file - - -
		if (!theMain.catalogCheckBox.isSelected()) {
			file outf = new file(outfName);
			outf.initWrite(false);
			outf.write("IBEX-LO TOF GSE-PHA Out Results\n");
			outf.write("Processed file: " + theMain.inFileField.getText()+"\n");
			outf.write("This file: " + theMain.outFileField.getText()+"\n");
			outf.write("Delat T in file: " + deltaT + "\n");
			outf.write("Total Events: " + numTotal + " \nPassing filter: " + numAccepted +"\n\n");
			outf.write("Valid TOF0: " + t0_tally + "\trate: " + ((float)t0_tally/deltaT)+"\n");
			outf.write("Valid TOF1: " + t1_tally + "\trate: " + ((float)t1_tally/deltaT)+"\n");
			outf.write("Valid TOF2: " + t2_tally + "\trate: " + ((float)t2_tally/deltaT)+"\n");
			outf.write("Valid TOF3: " + t3_tally + "\trate: " + ((float)t3_tally/deltaT)+"\n");
			outf.write("Approximate Counts by Species: \n");
			outf.write("Hydrogen: " + rates1+"\n");
			outf.write("Mass8: " + rates2+"\n");
			outf.write("Oxygen: " + rates3+"\n");
			outf.write("\n\n");
			outf.write("TOF3 Sector counts: "+sec0+" - "+sec1+" - "+sec2+" - "+sec3+" - "+sec4);
			outf.write("\n\n");
			outf.write("Event types (without filter): \n");
			outf.write("(valid tof0, tof1, tof2, tof3) \n");
			outf.write("1111:" + ev_trip + "\n");
			outf.write("0111: " + ev_not0 + "\n");
			outf.write("1011: " + ev_not1 + "\n");
			outf.write("1101: " + ev_not2 + "\n");
			outf.write("1110: " + ev_not3 + "\n");
			outf.write("1100: " + ev_1100 + "\n");
			outf.write("1010: " + ev_1010 + "\n");
			outf.write("1001: " + ev_1001 + "\n");
			outf.write("0110: " + ev_0110 + "\n");
			outf.write("0101: " + ev_0101 + "\n");
			outf.write("0011: " + ev_0011 + "\n");
			outf.write("1000: " + ev_t0 + "\n");
			outf.write("0100: " + ev_t1 + "\n");
			outf.write("0010: " + ev_t2 + "\n");
			outf.write("0001: " + ev_t3 + "\n");
			outf.write("0000: " + ev_junk + "\n");
			outf.write("other: " + ev_other + "\n\n");
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
				outf.write("TOF Histogram Data\n");
				outf.write("tof (ns)\tTOF0 counts\tTOF1 counts\tTOF2 counts\tTOF3 counts\tchecksum\tcounts\n ");
				for (int i=0; i<t0h.numberOfBins; i++) {
					outf.write(t0h.label[i]+"\t"+t0h.data[i]+"\t"+
								+t1h.data[i]+"\t"+t2h.data[i]+"\t");
					if (i<t3h.data.length) outf.write(t3h.label[i]+"\t"+t3h.data[i]+"\t"+csh.label[i]+"\t"+csh.data[i]+"\n");
					else outf.write("\n");
				}
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
			outf.closeWrite();
		}
		System.out.println("time passed: " + (lastTime-firstTime));
		System.out.println("total: " + numTotal + " acc.: " + numAccepted);
	}

	public boolean isHydrogen(double t0, double t1, double t2, double t3) {
		if (t0    < 24 | t0    > 60) return false;
		if (t1-t3 < 10 | t1-t3 > 50) return false;
		if (t2    < 10 | t2    > 40) return false;
		return true;
	}

	public boolean isOxygen(double t0, double t1, double t2, double t3) {
		if (t0    < 100 | t0    > 300) return false;
		if (t1-t3 < 50  | t1-t3 > 125) return false;
		if (t2    < 60  | t2    > 130) return false;
		return true;
	}

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

	public static final void o(String s) {
		System.out.println(s);
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