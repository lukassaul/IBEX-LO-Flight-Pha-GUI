import java.lang.Math;
import java.util.Date;
//import gaiatools.numeric.integration.Simpson;
import flanagan.integration.*;
//import drasys.or.nonlinear.*; // our friend mr. simpson resides here

/**
*  This class should take care of doing multi-dimensional numeric integrations.
*  This will be slow!!
*
*   Actually not half bad...
*
*
*  Lukas Saul
*  UNH Physics
*  May, 2001
*
*   Updated Aug. 2003 - only works with 2D integrals for now...
*
*   Updated Aug. 2004 - 3D integrals OK!  Did that really take 1 year??
*
*   About 2.1 seconds to integrate e^(-x^2-y^2-z^2) !!
*/
public class MultipleIntegration {

	private double result;
	public int npoints=32;
	//private Simpson s; // tool to do integrations
	/**
	* for testing - increments at every 1D integration performed
	*/
	public long counter, counter2;

	/**
	* Constructor just sets up the 1D integration routine for running
	*  after which all integrations may be called.
	*/
	public MultipleIntegration() {
		counter = 0;
		counter2 = 0;
		result = 0.0;
		//s=new Simpson();
		//s.setErrMax(0.001);
	//	s.setMaxIterations(15);
	}

	/**
	* sets counter to zero
	*/
	public void reset() {
		counter = 0;
	}
	public void reset2() {
		counter2 = 0;
	}

	/**
	* Set accuracy of integration
	*/
	public void setEpsilon(double d) {
		//s.setErrMax(d);
	}

