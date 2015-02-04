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
public class ESAInterfaceElectrons  {

	public int n = 30; // at each source location
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
	public double m = 0.00054858;
	public double m2 = 1.0;
	public double q = -1.0;
	public String filename = "entrance_electrons.ion";
	public double cwf=1.0;
	public double pen=10.0;
	public double gridsPerMM = 8.0;
	public double y,x,az,el,ev;

	public ESAInterfaceElectrons() {
		file outF = new file(filename);
		outF.initWrite(false);
	    Date d1 = new Date();
	    Random r = new Random(d1.getTime());

		// high energy elecrons from esa entrance

		x=37.7;
		y=31.6;
		for (int i=0; i<n; i++) {
			x += (42.0-37.5)/(double)n;
			y += (33.6-31.6)/(double)n;
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

			outF.write(0.0+", "+ m +", "+ q +", " + (x)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+2 +"\n");
		}
		x=37.7;
		y=31.6;
		for (int i=0; i<n; i++) {
			x += (42.0-37.5)/(double)n;
			y += (33.6-31.6)/(double)n;
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
			outF.write(0.0+", "+ m2 +", "+ q +", " + (x)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+3 +"\n");
		}

		x=42.0;
		y=47.0;
		for (int i=0; i<n; i++) {
			x += (37.5-42.0)/(double)n;
			y += (59.0-47.0)/(double)n;
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
			outF.write(0.0+", "+ m +", "+ q +", " + (x)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+2 +"\n");
		}
		x=42.0;
		y=47.0;
		for (int i=0; i<n; i++) {
			x += (37.5-42.0)/(double)n;
			y += (59.0-47.0)/(double)n;
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
			outF.write(0.0+", "+ m2 +", "+ q +", " + (x)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+3 +"\n");
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
		ESAInterfaceElectrons se = new ESAInterfaceElectrons();
	}
}