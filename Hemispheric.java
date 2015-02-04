import java.util.*;
import cern.jet.math.Bessel; // use cern library for i0 and j0

/**
* Use this to model PUI distribution & observed eflux a la
*
*  Isenberg, JGR, 102, A3, 4719-4724, 1997
*
*
*  Assume spacecraft samples only antisunwardest hemisphere,
*  field near radial...
*
*/
public class Hemispheric {

	public static double AU  = 1.49598* Math.pow(10,11); //meters
	public static double NaN = Double.NaN;
	public static double MAX = Double.MAX_VALUE;
	public static double U   = 440*1000;
	public static double N0  = Math.pow(10,-6);
	public static double G = 6.673 * Math.pow(10,-11);  // m^3/s^2/kg
	public static double Ms = 1.98892 * Math.pow(10,30);  // kg
	//public static double gamma = -3.0/2.0;  // energy dependence in eflux ??

	// The diffusion coefficient for cross hemispheric transport
	double D = 0.0;
	double gamma;
	public static double beta = 3*Math.pow(10,-8);
	public static double PI = Math.PI;
	MultipleIntegration mi;
	/**
	* THe velocity of a cold interstellar particle at earth.  Calculate in constructor.
	*/
	public double v_earth;
	public static double v_infinity = 27500.0;  //m/s

	//Integration ig;

	public Hemispheric() {
		this(0.0);
	}

	public Hemispheric(double gg) {
		gamma = gg;
		// we need to do a numeric integral
		mi = new MultipleIntegration();
		//ig = new Integration();

		// calculate v_earth from energy conservation
		v_earth = Math.sqrt(2*G*Ms/AU + v_infinity*v_infinity);
		System.out.println("v_earth: " + v_earth);

	}

	public double eflux(final double _D, final double norm, final double w2) {
		double nrm = 3*beta*AU*AU/8/PI/AU/U/U/U/U*N(AU);
		double tbr = eflux(_D,norm/nrm,1.0,w2);
		//System.out.println("eflux: " + _D + "\t" + norm + "\t" + w2 + " " + tbr);
		return tbr;
	}

	/**
	*calculate EFlux at r in w = v/ v_sw
	*/
	public double eflux(final double _D, final double norm, final double gg, final double w2) {
		gamma = gg;
		return f(AU,_D,norm,w2)*Math.pow((w2),gamma);
	}

	public double f(final double r, final double _D, final double w) {
		return f(r,_D,1.0,w);
	}

	/**
	*  The main portion of this class..
	*    here we calculate the f+ distribution analytically (except for one numeric
	*    integration).
	*/
	public double f(final double r, final double _D, final double norm, final double w) {
		if (w>=1.0) return 0.0;

		// establish some variables
		D = _D;
		final double a   = 3*(1-w)/4;
		final double b   = r*Math.pow(w,3/2);

		// first we need to do an integral.  Establish the integrand.
		FunctionI integrand = new FunctionI () {

			public double function(double z) {
				double phi;
				if (1-D*D > 0) {
					phi = Math.sqrt(z*(2*a-z)*(1-D*D));
					return N(b*Math.exp(z-a)) * Bessel.j0(phi);
				}

				else {
					phi = Math.sqrt(z*(2*a-z)*(D*D-1));
					return N(b*Math.exp(z-a)) * Bessel.i0(phi);
				}
			}

		};
			//double phi = Math.sqrt(a/2*(2*a-a/2)*(1-D*D));
	//	System.out.println("int: " + N(r*b*Math.exp(a-a/2)) );
	//	System.out.println("int: " + Math.sqrt((2*a-a/2)/a/2) );
	//	System.out.println("int: " + phi);
	//	System.out.println("int: " + j0(phi));
	//	System.out.println("integrand(a/2): " + integrand.function(a/2));
		double integral = mi.integrate(integrand, 0.0, 2*a);

	//	ig.setFunction(integrand);
	//	ig.setMaxError(10);
	//	double integral = ig.integrate(0.01,2*a-0.01);

		double factor = 3*beta*AU*AU/8/PI/U/U/U/U * (D+1)/b * Math.exp(-a*D);

		return factor*integral*norm;
	}

