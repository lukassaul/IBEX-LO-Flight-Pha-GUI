

public class DoubleMaxwellian {

	public double mid,amp,t1,t2;

	public DoubleMaxwellian() {
		this(0.0,1.0,1.0,1.0);
	}

	public DoubleMaxwellian(double m, double a, double to, double tt) {
		mid = m;
		amp = a;
		t1 = to;
		t2 = tt;
	}

	double f(double p3, double p4, double x) {
		t1 = p3;
		t2 = p4;
		return f(x);
	}

	double f(double p1, double p2, double p3, double p4, double x) {
		mid=p1;
		amp = p2;
		t1=p3;
		t2=p4;
		return f(x);
	}

	/**
	*  here's the function..
	*
	*/
	public double f(double x) {
	    if (x<=mid) {
			return amp*Math.exp(-(x-mid)*(x-mid)/2/t1);
		}
		else return amp*Math.exp(-(x-mid)*(x-mid)/2/t2);
	}

	public static final void main(String[] args) {
		DoubleMaxwellian dm = new DoubleMaxwellian();

		// do a simple test for graphing
		dm.mid = 24;
		dm.amp=891;
		dm.t2=2.3;
		dm.t1=0.35;
		file f = new file("test_dm_he.dat");
		f.initWrite(false);
		for (double d=0; d<100; d+=0.5) {
			f.write(d+"\t"+dm.f(d)+"\n");
		}
		f.closeWrite();

	}
}