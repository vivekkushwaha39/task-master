/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskclient.main;

import Contract.Task;
import Contract.TaskList;
import Contract.TaskObject;
import taskclient.NetworkClient;
import taskclient.NotificationListener;

public class Driver implements NotificationListener {

	private TaskList tl = null;
	private TaskObject t = null;
	private Object res = null;
	private NetworkClient client;

	public Driver() {
		client = new NetworkClient(this);
	}

	public void startClient() {
		client.init();
		client.getTaskList();
		if (tl == null) {
			client.close();
			return;
		}

		String[] tasks = tl.getTaskClassName();
		System.out.println("Available Tasks ");

		for (String s : tasks) {
			System.out.println(s);
		}

		client.getTask(tasks[0]);

		if (t == null) {
			System.out.println("task is null");
			client.close();
			return;
		}
		
		t.getTObject().executeTask();
		res = t.getTObject().getResult();
		client.sendResult(res);
		
	}

	@Override
	public void OnTaskList(TaskList tl) {
		this.tl = tl;
	}

	@Override
	public void OnTask(TaskObject t) {
		System.out.println("Ontask");
		this.t = t;
	}

	@Override
	public void OnError(Exception ex) {
		ex.printStackTrace();
	}

}
