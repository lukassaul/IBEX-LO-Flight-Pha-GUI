

import java.util.StringTokenizer;

class CSBFixer {

	public CSBFixer() {

		int spacer = 300;

		double num = 32763;
		file inFile = new file("ANGLESCAN_600V_PEAK LUMN..CSB");
		inFile.initRead();

		file outFile = new file("600V_Peak_Lumn_f300.dat");
		outFile.initWrite(false);

		String dum = "";
		for (int i=0; i<19; i++) {
			dum = inFile.readLine();
		}

		int counter = 0;
		double numFactor = 16.0/num;

		while((dum=inFile.readLine())!=null) {

			StringTokenizer st = new StringTokenizer(dum,",");
			String t = st.nextToken();
			int nume = Integer.parseInt(t);
			double ans = (5.12*20.0-nume*20.0*10.24/256.0)/10.0;

			if (counter%spacer==0) {
				outFile.write(counter*numFactor-8 + "\t" + ans + "\n");
				//System.out.println(st.nextToken()+","+st.nextToken());
			}
			counter++;
		}
		outFile.closeWrite();
	}

	public static final void main(String[] args) {
		CSBFixer csbf = new CSBFixer();
	}

}