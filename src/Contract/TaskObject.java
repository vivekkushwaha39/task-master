/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import java.io.Serializable;

public class TaskObject implements Serializable{
	private Integer TaskID=0;
	private Integer Credit=0;
	private Task TObject=null;

	public Integer getTaskID() {
		return TaskID;
	}

	public void setTaskID(Integer TaskID) {
		this.TaskID = TaskID;
	}

	public Integer getCredit() {
		return Credit;
	}

	public void setCredit(Integer Credit) {
		this.Credit = Credit;
	}

	public Task getTObject() {
		return TObject;
	}

	public void setTObject(Task TObject) {
		this.TObject = TObject;
	}
	
}
