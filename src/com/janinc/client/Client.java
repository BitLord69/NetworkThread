package com.janinc.client;

import java.util.Scanner;



public class Client implements Runnable {

    private String userName;
    private boolean running = true;
    private NetworkClient nc;

    public Client() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username");
        userName = input.nextLine();
        nc = new NetworkClient();
        nc.addObserver(this::msgReceived);
        Thread clientThread = new Thread(nc, "Network Client");
        clientThread.start();
    }

    private void msgReceived(Object o) {
        String s = (String) o;
        System.out.println(s);
    }

    @Override
    public void run() {
        Scanner input = new Scanner(System.in);
        while (running) {
            System.out.print("Enter message (ctrl + D to end): ");
            if (input.hasNextLine()) {
                String message = input.nextLine();
                if (!message.isBlank()) {
                    nc.sendMsgToServer(userName + "|" + message);
                }
            } else {
                running = false;
                nc.setRunning(false);
            }
        }
    }
    public static void main(String[] args){
        Client c = new Client();
        Thread inputThread = new Thread(c, "Network Client");
        inputThread.start();
    }
}
