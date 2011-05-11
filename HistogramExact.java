import java.util.Vector;

/**
*  Originally from CTOF dataset tools - Lukas Saul UNH '05
*
*/
public class HistogramExact {

	public int numberOfBins;
	public float[] data; // this has a number of "events" per bin
	public float top, bottom, binWidth;
	public float[] label; // this has the bottom value of each bin

	public Vector data_v;
	public Vector label_v;

	public int currentBin;

	public HistogramExact() {
		numberOfBins = 0;
		data = new float[0];
		top = 0;
		bottom = 0;
		binWidth = 0;
		label = new float[0];
		data_v = new Vector();
		label_v = new Vector();
	}

	// a given histogram will only call one of these addEvent guys!!!  So don't worry about currentBin!!

	public void addEvent(double evt) {
		addEvent((float)evt);
	}
	public void addEvent(double evt, int count) {
		addEvent((float)evt, count);
	}
	public void addEvent(float evt) {
		boolean found = false;
		for (int i=0; i<label_v.size(); i++) {
			if (evt==(float)((Float)label_v.elementAt(i))) {
				// we have a match
				found = true;
				int previous = (int)((Integer)data_v.elementAt(i));
				int newOne = previous+1;
				data_v.set(i,(Integer)newOne);
				i=label_v.size();
			}
		}
		if (!found) {
			// add a new bin
			label_v.add((Float)evt);
			data_v.add((Integer)1);
		}

	}

	public void finalize() {
		System.out.println("finalizing: "+ data_v.size() + " label: "+ label_v.size());
		data = new float[data_v.size()];
		label = new float[label_v.size()];
		for (int i=0; i<data.length; i++) {
			data[i] = (float)((Integer)data_v.elementAt(i));
			label[i] = (float)((Float)label_v.elementAt(i));
		}
	}

	public double[] getArray() {
		double[] tbr = new double[data.length];
		for (int i=0; i<tbr.length; i++) {
			tbr[i] = (double)data[i];
		}
		return tbr;
	}

	/**
	* for simple 2 column histogram data reading and histogramming, use this puppy
	*/

}




