/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskclient.main;

import Contract.TaskList;
import Contract.TaskObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import taskclient.NetworkClient;
import taskclient.NotificationListener;
import taskclient.gui.ClientUI;

/**
 *
 */
public class DriverGUI extends ClientUI implements NotificationListener, ActionListener {

	private TaskList tl = null;
	private TaskObject t = null;
	private Object res = null;
	private NetworkClient client;
	private boolean isBusy = false;
	public DriverGUI() {
		step1();
		btnAvTask.addActionListener(this);
		btnDwnldTsk.addActionListener(this);
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

	@Override
	public void actionPerformed(ActionEvent e) {

		if (isBusy == true){
			System.out.println("GUI is busy");
		}
		System.out.println("actionPerformed");
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					DriverGUI.this.isBusy = true;
					if (e.getSource().equals(btnAvTask)) {
						populateTaskList();

					} else if (e.getSource().equals(btnDwnldTsk)) {
						downloadTask();
					}
					
					DriverGUI.this.isBusy = false;
				}
			}).start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void populateTaskList() {

		btnAvTask.setEnabled(false);
		client = new NetworkClient(this);
		client.init();
		addLog("Getting task list from master");
		client.getTaskList();
		if (tl == null) {
			addLog("TaskList not avaialable");
			client.close();
			return;
		}

		System.out.println("Available Tasks");
		String[] tasks = tl.getTaskClassName();
		for (String s : tasks) {
			System.out.println(s);
		}
		setTasks(new LinkedList<String>(Arrays.asList(tasks)));
		btnAvTask.setEnabled(true);
		addLog("Downloaded available task list");
		step2();
	}

	private void downloadTask() {
		addLog("Downloading tasks");
		int selItem = cmbTaskList.getSelectedIndex();
		if (selItem == -1) {
			addLog("Please Select task from Dropdown");
			System.out.println("No task selected");
			addLog("No task selected");
			return;
		}
		String selTask = cmbTaskList.getItemAt(selItem);
		client.getTask(selTask);
		addLog("Downloading task " + selTask);
		if (t == null) {
			addLog("Selected task is null aborting");
			System.out.println("task is null");
			addLog("task is null");
			client.close();
			step1();
			return;
		}
		addLog("Executing task");
		t.getTObject().executeTask();

		res = t.getTObject().getResult();
		addLog("Result is '" + res.toString()+ "'");
		addLog("Sending result to master");
		client.sendResult(res);
		addLog("result sent to master");
		client.close();
		step1();
	}
}
