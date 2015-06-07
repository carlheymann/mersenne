package chheymann.mersenne;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Used to obtained prime numbers in increasing order
 */
public class PrimeSource implements Iterable<Integer> {
    private final int MAX_PRIME;
    private List<Integer> primes;

    public PrimeSource(int maxPrime) {
        MAX_PRIME = maxPrime;
        initialisePrimes();
    }

    @Override
    public Iterator<Integer> iterator() {
        return primes.iterator();
    }

    public int get(int index) {
        return primes.get(index);
    }

    public List<Integer> getPrimes() {
        return primes;
    }

    private void initialisePrimes() {
        primes = new ArrayList<Integer>();
        primes.add(2);
        primes.add(3);
        for (int i = 6; i < MAX_PRIME; i += 6) {
            testAndAdd(i - 1);
            testAndAdd(i + 1);
        }
        primes = Collections.unmodifiableList(primes);
    }

    private void testAndAdd(int i) {
        if (isPrime(i)) {
            addPrime(i);
        }
    }

    private boolean isPrime(int i) {
        int squareRoot = (int) Math.sqrt(i);
        for (Integer prime : primes) {
            if (prime > squareRoot) {
                break;
            }
            if (i % prime == 0) {
                return false;
            }

        }
        return true;
    }

    private void addPrime(int prime) {
        primes.add(prime);
    }
}
