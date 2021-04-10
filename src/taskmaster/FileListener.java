/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskmaster;

import Contract.Task;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

/**
 * Class for listening file clients
 * for downloading class files
 */
public class FileListener {

    private ServerSocket server = null;

    private DataProvider provider;
    private boolean keepListen = true;
    private int port = 1235;

    public FileListener(DataProvider provider) {
        this.provider = provider;
    }

    /**
     * function to create server socket
     * @return boolean is com initialized
     */
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
     * Listen for new connections forever
     */
    public void startListening() {
        while (keepListen) {
            Socket cliSocket;
            try {
                System.out.printf("Waiting for new file client on port: %d\n", this.port);
                cliSocket = server.accept();
                System.out.println("New file client connected");
                ClientHandler handler = new ClientHandler(cliSocket, provider);
                Thread t = new Thread(handler);
                t.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * class to handle clients in separate threads
     */
    private class ClientHandler implements Runnable {

        Socket cliSock = null;
        DataProvider provider;
        ObjectOutputStream oOutS;
        BufferedReader rdr;

        public ClientHandler(Socket cliSock, DataProvider provider) {
            this.cliSock = cliSock;
            this.provider = provider;
        }

        private URL getTaskClassUrl(Task to) {
            URL url = to.getClass().getResource(to.getClass().getSimpleName() + ".class");
            return url;
        }

        /**
         * Function to send class file to clients.
         * Protocol:
         * S->C: Send file size
         * C->S: OK (ACK)
         * S->C: Sending file bytes as streams.
         * 
         */
        private boolean sendFile(File f) {
            boolean ret = true;
            if (f.exists() == false) {
                System.out.println("Error: class file doesn't exists " + f.getAbsolutePath());
                ret = false;
                return ret;
            }
            try {
                Long fLenth = f.length();
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
                    System.out.println("Invalid ack");
                    ret = false;
                }
            } catch (FileNotFoundException ex) {
                System.out.println("File not found");
                ex.printStackTrace();
                ret = false;

            } catch (IOException ex) {
                System.out.println("IO Exception");
                ex.printStackTrace();
                ret = false;
            }

            return ret;
        }

        @Override
        public void run() {
            boolean wait = true;
            try {
                rdr = new BufferedReader(new InputStreamReader(cliSock.getInputStream()));
                oOutS = new ObjectOutputStream(cliSock.getOutputStream());

                while (wait == true) {
                    String data = rdr.readLine();
                    System.out.println("data for fileserver " + data);
                    switch (data) {
                        case "GET_TASK": // Client reuesting a particular class file.
                            data = rdr.readLine(); // Get Task's class name from client
                            Task to = provider.getTaskByClassName(data);
                            URL fileUrl = getTaskClassUrl(to);
                            File classFile = new File(fileUrl.getPath());
                            boolean ret = sendFile(classFile);
                            if (ret == false) {
                                cliSock.close();
                                wait = false;
                                break;
                            }
                            data = rdr.readLine();
                            if (data.equals("OK")) {
                                System.out.println("File transferred");
                            } else {
                                System.out.println("File transfer failed");
                                wait = false;
                                cliSock.close();
                                break;
                            }

                            oOutS.writeObject(to); 
                            oOutS.flush();
                            cliSock.close();
                            System.out.println("file sock closed");
                            wait = false;
                            break;
                        default:
                            System.out.println("FileServer: command not understood");
                            break;
                    }

                }
            } catch (Exception e) {
                System.out.println("Error:" + e.getMessage());
//                e.printStackTrace();
            }
        }

    }

}
