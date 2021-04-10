/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskmaster;

import Contract.Task;
import Contract.TaskList;
import Contract.TaskObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import Contract.CalculateGCD;
import Contract.CalculatePi;
import Contract.CalculatePrimes;

/**
 *
 * Driver class
 */
public class Server implements DataProvider {

//    LinkedList<Pair<Integer, TaskObject>> availableJobs = new LinkedList<>();
//    LinkedList<Pair<Integer, TaskObject>> processingJobs = new LinkedList<>();
    HashMap<TaskObject, Object> compJobs = new HashMap<>();
    TaskList tl = new TaskList();
    TaskListener networkListener = null;
    FileListener fileListener = null;

    public Server() {
        tl.setAvailableTasks(new String[]{"Calculate value of pi", "An arbitary Task", "Calculate prime numbers", "Greatest Common Divisor"});
        tl.setTaskClassName(new String[]{"CalculatePi", "Task2", "CalculatePrimes", "CalculateGCD"});
        networkListener = new TaskListener(this);
        fileListener = new FileListener(this);
    }

    @Override
    public TaskList getTaskList() {
        return tl;
    }

    /*
    @Override
    public TaskObject getTaskByClassName(String className) {
        synchronized (this) {
            TaskObject to = null;

            System.out.println("getTaskByClassName " + availableJobs.size());
            try {
                for (int i = 0; i < availableJobs.size(); i++) {
                    Pair<Integer, TaskObject> c = availableJobs.get(i);
//				System.out.println(c.getValue().getTObject().getClass());	
                    if (c != null && c.getValue().getTObject().getClass().equals(Class.forName("taskmaster.tasks." + className))) {

                        availableJobs.remove(i);
                        processingJobs.addLast(c);
                        to = c.getValue();
                        System.out.println("Found task by class name");
                        break;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return to;

        }
    }
     */
    @Override
    public Task getTaskByClassName(String className) {
        Task t = null;
        try {
            t = (Task) Class.forName("taskmaster.tasks." + className).newInstance();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return t;
    }

    /**
     * Generate some tasks
     */
    public void genAvTasks() {

        String[] tasks = {"CalculatePi upto 10 digits", "CalculatePi upto 4 digits", "CalculatePrimes upto 100", "CalculateGCD of 5 and 7"};
        String[] taskClasses = {"CalculatePi", "CalculatePrimes", "CalculateGCD"};
        tl.setAvailableTasks(tasks);
        tl.setTaskClassName(taskClasses);
    }

    @Override
    public void completeJob(TaskObject o, Object res) {
        compJobs.put(o, res);
    }

    public void initServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (networkListener.initComm()) {
                    networkListener.startListening();
                }

            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                fileListener.initComm();
                fileListener.startListening();

            }
        }).start();
    }

    @Override
    public TaskObject fillTaskObject(TaskObject to) {
        int tid = to.getTaskID();
        return to;
    }

    public Task getTaskByID(int id) {
        Task t = null;

        switch (id) {
            case 0:
            case 1:
                CalculatePi pi = new CalculatePi();
                int upto = (id == 0) ? 10 : 4;
                pi.setUpto(upto);
                t = pi;
                break;
            case 2:
                CalculatePrimes pr = new CalculatePrimes();
                pr.setUpto(100);
                t = pr;
                break;
            case 3:
            default:
                CalculateGCD gcd = new CalculateGCD();
                gcd.setNum1(5);
                gcd.setNum2(7);
                t = gcd;
                break;

        }
        return t;
    }

}
