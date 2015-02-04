import java.io.*;
/**
*
* Read data from binary .rec files
*   this version just a test run 06/14/06
*/
public class RecReader {
	public DataInputStream dis;
	public byte[] b;
	public RecReader(String ff) {

		try {
			dis = new DataInputStream(new FileInputStream(ff));
		}
		catch (Exception e) {
			System.out.println("Problems loading binary data file - 1");
			e.printStackTrace();
		}

		try{

			// Reading file header byte by byte..
			b = new byte[5];
			dis.readFully(b);
			System.out.println("code: "+new String(b)+"");
			b = new byte[2];
			dis.readFully(b);
			System.out.println("version: "+new String(b)+"");
			b = new byte[19];
			dis.readFully(b);
			System.out.println("project: "+new String(b)+"");
			//b = new byte[4];
			int time2 = dis.readInt();
			System.out.println("time: "+time2);
			b = new byte[4];
			dis.readFully(b);
			System.out.println("spare: "+new String(b)+"");
			b = new byte[5];
			dis.readFully(b);
			System.out.println("next: "+new String(b)+"");
			b=new byte[2];
			System.out.println(new String(b)+"");

			byte[] four = new byte[4];
			for (int i=0; i<100; i++) {
				nextTA();
				short a = dis.readShort();
				dis.readFully(four);
				String stamp = new String(four);
				dis.readFully(four);
				int size = toInt(four);
				int junk = dis.readInt();
				dis.readFully(four);
				float time = toInt(four);
				int junk2 = dis.readInt();
				System.out.println(" a: " + a + " " + stamp + " " + size + " " + junk + " " + time + " " + junk2);
				//byte[] data = new byte[size];  dis.readFully(data);
				//int blocklen = dis.readInt();
			}
			//b = new byte[10000];
			//System.out.println("and now: " + new String(b));
		}
		catch (Exception e) {
			System.out.println("Problems loading binary data file - 2");
			e.printStackTrace();
		}
	}
	// from python
	public int toInt(byte[] string) {
      int result = 0;
      int i = string.length - 1;
      while (i >= 0) {
         result = (result << 8) | (int)string[i];
         i += -1;
	  }
      return result;
  }

	public void nextTA() {
		try {
			// waiting for a bit string..
			String test = "";
			int idex = 0;
			byte[] single = new byte[1];
			b = new byte[2];
			dis.readFully(single);
			b[0]=single[0];
			dis.readFully(single);
			b[1]=single[0];
			test = new String(b);
			while (!test.equals("TA")) {
				dis.readFully(single);
				b[0]=b[1]; b[1]=single[0];
				test = new String(b);
				idex++;
			}
			System.out.println("found at: " + idex);
		}
		catch (Exception e) {e.printStackTrace();}
	}

	public static final void main(String[] args) {
		String fileName2 = "IBEX-Lo_ETU_EGSE_20060603_115557.rec";
		String fileName = "IBEX-Lo_ETU_EGSE_20060607_074724.rec";
		RecReader rr = new RecReader(fileName);
		rr = new RecReader(fileName2);
	}
}