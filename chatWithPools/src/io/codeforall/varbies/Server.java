package io.codeforall.varbies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static Vector<ServerWorker> serverWorkers = new Vector<>();

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket = new ServerSocket(9052);

            ExecutorService cachedPool = Executors.newCachedThreadPool();

            while (true) {
                Socket clientSocket = serverSocket.accept();

                Server server = new Server();

                ServerWorker user = server.new ServerWorker(clientSocket);

                serverWorkers.add(user);

                cachedPool.submit(server.new ServerWorker(clientSocket));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private class ServerWorker implements Runnable {

        Socket clientSocket;

        public ServerWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {

                BufferedReader inName = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientSocket.getOutputStream().write("Your name?\n".getBytes());
                String nameInput = inName.readLine();
                Thread.currentThread().setName(nameInput);
                clientSocket.getOutputStream().write(("Hello " + Thread.currentThread().getName() + ", have fun chatting and write /quit when you want to leave!\n").getBytes());


                while (true) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String input = in.readLine();
                    if (input != null && !input.equals("/quit")) {
                        System.out.println(Thread.currentThread().getName() + ": " + input);
                    }

                    if (input.equals("/quit")) {
                        System.out.println(Thread.currentThread().getName() + " DISCONNECTED");
                        inName.close();
                        in.close();
                        clientSocket.close();
                        for (ServerWorker user : serverWorkers) {
                            user.clientSocket.getOutputStream().write((Thread.currentThread().getName() + " DISCONNECTED\n").getBytes());
                        }
                    }

                    for (ServerWorker user : serverWorkers) {

                        //TODO: STUDY THIS!!!
                        user.clientSocket.getOutputStream().write(("\n" + Thread.currentThread().getName() + ": " + input + "\n").getBytes());

                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
