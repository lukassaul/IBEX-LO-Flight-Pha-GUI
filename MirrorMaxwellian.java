

public class MirrorMaxwellian {

	public double mid,amp,t1,center;

	public MirrorMaxwellian() {
		this(1.0,1.0,1.0,0.0);
	}

	public MirrorMaxwellian(double m, double a, double t11, double c) {
		mid = m;
		amp = a;
		t1 = t11;
		center = c;
	}

	double f(double midd, double t11, double cc, double x) {
		mid = midd;
		t1 = t11;
		center = cc;
		//t1 = p3;
		//t2 = p4;
		return f(x);
	}

	double f(double p1, double p2, double p3, double p4, double x) {
		mid=p1;
		amp = p2;
		t1=p3;
		center=p4;
		return f(x);
	}

	/**
	*  here's the function..
	*
	*/
	public double f(double x) {
	    return amp*Math.exp(-(x-mid-center)*(x-mid-center)/2/t1) +

	    		amp*Math.exp(-(x+mid-center)*(x+mid-center)/2/t1);
	}

	public static final void main(String[] args) {
		MirrorMaxwellian dm = new MirrorMaxwellian();

		// do a simple test for graphing
		dm.mid = 24;
		dm.amp=891;
		//dm.t2=2.3;
		dm.t1=0.35;
		file f = new file("test_dm_he.dat");
		f.initWrite(false);
		for (double d=0; d<100; d+=0.5) {
			f.write(d+"\t"+dm.f(d)+"\n");
		}
		f.closeWrite();

	}
}