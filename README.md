# mersenne
Some java code to find Mersenne primes

# Build and run it
Ensure java 8 and gradle is installed. Then:
gradle build
java -jar build/libs/mersenne.jar

## What does it do?
A main method spawns Mersenne checkers for prime exponents below 1 million.
These checkers are executed in several threads, one thread per core.
Note that the results could appear out of order for the first few fast checks.
The Mersenne number M_p = 2^p - 1.
Each check for Mersenne number M_p does the following:
- Look for a small prime factor of M_p, below 2^32:
-- consider each possible factor in the form 2kp + 1
-- skip possible factors that are not congruent to 1 or 7 (mod 8)
-- skip possible factors that themselves have small factors (below 100)
-- test the factor "c" using the efficient "2^p mod c" algorithm as per http://www.mersenne.org/various/math.php
- If a small factor is found, M_p is not a prime
- If no small factor is found do a Lucas Lehmer test:
-- Use BigInteger.modPow(..) to square s in (mod M_p).
-- TODO: use FFTs to square faster

## Results and limits
All Mersenne numbers up to p=4423 are foud correctly within a minute or so, and
up to p=23209 are found correctly within an hour or so, depending on hardware.
Small factors are found for about 50-60% of primes tested.
For higher values of p, the Lucas Lehmer test gets too slow. E.g on my laptop:
p=1279 takes a few ms
p=4253 takes 1s
p=9689 takes 7s
p=21701 takes 70s
p=44497 takes 8 minutes

A faster Lucas Lehmer test is therefore needed. A faster square and mod can
apparently be done using FFTs, so using a FFTW implementation would be 
the next logical step.

The GIMPS project uses Pollard's p-1 factoring as well, this should also be investigated,
to find a factor quickly if there is a factor q where q-1 is itself highly composite.

The current implementation only considers p < 1million. To test higher p, the
Lucas Lehmer test must be sped up, and more primes can be generated on the fly
as necessary, in PrimeSource.

The stats could be used to tune the limits when searching for factors.
