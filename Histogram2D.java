/**
*  Originally from CTOF dataset tools - Lukas Saul UNH '05
*
*/
public class Histogram2D {

	public int numberOfBins;
	public float[][] data; // this has a number of "events" per bin
	public float topY, topX, bottomX, bottomY, binWidth;
	public float[] labelX; // this has the bottom value of each bin
	public float[] labelY;

	public int currentBin;

	public Histogram2D() {
		numberOfBins = 0;
		data = new float[0][0];
		top = 0;
		bottom = 0;
		binWidth = 0;
		labelX = new float[0];
		labelY = new float[0];
	}

	// a given histogram is made with one of these constructors
	public Histogram2D(float _bottom, float _top, int _numberOfBins) {
		//System.out.println("creating new histogram with numBins");
		top = _top;
		bottom = _bottom;
		numberOfBins = _numberOfBins;
		currentBin = 0;
		data = new float[numberOfBins];
		label = new float[numberOfBins];

		binWidth = (top - bottom)/numberOfBins;
		for (int i=0; i<label.length; i++) {
			label[i] = bottom + i*binWidth;
		}

		for (int j=0; j<data.length; j++) {
			data[j] = 0;
		}
		//System.out.println("numBins: " + numberOfBins + " binWidth" + binWidth);
		//System.out.println("label1 " + label[1] + " label0 " + label[0]+ " bw: " + binWidth);
	}

	public Histogram2D(float _bottom, float _top, float _binWidth) {
		System.out.println("creating new histogram with binWidth");
		top = _top;
		bottom = _bottom;
		binWidth = _binWidth;
		currentBin = 0;
		numberOfBins = (int)((top - bottom)/binWidth);
		System.out.println("going to use " + numberOfBins + "bins");
		data = new float[numberOfBins];
		label = new float[numberOfBins];

		for (int i=0; i<label.length; i++) {
			label[i] = bottom + (i*binWidth);
		}

		for (int j=0; j<data.length; j++) {
			data[j] = 0;
		}
		System.out.println("numBins: " + numberOfBins + " binWidth: " + binWidth);
		System.out.println("label1 " + label[1] + " label0 + bw " + label[0]+binWidth);
	}

	// a given histogram will only call one of these addEvent guys!!!  So don't worry about currentBin!!
	public void addEvent(float evt) {
		boolean added = false;
		if ( (evt >= label[currentBin]) && (evt <= label[currentBin]+binWidth) ) {
			data[currentBin]++;
			added = true;
		}
		else {
			for (int i=0; i<numberOfBins; i++) {
				if ( (evt >= label[i]) && (evt <= label[i]+binWidth) ) {
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

	// Adds events by splitting them to avoid fencepost effect
	public void addEvent(float lowEvt, float highEvt) {
		// if it's all inside, just increment current bin!!
		if ( (lowEvt >= label[currentBin]) && (highEvt <= label[currentBin]+binWidth) )
			data[currentBin]++;
		else {
			// we first need to find the bin that the lowEvt is inside of
			boolean gotIt = false;
			for (int i=0; i<numberOfBins; i++) {
				if ( (lowEvt >= label[i]) && (lowEvt <= label[i]+binWidth) ) {
					gotIt = true;
					currentBin = i;
					i = numberOfBins;
				}
			}
			if (gotIt) { // low end was in bin.  If high is to, we in.
				if ( (highEvt >= label[currentBin]) && (highEvt < label[currentBin]+binWidth) ) {
					data[currentBin]++;
				}
				else if (currentBin < (numberOfBins-1)) { // is high end in next bin?
					if ( (highEvt >= label[currentBin+1]) && (highEvt <= label[currentBin+1]+binWidth) ){
						// we spit the result:
						float topHalf = (highEvt - label[currentBin+1])/(highEvt - lowEvt);
						float bottomHalf = (label[currentBin+1]-lowEvt)/(highEvt - lowEvt);
						data[currentBin] += bottomHalf;
						data[currentBin+1] += topHalf;
					}
				}
			}
		}
	}

	public long[] getArray() {
		long[] tbr = new long[data.length];
		for (int i=0; i<tbr.length; i++) {
			tbr[i] = (long)data[i];
		}
		return tbr;
	}

	/**
	* for simple 2 column histogram data reading and histogramming, use this puppy
	*/

}




