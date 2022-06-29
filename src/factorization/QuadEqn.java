package factorization;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Optional;

/**
 * This class contains operations on the quadratic equation of the form
 *      ax^2 + bx + c = 0
 */
public class QuadEqn {

    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal ONE = BigDecimal.ONE;
    public static final BigDecimal TWO = ONE.add(ONE);
    public static final BigDecimal FOUR = TWO.add(TWO);


    public static Optional<BigDecimal[]> solve(BigInteger a, BigInteger b, BigInteger c, MathContext precision) {
        return solve(new BigDecimal(a), new BigDecimal(b), new BigDecimal(c), precision);
    }

    /**
     * A method to give the solution to the quadratic equation
     *      Sol = (-b +- sqrt(b^2 - 4ac)) / 2a
     *
     * @param a Coefficient of X^2  [ax^2 + bx + c = 0]
     * @param b Coefficient of X    [ax^2 + bx + c = 0]
     * @param c Constant c          [ax^2 + bx + c = 0]
     * @return  Array of BigDecimal that contains the two solutions.
     */
    public static Optional<BigDecimal[]> solve(BigDecimal a, BigDecimal b, BigDecimal c, MathContext precision) {
        BigDecimal firstSol, secondSol;

        try {
            BigDecimal sqrt = b.pow(2).subtract(FOUR.multiply(a).multiply(c)).sqrt(precision);   // Calc sqrt(b^2 - 4ac)
            BigDecimal minusB = ZERO.subtract(b);                                                   // Calc -b
            BigDecimal twoA = TWO.multiply(a);                                                      // Calc 2a

            firstSol = minusB.add(sqrt).divide(twoA, precision);         // Sol1 = (-b + sqrt(b^2 - 4ac)) / 2a
            secondSol = minusB.subtract(sqrt).divide(twoA, precision);   // Sol2 = (-b + sqrt(b^2 - 4ac)) / 2a

            BigDecimal[] ret = {firstSol, secondSol};
            return Optional.of(ret);

        } catch(Exception e) {
            System.out.println("[!] " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * A function to give the INTEGERS solutions to the quadratic equation, and returns empty if one of them is decimal
     *
     * @param a Coefficient of X^2  [ax^2 + bx + c = 0]
     * @param b Coefficient of X    [ax^2 + bx + c = 0]
     * @param c Constant c          [ax^2 + bx + c = 0]
     * @return Array of BigInteger that contains the two solutions or returns empty if no integer solutions found
     */
    public static Optional<BigInteger> solveForInteger(BigInteger a, BigInteger b, BigInteger c) {
        int maxBigLen = Math.max(Math.max(a.bitLength(), b.bitLength()), c.bitLength());
        Optional<BigDecimal[]> solutions = solve(a, b, c, new MathContext(maxBigLen + 7));

        if (solutions.isEmpty())
            return Optional.empty();
        else {
            BigDecimal[] arr = solutions.get();

            if (isIntegerValue(arr[0]))
                return Optional.of(arr[0].toBigIntegerExact());
            else if (isIntegerValue(arr[1]))
                return Optional.of(arr[1].toBigIntegerExact());
            else
                return Optional.empty();
        }
    }

    public static boolean isIntegerValue(BigDecimal bd) {
        return bd.stripTrailingZeros().scale() <= 0;
    }

}