	/**
	* Here's the goods, for 3D integrations.
	*
	* Limits are in order as folows:  zlow, zhigh, ylow, yhigh, xlow, xhigh
	*
	*/
	public double integrate(final FunctionIII f, final double[] limits) {
		reset();
		System.out.println("Called 3D integrator");
		System.out.println("Integrating from: \n"+limits[0]+" "+limits[1]+
					"\n"+limits[2]+" "+limits[3]+"\n"+limits[4]+" "+limits[5]);
		double[] nextLims = new double[4];
		for (int i=0; i<4; i++) {
			nextLims[i] = limits[i+2];
		}

		final double[] nextLimits = nextLims;

		FunctionI funcII = new FunctionI () {
			public double function(double x) {
				return integrate(f.getFunctionII(2,x), nextLimits);
			}
		};
		try {
			//s.setInterval(limits[0],limits[1]);
			//result=s.getResult(funcII);
			result = Integration.gaussQuad(funcII,limits[0],limits[1],npoints);
			//result = integrate(funcII, limits[0], limits[1]);
			return result;
			// each call to funcII by the integrator does a double integration
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	/**
	* Here's the goods, for 2D integrations
	*/
	public double integrate(final FunctionII f, final double[] limits) {
		//System.out.println("called 2D integrator");
		//System.out.println("Integrating from: \n"+limits[0]+" "+limits[1]+
		//			"\n"+limits[2]+" "+limits[3]);
		//reset2();
		//if (limits.length!=4) return null;
		//System.out.println("called 2D integrator");
		FunctionI f1 = new FunctionI() {
			public double function(double x) {
				return integrate(f.getFunction(1,x),limits[2],limits[3]);
			}
		};
		try{
			//s.setInterval(limits[0],limits[1]);
			//result=s.getResult(f1);
			result = Integration.gaussQuad(f1,limits[0],limits[1],npoints);
			//result = integrate(f1, limits[0], limits[1]);
			//System.out.println("in2d - result: " + result + " " + counter);
			return result;
			// each call to f1 by the intgrator does an integration
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}


	/**
	* Here's the simple goods, for 1D integrations
	*   courtesy of our friends at drasys.or.nonlinear
	*/
	public double integrate(final FunctionI f, double lowLimit, double highLimit) {
		//System.out.println("Called 1D integrator");
		try {
			counter2++;
			counter++;
			if (counter%10000==1) System.out.println("Counter: " + counter);

			//s.setInterval(lowLimit,highLimit);
			//return s.getResult(f);
			return Integration.gaussQuad(f,lowLimit,highLimit,npoints);
			//return result;
			//return s.getResult(f,lowLimit,highLimit);
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	* Just for testing only here!!!
	*
	* seems to work - may 3, 2001
	*
	*  lots more testing for limits of 3d , polar vs. cartesian -  Oct. 2004
	*/
	public static final void main(String[] args) {

		MultipleIntegration mi = new MultipleIntegration();

		// some functions to integrate!!

		FunctionI testf3 = new FunctionI() {
			public double function(double x) {
				return Math.exp(-x*x);
			}
		};
		FunctionI testf4 = new FunctionI() {
			public double function(double x) {
				return x*x*x;
			}
		};
		FunctionIII testf = new FunctionIII () {
			public double function3(double x, double y, double z) {
				return (Math.exp(-(x*x+y*y+z*z)));
			}
		};
		FunctionIII testlims = new FunctionIII () {
			public double function3(double x, double y, double z) {
				return (x*y*y*z*z*z);
			}
		};
		FunctionIII testfs = new FunctionIII () {
			public double function3(double r, double p, double t) {
				return (r*r*Math.sin(t)*Math.exp(-(r*r)));
			}
		};
		FunctionII testf2a = new FunctionII () {
			public double function2(double x, double y) {
				return (Math.exp(-(x*x+y*y)));
			}
		};
		FunctionII testf2b = new FunctionII () {
			public double function2(double r, double t) {
				return r*Math.exp(-(r*r));
			}
		};
		FunctionII testf2 = testf.getFunctionII(0,0.0);
		// these should be the same!!  compare z=0 in above..  test here...
		System.out.println("test2val: " + testf2.function2(0.1,0.2));
		System.out.println("test2aval: " + testf2a.function2(0.1,0.2));


		Date d5 = new Date();
		double test3 = mi.integrate(testf4,0.0,2.0);
		double test4 = mi.integrate(testf3,-10.0,10.0);
		Date d6 = new Date();

		System.out.println("Answer x^3 0->2: " + test3);
		System.out.println("Answer e^-x^2: " + test4);
		System.out.println("answer^2: " + test4*test4);
		System.out.println("took: " + (d6.getTime()-d5.getTime()));
		System.out.println("1D integrations: " + mi.counter);


		double[] lims2 = new double[4];
		lims2[0]=-100.0;
		lims2[1]=100.0;
		lims2[2]=-100.0;
		lims2[3]=100.0;

		Date d3 = new Date();
		double test2 = mi.integrate(testf2,lims2);
		Date d4 = new Date();

		System.out.println("Answer frm 2d testf2: " + test2);
		System.out.println("took: " + (d4.getTime()-d3.getTime()));
		System.out.println("1D integrations: " + mi.counter);


		d3 = new Date();
		test2 = mi.integrate(testf2a,lims2);
		d4 = new Date();
		System.out.println("Answer frm 2d testf2a: " + test2);
		System.out.println("took: " + (d4.getTime()-d3.getTime()));
		System.out.println("1D integrations: " + mi.counter);



		System.out.println("trying polar 2d now");
		lims2 = new double[4];
		lims2[0]=0;
		lims2[1]=2*Math.PI;
		lims2[2]=0;
		lims2[3]=10;

		d3 = new Date();
		double ttest = mi.integrate(testf2b,lims2);
		d4 = new Date();

		System.out.println("2d polar Answer: " + ttest);
		System.out.println("took: " + (d4.getTime()-d3.getTime()));
		System.out.println("1D integrations: " + mi.counter);





		System.out.println("trying 3d now... ");
		// basic limit test here,
		double[] lims = new double[6];
		lims[0]=0.0;
		lims[1]=3.00;
		lims[2]=0.0;
		lims[3]=1.0;
		lims[4]=0.0;
		lims[5]=2.0;

		Date d1 = new Date();
		double test = mi.integrate(testlims,lims);
		Date d2 = new Date();

		System.out.println("Answer: " + test);
		System.out.println("took: " + (d2.getTime()-d1.getTime()));
		System.out.println("1D integrations: " + mi.counter);
		System.out.println("answer: " + 8*81/2/3/4);



		lims = new double[6];
		lims[0]=-10.0;
		lims[1]=10.00;
		lims[2]=-10.0;
		lims[3]=10.0;
		lims[4]=-10.0;
		lims[5]=10.0;

		d1 = new Date();
		test = mi.integrate(testf,lims);
		d2 = new Date();

		System.out.println("Answer: " + test);
		System.out.println("took: " + (d2.getTime()-d1.getTime()));
		System.out.println("1D integrations: " + mi.counter);
		System.out.println("test^2/3: " + Math.pow(test,2.0/3.0));
		System.out.println("trying 3d now... ");



		// 3d Function integration working in spherical coords??
	    lims = new double[6];
		lims[0]=0;
		lims[1]=Math.PI;
		lims[2]=0;
		lims[3]=2*Math.PI;
		lims[4]=0;
		lims[5]=10.0;

		d1 = new Date();
		test = mi.integrate(testfs,lims);
		d2 = new Date();

		System.out.println("Answer: " + test);
		System.out.println("took: " + (d2.getTime()-d1.getTime()));
		System.out.println("1D integrations: " + mi.counter);
		System.out.println("test^2/3: " + Math.pow(test,2.0/3.0));




	}
}