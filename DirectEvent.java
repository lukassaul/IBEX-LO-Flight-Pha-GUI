
import java.util.StringTokenizer;
import java.io.*;


/**
*  Use this to create a special binary file with direct events
*
*/
public class DirectEvent {
	int skipLines = 4;
	int numTotal=0;
	double timeStamp, dec, asc;
	float spinPhase;
	int ebin;
	double t0_val,t1_val,t2_val,t3_val;
	int t0_stop, t1_stop, t2_stop, t3_stop;
	int t0_start, t1_start, t2_start, t3_start;
	int t0_tof, t1_tof, t2_tof, t3_tof;
	file[] files;

	public DirectEvent() {
		try {
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("fall1_trips_b.bin")));


			// build file information
			file filefile = new file("filelist_fall1_all.txt");
			files = new file[filefile.readStuffNumLines()];
			filefile.initRead();
			for (int i=0; i<files.length; i++) {
				files[i]=new file(filefile.readLine());
			}
			filefile.closeRead();

			// this stuff dragged in from GSE_ep.java
			// here's where we read the data file
			for (int i=0; i<files.length; i++) {

				System.out.println("reading file: " + files[i].fileName);
				files[i].initRead();
				String line = "";
				String garbage = "";
				for (int j=0; j<skipLines; j++)	 line = files[i].readLine();
				boolean firstDate = true;
				while ((line=files[i].readLine())!=null && line.length()>40) {  // until end of file

					//
					// parsing output of: $lo_tof_de -t gseos
					// first line
					StringTokenizer st = new StringTokenizer(line);
					timeStamp = Double.parseDouble(st.nextToken());      //
					dec = Double.parseDouble(st.nextToken());			//
					asc = Double.parseDouble(st.nextToken());			//
					String bin = st.nextToken();
					//System.out.println("bin: " + bin);
					bin = bin.substring(1,2);
					//System.out.println("bin: " + bin);
					//bin = bin.substring(1,1);
					//System.out.println("bin: " + bin);
					if (bin.equals("B")) ebin = 9;
					else if (bin.equals("D")) ebin = 10;
					else ebin = Integer.parseInt(bin);
					garbage = st.nextToken();
					garbage = st.nextToken();
					garbage = st.nextToken();
					spinPhase = Float.parseFloat(st.nextToken());

					// second line
					line = files[i].readLine();
					st = new StringTokenizer(line);
					t0_val = Double.parseDouble(st.nextToken());
					t0_start = Integer.parseInt(st.nextToken());
					t0_stop = Integer.parseInt(st.nextToken());
					t0_tof = Integer.parseInt(st.nextToken());
					t1_val = Double.parseDouble(st.nextToken());
					t1_start = Integer.parseInt(st.nextToken());
					t1_stop = Integer.parseInt(st.nextToken());
					t1_tof = Integer.parseInt(st.nextToken());
					t2_val = Double.parseDouble(st.nextToken());
					t2_start = Integer.parseInt(st.nextToken());
					t2_stop = Integer.parseInt(st.nextToken());
					t2_tof = Integer.parseInt(st.nextToken());
					t3_val = Double.parseDouble(st.nextToken());
					t3_start = Integer.parseInt(st.nextToken());
					t3_stop = Integer.parseInt(st.nextToken());
					t3_tof = Integer.parseInt(st.nextToken());
					//pac = (float)Double.parseDouble(st.nextToken());
					//mcp = (float)Double.parseDouble(st.nextToken());
					//String garbage = st.nextToken(); // golden
					//timeStamp = Double.parseLong(st.nextToken());

					// done reading values for this event
					// let's write them to our binary file
					if (t0_tof==1 & t1_tof==1 & t2_tof==1 & t3_tof==1) {
						dos.writeFloat((float)timeStamp);
						dos.writeInt((int)(dec*1000));
						dos.writeInt((int)(asc*1000));
						dos.writeInt((int)(spinPhase*1000));
						dos.writeShort(ebin);
						if (t0_tof==1) dos.writeInt((int)(t0_val*1000)); else dos.writeInt(0);
						if (t1_tof==1) dos.writeInt((int)(t1_val*1000)); else dos.writeInt(0);
						if (t2_tof==1) dos.writeInt((int)(t2_val*1000)); else dos.writeInt(0);
						if (t3_tof==1) dos.writeInt((int)(t3_val*1000)); else dos.writeInt(0);
					}

					numTotal++;  // total number of events
/*
					System.out.println("double: " + timeStamp);
					System.out.println("float: " + (float)timeStamp);
					System.out.println("int: " + (int)timeStamp);
					System.out.println("short: " + (short)ebin);
					System.out.println("asc: "+asc);
					System.out.println("dec: "+dec);
					System.out.println("int: " + (int)(dec*100));
					System.out.println("int: " + (int)(asc*100));
					System.out.println("short: " + (short)(dec*100));
					System.out.println("short: " + (short)(asc*100));
*/

					if (numTotal%10000==0) {
						System.out.println("file: "+ files[i].fileName+ "read: "+numTotal);

						//break;
					}
				}
				files[i].closeRead();
			}
			dos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void main(String[] args) {
		DirectEvent de = new DirectEvent();
	}
}