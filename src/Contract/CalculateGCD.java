/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import Contract.Task;
import java.io.Serializable;

/**
 * Task class to calculate GCD
 */
public class CalculateGCD implements Task, Serializable {

    long num1;
    long num2;
    long res=0;
    public void setNum1(long num1) {
        this.num1 = num1;
    }

    public void setNum2(long num2) {
        this.num2 = num2;
    }

    @Override
    public void executeTask() {
        res = CalculateGCD.calculateGCD(num1, num2);
    }

    @Override
    public Object getResult() {
        return res;
    }

    public static long calculateGCD(long a, long b) {
        if (a == 0) {
            return b;
        } else {
            while (b != 0) {
                if (a > b) {
                    a = a - b;
                } else {
                    b = b - a;
                }
            }
            return a;
        }
    }

}
