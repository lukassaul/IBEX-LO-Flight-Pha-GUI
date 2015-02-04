
//import drasys.or.nonlinear.*;
import gaiatools.numeric.function.Function;
import flanagan.integration.*;


/*
* Utility class for passing around functions
*
*  implements the drasys "FunctionI" interface to enable Simpsons method
*   outsource.
*/
public abstract class FunctionI extends Function implements IntegralFunction{
	public double function(double x) {
		return 0;
	}
	public double value(double var) {
		return function(var);
	}
	public String toString() {
		return "a string";
	}
	public double ddif(int x, double y) {
		return 0.0;
	}
	public double value(double[] x) {
		return 0.0;
	}
}
