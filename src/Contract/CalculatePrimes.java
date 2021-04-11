/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 *
 */
public class CalculatePrimes implements Task, Serializable {

    int num = 100;
    LinkedList<Integer> res = new LinkedList<>();

    public void setNum(int n) {
        this.num = n;
    }

    @Override
    public void executeTask() {
        for (int i = 1; i <= num; i++) {
            if( CalculatePrimes.isPrime(i)){
                res.add(i);
            }
        }

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
