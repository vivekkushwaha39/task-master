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
import java.util.logging.Level;
import java.util.logging.Logger;
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


    @Override
    public Task getTaskByClassName(String className) {
        Task t = null;
        try {
            t = (Task) Class.forName("Contract." + className).newInstance();
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

        String[] tasks = {"Calculate value of Pi upto 10 digits", "Calculate value of Pi upto 4 digits", "Is prime: 100", "CalculateGCD of 5 and 7"};
        String[] taskClasses = {"CalculatePi", "CalculatePrimes", "CalculateGCD"};
        tl.setAvailableTasks(tasks);
        tl.setTaskClassName(taskClasses);
    }

    @Override
    public void completeJob(TaskObject o) {
//        compJobs.put(o, res);
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
        Task t = getTaskByID(tid);
        to.setTObject(t);
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
                pr.setNum(100);
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
