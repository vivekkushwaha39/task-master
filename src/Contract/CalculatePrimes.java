/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import Contract.Task;
import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 *
 */
public class CalculatePrimes implements Task, Serializable {

    int num = 100;
    boolean res = false;
    
    public void setNum(int n) {
        this.num = n;
    }

    @Override
    public void executeTask() {
        res = CalculatePrimes.isPrime(num);
    }

    public static boolean isPrime(int number) {
        for (int i = 2; i < number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getResult() {
        return res;
    }

}
