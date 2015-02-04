import java.util.Random;

public class Chopper {
	public static int skipLines = 35;
	public int factor = 10;
	public Random r;
	public Chopper(String fn, int factor) {
		r = new Random();
		this.factor = factor;
		file inFile = new file(fn);
		file outFile = new file(fn.substring(0,fn.length()-4)+"_choppedBy_"+factor+".txt");
		inFile.initRead();
		outFile.initWrite(false);
		String line = "";
		for (int i=0; i<skipLines; i++)	 {
			line = inFile.readLine();
			outFile.write(line+"\n");
		}
		while ((line=inFile.readLine())!=null) {  // until end of file
			int num = r.nextInt(factor);
			line = inFile.readLine();
			if (num==1) outFile.write(line+"\n");
		}
		outFile.closeWrite();
		System.out.println("completed chopping by " + factor + " of " + fn);
	}

	public static final void main(String[] args) {
		try {
			int ff = Integer.parseInt(args[1]);
			Chopper c = new Chopper(args[0],ff);
		} catch (Exception e) { e.printStackTrace(); }
	}
}
