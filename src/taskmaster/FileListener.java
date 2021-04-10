/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskmaster;

import Contract.Task;
import Contract.TaskObject;
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
 *
 * @author vfi
 */
public class FileListener {

    private ServerSocket server = null;

    private DataProvider provider;
    private boolean keepListen = true;

    public FileListener(DataProvider provider) {
        this.provider = provider;
    }

    public boolean initComm() {
        boolean ret = false;

        try {

            server = new ServerSocket(1235);
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
                System.out.println("Waiting for new file client");
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
                while (wait == true) {
                    rdr = new BufferedReader(new InputStreamReader(cliSock.getInputStream()));
                    oOutS = new ObjectOutputStream(cliSock.getOutputStream());
                    String data = rdr.readLine();
                    System.out.println("data for fileserver " + data);
                    switch (data) {
                        case "GET_TASK":
                            data = rdr.readLine();
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
                            break;
                        default:
                            System.out.println("FileServer: command not understood");
                            break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
