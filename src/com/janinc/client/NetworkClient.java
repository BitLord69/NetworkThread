package com.janinc.client;

import com.janinc.server.NetworkServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NetworkClient implements Runnable {

    private final String SERVER_IP = "10.152.190.15";

    private final int SLEEP_MS = 100;

    private DatagramSocket socket;
    private boolean isRunning = true;
    private InetAddress serverAddress;
    private List<Consumer> observers = new ArrayList<>();

    public NetworkClient(){
        try {
            serverAddress = InetAddress.getByName(SERVER_IP);
            socket = new DatagramSocket(null);
            socket.setSoTimeout(SLEEP_MS);
        } catch(Exception e){ System.out.println("NetworkCients konstruktor säger: " + e.getMessage()); }
    } // NetworkClient

    public void setRunning(boolean running) {
        isRunning = running;
    }
    public void addObserver(Consumer consumer){
        observers.add(consumer);
    }
    public void removeObserver (Consumer consumer){
        observers.remove(consumer);
    }
    private void sendToObservers(String msg){
        for(Consumer c : observers){
            c.accept(msg);
        } // for Consumer...
    } // sendToObservers

    public void sendMsgToServer(String msg) {
        byte[] buffer = msg.getBytes();
        DatagramPacket request = new DatagramPacket(buffer, buffer.length, this.serverAddress, NetworkServer.PORT);
        try { socket.send(request); } catch (Exception e) {
            System.out.println("NetworkClient.sendMsgToServer: " + e.getMessage());
        } // catch
    } // sendMsgToServer

    private void receiveMessageFromServer() {
        byte[] buffer = new byte[NetworkServer.MSG_SIZE];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(response);
            String serverMsg = new String(buffer, 0, response.getLength());
            sendToObservers(serverMsg);
            // TODO: Save the msg to a queue instead
        } catch (Exception ex) {
            try { Thread.sleep(SLEEP_MS); }
            catch (Exception e) {
                System.out.println("receiveMessageFromServer: " + e.getMessage());
            } // catch
        } // catch
    } // receiveMessageFromServer

    @Override
    public void run() {
        while (isRunning) {
            receiveMessageFromServer();
        } // while isRunning...

        socket.close();
    } // run
} // class NetworkClient