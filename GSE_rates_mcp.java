import java.util.StringTokenizer;
import java.util.Vector;

/**
*  let's parse some gse data in RATES form
*
*/
public class GSE_rates_mcp {
	public static int skipLines = 28;

	/**
	*
	* data to collect:
	*/

	int ra, rb0, rb3, rc;
	int rt0, rt1, rt2, rt3;
	int t1_start, t2_start, t3_start;
	int golden;
	float mcp_val;
	long time;
	String garbage = "";

	public Histogram rah, rb0h, rb3h, rch, rt0h, rt1h, rt2h, rt3h, goldh;
	public Histogram rahN, rb0hN, rb3hN, rchN, rt0hN, rt1hN, rt2hN, rt3hN, goldhN;
	public Vector tof0, tof1, tof2, tof3;
	//public IbexPhaGui theMain;
	public file f;
	public file outf;
	public String outfName;
	public int numBins;
	public int firstTime=-1;
	public int timeStamp=0;
	public int lastTime=0;

	public int rates1 = 0;
	public int rates2 = 0;
	public int rates3 = 0;

	public double min0,min1,min2,min3,max0,max1,max2,max3,mincs,maxcs,minpac,maxpac;
	public boolean b0=false;
	public boolean b1=false;
	public boolean b2=false;
	public boolean b3=false;
	public boolean bcs=false;

	//public NOAAHistogramFrame nhf1,nhf2,nhf3,nhf4;

