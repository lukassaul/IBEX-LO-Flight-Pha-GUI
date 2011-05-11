/**
*  Originally from CTOF dataset tools - Lukas Saul UNH '05
*   now adopted to make spin phase time profile color chart
*/
public class SpinTimeHistogram {

	public int numberOfBins;
	public Histogram[] data; // this has a number of "events" per bin
	public float xMin, yMin, xMax, yMax;
	public float[] labelX; // this has the bottom value of each bin
	public float[] labelY; // this has the bottom value of each bin
	public float xWidth, yWidth;
	public int numAngBins=180;
	// use this to speed up adding event.. idea is a lot of addEvents will got to same histogram bin so avoid searching all bins
	//
	public int currentBin;

	public SpinTimeHistogram() {
		numberOfBins = 0;
		data = new Histogram[0];
	}

	// a given histogram is made with one of these constructors
	/*
	*
	* pass in limits and number of bins per side ()
	*
	*   x is time,  y is spin phaxe
	*/
	public SpinTimeHistogram(float xmin, float xmax, int bins) {
		//System.out.println("creating new histogram with numBins");
		xMin=xmin;
		xMax=xmax;
		numberOfBins = bins;
		currentBin = 0;
		yMin=0.0f;
		yMax=1.0f;
		data = new Histogram[numberOfBins];
		labelX = new float[numberOfBins];
		labelY = new float[numAngBins];

		xWidth = (xMax - xMin)/numberOfBins;
		yWidth = (yMax - yMin)/numAngBins;

		for (int i=0; i<labelY.length; i++) {
			labelY[i] = yMin + i*yWidth;
		}

		for (int i=0; i<labelX.length; i++) {
			labelX[i] = xMin + i*xWidth;
			data[i]=new Histogram(yMin,yMax,labelY.length);
		}
	}

	public void addEvent(float evtX, float evtY) {
		boolean added = false;
		if ( (evtX >= labelX[currentBin]) && (evtX <= labelX[currentBin]+xWidth) ) {
			data[currentBin].addEvent(evtY);
			added = true;
		}
		else {
			for (int i=0; i<numberOfBins; i++) {
				if ( (evtX >= labelX[i]) && (evtX <= labelX[i]+xWidth) ) {
					currentBin = i;
					data[currentBin].addEvent(evtY);
					added = true;
					i = numberOfBins;
				}
			}
		}
		if (!added) {
			System.out.println("couldn't add date to spin/time profile : " + evtX);
		}
	}
	public void addEvent(float evtX, float evtY, int count) {
		boolean added = false;
		if ( (evtX >= labelX[currentBin]) && (evtX <= labelX[currentBin]+xWidth) ) {
			data[currentBin].addEvent(evtY,count);
			added = true;
		}
		else {
			for (int i=0; i<numberOfBins; i++) {
				if ( (evtX >= labelX[i]) && (evtX <= labelX[i]+xWidth) ) {
					currentBin = i;
					data[currentBin].addEvent(evtY,count);
					added = true;
					i = numberOfBins;
				}
			}
		}
		if (!added) {
			System.out.println("couldn't add date to spin/time profile : " + evtX);
		}
	}

	// output 2D data to single array, read rows then columns
	public double[] getArray() {
		double[] tbr = new double[data.length*data[0].numberOfBins];
		int counter = 0;
		for (int i=0; i<data.length; i++) {
			for (int j=0; j<data[i].numberOfBins; j++) {
				tbr[counter] = (double)data[i].data[j];
				counter++;
			}
		}
		return tbr;
	}

}




