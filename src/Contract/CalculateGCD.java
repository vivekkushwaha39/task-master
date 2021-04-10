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

    public void setNum1(long num1) {
        this.num1 = num1;
    }

    public void setNum2(long num2) {
        this.num2 = num2;
    }

    @Override
    public void executeTask() {

    }

    @Override
    public Object getResult() {
        return "0";
    }

}
