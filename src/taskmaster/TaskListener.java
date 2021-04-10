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
        TaskObject to = null;

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
                        /*case "GET_TASK": // Send .class file and Task Object
                            System.out.println("Get TASK request");
                            rdr = new BufferedReader(new InputStreamReader(cSock.getInputStream()));
                            System.out.println("reading the class name");
                            String clsName = rdr.readLine();
                            System.out.println("Class nameis " + clsName);
                            to = p.getTaskByClassName(clsName);
                            if (to == null) {
                                System.out.println("to is null");
                                cSock.close();
                                break;
                            }
                            URL url = to.getTObject().getClass().getResource(to.getTObject().getClass().getSimpleName() + ".class");
                            System.out.println(url.getPath());
                            System.out.println(to.getTObject().getClass().getSimpleName());
                            File f = new File(url.getPath());
                            Long fLenth = f.length();
                            oOutS = new ObjectOutputStream(cSock.getOutputStream());
                            oOutS.writeObject(fLenth);
                            oOutS.flush();
                            System.out.println("Size written " + fLenth);
                            String conf = rdr.readLine();
                            if (conf.equals("OK")) {
                                byte[] buffer = new byte[1024];
                                int read = 0;
                                FileInputStream fIS = new FileInputStream(f);
                                while ((read = fIS.read(buffer)) > 0) {
                                    System.out.println("writing bytes " + read);
                                    oOutS.write(buffer, 0, read);
                                }
                                oOutS.flush();
                            } else {
                                cSock.close();
                                wait = false;
                                break;
                            }
                            System.out.println("File Sent");
                            String conf2 = rdr.readLine();
                            if (conf2.equals("OK") == false) {
                                cSock.close();
                                wait = false;
                                break;
                            }

                            System.out.println("ACK recieved");
                            oOutS.writeObject(to);
                            oOutS.flush();
                            System.out.println("Task Object is written");
                            break;*/
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
                            ois.close();
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
