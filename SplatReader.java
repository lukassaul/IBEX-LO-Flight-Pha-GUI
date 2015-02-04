import java.util.StringTokenizer;

public class SplatReader {

	public SplatReader() {
		//
		// expect atlas.rec simion output format!  easier to parse..
		//
		file f = new file ("foil_ions_rec4.txt");
		f.initRead();

		int skipLines = 12;
		//Histogram hH, hC, hO;

		double avgH, avgC, avgO;
		avgH=0.0; avgC=0.0; avgO=0.0;
		//hH = new Histogram(0.0,

		String line = "";
		for (int i=0; i<skipLines; i++) {
			line = f.readLine();
		}
		System.out.println("last skipped: " + line);
		for (int j=0; j<2; j++) {
			avgH=0.0; avgC=0.0; avgO=0.0;
			int index=0;
			for (int i=0; i<100; i++) {
				line = f.readLine();
				line = f.readLine();
				StringTokenizer st = new StringTokenizer(line,",");
				String dummy = st.nextToken();
				dummy = st.nextToken();
				double tof = Double.parseDouble(st.nextToken());
				dummy = st.nextToken();
				dummy = st.nextToken();
				dummy = st.nextToken();
				dummy = st.nextToken();
				double x = Double.parseDouble(st.nextToken());
				double y = Double.parseDouble(st.nextToken());
				//System.out.println("x : " + x + " tof: " + tof);
				if (x==50.0) {
					avgH += tof;
					index++;
				}
				//System.out.println("H tof: " + tof);

			}
			avgH /= index;
			index = 0;
			for (int i=0; i<100; i++) {
				line = f.readLine();
				line = f.readLine();
				StringTokenizer st = new StringTokenizer(line,",");
				String dummy = st.nextToken();
				dummy = st.nextToken();
				double tof = Double.parseDouble(st.nextToken());
				dummy = st.nextToken();
				dummy = st.nextToken();
				dummy = st.nextToken();
				dummy = st.nextToken();
				double x = Double.parseDouble(st.nextToken());
				double y = Double.parseDouble(st.nextToken());
				if (x==50.0) {
					avgC += tof;
					index++;
				}
				//System.out.println("C tof: " + tof);
			}
			avgC/=index;
			index = 0;
			for (int i=0; i<100; i++) {
				line = f.readLine();
				line = f.readLine();
				StringTokenizer st = new StringTokenizer(line,",");
				String dummy = st.nextToken();
				dummy = st.nextToken();
				double tof = Double.parseDouble(st.nextToken());
				dummy = st.nextToken();
				dummy = st.nextToken();
				dummy = st.nextToken();
				dummy = st.nextToken();
				double x = Double.parseDouble(st.nextToken());
				double y = Double.parseDouble(st.nextToken());
				if (x==50.0) {
					avgO += tof;
					index++;
				}
				//System.out.println("O tof: " + tof);
			}
			avgO/=index;
			index = 0;
			System.out.println("average tof H- : " + avgH);
			System.out.println("average tof C- : " + avgC);
			System.out.println("average tof O- : " + avgO);
		}
		f.closeRead();

	}

	public final static void main(String[] args) {
		SplatReader sr = new SplatReader();
	}

}