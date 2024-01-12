package io.codeforall.varbies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static void main(String[] args) {
        Socket clientSocket = null;
        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        Client client = new Client();

        try {
            clientSocket = new Socket("localhost", 9052);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            threadPool.submit(client.new ReceiveInputFromServer(clientSocket));

            while (true) {
                String input = in.readLine();
                out.println(input);

                if (input.equals("/quit")) {
                    in.close();
                    out.close();
                    clientSocket.close();
                }
            }

        } catch (IOException e) {
        }

    }

    private class ReceiveInputFromServer implements Runnable {
        private Socket clientSocket;

        public ReceiveInputFromServer(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while (true) {
                    String serverInput = serverIn.readLine();
                    if (serverInput != null) {
                        System.out.println(serverInput);
                    }
                }
            } catch (IOException e) {
            }
        }
    }
}

// WHEN ONE CLIENT DISCONNECTS, THE OTHER CAN ONLY SEND ONE MESSAGE THAT IS READABLE. WHY?