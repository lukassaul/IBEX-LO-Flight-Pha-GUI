/**
* Utility class for passing around 2D functions
*
* Use this to get a 1D function by fixing one of the variables..
*  the index to fix is passed in ( 0 or 1 )
*/
//import gaiatools.numeric.function.Function;
public abstract class FunctionII {

	/**
	*  Override this for the function needed!!
	*
	*/
	public double function2(double x, double y) {
		return 0;
	}

	/**
	* don't override this one...
	*
	*/
	//MyDouble value_ = new MyDouble(0.0);
	double value_ = 0.0;

	/**
	*  here's the Function implementation to return, modified in the public method..
	*  return this for index = 0
	*/
	FunctionI ff0 = new FunctionI() {
		public double function(double x) {
			return function2(value_,x);
		}
	};

	/**
	*  here's the Function implementation to return, modified in the public method..
	*  return this for index = 1
	*/
	FunctionI ff1 = new FunctionI() {
		public double function(double x) {
			return function2(x,value_);
		}
	};

	/**
	*
	* This returns a one dimensional function, given one of the values fixed
	*
	*/
	public final FunctionI getFunction(final int index, final double value) {
		//value_=value;
		if (index == 0) return new FunctionI() {
			public double function(double x) {
				return function2(value,x);
			}
		};
		else if (index == 1) return new FunctionI() {
			public double function(double x) {
				return function2(x,value);
			}
		};
		else {
			System.out.println("index out of range in FunctionII.getFunction");
			return null;
		}
	}

	public static final void main(String[] args) {
		FunctionI f1 = new FunctionI() {
			public double function(double x) {
				return (Math.exp(x*x));
			}
		};

		FunctionII f2 = new FunctionII() {
			public double function2(double x, double y) {
				return (Math.exp(x*x+y*y));
			}
		};

		FunctionI f3 = f2.getFunction(0,0.0);
		FunctionI f4 = f2.getFunction(1,0.0);


		System.out.println("f1: " + f1.function(0.5));
		System.out.println("f3: " + f3.function(0.5));
		System.out.println("f4: " + f4.function(0.5));
	}
}
