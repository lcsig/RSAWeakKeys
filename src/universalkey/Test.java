package universalkey;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Test {
    public static void main(String[] args) {
        SecureRandom x = new SecureRandom();
        for (int i = 0; i < 20; i++) {
            System.out.println(BigInteger.probablePrime(3, x));
        }
    }
}
