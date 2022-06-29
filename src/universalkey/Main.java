package universalkey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Scanner;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;


public class Main {

    public static void main(String[] args) throws IOException {
        BigInteger q, p, n, e = BigInteger.valueOf(65537);
        int primeBitSize = 1024, maxTotientFactor = 20;

        /*
         * Generating the primes
         */
        System.out.println("[+] Generating primes.");
        BigInteger[] genPrimes = generateTwoPrimesWithConstraintOnMaximumTotientFactorSize(maxTotientFactor, primeBitSize, e);
        q = genPrimes[0];
        p = genPrimes[1];
        n = q.multiply(p);
        System.out.println("[+] Primes has been generated.");
        System.out.println("e : " + e);
        System.out.println("q : " + q);
        System.out.println("p : " + p);
        System.out.println("N : " + n);

        /*
         * Encrypt Some Data ...
         */
        System.out.println("[+] Encrypting Some Data.");
        BigInteger m = new BigInteger("111000222000333000444000555000666000777000888000999000111000");
        BigInteger cipher = m.modPow(e, n);
        System.out.println("[+] Data has been encrypted.");
        System.out.println("m : " + m);
        System.out.println("c : " + cipher);

        /*
         * Decrypt With The Universal Key, without knowing the private key
         */
        System.out.println("[+] Decryption process has been started.");
        BigInteger uniKey;
        if (!Files.exists(Path.of("uniKey_24d.txt"), NOFOLLOW_LINKS)) {
            File file = new File("uniKey_24.txt");
            Scanner sc = new Scanner(file);
            String k = sc.nextLine().trim();
            System.out.println("[+] Key has been read successfully. Processing into memory ... Please wait!");

            uniKey = new BigInteger(k, 10);
            System.out.println("[+] Key has been processed successfully.");

            uniKey = uniKey.divide(e);
            System.out.println("[+] GCD(e, UniKey): " + uniKey.gcd(e));

            uniKey = e.modInverse(uniKey);
            System.out.println("[+] Private Key Has Been Generated.");
            System.out.println("[+] d Size: " + uniKey.bitLength());

            FileWriter fileWriter = new FileWriter("uniKey_24d.txt");
            fileWriter.write(uniKey.toString());
            fileWriter.close();
            System.out.println("[+] Key Has Ben Written Successfully ... (uniKey_24d.txt)");
        } else {
            File file =  new File("uniKey_24d.txt");
            Scanner sc = new Scanner(file);
            String k = sc.nextLine().trim();
            System.out.println("[+] Key has been read successfully. Processing into memory ... Please wait!");
            uniKey = new BigInteger(k, 10);
            System.out.println("[+] Key has been processed successfully.");
        }

        /*
         * Finally, decrypting the message by the calculated private key!
         */
        System.out.println("m : " + cipher.modPow(uniKey, n));
        System.out.println("[+] Finished Successfully.");
    }

    /**
     * Generate two primes with a constraint on the maximum totient factor and check for RSA conditions
     *
     *  Check The Conditions
     *      1. sqrt(2) * 2^(nlen / 2 - 1) ≤ q ≤ 2^(nlen / 2) - 1
     *      2. sqrt(2) * 2^(nlen / 2 - 1) ≤ p ≤ 2^(nlen / 2) - 1
     *      3. |p – q| > 2^(nlen / 2 - 100)
     *
     * @param maxTotientFactorBitSize The maximum bits size for each factor of (phi(prime) - 1)
     * @param primeBitsSize The bit length of primes
     * @return An array of BigInteger that contains tow elements, and they satisfy RSA key conditions
     */
    public static BigInteger[] generateTwoPrimesWithConstraintOnMaximumTotientFactorSize(int maxTotientFactorBitSize, int primeBitsSize, BigInteger e) {
        BigInteger q = null, p = null, tmp = null;
        BigInteger[] bigIntegersArray = new BigInteger[2];
        SecureRandom rnd = new SecureRandom();
        // BigInteger differenceSize = BigInteger.TWO.pow(primeBitsSize % maxTotientFactorBitSize);
        int primeCertainty = 1000;


        while(true) {
            for (int i = 0; i < 2; i++) {
                while(true) {
                    tmp = TWO;

                    while(tmp.bitLength() < primeBitsSize) {
                        if (primeBitsSize - tmp.bitLength() <= 2)
                            tmp = TWO;
                        else
                            tmp = tmp.multiply(getPrime(Math.min(primeBitsSize - tmp.bitLength(), maxTotientFactorBitSize), rnd));
                    }

                    if (tmp.bitLength() == primeBitsSize && tmp.add(ONE).isProbablePrime(primeCertainty)) {
                        tmp = tmp.add(ONE);
                        bigIntegersArray[i] = tmp;
                        break;
                    }
                }
            }

            boolean isRSAConditionsSatisfied = checkRSAPrimesConditions(bigIntegersArray[0], bigIntegersArray[1], primeBitsSize * 2, e);
            if (isRSAConditionsSatisfied)
                break;
        }

        return bigIntegersArray;
    }

