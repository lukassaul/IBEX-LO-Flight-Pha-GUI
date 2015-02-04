import java.util.Random;
import java.util.Date;
import java.util.StringTokenizer;


// 0.0000E+000, 5.4858E-004,-1.0000E+000, 2.2000E+002, 9.0052E+001, 0.0000E+000,
//			-7.2782E+000, 3.6819E+000, 9.7432E+000, 1.0000E+000, 1.0000E+001
// 0.0000E+000, 5.4858E-004,-1.0000E+000, 2.2000E+002, 8.5943E+001, 0.0000E+000,
//			5.2012E+001, 5.5002E+001, 1.2645E+001, 1.0000E+000, 1.0000E+001
// 0.0000E+000, 5.4858E-004,-1.0000E+000, 2.2000E+002, 7.8101E+001, 0.0000E+000,
//			-3.6620E+000,-4.4667E+000, 1.5900E+001, 1.0000E+000, 1.0000E+001


// birth time, mass,           charge,       x,            y,            z
//			azimuth,      elevation,       ev,         cwf,      color


//       randomE = RANDOMU(seed) * A2
//       randomy = RANDOMU(seed)
//       fE = A3 * randomE / (randomE^A4 + A5)  ;home made function!

//A1=2
//A2=20
//A3=5.66
//A4=2.3
//A5=6.4

/**
* Based on IDE code with following header:
*;====================================================================
*; Modified:  D.F. Heirtzler 6 Jun 2002
*; 06-11-2002 : added external file read for all constants
*;
*; created:  A.B. Galvin, 1 Nov 2001 based on another program
*;           "generatestartelectrons.pro" by
*;                  Frédéric Allegrini, 15.05.2000
*;
* Create start electrons here copying IDL style code but w/ more controls
*
*  Creating other specific e- distributions here in position and angle.
*
*
*  Now we are taking as an input file the spalt output from the ESA simulations-
*   -  courtesy Martin Wieser
*
*/
public class StartESA3 {

	public int n = 350;
	public double startX = 0;
	public double minY = 17;
	public double maxY = 25;
	public double yMid = 19;
	public double ySigma = 3;
	public double midE = 10;
	public double eMin =0;
	public double eMax = 20;
	public double eSigma = 5;
	public double maxAngle = 40;
	public double angleSigma = 20;
	public double m = 0.000054858;
	public double q = -1.0;
	public String filename = "1f_fromESA1.ion";
	public String inputFileName = "ak7_20kV_mp33mm_bin1_splat.log.splat";
	public double cwf=1.0;
	public double pen=10.0;
	public double gridsPerMM = 4.0;

	public Random r;
	public double x,y,z,az,el,ev,az_e,el_e,ev_e;
	public int counter, counterMiss;

	public StartESA3() {
		file inF = new file(inputFileName);
		inF.initRead();
		String garbage = inF.readLine(); // throw out 1st line of splat file (titles)
		counter = 0;
		counterMiss = 0;

		file outF = new file(filename);
		outF.initWrite(false);
		file testElF = new file("test_elevations.dat");
		testElF.initWrite(false);
	    r = new Random(new Date().getTime()); // initialize random # generator w/ system clock
		boolean eof = false;
		String line = "";
		StringTokenizer st;
		while ((line=inF.readLine())!=null) {
			counter++;
			st = new StringTokenizer(line,";");
			for (int i=0;i<7;i++) garbage=st.nextToken(); // 1st 7 are garbage
			x=Double.parseDouble(st.nextToken());
			y=Double.parseDouble(st.nextToken());
			z=Double.parseDouble(st.nextToken());
			double rad = Math.sqrt(y*y+z*z);
			garbage=st.nextToken(); // azimuth
			el=Double.parseDouble(st.nextToken());
			testElF.write(el+"\t"+(rad + startX*Math.tan(el*Math.PI/180.0)) + "\n");

			// we need to see where this guy is going to wind up on the 2nd foil..
			//a bit of trig
			//double rad2 = rad + startX*Math.tan(el*Math.PI/180.0);

			az_e = nextAngle()*180.0/Math.PI;
			el_e = nextAngle()*180.0/Math.PI;
			ev_e = nextEnergy();

			double ran2 = r.nextDouble();

			// check if we should include this one:
			if (el>-9.0 && el<5.0 && rad>35.75 && rad<44.75 && /*rad2>31.8 && rad2<46.9 && */ran2<1.0) {
				outF.write(0.0+", "+ m +", "+q+", "+(startX*gridsPerMM+4.0) +", "+rad*gridsPerMM+", "
			                 + 0.0 +", "+az_e+", "+el_e+", "+ev_e+", "+cwf+", "+pen+"\n");
			}
			else {
				counterMiss++;
			}
				// birth time, mass,           charge,       x,            y,            z
				//			azimuth,      elevation       ev,         cwf,      color
		}
		outF.closeWrite();
		inF.closeRead();
		testElF.closeWrite();
		System.out.println("total events: " + counter);
		System.out.println("missed events: " + counterMiss);
	}

	/**
	* To simulate energy of e- ejected as ion passes through foil.
	*
	*/
	public double homeMadeFunction(double e) {
		//double A3=5.66;
		//double A4=2.3;
    	//double A5=6.4;
		return 5.66*e/(Math.pow(e,2.3) + 6.4);
	}

	/**
	*  Next randomly distributed energy -
	*    to fit observations of energy distribution outside thin foil
	*/
	public double nextEnergy() {
		double ran = r.nextDouble();
		double energy = r.nextDouble()*maxEnergy;
		while (homeMadeFunction(energy)<ran) {
    		ran = r.nextDouble();
			energy = r.nextDouble()*maxEnergy;
		}
		return energy;
	}
	public double maxEnergy = 15; // ev

    /**
    *  The cosine of the angle is random, not the angle itself
    *    (cosine distribution)
    */
	public double nextAngle() {
		double angle = (2*r.nextDouble()-1.0)*Math.PI/2.0;
		double ran = r.nextDouble();
		while (Math.cos(angle)<ran) {
			angle = (2*r.nextDouble()-1.0)*Math.PI/2.0;
			ran = r.nextDouble();
		}
		return angle;
	}

	public static void main(String[] args) {
		//System.out.println("tan -.25 , +.25" + Math.tan(-0.25) + " " + Math.tan(0.25));
		StartESA3 se = new StartESA3();
	}
}