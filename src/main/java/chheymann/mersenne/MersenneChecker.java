package chheymann.mersenne;


import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;

import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;


/**
 * Single threaded class to check if the Mersenne number with exponent "p", called M_p, is prime.
 */
public class MersenneChecker {
    private static final BigInteger TWO = valueOf(2);
    private static final BigInteger FOUR = valueOf(4);

    /**
     * We won't check primes bigger than this for being factors of M_p
     */
    private static final BigInteger MAX_FACTOR_CEIL = ONE.shiftLeft(32);
    /**
     * We won't check primes bigger than this when determining if a potential factor is itself
     * possibly not prime
     */
    private static final long MAX_SMALLPRIME = 100;

    private final int p;
    private final PrimeSource primeSource;
    private final Statistics stats;
    private final CountDownLatch latch;

    /**
     * The Mersenne number 2^p - 1
     */
    private BigInteger M_p;

    /**
     * FOR TESTING ONLY
     * 
     * @param p the Mersenne number's exponent
     */
    public MersenneChecker(int p) {
        this(p, null, null, null);
        M_p = ONE.shiftLeft(p).subtract(ONE);
    }

    /**
     * FOR TESTING ONLY
     * 
     * @param p the Mersenne number's exponent
     * @param primeSource a source of prime numbers, used to check for very small factors of
     *            potential factors of M_p. Ignored if null.
     */
    MersenneChecker(int p, PrimeSource primeSource) {
        this(p, primeSource, null, null);
        M_p = ONE.shiftLeft(p).subtract(ONE);
    }

    /**
     * @param p the Mersenne number's exponent
     * @param primeSource a source of prime numbers, used to check for very small factors of
     *            potential factors of M_p. Ignored if null.
     * @param stats used to track statistics about this check. Ignored if null
     * @param latch used to signal when the Mersenne check is complete. Ignored if null
     */
    public MersenneChecker(int p, PrimeSource primeSource, Statistics stats, CountDownLatch latch) {
        this.p = p;
        this.primeSource = primeSource;
        this.stats = stats;
        this.latch = latch;
    }

    /**
     * Compute M_p, then check if it is prime. First check if it has any small factors, up to
     * MAX_FACTOR_CEIL, and if not do the Lucas Lehmer test.
     * 
     * @return true if M_p is prime
     */
    public boolean isPrime() {
        try {
            M_p = ONE.shiftLeft(p).subtract(ONE);

            // do a very crude "square root" by halving the bits and maxing them out
            BigInteger maxFactor = (ONE.shiftLeft((int) Math.ceil(M_p.bitLength() / 2d))).subtract(ONE);
            if (maxFactor.compareTo(MAX_FACTOR_CEIL) > 0) {
                maxFactor = MAX_FACTOR_CEIL;
            }

            long start = System.currentTimeMillis();
            boolean foundFactor = hasSmallFactorBelow(maxFactor.longValue());
            if (stats != null) {
                stats.searchedForFactor(p, System.currentTimeMillis() - start);
            }
            if (foundFactor) {
                return false;
            }
            if (maxFactor.compareTo(MAX_FACTOR_CEIL) > 0) {
                // no clamping was needed, so we can skip the Lucas Lehmer stage (factor search was exhaustive)
                return true;
            }

            start = System.currentTimeMillis();
            boolean result = doLucasLehmerTest();
            if (stats != null) {
                stats.perfomedLucasLehmer(p, System.currentTimeMillis() - start);
            }
            return result;
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    /**
     * Check if M_p has a small factor, checking all possible factors up to maxFactor.
     * <p>
     * factors all have the form 2pk + 1
     * <p>
     * factors are all congruent to 1 or 7 (mod 8)
     * 
     * @param maxFactor
     * @return true if M_p has a factor below maxFactor
     */
    private boolean hasSmallFactorBelow(long maxFactor) {
        long candidate;
        long twoP = 2L * p;
        long base = 0L;
        short cMod8;
        while (true) {
            base += twoP;
            candidate = base + 1;
            if (candidate > maxFactor) {
                return false;
            }
            cMod8 = (short) (candidate & 0x07);
            if (cMod8 == 3 || cMod8 == 5) {
                continue;
            }
            if (hasSmallPrimeFactor(candidate)) {
                continue;
            }
            if (mod_Mp(candidate) == 1L) {
                return true;
            }
        }
    }

    /**
     * @param candidate
     * @return true if the candidate number has a small prime factor, i.e. is guaranteed to not be
     *         prime itself
     */
    boolean hasSmallPrimeFactor(long candidate) {
        if (primeSource == null) {
            return false;
        }
        for (Integer p : primeSource) {
            if (p > MAX_SMALLPRIME) {
                return false;
            }
            if (candidate % p == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * A fast algorithm for "2^p mod candidate", as per http://www.mersenne.org/various/math.php
     * 
     * @param candidate the possible factor of 2^p, to check in the algorithm
     * @return 0 if the candidate is a factor of 2^p, otherwise any number between 1 and candidate-1
     */
    long mod_Mp(long candidate) {
        long x = 1;
        int leadingZeros = Long.numberOfLeadingZeros(p);
        int numExpBits = 64 - leadingZeros;
        long exponentBits = Long.reverse(p) >> leadingZeros;
        int topBit;
        while (numExpBits > 0) {
            x = x * x;
            topBit = (int) (exponentBits & 1);
            x <<= topBit;
            x = x % candidate;
            exponentBits >>= 1;
            numExpBits--;
        }
        return x;
    }

    /**
     * A basic implementation of the Lucas Lehmer test, using BigInteger.modPow to do modular
     * exponentiation quickly.
     * 
     * TODO: FFT to square and mod faster?
     * 
     * @return true if the Lucas Lehmer test indicates that M_p is a prime
     */
    boolean doLucasLehmerTest() {
        // the test is only valid for p > 2
        if (p == 2) {
            return true;
        }

        BigInteger s = FOUR;
        for (int i = 1; i <= (p - 2); i++) {
            s = s.modPow(TWO, M_p).subtract(TWO);
        }
        // if the result is zero, there's no need for a last mod
        return s.equals(ZERO);
    }
}
