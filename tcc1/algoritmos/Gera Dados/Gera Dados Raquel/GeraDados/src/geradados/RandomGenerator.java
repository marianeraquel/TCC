package geradados;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * --------------------------------------------------------------------------
 * This is an ANSI C library for generating random variates from six discrete
 * distributions
 *
 * Generator Range (x) Mean Variance
 *
 * Bernoulli(p) x = 0,1 p p*(1-p) Binomial(n, p) x = 0,...,n n*p n*p*(1-p)
 * Equilikely(a, b) x = a,...,b (a+b)/2 ((b-a+1)*(b-a+1)-1)/12 Geometric(p) x =
 * 0,... p/(1-p) p/((1-p)*(1-p)) Pascal(n, p) x = 0,... n*p/(1-p)
 * n*p/((1-p)*(1-p)) Poisson(m) x = 0,... m m
 *
 * and seven continuous distributions
 *
 * Uniform(a, b) a < x < b (a + b)/2 (b - a)*(b - a)/12 Exponential(m) x > 0 m
 * m*m Erlang(n, b) x > 0 n*b n*b*b Normal(m, s) all x m s*s Lognormal(a, b) x >
 * 0 see below Chisquare(n) x > 0 n 2*n Student(n) all x 0 (n > 1) n/(n - 2) (n
 * > 2)
 *
 * For the a Lognormal(a, b) random variable, the mean and variance are
 *
 * mean = exp(a + 0.5*b*b) variance = (exp(b*b) - 1) * exp(2*a + b*b)
 *
 * Name : rvgs.c (Random Variate GeneratorS) Author : Steve Park & Dave Geyer
 * Language : ANSI C Latest Revision : 10-28-98
 * --------------------------------------------------------------------------
 */
public class RandomGenerator {

    Random random = new Random();
    
    public int next(){
        return Math.abs(random.nextInt());
    }
    
        public double nextNormal(){
        return Math.abs(random.nextGaussian());
    }

    /*
     * ======================================================== Returns 1 with
     * probability p or 0 with probability 1 - p. NOTE: use 0.0 < p < 1.0
     * ========================================================
     */ 
    
    long Bernoulli(double p) {
        return ((random.nextFloat() < (1.0 - p)) ? 0 : 1);
    }
    /*
     * ================================================================ Returns
     * a binomial distributed integer between 0 and n inclusive. NOTE: use n > 0
     * and 0.0 < p < 1.0
     * ================================================================
     */

    long Binomial(long n, double p) {
        long i, x = 0;

        for (i = 0; i < n; i++) {
            x += Bernoulli(p);
        }
        return (x);
    }
    
    /*
     * ===================================================================
     * Returns an equilikely distributed integer between a and b inclusive.
     * NOTE: use a < b
     * ===================================================================
     */

    long Equilikely(long a, long b) {
        return (a + (long) ((b - a + 1) * random.nextFloat()));
    }
    
    /*
     * ==================================================== 
     * Returns a geometric
     * distributed non-negative integer. NOTE: use 0.0 < p < 1.0
     * ====================================================
     */

    long Geometric(double p) {
        return ((long) (Math.log(1.0 - random.nextFloat()) / Math.log(p)));
    }
    
    /*
     * ================================================= 
     * Returns a Pascal
     * distributed non-negative integer. NOTE: use n > 0 and 0.0 < p < 1.0
     * =================================================
     */

    long Pascal(long n, double p) {
        long i, x = 0;

        for (i = 0; i < n; i++) {
            x += Geometric(p);
        }
        return (x);
    }
    
    /*
     * ================================================== 
     * Returns a Poisson
     * distributed non-negative integer. NOTE: use m > 0
     * ==================================================
     */

    long Poisson(double m) {
        double t = 0.0;
        long x = 0;

        while (t < m) {
            t += Exponential(1.0);
            x++;
        }
        return (x - 1);
    }
    
    /*
     * =========================================================== 
     * Returns a
     * uniformly distributed real number between a and b. NOTE: use a < b
     * ===========================================================
     */

    double Uniform(double a, double b) {
        return (a + (b - a) * random.nextFloat());
    }
    /*
     * ========================================================= 
     * Returns an
     * exponentially distributed positive real number. NOTE: use m > 0.0
     * =========================================================
     */

    double Exponential(double m) {
        return (-m * Math.log(1.0 - random.nextFloat()));
    }
    
