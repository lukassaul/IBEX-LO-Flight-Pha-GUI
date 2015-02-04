
/**
*
* Use this to calculate the v dist. of int. helium using
*   the assumption of isotropic adiabatic cooling
*
*/
public class SemiModel  {

	public double AU  = 1.49598* Math.pow(10,11); //meters
	public double NaN = Double.NaN;
	public double MAX = Double.MAX_VALUE;
	public double U   = 440*1000;
	public double N0  = Math.pow(10,-6);
	public double GAMMA = 3.0/2.0;  // energy dependence in eflux ?? - inverse gamma in derivation..
	static double beta = 3*Math.pow(10,-8);
	static double PI = Math.PI;
	public Legendre l;
	public int degree = 20;
	public double mu_i = 1.0;
	public double D = 0.00001;
	public double VSW = 400000.0;
	public static double v_infinity = 27500.0;  //m/s
	public static double G = 6.673 * Math.pow(10,-11);  // m^3/s^2/kg
	public static double Ms = 1.98892 * Math.pow(10,30);  // kg
	public MultipleIntegration mi;
	public double instr_normalization;
	public int counter=0;

    public SemiModel() {
		mu_i = 0.985;  // cos(10 degrees)
		l = new Legendre();
		mi = new MultipleIntegration();
		//for (double v = 0; v<VSW*1.1; v+= VSW/100) {
		//	System.out.println("v: "+ v + " f: "+ f(v));
		//}
		FunctionI integrand = new FunctionI () {
			public double function(double mu) {
				return instr_function(mu);
			}
		};
		instr_normalization = mi.integrate(integrand, -1, 1);
		System.out.println("norm: " + instr_normalization);
		System.out.println("predict: " + Math.sqrt(1.0/2.0/Math.PI/13.2/Math.PI*180.0));
	}

	public double f(double v, double mu) {
		//D = d;
		//GAMMA = gamma;
		double tbr=0.0;
		for (int i=0; i<degree; i++) {
			tbr += Math.pow(v/VSW,1-GAMMA)*(2.0*i+1)/2.0*l.p(i,mu_i)*l.p(i,mu)*Math.exp(-i*(i+1.0)*D*AU/VSW*(1.0-Math.pow(v/VSW,GAMMA)));
		}
		tbr*=N(AU*Math.pow(v/VSW,GAMMA));
		tbr*=(instr_function(Math.acos(mu))+instr_normalization)/2;
		return tbr;
	}

	/**
	*  now including instrument function - april 06 - from Aelig thesis
	*
	*/
	public double f(double v) {
		final double v_ = v;
		FunctionI integrand = new FunctionI () {
			public double function(double mu) {
				return f(v_,mu);
			}
		};
		double integral = mi.integrate(integrand, -1.0, -0.866);
		return integral;
	}

	public double f(double norm, double dd, double gamm, double v) {
		counter++;
		D = dd;
		GAMMA = gamm;
		return norm*v*f(v*VSW);
	}

	public double f(double norm, double dd, double v) {
		counter++;
		D=dd;
		return norm*v*f(v*VSW);
	}

	/**
	*  From Aelig thesis..  angular calibration..
	*/
	public double instr_function(double mu) {
		double sigma = 13.2*Math.PI/180.0;
		double factorAlph = Math.exp(-mu*mu/2.0/sigma/sigma);
		return factorAlph;  //(factorAlph+1.0d)/2.0d;
	}




	/**
	* The model of interstellar neutral density..
	*
	*   based on cold model, due upwind..
	*   see Moebius SW&CR homework set #4!
	*/
	private double N(double r) {
		return N0*Math.exp(-beta*AU*AU*Math.sqrt(v_infinity*v_infinity+2.0*G*Ms/r)/G/Ms);
		//return N0*Math.exp(-2*beta*AU*AU/r/v_infinity);
	}

	public static final void main(String[] args) {
		SemiModel sm = new SemiModel();
		double[] test = new double[20];
		double[] test1 = new double[20];
		double[] test2 = new double[20];
		double[] test3 = new double[20];
		double[] test4 = new double[20];
		double[] test5 = new double[20];
		double[] test6 = new double[20];
		double[] dd = {4.38e-6,4.6e-6,4.0e-6,3.1e-6,2.6e-6,1.9e-6};
		double ddtemp = 4.0e-6;
		double[] nn = {3.01e12,2.1e11,2.2e11,2.6e11,3.2e11,5.5e11};
		double nntemp = 2e11;
		double[] gamma = {1.5d, 0.8d, 1.1d, 1.4d, 1.7d, 2.0d};
		file f = new file("semi_out_test.dat");
		f.initWrite(false);
		int index = 0;
		for (double w=0.0; w<=1.0; w+=0.02) {
			index=0;
			f.write(w+"\t");
			while (index<6) {
				f.write(sm.f(nn[index],dd[index],gamma[index],w)+"\t");
				index++;
			}
			f.write("\n");
		}
		f.closeWrite();
	}
}
