/**Lukas Saul, 2000
*  this class is for keeping track of time intervals.
*  use boolean check() to see if a time is in an interval
*   We are avoiding java's date class so we can use "int".
*    In other words, time res. is limited to seconds
*/


public class TimeInterval {
	public int startDate;
	public int finishDate;
	public float startFloat, finishFloat;

	public TimeInterval(int l1, int l2) {
		startDate = l1; startFloat = (float)l1;
		finishDate = l2; finishFloat = (float)l2;
	}

	public TimeInterval(double l1, double l2) {
		this((float)l1,(float)l2);
	}

	public TimeInterval(float l1, float l2) {
		startFloat = l1; startDate = (int)l1;
		finishFloat = l2; finishDate = (int)l2;
	}

	public boolean check(int l) {
		if ( (l>=startDate) && (l<=finishDate) ) return true;
		else return false;
	}

	public boolean check(float l) {
		if ( (l>=startFloat) && (l<=finishFloat) ) return true;
		else return false;
	}

	public float getWidth() {
		return finishFloat - startFloat;
	}

	/** For testing only
	*
	*/
	public final static void main(String[] args) {
		System.out.println("Short "+Short.MAX_VALUE);
		System.out.println("Long "+Long.MAX_VALUE);
		System.out.println("Int "+Integer.MAX_VALUE);
		System.out.println("Float "+Float.MAX_VALUE);
		System.out.println("Double "+Double.MAX_VALUE);
	}

}
