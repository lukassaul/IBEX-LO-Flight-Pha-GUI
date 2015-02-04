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
*  This version is to look for trajectories of secondary ions..
*   which could be a source of background
*
*/
public class EntranceElectrons  {

	public int n = 64; // at each source location
	public double startX = 55;
	public double minY = 17;
	public double maxY = 25;
	public double yMid = 19;
	public double ySigma = 3;
	public double midE = 10;
	public double eMin =0;
	public double eMax = 20;
	public double eSigma = 5;
	public double maxAngle = 360;
	public double angleSigma = 180;
	public double m = 0.000054858;
	public double q = -1.0;
	public String filename = "fin_secondaries.ion";
	public double cwf=1.0;
	public double pen=10.0;
	public double gridsPerMM = 8.0;



	public double y,x,az,el,ev;

	public Secondaries() {
		file outF = new file(filename);
		outF.initWrite(false);
	    Date d1 = new Date();
	    Random r = new Random(d1.getTime());
	    /*
		for (int i=0; i<n; i++) {
			y = 190;
			x=18;
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
			outF.write(0.0+", "+ 16 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+8+"\n");
		}
		for (int i=0; i<n; i++) {
			y = 190;
			x=18;
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
			outF.write(0.0+", "+ 1 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10+"\n");
		}
		for (int i=0; i<n; i++) {
			x = 34;
			y = 199.7;
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
			outF.write(0.0+", "+ 16 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+8+"\n");
		}
		for (int i=0; i<n; i++) {
			x = 34;
			y = 199.7;
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
			outF.write(0.0+", "+ 1 +", "+ q +", " + x+", " + y +", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10+"\n");
		}
		for (int i=0; i<n; i++) {
			x = 153.2;
			y = 103.4;
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
			outF.write(0.0+", "+ 16 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+8+"\n");
		}
		for (int i=0; i<n; i++) {
			x = 153.2;
			y = 103.4;
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
			outF.write(0.0+", "+ 1 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10+"\n");
		}
		y=187;
		for (int i=0; i<n; i++) {
			x = 96.7;
			y += (210-187)/(double)n;
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
			outF.write(0.0+", "+ 16 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+8+"\n");
		}
		y=187;
		for (int i=0; i<n; i++) {
			x = 96.7;
			y += (210-187)/(double)n;
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
			outF.write(0.0+", "+ 1 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10+"\n");
		}
		y=211;
		for (int i=0; i<n; i++) {
			x = 155;
			x += (170.4-97)/(double)n;
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
			outF.write(0.0+", "+ 16 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+8+"\n");
		}
		y=211;
		for (int i=0; i<n; i++) {
			y = 211;
			x += (170-97)/(double)n;
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
			outF.write(0.0+", "+ 1 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10+"\n");
		}
		y=95;
		for (int i=0; i<n; i++) {
			x = 180;
			y += (107-95)/(double)n;
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
			outF.write(0.0+", "+ 16 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+8+"\n");
		}
		y=95;
		for (int i=0; i<n; i++) {
			x = 180;
			y += (107-95)/(double)n;
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
			outF.write(0.0+", "+ 1 +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10+"\n");
		}
		*/
		// and secondary electrons from MCP
		/*
		y=112;
		for (int i=0; i<n; i++) {
			x = 203.4;
			y += (196-112)/(double)n;
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
			outF.write(0.0+", "+ m +", "+ q +", " + x+", " + y+", "
			                 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10 +"\n");
		}

		*/

		// fin secondary electrons from lower chamber
		x= 28.0;
		for (int i=0; i<n; i++) {
			y = 47.2;
			x += (30.6-28.0)/(double)n;
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
			outF.write(0.0+", "+ m +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10 +"\n");
		}
		y= 48.0;
		for (int i=0; i<n; i++) {
			x = 34.8;
			y += (49.0-48.0)/(double)n;
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
			outF.write(0.0+", "+ m +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10 +"\n");
		}
		y= 48.0;
		for (int i=0; i<n; i++) {
			x = 37.9;
			y += (49.0-48.0)/(double)n;
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
			outF.write(0.0+", "+ m +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10 +"\n");
		}
		x= 39.4;
		for (int i=0; i<n; i++) {
			y = 49.0;
			x += (41.0-39.4)/(double)n;
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
			outF.write(0.0+", "+ m +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
							 + 0.0 +", "+az+", "+el+", "+ev+", "+cwf+", "+10 +"\n");
		}
		x= 41.8;
		for (int i=0; i<n; i++) {
			y = 49.0;
			x += (44.0-41.8)/(double)n;
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
			outF.write(0.0+", "+ m +", "+ q +", " + (x+1)*gridsPerMM+", " + y*gridsPerMM+", "
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
		Secondaries se = new Secondaries();
	}
}