    /**
     * Get prime number that is bigger than (sqrt(2) * 2^(nlen / 2 - 1))
     *
     * @param primeBitsSize The prime number size
     * @param rnd SecureRandom Variable
     * @return A prime number that satisfies (q >= sqrt(2) * 2^(nlen / 2 - 1))
     */
    public static BigInteger getPrime(int primeBitsSize, SecureRandom rnd) {
        BigDecimal TWO = BigDecimal.valueOf(2);
        BigDecimal _2sqrt = TWO.sqrt(new MathContext(primeBitsSize));
        BigInteger prime;

        do {
            prime = BigInteger.probablePrime(primeBitsSize, rnd);
        } while (_2sqrt.multiply(TWO.pow(primeBitsSize - 1)).compareTo(new BigDecimal(prime)) >= 0);

        return prime;
    }

    /**
     * Check RSA Modulus Factors Conditions
     *      1. sqrt(2) * 2^(nlen / 2 - 1) ≤ q ≤ 2^(nlen / 2) - 1
     *      2. sqrt(2) * 2^(nlen / 2 - 1) ≤ p ≤ 2^(nlen / 2) - 1
     *      3. |p – q| > 2^(nlen / 2 - 100)
     *
     * @param q First Prime Factor
     * @param p Second Prime Factor
     * @param modulusSize The N bit size
     * @param e The value of the public key
     * @return True if the conditions satisfied, false otherwise.
     */
    public static boolean checkRSAPrimesConditions(BigInteger q, BigInteger p, int modulusSize, BigInteger e) {
        MathContext c = new MathContext(q.bitLength());
        BigDecimal Q = new BigDecimal(q.min(p));
        BigDecimal P = new BigDecimal(q.max(p));

        BigDecimal TWO = BigDecimal.valueOf(2);
        BigDecimal _2sqrt = TWO.sqrt(c);
        BigDecimal leftTerm = _2sqrt.multiply(TWO.pow(modulusSize / 2 - 1));
        BigDecimal rightTerm = TWO.pow(modulusSize / 2).subtract(BigDecimal.ONE);

        boolean c1 = (leftTerm.compareTo(Q) <= 0 && Q.compareTo(rightTerm) <= 0);
        boolean c2 = (leftTerm.compareTo(P) <= 0 && P.compareTo(rightTerm) <= 0);
        boolean c3 = (P.subtract(Q).compareTo(TWO.pow(modulusSize / 2 - 100)) >= 0);

        if (c1 && c2 && c3) {
            BigInteger LCM = p.subtract(ONE)
                    .multiply(q.subtract(ONE))
                    .divide(p.subtract(ONE).gcd(q.subtract(ONE)));
            BigInteger d = e.modInverse(LCM);

            return d.compareTo(BigInteger.TWO.pow(modulusSize / 2)) > 0 && d.compareTo(LCM) < 0;
        }

        return false;
    }
}
