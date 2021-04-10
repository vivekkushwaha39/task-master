/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import java.io.Serializable;

public class TaskList implements Serializable {

	private String AvailableTasks[];
	private String TaskClassName[];

	public String[] getAvailableTasks() {
		return AvailableTasks;
	}

	public void setAvailableTasks(String[] AvailableTasks) {
		this.AvailableTasks = AvailableTasks;
	}

	public String[] getTaskClassName() {
		return TaskClassName;
	}

	public void setTaskClassName(String[] TaskClassName) {
		this.TaskClassName = TaskClassName;
	}

}
