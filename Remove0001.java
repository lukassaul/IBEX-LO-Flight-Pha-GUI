import java.util.StringTokenizer;

public class Remove0001 {
	public static int skipLines = 35;
	boolean extra = false;

	public Remove0001(String fn, boolean ex) {
		extra = ex;
		file inFile = new file(fn);
		file outFile = new file(fn.substring(0,fn.length()-4)+"_no0001.txt");
		inFile.initRead();
		outFile.initWrite(false);
		String line = "";
		for (int i=0; i<skipLines; i++)	 {
			line = inFile.readLine();
			outFile.write(line+"\n");
		}
		while ((line=inFile.readLine())!=null && line.length()>5) {  // until end of file
			StringTokenizer st = new StringTokenizer(line);
			double t0_val = Double.parseDouble(st.nextToken());
			double t0_start = Integer.parseInt(st.nextToken());
			double t0_stop = Integer.parseInt(st.nextToken());
			double t0_tof = Integer.parseInt(st.nextToken());
			double t1_val = Double.parseDouble(st.nextToken());
			double t1_start = Integer.parseInt(st.nextToken());
			double t1_stop = Integer.parseInt(st.nextToken());
			double t1_tof = Integer.parseInt(st.nextToken());
			double t2_val = Double.parseDouble(st.nextToken());
			double t2_start = Integer.parseInt(st.nextToken());
			double t2_stop = Integer.parseInt(st.nextToken());
			double t2_tof = Integer.parseInt(st.nextToken());
			double t3_val = Double.parseDouble(st.nextToken());
			double t3_start = Integer.parseInt(st.nextToken());
			double t3_stop = Integer.parseInt(st.nextToken());
			double t3_tof = Integer.parseInt(st.nextToken());
			double pac = (float)Double.parseDouble(st.nextToken());
			double mcp = (float)Double.parseDouble(st.nextToken());
			if(t0_tof!=0 || t1_tof!=0 || t2_tof!=0) {
				if (extra) outFile.write(line+"\t"+(t0_tof+t1_tof+t2_tof+t3_tof)+"\t"+(t0_tof+t1_tof+t2_tof)+
						"\t"+(t1_val+t2_val-t0_val-t3_val)+"\t"+(t0_val+t3_val/2)+"\t"+(t1_val-t3_val/2)+"\n");
				else outFile.write(line+"\n");
			}
		}
		outFile.closeWrite();
		System.out.println("completed 0001 removal of " + fn);
	}

	public static final void main(String[] args) {
		if (args.length>1 && args[1].equals("-extra")) {
			Remove0001 a = new Remove0001(args[0],true);
		}
		else {
			Remove0001 a = new Remove0001(args[0],false);
		}
	}
}
