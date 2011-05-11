/**
*  Originally from CTOF dataset tools - Lukas Saul UNH '05
*
*/
public class HistogramNest {

	public int numberOfBins;
	public Histogram[] data; // this has a number of "events" per bin
	public float xMin, yMin, xMax, yMax;
	public float[] labelX; // this has the bottom value of each bin
	public float[] labelY; // this has the bottom value of each bin
	public float xWidth, yWidth;
	// use this to speed up adding event.. idea is a lot of addEvents will got to same histogram bin so avoid searching all bins
	//
	public int currentBin;

	public HistogramNest() {
		numberOfBins = 0;
		data = new Histogram[0];
	}

	// a given histogram is made with one of these constructors
	/*
	*
	* pass in limits and number of bins per side (total bins is bins^2)
	*
	*  here we need to put the
	*/
	public HistogramNest(float xmin, float xmax, float ymin, float ymax, int bins) {
		//System.out.println("creating new histogram with numBins");
		xMin=xmin;
		xMax=xmax;
		yMin=ymin;
		yMax=ymax;
		numberOfBins = bins;
		currentBin = 0;
		data = new Histogram[numberOfBins];
		labelX = new float[numberOfBins];
		labelY = new float[numberOfBins];

		xWidth = (xMax - xMin)/numberOfBins;
		yWidth = (yMax - yMin)/numberOfBins;

		for (int i=0; i<labelX.length; i++) {
			labelX[i] = xMin + i*xWidth;
			labelY[i] = yMin + i*yWidth;
			data[i]=new Histogram(xMin,xMax,numberOfBins);
		}
		//System.out.println("numBins: " + numberOfBins + " binWidth" + binWidth);
		//System.out.println("label1 " + label[1] + " label0 " + label[0]+ " bw: " + binWidth);
	}

	public void addEvent(float evtX, float evtY) {
		boolean added = false;
		if ( (evtY >= labelY[currentBin]) && (evtY <= labelY[currentBin]+yWidth) ) {
			data[currentBin].addEvent(evtX);
			added = true;
		}
		else {
			for (int i=0; i<numberOfBins; i++) {
				if ( (evtY >= labelY[i]) && (evtY <= labelY[i]+yWidth) ) {
					currentBin = i;
					data[currentBin].addEvent(evtX);
					added = true;
					i = numberOfBins;
				}
			}
		}
		if (!added) {
			//System.out.println("couldn't add date to time profile : " + evt);
		}
	}

	// output 2D data to single array, read rows then columns
	public double[] getArray() {
		double[] tbr = new double[data.length*data.length];
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




