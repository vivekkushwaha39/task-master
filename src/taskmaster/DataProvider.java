/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskmaster;

import Contract.TaskObject;

public interface DataProvider {
	public Contract.TaskList getTaskList();
	public Contract.Task getTaskByClassName(String className);
	public void completeJob(TaskObject o);
        public TaskObject fillTaskObject(TaskObject to);
}