	public GSE_rates_mcp(String fn) {
		int numTotal = 0;

		rah = new Histogram(2600,3400,18);
		rb0h = new Histogram(2600,3400,18);
		rb3h = new Histogram(2600,3400,18);
		rch = new Histogram(2600,3400,18);
		rt0h = new Histogram(2600,3400,18);
		rt1h = new Histogram(2600,3400,18);
		rt2h = new Histogram(2600,3400,18);
		rt3h = new Histogram(2600,3400,18);
		goldh = new Histogram(2600,3400,18);


		rahN = new Histogram(2600,3400,18);
		rb0hN = new Histogram(2600,3400,18);
		rb3hN = new Histogram(2600,3400,18);
		rchN = new Histogram(2600,3400,18);
		rt0hN = new Histogram(2600,3400,18);
		rt1hN = new Histogram(2600,3400,18);
		rt2hN = new Histogram(2600,3400,18);
		rt3hN = new Histogram(2600,3400,18);
		goldhN = new Histogram(2600,3400,18);

		//file f = new file("TOF_PHA_20060605_063443.txt");
		file f = new file(fn);
		f.initRead();

		// read all appropriate settings from the gui first before reading data file
		outfName = "mcp_out_"+fn;

		String line = "";
		for (int i=0; i<skipLines; i++) {
			line = f.readLine();
		}

		boolean goodEvent = false;
		long lastTime = 0;
        // start reading data file
       /* ## The following items are exported:
		## Group #0: TOF_HK.Rate_Start_A
		##          TOF_HK.Rate_Start_C
		##          TOF_HK.Rate_Pos_B0
		##          TOF_HK.Rate_Pos_B3
		##          TOF_HK.Rate_TOF_0_AB
		##          TOF_HK.Rate_TOF_1_CB
		##          TOF_HK.Rate_TOF_2_AC
		##          TOF_HK.Rate_TOF_3_Pos_B0B3
		##          EU("TOF_HK.Vmon_MCP")
		##          EU("TOF_HK.Imon_MCP")
		##          TOF_IF.MCP_VSET
		##          EU("TOF_IF.MCP_VSET")
		##          TOF_HK.RateSilver
		##          TOF_HK.RateGold
		##          RecBlockStatus.Time
		##          EU("TOF_IF.PAC_VSET")
		##          TOF_IF.PAC_VSET
		##          EU("TOF_IF.pac_vm")
##          EU("TOF_IF.mcp_vm")
		*/
		while ((line=f.readLine())!=null) {
			StringTokenizer st = new StringTokenizer(line);
			ra = Integer.parseInt(st.nextToken());
			rb0 = Integer.parseInt(st.nextToken());
			rb3 = Integer.parseInt(st.nextToken());
			rc = Integer.parseInt(st.nextToken());

			rt0 = Integer.parseInt(st.nextToken());
			rt1 = Integer.parseInt(st.nextToken());
			rt2 = Integer.parseInt(st.nextToken());
			rt3 = Integer.parseInt(st.nextToken());

			mcp_val = Float.parseFloat(st.nextToken());
			garbage = st.nextToken(); // vset
			garbage = st.nextToken(); // vset
			garbage = st.nextToken(); // vset
			garbage = st.nextToken(); // vset
			garbage = st.nextToken(); // pac
			garbage = st.nextToken(); // pac
			//time = Long.parseLong(st.nextToken());

			//if (time-lastTime==2) {
				for (int i=0; i<ra; i++) rah.addEvent(mcp_val);
				for (int i=0; i<rb0; i++) rb0h.addEvent(mcp_val);
				for (int i=0; i<rb3; i++) rb3h.addEvent(mcp_val);
				for (int i=0; i<rc; i++) rch.addEvent(mcp_val);
				for (int i=0; i<rt0; i++) rt0h.addEvent(mcp_val);
				for (int i=0; i<rt1; i++) rt1h.addEvent(mcp_val);
				for (int i=0; i<rt2; i++) rt2h.addEvent(mcp_val);
				for (int i=0; i<rt3; i++) rt3h.addEvent(mcp_val);
				for (int i=0; i<golden; i++) rt3h.addEvent(mcp_val);

				rahN.addEvent(mcp_val);
				rb0hN.addEvent(mcp_val);
				rb3hN.addEvent(mcp_val);
				rchN.addEvent(mcp_val);
				rt0hN.addEvent(mcp_val);
				rt1hN.addEvent(mcp_val);
				rt2hN.addEvent(mcp_val);
				rt3hN.addEvent(mcp_val);
				goldhN.addEvent(mcp_val);
			//}
			lastTime  = time;
			numTotal++;
		}

		System.out.println("finished reading");
		f.closeRead();


		// normalize the histograms
		for (int i=0; i<rah.numberOfBins; i++) rah.data[i]/=rahN.data[i];
		for (int i=0; i<rb0h.numberOfBins; i++) rb0h.data[i]/=rb0hN.data[i];
		for (int i=0; i<rb3h.numberOfBins; i++) rb3h.data[i]/=rb3hN.data[i];
		for (int i=0; i<rch.numberOfBins; i++) rch.data[i]/=rchN.data[i];
		for (int i=0; i<rt0h.numberOfBins; i++) rt0h.data[i]/=rt0hN.data[i];
		for (int i=0; i<rt1h.numberOfBins; i++) rt1h.data[i]/=rt1hN.data[i];
		for (int i=0; i<rt2h.numberOfBins; i++) rt2h.data[i]/=rt2hN.data[i];
		for (int i=0; i<rt3h.numberOfBins; i++) rt3h.data[i]/=rt3hN.data[i];
		for (int i=0; i<goldh.numberOfBins; i++) goldh.data[i]/=goldhN.data[i];

		file outf = new file(outfName);
		outf.initWrite(false);
		//outf.write("Processed file: " + theMain.inFileField.getText());
		//outf.write("Total Events: " + numTotal + " accepted: " + numAccepted +"\n");
		//outf.write("Hydrogen: " + rates1+"\n");
		//outf.write("Mass8: " + rates2+"\n");
		//outf.write("Oxygen: " + rates3+"\n");
		for (int i=0; i<rt0h.numberOfBins; i++) {
			outf.write(rah.label[i]+"\t"+rah.data[i]+"\t"+
                        "\t"+rb0h.data[i]+"\t"+
						"\t"+rb3h.data[i]+"\t"+
						"\t"+rch.data[i]+"\t"+
						"\t"+rt0h.data[i]+"\t"+
						"\t"+rt1h.data[i]+"\t"+
						"\t"+rt2h.data[i]+"\t"+
						"\t"+rt3h.data[i]+
						"\t"+goldh.data[i]+"\n");
		}

		outf.closeWrite();
	}


	public static final void main(String[] args) {
	    GSE_rates_mcp ge = new GSE_rates_mcp(args[0]);
	}
}