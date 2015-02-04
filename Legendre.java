//Lukas Saul  -  Warsaw 2000
//
// modified - jan. 2006

import java.util.StringTokenizer;
public class Legendre {

  public int N=200; // change to max polynomial size
  double[][] P, P_t; //= new double[N+1][N+1]; // coefficients of Pn(x)
  double[][] DP;// = new double[N][N]; // derivitives of Pn(x)
  double[][] R;// = new double[N][N]; // roots of Pn(x) the x[i]
  double[][] W; //= new double[N][N]; // weights of Pn(x) the w[i]

  public Legendre() {
	  this(200);
  }

  public Legendre(int k) {
		N = k;
		P = new double[N+1][N+1]; // coefficients of Pn(x)
		P_t = new double [N+1][N+1];
		DP = new double[N][N]; // derivitives of Pn(x)
		R = new double[N][N]; // roots of Pn(x) the x[i]
		W = new double[N][N]; // weights of Pn(x) the w[i]

		//createCoefficientFile();
		loadFromFile();
  }


  public double p(int n, double arg) {
	   double tbr = 0.0;
	   double argPow = 1.0;
	   for(int i=0; i<N-1; i++) {
		   tbr+=P[n][i]*argPow;
	       argPow*=arg;
	   }
	   return tbr;
  }

  public void loadFromFile() {
	   file f = new file("legendre200.dat");
	   f.initRead();
	   String s = "";
	   StringTokenizer st;
	   String garbage = f.readLine();
	   int counter = 0;
	   try {
	   	 	while ((s=f.readLine())!=null) {
				st=new StringTokenizer(s);
				int i = Integer.parseInt(st.nextToken());
				int j = Integer.parseInt(st.nextToken());
				if (i<N && j<N)
				P[i][j] = Double.parseDouble(st.nextToken());
		  	}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
   }

   public void createCoefficientFile() {
	   System.out.println("Legendre.java running");
		file ldatf = new file("legendre200.dat");
		ldatf.initWrite(false);
		// generate family of Legendre polynomials Pn(x)
		// as P[degree][coefficients]
		// build coefficients of Pn(x)
		for(int n=0; n<N+1; n++) for(int i=0; i<N; i++) P[n][i]=0.0;
		P[0][0]=1.0;
		P[1][1]=1.0; // start for recursion
		for(int n=2; n<N+1; n++) {
			  for(int i=0; i<=n; i++) {
				if(i<n-1) P[n][i]=-((n-1)/(double)n)*P[n-2][i];
				if(i>0)   P[n][i]=P[n][i]+((2*n-1)/(double)n)*P[n-1][i-1];
				System.out.println("P["+n+"]["+i+"]="+P[n][i]);
			  }
		}
		/*System.out.println("checking calcuations: ");
		for (int i=0; i<N+1; i++) {
			for (int j=0; j<=i; j++) {
				P_t[i][j] = binomial(i,j)*binomial(i,i-j)/Math.pow(2.0,i);

				if (P[i][j] != P_t[i][j]) {
					System.out.println("problem: P_ij: " +i + " " + j + " "+ P[i][j] + " P_tij: " + P_t[i][j]);
				}
				ldatf.write(i + "\t" + j +"\t" + P[i][j]+"\n");
			}
		}*/

		System.out.println("saving to data file..");
		ldatf.write("Coefficients of Legendre Polynomials \n" );
		for (int i=0; i<N+1; i++) {
			for (int j=0; j<=i; j++) {
				ldatf.write(i + "\t" + j +"\t" + P[i][j]+"\n");
			}
		}
		ldatf.closeWrite();
	}

   public static double binomial(int i, int j) {
	   return fact(i) / (fact(j)*fact(i-j));
   }

   public static double fact(int i) {
	   if (i==0) return 1.0;
	   if (i==1) return 1.0;
	   else return i*fact(i-1);
   }

	// recursively computes legendre polynomial of order n, argument x
	public static double compute(int n,double x) {
		if (n == 0) return 1;
		else if (n == 1) return x;
		return ( (2*n-1)*x*compute(n-1,x) - (n-1)*compute(n-2,x) ) / n;
	}

	public static final void main(String[] args) {
		//System.out.println(fact(2) + " " + fact(4) + " " + fact(6));
		//System.out.println(binomial(2,1) + " " + binomial(3,1) + " " + binomial(3,2));
		Legendre l = new Legendre(200);
		System.out.println(l.p(1,0) + " " + l.p(2,0) + l.p(3, 0.5));
	}
}



// Guess we don't need this stuff...

/*public static long factorial(int m) {
		if (m==1) return 1;
		else return m*factorial(m-1);
	}
}*/// L

/*C***    LEGENDRE POLYNOM P(X) OF ORDER N
C***	EXPLICIT EXPRESSION

	   POLLEG=0.
	   NEND=N/2
		DO M=0,NEND
	        	N2M2=N*2-M*2
			 NM2=N-M*2
			  NM=N-M
		    TERM=X**NM2*(-1.)**M
		    TERM=TERM/NFAK(M)*NFAK(N2M2)
		    TERM=TERM/NFAK(NM)/NFAK(NM2)
		  POLLEG=POLLEG+TERM
		END DO
	  POLLEG=POLLEG/2**N

	 RETURN

	END
C*/

// I love fortran!