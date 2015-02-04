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
*;
* Create start electrons here copying IDL style code but w/ more controls
*
*  Creating other specific e- distributions here in position and angle.
*
*
*/
public class StartE {

	public int n = 500;
	public double startX = 28.8;
	public double minY = 31.5;
	public double maxY = 47.1;
	public double yMid = 39.1;
	public double ySigma = 3;
	public double midE = 10;
	public double eMin =0;
	public double eMax = 20;
	public double maxEnergy = 20;
	public double eSigma = 5;
	public double maxAngle = 40;
	public double angleSigma = 20;
	public double m = 0.000054858;
	public double q = -1.0;
	public String filename = "start_e_second.ion";
	public double cwf=1.0;
	public double pen=10.0;
	public double gridsPerMM = 8.0;

	public double y,az,el,ev;
	public Random r;

	public StartE() {
		file outF = new file(filename);
		outF.initWrite(false);
	    Date d1 = new Date();
	    r = new Random(d1.getTime());
		for (int i=0; i<n; i++) {
			y= ySigma*r.nextGaussian()+yMid;
			while (y>maxY | y<minY) {
				y= ySigma*r.nextGaussian()+yMid;
			}
			//az = 0.0;
			//el = 0.0;
			az = nextAngle()*180.0/Math.PI;
			el = nextAngle()*180.0/Math.PI;
			//az = r.nextGaussian()*angleSigma;
			//while (Math.abs(az)>maxAngle) {
			//	az = r.nextGaussian()*angleSigma;
			//}
			//el = r.nextGaussian()*angleSigma;
			//while (Math.abs(el)>maxAngle) {
			//	el = r.nextGaussian()*angleSigma;
			//}
			ev = nextEnergy();
			//ev = 1/16.0*m*16000;
			//while (ev<eMin | ev>eMax) {
			//	ev = nextEnergy();
				//ev = r.nextGaussian()*eSigma+midE;
			//}
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
	double A3=5.66;
	double A4=2.3;
    double A5=6.4;
	public double homeMadeFunction(double e) {
		return A3*e/(Math.pow(e,A4) + A5);
	}

	//angle = RANDOMU(seed) * !PI / 2
	//Y = RANDOMU(seed)
	//ENDREP UNTIL (Y LE COS(angle))


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
		StartE se = new StartE();
	}
}