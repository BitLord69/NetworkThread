package com.janinc.server;

import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkServer implements Runnable {
    private static NetworkServer mInstance;

    private static final String RECEIVED_MESSAGE = "Server|Message received!";

    public static final int USERNAME = 0;
    public static final int MESSAGE = 1;
    public final int MSG_SIZE = 512;
    public final int PORT = 80;
    private final int SLEEP_MS = 100;
    private DatagramSocket socket;

    ConcurrentHashMap<InetSocketAddress, String> userList = new ConcurrentHashMap<InetSocketAddress, String>();

    private boolean isRunning = true;

    private NetworkServer() {
        try {
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout(SLEEP_MS);
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } // catch
    } // NetworkServer

    public static NetworkServer get() {
        if (mInstance == null) {
            mInstance = new NetworkServer();
        }
        return mInstance;
    }

    public void sendMsgToClient(String msg, SocketAddress clientSocketAddress) {
        byte[] buffer = msg.getBytes();

        DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientSocketAddress);

        try {
            socket.send(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            DatagramPacket clientRequest = new DatagramPacket(new byte[MSG_SIZE], MSG_SIZE);

            if (!receiveMsgFromAnyClient(clientRequest)) {
                continue;
            } // if !receiveMsgFromAnyClient...

            String clientMsg = new String(clientRequest.getData(), 0, clientRequest.getLength());
            String[] msgParts = clientMsg.split("|");
            userList.put(new InetSocketAddress(clientRequest.getAddress(), PORT), msgParts[USERNAME]);
            userList.
                    entrySet().
                    stream().
                    filter(e -> !((String)e.getValue()).equals(msgParts[USERNAME])).
                    forEach(e -> sendMsgToClient(msgParts[MESSAGE], e.getKey()));
            sendMsgToClient(RECEIVED_MESSAGE, new InetSocketAddress(clientRequest.getAddress(), PORT));
            System.out.println(msgParts[MESSAGE]); // debugging purpose only!
            // TODO: Save the msg to a queue instead
        } // while...
    } // run

    private boolean receiveMsgFromAnyClient(DatagramPacket clientRequest) {
        try {
            socket.receive(clientRequest);
        } catch (Exception ex) {
            return false;
        } // catch
        return true;
    } // receiveMsgFromAnyClient
} // class NetworkServer