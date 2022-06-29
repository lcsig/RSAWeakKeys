package universalkey;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class UniversalModulusGeneration {

    /**
     * Fast prime check function
     *
     * @param n Integer to be checked
     * @implNote Credits for Bart Kiers
     * @return true if prime, false otherwise.
     */
    static boolean isPrime(long n) {
        if (n < 2)
            return false;

        if (n == 2 || n == 3)
            return true;

        if (n % 2 == 0 || n % 3 == 0)
            return false;

        long sqrtN = (long)Math.sqrt(n) + 1;

        for (long i = 6L; i <= sqrtN; i += 6) {
            if (n % (i - 1) == 0 || n % (i + 1) == 0)
                return false;
        }

        return true;
    }


    /**
     * Multiply all primes from 2 up to 2^24
     */
    public static void main(String[] args) throws IOException {
        System.out.println("[+] Universal Modulus Generation Function (Multiply all primes from 2 up to 2^24)");

        BigInteger uniKey = BigInteger.ONE;

        for (long i = 2; i < Math.pow(2, 24); i++)
            if (isPrime(i))
                uniKey = uniKey.multiply(BigInteger.valueOf(i));

        FileWriter fileWriter = new FileWriter("uniKey_24.txt");
        fileWriter.write(uniKey.toString());
        fileWriter.close();

        System.out.println("[+] Generated Successfully.");
    }

}
