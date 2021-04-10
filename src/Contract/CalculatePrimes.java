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
public class CalculatePrimes implements Task,Serializable {

	int upto=100;
	LinkedList<Integer> result = new LinkedList<>();

	public void setUpto(int upto) {
		this.upto = upto;
	}

	
	@Override
	public void executeTask() {
		for (int i = 2; i <= upto; i++) {
			int c = 0;
			for (int j = 1; j <= i; j++) {
				if (i % j == 0) {
					c++;
				}
			}

			if (c == 2) {
				result.addLast(i);
			}
		}
	}

	@Override
	public Object getResult() {
		return result;
	}

}
