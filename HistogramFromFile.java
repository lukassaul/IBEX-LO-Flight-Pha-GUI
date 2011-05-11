import java.util.StringTokenizer;

public class HistogramFromFile {
	public int numberOfBins;
	public float[] data; // this has a number of "events" per bin
	public float top, bottom, binWidth;
	public float[] label; // this has the bottom value of each bin
	public int currentBin;

	public HistogramFromFile(String filename) {
		file f = new file(filename);
		int numlines = f.readStuffNumLines();
		numberOfBins = numlines/2;
		data = new float[numberOfBins];
		top = 0;
		bottom = 0;
		binWidth = 0;
		label = new float[numberOfBins];
		f.initRead();
		String garbage = "";
		for (int i=0; i<numberOfBins; i++) {
			String line = f.readLine();
			line = f.readLine();
			StringTokenizer st = new StringTokenizer(line);
			garbage = st.nextToken();
			garbage = st.nextToken();
			garbage = st.nextToken();
			garbage = st.nextToken();
			garbage = st.nextToken();
			String met = st.nextToken();
			float met_f = getDOY(Float.parseFloat(met));
			label[i]=met_f;
		}
		// got our labels
	}
	public void addEvent(double evt) {
		addEvent((float)evt);
	}
	public void addEvent(double evt, int count) {
		addEvent((float)evt, count);
	}
	public void addEvent(float evt) {
		boolean added = false;
		if ( (evt >= label[currentBin]) && (evt <= label[currentBin+1]) ) {
			data[currentBin]++;
			added = true;
		}
		else {
			for (int i=0; i<numberOfBins; i++) {
				if ( (evt >= label[i]) && (evt <= label[i+1]) ) {
					currentBin = i;
					data[currentBin]++;
					added = true;
					i = numberOfBins;
				}
			}
		}
		if (!added) {
			//System.out.println("couldn't add date to time profile : " + evt);
		}
	}

	public void addEvent(float evt, int count) {
		boolean added = false;
		if ( (evt >= label[currentBin]) && (evt <= label[currentBin+1]) ) {
			data[currentBin]+= count;
			added = true;
		}
		else {
			for (int i=0; i<numberOfBins; i++) {
				if ( (evt >= label[i]) && (evt <= label[i+1]) ) {
					currentBin = i;
					data[currentBin]+= count;
					added = true;
					i = numberOfBins;
				}
			}
		}
		if (!added) {
			//System.out.println("couldn't add date to time profile : " + evt);
		}
	}
	public float getDOY(float gpsTime) {
		float tbr = 0.0f;
		float numSecs = gpsTime - 913591566;
		float numDays = numSecs/24/60/60;
		tbr = 352.97629630f + numDays;
		if (tbr>366) tbr=tbr-366;
		return tbr;
	}
}

