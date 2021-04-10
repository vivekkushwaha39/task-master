/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import Contract.Task;
import java.io.Serializable;

public class Task2 implements Task, Serializable{

	@Override
	public void executeTask() {
		System.out.println("EXECUTING TASK2");
	}

	@Override
	public Object getResult() {
		return "THIS IS TASK2 result";
	}
	
}
