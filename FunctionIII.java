
/**
* Utility class for passing around 3D functions
*
* Use this to generate a 2D function by fixing one of the variables
*/
public abstract class FunctionIII {

	/**
	*  Override this with an interesting 3D function
	*
	*/
	public double function3(double x, double y, double z) {
		return 0;
	}


	/**
	*  here's the Function implementation to return, modified in the public method..
	*  return this for index = 0
	*/
	FunctionII ff0 = new FunctionII() {
		public double function2(double x, double y) {
			return function3(value_,x,y);
		}
	};

	/**
	*  here's the Function implementation to return, modified in the public method..
	*  return this for index = 1
	*/
	FunctionII ff1 = new FunctionII() {
		public double function2(double x, double y) {
			return function3(x,value_,y);
		}
	};

	/**
	*  here's the Function implementation to return, modified in the public method..
	*  return this for index = 2
	*/
	FunctionII ff2 = new FunctionII() {
		public double function2(double x, double y) {
			return function3(x,y,value_);
		}
	};


	/*
	* This returns a two dimensional function, given one of the values
	*
	*/
	public final FunctionII getFunctionII(final int index, final double value) {
		//value_ = value;
		if (index == 0) return new FunctionII() {
			public double function2(double x, double y) {
				return function3(value,x,y);
			}
		};
		else if (index == 1) return new FunctionII() {
			public double function2(double x, double y) {
				return function3(x,value,y);
			}
		};
		else if (index == 2) return new FunctionII() {
			public double function2(double x, double y) {
				return function3(x,y,value);
			}
		};
		else {
			System.out.println("index out of range in FunctionIII.getFunctionII");
			return null;
		}
	}

	public static final void main(String[] args) {
		FunctionII f1 = new FunctionII() {
			public double function2(double x, double y) {
				return (Math.exp(x*x+y*y));
			}
		};

		FunctionIII f2 = new FunctionIII() {
			public double function3(double x, double y, double z) {
				return (Math.exp(x*x+y*y+z*z));
			}
		};

		FunctionII f3 = f2.getFunctionII(0,0.0);
		FunctionII f4 = f2.getFunctionII(1,0.0);
		FunctionII f5 = f2.getFunctionII(2,0.0);

		System.out.println("f1: " + f1.function2(0.5,0.0));
		System.out.println("f3: " + f3.function2(0.5,0.0));
		System.out.println("f4: " + f4.function2(0.5,0.0));
		System.out.println("f5: " + f5.function2(0.0,0.5));
	}
}

