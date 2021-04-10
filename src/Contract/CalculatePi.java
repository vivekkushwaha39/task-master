/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import Contract.Task;
import java.io.Serializable;
import java.math.BigDecimal;

public class CalculatePi implements Task, Serializable {

    int upto = 5;
    BigDecimal res = BigDecimal.valueOf(0);
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);

    public void setUpto(int upto) {
        this.upto = upto;
    }

    @Override
    public void executeTask() {
        res = CalculatePi.computePi(upto);
    }

    @Override
    public Object getResult() {
        return res;
    }

    public static BigDecimal computePi(int digits) {
        int scale = digits + 5;
        BigDecimal arctan1_5 = arctan(5, scale, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal arctan1_239 = arctan(239, scale, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);
        return pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal arctan(int inverseX, int scale, int roundingMode) {
        BigDecimal result, numer, term;
        BigDecimal invX = BigDecimal.valueOf(inverseX);
        BigDecimal invX2 = BigDecimal.valueOf(inverseX * inverseX);
        numer = BigDecimal.ONE.divide(invX, scale, roundingMode);
        result = numer;
        int i = 1;
        do {
            numer = numer.divide(invX2, scale, roundingMode);
            int denom = 2 * i + 1;
            term = numer.divide(BigDecimal.valueOf(denom), scale, roundingMode);
            if ((i % 2) != 0) {
                result = result.subtract(term);
            } else {
                result = result.add(term);
            }
            i++;
        } while (term.compareTo(BigDecimal.ZERO) != 0);
        return result;
    }   

}
