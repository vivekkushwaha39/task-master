/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskclient;

import Contract.Task;
import Contract.TaskList;
import Contract.TaskObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkClient {

	NotificationListener notificationListener;
	Socket cliSock = null;

	public NetworkClient(NotificationListener notificationListener) {
		this.notificationListener = notificationListener;
	}

	public void init() {
		try {
			cliSock = new Socket("localhost", 1234);
		} catch (IOException ex) {
			ex.printStackTrace();
			notificationListener.OnError(ex);
		}
	}

	public void getTaskList() {
		System.out.println("Getting task list");
		ObjectInputStream ios = null;
		try {
			
			System.out.println("Getting output");
			BufferedWriter pr = new BufferedWriter(new PrintWriter(cliSock.getOutputStream()));
			System.out.println("Getting task list");
			pr.write("GET_TASKS_LIST");
			pr.newLine();
			pr.flush();
			ios = new ObjectInputStream(cliSock.getInputStream());
			System.out.println("wating for tasklist obj");
			Object taskList = ios.readObject();
			TaskList tl = (TaskList) taskList;
			notificationListener.OnTaskList(tl);
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		} 
	}

	public void getTask(String className) {
		ObjectInputStream ios = null;
		try {
			BufferedWriter pr = new BufferedWriter(new PrintWriter(cliSock.getOutputStream()));
			pr.write("GET_TASK");
			pr.newLine();
			pr.flush();
			System.out.println("wrting class name");
			pr.write(className);
			pr.newLine();
			pr.flush();
			ios = new ObjectInputStream(cliSock.getInputStream());
			Object task = ios.readObject();
			if (task == null )
			{
				System.out.println("NULL is recieved");
			}
			TaskObject t = (TaskObject) task;
			notificationListener.OnTask(t);
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public void sendResult(Object res) {
		System.out.println("Sending result");
		try {
			BufferedWriter pr = new BufferedWriter(new PrintWriter(cliSock.getOutputStream()));
			pr.write("RESULT");
			pr.newLine();
			pr.flush();
			Thread.sleep(500);
			System.out.println("Writing object");
			ObjectOutputStream oOut = new ObjectOutputStream(cliSock.getOutputStream());
			oOut.writeObject("baasa");
			Thread.sleep(1000);
			oOut.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		
		try {
			cliSock.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void close(){
		try {
			this.cliSock.close();
		} catch (IOException ex) {
			Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
