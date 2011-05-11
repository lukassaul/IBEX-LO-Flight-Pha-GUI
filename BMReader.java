import java.io.*;
import java.util.StringTokenizer;

/**
* Read background monitor data on the fly
*
*/
public class BMReader {


		// we are going to keep two in memory as we parse through the times
	long firstD, secondD; // dates
	float firstC, secondC;  // counts
	public StringTokenizer st;
	public String line;

	private file bm_file;
	public static int NO_DATA = -2;

	/**
	* Initialize the searcher here in the constructor
	*/
	public BMReader() {
		firstD = 0;
		secondD = 0;
		firstC = 0;
		secondC = 0;
		bm_file=new file("bm_all_savg.txt");
		bm_file.initRead();
		loadNext();
		System.out.println(secondD + " ");
		System.out.println(secondC + " ");
	}


	/**
	*  This is called to get avg. background monitor over spin
	*    input date is in ms from 1970 - c ansi standard!
	*/
	public float getAvg(long date) {
		//System.out.println("calling BMR : " + date);

		if (date <= secondD & date >= firstD) {
			//System.out.println("found it right away: " + date + " " + secondD + " " + firstD);
			return firstC;
		}

		else if (date< firstD) {
			System.out.println("searching backwards");
			reLoad();
			return getAvg(date);
		}
		else {
			//System.out.println("Breader going forward!!");
			while(loadNext()) {
				if (date <= secondD & date >= firstD) {
					//System.out.println("found it later: " + date + " " + secondD + " " + firstD);
					return firstC; // it was in the next interval
				}

				else if (date< firstD) {
					System.out.println("searching backwards in later");
					reLoad();
					return getAvg(date);
				} // and if it's still higher... load the next!!
			}
			reLoad();
		}
        return -1;
	}



	/** Changing of the guard
	*  Read magnetic field info here...
	*/
	private boolean loadNext() {
		try{
			//zerothC = firstC;
			firstC = secondC;
			firstD = secondD;
			line = bm_file.readLine();
			st = new StringTokenizer(line);
			secondD = Long.parseLong(st.nextToken());
			secondC = Float.parseFloat(st.nextToken());
			//System.out.println("loadedr: " +  " " + secondD + " " + firstD);
			//System.out.println("loadedr: " +  " " + secondC + " " + firstC);
			return true;
		}
		catch (Exception e) {
			System.out.println("Done with B data");
			//e.printStackTrace();
			return false;
		}
	}


	private void reLoad() {
		System.out.println("called reload background monitor file");
		bm_file = new file("bm_all_savg.txt");
		bm_file.initRead();
		secondC = 0;
		secondD = 0;
		loadNext();
	}

	public static final void main(String[] args) {
		//1278135954445
		// for testing
		BMReader bmr = new BMReader();
		System.out.println("8.9 or so: " + bmr.getAvg(Long.parseLong("1231556111119")));
		System.out.println("8.9 or so: " + bmr.getAvg(Long.parseLong("1231556111120")));
		System.out.println("8.9 or so: " + bmr.getAvg(Long.parseLong("1231556111121")));
		System.out.println("8.9 or so: " + bmr.getAvg(Long.parseLong("1231557035118")));
	}


}
