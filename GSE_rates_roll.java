import java.util.StringTokenizer;
import java.util.Vector;

/**
*  let's parse some gse data in RATES form
*
*/
public class GSE_rates_roll {
	public static int skipLines = 26;

	/**
	*
	* data to collect:
	*/

	int ra, rb0, rb3, rc;
	int rt0, rt1, rt2, rt3;
	int t1_start, t2_start, t3_start;
	int roll, pitch;
	float roll_val;
	long time;
	String garbage = "";

	public Histogram rah, rb0h, rb3h, rch, rt0h, rt1h, rt2h, rt3h;
	public Histogram rahN, rb0hN, rb3hN, rchN, rt0hN, rt1hN, rt2hN, rt3hN;
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

	public GSE_rates_roll() {
		int numTotal = 0;

		rah = new Histogram(-22,158,180);
		rb0h = new Histogram(-22,158,180);
		rb3h = new Histogram(-22,158,180);
		rch = new Histogram(-22,158,180);
		rt0h = new Histogram(-22,158,180);
		rt1h = new Histogram(-22,158,180);
		rt2h = new Histogram(-22,158,180);
		rt3h = new Histogram(-22,158,180);


		rahN = new Histogram(-22,158,180);
		rb0hN = new Histogram(-22,158,180);
		rb3hN = new Histogram(-22,158,180);
		rchN = new Histogram(-22,158,180);
		rt0hN = new Histogram(-22,158,180);
		rt1hN = new Histogram(-22,158,180);
		rt2hN = new Histogram(-22,158,180);
		rt3hN = new Histogram(-22,158,180);

		//file f = new file("TOF_PHA_20060605_063443.txt");
		file f = new file("roll_h2_rates.txt");
		f.initRead();

		// read all appropriate settings from the gui first before reading data file
		outfName = "roll_h2_rates_out.txt";

		String line = "";
		for (int i=0; i<skipLines; i++) {
			line = f.readLine();
		}

		boolean goodEvent = false;
		long lastTime = 0;
        // start reading data file
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

			//String garbage = st.nextToken(); // PAC
			garbage = st.nextToken();
			garbage = st.nextToken();
			garbage = st.nextToken();
			garbage = st.nextToken();
			garbage = st.nextToken();
			garbage = st.nextToken();
			garbage = st.nextToken();
			garbage = st.nextToken();


			roll_val = Float.parseFloat(st.nextToken());
			//time = Long.parseLong(st.nextToken());
			if (roll_val > 360) roll_val = roll_val-65536;

			//if (time-lastTime==2) {
				for (int i=0; i<ra; i++) rah.addEvent(roll_val);
				for (int i=0; i<rb0; i++) rb0h.addEvent(roll_val);
				for (int i=0; i<rb3; i++) rb3h.addEvent(roll_val);
				for (int i=0; i<rc; i++) rch.addEvent(roll_val);
				for (int i=0; i<rt0; i++) rt0h.addEvent(roll_val);
				for (int i=0; i<rt1; i++) rt1h.addEvent(roll_val);
				for (int i=0; i<rt2; i++) rt2h.addEvent(roll_val);
				for (int i=0; i<rt3; i++) rt3h.addEvent(roll_val);

				rahN.addEvent(roll_val);
				rb0hN.addEvent(roll_val);
				rb3hN.addEvent(roll_val);
				rchN.addEvent(roll_val);
				rt0hN.addEvent(roll_val);
				rt1hN.addEvent(roll_val);
				rt2hN.addEvent(roll_val);
				rt3hN.addEvent(roll_val);
			//}
			//lastTime  = time;
			numTotal++;
		}

		System.out.println("finished reading");

		f.closeRead();
		for (int i=0; i<rah.numberOfBins; i++) rah.data[i]/=rahN.data[i];
		for (int i=0; i<rb0h.numberOfBins; i++) rb0h.data[i]/=rb0hN.data[i];
		for (int i=0; i<rb3h.numberOfBins; i++) rb3h.data[i]/=rb3hN.data[i];
		for (int i=0; i<rch.numberOfBins; i++) rch.data[i]/=rchN.data[i];
		for (int i=0; i<rt0h.numberOfBins; i++) rt0h.data[i]/=rt0hN.data[i];
		for (int i=0; i<rt1h.numberOfBins; i++) rt1h.data[i]/=rt1hN.data[i];
		for (int i=0; i<rt2h.numberOfBins; i++) rt2h.data[i]/=rt2hN.data[i];
		for (int i=0; i<rt3h.numberOfBins; i++) rt3h.data[i]/=rt3hN.data[i];

		file outf = new file(outfName);
		outf.initWrite(false);
		//outf.write("Processed file: " + theMain.inFileField.getText());
		//outf.write("Total Events: " + numTotal + " accepted: " + numAccepted +"\n");
		//outf.write("Hydrogen: " + rates1+"\n");
		//outf.write("Mass8: " + rates2+"\n");
		//outf.write("Oxygen: " + rates3+"\n");
		for (int i=0; i<rt0h.numberOfBins; i++) {
			outf.write(rah.label[i]+"\t"+rah.data[i]+"\t"+
						rb0h.label[i]+"\t"+rb0h.data[i]+"\t"+
						rb3h.label[i]+"\t"+rb3h.data[i]+"\t"+
						rch.label[i]+"\t"+rch.data[i]+"\t"+
						rt0h.label[i]+"\t"+rt0h.data[i]+"\t"+
						rt1h.label[i]+"\t"+rt1h.data[i]+"\t"+
						rt2h.label[i]+"\t"+rt2h.data[i]+"\t"+
						rt3h.label[i]+"\t"+rt3h.data[i]+"\n");
		}

		outf.closeWrite();
		//System.out.println("time passed: " + (lastTime-firstTime));
		//System.out.println("total: " + numTotal + " acc.: " + numAccepted);
	}


	public static final void main(String[] args) {
	    GSE_rates_roll ge = new GSE_rates_roll();
	}
}