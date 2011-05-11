


/**
* This should be an intelligent histogram that can grow with new events
*
*/
public class HistogramVector {

	public int numberOfBins;
	public float[] data; // this has a number of "events" per bin
	public float top, bottom, binWidth;
	public float[] label; // this has the bottom value of each bin
	public boolean firstEvent = true;
	public int lastBin = 0;
	public String name = "n_";

	public HistogramVector (float width) {
		numberOfBins = 0;
		data = new float[0];
		label = new float[0];
		binWidth = width;
	}


	public void addEvent(double evt) {
		addEvent((float)evt);
	}

	public void addEvent(float evt) {
		addEvent(evt, 1);
	}

	/**
	* We are adding a single count a position "evt"
	*   for this one lets assume the events come in chronological order, so addEvent(2) will not come after addEvent(3)
	*
	*   second argument is for adding multiple counts at once
	*/
	public void addEvent(float evt, int count) {
		boolean eventAdded = false;
		if (firstEvent) {
			// set arrays
			data = new float[1];
			data[0]=count;
			label = new float[1];
			label[0] = evt;
			firstEvent = false;
			eventAdded = true;
			o("set first event with evt: " + evt);
		}

		else {

			// first lets see if we are inside the current bin or what
			if (evt>=label[lastBin] && evt<=label[lastBin]+binWidth) {
				// we are inside an existing bin
				//o("inside existing bin with evt: "+ evt + " bw: " + binWidth);
				data[lastBin]+=count;
				eventAdded = true;
			}
		}

		if (!eventAdded & evt>=label[lastBin]+binWidth & evt<label[lastBin]+2*binWidth) {
			// ok now what if we are outside a bin width;
			// first lets see if we are in the "next bin"
			// in this case we only need to add a single bin
			//do the arrays as well

			//o("current size: " + data.length);
			//System.out.println(name + " adding new bin next to last with evt: " + evt);
			float[] tempData = new float[data.length+1];
			float[] tempLabel = new float[label.length+1];
			for (int i=0; i<data.length; i++) {
				tempData[i] = data[i];
				tempLabel[i] = label[i];
			}
			data= new float[tempData.length];
			label = new float[tempLabel.length];

			for (int i=0; i<tempData.length; i++) {
				data[i] = tempData[i];
				label[i] = tempLabel[i];
			}
			lastBin++;
			data[lastBin]+=count;
			label[lastBin]=label[lastBin-1]+binWidth;
			eventAdded = true;
		}

		if (!eventAdded & evt>=label[lastBin]+2*binWidth) {
			// ok now we are going to start a new bin right here
			//System.out.println(name + " adding new bin far from last with evt: " + evt);
			//o("current size: " + data.length);
			float[] tempData = new float[data.length+1];
			float[] tempLabel = new float[label.length+1];
			for (int i=0; i<data.length; i++) {
				tempData[i] = data[i];
				tempLabel[i] = label[i];
			}
			data= new float[tempData.length];
			label = new float[tempLabel.length];

			for (int i=0; i<tempData.length; i++) {
				data[i] = tempData[i];
				label[i] = tempLabel[i];
			}
			lastBin++;
			data[lastBin]+=count;
			label[lastBin]=evt;
			eventAdded = true;
		}

		if (!eventAdded) {
			System.out.println(" evt not added in HistVect! evt: " + evt + " lastBinLabel " + label[lastBin]);
		}

		numberOfBins = data.length;
	}

	public void o(String s) {
		System.out.println(s);
	}
}











