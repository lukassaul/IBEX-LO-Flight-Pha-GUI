import java.util.Random;
import java.util.Date;
import java.util.StringTokenizer;



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
*/
public class StartEWithColor {

	public int n = 1000;
	public double startX = 0;
	public double minY = 35.75;
	public double maxY = 44.75;
	public double midE = 10;
	public double m = 0.000054858;
	public double q = -1.0;
	public String filename = "2f_ran_c.ion";
	public double cwf=1.0;
	public double pen=10.0;
	public double gridsPerMM = 8.0;

	public Random r;
	public double x,y,z,az,el,ev,az_e,el_e,ev_e;
	public int counter, counterMiss;

	public StartEWithColor () {

		counter = 0;
		counterMiss = 0;

		file outF = new file(filename);
		outF.initWrite(false);
	    r = new Random(new Date().getTime()); // initialize random # generator w/ system clock
		boolean eof = false;
		String line = "";
		StringTokenizer st;
		for (int i=0; i<n; i++) {
			counter++;
			double ySize = maxY-minY;
			y=ySize*r.nextDouble()+minY;

			az_e = nextAngle()*180.0/Math.PI;
			el_e = nextAngle()*180.0/Math.PI;
			ev_e = nextEnergy();

			if (ev_e>10) pen=9;
			else pen=10;

			double ran2 = 30*r.nextDouble();

			// check if we should include this one:
			outF.write(0.0+", "+ m +", "+q+", "+(startX*gridsPerMM+gridsPerMM) +", "+y*gridsPerMM+", "
			                 + 0.0 +", "+az_e+", "+el_e+", "+ev_e+", "+cwf+", "+pen+"\n");

				// birth time, mass,           charge,       x,            y,            z
				//			azimuth,      elevation       ev,         cwf,      color
		}
		outF.closeWrite();
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
		StartEWithColor  se = new StartEWithColor ();
	}
}