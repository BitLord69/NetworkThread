package com.janinc.client;

import com.janinc.server.NetworkServer;

import java.util.Scanner;

public class Client implements Runnable {
    private String userName;
    private boolean running = true;
    private NetworkClient nc;

    public Client() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter username: ");
        userName = input.nextLine();
        nc = new NetworkClient();
        nc.addObserver(this::msgReceived);
        Thread clientThread = new Thread(nc, "Network Client");
        clientThread.start();

        System.out.println("ClientThread startad...");
    } // Client

    private void msgReceived(Object o) {
        String s = (String) o;
        System.out.println("Client says in msgReceived: " + s);
    } // msgReceived

    @Override
    public void run() {
        Scanner input = new Scanner(System.in);
        while (running) {
            System.out.print("Enter message (ctrl + D to end): ");
            if (input.hasNextLine()) {
                String message = input.nextLine();
                if (!message.isBlank()) {
                    nc.sendMsgToServer(userName + NetworkServer.SPLIT_CHARACTER + message);
                }
            } else {
                running = false;
                nc.setRunning(false);
            } // else
        } // while running
    } // run
    public static void main(String[] args){
        Client c = new Client();
        Thread inputThread = new Thread(c, "Network Client");
        inputThread.start();
    } // main
} // class Client
