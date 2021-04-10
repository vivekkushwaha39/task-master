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

/**
 * Class for managing sockets i.e. multi-threaded server
 *
 */
public class TaskListener {

    private ServerSocket server = null;

    private DataProvider provider;
    private boolean keepListen = true;

    public TaskListener(DataProvider provider) {
        this.provider = provider;
    }

    public boolean initComm() {
        boolean ret = false;

        try {

            server = new ServerSocket(1234);
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
                System.out.println("Waiting for new client");
                cliSocket = server.accept();
                System.out.println("New client");
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

                /**
                 * Wait for messages from client flow.. S: server C: client
                 * C->S: "GET_TASK_LIST" S->C: Send TaskList Object C->S:
                 * "GET_TASK" C->S: "Task class name" S->C: Send .class file
                 * size C->S: "OK" S->C: Send .class FIle C->S: "OK" S->C: Task
                 * Object C->S: Result object
                 */
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
                            //ObjectInputStream oIs = new ObjectInputStream(cSock.getInputStream());
                            System.out.println("Reading result");
                            TaskObject res = (TaskObject) ois.readObject();
                            System.out.println("Reading done");
                            // TODO: remove this logic to add into pool
                            p.completeJob(res, res.getTObject().getResult());
                            res.setCredit(1000);
                            oOutS.writeObject(res);
                            wait = false;
                            break;
                        default:
                            System.out.println("TaskS Server command not understood");
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