    /*
     * ================================================== 
     * Returns an Erlang
     * distributed positive real number. NOTE: use n > 0 and b > 0.0
     * ==================================================
     */

    double Erlang(long n, double b) {
        long i;
        double x = 0.0;

        for (i = 0; i < n; i++) {
            x += Exponential(b);
        }
        return (x);
    }
    

    /*
     * ========================================================================
     * Returns a normal (Gaussian) distributed real number. NOTE: use s > 0.0
     *
     * Uses a very accurate approximation of the normal idf due to Odeh & Evans,
     * J. Applied Statistics, 1974, vol 23, pp 96-97.
     * ========================================================================
     */ 
    double Normal(double m, double s) {
        double p0 = 0.322232431088;
        double q0 = 0.099348462606;
        double p1 = 1.0;
        double q1 = 0.588581570495;
        double p2 = 0.342242088547;
        double q2 = 0.531103462366;
        double p3 = 0.204231210245e-1;
        double q3 = 0.103537752850;
        double p4 = 0.453642210148e-4;
        double q4 = 0.385607006340e-2;
        double u, t, p, q, z;

        u = random.nextFloat();
        if (u < 0.5) {
            t = Math.sqrt(-2.0 * Math.log(u));
        } else {
            t = Math.sqrt(-2.0 * Math.log(1.0 - u));
        }
        p = p0 + t * (p1 + t * (p2 + t * (p3 + t * p4)));
        q = q0 + t * (q1 + t * (q2 + t * (q3 + t * q4)));
        if (u < 0.5) {
            z = (p / q) - t;
        } else {
            z = t - (p / q);
        }
//        if (z >= 2) return s;
        return Math.abs((m + s * z));
    }

    /*
     * ==================================================== 
     * Returns a lognormal
     * distributed positive real number. NOTE: use b > 0.0
     * ====================================================
     */ 
     double Lognormal(double a, double b) {
        return (Math.exp(a + b * Normal(0.0, 1.0)));
    }
     
    /*
     * ===================================================== 
     * Returns a
     * chi-square distributed positive real number. NOTE: use n > 0
     * =====================================================
     */

    double Chisquare(long n) {
        long i;
        double z, x = 0.0;

        for (i = 0; i < n; i++) {
            z = Normal(0.0, 1.0);
            x += z * z;
        }
        return (x);
    }

    /*
     * =========================================== 
     * Returns a student-t
     * distributed real number. NOTE: use n > 0
     * ===========================================
     */ 
    double Student(long n) {
        return (Normal(0.0, 1.0) / Math.sqrt(Chisquare(n) / n));
    }
}

//public class RandomGenerator {
//
//    private long seed = 0;
//    private static final long mask32 = (1l << 32) - 1;
//    
//    /**
//     * The number of iterations separating the precomputed seeds.
//     */
//    private static final int seedSkip = 128 * 1024 * 1024;
//    /**
//     * The precomputed seed values after every seedSkip iterations. There should
//     * be enough values so that a 2**32 iterations are covered.
//     */
//    private static final long[] seeds = new long[]{0L,
//        4160749568L,
//        4026531840L,
//        3892314112L,
//        3758096384L,
//        3623878656L,
//        3489660928L,
//        3355443200L,
//        3221225472L,
//        3087007744L,
//        2952790016L,
//        2818572288L,
//        2684354560L,
//        2550136832L,
//        2415919104L,
//        2281701376L,
//        2147483648L,
//        2013265920L,
//        1879048192L,
//        1744830464L,
//        1610612736L,
//        1476395008L,
//        1342177280L,
//        1207959552L,
//        1073741824L,
//        939524096L,
//        805306368L,
//        671088640L,
//        536870912L,
//        402653184L,
//        268435456L,
//        134217728L,};
//
//    /**
//     * Start the random number generator on the given iteration.
//     *
//     * @param initalIteration the iteration number to start on
//     */
//    RandomGenerator(long initalIteration) {
//        int baseIndex = (int) ((initalIteration & mask32) / seedSkip);
//        seed = seeds[baseIndex];
//        for (int i = 0; i < initalIteration % seedSkip; ++i) {
//            next();
//        }
//    }
//
//    RandomGenerator() {
//        this(0);
//    }
//
//    long next() {
//        seed = (seed * 3141592621l + 663896637) & mask32;
//        return seed;
//    }
//}