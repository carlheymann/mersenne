package chheymann.mersenne;


import org.junit.Assert;
import org.junit.Test;

import chheymann.mersenne.PrimeSource;


public class PrimeSourceTest {
    static PrimeSource primeSource = new PrimeSource(1000000);

    @Test
    public void checkSmallPrimes() {
        int i = 0;
        Assert.assertEquals(2, primeSource.get(i++));
        Assert.assertEquals(3, primeSource.get(i++));
        Assert.assertEquals(5, primeSource.get(i++));
        Assert.assertEquals(7, primeSource.get(i++));
        Assert.assertEquals(11, primeSource.get(i++));
        Assert.assertEquals(13, primeSource.get(i++));
        Assert.assertEquals(17, primeSource.get(i++));
        Assert.assertEquals(19, primeSource.get(i++));
        Assert.assertEquals(23, primeSource.get(i++));
        Assert.assertEquals(29, primeSource.get(i++));
        Assert.assertEquals(31, primeSource.get(i++));
        Assert.assertEquals(37, primeSource.get(i++));
        Assert.assertEquals(41, primeSource.get(i++));
        Assert.assertEquals(43, primeSource.get(i++));
        Assert.assertEquals(47, primeSource.get(i++));
        Assert.assertEquals(53, primeSource.get(i++));
        Assert.assertEquals(59, primeSource.get(i++));
    }

    @Test
    public void checkLargeKnownPrimes() {
        Assert.assertEquals(389171, primeSource.get(33000 - 1));
        Assert.assertEquals(389173, primeSource.get(33001 - 1));
        Assert.assertEquals(389189, primeSource.get(33002 - 1));
    }
}
