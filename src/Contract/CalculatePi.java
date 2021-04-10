/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import Contract.Task;
import java.io.Serializable;

public class CalculatePi implements Task, Serializable {

    int upto = 5;
    double pi = 3.14;

    public void setUpto(int upto) {
        this.upto = upto;
    }

    @Override
    public void executeTask() {
        pi = 2 * Math.acos(0.0);
    }

    @Override
    public Object getResult() {
        return pi;
    }

}