	/**
	*  Taken from cern.jet.math, a fast Bessl Algorithm here.
	*/
	/*static public double j0(double x) {
		double ax;

		if( (ax=Math.abs(x)) < 8.0 ) {
		   double y=x*x;
		   double ans1=57568490574.0+y*(-13362590354.0+y*(651619640.7
					   +y*(-11214424.18+y*(77392.33017+y*(-184.9052456)))));
		   double ans2=57568490411.0+y*(1029532985.0+y*(9494680.718
					   +y*(59272.64853+y*(267.8532712+y*1.0))));

		   return ans1/ans2;

		}
		else {
		   double z=8.0/ax;
		   double y=z*z;
		   double xx=ax-0.785398164;
		   double ans1=1.0+y*(-0.1098628627e-2+y*(0.2734510407e-4
					   +y*(-0.2073370639e-5+y*0.2093887211e-6)));
		   double ans2 = -0.1562499995e-1+y*(0.1430488765e-3
					   +y*(-0.6911147651e-5+y*(0.7621095161e-6
					   -y*0.934935152e-7)));

		   return Math.sqrt(0.636619772/ax)*
				  (Math.cos(xx)*ans1-z*Math.sin(xx)*ans2);
		}
	}*/


	/**
	* The model of interstellar neutral density..
	*
	*   based on no inflow, e.g. no angulage dependence just
	*   ionization depletion.
	*/
	/*private static double N(double r) {
		double A = 4.0*AU; // one parameter model
		return N0*Math.exp(-A/r);
	}*/


	/**
	* The model of interstellar neutral density..
	*
	*   based on cold model, due upwind..
	*   see Moebius SW&CR homework set #4!
	*/
	private double N(double r) {

		return N0*Math.exp(-beta*AU*AU*Math.sqrt(v_infinity*v_infinity+2*G*Ms/r)/G/Ms);

		//return N0*Math.exp(-2*beta*AU*AU/r/v_infinity);
	}




	/**
	* For Testing
	*/
	public static final void main(String[] args) {
		file q;
		System.out.println(""+2*beta*AU*AU/v_infinity/AU);
		System.out.println(""+beta*AU*AU*Math.sqrt(v_infinity*v_infinity+2*G*Ms/AU)/G/Ms);

		// test bessel function:
	/*	q = new file("besselTest.dat");
		q.initWrite(false);
		for (double i=-5; i<5; i+=.1) {
			try {
				q.write(i+"\t"+Bessel.i0(i)+"\n");
			} catch (Exception e) {e.printStackTrace();}
		}
		q.closeWrite(); */

		// a series of outputs of the model varying parameters..
		double[] dd = {6.73,4.92,3.92,2.61,1.40,0.61};
		double[] nn = {15776,14724,14185,14144,14643,19739};
		file f = new file("hemis_0306_out.dat");
		f.initWrite(false);
		int index = 0;
		Hemispheric h = new Hemispheric();
		for (double w=0.0; w<=1.0; w+=0.02) {
			index=0;
			f.write(w+"\t");
			while (index<6) {
				f.write(h.eflux(dd[index],nn[index],w)+"\t");
				index++;
			}
			f.write("\n");
		}
		f.closeWrite();

		// single output
		//double norm = 3*beta*AU*AU/8/PI/AU/U/U/U/U*N(AU);
		/*double a=0.586;
		double b=19830.0;
		Hemispheric h = new Hemispheric();
		q = new file("hemisTest.dat");
		q.initWrite(false);
		for (double w=0.01; w<1.01; w+=.01) {
			try {
				q.write((w+1.0)+"\t"+h.eflux(a,b,w)+"\n");
				//q.write((w+1.0)+"\t"+h.f(AU,1.0,1.0,w)+"\n");
			} catch (Exception e) {e.printStackTrace();}
		}
		q.closeWrite();*/
	}

}

