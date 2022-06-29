package factorization;

import java.math.BigInteger;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        BigInteger n = new BigInteger("18369583373607319524848230962864856788641872197252249438510296626216984019007677702311097233452292800473780549833857255316215760088065227188397682289515575253632392397154509976661652110491280121945920658057741810958542678894186440036821454425304791711282798209813170929253634748758078024559105723170657056597590907448619311520601807972190177072206898115185040151891234092197380308491753453831541747105318516636718409456228079194323214814365300355951159745383220310112790585573021538809712101420219793936813969140292008646607866839526754976413947326170794193769109782049514397188341708331264800385010840190811956595297");
        Optional<BigInteger[]> sol = factorN(n, 24, 24, true);

        if (sol.isPresent()) {
            BigInteger q = sol.get()[0];
            BigInteger p = sol.get()[1];

            System.out.println("N : " + n);
            System.out.println("Q : " + q);
            System.out.println("P : " + p);
            System.out.println("qp: " + q.multiply(p));
        } else {
            System.out.println("[+] Could not factorize");
        }
    }

    /**
     * Factorization of N where q - 1 = A.C and p - 1 = B.C
     *
     * @param n The RSA modulus (q times p)
     * @param sizeOfA Maximum size of A in bits to be brute forced
     * @param sizeOfB Maximum size of B in bits to be brute forced
     * @return List of BigIntegers that contains two elements (i.e. p & q)
     */
    public static Optional<BigInteger[]> factorN(BigInteger n, int sizeOfA, int sizeOfB, boolean echo) {
        BigInteger maxLimitOfA = BigInteger.TWO.pow(sizeOfA);
        BigInteger maxLimitOfB = BigInteger.TWO.pow(sizeOfB);
        BigInteger oneSubtractN = BigInteger.ONE.subtract(n);
        BigInteger echoVal = BigInteger.valueOf(1000);

        for (BigInteger a = BigInteger.ONE;
             a.compareTo(maxLimitOfA) < 0;
             a = a.add(BigInteger.ONE)
        ) {
            for (BigInteger b = BigInteger.ONE;
                 b.compareTo(maxLimitOfB) < 0;
                 b = b.add(BigInteger.ONE)
            ) {
                Optional<BigInteger> sol = QuadEqn.solveForInteger(a.multiply(b), a.add(b), oneSubtractN);

                if (echo && b.mod(echoVal).equals(BigInteger.ZERO))
                    System.out.println(b);

                if (sol.isPresent()) {
                    BigInteger q = a.multiply(sol.get()).add(BigInteger.ONE);
                    BigInteger p = b.multiply(sol.get()).add(BigInteger.ONE);

                    return Optional.of(new BigInteger[] {q, p});
                }
            }
        }

        return Optional.empty();
    }

}