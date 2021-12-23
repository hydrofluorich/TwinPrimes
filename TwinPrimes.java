/* List of twin prime pairs, using Java
 * Uses deterministic Miller–Rabin test and a difference array
 * Accurate for primes < ~340 billion but currently gets stuck at a prime ~3 billion
 * Created December 2021
 */

import java.util.*;

public class TwinPrimes {
	public static void main(String[] args) {
		long numPairs = 9000000; // the desired number of twin prime pairs
		
		ArrayList<Integer> adds = new ArrayList<Integer>(); // difference array
		adds.add(6);
		adds.add(12);
		
		int cur = 6;
		int prev = 5;
		int r5 = cur%5;
		int r7 = cur%7;
		int r11 = cur%11;
		int r13 = cur%13;
		int r17 = cur%17;
		int r19 = cur%19;
		
		// generate difference array
		while(!(r5 == 2 && r7 == 2 && r11 == 2 && r13 == 2 && r17 == 2 && r19 == 2)) {
			if(r5 == 1 || r5 == 4 || r7 == 1 || r7 == 6 || r11 == 2 || r11 == 9 || r13 == 2 || r13 == 11 || r17 == 3 || r17 == 14 || r19 == 3 || r19 == 16) {
				cur++;
			} else {
				adds.add(6*(cur-prev));
				prev = cur;
				cur++;
			}
			r5 = cur%5;
			r7 = cur%7;
			r11 = cur%11;
			r13 = cur%13;
			r17 = cur%17;
			r19 = cur%19;
		}
		adds.add(6*(cur-prev));
		
		// first two pairs
		System.out.println("3, 5");
		System.out.println("5, 7");
		
		int count = 2; // the number of twin prime pairs outputted so far
		int addsdex = -1; // the current index of adds
		
		// every twin prime pair is of the form 6k-1, 6k+1 for some integer k
		// so check multiples of 6, skipping when a candidate prime is actually a multiple of a small prime
		for(long n = 12; count<numPairs; n+=adds.get(addsdex)) { // n is the number to check for primality, with n < 341550071728321
			addsdex++;
			if(addsdex==adds.size()) addsdex = 0;
			
			if(isPrime(n-1) && isPrime(n+1)) {
				System.out.println((n-1)+", "+(n+1));
				count++;
			}
		}
	}
	
	public static boolean isPrime(long n) {
		long d = n-1;
		long r = 0;
		// find d and r such that n = d*(2^r) + 1, where d is odd
		while(d%2 == 0) {
			d/=2;
			r++;
		}
		
		// depending on the size of the number, certain bases will suffice for a deterministic Miller–Rabin test
		//if(n<2047) return (mr(2, d, r, n));
		if(n<1373653) return (mr(2, d, r, n) && mr(3, d, r, n));
		if(n<25326001) return (mr(2, d, r, n) && mr(3, d, r, n) && mr(5, d, r, n));
		if(n<3215031751L) return (mr(2, d, r, n) && mr(3, d, r, n) && mr(5, d, r, n) && mr(7, d, r, n));
		if(n<2152302898747L) return (mr(2, d, r, n) && mr(3, d, r, n) && mr(5, d, r, n) && mr(7, d, r, n) && mr(11, d, r, n));
		if(n<3474749660383L) return (mr(2, d, r, n) && mr(3, d, r, n) && mr(5, d, r, n) && mr(7, d, r, n) && mr(11, d, r, n) && mr(13, d, r, n));
		if(n<341550071728321L) return (mr(2, d, r, n) && mr(3, d, r, n) && mr(5, d, r, n) && mr(7, d, r, n) && mr(11, d, r, n) && mr(13, d, r, n) && mr(17, d, r, n));
		
		return false; // placeholder - it should never get here
	}
	
	// n = d*(2^r) + 1, where n is the integer to test for primality and d is odd
	public static boolean mr(long a, long d, long r, long n) { // Miller–Rabin test for base a
		long x = modexp(1, a, d, n);
		if(x == 1 || x == n-1) return true;
		
		x = (x*x)%n;
		for(int i = 0; i<r-1; i++) {
			if(x == n-1) return true;
			x = (x*x)%n;
		}
		
		return false;
	}
	
	// modular exponentiation
	public static long modexp (long r, long b, long e, long m) { // remainder (initially 1), base, exponent, mod
		if(m == 1) return 0;
		if(e == 0) return r;
		if(e%2 == 1) return modexp((r*b)%m, (b*b)%m, e/2, m); // e/2 is floor
		return modexp(r, (b*b)%m, e/2, m);
	}
}