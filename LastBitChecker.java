import java.util.StringTokenizer;

public class LastBitChecker {

	public LastBitChecker() {
		file f = new file("test_bin_out.txt");
		f.initRead();
		String line = "";
		StringTokenizer st;
		int num0=0;
		int num1=0;
		int num2=0;
		int num3=0;
		int numBits = 0;
		while ((line=f.readLine())!=null) {
			st=new StringTokenizer(line);
			int len = Integer.parseInt(st.nextToken());
			for (int i=0; i<len; i++) {
				numBits++;
				int num = Integer.parseInt(st.nextToken());
				if (num%4==0 & num!=0) num0++;
				if (num%4==1 & num!=0) num1++;
				if (num%4==2 & num!=0) num2++;
				if (num%4==3 & num!=0) num3++;
			}
			line =f.readLine();
		}
		System.out.println("numbits: " + numBits);
		System.out.println("num0: " + num0);
		System.out.println("num1: " + num1);
		System.out.println("num2: " + num2);
		System.out.println("num3: " + num3);
	}

	public static final void main(String[] args) {
		LastBitChecker lbc = new LastBitChecker();
	}
}