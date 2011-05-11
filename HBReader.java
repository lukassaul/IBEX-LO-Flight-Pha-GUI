import java.util.StringTokenizer;
public class HBReader {
	public file f;
	public double asc, dec, count, phase, snb;
	public long timeStamp;
	public String ch, ty;
	public int ebin, type;
	public long[] typeCounter;
	public int counter81, counter82;
	public HBReader (String fileName) {
		f=new file(fileName);
		f.initRead();
		int skipLines = 5;
		counter81=0; counter82=0;

		Histogram timeHist[] = new Histogram[8];

		typeCounter = new long[16];
	  // here's where we read the data file
		String line = "";
		String garbage = "";
		for (int i=0; i<skipLines; i++)	 line = f.readLine();
		boolean firstDate = true;
		long firstTime;
		long lastTime = 0;
		while ((line=f.readLine())!=null && line.length()>40) {  // until end of file

			boolean eventOK = true;
			//
			//
			// parsing output of: $me_show -vv o000x.lohb
			// first line
			//# me_type=0x20 me_size=28 lines=1 gps
			//##:MET(s,GPS)   R.A.     Decl   ch ty count selnbits  phase  loc-X-RE  loc-Y-RE  loc-Z-RE

			StringTokenizer st = new StringTokenizer(line);
			timeStamp = (long)Double.parseDouble(st.nextToken());
			//asc = Double.parseDouble(st.nextToken());
			//dec = Double.parseDouble(st.nextToken());
			garbage = st.nextToken();
			garbage = st.nextToken();
			ch = st.nextToken();
			ty = st.nextToken();
			count = Double.parseDouble(st.nextToken());
			garbage = st.nextToken();
			phase = Double.parseDouble(st.nextToken());
			//System.out.println("ch: " + ch);
			//System.out.println("ty: " + ty);
			if (ty.equals("81")) counter81+=count;
			if (ty.equals("82")) counter82+=count;

			if (firstDate) {
				firstTime = (long)timeStamp;
				//if (true) {
				for (int i=0; i<8; i++)
					timeHist[i]=new Histogram(getDOY(timeStamp),getDOY(timeStamp)+7.0, 7*12);
				//}
				firstDate = false;
			}

			if (timeStamp>lastTime) lastTime=(long)timeStamp;

			//if (ch.indexOf("E")==-1) eventOK = false;
			if (ty.equals("81")) counter81+=count;
			if (ty.equals("82")) counter82+=count;

			if (!ty.equals("81") & !ty.equals("82")) eventOK = false;

			if (eventOK) {
				//PARSE ENERGY
				ebin = Integer.parseInt(ch.substring(1,2));
				//type = Integer.parseInt(ty.substring(1,2),16);
				//System.out.println(ebin+"\t"+type + "\t"+ count);
				//typeCounter[type]+=count;
				for (int i=0; i<count; i++)
						timeHist[ebin-1].addEvent(getDOY(timeStamp));
			}
		}
		System.out.println("done reading");
		for (int i=0; i<typeCounter.length; i++) {
			//System.out.println(typeCounter[i]);
		}
		System.out.println("counter 81: " + counter81);
		System.out.println("counter 82: " + counter82);
		System.out.println("hb H+O : " + (counter81+counter82));
		file outf = new file("hbOut2.txt");
		outf.initWrite(false);
		outf.write("HistBin output \n");
		outf.write("Time Histogram results: \n");
		outf.write("time \t counts\n");
		for (int i=0; i<timeHist[0].numberOfBins; i++) {
			outf.write(timeHist[0].label[i]+"\t");
			for (int j=0; j<8; j++) {
				outf.write(timeHist[j].data[i]+"\t");
			}
			outf.write("\n");
			//outf.write(((timeHist.label[i]-timeHist.label[0])/60/60)+"\t"+timeHist.data[i]+"\n");
		}
		outf.write("\n\n");
		outf.closeWrite();

	}

	public float getDOY(float gpsTime) {
		float tbr = 0.0f;
		float numSecs = gpsTime - 913591566;
		float numDays = numSecs/24/60/60;
		tbr = 352.97629630f + numDays;
		if (tbr>366) tbr=tbr-366;
		return tbr;
	}

	public static final void main(String[] args) {
		HBReader hr = new HBReader("c:\\flight\\o0018\\o18hb.txt");
	}

}

/*
 lo_type_0=0000 lo_coin_0=TRIPLE
 lo_type_1=0001 lo_coin_1=triple
 lo_type_2=0010 lo_coin_2=triple
 lo_type_3=0011 lo_coin_3=double
 lo_type_4=0100 lo_coin_4=triple
 lo_type_5=0101 lo_coin_5=triple
 lo_type_6=0110 lo_coin_6=DOUBLE
 lo_type_7=0111 lo_coin_7=double
 lo_type_8=1000 lo_coin_8=triple
 lo_type_9=1001 lo_coin_9=triple
 lo_type_A=1010 lo_coin_A=DOUBLE
 lo_type_B=1011 lo_coin_B=double
 lo_type_C=1100 lo_coin_C=triple
 lo_type_D=1101 lo_coin_D=DOUBLE
 lo_type_E=1110 lo_coin_E=single
 lo_type_F=1111 lo_coin_F=absent
 me_energy_0=lo-1 me_channel_0=ch-0
 me_energy_1=lo-2 me_channel_1=ch-1
 me_energy_2=lo-3 me_channel_2=ch-2
 me_energy_3=lo-4 me_channel_3=ch-3
 me_energy_4=lo-5 me_channel_4=ch-4
 me_energy_5=lo-6 me_channel_5=ch-5
 me_energy_6=lo-7 me_channel_6=ch-6
 me_energy_7=lo-8 me_channel_7=ch-7
 lo_type_81=H
 lo_type_82=O
 lo_type_C0=START_A         1
 lo_type_C1=START_C			2
 lo_type_C2=POS_B0			3
 lo_type_C3=POS_B3			4
 lo_type_C4=TOF_AB_0		5
 lo_type_C5=TOF_CB_1		6
 lo_type_C6=TOF_AC_2		7
 lo_type_C7=TOF_BB_3		8
 lo_type_C8=ANODE_B0		9
 lo_type_C9=ANODE_B1		10
 lo_type_CA=ANODE_B2		11
 lo_type_CB=ANODE_B3		12
 lo_type_CC=VAL_AB_POS		13
 lo_type_CD=VAL_CB_POS		14
 lo_type_CE=VAL_ALL			15
*/
