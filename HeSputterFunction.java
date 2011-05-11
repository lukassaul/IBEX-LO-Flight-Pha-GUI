import java.util.StringTokenizer;

/**
*  Use this to interpolate from a set of datat
*
*  The _maim_ class will create a file that is a monotonic increasing data file
*/
public class HeSputterFunction {

	/**
	* For the _main_ class, to be changed as needed for various date conventions
	*/

	//public static int NUM_LINES = 40640;
	private int lastIndex;
	private float[] tofs;
	private float[] data;
	private int NUM_LINES;

	/**
	*  Use this constructor to load the data into RAM
	*
	*/
	public HeSputterFunction (String fileName, int num_lines) {
		NUM_LINES = num_lines;
		StringTokenizer st;
		tofs = new float[NUM_LINES];
		data = new float[NUM_LINES ];

		file f = new file(fileName);
		f.initRead();
		String line = "";

		boolean eof = false;
		int i = 0;
		while ((line=f.readLine())!=null) {
			st = new StringTokenizer(line);
			tofs[i] = Float.parseFloat(st.nextToken());
			data[i] = Float.parseFloat(st.nextToken());
			i++;
		}
		f.closeRead();
		System.out.println("Loaded He Cal Data " + i);
		System.out.println("a sample:  "+tofs[0]+" "+data[0]+" "+tofs[10]+" "+data[10]);
		lastIndex = 0;
	}

	public double getSpec(double tof) {
		return (double)getSpec((float)tof);
	}

	/**
	* Use this to get the data at a value time of flight
	*   making requests nearby each other will speed up search
	*   uses linear interpolation and hunt/bifurcate search
	*
	*/
	public float getSpec(float tof)   {

		// no extrapolation allowed
		if (tof<tofs[0] | tof>tofs[NUM_LINES-1]) {
			//System.out.println("outside data range in getShift with: " + tof);
			//throw new Exception();
			return 0.0f;
		}

		int index = 0;
		lastIndex = huntNM(tofs,tof,lastIndex);
		index = lastIndex;

		//System.out.println(lastIndex+"");
		if (index>NUM_LINES-2) System.out.println("a problem.with finding a tof " + tof);

		float div = tofs[index+1] - tofs[index];
		float dif = tof - tofs[index];

		if (div==0) System.out.println("EEH?  a problem finding a tof, div=0");
		float pseudoIndex = index + (dif/div);

		return lineSolver(data, pseudoIndex);
	}

	/**
	* This interpolates the value at pseudo index jj (between j and j+1)
	* (linear interpolation)
	*/
	private float lineSolver(float[] xx, float jj) {
		if ((int)Math.floor((double)jj)>=xx.length-1) return xx[(int)Math.floor((double)jj)];
		try {
			int j = (int)Math.floor((double)jj);
			return ( (xx[j+1]-xx[j])*(jj-(float)(j+1)) + xx[j+1]);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("index search: " + jj + " size: " + xx.length);
			return 1/0;
		}
	}

	/**
	*  This one finds the appropraite index for a non-monotonic array
 	*
	*
	*/
	public int huntNM(float[]xx, float x, int jlow) {
		if (xx[jlow]<=x) {
			for (int i=jlow+1; i<xx.length; i++) {
				if (xx[i]>x) {
					return(i-1);
					//i=xx.length;
				}
			}
		}
		if (xx[jlow]>=x) {
			for (int i=jlow-1; i>=0; i--) {
				if (xx[i]<x) {
					return i;
					//i=-1;
				}
			}
		}
		System.out.println("search for: " + x + " return: " + jlow);
		System.out.println("didn't find the jlow, out of range?");
		return 0;
	}

	/**
	* For testing
	*/
	public static final void main(String[] args) {
		HeSputterFunction hsf = new HeSputterFunction("cal_he_b1_tof2.txt",400);
		for (int i=1; i<200; i++) System.out.println(""+hsf.getSpec((float)i));

	}
}
