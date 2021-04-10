/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskmaster;

import Contract.TaskObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Class for managing sockets i.e. multi-threaded server
 *
 */
public class TaskListener {

    private ServerSocket server = null;

    private DataProvider provider;
    private boolean keepListen = true;
    private int port = 1234;

    public TaskListener(DataProvider provider) {
        this.provider = provider;
    }

    public boolean initComm() {
        boolean ret = false;

        try {

            server = new ServerSocket(this.port);
            server.setReuseAddress(true);

            ret = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            ret = false;
        }
        return ret;
    }

    /**
     * Listen for connections forever
     */
    public void startListening() {
        while (keepListen) {
            Socket cliSocket;
            try {

                System.out.printf("Waiting for new task client on port: %d\n", this.port);
                cliSocket = server.accept();
                System.out.println("New task client accepted");
                ClientHandler handler = new ClientHandler(cliSocket, provider);
                Thread t = new Thread(handler);
                t.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Private Class to handle one client connection on separate thread/
     */
    class ClientHandler implements Runnable {

        private Socket cSock = null;
        DataProvider p;

        public ClientHandler(Socket s, DataProvider p) {
            this.cSock = s;
            this.p = p;
        }

        @Override
        public void run() {
            boolean wait = true;
            System.out.println("Thread started");
            try {
                ObjectInputStream ois = new ObjectInputStream(cSock.getInputStream());
                ObjectOutputStream oOutS = new ObjectOutputStream(cSock.getOutputStream());

                while (wait == true) {
                    System.out.println("waiting for input");
                    //BufferedReader rdr = new BufferedReader(new InputStreamReader(cSock.getInputStream()));
                    String data = (String) ois.readObject();
                    System.out.println("data is  " + data);

                    switch (data) {
                        case "GET_TASKS_LIST":
                            Object dp = this.p.getTaskList();
                            oOutS.writeObject(dp);
                            oOutS.flush();
                            wait = false;
                            break;
                        case "FILL_TASK":
                            TaskObject to = (TaskObject) ois.readObject();
                            if (to == null) {
                                System.out.println("TaskObject is null");
                                wait = false;
                                break;
                            }
                            to = provider.fillTaskObject(to);
                            if (to == null || to.getTObject() != null) {
                                oOutS.writeObject(to);
                            } else {
                                System.out.println("Unable to get task");
                                wait = false;
                            }
                            break;
                        case "RESULT": //read result object from client

                            System.out.println("Reading result");
                            TaskObject res = (TaskObject) ois.readObject();
                            System.out.println("Reading done result is " + res.getTObject().getResult().toString());
                            p.completeJob(res);
                            int cred = new Random().nextInt(5000);
                            System.out.println("Tassk completed, Credit assigned: " + cred);
                            res.setCredit(cred);
                            oOutS.writeObject(res);
                            wait = false;
                            break;
                        default:
                            System.out.println("Task Server command not understood");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                cSock.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}
