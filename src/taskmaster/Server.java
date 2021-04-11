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

        String[] tasks = {
            "Calculate value of Pi upto 50 decimal digits",
            "Calculate prime from 1 to 70",
            "Calculate GCD of 128 and 76",
            "Calculate value of Pi upto 70 decimal digits",
            "Calculate prime from 1 to 100",
            "Calculate GDC of 252 and 24"
        
        };
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
                CalculatePi pi = new CalculatePi();
                pi.setUpto(50);
                t = pi;
                break;
            case 1:
                CalculatePrimes pr1 = new CalculatePrimes();
                pr1.setNum(50);
                t=pr1;
                break;
            case 2:
                CalculateGCD gcd1 = new CalculateGCD();
                gcd1.setNum1(128);
                gcd1.setNum2(76);
                t=gcd1;
                break;
            case 3:
                CalculatePi pi2 = new CalculatePi();
                pi2.setUpto(70);
                t = pi2;
                break;
            case 4:
                CalculatePrimes pr2 = new CalculatePrimes();
                pr2.setNum(100);
                t=pr2;
                break;
            default:
                CalculateGCD gcd = new CalculateGCD();
                gcd.setNum1(252);
                gcd.setNum2(24);
                t = gcd;
                break;

        }
        return t;
    }

}
