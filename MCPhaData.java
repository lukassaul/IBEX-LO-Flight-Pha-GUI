
public class MCPhaData {

	public long startTime;
	public long endTime;

	public float minV;
	public float maxV;

	public float ct0, ct1, ct2, ct3;
	public float cTriples;

	public long deltaT;

	public MCPhaData(int min) {
		// initialize
		startTime = Long.MAX_VALUE;
		endTime = Long.MIN_VALUE;
		minV=(float)min;
		maxV=0.0f;
		ct0=0.0f;
		ct1=0.0f;
		ct2=0.0f;
		ct3=0.0f;
		cTriples=0.0f;
	}

	public void finalize() {
		deltaT = endTime - startTime;
		ct0 = ct0/deltaT;
		ct1 = ct1/deltaT;
		ct2 = ct2/deltaT;
		ct3 = ct3/deltaT;
		cTriples = cTriples/deltaT;
	}
}
