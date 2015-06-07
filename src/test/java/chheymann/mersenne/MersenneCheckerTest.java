package chheymann.mersenne;


import org.junit.Assert;
import org.junit.Test;


public class MersenneCheckerTest {
    static PrimeSource primeSource = new PrimeSource(1000000);

    @Test
    public void testFastFactorCheck() {
        Assert.assertEquals(1L, new MersenneChecker(23).mod_Mp(47));
        Assert.assertNotEquals(1L, new MersenneChecker(23).mod_Mp(123));
    }

    @Test
    public void testMersenneCheck() {
        Assert.assertTrue(new MersenneChecker(2, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(3, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(5, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(7, primeSource).isPrime());
        Assert.assertFalse(new MersenneChecker(11, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(13, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(17, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(19, primeSource).isPrime());
        Assert.assertFalse(new MersenneChecker(23, primeSource).isPrime());
        Assert.assertFalse(new MersenneChecker(29, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(31, primeSource).isPrime());
        Assert.assertFalse(new MersenneChecker(37, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(61, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(89, primeSource).isPrime());
        Assert.assertTrue(new MersenneChecker(107, primeSource).isPrime());
    }

    @Test
    public void testLucasLehmerDuration() {
        timeLucasLehmer(1279);
        timeLucasLehmer(1279);
        timeLucasLehmer(2203);
        timeLucasLehmer(3217);
        timeLucasLehmer(4253);
        timeLucasLehmer(9689);
//        timeLucasLehmer(21701); // takes 70s
//        timeLucasLehmer(44497); // takes very long
    }

    void timeLucasLehmer(int p) {
        long start = System.currentTimeMillis();
        new MersenneChecker(p, primeSource).doLucasLehmerTest();
        System.out.println("p: " + p + " took " + (System.currentTimeMillis() - start) + "ms");
    }
}
