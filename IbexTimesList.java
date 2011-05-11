// tested 4/18/2011

import java.util.StringTokenizer;

public class IbexTimesList {
	public double[] startTimes;
	public double[] endTimes;
	public String garbage;
	public StringTokenizer st;

	public IbexTimesList(String fName) {
		file f = new file(fName);
		f.initRead();
		int hh = 0;
		int nn = 0;
		String line = "";
		while ((line = f.readLine())!=null) {
			if (line.substring(0,1).contains("#")) hh++;
			else break;
		}
		f.closeRead();
		f.initRead();

		for (int i = 0; i<hh; i++) garbage = f.readLine();
		while ((line = f.readLine())!=null) nn++;
		System.out.println("got header length: "+ hh);
		System.out.println("got segment length: "+ nn);
		setup(fName,hh,nn);
	}


	public void setup(String fName, int headerLength, int numSegments) {
		startTimes = new double[numSegments];
		endTimes = new double[numSegments];
		file f = new file(fName);
		f.initRead();
		// first get past the header
		for (int i = 0; i<headerLength; i++) garbage = f.readLine();

		for (int i=0; i<numSegments; i++) {
			st = new StringTokenizer (f.readLine());
			garbage = st.nextToken();
			startTimes[i]=Double.parseDouble(st.nextToken());
			endTimes[i]=Double.parseDouble(st.nextToken());
		}
	}

	/**
	* Takes only argument in MET time format!
	*/
	public boolean isValidTime(double gpsDate) {
		for (int i=0; i<startTimes.length; i++) {
			if (startTimes[i]<gpsDate && endTimes[i]>gpsDate) return true;
		}
		return false;
	}

	/**
	* For testing
	*/
	public static final void main(String[] args) {
		IbexTimesList itl = new IbexTimesList("LoISNTimes.txt");
		//IbexTimesList itl = new IbexTimesList("LoISNTimes.txt",21,94);
		// output all
		for (int i=0; i<itl.startTimes.length; i++) {
			System.out.println(itl.startTimes[i]+ "\t"+ itl.endTimes[i]+"\t"+i+"\n");
		}

		System.out.println(itl.isValidTime(946642124.891000));
		System.out.println(itl.isValidTime(946642124.891001));

	}
}