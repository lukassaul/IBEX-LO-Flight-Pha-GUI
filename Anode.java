import java.util.Random;
import java.util.Date;


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
*/
public class StartE {

	public int n = 350;
	public double startX = 55;
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
	public String filename = "anode_inner_max40deg.ion";
	public double cwf=1.0;
	public double pen=10.0;
	public double gridsPerMM = 4.0;



	public double y,az,el,ev;

	public StartE() {
		file outF = new file(filename);
		outF.initWrite(false);
	    Date d1 = new Date();
	    Random r = new Random(d1.getTime());
		for (int i=0; i<n; i++) {
			y= ySigma*r.nextGaussian()+yMid;
			while (y>maxY | y<minY) {
				y= ySigma*r.nextGaussian()+yMid;
			}
			az = r.nextGaussian()*angleSigma;
			while (Math.abs(az)>maxAngle) {
				az = r.nextGaussian()*angleSigma;
			}
			el = r.nextGaussian()*angleSigma;
			while (Math.abs(el)>maxAngle) {
				el = r.nextGaussian()*angleSigma;
			}
			ev = r.nextGaussian()*eSigma+midE;
			while (ev<eMin | ev>eMax) {
				ev = r.nextGaussian()*eSigma+midE;
			}
			outF.write(0.0+", "+ m +", "+ q +", " + startX*gridsPerMM+", " + y*gridsPerMM+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+pen+"\n");
				// birth time, mass,           charge,       x,            y,            z
				//			azimuth,      elevation       ev,         cwf,      color
		}
		outF.closeWrite();
	}

	/**
	* To simulate energy of e- ejected as ion passes through foil.
	*
	*/
	public double homeMadeFunction(double e) {
		return A3*e/(Math.pow(e,A4) + A5);
	}

	//angle = RANDOMU(seed) * !PI / 2
	//Y = RANDOMU(seed)
	//ENDREP UNTIL (Y LE COS(angle))



	double A3=5.66
	double A4=2.3
    double A5=6.4

	public static void main(String[] args) {
		StartE se = new StartE();
	}
}