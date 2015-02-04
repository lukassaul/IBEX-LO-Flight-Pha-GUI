import java.util.Random;
import java.util.Date;

/**
* Based on IDE code with following header:
*;====================================================================
*; Modified:  D.F. Heirtzler 6 Jun 2002
*; 06-11-2002 : added external file read for all constants
*;
*; created:  A.B. Galvin, 1 Nov 2001 based on another program
*;           "generatestartelectrons.pro" by
*;                  Frédéric Allegrini, 15.05.2000
*
* Create start electrons here copying IDL style code but w/ more controls
*
*  Creating other specific e- distributions here in position and angle.
*
*
*  This version to simulate ion feedback initiation in the ESA/TOF interface
*
*
*
*/
public class FoilIons  {

	public int n = 100; // at each source location
	public double startX = 55;
	public double minY = 17;
	public double maxY = 25;
	public double yMid = 19;
	public double ySigma = 3;
	public double midE = 10;
	public double eMin =0;
	public double eMax = 20;
	public double eSigma = 5;
	public double maxAngle = 180;
	public double angleSigma = 45;
	public double me = 0.00054858;
	public double mH = 1.0;
	public double mC = 12.0;
	public double mO = 16.0;
	public double q = -1.0;
	public String filename = "foil_ions.ion";
	public double cwf=1.0;
	public double pen=10.0;
	public double gridsPerMM = 8.0;
	public double y,x,az,el,ev;

	public FoilIons() {
		file outF = new file(filename);
		outF.initWrite(false);
	    Date d1 = new Date();
	    Random r = new Random(d1.getTime());

		// ions sputtered from the foil..  1st foil first
		x=0.02;
		y=36.0;
		for (int i=0; i<n; i++) {
			//x += (42.0-37.5)/(double)n;
			y += (44.75-36)/(double)n;
			az = r.nextGaussian()*angleSigma;
			while (Math.abs(az)>maxAngle) {
				az = r.nextGaussian()*angleSigma;
			}
			el = r.nextGaussian()*angleSigma;
			while (Math.abs(el)>maxAngle) {
				el = r.nextGaussian()*angleSigma;
			}
			ev = r.nextGaussian()*eSigma;
			while (ev>eMax | ev<eMin) {
				ev = r.nextGaussian()*eSigma;
			}
			outF.write(0.0+", "+ me +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+2 +"\n");
		}x=0.02;
		y=36.0;
		for (int i=0; i<n; i++) {
			//x += (42.0-37.5)/(double)n;
			y += (44.75-36)/(double)n;
			az = r.nextGaussian()*angleSigma;
			while (Math.abs(az)>maxAngle) {
				az = r.nextGaussian()*angleSigma;
			}
			el = r.nextGaussian()*angleSigma;
			while (Math.abs(el)>maxAngle) {
				el = r.nextGaussian()*angleSigma;
			}
			ev = r.nextGaussian()*eSigma;
			while (ev>eMax | ev<eMin) {
				ev = r.nextGaussian()*eSigma;
			}
			outF.write(0.0+", "+ mC +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+3 +"\n");
		}x=0.02;
		y=36.0;
		for (int i=0; i<n; i++) {
			//x += (42.0-37.5)/(double)n;
			y += (44.75-36)/(double)n;
			az = r.nextGaussian()*angleSigma;
			while (Math.abs(az)>maxAngle) {
				az = r.nextGaussian()*angleSigma;
			}
			el = r.nextGaussian()*angleSigma;
			while (Math.abs(el)>maxAngle) {
				el = r.nextGaussian()*angleSigma;
			}
			ev = r.nextGaussian()*eSigma;
			while (ev>eMax | ev<eMin) {
				ev = r.nextGaussian()*eSigma;
			}
			outF.write(0.0+", "+ mO +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10 +"\n");
		}

		// 2nd foil now..
		x=27.8;
		y=31.5;
		for (int i=0; i<n; i++) {
			//x += (42.0-37.5)/(double)n;
			y += (47.15-31.5)/(double)n;
			az = r.nextGaussian()*angleSigma;
			while (Math.abs(az)>maxAngle) {
				az = r.nextGaussian()*angleSigma;
			}
			el = r.nextGaussian()*angleSigma;
			while (Math.abs(el)>maxAngle) {
				el = r.nextGaussian()*angleSigma;
			}
			ev = r.nextGaussian()*eSigma;
			while (ev>eMax | ev<eMin) {
				ev = r.nextGaussian()*eSigma;
			}
			outF.write(0.0+", "+ me +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+2 +"\n");
		}
		x=27.8;
		y=31.5;
		for (int i=0; i<n; i++) {
			//x += (42.0-37.5)/(double)n;
			y += (47.15-31.5)/(double)n;
			az = r.nextGaussian()*angleSigma;
			while (Math.abs(az)>maxAngle) {
				az = r.nextGaussian()*angleSigma;
			}
			el = r.nextGaussian()*angleSigma;
			while (Math.abs(el)>maxAngle) {
				el = r.nextGaussian()*angleSigma;
			}
			ev = r.nextGaussian()*eSigma;
			while (ev>eMax | ev<eMin) {
				ev = r.nextGaussian()*eSigma;
			}
			outF.write(0.0+", "+ mC +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+3 +"\n");
		}
		x=27.8;
		y=31.5;
		for (int i=0; i<n; i++) {
			//x += (42.0-37.5)/(double)n;
			y += (47.15-31.5)/(double)n;
			az = r.nextGaussian()*angleSigma;
			while (Math.abs(az)>maxAngle) {
				az = r.nextGaussian()*angleSigma;
			}
			el = r.nextGaussian()*angleSigma;
			while (Math.abs(el)>maxAngle) {
				el = r.nextGaussian()*angleSigma;
			}
			ev = r.nextGaussian()*eSigma;
			while (ev>eMax | ev<eMin) {
				ev = r.nextGaussian()*eSigma;
			}
			outF.write(0.0+", "+ mO +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10 +"\n");
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


	double A3=5.66;
	double A4=2.3;
    double A5=6.4;

	public static void main(String[] args) {
		FoilIons se = new FoilIons();
	}
}